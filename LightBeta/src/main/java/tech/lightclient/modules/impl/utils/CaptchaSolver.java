package tech.lightclient.modules.impl.utils;

import com.adl.nativeprotect.User;
import net.minecraft.block.MapColor;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapState;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.biome.BiomeKeys;
import tech.lightclient.client.events.impl.client.TickEvent;
import tech.lightclient.client.utils.captcha.CaptchaBackend;
import tech.lightclient.client.utils.eventbus.EventHandler;
import tech.lightclient.client.utils.player.ServerUtil;
import tech.lightclient.modules.api.Category;
import tech.lightclient.modules.api.Function;
import tech.lightclient.modules.api.FunctionAdd;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

/** Exact map captcha collector and Delta backend client. */
@FunctionAdd(
        name = "CaptchaSolver",
        alias = "Captcha Solver",
        category = Category.Utilities,
        description = "Automatically solves FunTime map captchas through the Delta backend"
)
public class CaptchaSolver extends Function {
    private static final int MAP_SIZE = 128;

    private final CaptchaBackend backend = new CaptchaBackend();
    private volatile byte[] lastImage;

    @EventHandler
    public void onTick(TickEvent event) {
        if (nullCheck() || !isCaptchaServer() || !backend.isConnected()) {
            return;
        }

        if (!(mc.crosshairTarget instanceof BlockHitResult hit)
                || hit.getType() != HitResult.Type.BLOCK) {
            return;
        }

        ItemFrameEntity origin = mc.world.getEntitiesByClass(
                ItemFrameEntity.class,
                new Box(hit.getBlockPos()).expand(0.5),
                this::isFilledMapFrame
        ).stream().findFirst().orElse(null);
        if (origin == null) {
            return;
        }

        byte[] image = renderCaptcha(origin);
        if (image == null || Arrays.equals(image, lastImage)) {
            return;
        }

        lastImage = image;
        backend.sendCaptcha(image);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        backend.setToken(getBackendToken());
        backend.setCaptchaCodeListener(code -> mc.execute(() -> submitCode(code)));
        backend.connect();
    }

    @Override
    public void onDisable() {
        backend.disconnect();
        lastImage = null;
        super.onDisable();
    }

    private boolean isFilledMapFrame(ItemFrameEntity frame) {
        if (frame == null) {
            return false;
        }
        ItemStack stack = frame.getHeldItemStack();
        return !stack.isEmpty() && stack.getItem() instanceof FilledMapItem;
    }

    /** Same gate as Delta's ServerUtil.a.c(): FunTime BotFilter swamp world. */
    private boolean isCaptchaServer() {
        if (!ServerUtil.isFunTime() || mc.world == null || mc.player == null || mc.getNetworkHandler() == null) {
            return false;
        }

        String brand = mc.getNetworkHandler().getBrand();
        return brand != null
                && brand.contains("BotFilter (https://vk.cc/8hr1pU)")
                && mc.world.getBiome(mc.player.getBlockPos()).matchesKey(BiomeKeys.SWAMP);
    }

    private boolean isUsableMapFrame(ItemFrameEntity frame) {
        if (!isFilledMapFrame(frame) || mc.world == null) {
            return false;
        }
        return FilledMapItem.getMapState(frame.getHeldItemStack(), mc.world) != null;
    }

    private byte[] renderCaptcha(ItemFrameEntity origin) {
        Direction facing = origin.getHorizontalFacing();
        boolean alongZ = facing.getAxis() == Direction.Axis.Z;
        boolean flip = facing == Direction.WEST || facing == Direction.SOUTH;

        List<ItemFrameEntity> frames = mc.world.getEntitiesByClass(
                ItemFrameEntity.class,
                origin.getBoundingBox().expand(16.0),
                frame -> frame.getHorizontalFacing() == facing && isUsableMapFrame(frame)
        );
        if (frames.isEmpty()) {
            return null;
        }

        int minU = Integer.MAX_VALUE;
        int maxU = Integer.MIN_VALUE;
        int minV = Integer.MAX_VALUE;
        int maxV = Integer.MIN_VALUE;
        for (ItemFrameEntity frame : frames) {
            BlockPos pos = frame.getAttachedBlockPos();
            int u = alongZ ? pos.getZ() : pos.getX();
            minU = Math.min(minU, u);
            maxU = Math.max(maxU, u);
            minV = Math.min(minV, pos.getY());
            maxV = Math.max(maxV, pos.getY());
        }

        int width = (maxU - minU + 1) * MAP_SIZE;
        int height = (maxV - minV + 1) * MAP_SIZE;
        if (width <= 0 || height <= 0) {
            return null;
        }

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        try {
            for (ItemFrameEntity frame : frames) {
                MapState state = FilledMapItem.getMapState(frame.getHeldItemStack(), mc.world);
                if (state == null || state.colors == null || state.colors.length != MAP_SIZE * MAP_SIZE) {
                    continue;
                }

                BufferedImage tile = new BufferedImage(MAP_SIZE, MAP_SIZE, BufferedImage.TYPE_INT_ARGB);
                for (int i = 0; i < state.colors.length; i++) {
                    int raw = state.colors[i] & 0xFF;
                    int color = raw < 4 ? 0 : MapColor.getRenderColor(raw);
                    tile.setRGB(i % MAP_SIZE, i / MAP_SIZE, color);
                }

                BlockPos pos = frame.getAttachedBlockPos();
                int u = alongZ ? pos.getZ() : pos.getX();
                int x = (flip ? maxU - u : u - minU) * MAP_SIZE;
                int y = (maxV - pos.getY()) * MAP_SIZE;
                AffineTransform transform = AffineTransform.getTranslateInstance(x, y);
                transform.rotate(Math.toRadians((frame.getRotation() & 3) * 90.0), 64.0, 64.0);
                graphics.drawImage(tile, transform, null);
            }
        } finally {
            graphics.dispose();
        }

        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", stream);
            byte[] bytes = stream.toByteArray();
            return Arrays.equals(bytes, lastImage) ? null : bytes;
        } catch (Exception ignored) {
            return null;
        }
    }

    private void submitCode(String code) {
        if (!isState() || mc.player == null || code == null || code.isBlank()) {
            return;
        }
        mc.player.networkHandler.sendChatMessage(code);
    }

    private static String getBackendToken() {
        try {
            String token = User.getInstance().profile("token");
            return token == null ? "" : token;
        } catch (Exception ignored) {
            return "";
        }
    }
}

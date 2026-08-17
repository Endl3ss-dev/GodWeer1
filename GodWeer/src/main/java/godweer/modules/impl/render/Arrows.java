package godweer.modules.impl.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;

import godweer.GodWeer;
import godweer.managers.FriendManager;
import godweer.managers.ModuleManager;
import godweer.managers.ThemeManager;
import godweer.modules.Module;
import godweer.modules.ModuleCategory;
import godweer.modules.api.setting.BooleanSetting;
import godweer.modules.api.setting.FloatSetting;
import godweer.modules.api.setting.ModeSetting;
import godweer.utils.color.ColorUtils;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.CoreShaders;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class Arrows extends Module {

    private static final ResourceLocation[] TEXTURES = {
            GodWeer.id("textures/modules/arrows/arrow.png"),
            GodWeer.id("textures/modules/arrows/arrow2.png"),
            GodWeer.id("textures/modules/arrows/arrow3.png"),
            GodWeer.id("textures/modules/arrows/arrow4.png"),
            GodWeer.id("textures/modules/arrows/arrow5.png"),
            GodWeer.id("textures/modules/arrows/arrow6.png")
    };

    private static final int FRIENDLY_COLOR = ColorUtils.rgb(70, 220, 110);
    private static final int HOSTILE_COLOR = ColorUtils.rgb(235, 60, 60);
    private static final int DEFAULT_FRIEND_COLOR = ColorUtils.rgb(80, 170, 255);

    private static final float ROTATION_SMOOTHING = 0.18F;
    private static final float FADE_IN = 0.12F;
    private static final float FADE_OUT = 0.10F;

    private static boolean eventsRegistered;

    private final ModeSetting texture = new ModeSetting("Текстура", "1", "1", "2", "3", "4", "5", "6");
    private final ModeSetting colorMode = new ModeSetting("Цвет", "Клиентский", "Клиентский", "Свой/Чужой", "Кастомное");
    private final FloatSetting playerRed = new FloatSetting("Игроки: красный", 230.0F, 0.0F, 255.0F, 1.0F)
            .visible(() -> colorMode.is("Кастомное"));
    private final FloatSetting playerGreen = new FloatSetting("Игроки: зелёный", 50.0F, 0.0F, 255.0F, 1.0F)
            .visible(() -> colorMode.is("Кастомное"));
    private final FloatSetting playerBlue = new FloatSetting("Игроки: синий", 57.0F, 0.0F, 255.0F, 1.0F)
            .visible(() -> colorMode.is("Кастомное"));
    private final FloatSetting friendRed = new FloatSetting("Друзья: красный", 80.0F, 0.0F, 255.0F, 1.0F)
            .visible(() -> colorMode.is("Кастомное"));
    private final FloatSetting friendGreen = new FloatSetting("Друзья: зелёный", 170.0F, 0.0F, 255.0F, 1.0F)
            .visible(() -> colorMode.is("Кастомное"));
    private final FloatSetting friendBlue = new FloatSetting("Друзья: синий", 255.0F, 0.0F, 255.0F, 1.0F)
            .visible(() -> colorMode.is("Кастомное"));
    private final BooleanSetting onlyNaked = new BooleanSetting("Голых", false);
    private final BooleanSetting showFriends = new BooleanSetting("Друзьях", true);
    private final FloatSetting size = new FloatSetting("Размер", 13.0F, 8.0F, 28.0F, 0.5F);
    private final FloatSetting distance = new FloatSetting("Дистанция", 58.0F, 30.0F, 120.0F, 1.0F);

    private final Map<UUID, ArrowState> states = new HashMap<>();
    private final Set<UUID> seen = new HashSet<>();

    public Arrows() {
        super("Arrows", "Стрелочки на игроков вокруг прицела", ModuleCategory.RENDER);
        addSettings(texture, colorMode, playerRed, playerGreen, playerBlue,
                friendRed, friendGreen, friendBlue, onlyNaked, showFriends, size, distance);
        registerEvents();
    }

    private static void registerEvents() {
        if (eventsRegistered) {
            return;
        }
        eventsRegistered = true;
        HudRenderCallback.EVENT.register((guiGraphics, deltaTracker) -> {
            Arrows module = ModuleManager.get(Arrows.class);
            if (module != null && module.isEnable()) {
                module.onHudRender(guiGraphics.pose(), deltaTracker.getGameTimeDeltaPartialTick(true));
            }
        });
    }

    @Override
    public void onDisable() {
        states.clear();
        super.onDisable();
    }

    private void onHudRender(PoseStack poseStack, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null || minecraft.options.hideGui) {
            states.clear();
            return;
        }
        if (!minecraft.options.getCameraType().isFirstPerson()) {
            fadeAll();
            return;
        }

        float centerX = minecraft.getWindow().getGuiScaledWidth() * 0.5F;
        float centerY = minecraft.getWindow().getGuiScaledHeight() * 0.5F;
        float arrowSize = size.get();
        float y = centerY - distance.get();
        float selfYaw = Mth.lerp(partialTick, minecraft.player.yRotO, minecraft.player.getYRot());
        Vec3 selfPos = interpolate(minecraft.player, partialTick);
        ResourceLocation arrowTexture = TEXTURES[texture.getIndex()];

        seen.clear();

        for (AbstractClientPlayer player : minecraft.level.players()) {
            if (player == minecraft.player || !player.isAlive() || player.isSpectator()) {
                continue;
            }

            boolean friend = FriendManager.isFriend(player);
            if (friend && !showFriends.get()) {
                continue;
            }
            if (!friend && onlyNaked.get() && !isNaked(player)) {
                continue;
            }

            UUID uuid = player.getUUID();
            ArrowState state = states.computeIfAbsent(uuid, id -> new ArrowState());
            seen.add(uuid);

            Vec3 targetPos = interpolate(player, partialTick);
            float targetYaw = relativeYaw(targetPos, selfPos, selfYaw);
            state.rotation = interpolateAngle(state.rotation, targetYaw, ROTATION_SMOOTHING);
            state.alpha = Mth.lerp(FADE_IN, state.alpha, 1.0F);
            float alpha = Mth.clamp(state.alpha, 0.0F, 1.0F);
            if (alpha <= 0.01F) {
                continue;
            }

            int color = resolveColor(friend);
            int drawColor = ColorUtils.applyAlpha(color, alpha);
            int shadowColor = ColorUtils.applyAlpha(ColorUtils.darken(color, 0.55F), alpha * 0.65F);
            renderArrow(poseStack, arrowTexture, centerX, centerY, y, arrowSize, state.rotation, drawColor, shadowColor);
        }

        states.entrySet().removeIf(entry -> {
            if (seen.contains(entry.getKey())) {
                return false;
            }
            ArrowState state = entry.getValue();
            state.alpha = Mth.lerp(FADE_OUT, state.alpha, 0.0F);
            return state.alpha <= 0.02F;
        });
    }

    private void renderArrow(PoseStack poseStack, ResourceLocation arrowTexture, float centerX, float centerY,
                             float y, float arrowSize, float rotation, int color, int shadowColor) {
        poseStack.pushPose();
        poseStack.translate(centerX, centerY, 0.0F);
        poseStack.mulPose(Axis.ZP.rotationDegrees(rotation));
        poseStack.translate(-centerX, -centerY, 0.0F);

        float x = centerX - arrowSize * 0.5F;
        drawTexture(poseStack, arrowTexture, x + 0.75F, y + 0.75F, arrowSize, arrowSize, shadowColor);
        drawTexture(poseStack, arrowTexture, x, y, arrowSize, arrowSize, color);

        poseStack.popPose();
    }

    private void drawTexture(PoseStack poseStack, ResourceLocation location, float x, float y,
                             float width, float height, int color) {
        int alpha = ColorUtils.alpha(color);
        if (alpha <= 0) {
            return;
        }
        int red = ColorUtils.red(color);
        int green = ColorUtils.green(color);
        int blue = ColorUtils.blue(color);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(CoreShaders.POSITION_TEX_COLOR);
        RenderSystem.setShaderTexture(0, location);

        Matrix4f matrix = poseStack.last().pose();
        BufferBuilder buffer = Tesselator.getInstance()
                .begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        buffer.addVertex(matrix, x, y, 0.0F).setUv(0.0F, 0.0F).setColor(red, green, blue, alpha);
        buffer.addVertex(matrix, x, y + height, 0.0F).setUv(0.0F, 1.0F).setColor(red, green, blue, alpha);
        buffer.addVertex(matrix, x + width, y + height, 0.0F).setUv(1.0F, 1.0F).setColor(red, green, blue, alpha);
        buffer.addVertex(matrix, x + width, y, 0.0F).setUv(1.0F, 0.0F).setColor(red, green, blue, alpha);

        MeshData mesh = buffer.build();
        if (mesh != null) {
            BufferUploader.drawWithShader(mesh);
        }
        RenderSystem.disableBlend();
    }

    private int resolveColor(boolean friend) {
        if (colorMode.is("Свой/Чужой")) {
            return friend ? FRIENDLY_COLOR : HOSTILE_COLOR;
        }
        if (colorMode.is("Кастомное")) {
            return friend
                    ? ColorUtils.rgb((int) friendRed.get(), (int) friendGreen.get(), (int) friendBlue.get())
                    : ColorUtils.rgb((int) playerRed.get(), (int) playerGreen.get(), (int) playerBlue.get());
        }
        return friend ? DEFAULT_FRIEND_COLOR : ThemeManager.getColor();
    }

    private boolean isNaked(Player player) {
        return player.getItemBySlot(EquipmentSlot.HEAD).isEmpty()
                && player.getItemBySlot(EquipmentSlot.CHEST).isEmpty()
                && player.getItemBySlot(EquipmentSlot.LEGS).isEmpty()
                && player.getItemBySlot(EquipmentSlot.FEET).isEmpty();
    }

    private void fadeAll() {
        states.entrySet().removeIf(entry -> {
            ArrowState state = entry.getValue();
            state.alpha = Mth.lerp(FADE_OUT, state.alpha, 0.0F);
            return state.alpha <= 0.02F;
        });
    }

    private Vec3 interpolate(Player player, float partialTick) {
        return new Vec3(
                Mth.lerp(partialTick, player.xo, player.getX()),
                Mth.lerp(partialTick, player.yo, player.getY()),
                Mth.lerp(partialTick, player.zo, player.getZ())
        );
    }

    private float relativeYaw(Vec3 target, Vec3 self, float selfYaw) {
        double dx = target.x - self.x;
        double dz = target.z - self.z;
        float yaw = (float) -Math.toDegrees(Math.atan2(dx, dz));
        return Mth.wrapDegrees(yaw - selfYaw);
    }

    private float interpolateAngle(float current, float target, float factor) {
        return current + Mth.wrapDegrees(target - current) * factor;
    }

    private static final class ArrowState {
        private float alpha;
        private float rotation;
    }
}

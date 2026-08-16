package godweer.modules.impl.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import godweer.managers.FriendManager;
import godweer.managers.ModuleManager;
import godweer.managers.ThemeManager;
import godweer.modules.Module;
import godweer.modules.ModuleCategory;
import godweer.modules.api.setting.BooleanSetting;
import godweer.modules.api.setting.FloatSetting;
import godweer.modules.api.setting.ModeSetting;
import godweer.utils.color.ColorUtils;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.CoreShaders;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import org.joml.Matrix4f;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public final class Trails extends Module {

    private static final int MAX_POINTS_PER_PLAYER = 512;

    private static boolean eventsRegistered;

    private final ModeSetting colorMode = new ModeSetting("Цвет", "Клиентский", "Клиентский", "Свой");
    private final FloatSetting customRed = new FloatSetting("Красный", 190.0F, 0.0F, 255.0F, 1.0F)
            .visible(() -> colorMode.is("Свой"));
    private final FloatSetting customGreen = new FloatSetting("Зелёный", 143.0F, 0.0F, 255.0F, 1.0F)
            .visible(() -> colorMode.is("Свой"));
    private final FloatSetting customBlue = new FloatSetting("Синий", 255.0F, 0.0F, 255.0F, 1.0F)
            .visible(() -> colorMode.is("Свой"));
    private final BooleanSetting showOthers = new BooleanSetting("Других", true);
    private final BooleanSetting showFriends = new BooleanSetting("Друзьях", true);
    private final FloatSetting length = new FloatSetting("Длина", 1.0F, 0.2F, 3.0F, 0.1F);

    private final Map<UUID, ArrayDeque<TrailPoint>> trails = new HashMap<>();

    public Trails() {
        super("Trails", "Красивый след за игроками", ModuleCategory.RENDER);
        addSettings(colorMode, customRed, customGreen, customBlue, showOthers, showFriends, length);
        registerEvents();
    }

    private static void registerEvents() {
        if (eventsRegistered) {
            return;
        }
        eventsRegistered = true;
        WorldRenderEvents.AFTER_TRANSLUCENT.register(context -> {
            Trails module = ModuleManager.get(Trails.class);
            if (module != null && module.isEnable()) {
                module.onWorldRender(context);
            }
        });
    }

    @Override
    public void onDisable() {
        trails.clear();
        super.onDisable();
    }

    private void onWorldRender(WorldRenderContext context) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null) {
            return;
        }

        PoseStack poseStack = context.matrixStack();
        if (poseStack == null) {
            return;
        }

        long now = System.currentTimeMillis();
        long lifetimeMs = (long) (length.get() * 1000.0F);
        float partialTick = context.tickCounter().getGameTimeDeltaPartialTick(true);

        for (AbstractClientPlayer player : minecraft.level.players()) {
            if (player == minecraft.player || !player.isAlive() || player.isSpectator()) {
                continue;
            }

            boolean friend = FriendManager.isFriend(player);
            if (friend ? !showFriends.get() : !showOthers.get()) {
                continue;
            }

            ArrayDeque<TrailPoint> points = trails.computeIfAbsent(player.getUUID(), uuid -> new ArrayDeque<>());
            points.addLast(new TrailPoint(
                    Mth.lerp(partialTick, player.xo, player.getX()),
                    Mth.lerp(partialTick, player.yo, player.getY()),
                    Mth.lerp(partialTick, player.zo, player.getZ()),
                    player.getBbHeight(),
                    now
            ));
            while (points.size() > MAX_POINTS_PER_PLAYER) {
                points.pollFirst();
            }
        }

        Iterator<Map.Entry<UUID, ArrayDeque<TrailPoint>>> iterator = trails.entrySet().iterator();
        while (iterator.hasNext()) {
            ArrayDeque<TrailPoint> points = iterator.next().getValue();
            // Точки упорядочены по времени, поэтому чистим только с головы очереди.
            while (!points.isEmpty() && now - points.peekFirst().time() > lifetimeMs) {
                points.pollFirst();
            }
            if (points.isEmpty()) {
                iterator.remove();
            }
        }

        if (trails.isEmpty()) {
            return;
        }

        Vec3 camera = context.camera().getPosition();
        poseStack.pushPose();
        poseStack.translate(-camera.x, -camera.y, -camera.z);
        Matrix4f matrix = poseStack.last().pose();

        int color = resolveColor();
        int red = ColorUtils.red(color);
        int green = ColorUtils.green(color);
        int blue = ColorUtils.blue(color);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(CoreShaders.POSITION_COLOR);

        for (ArrayDeque<TrailPoint> points : trails.values()) {
            renderTrail(matrix, points, red, green, blue);
        }

        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        poseStack.popPose();
    }

    private void renderTrail(Matrix4f matrix, ArrayDeque<TrailPoint> points, int red, int green, int blue) {
        int count = points.size();
        if (count < 2) {
            return;
        }

        BufferBuilder buffer = Tesselator.getInstance()
                .begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);
        int index = 0;
        for (TrailPoint point : points) {
            int alpha = (int) (index / (float) count * 0.7F * 255.0F);
            float x = (float) point.x();
            float y = (float) point.y();
            float z = (float) point.z();
            buffer.addVertex(matrix, x, y + point.height(), z).setColor(red, green, blue, alpha);
            buffer.addVertex(matrix, x, y, z).setColor(red, green, blue, alpha);
            index++;
        }
        draw(buffer);

        RenderSystem.lineWidth(2.0F);
        renderEdge(matrix, points, true, red, green, blue);
        renderEdge(matrix, points, false, red, green, blue);
    }

    private void renderEdge(Matrix4f matrix, ArrayDeque<TrailPoint> points, boolean top, int red, int green, int blue) {
        BufferBuilder buffer = Tesselator.getInstance()
                .begin(VertexFormat.Mode.DEBUG_LINE_STRIP, DefaultVertexFormat.POSITION_COLOR);
        int count = points.size();
        int index = 0;
        for (TrailPoint point : points) {
            int alpha = (int) (Math.min(index / (float) count * 1.5F, 1.0F) * 255.0F);
            float y = top ? (float) point.y() + point.height() : (float) point.y();
            buffer.addVertex(matrix, (float) point.x(), y, (float) point.z()).setColor(red, green, blue, alpha);
            index++;
        }
        draw(buffer);
    }

    private void draw(BufferBuilder buffer) {
        MeshData mesh = buffer.build();
        if (mesh != null) {
            BufferUploader.drawWithShader(mesh);
        }
    }

    private int resolveColor() {
        if (colorMode.is("Свой")) {
            return ColorUtils.rgb((int) customRed.get(), (int) customGreen.get(), (int) customBlue.get());
        }
        return ThemeManager.getColor();
    }

    private record TrailPoint(double x, double y, double z, float height, long time) {
    }
}

package godweer.modules.impl.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;

import godweer.managers.FriendManager;
import godweer.managers.ModuleManager;
import godweer.managers.ThemeManager;
import godweer.modules.Module;
import godweer.modules.ModuleCategory;
import godweer.modules.api.setting.BooleanSetting;
import godweer.utils.color.ColorUtils;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.CoreShaders;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.phys.Vec3;

import org.joml.Matrix4f;

public final class ChinaHat extends Module {

    private static final int SEGMENTS = 90;
    private static final float SEGMENT_STEP = (float) (Math.PI * 2.0 / SEGMENTS);
    private static final float CONE_HEIGHT = 0.3F;
    private static final float GLOW_SKIRT = 0.16F;
    private static final float GLOW_DROP = 0.06F;
    private static final float MAX_PITCH_TILT = 30.0F;
    private static final float PITCH_TILT_SCALE = 0.35F;
    private static final float SHIMMER_SPEED_MS = 2600.0F;

    private static boolean eventsRegistered;

    private final BooleanSetting showOthers = new BooleanSetting("На Других", true);
    private final BooleanSetting showFriends = new BooleanSetting("На Друзьях", true);

    public ChinaHat() {
        super("ChinaHat", "Китайская шляпа над головой", ModuleCategory.RENDER);
        addSettings(showOthers, showFriends);
        registerEvents();
    }

    private static void registerEvents() {
        if (eventsRegistered) {
            return;
        }
        eventsRegistered = true;
        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            ChinaHat module = ModuleManager.get(ChinaHat.class);
            if (module != null && module.isEnable()) {
                module.onWorldRender(context);
            }
        });
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

        float partialTick = context.tickCounter().getGameTimeDeltaPartialTick(true);
        Vec3 camera = context.camera().getPosition();

        int baseColor = ThemeManager.getColor();
        int accentColor = ThemeManager.getColor(1);
        // Фаза переливания общая на кадр: цвет "течёт" по окружности конуса.
        float shimmerPhase = (System.currentTimeMillis() % (long) SHIMMER_SPEED_MS) / SHIMMER_SPEED_MS;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(CoreShaders.POSITION_COLOR);

        for (AbstractClientPlayer player : minecraft.level.players()) {
            if (!shouldRender(minecraft, player)) {
                continue;
            }
            renderHat(poseStack, camera, player, partialTick, baseColor, accentColor, shimmerPhase);
        }

        RenderSystem.depthMask(true);
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    private boolean shouldRender(Minecraft minecraft, AbstractClientPlayer player) {
        if (!player.isAlive() || player.isSpectator() || player.isInvisible()) {
            return false;
        }
        if (player == minecraft.player) {
            return !minecraft.options.getCameraType().isFirstPerson();
        }
        return FriendManager.isFriend(player) ? showFriends.get() : showOthers.get();
    }

    private void renderHat(PoseStack poseStack, Vec3 camera, AbstractClientPlayer player, float partialTick,
                           int baseColor, int accentColor, float shimmerPhase) {
        double x = Mth.lerp(partialTick, player.xo, player.getX());
        double y = Mth.lerp(partialTick, player.yo, player.getY());
        double z = Mth.lerp(partialTick, player.zo, player.getZ());

        float headYaw = Mth.lerp(partialTick, player.yHeadRotO, player.yHeadRot);
        float pitch = Mth.lerp(partialTick, player.xRotO, player.getXRot());
        // Полный pitch при взгляде вниз кладёт конус ребром к камере — шляпа выглядит плоской.
        float tilt = Mth.clamp(pitch * PITCH_TILT_SCALE, -MAX_PITCH_TILT, MAX_PITCH_TILT);

        float eyeHeight = player.getEyeHeight();
        float hatOffset = player.getItemBySlot(EquipmentSlot.HEAD).isEmpty() ? 0.415F : 0.48F;
        float radius = (float) (player.getBoundingBox().maxX - player.getBoundingBox().minX);

        poseStack.pushPose();
        poseStack.translate(x - camera.x, y + eyeHeight - camera.y, z - camera.z);
        poseStack.mulPose(Axis.YP.rotationDegrees(-headYaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(tilt));
        poseStack.translate(0.0F, hatOffset, 0.0F);

        Matrix4f matrix = poseStack.last().pose();
        Tesselator tesselator = Tesselator.getInstance();

        RenderSystem.defaultBlendFunc();
        BufferBuilder buffer = tesselator.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);
        for (int i = 0; i <= SEGMENTS; i++) {
            float angle = i * SEGMENT_STEP;
            float px = Mth.sin(angle) * radius;
            float pz = Mth.cos(angle) * radius;
            int rimColor = shimmer(baseColor, accentColor, i, shimmerPhase, 150);
            int apexColor = shimmer(accentColor, baseColor, i, shimmerPhase, 235);
            buffer.addVertex(matrix, px, 0.0F, pz).setColor(rimColor);
            buffer.addVertex(matrix, 0.0F, CONE_HEIGHT, 0.0F).setColor(apexColor);
        }
        draw(buffer);

        // Аддитивная "юбка" от кромки вниз-наружу: даёт мягкое свечение вместо жёсткого контура.
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        buffer = tesselator.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);
        float glowRadius = radius + GLOW_SKIRT;
        for (int i = 0; i <= SEGMENTS; i++) {
            float angle = i * SEGMENT_STEP;
            float sin = Mth.sin(angle);
            float cos = Mth.cos(angle);
            int glowColor = shimmer(baseColor, accentColor, i, shimmerPhase, 110);
            buffer.addVertex(matrix, sin * radius, 0.0F, cos * radius).setColor(glowColor);
            buffer.addVertex(matrix, sin * glowRadius, -GLOW_DROP, cos * glowRadius)
                    .setColor(ColorUtils.setAlphaColor(glowColor, 0));
        }
        draw(buffer);

        buffer = tesselator.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);
        for (int i = 0; i <= SEGMENTS; i++) {
            float angle = i * SEGMENT_STEP;
            float px = Mth.sin(angle) * radius;
            float pz = Mth.cos(angle) * radius;
            int rimGlow = shimmer(accentColor, baseColor, i, shimmerPhase, 90);
            buffer.addVertex(matrix, px, 0.0F, pz).setColor(rimGlow);
            buffer.addVertex(matrix, 0.0F, CONE_HEIGHT, 0.0F).setColor(ColorUtils.setAlphaColor(rimGlow, 25));
        }
        draw(buffer);

        poseStack.popPose();
    }

    // Синусоидальная волна цвета по сегментам: плавное переливание base <-> accent без резких швов на стыке 0/2π.
    private int shimmer(int from, int to, int segment, float phase, int alpha) {
        float wave = Mth.sin((segment / (float) SEGMENTS + phase) * Mth.TWO_PI) * 0.5F + 0.5F;
        return ColorUtils.setAlphaColor(ColorUtils.interpolate(from, to, wave), alpha);
    }

    private void draw(BufferBuilder buffer) {
        MeshData mesh = buffer.build();
        if (mesh != null) {
            BufferUploader.drawWithShader(mesh);
        }
    }
}

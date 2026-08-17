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
<<<<<<< Updated upstream
import godweer.utils.color.ColorUtils;
=======
import godweer.modules.api.setting.FloatSetting;
import godweer.modules.api.setting.ModeSetting;
import godweer.utils.color.ColorUtils;
import godweer.utils.render.ShaderUtils;
>>>>>>> Stashed changes

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
<<<<<<< Updated upstream
import net.minecraft.client.renderer.CoreShaders;
=======
import net.minecraft.client.renderer.CompiledShaderProgram;
>>>>>>> Stashed changes
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.phys.Vec3;

import org.joml.Matrix4f;

public final class ChinaHat extends Module {

    private static final int SEGMENTS = 90;
    private static final float SEGMENT_STEP = (float) (Math.PI * 2.0 / SEGMENTS);
<<<<<<< Updated upstream
    private static final float CONE_HEIGHT = 0.3F;
=======
>>>>>>> Stashed changes
    private static final float GLOW_SKIRT = 0.16F;
    private static final float GLOW_DROP = 0.06F;
    private static final float MAX_PITCH_TILT = 30.0F;
    private static final float PITCH_TILT_SCALE = 0.35F;
<<<<<<< Updated upstream
    private static final float SHIMMER_SPEED_MS = 2600.0F;

    private static boolean eventsRegistered;

=======

    private static boolean eventsRegistered;

<<<<<<< HEAD
    private final ModeSetting mode = new ModeSetting("Режим", "Дыхание ветра", "Градиент", "Дыхание ветра", "Квантовый");
=======
    private final ModeSetting mode = new ModeSetting("Режим", "Дыхание ветра", "Градиент", "Дыхание ветра", "Квантовый", "Голограмма Never");
>>>>>>> 49e82c67882decdf97540abd3c386d782f08425e
    private final ModeSetting colorMode = new ModeSetting("Окрас", "Тема", "Тема", "Радуга", "Астрал");
    private final FloatSetting speed = new FloatSetting("Скорость", 1.5F, 0.1F, 5.0F, 0.1F);
    private final FloatSetting glowStrength = new FloatSetting("Свечение", 1.0F, 0.0F, 3.0F, 0.1F);
    private final FloatSetting frequency = new FloatSetting("Частота", 2.0F, 0.5F, 15.0F, 0.1F);
    private final FloatSetting windStrength = new FloatSetting("Сила ветра", 1.5F, 0.0F, 5.0F, 0.1F).visible(() -> mode.is("Дыхание ветра"));
    private final FloatSetting coneDepth = new FloatSetting("Прозрачность", 0.8F, 0.1F, 1.0F, 0.05F);
    private final FloatSetting coneHeight = new FloatSetting("Высота шляпы", 0.3F, 0.1F, 1.0F, 0.05F);
    private final FloatSetting radiusOffset = new FloatSetting("Доп. Радиус", 0.0F, -0.3F, 0.5F, 0.05F);
>>>>>>> Stashed changes
    private final BooleanSetting showOthers = new BooleanSetting("На Других", true);
    private final BooleanSetting showFriends = new BooleanSetting("На Друзьях", true);

    public ChinaHat() {
        super("ChinaHat", "Китайская шляпа над головой", ModuleCategory.RENDER);
<<<<<<< Updated upstream
        addSettings(showOthers, showFriends);
=======
        addSettings(mode, colorMode, speed, glowStrength, frequency, windStrength, coneDepth, coneHeight, radiusOffset, showOthers, showFriends);
>>>>>>> Stashed changes
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

<<<<<<< Updated upstream
=======
        CompiledShaderProgram shader = ShaderUtils.get(ShaderUtils.CHINA_HAT);
        if (shader == null) {
            return;
        }

>>>>>>> Stashed changes
        float partialTick = context.tickCounter().getGameTimeDeltaPartialTick(true);
        Vec3 camera = context.camera().getPosition();

        int baseColor = ThemeManager.getColor();
        int accentColor = ThemeManager.getColor(1);
<<<<<<< Updated upstream
        // Фаза переливания общая на кадр: цвет "течёт" по окружности конуса.
        float shimmerPhase = (System.currentTimeMillis() % (long) SHIMMER_SPEED_MS) / SHIMMER_SPEED_MS;
=======
>>>>>>> Stashed changes

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(false);
<<<<<<< Updated upstream
        RenderSystem.setShader(CoreShaders.POSITION_COLOR);
=======

        RenderSystem.setShader(ShaderUtils.CHINA_HAT);

        float timeSec = (System.currentTimeMillis() % 1000000L) / 1000.0F;
        ShaderUtils.set(shader, "Time", timeSec);
        ShaderUtils.set(shader, "Speed", speed.get());
        ShaderUtils.set(shader, "Frequency", frequency.get());
        ShaderUtils.set(shader, "Glow", glowStrength.get());
        ShaderUtils.set(shader, "ConeDepth", coneDepth.get());

        // Используем силу ветра в качестве параметра сдува (OutlineSize в шейдере)
        float wind = windStrength.get();
        ShaderUtils.set(shader, "OutlineSize", wind);

        int modeValue = 0;
        if (mode.is("Дыхание ветра")) {
            modeValue = 1;
        } else if (mode.is("Квантовый")) {
            modeValue = 2;
<<<<<<< HEAD
=======
        } else if (mode.is("Голограмма Never")) {
            modeValue = 3;
>>>>>>> 49e82c67882decdf97540abd3c386d782f08425e
        }
        ShaderUtils.set(shader, "Mode", modeValue);

        int colorModeValue = 0;
        if (colorMode.is("Радуга")) {
            colorModeValue = 1;
        } else if (colorMode.is("Астрал")) {
            colorModeValue = 2;
        }
        ShaderUtils.set(shader, "ColorMode", colorModeValue);

        ShaderUtils.setColor(shader, "BaseColor", baseColor);
        ShaderUtils.setColor(shader, "AccentColor", accentColor);

        // Рассчитываем физическое покачивание шляпы на ветру
        float swayX = 0.0F;
        float swayZ = 0.0F;
        if (mode.is("Дыхание ветра")) {
            swayX = Mth.sin(timeSec * speed.get() * 1.2F) * wind * 2.2F;
            swayZ = Mth.cos(timeSec * speed.get() * 0.9F) * wind * 1.8F;
        }
>>>>>>> Stashed changes

        for (AbstractClientPlayer player : minecraft.level.players()) {
            if (!shouldRender(minecraft, player)) {
                continue;
            }
<<<<<<< Updated upstream
            renderHat(poseStack, camera, player, partialTick, baseColor, accentColor, shimmerPhase);
=======
            renderHat(poseStack, camera, player, partialTick, swayX, swayZ);
>>>>>>> Stashed changes
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

<<<<<<< Updated upstream
    private void renderHat(PoseStack poseStack, Vec3 camera, AbstractClientPlayer player, float partialTick,
                           int baseColor, int accentColor, float shimmerPhase) {
=======
    private void renderHat(PoseStack poseStack, Vec3 camera, AbstractClientPlayer player, float partialTick, float swayX, float swayZ) {
>>>>>>> Stashed changes
        double x = Mth.lerp(partialTick, player.xo, player.getX());
        double y = Mth.lerp(partialTick, player.yo, player.getY());
        double z = Mth.lerp(partialTick, player.zo, player.getZ());

        float headYaw = Mth.lerp(partialTick, player.yHeadRotO, player.yHeadRot);
        float pitch = Mth.lerp(partialTick, player.xRotO, player.getXRot());
<<<<<<< Updated upstream
        // Полный pitch при взгляде вниз кладёт конус ребром к камере — шляпа выглядит плоской.
=======
>>>>>>> Stashed changes
        float tilt = Mth.clamp(pitch * PITCH_TILT_SCALE, -MAX_PITCH_TILT, MAX_PITCH_TILT);

        float eyeHeight = player.getEyeHeight();
        float hatOffset = player.getItemBySlot(EquipmentSlot.HEAD).isEmpty() ? 0.415F : 0.48F;
<<<<<<< Updated upstream
        float radius = (float) (player.getBoundingBox().maxX - player.getBoundingBox().minX);
=======
        float radius = (float) (player.getBoundingBox().maxX - player.getBoundingBox().minX) + radiusOffset.get();
        float height = coneHeight.get();
>>>>>>> Stashed changes

        poseStack.pushPose();
        poseStack.translate(x - camera.x, y + eyeHeight - camera.y, z - camera.z);
        poseStack.mulPose(Axis.YP.rotationDegrees(-headYaw));
<<<<<<< Updated upstream
        poseStack.mulPose(Axis.XP.rotationDegrees(tilt));
=======
        poseStack.mulPose(Axis.XP.rotationDegrees(tilt + swayX));
        poseStack.mulPose(Axis.ZP.rotationDegrees(swayZ));
>>>>>>> Stashed changes
        poseStack.translate(0.0F, hatOffset, 0.0F);

        Matrix4f matrix = poseStack.last().pose();
        Tesselator tesselator = Tesselator.getInstance();

        RenderSystem.defaultBlendFunc();
<<<<<<< Updated upstream
        BufferBuilder buffer = tesselator.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);
=======
        BufferBuilder buffer = tesselator.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_TEX_COLOR);
>>>>>>> Stashed changes
        for (int i = 0; i <= SEGMENTS; i++) {
            float angle = i * SEGMENT_STEP;
            float px = Mth.sin(angle) * radius;
            float pz = Mth.cos(angle) * radius;
<<<<<<< Updated upstream
            int rimColor = shimmer(baseColor, accentColor, i, shimmerPhase, 150);
            int apexColor = shimmer(accentColor, baseColor, i, shimmerPhase, 235);
            buffer.addVertex(matrix, px, 0.0F, pz).setColor(rimColor);
            buffer.addVertex(matrix, 0.0F, CONE_HEIGHT, 0.0F).setColor(apexColor);
        }
        draw(buffer);

        // Аддитивная "юбка" от кромки вниз-наружу: даёт мягкое свечение вместо жёсткого контура.
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        buffer = tesselator.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);
=======
            float u = (float) i / SEGMENTS;
            buffer.addVertex(matrix, px, 0.0F, pz).setUv(u, 0.0F).setColor(255, 255, 255, 255);
            buffer.addVertex(matrix, 0.0F, height, 0.0F).setUv(u, 1.0F).setColor(255, 255, 255, 255);
        }
        draw(buffer);

        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        buffer = tesselator.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_TEX_COLOR);
>>>>>>> Stashed changes
        float glowRadius = radius + GLOW_SKIRT;
        for (int i = 0; i <= SEGMENTS; i++) {
            float angle = i * SEGMENT_STEP;
            float sin = Mth.sin(angle);
            float cos = Mth.cos(angle);
<<<<<<< Updated upstream
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
=======
            float u = (float) i / SEGMENTS;
            buffer.addVertex(matrix, sin * radius, 0.0F, cos * radius).setUv(u, 0.0F).setColor(255, 255, 255, 255);
            buffer.addVertex(matrix, sin * glowRadius, -GLOW_DROP, cos * glowRadius).setUv(u, -1.0F).setColor(255, 255, 255, 255);
>>>>>>> Stashed changes
        }
        draw(buffer);

        poseStack.popPose();
    }

<<<<<<< Updated upstream
    // Синусоидальная волна цвета по сегментам: плавное переливание base <-> accent без резких швов на стыке 0/2π.
    private int shimmer(int from, int to, int segment, float phase, int alpha) {
        float wave = Mth.sin((segment / (float) SEGMENTS + phase) * Mth.TWO_PI) * 0.5F + 0.5F;
        return ColorUtils.setAlphaColor(ColorUtils.interpolate(from, to, wave), alpha);
    }

=======
>>>>>>> Stashed changes
    private void draw(BufferBuilder buffer) {
        MeshData mesh = buffer.build();
        if (mesh != null) {
            BufferUploader.drawWithShader(mesh);
        }
    }
}

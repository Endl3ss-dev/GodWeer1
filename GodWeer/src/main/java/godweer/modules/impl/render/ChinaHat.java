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
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.phys.Vec3;

import org.joml.Matrix4f;

public final class ChinaHat extends Module {

    private static final int SEGMENTS = 90;
    private static final float SEGMENT_STEP = (float) (Math.PI * 2.0 / SEGMENTS);
    private static final float CONE_HEIGHT = 0.3F;

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
    private final BooleanSetting showSelf = new BooleanSetting("Себе", true);

    public ChinaHat() {
        super("ChinaHat", "Китайская шляпа над головой", ModuleCategory.RENDER);
        addSettings(colorMode, customRed, customGreen, customBlue, showOthers, showFriends, showSelf);
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

        int baseColor = resolveColor();
        int coneColor = ColorUtils.setAlphaColor(baseColor, 125);
        int outlineColor = ColorUtils.setAlphaColor(ColorUtils.darken(baseColor, 0.5F), 180);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.setShader(CoreShaders.POSITION_COLOR);
        RenderSystem.lineWidth(2.0F);

        for (AbstractClientPlayer player : minecraft.level.players()) {
            if (!shouldRender(minecraft, player)) {
                continue;
            }
            renderHat(poseStack, camera, player, partialTick, baseColor, coneColor, outlineColor);
        }

        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    private boolean shouldRender(Minecraft minecraft, AbstractClientPlayer player) {
        if (!player.isAlive() || player.isSpectator() || player.isInvisible()) {
            return false;
        }
        if (player == minecraft.player) {
            return showSelf.get() && !minecraft.options.getCameraType().isFirstPerson();
        }
        return FriendManager.isFriend(player) ? showFriends.get() : showOthers.get();
    }

    private void renderHat(PoseStack poseStack, Vec3 camera, AbstractClientPlayer player, float partialTick,
                           int baseColor, int coneColor, int outlineColor) {
        double x = Mth.lerp(partialTick, player.xo, player.getX());
        double y = Mth.lerp(partialTick, player.yo, player.getY());
        double z = Mth.lerp(partialTick, player.zo, player.getZ());

        float headYaw = Mth.lerp(partialTick, player.yHeadRotO, player.yHeadRot);
        float pitch = Mth.lerp(partialTick, player.xRotO, player.getXRot());

        float eyeHeight = player.getEyeHeight();
        float hatOffset = player.getItemBySlot(EquipmentSlot.HEAD).isEmpty() ? 0.415F : 0.48F;
        float radius = (float) (player.getBoundingBox().maxX - player.getBoundingBox().minX);

        poseStack.pushPose();
        poseStack.translate(x - camera.x, y + eyeHeight - camera.y, z - camera.z);
        // Поворот в системе головы: сначала yaw, затем наклон pitch — шляпа следует за взглядом.
        poseStack.mulPose(Axis.YP.rotationDegrees(-headYaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(pitch));
        poseStack.translate(0.0F, hatOffset, 0.0F);

        Matrix4f matrix = poseStack.last().pose();
        Tesselator tesselator = Tesselator.getInstance();

        BufferBuilder buffer = tesselator.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);
        for (int i = 0; i <= SEGMENTS; i++) {
            float angle = i * SEGMENT_STEP;
            float px = Mth.sin(angle) * radius;
            float pz = Mth.cos(angle) * radius;
            buffer.addVertex(matrix, px, 0.0F, pz).setColor(coneColor);
            buffer.addVertex(matrix, 0.0F, CONE_HEIGHT, 0.0F).setColor(baseColor);
        }
        draw(buffer);

        RenderSystem.depthMask(false);
        buffer = tesselator.begin(VertexFormat.Mode.DEBUG_LINE_STRIP, DefaultVertexFormat.POSITION_COLOR);
        for (int i = 0; i <= SEGMENTS; i++) {
            float angle = i * SEGMENT_STEP;
            buffer.addVertex(matrix, Mth.sin(angle) * radius, 0.0F, Mth.cos(angle) * radius).setColor(outlineColor);
        }
        draw(buffer);
        RenderSystem.depthMask(true);

        poseStack.popPose();
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
}

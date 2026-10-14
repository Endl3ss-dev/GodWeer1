package swerve.utils.render.spatial;

import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;

import swerve.mixins.accessor.GameRendererAccessor;
import swerve.modules.impl.render.SpatialGui;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.CoreShaders;
import net.minecraft.util.Mth;

import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

/**
 * Панель в мире: положение относительно камеры, анимация открытия/закрытия, меш (плоский или
 * изогнутый) и его отрисовка с перспективой уровня. Базисы панели нужны и для маппинга курсора.
 */
public final class ScreenSurface {

    private static final int CURVED_SEGMENTS = 32;

    private static final PoseStack WORLD_POSE = new PoseStack();
    private static final PoseStack CAPTURED_POSE = new PoseStack();
    private static final Matrix4f CAPTURED_PROJECTION = new Matrix4f();
    private static final Quaternionf ROTATION = new Quaternionf();

    static final Vector3f QUAD_CENTER = new Vector3f();
    static final Vector3f QUAD_RIGHT = new Vector3f();
    static final Vector3f QUAD_UP = new Vector3f();
    static final Vector3f QUAD_NORMAL = new Vector3f();
    static float quadHalfWidth;
    static float quadHalfHeight;
    static boolean quadValid;

    static final Vector3f CYLINDER_AXIS = new Vector3f();
    static final Vector3f CYLINDER_DIR = new Vector3f();
    static final Vector3f CYLINDER_REF = new Vector3f();
    static final Vector3f CYLINDER_CENTER = new Vector3f();
    static float cylinderRadius;
    static float cylinderArc;
    static float cylinderHalfHeight;
    static boolean cylinderValid;

    private static float poseScale;
    private static float poseAspect;

    private static long screenOpenTime;
    private static boolean recipeBookOpen;
    private static int recipeBookCloseDelay;

    private static boolean hasCapturedPerspective;
    private static boolean drawnThisFrame;

    private static boolean closing;
    private static long closeStartTime;
    private static long closeStartElapsed;
    private static long closeDurationMs;
    private static boolean closeFirstPerson;
    private static float closeYaw;
    private static float closePitch;

    private ScreenSurface() {
    }

    public static void onScreenOpened(long time) {
        screenOpenTime = time;
        quadValid = false;
        cylinderValid = false;
    }

    public static void onFrameStart() {
        drawnThisFrame = false;
    }

    public static void setRecipeBookOpen(boolean open) {
        if (recipeBookOpen && !open) {
            recipeBookCloseDelay = -2;
        }
        recipeBookOpen = open;
    }

    public static void resetRecipeBook() {
        recipeBookOpen = false;
        recipeBookCloseDelay = 0;
    }

    public static void resetPerspective() {
        hasCapturedPerspective = false;
        drawnThisFrame = false;
    }

    public static void capturePerspective(Matrix4f projection, Matrix4f modelView) {
        CAPTURED_PROJECTION.set(projection);
        CAPTURED_POSE.setIdentity();
        CAPTURED_POSE.mulPose(modelView);
        hasCapturedPerspective = true;
    }

    public static boolean isClosing() {
        return closing;
    }

    public static boolean isClosingFirstPerson() {
        return closeFirstPerson;
    }

    public static long closeElapsedMs() {
        return Math.max(0L, System.currentTimeMillis() - closeStartTime);
    }

    public static boolean isCloseFinished() {
        return System.currentTimeMillis() - closeStartTime >= closeDurationMs;
    }

    public static void endClosing() {
        closing = false;
    }

    private static long animationTotalMs() {
        long total = SpatialGui.FADE_MS;
        SpatialGui module = SpatialGui.get();
        if (module != null && module.openAnimation()) {
            total = Math.max(total, SpatialGui.OPEN_MS);
        }
        return total;
    }

    public static boolean beginClosing() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (!hasCapturedPerspective || player == null || SpatialTarget.get() == null) {
            return false;
        }
        long now = System.currentTimeMillis();
        boolean firstPerson = SpatialState.effectiveFirstPerson;
        long animationMs = Math.min(animationTotalMs(), Math.max(0L, now - screenOpenTime));
        long cameraMs = firstPerson ? 0L : Math.round(SpatialGui.TRANSITION_MS * (1.0F - SpatialGui.TRANSITION_SKIP));
        long duration = Math.max(animationMs, cameraMs);
        if (duration <= 0L) {
            return false;
        }
        closing = true;
        closeStartTime = now;
        closeStartElapsed = animationMs;
        closeDurationMs = duration;
        closeFirstPerson = firstPerson;
        closeYaw = player.getYRot();
        closePitch = player.getXRot();
        drawnThisFrame = false;
        return true;
    }

    private static boolean firstPersonNow() {
        return closing ? closeFirstPerson : SpatialState.effectiveFirstPerson;
    }

    private static boolean canDraw() {
        return closing ? animationElapsedMs() > 0L : SpatialState.shouldCapture();
    }

    private static long animationElapsedMs() {
        long now = System.currentTimeMillis();
        if (!closing) {
            return now - screenOpenTime;
        }
        return Math.max(0L, closeStartElapsed - (now - closeStartTime));
    }

    private static float fadeAlpha() {
        return Math.min(1.0F, animationElapsedMs() / (float) SpatialGui.FADE_MS);
    }

    private static float animatedScale(float baseScale, long elapsedMs) {
        int updatedDelay = recipeBookOpen ? -2 : Math.min(0, recipeBookCloseDelay + 1);
        boolean shrink = recipeBookOpen || updatedDelay < 0;
        float scale = shrink ? baseScale / SpatialGui.RECIPE_BOOK_SHRINK : baseScale;
        SpatialGui module = SpatialGui.get();
        if (module != null && module.openAnimation()) {
            float progress = Math.min(1.0F, elapsedMs / (float) SpatialGui.OPEN_MS);
            float eased = (float) module.easing().ease(progress);
            scale = SpatialMath.lerp(scale * SpatialGui.OPEN_START_SCALE, scale, eased);
        }
        return scale;
    }

    private static boolean curvedActive(boolean firstPerson) {
        SpatialGui module = SpatialGui.get();
        if (module == null || !firstPerson || !module.curvedScreen()) {
            return false;
        }
        float arc = module.curvedArcDegrees();
        return arc >= 5.0F && arc <= 150.0F;
    }

    // Масштаб панели следует за FOV: при широком обзоре панель растёт, чтобы занимать ту же долю экрана.
    private static float fovScaleMultiplier() {
        if (!SpatialGui.AUTO_SCALE_BY_FOV) {
            return 1.0F;
        }
        SpatialGui module = SpatialGui.get();
        float currentFov;
        if (module != null && module.overrideFov()) {
            currentFov = module.targetFov();
        } else {
            Minecraft mc = Minecraft.getInstance();
            Camera camera = mc.gameRenderer.getMainCamera();
            currentFov = ((GameRendererAccessor) mc.gameRenderer).swerve$getFov(camera, camera.getPartialTickTime(), true);
        }
        return (float) Math.pow(currentFov / SpatialGui.AUTO_SCALE_BASE_FOV, SpatialGui.AUTO_SCALE_SCREEN_POWER);
    }

    private static void applyTransform(PoseStack matrices, boolean firstPerson, float yawRad, float pitchRad,
                                       float distance, float sideOffset, float heightOffset, float yawOffsetDeg,
                                       float pitchOffsetDeg, double lookX, double lookY, double lookZ) {
        float yawOffsetRad = SpatialMath.rad(yawOffsetDeg);
        float pitchOffsetRad = SpatialMath.rad(pitchOffsetDeg);
        if (firstPerson) {
            matrices.translate(
                    lookX * distance + Math.cos(yawRad) * sideOffset,
                    lookY * distance + heightOffset,
                    lookZ * distance + Math.sin(yawRad) * sideOffset);
            ROTATION.identity().rotateY(-yawRad + yawOffsetRad).rotateX(-pitchRad + pitchOffsetRad);
        } else {
            matrices.translate(
                    -Mth.sin(yawRad) * distance + Math.cos(yawRad) * sideOffset,
                    heightOffset,
                    Mth.cos(yawRad) * distance + Math.sin(yawRad) * sideOffset);
            ROTATION.identity().rotateY(-yawRad + yawOffsetRad).rotateX(pitchOffsetRad);
        }
        matrices.mulPose(ROTATION);
    }

    /** Считает позу панели; при matrices != null применяет её и к переданному стеку. */
    private static void computeScreenPose(PoseStack matrices, boolean firstPerson) {
        LocalPlayer player = Minecraft.getInstance().player;
        SpatialGui module = SpatialGui.get();
        float yaw = closing ? closeYaw : player.getYRot();
        float pitch = firstPerson ? (closing ? closePitch : player.getXRot()) : 0.0F;
        pitch = Mth.clamp(pitch, -SpatialGui.FP_PITCH_CLAMP, SpatialGui.FP_PITCH_CLAMP);
        float yawRad = SpatialMath.rad(yaw);
        float pitchRad = SpatialMath.rad(pitch);

        float fovMultiplier = fovScaleMultiplier();
        float distance;
        float sideOffset;
        float heightOffset;
        float yawOffset;
        float pitchOffset;
        float baseScale;
        if (firstPerson) {
            distance = module.fpScreenDistance();
            sideOffset = SpatialGui.FP_SCREEN_SIDE_OFFSET;
            heightOffset = SpatialGui.FP_SCREEN_HEIGHT_OFFSET;
            yawOffset = SpatialGui.FP_SCREEN_YAW_OFFSET;
            pitchOffset = SpatialGui.FP_SCREEN_PITCH_OFFSET;
            baseScale = module.fpScreenScale() * fovMultiplier;
        } else {
            float thirdPersonMultiplier = 1.0F + (fovMultiplier - 1.0F) * SpatialGui.AUTO_SCALE_TP_SCREEN_MULT;
            sideOffset = SpatialGui.SCREEN_SIDE_OFFSET;
            yawOffset = SpatialGui.SCREEN_YAW_OFFSET;
            if (module.mirrorThirdPerson()) {
                sideOffset = -sideOffset;
                yawOffset = -yawOffset;
            }
            distance = module.screenDistance();
            heightOffset = SpatialGui.SCREEN_HEIGHT_OFFSET;
            pitchOffset = SpatialGui.SCREEN_PITCH_OFFSET;
            baseScale = module.screenScale() * thirdPersonMultiplier;
        }

        double lookX = -Math.sin(yawRad) * Math.cos(pitchRad);
        double lookY = -Math.sin(pitchRad);
        double lookZ = Math.cos(yawRad) * Math.cos(pitchRad);
        float scale = animatedScale(baseScale, animationElapsedMs());

        if (matrices != null) {
            applyTransform(matrices, firstPerson, yawRad, pitchRad, distance, sideOffset, heightOffset, yawOffset, pitchOffset, lookX, lookY, lookZ);
            matrices.scale(scale, scale, scale);
        }
        WORLD_POSE.setIdentity();
        applyTransform(WORLD_POSE, firstPerson, yawRad, pitchRad, distance, sideOffset, heightOffset, yawOffset, pitchOffset, lookX, lookY, lookZ);
        WORLD_POSE.scale(scale, scale, scale);

        TextureTarget target = SpatialTarget.get();
        poseAspect = target != null && target.height > 0 ? (float) target.width / (float) target.height : 1.0F;
        poseScale = scale;
    }

    private static void updateBases(boolean firstPerson) {
        Matrix4f worldPose = WORLD_POSE.last().pose();
        QUAD_CENTER.set(0.0F, 0.0F, 0.0F);
        worldPose.transformPosition(QUAD_CENTER);
        worldPose.transformDirection(1.0F, 0.0F, 0.0F, QUAD_RIGHT).normalize();
        worldPose.transformDirection(0.0F, 1.0F, 0.0F, QUAD_UP).normalize();
        QUAD_NORMAL.set(QUAD_RIGHT).cross(QUAD_UP).normalize();
        quadHalfWidth = poseAspect * 0.5F * poseScale;
        quadHalfHeight = 0.5F * poseScale;
        quadValid = true;

        if (curvedActive(firstPerson)) {
            float arc = SpatialMath.rad(SpatialGui.get().curvedArcDegrees());
            float radiusLocal = poseAspect / arc;
            CYLINDER_AXIS.set(0.0F, 0.0F, radiusLocal);
            worldPose.transformPosition(CYLINDER_AXIS);
            CYLINDER_CENTER.set(0.0F, 0.0F, 0.0F);
            worldPose.transformPosition(CYLINDER_CENTER);
            worldPose.transformDirection(0.0F, 1.0F, 0.0F, CYLINDER_DIR).normalize();
            worldPose.transformDirection(0.0F, 0.0F, -1.0F, CYLINDER_REF).normalize();
            cylinderRadius = radiusLocal * poseScale;
            cylinderArc = arc;
            cylinderHalfHeight = 0.5F * poseScale;
            cylinderValid = true;
        } else {
            cylinderValid = false;
        }
    }

    /** Базисы для маппинга курсора; до первой отрисовки считаются по текущей позе игрока. */
    public static boolean ensureBases() {
        if (quadValid) {
            return true;
        }
        if (Minecraft.getInstance().player == null || SpatialGui.get() == null) {
            return false;
        }
        boolean firstPerson = SpatialState.effectiveFirstPerson;
        computeScreenPose(null, firstPerson);
        updateBases(firstPerson);
        return quadValid;
    }

    private static void addVertex(VertexConsumer buffer, Matrix4f pose, float x, float y, float z, float u, float v) {
        buffer.addVertex(pose, x, y, z).setUv(u, v).setColor(255, 255, 255, SpatialGui.SCREEN_ALPHA);
    }

    private static void addMesh(VertexConsumer buffer, Matrix4f pose, float aspect, boolean firstPerson) {
        if (curvedActive(firstPerson)) {
            float arc = SpatialMath.rad(SpatialGui.get().curvedArcDegrees());
            float radius = aspect / arc;
            float halfArc = arc * 0.5F;
            float prevX = radius * (float) Math.sin(-halfArc);
            float prevZ = radius * (1.0F - (float) Math.cos(-halfArc));
            float prevU = 0.0F;
            for (int i = 1; i <= CURVED_SEGMENTS; i++) {
                float angle = -halfArc + arc * (i / (float) CURVED_SEGMENTS);
                float x = radius * (float) Math.sin(angle);
                float z = radius * (1.0F - (float) Math.cos(angle));
                float u = i / (float) CURVED_SEGMENTS;
                addVertex(buffer, pose, prevX, -0.5F, prevZ, prevU, 0.0F);
                addVertex(buffer, pose, x, -0.5F, z, u, 0.0F);
                addVertex(buffer, pose, x, 0.5F, z, u, 1.0F);
                addVertex(buffer, pose, prevX, 0.5F, prevZ, prevU, 1.0F);
                prevX = x;
                prevZ = z;
                prevU = u;
            }
            return;
        }
        float halfWidth = aspect * 0.5F;
        addVertex(buffer, pose, -halfWidth, -0.5F, 0.0F, 0.0F, 0.0F);
        addVertex(buffer, pose, halfWidth, -0.5F, 0.0F, 1.0F, 0.0F);
        addVertex(buffer, pose, halfWidth, 0.5F, 0.0F, 1.0F, 1.0F);
        addVertex(buffer, pose, -halfWidth, 0.5F, 0.0F, 0.0F, 1.0F);
    }

    private static void renderInWorld(PoseStack matrices) {
        Minecraft mc = Minecraft.getInstance();
        TextureTarget target = SpatialTarget.get();
        if (target == null || !canDraw() || mc.player == null || SpatialGui.get() == null) {
            return;
        }
        int textureId = target.getColorTextureId();
        if (textureId <= 0) {
            return;
        }

        matrices.pushPose();
        boolean firstPerson = firstPersonNow();
        computeScreenPose(matrices, firstPerson);
        recipeBookCloseDelay = recipeBookOpen ? -2 : Math.min(0, recipeBookCloseDelay + 1);

        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        addMesh(buffer, matrices.last().pose(), poseAspect, firstPerson);
        updateBases(firstPerson);
        matrices.popPose();

        draw(buffer.buildOrThrow(), textureId);
    }

    private static void draw(MeshData mesh, int textureId) {
        float alpha = fadeAlpha();
        int filter = SpatialGui.LINEAR_FILTER ? GL11.GL_LINEAR : GL11.GL_NEAREST;

        Minecraft.getInstance().getMainRenderTarget().bindWrite(true);
        RenderSystem.setShader(CoreShaders.POSITION_TEX_COLOR);
        RenderSystem.setShaderTexture(0, textureId);
        RenderSystem.bindTexture(textureId);
        GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, filter);
        GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, filter);
        GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

        RenderSystem.setShaderColor(alpha, alpha, alpha, alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        try {
            BufferUploader.drawWithShader(mesh);
        } finally {
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderSystem.enableCull();
            RenderSystem.disableBlend();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    /** Рисует панель с перспективой, снятой в LevelRenderer.renderLevel; не чаще одного раза за кадр. */
    public static void renderInWorldPost() {
        Minecraft mc = Minecraft.getInstance();
        TextureTarget target = SpatialTarget.get();
        if (!hasCapturedPerspective || drawnThisFrame || target == null || mc.player == null) {
            return;
        }
        if (target.getColorTextureId() <= 0) {
            return;
        }
        drawnThisFrame = true;

        Matrix4f previousProjection = RenderSystem.getProjectionMatrix();
        ProjectionType previousType = RenderSystem.getProjectionType();
        Matrix4fStack modelView = RenderSystem.getModelViewStack();
        try {
            RenderSystem.setProjectionMatrix(CAPTURED_PROJECTION, ProjectionType.PERSPECTIVE);
            modelView.pushMatrix();
            modelView.identity();
            renderInWorld(CAPTURED_POSE);
        } finally {
            modelView.popMatrix();
            RenderSystem.setProjectionMatrix(previousProjection, previousType);
        }
    }
}

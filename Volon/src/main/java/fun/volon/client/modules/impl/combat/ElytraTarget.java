package fun.volon.client.modules.impl.combat;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import fun.volon.api.utils.namespaced.Namespaced;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import fun.volon.Volon;
import fun.volon.api.events.EventLink;
import fun.volon.api.events.Priority;
import fun.volon.api.events.implement.Event3DRender;
import fun.volon.api.events.implement.EventRender;
import fun.volon.api.storages.implement.helpertstorages.enumvar.ModuleClass;
import fun.volon.api.utils.color.ColorUtils;
import fun.volon.api.utils.combat.PredictUtils;
import fun.volon.api.utils.render.BlockOverlayShaderRenderer;
import fun.volon.client.modules.Module;
import fun.volon.client.modules.settings.implement.BooleanSetting;
import fun.volon.client.modules.settings.implement.FloatSetting;
import fun.volon.client.modules.settings.implement.ModeSetting;

public class ElytraTarget extends Module {

    public static ElytraTarget INSTANCE = new ElytraTarget();

    private static final Identifier GLOW_TEXTURE = Namespaced.of("textures/trajectories/glow.png");
    private static final float LINE_GLOW_THICKNESS = 0.055f;
    private static final float LINE_GLOW_LINE_U = 0.4f;
    private static final int TRAJECTORY_SAMPLES = 14;

    private final BlockOverlayShaderRenderer overlayRenderer = new BlockOverlayShaderRenderer();
    private Box smoothedPredictionBox;
    private LivingEntity smoothedTarget;
    private int cachedThemeColor1 = 0xFFFFFFFF;
    private int cachedThemeColor2 = 0xFFFFFFFF;

    public final FloatSetting forward = new FloatSetting("Сила предикта", 3f, 1, 6, 1);
    public final ModeSetting displacementMode = new ModeSetting("Режим смещения", "Default", "Default", "ReallyWorld", "LegacyWorld");
    public final ModeSetting boxMode = new ModeSetting("Бокс", "Шейдер", "Шейдер", "Обычный");
    public final BooleanSetting trajectory = new BooleanSetting("Траектория", true);

    public ElytraTarget() {
        super("Predict", "Таргетит игрока на элитрах", ModuleCategory.MOVEMENT);
        addSettings(forward, displacementMode, boxMode, trajectory);
    }

    @EventLink(priority = Priority.LOW)
    public void onRender3D(Event3DRender event) {
        if (mc.player == null || mc.world == null) {
            resetPredictionState();
            return;
        }
        if (!mc.player.isGliding()) {
            resetPredictionState();
            return;
        }

        Aura aura = ModuleClass.aura;
        if (aura == null || !aura.isEnable()) {
            resetPredictionState();
            return;
        }

        LivingEntity target = aura.getTarget();
        if (target == null || !target.isAlive() || !target.isGliding()) {
            resetPredictionState();
            return;
        }

        Box predictedBox = buildPredictedBox(target);
        Box box = smoothPredictionBox(target, predictedBox);
        Vec3d cameraPos = event.getCamera().getPos();
        Box localBox = box.offset(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        Matrix4f matrix = event.getMatrices().peek().getPositionMatrix();

        updateCachedThemeColors();

        if (boxMode.is("Шейдер")) {
            overlayRenderer.writeMask(matrix, localBox);
        } else {
            // Обычный wireframe бокс
            renderWireframeBox(event.getMatrices(), localBox);
        }

        if (trajectory.isState()) {
            renderFlightTrajectory(matrix, cameraPos, box, target, event.getTickDelta());
        }
    }

    private void renderWireframeBox(MatrixStack matrices, Box box) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
        RenderSystem.lineWidth(1.5f);

        int color = ColorUtils.setAlphaColor(0xFFFFFFFF, 220);
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        int a = (color >> 24) & 0xFF;

        float x0 = (float) box.minX, y0 = (float) box.minY, z0 = (float) box.minZ;
        float x1 = (float) box.maxX, y1 = (float) box.maxY, z1 = (float) box.maxZ;

        Matrix4f m = matrices.peek().getPositionMatrix();
        BufferBuilder buf = Tessellator.getInstance().begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

        // 12 рёбер куба
        // Нижняя грань
        buf.vertex(m, x0, y0, z0).color(r, g, b, a);
        buf.vertex(m, x1, y0, z0).color(r, g, b, a);
        buf.vertex(m, x1, y0, z0).color(r, g, b, a);
        buf.vertex(m, x1, y0, z1).color(r, g, b, a);
        buf.vertex(m, x1, y0, z1).color(r, g, b, a);
        buf.vertex(m, x0, y0, z1).color(r, g, b, a);
        buf.vertex(m, x0, y0, z1).color(r, g, b, a);
        buf.vertex(m, x0, y0, z0).color(r, g, b, a);
        // Верхняя грань
        buf.vertex(m, x0, y1, z0).color(r, g, b, a);
        buf.vertex(m, x1, y1, z0).color(r, g, b, a);
        buf.vertex(m, x1, y1, z0).color(r, g, b, a);
        buf.vertex(m, x1, y1, z1).color(r, g, b, a);
        buf.vertex(m, x1, y1, z1).color(r, g, b, a);
        buf.vertex(m, x0, y1, z1).color(r, g, b, a);
        buf.vertex(m, x0, y1, z1).color(r, g, b, a);
        buf.vertex(m, x0, y1, z0).color(r, g, b, a);
        // Вертикальные рёбра
        buf.vertex(m, x0, y0, z0).color(r, g, b, a);
        buf.vertex(m, x0, y1, z0).color(r, g, b, a);
        buf.vertex(m, x1, y0, z0).color(r, g, b, a);
        buf.vertex(m, x1, y1, z0).color(r, g, b, a);
        buf.vertex(m, x1, y0, z1).color(r, g, b, a);
        buf.vertex(m, x1, y1, z1).color(r, g, b, a);
        buf.vertex(m, x0, y0, z1).color(r, g, b, a);
        buf.vertex(m, x0, y1, z1).color(r, g, b, a);

        BufferRenderer.drawWithGlobalProgram(buf.end());

        RenderSystem.lineWidth(1.0f);
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    @EventLink(priority = Priority.HIGHEST)
    public void onRender2D(EventRender.Default event) {
        if (!overlayRenderer.hasMask()) return;
        if (!boxMode.is("Шейдер")) return;

        int brightColor1 = ColorUtils.interpolateColor(cachedThemeColor1, 0xFFFFFFFF, 0.38f);
        int brightColor2 = ColorUtils.interpolateColor(cachedThemeColor2, 0xFFFFFFFF, 0.42f);
        overlayRenderer.renderOverlay(BlockOverlayShaderRenderer.Params.predict(brightColor1, brightColor2));
    }

    @Override
    public void onDisable() {
        resetPredictionState();
        overlayRenderer.destroy();
        super.onDisable();
    }

    private void resetPredictionState() {
        smoothedPredictionBox = null;
        smoothedTarget = null;
        overlayRenderer.clearMask();
    }

    private void renderFlightTrajectory(Matrix4f matrix, Vec3d cameraPos, Box box, LivingEntity target, float tickDelta) {
        Vec3d startWorld = mc.player.getLerpedPos(tickDelta).add(0.0, mc.player.getHeight() * 0.5, 0.0);
        Vec3d endWorld = box.getCenter();
        if (startWorld.squaredDistanceTo(endWorld) < 0.04) return;

        Vec3d[] points = buildFlightPath(startWorld, endWorld, target);
        if (points.length < 2) return;

        int startColor = ColorUtils.setAlphaColor(cachedThemeColor1, 200);
        int endColor = ColorUtils.setAlphaColor(ColorUtils.interpolateColor(cachedThemeColor2, 0xFFFFFFFF, 0.3f), 235);

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.setShaderTexture(0, GLOW_TEXTURE);
        RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);

        BufferBuilder quads = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        for (int i = 0; i < points.length - 1; i++) {
            float progress = i / (float) Math.max(1, points.length - 2);
            int segmentColor = ColorUtils.interpolateColor(startColor, endColor, progress);
            addGlowSegment(quads, matrix, cameraPos, points[i], points[i + 1], segmentColor, LINE_GLOW_THICKNESS);
        }
        BufferRenderer.drawWithGlobalProgram(quads.end());

        RenderSystem.setShaderTexture(0, 0);
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    private Vec3d[] buildFlightPath(Vec3d start, Vec3d end, LivingEntity target) {
        Vec3d diff = end.subtract(start);
        double distance = diff.length();
        if (distance < 0.05) {
            return new Vec3d[]{start, end};
        }

        Vec3d playerVel = mc.player.getVelocity();
        Vec3d targetVel = target.getVelocity();
        Vec3d control1 = start
                .add(playerVel.multiply(Math.min(distance * 0.42, 3.0)))
                .add(0.0, Math.min(distance * 0.14, 4.0), 0.0);
        Vec3d control2 = end.subtract(targetVel.multiply(Math.min(distance * 0.32, 2.5)));

        Vec3d[] points = new Vec3d[TRAJECTORY_SAMPLES];
        for (int i = 0; i < TRAJECTORY_SAMPLES; i++) {
            float t = i / (float) (TRAJECTORY_SAMPLES - 1);
            points[i] = cubicBezier(start, control1, control2, end, t);
        }
        return points;
    }

    private Vec3d cubicBezier(Vec3d p0, Vec3d p1, Vec3d p2, Vec3d p3, float t) {
        double it = 1.0 - t;
        double it2 = it * it;
        double t2 = t * t;
        return p0.multiply(it2 * it)
                .add(p1.multiply(3.0 * it2 * t))
                .add(p2.multiply(3.0 * it * t2))
                .add(p3.multiply(t2 * t));
    }

    private void addGlowSegment(BufferBuilder buffer, Matrix4f matrix, Vec3d camera, Vec3d startWorld, Vec3d endWorld, int color, float thickness) {
        addGlowSegment(buffer, matrix, startWorld.subtract(camera), endWorld.subtract(camera), color, thickness);
    }

    private void addGlowSegment(BufferBuilder buffer, Matrix4f matrix, Vec3d start, Vec3d end, int color, float thickness) {
        Vec3d edge = end.subtract(start);
        if (edge.lengthSquared() <= 1.0E-6) return;

        Vec3d direction = edge.normalize();
        double overlap = thickness * 0.22;
        start = start.subtract(direction.multiply(overlap));
        end = end.add(direction.multiply(overlap));
        edge = end.subtract(start);

        Vec3d center = start.add(end).multiply(0.5);
        Vec3d toCamera = center.negate();
        if (toCamera.lengthSquared() <= 1.0E-6) {
            toCamera = new Vec3d(0.0, 1.0, 0.0);
        }

        Vec3d side = edge.crossProduct(toCamera);
        if (side.lengthSquared() <= 1.0E-6) {
            side = edge.crossProduct(new Vec3d(0.0, 1.0, 0.0));
            if (side.lengthSquared() <= 1.0E-6) {
                side = edge.crossProduct(new Vec3d(1.0, 0.0, 0.0));
            }
        }

        side = side.normalize().multiply(thickness * 0.48);

        Vec3d p1 = start.add(side);
        Vec3d p2 = start.subtract(side);
        Vec3d p3 = end.subtract(side);
        Vec3d p4 = end.add(side);

        float[] rgba = ColorUtils.rgba(color);
        buffer.vertex(matrix, (float) p1.x, (float) p1.y, (float) p1.z).texture(LINE_GLOW_LINE_U, 0.0f).color(rgba[0], rgba[1], rgba[2], rgba[3]);
        buffer.vertex(matrix, (float) p2.x, (float) p2.y, (float) p2.z).texture(LINE_GLOW_LINE_U, 1.0f).color(rgba[0], rgba[1], rgba[2], rgba[3]);
        buffer.vertex(matrix, (float) p3.x, (float) p3.y, (float) p3.z).texture(LINE_GLOW_LINE_U, 1.0f).color(rgba[0], rgba[1], rgba[2], rgba[3]);
        buffer.vertex(matrix, (float) p4.x, (float) p4.y, (float) p4.z).texture(LINE_GLOW_LINE_U, 0.0f).color(rgba[0], rgba[1], rgba[2], rgba[3]);
    }

    private Box smoothPredictionBox(LivingEntity target, Box predictedBox) {
        if (smoothedPredictionBox == null || smoothedTarget != target || smoothedPredictionBox.getCenter().squaredDistanceTo(predictedBox.getCenter()) > 144.0D) {
            smoothedPredictionBox = predictedBox;
            smoothedTarget = target;
            return predictedBox;
        }

        double distance = Math.sqrt(smoothedPredictionBox.getCenter().squaredDistanceTo(predictedBox.getCenter()));
        double smoothFactor = MathHelper.clamp(0.08D + distance * 0.035D, 0.08D, 0.18D);
        smoothedPredictionBox = lerpBox(smoothedPredictionBox, predictedBox, smoothFactor);
        return smoothedPredictionBox;
    }

    private Box buildPredictedBox(LivingEntity target) {
        Box currentBox = target.getBoundingBox();
        if (!PredictUtils.shouldOvertake(target)) {
            return currentBox;
        }
        Vec3d predictedCenter = PredictUtils.predict(target, currentBox.getCenter(), Math.max(0, forward.getValue().intValue()));
        Vec3d offset = predictedCenter.subtract(currentBox.getCenter());
        return currentBox.offset(offset);
    }

    private Box lerpBox(Box from, Box to, double factor) {
        return new Box(
                MathHelper.lerp(factor, from.minX, to.minX),
                MathHelper.lerp(factor, from.minY, to.minY),
                MathHelper.lerp(factor, from.minZ, to.minZ),
                MathHelper.lerp(factor, from.maxX, to.maxX),
                MathHelper.lerp(factor, from.maxY, to.maxY),
                MathHelper.lerp(factor, from.maxZ, to.maxZ)
        );
    }

    private void updateCachedThemeColors() {
        if (Volon.INSTANCE == null || Volon.INSTANCE.themeStorage == null || Volon.INSTANCE.themeStorage.getThemes() == null) {
            cachedThemeColor1 = ColorUtils.getThemeColor(0);
            cachedThemeColor2 = ColorUtils.getThemeColor(180);
            return;
        }

        var theme = Volon.INSTANCE.themeStorage.getThemes().getTheme();
        if (theme == null) {
            cachedThemeColor1 = ColorUtils.getThemeColor(0);
            cachedThemeColor2 = ColorUtils.getThemeColor(180);
            return;
        }

        if (!"Rainbow".equals(theme.getName())) {
            int base = (theme.color != null && theme.color.length > 0) ? theme.color[0] : ColorUtils.getThemeColor(0);
            cachedThemeColor1 = base;
            cachedThemeColor2 = base;
        } else {
            cachedThemeColor1 = ColorUtils.getThemeColor();
            cachedThemeColor2 = ColorUtils.getThemeColor(180);
        }
    }
}

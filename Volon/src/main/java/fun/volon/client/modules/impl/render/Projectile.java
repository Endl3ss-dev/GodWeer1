package fun.volon.client.modules.impl.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import fun.volon.api.events.EventLink;
import fun.volon.api.events.implement.Event3DRender;
import fun.volon.api.events.implement.EventRender;
import fun.volon.api.utils.color.ColorUtils;
import fun.volon.api.utils.render.RenderUtils;
import fun.volon.api.utils.render.fonts.msdf.Font;
import fun.volon.api.utils.render.fonts.msdf.Fonts;
import fun.volon.client.modules.Module;
import fun.volon.client.modules.settings.implement.FloatSetting;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class Projectile extends Module {
    private final Font impactFont = Fonts.getFont("sf_regular", 14);
    private static final ItemStack PEARL_STACK = new ItemStack(Items.ENDER_PEARL);

    public static Projectile INSTANCE = new Projectile();

    private record ImpactPoint(Vec3d pos, float seconds, ItemStack stack) {
    }

    private final FloatSetting size = new FloatSetting("Размер", 1.2f, 0.6f, 2.4f, 0.1f);

    private final List<ImpactPoint> impactPoints = new ArrayList<>();
    private final Matrix4f lastProjectionMatrix = new Matrix4f();
    private final Quaternionf lastCameraRotation = new Quaternionf();
    private Vec3d lastCameraPos = Vec3d.ZERO;
    private boolean hasMatrices;

    public Projectile() {
        super("Projectile", "Траектория жемчуга эндера", ModuleCategory.RENDER);
        addSettings(size);
    }

    private List<Vec3d> simulate(EnderPearlEntity pearl, float tickDelta) {
        List<Vec3d> points = new ArrayList<>();

        if (mc.world == null || mc.player == null) return points;

        // Начальная позиция жемчуга
        Vec3d startPos = pearl.getPos();
        Vec3d velocity = pearl.getVelocity();

        points.add(startPos);

        // Параметры симуляции
        double gravity = 0.03;
        double airResistance = 0.99;
        int maxSteps = 400; // Максимум 20 секунд (20 тиков в секунду)

        Vec3d currentPos = startPos;
        Vec3d currentVel = velocity;

        for (int step = 0; step < maxSteps; step++) {
            // Обновляем позицию
            Vec3d newPos = currentPos.add(currentVel);

            // Проверка коллизии с блоками
            RaycastContext context = new RaycastContext(
                    currentPos, newPos,
                    RaycastContext.ShapeType.COLLIDER,
                    RaycastContext.FluidHandling.NONE,
                    mc.player
            );

            BlockHitResult hitResult = mc.world.raycast(context);

            if (hitResult.getType() != HitResult.Type.MISS) {
                // Попали в блок
                points.add(hitResult.getPos());
                break;
            }

            points.add(newPos);

            // Обновляем скорость (гравитация и сопротивление)
            currentVel = new Vec3d(
                    currentVel.x * airResistance,
                    currentVel.y * airResistance - gravity,
                    currentVel.z * airResistance
            );

            currentPos = newPos;

            // Останавливаем симуляцию, если скорость слишком мала
            if (currentVel.length() < 0.01 && step > 10) {
                break;
            }
        }

        return points;
    }

    @EventLink
    public void onRender3D(Event3DRender event) {
        if (mc.world == null || mc.player == null) return;

        impactPoints.clear();
        hasMatrices = true;
        lastProjectionMatrix.set(event.getProjectionMatrix());
        lastCameraPos = event.getCamera().getPos();
        lastCameraRotation.set(event.getCamera().getRotation());

        MatrixStack matrices = event.getMatrices();
        Camera camera = event.getCamera();
        Vec3d cameraPos = camera.getPos();
        float tickDelta = event.getTickDelta();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(ShaderProgramKeys.RENDERTYPE_LINES);
        RenderSystem.lineWidth(Math.max(1.0f, size.get() * 2.0f));
        GL11.glEnable(GL11.GL_LINE_SMOOTH);

        Box searchBox = mc.player.getBoundingBox().expand(128.0D);
        for (EnderPearlEntity pearl : mc.world.getEntitiesByClass(EnderPearlEntity.class, searchBox, Entity::isAlive)) {

            List<Vec3d> points = simulate(pearl, tickDelta);
            if (points.size() < 2) continue;

            float seconds = (points.size() - 1) / 20.0f;
            Vec3d impactPos = points.get(points.size() - 1);
            impactPoints.add(new ImpactPoint(impactPos, seconds, PEARL_STACK));

            matrices.push();
            matrices.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

            BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);
            int themeColor = ColorUtils.getThemeColor();
            for (int i = 1; i < points.size(); i++) {
                int alpha = MathHelper.clamp((int) (MathHelper.clamp(i / 25.0f, 0.0f, 1.0f) * 255.0f), 0, 255);
                int color = ColorUtils.setAlphaColor(themeColor, alpha);
                vertexLine(matrices, buffer, points.get(i - 1), points.get(i), color);
            }
            BufferRenderer.drawWithGlobalProgram(buffer.end());

            matrices.pop();
        }

        // Восстановление состояний
        RenderSystem.lineWidth(1.0f);
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
    }

    @EventLink
    public void onRender2D(EventRender.Default event) {
        if (!hasMatrices || impactPoints.isEmpty() || mc.player == null) return;

        DrawContext context = event.getContext();
        MatrixStack matrices = context.getMatrices();
        Font font = impactFont;
        if (font == null) return;

        for (ImpactPoint point : impactPoints) {
            Vec3d screenPos = projectToScreen(point.pos);
            if (screenPos != null && screenPos.z < 1.0f) {
                String text = String.format("%.1f", point.seconds).replace(',', '.') + " сек";
                float textWidth = font.getWidth(text);
                float width = textWidth + 17.0f;
                float height = 10.75f;
                float x = (float) screenPos.x - width / 2.0f;
                float y = (float) screenPos.y - height - 4.0f;

                RenderUtils.drawRoundedRect(matrices, x, y, width, height, 1.5f, ColorUtils.rgba(0, 0, 0, 120));
                RenderUtils.drawHudItem(context, point.stack(), x + 2.0f, y + 1.4f, 0.5f, 100.0f);
                font.drawString(matrices, text, x + 13.0f, y + 2.0f, ColorUtils.rgba(255, 255, 255, 255));
            }
        }
    }

    private void vertexLine(MatrixStack matrices, VertexConsumer buffer, Vec3d start, Vec3d end, int color) {
        MatrixStack.Entry entry = matrices.peek();
        Vector3f normal = getNormal(start, end);
        buffer.vertex(entry, start.toVector3f()).color(color).normal(entry, normal.x(), normal.y(), normal.z());
        buffer.vertex(entry, end.toVector3f()).color(color).normal(entry, normal.x(), normal.y(), normal.z());
    }

    private Vector3f getNormal(Vec3d start, Vec3d end) {
        float x = (float) (end.x - start.x);
        float y = (float) (end.y - start.y);
        float z = (float) (end.z - start.z);
        float length = MathHelper.sqrt(x * x + y * y + z * z);
        return length <= 0.0f ? new Vector3f(0.0f, 1.0f, 0.0f) : new Vector3f(x / length, y / length, z / length);
    }

    private Vec3d projectToScreen(Vec3d worldPos) {
        if (mc.player == null) return null;

        Vector3f relative = new Vector3f(
                (float) (worldPos.x - lastCameraPos.x),
                (float) (worldPos.y - lastCameraPos.y),
                (float) (worldPos.z - lastCameraPos.z)
        );

        relative.rotate(new Quaternionf(lastCameraRotation).conjugate());

        Vector4f vec = new Vector4f(relative.x, relative.y, relative.z, 1.0f);
        lastProjectionMatrix.transform(vec);

        if (vec.w() <= 0.00001f) return null;

        float invW = 1.0f / vec.w();
        float x = (vec.x() * invW + 1.0f) * mc.getWindow().getScaledWidth() / 2.0f;
        float y = (1.0f - vec.y() * invW) * mc.getWindow().getScaledHeight() / 2.0f;
        float z = vec.z() * invW;

        if (Float.isNaN(x) || Float.isNaN(y) || Float.isInfinite(x) || Float.isInfinite(y)) {
            return null;
        }

        return new Vec3d(x, y, z);
    }
}

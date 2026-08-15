package antileak.base.client.modules.impl.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import antileak.base.api.events.EventLink;
import antileak.base.api.events.implement.Event3DRender;
import antileak.base.api.utils.color.ColorUtils;
import antileak.base.client.modules.Module;

import java.util.ArrayList;
import java.util.List;

public class Ghost extends Module {

    public static Ghost INSTANCE = new Ghost();

    private static final Identifier GLOW = Identifier.of("punch", "textures/particle/bloom.png");

    private final Vec3d[] ghostPos = new Vec3d[3];
    private final Vec3d[] ghostVel = new Vec3d[3];
    private final List<Vec3d>[] trails = new ArrayList[3];
    private long lastUpdate = 0;

    public Ghost() {
        super("Ghost", "Призрачный эффект вокруг игрока", ModuleCategory.RENDER);
        for (int i = 0; i < 3; i++) {
            trails[i] = new ArrayList<>();
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        lastUpdate = System.currentTimeMillis();
        for (int i = 0; i < 3; i++) {
            ghostPos[i] = null;
            ghostVel[i] = Vec3d.ZERO;
            trails[i].clear();
        }
    }

    @Override
    public void onDisable() {
        lastUpdate = 0;
        for (int i = 0; i < 3; i++) {
            ghostPos[i] = null;
            ghostVel[i] = Vec3d.ZERO;
            trails[i].clear();
        }
        super.onDisable();
    }

    @EventLink
    public void onRender3D(Event3DRender e) {
        if (mc.player == null || mc.world == null) return;

        PlayerEntity player = mc.player;
        MatrixStack matrices = e.getMatrices();
        Camera camera = e.getCamera();
        float delta = e.getTickDelta();

        long now = System.currentTimeMillis();
        if (lastUpdate == 0) lastUpdate = now;
        float dt = (now - lastUpdate) / 1000f;
        lastUpdate = now;

        if (dt > 0.1f) dt = 0.1f;
        if (dt <= 0) dt = 0.001f;

        Vec3d playerPos = new Vec3d(
                MathHelper.lerp(delta, player.lastRenderX, player.getX()),
                MathHelper.lerp(delta, player.lastRenderY, player.getY()),
                MathHelper.lerp(delta, player.lastRenderZ, player.getZ())
        );

        float age = player.age + delta;
        float time = (now % 100000L) / 1000f;

        int primary = ColorUtils.getThemeColor();
        int secondary = ColorUtils.getThemeColor(90);

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        RenderSystem.setShaderTexture(0, GLOW);
        RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();

        BufferBuilder buffer = Tessellator.getInstance().begin(
                VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR
        );

        for (int j = 0; j < 3; j++) {
            double radians = Math.toRadians(((age) * 4.2 + j * 120) % 360);
            float baseR = player.getWidth() * 1.2f;
            float dynR = baseR * (0.95f + 0.1f * (float) Math.sin(time * 0.9 + j));

            double tx = Math.cos(radians) * dynR;
            double tz = Math.sin(radians) * dynR;
            double ty = 0.2 + 0.13 * Math.sin(time * 2.2 + j);

            Vec3d target = playerPos.add(tx, ty, tz);

            if (ghostPos[j] == null || ghostPos[j].distanceTo(target) > 12) {
                ghostPos[j] = target;
                ghostVel[j] = Vec3d.ZERO;
            }

            Vec3d diff = target.subtract(ghostPos[j]);
            double spring = 18.0;
            double damping = 0.82;

            ghostVel[j] = ghostVel[j]
                    .add(diff.multiply(spring * dt))
                    .multiply(Math.pow(damping, dt * 60));

            ghostPos[j] = ghostPos[j].add(ghostVel[j].multiply(dt * 60));

            if (trails[j].isEmpty() || trails[j].get(0).distanceTo(ghostPos[j]) > 0.012) {
                trails[j].add(0, ghostPos[j]);
                while (trails[j].size() > 22) {
                    trails[j].remove(trails[j].size() - 1);
                }
            }

            float colorProgress = j / 3f;
            int baseColor = interpolateColor(primary, secondary, colorProgress);
            int brightColor = interpolateColor(baseColor, 0xFFFFFFFF, 0.32f);
            int colorWithAlpha = ColorUtils.setAlphaColor(baseColor, 255);

            for (int i = 0; i < trails[j].size(); i++) {
                Vec3d p = trails[j].get(i);
                float offset = 1.0f - (float) i / trails[j].size();
                float pulse = 1.0f + 0.06f * MathHelper.sin((age * 0.7f) + i * 0.4f + j);
                float size = (0.09f + offset * 0.11f) * pulse;

                drawGlow(buffer, matrices, camera, p, size * 2.5f, withAlpha(colorWithAlpha, (int) (offset * 38)));
                drawGlow(buffer, matrices, camera, p, size * 1.4f, withAlpha(colorWithAlpha, (int) (offset * 85)));
                drawGlow(buffer, matrices, camera, p, size * 0.6f, withAlpha(brightColor, (int) (offset * 165)));
            }
        }

        try {
            BufferRenderer.drawWithGlobalProgram(buffer.end());
        } catch (Exception ignored) {
        }

        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    }

    private void drawGlow(BufferBuilder buffer, MatrixStack matrices, Camera camera,
                          Vec3d pos, float scale, int color) {
        Vec3d cam = camera.getPos();

        matrices.push();
        matrices.translate(pos.x - cam.x, pos.y - cam.y, pos.z - cam.z);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-camera.getYaw()));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));

        Matrix4f mat = matrices.peek().getPositionMatrix();

        buffer.vertex(mat, -scale, scale, 0).texture(0, 1).color(color);
        buffer.vertex(mat, scale, scale, 0).texture(1, 1).color(color);
        buffer.vertex(mat, scale, -scale, 0).texture(1, 0).color(color);
        buffer.vertex(mat, -scale, -scale, 0).texture(0, 0).color(color);

        matrices.pop();
    }

    private int withAlpha(int color, int alpha) {
        return (MathHelper.clamp(alpha, 0, 255) << 24) | (color & 0x00FFFFFF);
    }

    private int interpolateColor(int c1, int c2, float t) {
        t = MathHelper.clamp(t, 0f, 1f);
        int r1 = (c1 >> 16) & 255, g1 = (c1 >> 8) & 255, b1 = c1 & 255;
        int r2 = (c2 >> 16) & 255, g2 = (c2 >> 8) & 255, b2 = c2 & 255;

        int r = (int) (r1 + (r2 - r1) * t);
        int g = (int) (g1 + (g2 - g1) * t);
        int b = (int) (b1 + (b2 - b1) * t);

        return (255 << 24) | (r << 16) | (g << 8) | b;
    }
}

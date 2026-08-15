package antileak.base.client.modules.impl.render;

import antileak.base.api.utils.color.ColorUtils;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;

/**
 * KatanaRenderer - рендерит 3D катану на спине игрока
 */
public class KatanaRenderer extends FeatureRenderer<PlayerEntityRenderState, PlayerEntityModel> {

    public KatanaRenderer(FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack ms, VertexConsumerProvider vertexConsumers, int light,
                       PlayerEntityRenderState state, float limbAngle, float limbDistance) {
        Katana module = Katana.INSTANCE;
        MinecraftClient mc = MinecraftClient.getInstance();

        if (module == null || !module.isEnable() || mc.player == null || mc.world == null) return;

        boolean isSelf = (state.id == mc.player.getId());

        if (module.target.is("Себя") && !isSelf) return;
        if (module.target.is("Всех игроков") && isSelf) return;
        if (isSelf && mc.options.getPerspective().isFirstPerson()) return;

        // Получаем статичный цвет темы (не анимированный)
        int themeColor;
        var theme = antileak.base.Punch.INSTANCE.themeStorage.getThemes().getTheme();
        if (theme.getName().equals("Rainbow")) {
            // Для Rainbow берём первый цвет массива (фиксированный)
            themeColor = theme.color[0];
        } else {
            // Для остальных тем берём обычный цвет
            themeColor = theme.color[0];
        }
        
        int tr = (themeColor >> 16) & 0xFF;
        int tg = (themeColor >> 8) & 0xFF;
        int tb = themeColor & 0xFF;

        ms.push();

        // Трансформация тела
        ModelPart body = this.getContextModel().body;
        float savedPitch = body.pitch;
        float savedRoll = body.roll;
        body.pitch = 0f;
        body.roll = 0f;
        body.rotate(ms);
        body.pitch = savedPitch;
        body.roll = savedRoll;

        // Позиция катаны на спине
        ms.translate(-0.15f, 0.1f, 0.28f);
        ms.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-45f));
        ms.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-3f));
        ms.scale(1.15f, 1.15f, 1.15f);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);

        Tessellator tess = Tessellator.getInstance();
        Matrix4f mat = ms.peek().getPositionMatrix();

        // ── Двухпроходный рендер ─────────────────────────────────────────────
        // Проход 1: записываем в depth buffer непрозрачную форму
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.colorMask(false, false, false, false);

        box(tess, mat, -0.042f, -0.48f, -0.024f, 0.042f, -0.42f, 0.024f, 0, 0, 0, 255);
        box(tess, mat, -0.032f, -0.42f, -0.018f, 0.032f, 0.0f, 0.018f, 0, 0, 0, 255);
        box(tess, mat, -0.085f, 0.0f, -0.034f, 0.085f, 0.05f, 0.034f, 0, 0, 0, 255);
        box(tess, mat, -0.022f, 0.05f, -0.010f, 0.022f, 0.96f, 0.010f, 0, 0, 0, 255);
        box(tess, mat, -0.012f, 0.96f, -0.007f, 0.012f, 1.07f, 0.007f, 0, 0, 0, 255);

        RenderSystem.colorMask(true, true, true, true);

        // Проход 2: рисуем цветную прозрачную заливку
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        int bgA = (int) (module.fillAlpha.get() / 100f * 255f);
        int outA = (int) (module.outlineAlpha.get() / 100f * 255f);

        // Навершие рукояти
        box(tess, mat, -0.042f, -0.48f, -0.024f, 0.042f, -0.42f, 0.024f, tr, tg, tb, bgA);
        // Рукоять
        box(tess, mat, -0.032f, -0.42f, -0.018f, 0.032f, 0.0f, 0.018f, tr, tg, tb, bgA);
        // Гарда
        box(tess, mat, -0.085f, 0.0f, -0.034f, 0.085f, 0.05f, 0.034f, tr, tg, tb, bgA);
        // Клинок
        box(tess, mat, -0.022f, 0.05f, -0.010f, 0.022f, 0.96f, 0.010f, tr, tg, tb, bgA);
        // Острие
        box(tess, mat, -0.012f, 0.96f, -0.007f, 0.012f, 1.07f, 0.007f, tr, tg, tb, bgA);

        // ── Яркие грани (outline через тонкие боксы) ─────────────────────────
        boxEdge(tess, mat, -0.042f, -0.48f, -0.024f, 0.042f, -0.42f, 0.024f, tr, tg, tb, outA);
        boxEdge(tess, mat, -0.032f, -0.42f, -0.018f, 0.032f, 0.0f, 0.018f, tr, tg, tb, outA);
        boxEdge(tess, mat, -0.085f, 0.0f, -0.034f, 0.085f, 0.05f, 0.034f, tr, tg, tb, outA);
        boxEdge(tess, mat, -0.022f, 0.05f, -0.010f, 0.022f, 0.96f, 0.010f, tr, tg, tb, outA);
        boxEdge(tess, mat, -0.012f, 0.96f, -0.007f, 0.012f, 1.07f, 0.007f, tr, tg, tb, outA);

        // ── Glow ─────────────────────────────────────────────────────────────
        if (module.glow.isState()) {
            RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE);
            RenderSystem.depthMask(false);
            RenderSystem.disableDepthTest();

            float glowBase = module.glowLevel.get() / 100f;

            int layers = 12;
            float maxEx = 0.045f;

            for (int i = 0; i < layers; i++) {
                float t = (float) i / (layers - 1);
                float falloff = (1f - t) * (1f - t);
                int ga = (int) (glowBase * falloff * 180f);
                if (ga <= 1) continue;

                float ex = t * maxEx;
                box(tess, mat, -0.042f - ex, -0.48f - ex, -0.024f - ex, 0.042f + ex, -0.42f + ex, 0.024f + ex, tr, tg, tb, ga);
                box(tess, mat, -0.032f - ex, -0.42f - ex, -0.018f - ex, 0.032f + ex, 0.0f + ex, 0.018f + ex, tr, tg, tb, ga);
                box(tess, mat, -0.085f - ex, 0.0f - ex, -0.034f - ex, 0.085f + ex, 0.05f + ex, 0.034f + ex, tr, tg, tb, ga);
                box(tess, mat, -0.022f - ex, 0.05f - ex, -0.010f - ex, 0.022f + ex, 0.96f + ex, 0.010f + ex, tr, tg, tb, ga);
                box(tess, mat, -0.012f - ex, 0.96f - ex, -0.007f - ex, 0.012f + ex, 1.07f + ex, 0.007f + ex, tr, tg, tb, ga);
            }

            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(true);
            RenderSystem.defaultBlendFunc();
        }

        RenderSystem.disableBlend();
        RenderSystem.enableCull();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        ms.pop();
    }

    /**
     * Заполненный бокс (6 граней по 2 треугольника = QUADS)
     */
    private void box(Tessellator tess, Matrix4f mat,
                     float x0, float y0, float z0,
                     float x1, float y1, float z1,
                     int r, int g, int b, int a) {
        if (a <= 0) return;

        RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
        BufferBuilder buf = tess.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        // -Y
        buf.vertex(mat, x0, y0, z0).color(r, g, b, a);
        buf.vertex(mat, x1, y0, z0).color(r, g, b, a);
        buf.vertex(mat, x1, y0, z1).color(r, g, b, a);
        buf.vertex(mat, x0, y0, z1).color(r, g, b, a);

        // +Y
        buf.vertex(mat, x0, y1, z0).color(r, g, b, a);
        buf.vertex(mat, x0, y1, z1).color(r, g, b, a);
        buf.vertex(mat, x1, y1, z1).color(r, g, b, a);
        buf.vertex(mat, x1, y1, z0).color(r, g, b, a);

        // -Z
        buf.vertex(mat, x0, y0, z0).color(r, g, b, a);
        buf.vertex(mat, x0, y1, z0).color(r, g, b, a);
        buf.vertex(mat, x1, y1, z0).color(r, g, b, a);
        buf.vertex(mat, x1, y0, z0).color(r, g, b, a);

        // +Z
        buf.vertex(mat, x0, y0, z1).color(r, g, b, a);
        buf.vertex(mat, x1, y0, z1).color(r, g, b, a);
        buf.vertex(mat, x1, y1, z1).color(r, g, b, a);
        buf.vertex(mat, x0, y1, z1).color(r, g, b, a);

        // -X
        buf.vertex(mat, x0, y0, z0).color(r, g, b, a);
        buf.vertex(mat, x0, y0, z1).color(r, g, b, a);
        buf.vertex(mat, x0, y1, z1).color(r, g, b, a);
        buf.vertex(mat, x0, y1, z0).color(r, g, b, a);

        // +X
        buf.vertex(mat, x1, y0, z0).color(r, g, b, a);
        buf.vertex(mat, x1, y1, z0).color(r, g, b, a);
        buf.vertex(mat, x1, y1, z1).color(r, g, b, a);
        buf.vertex(mat, x1, y0, z1).color(r, g, b, a);

        BufferRenderer.drawWithGlobalProgram(buf.end());
    }

    /**
     * Обводка — 12 тонких боксов вдоль рёбер куба
     */
    private void boxEdge(Tessellator tess, Matrix4f mat,
                         float x0, float y0, float z0,
                         float x1, float y1, float z1,
                         int r, int g, int b, int a) {
        if (a <= 0) return;

        float e = 0.004f; // толщина ребра
        int br = Math.min(r + 40, 255);
        int bg = Math.min(g + 40, 255);
        int bb = Math.min(b + 40, 255);

        // 4 горизонтальных ребра снизу (Y=y0)
        box(tess, mat, x0 - e, y0 - e, z0 - e, x1 + e, y0 + e, z0 + e, br, bg, bb, a);
        box(tess, mat, x0 - e, y0 - e, z1 - e, x1 + e, y0 + e, z1 + e, br, bg, bb, a);
        box(tess, mat, x0 - e, y0 - e, z0 - e, x0 + e, y0 + e, z1 + e, br, bg, bb, a);
        box(tess, mat, x1 - e, y0 - e, z0 - e, x1 + e, y0 + e, z1 + e, br, bg, bb, a);

        // 4 горизонтальных ребра сверху (Y=y1)
        box(tess, mat, x0 - e, y1 - e, z0 - e, x1 + e, y1 + e, z0 + e, br, bg, bb, a);
        box(tess, mat, x0 - e, y1 - e, z1 - e, x1 + e, y1 + e, z1 + e, br, bg, bb, a);
        box(tess, mat, x0 - e, y1 - e, z0 - e, x0 + e, y1 + e, z1 + e, br, bg, bb, a);
        box(tess, mat, x1 - e, y1 - e, z0 - e, x1 + e, y1 + e, z1 + e, br, bg, bb, a);

        // 4 вертикальных ребра
        box(tess, mat, x0 - e, y0 - e, z0 - e, x0 + e, y1 + e, z0 + e, br, bg, bb, a);
        box(tess, mat, x1 - e, y0 - e, z0 - e, x1 + e, y1 + e, z0 + e, br, bg, bb, a);
        box(tess, mat, x0 - e, y0 - e, z1 - e, x0 + e, y1 + e, z1 + e, br, bg, bb, a);
        box(tess, mat, x1 - e, y0 - e, z1 - e, x1 + e, y1 + e, z1 + e, br, bg, bb, a);
    }
}

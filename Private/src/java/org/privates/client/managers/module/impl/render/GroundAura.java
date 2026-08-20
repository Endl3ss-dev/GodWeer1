package org.privates.client.managers.module.impl.render;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.platform.GlStateManager;
import net.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL11;
import org.privates.client.api.annotations.Category3;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.render.Render3DPosedEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.math.Interpolator;
import org.privates.client.utils.render.color.ColorUtil;

@Category3
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "GroundAura", category = Category.RENDER, description = "Пульсирующая аура на земле под игроком")
public class GroundAura extends Module {

    final SliderSetting radius = new SliderSetting(this, "Радиус", 0.9f, 0.35f, 2.4f, 0.05f);
    final SliderSetting thickness = new SliderSetting(this, "Толщина", 0.08f, 0.02f, 0.3f, 0.01f);
    final SliderSetting speed = new SliderSetting(this, "Скорость", 1.4f, 0.2f, 4.0f, 0.1f);
    final SliderSetting alpha = new SliderSetting(this, "Прозрачность", 0.85f, 0.1f, 1.0f, 0.05f);
    final SliderSetting detail = new SliderSetting(this, "Детализация", 72f, 24f, 160f, 1f);

    @EventHandler
    public void onRender3D(Render3DPosedEvent event) {
        if (mc.player == null || mc.world == null) return;

        float partial = event.getPartialTicks();
        double px = Interpolator.lerp(mc.player.lastTickPosX, mc.player.getPosX(), partial);
        double py = Interpolator.lerp(mc.player.lastTickPosY, mc.player.getPosY(), partial) + 0.02;
        double pz = Interpolator.lerp(mc.player.lastTickPosZ, mc.player.getPosZ(), partial);

        Vector3d cam = mc.getRenderManager().info.getProjectedView();
        Vector3d center = new Vector3d(px - cam.x, py - cam.y, pz - cam.z);

        float t = (System.currentTimeMillis() % 1000000L) / 1000.0f * speed.getValue();
        float pulse = 0.88f + 0.12f * (float) Math.sin(t * 2.6f);
        float r1 = radius.getValue() * pulse;
        float r2 = r1 + thickness.getValue();
        float r3 = r1 * (0.68f + 0.04f * (float) Math.cos(t * 3.1f));
        float r4 = r3 + thickness.getValue() * 0.78f;

        int base = Theme.getInstance().clientColor();
        int cOuter = ColorUtil.replAlpha(base, (int) (255 * alpha.getValue() * 0.12f));
        int cInner = ColorUtil.replAlpha(base, (int) (255 * alpha.getValue() * 0.78f));
        int cCore = ColorUtil.replAlpha(base, (int) (255 * alpha.getValue() * 0.55f));

        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.disableAlphaTest();
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);
        RenderSystem.shadeModel(GL11.GL_SMOOTH);
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

        drawRing(event.getMatrix(), center, r1, r2, cOuter, cInner, detail.getValue().intValue(), t);
        drawRing(event.getMatrix(), center, r3, r4, cOuter, cCore, detail.getValue().intValue(), -t * 1.25f);

        RenderSystem.defaultBlendFunc();
        RenderSystem.shadeModel(GL11.GL_FLAT);
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableTexture();
    }

    private void drawRing(MatrixStack ms, Vector3d c, float inner, float outer, int colOuter, int colInner, int steps, float phase) {
        steps = Math.max(16, steps);
        BufferBuilder b = Tessellator.getInstance().getBuffer();
        Matrix4f mat = ms.getLast().getMatrix();
        b.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR);

        for (int i = 0; i <= steps; i++) {
            float pct = i / (float) steps;
            float a = (float) (pct * Math.PI * 2.0);
            float warp = 1.0f + 0.03f * (float) Math.sin(a * 6.0f + phase * 2.0f);
            float cs = MathHelper.cos(a);
            float sn = MathHelper.sin(a);

            float ox = cs * outer * warp;
            float oz = sn * outer * warp;
            float ix = cs * inner * warp;
            float iz = sn * inner * warp;

            b.pos(mat, (float) (c.x + ox), (float) c.y, (float) (c.z + oz)).color(colOuter).endVertex();
            b.pos(mat, (float) (c.x + ix), (float) c.y, (float) (c.z + iz)).color(colInner).endVertex();
        }
        Tessellator.getInstance().draw();
    }
}

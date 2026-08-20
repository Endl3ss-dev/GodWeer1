package org.privates.client.managers.module.impl.render;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.vector.Vector3d;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.platform.GlStateManager;
import net.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.privates.client.api.annotations.Category2;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.api.interfaces.IRender;
import org.privates.client.managers.events.render.Render3DPosedEvent;
import org.privates.client.managers.events.world.WorldChangeEvent;
import org.privates.client.managers.events.world.WorldLoadEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil3D;
import org.privates.client.utils.render.shader.ShaderManager;
import org.privates.lib.util.time.StopWatch;

import java.util.ArrayList;
import java.util.List;

@Category2
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "Trails", category = Category.RENDER, description = "Красивый след после ходьбы игрока")
public class Trails extends Module implements IRender {

    private final SliderSetting length = new SliderSetting(this, "Длинна", 250F, 150F, 350F, 5F);
    private final BooleanSetting shaderMode = new BooleanSetting(this, "Shader Mode", false);
    private final SliderSetting waveSpeed = new SliderSetting(this, "Wave Speed", 1.0F, 0.1F, 3.0F, 0.1F).setVisible(shaderMode::getValue);
    private final SliderSetting waveIntensity = new SliderSetting(this, "Wave Intensity", 1.5F, 0.5F, 3.0F, 0.1F).setVisible(shaderMode::getValue);


    private final List<Point> points = new ArrayList<>();

    private void clear() {
        points.clear();
    }

    @Override
    public void toggle() {
        super.toggle();
        clear();
    }

    @EventHandler
    public void onEvent(WorldChangeEvent event) {
        clear();
    }

    @EventHandler
    public void onEvent(WorldLoadEvent event) {
        clear();
    }

    @EventHandler
    public void onEvent(Render3DPosedEvent event) {
        final ClientPlayerEntity player = mc.player;

        final MatrixStack matrix = event.getMatrix();
        final boolean light = GL11.glIsEnabled(GL11.GL_LIGHTING);
        RenderSystem.pushMatrix();
        matrix.push();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
        if (light) RenderSystem.disableLighting();
        GL11.glShadeModel(GL11.GL_SMOOTH);
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

        matrix.push();

        if (!mc.gameSettings.getPointOfView().firstPerson()) {
            points.removeIf(point -> point.time.finished(length.getValue()));
            Vector3d playerPos = RenderUtil3D.interpolate(player, event.getPartialTicks());
            points.add(new Point(playerPos));
            drawTrail(matrix);
        }

        matrix.pop();

        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.clearCurrentColor();
        GL11.glShadeModel(GL11.GL_FLAT);
        if (light) RenderSystem.enableLighting();
        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
        RenderSystem.enableAlphaTest();
        matrix.pop();
        RenderSystem.popMatrix();
    }

    private void drawTrail(MatrixStack matrix) {
        if (shaderMode.getValue()) {
            drawTrailWithShader(matrix);
        } else {
            drawTrailNormal(matrix);
        }
    }

    private void drawTrailNormal(MatrixStack matrix) {
        startRendering();
        BUFFER.begin(GL11.GL_QUAD_STRIP, DefaultVertexFormats.POSITION_COLOR);
        final Vector3d projectedView = mc.getRenderManager().info.getProjectedView();
        int index = 0;
        for (Point point : points) {
            final float alpha = Math.min((float) index / (float) points.size(), 1F);
            final int color = Theme.getInstance().clientColor();
            final int finalColor = ColorUtil.replAlpha(color, alpha / 2F);
            final Vector3d pos = point.pos.subtract(projectedView);
            BUFFER.pos(matrix.getLast().getMatrix(), (float) pos.x, (float) (pos.y + mc.player.getHeight()), (float) pos.z).color(finalColor).endVertex();
            BUFFER.pos(matrix.getLast().getMatrix(), (float) pos.x, (float) pos.y + 0.0005F, (float) pos.z).color(finalColor).endVertex();
            index++;
        }
        TESSELLATOR.draw();
        RenderSystem.lineWidth(2);
        drawLine(matrix, points, true);
        drawLine(matrix, points, false);
        stopRendering();
    }

    private void drawTrailWithShader(MatrixStack matrix) {
        mc.getFramebuffer().bindFramebufferTexture();
        
        ShaderManager.trailWaves.load();
        ShaderManager.trailWaves.setUniformf("u_texel_size",
                1.0F / mc.getFramebuffer().framebufferWidth,
                1.0F / mc.getFramebuffer().framebufferHeight);
        ShaderManager.trailWaves.setUniformf("u_time", (System.currentTimeMillis() % 1000000) / 1000.0F);
        ShaderManager.trailWaves.setUniformf("u_wave_speed", waveSpeed.getValue());
        ShaderManager.trailWaves.setUniformf("u_wave_intensity", waveIntensity.getValue());
        
        int color = Theme.getInstance().clientColor();
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;
        ShaderManager.trailWaves.setUniformf("u_color", r, g, b, 1.0f);
        
        ShaderManager.trailWaves.setUniformi("u_diffuse_sampler", 0);
        ShaderManager.trailWaves.setUniformi("u_other_sampler", 20);
        
        GL13.glActiveTexture(GL13.GL_TEXTURE20);
        mc.getFramebuffer().bindFramebufferTexture();
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        mc.getFramebuffer().bindFramebufferTexture();
        
        startRendering();
        
        final Vector3d projectedView = mc.getRenderManager().info.getProjectedView();
        final float width = 0.4f;
        final float time = (System.currentTimeMillis() % 1000000) / 1000.0F;
        
        BUFFER.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        
        for (int i = 0; i < points.size() - 1; i++) {
            Point point = points.get(i);
            Point nextPoint = points.get(i + 1);
            
            final float progress = (float) i / (float) points.size();
            final float nextProgress = (float) (i + 1) / (float) points.size();
            
            final Vector3d pos = point.pos.subtract(projectedView);
            final Vector3d nextPos = nextPoint.pos.subtract(projectedView);
            
            Vector3d direction = nextPos.subtract(pos).normalize();
            Vector3d perpendicular = new Vector3d(-direction.z, 0, direction.x).normalize();
            
            float wavePhase = progress - time * waveSpeed.getValue() * 0.3f;
            float nextWavePhase = nextProgress - time * waveSpeed.getValue() * 0.3f;
            
            float waveAmp = (float) Math.exp(-Math.pow(wavePhase * 4.0, 2.0)) * waveIntensity.getValue();
            float nextWaveAmp = (float) Math.exp(-Math.pow(nextWavePhase * 4.0, 2.0)) * waveIntensity.getValue();
            
            float waveWidth = width * (1.0f + waveAmp * 0.5f);
            float nextWaveWidth = width * (1.0f + nextWaveAmp * 0.5f);
            
            float waveHeight = waveAmp * 0.3f;
            float nextWaveHeight = nextWaveAmp * 0.3f;
            
            Vector3d p1 = pos.add(perpendicular.scale(waveWidth));
            Vector3d p2 = pos.subtract(perpendicular.scale(waveWidth));
            Vector3d p3 = nextPos.subtract(perpendicular.scale(nextWaveWidth));
            Vector3d p4 = nextPos.add(perpendicular.scale(nextWaveWidth));
            
            float y1 = (float) (pos.y + mc.player.getHeight() + waveHeight);
            float y2 = (float) (pos.y + waveHeight);
            float nextY1 = (float) (nextPos.y + mc.player.getHeight() + nextWaveHeight);
            float nextY2 = (float) (nextPos.y + nextWaveHeight);
            
            BUFFER.pos(matrix.getLast().getMatrix(), (float) p1.x, y1, (float) p1.z).tex(progress, 1).endVertex();
            BUFFER.pos(matrix.getLast().getMatrix(), (float) p2.x, y1, (float) p2.z).tex(progress, 1).endVertex();
            BUFFER.pos(matrix.getLast().getMatrix(), (float) p3.x, nextY1, (float) p3.z).tex(nextProgress, 1).endVertex();
            BUFFER.pos(matrix.getLast().getMatrix(), (float) p4.x, nextY1, (float) p4.z).tex(nextProgress, 1).endVertex();
            
            BUFFER.pos(matrix.getLast().getMatrix(), (float) p1.x, y2, (float) p1.z).tex(progress, 0).endVertex();
            BUFFER.pos(matrix.getLast().getMatrix(), (float) p2.x, y2, (float) p2.z).tex(progress, 0).endVertex();
            BUFFER.pos(matrix.getLast().getMatrix(), (float) p3.x, nextY2, (float) p3.z).tex(nextProgress, 0).endVertex();
            BUFFER.pos(matrix.getLast().getMatrix(), (float) p4.x, nextY2, (float) p4.z).tex(nextProgress, 0).endVertex();
            
            BUFFER.pos(matrix.getLast().getMatrix(), (float) p1.x, y2, (float) p1.z).tex(progress, 0).endVertex();
            BUFFER.pos(matrix.getLast().getMatrix(), (float) p1.x, y1, (float) p1.z).tex(progress, 1).endVertex();
            BUFFER.pos(matrix.getLast().getMatrix(), (float) p4.x, nextY1, (float) p4.z).tex(nextProgress, 1).endVertex();
            BUFFER.pos(matrix.getLast().getMatrix(), (float) p4.x, nextY2, (float) p4.z).tex(nextProgress, 0).endVertex();
            
            BUFFER.pos(matrix.getLast().getMatrix(), (float) p2.x, y2, (float) p2.z).tex(progress, 0).endVertex();
            BUFFER.pos(matrix.getLast().getMatrix(), (float) p2.x, y1, (float) p2.z).tex(progress, 1).endVertex();
            BUFFER.pos(matrix.getLast().getMatrix(), (float) p3.x, nextY1, (float) p3.z).tex(nextProgress, 1).endVertex();
            BUFFER.pos(matrix.getLast().getMatrix(), (float) p3.x, nextY2, (float) p3.z).tex(nextProgress, 0).endVertex();
        }
        
        TESSELLATOR.draw();
        
        ShaderManager.trailWaves.unload();
        
        GL13.glActiveTexture(GL13.GL_TEXTURE20);
        GlStateManager.bindTexture(0);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GlStateManager.bindTexture(0);
        
        stopRendering();
    }

    private void drawLine(MatrixStack matrix, List<Point> points, boolean withHeight) {
        BUFFER.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        final Vector3d projectedView = mc.getRenderManager().info.getProjectedView();
        int index = 0;
        for (Point point : points) {
            final float alpha = Math.min((float) index / (float) points.size(), 1F);
            final int color = Theme.getInstance().clientColor();
            final int finalColor = ColorUtil.replAlpha(color, alpha / 2F);
            final Vector3d pos = point.pos.subtract(projectedView);
            if (withHeight)
                BUFFER.pos(matrix.getLast().getMatrix(), (float) pos.x, (float) (pos.y + mc.player.getHeight()), (float) pos.z).color(finalColor).endVertex();
            else
                BUFFER.pos(matrix.getLast().getMatrix(), (float) pos.x, (float) pos.y + 0.0005F, (float) pos.z).color(finalColor).endVertex();
            index++;
        }
        TESSELLATOR.draw();
    }

    private static class Point {
        private final Vector3d pos;
        private final StopWatch time = new StopWatch();

        public Point(Vector3d pos) {
            this.pos = pos;
        }
    }

    private void startRendering() {
        RenderSystem.pushMatrix();
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.disableCull();
        RenderSystem.disableAlphaTest();
        RenderSystem.color4f(0, 0, 0, 0.1f);

    }

    private void stopRendering() {
        RenderSystem.enableAlphaTest();
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
        RenderSystem.popMatrix();
    }
}
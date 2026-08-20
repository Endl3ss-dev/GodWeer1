package fun.volon.api.utils.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import fun.volon.api.QClient;
import fun.volon.api.utils.color.ColorUtils;
import fun.volon.client.modules.impl.render.WorldTweaks;

public final class AuroraSkyRenderer implements QClient {

    public static final AuroraSkyRenderer INSTANCE = new AuroraSkyRenderer();

    private AuroraSkyRenderer() {
    }

    public void render(MatrixStack matrices, Matrix4f projectionMatrix) {
        WorldTweaks worldTweaks = WorldTweaks.INSTANCE;
        if (worldTweaks == null || !worldTweaks.isAuroraEnabled()) {
            return;
        }

        int color1 = worldTweaks.getAuroraColor1();
        int color2 = worldTweaks.getAuroraColor2();
        float speed = worldTweaks.getAuroraSpeed();
        float intensity = worldTweaks.getAuroraIntensity();
        float scale = worldTweaks.getAuroraScale();

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(org.lwjgl.opengl.GL11.GL_SRC_ALPHA, org.lwjgl.opengl.GL11.GL_ONE);
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
        RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);

        Matrix4f matrix = matrices.peek().getPositionMatrix();
        
        // Рисуем красивое северное сияние процедурно
        drawProceduralAurora(matrix, color1, color2, speed, intensity, scale);

        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
    }

    private void drawProceduralAurora(Matrix4f matrix, int color1, int color2, float speed, float intensity, float scale) {
        float time = (System.currentTimeMillis() % 100000L) / 1000.0f * speed;
        
        BufferBuilder buffer = Tessellator.getInstance().begin(
            VertexFormat.DrawMode.QUADS, 
            VertexFormats.POSITION_COLOR
        );

        float radius = 200.0f;
        int segments = 64;
        int rings = 24;
        
        // Рисуем полусферу неба с волнами северного сияния
        for (int ring = 0; ring < rings; ring++) {
            float phi0 = (float) Math.PI * 0.5f * ring / rings;
            float phi1 = (float) Math.PI * 0.5f * (ring + 1) / rings;
            
            float y0 = radius * (float) Math.sin(phi0);
            float r0 = radius * (float) Math.cos(phi0);
            
            float y1 = radius * (float) Math.sin(phi1);
            float r1 = radius * (float) Math.cos(phi1);
            
            for (int seg = 0; seg < segments; seg++) {
                float theta0 = 2.0f * (float) Math.PI * seg / segments;
                float theta1 = 2.0f * (float) Math.PI * (seg + 1) / segments;
                
                float x00 = r0 * (float) Math.cos(theta0);
                float z00 = r0 * (float) Math.sin(theta0);
                
                float x01 = r0 * (float) Math.cos(theta1);
                float z01 = r0 * (float) Math.sin(theta1);
                
                float x10 = r1 * (float) Math.cos(theta0);
                float z10 = r1 * (float) Math.sin(theta0);
                
                float x11 = r1 * (float) Math.cos(theta1);
                float z11 = r1 * (float) Math.sin(theta1);
                
                // Создаем волны северного сияния
                float wave1 = (float) Math.sin(theta0 * 2.0 * scale + time) * 0.3f;
                float wave2 = (float) Math.sin(theta1 * 2.0 * scale + time) * 0.3f;
                float wave3 = (float) Math.sin(theta0 * 4.0 * scale - time * 0.7) * 0.2f;
                float wave4 = (float) Math.sin(theta1 * 4.0 * scale - time * 0.7) * 0.2f;
                float wave5 = (float) Math.sin(phi0 * 3.0 + time * 0.5) * 0.15f;
                float wave6 = (float) Math.sin(phi1 * 3.0 + time * 0.5) * 0.15f;
                
                // Комбинируем волны для создания сложного паттерна
                float waveIntensity0 = (wave1 + wave3 + wave5 + 1.0f) * 0.5f;
                float waveIntensity1 = (wave2 + wave4 + wave5 + 1.0f) * 0.5f;
                float waveIntensity2 = (wave1 + wave3 + wave6 + 1.0f) * 0.5f;
                float waveIntensity3 = (wave2 + wave4 + wave6 + 1.0f) * 0.5f;
                
                // Интерполируем цвета на основе волн
                int c00 = ColorUtils.interpolateColor(color1, color2, waveIntensity0);
                int c01 = ColorUtils.interpolateColor(color1, color2, waveIntensity1);
                int c10 = ColorUtils.interpolateColor(color1, color2, waveIntensity2);
                int c11 = ColorUtils.interpolateColor(color1, color2, waveIntensity3);
                
                // Альфа зависит от высоты - ярче вверху, прозрачнее внизу
                float alpha0 = (phi0 / ((float) Math.PI * 0.5f)) * intensity * 0.7f;
                float alpha1 = (phi1 / ((float) Math.PI * 0.5f)) * intensity * 0.7f;
                
                // Добавляем дополнительную яркость в местах волн
                alpha0 *= (0.5f + waveIntensity0 * 0.5f);
                alpha1 *= (0.5f + waveIntensity2 * 0.5f);
                
                c00 = ColorUtils.setAlphaColor(c00, (int) (alpha0 * 255));
                c01 = ColorUtils.setAlphaColor(c01, (int) (alpha0 * 255));
                c10 = ColorUtils.setAlphaColor(c10, (int) (alpha1 * 255));
                c11 = ColorUtils.setAlphaColor(c11, (int) (alpha1 * 255));
                
                // Рисуем квад
                buffer.vertex(matrix, x00, y0, z00).color(c00);
                buffer.vertex(matrix, x10, y1, z10).color(c10);
                buffer.vertex(matrix, x11, y1, z11).color(c11);
                buffer.vertex(matrix, x01, y0, z01).color(c01);
            }
        }
        
        BufferRenderer.drawWithGlobalProgram(buffer.end());
        
        // Добавляем второй слой с другими волнами для глубины
        BufferBuilder buffer2 = Tessellator.getInstance().begin(
            VertexFormat.DrawMode.QUADS, 
            VertexFormats.POSITION_COLOR
        );
        
        for (int ring = 0; ring < rings; ring++) {
            float phi0 = (float) Math.PI * 0.5f * ring / rings;
            float phi1 = (float) Math.PI * 0.5f * (ring + 1) / rings;
            
            float y0 = radius * (float) Math.sin(phi0) * 0.98f;
            float r0 = radius * (float) Math.cos(phi0) * 0.98f;
            
            float y1 = radius * (float) Math.sin(phi1) * 0.98f;
            float r1 = radius * (float) Math.cos(phi1) * 0.98f;
            
            for (int seg = 0; seg < segments; seg++) {
                float theta0 = 2.0f * (float) Math.PI * seg / segments;
                float theta1 = 2.0f * (float) Math.PI * (seg + 1) / segments;
                
                float x00 = r0 * (float) Math.cos(theta0);
                float z00 = r0 * (float) Math.sin(theta0);
                
                float x01 = r0 * (float) Math.cos(theta1);
                float z01 = r0 * (float) Math.sin(theta1);
                
                float x10 = r1 * (float) Math.cos(theta0);
                float z10 = r1 * (float) Math.sin(theta0);
                
                float x11 = r1 * (float) Math.cos(theta1);
                float z11 = r1 * (float) Math.sin(theta1);
                
                // Второй слой волн с другой частотой и направлением
                float wave1 = (float) Math.sin(theta0 * 3.0 * scale - time * 1.3) * 0.4f;
                float wave2 = (float) Math.sin(theta1 * 3.0 * scale - time * 1.3) * 0.4f;
                float wave3 = (float) Math.cos(phi0 * 5.0 + time * 0.8) * 0.3f;
                float wave4 = (float) Math.cos(phi1 * 5.0 + time * 0.8) * 0.3f;
                
                float waveIntensity0 = (wave1 + wave3 + 1.0f) * 0.5f;
                float waveIntensity1 = (wave2 + wave3 + 1.0f) * 0.5f;
                float waveIntensity2 = (wave1 + wave4 + 1.0f) * 0.5f;
                float waveIntensity3 = (wave2 + wave4 + 1.0f) * 0.5f;
                
                // Обратная интерполяция цветов для контраста
                int c00 = ColorUtils.interpolateColor(color2, color1, waveIntensity0);
                int c01 = ColorUtils.interpolateColor(color2, color1, waveIntensity1);
                int c10 = ColorUtils.interpolateColor(color2, color1, waveIntensity2);
                int c11 = ColorUtils.interpolateColor(color2, color1, waveIntensity3);
                
                float alpha0 = (phi0 / ((float) Math.PI * 0.5f)) * intensity * 0.4f;
                float alpha1 = (phi1 / ((float) Math.PI * 0.5f)) * intensity * 0.4f;
                
                alpha0 *= (0.3f + waveIntensity0 * 0.7f);
                alpha1 *= (0.3f + waveIntensity2 * 0.7f);
                
                c00 = ColorUtils.setAlphaColor(c00, (int) (alpha0 * 255));
                c01 = ColorUtils.setAlphaColor(c01, (int) (alpha0 * 255));
                c10 = ColorUtils.setAlphaColor(c10, (int) (alpha1 * 255));
                c11 = ColorUtils.setAlphaColor(c11, (int) (alpha1 * 255));
                
                buffer2.vertex(matrix, x00, y0, z00).color(c00);
                buffer2.vertex(matrix, x10, y1, z10).color(c10);
                buffer2.vertex(matrix, x11, y1, z11).color(c11);
                buffer2.vertex(matrix, x01, y0, z01).color(c01);
            }
        }
        
        BufferRenderer.drawWithGlobalProgram(buffer2.end());
        
        // Добавляем яркие полосы света
        BufferBuilder buffer3 = Tessellator.getInstance().begin(
            VertexFormat.DrawMode.QUADS, 
            VertexFormats.POSITION_COLOR
        );
        
        int numRays = 8;
        for (int i = 0; i < numRays; i++) {
            float rayAngle = (2.0f * (float) Math.PI * i / numRays) + time * 0.3f;
            float rayWidth = 0.15f;
            
            for (int ring = 0; ring < rings; ring++) {
                float phi0 = (float) Math.PI * 0.5f * ring / rings;
                float phi1 = (float) Math.PI * 0.5f * (ring + 1) / rings;
                
                float y0 = radius * (float) Math.sin(phi0) * 0.96f;
                float r0 = radius * (float) Math.cos(phi0) * 0.96f;
                
                float y1 = radius * (float) Math.sin(phi1) * 0.96f;
                float r1 = radius * (float) Math.cos(phi1) * 0.96f;
                
                float x00 = r0 * (float) Math.cos(rayAngle - rayWidth);
                float z00 = r0 * (float) Math.sin(rayAngle - rayWidth);
                
                float x01 = r0 * (float) Math.cos(rayAngle + rayWidth);
                float z01 = r0 * (float) Math.sin(rayAngle + rayWidth);
                
                float x10 = r1 * (float) Math.cos(rayAngle - rayWidth);
                float z10 = r1 * (float) Math.sin(rayAngle - rayWidth);
                
                float x11 = r1 * (float) Math.cos(rayAngle + rayWidth);
                float z11 = r1 * (float) Math.sin(rayAngle + rayWidth);
                
                float brightness = (float) Math.sin(time * 2.0 + i * 0.5) * 0.3f + 0.7f;
                float alpha0 = (phi0 / ((float) Math.PI * 0.5f)) * intensity * 0.5f * brightness;
                float alpha1 = (phi1 / ((float) Math.PI * 0.5f)) * intensity * 0.5f * brightness;
                
                int brightColor = ColorUtils.interpolateColor(color1, color2, 0.5f);
                int c0 = ColorUtils.setAlphaColor(brightColor, (int) (alpha0 * 255));
                int c1 = ColorUtils.setAlphaColor(brightColor, (int) (alpha1 * 255));
                
                buffer3.vertex(matrix, x00, y0, z00).color(c0);
                buffer3.vertex(matrix, x10, y1, z10).color(c1);
                buffer3.vertex(matrix, x11, y1, z11).color(c1);
                buffer3.vertex(matrix, x01, y0, z01).color(c0);
            }
        }
        
        BufferRenderer.drawWithGlobalProgram(buffer3.end());
    }
}
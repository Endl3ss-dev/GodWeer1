package swerve.ui.menu;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import swerve.Swerve;
import swerve.utils.render.ShaderUtils;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.CompiledShaderProgram;

import org.joml.Matrix4f;

/**
 * Шейдерный фон GUI меню: «шёлковое полотно» с плазменными прожилками в цветах палитры.
 *
 * <p>Рисуется одним полноэкранным квадом поверх тонированного размытия и под панелями меню.
 * Цвета берутся из {@link MenuTheme} (акцент, второй цвет, база), поэтому фон всегда в теме
 * и плавно перетекает при смене палитры. Время замирает при выключенных анимациях.
 */
final class MenuBg {

    /** Старт отсчёта времени: float хватает на часы непрерывной работы. */
    private static long startNanos = System.nanoTime();

    private static boolean warned;

    private MenuBg() {
    }

    /**
     * Рисует фон на всю область. Координаты — реальные пиксели окна (пространство {@link Px}).
     *
     * @param alpha общая прозрачность (анимация открытия/закрытия меню)
     */
    static void draw(GuiGraphics g, int width, int height, float alpha) {
        if (width <= 0 || height <= 0 || alpha <= 0.01f) {
            return;
        }
        CompiledShaderProgram shader = ShaderUtils.get(ShaderUtils.MENU_BG);
        if (shader == null) {
            if (!warned) {
                warned = true;
                Swerve.LOGGER.error("Shader 'menubg' is not compiled, menu background falls back to flat dim");
            }
            return;
        }

        // время течёт только при включённых анимациях; %3600 бережёт точность float
        float time = MenuPrefs.animations
                ? (System.nanoTime() - startNanos) / 1_000_000_000.0f % 3600.0f
                : 0.0f;

        ShaderUtils.set(shader, "Time", time);
        ShaderUtils.set(shader, "Resolution", (float) width, (float) height);
        ShaderUtils.setColor(shader, "ColorA", MenuTheme.accent() | 0xFF000000);
        ShaderUtils.setColor(shader, "ColorB", MenuTheme.accent2() | 0xFF000000);
        ShaderUtils.setColor(shader, "Base", MenuTheme.p().bg());
        ShaderUtils.set(shader, "Intensity", alpha);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();

        Matrix4f matrix = g.pose().last().pose();
        BufferBuilder buffer = Tesselator.getInstance()
                .begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        buffer.addVertex(matrix, 0.0f, 0.0f, 0.0f).setColor(1.0f, 1.0f, 1.0f, 1.0f);
        buffer.addVertex(matrix, 0.0f, (float) height, 0.0f).setColor(1.0f, 1.0f, 1.0f, 1.0f);
        buffer.addVertex(matrix, (float) width, (float) height, 0.0f).setColor(1.0f, 1.0f, 1.0f, 1.0f);
        buffer.addVertex(matrix, (float) width, 0.0f, 0.0f).setColor(1.0f, 1.0f, 1.0f, 1.0f);

        MeshData mesh = buffer.build();
        if (mesh != null) {
            RenderSystem.setShader(ShaderUtils.MENU_BG);
            BufferUploader.drawWithShader(mesh);
        }

        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        RenderSystem.setShaderTexture(0, 0);
    }
}

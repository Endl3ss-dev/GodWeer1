package org.privates.client.utils.render.draw;

import lombok.experimental.UtilityClass;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

@UtilityClass
public class ScissorUtil {
    public void enable() {
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
    }

    public void disable() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }


    public static void disableScissor() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    public static void enableScissor(double x, double y, double width, double height) {
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        // Конвертация координат в пиксели экрана
        MainWindow window = Minecraft.getInstance().getMainWindow();
        double scale = window.getScaleFactor();
        GL11.glScissor(
                (int) (x * scale),
                (int) ((window.getScaledHeight() - (y + height)) * scale),
                (int) (width * scale),
                (int) (height * scale)
        );
    }

    public void scissor(MainWindow window, double x, double y, double width, double height) {
        if (x + width == x || y + height == y || x < 0 || y + height < 0) return;
        final double scaleFactor = window.getScaleFactor();
        GL11.glScissor((int) Math.round(x * scaleFactor), (int) Math.round((window.getScaledHeight() - (y + height)) * scaleFactor), Math.max(1, (int) Math.round(width * scaleFactor)), Math.max(1, (int) Math.round(height * scaleFactor)));
    }
}
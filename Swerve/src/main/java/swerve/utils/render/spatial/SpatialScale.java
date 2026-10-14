package swerve.utils.render.spatial;

import com.mojang.blaze3d.platform.Window;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.Mth;

/**
 * Масштаб GUI для панели: всегда «авто» от размера окна, независимо от настройки игрока,
 * чтобы текстура экрана была полного разрешения.
 */
public final class SpatialScale {

    private SpatialScale() {
    }

    public static int autoScale(int width, int height) {
        if (width <= 0 || height <= 0) {
            return 1;
        }
        int scale = 1;
        while (width / (scale + 1) >= 320 && height / (scale + 1) >= 240) {
            scale++;
        }
        if (scale == 1 && height >= 430 && width >= 640) {
            scale = 2;
        }
        return scale;
    }

    public static int effective(Window window) {
        return autoScale(window.getWidth(), window.getHeight());
    }

    public static int scaledWidth(Window window) {
        return Mth.ceil(window.getWidth() / (double) effective(window));
    }

    public static int scaledHeight(Window window) {
        return Mth.ceil(window.getHeight() / (double) effective(window));
    }

    /** Подмена Window.getGuiScale/getGuiScaledWidth/Height активна только вне отрисовки мира и HUD. */
    public static boolean shouldOverride() {
        if (SpatialState.skipWindowOverride || !SpatialState.active()) {
            return false;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.level == null || mc.gui == null) {
            return false;
        }
        if (SpatialState.hookable(mc.screen)) {
            return true;
        }
        return SpatialState.hookable(SpatialState.hooked);
    }

    /** Размеры в Screen.init/resize: перехватываемый экран раскладывается под масштаб панели. */
    public static int fixSize(Screen screen, int original, boolean width) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.level == null || !SpatialState.hookable(screen)) {
            return original;
        }
        Window window = mc.getWindow();
        return width ? scaledWidth(window) : scaledHeight(window);
    }
}

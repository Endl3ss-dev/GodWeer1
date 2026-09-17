package godweer.utils.render.blur;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;

/**
 * Порт {@code org.privates.client.utils.render.blur.GaussianBlur} из Private.
 * <p>
 * Фаза {@link #startBlur()} делает снимок кадра и выполняет отложенные вызовы
 * {@link BlurRenderer#registerRenderCall(Runnable)}, фаза {@link #endBlur(float, int)} прогоняет
 * цепочку и выводит результат на весь экран.
 * <p>
 * Отличие от источника: между {@code startBlur()} и {@code endBlur()} offscreen-таргет НЕ остаётся
 * привязанным — иначе исключение в чужом коде оставит клиент с активным FBO. Для захвата
 * собственной отрисовки используй {@link BlurRenderer#registerRenderCall(Runnable)}.
 */
public final class GaussianBlur {

    private static final float DEFAULT_RADIUS = 8.0F;
    private static final PoseStack IDENTITY = new PoseStack();

    private static boolean active;

    private GaussianBlur() {
    }

    public static void startBlur() {
        active = BlurRenderer.capture(IDENTITY);
    }

    public static void endBlur() {
        endBlur(DEFAULT_RADIUS, 0);
    }

    public static void endBlur(float radius) {
        endBlur(radius, 0);
    }

    public static void endBlur(float radius, int iterations) {
        if (!active) {
            return;
        }

        active = false;

        if (!BlurRenderer.finish(IDENTITY, radius, iterations)) {
            return;
        }

        Window window = Minecraft.getInstance().getWindow();
        BlurRenderer.drawBlurred(IDENTITY, 0.0F, 0.0F,
                window.getGuiScaledWidth(), window.getGuiScaledHeight(), 0xFFFFFFFF);
    }

    public static boolean isActive() {
        return active;
    }

    public static void cleanup() {
        active = false;
        BlurRenderer.cleanup();
    }
}

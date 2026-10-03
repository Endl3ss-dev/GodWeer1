package godweer.ui.newgui;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;

// Математика анимаций перенесена 1:1 из исходного клиента: fast() опирается на FPS,
// как и оригинал, поэтому скорости анимаций совпадают при любом FPS.
public final class Mathf {

    private Mathf() {
    }

    private static float deltaTime() {
        int fps = Minecraft.getInstance().getFps(); // VERIFY: существует в 1.21.4 (используется ванильным FPS-худом)
        return fps > 0 ? 1.0f / fps : 1.0f;
    }

    public static float fast(float end, float start, float multiple) {
        float t = Mth.clamp(deltaTime() * multiple, 0.0f, 1.0f);
        return (1.0f - t) * end + t * start;
    }

    public static double step(double value, double steps) {
        double roundedValue = Math.round(value / steps) * steps;
        return Math.round(roundedValue * 100.0) / 100.0;
    }

    public static boolean inRange(float y, float height, float rangeY, float rangeHeight) {
        return y <= rangeY + rangeHeight && y + height >= rangeY;
    }

    public static boolean isHover(double mouseX, double mouseY, float x, float y, float width, float height) {
        return mouseX >= x && mouseY >= y && mouseX <= x + width && mouseY <= y + height;
    }
}

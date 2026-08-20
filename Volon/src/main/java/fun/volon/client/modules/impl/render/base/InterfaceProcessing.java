package fun.volon.client.modules.impl.render.base;

import lombok.RequiredArgsConstructor;
import net.minecraft.client.util.math.MatrixStack;
import fun.volon.Volon;
import fun.volon.api.QClient;
import fun.volon.api.events.implement.EventRender;
import fun.volon.api.events.implement.EventUpdate;
import fun.volon.api.storages.implement.helpertstorages.enumvar.ModuleClass;
import fun.volon.api.utils.color.ColorUtils;
import fun.volon.api.utils.draggable.Draggable;
import fun.volon.api.utils.render.RenderUtils;
import fun.volon.api.utils.render.blur.Blur;
import fun.volon.client.modules.impl.render.Interface;

@RequiredArgsConstructor
public class InterfaceProcessing implements QClient {

    protected static final Blur BLUR = new Blur();
    protected static final float BLUR_STRENGTH = 8.0f;

    public final Draggable draggable;
    private HudRectStyle hudRectStyle = HudRectStyle.SHADER;
    private boolean blurEnabled = false;
    /** Если true — рендерится v2 стиль этого элемента вместо v1 */
    private boolean useV2Style = false;

    public boolean isUseV2Style() { return useV2Style; }
    public void setUseV2Style(boolean v) { this.useV2Style = v; }

    public HudRectStyle getHudRectStyle() {
        return hudRectStyle;
    }

    public void setHudRectStyle(HudRectStyle hudRectStyle) {
        this.hudRectStyle = hudRectStyle != null ? hudRectStyle : HudRectStyle.SHADER;
    }

    public boolean isHudRectThreads() {
        return hudRectStyle == HudRectStyle.THREADS;
    }

    public boolean isBlurEnabled() {
        return blurEnabled;
    }

    public void setBlurEnabled(boolean blurEnabled) {
        this.blurEnabled = blurEnabled;
    }

    /**
     * Рисует фон HUD элемента — тёмный, с лёгким оттенком цвета темы.
     */
    public void drawHudBackground(MatrixStack matrices, float x, float y, float width, float height, int themeColor) {
        // Тёмный фон с лёгким цветовым оттенком от темы
        int r = (themeColor >> 16) & 0xFF;
        int g = (themeColor >> 8)  & 0xFF;
        int b =  themeColor        & 0xFF;
        // Смешиваем: базовый тёмный (12, 12, 14) + 8% цвет темы
        int mixR = (int)(12 + r * 0.08f);
        int mixG = (int)(12 + g * 0.08f);
        int mixB = (int)(14 + b * 0.08f);
        int bgColor = ColorUtils.rgba(
                Math.min(mixR, 255),
                Math.min(mixG, 255),
                Math.min(mixB, 255),
                210
        );
        RenderUtils.drawRoundedRect(matrices, x, y, width, height, 3f, bgColor);
    }

    /**
     * Фон HUD-элемента: polar-blur через шейдер (если включён) + полупрозрачный тонирующий слой,
     * либо обычный rounded rect.
     */
    public void drawHudBlurOrRect(MatrixStack matrices, float x, float y, float width, float height,
                                  float radius, int color) {
        // blur убран — вызывал краш при выходе с сервера
        RenderUtils.drawRoundedRect(matrices, x, y, width, height, radius, color);
    }

    public void drawHudBlurOrRect(MatrixStack matrices, float x, float y, float width, float height,
                                  float topLeft, float topRight, float bottomRight, float bottomLeft, int color) {
        // blur убран — вызывал краш при выходе с сервера
        RenderUtils.drawRoundedRect(matrices, x, y, width, height, topLeft, topRight, bottomRight, bottomLeft, color);
    }

    public void onUpdate(EventUpdate eventUpdate) {
    }

    public void onRender(EventRender.Default eventRender) {

    }
}

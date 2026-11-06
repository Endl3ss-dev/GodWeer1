package tech.lightclient.ui.gui.elements;

import tech.lightclient.client.utils.render.CursorHelper;
import tech.lightclient.client.utils.render.animations.Easings;
import tech.lightclient.client.utils.render.animations.impl.SimpleLinearAnimation;
import tech.lightclient.client.utils.render.main.core.Renderer2D;
import tech.lightclient.client.utils.render.main.text.FontRegistry;
import tech.lightclient.modules.api.settings.impl.RangeSliderSetting;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

/** Two-handle slider, drawn to match {@link CsSliderComponent}. */
public final class CsRangeSliderComponent extends CsSettingComponent<RangeSliderSetting> {

    private double visualFrom;
    private double visualTo;
    private int dragging = -1; // -1 none, 0 = from handle, 1 = to handle
    private final SimpleLinearAnimation knobHover = new SimpleLinearAnimation(160);

    CsRangeSliderComponent(RangeSliderSetting setting, float width) {
        super(setting, width, 43f);
        visualFrom = pct(setting.from());
        visualTo = pct(setting.to());
        knobHover.setEasing(Easings.EASE_OUT_CUBIC);
    }

    private double pct(float value) {
        float range = setting.max - setting.min;
        if (range <= 0f) return 0.0;
        return Math.max(0f, Math.min(1f, (value - setting.min) / range));
    }

    private float valueAt(double percent) {
        float raw = setting.min + (float) percent * (setting.max - setting.min);
        if (setting.increment > 0f) raw = Math.round(raw / setting.increment) * setting.increment;
        return Math.max(setting.min, Math.min(setting.max, raw));
    }

    @Override
    public void draw(Renderer2D render, float x, float y, int mouseX, int mouseY, int alpha) {
        visualFrom += (pct(setting.from()) - visualFrom) * 0.22;
        visualTo += (pct(setting.to()) - visualTo) * 0.22;

        float rowX = x + ROW_PAD_X;
        float rowW = width - ROW_PAD_X * 2f;

        String value = format(setting.from()) + " - " + format(setting.to());
        float valueSize = 11.5f;
        float valueW = textWidth(value, valueSize);

        drawAnimatedSettingText(render, rowX, y + 11.5f, rowW - valueW - 8f, 13.5f,
                setting.getName(), withAlpha(COLOR_NAME, alpha));
        render.text(FontRegistry.DELTA_CLICKGUI, rowX + rowW - valueW, y + 11.5f, valueSize, value, themeAccent(alpha));

        float trackH = 4.5f;
        float barY = y + 23.5f;

        boolean barHover = hovered(mouseX, mouseY, rowX, barY - 6f, rowW, trackH + 12f);
        if (barHover) {
            CursorHelper.setHResize();
            knobHover.show();
        } else {
            knobHover.hide();
        }

        if (dragging >= 0) {
            long handle = MinecraftClient.getInstance().getWindow().getHandle();
            if (GLFW.glfwGetMouseButton(handle, GLFW.GLFW_MOUSE_BUTTON_LEFT) != GLFW.GLFW_PRESS) {
                dragging = -1;
            } else {
                applyDrag(mouseX, rowX, rowW);
            }
        }

        render.rect(rowX, barY, rowW, trackH, trackH * 0.5f, withAlpha(0xFFFFFF, (int) (alpha * 0.12f)));

        float fromX = rowX + rowW * (float) visualFrom;
        float toX = rowX + rowW * (float) visualTo;
        render.rect(fromX, barY, Math.max(0f, toX - fromX), trackH, trackH * 0.5f, themeAccent((int) (alpha * 0.95f)));

        float knobR = 5f + 1.4f * knobHover.getProgress();
        float knobY = barY + trackH * 0.5f;
        for (float kx : new float[]{fromX, toX}) {
            render.circle(kx, knobY, knobR + 1.5f, 0f, 1f, withAlpha(0x000000, (int) (alpha * 0.25f)));
            render.circle(kx, knobY, knobR, 0f, 1f, withAlpha(0xF5F6FA, alpha));
        }

        float labelY = barY + trackH + 14f;
        float labelSize = 10f;
        int labelCol = withAlpha(COLOR_VALUE, (int) (alpha * 0.85f));
        String minLabel = format(setting.min);
        String maxLabel = format(setting.max);
        render.text(FontRegistry.DELTA_CLICKGUI, rowX, labelY, labelSize, minLabel, labelCol);
        render.text(FontRegistry.DELTA_CLICKGUI, rowX + rowW - textWidth(maxLabel, labelSize), labelY, labelSize, maxLabel, labelCol);
    }

    private String format(float v) {
        if (setting.increment >= 1f) return String.valueOf((int) v);
        return String.format(java.util.Locale.US, "%.2f", v);
    }

    private void applyDrag(double mouseX, float barX, float barW) {
        if (barW <= 0f) return;
        double percent = Math.max(0.0, Math.min(1.0, (mouseX - barX) / barW));
        float v = valueAt(percent);
        if (dragging == 0) {
            setting.setRange(Math.min(v, setting.to()), setting.to());
        } else {
            setting.setRange(setting.from(), Math.max(v, setting.from()));
        }
    }

    @Override
    public void mouseClicked(double mx, double my, int button, float x, float y) {
        if (button != GLFW.GLFW_MOUSE_BUTTON_LEFT) return;
        float rowX = x + ROW_PAD_X;
        float rowW = width - ROW_PAD_X * 2f;
        float barY = y + 23.5f;
        if (!hovered(mx, my, rowX, barY - 6f, rowW, 4.5f + 12f)) return;

        // Grab whichever handle is nearer to the click.
        double clickPct = Math.max(0.0, Math.min(1.0, (mx - rowX) / rowW));
        dragging = Math.abs(clickPct - pct(setting.from())) <= Math.abs(clickPct - pct(setting.to())) ? 0 : 1;
        applyDrag(mx, rowX, rowW);
    }
}

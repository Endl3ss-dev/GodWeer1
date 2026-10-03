package godweer.ui.newgui;

import com.mojang.blaze3d.vertex.PoseStack;

import godweer.managers.ThemeManager;
import godweer.modules.api.setting.BindSetting;
import godweer.modules.api.setting.BooleanSetting;
import godweer.modules.api.setting.FloatSetting;
import godweer.modules.api.setting.ListSetting;
import godweer.modules.api.setting.ModeSetting;
import godweer.modules.api.setting.Setting;
import godweer.modules.api.setting.TextSetting;
import godweer.utils.color.ColorUtils;
import godweer.utils.render.ScissorUtils;

import net.minecraft.util.Mth;

import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

// Компонент настройки newgui. Ветками покрыты типы, существующие в GodWeer:
// Boolean / Float (слайдер) / Mode / List (мульти-Boolean) / Bind / Text / сетка тем.
// Режим отображения зафиксирован на дефолте исходника — «Под модулем».
public class SettingComponent extends Builder {

    private static final long BIND_CLICK_COOLDOWN_MS = 200;

    private static long lastBindClickTime;

    private final ModuleComponent module;
    private final Setting setting;

    private boolean selected;
    private float selectedV, animV, alphaV;
    private final ArrayList<Float> interpolate = new ArrayList<>();
    private float deltaV;
    private long time;
    private int offsetX;

    private float gridScroll;
    private float gridScrollV;

    private float nameScrollX;
    private long lastScrollTime;
    private boolean scrollingForward = true;
    private long scrollPauseUntil;

    private boolean isInputMode = false;
    private String inputValueString = "";

    private float hoverAnim = 0.0f;
    private boolean wasHovered = false;

    // Кэш форматированного значения слайдера: String.format не должен зваться каждый кадр.
    private float cachedSliderValue = Float.NaN;
    private String cachedSliderText = "";

    private String cachedListValues;
    private int cachedListMask = -1;

    public SettingComponent(ModuleComponent module, Setting setting, float x, float y) {
        this.module = module;
        this.setting = setting;
        this.x = x;
        this.y = y;
        float sScale = module.getPanel().getGui().getResolutionScale();
        this.width = 270.0f * sScale;
        this.height = 12.0f * sScale;
        if (setting instanceof ListSetting s) {
            List<BooleanSetting> values = s.getSettings();
            for (int i = 0; i < values.size(); i++) {
                interpolate.add(values.get(i).get() ? 1.0f : 0.0f);
            }
            time = System.currentTimeMillis();
        }
    }

    private float getResolutionScale() {
        return module.getPanel().getGui().getResolutionScale();
    }

    public Setting getSetting() {
        return setting;
    }

    public float getDeltaV() {
        return deltaV;
    }

    public void setDeltaV(float deltaV) {
        this.deltaV = deltaV;
    }

    public void setAlphaV(float alphaV) {
        this.alphaV = alphaV;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    private String sliderText(float value) {
        if (cachedSliderText.isEmpty() || cachedSliderValue != value) {
            cachedSliderText = String.format("%.2f", value);
            cachedSliderValue = value;
        }
        return cachedSliderText;
    }

    private String listValues(ListSetting s) {
        List<BooleanSetting> values = s.getSettings();
        int mask = 0;
        for (int i = 0; i < values.size(); i++) {
            if (values.get(i).get()) {
                mask |= 1 << i;
            }
        }
        if (mask != cachedListMask || cachedListValues == null) {
            cachedListMask = mask;
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < values.size(); i++) {
                BooleanSetting value = values.get(i);
                if (value.get()) {
                    if (builder.length() > 0) {
                        builder.append(", ");
                    }
                    builder.append(value.name());
                }
            }
            cachedListValues = builder.toString();
        }
        return cachedListValues;
    }

    private void drawScrollingName(PoseStack stack, String name, float x, float y,
                                   float availableWidth, int color, float size) {
        float sScale = getResolutionScale();
        float nameWidth = GuiFonts.SF_BOLD.getWidth(name, size);

        if (nameWidth > availableWidth) {
            long currentTime = System.currentTimeMillis();

            if (currentTime > scrollPauseUntil) {
                float speed = 0.5f * sScale;
                long updateRate = 20;

                if (currentTime - lastScrollTime > updateRate) {
                    if (scrollingForward) {
                        nameScrollX -= speed;
                        if (nameScrollX <= -(nameWidth - availableWidth)) {
                            nameScrollX = -(nameWidth - availableWidth);
                            scrollingForward = false;
                            scrollPauseUntil = currentTime + 1500;
                        }
                    } else {
                        nameScrollX += speed;
                        if (nameScrollX >= 0) {
                            nameScrollX = 0;
                            scrollingForward = true;
                            scrollPauseUntil = currentTime + 1500;
                        }
                    }
                    lastScrollTime = currentTime;
                }
            }
        } else {
            nameScrollX = 0;
        }

        ScissorUtils.push();
        ScissorUtils.setFromComponentCoordinates(x, y - 2.0f * sScale, availableWidth, size + 4.0f * sScale);
        GuiFonts.SF_BOLD.draw(stack, name, x + nameScrollX, y, color, size);
        ScissorUtils.unset();
        ScissorUtils.pop();
    }

    private void applyInputValue(FloatSetting s) {
        try {
            float newValue = Float.parseFloat(inputValueString);
            s.setValue(Mth.clamp(newValue, s.getMin(), s.getMax()));
        } catch (NumberFormatException ignored) {
        }
        inputValueString = "";
        isInputMode = false;
    }

    // Колесо мыши по раскрытой сетке тем; true — событие поглощено.
    public boolean scrollThemeGrid(float mouseX, float mouseY, float delta) {
        if (selected && setting instanceof ThemeGridSetting) {
            float sScale = getResolutionScale();
            float panelY = y + deltaV + 16.0f * sScale;
            float viewport = height - 16.0f * sScale;
            if (isHovered(mouseX, mouseY, x, panelY, width, viewport)) {
                gridScroll -= delta * GuiStyle.themeScrollSpeed();
                return true;
            }
        }
        return false;
    }

    public void render(PoseStack stack, float mouseX, float mouseY) {
        float sScale = getResolutionScale();

        boolean isCurrentlyHovered = isHovered(mouseX, mouseY, x, y + deltaV, width, 12.0f * sScale)
                && setting.name() != null && !setting.name().isEmpty();
        hoverAnim = Mathf.fast(hoverAnim, isCurrentlyHovered ? 1.0f : 0.0f, 10.0f);

        if (isCurrentlyHovered && !wasHovered) {
            wasHovered = true;
        } else if (!isCurrentlyHovered) {
            wasHovered = false;
        }

        if (setting instanceof BooleanSetting s) {
            renderBoolean(stack, s, sScale);
        } else if (setting instanceof FloatSetting s) {
            renderSlider(stack, s, sScale, mouseX, mouseY);
        } else if (setting instanceof ModeSetting s) {
            renderMode(stack, s, sScale);
        } else if (setting instanceof ListSetting s) {
            renderList(stack, s, sScale);
        } else if (setting instanceof BindSetting s) {
            renderBind(stack, s, sScale);
        } else if (setting instanceof TextSetting s) {
            renderText(stack, s, sScale);
        } else if (setting instanceof ThemeGridSetting s) {
            renderThemeGrid(stack, s, sScale, mouseX, mouseY);
        }
    }

    private void renderBoolean(PoseStack stack, BooleanSetting s, float sScale) {
        height = 12.0f * sScale;
        selectedV = Mathf.fast(selectedV, s.get() ? 1.0f : 0.0f, 16.0f);

        float controlWidth = 16.0f * sScale + 4.0f * sScale;
        float availableNameWidth = width - controlWidth;
        float nameY = y + deltaV + height / 2.0f - GuiFonts.SF_BOLD.getHeight(s.name(), 7.0f * sScale) / 1.5f;
        drawScrollingName(stack, s.name(), x, nameY, availableNameWidth,
                ColorUtils.multAlpha(GuiTheme.disabledColor(), alphaV), 7.0f * sScale);

        GuiRender.roundedWithOutline(stack,
                x + width - 16.0f * sScale, y + deltaV + 2.0f * sScale, 16.0f * sScale, height - 4.0f * sScale,
                sScale,
                ColorUtils.multAlpha(ColorUtils.overCol(ColorUtils.getColor(40, 40, 40, 60),
                        ColorUtils.multAlpha(GuiTheme.clientColor(), 0.1f), selectedV), alphaV),
                ColorUtils.multAlpha(ColorUtils.overCol(ColorUtils.getColor(100, 100, 100, 60),
                        ColorUtils.multAlpha(GuiTheme.clientColor(), 0.2f), selectedV), alphaV),
                GuiStyle.toggleRadius() * sScale);
        GuiRender.roundedRect(stack,
                x + width - 16.0f * sScale + (selectedV * 8.0f * sScale + 1.0f * sScale),
                y + deltaV + 3.0f * sScale, 6.0f * sScale, 6.0f * sScale,
                GuiStyle.toggleRadius() * sScale,
                ColorUtils.overCol(ColorUtils.getColor(100, 100, 100, (int) (alphaV * 255.0f)),
                        ColorUtils.getColor(220, 220, 220, (int) (alphaV * 255.0f)), selectedV));
    }

    private void renderSlider(PoseStack stack, FloatSetting s, float sScale, float mouseX, float mouseY) {
        height = 20.0f * sScale;

        float nameY = y + deltaV + 2.0f * sScale;
        String valueText = isInputMode ? inputValueString + "_" : sliderText(s.get());
        float valueTextWidth = GuiFonts.SF_BOLD.getWidth(valueText, 7.0f * sScale);
        float boxWidth = valueTextWidth + 4.0f * sScale;
        float boxHeight = GuiFonts.SF_BOLD.getHeight(valueText, 7.0f * sScale) + 4.0f * sScale;
        float boxX = x + width - boxWidth - 2.0f * sScale;
        float boxY = nameY - 2.0f * sScale;

        float availableNameWidth = boxX - x - 2.0f * sScale;
        drawScrollingName(stack, s.name(), x + 0.5f * sScale, nameY, availableNameWidth,
                ColorUtils.multAlpha(GuiTheme.disabledColor(), alphaV), 7.0f * sScale);

        GuiRender.roundedRect(stack, boxX, boxY, boxWidth, boxHeight,
                3.0f * sScale,
                ColorUtils.multAlpha(GuiTheme.clientColor(), 0.1f * alphaV));

        int valueColor = ColorUtils.multAlpha(
                GuiStyle.sliderThemeValues() ? GuiTheme.clientColor() : ColorUtils.getColor(255, 255, 255, 255),
                (int) (alphaV * 255.0f));
        if (isInputMode) {
            String renderString = inputValueString;
            if (System.currentTimeMillis() % 1000 < 500) {
                renderString += "_";
            }
            GuiFonts.SF_BOLD.draw(stack, renderString, boxX + 2.0f * sScale, boxY + 2.0f * sScale,
                    valueColor, 7.0f * sScale);
        } else {
            GuiFonts.SF_BOLD.draw(stack, valueText, boxX + 2.0f * sScale, boxY + 2.0f * sScale,
                    valueColor, 7.0f * sScale);
        }

        float sliderWidth = width - 4.0f * sScale;
        float sliderX = x + 0.5f * sScale;
        float sliderY = y + deltaV + height - 5.0f * sScale;

        if (selected) {
            float sliderMinX = sliderX;
            float sliderMaxX = sliderX + sliderWidth + 2.0f;
            float sliderMinY = sliderY - 3.0f * sScale;
            float sliderMaxY = sliderY + 7.0f * sScale + 2.0f;

            if (mouseX >= sliderMinX && mouseX <= sliderMaxX && mouseY >= sliderMinY && mouseY <= sliderMaxY) {
                float newProgress = Mth.clamp((mouseX - sliderX) / sliderWidth, 0.0f, 1.0f);
                float step = s.getIncrement() <= 0.0f ? 0.1f : s.getIncrement();
                s.setValue((float) Mathf.step(s.getMin() + (s.getMax() - s.getMin()) * newProgress, step));
            } else {
                selected = false;
            }
        }

        float progress = (s.getMax() == s.getMin()) ? 0.0f : (s.get() - s.getMin()) / (s.getMax() - s.getMin());
        float targetKnobX = sliderX + ((sliderWidth - 6.5f * sScale) * progress) + 1.75f * sScale;
        animV = Mathf.fast(animV, targetKnobX, 16.0f);

        GuiRender.roundedRect(stack, sliderX, sliderY, sliderWidth, 4.0f * sScale,
                3.0f * sScale, ColorUtils.multAlpha(GuiTheme.clientColor(), alphaV / 10.0f));
        GuiRender.roundedWithOutline(stack,
                animV, sliderY - 1.0f + sScale, 4.0f * sScale, 3.75f * sScale, 1.0f * sScale,
                ColorUtils.multAlpha(GuiTheme.clientColor(), 0.4f * alphaV),
                ColorUtils.multAlpha(GuiTheme.clientColor(), 0.2f * alphaV),
                1.75f * sScale);
    }

    private void renderMode(PoseStack stack, ModeSetting s, float sScale) {
        List<String> modes = s.getMods();
        selectedV = selected
                ? 18.0f * sScale + modes.size() * 10.0f * sScale
                : 12.0f * sScale;
        height = Mathf.fast(height, selectedV, 12.0f * sScale);
        animV = Mathf.fast(animV, selectedV / (18.0f * sScale + modes.size() * 10.0f * sScale), 15.0f);

        float modeWidth = width * 0.55f;
        float modeX = x + width - modeWidth;
        float availableNameWidth = width - modeWidth - 4.0f * sScale;
        float nameY = y + deltaV + 6.0f * sScale - GuiFonts.SF_BOLD.getHeight(s.name(), 7.0f * sScale) / 1.5f;
        drawScrollingName(stack, s.name(), x, nameY, availableNameWidth,
                ColorUtils.multAlpha(GuiTheme.disabledColor(), alphaV), 7.0f * sScale);

        GuiRender.roundedWithOutline(stack, modeX, y + deltaV, modeWidth, 12.0f * sScale, sScale,
                ColorUtils.multAlpha(ColorUtils.overCol(ColorUtils.getColor(40, 40, 40, 60),
                        ColorUtils.multAlpha(GuiTheme.clientColor(), 0.1f), animV), alphaV),
                ColorUtils.multAlpha(ColorUtils.overCol(ColorUtils.getColor(100, 100, 100, 60),
                        ColorUtils.multAlpha(GuiTheme.clientColor(), 0.2f), animV), alphaV),
                3.0f * sScale);
        ScissorUtils.push();
        ScissorUtils.setFromComponentCoordinates(modeX + 2.0f * sScale, y + deltaV,
                modeWidth - 2.0f * sScale, 12.0f * sScale);
        GuiFonts.SF_BOLD.draw(stack, s.getCurrent(), modeX + 2.0f * sScale, y + deltaV + 2.0f * sScale,
                ColorUtils.getColor(160, 160, 160, (int) (alphaV * 255.0f)), 7.0f * sScale);
        ScissorUtils.unset();
        ScissorUtils.pop();

        if (height >= 12.1f * sScale) {
            float offsetY = y + 16.0f * sScale + deltaV;
            GuiRender.roundedWithOutline(stack, modeX, offsetY - 1.0f * sScale, modeWidth,
                    height - 16.0f * sScale, 1.5f * sScale,
                    ColorUtils.multAlpha(ColorUtils.getColor(40, 40, 40, 60), alphaV),
                    ColorUtils.multAlpha(ColorUtils.getColor(100, 100, 100, 60), alphaV),
                    3.0f * sScale);
            ScissorUtils.push();
            ScissorUtils.setFromComponentCoordinates(modeX, offsetY, modeWidth, height - 14.0f * sScale);
            for (String string : modes) {
                if (s.is(string)) {
                    GuiRender.roundedRect(stack, modeX + 2.5f * sScale, offsetY + 2.5f * sScale,
                            0.5f * sScale, 4.0f * sScale,
                            1.0f * sScale, ColorUtils.multAlpha(GuiTheme.textColor(), animV * alphaV));
                }
                GuiFonts.SF_BOLD.draw(stack, string,
                        modeX + 2.0f * sScale + (s.is(string) ? 3.0f * sScale : 0.0f), offsetY,
                        s.is(string)
                                ? ColorUtils.multAlpha(GuiTheme.textColor(), animV * alphaV)
                                : ColorUtils.getColor(160, 160, 160, (int) (animV * alphaV * 255.0f)),
                        7.0f * sScale);
                offsetY += 10.0f * sScale * (height / (18.0f * sScale + modes.size() * 10.0f * sScale));
            }
            ScissorUtils.unset();
            ScissorUtils.pop();
        }
    }

    private void renderList(PoseStack stack, ListSetting s, float sScale) {
        List<BooleanSetting> values = s.getSettings();
        String valuesText = listValues(s);
        selectedV = selected
                ? 18.0f * sScale + values.size() * 10.0f * sScale
                : 12.0f * sScale;
        height = Mathf.fast(height, selectedV, 12.0f * sScale);
        animV = Mathf.fast(animV, selectedV / (18.0f * sScale + values.size() * 10.0f * sScale), 15.0f);

        float modeWidth = width * 0.55f;
        float modeX = x + width - modeWidth;

        float availableNameWidth = width - modeWidth - 4.0f * sScale;
        float nameY = y + deltaV + 6.0f * sScale - GuiFonts.SF_BOLD.getHeight(s.name(), 7.0f * sScale) / 1.5f;
        drawScrollingName(stack, s.name(), x, nameY, availableNameWidth,
                ColorUtils.multAlpha(GuiTheme.disabledColor(), alphaV), 7.0f * sScale);

        // Бегущая строка переполненного списка выбранных значений — тайминг исходника.
        float maxValuesWidth = modeWidth - 2.0f * sScale;
        if (GuiFonts.SF_BOLD.getWidth(valuesText, 7.0f * sScale) >= maxValuesWidth) {
            if (System.currentTimeMillis() - time >= 25L && offsetX != 0) {
                time = System.currentTimeMillis();
                offsetX = offsetX <= -GuiFonts.SF_BOLD.getWidth(valuesText, 7.0f * sScale)
                        ? (int) maxValuesWidth
                        : offsetX - 1;
            } else if (offsetX == 0 && System.currentTimeMillis() - time >= 1000L) {
                offsetX -= 1;
                time = System.currentTimeMillis();
            }
        } else if (offsetX != 0 && System.currentTimeMillis() - time >= 25L) {
            offsetX = (offsetX > 0) ? offsetX - 1 : offsetX + 1;
            time = System.currentTimeMillis();
        }

        GuiRender.roundedWithOutline(stack, modeX, y + deltaV, modeWidth, 12.0f * sScale, sScale,
                ColorUtils.multAlpha(ColorUtils.overCol(ColorUtils.getColor(40, 40, 40, 60),
                        ColorUtils.multAlpha(GuiTheme.clientColor(), 0.1f), animV), alphaV),
                ColorUtils.multAlpha(ColorUtils.overCol(ColorUtils.getColor(100, 100, 100, 60),
                        ColorUtils.multAlpha(GuiTheme.clientColor(), 0.2f), animV), alphaV),
                3.0f * sScale);
        ScissorUtils.push();
        ScissorUtils.setFromComponentCoordinates(modeX + 2.0f * sScale, y + deltaV,
                modeWidth - 2.0f * sScale, 12.0f * sScale);
        GuiFonts.SF_BOLD.draw(stack, valuesText, modeX + 2.0f * sScale + offsetX, y + deltaV + 2.0f * sScale,
                ColorUtils.getColor(160, 160, 160, (int) (alphaV * 255.0f)), 7.0f * sScale);
        ScissorUtils.unset();
        ScissorUtils.pop();

        if (height >= 12.1f * sScale) {
            float offsetY = y + 16.0f * sScale + deltaV;
            GuiRender.roundedWithOutline(stack, modeX, offsetY - 1.0f * sScale, modeWidth,
                    height - 16.0f * sScale, 1.5f * sScale,
                    ColorUtils.multAlpha(ColorUtils.getColor(40, 40, 40, 60), alphaV),
                    ColorUtils.multAlpha(ColorUtils.getColor(100, 100, 100, 60), alphaV),
                    3.0f * sScale);
            ScissorUtils.push();
            ScissorUtils.setFromComponentCoordinates(modeX, offsetY, modeWidth, height - 14.0f * sScale);
            int id = 0;
            for (BooleanSetting booleanSetting : values) {
                interpolate.set(id, Mathf.fast(interpolate.get(id), booleanSetting.get() ? 1.0f : 0.0f, 16.0f));
                if (interpolate.get(id) >= 0.1f) {
                    GuiRender.roundedRect(stack, modeX + 2.5f * sScale, offsetY + 2.5f * sScale,
                            0.5f * sScale, 4.0f * sScale,
                            1.0f * sScale,
                            ColorUtils.multAlpha(GuiTheme.textColor(), animV * interpolate.get(id) * alphaV));
                }
                GuiFonts.SF_BOLD.draw(stack, booleanSetting.name(),
                        modeX + 2.0f * sScale + interpolate.get(id) * 3.0f * sScale, offsetY,
                        ColorUtils.overCol(
                                ColorUtils.getColor(160, 160, 160, (int) (animV * alphaV * 255.0f)),
                                ColorUtils.multAlpha(GuiTheme.textColor(), animV),
                                interpolate.get(id) * alphaV),
                        7.0f * sScale);
                offsetY += 10.0f * sScale
                        * (height / (18.0f * sScale + values.size() * 10.0f * sScale));
                id++;
            }
            ScissorUtils.unset();
            ScissorUtils.pop();
        }
    }

    private void renderBind(PoseStack stack, BindSetting s, float sScale) {
        height = 12.0f * sScale;

        String text;
        if (selected) {
            text = "...";
        } else {
            int currentBind = s.getKey();
            if (currentBind >= 1000) {
                text = "M" + (currentBind - 1000 + 1);
            } else {
                text = currentBind == -1 ? "нема" : GuiKeys.keyName(currentBind);
            }
        }

        float bindWidth = GuiFonts.SF_BOLD.getWidth(text, 7.0f * sScale) + 4.0f * sScale;
        float availableNameWidth = width - bindWidth - 4.0f * sScale;
        float nameY = y + deltaV + height / 2.0f - GuiFonts.SF_BOLD.getHeight(s.name(), 7.0f * sScale) / 1.5f;
        drawScrollingName(stack, s.name(), x, nameY, availableNameWidth,
                ColorUtils.multAlpha(GuiTheme.disabledColor(), alphaV), 7.0f * sScale);

        GuiRender.roundedWithOutline(stack, x + width - bindWidth, y + deltaV + 1.0f * sScale,
                bindWidth, height - 2.0f * sScale, sScale,
                ColorUtils.multAlpha(ColorUtils.getColor(40, 40, 40, 60), alphaV),
                ColorUtils.multAlpha(ColorUtils.getColor(100, 100, 100, 60), alphaV),
                3.0f * sScale);
        GuiFonts.SF_BOLD.draw(stack, text, x + width - bindWidth + 2.0f * sScale,
                y + deltaV + 2.0f * sScale,
                ColorUtils.multAlpha(GuiTheme.disabledColor(), alphaV), 7.0f * sScale);
    }

    private void renderText(PoseStack stack, TextSetting s, float sScale) {
        height = 12.0f * sScale;
        float stringWidth = width * 0.55f;
        animV = Mathf.fast(animV, selected ? 1.0f : 0.0f, 16.0f * sScale);

        float availableNameWidth = width - stringWidth - 4.0f * sScale;
        float nameY = y + deltaV + height / 2.0f - GuiFonts.SF_BOLD.getHeight(s.name(), 7.0f * sScale) / 1.5f;
        drawScrollingName(stack, s.name(), x, nameY, availableNameWidth,
                ColorUtils.multAlpha(GuiTheme.disabledColor(), alphaV), 7.0f * sScale);

        GuiRender.roundedWithOutline(stack, x + width - stringWidth, y + deltaV + 1.0f * sScale,
                stringWidth, height - 2.0f * sScale, sScale,
                ColorUtils.multAlpha(ColorUtils.overCol(ColorUtils.getColor(40, 40, 40, 60),
                        ColorUtils.multAlpha(GuiTheme.clientColor(), 0.1f), animV), alphaV),
                ColorUtils.multAlpha(ColorUtils.overCol(ColorUtils.getColor(100, 100, 100, 60),
                        ColorUtils.multAlpha(GuiTheme.clientColor(), 0.2f), animV), alphaV),
                3.0f * sScale);
        String shown = (selected || !s.getText().isEmpty()) ? s.getText() : "";
        GuiFonts.SF_BOLD.draw(stack, shown, x + width - stringWidth + 2.0f * sScale,
                y + deltaV + 2.0f * sScale,
                ColorUtils.multAlpha(GuiTheme.disabledColor(), alphaV), 7.0f * sScale);
    }

    // Геометрия окна просмотра сетки тем (общая для рендера, кликов и скролла).
    private float gridViewportHeight(float sScale) {
        float boxSize = GuiStyle.themeBoxSize() * sScale;
        float spacing = GuiStyle.themeGridSpacing() * sScale;
        float viewport = GuiStyle.themeRows() * (boxSize + spacing);
        if (GuiStyle.themeNames()) {
            viewport += 10.0f * sScale;
        }
        return viewport;
    }

    private void renderThemeGrid(PoseStack stack, ThemeGridSetting s, float sScale, float mouseX, float mouseY) {
        int presetCount = ThemeManager.getThemes().size();
        int cols = GuiStyle.themeCols();
        int visibleRows = GuiStyle.themeRows();
        int totalRows = (int) Math.ceil((float) presetCount / cols);

        float boxSize = GuiStyle.themeBoxSize() * sScale;
        float spacing = GuiStyle.themeGridSpacing() * sScale;
        float viewport = gridViewportHeight(sScale);
        float listHeight = visibleRows * (boxSize + spacing);

        float maxScroll = Math.max(0.0f, totalRows * (boxSize + spacing) - listHeight);
        gridScroll = Math.max(0.0f, Math.min(maxScroll, gridScroll));
        gridScrollV = Mathf.fast(gridScrollV, gridScroll, GuiStyle.anim(GuiStyle.themeScrollSpeed()));

        selectedV = selected ? (18.0f * sScale + viewport + 2.0f * sScale) : 12.0f * sScale;
        height = Mathf.fast(height, selectedV, 12.0f * sScale);
        animV = Mathf.fast(animV, selected ? 1.0f : 0.0f, 15.0f);

        float buttonWidth = 50.0f * sScale;
        float availableNameWidth = width - buttonWidth - 8.0f * sScale;
        float nameY = y + deltaV + 6.0f * sScale - GuiFonts.SF_BOLD.getHeight(s.name(), 7.0f * sScale) / 1.5f;
        drawScrollingName(stack, s.name(), x, nameY, availableNameWidth,
                ColorUtils.multAlpha(GuiTheme.disabledColor(), alphaV), 7.0f * sScale);

        float buttonX = x + width - buttonWidth - 2.0f * sScale;
        float buttonY = y + deltaV + 1.0f * sScale;
        float buttonHeight = 10.0f * sScale;
        boolean buttonHovered = isHovered(mouseX, mouseY, buttonX, buttonY, buttonWidth, buttonHeight);

        int hoverColor = GuiTheme.clientColor();
        GuiRender.roundedWithOutline(stack, buttonX, buttonY, buttonWidth, buttonHeight, sScale,
                ColorUtils.multAlpha(buttonHovered ? multBright(hoverColor, 0.8f)
                        : ColorUtils.getColor(40, 40, 40, 60), alphaV),
                ColorUtils.multAlpha(buttonHovered ? hoverColor
                        : ColorUtils.getColor(100, 100, 100, 60), alphaV),
                3.0f * sScale);

        String buttonText = selected ? "Закрыть" : "Открыть";
        GuiFonts.SF_BOLD.draw(stack, buttonText,
                buttonX + buttonWidth / 2.0f - GuiFonts.SF_BOLD.getWidth(buttonText, 6.0f * sScale) / 2.0f,
                buttonY + buttonHeight / 2.0f - GuiFonts.SF_BOLD.getHeight(buttonText, 6.0f * sScale) / 2.0f,
                ColorUtils.multAlpha(GuiTheme.textColor(), alphaV), 6.0f * sScale);

        if (height >= 12.1f * sScale) {
            float panelY = y + deltaV + 16.0f * sScale;
            float panelHeight = height - 16.0f * sScale;

            GuiRender.roundedWithOutline(stack, x, panelY - 1.0f * sScale, width, panelHeight, 1.5f * sScale,
                    ColorUtils.multAlpha(ColorUtils.getColor(40, 40, 40, 60), alphaV),
                    ColorUtils.multAlpha(ColorUtils.getColor(100, 100, 100, 60), alphaV),
                    3.0f * sScale);

            ScissorUtils.push();
            ScissorUtils.setFromComponentCoordinates(x, panelY, width, panelHeight - 2.0f * sScale);

            float startX = x + 4.0f * sScale;
            float currentX = startX;
            float currentY = panelY + 2.0f * sScale - gridScrollV;

            int currentCol = 0;
            godweer.utils.color.Theme currentTheme = ThemeManager.getCurrent();
            String hoveredName = null;

            for (int i = 0; i < ThemeManager.getThemes().size(); i++) {
                godweer.utils.color.Theme preset = ThemeManager.getThemes().get(i);
                boolean isSelected = preset == currentTheme;
                boolean isHovered = isHovered(mouseX, mouseY, currentX, currentY, boxSize, boxSize);
                if (isHovered) {
                    hoveredName = preset.getName();
                }
                float alpha = animV * alphaV * GuiStyle.themeGridAlpha();

                GuiRender.roundedRect(stack, currentX + 1.0f * sScale, currentY + 1.0f * sScale,
                        boxSize, boxSize, 3.0f * sScale,
                        ColorUtils.multAlpha(ColorUtils.getColor(0, 0, 0, 20), alpha));

                int baseColor = ColorUtils.multAlpha(preset.getStaticColor(), alpha);
                int darkColor = ColorUtils.multDark(baseColor, 0.7f);
                GuiRender.drawGradientRound(stack, currentX, currentY, boxSize, boxSize, 3.0f * sScale,
                        baseColor, darkColor, darkColor, baseColor);

                if (isSelected) {
                    int outlineStart = ColorUtils.multAlpha(GuiTheme.clientColor(), alpha);
                    int outlineEnd = ColorUtils.multDark(outlineStart, 0.5f);
                    GuiRender.drawGradientRound(stack, currentX - 1.5f * sScale, currentY - 1.5f * sScale,
                            boxSize + 3.0f * sScale, boxSize + 3.0f * sScale, 4.0f * sScale,
                            outlineStart, outlineEnd, outlineEnd, outlineStart);
                }

                if (isHovered && !isSelected) {
                    int hoverOutlineColor = ColorUtils.multAlpha(ColorUtils.getColor(255, 255, 255, 80), alpha);
                    GuiRender.roundedOutline(stack, currentX, currentY, boxSize, boxSize,
                            1.5f * sScale, hoverOutlineColor, 3.0f * sScale);
                }

                currentCol++;
                if (currentCol >= cols) {
                    currentCol = 0;
                    currentX = startX;
                    currentY += boxSize + spacing;
                } else {
                    currentX += boxSize + spacing;
                }
            }

            // Название темы под курсором — в отдельной строке снизу.
            if (GuiStyle.themeNames()) {
                float nameLineY = panelY + 2.0f * sScale + listHeight;
                GuiFonts.SF_BOLD.draw(stack, hoveredName != null ? hoveredName : "—",
                        x + width / 2.0f - GuiFonts.SF_BOLD.getWidth(hoveredName != null ? hoveredName : "—",
                                6.0f * sScale) / 2.0f,
                        nameLineY + 1.0f * sScale,
                        ColorUtils.multAlpha(hoveredName != null ? GuiTheme.clientColor()
                                : ColorUtils.getColor(255, 255, 255, 255), alphaV * 0.7f), 6.0f * sScale);
            }

            // Полоса прокрутки сетки.
            if (GuiStyle.themeScrollbar() && maxScroll > 0.0f) {
                float trackX = x + width - 4.0f * sScale;
                float trackY = panelY + 2.0f * sScale;
                float trackH = listHeight - 2.0f * sScale;
                float thumbH = Math.max(6.0f * sScale, trackH * (listHeight / (maxScroll + listHeight)));
                float thumbY = trackY + (trackH - thumbH) * (gridScrollV / maxScroll);
                GuiRender.roundedRect(stack, trackX, trackY, 2.0f * sScale, trackH, sScale,
                        ColorUtils.multAlpha(ColorUtils.getColor(255, 255, 255, 25), alphaV));
                GuiRender.roundedRect(stack, trackX, thumbY, 2.0f * sScale, thumbH, sScale,
                        ColorUtils.multAlpha(GuiTheme.clientColor(), alphaV * 0.8f));
            }

            ScissorUtils.unset();
            ScissorUtils.pop();
        }
    }

    private static int multBright(int color, float percent01) {
        return ColorUtils.getColor(
                Math.min(255, Math.round(ColorUtils.red(color) / percent01)),
                Math.min(255, Math.round(ColorUtils.green(color) / percent01)),
                Math.min(255, Math.round(ColorUtils.blue(color) / percent01)),
                ColorUtils.alpha(color));
    }

    @Override
    protected void mouse(float mouseX, float mouseY, int button, int action) {
        float sScale = getResolutionScale();

        float effectiveX = x;
        float effectiveY = y + deltaV;
        float effectiveWidth = width;
        float effectiveHeight = height;

        if (isInputMode && action == 0 && setting instanceof FloatSetting s) {
            applyInputValue(s);
        }

        if (setting instanceof BindSetting s && selected && action == 0) {
            if (System.currentTimeMillis() - lastBindClickTime < BIND_CLICK_COOLDOWN_MS) {
                return;
            }
            // Конвенция GodWeer: мышь кодируется как 1000 + номер кнопки (KeyBoardUtils).
            s.setKey(1000 + button);
            selected = false;
            playSound("bind_success.wav", 0.3f);
            lastBindClickTime = System.currentTimeMillis();
            return;
        }

        if (action == GLFW.GLFW_PRESS && button == 0) {
            if ((setting instanceof ModeSetting || setting instanceof ListSetting
                    || setting instanceof BindSetting || setting instanceof TextSetting
                    || setting instanceof ThemeGridSetting)
                    && isHovered(mouseX, mouseY, effectiveX, effectiveY, effectiveWidth, 12.0f * sScale)) {
                if (setting instanceof BindSetting) {
                    if (System.currentTimeMillis() - lastBindClickTime < BIND_CLICK_COOLDOWN_MS) {
                        return;
                    }
                    playSound("bind_wait.wav", 0.1f);
                    lastBindClickTime = System.currentTimeMillis();
                } else if (setting instanceof ModeSetting || setting instanceof ListSetting) {
                    playSound(selected ? "mode_close.wav" : "mode_open.wav", 0.1f);
                }

                if (setting instanceof ThemeGridSetting) {
                    float buttonWidth = 50.0f * sScale;
                    float buttonX = effectiveX + effectiveWidth - buttonWidth - 2.0f * sScale;
                    float buttonY = effectiveY + 1.0f * sScale;
                    float buttonHeight = 10.0f * sScale;

                    if (isHovered(mouseX, mouseY, buttonX, buttonY, buttonWidth, buttonHeight)) {
                        selected = !selected;
                    }
                } else {
                    selected = !selected;
                }
                return;
            }
        }

        if (button == 0) {
            float modeWidth = effectiveWidth * 0.55f;
            float modeX = effectiveX + effectiveWidth - modeWidth;

            if (action == 0 && setting instanceof BooleanSetting s
                    && isHovered(mouseX, mouseY, effectiveX, effectiveY, effectiveWidth, effectiveHeight)) {
                s.setState(!s.get());
                playSound(s.get() ? "cb_enabled.wav" : "cb_disabled.wav", 0.1f);
            } else if (setting instanceof FloatSetting s) {
                float sliderWidth = effectiveWidth - 4.0f * sScale;
                float sliderX = effectiveX + 0.5f * sScale;
                float sliderY = effectiveY + effectiveHeight - 5.0f * sScale;

                String valueText = sliderText(s.get());
                float valueTextWidth = GuiFonts.SF_BOLD.getWidth(valueText, 7.0f * sScale);
                float valueBoxWidth = valueTextWidth + 4.0f * sScale;
                float valueBoxX = effectiveX + effectiveWidth - valueBoxWidth - 2.0f * sScale;
                float valueBoxY = effectiveY + 2.0f * sScale - 2.0f * sScale;
                float valueBoxHeight = GuiFonts.SF_BOLD.getHeight(valueText, 7.0f * sScale) + 4.0f * sScale;

                if (action == 0 && isHovered(mouseX, mouseY, valueBoxX, valueBoxY, valueBoxWidth, valueBoxHeight)) {
                    isInputMode = true;
                    inputValueString = sliderText(s.get());
                    selected = false;
                    return;
                }

                if (action == GLFW.GLFW_PRESS && isHovered(mouseX, mouseY,
                        sliderX - 2.0f * sScale, sliderY - 3.0f * sScale,
                        sliderWidth + 4.0f * sScale, 10.0f * sScale)) {
                    // В исходнике здесь "slider.wav" — файла в ресурсах нет, SoundUtil молчит.
                    selected = true;
                }
            } else if (action == 0 && setting instanceof ModeSetting s && selected
                    && isHovered(mouseX, mouseY, modeX, effectiveY + 16.0f * sScale, modeWidth,
                    effectiveHeight - 16.0f * sScale)) {
                float offsetY = effectiveY + 16.0f * sScale;
                for (String string : s.getMods()) {
                    if (isHovered(mouseX, mouseY, modeX, offsetY, modeWidth, 10.0f * sScale)) {
                        selected = false;
                        boolean wasSelected = s.is(string);
                        s.set(string);
                        playSound(wasSelected ? "mode_pick.wav" : "mode_pick_on.wav", 0.1f);
                        break;
                    }
                    offsetY += 10.0f * sScale
                            * (effectiveHeight / (16.0f * sScale + s.getMods().size() * 10.0f * sScale));
                }
            } else if (action == 0 && setting instanceof ListSetting s && selected
                    && isHovered(mouseX, mouseY, modeX, effectiveY + 16.0f * sScale, modeWidth,
                    effectiveHeight - 16.0f * sScale)) {
                float offsetY = effectiveY + 16.0f * sScale;
                for (BooleanSetting booleanSetting : s.getSettings()) {
                    if (isHovered(mouseX, mouseY, modeX, offsetY, modeWidth, 10.0f * sScale)) {
                        boolean wasSelected = booleanSetting.get();
                        booleanSetting.setState(!booleanSetting.get());
                        playSound(wasSelected ? "mode_pick.wav" : "mode_pick_on.wav", 0.1f);
                        break;
                    }
                    offsetY += 10.0f * sScale
                            * (effectiveHeight / (18.0f * sScale + s.getSettings().size() * 10.0f * sScale));
                }
            } else if (action == 0 && setting instanceof ThemeGridSetting && selected
                    && isHovered(mouseX, mouseY, effectiveX, effectiveY + 16.0f * sScale, effectiveWidth,
                    effectiveHeight - 16.0f * sScale)) {
                float panelY = effectiveY + 16.0f * sScale;
                float boxSize = GuiStyle.themeBoxSize() * sScale;
                float spacing = GuiStyle.themeGridSpacing() * sScale;
                float startX = effectiveX + 4.0f * sScale;
                float currentX = startX;
                float currentY = panelY + 2.0f * sScale - gridScrollV;

                int currentCol = 0;
                int cols = GuiStyle.themeCols();

                for (godweer.utils.color.Theme preset : ThemeManager.getThemes()) {
                    if (isHovered(mouseX, mouseY, currentX, currentY, boxSize, boxSize)) {
                        ThemeManager.setCurrent(preset);
                        selected = false;
                        playSound("cb_enabled.wav", 0.1f);
                        break;
                    }

                    currentCol++;
                    if (currentCol >= cols) {
                        currentCol = 0;
                        currentX = startX;
                        currentY += boxSize + spacing;
                    } else {
                        currentX += boxSize + spacing;
                    }
                }
            }
        }

        if (action == GLFW.GLFW_RELEASE) {
            if (selected && setting instanceof FloatSetting) {
                selected = false;
            }
        }
    }

    @Override
    protected void key(int key) {
        if (isInputMode && setting instanceof FloatSetting s) {
            if (key == GLFW.GLFW_KEY_ESCAPE || key == GLFW.GLFW_KEY_ENTER) {
                applyInputValue(s);
            } else if (key == GLFW.GLFW_KEY_BACKSPACE) {
                if (!inputValueString.isEmpty()) {
                    inputValueString = inputValueString.substring(0, inputValueString.length() - 1);
                }
            }
            return;
        }

        if (setting instanceof BindSetting s && selected) {
            if (System.currentTimeMillis() - lastBindClickTime < BIND_CLICK_COOLDOWN_MS) {
                return;
            }
            selected = false;
            if (key == GLFW.GLFW_KEY_DELETE || key == GLFW.GLFW_KEY_ESCAPE) {
                s.setKey(-1);
                playSound("bind_remove.wav", 0.1f);
            } else {
                s.setKey(key);
                playSound("bind_success.wav", 0.1f);
            }
            lastBindClickTime = System.currentTimeMillis();
        } else if (setting instanceof TextSetting s && selected) {
            if (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_ESCAPE) {
                selected = false;
            } else if (key == GLFW.GLFW_KEY_BACKSPACE) {
                s.setText(s.getText().isEmpty() ? "" : s.getText().substring(0, s.getText().length() - 1));
            }
        }
    }

    @Override
    public void charTyped(char typed) {
        if (isInputMode && setting instanceof FloatSetting) {
            if (Character.isDigit(typed) || (typed == '.' && !inputValueString.contains("."))
                    || (typed == '-' && inputValueString.isEmpty())) {
                inputValueString += typed;
            }
            return;
        }

        if (selected && setting instanceof TextSetting s) {
            s.setText(s.getText() + typed);
        }
    }

    // Локальные вызовы звука: громкости перенесены из исходника 1:1.
    private static void playSound(String name, float volume) {
        GuiStyle.playSound(name, volume);
    }
}

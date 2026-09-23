package godweer.ui.newgui;

import com.mojang.blaze3d.vertex.PoseStack;

import godweer.modules.api.setting.BindSetting;
import godweer.modules.api.setting.BooleanSetting;
import godweer.modules.api.setting.ColorSetting;
import godweer.modules.api.setting.FloatSetting;
import godweer.modules.api.setting.ModeSetting;
import godweer.modules.api.setting.TextSetting;
import godweer.utils.color.ColorUtils;
import godweer.utils.input.KeyBoardUtils;
import godweer.utils.math.Mathf;
import godweer.utils.render.draw.RenderDraw;
import godweer.utils.render.draw.Round;
import godweer.utils.render.draw.ScissorExp;
import godweer.utils.render.font.GuiFonts;
import godweer.utils.render.font.GuiTheme;

import net.minecraft.util.Mth;

import org.lwjgl.glfw.GLFW;

public class Setting extends Builder {

    private final Module module;
    private final godweer.modules.api.setting.Setting setting;

    private boolean selected = false;
    private float selectedV, animV, offsetY, alphaV;
    private float delta, deltaV;
    private ColorPicker colorPicker;

    private float nameScrollX;
    private long lastScrollTime;
    private boolean scrollingForward = true;
    private long scrollPauseUntil;

    private boolean isInputMode = false;
    private String inputValueString = "";

    private float hoverAnim = 0.0f;
    private int typingProgress = 0;
    private long lastTypeTime = 0;
    private boolean wasHovered = false;

    private static long lastBindClickTime = 0;
    private static final int BIND_CLICK_COOLDOWN_MS = 200;

    public Setting(Module module, godweer.modules.api.setting.Setting setting, float x, float y) {
        this.module = module;
        this.setting = setting;
        this.x = x;
        this.y = y;
        float sScale = module.getPanel().getGui().getResolutionScale();
        this.width = 270 * sScale;
        height = 12 * sScale;
        offsetY = setting.visible() ? 1 : 0;
    }

    private float getResolutionScale() {
        return module.getPanel().getGui().getResolutionScale();
    }

    private void drawScrollingName(PoseStack stack, String name, float x, float y, float availableWidth, int color, float size) {
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

        ScissorExp.push();
        ScissorExp.setFromComponentCoordinates(x, y - 2 * sScale, availableWidth, size + 4 * sScale);
        GuiFonts.SF_BOLD.draw(stack, name, x + nameScrollX, y, color, size);
        ScissorExp.unset();
        ScissorExp.pop();
    }

    private void applyInputValue(FloatSetting s) {
        try {
            float newValue = Float.parseFloat(inputValueString);
            newValue = Mth.clamp(newValue, s.getMin(), s.getMax());
            s.setValue(newValue);
        } catch (NumberFormatException ignored) {
        }
        inputValueString = "";
        isInputMode = false;
    }

    @Override
    public void render(PoseStack stack, float mouseX, float mouseY) {
        float sScale = getResolutionScale();
        GuiTheme theme = GuiTheme.getInstance();

        boolean isCurrentlyHovered = isHovered(mouseX, mouseY, x, y + deltaV, width, 12 * sScale);
        hoverAnim = Mathf.fast(hoverAnim, isCurrentlyHovered ? 1.0f : 0.0f, 10);

        if (isCurrentlyHovered && !wasHovered) {
            typingProgress = 0;
            lastTypeTime = System.currentTimeMillis();
        }
        wasHovered = isCurrentlyHovered;

        if (setting instanceof BooleanSetting s) {
            height = 12 * sScale;
            selectedV = Mathf.fast(selectedV, s.isState() ? 1 : 0, 16);

            float controlWidth = 16 * sScale + 4 * sScale;
            float availableNameWidth = width - controlWidth;
            float nameY = y + deltaV + height / 2f - GuiFonts.SF_BOLD.getHeight(s.name(), 7 * sScale) / 1.5f;
            drawScrollingName(stack, s.name(), x, nameY, availableNameWidth, ColorUtils.multAlpha(theme.disabledColor(), alphaV), 7 * sScale);

            RenderDraw.Rounded.roundedWithOutline(stack, x + width - 16 * sScale, y + deltaV + 2 * sScale, 16 * sScale, height - 4 * sScale, sScale, ColorUtils.multAlpha(ColorUtils.overCol(ColorUtils.getColor(40, 40, 40, 60), ColorUtils.multAlpha(theme.clientColor(), .1f), selectedV), alphaV), ColorUtils.multAlpha(ColorUtils.overCol(ColorUtils.getColor(100, 100, 100, 60), ColorUtils.multAlpha(theme.clientColor(), .2f), selectedV), alphaV), Round.of(4 * sScale));
            RenderDraw.Rounded.roundedRect(stack, x + width - 16 * sScale + (selectedV * 8 * sScale + 1 * sScale), y + deltaV + 3 * sScale, 6 * sScale, 6 * sScale, ColorUtils.overCol(ColorUtils.getColor(100, 100, 100, (int) (alphaV * 255)), ColorUtils.getColor(220, 220, 220, (int) (alphaV * 255)), selectedV), Round.of(4 * sScale));

        } else if (setting instanceof FloatSetting s) {
            height = 12 * sScale;
            float sliderWidth = 138 * sScale;
            float sliderX = x + width - sliderWidth;

            String valueText = String.format("%.2f", s.getValue());
            float valueTextWidth = GuiFonts.SF_BOLD.getWidth(isInputMode ? inputValueString + "_" : valueText, 7 * sScale);

            float availableNameWidth = sliderX - (valueTextWidth + 7 * sScale) - x - 4 * sScale;
            float nameY = y + deltaV + height / 2f - GuiFonts.SF_BOLD.getHeight(s.name(), 7 * sScale) / 1.5f;

            drawScrollingName(stack, s.name(), x, nameY, availableNameWidth, ColorUtils.multAlpha(theme.disabledColor(), alphaV), 7 * sScale);

            if (selected) {
                float sliderMinX = sliderX;
                float sliderMaxX = sliderX + sliderWidth;
                float sliderMinY = y + deltaV;
                float sliderMaxY = y + deltaV + height;

                if (mouseX >= sliderMinX && mouseX <= sliderMaxX && mouseY >= sliderMinY && mouseY <= sliderMaxY) {
                    float denominator = sliderWidth;
                    if (denominator <= 0) denominator = 1 * sScale;
                    float step = s.getIncrement() <= 0 ? 0.1f : s.getIncrement();
                    s.setValue(Mth.clamp(
                            Mathf.step((mouseX - sliderX) / denominator * (s.getMax() - s.getMin()) + s.getMin(), step),
                            s.getMin(), s.getMax()));
                } else {
                    selected = false;
                }
            }

            float progress = (s.getMax() == s.getMin()) ? 0 : (s.getValue() - s.getMin()) / (s.getMax() - s.getMin());
            float targetKnobX = sliderX + ((sliderWidth - 8 * sScale) * progress) + 2 * sScale;
            animV = Mathf.fast(animV, targetKnobX, 16);

            RenderDraw.Rounded.roundedRect(stack, sliderX, y + deltaV + 5.5f * sScale, sliderWidth, 5 * sScale, ColorUtils.multAlpha(theme.clientColor(), alphaV / 10f), Round.of(5 * sScale));
            RenderDraw.Rounded.roundedWithOutline(
                    stack, animV, y + deltaV + 6 * sScale, 4 * sScale, 4 * sScale, 1.5f * sScale,
                    ColorUtils.multAlpha(theme.clientColor(), .4f * alphaV),
                    ColorUtils.multAlpha(theme.clientColor(), .2f * alphaV), Round.of(2 * sScale));

            float valueBoxWidth = valueTextWidth + 4 * sScale;
            float valueBoxX = sliderX - valueBoxWidth - 3 * sScale;
            float valueBoxY = y + deltaV + height / 2f - (GuiFonts.SF_BOLD.getHeight(valueText, 7 * sScale) + 4 * sScale) / 2f;

            RenderDraw.Rounded.roundedRect(stack, valueBoxX, valueBoxY, valueBoxWidth, GuiFonts.SF_BOLD.getHeight(valueText, 7 * sScale) + 4 * sScale, ColorUtils.multAlpha(theme.clientColor(), .1f * alphaV), Round.of(3 * sScale));

            if (isInputMode) {
                String renderString = inputValueString;
                if (System.currentTimeMillis() % 1000 < 500) {
                    renderString += "_";
                }
                GuiFonts.SF_BOLD.draw(stack, renderString, valueBoxX + 2 * sScale, valueBoxY + 2 * sScale, ColorUtils.getColor(255, 255, 255, (int) (alphaV * 255)), 7 * sScale);
            } else {
                GuiFonts.SF_BOLD.draw(stack, valueText, valueBoxX + 2 * sScale, valueBoxY + 2 * sScale, ColorUtils.getColor(255, 255, 255, (int) (alphaV * 255)), 7 * sScale);
            }

        } else if (setting instanceof ModeSetting s) {
            selectedV = (selected) ? 18 * sScale + s.getMods().size() * 10 * sScale : 12 * sScale;
            height = Mathf.fast(height, selectedV, 12 * sScale);
            animV = Mathf.fast(animV, selectedV / (18 * sScale + s.getMods().size() * 10 * sScale), 15);

            float modeWidth = width * 0.55f;
            float modeX = x + width - modeWidth;
            float availableNameWidth = width - modeWidth - 4 * sScale;
            float nameY = y + deltaV + 6 * sScale - GuiFonts.SF_BOLD.getHeight(s.name(), 7 * sScale) / 1.5f;
            drawScrollingName(stack, s.name(), x, nameY, availableNameWidth, ColorUtils.multAlpha(theme.disabledColor(), alphaV), 7 * sScale);

            RenderDraw.Rounded.roundedWithOutline(stack, modeX, y + deltaV, modeWidth, 12 * sScale, sScale, ColorUtils.multAlpha(ColorUtils.overCol(ColorUtils.getColor(40, 40, 40, 60), ColorUtils.multAlpha(theme.clientColor(), .1f), animV), alphaV), ColorUtils.multAlpha(ColorUtils.overCol(ColorUtils.getColor(100, 100, 100, 60), ColorUtils.multAlpha(theme.clientColor(), .2f), animV), alphaV), Round.of(3 * sScale));
            GuiFonts.SF_BOLD.draw(stack, s.getCurrent(), modeX + 2 * sScale, y + deltaV + 2 * sScale, ColorUtils.getColor(160, 160, 160, (int) (alphaV * 255)), 7 * sScale);

            if (height >= 12.1f * sScale) {
                float offY = y + 16 * sScale + deltaV;
                RenderDraw.Rounded.roundedWithOutline(stack, modeX, offY - 1 * sScale, modeWidth, height - 16 * sScale, 1.5f * sScale, ColorUtils.multAlpha(ColorUtils.getColor(40, 40, 40, 60), alphaV), ColorUtils.multAlpha(ColorUtils.getColor(100, 100, 100, 60), alphaV), Round.of(3 * sScale));
                ScissorExp.push();
                ScissorExp.setFromComponentCoordinates(modeX, offY, modeWidth, height - 14 * sScale);
                for (String string : s.getMods()) {
                    if (s.is(string))
                        RenderDraw.Rounded.roundedRect(stack, modeX + 2.5f * sScale, offY + 2.5f * sScale, .5f * sScale, 4 * sScale, ColorUtils.multAlpha(theme.textColor(), animV * alphaV), Round.of(1 * sScale));
                    GuiFonts.SF_BOLD.draw(stack, string, modeX + 2 * sScale + (s.is(string) ? 3 * sScale : 0), offY, s.is(string) ? ColorUtils.multAlpha(theme.textColor(), animV * alphaV) : ColorUtils.getColor(160, 160, 160, (int) (animV * alphaV * 255)), 7 * sScale);
                    offY += 10 * sScale * (height / (18 * sScale + s.getMods().size() * 10 * sScale));
                }
                ScissorExp.unset();
                ScissorExp.pop();
            }

        } else if (setting instanceof ColorSetting s) {
            height = 12 * sScale;
            float[] c = ColorUtils.getColorA(s.getValue());
            float controlSize = 12 * sScale;
            float controlX = x + width - controlSize - 3 * sScale;
            float controlY = y + deltaV + (12 * sScale - controlSize) / 2f;

            float availableNameWidth = width - controlSize - 10 * sScale;
            float nameY = y + deltaV + height / 2f - GuiFonts.SF_BOLD.getHeight(s.name(), 7 * sScale) / 1.5f;
            drawScrollingName(stack, s.name(), x, nameY, availableNameWidth, ColorUtils.multAlpha(theme.disabledColor(), alphaV), 7 * sScale);

            RenderDraw.Shadow.drawShadow(stack, controlX, controlY, controlSize, controlSize, 3 * sScale, 0.6f, ColorUtils.getColor(0, 0, 0, 120));
            RenderDraw.Rounded.roundedRect(stack, controlX, controlY, controlSize, controlSize, ColorUtils.multAlpha(s.getValue(), (int) (c[3] * alphaV * 255)), Round.of(controlSize / 2f));
            RenderDraw.Rounded.roundedOutline(stack, controlX, controlY, controlSize, controlSize, 1.5f * sScale, ColorUtils.multAlpha(ColorUtils.multDark(s.getValue(), 0.8f), (int) ((c[3] + .2f) * alphaV * 255)), Round.of(controlSize / 2f));

        } else if (setting instanceof BindSetting s) {
            height = 12 * sScale;

            String text;
            if (selected) {
                text = "...";
            } else {
                int currentBind = s.getKey();
                if (currentBind >= KeyBoardUtils.MOUSE_BUTTON_OFFSET) {
                    text = "M" + (currentBind - KeyBoardUtils.MOUSE_BUTTON_OFFSET + 1);
                } else {
                    text = (currentBind == -1 ? "n/a" : KeyBoardUtils.getBindName(currentBind));
                }
            }

            float bindWidth = GuiFonts.SF_BOLD.getWidth(text, 7 * sScale) + 4 * sScale;
            float availableNameWidth = width - bindWidth - 4 * sScale;
            float nameY = y + deltaV + height / 2f - GuiFonts.SF_BOLD.getHeight(s.name(), 7 * sScale) / 1.5f;
            drawScrollingName(stack, s.name(), x, nameY, availableNameWidth, ColorUtils.multAlpha(theme.disabledColor(), alphaV), 7 * sScale);

            RenderDraw.Rounded.roundedWithOutline(stack, x + width - bindWidth, y + deltaV + 1 * sScale, bindWidth, height - 2 * sScale, sScale, ColorUtils.multAlpha(ColorUtils.getColor(40, 40, 40, 60), alphaV), ColorUtils.multAlpha(ColorUtils.getColor(100, 100, 100, 60), alphaV), Round.of(3 * sScale));
            GuiFonts.SF_BOLD.draw(stack, text, x + width - bindWidth + 2 * sScale, y + deltaV + 2 * sScale, ColorUtils.multAlpha(theme.disabledColor(), alphaV), 7 * sScale);

        } else if (setting instanceof TextSetting s) {
            height = 12 * sScale;
            float stringWidth = width * 0.55f;
            animV = Mathf.fast(animV, selected ? 1 : 0, 16 * sScale);

            float availableNameWidth = width - stringWidth - 4 * sScale;
            float nameY = y + deltaV + height / 2f - GuiFonts.SF_BOLD.getHeight(s.name(), 7 * sScale) / 1.5f;
            drawScrollingName(stack, s.name(), x, nameY, availableNameWidth, ColorUtils.multAlpha(theme.disabledColor(), alphaV), 7 * sScale);

            RenderDraw.Rounded.roundedWithOutline(stack, x + width - stringWidth, y + deltaV + 1 * sScale, stringWidth, height - 2 * sScale, sScale, ColorUtils.multAlpha(ColorUtils.overCol(ColorUtils.getColor(40, 40, 40, 60), ColorUtils.multAlpha(theme.clientColor(), .1f), animV), alphaV), ColorUtils.multAlpha(ColorUtils.overCol(ColorUtils.getColor(100, 100, 100, 60), ColorUtils.multAlpha(theme.clientColor(), .2f), animV), alphaV), Round.of(3 * sScale));
            GuiFonts.SF_BOLD.draw(stack, s.displayName(), x + width - stringWidth + 2 * sScale, y + deltaV + 2 * sScale, ColorUtils.multAlpha(theme.disabledColor(), alphaV), 7 * sScale);
        }
    }

    @Override
    protected void mouse(float mouseX, float mouseY, int button, int action) {
        float sScale = getResolutionScale();

        if (isInputMode && action == 0) {
            if (setting instanceof FloatSetting s) {
                applyInputValue(s);
            } else {
                isInputMode = false;
            }
        }

        if (setting instanceof BindSetting bindSetting && selected && action == 0) {
            if (System.currentTimeMillis() - lastBindClickTime < BIND_CLICK_COOLDOWN_MS) {
                return;
            }
            bindSetting.setKey(button + KeyBoardUtils.MOUSE_BUTTON_OFFSET);
            selected = false;
            lastBindClickTime = System.currentTimeMillis();
            return;
        }

        if (action == GLFW.GLFW_PRESS && button == 0) {
            if ((setting instanceof ModeSetting || setting instanceof BindSetting || setting instanceof TextSetting || setting instanceof ColorSetting) && isHovered(mouseX, mouseY, x, y + deltaV, width, 12 * sScale)) {
                selected = !selected;
                return;
            }
        }

        if (button == 0) {
            if (action == 0 && setting instanceof BooleanSetting s && isHovered(mouseX, mouseY, x, y + deltaV, width, height)) {
                s.setState(!s.isState());
            } else if (setting instanceof FloatSetting s) {
                float sliderWidth = 138 * sScale;
                float sliderX = x + width - sliderWidth;

                String valueText = String.format("%.2f", s.getValue());
                float valueTextWidth = GuiFonts.SF_BOLD.getWidth(valueText, 7 * sScale);
                float valueBoxWidth = valueTextWidth + 4 * sScale;
                float valueBoxX = sliderX - valueBoxWidth - 3 * sScale;
                float valueBoxY = y + deltaV + height / 2f - (GuiFonts.SF_BOLD.getHeight(valueText, 7 * sScale) + 4 * sScale) / 2f;
                float valueBoxHeight = GuiFonts.SF_BOLD.getHeight(valueText, 7 * sScale) + 4 * sScale;

                if (action == 0 && isHovered(mouseX, mouseY, valueBoxX, valueBoxY, valueBoxWidth, valueBoxHeight)) {
                    isInputMode = true;
                    inputValueString = String.format("%.2f", s.getValue()).replace(',', '.');
                    selected = false;
                    return;
                }

                if (action == GLFW.GLFW_PRESS && isHovered(mouseX, mouseY, sliderX, y + deltaV, sliderWidth, height)) {
                    selected = true;
                }
            } else if (action == 0 && setting instanceof ModeSetting s && selected && isHovered(mouseX, mouseY, x + width - width * 0.55f, y + deltaV + 16 * sScale, width * 0.55f, height - 16 * sScale)) {
                float modeWidth = width * 0.55f;
                float modeX = x + width - modeWidth;
                float offY = y + deltaV + 16 * sScale;
                for (String string : s.getMods()) {
                    if (isHovered(mouseX, mouseY, modeX, offY, modeWidth, 10 * sScale)) {
                        selected = false;
                        s.set(string);
                        break;
                    }
                    offY += 10 * sScale * (height / (18 * sScale + s.getMods().size() * 10 * sScale));
                }
            } else if (setting instanceof ColorSetting s) {
                if (selected && colorPicker != null) {
                    boolean hoveringPicker = isHovered(mouseX, mouseY, colorPicker.getX(), colorPicker.getY(), colorPicker.getWidth(), colorPicker.getHeight());
                    if (hoveringPicker && action == GLFW.GLFW_PRESS) {
                        colorPicker.mouseClicked(mouseX, mouseY, button);
                    }
                }
            }
        }

        if (action == GLFW.GLFW_RELEASE) {
            if (selected && setting instanceof FloatSetting) {
                selected = false;
            }
            if (setting instanceof ColorSetting && colorPicker != null) {
                colorPicker.mouseReleased(mouseX, mouseY, button);
            }
        }
    }

    @Override
    protected void key(int key) {
        float sScale = getResolutionScale();

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
            } else {
                s.setKey(key);
            }
            lastBindClickTime = System.currentTimeMillis();
        } else if (setting instanceof TextSetting s && selected) {
            if (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_ESCAPE)
                selected = false;
        }
    }

    @Override
    public void charTyped(char typed) {
        if (isInputMode && setting instanceof FloatSetting) {
            if (Character.isDigit(typed) || (typed == '.' && !inputValueString.contains(".")) || (typed == '-' && inputValueString.isEmpty())) {
                inputValueString += typed;
            }
            return;
        }
    }

    public godweer.modules.api.setting.Setting getSetting() {
        return setting;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public float getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(float offsetY) {
        this.offsetY = offsetY;
    }

    public float getAlphaV() {
        return alphaV;
    }

    public void setAlphaV(float alphaV) {
        this.alphaV = alphaV;
    }

    public float getDeltaV() {
        return deltaV;
    }

    public void setDeltaV(float deltaV) {
        this.deltaV = deltaV;
    }

    public float getDelta() {
        return delta;
    }

    public void setDelta(float delta) {
        this.delta = delta;
    }

    public ColorPicker getColorPicker() {
        return colorPicker;
    }
}

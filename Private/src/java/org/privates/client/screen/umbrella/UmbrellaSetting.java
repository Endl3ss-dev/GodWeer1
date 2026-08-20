package org.privates.client.screen.umbrella;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.math.MathHelper;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.lwjgl.glfw.GLFW;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.settings.impl.*;
import org.privates.client.screen.newgui.ColorPicker;
import org.privates.client.utils.keyboard.Keyboard;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.other.SoundUtil;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.draw.ScissorExp;
import org.privates.client.utils.render.font.Fonts;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Getter
@Setter
public class UmbrellaSetting {
    private final UmbrellaModuleButton module;
    private final org.privates.client.managers.module.settings.Setting setting;
    private float x, y, width, height;

    private boolean selected = false;
    private float selectedV = 0f;
    private float animV = 0f;
    private float alphaV = 1f;
    private ColorPicker colorPicker;

    private ArrayList<Float> interpolate = new ArrayList<>();
    private int offsetX;

    private boolean isInputMode = false;
    private String inputValueString = "";

    public UmbrellaSetting(UmbrellaModuleButton module, org.privates.client.managers.module.settings.Setting setting) {
        this.module = module;
        this.setting = setting;

        if (setting instanceof SliderSetting) {
            this.height = 20f;
        } else if (setting instanceof ModeSetting || setting instanceof MultiBooleanSetting) {
            this.height = 12f;
            if (setting instanceof MultiBooleanSetting s) {
                for (BooleanSetting booleanSetting : s.getValues()) {
                    interpolate.add((float) (booleanSetting.getValue() ? 1 : 0));
                }
            }
        } else {
            this.height = 12f;
        }
    }

    private void applyInputValue(SliderSetting s) {
        try {
            float newValue = Float.parseFloat(inputValueString);
            newValue = MathHelper.clamp(newValue, s.min, s.max);
            s.set(newValue);
        } catch (NumberFormatException ignored) {
        }
        inputValueString = "";
        isInputMode = false;
    }

    public void render(MatrixStack stack, float mouseX, float mouseY) {
        if (!(boolean) setting.getVisible().get()) {
            height = 0f;
            return;
        }

        Theme theme = Theme.getInstance();
        int accentColor = ColorUtil.rgba(220, 35, 64, 255);

        if (setting instanceof BooleanSetting s) {
            height = 13f;
            selectedV = Mathf.fast(selectedV, s.getValue() ? 1 : 0, 16);

            Fonts.SF_MEDIUM.draw(stack, s.getName(), x, y + 2f, ColorUtil.multAlpha(ColorUtil.getColor(146, 147, 150), alphaV), 10f);

            float toggleWidth = 24f;
            float toggleHeight = 12f;
            float toggleX = x + width - toggleWidth;
            float toggleY = y + 0.5f;

            int toggleBg = ColorUtil.rgba(24, 24, 28, 255);
            RenderUtil.Rounded.roundedRect(stack, toggleX, toggleY, toggleWidth, toggleHeight, ColorUtil.multAlpha(toggleBg, alphaV), Round.of(6f));

            int knobColor = ColorUtil.overCol(ColorUtil.getColor(103, 101, 103), accentColor, selectedV);
            float knobX = toggleX + 1f + (selectedV * 12f);
            RenderUtil.Rounded.roundedRect(stack, knobX, toggleY + 1f, 10f, 10f, ColorUtil.multAlpha(knobColor, alphaV), Round.of(5f));

        } else if (setting instanceof SliderSetting s) {
            height = 20f;

            String valueText = String.format("%.2f", s.getValue());
            float valueTextWidth = Fonts.SF_MEDIUM.getWidth(isInputMode ? inputValueString + "_" : valueText, 10f);
            float boxWidth = valueTextWidth + 4f;
            float boxHeight = 12f;
            float boxX = x + width - boxWidth - 2f;
            float boxY = y - 1f;

            Fonts.SF_MEDIUM.draw(stack, s.getName(), x, y + 2f, ColorUtil.multAlpha(ColorUtil.getColor(146, 147, 150), alphaV), 10f);

            RenderUtil.Rounded.roundedRect(stack, boxX, boxY, boxWidth, boxHeight, ColorUtil.multAlpha(ColorUtil.rgba(146, 147, 150, 20), alphaV), Round.of(5f));

            if (isInputMode) {
                String renderString = inputValueString;
                if (System.currentTimeMillis() % 1000 < 500) {
                    renderString += "_";
                }
                Fonts.SF_MEDIUM.draw(stack, renderString, boxX + 2f, boxY + 2f, ColorUtil.multAlpha(ColorUtil.WHITE, alphaV), 10f);
            } else {
                Fonts.SF_MEDIUM.draw(stack, valueText, boxX + 2f, boxY + 2f, ColorUtil.multAlpha(ColorUtil.WHITE, alphaV), 10f);
            }

            float sliderWidth = width - 4f;
            float sliderX = x + 0.5f;
            float sliderY = y + height - 5f;

            if (selected) {
                float newProgress = MathHelper.clamp((mouseX - sliderX) / sliderWidth, 0.0f, 1.0f);
                float step = s.increment <= 0 ? 0.1F : s.increment;
                s.set((float) Mathf.step(s.min + (s.max - s.min) * newProgress, step));
            }

            float progress = (s.max == s.min) ? 0 : (s.getValue() - s.min) / (s.max - s.min);
            float targetKnobX = sliderX + ((sliderWidth - 5f) * progress) + 1.5f;
            animV = Mathf.fast(animV, targetKnobX, 16);

            int bgColor = ColorUtil.rgba(10, 10, 10, 204);
            RenderUtil.Rounded.roundedRect(stack, sliderX, sliderY, sliderWidth, 2f, ColorUtil.multAlpha(bgColor, alphaV), Round.of(1f));

            int barColor = ColorUtil.rgba(220, 35, 64, 255);
            RenderUtil.Rounded.roundedRect(stack, sliderX, sliderY, (progress * sliderWidth), 2f, ColorUtil.multAlpha(barColor, alphaV), Round.of(3f));

        } else if (setting instanceof ModeSetting s) {
            selectedV = (selected) ? 18f + s.values.size() * 10f : 12f;
            height = Mathf.fast(height, selectedV, 12f);
            animV = Mathf.fast(animV, selectedV / (18f + s.values.size() * 10f), 15);

            Fonts.SF_MEDIUM.draw(stack, s.getName(), x, y + 2f, ColorUtil.multAlpha(ColorUtil.getColor(146, 147, 150), alphaV), 10f);

            float modeWidth = width * 0.4f;
            float modeX = x + width - modeWidth;

            int bgColor = ColorUtil.rgba(146, 147, 150, 20);
            RenderUtil.Rounded.roundedRect(stack, modeX, y, modeWidth, 13f, ColorUtil.multAlpha(bgColor, alphaV), Round.of(5f));

            String selectedValue = s.getValue();
            ScissorExp.push();
            ScissorExp.setFromComponentCoordinates(modeX + 2f, y, modeWidth - 4f, 13f);
            Fonts.SF_MEDIUM.draw(stack, selectedValue, modeX + 3f, y + 2f, ColorUtil.multAlpha(ColorUtil.WHITE, alphaV), 9f);
            ScissorExp.unset();
            ScissorExp.pop();

            if (height >= 12.1f) {
                float offsetY = y + 16f;
                RenderUtil.Rounded.roundedRect(stack, modeX, offsetY - 1f, modeWidth, height - 16f, ColorUtil.multAlpha(ColorUtil.rgba(16, 16, 16, 230), alphaV), Round.of(5f));
                ScissorExp.push();
                ScissorExp.setFromComponentCoordinates(modeX, offsetY, modeWidth, height - 14f);
                for (String string : s.values) {
                    if (s.is(string))
                        RenderUtil.Rounded.roundedRect(stack, modeX + 2.5f, offsetY + 2.5f, 1f, 4f, ColorUtil.multAlpha(accentColor, animV * alphaV), Round.of(1));
                    Fonts.SF_MEDIUM.draw(stack, string, modeX + 4f + (s.is(string) ? 3f : 0), offsetY, s.is(string) ? ColorUtil.multAlpha(accentColor, animV * alphaV) : ColorUtil.multAlpha(ColorUtil.getColor(146, 147, 150), animV * alphaV), 9f);
                    offsetY += 10f * (height / (18f + s.values.size() * 10f));
                }
                ScissorExp.unset();
                ScissorExp.pop();
            }

        } else if (setting instanceof MultiBooleanSetting s) {
            String values = s.getValue().values().stream().filter(BooleanSetting::getValue).map(BooleanSetting::getName).collect(Collectors.joining(", "));
            selectedV = (selected) ? 18f + s.getValues().size() * 10f : 12f;
            height = Mathf.fast(height, selectedV, 12f);
            animV = Mathf.fast(animV, selectedV / (18f + s.getValues().size() * 10f), 15);

            Fonts.SF_MEDIUM.draw(stack, s.getName(), x, y + 2f, ColorUtil.multAlpha(ColorUtil.getColor(146, 147, 150), alphaV), 10f);

            float modeWidth = width * 0.4f;
            float modeX = x + width - modeWidth;

            int bgColor = ColorUtil.rgba(146, 147, 150, 20);
            RenderUtil.Rounded.roundedRect(stack, modeX, y, modeWidth, 13f, ColorUtil.multAlpha(bgColor, alphaV), Round.of(5f));

            ScissorExp.push();
            ScissorExp.setFromComponentCoordinates(modeX + 2f, y, modeWidth - 4f, 13f);
            Fonts.SF_MEDIUM.draw(stack, values, modeX + 3f, y + 2f, ColorUtil.multAlpha(ColorUtil.WHITE, alphaV), 9f);
            ScissorExp.unset();
            ScissorExp.pop();

            if (height >= 12.1f) {
                float offsetY = y + 16f;
                RenderUtil.Rounded.roundedRect(stack, modeX, offsetY - 1f, modeWidth, height - 16f, ColorUtil.multAlpha(ColorUtil.rgba(16, 16, 16, 230), alphaV), Round.of(5f));
                ScissorExp.push();
                ScissorExp.setFromComponentCoordinates(modeX, offsetY, modeWidth, height - 14f);
                int id = 0;
                for (BooleanSetting booleanSetting : s.getValues()) {
                    interpolate.set(id, Mathf.fast(interpolate.get(id), booleanSetting.getValue() ? 1 : 0, 16));
                    if (interpolate.get(id) >= .1f)
                        RenderUtil.Rounded.roundedRect(stack, modeX + 2.5f, offsetY + 2.5f, 1f, 4f, ColorUtil.multAlpha(accentColor, animV * interpolate.get(id) * alphaV), Round.of(1));
                    Fonts.SF_MEDIUM.draw(stack, booleanSetting.getName(), modeX + 4f + interpolate.get(id) * 3f, offsetY, ColorUtil.overCol(ColorUtil.multAlpha(ColorUtil.getColor(146, 147, 150), animV * alphaV), ColorUtil.multAlpha(accentColor, animV), interpolate.get(id) * alphaV), 9f);
                    offsetY += 10f * (height / (18f + s.getValues().size() * 10f));
                    id++;
                }
                ScissorExp.unset();
                ScissorExp.pop();
            }

        } else if (setting instanceof ColorSetting s) {
            float targetHeight = selected ? 12f + 95f : 12f;
            height = Mathf.fast(height, targetHeight, 12f);
            animV = Mathf.fast(animV, selected ? 1 : 0, 15);

            Fonts.SF_MEDIUM.draw(stack, s.getName(), x, y + 2f, ColorUtil.multAlpha(ColorUtil.getColor(146, 147, 150), alphaV), 10f);

            float[] c = ColorUtil.getRGBAf(s.getValue());
            RenderUtil.Rounded.roundedRect(stack, x + width - 12f, y + 1f, 12f, 10f, ColorUtil.multAlpha(s.getValue(), c[3] * alphaV), Round.of(5f));
            RenderUtil.Rounded.roundedOutline(stack, x + width - 12f, y + 1f, 12f, 10f, 1f, ColorUtil.multAlpha(s.getValue(), (c[3] + .2f) * alphaV), Round.of(5f));

            if (selected || animV > 0.01f) {
                if (colorPicker == null) {
                    colorPicker = new org.privates.client.screen.newgui.ColorPicker(x, y + 14f, width, 90f, s, (org.privates.client.screen.newgui.Setting) null);
                }
                colorPicker.setX(x);
                colorPicker.setY(y + 14f);
                colorPicker.setWidth(width);
                colorPicker.setHeight(90f);

                if (animV > 0.01f) {
                    stack.push();
                    stack.translate(x + width / 2f, y + 14f + 45f, 0);
                    stack.scale(1, animV, 1);
                    stack.translate(-(x + width / 2f), -(y + 14f + 45f), 0);
                    colorPicker.render(stack, (int) mouseX, (int) mouseY);
                    stack.pop();
                }
            } else {
                colorPicker = null;
            }

        } else if (setting instanceof BindSetting s) {
            height = 12f;

            String text;
            if (selected) {
                text = "...";
            } else {
                int currentBind = s.getValue();
                if (currentBind >= 500) {
                    text = "M" + (currentBind - 500 + 1);
                } else {
                    text = (currentBind == -1 ? "нема" : Keyboard.keyName(currentBind));
                }
            }

            Fonts.SF_MEDIUM.draw(stack, s.getName(), x, y + 2f, ColorUtil.multAlpha(ColorUtil.getColor(146, 147, 150), alphaV), 10f);

            float bindWidth = Fonts.SF_MEDIUM.getWidth(text, 10f) + 4f;
            int bgColor = ColorUtil.rgba(146, 147, 150, 20);
            RenderUtil.Rounded.roundedRect(stack, x + width - bindWidth, y + 1f, bindWidth, 11f, ColorUtil.multAlpha(bgColor, alphaV), Round.of(5f));
            Fonts.SF_MEDIUM.draw(stack, text, x + width - bindWidth + 2f, y + 2f, ColorUtil.multAlpha(ColorUtil.WHITE, alphaV), 10f);

        } else if (setting instanceof StringSetting s) {
            height = 12f;
            animV = Mathf.fast(animV, selected ? 1 : 0, 16f);

            Fonts.SF_MEDIUM.draw(stack, s.getName(), x, y + 2f, ColorUtil.multAlpha(ColorUtil.getColor(146, 147, 150), alphaV), 10f);

            float stringWidth = width * 0.4f;
            float stringX = x + width - stringWidth;

            int bgColor = ColorUtil.rgba(146, 147, 150, 20);
            RenderUtil.Rounded.roundedRect(stack, stringX, y + 1f, stringWidth, 11f, ColorUtil.multAlpha(bgColor, alphaV), Round.of(5f));
            Fonts.SF_MEDIUM.draw(stack, (selected || !s.getValue().isEmpty()) ? s.getValue() : s.getDescription(), stringX + 2f, y + 2f, ColorUtil.multAlpha(ColorUtil.WHITE, alphaV), 9f);
        }
    }

    public void mouse(float mouseX, float mouseY, int button, int action) {
        if (!(boolean) setting.getVisible().get()) {
            return;
        }

        if (setting instanceof ColorSetting s && selected && colorPicker != null && action == 0) {
            boolean clickedOutside = !isHovered(mouseX, mouseY, x, y, width, height);
            if (clickedOutside) {
                selected = false;
                return;
            }
        }

        if (isInputMode && action == 0) {
            if (setting instanceof SliderSetting) applyInputValue((SliderSetting) setting);
            else isInputMode = false;
        }

        if (setting instanceof BindSetting bindSetting && selected && action == 0) {
            bindSetting.set(button + 500);
            selected = false;
            SoundUtil.playSound("bind_succes.wav", 0.3f);
            return;
        }

        if (action == GLFW.GLFW_PRESS && button == 0) {
            if ((setting instanceof ModeSetting || setting instanceof MultiBooleanSetting || setting instanceof BindSetting || setting instanceof StringSetting || setting instanceof ColorSetting) && isHovered(mouseX, mouseY, x, y, width, 12f)) {
                if (setting instanceof BindSetting) {
                    SoundUtil.playSound("bind_wait.wav", 0.1f);
                }

                if (setting instanceof ColorSetting) {
                    selected = !selected;
                    if (colorPicker == null && selected) {
                        colorPicker = new org.privates.client.screen.newgui.ColorPicker(x, y + 14f, width, 90f, (ColorSetting) setting, (org.privates.client.screen.newgui.Setting) null);
                    }
                    return;
                } else if (setting instanceof ModeSetting) {
                    SoundUtil.playSound(selected ? "mode_close.wav" : "mode_open.wav", 0.1f);
                    selected = !selected;
                } else {
                    selected = !selected;
                }
                return;
            }
        }

        if (button == 0) {
            float modeWidth = width * 0.4f;
            float modeX = x + width - modeWidth;

            if (action == 0 && setting instanceof BooleanSetting s && isHovered(mouseX, mouseY, x, y, width, height)) {
                s.set(!s.getValue());
                SoundUtil.playSound(s.getValue() ? "cb_enabled.wav" : "cb_disabled.wav", 0.1f);
            } else if (setting instanceof SliderSetting s) {
                float sliderWidth = width - 4f;
                float sliderX = x + 0.5f;
                float sliderY = y + height - 5f;

                String valueText = String.format("%.2f", s.getValue());
                float valueTextWidth = Fonts.SF_MEDIUM.getWidth(valueText, 10f);
                float valueBoxWidth = valueTextWidth + 4f;
                float valueBoxX = x + width - valueBoxWidth - 2f;
                float valueBoxY = y - 1f;
                float valueBoxHeight = 12f;

                if (action == 0 && isHovered(mouseX, mouseY, valueBoxX, valueBoxY, valueBoxWidth, valueBoxHeight)) {
                    isInputMode = true;
                    inputValueString = String.format("%.2f", s.getValue()).replace(',', '.');
                    selected = false;
                    return;
                }

                if (action == GLFW.GLFW_PRESS && isHovered(mouseX, mouseY, sliderX - 2f, sliderY - 3f, sliderWidth + 4f, 10f)) {
                    selected = true;
                    SoundUtil.playSound("slider.wav", 0.1f);
                }
            } else if (action == 0 && setting instanceof ModeSetting s && selected && isHovered(mouseX, mouseY, modeX, y + 16f, modeWidth, height - 16f)) {
                float offsetY = y + 16f;
                for (String string : s.values) {
                    if (isHovered(mouseX, mouseY, modeX, offsetY, modeWidth, 10f)) {
                        selected = false;
                        boolean wasSelected = s.is(string);
                        s.set(string);
                        if (!wasSelected) {
                            SoundUtil.playSound("mode_pick_on.wav", 0.1f);
                        } else {
                            SoundUtil.playSound("mode_pick.wav", 0.1f);
                        }
                        break;
                    }
                    offsetY += 10f * (height / (16f + s.values.size() * 10f));
                }
            } else if (action == 0 && setting instanceof MultiBooleanSetting s && selected && isHovered(mouseX, mouseY, modeX, y + 16f, modeWidth, height - 16f)) {
                float offsetY = y + 16f;
                for (BooleanSetting booleanSetting : s.getValues()) {
                    if (isHovered(mouseX, mouseY, modeX, offsetY, modeWidth, 10f)) {
                        boolean wasSelected = booleanSetting.getValue();
                        booleanSetting.set(!booleanSetting.getValue());
                        if (!wasSelected) {
                            SoundUtil.playSound("mode_pick_on.wav", 0.1f);
                        } else {
                            SoundUtil.playSound("mode_pick.wav", 0.1f);
                        }
                        break;
                    }
                    offsetY += 10f * (height / (18f + s.getValues().size() * 10f));
                }
            } else if (setting instanceof ColorSetting s) {
                if (selected && colorPicker != null) {
                    if (isHovered(mouseX, mouseY, x, y + 14f, width, 90f) && action == GLFW.GLFW_PRESS) {
                        colorPicker.mouseClicked(mouseX, mouseY, button);
                    }
                }
            }
        }

        if (action == GLFW.GLFW_RELEASE) {
            if (selected && setting instanceof SliderSetting) {
                selected = false;
            }
            if (setting instanceof ColorSetting s && colorPicker != null) {
                colorPicker.mouseReleased(mouseX, mouseY, button);
            }
        }
    }

    public void key(int key) {
        if (!(boolean) setting.getVisible().get()) {
            return;
        }

        if (isInputMode && setting instanceof SliderSetting s) {
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
            selected = false;
            if (key == GLFW.GLFW_KEY_DELETE || key == GLFW.GLFW_KEY_ESCAPE) {
                s.set(-1);
                SoundUtil.playSound("bind_remove.wav", 0.1f);
            } else {
                s.set(key);
                SoundUtil.playSound("bind_succes.wav", 0.1f);
            }
        } else if (setting instanceof StringSetting s && selected) {
            if (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_ESCAPE)
                selected = false;
            else if (key == GLFW.GLFW_KEY_BACKSPACE)
                s.set(s.getValue().isEmpty() ? "" : s.getValue().substring(0, s.getValue().length() - 1));
        }
    }

    public void charTyped(char typed) {
        if (!(boolean) setting.getVisible().get()) {
            return;
        }

        if (isInputMode && setting instanceof SliderSetting) {
            if (Character.isDigit(typed) || (typed == '.' && !inputValueString.contains(".")) || (typed == '-' && inputValueString.isEmpty())) {
                inputValueString += typed;
            }
            return;
        }

        if (selected && setting instanceof StringSetting s)
            s.set(s.getValue() + typed);
    }

    public void scroll(double d) {
    }

    public boolean isItemSelectorHovered(float mouseX, float mouseY) {
        return false;
    }

    private boolean isHovered(float mouseX, float mouseY, float x, float y, float width, float height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }
}
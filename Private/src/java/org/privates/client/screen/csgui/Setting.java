package org.privates.client.screen.csgui;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.ClipboardHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.math.MathHelper;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.settings.impl.*;
import org.privates.client.utils.keyboard.Keyboard;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.draw.ScissorUtil;
import org.privates.client.utils.render.font.Fonts;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

@Getter
public class Setting implements Element {

    private final Module module;
    private final org.privates.client.managers.module.settings.Setting<?> setting;
    private final float width;

    @Setter
    private float x, y, height;
    @Setter
    private float delta;
    @Setter
    private float value;
    private boolean selected;
    @Setter
    private float interpolate;
    private float modeWidth, modeWidthV;
    private boolean expanded = false;

    public Setting(Module module, org.privates.client.managers.module.settings.Setting<?> setting, float x, float y) {
        this.module = module;
        this.setting = setting;
        this.x = x;
        this.y = y;
        this.width = module.getWidth() - 20;
        this.selected = false;

        // Устанавливаем высоту в зависимости от типа настройки
        if (setting instanceof BooleanSetting) {
            height = 20;
        } else if (setting instanceof SliderSetting) {
            height = 35;
        } else if (setting instanceof ModeSetting s) {
            height = 25;
            value = height;
            modeWidth = Fonts.SF_MEDIUM.getWidth(s.getValue(), 9) + 15;
            modeWidthV = modeWidth;
        } else if (setting instanceof StringSetting) {
            height = 30;
        } else if (setting instanceof BindSetting) {
            height = 25;
        }
    }

    @Override
    public void render(MatrixStack stack, float mouseX, float mouseY) {
        Theme theme = Theme.getInstance();

        if (setting instanceof BooleanSetting s) {
            interpolate = (float) Mathf.lerp(interpolate, s.getValue() ? 1 : 0, 12f);

            // Название настройки
            Fonts.SF_MEDIUM.draw(stack, s.getName(), x, y + delta + 6,
                    Color.WHITE.getRGB(), 10);

            // Переключатель
            float toggleX = x + width - 30;
            float toggleY = y + delta + 2;

            Color enabledColor = new Color(theme.clientColor());
            Color disabledColor = new Color(50, 50, 60);
            Color switchColor = interpolateColor(enabledColor, disabledColor, interpolate);

            RenderUtil.Rounded.roundedRect(stack, toggleX, toggleY, 20, 10,
                    switchColor.getRGB(), Round.of(5));

            // Индикатор
            float indicatorX = toggleX + 2 + (interpolate * 8);
            Color indicatorColor = new Color(255, 255, 255);

            RenderUtil.Rounded.roundedRect(stack, indicatorX, toggleY + 2, 6, 6,
                    indicatorColor.getRGB(), Round.of(3));

        } else if (setting instanceof SliderSetting s) {
            if (selected) {
                float newValue = (float) MathHelper.clamp(
                        Mathf.step((mouseX - x) / (width - 20) * (s.max - s.min) + s.min, s.increment),
                        s.min, s.max);
                s.set(newValue);
            }

            value = (float) Mathf.lerp(value, (width - 20) * (s.getValue() - s.min) / (s.max - s.min), 15f);

            // Название настройки
            Fonts.SF_MEDIUM.draw(stack, s.getName(), x, y + delta + 5,
                    Color.WHITE.getRGB(), 10);

            // Значение
            String valueText = String.valueOf(s.getValue());
            float valueWidth = Fonts.SF_MEDIUM.getWidth(valueText, 9);
            Fonts.SF_MEDIUM.draw(stack, valueText, x + width - valueWidth - 10, y + delta + 5,
                    ColorUtil.getColor(150, 150, 150), 9);

            // Диапазон значений
            String rangeText = s.min + " / " + s.max;
            Fonts.SF_MEDIUM.draw(stack, rangeText, x + width - Fonts.SF_MEDIUM.getWidth(rangeText, 8) - 10,
                    y + delta + 17, ColorUtil.getColor(100, 100, 100), 8);

            // Слайдер
            float sliderY = y + delta + height - 8;
            RenderUtil.Rounded.roundedRect(stack, x, sliderY, width - 10, 3,
                    ColorUtil.getColor(40, 40, 50), Round.of(1.5f));

            RenderUtil.Rounded.roundedRect(stack, x, sliderY, value, 3,
                    theme.clientColor(), Round.of(1.5f));

            RenderUtil.Rounded.roundedRect(stack, x + value - 3, sliderY - 2, 6, 7,
                    theme.clientColor(), Round.of(3));

        } else if (setting instanceof ModeSetting s) {
            if (modeWidth != modeWidthV)
                modeWidthV = (float) Mathf.lerp(modeWidthV, modeWidth, 10f);

            if (height != value)
                height = (float) Mathf.lerp(height, value, 15f);

            // Название настройки
            Fonts.SF_MEDIUM.draw(stack, s.getName(), x, y + delta + 8,
                    Color.WHITE.getRGB(), 10);

            // Выпадающий список
            float dropdownX = x + width - modeWidthV;
            RenderUtil.Rounded.roundedRect(stack, dropdownX, y + delta + 2, modeWidthV,
                    expanded ? height - 4 : 20, ColorUtil.getColor(35, 35, 45), Round.of(4));
            RenderUtil.Rounded.roundedOutline(stack, dropdownX, y + delta + 2, modeWidthV,
                    expanded ? height - 4 : 20, 4f, ColorUtil.getColor(50, 50, 60), Round.of(4));

            // Текущее значение
            Fonts.SF_MEDIUM.draw(stack, s.getValue(), dropdownX + 8, y + delta + 8,
                    Color.WHITE.getRGB(), 9);

            // Стрелка
            String arrow = expanded ? "▴" : "▾";
            Fonts.SF_MEDIUM.draw(stack, arrow, dropdownX + modeWidthV - 15, y + delta + 8,
                    ColorUtil.getColor(150, 150, 150), 8);

            // Опции (если развернуто)
            if (expanded) {
                ScissorUtil.enableScissor(dropdownX, y + delta + 2, modeWidthV, height - 4);
                float optionY = y + delta + 25;
                for (String option : s.values) {
                    if (!option.equals(s.getValue())) {
                        Color textColor = option.equals(s.getValue()) ?
                                new Color(theme.clientColor()) : new Color(180, 180, 180);

                        Fonts.SF_MEDIUM.draw(stack, option, dropdownX + 8, optionY,
                                textColor.getRGB(), 9);
                        optionY += 15;
                    }
                }
                ScissorUtil.disableScissor();
            }

        } else if (setting instanceof BindSetting s) {
            // Название настройки
            Fonts.SF_MEDIUM.draw(stack, s.getName(), x, y + delta + 8,
                    Color.WHITE.getRGB(), 10);

            // Кнопка биндинга
            String text = selected ? "..." : ((s.getValue() > 0) ? Keyboard.keyName(s.getValue()) : "нема");
            float bindWidth = Math.max(40, Fonts.SF_MEDIUM.getWidth(text, 9) + 16);
            float bindX = x + width - bindWidth;

            RenderUtil.Rounded.roundedRect(stack, bindX, y + delta + 2, bindWidth, 20,
                    ColorUtil.getColor(35, 35, 45), Round.of(4));
            RenderUtil.Rounded.roundedOutline(stack, bindX, y + delta + 2, bindWidth, 20, 4f,
                    ColorUtil.getColor(50, 50, 60), Round.of(4));

            float textX = bindX + bindWidth / 2f - Fonts.SF_MEDIUM.getWidth(text, 9) / 2f;
            Fonts.SF_MEDIUM.draw(stack, text, textX, y + delta + 8,
                    Color.WHITE.getRGB(), 9);

        } else if (setting instanceof StringSetting s) {
            // Название настройки
            Fonts.SF_MEDIUM.draw(stack, s.getName(), x, y + delta + 5,
                    Color.WHITE.getRGB(), 10);

            // Поле ввода
            RenderUtil.Rounded.roundedRect(stack, x, y + delta + 16, width - 10, 12,
                    ColorUtil.getColor(35, 35, 45), Round.of(3));
            RenderUtil.Rounded.roundedOutline(stack, x, y + delta + 16, width - 10, 12, 3f,
                    selected ? theme.clientColor() : ColorUtil.getColor(50, 50, 60), Round.of(3));

            ScissorUtil.enableScissor(x + 5, y + delta + 16, width - 20, 12);
            String displayText = s.getValue().isEmpty() ? "description" : s.getValue();
            Color textColor = s.getValue().isEmpty() ?
                    new Color(100, 100, 100) : new Color(255, 255, 255);

            Fonts.SF_MEDIUM.draw(stack, displayText, x + 8, y + delta + 20,
                    textColor.getRGB(), 8);
            ScissorUtil.disableScissor();
        }
    }

    @Override
    public void mouse(float mouseX, float mouseY, int button, int action) {
        if (Mathf.isHover(mouseX, mouseY, x, y + delta, width, height)) {
            if (setting instanceof BooleanSetting s && action == GLFW.GLFW_PRESS) {
                s.set(!s.getValue());

            } else if (setting instanceof SliderSetting && action == GLFW.GLFW_PRESS) {
                selected = true;

            } else if (setting instanceof ModeSetting s && action == GLFW.GLFW_PRESS) {
                float dropdownX = x + width - modeWidthV;

                if (Mathf.isHover(mouseX, mouseY, dropdownX, y + delta + 2, modeWidthV, 20)) {
                    expanded = !expanded;
                    modeWidth = expanded ?
                            Math.max(100, s.values.stream().mapToInt(str -> (int) Fonts.SF_MEDIUM.getWidth(str, 9)).max().orElse(0) + 25) :
                            Fonts.SF_MEDIUM.getWidth(s.getValue(), 9) + 25;
                    value = expanded ? 25 + (s.values.size() - 1) * 15 : 25;

                } else if (expanded) {
                    float optionY = y + delta + 25;
                    for (String option : s.values) {
                        if (!option.equals(s.getValue()) &&
                                Mathf.isHover(mouseX, mouseY, dropdownX, optionY - 2, modeWidthV, 15)) {
                            s.set(option);
                            expanded = false;
                            value = 25;
                            modeWidth = Fonts.SF_MEDIUM.getWidth(s.getValue(), 9) + 25;
                            break;
                        }
                        if (!option.equals(s.getValue())) {
                            optionY += 15;
                        }
                    }
                }

            } else if ((setting instanceof BindSetting || setting instanceof StringSetting)
                    && action == GLFW.GLFW_PRESS) {
                selected = !selected;
            }
        } else if (setting instanceof SliderSetting && action == GLFW.GLFW_RELEASE) {
            selected = false;
        } else if (setting instanceof ModeSetting && expanded && action == GLFW.GLFW_PRESS) {
            expanded = false;
            value = 25;
            modeWidth = Fonts.SF_MEDIUM.getWidth(((ModeSetting) setting).getValue(), 9) + 25;
        }
    }

    @Override
    public void keyPressed(int key) {
        if (setting instanceof BindSetting s && selected) {
            s.set((key != GLFW.GLFW_KEY_DELETE && key != GLFW.GLFW_KEY_ESCAPE) ? key : -1);
            selected = false;
        } else if (setting instanceof StringSetting s && selected) {
            if (key == GLFW.GLFW_KEY_BACKSPACE || key == GLFW.GLFW_KEY_DELETE) {
                s.set(s.getValue().isEmpty() ? "" : s.getValue().substring(0, s.getValue().length() - 1));
            } else if (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_ESCAPE) {
                selected = false;
            } else if (key == GLFW.GLFW_KEY_V && InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), GLFW.GLFW_KEY_LEFT_CONTROL)) {
                s.set(new ClipboardHelper().getClipboardString(Minecraft.getInstance().getMainWindow().getHandle(), null));
                selected = false;
            }
        }
    }

    @Override
    public void charTyped(char typed) {
        if (setting instanceof StringSetting s && selected) {
            s.set(s.getValue() + typed);
        }
    }

    private boolean isInRange(float y, float height) {
        return y + height >= module.getCategory().getGui().getY() + 100 &&
                y <= module.getCategory().getGui().getY() + module.getCategory().getGui().getHeight() - 20;
    }

    private boolean isInRange(float y, float height, float parentY, float parentHeight) {
        return y >= parentY && y + height <= parentY + parentHeight;
    }

    private Color interpolateColor(Color color1, Color color2, float factor) {
        int red = Math.max(0, Math.min(255, (int) (color1.getRed() * factor + color2.getRed() * (1 - factor))));
        int green = Math.max(0, Math.min(255, (int) (color1.getGreen() * factor + color2.getGreen() * (1 - factor))));
        int blue = Math.max(0, Math.min(255, (int) (color1.getBlue() * factor + color2.getBlue() * (1 - factor))));
        return new Color(red, green, blue);
    }
}
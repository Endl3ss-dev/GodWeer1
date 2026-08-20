package org.privates.client.screen.rockgui;

import lombok.Getter;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.api.interfaces.IMinecraft;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.settings.Setting;
import org.privates.client.managers.module.settings.impl.*;
import org.privates.client.utils.animation.Animation;
import org.privates.client.utils.animation.util.Easings;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.font.Fonts;

@Getter
public class SettingRect implements IMinecraft {

    private final Setting parent;
    private float x, y, width, height;

    private final Animation hover = new Animation().setSpeed(200).setEasing(Easings.SINE_IN_OUT);
    private final Animation slowHover = new Animation().setSpeed(500).setEasing(Easings.SINE_IN_OUT);
    private final Animation hide = new Animation().setSpeed(200).setEasing(Easings.SINE_IN_OUT);

    private BooleanSetting dragBool;
    private SliderSetting dragSlider;

    public SettingRect(Setting parent) {
        this.parent = parent;
        this.height = 32;
    }

    public void set(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks, float anim) {
        Theme theme = Theme.getInstance();
        int bgColor = ColorUtil.multAlpha(theme.secondaryColor(), anim);
        int separatorColor = ColorUtil.multAlpha(theme.secondaryColor(), anim);
        int textColor = ColorUtil.multAlpha(theme.textColor(), anim);

        hover.setForward(Mathf.isHover(mouseX, mouseY, x, y, width, height));
        slowHover.setForward(Mathf.isHover(mouseX, mouseY, x, y, width, height));

        RenderUtil.CustomStyled.drawGui(matrixStack, x, y, width, height, 7, true, 20f);

        Fonts.SF_MEDIUM.draw(matrixStack, parent.getName(), x + 8.5f, y + 8.5f, textColor, 16);

        if (parent instanceof BooleanSetting bool) {
            renderBoolean(matrixStack, bool, anim);
        } else if (parent instanceof SliderSetting slider) {
            renderSlider(matrixStack, mouseX, mouseY, slider, anim);
        } else if (parent instanceof ModeSetting mode) {
            renderMode(matrixStack, mouseX, mouseY, mode, anim);
        } else if (parent instanceof MultiBooleanSetting multi) {
            renderMultiBoolean(matrixStack, mouseX, mouseY, multi, anim);
        } else if (parent instanceof ColorSetting color) {
            renderColor(matrixStack, color, anim);
        } else if (parent instanceof BindSetting bind) {
            renderBind(matrixStack, bind, anim);
        } else if (parent instanceof StringSetting string) {
            renderString(matrixStack, mouseX, mouseY, string, anim);
        }
    }

    private void renderBoolean(MatrixStack matrixStack, BooleanSetting bool, float anim) {
        Theme theme = Theme.getInstance();
        int separatorColor = ColorUtil.multAlpha(theme.secondaryColor(), anim);
        int textColor = ColorUtil.multAlpha(theme.textColor(), anim);

        float toggleX = x + width - height / 2 - 5;
        float toggleY = y + height / 2 - 5;

        float toggleAnim = bool.getValue() ? 1.0f : 0.0f;

        int toggleBg = ColorUtil.interpolateColor(separatorColor, theme.clientColor(), toggleAnim);

        RenderUtil.Rounded.smooth(matrixStack, toggleX, toggleY, 10, 10, toggleBg, Round.of(2));

        if (bool.getValue()) {
            Fonts.SF_BOLD.draw(matrixStack, "✓", toggleX + 2, toggleY + 1, textColor, 8);
        }
    }

    private void renderSlider(MatrixStack matrixStack, int mouseX, int mouseY, SliderSetting slider, float anim) {
        Theme theme = Theme.getInstance();
        int separatorColor = ColorUtil.multAlpha(theme.secondaryColor(), anim);
        int textColor = ColorUtil.multAlpha(theme.textColor(), anim);

        this.height = 39;

        if (dragSlider == slider) {
            float value = Mathf.clamp(
                    slider.min,
                    slider.max,
                    slider.min + (mouseX - (x + 8.5f)) / (width - 17) * (slider.max - slider.min)
            );
            slider.set(value);
        }

        String display = String.format("%.1f", slider.getValue());
        float displayWidth = Fonts.SF_MEDIUM.getWidth(display, 12) + 5;

        RenderUtil.Rounded.smooth(matrixStack,
                x + width - 28 / 2 + 5 - displayWidth,
                y + 28 / 2 - 5,
                displayWidth, 10,
                separatorColor,
                Round.of(2)
        );

        Fonts.SF_MEDIUM.draw(matrixStack, display,
                x + width - 9.5f - Fonts.SF_MEDIUM.getWidth(display, 12) - 2.5f,
                y + 10, textColor, 12);

        RenderUtil.Rounded.smooth(matrixStack,
                x + 8.5f, y + 25,
                width - 17, 4,
                separatorColor,
                Round.of(2)
        );

        float progress = (slider.getValue() - slider.min) / (slider.max - slider.min);

        RenderUtil.Rounded.roundedRect(matrixStack,
                x + 8.5f, y + 25,
                (width - 17) * progress, 4,
                theme.clientColor(),
                Round.of(2)
        );

        float circle = 5 + hover.get();
        RenderUtil.Rounded.smooth(matrixStack,
                x + 8.5f + (width - 17) * progress - circle / 2,
                y + 27 - circle / 2,
                circle, circle,
                ColorUtil.getColor(255, 255, 255),
                Round.of(circle / 2)
        );
    }

    private void renderMode(MatrixStack matrixStack, int mouseX, int mouseY, ModeSetting mode, float anim) {
        Theme theme = Theme.getInstance();
        int separatorColor = ColorUtil.multAlpha(theme.secondaryColor(), anim);
        int textColor = ColorUtil.multAlpha(theme.textColor(), anim);

        float offX = 0, offY = 0;
        float totalHeight = 25;

        for (String element : mode.values) {
            float elementWidth = Fonts.SF_MEDIUM.getWidth(element, 14) + 5;

            if (offX + elementWidth > width - 8) {
                offX = 0;
                offY += 13;
                totalHeight += 13;
            }

            boolean selected = mode.getValue().equals(element);
            int bgColor = selected ? theme.clientColor() : separatorColor;

            RenderUtil.Rounded.smooth(matrixStack,
                    x + offX + 8.5f,
                    y + 25 + offY,
                    elementWidth, 10,
                    bgColor,
                    Round.of(2)
            );

            int elemTextColor = selected ? ColorUtil.getColor(255, 255, 255) : textColor;
            Fonts.SF_MEDIUM.draw(matrixStack, element,
                    x + offX + 10.5f,
                    y + 25 + offY,
                    elemTextColor, 14);

            offX += elementWidth + 3;
        }

        this.height = totalHeight;
    }

    private void renderMultiBoolean(MatrixStack matrixStack, int mouseX, int mouseY, MultiBooleanSetting multi, float anim) {
        Theme theme = Theme.getInstance();
        int separatorColor = ColorUtil.multAlpha(theme.secondaryColor(), anim);
        int textColor = ColorUtil.multAlpha(theme.textColor(), anim);

        float offX = 0, offY = 0;
        float totalHeight = 25;

        for (BooleanSetting element : multi.getValues()) {
            float elementWidth = Fonts.SF_MEDIUM.getWidth(element.getName(), 14) + 5;

            if (offX + elementWidth > width - 8) {
                offX = 0;
                offY += 13;
                totalHeight += 13;
            }

            boolean selected = element.getValue();
            int bgColor = selected ? theme.clientColor() : separatorColor;

            RenderUtil.Rounded.smooth(matrixStack,
                    x + offX + 8.5f,
                    y + 25 + offY,
                    elementWidth, 10,
                    bgColor,
                    Round.of(2)
            );

            int elemTextColor = selected ? ColorUtil.getColor(255, 255, 255) : textColor;
            Fonts.SF_MEDIUM.draw(matrixStack, element.getName(),
                    x + offX + 10.5f,
                    y + 25 + offY,
                    elemTextColor, 14);

            offX += elementWidth + 3;
        }

        this.height = totalHeight;
    }

    private void renderColor(MatrixStack matrixStack, ColorSetting color, float anim) {
        Theme theme = Theme.getInstance();
        int separatorColor = ColorUtil.multAlpha(theme.secondaryColor(), anim);

        float pickerX = x + width - height / 2 - 5;
        float pickerY = y + height / 2 - 5;

        RenderUtil.Rounded.smooth(matrixStack, pickerX, pickerY, 10, 10, separatorColor, Round.of(5));
        RenderUtil.Rounded.smooth(matrixStack, pickerX + 1, pickerY + 1, 8, 8, color.getValue(), Round.of(4));
    }

    private void renderBind(MatrixStack matrixStack, BindSetting bind, float anim) {
        Theme theme = Theme.getInstance();
        int separatorColor = ColorUtil.multAlpha(theme.secondaryColor(), anim);
        int textColor = ColorUtil.multAlpha(theme.textColor(), anim);

        String display = "...";
        float size = Fonts.SF_MEDIUM.getWidth(display, 12) + 6;

        RenderUtil.Rounded.smooth(matrixStack,
                x + width - 28 / 2 + 5 - size,
                y + 28 / 2 - 5,
                size, 10,
                separatorColor,
                Round.of(2)
        );

        Fonts.SF_MEDIUM.draw(matrixStack, display,
                x + width - 9.5f - Fonts.SF_MEDIUM.getWidth(display, 12) - 2.5f,
                y + 10, textColor, 12);
    }

    private void renderString(MatrixStack matrixStack, int mouseX, int mouseY, StringSetting string, float anim) {
        Theme theme = Theme.getInstance();
        int separatorColor = ColorUtil.multAlpha(theme.secondaryColor(), anim);
        int textColor = ColorUtil.multAlpha(theme.textColor(), anim);

        this.height = 44;

        RenderUtil.Rounded.smooth(matrixStack,
                x + 8.5f, y + 23,
                width - 17, 12,
                separatorColor,
                Round.of(2)
        );

        Fonts.SF_MEDIUM.draw(matrixStack, string.getValue().isEmpty() ? "Enter text..." : string.getValue(),
                x + 13, y + 25,
                ColorUtil.multAlpha(textColor, string.getValue().isEmpty() ? 0.5f : 1.0f), 12);
    }

    public boolean clicked(double mouseX, double mouseY, int button, boolean release) {
        if (!(boolean) parent.getVisible().get()) return false;

        if (release) {
            dragBool = null;
            dragSlider = null;
        } else {
            if (button == 0 && Mathf.isHover((float) mouseX, (float) mouseY, x, y, width, height)) {
                if (parent instanceof BooleanSetting bool) {
                    bool.set(!bool.getValue());
                } else if (parent instanceof SliderSetting slider) {
                    float value = Mathf.clamp(
                            slider.min,
                            slider.max,
                            slider.min + ((float) mouseX - (x + 8.5f)) / (width - 17) * (slider.max - slider.min)
                    );
                    slider.set(value);
                    dragSlider = slider;
                } else if (parent instanceof ModeSetting mode) {
                    handleModeClick(mouseX, mouseY, mode);
                } else if (parent instanceof MultiBooleanSetting multi) {
                    handleMultiBoolClick(mouseX, mouseY, multi);
                } else if (parent instanceof ColorSetting color) {
                    if (mc.currentScreen != null) {
                        return false;
                    }
                }
            } else if ((button == 2 || button == 1) && Mathf.isHover((float) mouseX, (float) mouseY, x, y, width, height)) {
                if (!(parent instanceof ColorSetting)) {
                    return false;
                }
            }
        }

        return Mathf.isHover((float) mouseX, (float) mouseY, x, y, width, height);
    }

    private void handleModeClick(double mouseX, double mouseY, ModeSetting mode) {
        float offX = 0, offY = 0;

        for (String element : mode.values) {
            float elementWidth = Fonts.SF_MEDIUM.getWidth(element, 14) + 5;

            if (offX + elementWidth > width - 8) {
                offX = 0;
                offY += 13;
            }

            if (Mathf.isHover((float) mouseX, (float) mouseY, x + offX + 8.5f, y + 25 + offY, elementWidth, 10)) {
                mode.set(element);
                break;
            }

            offX += elementWidth + 3;
        }
    }

    private void handleMultiBoolClick(double mouseX, double mouseY, MultiBooleanSetting multi) {
        float offX = 0, offY = 0;

        for (BooleanSetting element : multi.getValues()) {
            float elementWidth = Fonts.SF_MEDIUM.getWidth(element.getName(), 14) + 5;

            if (offX + elementWidth > width - 8) {
                offX = 0;
                offY += 13;
            }

            if (Mathf.isHover((float) mouseX, (float) mouseY, x + offX + 8.5f, y + 25 + offY, elementWidth, 10)) {
                element.set(!element.getValue());
                break;
            }

            offX += elementWidth + 3;
        }
    }

    public boolean dragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return Mathf.isHover((float) mouseX, (float) mouseY, x, y, width, height) && button == 0;
    }

    public boolean pressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    public boolean charTyped(char codePoint, int modifiers) {
        return false;
    }

    public void tick() {
    }
}
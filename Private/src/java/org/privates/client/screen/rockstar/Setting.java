package org.privates.client.screen.rockstar;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.util.math.MathHelper;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.platform.GlStateManager;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.settings.impl.ItemSetting;
import org.privates.client.managers.module.settings.impl.*;
import org.privates.client.utils.keyboard.Keyboard;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RectUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.draw.ScissorExp;
import org.privates.client.utils.render.font.Fonts;

import java.util.ArrayList;
import java.util.List;

import static org.privates.client.api.interfaces.IMinecraft.mc;

@Getter
@Setter
@Accessors(fluent = true)
public class Setting extends Builder {

    private final Module module;
    private final org.privates.client.managers.module.settings.Setting setting;
    private final float fontSize = 7f;
    private final int id;
    private final ArrayList<Float> interpolates = new ArrayList<>();

    private boolean selected;
    private float selectedV, visibleV;
    private float delta;
    private float dragX, dragY;
    private float dragXV, dragYV;
    private float startX, startY;
    private long time;
    private org.privates.client.screen.rockstar.ItemSetting itemSetting;
    private ColorPicker colorPicker;

    public Setting(Module module, org.privates.client.managers.module.settings.Setting setting, float x, float y, int id) {
        this.module = module;
        this.setting = setting;
        this.x = x;
        this.y = y;
        this.width = 150;
        this.id = id;
        //if (setting instanceof BooleanSetting) {
        height = 20;
        /*} else*/
        if (setting instanceof SliderSetting)
            height = 30;
        else if (setting instanceof ModeSetting || setting instanceof MultiBooleanSetting || setting instanceof ListSetting<?>) {
            float offsetX = x + 7, offsetY = y + 12 + Fonts.SF_MEDIUM.getHeight(setting.getName(), fontSize);
            final List<?> values = (setting instanceof ModeSetting s) ? s.values : (setting instanceof MultiBooleanSetting s ? s.getValues().stream().map(BooleanSetting::getName).toList() : ((ListSetting<?>) setting).values);
            for (Object object : values) {
                final String string = object.toString();
                interpolates.add((setting instanceof ModeSetting s && s.is(string)) || (setting instanceof MultiBooleanSetting bool && bool.getValue(string)) || (setting instanceof ListSetting<?> list && list.getValue().equals(string)) ? 1F : 0F);
                if (offsetX + 6 + Fonts.SF_MEDIUM.getWidth(string, fontSize) >= x + width - 14 && offsetX != x + 7) {
                    offsetX = x + 7;
                    offsetY += 12;
                }

                if (offsetX + 2 + Fonts.SF_MEDIUM.getWidth(string, fontSize) >= x + width - 14 && offsetX != x + 7) {
                    offsetX = x + 7;
                    offsetY += 12;
                } else if (offsetX + 6 + Fonts.SF_MEDIUM.getWidth(string, fontSize) < x + width - 14) {
                    offsetX += 6 + Fonts.SF_MEDIUM.getWidth(string, fontSize);
                }
            }
            height = 18 + (offsetY - y);
        } else if (setting instanceof DragSetting s) {
            height = 165;
            dragX = (s.position.x / mc.getMainWindow().getScaledWidth()) * (width - 18);
            dragY = (s.position.y / mc.getMainWindow().getScaledHeight()) * (height - 18);
        } else if (setting instanceof ColorSetting s) {
            colorPicker = new ColorPicker(x + width - 2, y + 88, 90, 90, s);
        } else if (setting instanceof StringSetting) {
            height = 35;
            time = System.currentTimeMillis();
            interpolates.add(0F);
        } else if (setting instanceof ItemSetting || setting instanceof MultiItemSetting) {
            itemSetting = new org.privates.client.screen.rockstar.ItemSetting(this, setting, x + width - 2, y - 149);
        }
    }

    @Override
    void render(MatrixStack stack, float mouseX, float mouseY, float alpha) {

        final float y = this.y + delta;
        final float x = this.x + (id == 1 ? -(width + 15) + (width + 15) * visibleV : (width + 15) - (width + 15) * visibleV);
        alpha *= visibleV;
        final Theme theme = Theme.getInstance();
        //nf (height == 0)
        //    return;
        RenderUtil.Rounded.roundedWithOutline(stack, x, y, width, height, 1.3f, ColorUtil.getColor(13, 16, 20, alpha), ColorUtil.getColor(24, 26, 30, alpha), Round.of(6));
        ScissorExp.push();
        ScissorExp.setFromComponentCoordinates(x, y, width - 7, height);
        Fonts.SF_SEMIBOLD.draw(stack, setting.getName(), x + 7, y + 6, ColorUtil.getColor(168, 170, 176, alpha), fontSize);
        ScissorExp.unset();
        ScissorExp.pop();

        if (setting instanceof BooleanSetting s) {
            selectedV = Mathf.fast(selectedV, s.getValue() ? 1 : 0, 24);
            RenderUtil.Rounded.roundedRect(stack, x + width - 15, y + height / 2f - 4, 8, 8,
                    ColorUtil.overCol(ColorUtil.multAlpha(Theme.getInstance().clientColor(), alpha), ColorUtil.getColor(24, 26, 30, alpha), selectedV), Round.of(3));

            GlStateManager.pushMatrix();
            GlStateManager.translatef((x + width - 11), (y + height / 2f), 1);
            if (selectedV <= .95f) {
                GlStateManager.scalef(1 - selectedV, 1 - selectedV, 1 - selectedV);
                GlStateManager.translatef(-(x + width - 11), -(y + height / 2f), 1);
                Fonts.ROCKSTAR.draw(stack, "i", x + width - 11 - Fonts.ROCKSTAR.getWidth("i", fontSize - 2) / 2f, y + height / 2f - Fonts.ROCKSTAR.getHeight("i", fontSize - 2) / 2f - .5f, ColorUtil.getColor(189, 191, 195, (1 - selectedV) * alpha), fontSize - 2);
            } else if (selectedV >= .05f) {
                GlStateManager.scalef(selectedV, selectedV, selectedV);
                GlStateManager.translatef(-(x + width - 11), -(y + height / 2f), 1);
                Fonts.ROCKSTAR.draw(stack, "j", x + width - 11 - Fonts.ROCKSTAR.getWidth("j", fontSize - 2) / 2f, y + height / 2f - Fonts.ROCKSTAR.getHeight("j", fontSize - 2) / 2f, ColorUtil.getColor(189, 191, 195, selectedV * alpha), fontSize - 2);
            }
            GlStateManager.popMatrix();

        } else if (setting instanceof SliderSetting s) {
            final float offsetY = y + height - 11;
            RenderUtil.Rounded.roundedRect(stack, x + 7, offsetY, width - 14, 4, ColorUtil.getColor(27, 29, 35, alpha), Round.of(3));
            RenderUtil.Rounded.roundedRect(stack, x + 7, offsetY, selectedV, 4,
                    ColorUtil.multAlpha(ColorUtil.fade(0), alpha),
                    ColorUtil.multAlpha(ColorUtil.fade(0), alpha),
                    ColorUtil.multAlpha(ColorUtil.fade(30), alpha),
                    ColorUtil.multAlpha(ColorUtil.fade(30), alpha),
                    Round.of(3));
            RenderUtil.Rounded.roundedRect(stack, x + 4.5f + selectedV, offsetY - .5f, 5, 5, ColorUtil.multAlpha(Theme.getInstance().clientColor(), alpha), Round.of(4));

            final String text = String.valueOf(s.getValue());
            final float textWidth = Fonts.SF_MEDIUM.getWidth(text, fontSize) + 3;
            RenderUtil.Rounded.roundedRect(stack, x + width - textWidth - 7, y + 5.5f, textWidth, Fonts.SF_MEDIUM.getHeight(text, fontSize) + 2, ColorUtil.getColor(27, 29, 33, alpha), Round.of(3));
            Fonts.SF_MEDIUM.draw(stack, text, x + width - textWidth - 5.5f, y + 6, ColorUtil.getColor(138, 140, 144, alpha), fontSize);

            float progress = (s.max == s.min) ? 0 : (s.getValue() - s.min) / (s.max - s.min);
            selectedV = Mathf.fast(selectedV, Math.max(2.5f, ((width - 14) * progress) - .5f), 16);
            if (selected) {
                float step = s.increment <= 0 ? 0.1F : s.increment;
                s.set((float) MathHelper.clamp(
                        Mathf.step((mouseX - (x + 7)) / (width - 14) * (s.max - s.min) + s.min,
                                step), s.min, s.max));
            }
        } else if (setting instanceof ModeSetting s) {
            float offsetX = x + 7, offsetY = y + 12 + Fonts.SF_MEDIUM.getHeight(s.getName(), fontSize);
            int iteration = 0;
            for (String string : s.values) {
                if (offsetX + 6 + Fonts.SF_MEDIUM.getWidth(string, fontSize) >= x + width - 14 && offsetX != x + 7) {
                    offsetX = x + 7;
                    offsetY += 12;
                }

                interpolates.set(iteration, Mathf.fast(interpolates.get(iteration), s.is(string) ? 1 : 0, 16));
                final float interpolate = interpolates.get(iteration);
                final int leftGradient = ColorUtil.fade(7, (id * 60), theme.clientColor(), theme.darkColor()),
                        rightGradient = ColorUtil.fade(7, 30 + (id * 60), theme.clientColor(), theme.darkColor());
                final int leftColor = ColorUtil.multAlpha(ColorUtil.overCol(ColorUtil.getColor(26, 28, 32), leftGradient, interpolate), alpha),
                        rightColor = ColorUtil.multAlpha(ColorUtil.overCol(ColorUtil.getColor(26, 28, 32), rightGradient, interpolate), alpha);


                RenderUtil.Rounded.roundedRect(stack, offsetX, offsetY, Fonts.SF_MEDIUM.getWidth(string, fontSize) + 4, 10, leftColor, leftColor, rightColor, rightColor, Round.of(3));
                Fonts.SF_MEDIUM.draw(stack, string, offsetX + 2, offsetY + 1, ColorUtil.getColor(254, 254, 254, (.36f + interpolate * .56f) * alpha), fontSize);

                if (offsetX + 2 + Fonts.SF_MEDIUM.getWidth(string, fontSize) >= x + width - 14 && offsetX != x + 7) {
                    offsetX = x + 7;
                    offsetY += 12;
                } else if (offsetX + 6 + Fonts.SF_MEDIUM.getWidth(string, fontSize) < x + width - 14) {
                    offsetX += 6 + Fonts.SF_MEDIUM.getWidth(string, fontSize);
                }
                iteration++;
            }
        } else if (setting instanceof MultiBooleanSetting s) {
            float offsetX = x + 7, offsetY = y + 12 + Fonts.SF_MEDIUM.getHeight(s.getName(), fontSize);
            int id = 1, iteration = 0;
            final String text = String.format("%s из %s", s.getValues().stream().filter(BooleanSetting::getValue).toList().size(), s.getValue().size());
            Fonts.SF_SEMIBOLD.draw(stack, text, x + width - 7 - Fonts.SF_SEMIBOLD.getWidth(text, fontSize), y + 6, ColorUtil.getColor(168, 170, 176, alpha), fontSize);
            for (BooleanSetting string : s.getValues()) {
                if (offsetX + 6 + Fonts.SF_MEDIUM.getWidth(string.getName(), fontSize) >= x + width - 14 && offsetX != x + 7) {
                    offsetX = x + 7;
                    offsetY += 12;
                    id = 1;
                }

                interpolates.set(iteration, Mathf.fast(interpolates.get(iteration), string.getValue() ? 1 : 0, 16));
                final float interpolate = interpolates.get(iteration);
                final int leftGradient = ColorUtil.fade(7, (id * 60), theme.clientColor(), theme.darkColor()),
                        rightGradient = ColorUtil.fade(7, 30 + (id * 60), theme.clientColor(), theme.darkColor());
                final int leftColor = ColorUtil.multAlpha(ColorUtil.overCol(ColorUtil.getColor(26, 28, 32), leftGradient, interpolate), alpha),
                        rightColor = ColorUtil.multAlpha(ColorUtil.overCol(ColorUtil.getColor(26, 28, 32), rightGradient, interpolate), alpha);


                RenderUtil.Rounded.roundedRect(stack, offsetX, offsetY, Fonts.SF_MEDIUM.getWidth(string.getName(), fontSize) + 4, 10, leftColor, leftColor, rightColor, rightColor, Round.of(3));
                Fonts.SF_MEDIUM.draw(stack, string.getName(), offsetX + 2, offsetY + 1, ColorUtil.getColor(254, 254, 254, (.36f + interpolate * .56f) * alpha), fontSize);

                if (offsetX + 2 + Fonts.SF_MEDIUM.getWidth(string.getName(), fontSize) >= x + width - 14 && offsetX != x + 7) {
                    offsetX = x + 7;
                    offsetY += 12;
                    id = 1;
                } else if (offsetX + 6 + Fonts.SF_MEDIUM.getWidth(string.getName(), fontSize) < x + width - 14) {
                    offsetX += 6 + Fonts.SF_MEDIUM.getWidth(string.getName(), fontSize);
                    id++;
                }
                iteration++;
            }
        } else if (setting instanceof DragSetting s) {
            float offsetX = x + 7, offsetY = y + 12 + Fonts.SF_MEDIUM.getHeight(s.getName(), fontSize);

            if (selected) {

                mouseX = Math.max(x + 7, Math.min(x + width - 7, mouseX));
                mouseY = Math.max(y + 12 + Fonts.SF_SEMIBOLD.getHeight(s.getName(), fontSize), Math.min(y + 12 + Fonts.SF_SEMIBOLD.getHeight(s.getName(), fontSize) + height - (18 + Fonts.SF_MEDIUM.getHeight(s.getName(), fontSize)), mouseY));

                //if (isHovered(mouseX, mouseY, x + 7, y + 12 + Fonts.SF_SEMIBOLD.getHeight(s.getName(), fontSize), width - 14, height - (18 + Fonts.SF_MEDIUM.getHeight(s.getName(), fontSize)))) {

                dragX += (mouseX - startX);
                dragY += mouseY - startY;
                //}

                dragX = Math.max(0, Math.min(width - 17f, dragX));
                dragY = Math.max(0, Math.min(dragY, height - (20 + Fonts.SF_MEDIUM.getHeight(s.getName(), fontSize))));

                s.set(new Vector2f((dragXV / (width - 18) * mc.getMainWindow().getScaledWidth()), (dragYV / (height - (18 + Fonts.SF_MEDIUM.getHeight(s.getName(), fontSize)))) * mc.getMainWindow().getScaledHeight()));

            }

            dragXV = Mathf.fast(dragXV, dragX, 16);
            dragYV = Mathf.fast(dragYV, dragY, 16);

            RenderUtil.Rounded.roundedRect(stack, offsetX, offsetY, width - 14, height - (18 + Fonts.SF_MEDIUM.getHeight(s.getName(), fontSize)), ColorUtil.getColor(26, 30, 35, alpha), Round.of(2));

            final int lines = 9;
            for (int i = 1; i <= lines; i++) {
                RenderUtil.Rounded.roundedRect(stack, offsetX + 2, offsetY + 1 + (i * (14)), width - 18, 1, ColorUtil.getColor(5, 9, 14, alpha), Round.of(1));
                for (int j = 1; j <= lines; j++) {
                    RenderUtil.Rounded.roundedRect(stack, offsetX + 1 + (j * 13), offsetY + 2, 1, height - (22 + Fonts.SF_MEDIUM.getHeight(s.getName(), fontSize)), ColorUtil.getColor(5, 9, 14, alpha), Round.of(1));
                }
            }

            RenderUtil.Rounded.roundedRect(stack, offsetX + Math.max(dragXV, 2), offsetY + 2, 1f, height - (22 + Fonts.SF_MEDIUM.getHeight(s.getName(), fontSize)), ColorUtil.getColor(164, 168, 174, alpha), Round.of(0));
            RenderUtil.Rounded.roundedRect(stack, offsetX + 2, offsetY + Math.max(dragYV, 2), width - 18, 1f, ColorUtil.getColor(164, 168, 174, alpha), Round.of(0));
        } else if (setting instanceof ColorSetting s) {
            RenderUtil.Rounded.roundedRect(stack, x + width - 14, y + height / 2f - 3.5f, 7, 7, ColorUtil.multAlpha(s.getValue(), alpha), Round.of(5));
            RenderUtil.Rounded.roundedOutline(stack, x + width - 15.5f, y + height / 2f - 5, 10, 10, 1, ColorUtil.multAlpha(s.getValue(), alpha * .5f), Round.of(5));
        } else if (setting instanceof StringSetting s) {

            if (System.currentTimeMillis() - time >= 700L) {
                interpolates.set(0, selected && interpolates.get(0) == 0 ? 1F : 0F);
                time = System.currentTimeMillis();
            }

            selectedV = Mathf.fast(selectedV, interpolates.get(0), 16);

            final float offsetX = x + 7, offsetY = y + 12 + Fonts.SF_MEDIUM.getHeight(s.getName(), fontSize);
            RenderUtil.Rounded.roundedRect(stack, offsetX, offsetY, width - 14, 10, ColorUtil.getColor(26, 28, 32, alpha), Round.of(3));
            ScissorExp.push();
            ScissorExp.setFromComponentCoordinates(offsetX, offsetY, width - 14, 10);
            Fonts.SF_MEDIUM.draw(stack, selected || !s.getValue().isEmpty() ? s.getValue() : s.getDescription(), offsetX + 2, offsetY + .5f, ColorUtil.getColor(254, 254, 254, alpha * .3f), fontSize);
            RenderUtil.Rounded.roundedRect(stack, offsetX + 4 + Fonts.SF_MEDIUM.getWidth(s.getValue(), fontSize), offsetY + 5 - selectedV * 2.5f, 1, selectedV * 5, ColorUtil.getColor(254, 254, 254, alpha * .3f), Round.of(0));
            ScissorExp.unset();
            ScissorExp.pop();
        } else if (setting instanceof BindSetting s) {

            final String text = selected ? "..." : s.getValue() <= 0 ? "n/a" : Keyboard.keyName(s.getValue());
            selectedV = Mathf.fast(selectedV, Fonts.SF_MEDIUM.getWidth(text, fontSize) + 4, 16);

            RenderUtil.Rounded.roundedRect(stack, x + width - 7 - selectedV, y + 6, selectedV, Fonts.SF_MEDIUM.getHeight(text, fontSize) + 2, ColorUtil.getColor(26, 28, 32, alpha), Round.of(3));
            ScissorExp.push();
            ScissorExp.setFromComponentCoordinates(x + width - 7 - selectedV, y + 6, selectedV, Fonts.SF_MEDIUM.getHeight(text, fontSize) + 2);
            Fonts.SF_MEDIUM.draw(stack, text, x + width - 5 - selectedV, y + 6, ColorUtil.getColor(168, 170, 176, alpha), fontSize);
            ScissorExp.unset();
            ScissorExp.pop();
        } else if (setting instanceof ItemSetting s) {
            RectUtil.drawItemStack(s.getValue().getDefaultInstance(), x + width - 7 - 8, y + height / 2f - 4, false, 8, 8);

            selectedV = Mathf.fast(selectedV, selected ? 1 : 0, 16);
        } else if (setting instanceof MultiItemSetting s) {
            selectedV = Mathf.fast(selectedV, selected ? 1 : 0, 16);
        } else if (setting instanceof ListSetting<?> s) {
            float offsetX = x + 7, offsetY = y + 12 + Fonts.SF_MEDIUM.getHeight(s.getName(), fontSize);
            int iteration = 0;
            for (Object object : s.values) {
                final String string = object.toString();
                if (offsetX + 6 + Fonts.SF_MEDIUM.getWidth(string, fontSize) >= x + width - 14 && offsetX != x + 7) {
                    offsetX = x + 7;
                    offsetY += 12;
                }

                interpolates.set(iteration, Mathf.fast(interpolates.get(iteration), s.getValue().equals(object) ? 1 : 0, 16));
                final float interpolate = interpolates.get(iteration);
                final int leftGradient = ColorUtil.fade(7, (id * 60), theme.clientColor(), theme.darkColor()),
                        rightGradient = ColorUtil.fade(7, 30 + (id * 60), theme.clientColor(), theme.darkColor());
                final int leftColor = ColorUtil.multAlpha(ColorUtil.overCol(ColorUtil.getColor(26, 28, 32), leftGradient, interpolate), alpha),
                        rightColor = ColorUtil.multAlpha(ColorUtil.overCol(ColorUtil.getColor(26, 28, 32), rightGradient, interpolate), alpha);


                RenderUtil.Rounded.roundedRect(stack, offsetX, offsetY, Fonts.SF_MEDIUM.getWidth(string, fontSize) + 4, 10, leftColor, leftColor, rightColor, rightColor, Round.of(3));
                Fonts.SF_MEDIUM.draw(stack, string, offsetX + 2, offsetY + 1, ColorUtil.getColor(254, 254, 254, (.36f + interpolate * .56f) * alpha), fontSize);

                if (offsetX + 2 + Fonts.SF_MEDIUM.getWidth(string, fontSize) >= x + width - 14 && offsetX != x + 7) {
                    offsetX = x + 7;
                    offsetY += 12;
                } else if (offsetX + 6 + Fonts.SF_MEDIUM.getWidth(string, fontSize) < x + width - 14) {
                    offsetX += 6 + Fonts.SF_MEDIUM.getWidth(string, fontSize);
                }
                iteration++;
            }
        }
    }

    public void renderWithoutScissor(MatrixStack stack, float mouseX, float mouseY, float alpha) {
        final float y = this.y + delta;
        if (setting instanceof ColorSetting s) {


            if (colorPicker != null) {
                selectedV = Mathf.fast(selectedV, selected && inRange(colorPicker.getY(), colorPicker.getHeight(), module.category().gui().y() + 40, module.category().gui().height() - 40) ? 1 : 0, 16);
                colorPicker.setScaleV(selectedV * alpha);

                if (selectedV >= .1f) {
                    colorPicker.render(stack, (int) mouseX, (int) mouseY);
                }

                colorPicker.setX(x + width - 2);
                colorPicker.setY(y - colorPicker.getHeight() + 2);
            }
        } else if (itemSetting != null && selectedV >= .1f) {
            itemSetting.render(stack, mouseX, mouseY, selectedV * alpha);
            itemSetting.y(this.y + delta - itemSetting.height() + 2);
            itemSetting.x(this.x + width - 2);
        }
        startY = mouseY;
        startX = mouseX;
    }

    @Override
    void mouse(float mouseX, float mouseY, int button, boolean action) {
        if (visibleV <= .1f) return;
        final float y = this.y + delta;
        for (Setting s : module.settings()) {
            if (s != this && (s.setting() instanceof ColorSetting && s.selectedV() >= .1f && s.colorPicker() != null && isHovered(mouseX, mouseY, s.colorPicker().getX(), s.colorPicker().getY(), s.colorPicker().getWidth(), s.colorPicker().getHeight()) ||
                    (s.itemSetting() != null && s.selected() && isHovered(mouseX, mouseY, s.itemSetting().x(), s.itemSetting().y(), s.itemSetting().width(), s.itemSetting().height())))) {
                return;
            }
        }
        if (isHovered(mouseX, mouseY, x, y, width, height)) {
            if (action) {
                if (button == 0) {
                    if (setting instanceof BooleanSetting s)
                        s.set(!s.getValue());
                    else if (setting instanceof SliderSetting)
                        selected = true;
                    else if (setting instanceof ModeSetting s) {
                        float offsetX = x + 7, offsetY = y + 12 + Fonts.SF_MEDIUM.getHeight(s.getName(), fontSize);
                        for (String string : s.values) {
                            if (offsetX + 6 + Fonts.SF_MEDIUM.getWidth(string, fontSize) >= x + width - 14 && offsetX != x + 7) {
                                offsetX = x + 7;
                                offsetY += 12;
                            }

                            if (isHovered(mouseX, mouseY, offsetX, offsetY, Fonts.SF_MEDIUM.getWidth(string, fontSize) + 4, 10)) {
                                s.set(string);
                                break;
                            }

                            if (offsetX + 2 + Fonts.SF_MEDIUM.getWidth(string, fontSize) >= x + width - 14 && offsetX != x + 7) {
                                offsetX = x + 7;
                                offsetY += 12;
                            } else if (offsetX + 6 + Fonts.SF_MEDIUM.getWidth(string, fontSize) < x + width - 14) {
                                offsetX += 6 + Fonts.SF_MEDIUM.getWidth(string, fontSize);
                            }
                        }
                    } else if (setting instanceof MultiBooleanSetting s) {
                        float offsetX = x + 7, offsetY = y + 12 + Fonts.SF_MEDIUM.getHeight(s.getName(), fontSize);
                        for (BooleanSetting string : s.getValues()) {
                            if (offsetX + 6 + Fonts.SF_MEDIUM.getWidth(string.getName(), fontSize) >= x + width - 14 && offsetX != x + 7) {
                                offsetX = x + 7;
                                offsetY += 12;
                            }

                            if (isHovered(mouseX, mouseY, offsetX, offsetY, Fonts.SF_MEDIUM.getWidth(string.getName(), fontSize) + 4, 10)) {
                                string.set(!string.getValue());
                                break;
                            }

                            if (offsetX + 2 + Fonts.SF_MEDIUM.getWidth(string.getName(), fontSize) >= x + width - 14 && offsetX != x + 7) {
                                offsetX = x + 7;
                                offsetY += 12;
                            } else if (offsetX + 6 + Fonts.SF_MEDIUM.getWidth(string.getName(), fontSize) < x + width - 14) {
                                offsetX += 6 + Fonts.SF_MEDIUM.getWidth(string.getName(), fontSize);
                            }
                        }
                    } else if (setting instanceof ListSetting<?> s) {
                        float offsetX = x + 7, offsetY = y + 12 + Fonts.SF_MEDIUM.getHeight(s.getName(), fontSize);
                        for (Object object : s.values) {
                            final String string = object.toString();
                            if (offsetX + 6 + Fonts.SF_MEDIUM.getWidth(string, fontSize) >= x + width - 14 && offsetX != x + 7) {
                                offsetX = x + 7;
                                offsetY += 12;
                            }

                            if (isHovered(mouseX, mouseY, offsetX, offsetY, Fonts.SF_MEDIUM.getWidth(string, fontSize) + 4, 10)) {
                                s.setAsObject(object);
                                break;
                            }

                            if (offsetX + 2 + Fonts.SF_MEDIUM.getWidth(string, fontSize) >= x + width - 14 && offsetX != x + 7) {
                                offsetX = x + 7;
                                offsetY += 12;
                            } else if (offsetX + 6 + Fonts.SF_MEDIUM.getWidth(string, fontSize) < x + width - 14) {
                                offsetX += 6 + Fonts.SF_MEDIUM.getWidth(string, fontSize);
                            }
                        }
                    } else if (setting instanceof DragSetting s && isHovered(mouseX, mouseY, x + 7, y + 12 + Fonts.SF_SEMIBOLD.getHeight(s.getName(), fontSize), width - 14, height - (18 + Fonts.SF_MEDIUM.getHeight(s.getName(), fontSize)))) {
                        selected = true;

                        dragX = mouseX - (x + 7);
                        dragY = mouseY - (y + 12 + Fonts.SF_SEMIBOLD.getHeight(s.getName(), fontSize));

                        startX = mouseX;
                        startY = mouseY;
                    } else if (setting instanceof ColorSetting || setting instanceof StringSetting || setting instanceof BindSetting || setting instanceof ItemSetting || setting instanceof MultiItemSetting) {
                        selected = !selected;
                    }
                }
            }
        }
        if ((setting instanceof SliderSetting || setting instanceof DragSetting) && !action)
            selected = false;

        else if (setting instanceof ColorSetting && colorPicker != null) {
            if (action)
                colorPicker.mouseClicked(mouseX, mouseY, button);
            else
                colorPicker.mouseReleased(mouseX, mouseY, button);
        } else if (itemSetting != null && selected) {
            itemSetting.mouse(mouseX, mouseY, button, action);
        }
    }

    @Override
    void key(int key) {
        if (selected) {
            if (setting instanceof StringSetting s) {
                if (key == GLFW.GLFW_KEY_ESCAPE || key == GLFW.GLFW_KEY_ENTER)
                    selected = false;
                else if (key == GLFW.GLFW_KEY_BACKSPACE) {
                    s.set(s.getValue().isEmpty() ? "" : s.getValue().substring(0, s.getValue().length() - 1));
                }
            } else if (setting instanceof BindSetting s) {
                s.set(key == GLFW.GLFW_KEY_DELETE || key == GLFW.GLFW_KEY_BACKSPACE || key == GLFW.GLFW_KEY_ESCAPE ? -1 : key);
                selected = false;
                return;
            } else if (itemSetting != null)
                itemSetting.key(key);
        }
        if (setting instanceof StringSetting s && isHovered(startX, startY, x + 7, y + delta + 12 + Fonts.SF_MEDIUM.getHeight(s.getName(), fontSize), width - 14, 10) && key == GLFW.GLFW_KEY_DELETE) {
            selected = false;
            s.set("");
        }
    }

    @Override
    void charTyped(char typed) {
        if (setting instanceof StringSetting s && selected) {
            s.set(s.getValue() + typed);
        } else if (selected && itemSetting != null)
            itemSetting.charTyped(typed);
    }
}

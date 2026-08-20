package org.privates.client.screen.flatgui.impl;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.client.Minecraft;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.api.annotations.Beta;
import org.privates.client.api.annotations.Funtime;
import org.privates.client.api.annotations.HolyWorld;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.settings.Setting;
import org.privates.client.managers.module.settings.impl.*;
import org.privates.client.screen.flatgui.AbstractPanel;
import org.privates.client.screen.flatgui.FlatGuiScreen;
import org.privates.client.screen.flatgui.impl.components.*;
import org.privates.client.utils.animation.Animation;
import org.privates.client.utils.animation.util.Easings;
import org.privates.client.utils.keyboard.Keyboard;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.font.Fonts;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Accessors(fluent = true)
public class FlatModuleComponent extends AbstractPanel {
    private final Module module;
    private final FlatGuiScreen flatGuiScreen;
    private final List<AbstractPanel> settingComponents = new ArrayList<>();
    private boolean expanded = false;
    private boolean binding = false;
    private final float fontSize = 7F;
    private final Animation circleAlphaAnimation = new Animation();

    public FlatModuleComponent(Module module, FlatGuiScreen flatGuiScreen) {
        this.module = module;
        this.flatGuiScreen = flatGuiScreen;
        size().set(100F, 20F);
        initComponents();
    }

    private void initComponents() {
        for (Setting<?> setting : module.getSettings()) {
            if (setting instanceof BindSetting) {
                settingComponents.add(new FlatBindSettingComponent((BindSetting) setting));
            } else if (setting instanceof BooleanSetting) {
                settingComponents.add(new FlatBooleanSettingComponent((BooleanSetting) setting));
            } else if (setting instanceof ColorSetting) {
                settingComponents.add(new FlatColorSettingComponent((ColorSetting) setting));
            } else if (setting instanceof DelimiterSetting) {
                settingComponents.add(new FlatDelimiterSettingComponent((DelimiterSetting) setting));
            } else if (setting instanceof ListSetting) {
                settingComponents.add(new FlatListSettingComponent((ListSetting<?>) setting));
            } else if (setting instanceof ModeSetting) {
                settingComponents.add(new FlatModeSettingComponent((ModeSetting) setting));
            } else if (setting instanceof MultiBooleanSetting) {
                settingComponents.add(new FlatMultiBooleanSettingComponent((MultiBooleanSetting) setting));
            } else if (setting instanceof SliderSetting) {
                settingComponents.add(new FlatSliderSettingComponent((SliderSetting) setting));
            } else if (setting instanceof StringSetting) {
                settingComponents.add(new FlatStringSettingComponent((StringSetting) setting));
            }
        }
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        settingComponents.forEach(component -> component.resize(minecraft, width, height));
    }

    @Override
    public void init() {
        settingComponents.forEach(AbstractPanel::init);
    }

    public int backgroundColor() {
        return ColorUtil.getColor(20, 20, 28, alphaPC() / 1.5F);
    }

    public float alphaPC() {
        return Mathf.clamp01(255);
    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        boolean isHover = hover(mouseX, mouseY);
        boolean isSelected = flatGuiScreen.selectedModule() == module;

        if (isSelected) {
            RenderUtil.Rounded.smooth(matrix, position().x, position().y, size().x, size().y, ColorUtil.getColor(30, 30, 38, alpha()), Round.of(4));
        }
        int settingBackground = ColorUtil.multAlpha(backgroundColor(), 1F);
        RenderUtil.Rounded.smooth(matrix, position().x, position().y, size().x, size().y, settingBackground, Round.of(4));

        String displayText = binding ? "Binding..." : module.getName();
        int color = module.isEnabled() ? ColorUtil.multAlpha(-1, alpha()) : ColorUtil.getColor(128, 128, 128, alpha());
        float textX = position().x + 5;
        Fonts.SF_MEDIUM.draw(matrix, displayText, textX, position().y + (size().y / 2F) - (fontSize / 2F), color, fontSize);

        float annotationX = textX + Fonts.SF_MEDIUM.getWidth(displayText, fontSize) + 3;
        if (module.getClass().isAnnotationPresent(Beta.class)) {
            Fonts.SF_MEDIUM.draw(matrix, "BETA", annotationX, position().y + (size().y / 2F) - (5F / 2F), ColorUtil.getColor(0, 128, 255, alpha()), 5F);
        } else if (module.getClass().isAnnotationPresent(Funtime.class)) {
            Fonts.SF_MEDIUM.draw(matrix, "FT", annotationX, position().y + (size().y / 2F) - (5F / 2F), ColorUtil.getColor(0, 128, 255, alpha()), 5F);
        } else if (module.getClass().isAnnotationPresent(HolyWorld.class)) {
            Fonts.SF_MEDIUM.draw(matrix, "HW", annotationX, position().y + (size().y / 2F) - (5F / 2F), ColorUtil.getColor(0, 128, 255, alpha()), 5F);
        }

        circleAlphaAnimation.update();
        circleAlphaAnimation.run(module.isEnabled() ? 255 : 0, 0.25, Easings.BACK_OUT, true);
        float circleAlpha = circleAlphaAnimation.get();
        if (circleAlpha > 0) {
            RenderUtil.Rounded.smooth(matrix, position().x + size().x - 10, position().y + (size().y / 2F) - (5F / 2F), 5F, 5F, ColorUtil.getColor(0, 255, 0, circleAlpha), Round.of(2.5F));
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (hover(mouseX, mouseY)) {
            if (button == 0) {
                module.toggle();
                return true;
            }
            if (button == 1) {
                for (FlatModuleComponent component : flatGuiScreen.moduleListWidget().moduleComponents()) {
                    component.expanded(component == this);
                }
                flatGuiScreen.setSelectedModule(module);
                expanded = true;
                return true;
            }
            if (button == 2) {
                binding = !binding;
                return true;
            }
        }

        if (expanded && flatGuiScreen.selectedModule() == module) {
            for (AbstractPanel component : settingComponents) {
                if (component.visible() && component.mouseClicked(mouseX, mouseY, button)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (expanded && flatGuiScreen.selectedModule() == module) {
            for (AbstractPanel component : settingComponents) {
                if (component.visible() && component.mouseReleased(mouseX, mouseY, button)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (binding) {
            if (keyCode == Keyboard.KEY_ESCAPE.getKey() || keyCode == Keyboard.KEY_DELETE.getKey()) {
                module.setKey(Keyboard.KEY_NONE.getKey());
                binding = false;
            } else if (keyCode >= 0 && keyCode <= 348) {
                module.setKey(keyCode);
                binding = false;
            }
            return true;
        }

        if (expanded && flatGuiScreen.selectedModule() == module) {
            for (AbstractPanel component : settingComponents) {
                if (component.visible() && component.keyPressed(keyCode, scanCode, modifiers)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (expanded && flatGuiScreen.selectedModule() == module) {
            for (AbstractPanel component : settingComponents) {
                if (component.visible() && component.keyReleased(keyCode, scanCode, modifiers)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (expanded && flatGuiScreen.selectedModule() == module) {
            for (AbstractPanel component : settingComponents) {
                if (component.visible() && component.charTyped(codePoint, modifiers)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onClose() {
        binding = false;
        settingComponents.forEach(AbstractPanel::onClose);
    }
}
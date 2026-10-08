package never.lose.ui.component;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;
import never.lose.modules.api.Module;
import never.lose.modules.api.setting.BindSetting;
import never.lose.modules.api.setting.BooleanSetting;
import never.lose.modules.api.setting.ColorSetting;
import never.lose.modules.api.setting.ItemModelSetting;
import never.lose.modules.api.setting.ModeListSetting;
import never.lose.modules.api.setting.ModeSetting;
import never.lose.modules.api.setting.Setting;
import never.lose.modules.api.setting.SliderSetting;
import never.lose.modules.api.setting.StringSetting;
import never.lose.modules.api.setting.ThemeSetting;
import never.lose.ui.component.impl.BindComponent;
import never.lose.ui.component.impl.BooleanComponent;
import never.lose.ui.component.impl.ColorPickerComponent;
import never.lose.ui.component.impl.ItemModelComponent;
import never.lose.ui.component.impl.ModeComponent;
import never.lose.ui.component.impl.ModeListComponent;
import never.lose.ui.component.impl.SliderComponent;
import never.lose.ui.component.impl.StringComponent;
import never.lose.ui.component.impl.ThemeComponent;
import never.lose.ui.layout.Panel;
import never.lose.utils.animation.Animation;
import never.lose.utils.animation.Easing;
import never.lose.utils.font.Fonts;
import never.lose.utils.input.CursorManager;
import never.lose.utils.render.Draw;
import never.lose.utils.render.Hover;
import never.lose.utils.render.Scissor;
import never.lose.utils.render.providers.ColorProvider;

import java.util.ArrayList;
import java.util.List;

public class ModuleComponent extends Component {

    private final Module module;
    private final Panel panel;

    private final Animation animation = new Animation(Easing.QUINTIC_OUT, 320);
    private final Animation hoverAnim = new Animation(Easing.QUINTIC_OUT, 300);
    private final Animation enabledAnim = new Animation(Easing.QUINTIC_OUT, 400);

    public boolean open;
    private boolean isHovered;
    private boolean binding;

    private final List<Component> components = new ArrayList<>();

    private static float cachedSettingsIconWidth = -1f;

    public ModuleComponent(Module module, Panel panel) {
        this.module = module;
        this.panel = panel;
        for (Setting setting : module.getSettings()) {
            if (setting instanceof BooleanSetting option) {
                components.add(new BooleanComponent(option));
            } else if (setting instanceof ItemModelSetting option) {
                components.add(new ItemModelComponent(option, panel::openItemModelGallery));
            } else if (setting instanceof ModeSetting option) {
                components.add(new ModeComponent(option));
            } else if (setting instanceof ModeListSetting option) {
                components.add(new ModeListComponent(option));
            } else if (setting instanceof SliderSetting option) {
                components.add(new SliderComponent(option));
            } else if (setting instanceof BindSetting option) {
                components.add(new BindComponent(option));
            } else if (setting instanceof ThemeSetting option) {
                components.add(new ThemeComponent(option));
            } else if (setting instanceof ColorSetting option) {
                components.add(new ColorPickerComponent(option));
            } else if (setting instanceof StringSetting option) {
                components.add(new StringComponent(option));
            }
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float partialTicks) {
        isHovered = Hover.isHovered(mouseX, mouseY, x, y, width, 19);

        hoverAnim.run(isHovered);
        animation.run(open);
        enabledAnim.run(module.isEnabled());

        if (Hover.isHovered(mouseX, mouseY, x, y, width, 19)) CursorManager.requestHand();

        float alpha = Math.max(Math.min(panel.getAnimationAlpha().getValue(), 1), 0);

        int textColor = ColorProvider.interpolateColor(
                ColorProvider.setAlpha(ColorProvider.getColorInactiveText(), (int) (255 * alpha)),
                ColorProvider.setAlpha(ColorProvider.getColorText(), (int) (255 * alpha)),
                enabledAnim.getValue()
        );

        int innerColor = ColorProvider.interpolateColor(
                ColorProvider.setAlpha(ColorProvider.getColorMain(), (int) (28 * alpha)),
                ColorProvider.setAlpha(ColorProvider.getColorVisualModules(), (int) (22 * alpha)),
                enabledAnim.getValue()
        );

        float currentHeight = 19f + ((height - 19f) * animation.getValue());

        float visTop = panel.getY() + 25f;
        float visBottom = panel.getY() + panel.getHeight() - 4f;
        if (y + currentHeight < visTop || y > visBottom) {
            isHovered = false;
            return;
        }

        Draw.blur(x, y, width, currentHeight - 0.5f, 3f, ColorProvider.rgba(200, 200, 200, (int) (255 * alpha)), 10f);
        Draw.round(x - 1f, y - 1f, width + 2f, currentHeight + 1f, 3.5f, ColorProvider.rgba(48, 66, 122, (int) (55 * alpha)));
        Draw.round(x, y, width, currentHeight - 0.5f, 3f, innerColor);

        if (binding) {
            Draw.text(Fonts.REGULAR, "Press a key...",
                    x + width / 2f - Draw.width(Fonts.REGULAR, "Press a key...", 7.5f) / 2f,
                    y + 5.75f, ColorProvider.rgba(255, 255, 255, (int) (255 * alpha)), 7.5f);
        } else {
            float textY = y + 5.75f;

            if (!components.isEmpty()) {
                String icon = "{/}";
                if (cachedSettingsIconWidth < 0f) {
                    cachedSettingsIconWidth = Draw.width(Fonts.REGULAR, icon, 7.5f);
                }
                float iconWidth = cachedSettingsIconWidth;

                Draw.text(Fonts.REGULAR, icon, x + 3f, textY,
                        ColorProvider.setAlpha(ColorProvider.getColorInactiveText(), (int) (180 * alpha)), 7.5f);
                Draw.text(Fonts.REGULAR, " " + module.getName(), x + 3f + iconWidth, textY, textColor, 7.5f);
            } else {
                Draw.text(Fonts.REGULAR, module.getName(), x + 3f, textY, textColor, 7.5f);
            }

            float toggleW = 20f;
            float toggleH = 10f;
            float toggleX = x + width - toggleW - 4f;
            float toggleY = y + (19f - toggleH) / 2f;

            int toggleBgColor = ColorProvider.interpolateColor(
                    ColorProvider.rgba(40, 52, 92, (int) (150 * alpha)),
                    ColorProvider.setAlpha(ColorProvider.getColorVisualModules(), (int) (200 * alpha)),
                    enabledAnim.getValue()
            );
            Draw.round(toggleX, toggleY, toggleW, toggleH, toggleH / 2f, toggleBgColor);

            float circleSize = toggleH - 2.5f;
            float circleX = toggleX + 1.25f + ((toggleW - circleSize - 2.5f) * enabledAnim.getValue());
            float circleY = toggleY + 1.25f;
            Draw.round(circleX, circleY, circleSize, circleSize, circleSize / 2f,
                    ColorProvider.rgba(255, 255, 255, (int) (255 * alpha)));
        }

        if (animation.getValue() > 0.01f) {
            float compY = y + 22f;
            float panelTop = panel.getY() + 25;
            float panelBottom = panel.getY() + panel.getHeight() - 4;

            float boxBottom = y + currentHeight;
            float intersectY = Math.max(y + 19, panelTop);
            float intersectBottom = Math.min(boxBottom, panelBottom);
            float intersectHeight = Math.max(0, intersectBottom - intersectY);

            float darkHeight = currentHeight - 19f;
            if (darkHeight > 0) {
                Draw.round(x + 1f, y + 19, width - 2f, darkHeight, 0f, ColorProvider.rgba(0, 0, 0, (int) (30 * alpha * animation.getValue())));
            }

            for (Component component : components) {
                component.getAlphaAnim().setValue(Math.min(panel.getAnimationAlpha().getValue(), 1) * animation.getValue());
                component.getAlphaAnimSetting().run(component.isVisible());

                float visibleProgress = MathHelper.clamp(component.getAlphaAnimSetting().getValue(), 0f, 1f);
                if (component.isVisible() || visibleProgress > 0) {
                    component.setX(x);
                    component.setY(compY);
                    component.setWidth(width - 4);

                    Scissor.push();
                    Scissor.setFromComponentCoordinates(x, intersectY, width, intersectHeight);

                    component.render(context, mouseX, mouseY, partialTicks);

                    Scissor.unset();
                    Scissor.pop();

                    compY += component.getHeight() * visibleProgress;
                }
            }
        }
    }

    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (isHovered(mouseX, mouseY, 19)) {
            if (button == 0) module.setEnabled(!module.isEnabled());
            if (button == 1 && !components.isEmpty()) {
                open = !open;
                if (!open) {
                    unfocusText();
                }
            }
            if (button == 2) binding = !binding;
        }

        if (open) {
            for (Component component : components) {
                if (component.isVisible() && component.getAlphaAnimSetting().getValue() > 0.5f) {
                    component.mouseClicked(mouseX, mouseY, button);
                }
            }
        }
    }

    public void mouseReleased(double mouseX, double mouseY, int button) {
        if (open) {
            for (Component component : components) {
                component.mouseReleased(mouseX, mouseY, button);
            }
        }
    }

    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        if (binding) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_DELETE) {
                module.setKey(-1);
            } else {
                module.setKey(keyCode);
            }
            binding = false;
        }

        if (open) {
            for (Component component : components) {
                component.keyPressed(keyCode, scanCode, modifiers);
            }
        }
    }

    public void charTyped(char chr, int modifiers) {
        if (!open) {
            return;
        }
        for (Component component : components) {
            if (component.isVisible()) {
                component.charTyped(chr, modifiers);
            }
        }
    }

    public boolean isTextFocused() {
        if (!open) {
            return false;
        }
        for (Component component : components) {
            if (component instanceof StringComponent stringComponent && stringComponent.isFocused()) {
                return true;
            }
        }
        return false;
    }

    public void unfocusText() {
        for (Component component : components) {
            if (component instanceof StringComponent stringComponent) {
                stringComponent.setFocused(false);
            }
        }
    }

    public boolean isBinding() {
        return binding;
    }

    public boolean isHovered() {
        return isHovered;
    }

    public Module getModule() {
        return module;
    }

    public List<Component> getComponents() {
        return components;
    }

    public Animation getAnimation() {
        return animation;
    }

    private boolean isHovered(double mouseX, double mouseY, float heightCheck) {
        return Hover.isHovered(mouseX, mouseY, x, y, width, heightCheck);
    }
}

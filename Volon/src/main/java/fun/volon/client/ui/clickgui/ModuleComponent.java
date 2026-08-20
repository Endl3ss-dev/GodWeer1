package fun.volon.client.ui.clickgui;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;
import fun.volon.api.utils.animation.gui.GuiAnimation;
import fun.volon.api.utils.animation.gui.GuiEasing;
import fun.volon.api.utils.color.ColorUtils;
import fun.volon.api.utils.input.CursorManager;
import fun.volon.api.utils.math.HoveringUtils;
import fun.volon.api.utils.render.RenderUtils;
import fun.volon.api.utils.render.fonts.msdf.Font;
import fun.volon.api.utils.render.fonts.msdf.Fonts;
import fun.volon.api.utils.scissor.ScissorUtils;
import fun.volon.client.modules.Module;
import fun.volon.client.modules.settings.Setting;
import fun.volon.client.modules.settings.implement.BindSetting;
import fun.volon.client.modules.settings.implement.BooleanSetting;
import fun.volon.client.modules.settings.implement.ColorSetting;
import fun.volon.client.modules.settings.implement.FloatSetting;
import fun.volon.client.modules.settings.implement.ListSetting;
import fun.volon.client.modules.settings.implement.ModeSetting;
import fun.volon.client.modules.settings.implement.TextSetting;
import fun.volon.client.modules.settings.implement.ThemeSetting;

@Getter
public class ModuleComponent extends Component {
    private final Module module;
    private final Panel panel;

    private final GuiAnimation animation = new GuiAnimation(GuiEasing.QUINTIC_OUT, 320);
    private final GuiAnimation hoverAnim = new GuiAnimation(GuiEasing.QUINTIC_OUT, 300);
    private final GuiAnimation enabledAnim = new GuiAnimation(GuiEasing.QUINTIC_OUT, 400);

    public boolean open;
    private boolean isHovered;
    private boolean binding;

    private final ObjectArrayList<Component> components = new ObjectArrayList<>();

    public ModuleComponent(Module module, Panel panel) {
        this.module = module;
        this.panel = panel;
        for (Setting setting : module.getSettings()) {
            if (setting instanceof BooleanSetting bs) components.add(new BooleanComponent(bs));
            else if (setting instanceof FloatSetting fs) components.add(new SliderComponent(fs));
            else if (setting instanceof ModeSetting ms) components.add(new ModeComponent(ms));
            else if (setting instanceof ListSetting ls) components.add(new ListComponent(ls));
            else if (setting instanceof BindSetting bds) components.add(new BindComponent(bds));
            else if (setting instanceof ColorSetting cs) components.add(new ColorPickerComponent(cs));
            else if (setting instanceof ThemeSetting ts) components.add(new ThemeComponent(ts));
            else if (setting instanceof TextSetting txt) components.add(new TextComponent(txt));
        }

        if ("Interface".equalsIgnoreCase(module.getName())) {
            components.add(new ThemeActionComponent(this));
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
        isHovered = HoveringUtils.isHovered(mouseX, mouseY, x, y, width, 19);

        hoverAnim.run(isHovered);
        animation.run(open);
        enabledAnim.run(module.isEnable());

        if (isHovered) CursorManager.requestHand();

        float alpha = Math.max(Math.min(panel.getAnimationAlpha().getValue(), 1), 0);

        int textColor = ColorUtils.interpolateColor(
                ColorUtils.setAlphaColor(ColorUtils.getColorInactiveText(), (int) (255 * alpha)),
                ColorUtils.setAlphaColor(ColorUtils.getColorText(), (int) (255 * alpha)),
                enabledAnim.getValue()
        );

        int innerColor = ColorUtils.interpolateColor(
                ColorUtils.setAlphaColor(ColorUtils.getColorMain(), (int) (70 * alpha)),
                ColorUtils.setAlphaColor(ColorUtils.getColorVisualModules(), (int) (50 * alpha)),
                enabledAnim.getValue()
        );

        float currentHeight = 19f + ((height - 19f) * animation.getValue());

        RenderUtils.drawRoundedRect(matrices, x, y, width, currentHeight - 0.5f, 3f, innerColor);

        Font nameFont = Fonts.getFont("sf_regular", 15);
        Font arrowFont = Fonts.getFont("divine", 15);
        if (nameFont != null) {
            if (binding) {
                String bindingText = "Нажмите клавишу...";
                float tw = nameFont.getStringWidth(bindingText);
                drawText(matrices, nameFont, bindingText, x + width / 2f - tw / 2f, y + 5.75f,
                        ColorUtils.rgba(255, 255, 255, (int) (255 * alpha)));
            } else {
                float arrowReserve = components.isEmpty() ? 4.5f : 12f;
                float nameMaxWidth = width - 4.5f - arrowReserve - 1f;
                drawTextScrolling(matrices, nameFont, module.getDisplayName(),
                        x + 4.5f, y + 5.25f, nameMaxWidth, textColor);

                if (!components.isEmpty() && arrowFont != null) {
                    drawText(matrices, arrowFont, "h", x + width - 12, y + 5.75f, textColor);
                }
            }
        }

        if (animation.getValue() > 0.01f) {
            float compY = y + 17.5f;
            float panelTop = panel.getY() + 20;
            float panelBottom = panel.getY() + panel.getHeight() - 4;
            float settingsY = y + 19;
            float settingsBottom = y + currentHeight;

            float intersectY = Math.max(settingsY, panelTop);
            float intersectBottom = Math.min(settingsBottom, panelBottom);
            float intersectHeight = Math.max(0, intersectBottom - intersectY);

            float darkHeight = currentHeight - 19f;
            if (darkHeight > 0) {
                RenderUtils.drawRoundedRect(matrices, x + 1f, y + 19, width - 2f, darkHeight, 0f,
                        ColorUtils.rgba(0, 0, 0, (int) (30 * alpha * animation.getValue())));
            }

            for (Component component : components) {
                component.getAlphaAnim().setValue(Math.min(panel.getAnimationAlpha().getValue(), 1));
                component.getAlphaAnimSetting().run(component.isVisible());

                float visibleProgress = MathHelper.clamp(component.getAlphaAnimSetting().getValue(), 0f, 1f);
                if (component.isVisible() || visibleProgress > 0) {
                    component.setX(x);
                    component.setY(compY);
                    component.setWidth(width - 4);

                    ScissorUtils.push();
                    ScissorUtils.setFromComponentCoordinates(x, intersectY, width, intersectHeight);

                    component.render(matrices, mouseX, mouseY, partialTicks);

                    ScissorUtils.pop();

                    compY += component.getHeight() * visibleProgress;
                }
            }
        }
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (isHovered(mouseX, mouseY, 19)) {
            if (button == 0) module.toggle();
            if (button == 1 && !components.isEmpty()) open = !open;
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

    @Override
    public void mouseReleased(double mouseX, double mouseY, int button) {
        if (open) {
            for (Component component : components) {
                component.mouseReleased(mouseX, mouseY, button);
            }
        }
    }

    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        if (binding) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_DELETE) {
                module.setKey(-1);
            } else {
                module.setKey(keyCode);
            }
            binding = false;
            return;
        }

        if (open) {
            for (Component component : components) {
                component.keyPressed(keyCode, scanCode, modifiers);
            }
        }
    }

    @Override
    public void charTyped(char chr, int modifiers) {
        if (open) {
            for (Component component : components) {
                component.charTyped(chr, modifiers);
            }
        }
    }

    private boolean isHovered(double mouseX, double mouseY, float heightCheck) {
        return HoveringUtils.isHovered(mouseX, mouseY, x, y, width, heightCheck);
    }

    public static class ThemeActionComponent extends Component {
        private final ModuleComponent parent;
        private final GuiAnimation hoverAnim = new GuiAnimation(GuiEasing.QUINTIC_OUT, 300);

        public ThemeActionComponent(ModuleComponent parent) {
            this.parent = parent;
        }

        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
            float btnX = x + 2f;
            float btnY = y + 1f;
            float btnW = width - 4f;
            float btnH = 14f;

            boolean isHovered = HoveringUtils.isHovered(mouseX, mouseY, btnX, btnY, btnW, btnH);
            hoverAnim.run(isHovered);
            if (isHovered) CursorManager.requestHand();

            float alpha = getAlphaAnim().getValue();
            int outlineAlpha = (int) ((25 + (40 * hoverAnim.getValue())) * alpha);
            int outlineColor = ColorUtils.rgba(255, 255, 255, outlineAlpha);
            int innerColor = ColorUtils.rgba(44, 44, 44, (int) (140 * alpha));

            RenderUtils.drawRoundedRect(matrices, btnX - 0.5f, btnY - 0.5f, btnW + 1f, btnH + 0.5f, 3.5f, outlineColor);
            RenderUtils.drawBlur(matrices, btnX, btnY, btnW, btnH - 0.5f, 3f, 20f, innerColor);

            int textColor = ColorUtils.rgba(255, 255, 255, (int) (255 * alpha));
            Font font = Fonts.getFont("sf_regular", 13);
            Component.drawText(matrices, font, "Открыть менеджер тем", btnX + 3.5f, btnY + 3.75f, textColor);
        }

        @Override
        public void mouseClicked(double mouseX, double mouseY, int button) {
            float btnX = x + 2f;
            float btnY = y + 1f;
            float btnW = width - 4f;
            float btnH = 14f;

            if (HoveringUtils.isHovered(mouseX, mouseY, btnX, btnY, btnW, btnH) && button == 0) {
                ThemeManagerWindow tm = parent.getPanel().getParent().getThemeManager();
                tm.setOpen(!tm.isOpen());
            }
        }

        @Override
        public boolean isVisible() {
            return true;
        }

        @Override
        public float getHeight() {
            return 16f;
        }
    }
}

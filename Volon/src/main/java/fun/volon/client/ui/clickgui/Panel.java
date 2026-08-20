package fun.volon.client.ui.clickgui;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import fun.volon.api.storages.implement.helpertstorages.enumvar.ModuleClass;
import fun.volon.api.utils.animation.gui.GuiAnimation;
import fun.volon.api.utils.animation.gui.GuiEasing;
import fun.volon.api.utils.color.ColorUtils;
import fun.volon.api.utils.math.HoveringUtils;
import fun.volon.api.utils.render.RenderUtils;
import fun.volon.api.utils.render.fonts.msdf.Font;
import fun.volon.api.utils.render.fonts.msdf.Fonts;
import fun.volon.api.utils.scissor.ScissorUtils;
import fun.volon.client.modules.Module;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Getter
@Setter
public class Panel {
    public float x, y, width, height;
    public final Module.ModuleCategory category;
    public List<ModuleComponent> moduleComponents = new ArrayList<>();
    private GuiAnimation animation = new GuiAnimation(GuiEasing.QUINTIC_OUT, 350);
    private GuiAnimation animationAlpha = new GuiAnimation(GuiEasing.BOUNCE_OUT, 350);
    private final GuiAnimation scrollbarAnim = new GuiAnimation(GuiEasing.CUBIC_IN_OUT, 220);
    float scroll;
    float maxScroll;

    private final ClickGui parent;

    public Panel(Module.ModuleCategory category, ClickGui parent) {
        this.category = category;
        this.parent = parent;
        if (ModuleClass.INSTANCE != null) {
            ModuleClass.INSTANCE.getObject().stream()
                    .filter(m -> m.getCategory() == this.category)
                    .sorted(Comparator.comparing(m -> m.getName().toLowerCase()))
                    .forEach(m -> moduleComponents.add(new ModuleComponent(m, this)));
        }
    }

    public void clampScroll() {
        if (maxScroll > 0) {
            scroll = MathHelper.clamp(scroll, -maxScroll, 0);
        } else {
            scroll = 0;
        }
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
        animationAlpha.setValue(1);
        float alpha = Math.min(255 * animationAlpha.getValue(), 255);
        float alphaRatio = alpha / 255f;
        float cornerRadius = 8f;
        float headerHeight = 20f;

        int panelColor = ColorUtils.setAlphaColor(ColorUtils.getColorWindowBg(), (int) (30 * alphaRatio));

        RenderUtils.drawBlur(matrices, x, y, width, height, cornerRadius, 20f,
                ColorUtils.rgba(75, 75, 75, (int) (255 * alphaRatio)));
        RenderUtils.drawRoundedRect(matrices, x, y, width, height, cornerRadius, panelColor);
        RenderUtils.drawRoundedRect(matrices, x, y, width, headerHeight,
                cornerRadius, cornerRadius, 0f, 0f,
                ColorUtils.setAlphaColor(ColorUtils.getColorHeaderBg(), (int) (80 * alphaRatio)));

        String title = category.getName();
        String capitalizedTitle = title.isEmpty()
                ? title
                : title.substring(0, 1).toUpperCase() + title.substring(1).toLowerCase();

        Font nameFont = Fonts.getFont("sf_regular", 17);
        Font iconFont = Fonts.getFont("iconsminced", 17);
        String categoryIcon = switch (category) {
            case COMBAT -> "a";
            case MOVEMENT -> "b";
            case RENDER -> "c";
            case PLAYER -> "d";
            case MISC -> "e";
            default -> "";
        };
        float titleWidth = nameFont == null ? 0 : nameFont.getStringWidth(capitalizedTitle);
        float iconWidth = iconFont == null ? 0 : iconFont.getStringWidth(categoryIcon);
        float totalWidth = iconWidth + 3f + titleWidth;
        float startX = x + width / 2f - totalWidth / 2f - 1f;

        Component.drawText(matrices, iconFont, categoryIcon, startX, y + 6f,
                ColorUtils.setAlphaColor(ColorUtils.getColorIcons(), (int) alpha));
        Component.drawText(matrices, nameFont, capitalizedTitle, startX + iconWidth + 3f, y + 6f,
                ColorUtils.setAlphaColor(ColorUtils.getColorHeaderText(), (int) alpha));

        float offset = 2f;
        clampScroll();
        animation.run(scroll);

        ScissorUtils.push();
        ScissorUtils.setFromComponentCoordinates(x, y + headerHeight, width, height - headerHeight - 4);

        for (ModuleComponent component : moduleComponents) {
            if (parent.searchCheck(component.getModule().getName())) {
                continue;
            }

            component.setX(x + 3.5f);
            component.setY((float) (y + headerHeight + offset + animation.getValue()));
            component.setWidth(width - 7f);

            float baseHeight = 19f;
            float extraHeight = 0;
            if (component.getAnimation().getValue() > 0.01f) {
                for (Component comp : component.getComponents()) {
                    float visibleProgress = MathHelper.clamp(comp.getAlphaAnimSetting().getValue(), 0f, 1f);
                    if (comp.isVisible() || visibleProgress > 0f) {
                        extraHeight += comp.getHeight() * visibleProgress;
                    }
                }
            }
            component.setHeight(baseHeight + (extraHeight * component.getAnimation().getValue()));

            ScissorUtils.setFromComponentCoordinates(x, y + headerHeight, width, height - headerHeight - 4);
            component.render(matrices, mouseX, mouseY, partialTicks);
            ScissorUtils.setFromComponentCoordinates(x, y + headerHeight, width, height - headerHeight - 4);

            offset += component.getHeight() + 3.5f;
        }
        maxScroll = Math.max(0, offset - (height - headerHeight - 8));
        scrollbarAnim.run(maxScroll > 0f);

        ScissorUtils.unset();
        ScissorUtils.pop();
    }

    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (HoveringUtils.isHovered(mouseX, mouseY, x, y + 20, width, height - 20)) {
            for (ModuleComponent moduleComponent : moduleComponents) {
                if (!parent.searchCheck(moduleComponent.getModule().getName())) {
                    moduleComponent.mouseClicked(mouseX, mouseY, button);
                }
            }
        }
    }

    public void mouseReleased(double mouseX, double mouseY, int button) {
        for (ModuleComponent moduleComponent : moduleComponents) {
            if (!parent.searchCheck(moduleComponent.getModule().getName())) {
                moduleComponent.mouseReleased(mouseX, mouseY, button);
            }
        }
    }

    public void mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (HoveringUtils.isHovered(mouseX, mouseY, x, y, width, height)) {
            scroll += (float) (verticalAmount * 30f);
            clampScroll();
        }
    }

    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        for (ModuleComponent moduleComponent : moduleComponents) {
            moduleComponent.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    public void charTyped(char chr, int modifiers) {
        for (ModuleComponent moduleComponent : moduleComponents) {
            moduleComponent.charTyped(chr, modifiers);
        }
    }
}

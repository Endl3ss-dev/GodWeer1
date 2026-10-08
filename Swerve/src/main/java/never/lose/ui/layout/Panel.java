package never.lose.ui.layout;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;
import never.lose.managers.ModuleManager;
import never.lose.modules.api.ModuleCategory;
import never.lose.modules.api.setting.ItemModelSetting;
import never.lose.ui.ClickGuiScreen;
import never.lose.ui.component.Component;
import never.lose.ui.component.ModuleComponent;
import never.lose.utils.animation.Animation;
import never.lose.utils.animation.Easing;
import never.lose.utils.font.Fonts;
import never.lose.utils.render.Draw;
import never.lose.utils.render.Hover;
import never.lose.utils.render.Scissor;
import never.lose.utils.render.providers.ColorProvider;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Panel {

    public float x, y, width, height;
    public final ModuleCategory category;
    public List<ModuleComponent> moduleComponents = new ArrayList<>();
    private final Animation animation = new Animation(Easing.QUINTIC_OUT, 350);
    private final Animation animationAlpha = new Animation(Easing.BOUNCE_OUT, 350);
    private final Animation scrollbarAnim = new Animation(Easing.CUBIC_IN_OUT, 220);
    public final Animation slideAnim = new Animation(Easing.QUINTIC_OUT, 320);
    public int slideDir = 1;
    float scroll;
    float maxScroll;

    private final ClickGuiScreen parent;

    private String cachedTitle;
    private String cachedIcon;
    private float cachedTitleWidth = -1f;
    private float cachedIconWidth = -1f;

    public Panel(ModuleCategory category, ClickGuiScreen parent) {
        this.category = category;
        this.parent = parent;
        ModuleManager.get().getModules().stream()
                .filter(m -> m.getCategory() == this.category)
                .sorted(Comparator.comparing(m -> m.getName().toLowerCase()))
                .forEach(m -> moduleComponents.add(new ModuleComponent(m, this)));
    }

    public void clampScroll() {
        if (maxScroll > 0) {
            scroll = MathHelper.clamp(scroll, -maxScroll, 0);
        } else {
            scroll = 0;
        }
    }

    public void render(DrawContext context, int mouseX, int mouseY, float partialTicks) {
        float alphaRatio = MathHelper.clamp(slideAnim.getValue(), 0f, 1f);
        animationAlpha.setValue(alphaRatio);
        float alpha = Math.min(255 * alphaRatio, 255);
        float cornerRadius = 8f;
        float headerHeight = 25f;

        int panelColor = ColorProvider.setAlpha(ColorProvider.getColorClickGui(), (int) (130 * alphaRatio));

        Draw.blur(x, y, width, height, cornerRadius, ColorProvider.rgba(200, 200, 200, (int) (255 * alphaRatio)), 14f);
        Draw.round(x - 1f, y - 1f, width + 2f, height + 2f, cornerRadius + 0.5f, ColorProvider.rgba(48, 66, 122, (int) (70 * alphaRatio)));
        Draw.round(x, y, width, height, cornerRadius, panelColor);
        Draw.round(x, y, width, headerHeight, cornerRadius, 0, 0, cornerRadius,
                ColorProvider.setAlpha(ColorProvider.getColorHeaderBg(), (int) (45 * alphaRatio)));

        float iconSize = 8.5f;
        if (cachedTitleWidth < 0f) {
            String title = category.name();
            cachedTitle = title.substring(0, 1).toUpperCase() + title.substring(1).toLowerCase();
            cachedIcon = switch (category) {
                case COMBAT -> "a";
                case MOVEMENT -> "b";
                case RENDER -> "c";
                case PLAYER -> "d";
                case MISC -> "e";
            };
            cachedTitleWidth = Draw.width(Fonts.REGULAR, cachedTitle, 8.5f);
            cachedIconWidth = Draw.width(Fonts.ICONS, cachedIcon, iconSize);
        }
        String capitalizedTitle = cachedTitle;
        float titleWidth = cachedTitleWidth;
        String categoryIcon = cachedIcon;
        float iconWidth = cachedIconWidth;
        float totalWidth = iconWidth + 3f + titleWidth;
        float startX = x + width / 2f - totalWidth / 2f - 1f;

        float titleY = y + headerHeight / 2f - 8.5f / 2f;
        float iconY = titleY + 1f;
        Draw.text(Fonts.ICONS, categoryIcon, startX, iconY, ColorProvider.setAlpha(ColorProvider.getColorIcons(), (int) alpha), iconSize);
        Draw.text(Fonts.REGULAR, capitalizedTitle, startX + iconWidth + 3f, titleY, ColorProvider.setAlpha(ColorProvider.getColorHeaderText(), (int) alpha), 8.5f);

        float offset = 2f;
        clampScroll();
        animation.run(scroll);

        Scissor.push();
        Scissor.setFromComponentCoordinates(x, y + headerHeight, width, height - headerHeight - 4);

        for (ModuleComponent component : moduleComponents) {
            if (parent.searchCheck(component.getModule().getName())) {
                continue;
            }

            float sideMargin = 6f;
            component.setX(x + sideMargin);
            component.setY((float) (y + headerHeight + offset + animation.getValue()));
            component.setWidth(width - sideMargin * 2f);

            float baseHeight = 19f;
            float extraHeight = 0;
            if (component.getAnimation().getValue() > 0.01f) {
                extraHeight = 3f;
                for (Component comp : component.getComponents()) {
                    float visibleProgress = MathHelper.clamp(comp.getAlphaAnimSetting().getValue(), 0f, 1f);
                    if (comp.isVisible() || visibleProgress > 0f) {
                        extraHeight += comp.getHeight() * visibleProgress;
                    }
                }
                extraHeight += 5f;
            }
            component.setHeight(baseHeight + (extraHeight * component.getAnimation().getValue()));

            Scissor.setFromComponentCoordinates(x, y + headerHeight, width, height - headerHeight - 4);
            component.render(context, mouseX, mouseY, partialTicks);
            Scissor.setFromComponentCoordinates(x, y + headerHeight, width, height - headerHeight - 4);

            offset += component.getHeight() + 5f;
        }
        maxScroll = Math.max(0, offset - (height - headerHeight - 8));
        scrollbarAnim.run(maxScroll > 0f);

        Scissor.unset();
        Scissor.pop();
    }

    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (Hover.isHovered(mouseX, mouseY, x, y + 20, width, height - 20)) {
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
        if (Hover.isHovered(mouseX, mouseY, x, y, width, height)) {
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

    public boolean isTextFocused() {
        for (ModuleComponent moduleComponent : moduleComponents) {
            if (moduleComponent.isTextFocused()) {
                return true;
            }
        }
        return false;
    }

    public void unfocusText() {
        for (ModuleComponent moduleComponent : moduleComponents) {
            moduleComponent.unfocusText();
        }
    }

    public void openItemModelGallery(ItemModelSetting setting) {
        parent.openItemModelGallery(setting);
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

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public List<ModuleComponent> getModuleComponents() {
        return moduleComponents;
    }

    public Animation getAnimationAlpha() {
        return animationAlpha;
    }
}

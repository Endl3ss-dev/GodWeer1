package org.privates.client.screen.flatgui.impl;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.client.Minecraft;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.systems.RenderSystem;
import org.joml.Vector2f;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.screen.flatgui.AbstractPanel;
import org.privates.client.screen.flatgui.FlatGuiScreen;
import org.privates.client.utils.animation.Animation;
import org.privates.client.utils.animation.util.Easings;
import org.privates.client.utils.math.ScaleMath;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.font.Fonts;
import org.privates.client.utils.render.scroll.ScrollUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Accessors(fluent = true)
public class ModuleListWidget extends AbstractPanel {
    private final FlatGuiScreen flatGuiScreen;
    private final List<FlatModuleComponent> moduleComponents = new ArrayList<>();
    private final ScrollUtil scrollUtil = new ScrollUtil();
    private float moduleHeight = 20;
    private final float margin = 5F;
    private FlatSettingComponent settingComponent;
    private String searchQuery = "";
    private boolean isSearching = false;
    private final Animation cursorAnimation = new Animation();

    public ModuleListWidget(FlatGuiScreen flatGuiScreen) {
        this.flatGuiScreen = flatGuiScreen;
    }

    @Override
    public void init() {
        moduleComponents.forEach(FlatModuleComponent::init);
        if (settingComponent != null) {
            settingComponent.init();
        }
        cursorAnimation.set(0);
    }

    public void init(Category category) {
        moduleComponents.clear();
        List<Module> modules;
        if (isSearching && !searchQuery.isEmpty()) {
            modules = new ArrayList<>();
            for (Category cat : Category.values()) {
                modules.addAll(Instance.get(cat));
            }
            modules = modules.stream()
                    .filter(module -> module.getName().toLowerCase().contains(searchQuery.toLowerCase()))
                    .collect(Collectors.toList());
        } else {
            modules = Instance.get(category);
        }
        modules.sort(Comparator.comparing(module -> -Fonts.SF_SEMIBOLD.getWidth(module.getName(), 7F)));
        for (Module module : modules) {
            FlatModuleComponent component = new FlatModuleComponent(module, flatGuiScreen);
            component.size().set(size().x - margin * 2, 20F);
            moduleComponents.add(component);
        }
        settingComponent = new FlatSettingComponent(flatGuiScreen, this);
        init();
        scrollUtil.setMax(moduleComponents.size() * (20F + margin) + 30, size().y);
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        moduleComponents.forEach(component -> component.resize(minecraft, width, height));
        if (settingComponent != null) {
            settingComponent.resize(minecraft, width, height);
        }
        scrollUtil.setMax(moduleComponents.size() * (20F + margin) + 30, size().y);
    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        scrollUtil.update();

        float cornerRadius = 8F;
        RenderUtil.Rounded.smooth(matrix, position().x, position().y, size().x, size().y, ColorUtil.getColor(25, 25, 35, alpha() / 2F), Round.of(cornerRadius));

        float searchBarHeight = 20F;
        int searchBarColor = isSearching ? ColorUtil.getColor(40, 40, 50, alpha() * 0.5f) : ColorUtil.getColor(30, 30, 40, alpha() * 0.5f);
        RenderUtil.Rounded.smooth(matrix, position().x + margin, position().y + margin, size().x - margin * 2, searchBarHeight, searchBarColor, Round.of(4));

        int textColor = isSearching ? ColorUtil.getColor(255, 255, 255, alpha()) : ColorUtil.getColor(180, 180, 180, alpha());
        String displayText = searchQuery.isEmpty() ? "Search" : searchQuery;
        float textX = position().x + margin + 5;
        float textY = position().y + margin + (searchBarHeight / 2F) - (7F / 2F);
        Fonts.SF_MEDIUM.draw(matrix, displayText, textX, textY, textColor, 7F);

        if (isSearching) {
            cursorAnimation.update();
            if (cursorAnimation.isFinished()) {
                cursorAnimation.run(cursorAnimation.getValue() == 1 ? 0 : 1, 0.5, Easings.LINEAR);
            }
            float cursorAlpha = (float) (cursorAnimation.getValue() * alpha());
            if (cursorAlpha > 0) {
                float cursorX = textX + Fonts.SF_MEDIUM.getWidth(displayText, 7F);
                Fonts.SF_MEDIUM.draw(matrix, "_", cursorX, textY, ColorUtil.getColor(255, 255, 255, cursorAlpha), 7F);
            }
        }

        enableClipping(position().x, position().y + searchBarHeight + margin, position().x + size().x, position().y + size().y - 2);

        Vector2f scaledMouse = ScaleMath.getMouse(mouseX, mouseY);
        int scaledMouseX = (int) scaledMouse.x;
        int scaledMouseY = (int) scaledMouse.y;

        float offset = scrollUtil.getWheel() + searchBarHeight + margin * 2;
        for (FlatModuleComponent component : moduleComponents) {
            component.position().set(position().x + margin, position().y + offset);
            component.size().x = size().x - (margin * 2);
            if (component.position().y + component.size().y >= position().y + searchBarHeight + margin && component.position().y <= position().y + size().y) {
                component.render(matrix, scaledMouseX, scaledMouseY, partialTicks);
            }
            offset += component.size().y + margin;
        }
        moduleHeight = offset;

        disableClipping();

        if (settingComponent != null) {
            settingComponent.render(matrix, scaledMouseX, scaledMouseY, partialTicks);
        }

        if (!moduleComponents.isEmpty()) {
            scrollUtil.renderV(matrix, new Vector2f(position().x + size().x + 4 - cornerRadius, position().y + cornerRadius), size().y - cornerRadius * 2, alpha() * 0.15f);
        }
    }

    private void enableClipping(float x1, float y1, float x2, float y2) {
        int scaleFactor = (int) Minecraft.getInstance().getMainWindow().getScaleFactor();
        int scissorX = (int) (x1 * scaleFactor);
        int scissorY = (int) (Minecraft.getInstance().getMainWindow().getHeight() - (y2 * scaleFactor));
        int scissorWidth = (int) ((x2 - x1) * scaleFactor);
        int scissorHeight = (int) ((y2 - y1) * scaleFactor);

        RenderSystem.enableScissor(scissorX, scissorY, scissorWidth, scissorHeight);
    }

    private void disableClipping() {
        RenderSystem.disableScissor();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Vector2f scaledMouse = ScaleMath.getMouse((int) mouseX, (int) mouseY);
        float scaledMouseX = scaledMouse.x;
        float scaledMouseY = scaledMouse.y;

        float searchBarHeight = 20F;
        if (scaledMouseX >= position().x + margin && scaledMouseX <= position().x + size().x - margin &&
                scaledMouseY >= position().y + margin && scaledMouseY <= position().y + margin + searchBarHeight) {
            isSearching = true;
            return true;
        }

        if (flatGuiScreen.selectedModule() != null && settingComponent != null) {
            if (settingComponent.mouseClicked(scaledMouseX, scaledMouseY, button)) {
                return true;
            }
        }

        for (FlatModuleComponent component : moduleComponents) {
            if (component.position().y + component.size().y >= position().y && component.position().y <= position().y + size().y) {
                if (component.mouseClicked(scaledMouseX, scaledMouseY, button)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        Vector2f scaledMouse = ScaleMath.getMouse((int) mouseX, (int) mouseY);
        float scaledMouseX = scaledMouse.x;
        float scaledMouseY = scaledMouse.y;

        if (flatGuiScreen.selectedModule() != null && settingComponent != null) {
            if (settingComponent.mouseReleased(scaledMouseX, scaledMouseY, button)) {
                return true;
            }
        }

        for (FlatModuleComponent component : moduleComponents) {
            if (component.position().y + component.size().y >= position().y && component.position().y <= position().y + size().y) {
                if (component.mouseReleased(scaledMouseX, scaledMouseY, button)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (isSearching) {
            if (keyCode == 256) {
                isSearching = false;
                if (searchQuery.isEmpty()) {
                    init(FlatGuiScreen.selectedCategory());
                }
                return true;
            } else if (keyCode == 257) {
                isSearching = false;
                return true;
            } else if (keyCode == 259 && !searchQuery.isEmpty()) {
                searchQuery = searchQuery.substring(0, searchQuery.length() - 1);
                init(FlatGuiScreen.selectedCategory());
                return true;
            }
            return true;
        }

        if (flatGuiScreen.selectedModule() != null && settingComponent != null) {
            if (settingComponent.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
        }

        for (FlatModuleComponent component : moduleComponents) {
            if (component.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (flatGuiScreen.selectedModule() != null && settingComponent != null) {
            if (settingComponent.keyReleased(keyCode, scanCode, modifiers)) {
                return true;
            }
        }

        for (FlatModuleComponent component : moduleComponents) {
            if (component.keyReleased(keyCode, scanCode, modifiers)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (isSearching) {
            if (Character.isLetterOrDigit(codePoint) || Character.isSpaceChar(codePoint)) {
                searchQuery += codePoint;
                init(FlatGuiScreen.selectedCategory());
                return true;
            }
        }

        if (flatGuiScreen.selectedModule() != null && settingComponent != null) {
            if (settingComponent.charTyped(codePoint, modifiers)) {
                return true;
            }
        }

        for (FlatModuleComponent component : moduleComponents) {
            if (component.charTyped(codePoint, modifiers)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void onClose() {
        moduleComponents.forEach(FlatModuleComponent::onClose);
        if (settingComponent != null) {
            settingComponent.onClose();
        }
    }
}
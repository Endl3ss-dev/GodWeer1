package org.privates.client.screen.clickgui.component.category;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.Excellent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.screen.clickgui.ClickGuiScreen;
import org.privates.client.screen.clickgui.component.WindowComponent;
import org.privates.client.screen.clickgui.component.module.ModuleComponent;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.font.Fonts;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Getter
public class CategoryComponent extends WindowComponent {
    private final Category category;
    private final List<ModuleComponent> moduleComponents = new ArrayList<>();
    private float moduleHeight = 0;

    public CategoryComponent(Category category, ClickGuiScreen clickGui) {
        this.category = category;
        this.moduleComponents.addAll(getModules().stream()
                .map(module -> new ModuleComponent(module, clickGui))
                .toList());
        size.set(clickGui.categoryWidth(), clickGui.categoryHeight());
    }

    private List<Module> getModules() {
        List<Module> modulesList = Excellent.inst().moduleManager().get(category);
        modulesList.sort(Comparator.comparing(module -> -Fonts.SF_BOLD.getWidth(module.getModuleInfo().name(), 7)));
        return modulesList;
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        moduleComponents.forEach(c -> c.resize(minecraft, width, height));
    }

    @Override
    public void init() {
        moduleComponents.forEach(ModuleComponent::init);
    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        float x = (float) Mathf.step(position.x - 2, 0.5);
        float y = (float) Mathf.step(position.y - 2, 0.5);
        float width = (float) Mathf.step(size.x + 4, 0.5);
        float height = (float) Mathf.step(size.y + 4 + moduleHeight, 0.5);
        float radius = 7f;

        boolean useBlur = Theme.getInstance().blurStrength().getValue().floatValue() > 0;

        RenderUtil.CustomStyled.drawGui(matrix, x, y, width, height + 1, radius, useBlur, Theme.getInstance().blurStrength().getValue().floatValue());

        Fonts.CLICKGUI.drawRight(matrix, category.getIcon(),
                position.x - 4 - Fonts.SF_BOLD.getWidth(category.getName(), 8.5f) / 2 + width / 2,
                position.y + 6.3f,
                ColorUtil.replAlpha(ColorUtil.fade(), ClickGuiScreen.alpha.get()), 8.5f);

        Fonts.SF_BOLD.drawCenter(matrix, category.getName(),
                position.x + width / 2,
                position.y + 5.5f,
                ColorUtil.replAlpha(ColorUtil.getColor(140), ClickGuiScreen.alpha.get()), 8.5f);

        float offset = 0;
        for (ModuleComponent component : moduleComponents) {
            if (Excellent.inst().clickGui().searchCheck(component.getModule().getName())) continue;
            component.position().set(position.x, position.y + size.y + offset);
            component.render(matrix, mouseX, mouseY, partialTicks);
            offset += component.size().y + (component.expandAnimation().getValue() * component.getSettingHeight());
        }
        moduleHeight = offset;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (ModuleComponent component : moduleComponents) {
            if (Excellent.inst().clickGui().searchCheck(component.getModule().getName())) continue;
            if (component.mouseClicked(mouseX, mouseY, button)) return true;
        }
        return false; // не вызываем super — драг делает ClickGuiScreen
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (ModuleComponent component : moduleComponents) {
            if (Excellent.inst().clickGui().searchCheck(component.getModule().getName())) continue;
            if (component.mouseReleased(mouseX, mouseY, button)) return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (ModuleComponent component : moduleComponents) {
            if (Excellent.inst().clickGui().searchCheck(component.getModule().getName())) continue;
            if (component.keyPressed(keyCode, scanCode, modifiers)) return true;
        }
        return false;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        for (ModuleComponent component : moduleComponents) {
            if (Excellent.inst().clickGui().searchCheck(component.getModule().getName())) continue;
            if (component.keyReleased(keyCode, scanCode, modifiers)) return true;
        }
        return false;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        for (ModuleComponent component : moduleComponents) {
            if (Excellent.inst().clickGui().searchCheck(component.getModule().getName())) continue;
            if (component.charTyped(codePoint, modifiers)) return true;
        }
        return false;
    }

    @Override
    public void onClose() {
        moduleComponents.forEach(ModuleComponent::onClose);
    }
}
package org.privates.client.screen.clickgui.component;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Namespaced;
import net.minecraft.util.ResourceLocation;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.api.interfaces.IScreen;
import org.privates.client.api.interfaces.IWindow;
import org.privates.client.managers.module.Category;
import org.privates.client.screen.clickgui.ClickGuiScreen;
import org.privates.client.screen.clickgui.component.category.CategoryComponent;
import org.privates.client.screen.clickgui.component.module.ModuleComponent;
import org.privates.client.utils.keyboard.Keyboard;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RectUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.scroll.ScrollUtil;

import java.util.ArrayList;
import java.util.List;

import static org.privates.client.screen.clickgui.ClickGuiScreen.alpha;

@Getter
public class Panel implements IScreen, IWindow {
    private final ClickGuiScreen clickGui;
    private final ResourceLocation avatar = new Namespaced("texture/avatar.png");
    private final List<CategoryComponent> categoryComponents = new ArrayList<>();
    private final ScrollUtil scrollUtilV = new ScrollUtil();
    private final ScrollUtil scrollUtilH = new ScrollUtil();
    @Setter
    public ModuleComponent expandedModule = null;
    private boolean firstInit = true;
    private long lastScrollArrow = 0;
    private static final long intervalArrow = 16;

    public Panel(ClickGuiScreen clickGui) {
        this.clickGui = clickGui;

        float posX = 0;
        for (Category category : Category.values()) {
            CategoryComponent component = new CategoryComponent(category, clickGui);
            component.position().set(posX, 0);
            categoryComponents.add(component);
            posX += clickGui.categoryWidth() + clickGui.categoryOffset();
        }

    }

    private void setPosition() {
        double categoryHeight = categoryComponents.stream()
                .mapToDouble(CategoryComponent::getModuleHeight)
                .max()
                .orElse(0) + clickGui.categoryHeight();


        scrollUtilH.setMax(scaled().x + 2 * (categoryComponents.size() * (clickGui.categoryWidth() + clickGui.categoryOffset()) - clickGui.categoryOffset()), (categoryComponents.size() * (clickGui.categoryWidth() + clickGui.categoryOffset()) - clickGui.categoryOffset()));
        scrollUtilV.setMax(scaled().y, (float) -categoryHeight);

        scrollUtilH.setEnabled(false);
        scrollUtilH.setAutoReset(false);
        scrollUtilH.setWheel(scrollUtilH.getMax() / 2F);
        scrollUtilH.setTarget(scrollUtilH.getMax() / 2F);
        scrollUtilH.getAnimation().set(scrollUtilH.getMax() / 2F);
        scrollUtilV.setEnabled(false);
        scrollUtilV.setAutoReset(false);
        scrollUtilV.setWheel(scrollUtilV.getMax() * 0.5F);
        scrollUtilV.setTarget(scrollUtilV.getMax() * 0.5F);
        scrollUtilV.getAnimation().set(scrollUtilV.getMax() * 0.25F);

    }


    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        setPosition();
        for (CategoryComponent component : categoryComponents) {
            component.resize(minecraft, width, height);
        }
    }

    @Override
    public void init() {
        if (firstInit) {
            setPosition();
            firstInit = false;
        }

        // Убеждаемся, что все категории инициализированы
        for (CategoryComponent component : categoryComponents) {
            if (component != null) {
                component.init();
            }
        }
    }

    private void componentMove(MatrixStack matrixStack) {
        double categoryHeight = categoryComponents.stream()
                .mapToDouble(CategoryComponent::getModuleHeight)
                .max()
                .orElse(0) + clickGui.categoryHeight();

        if (Keyboard.isKeyDown(Keyboard.KEY_LEFT_SHIFT.getKey())) {


            scrollUtilV.setEnabled(false);
            scrollUtilV.setAutoReset(false);
            scrollUtilH.setEnabled(true);
            scrollUtilH.setMax(scaled().x + 2 * (categoryComponents.size() * (clickGui.categoryWidth() + clickGui.categoryOffset()) - clickGui.categoryOffset()), (categoryComponents.size() * (clickGui.categoryWidth() + clickGui.categoryOffset()) - clickGui.categoryOffset()));
        } else {

            scrollUtilH.setEnabled(false);
            scrollUtilH.setAutoReset(false);
            scrollUtilV.setEnabled(true);
            scrollUtilV.setMax(scaled().y, (float) -categoryHeight);
        }

        handleScrollArrow();

        scrollUtilH.update();
        scrollUtilV.update();
    }

    private void handleScrollArrow() {
        if (clickGui.isSearchFieldSelected()) {
            return;
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastScrollArrow < intervalArrow) {
            return;
        }
        lastScrollArrow = currentTime;
        float scrollAmount = 30.0F;

        if (Keyboard.isKeyDown(Keyboard.KEY_DOWN.getKey())) {
            scrollUtilV.setEnabled(true);
            scrollUtilV.setTarget(Math.max(scrollUtilV.getTarget() - scrollAmount, scrollUtilV.getMax()));
        } else if (Keyboard.isKeyDown(Keyboard.KEY_UP.getKey())) {
            scrollUtilV.setEnabled(true);
            scrollUtilV.setTarget(Math.min(scrollUtilV.getTarget() + scrollAmount, 0));
        } else if (Keyboard.isKeyDown(Keyboard.KEY_LEFT.getKey())) {
            scrollUtilH.setEnabled(true);
            scrollUtilH.setTarget(Math.max(scrollUtilH.getTarget() - scrollAmount, scrollUtilH.getMax()));
        } else if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT.getKey())) {
            scrollUtilH.setEnabled(true);
            scrollUtilH.setTarget(Math.min(scrollUtilH.getTarget() + scrollAmount, 0));
        }
    }


    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {

        // if (anim1.get() != 0) {
        RectUtil.drawRect(matrix, 0, 0, width(), height(), ColorUtil.getColor(0, 0.5F * alpha.get()));
        // }
        // if (anim2.get() != 0) {
        //     GaussianBlur.blur(6 * alpha.get() * anim2.get(), 1 * alpha.get() * anim2.get());
        // }
        // //  RenderUtil.Shader.blur(0, 0, sr.getWidth(), sr.getHeight(), 8);
        // if (anim3.get() != 0) {
        //     RectUtil.drawRect(matrix, 0, 0, width(), height(), ColorUtil.getColor(0, 0.5F * alpha.get() * anim3.get()));

        RenderUtil.drawMainMenuShader(mc.getMainWindow().getScaledWidth() * 2, mc.getMainWindow().getScaledHeight() * 2, mouseX, mouseY, 1);

        //     RenderUtil.drawImage(new Namespaced("texture/top_glow.png"), matrix, 0, 0, mc.getMainWindow().getScaledWidth(), mc.getMainWindow().getScaledHeight(), ColorUtil.multAlpha(InterFace.getInstance().clientColor(), alpha.get() * anim3.get()));

        //     RenderUtil.drawImage(new Namespaced("texture/circles_effect.png"), matrix, 0, 0, mc.getMainWindow().getScaledWidth(), mc.getMainWindow().getScaledHeight(), ColorUtil.multAlpha(InterFace.getInstance().clientColor(), alpha.get() * anim3.get()));
        // }

        componentMove(matrix);

        double categoryHeight = categoryComponents.stream()
                .mapToDouble(CategoryComponent::getModuleHeight)
                .max()
                .orElse(0) + clickGui.categoryHeight();

        double categoryWidth = (categoryComponents.size() * (clickGui.categoryWidth() + clickGui.categoryOffset()) - clickGui.categoryOffset());


        matrix.push();
        // GuiRenderTheme.renderTheme(matrix, mouseX, mouseY);

        renderComponents(matrix, mouseX, mouseY, partialTicks);

        matrix.pop();
    }


    private void renderComponents(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        float offset = 0, y = scaled().y;
        for (CategoryComponent component : categoryComponents) {
            float x = ((scaled().x + offset) + scrollUtilH.getWheel() + ((categoryComponents.size() * (clickGui.categoryWidth() + clickGui.categoryOffset()) - clickGui.categoryOffset()))) - (categoryComponents.size() * (clickGui.categoryWidth() + clickGui.categoryOffset()) - clickGui.categoryOffset());
            float y_old = 20 - (alpha.get() * 20);

            if (component.getCategory() == Category.MOVEMENT || component.getCategory() == Category.PLAYER) {
                y_old = -20 + (alpha.get() * 20);
            }

            component.position().set(x, y + scrollUtilV.getWheel() + y_old);
            component.render(matrix, mouseX, mouseY, partialTicks);

            offset += clickGui.categoryWidth() + clickGui.categoryOffset();
        }
    }


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (CategoryComponent component : categoryComponents) {
            if (component != null) {
                component.mouseClicked(mouseX, mouseY, button);
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (CategoryComponent component : categoryComponents) {
            component.mouseReleased(mouseX, mouseY, button);
        }
        return false;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        for (CategoryComponent component : categoryComponents) {
            if (component.mouseScrolled(mouseX, mouseY, delta)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (CategoryComponent component : categoryComponents) {
            component.keyPressed(keyCode, scanCode, modifiers);
        }
        return false;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        for (CategoryComponent component : categoryComponents) {
            component.keyReleased(keyCode, scanCode, modifiers);
        }
        return false;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        for (CategoryComponent component : categoryComponents) {
            component.charTyped(codePoint, modifiers);
        }
        return false;
    }

    @Override
    public void onClose() {
        for (CategoryComponent component : categoryComponents) {
            component.onClose();
        }
    }
}
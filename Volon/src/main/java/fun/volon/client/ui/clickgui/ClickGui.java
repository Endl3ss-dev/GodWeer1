package fun.volon.client.ui.clickgui;

import lombok.Getter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import fun.volon.api.QClient;
import fun.volon.api.utils.animation.gui.GuiEasing;
import fun.volon.api.utils.color.ColorUtils;
import fun.volon.api.utils.input.CursorManager;
import fun.volon.api.utils.math.HoveringUtils;
import fun.volon.api.utils.render.RenderUtils;
import fun.volon.api.utils.render.fonts.msdf.Font;
import fun.volon.api.utils.render.fonts.msdf.Fonts;
import fun.volon.client.modules.Module;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ClickGui extends Screen implements QClient {

    private final List<Panel> panels = new ArrayList<>();
    private final SearchField searchField;
    private final ThemeManagerWindow themeManager;

    public ClickGui() {
        super(Text.of("Volon ClickGui"));
        searchField = new SearchField("Search...");
        for (Module.ModuleCategory category : Module.ModuleCategory.values()) {
            if (category.isThemesPanel()) continue;
            panels.add(new Panel(category, this));
        }
        themeManager = new ThemeManagerWindow(this);
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (mc == null || mc.getWindow() == null) return;

        CursorManager.reset();
        CursorManager.resetIBeam();
        CursorManager.resetClick();

        int windowWidth = mc.getWindow().getScaledWidth();
        int windowHeight = mc.getWindow().getScaledHeight();

        float panelWidth = 105f;
        float spacing = 4f;
        float panelHeight = 240f;
        float panelTotalWidth = panels.size() * (panelWidth + spacing) - spacing;

        float startX = (windowWidth - panelTotalWidth) / 2f;
        float panelY = (windowHeight - panelHeight) / 2f - 20;

        for (int i = 0; i < panels.size(); i++) {
            Panel panel = panels.get(i);
            panel.getAnimationAlpha().setDuration(650);
            panel.getAnimationAlpha().run(1);
            panel.getAnimationAlpha().setEasing(GuiEasing.QUINTIC_OUT);

            panel.setX(startX + i * (panelWidth + spacing));
            panel.setY(panelY);
            panel.setWidth(panelWidth);
            panel.setHeight(panelHeight);

            panel.render(context.getMatrices(), mouseX, mouseY, delta);
        }

        float searchW = 90;
        float searchH = 18;
        float searchX = windowWidth / 2f - searchW / 2f;
        float searchY = panelY + panelHeight + 15;

        searchField.setBounds(searchX, searchY, searchW, searchH);
        searchField.render(context.getMatrices(), mouseX, mouseY, delta);

        themeManager.setX(20);
        themeManager.setY(panelY);
        themeManager.render(context.getMatrices(), mouseX, mouseY, delta);

        Font descFont = Fonts.getFont("sf_regular", 16);
        for (Panel panel : panels) {
            boolean isMouseInPanel = HoveringUtils.isHovered(mouseX, mouseY,
                    panel.getX(), panel.getY(), panel.getWidth(), panel.getHeight());

            for (ModuleComponent component : panel.getModuleComponents()) {
                if (component.isHovered() && isMouseInPanel && searchField.isEmpty()) {
                    String desc = component.getModule().getDisplayDescription();
                    if (desc != null && !desc.isEmpty() && !"NULLABLE".equalsIgnoreCase(desc)) {
                        float textWidth = (descFont == null ? desc.length() * 4f : descFont.getStringWidth(desc)) + 2;
                        RenderUtils.drawRoundedRect(context.getMatrices(),
                                windowWidth / 2f - textWidth / 2f - 2,
                                windowHeight / 2f - 180, textWidth + 8, 14, 0,
                                ColorUtils.rgba(0, 0, 0, 111));
                        Component.drawText(context.getMatrices(), descFont, desc,
                                windowWidth / 2f - textWidth / 2f, windowHeight / 2f - 176,
                                ColorUtils.rgba(255, 255, 255, 255));
                    }
                }
            }
        }

        long window = mc.getWindow().getHandle();
        if (CursorManager.shouldBeHand())
            GLFW.glfwSetCursor(window, GLFW.glfwCreateStandardCursor(GLFW.GLFW_HAND_CURSOR));
        else if (CursorManager.shouldIBeam())
            GLFW.glfwSetCursor(window, GLFW.glfwCreateStandardCursor(GLFW.GLFW_IBEAM_CURSOR));
        else if (CursorManager.shouldClick())
            GLFW.glfwSetCursor(window, GLFW.glfwCreateStandardCursor(GLFW.GLFW_POINTING_HAND_CURSOR));
        else
            GLFW.glfwSetCursor(window, GLFW.glfwCreateStandardCursor(GLFW.GLFW_ARROW_CURSOR));
    }

    public boolean searchCheck(String text) {
        return !searchField.isEmpty()
                && !text.replaceAll(" ", "").toLowerCase().contains(searchField.text.replaceAll(" ", "").toLowerCase());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        themeManager.mouseClicked(mouseX, mouseY, button);
        searchField.mouseClicked(mouseX, mouseY, button);

        if (searchField.isEmpty()) {
            for (Panel panel : panels) {
                if (HoveringUtils.isHovered(mouseX, mouseY, panel.getX(), panel.getY(), panel.getWidth(), panel.getHeight())) {
                    panel.mouseClicked(mouseX, mouseY, button);
                }
            }
        } else {
            for (Panel panel : panels) {
                panel.mouseClicked(mouseX, mouseY, button);
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        themeManager.mouseReleased(mouseX, mouseY, button);
        for (Panel panel : panels) {
            panel.mouseReleased(mouseX, mouseY, button);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        themeManager.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
        for (Panel panel : panels) {
            panel.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        themeManager.keyPressed(keyCode);
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            long window = mc.getWindow().getHandle();
            GLFW.glfwSetCursor(window, GLFW.glfwCreateStandardCursor(GLFW.GLFW_ARROW_CURSOR));
        }
        searchField.keyPressed(keyCode, scanCode, modifiers);
        for (Panel panel : panels) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                panel.getAnimationAlpha().setValue(0);
                panel.getAnimationAlpha().reset();
            }
            panel.keyPressed(keyCode, scanCode, modifiers);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        themeManager.charTyped(chr);
        searchField.charTyped(chr, modifiers);
        for (Panel panel : panels) {
            panel.charTyped(chr, modifiers);
        }
        return super.charTyped(chr, modifiers);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}

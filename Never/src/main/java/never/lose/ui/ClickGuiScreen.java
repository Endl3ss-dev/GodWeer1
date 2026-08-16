package never.lose.ui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import never.lose.NeverLose;
import never.lose.managers.ModuleManager;
import never.lose.modules.api.ModuleCategory;
import never.lose.modules.api.setting.ItemModelSetting;
import never.lose.modules.impl.Render.ClickGui;
import never.lose.ui.component.ItemModelGalleryPopup;
import never.lose.ui.component.ModuleComponent;
import never.lose.ui.component.SearchField;
import never.lose.ui.layout.Panel;
import never.lose.utils.animation.Animation;
import never.lose.utils.animation.Easing;
import never.lose.utils.font.Fonts;
import never.lose.utils.input.CursorManager;
import never.lose.utils.render.Draw;
import never.lose.utils.render.Hover;
import never.lose.utils.render.Render;
import never.lose.utils.render.Scissor;
import never.lose.utils.render.providers.ColorProvider;

import java.util.ArrayList;
import java.util.List;

public class ClickGuiScreen extends Screen {

    private static ClickGuiScreen instance;

    private final List<Panel> panels = new ArrayList<>();
    private final SearchField searchField;
    private final ThemeEditor themeEditor = new ThemeEditor();
    private ItemModelGalleryPopup itemModelGallery;

    private final Animation descAnim = new Animation(Easing.QUINTIC_OUT, 220);
    private String lastDesc = null;

    private boolean closing = false;

    private long handCursor, iBeamCursor, pointingCursor, arrowCursor;
    private boolean cursorsCreated = false;
    private long currentCursor = 0L;

    private String cachedRawQuery = null;
    private String cachedNormalizedQuery = "";

    public static ClickGuiScreen get() {
        if (instance == null) {
            instance = new ClickGuiScreen();
        }
        return instance;
    }

    private void ensureCursors() {
        if (cursorsCreated) return;
        handCursor = GLFW.glfwCreateStandardCursor(GLFW.GLFW_HAND_CURSOR);
        iBeamCursor = GLFW.glfwCreateStandardCursor(GLFW.GLFW_IBEAM_CURSOR);
        pointingCursor = GLFW.glfwCreateStandardCursor(GLFW.GLFW_POINTING_HAND_CURSOR);
        arrowCursor = GLFW.glfwCreateStandardCursor(GLFW.GLFW_ARROW_CURSOR);
        cursorsCreated = true;
    }

    private void applyCursor(long cursor) {
        if (cursor == currentCursor) return;
        GLFW.glfwSetCursor(NeverLose.mc.getWindow().getHandle(), cursor);
        currentCursor = cursor;
    }

    public void playOpenAnimation() {
        closing = false;
        itemModelGallery = null;
        for (Panel panel : panels) {
            panel.slideAnim.reset(0f);
        }
        themeEditor.resetAppear();
        searchField.resetAppear();
        searchField.setFocused(false);
        unfocusTextFields();
    }

    public ClickGuiScreen() {
        super(Text.of("Endless ClickGui"));
        searchField = new SearchField("Search...");
        for (ModuleCategory category : ModuleCategory.values()) {
            panels.add(new Panel(category, this));
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        CursorManager.reset();
        CursorManager.resetIBeam();
        CursorManager.resetClick();
        ModuleManager.get().tickAnimations();

        int windowWidth = NeverLose.mc.getWindow().getScaledWidth();
        int windowHeight = NeverLose.mc.getWindow().getScaledHeight();

        float guiScale = guiScale();
        float centerX = windowWidth / 2f;
        float centerY = windowHeight / 2f;
        int mouseGx = (int) ((mouseX - centerX) / guiScale + centerX);
        int mouseGy = (int) ((mouseY - centerY) / guiScale + centerY);
        mouseX = mouseGx;
        mouseY = mouseGy;

        Scissor.setGuiTransform(guiScale, centerX, centerY);

        Matrix4f model = Render.matrixStack.pushMatrix();
        model.translate(centerX, centerY, 0f);
        model.scale(guiScale, guiScale, 1f);
        model.translate(-centerX, -centerY, 0f);

        float panelWidth = 120f;
        float spacing = 4f;
        float panelHeight = 270f;
        float panelTotalWidth = panels.size() * (panelWidth + spacing) - spacing;

        float startX = (windowWidth - panelTotalWidth) / 2f;
        float panelY = (windowHeight - panelHeight) / 2f;

        float offscreen = windowHeight / 2f + panelHeight;
        for (int i = 0; i < panels.size(); i++) {
            Panel panel = panels.get(i);
            panel.slideDir = (i % 2 == 0) ? 1 : -1;
            panel.slideAnim.run(closing ? 0f : 1f);
            float slide = MathHelper.clamp(panel.slideAnim.getValue(), 0f, 1f);
            float yOffset = (1f - slide) * panel.slideDir * offscreen;

            panel.setX(startX + i * (panelWidth + spacing));
            panel.setY(panelY + yOffset);
            panel.setWidth(panelWidth);
            panel.setHeight(panelHeight);

            panel.render(context, mouseX, mouseY, delta);
        }

        if (closing) {
            boolean allClosed = true;
            for (Panel panel : panels) {
                if (panel.slideAnim.getValue() > 0.02f) {
                    allClosed = false;
                    break;
                }
            }
            if (allClosed) {
                closing = false;
                Render.matrixStack.popMatrix();
                Scissor.resetGuiTransform();
                close();
                return;
            }
        }

        float searchW = 90;
        float searchH = 18;
        float searchX = windowWidth / 2f - searchW / 2f;
        float searchY = panelY + panelHeight + 35;

        searchField.setBounds(searchX, searchY, searchW, searchH);
        searchField.render(context, mouseX, mouseY, delta);

        String hoveredDesc = null;
        for (Panel panel : panels) {
            boolean isMouseInPanel = Hover.isHovered(mouseX, mouseY, panel.getX(), panel.getY(), panel.getWidth(), panel.getHeight());
            for (ModuleComponent component : panel.getModuleComponents()) {
                if (component.isHovered() && isMouseInPanel && searchField.isEmpty()) {
                    String desc = component.getModule().getDesc();
                    if (desc != null && !desc.isEmpty()) hoveredDesc = desc;
                }
            }
        }
        if (hoveredDesc != null) lastDesc = hoveredDesc;

        descAnim.run(hoveredDesc != null);
        float da = descAnim.getValue();
        if (da > 0.01f && lastDesc != null) {
            float size = 7.5f;
            float padX = 8f;
            float padY = 5.5f;
            float textWidth = Draw.width(Fonts.REGULAR, lastDesc, size);
            float tooltipW = textWidth + padX * 2f;
            float tooltipH = size + padY * 2f;
            float tooltipX = MathHelper.clamp(windowWidth / 2f - tooltipW / 2f, 4f, windowWidth - tooltipW - 4f);
            float tooltipY = panelY - tooltipH - 8f;

            int a = (int) (255 * da);
            float cx = tooltipX + tooltipW / 2f;
            float cy = tooltipY + tooltipH / 2f;
            float scale = 0.92f + 0.08f * da;

            Matrix4f tip = Render.matrixStack.pushMatrix();
            tip.translate(cx, cy, 0);
            tip.scale(scale, scale, 1f);
            tip.translate(-cx, -cy, 0);

            Draw.blur(tooltipX, tooltipY, tooltipW, tooltipH, 4f, ColorProvider.rgba(200, 200, 200, (int) (255 * da)), 12f);
            Draw.round(tooltipX, tooltipY, tooltipW, tooltipH, 4f, ColorProvider.setAlpha(ColorProvider.getColorClickGui(), (int) (130 * da)));
            Draw.round(tooltipX - 0.5f, tooltipY - 0.5f, tooltipW + 1f, tooltipH + 1f, 4.5f, ColorProvider.rgba(48, 66, 122, (int) (90 * da)));
            Draw.text(Fonts.REGULAR, lastDesc, tooltipX + padX, tooltipY + (tooltipH - size) / 2f + 0.2f,
                    ColorProvider.setAlpha(ColorProvider.getColorText(), a), size);

            Render.matrixStack.popMatrix();
        }

        themeEditor.render(context, mouseX, mouseY, delta);
        if (itemModelGallery != null) {
            itemModelGallery.render(context, mouseX, mouseY, delta);
        }

        Render.matrixStack.popMatrix();
        Scissor.resetGuiTransform();

        ensureCursors();
        long desiredCursor;
        if (CursorManager.shouldBeHand()) desiredCursor = handCursor;
        else if (CursorManager.shouldIBeam()) desiredCursor = iBeamCursor;
        else if (CursorManager.shouldClick()) desiredCursor = pointingCursor;
        else desiredCursor = arrowCursor;
        applyCursor(desiredCursor);
    }

    @Override
    public void removed() {
        super.removed();
        if (cursorsCreated) {
            GLFW.glfwSetCursor(NeverLose.mc.getWindow().getHandle(), 0L);
            GLFW.glfwDestroyCursor(handCursor);
            GLFW.glfwDestroyCursor(iBeamCursor);
            GLFW.glfwDestroyCursor(pointingCursor);
            GLFW.glfwDestroyCursor(arrowCursor);
            cursorsCreated = false;
            currentCursor = 0L;
        }
    }

    public boolean searchCheck(String text) {
        if (searchField.isEmpty()) return false;
        String raw = searchField.text;
        if (!raw.equals(cachedRawQuery)) {
            cachedRawQuery = raw;
            cachedNormalizedQuery = raw.replaceAll(" ", "").toLowerCase();
        }
        return !text.replaceAll(" ", "").toLowerCase().contains(cachedNormalizedQuery);
    }

    public void openItemModelGallery(ItemModelSetting setting) {
        itemModelGallery = new ItemModelGalleryPopup(setting);
    }

    private float guiScale() {
        ClickGui module = ModuleManager.get().get(ClickGui.class);
        return module != null ? (float) module.size.getValue() : 1f;
    }

    private double scaleMouseX(double mouseX) {
        float s = guiScale();
        double cx = NeverLose.mc.getWindow().getScaledWidth() / 2.0;
        return (mouseX - cx) / s + cx;
    }

    private double scaleMouseY(double mouseY) {
        float s = guiScale();
        double cy = NeverLose.mc.getWindow().getScaledHeight() / 2.0;
        return (mouseY - cy) / s + cy;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        mouseX = scaleMouseX(mouseX);
        mouseY = scaleMouseY(mouseY);
        if (itemModelGallery != null) {
            if (!itemModelGallery.contains(mouseX, mouseY)) {
                itemModelGallery = null;
            } else {
                itemModelGallery.mouseClicked(mouseX, mouseY, button);
            }
            return true;
        }
        if (themeEditor.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        unfocusTextFields();
        searchField.mouseClicked(mouseX, mouseY, button);

        if (searchField.isEmpty()) {
            for (Panel panel : panels) {
                if (Hover.isHovered(mouseX, mouseY, panel.getX(), panel.getY(), panel.getWidth(), panel.getHeight())) {
                    panel.mouseClicked(mouseX, mouseY, button);
                }
            }
        } else {
            for (Panel panel : panels) {
                panel.mouseClicked(mouseX, mouseY, button);
            }
        }
        if (isTextFocused()) {
            searchField.setFocused(false);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        mouseX = scaleMouseX(mouseX);
        mouseY = scaleMouseY(mouseY);
        if (itemModelGallery != null) {
            itemModelGallery.mouseReleased(mouseX, mouseY, button);
            return true;
        }
        themeEditor.mouseReleased(mouseX, mouseY, button);
        for (Panel panel : panels) {
            panel.mouseReleased(mouseX, mouseY, button);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        mouseX = scaleMouseX(mouseX);
        mouseY = scaleMouseY(mouseY);
        if (itemModelGallery != null) {
            itemModelGallery.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
            return true;
        }
        for (Panel panel : panels) {
            panel.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (itemModelGallery != null) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                itemModelGallery = null;
            } else {
                itemModelGallery.keyPressed(keyCode, scanCode, modifiers);
            }
            return true;
        }
        boolean anyModuleBinding = false;
        for (Panel panel : panels) {
            for (ModuleComponent component : panel.getModuleComponents()) {
                if (component.isBinding()) {
                    anyModuleBinding = true;
                    break;
                }
            }
            if (anyModuleBinding) break;
        }

        if (keyCode == GLFW.GLFW_KEY_ESCAPE && anyModuleBinding) {
            for (Panel panel : panels) {
                panel.keyPressed(keyCode, scanCode, modifiers);
            }
            return true;
        }

        if (isTextFocused()) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                unfocusTextFields();
                return true;
            }
            for (Panel panel : panels) {
                panel.keyPressed(keyCode, scanCode, modifiers);
            }
            return true;
        }

        if (searchField.isFocused()) {
            searchField.keyPressed(keyCode, scanCode, modifiers);
            return true;
        }

        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            ensureCursors();
            applyCursor(arrowCursor);
            closing = true;
            return true;
        }
        for (Panel panel : panels) {
            panel.keyPressed(keyCode, scanCode, modifiers);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (itemModelGallery != null) {
            itemModelGallery.charTyped(chr, modifiers);
            return true;
        }
        if (isTextFocused()) {
            for (Panel panel : panels) {
                panel.charTyped(chr, modifiers);
            }
            return true;
        }
        if (searchField.isFocused()) {
            searchField.charTyped(chr, modifiers);
            return true;
        }
        for (Panel panel : panels) {
            panel.charTyped(chr, modifiers);
        }
        return super.charTyped(chr, modifiers);
    }

    private boolean isTextFocused() {
        for (Panel panel : panels) {
            if (panel.isTextFocused()) {
                return true;
            }
        }
        return false;
    }

    private void unfocusTextFields() {
        for (Panel panel : panels) {
            panel.unfocusText();
        }
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    public List<Panel> getPanels() {
        return panels;
    }
}

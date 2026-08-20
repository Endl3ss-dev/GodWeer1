package org.privates.client.screen.figmagui;

import lombok.Getter;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.lwjgl.glfw.GLFW;
import org.privates.client.Excellent;
import org.privates.client.api.interfaces.IMinecraft;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.impl.client.FlatGui;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.other.SoundUtil;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.font.Fonts;

import java.util.ArrayList;

@Getter
public class FigmaGuiScreen extends Screen implements IMinecraft {

    private final ArrayList<FigmaPanel> panels = new ArrayList<>();
    private final FigmaSearchBar searchBar;

    private float scale = 1f;
    private float scaleV = 0f;
    private boolean closing = false;
    private boolean isInitialOpen = true;

    private static final float FIGMA_SCALE = 1f / 1.6f;
    private static final float PANEL_WIDTH_F = 150f;
    private static final float PANEL_HEIGHT_F = 571f;
    private static final float PANEL_SPACING_F = 20f;

    private float panelWidth;
    private float panelHeight;
    private float panelSpacing;

    public FigmaGuiScreen() {
        super(new StringTextComponent(""));
        this.searchBar = new FigmaSearchBar(this);
    }

    public float fs() {
        try {
            String value = FlatGui.getInstance().testGuiScale().getValue();
            float mul = 1f;
            if (value != null && value.endsWith("%")) {
                mul = Float.parseFloat(value.replace("%", "")) / 100f;
            }
            return FIGMA_SCALE * mul;
        } catch (Exception e) {
            return FIGMA_SCALE;
        }
    }

    @Override
    protected void init() {
        panelWidth = PANEL_WIDTH_F * fs();
        panelHeight = PANEL_HEIGHT_F * fs();
        panelSpacing = PANEL_SPACING_F * fs();

        if (panels.isEmpty()) {
            buildPanels();
        } else {
            repositionPanels();
        }

        if (isInitialOpen) {
            SoundUtil.playSound("guiopen.wav", 0.3f);
            scale = 1f;
            closing = false;
            for (FigmaPanel panel : panels) {
                panel.setupAnimation(true);
            }
            isInitialOpen = false;
        }

        searchBar.init();
        super.init();
    }

    private void buildPanels() {
        panels.clear();
        int panelCount = Category.values().length + 2;
        float totalW = panelCount * panelWidth + (panelCount - 1) * panelSpacing;
        float startX = mc.getMainWindow().getScaledWidth() / 2f - totalW / 2f;
        float startY = mc.getMainWindow().getScaledHeight() / 2f - panelHeight / 2f;

        int idx = 0;
        panels.add(FigmaPanel.configs(this, startX, startY, idx++));
        startX += panelWidth + panelSpacing;
        for (Category category : Category.values()) {
            panels.add(FigmaPanel.category(this, category, startX, startY, idx));
            startX += panelWidth + panelSpacing;
            idx++;
        }
        panels.add(FigmaPanel.theme(this, startX, startY, idx));
    }

    private void repositionPanels() {
        int panelCount = panels.size();
        float totalW = panelCount * panelWidth + (panelCount - 1) * panelSpacing;
        float startX = mc.getMainWindow().getScaledWidth() / 2f - totalW / 2f;
        float startY = mc.getMainWindow().getScaledHeight() / 2f - panelHeight / 2f;
        for (FigmaPanel panel : panels) {
            panel.setTargetX(startX);
            panel.setTargetY(startY);
            panel.setX(startX);
            panel.setY(startY);
            panel.syncSizeForType(panelWidth, panelHeight);
            startX += panelWidth + panelSpacing;
        }
    }

    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
        scaleV = Mathf.fast(scaleV, scale, 16);
        if (scale == 0f && scaleV < 0.01f) {
            super.closeScreen();
            return;
        }

        for (int i = 0; i < panels.size(); i++) {
            FigmaPanel panel = panels.get(i);
            panel.updateAnimation(System.currentTimeMillis(), scaleV);
            if (panel.getPanelScale() > 0.01f || !closing) {
                ms.push();
                ms.translate(0, 0, i * 0.01f);
                panel.render(ms, mouseX, mouseY);
                ms.pop();
            }
        }
        searchBar.render(ms, mouseX, mouseY, scaleV);

        super.render(ms, mouseX, mouseY, partialTicks);
        renderBindOverlay(ms);
    }

    private void renderBindOverlay(MatrixStack ms) {
        if (!isAnyBindAwaiting()) {
            return;
        }
        ms.push();
        ms.translate(0, 0, 3000);
        int w = mc.getMainWindow().getScaledWidth();
        int h = mc.getMainWindow().getScaledHeight();
        RenderUtil.Rounded.roundedRect(ms, 0, 0, w, h, ColorUtil.getColor(0, 0, 0, 210), Round.of(0));
        float fs = fs();
        float bw = 260f * fs;
        float bh = 60f * fs;
        float bx = w / 2f - bw / 2f;
        float by = h / 2f - bh / 2f;
        RenderUtil.Rounded.roundedRect(ms, bx, by, bw, bh, ColorUtil.getColor(0, 0, 0, 242), Round.of(8f * fs));
        RenderUtil.Rounded.roundedOutline(ms, bx - 0.5f * fs, by - 0.5f * fs, bw + fs, bh + fs, fs, ColorUtil.getColor(255, 255, 255, 55), Round.of(8f * fs));
        String ru = "Нажмите любую кнопку на клавиатуре";
        String en = "Press any key on your keyboard";
        float tsRu = 12f * fs;
        float tsEn = 9f * fs;
        float twRu = Fonts.SF_BOLD.getWidth(ru, tsRu);
        float twEn = Fonts.SF_BOLD.getWidth(en, tsEn);
        Fonts.SF_BOLD.draw(ms, ru, bx + bw / 2f - twRu / 2f, by + 18f * fs, ColorUtil.getColor(255, 255, 255), tsRu);
        Fonts.SF_BOLD.draw(ms, en, bx + bw / 2f - twEn / 2f, by + 34f * fs, ColorUtil.getColor(210, 210, 210), tsEn);
        ms.pop();
    }

    public boolean isAnyBindAwaiting() {
        for (FigmaPanel panel : panels) {
            if (panel.isAnyBindAwaiting()) {
                return true;
            }
        }
        return false;
    }

    public boolean isSearchInputBlocked() {
        if (isAnyBindAwaiting()) {
            return true;
        }
        for (FigmaPanel panel : panels) {
            if (panel.hasExpandedModules() || panel.isAnySettingInputActive()) {
                return true;
            }
        }
        return false;
    }

    public void collapseAllModules() {
        for (FigmaPanel panel : panels) {
            panel.collapseAllModules();
        }
    }

    @Override
    public boolean mouseClicked(double mx, double my, int btn) {
        searchBar.mouseClicked((int) mx, (int) my, btn);
        for (FigmaPanel panel : panels) {
            panel.mouse((float) mx, (float) my, btn, GLFW.GLFW_PRESS);
        }
        return super.mouseClicked(mx, my, btn);
    }

    @Override
    public boolean mouseReleased(double mx, double my, int btn) {
        for (FigmaPanel panel : panels) {
            panel.mouse((float) mx, (float) my, btn, GLFW.GLFW_RELEASE);
        }
        return super.mouseReleased(mx, my, btn);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double delta) {
        for (FigmaPanel panel : panels) {
            panel.scroll((float) mx, (float) my, (float) delta);
        }
        return super.mouseScrolled(mx, my, delta);
    }

    @Override
    public boolean keyPressed(int key, int scan, int mods) {
        if (isAnyBindAwaiting()) {
            for (FigmaPanel panel : panels) {
                panel.key(key);
            }
            return true;
        }
        if (key == GLFW.GLFW_KEY_ESCAPE) {
            closeScreen();
            return true;
        }
        if (key == GLFW.GLFW_KEY_LEFT_CONTROL || key == GLFW.GLFW_KEY_RIGHT_CONTROL) {
            return false;
        }
        searchBar.keyPressed(key);
        for (FigmaPanel panel : panels) {
            panel.key(key);
        }
        return super.keyPressed(key, scan, mods);
    }

    @Override
    public boolean charTyped(char c, int mods) {
        if (!isAnyBindAwaiting()) {
            searchBar.charTyped(c);
        }
        for (FigmaPanel panel : panels) {
            panel.charTyped(c);
        }
        return super.charTyped(c, mods);
    }

    @Override
    public void closeScreen() {
        SoundUtil.playSound("guiclose.wav", 0.3f);
        closing = true;
        scale = 0f;
        for (FigmaPanel panel : panels) {
            panel.setupAnimation(false);
        }
        isInitialOpen = true;
        Excellent.inst().configManager().set();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}

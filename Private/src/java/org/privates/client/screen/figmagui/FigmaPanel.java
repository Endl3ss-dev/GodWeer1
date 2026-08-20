package org.privates.client.screen.figmagui;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.Util;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.lwjgl.glfw.GLFW;
import org.privates.client.Excellent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.impl.client.FlatGui;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.other.config.ConfigFile;
import org.privates.client.managers.other.config.ConfigManager;
import org.privates.client.screen.newgui.Builder;
import org.privates.client.screen.newgui.SubCategory;
import org.privates.client.utils.keyboard.Keyboard;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.draw.ScissorExp;
import org.privates.client.utils.render.font.Fonts;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

@Getter
@Setter
public class FigmaPanel extends Builder {

    public enum PanelType {
        CONFIGS,
        CATEGORY,
        THEME
    }

    private final FigmaGuiScreen gui;
    private final PanelType type;
    private final Category category;
    private final int index;
    private final ArrayList<FigmaModule> modules = new ArrayList<>();
    private final ArrayList<ConfigEntry> configEntries = new ArrayList<>();
    private SubCategory currentSubCategory;
    private String searchKey = "";

    private float delta;
    private float deltaV;
    private float panelScale = 0f;
    private float animProgress = 0f;
    private long animStartTime = 0L;
    private boolean opening = true;
    private float targetX;
    private float targetY;
    private float subIndicatorX = -1f;
    private float subIndicatorW = 0f;
    private float modulesTransitionV = 1f;
    private float modulesTransitionDir = 1f;

    private static final float HEADER_H_F = 38f;
    private static final float HEADER_RADIUS_F = 12f;
    private static final float BODY_RADIUS_F = 18f;
    private static final float PANEL_SPLIT_GAP_F = 10f;
    private static final float BODY_TOP_PAD_F = 7f;
    private static final float BODY_BOTTOM_PAD_F = 8f;
    private static final float TAB_H_F = 14f;
    private static final float MODULE_GAP_F = 4f;
    private static final float CONFIG_ROW_H_F = 56f;

    public static FigmaPanel category(FigmaGuiScreen gui, Category category, float x, float y, int index) {
        return new FigmaPanel(gui, PanelType.CATEGORY, category, x, y, index);
    }

    public static FigmaPanel configs(FigmaGuiScreen gui, float x, float y, int index) {
        return new FigmaPanel(gui, PanelType.CONFIGS, null, x, y, index);
    }

    public static FigmaPanel theme(FigmaGuiScreen gui, float x, float y, int index) {
        return new FigmaPanel(gui, PanelType.THEME, null, x, y, index);
    }

    private FigmaPanel(FigmaGuiScreen gui, PanelType type, Category category, float x, float y, int index) {
        this.gui = gui;
        this.type = type;
        this.category = category;
        this.index = index;
        this.targetX = x;
        this.targetY = y;
        this.x = x;
        this.y = y;
        this.width = gui.getPanelWidth();
        this.height = gui.getPanelHeight();
        syncSizeForType(gui.getPanelWidth(), gui.getPanelHeight());
        if (type == PanelType.CATEGORY) {
            this.currentSubCategory = SubCategory.getDefault(category);
            loadModules("");
        } else if (type == PanelType.THEME) {
            loadThemeModule();
        } else {
            refreshConfigs("");
        }
    }

    public void syncSizeForType(float baseWidth, float baseHeight) {
        this.width = baseWidth;
        float fs = gui.fs();
        if (type == PanelType.CONFIGS) {
            this.height = 240f * fs;
        } else if (type == PanelType.THEME) {
            this.height = 270f * fs;
        } else {
            this.height = baseHeight;
        }
        updateModulePositions();
    }

    public void setupAnimation(boolean open) {
        this.opening = open;
        int total = gui.getPanels().isEmpty() ? Category.values().length + 2 : gui.getPanels().size();
        float center = (total - 1) / 2f;
        long delay = (long) (Math.abs(index - center) * 55L);
        this.animStartTime = System.currentTimeMillis() + (open ? delay : 0L);
    }

    public void updateAnimation(long now, float scaleV) {
        long elapsed = now - animStartTime;
        float dur = opening ? 220f : 110f;
        animProgress = Math.min(1f, Math.max(0f, elapsed / dur));
        if (!opening) {
            animProgress = 1f - animProgress;
        }
        panelScale = (1f - (float) Math.pow(1f - animProgress, 3f)) * scaleV;
        this.x = targetX;
        this.y = targetY;
        deltaV = Mathf.fast(deltaV, delta, 16f);
        updateModulePositions();
    }

    private float bodyStartY(float fs) {
        return y + HEADER_H_F * fs + PANEL_SPLIT_GAP_F * fs;
    }

    private float tabY(float fs) {
        return bodyStartY(fs) + BODY_TOP_PAD_F * fs;
    }

    private float contentStartY(float fs) {
        if (type == PanelType.CATEGORY) {
            return tabY(fs) + TAB_H_F * fs + 6f * fs;
        }
        return bodyStartY(fs) + BODY_TOP_PAD_F * fs;
    }

    private float contentVisibleHeight(float fs) {
        return height - HEADER_H_F * fs - PANEL_SPLIT_GAP_F * fs - BODY_TOP_PAD_F * fs - BODY_BOTTOM_PAD_F * fs - (type == PanelType.CATEGORY ? TAB_H_F * fs + 6f * fs : 0f);
    }

    private void updateModulePositions() {
        float fs = gui.fs();
        float top = contentStartY(fs);
        for (FigmaModule module : modules) {
            module.setX(x + 7f * fs);
            module.setY(top);
            module.setWidth(width - 14f * fs);
            top += module.getHeight() + MODULE_GAP_F * fs;
        }
    }

    @Override
    public void render(MatrixStack ms, float mouseX, float mouseY) {
        if (panelScale < 0.01f) {
            return;
        }
        float fs = gui.fs();
        Theme theme = Theme.getInstance();
        float cx = x + width / 2f;
        float cy = y + height / 2f;

        ms.push();
        ms.translate(cx, cy, 0);
        ms.scale(panelScale, panelScale, 1f);
        ms.translate(-cx, -cy, 0);

        renderHeader(ms, fs, theme);
        renderBody(ms, mouseX, mouseY, fs, theme);

        ms.pop();
    }

    private void renderHeader(MatrixStack ms, float fs, Theme theme) {
        float headerH = HEADER_H_F * fs;
        int headerBg = ColorUtil.getColor(0, 0, 0, (int) (0.90f * 255));
        RenderUtil.Rounded.roundedRect(ms, x, y, width, headerH, headerBg, Round.of(HEADER_RADIUS_F * fs));

        String title = type == PanelType.CONFIGS ? "Configs" : type == PanelType.THEME ? "Theme" : category.getName();
        float titleSize = 15f * fs;
        Fonts.SF_BOLD.draw(ms, title, x + 15f * fs, y + headerH / 2f - titleSize / 2f - 0.5f * fs, theme.textColor(), titleSize);

        float iconSize = 16f * fs;
        float iconX = x + width - 31f * fs;
        float iconY = y + headerH / 2f - iconSize / 2f;
        if (type == PanelType.CATEGORY) {
            if (category == Category.COMBAT) {
                Fonts.iconf.draw(ms, "c", iconX, iconY, theme.clientColor(), iconSize);
            } else if (category == Category.MOVEMENT) {
                Fonts.iconf.draw(ms, "e", iconX, iconY, theme.clientColor(), iconSize);
            } else {
                Fonts.CLICKGUI.draw(ms, category.getIcon(), iconX, iconY, theme.clientColor(), iconSize);
            }
        } else if (type == PanelType.THEME) {
            Fonts.iconf.draw(ms, "n", iconX, iconY, theme.clientColor(), iconSize);
        } else {
            Fonts.iconf.draw(ms, "f", iconX, iconY, theme.clientColor(), iconSize);
        }
    }

    private void renderBody(MatrixStack ms, float mouseX, float mouseY, float fs, Theme theme) {
        float bodyY = bodyStartY(fs);
        float bodyH = height - HEADER_H_F * fs - PANEL_SPLIT_GAP_F * fs;
        RenderUtil.CustomStyled.drawGui(ms, x, bodyY, width, bodyH, BODY_RADIUS_F * fs, true, theme.blurStrength().getValue());

        if (type == PanelType.CATEGORY) {
            renderSubCategoryTabs(ms, mouseX, mouseY, fs, theme);
        }

        float contentY = contentStartY(fs);
        float visibleH = contentVisibleHeight(fs);
        ScissorExp.push();
        ScissorExp.setFromComponentCoordinates(x, contentY, width, visibleH);

        if (type == PanelType.CONFIGS) {
            renderConfigRows(ms, mouseX, mouseY, fs, theme, contentY, visibleH);
        } else {
            renderModules(ms, mouseX, mouseY, fs, contentY, visibleH);
        }

        ScissorExp.unset();
        ScissorExp.pop();
    }

    private void renderModules(MatrixStack ms, float mouseX, float mouseY, float fs, float contentY, float visibleH) {
        modulesTransitionV = Mathf.fast(modulesTransitionV, 1f, 10f);
        float offsetX = (1f - modulesTransitionV) * 14f * fs * modulesTransitionDir;
        float alphaMul = modulesTransitionV;
        for (FigmaModule module : modules) {
            module.setDelta(deltaV);
            module.setAlpha(alphaMul);
            float oldX = module.getX();
            module.setX(oldX + offsetX);
            float top = module.getY() + deltaV;
            float bot = top + module.getHeight();
            if (bot > contentY && top < contentY + visibleH) {
                module.render(ms, mouseX, mouseY);
            }
            module.setX(oldX);
        }
    }

    private void renderConfigRows(MatrixStack ms, float mouseX, float mouseY, float fs, Theme theme, float contentY, float visibleH) {
        float rowY = contentY + deltaV;
        float rowH = CONFIG_ROW_H_F * fs;
        for (ConfigEntry entry : configEntries) {
            if (rowY + rowH >= contentY && rowY <= contentY + visibleH) {
                float rowX = x + 7f * fs;
                float rowW = width - 14f * fs;
                boolean hover = isHovered(mouseX, mouseY, rowX, rowY, rowW, rowH);
                int rowBg = ColorUtil.getColor(0, 0, 0, (int) ((hover ? 0.74f : 0.65f) * 255));
                RenderUtil.Rounded.roundedRect(ms, rowX, rowY, rowW, rowH, rowBg, Round.of(6f * fs));

                float nameSize = 12f * fs;
                float dateSize = 10f * fs;
                float dateW = Fonts.SF_BOLD.getWidth(entry.date, dateSize);
                Fonts.SF_BOLD.draw(ms, entry.date, rowX + rowW - 6f * fs - dateW, rowY + 5f * fs, ColorUtil.getColor(255, 255, 255, 120), dateSize);
                drawConfigName(ms, entry.name, rowX + 6f * fs, rowY + 4f * fs, rowW - dateW - 14f * fs, nameSize, theme.textColor(), fs);

                List<ButtonRect> buttons = buildConfigButtons(rowX + 6f * fs, rowY + 21f * fs, rowW - 12f * fs, fs);
                for (ButtonRect button : buttons) {
                    renderConfigButton(ms, button.label, button.x, button.y, button.w, button.h, "Load".equals(button.label), fs, theme, button.icon);
                }
            }
            rowY += rowH + MODULE_GAP_F * fs;
        }
    }

    private void drawConfigName(MatrixStack ms, String text, float x, float y, float maxW, float size, int color, float fs) {
        ScissorExp.push();
        ScissorExp.setFromComponentCoordinates(x, y - 1f * fs, maxW, size + 2f * fs);
        Fonts.SF_BOLD.draw(ms, text, x, y, color, size);
        ScissorExp.unset();
        ScissorExp.pop();
    }

    private List<ButtonRect> buildConfigButtons(float x, float y, float width, float fs) {
        String[] labels = new String[]{"Load", "Save", "Unload", "Open Folder"};
        String[] icons = new String[]{"q", "w", "r", "t"};
        List<ButtonRect> rects = new ArrayList<>();
        float gap = 2f * fs;
        float h = 14f * fs;
        float cellW = (width - gap) / 2f;
        for (int i = 0; i < labels.length; i++) {
            int row = i / 2;
            int col = i % 2;
            float rx = x + col * (cellW + gap);
            float ry = y + row * (h + gap);
            rects.add(new ButtonRect(labels[i], icons[i], rx, ry, cellW, h));
        }
        return rects;
    }

    private void renderConfigButton(MatrixStack ms, String text, float x, float y, float w, float h, boolean active, float fs, Theme theme, String icon) {
        int bg = active ? ColorUtil.multAlpha(theme.clientColor(), 1f) : ColorUtil.getColor(64, 64, 64, (int) (0.65f * 255));
        int tc = active ? ColorUtil.getColor(255, 255, 255) : ColorUtil.getColor(230, 230, 230);
        RenderUtil.Rounded.roundedRect(ms, x, y, w, h, bg, Round.of(4f * fs));
        float ts = 8.3f * fs;
        float is = 8.3f * fs;
        float tw = Fonts.SF_BOLD.getWidth(text, ts);
        float iw = Fonts.icon_new.getWidth(icon, is);
        float start = x + w / 2f - (iw + 2f * fs + tw) / 2f;
        Fonts.icon_new.draw(ms, icon, start, y + h / 2f - is / 2f, tc, is);
        Fonts.SF_BOLD.draw(ms, text, start + iw + 2f * fs, y + h / 2f - ts / 2f, tc, ts);
    }

    private void renderSubCategoryTabs(MatrixStack ms, float mouseX, float mouseY, float fs, Theme theme) {
        SubCategory[] subs = SubCategory.getSubCategories(category);
        if (subs.length == 0) {
            return;
        }
        float y0 = tabY(fs);
        float w = width - 14f * fs;
        float x0 = x + 7f * fs;
        float bw = (w - 4f * fs) / 3f;
        float bh = TAB_H_F * fs;
        float gap = 2f * fs;
        float targetX = x0;
        float targetW = bw;
        for (int i = 0; i < subs.length; i++) {
            SubCategory sub = subs[i];
            float bx = x0 + i * (bw + gap);
            boolean active = sub == currentSubCategory;
            boolean hover = isHovered(mouseX, mouseY, bx, y0, bw, bh);
            int bg = ColorUtil.multAlpha(ColorUtil.getColor(64, 64, 64, 90), hover ? 0.9f : 0.68f);
            int tc = active ? ColorUtil.multAlpha(theme.clientColor(), 1f) : ColorUtil.multAlpha(theme.textColor(), 0.82f);
            RenderUtil.Rounded.roundedRect(ms, bx, y0, bw, bh, bg, Round.of(5f * fs));
            float textSize = 8f * fs;
            float tw = Fonts.SF_BOLD.getWidth(sub.getName(), textSize);
            Fonts.SF_BOLD.draw(ms, sub.getName(), bx + bw / 2f - tw / 2f, y0 + bh / 2f - Fonts.SF_BOLD.getHeight(sub.getName(), textSize) / 2f - 0.2f * fs, tc, textSize);
            if (active) {
                targetX = bx;
                targetW = bw;
            }
        }
        if (subIndicatorX < 0f) {
            subIndicatorX = targetX;
            subIndicatorW = targetW;
        }
        subIndicatorX = Mathf.fast(subIndicatorX, targetX, 10f);
        subIndicatorW = Mathf.fast(subIndicatorW, targetW, 10f);
        RenderUtil.Rounded.roundedRect(ms, subIndicatorX, y0, subIndicatorW, bh, ColorUtil.multAlpha(theme.clientColor(), 0.22f), Round.of(5f * fs));
    }

    public void onSearchChanged(String key) {
        searchKey = key == null ? "" : key;
        if (type == PanelType.CATEGORY) {
            loadModules(searchKey);
        } else if (type == PanelType.CONFIGS) {
            refreshConfigs(searchKey);
        }
    }

    public void mouse(float mx, float my, int btn, int act) {
        if (!isHovered(mx, my, x, y, width, height)) {
            return;
        }
        float fs = gui.fs();
        if (type == PanelType.CATEGORY && btn == 0 && act == GLFW.GLFW_PRESS) {
            SubCategory[] subs = SubCategory.getSubCategories(category);
            float y0 = tabY(fs);
            float w = width - 14f * fs;
            float x0 = x + 7f * fs;
            float bw = (w - 4f * fs) / 3f;
            float bh = TAB_H_F * fs;
            float gap = 2f * fs;
            for (int i = 0; i < subs.length; i++) {
                float bx = x0 + i * (bw + gap);
                if (isHovered(mx, my, bx, y0, bw, bh)) {
                    if (currentSubCategory != subs[i]) {
                        modulesTransitionDir = i > currentSubCategory.getIndex() ? 1f : -1f;
                        modulesTransitionV = 0f;
                        currentSubCategory = subs[i];
                        loadModules(searchKey);
                    }
                    return;
                }
            }
        }

        if (type == PanelType.CONFIGS) {
            handleConfigClick(mx, my, btn, act, fs);
            return;
        }

        for (FigmaModule module : modules) {
            module.mouse(mx, my, btn, act);
        }
    }

    private void handleConfigClick(float mx, float my, int btn, int act, float fs) {
        if (btn != 0 || act != GLFW.GLFW_PRESS) {
            return;
        }
        float rowY = contentStartY(fs) + deltaV;
        float rowH = CONFIG_ROW_H_F * fs;
        for (ConfigEntry entry : configEntries) {
            float rowX = x + 7f * fs;
            float rowW = width - 14f * fs;
            List<ButtonRect> buttons = buildConfigButtons(rowX + 6f * fs, rowY + 21f * fs, rowW - 12f * fs, fs);
            for (ButtonRect button : buttons) {
                if (isHovered(mx, my, button.x, button.y, button.w, button.h)) {
                    if ("Load".equals(button.label)) {
                        loadConfig(entry.name);
                    } else if ("Save".equals(button.label)) {
                        saveConfig(entry.name);
                    } else if ("Unload".equals(button.label)) {
                        unloadConfig();
                    } else if ("Open Folder".equals(button.label)) {
                        openConfigDirectory();
                    }
                    return;
                }
            }
            rowY += rowH + MODULE_GAP_F * fs;
        }
    }

    public void key(int key) {
        for (FigmaModule module : modules) {
            module.key(key);
        }
    }

    public void charTyped(char c) {
        for (FigmaModule module : modules) {
            module.charTyped(c);
        }
    }

    public void scroll(float mx, float my, float amount) {
        if (!isHovered(mx, my, x, y, width, height)) {
            return;
        }
        float fs = gui.fs();
        float totalH = 0f;
        if (type == PanelType.CONFIGS) {
            totalH = configEntries.size() * (CONFIG_ROW_H_F * fs + MODULE_GAP_F * fs);
            if (totalH > 0f) {
                totalH -= MODULE_GAP_F * fs;
            }
        } else {
            for (FigmaModule module : modules) {
                totalH += module.getHeight() + MODULE_GAP_F * fs;
            }
            if (totalH > 0f) {
                totalH -= MODULE_GAP_F * fs;
            }
        }
        float visible = contentVisibleHeight(fs);
        if (!(amount > 0 && delta >= 0) && !(amount < 0 && totalH + delta <= visible)) {
            delta += amount * 9f;
        }
    }

    public void loadModules(String key) {
        if (type != PanelType.CATEGORY) {
            return;
        }
        float fs = gui.fs();
        modules.clear();
        delta = 0f;
        deltaV = 0f;
        float start = contentStartY(fs);
        for (org.privates.client.managers.module.Module m : Excellent.inst().moduleManager().get(category)) {
            boolean matchesSearch = key == null || key.isEmpty() || m.getName().toLowerCase(Locale.ROOT).contains(key.toLowerCase(Locale.ROOT));
            boolean matchesSub = key != null && !key.isEmpty() || currentSubCategory == null || m.getClass().isAnnotationPresent((Class) currentSubCategory.getAnnotationClass());
            if (!matchesSearch || !matchesSub) {
                continue;
            }
            FigmaModule module = new FigmaModule(this, m, x + 7f * fs, start);
            modules.add(module);
            start += module.getHeight() + MODULE_GAP_F * fs;
        }
    }

    private void loadThemeModule() {
        float fs = gui.fs();
        modules.clear();
        delta = 0f;
        deltaV = 0f;
        org.privates.client.managers.module.Module themeModule = Excellent.inst().moduleManager().get(Theme.class);
        if (themeModule == null) {
            return;
        }
        FigmaModule module = new FigmaModule(this, themeModule, x + 7f * fs, contentStartY(fs));
        module.setExpanded(true);
        module.setLockExpanded(true);
        modules.add(module);
    }

    private void refreshConfigs(String key) {
        configEntries.clear();
        ConfigManager manager = Excellent.inst().configManager();
        manager.update();
        List<ConfigFile> files = new ArrayList<>(manager.getConfigs());
        files.sort(Comparator.comparingLong(a -> -a.getFile().lastModified()));
        for (ConfigFile file : files) {
            String name = file.getFile().getName();
            if (name.endsWith(".cfg")) {
                name = name.substring(0, name.length() - 4);
            }
            if (key != null && !key.isEmpty() && !name.toLowerCase(Locale.ROOT).contains(key.toLowerCase(Locale.ROOT))) {
                continue;
            }
            configEntries.add(new ConfigEntry(name, file.getFile(), formatConfigDate(file.getFile())));
        }
        delta = 0f;
        deltaV = 0f;
        modulesTransitionV = 0f;
    }

    private String formatConfigDate(File file) {
        try {
            BasicFileAttributes attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
            long ms = attrs.creationTime() != null ? attrs.creationTime().toMillis() : file.lastModified();
            return new SimpleDateFormat("dd.MM.yyyy", Locale.US).format(new Date(ms));
        } catch (Exception e) {
            return new SimpleDateFormat("dd.MM.yyyy", Locale.US).format(new Date(file.lastModified()));
        }
    }

    private void loadConfig(String name) {
        ConfigManager manager = Excellent.inst().configManager();
        manager.update();
        ConfigFile file = manager.get(name);
        if (file == null || !file.getFile().exists()) {
            return;
        }
        CompletableFuture.runAsync(() -> {
            if (file.read()) {
                manager.set();
                if (FlatGui.getInstance().getKey() == Keyboard.KEY_NONE.getKey()) {
                    FlatGui.getInstance().setKey(Keyboard.KEY_RIGHT_SHIFT.getKey());
                }
            }
        });
    }

    private void saveConfig(String name) {
        ConfigManager manager = Excellent.inst().configManager();
        manager.set(name);
        manager.set();
        refreshConfigs(searchKey);
    }

    private void unloadConfig() {
        ConfigFile file = Excellent.inst().configManager().get("default");
        if (file != null) {
            file.read();
            Excellent.inst().configManager().set();
        }
    }

    private void openConfigDirectory() {
        Util.getOSType().openFile(ConfigManager.CONFIG_DIRECTORY);
    }

    public boolean isAnyBindAwaiting() {
        for (FigmaModule figmaModule : modules) {
            if (figmaModule.isAnyBindAwaiting()) {
                return true;
            }
        }
        return false;
    }

    public boolean isAnySettingInputActive() {
        for (FigmaModule figmaModule : modules) {
            if (figmaModule.isAnySettingInputActive()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasExpandedModules() {
        if (type != PanelType.CATEGORY) {
            return false;
        }
        for (FigmaModule figmaModule : modules) {
            if (figmaModule.isExpanded()) {
                return true;
            }
        }
        return false;
    }

    public void collapseAllModules() {
        if (type != PanelType.CATEGORY) {
            return;
        }
        for (FigmaModule figmaModule : modules) {
            figmaModule.setExpanded(false);
        }
    }

    @Getter
    private static class ConfigEntry {
        private final String name;
        private final File file;
        private final String date;

        private ConfigEntry(String name, File file, String date) {
            this.name = name;
            this.file = file;
            this.date = date;
        }
    }

    @Getter
    private static class ButtonRect {
        private final String label;
        private final String icon;
        private final float x;
        private final float y;
        private final float w;
        private final float h;

        private ButtonRect(String label, String icon, float x, float y, float w, float h) {
            this.label = label;
            this.icon = icon;
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }
    }
}

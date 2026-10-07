package swerve.ui.newgui;

import com.mojang.blaze3d.vertex.PoseStack;

import swerve.managers.ModuleManager;
import swerve.utils.color.ColorUtils;
import swerve.modules.Module;
import swerve.modules.ModuleCategory;
import swerve.utils.render.ScissorUtils;

import java.util.ArrayList;

// Панель категории newgui. Отличия от исходника минимальны и продиктованы Swerve:
// у модулей нет субкатегорий (@Category1/2/3), поэтому строка субкатегорий не рендерится,
// а высота заголовка панели — 25 GUI-пикселей, как у панели Client в исходнике.
// Стиль зафиксирован на дефолте исходника: «Слитно» + «Новый» режим панелей.
public class Panel extends Builder {

    private final ClickGuiScreen gui;
    private final ModuleCategory category;
    private final int index;
    private final ArrayList<ModuleComponent> modules = new ArrayList<>();

    private float delta, deltaV;
    private final boolean isThemePanel;

    private float customWidth = 110.0f;
    private float customHeight = 300.0f;
    private float animatedY;
    private float animX, animY;
    private float animProgress = 0.0f;
    private long animStartTime = 0L;
    private boolean opening = true;
    private float overallScale = 1.0f;
    private float targetX, targetY;
    private float panelScale = 0.0f;

    private final float[] moduleAnimations = new float[128];

    public Panel(ClickGuiScreen gui, ModuleCategory category, float x, float y, int index, boolean isThemePanel) {
        this.gui = gui;
        this.category = category;
        this.index = index;
        this.isThemePanel = isThemePanel;
        this.targetX = x;
        this.targetY = y;
        this.x = x;
        this.y = y;
        float sScale = gui.getResolutionScale();
        this.width = customWidth * sScale;
        this.height = customHeight * sScale;
        this.animatedY = y;
        this.animX = x;
        this.animY = y;
        if (isThemePanel) {
            loadThemePanel();
        } else {
            loadModules("");
        }
    }

    public ClickGuiScreen getGui() {
        return gui;
    }

    public ModuleCategory getCategory() {
        return category;
    }

    public int getIndex() {
        return index;
    }

    public boolean isThemePanel() {
        return isThemePanel;
    }

    public ArrayList<ModuleComponent> getModules() {
        return modules;
    }

    public float getAnimatedY() {
        return animatedY;
    }

    public float getAnimProgress() {
        return animProgress;
    }

    public float getPanelScale() {
        return panelScale;
    }

    @Override
    public float getWidth() {
        return width;
    }

    @Override
    public float getHeight() {
        return height;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setTargetX(float targetX) {
        this.targetX = targetX;
    }

    public void setTargetY(float targetY) {
        this.targetY = targetY;
    }

    private void loadThemePanel() {
        float sScale = gui.getResolutionScale();
        modules.clear();
        float offsetY = y + 25.0f * sScale;
        delta = 0.0f;
        deltaV = 0.0f;

        // Синтетический модуль панели Client: его настройки — тема, блюр и прозрачность фона GUI.
        Module clientSettingsModule = new ClientSettingsModule();
        ModuleComponent module = new ModuleComponent(this, clientSettingsModule, x * sScale, offsetY);
        module.setWidth(width - 2.0f * GuiStyle.moduleGap() * sScale);
        modules.add(module);
    }

    // Синтетический модуль настроек GUI: в управлении модулями не участвует, toggle запрещён.
    // Всегда «включён» — как autoEnabled = true у модуля Theme в исходнике.
    private static final class ClientSettingsModule extends Module {
        private ClientSettingsModule() {
            super("Client", "Настройки клиента Swerve", ModuleCategory.RENDER);
            addSettings(new ThemeGridSetting(), GuiConfig.blurStrength(), GuiConfig.settingsBlurStrength(),
                    GuiConfig.backgroundAlpha());
            addSettings(GuiStyle.settings());
        }

        @Override
        public boolean isEnable() {
            return true;
        }

        @Override
        public void toggle() {
            // Панель Client нельзя «включить» — как allowDisable = false в исходнике.
        }
    }

    public void setupInitialAnimation() {
        this.opening = true;

        if (isThemePanel) {
            animStartTime = System.currentTimeMillis();
        } else {
            int totalPanels = ModuleCategory.values().length;
            float centerIndex = (totalPanels - 1) / 2.0f;

            if (index == (int) centerIndex) {
                animStartTime = System.currentTimeMillis();
            } else if (index == (int) centerIndex - 1 || index == (int) centerIndex + 1) {
                animStartTime = System.currentTimeMillis() + 100L;
            } else {
                animStartTime = System.currentTimeMillis() + 200L;
            }
        }

        this.animX = targetX;
        this.animY = targetY;
        this.animatedY = animY;
    }

    public void startClosingAnimation() {
        opening = false;
        animStartTime = System.currentTimeMillis();

        if (isThemePanel) {
            animX = targetX;
            animY = targetY;
        } else {
            int totalPanels = ModuleCategory.values().length;
            float centerIndex = (totalPanels - 1) / 2.0f;

            if (index == (int) centerIndex) {
                animStartTime = System.currentTimeMillis();
            } else if (index == (int) centerIndex - 1 || index == (int) centerIndex + 1) {
                animStartTime = System.currentTimeMillis() + 100L;
            } else {
                animStartTime = System.currentTimeMillis() + 200L;
            }

            animX = targetX;
            animY = targetY;
        }
    }

    private float getEase(float t) {
        return (float) (1.0 - Math.pow(1.0 - t, 3.0));
    }

    public void updateAnimation(long currentTime, float scale) {
        overallScale = scale;
        long elapsed = currentTime - animStartTime;
        float duration = opening ? 250.0f : 120.0f;

        if (opening && elapsed < 0) {
            animProgress = 0.0f;
        } else {
            animProgress = Math.min(1.0f, Math.max(0.0f, elapsed / duration));
        }

        if (!opening) {
            animProgress = 1.0f - animProgress;
        }

        float eased = getEase(animProgress);
        panelScale = eased;

        this.x = targetX;
        this.y = targetY;

        this.animatedY = this.y;
    }

    public void setCustomSize(float width, float height) {
        float sScale = gui.getResolutionScale();
        this.customWidth = width / sScale;
        this.customHeight = height / sScale;
        this.width = width;
        this.height = height;
        this.targetX = x;
        this.targetY = y;
    }

    @Override
    public void render(PoseStack stack, float mouseX, float mouseY) {
        deltaV = GuiStyle.smoothScroll()
                ? Mathf.fast(deltaV, delta, GuiStyle.anim(16.0f))
                : delta;

        float centerX = x + width / 2.0f;
        float centerY = animatedY + height / 2.0f;

        stack.pushPose();
        stack.translate(centerX, centerY, 0.0f);
        stack.scale(panelScale, panelScale, 1.0f);
        stack.translate(-centerX, -centerY, 0.0f);

        float sScale = gui.getResolutionScale();
        float radius = GuiStyle.panelRadius() * sScale;

        GuiRender.drawGlass(stack, x, animatedY, width, height, radius, true, 0.0f);

        float headerHeight = 25.0f * sScale;
        float modulePanelY = animatedY + headerHeight;
        float modulePanelHeight = height - headerHeight;

        ScissorUtils.push();
        ScissorUtils.setFromComponentCoordinates(x, animatedY, width, headerHeight + 3.0f);
        renderHeader(stack, sScale);
        ScissorUtils.unset();
        ScissorUtils.pop();

        ScissorUtils.push();
        ScissorUtils.setFromComponentCoordinates(x, modulePanelY, width, modulePanelHeight - 5.0f);

        float offsetY = GuiStyle.contentOffset() * sScale;
        int moduleIndex = 0;
        for (ModuleComponent module : modules) {
            if (moduleIndex < moduleAnimations.length) {
                moduleAnimations[moduleIndex] = Mathf.fast(moduleAnimations[moduleIndex], 1.0f, 12.0f);
                module.setAlpha(moduleAnimations[moduleIndex]);
            }

            module.setX(x + GuiStyle.moduleGap() * sScale);
            module.setY(modulePanelY + offsetY);
            module.setDelta(deltaV);
            module.setWidth(width - 2.0f * GuiStyle.moduleGap() * sScale);

            float moduleTop = modulePanelY + offsetY + deltaV;
            float moduleBottom = moduleTop + module.getHeight();

            if (isThemePanel || (moduleBottom > modulePanelY && moduleTop < modulePanelY + modulePanelHeight)) {
                module.render(stack, mouseX, mouseY);
            }
            offsetY += module.getHeight() + GuiStyle.moduleGap() * sScale;
            moduleIndex++;
        }

        ScissorUtils.unset();
        ScissorUtils.pop();

        stack.popPose();
    }

    private void renderHeader(PoseStack stack, float sScale) {
        if (!GuiStyle.showHeader()) {
            return;
        }
        String panelName = isThemePanel ? "Client" : category.getName();
        float adaptiveTitleSize = gui.getAdaptiveTitleSize();
        int titleColor = ColorUtils.multAlpha(
                GuiStyle.headerThemeColor() ? GuiTheme.clientColor() : GuiTheme.textColor(),
                (int) (255.0f * GuiStyle.headerAlpha()));

        float textWidth = GuiFonts.SF_MEDIUM.getWidth(panelName, adaptiveTitleSize);
        float textX, iconX;
        float textY = y + 12.5f * sScale - GuiFonts.SF_MEDIUM.getHeight(panelName, adaptiveTitleSize) / 1.5f;

        if (isThemePanel) {
            float iconSize = adaptiveTitleSize * 1.15f + 2.0f;
            float actualIconWidth = GuiStyle.clientIcon() ? GuiFonts.ICONF.getWidth("n", iconSize) : 0.0f;
            float iconHeight = GuiFonts.ICONF.getHeight("n", iconSize);
            float iconY = y + 12.5f * sScale - iconHeight / 2.0f;

            // Выравнивание «Центрично» — дефолт исходника.
            float totalWidth = textWidth + (GuiStyle.clientIcon() ? actualIconWidth + 8.0f * sScale : 0.0f);
            float startX = x + width / 2.0f - totalWidth / 2.0f;
            textX = startX;
            iconX = startX + textWidth + 8.0f * sScale;

            GuiFonts.SF_MEDIUM.draw(stack, panelName, textX, textY, titleColor, adaptiveTitleSize);
            if (GuiStyle.clientIcon()) {
                GuiFonts.ICONF.draw(stack, "n", iconX - 3.0f, iconY - 1.5f, titleColor, iconSize);
            }
        } else {
            String icon = ModuleComponent.categoryIcon(category);

            float iconSize = adaptiveTitleSize * 1.15f * GuiStyle.iconSize();
            float iconW = GuiStyle.categoryIcons() ? iconWidth(icon, iconSize) : 0.0f;
            float iconH = iconHeight(icon, iconSize);
            float iconY = y + 12.5f * sScale - iconH / 2.0f;

            float totalWidth = textWidth + (GuiStyle.categoryIcons() ? iconW + 8.0f * sScale : 0.0f);
            float startX = x + width / 2.0f - totalWidth / 2.0f;
            textX = startX;
            iconX = startX + textWidth + 8.0f * sScale;

            GuiFonts.SF_MEDIUM.draw(stack, panelName, textX, textY, titleColor, adaptiveTitleSize);
            if (GuiStyle.categoryIcons()) {
                drawIcon(stack, icon, iconX, iconY, titleColor, iconSize);
            }
        }

        if (GuiStyle.headerLine()) {
            GuiRender.roundedRect(stack, x + 4.0f * sScale, y + 24.0f * sScale,
                    width - 8.0f * sScale, sScale, sScale,
                    ColorUtils.multAlpha(GuiTheme.clientColor(), 0.24f));
        }
    }

    private float iconWidth(String icon, float iconSize) {
        return (category == ModuleCategory.COMBAT || category == ModuleCategory.MOVEMENT)
                ? GuiFonts.ICONF.getWidth(icon, iconSize)
                : GuiFonts.CLICKGUI.getWidth(icon, iconSize);
    }

    private float iconHeight(String icon, float iconSize) {
        return (category == ModuleCategory.COMBAT || category == ModuleCategory.MOVEMENT)
                ? GuiFonts.ICONF.getHeight(icon, iconSize)
                : GuiFonts.CLICKGUI.getHeight(icon, iconSize);
    }

    private void drawIcon(PoseStack stack, String icon, float iconX, float iconY,
                          int color, float iconSize) {
        if (category == ModuleCategory.COMBAT || category == ModuleCategory.MOVEMENT
                || category == ModuleCategory.INTERFACE) {
            GuiFonts.ICONF.draw(stack, icon, iconX, iconY, color, iconSize);
        } else {
            GuiFonts.CLICKGUI.draw(stack, icon, iconX, iconY, color, iconSize);
        }
    }

    public DescriptionInfo getDescriptionInfo(float mouseX, float mouseY) {
        long currentTime = System.currentTimeMillis();
        String currentDescription = "";
        float maxAlpha = 0.0f;

        for (int i = 0; i < modules.size(); i++) {
            ModuleComponent module = modules.get(i);
            float sScale2 = module.getPanel().getGui().getResolutionScale();
            float baseHeight = 14.0f * sScale2;
            float yExpansion = baseHeight * 0.05f;
            float renderY = module.getY() + module.getDelta() - yExpansion;
            float renderHeight = baseHeight + yExpansion * 2.0f;

            if (isHovered(mouseX, mouseY, module.getX(), renderY, module.getWidth(), renderHeight)) {
                if (module.getHoverStartTime() == 0L) {
                    module.setHoverStartTime(currentTime);
                }

                long hoverDuration = currentTime - module.getHoverStartTime();
                if (hoverDuration >= 250L) {
                    String description = module.getModule().getDescription();
                    if (description == null || description.isEmpty() || "NULLABLE".equals(description)) {
                        description = "Ручки кодера ещё не дошли до того чтобы написать описание это-й функции";
                    }

                    module.setDescriptionAlphaAnim(Mathf.fast(module.getDescriptionAlphaAnim(), 1.0f, 6.0f));

                    if (module.getDescriptionAlphaAnim() > maxAlpha) {
                        maxAlpha = module.getDescriptionAlphaAnim();
                        currentDescription = description;
                    }
                }
            } else {
                module.setHoverStartTime(0L);
                module.setDescriptionAlphaAnim(Mathf.fast(module.getDescriptionAlphaAnim(), 0.0f, 6.0f));
            }
        }

        return new DescriptionInfo(currentDescription, maxAlpha);
    }

    public record DescriptionInfo(String description, float alpha) {
    }

    @Override
    protected void mouse(float mouseX, float mouseY, int button, int action) {
        if (isHovered(mouseX, mouseY, x, animatedY, width, height)) {
            for (int i = 0; i < modules.size(); i++) {
                modules.get(i).mouse(mouseX, mouseY, button, action);
            }
        }
    }

    @Override
    protected void key(int key) {
        for (int i = 0; i < modules.size(); i++) {
            modules.get(i).key(key);
        }
    }

    @Override
    public void charTyped(char typed) {
        for (int i = 0; i < modules.size(); i++) {
            modules.get(i).charTyped(typed);
        }
    }

    public void scroll(float mouseX, float mouseY, float delta) {
        float sScale = gui.getResolutionScale();
        if (modules.isEmpty()) {
            return;
        }

        // Раскрытая сетка тем перехватывает колесо, если курсор над ней.
        for (int i = 0; i < modules.size(); i++) {
            if (modules.get(i).scrollThemeGrid(mouseX, mouseY, delta)) {
                return;
            }
        }

        float totalContentHeight = 0.0f;
        for (int i = 0; i < modules.size(); i++) {
            totalContentHeight += modules.get(i).getHeight() + GuiStyle.moduleGap() * sScale;
        }
        totalContentHeight -= GuiStyle.moduleGap() * sScale;

        float scrollableAreaHeight = height - 27.0f * sScale;

        boolean shouldScroll = isHovered(mouseX, mouseY, x, animatedY, width, height);

        float dir = GuiStyle.invertScroll() ? -delta : delta;
        if (shouldScroll
                && !(dir > 0 && this.delta >= 0)
                && !(dir < 0 && totalContentHeight + this.delta <= scrollableAreaHeight)) {
            this.delta += dir * GuiStyle.scrollSpeed();
        }
    }

    public void loadModules(String key) {
        float sScale = gui.getResolutionScale();
        modules.clear();

        float offsetY = y + GuiStyle.moduleGap() * sScale;
        delta = 0.0f;
        deltaV = 0.0f;

        boolean hideDisabled = GuiStyle.isHideDisabled();
        java.util.List<Module> categoryModules = ModuleManager.getModules(category);
        for (int i = 0; i < categoryModules.size(); i++) {
            Module m = categoryModules.get(i);
            boolean matchesSearch = key.isEmpty() || m.getName().toLowerCase().contains(key.toLowerCase());
            if (matchesSearch && !(hideDisabled && !m.isEnable())) {
                ModuleComponent module = new ModuleComponent(this, m, x + GuiStyle.moduleGap() * sScale, offsetY);
                module.setWidth(width - 2.0f * GuiStyle.moduleGap() * sScale);
                modules.add(module);
                offsetY += module.getHeight() + GuiStyle.moduleGap() * sScale;
            }
        }
    }
}

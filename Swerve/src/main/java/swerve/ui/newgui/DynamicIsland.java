package swerve.ui.newgui;

import com.mojang.blaze3d.vertex.PoseStack;

import swerve.managers.ModuleManager;
import swerve.modules.api.setting.FloatSetting;
import swerve.modules.impl.render.ViewModel;
import swerve.utils.color.ColorUtils;

import net.minecraft.client.Minecraft;

import java.util.Locale;

// Динамический остров с вкладками UI / INTERFACE / CUSTOM / HANDS.
// Островок можно зажать и откинуть вверх: из-под него выезжает плашка кастомизации рук
// (Сила и Скорость удара) — быстрые ручки без поиска модуля по панелям.
public class DynamicIsland {

    private static final float BASE_ISLAND_HEIGHT = 20.25f;
    private static final float PADDING = 8.0f;
    private static final float EXPAND_TRAVEL = 26.0f;
    private static final float PLATE_PADDING = 8.0f;
    private static final float PLATE_ROW_HEIGHT = 16.0f;

    private final ClickGuiScreen parent;
    private float x, y, width, height;
    private float animation;
    private boolean open;

    private IslandTab selectedTab = IslandTab.UI;
    private final float[] tabAnimations = new float[4];

    // Откидывание вверх: держим островок, тянем, на отпускании он прилипает к позиции.
    private boolean islandDragging;
    private float grabOffsetY;
    private float dragProgress;
    private boolean expanded;
    private float expandAnim;

    // -1 — ползунки не трогаем, 0 — Сила, 1 — Скорость.
    private int sliderDrag = -1;

    private float plateX, plateY, plateWidth, plateHeight;

    public DynamicIsland(ClickGuiScreen parent) {
        this.parent = parent;
    }

    public void init() {
        float sScale = parent.getResolutionScale();
        this.height = BASE_ISLAND_HEIGHT * sScale;
        calculateWidth(sScale);
        this.x = Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2.0f - width / 2.0f;
        this.y = 15.0f * sScale;
        this.animation = 0.0f;
        this.open = true;
        this.expanded = false;
        this.expandAnim = 0.0f;
        this.islandDragging = false;
        this.sliderDrag = -1;
    }

    private void calculateWidth(float sScale) {
        float totalWidth = PADDING * 2.0f * sScale;
        IslandTab[] tabs = IslandTab.values();
        for (int i = 0; i < tabs.length; i++) {
            float textWidth = GuiFonts.SF_MEDIUM.getWidth(tabs[i].getText(), 7.0f * sScale);
            totalWidth += textWidth + 16.0f * sScale;
            if (i < tabs.length - 1) {
                totalWidth += GuiStyle.islandButtonGap() * sScale;
            }
        }
        this.width = totalWidth;
    }

    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks, float fadeAlpha) {
        animation = Mathf.fast(animation, open ? 1.0f : 0.0f, 12.0f);
        if (animation < 0.01f && !open) {
            return;
        }

        float sScale = parent.getResolutionScale();
        float baseY = 15.0f * sScale;
        float travel = EXPAND_TRAVEL * sScale;

        calculateWidth(sScale);
        x = Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2.0f - width / 2.0f;

        if (islandDragging) {
            expandAnim = dragProgress;
        } else {
            y = Mathf.fast(y, expanded ? baseY - travel : baseY, 16.0f);
            expandAnim = Mathf.fast(expandAnim, expanded ? 1.0f : 0.0f, 14.0f);
        }

        GuiRender.drawGlass(stack, x, y, width, height, height / 2.0f, GuiStyle.islandBlur(), 0.0f);

        float buttonY = y + 2.625f * sScale;
        float buttonH = 15.0f * sScale;
        float currentX = x + PADDING * sScale;

        IslandTab[] tabs = IslandTab.values();
        for (int i = 0; i < tabs.length; i++) {
            IslandTab tab = tabs[i];
            float textWidth = GuiFonts.SF_MEDIUM.getWidth(tab.getText(), 7.0f * sScale);
            float buttonW = textWidth + 16.0f * sScale;

            boolean hovered = mouseX >= currentX && mouseX <= currentX + buttonW
                    && mouseY >= buttonY && mouseY <= buttonY + buttonH;
            boolean active = selectedTab == tab;

            float targetAnim = (hovered || active) ? 1.0f : 0.0f;
            tabAnimations[i] = Mathf.fast(tabAnimations[i], targetAnim, 14.0f);

            if (tabAnimations[i] > 0.01f) {
                int bgColor = ColorUtils.multAlpha(GuiTheme.clientColor(),
                        (active ? 0.1f : 0.07f) * animation * tabAnimations[i] * fadeAlpha);
                GuiRender.roundedRect(stack, currentX, buttonY, buttonW, buttonH, GuiStyle.islandButtonRadius() * sScale, bgColor);
            }

            float textSize = 7.0f * sScale;
            int textColor = ColorUtils.multAlpha(active ? GuiTheme.clientColor() : GuiTheme.textColor(),
                    (active ? 0.9f : 0.6f) * animation * fadeAlpha);
            float textX = currentX + buttonW / 2.0f - textWidth / 2.0f;
            float textY = buttonY + buttonH / 2.0f
                    - GuiFonts.SF_MEDIUM.getHeight(tab.getText(), textSize) / 2.0f;

            GuiFonts.SF_MEDIUM.draw(stack, tab.getText(), textX, textY, textColor, textSize);

            currentX += buttonW + GuiStyle.islandButtonGap() * sScale;
        }

        if (expandAnim > 0.01f) {
            renderCustomizePlate(stack, sScale, fadeAlpha);
        }
    }

    // Плашка кастомизации выезжает из-под островка: высота и прозрачность растут с expandAnim.
    private void renderCustomizePlate(PoseStack stack, float sScale, float fadeAlpha) {
        ViewModel viewModel = ModuleManager.get(ViewModel.class);
        if (viewModel == null) {
            return;
        }

        float pad = PLATE_PADDING * sScale;
        float rowH = PLATE_ROW_HEIGHT * sScale;
        float fullHeight = pad * 2.0f + rowH * 2.0f;

        plateX = x;
        plateWidth = width;
        plateY = y + height + 4.0f * sScale;
        plateHeight = fullHeight * expandAnim;

        float alpha = expandAnim * animation * fadeAlpha;
        GuiRender.drawGlass(stack, plateX, plateY, plateWidth, plateHeight, 6.0f * sScale,
                GuiStyle.islandBlur(), 0.0f);

        if (expandAnim < 0.2f) {
            return;
        }

        float labelSize = 7.0f * sScale;
        for (int i = 0; i < 2; i++) {
            FloatSetting setting = i == 0 ? viewModel.strengthSetting() : viewModel.speedSetting();
            String label = i == 0 ? "Сила" : "Скорость";
            String value = String.format(Locale.ROOT, "%.2f", setting.get());

            float rowY = plateY + pad + i * rowH;
            float labelWidth = GuiFonts.SF_MEDIUM.getWidth(label, labelSize);
            float valueWidth = GuiFonts.SF_MEDIUM.getWidth(value, labelSize);

            float trackX = plateX + pad + labelWidth + 6.0f * sScale;
            float trackWidth = plateWidth - pad * 2.0f - labelWidth - valueWidth - 14.0f * sScale;
            float trackH = 4.0f * sScale;
            float trackY = rowY + rowH / 2.0f - trackH / 2.0f;

            float t = (setting.get() - setting.getMin()) / Math.max(1.0E-4f, setting.getMax() - setting.getMin());
            t = t < 0.0f ? 0.0f : (t > 1.0f ? 1.0f : t);

            GuiRender.roundedRect(stack, trackX, trackY, trackWidth, trackH, trackH / 2.0f,
                    ColorUtils.multAlpha(GuiTheme.textColor(), 0.18f * alpha));
            GuiRender.roundedRect(stack, trackX, trackY, Math.max(trackH, trackWidth * t), trackH, trackH / 2.0f,
                    ColorUtils.multAlpha(GuiTheme.clientColor(), 0.9f * alpha));

            float knobX = trackX + trackWidth * t;
            float knobH = trackH + 3.0f * sScale;
            GuiRender.roundedRect(stack, knobX - 2.5f * sScale, trackY - 1.5f * sScale, 5.0f * sScale, knobH,
                    knobH / 2.0f, ColorUtils.multAlpha(GuiTheme.clientColor(), alpha));

            GuiFonts.SF_MEDIUM.draw(stack, label, plateX + pad, rowY + rowH / 2.0f - labelSize / 2.0f,
                    ColorUtils.multAlpha(GuiTheme.textColor(), 0.8f * alpha), labelSize);
            GuiFonts.SF_MEDIUM.draw(stack, value, plateX + plateWidth - pad - valueWidth,
                    rowY + rowH / 2.0f - labelSize / 2.0f,
                    ColorUtils.multAlpha(GuiTheme.textColor(), 0.6f * alpha), labelSize);
        }
    }

    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        if (button != 0) {
            return false;
        }

        float sScale = parent.getResolutionScale();
        float buttonY = y + 5.625f * sScale;
        float buttonH = 15.0f * sScale;
        float currentX = x + PADDING * sScale;

        boolean insideIsland = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;

        IslandTab[] tabs = IslandTab.values();
        for (int i = 0; i < tabs.length; i++) {
            IslandTab tab = tabs[i];
            float textWidth = GuiFonts.SF_MEDIUM.getWidth(tab.getText(), 7.0f * sScale);
            float buttonW = textWidth + 16.0f * sScale;

            if (mouseX >= currentX && mouseX <= currentX + buttonW
                    && mouseY >= buttonY && mouseY <= buttonY + buttonH) {
                if (selectedTab != tab) {
                    selectedTab = tab;
                    GuiStyle.playSound("mode_pick.wav", 0.1f);
                    parent.onTabChanged(tab);
                }
                return true;
            }

            currentX += buttonW + GuiStyle.islandButtonGap() * sScale;
        }

        // Держим за корпус островка — его можно откинуть вверх.
        if (insideIsland) {
            islandDragging = true;
            grabOffsetY = mouseY - y;
            return true;
        }

        if (expandAnim > 0.5f && mouseX >= plateX && mouseX <= plateX + plateWidth
                && mouseY >= plateY && mouseY <= plateY + plateHeight) {
            float pad = PLATE_PADDING * sScale;
            float rowH = PLATE_ROW_HEIGHT * sScale;
            for (int i = 0; i < 2; i++) {
                float rowY = plateY + pad + i * rowH;
                if (mouseY >= rowY && mouseY <= rowY + rowH) {
                    sliderDrag = i;
                    applySlider(i, mouseX, sScale);
                    return true;
                }
            }
            return true;
        }

        return false;
    }

    public boolean mouseDragged(int mouseX, int mouseY) {
        float sScale = parent.getResolutionScale();

        if (islandDragging) {
            float baseY = 15.0f * sScale;
            float travel = EXPAND_TRAVEL * sScale;
            float target = mouseY - grabOffsetY;
            y = target < baseY - travel ? baseY - travel : (target > baseY ? baseY : target);
            dragProgress = (baseY - y) / travel;
            return true;
        }

        if (sliderDrag >= 0) {
            applySlider(sliderDrag, mouseX, sScale);
            return true;
        }

        return false;
    }

    public void mouseReleased() {
        if (islandDragging) {
            expanded = dragProgress > 0.5f;
            islandDragging = false;
            GuiStyle.playSound("mode_pick.wav", 0.08f);
        }
        sliderDrag = -1;
    }

    private void applySlider(int index, int mouseX, float sScale) {
        ViewModel viewModel = ModuleManager.get(ViewModel.class);
        if (viewModel == null) {
            return;
        }

        FloatSetting setting = index == 0 ? viewModel.strengthSetting() : viewModel.speedSetting();
        float pad = PLATE_PADDING * sScale;
        float labelWidth = GuiFonts.SF_MEDIUM.getWidth(index == 0 ? "Сила" : "Скорость", 7.0f * sScale);
        float valueWidth = GuiFonts.SF_MEDIUM.getWidth(String.format(Locale.ROOT, "%.2f", setting.get()), 7.0f * sScale);
        float trackX = plateX + pad + labelWidth + 6.0f * sScale;
        float trackWidth = plateWidth - pad * 2.0f - labelWidth - valueWidth - 14.0f * sScale;
        if (trackWidth <= 1.0f) {
            return;
        }

        float t = (mouseX - trackX) / trackWidth;
        t = t < 0.0f ? 0.0f : (t > 1.0f ? 1.0f : t);
        setting.setValue(setting.getMin() + t * (setting.getMax() - setting.getMin()));
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

    public IslandTab getSelectedTab() {
        return selectedTab;
    }

    public void setSelectedTab(IslandTab tab) {
        this.selectedTab = tab;
    }

    public enum IslandTab {
        UI("UI"),
        INTERFACE("INTERFACE"),
        CLIENT("CUSTOM"),
        HANDS("HANDS");

        private final String text;

        IslandTab(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }
}

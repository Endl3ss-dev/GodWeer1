package godweer.ui.newgui;

import com.mojang.blaze3d.vertex.PoseStack;

import godweer.utils.color.ColorUtils;

import net.minecraft.client.Minecraft;

// Динамический остров с вкладками UI / INTERFACE / CUSTOM / HUD SETTING.
public class DynamicIsland {

    private static final float BASE_ISLAND_HEIGHT = 20.25f;
    private static final float PADDING = 8.0f;

    private final ClickGuiScreen parent;
    private float x, y, width, height;
    private float animation;
    private boolean open;

    private IslandTab selectedTab = IslandTab.UI;
    private final float[] tabAnimations = new float[4];

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

        calculateWidth(sScale);
        x = Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2.0f - width / 2.0f;

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
    }

    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (button != 0) {
            return;
        }

        float sScale = parent.getResolutionScale();
        float buttonY = y + 5.625f * sScale;
        float buttonH = 15.0f * sScale;
        float currentX = x + PADDING * sScale;

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
                break;
            }

            currentX += buttonW + GuiStyle.islandButtonGap() * sScale;
        }
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
        HUD("HUD SETTING");

        private final String text;

        IslandTab(String text) {
            this.text = text;
        }

        public String getText() {
            return this.text;
        }
    }
}

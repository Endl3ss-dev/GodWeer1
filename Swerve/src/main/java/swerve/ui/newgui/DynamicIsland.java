package swerve.ui.newgui;

import com.mojang.blaze3d.vertex.PoseStack;

import swerve.utils.color.ColorUtils;

import net.minecraft.client.Minecraft;

public class DynamicIsland {

    private static final float BASE_HEIGHT = 20.25F;
    private static final float PADDING = 8.0F;
    private static final IslandTab[] TABS = IslandTab.values();

    private final ClickGuiScreen parent;
    private final float[] tabAnimations = new float[TABS.length];

    private float x;
    private float y;
    private float width;
    private float height;
    private float animation;
    private boolean open;
    private IslandTab selectedTab = IslandTab.UI;

    public DynamicIsland(ClickGuiScreen parent) {
        this.parent = parent;
    }

    public void init() {
        float scale = parent.getResolutionScale();
        height = BASE_HEIGHT * scale;
        calculateWidth(scale);
        x = Minecraft.getInstance().getWindow().getGuiScaledWidth() * 0.5F - width * 0.5F;
        y = 15.0F * scale;
        animation = 0.0F;
        open = true;
    }

    private void calculateWidth(float scale) {
        float totalWidth = PADDING * 2.0F * scale;
        float gap = GuiStyle.islandButtonGap() * scale;
        for (int i = 0; i < TABS.length; i++) {
            totalWidth += GuiFonts.SF_MEDIUM.getWidth(TABS[i].getText(), 7.0F * scale) + 16.0F * scale;
            if (i + 1 < TABS.length) {
                totalWidth += gap;
            }
        }
        width = totalWidth;
    }

    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks, float fadeAlpha) {
        animation = Mathf.fast(animation, open ? 1.0F : 0.0F, 12.0F);
        if (animation < 0.01F && !open) {
            return;
        }

        float scale = parent.getResolutionScale();
        calculateWidth(scale);
        x = Minecraft.getInstance().getWindow().getGuiScaledWidth() * 0.5F - width * 0.5F;
        y = 15.0F * scale;

        GuiRender.drawGlass(stack, x, y, width, height, height * 0.5F, GuiStyle.islandBlur(), 0.0F);

        float buttonY = y + 2.625F * scale;
        float buttonHeight = 15.0F * scale;
        float currentX = x + PADDING * scale;
        float gap = GuiStyle.islandButtonGap() * scale;
        float textSize = 7.0F * scale;

        for (int i = 0; i < TABS.length; i++) {
            IslandTab tab = TABS[i];
            float textWidth = GuiFonts.SF_MEDIUM.getWidth(tab.getText(), textSize);
            float buttonWidth = textWidth + 16.0F * scale;
            boolean hovered = mouseX >= currentX && mouseX <= currentX + buttonWidth
                    && mouseY >= buttonY && mouseY <= buttonY + buttonHeight;
            boolean active = selectedTab == tab;

            tabAnimations[i] = Mathf.fast(tabAnimations[i], hovered || active ? 1.0F : 0.0F, 14.0F);
            float tabAlpha = tabAnimations[i];
            if (tabAlpha > 0.01F) {
                int background = ColorUtils.multAlpha(GuiTheme.clientColor(),
                        (active ? 0.10F : 0.07F) * animation * tabAlpha * fadeAlpha);
                GuiRender.roundedRect(stack, currentX, buttonY, buttonWidth, buttonHeight,
                        GuiStyle.islandButtonRadius() * scale, background);
            }

            int textColor = ColorUtils.multAlpha(active ? GuiTheme.clientColor() : GuiTheme.textColor(),
                    (active ? 0.9F : 0.6F) * animation * fadeAlpha);
            float textX = currentX + (buttonWidth - textWidth) * 0.5F;
            float textY = buttonY + buttonHeight * 0.5F
                    - GuiFonts.SF_MEDIUM.getHeight(tab.getText(), textSize) * 0.5F;
            GuiFonts.SF_MEDIUM.draw(stack, tab.getText(), textX, textY, textColor, textSize);

            currentX += buttonWidth + gap;
        }
    }

    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        if (button != 0) {
            return false;
        }

        float scale = parent.getResolutionScale();
        float buttonY = y + 2.625F * scale;
        float buttonHeight = 15.0F * scale;
        float currentX = x + PADDING * scale;
        float gap = GuiStyle.islandButtonGap() * scale;

        for (int i = 0; i < TABS.length; i++) {
            IslandTab tab = TABS[i];
            float buttonWidth = GuiFonts.SF_MEDIUM.getWidth(tab.getText(), 7.0F * scale) + 16.0F * scale;
            if (mouseX >= currentX && mouseX <= currentX + buttonWidth
                    && mouseY >= buttonY && mouseY <= buttonY + buttonHeight) {
                if (selectedTab != tab) {
                    selectedTab = tab;
                    GuiStyle.playSound("mode_pick.wav", 0.1F);
                    parent.onTabChanged(tab);
                }
                return true;
            }
            currentX += buttonWidth + gap;
        }

        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public boolean mouseDragged(int mouseX, int mouseY) {
        return false;
    }

    public void mouseReleased() {
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
        if (tab != null) {
            selectedTab = tab;
        }
    }

    public enum IslandTab {
        UI("UI"),
        INTERFACE("INTERFACE"),
        CLIENT("CUSTOM");

        private final String text;

        IslandTab(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }
}

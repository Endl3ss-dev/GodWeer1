package godweer.ui.newgui;

import com.mojang.blaze3d.vertex.PoseStack;

import godweer.utils.color.ColorUtils;
import godweer.utils.math.Mathf;
import godweer.utils.render.draw.RenderDraw;
import godweer.utils.render.draw.Round;
import godweer.utils.render.font.GuiFont;
import godweer.utils.render.font.GuiFonts;
import godweer.utils.render.font.GuiTheme;

import net.minecraft.client.Minecraft;

public class DynamicIsland {

    private final ClickGuiScreen parent;
    private float x, y, width, height;
    private float animation = 0f;
    private boolean open = false;

    private IslandTab selectedTab = IslandTab.UI;
    private final float[] tabAnimations = new float[4];

    private static final float BASE_ISLAND_HEIGHT = 20.25f;
    private static final float BUTTON_SPACING = 4f;
    private static final float PADDING = 8f;

    public DynamicIsland(ClickGuiScreen parent) {
        this.parent = parent;
    }

    public void init() {
        float sScale = parent.getResolutionScale();
        this.height = BASE_ISLAND_HEIGHT * sScale;
        calculateWidth(sScale);
        this.x = Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2f - width / 2f;
        this.y = 15 * sScale;
        animation = 0f;
        open = true;
    }

    private void calculateWidth(float sScale) {
        float totalWidth = PADDING * 2 * sScale;
        IslandTab[] tabs = IslandTab.values();
        for (int i = 0; i < tabs.length; i++) {
            float textWidth = GuiFonts.SF_MEDIUM.getWidth(tabs[i].getText(), 7f * sScale);
            totalWidth += textWidth + 16 * sScale;
            if (i < tabs.length - 1) {
                totalWidth += BUTTON_SPACING * sScale;
            }
        }
        this.width = totalWidth;
    }

    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks, float fadeAlpha) {
        animation = Mathf.fast(animation, open ? 1f : 0f, 12f);
        if (animation < 0.01f && !open) return;

        float sScale = parent.getResolutionScale();
        GuiTheme theme = GuiTheme.getInstance();

        calculateWidth(sScale);
        x = Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2f - width / 2f;

        RenderDraw.CustomStyled.drawGui(stack, x, y, width, height, height / 2f, true, (int) (theme.blurStrength() * fadeAlpha));

        float buttonY = y + 2.625f * sScale;
        float buttonH = 15f * sScale;
        float currentX = x + PADDING * sScale;

        IslandTab[] tabs = IslandTab.values();
        for (int i = 0; i < tabs.length; i++) {
            IslandTab tab = tabs[i];
            float textWidth = GuiFonts.SF_MEDIUM.getWidth(tab.getText(), 7f * sScale);
            float buttonW = textWidth + 16 * sScale;

            boolean hovered = mouseX >= currentX && mouseX <= currentX + buttonW && mouseY >= buttonY && mouseY <= buttonY + buttonH;
            boolean active = selectedTab == tab;

            float targetAnim = (hovered || active) ? 1f : 0f;
            tabAnimations[i] = Mathf.fast(tabAnimations[i], targetAnim, 14f);

            if (tabAnimations[i] > 0.01f) {
                int bgColor = ColorUtils.multAlpha(theme.clientColor(), (active ? 0.1f : 0.07f) * animation * tabAnimations[i] * fadeAlpha);
                RenderDraw.Rounded.roundedRect(stack, currentX, buttonY, buttonW, buttonH, bgColor, Round.of(8.5f * sScale));
            }

            float textSize = 7f * sScale;
            int textColor = ColorUtils.multAlpha(active ? theme.clientColor() : theme.textColor(), (active ? 0.9f : 0.6f) * animation * fadeAlpha);
            float textX = currentX + buttonW / 2f - textWidth / 2f;
            float textY = buttonY + buttonH / 2f - GuiFonts.SF_MEDIUM.getHeight(tab.getText(), textSize) / 2f;

            GuiFonts.SF_MEDIUM.draw(stack, tab.getText(), textX, textY, textColor, textSize);

            currentX += buttonW + BUTTON_SPACING * sScale;
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (button != 0) return;

        float sScale = parent.getResolutionScale();
        float buttonY = y + 5.625f * sScale;
        float buttonH = 15f * sScale;
        float currentX = x + PADDING * sScale;

        IslandTab[] tabs = IslandTab.values();
        for (int i = 0; i < tabs.length; i++) {
            IslandTab tab = tabs[i];
            float textWidth = GuiFonts.SF_MEDIUM.getWidth(tab.getText(), 7f * sScale);
            float buttonW = textWidth + 16 * sScale;

            if (mouseX >= currentX && mouseX <= currentX + buttonW && mouseY >= buttonY && mouseY <= buttonY + buttonH) {
                if (selectedTab != tab) {
                    selectedTab = tab;
                    parent.onTabChanged(tab);
                }
                break;
            }

            currentX += buttonW + BUTTON_SPACING * sScale;
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

    public void setSelectedTab(IslandTab tab) {
        this.selectedTab = tab;
    }

    public enum IslandTab {
        UI("UI"),
        CLIENT("CUSTOM"),
        HUD("HUD SETTING"),
        SEARCH("SEARCH");

        private final String text;

        IslandTab(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }
}

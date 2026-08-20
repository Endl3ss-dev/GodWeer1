package fun.volon.client.ui.clickgui;

import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.glfw.GLFW;
import fun.volon.api.utils.color.ColorUtils;
import fun.volon.api.utils.input.CursorManager;
import fun.volon.api.utils.math.HoveringUtils;
import fun.volon.api.utils.render.RenderUtils;
import fun.volon.api.utils.render.fonts.msdf.Font;
import fun.volon.api.utils.render.fonts.msdf.Fonts;

public class SearchField {
    private float x, y, width, height;
    public String text = "";
    private boolean isFocused;
    private final String placeholder;

    private static final float ICON_BOX_W = 17f;
    private static final float RADIUS = 4f;

    public SearchField(String placeholder) {
        this.placeholder = placeholder;
    }

    public void setBounds(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
        if (HoveringUtils.isHovered(mouseX, mouseY, x, y, width, height)) {
            CursorManager.requestIBeam();
        }

        RenderUtils.drawBlur(matrices, x, y, width, height, RADIUS, 20f, ColorUtils.rgba(75, 75, 75, 255));
        RenderUtils.drawRoundedRect(matrices, x, y, width, height, RADIUS,
                ColorUtils.setAlphaColor(ColorUtils.getColorWindowBg(), 30));

        RenderUtils.drawRoundedRect(matrices, x, y, ICON_BOX_W, height, RADIUS, RADIUS, RADIUS, RADIUS,
                ColorUtils.setAlphaColor(ColorUtils.getColorHeaderBg(), 80));

        Font iconFont = Fonts.getFont("iconsminced", 20);
        if (iconFont != null) {
            float iconW = iconFont.getStringWidth("l");
            float iconX = x + (ICON_BOX_W - iconW) / 2f + 1f;
            float iconY = y + (height / 2f) - 4.5f;
            Component.drawText(matrices, iconFont, "l", iconX, iconY,
                    ColorUtils.setAlphaColor(ColorUtils.getColorIcons(), 200));
        }

        Font textFont = Fonts.getFont("sf_regular", 14);
        String textToDraw = text.isEmpty() && !isFocused ? placeholder : text;
        String cursor = isFocused && System.currentTimeMillis() % 1000 > 500 ? "|" : "";
        int textColor = text.isEmpty() && !isFocused
                ? ColorUtils.setAlphaColor(ColorUtils.getColorInactiveText(), 255)
                : ColorUtils.setAlphaColor(ColorUtils.getColorText(), 255);
        Component.drawText(matrices, textFont, textToDraw + cursor,
                x + ICON_BOX_W + 4f, y + (height / 2f) - 3.5f, textColor);
    }

    public void charTyped(char codePoint, int modifiers) {
        if (isFocused && text.length() < 15) {
            text += codePoint;
        }
    }

    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        if (isFocused) {
            if (keyCode == GLFW.GLFW_KEY_BACKSPACE && !text.isEmpty()) {
                text = text.substring(0, text.length() - 1);
            }
            if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_ESCAPE) {
                isFocused = false;
            }
        }
    }

    public void mouseClicked(double mouseX, double mouseY, int button) {
        isFocused = HoveringUtils.isHovered(mouseX, mouseY, x, y, width, height);
    }

    public boolean isEmpty() {
        return text.isEmpty();
    }

    public String getText() {
        return text;
    }
}

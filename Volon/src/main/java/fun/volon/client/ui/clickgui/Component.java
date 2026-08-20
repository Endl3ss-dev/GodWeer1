package fun.volon.client.ui.clickgui;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.util.math.MatrixStack;
import fun.volon.api.utils.animation.gui.GuiAnimation;
import fun.volon.api.utils.animation.gui.GuiEasing;
import fun.volon.api.utils.render.fonts.msdf.Font;
import fun.volon.api.utils.scissor.ScissorUtils;

@Getter
@Setter
public abstract class Component {

    /** Compensation to align text the same way polyak does, despite Font.draw's broken baseline. */
    private static float alignY(Font font, float y) {
        return y + 3.5f - 0.126f * font.getSize();
    }

    public static void drawText(MatrixStack matrices, Font font, String text,
                                float x, float y, int color) {
        if (font == null) return;
        font.draw(matrices, text, x, alignY(font, y), color);
    }

    public static void drawTextScrolling(MatrixStack matrices, Font font, String text,
                                         float x, float y, float maxWidth, int color) {
        if (font == null || text == null || text.isEmpty()) return;
        float ay = alignY(font, y);
        float textWidth = font.getStringWidth(text);
        if (textWidth <= maxWidth + 0.5f) {
            font.draw(matrices, text, x, ay, color);
            return;
        }
        double timeSec = System.currentTimeMillis() / 1000.0;
        double period = 5.5;
        double phase = (timeSec % period) / period;
        double tri = phase < 0.5 ? phase * 2.0 : 2.0 - phase * 2.0;
        float eased = (float) (tri * tri * (3.0 - 2.0 * tri));
        float maxOffset = Math.max(0f, textWidth - maxWidth - 6f);
        float offset = eased * maxOffset;

        ScissorUtils.push();
        ScissorUtils.setFromComponentCoordinates(x, y - 5f, maxWidth, 20f);
        font.draw(matrices, text, x - offset, ay, color);
        ScissorUtils.pop();
    }

    public float x, y, width, height;

    private final GuiAnimation alphaAnim = new GuiAnimation(GuiEasing.BACK_OUT, 550);
    private final GuiAnimation alphaAnimSetting = new GuiAnimation(GuiEasing.CUBIC_OUT, 280);
    private final GuiAnimation alphaAnimBack = new GuiAnimation(GuiEasing.CUBIC_OUT, 280);

    public boolean isVisible() {
        return true;
    }

    public void setBounds(float x, float y, float width) {
        this.x = x;
        this.y = y;
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
    }

    public void mouseClicked(double mouseX, double mouseY, int button) {
    }

    public void mouseReleased(double mouseX, double mouseY, int button) {
    }

    public void keyPressed(int keyCode, int scanCode, int modifiers) {
    }

    public void charTyped(char chr, int modifiers) {
    }
}

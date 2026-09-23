package godweer.utils.render.font;

import com.mojang.blaze3d.vertex.PoseStack;

import godweer.utils.render.msdf.FontRenderer;
import godweer.utils.render.msdf.Fonts;

public final class GuiFont {

    private final Fonts source;

    public GuiFont(Fonts source) {
        this.source = source;
    }

    public void draw(PoseStack stack, String text, float x, float y, int color, float size) {
        FontRenderer renderer = source.get((int) size);
        if (renderer == null || text == null || text.isEmpty()) {
            return;
        }
        renderer.draw(stack, text, x, y, color);
    }

    public void drawCenter(PoseStack stack, String text, float x, float y, int color, float size) {
        FontRenderer renderer = source.get((int) size);
        if (renderer == null || text == null || text.isEmpty()) {
            return;
        }
        renderer.drawCenteredString(stack, text, x, y, color);
    }

    public float getWidth(String text, float size) {
        if (text == null || text.isEmpty()) {
            return 0.0f;
        }
        FontRenderer renderer = source.get((int) size);
        return renderer == null ? 0.0f : renderer.getWidth(text);
    }

    public float getHeight(String text, float size) {
        FontRenderer renderer = source.get((int) size);
        return renderer == null ? 0.0f : renderer.getHeight();
    }
}

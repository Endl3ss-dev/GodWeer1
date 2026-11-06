package tech.lightclient.client.utils.render.main.text;

import net.minecraft.text.Text;

import java.util.Objects;

public final class FontObject {
    public final String id;

    public FontObject(String id) {
        this.id = Objects.requireNonNull(id, "id");
    }

    public float getWidth(String text, float size) {
        if (text == null || text.isEmpty() || size <= 0f) {
            return 0f;
        }
        FontRenderer renderer = FontRegistry.createTextRenderer(this);
        return renderer.measureText(text, size).width;
    }

    public float getHeight(String text, float size) {
        if (text == null || text.isEmpty() || size <= 0f) {
            return 0f;
        }
        FontRenderer renderer = FontRegistry.createTextRenderer(this);
        return renderer.measureText(text, size).height;
    }

    public float getWidth(Text text, float size) {
        if (text == null || size <= 0f) {
            return 0f;
        }
        String content = text.getString();
        if (content.isEmpty()) return 0f;

        FontRenderer renderer = FontRegistry.createTextRenderer(this);
        return renderer.measureText(content, size).width;
    }

    public float getHeight(Text text, float size) {
        if (text == null || size <= 0f) {
            return 0f;
        }
        String content = text.getString();
        if (content.isEmpty()) return 0f;

        FontRenderer renderer = FontRegistry.createTextRenderer(this);
        return renderer.measureText(content, size).height;
    }

    public float getLineHeight(float size) {
        if (size <= 0f) {
            return 0f;
        }
        return FontRegistry.lineHeight(this, size);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FontObject that = (FontObject) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "FontObject(" + id + ")";
    }
}

package tech.lightclient.ui.hud.star;

import tech.lightclient.client.utils.render.main.core.Renderer2D;
import tech.lightclient.client.utils.render.main.text.FontObject;

public class SNum {
    private boolean drawZero;
    private int color;
    private final float offset;
    private final FontObject font;
    private final String[] numbers = new String[]{"", ""};
    private final String[] prevs = new String[]{"", ""};
    private final SAnim[] animations;
    private float lastOffset;

    public SNum(FontObject font, float offset, long speed, SEasing easing) {
        this.font = font;
        this.offset = offset;
        this.animations = new SAnim[]{new SAnim(speed, easing), new SAnim(speed, easing)};
        this.color = 0xFFFFFFFF;
    }

    public void render(Renderer2D r, float x, float y) {
        for (SAnim animation : this.animations) {
            animation.update(1.0f);
        }
        r.text(this.font, x, y + this.offset * this.animations[0].getValue(), size(), prevs[0], withFade(color, 1.0f - this.animations[0].getValue()));
        r.text(this.font, x, y - this.offset + this.offset * this.animations[0].getValue(), size(), numbers[0], withFade(color, this.animations[0].getValue()));
        r.text(this.font, x + this.lastOffset, y + this.offset * this.animations[1].getValue(), size(), prevs[1], withFade(color, 1.0f - this.animations[1].getValue()));
        r.text(this.font, x + this.font.getWidth(this.numbers[0], size()), y - this.offset + this.offset * this.animations[1].getValue(), size(),
                numbers[1], withFade(color, this.animations[1].getValue()));
    }

    private float size() {
        return 6.0f;
    }

    public float getWidth() {
        return this.font.getWidth(this.numbers[0] + this.numbers[1], size());
    }

    public void update(int updated) {
        String first = String.valueOf(updated / 10);
        String second = String.valueOf(updated % 10);
        if (!second.equals(this.numbers[1])) {
            this.lastOffset = this.font.getWidth(this.numbers[0], size());
            this.prevs[1] = this.numbers[1];
            this.numbers[1] = second;
            this.animations[1].setValue(0.0f);
        }

        if (!first.equals(this.numbers[0])) {
            this.prevs[0] = this.numbers[0];
            this.numbers[0] = this.drawZero ? first : (first.equals("0") ? "" : first);
            this.animations[0].setValue(0.0f);
        }
    }

    public void settings(boolean drawZero, int argb) {
        this.drawZero = drawZero;
        this.color = argb;
    }

    private static int withFade(int argb, float factor) {
        int a = (argb >>> 24) & 0xFF;
        int na = Math.max(0, Math.min(255, Math.round(a * factor)));
        return (na << 24) | (argb & 0x00FFFFFF);
    }
}

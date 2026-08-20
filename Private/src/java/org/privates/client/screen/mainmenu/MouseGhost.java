package org.privates.client.screen.mainmenu;

public class MouseGhost {
    public final float x, y;
    public final long birthTime;
    public static final long LIFE = 1000;

    public MouseGhost(float x, float y, long now) {
        this.x = x;
        this.y = y;
        this.birthTime = now;
    }

    public float alpha(long now) {
        float t = (now - birthTime) / (float) LIFE;
        if (t < 0.0f) return 0f;
        if (t > 1.0f) return 0f;
        if (t < 0.4f) return t / 0.4f;
        return (1.0f - (t - 0.4f) / 0.6f);
    }
}
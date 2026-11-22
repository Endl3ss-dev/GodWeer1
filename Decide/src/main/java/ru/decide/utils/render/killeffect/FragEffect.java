/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1109
 *  net.minecraft.class_1113
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_332
 *  net.minecraft.class_3414
 */
package ru.decide.utils.render.killeffect;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1109;
import net.minecraft.class_1113;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_3414;
import ru.decide.utils.render.Draw;

@Environment(value=EnvType.CLIENT)
public final class FragEffect {
    private static final FragEffect INSTANCE = new FragEffect();
    private static final class_2960 VIGNETTE = class_2960.method_60655((String)"decide", (String)"textures/killeffect/frag_vignette.png");
    private static final class_2960 SOUND = class_2960.method_60655((String)"decide", (String)"killeffect");
    private long startedAt;

    private FragEffect() {
    }

    public static FragEffect get() {
        return INSTANCE;
    }

    public void trigger(float durationSeconds) {
        this.startedAt = System.currentTimeMillis();
        class_310 mc = class_310.method_1551();
        if (mc.method_1483() != null) {
            mc.method_1483().method_4873((class_1113)class_1109.method_4757((class_3414)class_3414.method_47908((class_2960)SOUND), (float)1.0f, (float)6.0f));
        }
    }

    public void clear() {
        this.startedAt = 0L;
    }

    public boolean isActive() {
        return this.startedAt > 0L && this.progress() < 1.0f;
    }

    private float progress() {
        return this.progress(1.15f);
    }

    private float progress(float durationSeconds) {
        if (this.startedAt <= 0L) {
            return 1.0f;
        }
        float totalMs = Math.max(1.0f, durationSeconds * 1000.0f);
        return FragEffect.clamp((float)(System.currentTimeMillis() - this.startedAt) / totalMs, 0.0f, 1.0f);
    }

    public float strength() {
        if (this.startedAt <= 0L) {
            return 1.0f;
        }
        float p = this.progress();
        if (p >= 1.0f) {
            this.startedAt = 0L;
            return 1.0f;
        }
        return FragEffect.clamp(1.0f - FragEffect.easeProgress(p) * 0.28f, 0.1f, 1.0f);
    }

    public void render(class_332 context, float durationSeconds) {
        if (this.startedAt <= 0L) {
            return;
        }
        float p = this.progress(durationSeconds);
        if (p >= 1.0f) {
            this.startedAt = 0L;
            return;
        }
        float eased = FragEffect.easeProgress(p);
        int r = Math.round(255.0f * (1.0f - eased / 1.25f));
        int b = Math.round(190.0f * eased);
        int color = 0xFF000000 | r << 16 | r << 8 | b;
        float w = context.method_51421();
        float h = context.method_51443();
        float spread = FragEffect.smoothStep(FragEffect.clamp(p / 0.9f, 0.0f, 1.0f));
        float margin = 1.0f - spread;
        float padX = w * 0.38f * margin;
        float padY = h * 0.38f * margin;
        Draw.glowTexture(VIGNETTE, -padX, -padY, w + padX * 2.0f, h + padY * 2.0f, color);
    }

    private static float easeProgress(float t) {
        if (t <= 0.22f) {
            float v = FragEffect.clamp(t / 0.35f, 0.0f, 1.0f);
            return 1.0f - (float)Math.pow(1.0f - v, 3.0);
        }
        float v = FragEffect.clamp((t - 0.22f) / 0.78f, 0.0f, 1.0f);
        return 1.0f - FragEffect.smoothStep(v);
    }

    private static float smoothStep(float t) {
        return t * t * (3.0f - 2.0f * t);
    }

    private static float clamp(float v, float min, float max) {
        return Math.max(min, Math.min(max, v));
    }
}


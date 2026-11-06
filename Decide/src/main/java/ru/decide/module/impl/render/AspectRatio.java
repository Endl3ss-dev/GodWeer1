/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.module.impl.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.module.api.ModuleInfo;
import ru.decide.module.api.settings.impl.BooleanSetting;
import ru.decide.module.api.settings.impl.ModeSetting;
import ru.decide.module.api.settings.impl.SliderSetting;
import ru.decide.utils.other.Instance;

@ModuleInfo(name="Aspect Ratio", desc="\u0424\u0438\u043a\u0441\u0438\u0440\u0443\u0435\u0442 \u0441\u043e\u043e\u0442\u043d\u043e\u0448\u0435\u043d\u0438\u0435 \u0441\u0442\u043e\u0440\u043e\u043d \u043c\u0438\u0440\u0430 (16:9, 21:9, 4:3 \u0438 \u0441\u0432\u043e\u0451)", category=Category.VISUALS)
@Environment(value=EnvType.CLIENT)
public final class AspectRatio
extends Module {
    public static final String[] MODES = new String[]{"16:9", "16:10", "21:9", "4:3", "32:9", "3:2", "5:4", "\u0421\u0432\u043e\u0439"};
    public final ModeSetting mode = new ModeSetting((Module)this, "\u0421\u043e\u043e\u0442\u043d\u043e\u0448\u0435\u043d\u0438\u0435", MODES);
    public final SliderSetting customWidth = new SliderSetting(this, "\u0428\u0438\u0440\u0438\u043d\u0430", 16.0f, 1.0f, 64.0f, 0.1f).setVisible(() -> this.mode.is("\u0421\u0432\u043e\u0439"));
    public final SliderSetting customHeight = new SliderSetting(this, "\u0412\u044b\u0441\u043e\u0442\u0430", 9.0f, 1.0f, 64.0f, 0.1f).setVisible(() -> this.mode.is("\u0421\u0432\u043e\u0439"));
    public final BooleanSetting smooth = new BooleanSetting((Module)this, "\u041f\u043b\u0430\u0432\u043d\u043e", false);
    public final SliderSetting smoothSpeed = new SliderSetting(this, "\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c \u0441\u0433\u043b\u0430\u0436\u0438\u0432\u0430\u043d\u0438\u044f", 8.0f, 1.0f, 20.0f, 0.1f).setVisible(() -> this.smooth.getValue());
    private float smoothedRatio = -1.0f;
    private long lastFrameNanos;

    public static AspectRatio getInstance() {
        try {
            return Instance.get(AspectRatio.class);
        }
        catch (RuntimeException ignored) {
            return null;
        }
    }

    @Override
    protected void onEnable() {
        super.onEnable();
        this.smoothedRatio = -1.0f;
        this.lastFrameNanos = 0L;
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        this.smoothedRatio = -1.0f;
        this.lastFrameNanos = 0L;
    }

    public float targetRatio() {
        return switch (this.mode.getValue()) {
            case "16:10" -> 1.6f;
            case "21:9" -> 2.3333335f;
            case "4:3" -> 1.3333334f;
            case "32:9" -> 3.5555556f;
            case "3:2" -> 1.5f;
            case "5:4" -> 1.25f;
            case "\u0421\u0432\u043e\u0439" -> this.customWidth.getValue().floatValue() / Math.max(0.1f, this.customHeight.getValue().floatValue());
            default -> 1.7777778f;
        };
    }

    public void beginFrame() {
        float target = this.targetRatio();
        if (!AspectRatio.valid(target)) {
            this.smoothedRatio = -1.0f;
            this.lastFrameNanos = 0L;
            return;
        }
        if (!this.smooth.getValue().booleanValue()) {
            this.smoothedRatio = target;
            this.lastFrameNanos = 0L;
            return;
        }
        long now = System.nanoTime();
        if (this.smoothedRatio <= 0.0f || this.lastFrameNanos == 0L) {
            this.smoothedRatio = target;
            this.lastFrameNanos = now;
            return;
        }
        float dt = Math.min(0.25f, (float)(now - this.lastFrameNanos) / 1.0E9f);
        this.lastFrameNanos = now;
        float t = 1.0f - (float)Math.exp(-Math.max(0.1f, this.smoothSpeed.getValue().floatValue()) * dt);
        this.smoothedRatio += (target - this.smoothedRatio) * t;
        if (Math.abs(target - this.smoothedRatio) < 1.0E-4f) {
            this.smoothedRatio = target;
        }
    }

    public float currentRatio() {
        return this.smoothedRatio > 0.0f ? this.smoothedRatio : this.targetRatio();
    }

    private static boolean valid(float ratio) {
        return ratio > 0.0f && !Float.isNaN(ratio) && !Float.isInfinite(ratio);
    }
}


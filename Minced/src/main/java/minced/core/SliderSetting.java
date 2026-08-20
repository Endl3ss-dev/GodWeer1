/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.util.math.MathHelper
 */
package minced.core;

import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import minced.core.MathUtil;
import minced.core.Setting;
import minced.core.Animation;
import minced.core.Easings;
import net.minecraft.util.math.MathHelper;

public class SliderSetting
extends Setting {
    public float field7742;
    public float field7743;
    public float field7744;
    float field7745;
    static long field7746 = 80L;
    Animation field7747;
    Supplier field7748;
    Supplier I;
    static float field7749 = 1.0E-6f;

    public SliderSetting(String param1, float param2, float param3, float param4, float param5, BooleanSupplier param6, Supplier param7) {
        this(param1, param2, param3, param4, param5, param6, null, param7);
    }

    public SliderSetting(String param1, float param2, float param3, float param4, float param5) {
        this(param1, param2, param3, param4, param5, null, null, null);
    }

    private SliderSetting(String param1, float param2, float param3, float param4, float param5, BooleanSupplier param6, Supplier param7, Supplier param8) {
        super(param1, Float.valueOf(param2));
        this.field7742 = param3;
        this.field7743 = param4;
        this.field7744 = param2;
        this.field7745 = param5;
        float range = param4 - param3;
        this.field7747 = new Animation(Easings.field11969, range == 0.0f ? 0.0f : (param2 - param3) / range, field7746);
        if (param6 != null) {
            this.visibleWhen(param6);
        }
        this.field7748 = param7;
        this.I = param8;
    }

    public SliderSetting(String param1, float param2, float param3, float param4, float param5, BooleanSupplier param6) {
        this(param1, param2, param3, param4, param5, param6, null, null);
    }

    public void method1906(Float param1) {
        if (param1 == null || !Float.isFinite(param1.floatValue())) {
            return;
        }
        Object suppliedMin = this.field7748 != null ? this.field7748.get() : null;
        Object suppliedMax = this.I != null ? this.I.get() : null;
        float v2 = suppliedMin instanceof Number ? ((Number)suppliedMin).floatValue() : this.field7742;
        float v3 = suppliedMax instanceof Number ? ((Number)suppliedMax).floatValue() : this.field7743;
        if (v2 > v3) {
            float swap = v2;
            v2 = v3;
            v3 = swap;
        }
        float v4 = MathUtil.method1868(param1.floatValue(), this.field7745);
        float v5 = MathHelper.clamp((float)v4, (float)v2, (float)v3);
        if (Math.abs(((Float)this.getValue()).floatValue() - v5) < field7749) {
            return;
        }
        super.setValue(Float.valueOf(v5));
    }

    public SliderSetting(String param1, float param2, float param3, float param4, float param5, SliderSetting param6) {
        this(param1, param2, param3, param4, param5, null, null, () -> {
            Objects.requireNonNull(param6);
            return param6.getValue();
        });
        param6.method1907(this);
    }

    public SliderSetting method1907(SliderSetting param1) {
        Objects.requireNonNull(param1);
        this.field7748 = param1::getValue;
        return this;
    }

    public SliderSetting(String param1, float param2, float param3, float param4, float param5, BooleanSupplier param6, SliderSetting param7) {
        this(param1, param2, param3, param4, param5, param6, null, () -> param7.getValue());
        param7.method1907(this);
    }

    public SliderSetting(String param1, float param2, float param3, float param4, float param5, Supplier param6) {
        this(param1, param2, param3, param4, param5, null, null, param6);
    }
}




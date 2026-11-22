/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.ui.util.anim;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.ui.util.anim.Easing;

@Environment(value=EnvType.CLIENT)
public final class Easings {
    public static final Easing LINEAR = x -> x;
    public static final Easing QUAD_OUT = x -> 1.0 - (1.0 - x) * (1.0 - x);
    public static final Easing CUBIC_OUT = x -> 1.0 - Math.pow(1.0 - x, 3.0);
    public static final Easing QUART_OUT = x -> 1.0 - Math.pow(1.0 - x, 4.0);
    public static final Easing EXPO_IN = x -> x == 0.0 ? 0.0 : Math.pow(2.0, 10.0 * (x - 1.0));
    public static final Easing EXPO_OUT = x -> x == 1.0 ? 1.0 : 1.0 - Math.pow(2.0, -10.0 * x);
    public static final Easing EXPO_IN_OUT = x -> {
        if (x == 0.0) {
            return 0.0;
        }
        if (x == 1.0) {
            return 1.0;
        }
        return x < 0.5 ? Math.pow(2.0, 20.0 * x - 10.0) / 2.0 : (2.0 - Math.pow(2.0, -20.0 * x + 10.0)) / 2.0;
    };
    public static final Easing SINE_OUT = x -> Math.sin(x * Math.PI / 2.0);
    public static final Easing BACK_OUT = x -> {
        double c1 = 1.70158;
        double c3 = c1 + 1.0;
        return 1.0 + c3 * Math.pow(x - 1.0, 3.0) + c1 * Math.pow(x - 1.0, 2.0);
    };

    private Easings() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}


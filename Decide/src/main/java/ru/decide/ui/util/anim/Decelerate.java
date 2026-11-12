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
import ru.decide.ui.util.anim.Animation;

@Environment(value=EnvType.CLIENT)
public class Decelerate
extends Animation {
    @Override
    public double calculation(double x) {
        return 1.0 - (1.0 - x) * (1.0 - x);
    }

    @Override
    public Decelerate setValue(double d) {
        super.setValue(d);
        return this;
    }

    @Override
    public Decelerate setMs(int n) {
        super.setMs(n);
        return this;
    }
}


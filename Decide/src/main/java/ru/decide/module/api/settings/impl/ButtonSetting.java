/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.module.api.settings.impl;

import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.module.api.Module;
import ru.decide.module.api.settings.Setting;
import ru.decide.utils.animation.Animation;
import ru.decide.utils.animation.Easings;

@Environment(value=EnvType.CLIENT)
public class ButtonSetting
extends Setting<Boolean> {
    private final Runnable action;
    public final Animation pressAnim = new Animation();

    public ButtonSetting(Module parent, String name, Runnable action) {
        super(parent, name, false);
        this.action = action;
    }

    public void press() {
        if (this.action != null) {
            this.action.run();
        }
        this.pressAnim.set(1.0);
        this.pressAnim.run(0.0, (double)0.45f, Easings.QUAD_OUT);
    }

    public ButtonSetting setVisible(Supplier<Boolean> value) {
        return (ButtonSetting)super.setVisible(value);
    }
}


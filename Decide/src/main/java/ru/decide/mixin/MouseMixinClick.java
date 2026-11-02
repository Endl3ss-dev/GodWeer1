/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_11910
 *  net.minecraft.class_310
 *  net.minecraft.class_312
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package ru.decide.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_11910;
import net.minecraft.class_310;
import net.minecraft.class_312;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.decide.module.impl.utils.EffectSounds;
import ru.decide.module.impl.utils.SoundsKey;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_312.class})
public abstract class MouseMixinClick {
    @Inject(method={"method_1601"}, at={@At(value="TAIL")})
    private void decide$mouseSound(long window, class_11910 button, int action, CallbackInfo info) {
        EffectSounds.noteUserActivity();
        if (action != 1) {
            return;
        }
        class_310 client = class_310.method_1551();
        if (window != client.method_22683().method_4490()) {
            return;
        }
        int code = button.comp_4801();
        if (code != 0 && code != 1) {
            return;
        }
        SoundsKey.playMouse();
    }
}


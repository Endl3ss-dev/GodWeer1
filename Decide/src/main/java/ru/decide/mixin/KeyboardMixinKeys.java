/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_11905
 *  net.minecraft.class_11908
 *  net.minecraft.class_309
 *  net.minecraft.class_310
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package ru.decide.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_11905;
import net.minecraft.class_11908;
import net.minecraft.class_309;
import net.minecraft.class_310;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.decide.module.impl.utils.EffectSounds;
import ru.decide.module.impl.utils.SoundsKey;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_309.class})
public abstract class KeyboardMixinKeys {
    @Inject(method={"method_1466"}, at={@At(value="TAIL")})
    private void decide$keySound(long window, int action, class_11908 key, CallbackInfo info) {
        EffectSounds.noteUserActivity();
        class_310 client = class_310.method_1551();
        if (window != client.method_22683().method_4490()) {
            return;
        }
        SoundsKey.keyEvent(key.comp_4795(), action);
    }

    @Inject(method={"method_1457"}, at={@At(value="HEAD")})
    private void decide$charSound(long window, class_11905 input, CallbackInfo info) {
        class_310 client = class_310.method_1551();
        if (window != client.method_22683().method_4490()) {
            return;
        }
        SoundsKey.noteChar(input);
    }
}


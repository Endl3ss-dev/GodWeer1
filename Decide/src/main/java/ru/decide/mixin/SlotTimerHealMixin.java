/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1309
 *  net.minecraft.class_1799
 *  net.minecraft.class_1937
 *  net.minecraft.class_310
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package ru.decide.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1309;
import net.minecraft.class_1799;
import net.minecraft.class_1937;
import net.minecraft.class_310;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.decide.utils.render.SlotTimerOverlay;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_1799.class})
public abstract class SlotTimerHealMixin {
    @Inject(method={"method_7910"}, at={@At(value="HEAD")}, require=0)
    private void decide$finishUsing(class_1937 world, class_1309 user, CallbackInfoReturnable<class_1799> cir) {
        if (class_310.method_1551().field_1724 == user) {
            SlotTimerOverlay.onFinishUsing((class_1799)this);
        }
    }
}


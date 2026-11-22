/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1799
 *  net.minecraft.class_327
 *  net.minecraft.class_332
 *  org.jetbrains.annotations.Nullable
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package ru.decide.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1799;
import net.minecraft.class_327;
import net.minecraft.class_332;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.decide.utils.render.SlotTimerOverlay;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_332.class})
public abstract class SlotTimerDrawMixin {
    @Inject(method={"method_51432"}, at={@At(value="TAIL")}, require=0)
    private void decide$slotTimer(class_327 font, class_1799 stack, int x, int y, @Nullable String countOverride, CallbackInfo ci) {
        SlotTimerOverlay.draw((class_332)this, font, stack, x, y);
    }
}


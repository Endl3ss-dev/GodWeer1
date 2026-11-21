/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_11398
 *  net.minecraft.class_4184
 *  net.minecraft.class_638
 *  net.minecraft.class_7285
 *  net.minecraft.class_9779
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package ru.decide.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_11398;
import net.minecraft.class_4184;
import net.minecraft.class_638;
import net.minecraft.class_7285;
import net.minecraft.class_9779;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.decide.module.impl.render.WorldTweaks;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_11398.class})
public class AtmosphericFogModifierMixin {
    @Inject(method={"method_71654"}, at={@At(value="RETURN")}, cancellable=true)
    private void onGetFogColor(class_638 world, class_4184 camera, int viewDistance, float tickProgress, CallbackInfoReturnable<Integer> cir) {
        WorldTweaks worldTweaks = WorldTweaks.get();
        if (worldTweaks == null || !worldTweaks.isEnabled() || !worldTweaks.fogs.getValue().booleanValue()) {
            return;
        }
        cir.setReturnValue((Object)worldTweaks.getColor());
    }

    @Inject(method={"method_42591"}, at={@At(value="RETURN")})
    private void onApplyStartEnd(class_7285 fogData, class_4184 camera, class_638 world, float viewDistance, class_9779 tickCounter, CallbackInfo ci) {
        WorldTweaks worldTweaks = WorldTweaks.get();
        if (worldTweaks == null || !worldTweaks.isEnabled() || !worldTweaks.fogs.getValue().booleanValue()) {
            return;
        }
        float end = worldTweaks.fog.getValue().floatValue();
        fogData.field_60582 = Math.min(2.0f, end * 0.25f);
        fogData.field_60584 = end;
        fogData.field_60099 = end;
        fogData.field_60100 = end;
    }
}


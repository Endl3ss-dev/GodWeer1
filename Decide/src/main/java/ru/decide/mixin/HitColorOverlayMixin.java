/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1043
 *  net.minecraft.class_4608
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package ru.decide.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1043;
import net.minecraft.class_4608;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.decide.utils.render.HitColorOverlay;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_4608.class})
public abstract class HitColorOverlayMixin {
    @Shadow
    @Final
    private class_1043 field_21013;

    @Inject(method={"<init>"}, at={@At(value="TAIL")}, require=0)
    private void decide$attachHitColor(CallbackInfo ci) {
        HitColorOverlay.attach(this.field_21013);
    }
}


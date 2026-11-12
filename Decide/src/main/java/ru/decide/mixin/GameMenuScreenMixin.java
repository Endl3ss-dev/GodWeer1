/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_332
 *  net.minecraft.class_4185
 *  net.minecraft.class_433
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package ru.decide.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_332;
import net.minecraft.class_4185;
import net.minecraft.class_433;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.decide.module.impl.player.PvpSafe;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_433.class})
public class GameMenuScreenMixin {
    @Shadow
    private class_4185 field_40792;

    @Inject(method={"method_25394"}, at={@At(value="HEAD")})
    private void pvpSafe$disableExit(class_332 context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (this.field_40792 != null) {
            this.field_40792.field_22763 = !PvpSafe.isActive();
        }
    }
}


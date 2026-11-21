/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1268
 *  net.minecraft.class_1657
 *  net.minecraft.class_310
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package ru.decide.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1268;
import net.minecraft.class_1657;
import net.minecraft.class_310;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.decide.cosmetics.melee.MeleeController;
import ru.decide.cosmetics.melee.MeleeRules;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_310.class})
public abstract class MeleeMinecraftClientMixin {
    @Inject(method={"method_1536"}, at={@At(value="HEAD")})
    private void decide$meleePrimaryAttack(CallbackInfoReturnable<Boolean> cir) {
        if (MeleeRules.handlesAnyHand()) {
            MeleeController.getInstance().onAttack(true);
        }
    }

    @Inject(method={"method_1583"}, at={@At(value="RETURN")})
    private void decide$meleeSecondaryAttack(CallbackInfo ci) {
        class_310 client = class_310.method_1551();
        if (client.field_1724 == null) {
            return;
        }
        if (MeleeRules.usesHand((class_1657)client.field_1724, class_1268.field_5810)) {
            return;
        }
        if (MeleeRules.handlesAnyHand()) {
            MeleeController.getInstance().onAttack(false);
        }
    }
}


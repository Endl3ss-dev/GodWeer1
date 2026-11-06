/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1268
 *  net.minecraft.class_1269
 *  net.minecraft.class_1297
 *  net.minecraft.class_1657
 *  net.minecraft.class_3966
 *  net.minecraft.class_636
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
import net.minecraft.class_1269;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_3966;
import net.minecraft.class_636;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.decide.module.impl.render.custompet.entity.CustomPetEntity;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_636.class})
public abstract class CustomPetClientPlayerInteractionMixin {
    @Inject(method={"method_2918"}, at={@At(value="HEAD")}, cancellable=true, require=0)
    private void decide_cancelCustomPetAttack(class_1657 player, class_1297 target, CallbackInfo ci) {
        if (target instanceof CustomPetEntity) {
            ci.cancel();
        }
    }

    @Inject(method={"method_2905"}, at={@At(value="HEAD")}, cancellable=true, require=0)
    private void decide_cancelCustomPetInteract(class_1657 player, class_1297 target, class_1268 hand, CallbackInfoReturnable<class_1269> cir) {
        if (target instanceof CustomPetEntity) {
            cir.setReturnValue((Object)class_1269.field_5811);
        }
    }

    @Inject(method={"method_2917"}, at={@At(value="HEAD")}, cancellable=true, require=0)
    private void decide_cancelCustomPetInteractAt(class_1657 player, class_1297 target, class_3966 hitResult, class_1268 hand, CallbackInfoReturnable<class_1269> cir) {
        if (target instanceof CustomPetEntity) {
            cir.setReturnValue((Object)class_1269.field_5811);
        }
    }
}


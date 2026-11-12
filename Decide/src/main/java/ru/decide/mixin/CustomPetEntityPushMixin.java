/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1297
 *  net.minecraft.class_310
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package ru.decide.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1297;
import net.minecraft.class_310;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.decide.module.impl.render.custompet.entity.CustomPetEntity;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_1297.class})
public abstract class CustomPetEntityPushMixin {
    @Inject(method={"method_5697"}, at={@At(value="HEAD")}, cancellable=true, require=0)
    private void decide_cancelCustomPetPush(class_1297 other, CallbackInfo ci) {
        boolean petInvolved;
        class_310 mc = class_310.method_1551();
        if (mc == null || mc.field_1724 == null) {
            return;
        }
        class_1297 self = (class_1297)this;
        boolean playerInvolved = self == mc.field_1724 || other == mc.field_1724;
        boolean bl = petInvolved = self instanceof CustomPetEntity || other instanceof CustomPetEntity;
        if (playerInvolved && petInvolved) {
            ci.cancel();
        }
    }
}


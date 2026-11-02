/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.ModifyExpressionValue
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10185
 *  net.minecraft.class_241
 *  net.minecraft.class_315
 *  net.minecraft.class_743
 *  net.minecraft.class_744
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package ru.decide.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10185;
import net.minecraft.class_241;
import net.minecraft.class_315;
import net.minecraft.class_743;
import net.minecraft.class_744;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.decide.manager.event_impl.EventMoveInput;
import ru.decide.manager.event_impl.InputEvent;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_743.class})
public abstract class KeyboardInputMixin
extends class_744 {
    @Shadow
    @Final
    private class_315 field_3902;

    @ModifyExpressionValue(method={"method_3129"}, at={@At(value="NEW", target="(ZZZZZZZ)Lnet/minecraft/class_10185;")})
    private class_10185 tickHook(class_10185 original) {
        InputEvent event = new InputEvent(original);
        event.hook();
        return original;
    }

    @Unique
    private float abobaGetMovementMultiplier(boolean positive, boolean negative) {
        if (positive == negative) {
            return 0.0f;
        }
        return positive ? 1.0f : -1.0f;
    }

    @Inject(method={"method_3129"}, at={@At(value="FIELD", target="Lnet/minecraft/class_743;field_54155:Lnet/minecraft/class_10185;", ordinal=0, shift=At.Shift.AFTER)}, cancellable=true)
    public void injectInputEvent(CallbackInfo ci) {
        EventMoveInput event = new EventMoveInput(this.field_54155, this.abobaGetMovementMultiplier(this.field_54155.comp_3159(), this.field_54155.comp_3160()), this.abobaGetMovementMultiplier(this.field_54155.comp_3161(), this.field_54155.comp_3162()));
        event.hook();
        if (event.isCancelled()) {
            return;
        }
        float forward = event.getForward();
        float strafe = event.getStrafe();
        this.field_55868 = new class_241(strafe, forward).method_35581();
        this.field_54155 = new class_10185(forward > 0.0f, forward < 0.0f, strafe > 0.0f, strafe < 0.0f, this.field_3902.field_1903.method_1434(), this.field_3902.field_1832.method_1434(), this.field_3902.field_1867.method_1434());
        ci.cancel();
    }
}


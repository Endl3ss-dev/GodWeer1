/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.ModifyExpressionValue
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1297
 *  net.minecraft.class_1657
 *  net.minecraft.class_310
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package ru.decide.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_310;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.decide.Client;
import ru.decide.module.impl.render.ShaderEsp;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_1297.class})
public class EntityMixin {
    @ModifyExpressionValue(method={"method_5784"}, at={@At(value="INVOKE", target="Lnet/minecraft/class_1297;method_66247()Z", ordinal=1)})
    public boolean fixFalldistanceValue(boolean original) {
        if (this == class_310.method_1551().field_1724) {
            return true;
        }
        return original;
    }

    @Inject(method={"method_5851"}, at={@At(value="RETURN")}, cancellable=true)
    private void espMarkGlowing(CallbackInfoReturnable<Boolean> cir) {
        if (((Boolean)cir.getReturnValue()).booleanValue()) {
            return;
        }
        ShaderEsp esp = ShaderEsp.getInstance();
        if (esp != null && esp.isEnabled() && ShaderEsp.isEspTarget((class_1297)this)) {
            cir.setReturnValue((Object)true);
        }
    }

    @Inject(method={"method_22861"}, at={@At(value="HEAD")}, cancellable=true, require=0)
    private void espFriendTeamColor(CallbackInfoReturnable<Integer> cir) {
        ShaderEsp esp = ShaderEsp.getInstance();
        if (esp == null || !esp.isEnabled()) {
            return;
        }
        class_1297 self = (class_1297)this;
        if (!(self instanceof class_1657)) {
            return;
        }
        class_1657 p = (class_1657)self;
        if (Client.get().friendManager().isFriend(p.method_5477().getString())) {
            cir.setReturnValue((Object)0x55FF55);
        }
    }
}


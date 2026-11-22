/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10197
 *  net.minecraft.class_12249
 *  net.minecraft.class_1921
 *  net.minecraft.class_2960
 *  net.minecraft.class_4608
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.ModifyArg
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package ru.decide.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10197;
import net.minecraft.class_12249;
import net.minecraft.class_1921;
import net.minecraft.class_2960;
import net.minecraft.class_4608;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import ru.decide.utils.render.HitColorEquipmentOverlay;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_10197.class})
public abstract class HitColorEquipmentMixin
implements HitColorEquipmentOverlay {
    @Unique
    private int decide$overlay = class_4608.field_21444;

    @ModifyArg(method={"method_64078"}, at=@At(value="INVOKE", target="Lnet/minecraft/class_11785;method_73490(Lnet/minecraft/class_3879;Ljava/lang/Object;Lnet/minecraft/class_4587;Lnet/minecraft/class_1921;IIILnet/minecraft/class_1058;ILnet/minecraft/class_11683$class_11792;)V"), index=5, require=0)
    private int decide$overlay(int vanilla) {
        return this.decide$overlay != class_4608.field_21444 ? this.decide$overlay : vanilla;
    }

    @Redirect(method={"method_64078"}, at=@At(value="INVOKE", target="Lnet/minecraft/class_12249;method_75966(Lnet/minecraft/class_2960;)Lnet/minecraft/class_1921;"), require=0)
    private class_1921 decide$layer(class_2960 texture) {
        return this.decide$overlay != class_4608.field_21444 ? class_12249.method_75994((class_2960)texture) : class_12249.method_75966((class_2960)texture);
    }

    @Override
    public void decide$setOverlay(int overlay) {
        this.decide$overlay = overlay;
    }
}


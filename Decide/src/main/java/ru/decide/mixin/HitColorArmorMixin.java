/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10034
 *  net.minecraft.class_10197
 *  net.minecraft.class_11659
 *  net.minecraft.class_4587
 *  net.minecraft.class_4608
 *  net.minecraft.class_970
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
import net.minecraft.class_10034;
import net.minecraft.class_10197;
import net.minecraft.class_11659;
import net.minecraft.class_4587;
import net.minecraft.class_4608;
import net.minecraft.class_970;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.decide.module.impl.render.HitColor;
import ru.decide.utils.render.HitColorEquipmentOverlay;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_970.class})
public abstract class HitColorArmorMixin {
    @Shadow
    @Final
    private class_10197 field_54183;

    @Inject(method={"method_17157"}, at={@At(value="HEAD")}, require=0)
    private void decide$armorOverlay(class_4587 matrices, class_11659 queue, int light, class_10034 state, float limbAngle, float limbDistance, CallbackInfo ci) {
        HitColor module = HitColor.getInstance();
        int overlay = class_4608.field_21444;
        if (module != null && module.isEnabled() && module.armor.getValue().booleanValue() && state.field_53460) {
            overlay = class_4608.method_23624((float)0.0f, (boolean)true);
        }
        ((HitColorEquipmentOverlay)this.field_54183).decide$setOverlay(overlay);
    }
}


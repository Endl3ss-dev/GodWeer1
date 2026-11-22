/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1007
 *  net.minecraft.class_11659
 *  net.minecraft.class_2960
 *  net.minecraft.class_4587
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package ru.decide.mixin.cosmetics;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1007;
import net.minecraft.class_11659;
import net.minecraft.class_2960;
import net.minecraft.class_4587;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.decide.cosmetics.maksai.hats.WatchFirstPerson;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_1007.class})
public abstract class WatchFirstPersonMixin {
    @Inject(method={"method_4220"}, at={@At(value="TAIL")}, require=0)
    private void decide$watchOnRightArm(class_4587 matrices, class_11659 queue, int light, class_2960 skinTexture, boolean sleeveVisible, CallbackInfo ci) {
        WatchFirstPerson.render((class_1007)this, matrices, queue, light, false);
    }

    @Inject(method={"method_4221"}, at={@At(value="TAIL")}, require=0)
    private void decide$watchOnLeftArm(class_4587 matrices, class_11659 queue, int light, class_2960 skinTexture, boolean sleeveVisible, CallbackInfo ci) {
        WatchFirstPerson.render((class_1007)this, matrices, queue, light, true);
    }
}


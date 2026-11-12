/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10017
 *  net.minecraft.class_1297
 *  net.minecraft.class_1309
 *  net.minecraft.class_4604
 *  net.minecraft.class_897
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package ru.decide.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10017;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_4604;
import net.minecraft.class_897;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.decide.module.impl.render.ModelCollapse;
import ru.decide.utils.render.killeffect.swing.SwingShatterEffect;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_897.class})
public class EntityRendererMixin<T extends class_1297, S extends class_10017> {
    @Inject(method={"method_3933"}, at={@At(value="HEAD")}, cancellable=true)
    private void hideCollapsedModel(class_1297 entity, class_4604 frustum, double camX, double camY, double camZ, CallbackInfoReturnable<Boolean> cir) {
        class_1309 living;
        if (entity instanceof class_1309 && ModelCollapse.shouldHideEntity(living = (class_1309)entity)) {
            cir.setReturnValue((Object)false);
            return;
        }
        if (SwingShatterEffect.shouldHide(entity)) {
            cir.setReturnValue((Object)false);
        }
    }
}


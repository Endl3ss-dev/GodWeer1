/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10442
 *  net.minecraft.class_1799
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.ModifyVariable
 */
package ru.decide.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10442;
import net.minecraft.class_1799;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import ru.decide.cosmetics.itemskins.ItemSkinRegistry;
import ru.decide.module.impl.render.swordreplacer.SwordReplacer;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_10442.class})
public abstract class ItemModelManagerMixin {
    @ModifyVariable(method={"method_65598"}, at=@At(value="HEAD"), argsOnly=true, ordinal=0)
    private class_1799 decidevisuals$replaceRenderedSword(class_1799 stack) {
        return ItemSkinRegistry.getRenderStack(SwordReplacer.getRenderStack(stack));
    }
}


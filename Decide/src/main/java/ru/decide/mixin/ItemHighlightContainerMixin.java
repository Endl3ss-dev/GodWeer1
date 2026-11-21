/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1735
 *  net.minecraft.class_332
 *  net.minecraft.class_465
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package ru.decide.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1735;
import net.minecraft.class_332;
import net.minecraft.class_465;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.decide.module.impl.render.ItemHighlight;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_465.class})
public abstract class ItemHighlightContainerMixin {
    @Inject(method={"method_2385"}, at={@At(value="HEAD")}, require=0)
    private void onDrawSlot(class_332 context, class_1735 slot, int mouseX, int mouseY, CallbackInfo ci) {
        int argb;
        ItemHighlight module;
        if (slot != null && (module = ItemHighlight.getInstance()) != null && (argb = module.backgroundFor(slot.method_7677(), false)) != 0) {
            module.drawSlotBackground(context, slot.field_7873, slot.field_7872, argb);
        }
    }
}


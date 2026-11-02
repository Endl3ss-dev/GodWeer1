/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1309
 *  net.minecraft.class_332
 *  net.minecraft.class_481
 *  net.minecraft.class_490
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package ru.decide.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1309;
import net.minecraft.class_332;
import net.minecraft.class_481;
import net.minecraft.class_490;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import ru.decide.module.impl.render.BetterMinecraft;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_490.class, class_481.class})
public abstract class InventoryScreenEntityMixin {
    @Redirect(method={"method_2389"}, at=@At(value="INVOKE", target="Lnet/minecraft/class_490;method_2486(Lnet/minecraft/class_332;IIIIIFFFLnet/minecraft/class_1309;)V"))
    private void decide$slideInventoryEntity(class_332 graphics, int x1, int y1, int x2, int y2, int size, float inflation, float mouseX, float mouseY, class_1309 entity) {
        int off = Math.round(BetterMinecraft.inventorySlideOffset());
        class_490.method_2486((class_332)graphics, (int)x1, (int)(y1 + off), (int)x2, (int)(y2 + off), (int)size, (float)inflation, (float)mouseX, (float)mouseY, (class_1309)entity);
    }
}


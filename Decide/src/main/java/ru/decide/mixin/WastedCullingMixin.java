/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_761
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.ModifyArg
 */
package ru.decide.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_761;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import ru.decide.module.impl.render.WastedDeath;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_761.class}, priority=1100)
public abstract class WastedCullingMixin {
    @ModifyArg(method={"method_74752"}, at=@At(value="INVOKE", target="Lnet/minecraft/class_8679;method_52834(ZLnet/minecraft/class_4184;Lnet/minecraft/class_4604;Ljava/util/List;Lit/unimi/dsi/fastutil/longs/LongOpenHashSet;)V"), index=0)
    private boolean decide$wastedDisableSmartCull(boolean smartCull) {
        return smartCull && !WastedDeath.isRunning();
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Pseudo
 *  org.spongepowered.asm.mixin.Shadow
 */
package ru.decide.mixin.velotune;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import ru.decide.optimization.velotune.compat.sodium.SodiumRenderSectionAccess;

@Environment(value=EnvType.CLIENT)
@Pseudo
@Mixin(targets={"net/caffeinemc/mods/sodium/client/render/chunk/RenderSection"}, remap=false)
abstract class SodiumRenderSectionMixin
implements SodiumRenderSectionAccess {
    SodiumRenderSectionMixin() {
    }

    @Shadow(remap=false)
    public abstract int getOriginY();

    @Override
    public int velotune$getOriginY() {
        return this.getOriginY();
    }
}


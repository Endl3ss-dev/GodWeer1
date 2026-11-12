/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.spongepowered.asm.mixin.Mixin
 */
package ru.decide.mixin.velotune;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;

@Environment(value=EnvType.CLIENT)
@Mixin(targets={"net/minecraft/client/render/entity/EntityRenderManager$LabelRenderer"}, remap=false)
abstract class LabelCommandsMixin {
    LabelCommandsMixin() {
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10041
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 */
package ru.decide.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10041;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import ru.decide.utils.render.killeffect.KillEffectColorAccess;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_10041.class})
public class LightningRenderStateMixin
implements KillEffectColorAccess {
    @Unique
    private Integer decide$killEffectColor;

    @Override
    public Integer decide$getKillEffectColor() {
        return this.decide$killEffectColor;
    }

    @Override
    public void decide$setKillEffectColor(Integer color) {
        this.decide$killEffectColor = color;
    }
}


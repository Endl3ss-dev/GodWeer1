/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10444$class_10446
 *  net.minecraft.class_10515
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package ru.decide.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10444;
import net.minecraft.class_10515;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_10444.class_10446.class})
public interface ItemLayerRenderStateAccessor {
    @Accessor(value="field_55349")
    public int[] decide$tints();

    @Accessor(value="field_55350")
    public class_10515<?> decide$specialModelType();
}


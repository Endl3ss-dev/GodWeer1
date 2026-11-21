/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.ModifyReturnValue
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_638$class_5271
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 */
package ru.decide.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_638;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import ru.decide.module.impl.render.WorldTweaks;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_638.class_5271.class})
public abstract class ClientWorldPropertiesMixin {
    @Shadow
    private long field_24438;
    @Shadow
    private long field_24439;

    @ModifyReturnValue(method={"method_217"}, at={@At(value="RETURN")})
    private long hookGetTime(long original) {
        return WorldTweaks.get().isEnabled() && WorldTweaks.get().times.getValue() != false ? (long)(WorldTweaks.get().time.getValue().floatValue() * 1000.0f) : this.field_24439;
    }
}


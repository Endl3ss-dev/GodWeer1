/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1291
 *  net.minecraft.class_1294
 *  net.minecraft.class_1297
 *  net.minecraft.class_5636
 *  net.minecraft.class_6880
 *  net.minecraft.class_7286
 *  org.jetbrains.annotations.Nullable
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package ru.decide.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1291;
import net.minecraft.class_1294;
import net.minecraft.class_1297;
import net.minecraft.class_5636;
import net.minecraft.class_6880;
import net.minecraft.class_7286;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.decide.module.impl.render.NoRender;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_7286.class})
public abstract class StatusEffectFogModifierMixin {
    @Shadow
    public abstract class_6880<class_1291> method_42590();

    @Inject(method={"method_42593"}, at={@At(value="HEAD")}, cancellable=true)
    private void onShouldApply(@Nullable class_5636 submersionType, class_1297 cameraEntity, CallbackInfoReturnable<Boolean> cir) {
        NoRender noRender = NoRender.getInstance();
        if (!noRender.isEnabled()) {
            return;
        }
        class_6880<class_1291> effect = this.method_42590();
        if (noRender.ignoreZalupa.getValue().booleanValue() && effect == class_1294.field_5919) {
            cir.setReturnValue((Object)false);
        }
        if (noRender.ignoreZalupa.getValue().booleanValue() && effect == class_1294.field_38092) {
            cir.setReturnValue((Object)false);
        }
    }
}


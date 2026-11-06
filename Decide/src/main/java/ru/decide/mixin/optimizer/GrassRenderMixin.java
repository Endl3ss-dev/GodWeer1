/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10889
 *  net.minecraft.class_1920
 *  net.minecraft.class_2246
 *  net.minecraft.class_2248
 *  net.minecraft.class_2338
 *  net.minecraft.class_2680
 *  net.minecraft.class_4587
 *  net.minecraft.class_4588
 *  net.minecraft.class_776
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package ru.decide.mixin.optimizer;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10889;
import net.minecraft.class_1920;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_776;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.decide.module.impl.render.Optimizer;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_776.class})
public abstract class GrassRenderMixin {
    @Inject(method={"method_3355"}, at={@At(value="HEAD")}, cancellable=true)
    private void decide$skipGrass(class_2680 state, class_2338 pos, class_1920 world, class_4587 matrices, class_4588 vertices, boolean hasBlockEntity, List<class_10889> parts, CallbackInfo ci) {
        if (!Optimizer.isNoGrass()) {
            return;
        }
        class_2248 block = state.method_26204();
        if (block == class_2246.field_10479 || block == class_2246.field_10214 || block == class_2246.field_10112 || block == class_2246.field_10313 || block == class_2246.field_10428 || block == class_2246.field_10376 || block == class_2246.field_10238) {
            ci.cancel();
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10017
 *  net.minecraft.class_10201
 *  net.minecraft.class_10442
 *  net.minecraft.class_1060
 *  net.minecraft.class_11697
 *  net.minecraft.class_11786
 *  net.minecraft.class_1297
 *  net.minecraft.class_310
 *  net.minecraft.class_315
 *  net.minecraft.class_327
 *  net.minecraft.class_330
 *  net.minecraft.class_5599
 *  net.minecraft.class_776
 *  net.minecraft.class_897
 *  net.minecraft.class_898
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package ru.decide.mixin;

import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10017;
import net.minecraft.class_10201;
import net.minecraft.class_10442;
import net.minecraft.class_1060;
import net.minecraft.class_11697;
import net.minecraft.class_11786;
import net.minecraft.class_1297;
import net.minecraft.class_310;
import net.minecraft.class_315;
import net.minecraft.class_327;
import net.minecraft.class_330;
import net.minecraft.class_5599;
import net.minecraft.class_776;
import net.minecraft.class_897;
import net.minecraft.class_898;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.decide.module.impl.render.custompet.render.CustomPetRendererBridge;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_898.class})
public abstract class CustomPetEntityRenderManagerMixin {
    @Inject(method={"<init>"}, at={@At(value="TAIL")})
    private void decide_bootstrapCustomPetRenderer(class_310 minecraft, class_1060 textureManager, class_10442 itemModelManager, class_330 mapRenderer, class_776 blockRenderManager, class_11697 atlasManager, class_327 font, class_315 options, Supplier<class_5599> modelSetSupplier, class_10201 equipmentManager, class_11786 playerSkinCache, CallbackInfo ci) {
        CustomPetRendererBridge.bootstrap((class_898)this, minecraft, blockRenderManager, itemModelManager, mapRenderer, atlasManager, font, modelSetSupplier, equipmentManager, playerSkinCache);
    }

    @Inject(method={"method_3953"}, at={@At(value="HEAD")}, cancellable=true, require=0)
    private <T extends class_1297> void decide_useCustomPetRenderer(T entity, CallbackInfoReturnable<class_897<? super T, ?>> cir) {
        class_897<T, ?> renderer = CustomPetRendererBridge.getCustomRenderer(entity);
        if (renderer != null) {
            cir.setReturnValue(renderer);
        }
    }

    @Inject(method={"method_68832"}, at={@At(value="HEAD")}, cancellable=true, require=0)
    private <S extends class_10017> void decide_useCustomPetStateRenderer(S renderState, CallbackInfoReturnable<class_897<?, ? super S>> cir) {
        class_897<S, ?> renderer = CustomPetRendererBridge.getCustomRenderer(renderState);
        if (renderer != null) {
            cir.setReturnValue(renderer);
        }
    }
}


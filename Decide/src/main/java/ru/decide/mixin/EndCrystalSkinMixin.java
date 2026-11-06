/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10014
 *  net.minecraft.class_11659
 *  net.minecraft.class_12075
 *  net.minecraft.class_243
 *  net.minecraft.class_4587
 *  net.minecraft.class_892
 *  net.minecraft.class_895
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package ru.decide.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10014;
import net.minecraft.class_11659;
import net.minecraft.class_12075;
import net.minecraft.class_243;
import net.minecraft.class_4587;
import net.minecraft.class_892;
import net.minecraft.class_895;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.decide.cosmetics.itemskins.EndCrystal3DRenderer;
import ru.decide.cosmetics.itemskins.ItemSkin;
import ru.decide.cosmetics.itemskins.ItemSkinRegistry;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_892.class})
public abstract class EndCrystalSkinMixin {
    @Inject(method={"method_3908"}, at={@At(value="HEAD")}, cancellable=true)
    private void decidevisuals$render3dCrystal(class_10014 state, class_4587 matrices, class_11659 queue, class_12075 camera, CallbackInfo ci) {
        ItemSkin skin = ItemSkinRegistry.equipped(ItemSkin.Kind.END_CRYSTAL);
        if (skin == null) {
            return;
        }
        matrices.method_22903();
        matrices.method_22905(2.0f, 2.0f, 2.0f);
        matrices.method_46416(0.0f, 0.55f, 0.0f);
        EndCrystal3DRenderer.render(state, matrices, queue);
        matrices.method_22909();
        class_243 beam = state.field_53312;
        if (beam != null) {
            float y = class_892.method_23155((float)state.field_53328);
            matrices.method_22903();
            matrices.method_22904(beam.field_1352, beam.field_1351, beam.field_1350);
            class_895.method_3917((float)((float)(-beam.field_1352)), (float)((float)(-beam.field_1351) + y), (float)((float)(-beam.field_1350)), (float)state.field_53328, (class_4587)matrices, (class_11659)queue, (int)state.field_61820);
            matrices.method_22909();
        }
        ci.cancel();
    }
}


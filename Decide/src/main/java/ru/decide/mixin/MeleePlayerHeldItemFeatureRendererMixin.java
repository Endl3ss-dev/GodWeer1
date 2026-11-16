/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10055
 *  net.minecraft.class_1007
 *  net.minecraft.class_10444
 *  net.minecraft.class_11659
 *  net.minecraft.class_1306
 *  net.minecraft.class_1799
 *  net.minecraft.class_310
 *  net.minecraft.class_4587
 *  net.minecraft.class_5697
 *  net.minecraft.class_591
 *  net.minecraft.class_742
 *  net.minecraft.class_746
 *  net.minecraft.class_7833
 *  org.joml.Quaternionfc
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package ru.decide.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10055;
import net.minecraft.class_1007;
import net.minecraft.class_10444;
import net.minecraft.class_11659;
import net.minecraft.class_1306;
import net.minecraft.class_1799;
import net.minecraft.class_310;
import net.minecraft.class_4587;
import net.minecraft.class_5697;
import net.minecraft.class_591;
import net.minecraft.class_742;
import net.minecraft.class_746;
import net.minecraft.class_7833;
import org.joml.Quaternionfc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.decide.cosmetics.melee.MeleeFirstPersonRenderer;
import ru.decide.cosmetics.melee.MeleeRules;
import ru.decide.mixin.LivingEntityRendererAccessor;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_5697.class})
public abstract class MeleePlayerHeldItemFeatureRendererMixin {
    @Inject(method={"method_62594"}, at={@At(value="HEAD")}, cancellable=true)
    private void decide$meleeThirdPerson(class_10055 state, class_10444 itemState, class_1799 item, class_1306 arm, class_4587 matrices, class_11659 queue, int light, CallbackInfo ci) {
        if (!MeleeRules.rendersThirdPerson(item)) {
            return;
        }
        class_310 client = class_310.method_1551();
        class_746 player = client.field_1724;
        if (player == null || player.method_5628() != state.field_53528) {
            return;
        }
        class_1007 renderer = client.method_1561().method_74405((class_742)player);
        if (renderer == null) {
            return;
        }
        class_591 model = (class_591)((LivingEntityRendererAccessor)renderer).decide$getModel();
        if (model == null) {
            return;
        }
        matrices.method_22903();
        model.method_72859(state, arm, matrices);
        matrices.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(-90.0f));
        matrices.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees(180.0f));
        boolean rendered = MeleeFirstPersonRenderer.getInstance().renderThirdPerson((class_742)player, arm, matrices, queue, light);
        matrices.method_22909();
        if (rendered) {
            ci.cancel();
        }
    }
}


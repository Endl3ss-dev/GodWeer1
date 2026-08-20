/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.render.VertexConsumerProvider
 *  net.minecraft.client.render.entity.feature.ArmorFeatureRenderer
 *  net.minecraft.client.render.entity.model.BipedEntityModel
 *  net.minecraft.client.render.entity.state.BipedEntityRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.EquipmentSlot
 *  net.minecraft.item.ItemStack
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package minced.mixin.render;
import com.mojang.blaze3d.systems.RenderSystem;
import minced.core.Minced;
import minced.module.visual.ArmorDurabilityView;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ArmorFeatureRenderer.class})
public class ArmorFeatureRendererMixin<S extends BipedEntityRenderState, M extends BipedEntityModel<S>, A extends BipedEntityModel<S>> {
    @Inject(method={"renderArmor(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EquipmentSlot;ILnet/minecraft/client/render/entity/model/BipedEntityModel;)V"}, at={@At(value="HEAD")})
    private void beforeRenderArmor(MatrixStack param1, VertexConsumerProvider param2, ItemStack param3, EquipmentSlot param4, int param5, A param6, CallbackInfo param7) {
        ArmorDurabilityView v8 = (ArmorDurabilityView)Minced.getInstance().getModuleManager().method187(ArmorDurabilityView.class);
        if (v8 != null && v8.isEnabled()) {
            float v9 = 1.0f;
            if (param3.isDamaged()) {
                int v10 = param3.getMaxDamage();
                int v11 = param3.getDamage();
                v9 = (float)(v10 - v11) / (float)v10;
            }
            float[] v10 = ArmorDurabilityView.method1184(v9);
            RenderSystem.setShaderColor((float)v10[0], (float)v10[1], (float)v10[2], (float)1.0f);
        }
    }

    @Inject(method={"renderArmor(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EquipmentSlot;ILnet/minecraft/client/render/entity/model/BipedEntityModel;)V"}, at={@At(value="RETURN")})
    private void afterRenderArmor(MatrixStack param1, VertexConsumerProvider param2, ItemStack param3, EquipmentSlot param4, int param5, A param6, CallbackInfo param7) {
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
    }
}




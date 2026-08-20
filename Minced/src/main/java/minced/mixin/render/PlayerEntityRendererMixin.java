/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.model.ModelPart
 *  net.minecraft.client.render.OverlayTexture
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.client.render.VertexConsumer
 *  net.minecraft.client.render.VertexConsumerProvider
 *  net.minecraft.client.render.entity.PlayerEntityRenderer
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.ColorHelper
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package minced.mixin.render;
import minced.core.ClientUtil;
import minced.core.Minced;
import minced.module.visual.SeeInvisibles;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={PlayerEntityRenderer.class})
public abstract class PlayerEntityRendererMixin {
    @Redirect(method={"renderArm"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/model/ModelPart;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V"))
    private void redirectArmRender(ModelPart param1, MatrixStack param2, VertexConsumer param3, int param4, int param5, MatrixStack param6, VertexConsumerProvider param7, int param8, Identifier param9, ModelPart param10, boolean param11) {
        SeeInvisibles v12 = (SeeInvisibles)Minced.getInstance().getModuleManager().method187(SeeInvisibles.class);
        boolean v13 = ClientUtil.I.player != null && ClientUtil.I.player.isInvisible();
        float alpha = v12 != null && v12.isEnabled() && v13 ? ((Float)v12.cfr_renamed_4().getValue()).floatValue() : 1.0f;
        int v14 = ColorHelper.fromFloats(alpha, 1.0f, 1.0f, 1.0f);
        param1.render(param2, param7.getBuffer(RenderLayer.getEntityTranslucent((Identifier)param9)), param4, OverlayTexture.DEFAULT_UV, v14);
    }
}




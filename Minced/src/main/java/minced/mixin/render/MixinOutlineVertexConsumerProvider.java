/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.render.OutlineVertexConsumerProvider
 *  net.minecraft.client.render.OutlineVertexConsumerProvider$OutlineVertexConsumer
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.client.render.VertexConsumer
 *  net.minecraft.client.render.VertexConsumerProvider$Immediate
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package minced.mixin.render;
import minced.module.visual.ShaderESP;
import net.minecraft.client.render.OutlineVertexConsumerProvider;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={OutlineVertexConsumerProvider.class})
public class MixinOutlineVertexConsumerProvider {
    @Shadow
    private VertexConsumerProvider.Immediate field_21059;
    @Shadow
    private int field_21060;
    @Shadow
    private int field_21061;
    @Shadow
    private int field_21062;
    @Shadow
    private int field_21063;

    @Inject(method={"getBuffer"}, at={@At(value="HEAD")}, cancellable=true)
    private void hook(RenderLayer param1, CallbackInfoReturnable<VertexConsumer> param22) {
        if (ShaderESP.field1856 != null && !ShaderESP.cfr_renamed && !param1.isOutline()) {
            param1.getAffectedOutline().ifPresent(param2 -> {
                VertexConsumer v4 = ((OutlineVertexConsumerProvider)((Object)this)).getBuffer(param2);
                param22.setReturnValue(v4);
                param22.cancel();
            });
        }
    }
}




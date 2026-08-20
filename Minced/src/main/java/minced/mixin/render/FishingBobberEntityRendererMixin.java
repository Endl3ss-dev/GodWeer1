/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.render.VertexConsumerProvider
 *  net.minecraft.client.render.entity.FishingBobberEntityRenderer
 *  net.minecraft.client.render.entity.state.FishingBobberEntityState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.projectile.FishingBobberEntity
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package minced.mixin.render;
import minced.core.EventNoRender;
import minced.core.Event;
import minced.core.EventBus;
import minced.enums.fire;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.FishingBobberEntityRenderer;
import net.minecraft.client.render.entity.state.FishingBobberEntityState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.projectile.FishingBobberEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={FishingBobberEntityRenderer.class})
public class FishingBobberEntityRendererMixin {
    @Unique
    private FishingBobberEntity capturedEntity;

    @Inject(method={"updateRenderState"}, at={@At(value="HEAD")})
    private void onUpdateRenderState(FishingBobberEntity param1, FishingBobberEntityState param2, float param3, CallbackInfo param4) {
        this.capturedEntity = param1;
    }

    @Inject(method={"render(Lnet/minecraft/client/render/entity/state/FishingBobberEntityState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"}, at={@At(value="HEAD")}, cancellable=true)
    private void onRender(FishingBobberEntityState param1, MatrixStack param2, VertexConsumerProvider param3, int param4, CallbackInfo param5) {
        if (this.capturedEntity != null && this.capturedEntity.getHookedEntity() != null && this.capturedEntity.getHookedEntity() == MinecraftClient.getInstance().player) {
            EventNoRender v6 = new EventNoRender(fire.fishingrod);
            EventBus.method3015(((Object)v6));
            if (v6.method2297()) {
                param5.cancel();
            }
        }
    }
}




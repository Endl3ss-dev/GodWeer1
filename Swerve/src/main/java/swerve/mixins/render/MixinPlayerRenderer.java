package swerve.mixins.render;

import swerve.utils.render.spatial.SpatialAvatar;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// SpatialGui: поза локального игрока (тело к панели, голова за курсором) подменяется в состоянии рендера.
@Mixin(PlayerRenderer.class)
public class MixinPlayerRenderer {

    @Inject(method = "extractRenderState(Lnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/client/renderer/entity/state/PlayerRenderState;F)V",
            at = @At("TAIL"))
    private void swerve$spatialPose(AbstractClientPlayer player, PlayerRenderState state, float partialTick, CallbackInfo ci) {
        SpatialAvatar.applyPose(player, state);
    }
}

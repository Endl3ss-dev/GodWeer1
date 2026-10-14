package swerve.mixins.render;

import swerve.utils.render.spatial.SpatialAvatar;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// SpatialGui: голова локального игрока скрывается, когда камера панели подъезжает вплотную.
@Mixin(PlayerModel.class)
public class MixinPlayerModel {

    // Флаг на экземпляр модели: ванила сбрасывает hat.visible перед каждым setupAnim, head — нет.
    @Unique
    private boolean swerve$headHidden;

    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/PlayerRenderState;)V", at = @At("TAIL"))
    private void swerve$spatialHideHead(PlayerRenderState state, CallbackInfo ci) {
        PlayerModel model = (PlayerModel) (Object) this;
        if (swerve$headHidden) {
            model.head.visible = true;
            swerve$headHidden = false;
        }
        if (SpatialAvatar.shouldHideHead(state)) {
            model.head.visible = false;
            model.hat.visible = false;
            swerve$headHidden = true;
        }
    }
}

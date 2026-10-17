package swerve.mixins.render;

import swerve.cosmetics.Wearables;
import swerve.cosmetics.cape.Capes;
import swerve.cosmetics.cape.ClothCapeRenderer;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Гардероб: после тела игрока дорисовываются надетые модели (крылья, рюкзак, питомец, шляпа).
 */
@Mixin(LivingEntityRenderer.class)
public class MixinLivingEntityRenderer {

    @Shadow
    protected EntityModel<?> model;

    @Inject(
            method = "render(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;"
                    + "Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("TAIL"))
    private void swerve$cosmetics(LivingEntityRenderState state, PoseStack pose, MultiBufferSource buffer,
                                  int light, CallbackInfo ci) {
        AbstractClientPlayer player = Wearables.current();
        Wearables.current(null);
        if (player == null || player.isInvisible()) {
            return;
        }
        if (!(this.model instanceof PlayerModel pm)) {
            return;
        }
        int[] items = Wearables.stateOf(player);
        if (items != null) {
            try {
                Wearables.render(pm, pose, buffer, light, items);
            } catch (RuntimeException ignored) {
            }
        }
        // Плащ: ткань на спине игрока, своя физика вместо ванильной.
        try {
            if (Capes.hasCape(player)) {
                ClothCapeRenderer.getInstance().render(pose, buffer, light, player, pm);
            }
        } catch (RuntimeException ignored) {
        }
    }
}

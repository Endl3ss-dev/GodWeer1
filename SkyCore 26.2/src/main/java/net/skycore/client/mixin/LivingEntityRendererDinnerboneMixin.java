package net.skycore.client.mixin;

import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import net.skycore.client.util.DinnerboneFlip;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Второй вариант того же переворота — на уровне {@link LivingEntityRenderer}.
 *
 * <p>Нужен на случай, если в сборке 26.2 игроков рендерит не {@code AvatarRenderer},
 * а общий рендерер живых сущностей: тогда {@link AvatarRendererDinnerboneMixin} не
 * применится, а этот — сработает. Оба помечены {@code require = 0}, поэтому
 * применяется тот, чей метод реально есть, и ничего не ломается, если нет ни одного.
 */
@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererDinnerboneMixin {

    @Inject(
            method = "extractRenderState(Lnet/minecraft/world/entity/LivingEntity;"
                    + "Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V",
            at = @At("TAIL"),
            require = 0)
    private void skycore$dinnerboneState(LivingEntity entity, LivingEntityRenderState state,
                                         float partialTick, CallbackInfo callbackInfo) {
        DinnerboneFlip.markState(state, DinnerboneFlip.shouldFlip(entity));
    }
}

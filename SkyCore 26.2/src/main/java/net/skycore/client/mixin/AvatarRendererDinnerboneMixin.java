package net.skycore.client.mixin;

import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Avatar;
import net.skycore.client.util.DinnerboneFlip;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Локальный «Dinnerbone» для игроков (модуль {@code DinnerboneModule}).
 *
 * <p>Ваниль переворачивает модель, если имя сущности {@code Dinnerbone}/{@code Grumm},
 * и складывает результат в рендер-состояние. Здесь мы ставим тот же флаг сами, но
 * только для выбранных игроков — дальше работает обычный ванильный код, поэтому
 * картинка совпадает с настоящим «Dinnerbone».
 *
 * <p>Точка внедрения выбрана не наугад: {@code AvatarRenderer.extractRenderState}
 * уже используется в этом проекте ({@code AvatarRendererFixElytraFlyingTwistMixin}),
 * значит сигнатура проверена на 26.2. {@code require = 0} оставлен, чтобы при
 * переименовании в будущих сборках клиент не падал, а просто не переворачивал.
 *
 * <p>Это <b>локальный</b> переворот: другие игроки его не видят, их клиенты рендерят
 * вас по имени, которое рассылает сервер.
 */
@Mixin(AvatarRenderer.class)
public abstract class AvatarRendererDinnerboneMixin {

    @Inject(
            method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;"
                    + "Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V",
            at = @At("TAIL"),
            require = 0)
    private void skycore$dinnerboneState(Avatar avatar, AvatarRenderState state, float partialTick,
                                         CallbackInfo callbackInfo) {
        DinnerboneFlip.markState(state, DinnerboneFlip.shouldFlip(avatar));
    }
}

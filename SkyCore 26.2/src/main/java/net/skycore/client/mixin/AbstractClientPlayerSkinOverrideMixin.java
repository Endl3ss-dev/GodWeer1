package net.skycore.client.mixin;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.PlayerSkin;
import net.skycore.client.module.misc.SkinStealerModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Подмена скина локального игрока для SkinStealer.
 *
 * <p>Скин берётся из {@link SkinStealerModule#overrideFor}: модуль отдаёт готовый
 * {@link PlayerSkin} только для {@code Minecraft.player} и только пока подмена
 * включена, поэтому остальные игроки рисуются как обычно.
 *
 * <p>{@code require = 0} стоит намеренно: если в конкретной сборке 26.2 метод
 * {@code getSkin} объявлен выше по иерархии, миксин просто не применится, и клиент
 * не упадёт при запуске. В этом случае модуль продолжает работать в своей основной
 * части — выдавать ник в чат для {@code /skin <ник>}.
 */
@Mixin(AbstractClientPlayer.class)
public class AbstractClientPlayerSkinOverrideMixin {

    @Inject(method = "getSkin", at = @At("RETURN"), cancellable = true, require = 0)
    private void skycore$skinStealerOverride(CallbackInfoReturnable<PlayerSkin> callback) {
        PlayerSkin skin = SkinStealerModule.overrideFor((AbstractClientPlayer) (Object) this);
        if (skin != null) {
            callback.setReturnValue(skin);
        }
    }
}

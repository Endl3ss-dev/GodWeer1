package swerve.mixins.client;

import swerve.Swerve;

import net.minecraft.client.User;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Ник клиента: всё, что читает имя сессии (профиль локального игрока, одиночный мир,
// интеграции), получает фиксированное имя вместо имени аккаунта лаунчера.
@Mixin(User.class)
public class MixinUser {

    @Inject(method = "getName", at = @At("HEAD"), cancellable = true)
    private void swerve$fixedNickname(CallbackInfoReturnable<String> cir) {
        cir.setReturnValue(Swerve.CLIENT_NICKNAME);
    }
}

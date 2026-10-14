package swerve.mixins.client;

import com.mojang.authlib.GameProfile;

import swerve.Swerve;
import swerve.utils.render.spatial.SpatialState;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Страховка к MixinUser: профиль локального игрока может собираться не из User.getName(),
// а из результата авторизации — тогда имя всё равно приводится к нику клиента.
// require = 0: если в этой версии метода нет, миксин просто не применяется, без падения игры.
@Mixin(Minecraft.class)
public class MixinMinecraft {

    @Inject(method = "getGameProfile", at = @At("RETURN"), require = 0)
    private void swerve$fixedProfileName(CallbackInfoReturnable<GameProfile> cir) {
        GameProfile profile = cir.getReturnValue();
        if (profile == null || Swerve.CLIENT_NICKNAME.equals(profile.getName())) {
            return;
        }
        cir.setReturnValue(new GameProfile(profile.getId(), Swerve.CLIENT_NICKNAME));
    }

    // [+] SpatialGui: смена экрана снимает перехват сразу (с анимацией закрытия), не дожидаясь кадра.
    @Inject(method = "setScreen", at = @At("HEAD"))
    private void swerve$spatialScreenChange(Screen screen, CallbackInfo ci) {
        SpatialState.onScreenChange(screen);
    }
}

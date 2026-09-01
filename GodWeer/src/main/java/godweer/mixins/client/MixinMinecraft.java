package godweer.mixins.client;

import godweer.utils.client.TestAccount;

import net.minecraft.client.Minecraft;
import net.minecraft.client.User;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {

    @Mutable
    @Shadow
    private User user;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void godweer$forceTestAccount(CallbackInfo ci) {
        String accessToken = this.user != null ? this.user.getAccessToken() : "0";
        this.user = TestAccount.create("GodWeer_Test", accessToken);
    }
}

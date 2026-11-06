package tech.lightclient.mixins.client;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.lightclient.util.SkullProfileNbtFixer;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class SkullProfilePacketMixin {

    @Inject(method = "onBlockEntityUpdate", at = @At("HEAD"))
    private void light$normalizeOnUpdatePacket(BlockEntityUpdateS2CPacket packet, CallbackInfo ci) {
        SkullProfileNbtFixer.normalize(packet.getNbt());
    }
}

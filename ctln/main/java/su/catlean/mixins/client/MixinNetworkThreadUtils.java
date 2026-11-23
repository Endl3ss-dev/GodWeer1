// deobf by pusia
package su.catlean.mixins.client;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.catlean.api.event.events.network.AfterReceivePacket;

@Mixin(
   targets = {"net.minecraft.network.PacketProcessor$ListenerAndPacket"}
)
public class MixinNetworkThreadUtils<T extends PacketListener> {
   @Final
   @Shadow
   private Packet<T> packet;

   public MixinNetworkThreadUtils() {
   }

   @Inject(
      method = {"handle"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/network/protocol/Packet;handle(Lnet/minecraft/network/PacketListener;)V",
         shift = Shift.AFTER
      )}
   )
   private void afterPacketApply(CallbackInfo callbackInfo) {
      if (Minecraft.getInstance().player != null) {
         if (this.packet instanceof ClientboundBundlePacket clientboundBundlePacket) {
            clientboundBundlePacket.subPackets().forEach(packet -> new AfterReceivePacket((Packet<?>)packet).call());
         } else {
            new AfterReceivePacket(this.packet).call();
         }
      }
   }
}
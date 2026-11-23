// deobf by pusia
package su.catlean.mixins.client;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.catlean.api.event.events.network.SendMessageEvent;
import su.catlean.api.event.events.player.FlagEvent;
import su.catlean.api.event.events.world.WorldUpdateEvent;

@Mixin({ClientPacketListener.class})
public class ClientPacketListenerMixin {

   public ClientPacketListenerMixin() {
   }

   @Inject(
      method = {"handleMovePlayer"},
      cancellable = true,
      at = {@At("RETURN")}
   )
   private void onPlayerPositionLookHook(ClientboundPlayerPositionPacket clientboundPlayerPositionPacket, CallbackInfo callbackInfo) {
      if (new FlagEvent().call(clientboundPlayerPositionPacket)) {
         callbackInfo.cancel();
      }
   }

   @Inject(
      method = {"sendChat"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void sendChatMessageHook(String text, CallbackInfo callbackInfo) {
      if (SendMessageEvent.INSTANCE.call(text)) {
         callbackInfo.cancel();
      }
   }

   @Inject(
      method = {"handleBlockUpdate"},
      at = {@At("TAIL")}
   )
   private void onBlockUpdateHook(CallbackInfo callbackInfo) {
      new WorldUpdateEvent().call();
   }

   @Inject(
      method = {"handleChunkBlocksUpdate"},
      at = {@At("TAIL")}
   )
   private void onChunkDeltaUpdateHook(CallbackInfo callbackInfo) {
      new WorldUpdateEvent().call();
   }
}
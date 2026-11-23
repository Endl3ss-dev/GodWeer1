// deobf by pusia
package su.catlean.mixins.render;

import net.minecraft.client.gui.components.PlayerTabOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import su.catlean.api.event.events.network.PlayerListSizeEvent;

@Mixin({PlayerTabOverlay.class})
public class PlayerTabOverlayMixin {

   public PlayerTabOverlayMixin() {
   }

   @ModifyArgs(
      method = {"getPlayerInfos"},
      at = @At(
         value = "INVOKE",
         target = "Ljava/util/stream/Stream;limit(J)Ljava/util/stream/Stream;"
      )
   )
   private void collectPlayerEntriesHook(Args args) {
      if (PlayerListSizeEvent.INSTANCE.call()) {
         args.set(0, 1000L);
      }
   }
}
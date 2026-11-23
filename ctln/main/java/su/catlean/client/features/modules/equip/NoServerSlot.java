// deobf by pusia
package su.catlean.client.features.modules.equip;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetHeldSlotPacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.network.ReceivePacket;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.network.PacketUtil;

public final class NoServerSlot extends Module {
   @NotNull
   public static final NoServerSlot INSTANCE = new NoServerSlot();

   private NoServerSlot() {
      super("no-server-slot", SubCategories.getSubCategory9(), null, 4, null);
   }

   @Flow
   public final void onReceivePacket(ReceivePacket event) {
      if (event.getPacket() instanceof ClientboundSetHeldSlotPacket) {
         event.cancel();
         Packet serverboundSetCarriedItemPacket = (Packet)(new ServerboundSetCarriedItemPacket(MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot()));
         PacketUtil.applySend(serverboundSetCarriedItemPacket);
      }
   }
}
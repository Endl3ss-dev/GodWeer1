// deobf by pusia
package su.catlean.client.features.modules.equip;

import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.network.SendPacket;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.features.modules.basic.GuiMove;
import su.catlean.gofra.Flow;

public final class XCarry extends Module {
   @NotNull
   public static final XCarry INSTANCE = new XCarry();

   private XCarry() {
      super("x-carry", SubCategories.getSubCategory9(), null, 4, null);
   }

   @Flow
   private final void onSendPacket(SendPacket sendPacket) {
      if (sendPacket.getPacket() instanceof ServerboundContainerClosePacket) {
         if (GuiMove.INSTANCE.getEnabled() && GuiMove.INSTANCE.getClickBypass() != GuiMove.Bypass.NONE) {
            this.updateText10("not-compatible");
            return;
         }

         sendPacket.cancel();
      }
   }
}
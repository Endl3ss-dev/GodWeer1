// deobf by pusia
package su.catlean.utils.inventory;

import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerInput;
import org.jetbrains.annotations.NotNull;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.network.PacketUtil;

public final class ContainerUtil {
   public static final void applyHandleContainerInput(int value, int value2, ContainerInput type, boolean flag) {
      MultiPlayerGameMode gameMode = MinecraftHolder.getMinecraft().gameMode;
      if (gameMode != null) {
         gameMode.handleContainerInput(
            MinecraftHolder.getLocalPlayer().containerMenu.containerId, value, value2, type, (Player)MinecraftHolder.getLocalPlayer()
         );
      }

      if (flag) {
         Packet serverboundContainerClosePacket = (Packet)(new ServerboundContainerClosePacket(MinecraftHolder.getLocalPlayer().containerMenu.containerId));
         PacketUtil.applySend(serverboundContainerClosePacket);
      }
   }

   public static void defaultImpl(int value, int value2, ContainerInput containerInput, boolean flag, int value3, Object object) {
      if ((value3 & 2) != 0) {
         value2 = 0;
      }

      if ((value3 & 4) != 0) {
         containerInput = ContainerInput.PICKUP;
      }

      if ((value3 & 8) != 0) {
         flag = false;
      }

      applyHandleContainerInput(value, value2, containerInput, flag);
   }

   @NotNull
   public static final InteractionResult computeInteractionResult(InteractionHand hand, float amount, float amount2) {
      return MinecraftHolder.getMultiPlayerGameMode().useItem((Player)MinecraftHolder.getLocalPlayer(), hand);
   }

   public static InteractionResult defaultImpl(InteractionHand interactionHand, float amount, float amount2, int value, Object object) {
      if ((value & 2) != 0) {
         amount = -1337.0F;
      }

      if ((value & 4) != 0) {
         amount2 = -1337.0F;
      }

      return computeInteractionResult(interactionHand, amount, amount2);
   }
}
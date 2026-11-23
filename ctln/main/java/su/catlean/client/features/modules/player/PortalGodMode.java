// deobf by pusia
package su.catlean.client.features.modules.player;

import kotlin.jvm.internal.Intrinsics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ServerboundAcceptTeleportationPacket;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.network.SendPacket;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.StopWatch;

public final class PortalGodMode extends Module {
   @NotNull
   public static final PortalGodMode INSTANCE = new PortalGodMode();
   @NotNull
   private static final StopWatch stopWatch = new StopWatch();

   private PortalGodMode() {
      super("portal-god-mode", SubCategories.getSubCategory10(), null, 4, null);
   }

   @Flow
   public final void onSendPacket(SendPacket e) {
      if (e.getPacket() instanceof ServerboundAcceptTeleportationPacket && stopWatch.getTime() < 5000L) {
         e.cancel();
      }
   }

   @Flow
   public final void onPlayerUpdate(PlayerUpdateEvent e) {
      int value = (int)(MinecraftHolder.getLocalPlayer().getX() - 2);

      for (int i = (int)(MinecraftHolder.getLocalPlayer().getX() + 2); value < i; value++) {
         int value2 = (int)(MinecraftHolder.getLocalPlayer().getZ() - 2);

         for (int j = (int)(MinecraftHolder.getLocalPlayer().getZ() + 2); value2 < j; value2++) {
            int value3 = (int)(MinecraftHolder.getLocalPlayer().getY() - 2);

            for (int k = (int)(MinecraftHolder.getLocalPlayer().getY() + 2); value3 < k; value3++) {
               if (Intrinsics.areEqual(
                  MinecraftHolder.getClientLevel().getBlockState(BlockPos.containing(value, value3, value2)).getBlock(), Blocks.NETHER_PORTAL
               )) {
                  stopWatch.update();
               }
            }
         }
      }
   }

   static {
      stopWatch.applyCurrentTimeMillis(99999);
   }
}
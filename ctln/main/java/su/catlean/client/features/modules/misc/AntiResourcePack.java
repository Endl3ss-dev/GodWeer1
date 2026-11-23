// deobf by pusia
package su.catlean.client.features.modules.misc;

import su.catlean.utils.KotlinCompat;
import kotlin.text.StringsKt;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundResourcePackPushPacket;
import net.minecraft.network.protocol.common.ServerboundResourcePackPacket;
import net.minecraft.network.protocol.common.ServerboundResourcePackPacket.Action;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.network.ReceivePacket;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.network.PacketUtil;

public final class AntiResourcePack extends Module {
   @NotNull
   public static final AntiResourcePack INSTANCE = new AntiResourcePack();
   private static boolean flag;
   private static boolean flag2;
   private static int value;

   private AntiResourcePack() {
      super("anti-resource-pack", SubCategories.getSubCategory11(), null, 4, null);
   }

   @Flow
   public final void onReceivePacket(ReceivePacket e) {
      if (e.getPacket() instanceof ClientboundResourcePackPushPacket) {
         flag = true;
         flag2 = false;
         value = 0;
         e.cancel();
      }
   }

   @Flow
   public final void onPlayerUpdate(PlayerUpdateEvent e) {
      ServerData serverData = MinecraftHolder.getClientPacketListener().getServerData();
      if (serverData != null && serverData.ip != null ? KotlinCompat.contains(serverData.ip, "crystalpvp.cc", false, 2, null) : false) {
         this.applyUnplug();
      }

      if (flag) {
         int value2 = value++;
         if (value > 15 + Math.random() * 15 && !flag2) {
            Packet serverboundResourcePackPacket = (Packet)(new ServerboundResourcePackPacket(MinecraftHolder.getLocalPlayer().getUUID(), Action.ACCEPTED));
            PacketUtil.applySend(serverboundResourcePackPacket);
            flag2 = true;
         }

         if (value > 40 + Math.random() * 20 && flag2) {
            Packet serverboundResourcePackPacket2 = (Packet)(new ServerboundResourcePackPacket(MinecraftHolder.getLocalPlayer().getUUID(), Action.SUCCESSFULLY_LOADED));
            PacketUtil.applySend(serverboundResourcePackPacket2);
            flag = false;
         }
      }
   }
}
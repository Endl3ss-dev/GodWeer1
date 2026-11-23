// deobf by pusia
package su.catlean.client.services;

import net.minecraft.world.level.Level;
import su.catlean.utils.KotlinCompat;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import kotlin.collections.ArrayDeque;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.network.ReceivePacket;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.service.Service;
import su.catlean.client.features.modules.misc.FastLatency;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;

public final class ServerInfoService implements Service {
   @NotNull
   public static final ServerInfoService INSTANCE = new ServerInfoService();
   @NotNull
   private static final ArrayDeque<Float> arrayDeque = new ArrayDeque(20);
   private static long time = 1L;
   private static float amount;
   private static int value;
   @NotNull
   private static List items = new ArrayList();
   private static long time2;
   private static long time3;

   private ServerInfoService() {
   }

   @NotNull
   public final List getItems() {
      return items;
   }

   public final long getTime2() {
      return time2;
   }

   public final float getAmount() {
      return new BigDecimal((double)amount).setScale(2, RoundingMode.HALF_UP).floatValue();
   }

   public final float getAmount2() {
      return new BigDecimal(20.0 * this.getAmount3()).setScale(2, RoundingMode.HALF_UP).floatValue();
   }

   public final float getAmount3() {
      ServerData serverData = MinecraftHolder.getClientPacketListener().getServerData();
      return (serverData != null && serverData.ip != null ? KotlinCompat.contains(serverData.ip, "pvp.mioclient.me", false, 2, null) : false)
         ? 1.0F
         : 1000.0F / RangesKt.coerceAtLeast((float)time, 1.0F);
   }

   @Flow(
      priority = 10
   )
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      int latency;
      label12: {
         items.clear();
         items.addAll(this.getItems2());
         ClientPacketListener connection = MinecraftHolder.getMinecraft().getConnection();
         if (connection != null) {
            PlayerInfo playerInfo = connection.getPlayerInfo(MinecraftHolder.getLocalPlayer().getUUID());
            if (playerInfo != null) {
               latency = playerInfo.getLatency();
               break label12;
            }
         }

         latency = 0;
      }

      value = latency;
   }

   @Flow
   private final void onReceivePacket(ReceivePacket receivePacket) {
      if (receivePacket.getPacket() instanceof ClientboundSetTimePacket) {
         if (time2 != 0L) {
            time = MinecraftHolder.getTime() - time2;
            if (MinecraftHolder.getMinecraft().player != null && MinecraftHolder.getLocalPlayer().tickCount < 20) {
               arrayDeque.clear();
               return;
            }

            if (time > 0L) {
               if (arrayDeque.size() >= 20) {
                  arrayDeque.removeFirst();
               }

               float amount3 = 20.0F * this.getAmount3();
               if (!Float.isNaN(amount3) && !Float.isInfinite(amount3)) {
                  arrayDeque.add(amount3);
               }

               amount = (float)CollectionsKt.averageOfFloat((Iterable)arrayDeque);
            }
         }

         time2 = MinecraftHolder.getTime();
      }

      time3 = MinecraftHolder.getTime();
   }

   public final int fetchConnection(Player pl) {
      if (Intrinsics.areEqual(pl, MinecraftHolder.getLocalPlayer())) {
         return FastLatency.INSTANCE.getEnabled() ? FastLatency.INSTANCE.getValue() : value;
      } else {
         PlayerInfo playerInfo = MinecraftHolder.getMinecraft().getConnection().getPlayerInfo(pl.getUUID());
         if (playerInfo == null) {
            return 0;
         } else {
            PlayerInfo playerInfo2 = playerInfo;
            return playerInfo2.getLatency();
         }
      }
   }

   public static int defaultImpl(ServerInfoService serverInfoService, Player player, int value, Object object) {
      if ((value & 1) != 0) {
         player = (Player)MinecraftHolder.getLocalPlayer();
      }

      return serverInfoService.fetchConnection(player);
   }

   public final boolean isFlag() {
      ClientLevel level = MinecraftHolder.getMinecraft().level;
      if (level != null) {
         ResourceKey<Level> dimension = level.dimension();
         if (dimension != null) {
            Identifier identifier = dimension.identifier();
            if (identifier != null) {
               String path = identifier.getPath();
               if (path != null) {
                  return Intrinsics.areEqual(path, "the_nether");
               }
            }
         }
      }

      return Intrinsics.areEqual("", "the_nether");
   }

   private final List getItems2() {
      List arrayList = new ArrayList();

      for (LevelChunk items3Item : (Iterable<LevelChunk>)this.getItems3()) {
         arrayList.addAll(items3Item.getBlockEntities().values());
      }

      return arrayList;
   }

   @NotNull
   public final List<LevelChunk> getItems3() {
      List<LevelChunk> arrayList = new ArrayList();
      int value2 = ((Number)MinecraftHolder.getMinecraft().options.renderDistance().get()).intValue();
      int value3 = -value2;
      if (value3 <= value2) {
         while (true) {
            int value4 = -value2;
            if (value4 <= value2) {
               while (true) {
                  LevelChunk chunkNow = MinecraftHolder.getClientLevel()
                     .getChunkSource()
                     .getChunkNow((int)MinecraftHolder.getLocalPlayer().getX() / 16 + value3, (int)MinecraftHolder.getLocalPlayer().getZ() / 16 + value4);
                  if (chunkNow != null) {
                     LevelChunk levelChunk = chunkNow;
                     arrayList.add(levelChunk);
                  }

                  if (value4 == value2) {
                     break;
                  }

                  value4++;
               }
            }

            if (value3 == value2) {
               break;
            }

            value3++;
         }
      }

      return arrayList;
   }

   @NotNull
   public final List<ChunkPos> getItems4() {
      List<ChunkPos> arrayList = new ArrayList();
      int value2 = ((Number)MinecraftHolder.getMinecraft().options.renderDistance().get()).intValue() + 2;
      int value3 = (int)MinecraftHolder.getLocalPlayer().getX() >> 4;
      int value4 = (int)MinecraftHolder.getLocalPlayer().getZ() >> 4;
      int value5 = -value2;
      if (value5 <= value2) {
         while (true) {
            int value6 = -value2;
            if (value6 <= value2) {
               while (true) {
                  arrayList.add(new ChunkPos(value3 + value5, value4 + value6));
                  if (value6 == value2) {
                     break;
                  }

                  value6++;
               }
            }

            if (value5 == value2) {
               break;
            }

            value5++;
         }
      }

      return arrayList;
   }

   public final boolean isFlag2() {
      return arrayDeque.size() < 20;
   }

   public final long getTime() {
      return time3 - time2;
   }
}
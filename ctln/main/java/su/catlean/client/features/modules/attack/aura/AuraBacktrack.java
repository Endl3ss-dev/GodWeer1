// deobf by pusia
package su.catlean.client.features.modules.attack.aura;

import com.google.common.collect.Queues;
import java.util.concurrent.ConcurrentLinkedQueue;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import net.minecraft.network.protocol.game.ClientboundEntityPositionSyncPacket;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetHealthPacket;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.network.protocol.game.VecDeltaCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.catlean.api.data.BTPacket;
import su.catlean.api.event.events.network.ReceivePacket;
import su.catlean.api.event.events.render.Render3DEvent;
import su.catlean.client.features.modules.attack.Aura;
import su.catlean.client.features.modules.basic.AntiKnockBack;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.render.Render3D;

public final class AuraBacktrack {
   @NotNull
   public static final AuraBacktrack INSTANCE = new AuraBacktrack();
   @NotNull
   private static final ConcurrentLinkedQueue<Object> concurrentLinkedQueue = Queues.newConcurrentLinkedQueue();
   @NotNull
   private static final ConcurrentLinkedQueue<Object> concurrentLinkedQueue2 = Queues.newConcurrentLinkedQueue();
   @Nullable
   private static Player player;
   @Nullable
   private static Vec3 vec3;
   @Nullable
   private static Vec3 vec32;
   private static long time = MinecraftHolder.getTime();

   private AuraBacktrack() {
   }

   @Nullable
   public final Player getPlayer() {
      return player;
   }

   public final void setPlayer(Player player) {
      AuraBacktrack.player = player;
   }

   public final void onRender3D(Render3DEvent event) {
      if (player != null && vec3 != null && vec32 != null && !(AntiKnockBack.INSTANCE.computeVec3((Entity)player).distanceToSqr(vec3) < 1.0)) {
         int currentTimeMillis = 0;
         currentTimeMillis = (int)(System.currentTimeMillis() / 2.0 % 360);
         currentTimeMillis = currentTimeMillis >= 180 ? 360 - currentTimeMillis : currentTimeMillis;
         Vec3 vec33 = Render3D.INSTANCE.computeVec3(vec32, vec3, RangesKt.coerceIn((float)(MinecraftHolder.getTime() - time) / 100.0F, 0.0F, 1.0F));
         Render3D.INSTANCE.render(event.getStack(), vec33, player, currentTimeMillis / 360.0F, true);
      }
   }

   public final void onReceivePacket(ReceivePacket e) {
      if (!concurrentLinkedQueue.isEmpty() || !concurrentLinkedQueue2.isEmpty() || player != null && player.isAlive()) {
         Packet<? extends PacketListener> packet = e.getPacket();
         if (!(packet instanceof ServerboundChatPacket)
            && !(packet instanceof ClientboundSystemChatPacket)
            && !(packet instanceof ClientboundSetTimePacket)
            && !(packet instanceof ServerboundChatCommandPacket)
            && !(packet instanceof ClientboundSoundPacket)
            && !(packet instanceof ClientboundStopSoundPacket)
            && !(packet instanceof ClientboundSetActionBarTextPacket)
            && !(packet instanceof ClientboundSetHealthPacket)
            && !(packet instanceof ClientboundBossEventPacket)) {
            if (!(packet instanceof ClientboundPlayerPositionPacket) && !(packet instanceof ClientboundDisconnectPacket)) {
               Vec3 decode = null;
               if (packet instanceof ClientboundMoveEntityPacket) {
                  if (!Intrinsics.areEqual(((ClientboundMoveEntityPacket)packet).getEntity((Level)MinecraftHolder.getClientLevel()), player)) {
                     return;
                  }

                  VecDeltaCodec vecDeltaCodec = new VecDeltaCodec();
                  vecDeltaCodec.setBase(vec3);
                  decode = vecDeltaCodec.decode(
                     ((ClientboundMoveEntityPacket)packet).getXa(), ((ClientboundMoveEntityPacket)packet).getYa(), ((ClientboundMoveEntityPacket)packet).getZa()
                  );
                  time = MinecraftHolder.getTime();
               }

               if (packet instanceof ClientboundTeleportEntityPacket) {
                  Player player2 = player;
                  if (player2 != null ? ((ClientboundTeleportEntityPacket)packet).id() != player2.getId() : true) {
                     return;
                  }

                  decode = ((ClientboundTeleportEntityPacket)packet).change().position();
               }

               if (packet instanceof ClientboundEntityPositionSyncPacket) {
                  Player player3 = player;
                  if (player3 != null ? ((ClientboundEntityPositionSyncPacket)packet).id() != player3.getId() : true) {
                     return;
                  }

                  decode = ((ClientboundEntityPositionSyncPacket)packet).values().position();
               }

               if (decode != null) {
                  vec32 = vec3;
                  vec3 = decode;
                  if (player != null
                     && AntiKnockBack.INSTANCE.computeVec3((Entity)MinecraftHolder.getLocalPlayer()).distanceToSqr(decode)
                        <= AntiKnockBack.INSTANCE
                           .computeVec3((Entity)MinecraftHolder.getLocalPlayer())
                           .distanceToSqr(AntiKnockBack.INSTANCE.computeVec3((Entity)player))) {
                     this.applyRemoveIf(true);
                     return;
                  }
               }

               concurrentLinkedQueue.add(new BTPacket(packet, MinecraftHolder.getTime()));
               e.cancel();
            } else {
               this.applyRemoveIf();
            }
         }
      }
   }

   public final void update() {
      Player entity = Aura.INSTANCE.getEnabled() && Aura.INSTANCE.getEntity() != null && Aura.INSTANCE.getEntity() instanceof Player
         ? (Player)Aura.INSTANCE.getEntity()
         : null;
      if (entity == null) {
         this.applyRemoveIf();
      } else {
         if (!Intrinsics.areEqual(entity, player)) {
            this.applyRemoveIf();
            vec3 = AntiKnockBack.INSTANCE.computeVec3((Entity)entity);
         }

         player = entity;
      }
   }

   public final void applyClear() {
      if (MinecraftHolder.getMinecraft().level != null && MinecraftHolder.getMinecraft().player != null) {
         if (player != null && player.isAlive()) {
            defaultImpl(this, false, 1, null);
         } else {
            this.applyRemoveIf();
         }

         concurrentLinkedQueue2.removeIf(
            arg -> {
               AuraBacktrack instance = INSTANCE;

               try {
                  String text = "null cannot be cast to non-null type net.minecraft.network.protocol.Packet<net.minecraft.network.protocol.game.ClientGamePacketListener>";
                  ((net.minecraft.network.protocol.Packet)arg).handle((PacketListener)MinecraftHolder.getClientPacketListener());
                  Unit instance2 = Unit.INSTANCE;
               } catch (Throwable exception) {
                  Object failure = ResultKt.createFailure(exception);
               }

               return true;
            }
         );
      } else {
         concurrentLinkedQueue.clear();
         player = null;
         vec3 = null;
      }
   }

   public final void applyRemoveIf(boolean flag) {
      concurrentLinkedQueue.removeIf(arg -> {
         boolean flag2 = flag || ((BTPacket)arg).getTime() <= MinecraftHolder.getTime() - Aura.INSTANCE.getMoveDelay();
         if (flag2) {
            concurrentLinkedQueue2.add(((BTPacket)arg).getPacket());
         }

         return flag2;
      });
   }

   public static void defaultImpl(AuraBacktrack auraBacktrack, boolean flag, int value, Object object) {
      if ((value & 1) != 0) {
         flag = false;
      }

      auraBacktrack.applyRemoveIf(flag);
   }

   public final void applyRemoveIf() {
      concurrentLinkedQueue.removeIf(arg -> {
         AuraBacktrack instance = INSTANCE;

         try {
            ((BTPacket)arg).getPacket().handle((PacketListener)MinecraftHolder.getClientPacketListener());
            Unit instance2 = Unit.INSTANCE;
         } catch (Throwable exception) {
            Object failure = ResultKt.createFailure(exception);
         }

         return true;
      });
      player = null;
      vec3 = null;
   }
}
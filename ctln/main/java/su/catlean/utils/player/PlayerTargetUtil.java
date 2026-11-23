// deobf by pusia
package su.catlean.utils.player;

import net.minecraft.network.PacketListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import kotlin.NoWhenBranchMatchedException;
import kotlin.collections.CollectionsKt;
import kotlin.comparisons.ComparisonsKt;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.catlean.api.event.events.network.PlayerEntityDeathEvent;
import su.catlean.api.event.events.network.ReceivePacket;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.client.features.modules.attack.AutoCrystal;
import su.catlean.client.features.modules.misc.Notifier;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;

public final class PlayerTargetUtil {
   @NotNull
   public static final PlayerTargetUtil INSTANCE = new PlayerTargetUtil();
   @NotNull
   private static final Map map = new LinkedHashMap();
   @NotNull
   private static final Map<String, Long> map2 = new LinkedHashMap();

   private PlayerTargetUtil() {
   }

   @Flow(
      priority = 10
   )
   private final void onReceivePacket(ReceivePacket receivePacket) {
      Packet<? extends PacketListener> packet = receivePacket.getPacket();
      if (packet instanceof ClientboundEntityEventPacket && ((ClientboundEntityEventPacket)packet).getEventId() == 35) {
         Entity entity = ((ClientboundEntityEventPacket)packet).getEntity((Level)MinecraftHolder.getClientLevel());
         Player player = entity instanceof Player ? (Player)entity : null;
         if ((entity instanceof Player ? (Player)entity : null) == null) {
            return;
         }

         Player player2 = player;
         if (Intrinsics.areEqual(player2, MinecraftHolder.getMinecraft().player)) {
            return;
         }

         Map<String, Integer> map3 = map;
         String string = player2.getName().getString();
         Integer integer = ((Number)map.getOrDefault(player2.getName().getString(), 0)).intValue() + 1;
         map3.put(string, integer);
         if (Notifier.INSTANCE.getEnabled()) {
            Notifier.INSTANCE.updateText(player2.getName().getString(), ((Number)map.getOrDefault(player2.getName().getString(), 0)).intValue());
         }
      }
   }

   @Flow(
      priority = 10
   )
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      Iterable<AbstractClientPlayer> players = MinecraftHolder.getClientLevel().players();
      boolean flag = false;
      var arrayList = new ArrayList();

      for (Object object : players) {
         AbstractClientPlayer abstractClientPlayer = (AbstractClientPlayer)object;
         if ((abstractClientPlayer.getHealth() <= 0.0F || !abstractClientPlayer.isAlive() || abstractClientPlayer.isDeadOrDying())
            && !Intrinsics.areEqual(abstractClientPlayer, MinecraftHolder.getLocalPlayer())
            && !map2.containsKey(abstractClientPlayer.getName().getString())) {
            arrayList.add(object);
         }
      }

      players = arrayList;
      flag = false;

      for (Object object2 : players) {
         AbstractClientPlayer abstractClientPlayer2 = (AbstractClientPlayer)object2;
         PlayerEntityDeathEvent instance = PlayerEntityDeathEvent.INSTANCE;
         instance.setPlayer((Player)abstractClientPlayer2);
         instance.setTotemsPopped(((Number)map.getOrDefault(abstractClientPlayer2.getName().getString(), 0)).intValue());
         instance.call();
         map2.put(abstractClientPlayer2.getName().getString(), MinecraftHolder.getTime());
         map.remove(abstractClientPlayer2.getName().getString());
      }

      List<String> arrayList2 = new ArrayList();
      Map<String, Long> map22 = map2;
      boolean flag6 = false;

      for (Map.Entry<String, Long> entrySetItem : map22.entrySet()) {
         String key = (String)entrySetItem.getKey();
         long value = ((Number)entrySetItem.getValue()).longValue();
         if (MinecraftHolder.getTime() - value > 1000L) {
            arrayList2.add(key);
         }
      }

      Iterable<String> iterable = arrayList2;
      flag6 = false;

      for (Object object3 : iterable) {
         String text = (String)object3;
         map2.remove(text);
      }
   }

   public final int fetchName(Player entity) {
      return this.computeValue(entity.getName().getString());
   }

   public final int computeValue(String name) {
      Integer name2 = (Integer)map.get(name);
      return name2 != null ? name2 : 0;
   }

   @Nullable
   public final Player computePlayer(float amount, AutoCrystal.TargetSort targetBy, boolean flag, boolean flag2) {
      return switch (PlayerTargetUtil.WhenMappings.values[targetBy.ordinal()]) {
         case 1 -> this.computePlayer2(amount, flag, flag2);
         case 2 -> this.fetchAbsorptionAmount(amount, flag, flag2);
         case 3 -> this.computePlayer(amount, flag, flag2);
         default -> throw new NoWhenBranchMatchedException();
      };
   }

   public static Player defaultImpl(
      PlayerTargetUtil playerTargetUtil, float amount, AutoCrystal.TargetSort targetSort, boolean flag, boolean flag2, int value, Object object
   ) {
      if ((value & 4) != 0) {
         flag = false;
      }

      if ((value & 8) != 0) {
         flag2 = false;
      }

      return playerTargetUtil.computePlayer(amount, targetSort, flag, flag2);
   }

   private final List computeItems(float amount, boolean flag, boolean flag2) {
      Iterable<AbstractClientPlayer> players = MinecraftHolder.getClientLevel().players();
      var arrayList = new ArrayList();

      for (Object object : players) {
         AbstractClientPlayer abstractClientPlayer = (AbstractClientPlayer)object;
         if (!abstractClientPlayer.isDeadOrDying()
            && !PlayerNameUtil.checkGetGameProfile((Player)abstractClientPlayer)
            && !Intrinsics.areEqual(abstractClientPlayer, MinecraftHolder.getLocalPlayer())
            && MinecraftHolder.getLocalPlayer().distanceTo((Entity)abstractClientPlayer) < amount
            && (!flag || abstractClientPlayer.getArmorValue() > 0)
            && (!flag2 || !abstractClientPlayer.isCreative())) {
            arrayList.add(object);
         }
      }

      players = arrayList;
      return CollectionsKt.sortedWith(players, new PlayerTargetUtil.DistanceOrdering());
   }

   @Nullable
   public final Player computePlayer(float amount, boolean flag, boolean flag2) {
      Iterable items = this.computeItems(amount, flag, flag2);
      Iterator iterator = items.iterator();
      Object object;
      if (!iterator.hasNext()) {
         object = null;
      } else {
         Object object2 = iterator.next();
         if (!iterator.hasNext()) {
            object = object2;
         } else {
            Player player = (Player)object2;
            float distanceTo = MinecraftHolder.getLocalPlayer().distanceTo((Entity)player);

            do {
               Object object3 = iterator.next();
               Player player2 = (Player)object3;
               float distanceTo2 = MinecraftHolder.getLocalPlayer().distanceTo((Entity)player2);
               if (Float.compare(distanceTo, distanceTo2) > 0) {
                  object2 = object3;
                  distanceTo = distanceTo2;
               }
            } while (iterator.hasNext());

            object = object2;
         }
      }

      return (Player)object;
   }

   @Nullable
   public final Player fetchAbsorptionAmount(float amount, boolean flag, boolean flag2) {
      Iterable items = this.computeItems(amount, flag, flag2);
      Iterator iterator = items.iterator();
      Object object;
      if (!iterator.hasNext()) {
         object = null;
      } else {
         Object object2 = iterator.next();
         if (!iterator.hasNext()) {
            object = object2;
         } else {
            Player player = (Player)object2;
            float health = player.getHealth() + player.getAbsorptionAmount();

            do {
               Object object3 = iterator.next();
               Player player2 = (Player)object3;
               float health2 = player2.getHealth() + player2.getAbsorptionAmount();
               if (Float.compare(health, health2) > 0) {
                  object2 = object3;
                  health = health2;
               }
            } while (iterator.hasNext());

            object = object2;
         }
      }

      return (Player)object;
   }

   @Nullable
   public final Player computePlayer2(float amount, boolean flag, boolean flag2) {
      Iterable items = this.computeItems(amount, flag, flag2);
      Iterator iterator = items.iterator();
      Object object;
      if (!iterator.hasNext()) {
         object = null;
      } else {
         Object object2 = iterator.next();
         if (!iterator.hasNext()) {
            object = object2;
         } else {
            Player player = (Player)object2;
            float value = INSTANCE.fetchX((LivingEntity)player);

            do {
               Object object3 = iterator.next();
               Player player2 = (Player)object3;
               float value2 = INSTANCE.fetchX((LivingEntity)player2);
               if (Float.compare(value, value2) > 0) {
                  object2 = object3;
                  value = value2;
               }
            } while (iterator.hasNext());

            object = object2;
         }
      }

      return (Player)object;
   }

   private final float fetchX(LivingEntity livingEntity) {
      return Math.abs(
         (float)Mth.wrapDegrees(
               Math.toDegrees(
                     Math.atan2(livingEntity.getZ() - MinecraftHolder.getLocalPlayer().getZ(), livingEntity.getX() - MinecraftHolder.getLocalPlayer().getX())
                  )
                  - 90.0
            )
            - Mth.wrapDegrees(MinecraftHolder.getLocalPlayer().getYRot())
      );
   }

   public static final class DistanceOrdering implements Comparator {
      public DistanceOrdering() {
      }

      @Override
      public final int compare(Object object, Object object2) {
         AbstractClientPlayer object3 = (AbstractClientPlayer)object;
         Comparable distanceTo = MinecraftHolder.getLocalPlayer().distanceTo((Entity)object3);
         object3 = (AbstractClientPlayer)object2;
         Comparable comparable = distanceTo;
         return ComparisonsKt.compareValues(comparable, MinecraftHolder.getLocalPlayer().distanceTo((Entity)object3));
      }
   }

   public static final class WhenMappings {
      public static final int[] values;

      static {
         int[] values2 = new int[AutoCrystal.TargetSort.values().length];

         try {
            values2[AutoCrystal.TargetSort.FOV.ordinal()] = 1;
         } catch (NoSuchFieldError exception) {
         }

         try {
            values2[AutoCrystal.TargetSort.HEALTH.ordinal()] = 2;
         } catch (NoSuchFieldError exception2) {
         }

         try {
            values2[AutoCrystal.TargetSort.DISTANCE.ordinal()] = 3;
         } catch (NoSuchFieldError exception3) {
         }

         values = values2;
      }
   }
}
// deobf by pusia
package su.catlean.client.features.modules.esp;

import net.minecraft.world.phys.AABB;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import kotlin.collections.CollectionsKt;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import su.catlean.utils.MinecraftHolder;

public final class PlayerSkeletonTracker {
   @NotNull
   public static final PlayerSkeletonTracker INSTANCE = new PlayerSkeletonTracker();
   @NotNull
   private static final Map map = new LinkedHashMap();

   private PlayerSkeletonTracker() {
   }

   @NotNull
   public final Map getMap() {
      return map;
   }

   public final void applyPlayers(int value) {
      Iterable<AbstractClientPlayer> players = MinecraftHolder.getClientLevel().players();

      for (Object object : players) {
         AbstractClientPlayer abstractClientPlayer = (AbstractClientPlayer)object;
         INSTANCE.applyAddLast((Player)abstractClientPlayer, value);
      }

      Iterable<AbstractClientPlayer> players2 = MinecraftHolder.getClientLevel().players();
      var arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(players2, 10));

      for (Object object2 : players2) {
         AbstractClientPlayer abstractClientPlayer2 = (AbstractClientPlayer)object2;
         arrayList.add(abstractClientPlayer2.getId());
      }

      List list = arrayList;
      map.keySet().removeIf(arg -> !list.contains(arg));
   }

   private final void applyAddLast(Player player, int value) {
      ArrayDeque<AABB> ifAbsent = (ArrayDeque)map.computeIfAbsent(player.getId(), arg -> new ArrayDeque());
      ifAbsent.addLast(player.getBoundingBox().inflate(0.0));

      while (ifAbsent.size() > value) {
         ifAbsent.removeFirst();
      }
   }
}
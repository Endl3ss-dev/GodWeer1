// deobf by pusia
package su.catlean.client.services;

import java.util.Map;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.service.Service;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;

public final class BreakingBlocksService implements Service {
   @NotNull
   public static final BreakingBlocksService INSTANCE = new BreakingBlocksService();
   @NotNull
   private static final ConcurrentHashMap<BlockPos, Long> map = new ConcurrentHashMap();

   private BreakingBlocksService() {
   }

   public final boolean checkContainsKey(BlockPos bp) {
      return map.containsKey(bp);
   }

   public final void applyPut(BlockPos pos) {
      map.put(pos, MinecraftHolder.getTime());
   }

   public final void applyRemove(BlockPos pos) {
      map.remove(pos);
   }

   @Flow(
      priority = 10
   )
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      Iterator<Map.Entry<BlockPos, Long>> iterator = map.entrySet().iterator();

      while (iterator.hasNext()) {
         Long value = (Long)((Entry)iterator.next()).getValue();
         if (MinecraftHolder.getTime() - value > ServerInfoService.defaultImpl(ServerInfoService.INSTANCE, null, 1, null) * 3) {
            iterator.remove();
         }
      }
   }
}
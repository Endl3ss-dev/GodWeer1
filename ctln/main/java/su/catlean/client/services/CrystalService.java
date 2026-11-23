// deobf by pusia
package su.catlean.client.services;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentHashMap.KeySetView;
import kotlin.Pair;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.catlean.api.data.Attempt;
import su.catlean.api.service.Service;
import su.catlean.client.features.modules.attack.AutoCrystal;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.math.Vec3iUtil;

public final class CrystalService implements Service {
   @NotNull
   public static final CrystalService INSTANCE = new CrystalService();
   @NotNull
   private static final ConcurrentHashMap map = new ConcurrentHashMap();
   @NotNull
   private static final ConcurrentHashMap map2 = new ConcurrentHashMap();
   @NotNull
   private static final ConcurrentHashMap map3 = new ConcurrentHashMap();
   @NotNull
   private static final ConcurrentHashMap<AABB, Long> map4 = new ConcurrentHashMap();

   private CrystalService() {
   }

   @NotNull
   public final ConcurrentHashMap<AABB, Long> getMap4() {
      return map4;
   }

   public final void applyIntValue(Pair crystal) {
      this.computeLongValue(((Number)crystal.getFirst()).intValue(), MinecraftHolder.getTime());
      this.applyCompute(crystal);
   }

   public final void applyClear() {
      map.clear();
      map2.clear();
      map3.clear();
   }

   public final void applyEntrySet() {
      long time = MinecraftHolder.getTime();
      long defaultImpl = ServerInfoService.defaultImpl(ServerInfoService.INSTANCE, null, 1, null) * 2L;
      map.entrySet().removeIf(arg -> time - ((Number)((java.util.Map.Entry)arg).getValue()).longValue() > defaultImpl);
      map2.entrySet().removeIf(arg2 -> ((Attempt)((java.util.Map.Entry)arg2).getValue()).isFlag2());
      map3.entrySet().removeIf(arg3 -> ((Attempt)((java.util.Map.Entry)arg3).getValue()).isFlag2());
      map4.entrySet()
         .removeIf(
            arg4 -> MinecraftHolder.getTime() - ((Number)arg4.getValue()).longValue()
               > Math.max(ServerInfoService.defaultImpl(ServerInfoService.INSTANCE, null, 1, null) * 1.5, 50.0)
         );
   }

   public final boolean checkContainsKey(int value) {
      return map.containsKey(value);
   }

   public final long fetchOrDefault(int value) {
      return ((Number)map.getOrDefault(value, -1L)).longValue();
   }

   @Nullable
   public final Long computeLongValue(int value, long time) {
      return (Long)map.putIfAbsent(value, time);
   }

   public final boolean checkContainsKey2(int value) {
      return map2.containsKey(value) && ((Attempt)map2.get(value)).isFlag3();
   }

   private final void applyCompute(Pair pair) {
      map2.compute(pair.getFirst(), (arg, arg2) -> {
         if (arg2 == null) {
            return new Attempt(MinecraftHolder.getTime(), 1, (Vec3)pair.getSecond(), false);
         } else {
            ((Attempt)arg2).update();
            return (Object)arg2;
         }
      });
   }

   public final void applyGetX(BlockPos blockPos) {
      boolean checkAbove = AutoCrystal.INSTANCE.checkAbove(blockPos.above(), Lists.newArrayList(MinecraftHolder.getClientLevel().entitiesForRendering()));
      map3.compute(blockPos, (arg, arg2) -> {
         if (arg2 == null) {
            return new Attempt(MinecraftHolder.getTime(), 1, Vec3iUtil.computeVec3(blockPos), true);
         } else {
            if (!checkAbove) {
               ((Attempt)arg2).update();
            }

            return (Object)arg2;
         }
      });
      AABB aabb = new AABB(
         blockPos.getX() - 0.5, blockPos.getY() + 1.0, blockPos.getZ() - 0.5, blockPos.getX() + 1.5, blockPos.getY() + 3.0, blockPos.getZ() + 1.5
      );
      if (!map4.containsKey(aabb)) {
         map4.put(aabb, MinecraftHolder.getTime());
      }
   }

   public final boolean checkGetCenter(BlockPos blockPos, boolean flag) {
      AABB aabb = new AABB(
         blockPos.getX() - 0.5, blockPos.getY() + 1.0, blockPos.getZ() - 0.5, blockPos.getX() + 1.5, blockPos.getY() + 3.0, blockPos.getZ() + 1.5
      );
      Map<AABB, Long> map42 = map4;
      boolean flag2;
      if (map42.isEmpty()) {
         flag2 = false;
      } else {
         Iterator<Map.Entry<AABB, Long>> iterator = map42.entrySet().iterator();

         while (true) {
            if (!iterator.hasNext()) {
               flag2 = false;
               break;
            }

            Entry entry = (Entry)iterator.next();
            if (((AABB)entry.getKey()).intersects(aabb) && (flag || !Intrinsics.areEqual(((AABB)entry.getKey()).getCenter(), aabb.getCenter()))) {
               flag2 = true;
               break;
            }
         }
      }

      return flag2;
   }

   @Nullable
   public final Attempt computeAttempt(Vec3 pos) {
      Attempt attempt = null;
      Iterator iterator = map3.entrySet().iterator();

      while (iterator.hasNext()) {
         Entry entry = (Entry)iterator.next();
         BlockPos key = (BlockPos)entry.getKey();
         Attempt value = (Attempt)entry.getValue();
         if (pos.distanceToSqr(Vec3iUtil.computeVec3(key)) < 0.3) {
            BreakingBlocksService.INSTANCE.applyRemove(key);
            iterator.remove();
            attempt = value;
            break;
         }
      }

      Iterator<Map.Entry<AABB, Long>> iterator2 = map4.entrySet().iterator();

      while (iterator2.hasNext()) {
         AABB key2 = (AABB)((Entry)iterator2.next()).getKey();
         if (pos.distanceToSqr(key2.getCenter()) < 2.25) {
            iterator2.remove();
         }
      }

      if (attempt != null) {
         ((KeySetView)map3.keySet()).removeIf(arg -> pos.distanceToSqr(Vec3iUtil.computeVec3((BlockPos)arg)) < 0.3);
      }

      return attempt;
   }

   public final boolean testBlockPos(BlockPos bp) {
      Attempt map3Item = (Attempt)map3.get(bp);
      return map3Item != null ? map3Item.isFlag3() : false;
   }
}
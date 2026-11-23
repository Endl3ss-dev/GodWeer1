// deobf by pusia
package su.catlean.client.features.modules.attack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import kotlin.collections.CollectionsKt;
import kotlin.comparisons.ComparisonsKt;
import kotlin.ranges.RangesKt;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.catlean.api.data.ConfigState;
import su.catlean.api.data.CrystalWrapper;
import su.catlean.api.data.DamageComparatorResult;
import su.catlean.api.data.DamageData;
import su.catlean.client.features.modules.basic.MoveFix;
import su.catlean.client.features.modules.protect.SurroundPlanner;
import su.catlean.client.features.modules.protect.TrapPlanner;
import su.catlean.client.services.CrystalPredictor;
import su.catlean.client.services.CrystalService;
import su.catlean.client.services.InteractionManager;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.math.MathUtil;
import su.catlean.utils.world.BlockInteractionPoint;
import su.catlean.utils.world.WorldUtil;

public final class CrystalInteractPoint {
   @NotNull
   public static final CrystalInteractPoint INSTANCE = new CrystalInteractPoint();
   @Nullable
   private static EndCrystal endCrystal;

   private CrystalInteractPoint() {
   }

   @Nullable
   public final EndCrystal getEndCrystal() {
      return endCrystal;
   }

   public final void setEndCrystal(EndCrystal endCrystal) {
      CrystalInteractPoint.endCrystal = endCrystal;
   }

   @Nullable
   public final CrystalWrapper computeCrystalWrapper(AutoCrystal self, ConfigState configState) {
      if (endCrystal != null) {
         EndCrystal endCrystal2 = endCrystal;
         endCrystal = null;
         if (this.fetchY(self, self.computeVec3((Entity)endCrystal2), configState, true, endCrystal2.tickCount) != null) {
            return new CrystalWrapper(endCrystal2, true);
         }
      }

      DamageData damageData = SurroundPlanner.computeDamageData(configState, this.fetchId(self, configState));
      if (damageData != null) {
         EndCrystal object = (EndCrystal)damageData.getObject();
         if (object != null) {
            EndCrystal endCrystal3 = object;
            return new CrystalWrapper(endCrystal3, false, 2, null);
         }
      }

      return null;
   }

   private final List fetchId(AutoCrystal autoCrystal, ConfigState configState) {
      Iterable entities = configState.getEntities();
      var arrayList = new ArrayList();

      for (Object object : entities) {
         if (object instanceof EndCrystal) {
            arrayList.add(object);
         }
      }

      entities = arrayList;
      arrayList = new ArrayList();

      for (Object object2 : entities) {
         EndCrystal endCrystal2 = (EndCrystal)object2;
         if (!endCrystal2.isRemoved()
            && !CrystalService.INSTANCE.checkContainsKey2(endCrystal2.getId())
            && !CrystalService.INSTANCE.checkContainsKey(endCrystal2.getId())
            && autoCrystal.computeVec3((Entity)MinecraftHolder.getLocalPlayer()).distanceToSqr(autoCrystal.computeVec3((Entity)endCrystal2)) <= 49.0) {
            arrayList.add(object2);
         }
      }

      entities = arrayList;
      arrayList = new ArrayList();

      for (Object object3 : entities) {
         EndCrystal endCrystal3 = (EndCrystal)object3;
         DamageComparatorResult autoCrystal2 = INSTANCE.fetchY(autoCrystal, autoCrystal.computeVec3((Entity)endCrystal3), configState, false, endCrystal3.tickCount);
         DamageData damageData;
         if (autoCrystal2 != null) {
            DamageComparatorResult damageComparatorResult = autoCrystal2;
            damageData = new DamageData(endCrystal3, damageComparatorResult);
         } else {
            damageData = null;
         }

         if (damageData != null) {
            DamageData damageData2 = damageData;
            arrayList.add(damageData2);
         }
      }

      return arrayList;
   }

   @Nullable
   public final Vec3 fetchEyePosition(Vec3 vec, ConfigState configState, Vec3 eyes) {
      if (!configState.isFlag3()) {
         double coerceIn = vec.x - 1.0;
         double coerceIn2 = RangesKt.coerceIn(MinecraftHolder.getLocalPlayer().getEyePosition().x, coerceIn, vec.x + 1.0);
         coerceIn = RangesKt.coerceIn(MinecraftHolder.getLocalPlayer().getEyePosition().y, vec.y, vec.y + 2.0);
         double value = vec.z - 1.0;
         return new Vec3(coerceIn2, coerceIn, RangesKt.coerceIn(MinecraftHolder.getLocalPlayer().getEyePosition().z, value, vec.z + 1.0));
      } else {
         ArrayList<Vec3> arrayList = new ArrayList();
         Vec3 vec3 = vec.add(-1.0, 0.0, -1.0);

         for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
               for (int k = 0; k < 3; k++) {
                  arrayList.add(new Vec3(vec3.x + i, vec3.y + j, vec3.z + k));
               }
            }
         }

         Iterable<Vec3> iterable = arrayList;
         boolean flag = false;
         var arrayList2 = new ArrayList();

         for (Object object : iterable) {
            Vec3 vec32 = (Vec3)object;
            if (configState.isFlag9() ? BlockInteractionPoint.INSTANCE.checkContains(vec, vec32) : eyes.distanceTo(vec32) <= configState.getAmount2()) {
               arrayList2.add(object);
            }
         }

         iterable = arrayList2;
         flag = false;

         for (Vec3 sortedWithItem : (Iterable<Vec3>)CollectionsKt.sortedWith(iterable, new CrystalInteractPoint.DistanceOrdering(eyes))) {
            if (CrystalPredictor.defaultImpl(CrystalPredictor.INSTANCE, eyes, sortedWithItem, false, null, null, 28, null) == null) {
               return sortedWithItem;
            }
         }

         return null;
      }
   }

   public static Vec3 defaultImpl(CrystalInteractPoint crystalInteractPoint, Vec3 vec3, ConfigState configState, Vec3 vec32, int value, Object object) {
      if ((value & 4) != 0) {
         vec32 = InteractionManager.INSTANCE.fetchEyePosition((Entity)MinecraftHolder.getLocalPlayer());
      }

      return crystalInteractPoint.fetchEyePosition(vec3, configState, vec32);
   }

   @Nullable
   public final DamageComparatorResult fetchY(AutoCrystal self, Vec3 pos, ConfigState configState, boolean flag, int value) {
      BlockPos containing = BlockPos.containing((Position)pos.add(0.0, -0.1, 0.0));
      if (self.getRandomizeDelay()) {
         int defaultImpl2;
         if (self.getValue4() % 4 == 0) {
            defaultImpl2 = 2;
         } else {
            Object object = null;
            byte byteValue = 4;
            boolean flag2 = false;
            byte byteValue2 = 1;
            byte byteValue3 = 0;
            defaultImpl2 = MathUtil.defaultImpl((int)byteValue3, (int)byteValue2, flag2, byteValue, object);
         }

         if (value < defaultImpl2) {
            return null;
         }
      }

      if (!configState.isFlag3()) {
         int value2 = MoveFix.INSTANCE.getEnabled() ? containing.above().getY() : containing.getY();
         Vec3 pos2 = pos;
         if (MinecraftHolder.getLocalPlayer().getEyePosition().y > value2) {
            Iterable items = TrapPlanner.getItems();
            Iterator iterator = items.iterator();
            if (!iterator.hasNext()) {
               throw new NoSuchElementException();
            }

            Object object2 = iterator.next();
            Object object3;
            if (!iterator.hasNext()) {
               object3 = object2;
            } else {
               Vec3 vec3 = (Vec3)object2;
               float amount = WorldUtil.INSTANCE.computeAmount(new Vec3(containing.getX() + vec3.x, containing.getY() + vec3.y + 1.0, containing.getZ() + vec3.z));

               do {
                  Object object4 = iterator.next();
                  Vec3 vec32 = (Vec3)object4;
                  float amount2 = WorldUtil.INSTANCE.computeAmount(new Vec3(containing.getX() + vec32.x, containing.getY() + vec32.y + 1.0, containing.getZ() + vec32.z));
                  if (Float.compare(amount, amount2) > 0) {
                     object2 = object4;
                     amount = amount2;
                  }
               } while (iterator.hasNext());

               object3 = object2;
            }

            Vec3 vec33 = (Vec3)object3;
            pos2 = new Vec3(containing.getX() + vec33.x, containing.getY() + 1.0, containing.getZ() + vec33.z);
         }

         if (configState.isFlag9()) {
            if (!BlockInteractionPoint.INSTANCE.checkContains(pos, pos2)) {
               return null;
            }
         } else if (pos2.distanceToSqr(InteractionManager.INSTANCE.fetchEyePosition((Entity)MinecraftHolder.getLocalPlayer()))
            > configState.getAmount() * configState.getAmount()) {
            return null;
         }
      } else if (defaultImpl(this, pos, configState, null, 4, null) == null) {
         return null;
      }

      return SurroundPlanner.fetchAbsorptionAmount(self, containing, configState, flag, false, false);
   }

   public static final class DistanceOrdering implements Comparator {
      public final Vec3 vec3;

      public DistanceOrdering(Vec3 vec3) {
         this.vec3 = vec3;
      }

      @Override
      public final int compare(Object object, Object object2) {
         Vec3 object3 = (Vec3)object;
         Comparable distanceTo = this.vec3.distanceTo(object3);
         object3 = (Vec3)object2;
         Comparable comparable = distanceTo;
         return ComparisonsKt.compareValues(comparable, this.vec3.distanceTo(object3));
      }
   }
}
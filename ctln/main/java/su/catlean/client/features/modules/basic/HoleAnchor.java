// deobf by pusia
package su.catlean.client.features.modules.basic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.Rotation;
import su.catlean.api.enums.SwitchMode;
import su.catlean.api.event.events.player.MoveEvent;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.FloatSetting;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.features.modules.attack.AutoDripstope;
import su.catlean.client.services.ChunkScanner;
import su.catlean.client.services.CrystalPredictor;
import su.catlean.client.services.RotationManager;
import su.catlean.client.services.TickService;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.math.Vec3iUtil;
import su.catlean.utils.world.WorldUtil;

public final class HoleAnchor extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(HoleAnchor.class, "disableOverHole", "getDisableOverHole()Z", 0), new PropertyReference1Impl(HoleAnchor.class, "pitch", "getPitch()I", 0), new PropertyReference1Impl(HoleAnchor.class, "maxSpeed", "getMaxSpeed()F", 0), new PropertyReference1Impl(HoleAnchor.class, "rubberBandSpeed", "getRubberBandSpeed()F", 0), new PropertyReference1Impl(HoleAnchor.class, "rubberBandTimeOut", "getRubberBandTimeOut()I", 0), new PropertyReference1Impl(HoleAnchor.class, "useTimer", "getUseTimer()Z", 0), new PropertyReference1Impl(HoleAnchor.class, "timerValue", "getTimerValue()F", 0), new PropertyReference1Impl(HoleAnchor.class, "hRange", "getHRange()I", 0), new PropertyReference1Impl(HoleAnchor.class, "vRange", "getVRange()I", 0), new PropertyReference1Impl(HoleAnchor.class, "prioritizedFov", "getPrioritizedFov()I", 0) };
   @NotNull
   public static final HoleAnchor INSTANCE = new HoleAnchor();
   @NotNull
   private static final BooleanSetting disableOverHole = SettingFactory.defaultImpl(INSTANCE, "disable-over-hole", false, null, null, 12, null);
   @NotNull
   private static final IntSetting pitch = SettingFactory.defaultImpl(INSTANCE, "pitch", 60, new IntRange(0, 90), null, null, 24, null);
   @NotNull
   private static final FloatSetting maxSpeed = SettingFactory.defaultImpl(
      INSTANCE, "max-speed", 2.0F, RangesKt.rangeTo(0.1F, 5.0F), null, () -> !MoveFix.INSTANCE.getEnabled(), 8, null
   );
   @NotNull
   private static final FloatSetting rubberbandSpeed = SettingFactory.defaultImpl(
      INSTANCE, "rubberband-speed", 1.0F, RangesKt.rangeTo(0.1F, 5.0F), null, () -> !MoveFix.INSTANCE.getEnabled(), 8, null
   );
   @NotNull
   private static final IntSetting rubberbandTimeout = SettingFactory.defaultImpl(
      INSTANCE, "rubberband-timeout", 10, new IntRange(1, 30), null, () -> !MoveFix.INSTANCE.getEnabled(), 8, null
   );
   @NotNull
   private static final BooleanSetting useTimer = SettingFactory.defaultImpl(INSTANCE, "use-timer", false, null, () -> !MoveFix.INSTANCE.getEnabled(), 4, null);
   @NotNull
   private static final FloatSetting timerValue = SettingFactory.defaultImpl(
      INSTANCE, "timer-value", 1.0F, RangesKt.rangeTo(1.0F, 10.0F), null, () -> INSTANCE.getUseTimer() && !MoveFix.INSTANCE.getEnabled(), 8, null
   );
   @NotNull
   private static final IntSetting horizontalRange = SettingFactory.defaultImpl(INSTANCE, "horizontal-range", 3, new IntRange(1, 3), null, null, 24, null);
   @NotNull
   private static final IntSetting verticalRange = SettingFactory.defaultImpl(INSTANCE, "vertical-range", 3, new IntRange(1, 3), null, null, 24, null);
   @NotNull
   private static final IntSetting prioritizedFov = SettingFactory.defaultImpl(INSTANCE, "prioritized-fov", 30, new IntRange(10, 180), null, null, 24, null);

   private HoleAnchor() {
      super("hole-anchor", SubCategories.getSubCategory4(), null, 4, null);
   }

   private final boolean getDisableOverHole() {
      return (Boolean)disableOverHole.computeObject(this, $$delegatedProperties[0]);
   }

   private final int getPitch() {
      return ((Number)pitch.computeObject(this, $$delegatedProperties[1])).intValue();
   }

   private final float getMaxSpeed() {
      return ((Number)maxSpeed.computeObject(this, $$delegatedProperties[2])).floatValue();
   }

   private final float getRubberBandSpeed() {
      return ((Number)rubberbandSpeed.computeObject(this, $$delegatedProperties[3])).floatValue();
   }

   private final int getRubberBandTimeOut() {
      return ((Number)rubberbandTimeout.computeObject(this, $$delegatedProperties[4])).intValue();
   }

   private final boolean getUseTimer() {
      return (Boolean)useTimer.computeObject(this, $$delegatedProperties[5]);
   }

   private final float getTimerValue() {
      return ((Number)timerValue.computeObject(this, $$delegatedProperties[6])).floatValue();
   }

   public final int getHRange() {
      return ((Number)horizontalRange.computeObject(this, $$delegatedProperties[7])).intValue();
   }

   public final int getVRange() {
      return ((Number)verticalRange.computeObject(this, $$delegatedProperties[8])).intValue();
   }

   private final int getPrioritizedFov() {
      return ((Number)prioritizedFov.computeObject(this, $$delegatedProperties[9])).intValue();
   }

   @Flow
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      if (MoveFix.INSTANCE.getEnabled()) {
         BlockPos blockPos = this.getBlockPos();
         if (blockPos != null) {
            BlockPos below = blockPos.below();
            Vec3 vec3 = Vec3iUtil.computeVec3(below);
            RotationManager.INSTANCE.updateRotation(Rotation.vectorUtil.computeRotation(vec3).computeRotation(SwitchMode.FORCE), 0);
         }
      }
   }

   @Flow(
      priority = -10
   )
   private final void onMove(MoveEvent moveEvent) {
      if (!MoveFix.INSTANCE.getEnabled()) {
         BlockPos blockPos = this.getBlockPos();
         if (blockPos != null) {
            BlockPos below = blockPos.below();
            Vec3 vec3 = Vec3iUtil.computeVec3(below);
            Vec3 scale = vec3.subtract(INSTANCE.computeVec3((Entity)MinecraftHolder.getLocalPlayer()))
               .normalize()
               .scale(Math.min(INSTANCE.getAmount(), vec3.distanceTo(INSTANCE.computeVec3((Entity)MinecraftHolder.getLocalPlayer()))));
            moveEvent.setX(scale.x);
            moveEvent.setZ(scale.z);
            moveEvent.cancel();
            if (INSTANCE.getUseTimer()) {
               TickService.INSTANCE.setAmount2(INSTANCE.getTimerValue());
            }
         }
      }
   }

   private final BlockPos getBlockPos() {
      BlockPos containing = BlockPos.containing((Position)this.computeVec3((Entity)MinecraftHolder.getLocalPlayer()));
      if (MinecraftHolder.getLocalPlayer().getXRot() < this.getPitch()) {
         return null;
      } else if (ChunkScanner.INSTANCE.checkOffset(containing)) {
         if (this.getDisableOverHole()) {
            this.applyUnplug();
         }

         return null;
      } else {
         List<BlockPos> arrayList = new ArrayList();
         int hRange = containing.getX() - this.getHRange();
         int hRange2 = containing.getX() + this.getHRange();
         if (hRange <= hRange2) {
            while (true) {
               int hRange3 = containing.getZ() - this.getHRange();
               int hRange4 = containing.getZ() + this.getHRange();
               if (hRange3 <= hRange4) {
                  while (true) {
                     int vRange = containing.getY() - this.getVRange();
                     int value = containing.getY();
                     if (vRange <= value) {
                        while (true) {
                           BlockPos blockPos = new BlockPos(hRange, vRange, hRange3);
                           BlockPos below = blockPos.below();
                           Vec3 vec3 = Vec3iUtil.computeVec3(below);
                           boolean defaultImpl = CrystalPredictor.defaultImpl(CrystalPredictor.INSTANCE, vec3, null, null, null, 14, null);
                           if (defaultImpl) {
                              ChunkScanner instance = ChunkScanner.INSTANCE;
                              below = blockPos.below();
                              if (instance.checkOffset(below)) {
                                 arrayList.add(blockPos);
                              }
                           }

                           if (vRange == value) {
                              break;
                           }

                           vRange++;
                        }
                     }

                     if (hRange3 == hRange4) {
                        break;
                     }

                     hRange3++;
                  }
               }

               if (hRange == hRange2) {
                  break;
               }

               hRange++;
            }
         }

         Iterable<BlockPos> iterable = arrayList;
         boolean flag = false;
         var arrayList2 = new ArrayList();

         for (Object object : iterable) {
            BlockPos blockPos2 = (BlockPos)object;
            if (AutoDripstope.INSTANCE.computeAmount(blockPos2) <= INSTANCE.getPrioritizedFov()) {
               arrayList2.add(object);
            }
         }

         var value2 = arrayList2;
         flag = false;
         Iterator iterator = value2.iterator();
         Object object2;
         if (!iterator.hasNext()) {
            object2 = null;
         } else {
            Object object3 = iterator.next();
            if (!iterator.hasNext()) {
               object2 = (BlockPos)object3;
            } else {
               BlockPos blockPos3 = (BlockPos)object3;
               double distanceTo = INSTANCE.computeVec3((Entity)MinecraftHolder.getLocalPlayer()).distanceTo(Vec3iUtil.computeVec3(blockPos3));

               do {
                  Object object4 = iterator.next();
                  BlockPos blockPos4 = (BlockPos)object4;
                  double distanceTo2 = INSTANCE.computeVec3((Entity)MinecraftHolder.getLocalPlayer()).distanceTo(Vec3iUtil.computeVec3(blockPos4));
                  if (Double.compare(distanceTo, distanceTo2) > 0) {
                     object3 = object4;
                     distanceTo = distanceTo2;
                  }
               } while (iterator.hasNext());

               object2 = (BlockPos)object3;
            }
         }

         BlockPos blockPos5 = (BlockPos)object2;
         object2 = blockPos5;
         if (blockPos5 == null) {
            Iterable<BlockPos> iterable2 = arrayList;
            flag = false;
            iterator = iterable2.iterator();
            if (!iterator.hasNext()) {
               object2 = null;
            } else {
               Object object5 = iterator.next();
               if (!iterator.hasNext()) {
                  object2 = (BlockPos)object5;
               } else {
                  BlockPos blockPos6 = (BlockPos)object5;
                  double distanceTo3 = INSTANCE.computeVec3((Entity)MinecraftHolder.getLocalPlayer()).distanceTo(Vec3iUtil.computeVec3(blockPos6));

                  do {
                     Object object6 = iterator.next();
                     BlockPos blockPos7 = (BlockPos)object6;
                     double distanceTo4 = INSTANCE.computeVec3((Entity)MinecraftHolder.getLocalPlayer()).distanceTo(Vec3iUtil.computeVec3(blockPos7));
                     if (Double.compare(distanceTo3, distanceTo4) > 0) {
                        object5 = object6;
                        distanceTo3 = distanceTo4;
                     }
                  } while (iterator.hasNext());

                  object2 = (BlockPos)object5;
               }
            }

            object2 = object2;
         }

         return (BlockPos)object2;
      }
   }

   private final double getAmount() {
      return WorldUtil.INSTANCE.getTime4() < this.getRubberBandTimeOut() * 1000 ? 0.2873 * this.getRubberBandSpeed() : 0.2873 * this.getMaxSpeed();
   }

   static {
   }
}
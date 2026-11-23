// deobf by pusia
package su.catlean.client.services.rotation.modes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import kotlin.ranges.ClosedFloatingPointRange;
import kotlin.ranges.ClosedRange;
import kotlin.ranges.RangesKt;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.Rotation;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.math.MathUtil;
import su.catlean.utils.world.EntityPositionUtil;

public final class UnusedRotationB extends RotationStrategy {
   @NotNull
   public static final UnusedRotationB INSTANCE = new UnusedRotationB();
   private static double amount;
   private static int value;
   private static int value2;
   private static double amount2;
   @NotNull
   private static ArrayList<Float> items = new ArrayList();
   @NotNull
   private static ArrayList<Float> items2 = new ArrayList();
   private static double amount3;

   private UnusedRotationB() {
   }

   @NotNull
   @Override
   public Rotation fetchBoundingBox(Entity target, Rotation currentRotation, boolean flag) {
      double tickCount = (double)target.tickCount / 4.0F;
      double xsize = target.getBoundingBox().getXsize() / 2.3 * Math.sin(Math.toRadians(tickCount));
      double ysize = target.getBoundingBox().getYsize() / 10.0 * Math.sin(Math.toRadians(tickCount + 10.0));
      double zsize = target.getBoundingBox().getZsize() / 2.3 * -Math.cos(Math.toRadians(tickCount));
      Vec3 vec3 = EntityPositionUtil.computeVec3(target).add(xsize, target.getBbHeight() / 2.0 + ysize, zsize);
      double wrapDegrees = Mth.wrapDegrees(
         Math.toDegrees(Math.atan2(vec3.z - MinecraftHolder.getLocalPlayer().getZ(), vec3.x - MinecraftHolder.getLocalPlayer().getX()))
            - 90
            - currentRotation.getAmount()
      );
      double amount22 = -Math.toDegrees(
            Math.atan2(
               vec3.y
                  - (
                     EntityPositionUtil.computeVec3((Entity)MinecraftHolder.getLocalPlayer()).y
                        + MinecraftHolder.getLocalPlayer().getEyeHeight(MinecraftHolder.getLocalPlayer().getPose())
                  ),
               Math.hypot(vec3.x - MinecraftHolder.getLocalPlayer().getX(), vec3.z - MinecraftHolder.getLocalPlayer().getZ())
            )
         )
         - currentRotation.getAmount2();
      if (Math.abs(amount) > 300.0 || amount3 > 100.0) {
         amount = 0.0;
         value = MathUtil.computeValue(1, 3, false);
         amount2 = 0.0;
         amount3 = 0.0;
      }

      if (value > 0) {
         int value3 = value;
         value = value3 + -1;
         amount2 = 0.0;
         float amount4 = MathUtil.computeAmount(0.15F, 0.4F, false);
         return new Rotation(currentRotation.getAmount() + amount4, currentRotation.getAmount2() - amount4 / 2.0F, false, null, 12, null);
      } else {
         boolean flag2 = value2 > 20 && wrapDegrees > 60.0;
         if (flag2) {
            value2 = 0;
         }

         ClosedFloatingPointRange<Float> rangeTo = RangesKt.rangeTo(25.0F, 35.0F);
         double amount23 = flag2
            ? 84.0 + MathUtil.computeAmount2((ClosedRange)RangesKt.rangeTo(-1.0F, 1.0F), false)
            : MathUtil.computeAmount2((ClosedRange)rangeTo, true);
         double amount5 = Math.abs(MathUtil.computeAmount((ClosedRange)RangesKt.rangeTo(-5.1, 5.1), true));
         if (MinecraftHolder.getLocalPlayer().tickCount % MathUtil.computeValue(2, 10, false) == 0) {
            amount23 /= 10.0F;
         }

         wrapDegrees = RangesKt.coerceIn(wrapDegrees, -amount23, amount23);
         amount22 = RangesKt.coerceIn(amount22, -amount5, amount5);
         amount22 += wrapDegrees / 20.0F;
         float amount6 = currentRotation.getAmount() + (float)wrapDegrees;
         float amount24 = currentRotation.getAmount2() + (float)amount22;
         boolean flag3 = target.getEyePosition().distanceTo(MinecraftHolder.getLocalPlayer().getEyePosition()) < 1.5;
         float tickCount2 = flag3 ? -40.0F + MinecraftHolder.getLocalPlayer().tickCount % 100 / 25.0F + MathUtil.computeAmount(-0.1F, 0.1F, false) : -90.0F;
         float tickCount3 = flag3 ? 60.0F + MinecraftHolder.getLocalPlayer().tickCount % 100 / 25.0F + MathUtil.computeAmount(-0.1F, 0.1F, false) : 90.0F;
         amount += wrapDegrees;
         if (wrapDegrees < 20.0) {
            int value4 = value2++;
         } else {
            int value22 = value2;
            value2 = value22 + -1;
         }

         amount6 += MathUtil.computeAmount2((ClosedRange)RangesKt.rangeTo(-2.0F, 2.0F), false);
         amount24 += MathUtil.computeAmount2((ClosedRange)RangesKt.rangeTo(-0.5F, 0.5F), false);
         items.add(amount6);
         items2.add((float)wrapDegrees);
         if (items.size() >= 25) {
            float amount7 = this.computeAmount(this.computeItems(items));
            amount3 += amount7;
            items.clear();
         }

         amount2 = amount2 + MathUtil.computeAmount((ClosedRange)RangesKt.rangeTo(0.1, 0.3), false);
         return new Rotation(amount6, RangesKt.coerceIn(amount24, tickCount2, tickCount3), false, null, 12, null);
      }
   }

   private final List<Float> computeItems(List items) {
      List<Float> arrayList = new ArrayList();
      Iterator iterator = items.iterator();

      while (iterator.hasNext()) {
         float value3 = ((Number)iterator.next()).floatValue();
         arrayList.add(value3);
      }

      for (int i = 0; i < 6; i++) {
         List<Float> arrayList2 = new ArrayList();
         float miN_VALUE = Float.MIN_VALUE;
         Iterator<Float> iterator2 = arrayList.iterator();

         while (iterator2.hasNext()) {
            float value4 = ((Number)iterator2.next()).floatValue();
            if (miN_VALUE == Float.MIN_VALUE) {
               miN_VALUE = value4;
            } else {
               arrayList2.add((float)Math.abs(Math.abs((double)value4) - Math.abs((double)miN_VALUE)));
               miN_VALUE = value4;
            }
         }

         arrayList = new ArrayList(arrayList2);
      }

      return arrayList;
   }

   private final float computeAmount(List items) {
      List list = (List)items.stream().sorted().collect(Collectors.toList());
      int size = list.size();
      float value3 = 0.0F;

      for (int i = 0; i < size; i++) {
         float value4 = Math.abs((i + 1.0F) / size - ((Number)list.get(i)).floatValue());
         value3 = Math.max(value3, value4);
      }

      return value3;
   }
}
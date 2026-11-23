// deobf by pusia
package su.catlean.client.features.modules.misc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.IntIterator;
import kotlin.ranges.IntRange;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.event.events.render.Render3DEvent;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.services.CrystalPredictor;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.chat.ChatUtil;
import su.catlean.utils.math.Vec3iUtil;

public final class Test extends Module {
   @NotNull
   public static final Test INSTANCE = new Test();

   private Test() {
      super("test", SubCategories.getSubCategory11(), null, 4, null);
   }

   @Flow
   public final void onPlayerUpdate(PlayerUpdateEvent e) {
   }

   @Flow
   private final void onRender3D(Render3DEvent render3DEvent) {
      BlockPos containing = BlockPos.containing((Position)MinecraftHolder.getLocalPlayer().getEyePosition());
      byte byteValue = 6;
      IntRange intRange = new IntRange(containing.getX() - byteValue, containing.getX() + byteValue);
      IntRange intRange2 = new IntRange(containing.getY() - byteValue, containing.getY() + byteValue);
      IntRange intRange3 = new IntRange(containing.getZ() - byteValue, containing.getZ() + byteValue);
      long value = 0L;
      long value2 = 0L;
      Iterable iterable = (Iterable)intRange;
      Collection arrayList = new ArrayList();
      Iterator iterator = iterable.iterator();

      while (iterator.hasNext()) {
         int nextInt = ((IntIterator)iterator).nextInt();
         int value3 = nextInt;
         Iterable iterable2 = (Iterable)intRange2;
         var arrayList2 = new ArrayList();
         Iterator iterator2 = iterable2.iterator();

         while (iterator2.hasNext()) {
            int nextInt2 = ((IntIterator)iterator2).nextInt();
            int value4 = nextInt2;
            Iterable iterable3 = (Iterable)intRange3;
            var arrayList3 = new ArrayList();
            Iterator iterator3 = iterable3.iterator();

            while (iterator3.hasNext()) {
               int nextInt3 = ((IntIterator)iterator3).nextInt();
               CrystalPredictor.INSTANCE.setFlag(false);
               float defaultImpl = 0.0F;
               long time = MinecraftHolder.getTime();
               CrystalPredictor instance = CrystalPredictor.INSTANCE;
               BlockPos blockPos = new BlockPos(value3, value4, nextInt3);
               defaultImpl = CrystalPredictor.defaultImpl(instance, Vec3iUtil.computeVec3(blockPos).add(0.0, 0.5, 0.0), null, 0, true, null, null, 54, null);
               Unit instance2 = Unit.INSTANCE;
               value2 += ((Number)TuplesKt.to(instance2, MinecraftHolder.getTime() - time).getSecond()).longValue();
               CrystalPredictor.INSTANCE.setFlag(true);
               long time2 = MinecraftHolder.getTime();
               instance = CrystalPredictor.INSTANCE;
               blockPos = new BlockPos(value3, value4, nextInt3);
               float defaultImpl2 = CrystalPredictor.defaultImpl(instance, Vec3iUtil.computeVec3(blockPos).add(0.0, 0.5, 0.0), null, 0, true, null, null, 54, null);
               instance2 = Unit.INSTANCE;
               value += ((Number)TuplesKt.to(instance2, MinecraftHolder.getTime() - time2).getSecond()).longValue();
               if (defaultImpl != defaultImpl2) {
                  ChatUtil.defaultImpl(INSTANCE, new BlockPos(value3, value4, nextInt3) + " " + defaultImpl + " != " + defaultImpl2, false, 2, null);
               }

               Unit instance3 = Unit.INSTANCE;
               arrayList3.add(instance3);
            }

            var value6 = arrayList3;
            CollectionsKt.addAll(arrayList2, value6);
         }

         var value7 = arrayList2;
         CollectionsKt.addAll(arrayList, value7);
      }

      int value8 = (int)((float)(value2 - value) / (float)Math.max(value2, 1L) * 100.0F);
      ChatUtil.defaultImpl(this, "time: " + value2 + "; optimized: " + value + ", saved: " + value8 + "%", false, 2, null);
   }
}
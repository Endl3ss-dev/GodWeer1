// deobf by pusia
package su.catlean.client.services;

import su.catlean.utils.KotlinCompat;
import kotlin.text.StringsKt;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.DamageComparatorResult;
import su.catlean.client.features.modules.attack.AutoCrystal;
import su.catlean.client.features.modules.player.SpeedMine;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.StopWatch;

public final class ServerConfigProfile {
   @NotNull
   public static final ServerConfigProfile INSTANCE = new ServerConfigProfile();
   private static float amount;
   @NotNull
   private static final StopWatch stopWatch = new StopWatch();
   @NotNull
   private static final StopWatch stopWatch2 = new StopWatch();
   private static boolean flag;
   private static float amount2;
   private static long time;

   private ServerConfigProfile() {
   }

   public final float getAmount() {
      return amount;
   }

   public final void setAmount(float amount) {
      ServerConfigProfile.amount = amount;
   }

   @NotNull
   public final StopWatch getStopWatch() {
      return stopWatch;
   }

   @NotNull
   public final StopWatch getStopWatch2() {
      return stopWatch2;
   }

   public final boolean isFlag() {
      return flag;
   }

   public final void setFlag(boolean flag) {
      ServerConfigProfile.flag = flag;
   }

   public final float getAmount2() {
      return amount2;
   }

   public final void setAmount2(float amount) {
      amount2 = amount;
   }

   public final long getTime() {
      return time;
   }

   public final void setTime(long time) {
      ServerConfigProfile.time = time;
   }

   public final void updateAmount(float amount) {
      ServerConfigProfile.amount = amount;
      stopWatch.update();
      flag = !flag;
   }

   public final void getStopWatch3() {
      stopWatch2.update();
   }

   public final boolean checkGetServerData(
      DamageComparatorResult damageComparatorResult, boolean flag, AutoCrystal.DamageSync damageSync, boolean flag2, boolean flag3, float amount, float amount2
   ) {
      if (damageComparatorResult == null) {
         return true;
      } else if (damageSync == AutoCrystal.DamageSync.OFF) {
         return true;
      } else {
         if (damageSync == AutoCrystal.DamageSync.CONDITIONS) {
            boolean flag4 = flag3 && InventoryManager.INSTANCE.fetchItem(Items.END_CRYSTAL, true) <= 64 * amount
               || flag2 && !damageComparatorResult.isFlag2() && damageComparatorResult.getAmount() < amount2;
            if (!flag4) {
               return true;
            }
         }

         if (damageComparatorResult.getAmount() - ServerConfigProfile.amount > 2.0F) {
            return true;
         } else if (stopWatch.testValue((int)((510.0F + (flag ? 60 : 0)) / ServerInfoService.INSTANCE.getAmount3()))) {
            return true;
         } else if (!SpeedMine.defaultImpl(SpeedMine.INSTANCE, false, 1, null).isEmpty()) {
            return true;
         } else {
            if (MinecraftHolder.getClientPacketListener().getServerData() != null
               && KotlinCompat.contains(MinecraftHolder.getClientPacketListener().getServerData().ip, "tntland", false, 2, null)) {
               if (ServerConfigProfile.flag) {
                  return true;
               }

               if (!stopWatch2.testValue(15500)) {
                  return true;
               }
            }

            return false;
         }
      }
   }

   public final float computeAmount(float amount) {
      return amount;
   }

   public final void update() {
      amount = 0.0F;
      amount2 = 0.0F;
      time = 0L;
   }
}
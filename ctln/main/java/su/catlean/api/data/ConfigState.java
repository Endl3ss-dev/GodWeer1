// deobf by pusia
package su.catlean.api.data;

import java.util.List;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import su.catlean.client.features.modules.attack.AutoCrystal;

public final class ConfigState {
   @NotNull
   private final List entities;
   @NotNull
   private final Player target;
   private final float amount;
   private final float amount2;
   private final float amount3;
   private final float amount4;
   private final boolean flag;
   private final float amount5;
   private final float amount6;
   private final boolean flag2;
   private final float amount7;
   private final boolean flag3;
   @NotNull
   private final AutoCrystal.FriendProtection friendProtection;
   private final boolean flag4;
   private final boolean flag5;
   private final int value;
   private final boolean flag6;
   private final float amount8;
   private final boolean flag7;
   private final boolean flag8;
   private final boolean flag9;
   private final boolean flag10;
   private final boolean flag11;
   private final boolean flag12;
   private final boolean flag13;

   public ConfigState(
      List entities,
      Player target,
      float amount,
      float amount2,
      float amount3,
      float amount4,
      boolean flag,
      float amount5,
      float amount6,
      boolean flag2,
      float amount7,
      boolean flag3,
      AutoCrystal.FriendProtection friendProtection,
      boolean flag4,
      boolean flag5,
      int value,
      boolean flag6,
      float amount8,
      boolean flag7,
      boolean flag8,
      boolean flag9,
      boolean flag10,
      boolean flag11,
      boolean flag12,
      boolean flag13
   ) {
      this.entities = entities;
      this.target = target;
      this.amount = amount;
      this.amount2 = amount2;
      this.amount3 = amount3;
      this.amount4 = amount4;
      this.flag = flag;
      this.amount5 = amount5;
      this.amount6 = amount6;
      this.flag2 = flag2;
      this.amount7 = amount7;
      this.flag3 = flag3;
      this.friendProtection = friendProtection;
      this.flag4 = flag4;
      this.flag5 = flag5;
      this.value = value;
      this.flag6 = flag6;
      this.amount8 = amount8;
      this.flag7 = flag7;
      this.flag8 = flag8;
      this.flag9 = flag9;
      this.flag10 = flag10;
      this.flag11 = flag11;
      this.flag12 = flag12;
      this.flag13 = flag13;
   }

   @NotNull
   public final List getEntities() {
      return this.entities;
   }

   @NotNull
   public final Player getTarget() {
      return this.target;
   }

   public final float getAmount() {
      return this.amount;
   }

   public final float getAmount2() {
      return this.amount2;
   }

   public final float getAmount3() {
      return this.amount3;
   }

   public final float getAmount4() {
      return this.amount4;
   }

   public final boolean isFlag() {
      return this.flag;
   }

   public final float getAmount5() {
      return this.amount5;
   }

   public final float getAmount6() {
      return this.amount6;
   }

   public final boolean isFlag2() {
      return this.flag2;
   }

   public final float getAmount7() {
      return this.amount7;
   }

   public final boolean isFlag3() {
      return this.flag3;
   }

   @NotNull
   public final AutoCrystal.FriendProtection getFriendProtection() {
      return this.friendProtection;
   }

   public final boolean isFlag4() {
      return this.flag4;
   }

   public final boolean isFlag5() {
      return this.flag5;
   }

   public final int getValue() {
      return this.value;
   }

   public final boolean isFlag6() {
      return this.flag6;
   }

   public final float getAmount8() {
      return this.amount8;
   }

   public final boolean isFlag7() {
      return this.flag7;
   }

   public final boolean isFlag8() {
      return this.flag8;
   }

   public final boolean isFlag9() {
      return this.flag9;
   }

   public final boolean isFlag10() {
      return this.flag10;
   }

   public final boolean isFlag11() {
      return this.flag11;
   }

   public final boolean isFlag12() {
      return this.flag12;
   }

   public final boolean isFlag13() {
      return this.flag13;
   }

   @NotNull
   public final List getEntities2() {
      return this.entities;
   }

   @NotNull
   public final Player getTarget2() {
      return this.target;
   }

   public final float getAmount9() {
      return this.amount;
   }

   public final float getAmount10() {
      return this.amount2;
   }

   public final float getAmount11() {
      return this.amount3;
   }

   public final float getAmount12() {
      return this.amount4;
   }

   public final boolean isFlag14() {
      return this.flag;
   }

   public final float getAmount13() {
      return this.amount5;
   }

   public final float getAmount14() {
      return this.amount6;
   }

   public final boolean isFlag15() {
      return this.flag2;
   }

   public final float getAmount15() {
      return this.amount7;
   }

   public final boolean isFlag16() {
      return this.flag3;
   }

   @NotNull
   public final AutoCrystal.FriendProtection getFriendProtection2() {
      return this.friendProtection;
   }

   public final boolean isFlag17() {
      return this.flag4;
   }

   public final boolean isFlag18() {
      return this.flag5;
   }

   public final int getValue2() {
      return this.value;
   }

   public final boolean isFlag19() {
      return this.flag6;
   }

   public final float getAmount16() {
      return this.amount8;
   }

   public final boolean isFlag20() {
      return this.flag7;
   }

   public final boolean isFlag21() {
      return this.flag8;
   }

   public final boolean isFlag22() {
      return this.flag9;
   }

   public final boolean isFlag23() {
      return this.flag10;
   }

   public final boolean isFlag24() {
      return this.flag11;
   }

   public final boolean isFlag25() {
      return this.flag12;
   }

   public final boolean isFlag26() {
      return this.flag13;
   }

   @NotNull
   @Override
   public String toString() {
      boolean flag132 = this.flag13;
      boolean flag122 = this.flag12;
      boolean flag112 = this.flag11;
      boolean flag102 = this.flag10;
      boolean flag92 = this.flag9;
      boolean flag82 = this.flag8;
      boolean flag72 = this.flag7;
      float amount82 = this.amount8;
      boolean flag62 = this.flag6;
      int value2 = this.value;
      boolean flag52 = this.flag5;
      boolean flag42 = this.flag4;
      AutoCrystal.FriendProtection friendProtection2 = this.friendProtection;
      boolean flag32 = this.flag3;
      float amount72 = this.amount7;
      boolean flag22 = this.flag2;
      float amount62 = this.amount6;
      float amount52 = this.amount5;
      boolean flag14 = this.flag;
      float amount42 = this.amount4;
      float amount32 = this.amount3;
      float amount22 = this.amount2;
      float amount9 = this.amount;
      Player target2 = this.target;
      List entities2 = this.entities;
      return "ConfigState(entities="
         + entities2
         + ", target="
         + target2
         + ", placeRange="
         + amount9
         + ", explodeRange="
         + amount22
         + ", maxSelfDamage="
         + amount32
         + ", minDamage="
         + amount42
         + ", oldVer="
         + flag14
         + ", armorScale="
         + amount52
         + ", facePlaceHp="
         + amount62
         + ", overrideSelfDamage="
         + flag22
         + ", efficiencyFactor="
         + amount72
         + ", rayTrace="
         + flag32
         + ", friendProtection="
         + friendProtection2
         + ", breakFire="
         + flag42
         + ", ignoreTerrain="
         + flag52
         + ", lethalMultiplier="
         + value2
         + ", minimizeSelfDamage="
         + flag62
         + ", maxDamageReduction="
         + amount82
         + ", calcShields="
         + flag72
         + ", stringWaterRemove="
         + flag82
         + ", grimBreakRange="
         + flag92
         + ", explodeEChestFarm="
         + flag102
         + ", antiPortal="
         + flag112
         + ", antiQuiver="
         + flag122
         + ", overrideMinDamage="
         + flag132
         + ")";
   }

   @Override
   public int hashCode() {
      int hashCode = this.entities.hashCode();
      hashCode = hashCode * 31 + this.target.hashCode();
      hashCode = hashCode * 31 + Float.hashCode(this.amount);
      hashCode = hashCode * 31 + Float.hashCode(this.amount2);
      hashCode = hashCode * 31 + Float.hashCode(this.amount3);
      hashCode = hashCode * 31 + Float.hashCode(this.amount4);
      hashCode = hashCode * 31 + Boolean.hashCode(this.flag);
      hashCode = hashCode * 31 + Float.hashCode(this.amount5);
      hashCode = hashCode * 31 + Float.hashCode(this.amount6);
      hashCode = hashCode * 31 + Boolean.hashCode(this.flag2);
      hashCode = hashCode * 31 + Float.hashCode(this.amount7);
      hashCode = hashCode * 31 + Boolean.hashCode(this.flag3);
      hashCode = hashCode * 31 + this.friendProtection.hashCode();
      hashCode = hashCode * 31 + Boolean.hashCode(this.flag4);
      hashCode = hashCode * 31 + Boolean.hashCode(this.flag5);
      hashCode = hashCode * 31 + Integer.hashCode(this.value);
      hashCode = hashCode * 31 + Boolean.hashCode(this.flag6);
      hashCode = hashCode * 31 + Float.hashCode(this.amount8);
      hashCode = hashCode * 31 + Boolean.hashCode(this.flag7);
      hashCode = hashCode * 31 + Boolean.hashCode(this.flag8);
      hashCode = hashCode * 31 + Boolean.hashCode(this.flag9);
      hashCode = hashCode * 31 + Boolean.hashCode(this.flag10);
      hashCode = hashCode * 31 + Boolean.hashCode(this.flag11);
      hashCode = hashCode * 31 + Boolean.hashCode(this.flag12);
      return hashCode * 31 + Boolean.hashCode(this.flag13);
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof ConfigState configState)) {
         return false;
      } else if (!Intrinsics.areEqual(this.entities, configState.entities)) {
         return false;
      } else if (!Intrinsics.areEqual(this.target, configState.target)) {
         return false;
      } else if (Float.compare(this.amount, configState.amount) != 0) {
         return false;
      } else if (Float.compare(this.amount2, configState.amount2) != 0) {
         return false;
      } else if (Float.compare(this.amount3, configState.amount3) != 0) {
         return false;
      } else if (Float.compare(this.amount4, configState.amount4) != 0) {
         return false;
      } else if (this.flag != configState.flag) {
         return false;
      } else if (Float.compare(this.amount5, configState.amount5) != 0) {
         return false;
      } else if (Float.compare(this.amount6, configState.amount6) != 0) {
         return false;
      } else if (this.flag2 != configState.flag2) {
         return false;
      } else if (Float.compare(this.amount7, configState.amount7) != 0) {
         return false;
      } else if (this.flag3 != configState.flag3) {
         return false;
      } else if (this.friendProtection != configState.friendProtection) {
         return false;
      } else if (this.flag4 != configState.flag4) {
         return false;
      } else if (this.flag5 != configState.flag5) {
         return false;
      } else if (this.value != configState.value) {
         return false;
      } else if (this.flag6 != configState.flag6) {
         return false;
      } else if (Float.compare(this.amount8, configState.amount8) != 0) {
         return false;
      } else if (this.flag7 != configState.flag7) {
         return false;
      } else if (this.flag8 != configState.flag8) {
         return false;
      } else if (this.flag9 != configState.flag9) {
         return false;
      } else if (this.flag10 != configState.flag10) {
         return false;
      } else if (this.flag11 != configState.flag11) {
         return false;
      } else {
         return this.flag12 != configState.flag12 ? false : this.flag13 == configState.flag13;
      }
   }
}
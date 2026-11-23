// deobf by pusia
package su.catlean.api.data;

import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

public final class SettingNameOverride {
   @NotNull
   private final String name;
   @NotNull
   private final Number value;
   @NotNull
   private final String overrideName;

   public SettingNameOverride(String name, Number value, String overrideName) {
      this.name = name;
      this.value = value;
      this.overrideName = overrideName;
   }

   @NotNull
   public final String getName() {
      return this.name;
   }

   @NotNull
   public final Number getValue() {
      return this.value;
   }

   @NotNull
   public final String getOverrideName() {
      return this.overrideName;
   }

   @NotNull
   public final String getName2() {
      return this.name;
   }

   @NotNull
   public final Number getValue2() {
      return this.value;
   }

   @NotNull
   public final String getOverrideName2() {
      return this.overrideName;
   }

   @NotNull
   @Override
   public String toString() {
      String overrideName2 = this.overrideName;
      Number value2 = this.value;
      String name2 = this.name;
      return "SettingNameOverride(name=" + name2 + ", value=" + value2 + ", overrideName=" + overrideName2 + ")";
   }

   @Override
   public int hashCode() {
      int hashCode = this.name.hashCode();
      hashCode = hashCode * 31 + this.value.hashCode();
      return hashCode * 31 + this.overrideName.hashCode();
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof SettingNameOverride settingNameOverride)) {
         return false;
      } else if (!Intrinsics.areEqual(this.name, settingNameOverride.name)) {
         return false;
      } else {
         return !Intrinsics.areEqual(this.value, settingNameOverride.value) ? false : Intrinsics.areEqual(this.overrideName, settingNameOverride.overrideName);
      }
   }
}
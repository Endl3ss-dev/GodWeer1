// deobf by pusia
package su.catlean.client.features.modules.basic;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.reflect.KProperty;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;

public final class MoveFix extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{new PropertyReference1Impl(MoveFix.class, "mode", "getMode()Lsu/catlean/yz;", 0)};
   @NotNull
   public static final MoveFix INSTANCE = new MoveFix();
   @NotNull
   private static final EnumSetting mode = SettingFactory.defaultImpl(INSTANCE, "mode", MoveFix.Mode.FREE, null, null, 12, null);

   private MoveFix() {
      super("move-fix", SubCategories.getSubCategory4(), null, 4, null);
   }

   @NotNull
   public final MoveFix.Mode getMode() {
      return (MoveFix.Mode)mode.computeObject(this, $$delegatedProperties[0]);
   }

   static {
   }

   public static enum Mode {
      FREE,
      FOCUS;

      private static final MoveFix.Mode[] modes = $values();
      private static final EnumEntries<MoveFix.Mode> enumEntries = EnumEntriesKt.enumEntries(modes);

      private Mode() {
      }

      @NotNull
      public static EnumEntries<MoveFix.Mode> getEnumEntries() {
         return enumEntries;
      }

      private static final MoveFix.Mode[] $values() {
         return new MoveFix.Mode[]{FREE, FOCUS};
      }
   }
}
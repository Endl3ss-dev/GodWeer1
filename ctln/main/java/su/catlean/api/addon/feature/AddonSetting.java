// deobf by pusia
package su.catlean.api.addon.feature;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.ranges.ClosedFloatingPointRange;
import kotlin.reflect.KProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class AddonSetting<T> {
   @NotNull
   private final String id;
   private final T defaultValue;
   @NotNull
   private final AddonSetting.Kind kind;
   @Nullable
   private final ClosedFloatingPointRange<Float> range;
   private T value;

   public AddonSetting(String id, T object, AddonSetting.Kind kind, ClosedFloatingPointRange<Float> closedFloatingPointRange) {
      this.id = id;
      this.defaultValue = (T)object;
      this.kind = kind;
      this.range = closedFloatingPointRange;
      this.value = this.defaultValue;
   }

   public AddonSetting(
      String text,
      Object object,
      AddonSetting.Kind kind,
      ClosedFloatingPointRange closedFloatingPointRange,
      int value,
      DefaultConstructorMarker defaultConstructorMarker
   ) {
      if ((value & 8) != 0) {
         closedFloatingPointRange = null;
      }

      this(text, (T)object, kind, closedFloatingPointRange);
   }

   @NotNull
   public final String getId() {
      return this.id;
   }

   public final T getDefaultValue() {
      return this.defaultValue;
   }

   @NotNull
   public final AddonSetting.Kind getKind$su_catlean() {
      return this.kind;
   }

   @Nullable
   public final ClosedFloatingPointRange<Float> getRange$su_catlean() {
      return this.range;
   }

   public final T getValue() {
      return this.value;
   }

   public final void setValue$su_catlean(T object) {
      this.value = (T)object;
   }

   public final void set(T object) {
      this.value = (T)object;
   }

   public final T getValue(Object object, KProperty<?> property) {
      return this.value;
   }

   public final void setValue(Object object, KProperty<?> property, T object2) {
      this.set((T)object2);
   }

   public static enum Kind {
      BOOLEAN,
      INT,
      FLOAT,
      STRING,
      ENUM;

      private static final EnumEntries<AddonSetting.Kind> $ENTRIES = EnumEntriesKt.enumEntries(values());

      private Kind() {
      }

      @NotNull
      public static EnumEntries<AddonSetting.Kind> getEntries() {
         return $ENTRIES;
      }

      private static final AddonSetting.Kind[] $values() {
         return new AddonSetting.Kind[]{BOOLEAN, INT, FLOAT, STRING, ENUM};
      }
   }
}
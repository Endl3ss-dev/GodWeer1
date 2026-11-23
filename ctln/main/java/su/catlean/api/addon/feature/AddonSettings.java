// deobf by pusia
package su.catlean.api.addon.feature;

import java.util.ArrayList;
import java.util.List;
import kotlin.ranges.ClosedFloatingPointRange;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import org.jetbrains.annotations.NotNull;

public final class AddonSettings {
   @NotNull
   private final List<AddonSetting<?>> entries = new ArrayList<>();

   public AddonSettings() {
   }

   @NotNull
   public final List<AddonSetting<?>> getEntries$su_catlean() {
      return this.entries;
   }

   @NotNull
   public final AddonSetting<Boolean> booleanSetting(String id, boolean flag) {
      return this.add(new AddonSetting<>(id, flag, AddonSetting.Kind.BOOLEAN, null, 8, null));
   }

   @NotNull
   public final AddonSetting<Integer> intSetting(String id, int value, IntRange range) {
      return this.add(new AddonSetting<>(id, value, AddonSetting.Kind.INT, RangesKt.rangeTo(range.getFirst(), range.getLast())));
   }

   @NotNull
   public final AddonSetting<Float> floatSetting(String id, float amount, ClosedFloatingPointRange<Float> range) {
      return this.add(new AddonSetting<>(id, amount, AddonSetting.Kind.FLOAT, range));
   }

   @NotNull
   public final AddonSetting<String> string(String id, String defaultValue) {
      return this.add(new AddonSetting<>(id, defaultValue, AddonSetting.Kind.STRING, null, 8, null));
   }

   @NotNull
   public final <E extends Enum<E>> AddonSetting<E> enumSetting(String id, E defaultValue) {
      return this.add(new AddonSetting<>(id, defaultValue, AddonSetting.Kind.ENUM, null, 8, null));
   }

   private final <T> AddonSetting<T> add(AddonSetting<T> addonSetting) {
      this.entries.add(addonSetting);
      return addonSetting;
   }
}
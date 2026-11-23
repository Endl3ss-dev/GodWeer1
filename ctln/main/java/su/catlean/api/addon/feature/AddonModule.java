// deobf by pusia
package su.catlean.api.addon.feature;

import java.util.List;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.ranges.ClosedFloatingPointRange;
import kotlin.ranges.IntRange;
import org.jetbrains.annotations.NotNull;

public abstract class AddonModule {
   @NotNull
   private final String id;
   @NotNull
   private final String categoryId;
   @NotNull
   private final List<String> aliases;
   @NotNull
   private final AddonSettings settings;

   public AddonModule(String id, String categoryId, List<String> aliases) {
      this.id = id;
      this.categoryId = categoryId;
      this.aliases = aliases;
      this.settings = new AddonSettings();
   }

   public AddonModule(String text, String text2, List items, int value, DefaultConstructorMarker defaultConstructorMarker) {
      if ((value & 2) != 0) {
         text2 = "misc";
      }

      if ((value & 4) != 0) {
         items = CollectionsKt.emptyList();
      }

      this(text, text2, items);
   }

   @NotNull
   public final String getId() {
      return this.id;
   }

   @NotNull
   public final String getCategoryId() {
      return this.categoryId;
   }

   @NotNull
   public final List<String> getAliases() {
      return this.aliases;
   }

   @NotNull
   public final AddonSettings getSettings() {
      return this.settings;
   }

   @NotNull
   public final AddonSetting<Boolean> setting(String id, boolean flag) {
      return this.settings.booleanSetting(id, flag);
   }

   @NotNull
   public final AddonSetting<Integer> setting(String id, int value, IntRange range) {
      return this.settings.intSetting(id, value, range);
   }

   @NotNull
   public final AddonSetting<Float> setting(String id, float amount, ClosedFloatingPointRange<Float> range) {
      return this.settings.floatSetting(id, amount, range);
   }

   @NotNull
   public final AddonSetting<String> setting(String id, String defaultValue) {
      return this.settings.string(id, defaultValue);
   }

   @NotNull
   public final <E extends Enum<E>> AddonSetting<E> setting(String id, E defaultValue) {
      return this.settings.enumSetting(id, (E)defaultValue);
   }

   public void onEnable() {
   }

   public void onDisable() {
   }

   public void onPlayerUpdate() {
   }
}
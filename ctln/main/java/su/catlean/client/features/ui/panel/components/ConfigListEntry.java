// deobf by pusia
package su.catlean.client.features.ui.panel.components;

import kotlin.jvm.internal.DefaultConstructorMarker;
import org.jetbrains.annotations.NotNull;

public final class ConfigListEntry {
   @NotNull
   private String name;
   private float amount;
   private float amount2;
   @NotNull
   private final ConfigType type;

   public ConfigListEntry(String name, float amount, float amount2, ConfigType type) {
      this.name = name;
      this.amount = amount;
      this.amount2 = amount2;
      this.type = type;
   }

   public ConfigListEntry(String text, float amount, float amount2, ConfigType configType, int value, DefaultConstructorMarker defaultConstructorMarker) {
      if ((value & 2) != 0) {
         amount = 0.0F;
      }

      if ((value & 4) != 0) {
         amount2 = 0.0F;
      }

      this(text, amount, amount2, configType);
   }

   @NotNull
   public final String getName() {
      return this.name;
   }

   public final float getAmount() {
      return this.amount;
   }

   public final void setAmount(float amount) {
      this.amount = amount;
   }

   public final float getAmount2() {
      return this.amount2;
   }

   public final void setAmount2(float amount) {
      this.amount2 = amount;
   }

   @NotNull
   public final ConfigType getType() {
      return this.type;
   }
}
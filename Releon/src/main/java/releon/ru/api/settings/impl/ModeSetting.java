package releon.ru.api.settings.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import releon.ru.api.settings.Setting;
import releon.ru.api.settings.SettingType;

public class ModeSetting extends Setting<String> {
   private final List<String> modes;
   private final boolean renderDescription;
   private Predicate<String> visibleMode = mode -> true;

   public ModeSetting(String name, String description, String defaultValue, String... modes) {
      this(name, description, true, defaultValue, modes);
   }

   public ModeSetting(String name, String description, boolean renderDescription, String defaultValue, String... modes) {
      super(name, description, defaultValue, SettingType.MODE);
      this.modes = List.of(modes);
      this.renderDescription = renderDescription;
      this.setValue(defaultValue);
   }

   public List<String> getModes() {
      return this.modes;
   }

   public List<String> getVisibleModes() {
      List<String> visibleModes = new ArrayList<>();

      for (String mode : this.modes) {
         if (this.isModeVisible(mode)) {
            visibleModes.add(mode);
         }
      }

      return visibleModes;
   }

   public boolean isModeVisible(String mode) {
      return this.visibleMode.test(mode);
   }

   public ModeSetting visibleModesWhen(Predicate<String> visibleMode) {
      this.visibleMode = visibleMode == null ? mode -> true : visibleMode;
      return this;
   }

   public boolean shouldRenderDescription() {
      return this.renderDescription;
   }

   public boolean is(String mode) {
      if (mode == null) {
         return false;
      } else {
         for (String availableMode : this.modes) {
            if (availableMode.equalsIgnoreCase(mode)) {
               return this.getValue().equals(availableMode);
            }
         }

         return false;
      }
   }

   public boolean isSelected(String mode) {
      return this.is(mode);
   }

   @Override
   public JsonElement toJson() {
      return new JsonPrimitive(this.getValue());
   }

   @Override
   public void fromJson(JsonElement element) {
      if (element != null && element.isJsonPrimitive()) {
         this.setValue(element.getAsString());
      }
   }

   protected String normalize(String value) {
      if (value != null && !this.modes.isEmpty()) {
         for (String mode : this.modes) {
            if (mode.equalsIgnoreCase(value)) {
               return mode;
            }
         }

         return this.modes.getFirst();
      } else {
         return this.modes.isEmpty() ? "" : this.modes.getFirst();
      }
   }
}

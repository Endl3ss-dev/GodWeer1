package releon.ru.screens.modernui.settings;

import releon.ru.api.settings.Setting;
import releon.ru.api.settings.impl.BindSetting;
import releon.ru.api.settings.impl.BooleanSetting;
import releon.ru.api.settings.impl.ButtonSetting;
import releon.ru.api.settings.impl.ColorSetting;
import releon.ru.api.settings.impl.ModeSetting;
import releon.ru.api.settings.impl.MultiModeSetting;
import releon.ru.api.settings.impl.NumberSetting;
import releon.ru.api.settings.impl.StringSetting;
import releon.ru.api.settings.impl.ThemeSetting;

final class SettingCardFactory {
   private SettingCardFactory() {
   }

   static SettingCardComponent<?> create(Setting<?> setting) {
      if (setting instanceof ThemeSetting themeSetting) {
         return new ThemeSettingCard(themeSetting);
      } else if (setting instanceof ModeSetting modeSetting) {
         return new ModeSettingCard(modeSetting);
      } else if (setting instanceof MultiModeSetting multiModeSetting) {
         return new MultiSettingCard(multiModeSetting);
      } else if (setting instanceof NumberSetting numberSetting) {
         return new SliderSettingCard(numberSetting);
      } else if (setting instanceof BooleanSetting booleanSetting) {
         return new BooleanSettingCard(booleanSetting);
      } else if (setting instanceof ColorSetting colorSetting) {
         return new ColorSettingCard(colorSetting);
      } else if (setting instanceof BindSetting bindSetting) {
         return new BindSettingCard(bindSetting);
      } else if (setting instanceof StringSetting stringSetting) {
         return new TextSettingCard(stringSetting);
      } else {
         return setting instanceof ButtonSetting buttonSetting ? new ButtonSettingCard(buttonSetting) : null;
      }
   }
}

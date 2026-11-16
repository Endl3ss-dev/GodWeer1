package releon.ru.screens.clickgui.impl.module;

import java.util.ArrayList;
import java.util.List;
import releon.ru.api.module.Module;
import releon.ru.api.settings.Setting;
import releon.ru.api.settings.impl.BindSetting;
import releon.ru.api.settings.impl.BooleanSetting;
import releon.ru.api.settings.impl.ButtonSetting;
import releon.ru.api.settings.impl.ColorSetting;
import releon.ru.api.settings.impl.ModeSetting;
import releon.ru.api.settings.impl.MultiModeSetting;
import releon.ru.api.settings.impl.NumberSetting;
import releon.ru.api.settings.impl.StringSetting;
import releon.ru.screens.clickgui.impl.options.BindOption;
import releon.ru.screens.clickgui.impl.options.BooleanOption;
import releon.ru.screens.clickgui.impl.options.ButtonOption;
import releon.ru.screens.clickgui.impl.options.ClickGuiOption;
import releon.ru.screens.clickgui.impl.options.ColorOption;
import releon.ru.screens.clickgui.impl.options.MultiOption;
import releon.ru.screens.clickgui.impl.options.SingleOption;
import releon.ru.screens.clickgui.impl.options.SliderOption;
import releon.ru.screens.clickgui.impl.options.TextOption;

public final class ModuleOptionFactory {
   private ModuleOptionFactory() {
   }

   public static ModuleOption create(Module module) {
      return new ModuleOption(module, createSettings(module));
   }

   private static List<ClickGuiOption> createSettings(Module module) {
      List<ClickGuiOption> options = new ArrayList<>();
      List<ClickGuiOption> buttonOptions = new ArrayList<>();

      for (Setting<?> setting : module.getSettings()) {
         ClickGuiOption option = createSettingOption(setting);
         if (option != null) {
            option.setSetting(setting);
            if (setting instanceof ButtonSetting) {
               buttonOptions.add(option);
            } else {
               options.add(option);
            }
         }
      }

      options.addAll(buttonOptions);
      return List.copyOf(options);
   }

   private static ClickGuiOption createSettingOption(Setting<?> setting) {
      if (setting instanceof ModeSetting modeSetting) {
         return new SingleOption(modeSetting);
      } else if (setting instanceof MultiModeSetting multiModeSetting) {
         return new MultiOption(multiModeSetting);
      } else if (setting instanceof NumberSetting numberSetting) {
         return new SliderOption(numberSetting, "");
      } else if (setting instanceof BooleanSetting booleanSetting) {
         return new BooleanOption(booleanSetting);
      } else if (setting instanceof StringSetting stringSetting) {
         return new TextOption(stringSetting);
      } else if (setting instanceof ColorSetting colorSetting) {
         return new ColorOption(colorSetting);
      } else if (setting instanceof BindSetting bindSetting) {
         return new BindOption(bindSetting);
      } else {
         return setting instanceof ButtonSetting buttonSetting ? new ButtonOption(buttonSetting, "Apply") : null;
      }
   }
}

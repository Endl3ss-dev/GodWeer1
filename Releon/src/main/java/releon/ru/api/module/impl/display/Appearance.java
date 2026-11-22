package releon.ru.api.module.impl.display;

import releon.ru.api.module.Module;
import releon.ru.api.module.ModuleCategory;
import releon.ru.api.settings.impl.ThemeSetting;

public final class Appearance extends Module {
   private final ThemeSetting theme = this.register(new ThemeSetting());

   public Appearance() {
      super("Appearance", "Client interface themes.", ModuleCategory.DISPLAY);
   }

   public ThemeSetting theme() {
      return this.theme;
   }
}

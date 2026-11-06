package releon.ru.api.module.impl.visual;

import releon.ru.api.module.Module;
import releon.ru.api.module.ModuleCategory;

public class GuiHands extends Module {
   private static GuiHands instance;

   public GuiHands() {
      super("Gui Hands", "Renders first-person hands while ClickGUI is open.", ModuleCategory.VISUAL);
      instance = this;
   }

   public static boolean shouldRender() {
      return instance != null && instance.isEnabled();
   }
}

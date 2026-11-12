package releon.ru.api.module.impl.visual;

import releon.ru.api.module.Module;
import releon.ru.api.module.ModuleCategory;

public class ItemPhysics extends Module {
   private static ItemPhysics instance;

   public ItemPhysics() {
      super("Item Physics", "Adds ground physics to dropped items.", ModuleCategory.VISUAL);
      instance = this;
   }

   public static ItemPhysics getInstance() {
      return instance;
   }
}

package releon.ru.api.module.impl.movement;

import net.minecraft.util.math.Box;
import releon.ru.api.events.annotation.SubscribeEvent;
import releon.ru.api.events.impl.InputEvent;
import releon.ru.api.module.Module;
import releon.ru.api.module.ModuleCategory;

public final class AirJump extends Module {
   public AirJump() {
      super("AirJump", "", ModuleCategory.MOVEMENT);
   }

   @SubscribeEvent
   private void onMovePost(InputEvent event) {
      if (mc.player != null && mc.world != null && event.getInput().jump()) {
         Box box = mc.player.getBoundingBox().offset(0.0, -1.0, 0.0).expand(0.1, 0.0, 0.1);
         boolean ХАЧЕБАНЫЙ = !mc.world.isSpaceEmpty(mc.player, box);
         if (ХАЧЕБАНЫЙ && mc.player.age % 2 == 0) {
            mc.player.fallDistance = 0.0;
            mc.player.jump();
         }
      }
   }
}

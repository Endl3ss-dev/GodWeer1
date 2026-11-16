package releon.ru.api.module.impl.movement;

import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import releon.ru.api.module.Module;
import releon.ru.api.module.ModuleCategory;
import releon.ru.api.settings.impl.ModeSetting;
import releon.ru.utils.inventory.interaction.PlayerInteractionHelper;
import releon.ru.utils.move.MoveUtil;

public final class NoWeb extends Module {
   private final ModeSetting webMode = this.register(new ModeSetting("Mode", "Cobweb bypass mode.", "Grim", "Grim"));

   public NoWeb() {
      super("No Web", "Reduces cobweb slowdown.", ModuleCategory.MOVEMENT);
   }

   @Override
   public void onTick(MinecraftClient client) {
      if (client.player != null && client.world != null) {
         if (this.webMode.is("Grim") && PlayerInteractionHelper.isPlayerInBlock(Blocks.COBWEB)) {
            double[] speed = MoveUtil.calculateDirection(0.35);
            client.player.addVelocity(speed[0], 0.0, speed[1]);
            client.player.setVelocity(speed[0], client.options.jumpKey.isPressed() ? 0.65 : (client.options.sneakKey.isPressed() ? -0.65 : 0.0), speed[1]);
         }
      }
   }
}

package releon.ru.api.module.impl.player;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import releon.ru.api.events.annotation.SubscribeEvent;
import releon.ru.api.events.impl.BlockBreakingEvent;
import releon.ru.api.module.Module;
import releon.ru.api.module.ModuleCategory;
import releon.ru.api.settings.impl.NumberSetting;
import releon.ru.mixin.accessor.MultiPlayerGameModeAccessor;

public final class FastBreak extends Module {
   private final NumberSetting multiplier = this.register(new NumberSetting("Multiplier", "Block breaking speed multiplier.", 1.5, 1.0, 3.0, 0.05));

   public FastBreak() {
      super("Fast Break", "Speeds up block breaking.", ModuleCategory.PLAYER);
   }

   @SubscribeEvent
   private void onBlockBreaking(BlockBreakingEvent event) {
      MinecraftClient client = MinecraftClient.getInstance();
      if (client.interactionManager != null && client.player != null && client.world != null) {
         float multiplierValue = this.multiplier.getFloat();
         if (!(multiplierValue <= 1.0F)) {
            BlockPos blockPos = event.blockPos();
            BlockState state = client.world.getBlockState(blockPos);
            if (!state.isAir()) {
               MultiPlayerGameModeAccessor controller = (MultiPlayerGameModeAccessor)client.interactionManager;
               controller.releon$setDestroyDelay(0);
               float delta = state.calcBlockBreakingDelta(client.player, client.player.getEntityWorld(), blockPos);
               float extraProgress = delta * (multiplierValue - 1.0F);
               float breakingProgress = controller.releon$getDestroyProgress() + extraProgress;
               controller.releon$setDestroyProgress(Math.min(1.0F, breakingProgress));
            }
         }
      }
   }
}

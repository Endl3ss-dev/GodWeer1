// deobf by pusia
package su.catlean.client.features.modules.player;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;

public final class MultiTask extends Module {
   @NotNull
   public static final MultiTask INSTANCE = new MultiTask();

   private MultiTask() {
      super("multi-task", SubCategories.getSubCategory10(), null, 4, null);
   }

   @Flow
   public final void onPlayerUpdate(PlayerUpdateEvent e) {
      if (MinecraftHolder.getMinecraft().hitResult instanceof BlockHitResult) {
         BlockHitResult hitResult = (BlockHitResult)MinecraftHolder.getMinecraft().hitResult;
         if (MinecraftHolder.getMinecraft().options.keyAttack.isDown()
            && MinecraftHolder.getMinecraft().options.keyUse.isDown()
            && !MinecraftHolder.getClientLevel().getBlockState(hitResult.getBlockPos()).isAir()) {
            if (MinecraftHolder.getMultiPlayerGameMode().isDestroying()) {
               MinecraftHolder.getMultiPlayerGameMode().continueDestroyBlock(hitResult.getBlockPos(), hitResult.getDirection());
            } else {
               MinecraftHolder.getMultiPlayerGameMode().startDestroyBlock(hitResult.getBlockPos(), hitResult.getDirection());
            }

            MinecraftHolder.getLocalPlayer().swing(InteractionHand.MAIN_HAND);
         }
      }

      if (MinecraftHolder.getMinecraft().hitResult instanceof EntityHitResult) {
         EntityHitResult hitResult2 = (EntityHitResult)MinecraftHolder.getMinecraft().hitResult;
         if (MinecraftHolder.getMinecraft().options.keyAttack.isDown()
            && MinecraftHolder.getMinecraft().options.keyUse.isDown()
            && MinecraftHolder.getLocalPlayer().getAttackStrengthScale(0.5F) > 0.9F) {
            MinecraftHolder.getMultiPlayerGameMode().attack((Player)MinecraftHolder.getLocalPlayer(), hitResult2.getEntity());
            MinecraftHolder.getLocalPlayer().swing(InteractionHand.MAIN_HAND);
         }
      }
   }
}
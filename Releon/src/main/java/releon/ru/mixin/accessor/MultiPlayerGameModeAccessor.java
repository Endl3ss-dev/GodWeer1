package releon.ru.mixin.accessor;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({ClientPlayerInteractionManager.class})
public interface MultiPlayerGameModeAccessor {
   @Accessor("breakingBlock")
   void releon$setDestroying(boolean var1);

   @Accessor("breakingBlock")
   boolean releon$isDestroying();

   @Accessor("currentBreakingPos")
   BlockPos releon$getDestroyBlockPos();

   @Invoker("syncSelectedSlot")
   void releon$ensureHasSentCarriedItem();

   @Accessor("blockBreakingCooldown")
   void releon$setDestroyDelay(int var1);

   @Accessor("currentBreakingProgress")
   float releon$getDestroyProgress();

   @Accessor("currentBreakingProgress")
   void releon$setDestroyProgress(float var1);
}

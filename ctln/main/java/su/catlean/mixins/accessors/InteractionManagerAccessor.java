// deobf by pusia
package su.catlean.mixins.accessors;

import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({MultiPlayerGameMode.class})
public interface InteractionManagerAccessor {

   @Invoker("ensureHasSentCarriedItem")
   void syncSlot();

   @Accessor("destroyProgress")
   float getCurBlockDamageMP();

   @Accessor("destroyProgress")
   void setCurBlockDamageMP(float value);

   @Accessor("carriedIndex")
   void setCarriedIndex(int value);
}
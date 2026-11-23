// deobf by pusia
package su.catlean.mixins.accessors;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({Minecraft.class})
public interface MinecraftAccessor {

   @Accessor("rightClickDelay")
   int getUseCooldown();

   @Accessor("rightClickDelay")
   void setUseCooldown(int value);

   @Invoker("startUseItem")
   void idoItemUse();

   @Invoker("startAttack")
   boolean idoAttack();
}
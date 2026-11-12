package net.skycore.client.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Minecraft.class)
public interface MinecraftGetRightClickDelayAccessor {
   @Invoker("startAttack")
   boolean skycore$startAttack();

   @Accessor("rightClickDelay")
   int skycore$getRightClickDelay();

   @Accessor("rightClickDelay")
   void skycore$setRightClickDelay(int value);

   @Accessor("missTime")
   int skycore$getMissTime();

   @Accessor("missTime")
   void skycore$setMissTime(int value);

   @Mutable
   @Accessor("user")
   void skycore$setUser(User user);
}

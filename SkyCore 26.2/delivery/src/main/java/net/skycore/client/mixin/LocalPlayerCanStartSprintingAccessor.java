package net.skycore.client.mixin;

import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

 
@Mixin(LocalPlayer.class)
public interface LocalPlayerCanStartSprintingAccessor {
   @Invoker("canStartSprinting")
   boolean skycore$canStartSprinting();
}

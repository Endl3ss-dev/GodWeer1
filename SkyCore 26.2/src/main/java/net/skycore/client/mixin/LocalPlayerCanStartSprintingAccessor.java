package net.skycore.client.mixin;

import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** Swerve: приватный ванильный шлюз старта спринта. */
@Mixin(LocalPlayer.class)
public interface LocalPlayerCanStartSprintingAccessor {
   @Invoker("canStartSprinting")
   boolean skycore$canStartSprinting();
}

package dev.swap;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.PlayerInput;

public final class MovementStop {
	private static final MinecraftClient MC = MinecraftClient.getInstance();

	private MovementStop() {
	}

	public static void force() {
		if (MC.player == null || MC.options == null) {
			return;
		}
		SwapUtil.getMovementKeys().forEach(key -> key.setPressed(false));
		MC.player.setSprinting(false);
		if (MC.player.input != null) {
			MC.player.input.playerInput = new PlayerInput(false, false, false, false, false, false, false);
		}
		var velocity = MC.player.getVelocity();
		MC.player.setVelocity(0.0D, velocity.y, 0.0D);
	}
}

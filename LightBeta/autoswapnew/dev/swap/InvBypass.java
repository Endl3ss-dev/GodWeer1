package dev.swap;

import dev.config.ConfigManager;
import dev.config.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import java.util.List;

public final class InvBypass {
	private static final MinecraftClient MC = MinecraftClient.getInstance();

	private InvBypass() {
	}

	public static void apply(List<KeyBinding> movementKeys) {
		ModConfig config = ConfigManager.get();
		if (config.invBypassMode == InvBypassMode.HOLY_WORLD) {
			MC.options.sprintKey.setPressed(false);
			if (MC.player != null) {
				MC.player.setSprinting(false);
			}
		} else {
			movementKeys.forEach(key -> key.setPressed(false));
		}
	}

	public static void restore(List<KeyBinding> movementKeys) {
		if (MC.getWindow() == null || MC.options == null) {
			return;
		}

		ModConfig config = ConfigManager.get();

		if (config.invBypassMode == InvBypassMode.HOLY_WORLD) {
			int code = MC.options.sprintKey.getDefaultKey().getCode();
			MC.options.sprintKey.setPressed(InputUtil.isKeyPressed(MC.getWindow(), code));
		} else {
			for (KeyBinding key : movementKeys) {
				int code = key.getDefaultKey().getCode();
				key.setPressed(InputUtil.isKeyPressed(MC.getWindow(), code));
			}
		}
	}
}

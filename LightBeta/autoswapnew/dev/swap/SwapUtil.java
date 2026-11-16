package dev.swap;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public final class SwapUtil {
	private static final MinecraftClient MC = MinecraftClient.getInstance();

	private SwapUtil() {
	}

	public static void openInventory() {
		if (MC.player != null && MC.player.networkHandler != null) {
			MC.player.networkHandler.sendPacket(new ClientCommandC2SPacket(MC.player, ClientCommandC2SPacket.Mode.OPEN_INVENTORY));
		}
	}

	public static void closeInventory() {
		if (MC.player != null && MC.player.networkHandler != null && MC.player.currentScreenHandler != null) {
			MC.player.networkHandler.sendPacket(new CloseHandledScreenC2SPacket(MC.player.currentScreenHandler.syncId));
		}
	}

	public static void performSwap(int slot, int destination) {
		if (MC.interactionManager == null || MC.player == null) {
			return;
		}
		int containerSlot = slot < 9 ? slot + 36 : slot;
		MC.interactionManager.clickSlot(
				MC.player.currentScreenHandler.syncId,
				containerSlot,
				destination,
				SlotActionType.SWAP,
				MC.player
		);
	}

	public static List<KeyBinding> getMovementKeys() {
		return List.of(
				MC.options.forwardKey,
				MC.options.backKey,
				MC.options.leftKey,
				MC.options.rightKey,
				MC.options.jumpKey,
				MC.options.sprintKey
		);
	}

	public static boolean isKeyPressed(int keyCode) {
		if (keyCode == GLFW.GLFW_KEY_UNKNOWN || keyCode < 0) {
			return false;
		}
		long window = MC.getWindow().getHandle();
		if (keyCode >= GLFW.GLFW_MOUSE_BUTTON_1 && keyCode <= GLFW.GLFW_MOUSE_BUTTON_8) {
			return GLFW.glfwGetMouseButton(window, keyCode) == GLFW.GLFW_PRESS;
		}
		return GLFW.glfwGetKey(window, keyCode) == GLFW.GLFW_PRESS;
	}

	public static InputUtil.Key toKey(int keyCode) {
		if (keyCode < 0) {
			return InputUtil.UNKNOWN_KEY;
		}
		if (keyCode >= GLFW.GLFW_MOUSE_BUTTON_1 && keyCode <= GLFW.GLFW_MOUSE_BUTTON_8) {
			return InputUtil.Type.MOUSE.createFromCode(keyCode);
		}
		return InputUtil.Type.KEYSYM.createFromCode(keyCode);
	}

	public static int fromKey(InputUtil.Key key) {
		if (key == null || key == InputUtil.UNKNOWN_KEY) {
			return -1;
		}
		return key.getCode();
	}
}

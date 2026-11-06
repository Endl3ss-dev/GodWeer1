package net.skycore.client.gui.decide;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.skycore.client.module.render.ClickGUIModule;
import org.lwjgl.glfw.GLFW;

public final class DecideGuiMovement {
    private static boolean active;
    private static boolean jumpWasDown;

    private DecideGuiMovement() {
    }

    public static void tick(Minecraft mc, ClickGUIModule clickGui) {
        if (mc == null || mc.player == null || mc.getWindow() == null || mc.options == null) {
            release(mc);
            return;
        }
        boolean allowed = clickGui != null
                && clickGui.menuStyle().is("Decide")
                && clickGui.runWithGui().get()
                && mc.gui.screen() instanceof DecideMenuScreen screen
                && !screen.isCapturingTextInput();
        if (!allowed) {
            release(mc);
            return;
        }
        active = true;
        Window window = mc.getWindow();
        long handle = window.handle();
        boolean forward = down(handle, GLFW.GLFW_KEY_W);
        boolean back = down(handle, GLFW.GLFW_KEY_S);
        boolean left = down(handle, GLFW.GLFW_KEY_A);
        boolean right = down(handle, GLFW.GLFW_KEY_D);
        boolean jump = down(handle, GLFW.GLFW_KEY_SPACE);
        boolean sneak = down(handle, GLFW.GLFW_KEY_LEFT_SHIFT) || down(handle, GLFW.GLFW_KEY_RIGHT_SHIFT);
        boolean sprint = down(handle, GLFW.GLFW_KEY_LEFT_CONTROL) || down(handle, GLFW.GLFW_KEY_RIGHT_CONTROL);

        mc.options.keyUp.setDown(forward);
        mc.options.keyDown.setDown(back);
        mc.options.keyLeft.setDown(left);
        mc.options.keyRight.setDown(right);
        mc.options.keyJump.setDown(jump);
        mc.options.keyShift.setDown(sneak);
        mc.options.keySprint.setDown(sprint || (forward && !jumpWasDown));
        jumpWasDown = jump;
    }

    private static boolean down(long handle, int key) {
        return GLFW.glfwGetKey(handle, key) == GLFW.GLFW_PRESS;
    }

    public static void release(Minecraft mc) {
        if (!active || mc == null) {
            return;
        }
        active = false;
        jumpWasDown = false;
        if (mc.options == null) {
            return;
        }
        mc.options.keyUp.setDown(false);
        mc.options.keyDown.setDown(false);
        mc.options.keyLeft.setDown(false);
        mc.options.keyRight.setDown(false);
        mc.options.keyJump.setDown(false);
        mc.options.keyShift.setDown(false);
        mc.options.keySprint.setDown(false);
    }
}

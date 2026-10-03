package godweer.ui.newgui;

import net.minecraft.client.Minecraft;

import org.lwjgl.glfw.GLFW;

// Имена клавиш для отображения биндов — 1:1 с исходным клиентом.
public final class GuiKeys {

    private GuiKeys() {
    }

    public static boolean isControlDown() {
        long window = Minecraft.getInstance().getWindow().getWindow();
        return GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_CONTROL) == GLFW.GLFW_PRESS
                || GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_CONTROL) == GLFW.GLFW_PRESS;
    }

    public static String keyName(int keyCode) {
        if (keyCode >= 1000) {
            return "M" + (keyCode - 1000 + 1);
        }

        switch (keyCode) {
            case GLFW.GLFW_KEY_SPACE: return "SPACE";
            case GLFW.GLFW_KEY_APOSTROPHE: return "APOSTROPHE";
            case GLFW.GLFW_KEY_COMMA: return "COMMA";
            case GLFW.GLFW_KEY_MINUS: return "MINUS";
            case GLFW.GLFW_KEY_PERIOD: return "PERIOD";
            case GLFW.GLFW_KEY_SLASH: return "SLASH";
            case GLFW.GLFW_KEY_SEMICOLON: return "SEMICOLON";
            case GLFW.GLFW_KEY_EQUAL: return "EQUAL";
            case GLFW.GLFW_KEY_LEFT_BRACKET: return "LBRACKET";
            case GLFW.GLFW_KEY_RIGHT_BRACKET: return "RBRACKET";
            case GLFW.GLFW_KEY_BACKSLASH: return "BACKSLASH";
            case GLFW.GLFW_KEY_GRAVE_ACCENT: return "GRAVE";
            case GLFW.GLFW_KEY_ESCAPE: return "ESCAPE";
            case GLFW.GLFW_KEY_ENTER: return "ENTER";
            case GLFW.GLFW_KEY_TAB: return "TAB";
            case GLFW.GLFW_KEY_BACKSPACE: return "BACKSPACE";
            case GLFW.GLFW_KEY_INSERT: return "INSERT";
            case GLFW.GLFW_KEY_DELETE: return "DELETE";
            case GLFW.GLFW_KEY_RIGHT: return "RIGHT";
            case GLFW.GLFW_KEY_LEFT: return "LEFT";
            case GLFW.GLFW_KEY_DOWN: return "DOWN";
            case GLFW.GLFW_KEY_UP: return "UP";
            case GLFW.GLFW_KEY_PAGE_UP: return "PAGEUP";
            case GLFW.GLFW_KEY_PAGE_DOWN: return "PAGEDOWN";
            case GLFW.GLFW_KEY_HOME: return "HOME";
            case GLFW.GLFW_KEY_END: return "END";
            case GLFW.GLFW_KEY_CAPS_LOCK: return "CAPSLOCK";
            case GLFW.GLFW_KEY_SCROLL_LOCK: return "SCROLLLOCK";
            case GLFW.GLFW_KEY_NUM_LOCK: return "NUMLOCK";
            case GLFW.GLFW_KEY_PRINT_SCREEN: return "PRINTSCREEN";
            case GLFW.GLFW_KEY_PAUSE: return "PAUSE";
            case GLFW.GLFW_KEY_LEFT_SHIFT: return "LSHIFT";
            case GLFW.GLFW_KEY_LEFT_CONTROL: return "LCONTROL";
            case GLFW.GLFW_KEY_LEFT_ALT: return "LMENU";
            case GLFW.GLFW_KEY_LEFT_SUPER: return "LSUPER";
            case GLFW.GLFW_KEY_RIGHT_SHIFT: return "RSHIFT";
            case GLFW.GLFW_KEY_RIGHT_CONTROL: return "RCONTROL";
            case GLFW.GLFW_KEY_RIGHT_ALT: return "RMENU";
            case GLFW.GLFW_KEY_RIGHT_SUPER: return "RSUPER";
            default: break;
        }

        if (keyCode >= GLFW.GLFW_KEY_F1 && keyCode <= GLFW.GLFW_KEY_F12) {
            return "F" + (keyCode - GLFW.GLFW_KEY_F1 + 1);
        }

        if (keyCode >= GLFW.GLFW_KEY_KP_0 && keyCode <= GLFW.GLFW_KEY_KP_9) {
            return "KP" + (keyCode - GLFW.GLFW_KEY_KP_0);
        }

        // Буквы A-Z и цифры 0-9 в GLFW идут подряд, имя совпадает с символом.
        if ((keyCode >= 'A' && keyCode <= 'Z') || (keyCode >= '0' && keyCode <= '9')) {
            return String.valueOf((char) keyCode);
        }

        return "KEY" + keyCode;
    }
}

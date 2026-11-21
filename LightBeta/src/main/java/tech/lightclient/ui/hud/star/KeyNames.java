package tech.lightclient.ui.hud.star;

import org.lwjgl.glfw.GLFW;

public final class KeyNames {
    private KeyNames() {
    }

    public static String get(int keyCode) {
        if (keyCode <= 0 || keyCode == GLFW.GLFW_KEY_UNKNOWN) {
            return "";
        }
        // EventKey stores mouse buttons as 1000 + GLFW button. Keep this in
        // sync with BindSetting, otherwise ServerAssistant binds are shown as
        // opaque values such as KEY1001.
        if (keyCode >= 1000 && keyCode <= 1000 + GLFW.GLFW_MOUSE_BUTTON_LAST) {
            return switch (keyCode - 1000) {
                case GLFW.GLFW_MOUSE_BUTTON_LEFT -> "LMB";
                case GLFW.GLFW_MOUSE_BUTTON_RIGHT -> "RMB";
                case GLFW.GLFW_MOUSE_BUTTON_MIDDLE -> "MMB";
                case GLFW.GLFW_MOUSE_BUTTON_4 -> "M4";
                case GLFW.GLFW_MOUSE_BUTTON_5 -> "M5";
                case GLFW.GLFW_MOUSE_BUTTON_6 -> "M6";
                case GLFW.GLFW_MOUSE_BUTTON_7 -> "M7";
                case GLFW.GLFW_MOUSE_BUTTON_8 -> "M8";
                default -> "M" + (keyCode - 999);
            };
        }
        if (keyCode > GLFW.GLFW_MOUSE_BUTTON_LEFT && keyCode <= GLFW.GLFW_MOUSE_BUTTON_LAST) {
            return switch (keyCode) {
                case GLFW.GLFW_MOUSE_BUTTON_RIGHT -> "RMB";
                case GLFW.GLFW_MOUSE_BUTTON_MIDDLE -> "MMB";
                case GLFW.GLFW_MOUSE_BUTTON_4 -> "M4";
                case GLFW.GLFW_MOUSE_BUTTON_5 -> "M5";
                case GLFW.GLFW_MOUSE_BUTTON_6 -> "M6";
                case GLFW.GLFW_MOUSE_BUTTON_7 -> "M7";
                case GLFW.GLFW_MOUSE_BUTTON_8 -> "M8";
                default -> "M" + (keyCode + 1);
            };
        }
        if (keyCode >= GLFW.GLFW_KEY_A && keyCode <= GLFW.GLFW_KEY_Z) {
            return String.valueOf((char) ('A' + (keyCode - GLFW.GLFW_KEY_A)));
        }
        if (keyCode >= GLFW.GLFW_KEY_0 && keyCode <= GLFW.GLFW_KEY_9) {
            return String.valueOf((char) ('0' + (keyCode - GLFW.GLFW_KEY_0)));
        }
        if (keyCode >= GLFW.GLFW_KEY_F1 && keyCode <= GLFW.GLFW_KEY_F12) {
            return "F" + (keyCode - GLFW.GLFW_KEY_F1 + 1);
        }
        if (keyCode >= GLFW.GLFW_KEY_KP_0 && keyCode <= GLFW.GLFW_KEY_KP_9) {
            return "KP" + (keyCode - GLFW.GLFW_KEY_KP_0);
        }
        return switch (keyCode) {
            case GLFW.GLFW_KEY_LEFT_SHIFT, GLFW.GLFW_KEY_RIGHT_SHIFT -> "SHIFT";
            case GLFW.GLFW_KEY_LEFT_CONTROL, GLFW.GLFW_KEY_RIGHT_CONTROL -> "CTRL";
            case GLFW.GLFW_KEY_LEFT_ALT, GLFW.GLFW_KEY_RIGHT_ALT -> "ALT";
            case GLFW.GLFW_KEY_SPACE -> "SPACE";
            case GLFW.GLFW_KEY_TAB -> "TAB";
            case GLFW.GLFW_KEY_CAPS_LOCK -> "CAPS";
            case GLFW.GLFW_KEY_LEFT -> "<-";
            case GLFW.GLFW_KEY_RIGHT -> "->";
            case GLFW.GLFW_KEY_UP -> "^";
            case GLFW.GLFW_KEY_DOWN -> "v";
            case GLFW.GLFW_KEY_MINUS -> "-";
            case GLFW.GLFW_KEY_EQUAL -> "=";
            case GLFW.GLFW_KEY_LEFT_BRACKET -> "[";
            case GLFW.GLFW_KEY_RIGHT_BRACKET -> "]";
            case GLFW.GLFW_KEY_SEMICOLON -> ";";
            case GLFW.GLFW_KEY_APOSTROPHE -> "'";
            case GLFW.GLFW_KEY_GRAVE_ACCENT -> "~";
            case GLFW.GLFW_KEY_BACKSLASH -> "\\";
            case GLFW.GLFW_KEY_COMMA -> ",";
            case GLFW.GLFW_KEY_PERIOD -> ".";
            case GLFW.GLFW_KEY_SLASH -> "/";
            case GLFW.GLFW_KEY_INSERT -> "INS";
            case GLFW.GLFW_KEY_DELETE -> "DEL";
            case GLFW.GLFW_KEY_HOME -> "HOME";
            case GLFW.GLFW_KEY_END -> "END";
            case GLFW.GLFW_KEY_PAGE_UP -> "PGUP";
            case GLFW.GLFW_KEY_PAGE_DOWN -> "PGDN";
            default -> "KEY" + keyCode;
        };
    }
}

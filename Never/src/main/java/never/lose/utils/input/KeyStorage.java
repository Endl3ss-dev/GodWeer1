package never.lose.utils.input;

import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;

public final class KeyStorage {

    public static final Map<String, Integer> KEY_MAP = new HashMap<>();
    private static final Map<Integer, String> REVERSE_KEY_MAP = new HashMap<>();

    private KeyStorage() {}

    public static String getKey(int integer) {
        return REVERSE_KEY_MAP.getOrDefault(integer, "");
    }

    public static Integer getKey(String key) {
        return KEY_MAP.getOrDefault(key, -1);
    }

    static {
        for (char c = 'A'; c <= 'Z'; c++) {
            KEY_MAP.put(String.valueOf(c), GLFW.GLFW_KEY_A + (c - 'A'));
        }
        for (char c = '0'; c <= '9'; c++) {
            KEY_MAP.put(String.valueOf(c), GLFW.GLFW_KEY_0 + (c - '0'));
        }
        for (int i = 1; i <= 12; i++) {
            KEY_MAP.put("F" + i, GLFW.GLFW_KEY_F1 + (i - 1));
        }

        KEY_MAP.put("MOUSE1", 0);
        KEY_MAP.put("MOUSE2", 1);
        KEY_MAP.put("MOUSE3", 2);
        KEY_MAP.put("MOUSE4", 3);
        KEY_MAP.put("MOUSE5", 4);
        KEY_MAP.put("NUMPAD1", GLFW.GLFW_KEY_KP_1);
        KEY_MAP.put("NUMPAD2", GLFW.GLFW_KEY_KP_2);
        KEY_MAP.put("NUMPAD3", GLFW.GLFW_KEY_KP_3);
        KEY_MAP.put("NUMPAD4", GLFW.GLFW_KEY_KP_4);
        KEY_MAP.put("NUMPAD5", GLFW.GLFW_KEY_KP_5);
        KEY_MAP.put("NUMPAD6", GLFW.GLFW_KEY_KP_6);
        KEY_MAP.put("NUMPAD7", GLFW.GLFW_KEY_KP_7);
        KEY_MAP.put("NUMPAD8", GLFW.GLFW_KEY_KP_8);
        KEY_MAP.put("NUMPAD9", GLFW.GLFW_KEY_KP_9);
        KEY_MAP.put("NUMPAD_DECIMAL", GLFW.GLFW_KEY_KP_DECIMAL);
        KEY_MAP.put("NUMPAD_DIVIDE", GLFW.GLFW_KEY_KP_DIVIDE);
        KEY_MAP.put("NUMPAD_MULTIPLY", GLFW.GLFW_KEY_KP_MULTIPLY);
        KEY_MAP.put("NUMPAD_SUBTRACT", GLFW.GLFW_KEY_KP_SUBTRACT);
        KEY_MAP.put("NUMPAD_ADD", GLFW.GLFW_KEY_KP_ADD);
        KEY_MAP.put("NUMPAD_ENTER", GLFW.GLFW_KEY_KP_ENTER);
        KEY_MAP.put("NUMPAD_EQUAL", GLFW.GLFW_KEY_KP_EQUAL);
        KEY_MAP.put("TAB", GLFW.GLFW_KEY_TAB);
        KEY_MAP.put("NUMPAD0", GLFW.GLFW_KEY_KP_0);
        KEY_MAP.put("SPACE", GLFW.GLFW_KEY_SPACE);
        KEY_MAP.put("ENTER", GLFW.GLFW_KEY_ENTER);
        KEY_MAP.put("ESCAPE", GLFW.GLFW_KEY_ESCAPE);
        KEY_MAP.put("HOME", GLFW.GLFW_KEY_HOME);
        KEY_MAP.put("INSERT", GLFW.GLFW_KEY_INSERT);
        KEY_MAP.put("DELETE", GLFW.GLFW_KEY_DELETE);
        KEY_MAP.put("END", GLFW.GLFW_KEY_END);
        KEY_MAP.put("PAGEUP", GLFW.GLFW_KEY_PAGE_UP);
        KEY_MAP.put("PAGEDOWN", GLFW.GLFW_KEY_PAGE_DOWN);
        KEY_MAP.put("RIGHT", GLFW.GLFW_KEY_RIGHT);
        KEY_MAP.put("LEFT", GLFW.GLFW_KEY_LEFT);
        KEY_MAP.put("DOWN", GLFW.GLFW_KEY_DOWN);
        KEY_MAP.put("UP", GLFW.GLFW_KEY_UP);
        KEY_MAP.put("RSHIFT", GLFW.GLFW_KEY_RIGHT_SHIFT);
        KEY_MAP.put("LSHIFT", GLFW.GLFW_KEY_LEFT_SHIFT);
        KEY_MAP.put("RCTRL", GLFW.GLFW_KEY_RIGHT_CONTROL);
        KEY_MAP.put("LCTRL", GLFW.GLFW_KEY_LEFT_CONTROL);
        KEY_MAP.put("RALT", GLFW.GLFW_KEY_RIGHT_ALT);
        KEY_MAP.put("LALT", GLFW.GLFW_KEY_LEFT_ALT);
        KEY_MAP.put("RSUPER", GLFW.GLFW_KEY_RIGHT_SUPER);
        KEY_MAP.put("LSUPER", GLFW.GLFW_KEY_LEFT_SUPER);
        KEY_MAP.put("MENU", GLFW.GLFW_KEY_MENU);
        KEY_MAP.put("CAPS_LOCK", GLFW.GLFW_KEY_CAPS_LOCK);
        KEY_MAP.put("NUM_LOCK", GLFW.GLFW_KEY_NUM_LOCK);
        KEY_MAP.put("SCROLL_LOCK", GLFW.GLFW_KEY_SCROLL_LOCK);
        KEY_MAP.put("PRINT_SCREEN", GLFW.GLFW_KEY_PRINT_SCREEN);
        KEY_MAP.put("APOSTROPHE", GLFW.GLFW_KEY_APOSTROPHE);
        KEY_MAP.put("SLASH", GLFW.GLFW_KEY_SLASH);
        KEY_MAP.put("MINUS", GLFW.GLFW_KEY_MINUS);
        KEY_MAP.put("EQUAL", GLFW.GLFW_KEY_EQUAL);
        KEY_MAP.put("BACKSPACE", GLFW.GLFW_KEY_BACKSPACE);
        KEY_MAP.put("BACKSLASH", GLFW.GLFW_KEY_BACKSLASH);
        KEY_MAP.put("PERIOD", GLFW.GLFW_KEY_PERIOD);
        KEY_MAP.put("COMMA", GLFW.GLFW_KEY_COMMA);
        KEY_MAP.put("PAUSE", GLFW.GLFW_KEY_PAUSE);
        KEY_MAP.put("GRAVE", GLFW.GLFW_KEY_GRAVE_ACCENT);

        for (Map.Entry<String, Integer> entry : KEY_MAP.entrySet()) {
            REVERSE_KEY_MAP.put(entry.getValue(), entry.getKey());
        }
    }
}

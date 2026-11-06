package tech.lightclient.scripting.api;

import org.lwjgl.glfw.GLFW;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Named key constants for {@code key(Key.G)} and {@code hotkey("...", Key.G)}.
 * Values are GLFW key codes so they slot straight into the client's existing bind handling.
 */
public final class Key {

    public static final int UNKNOWN = -1;

    public static final int A = GLFW.GLFW_KEY_A;
    public static final int B = GLFW.GLFW_KEY_B;
    public static final int C = GLFW.GLFW_KEY_C;
    public static final int D = GLFW.GLFW_KEY_D;
    public static final int E = GLFW.GLFW_KEY_E;
    public static final int F = GLFW.GLFW_KEY_F;
    public static final int G = GLFW.GLFW_KEY_G;
    public static final int H = GLFW.GLFW_KEY_H;
    public static final int I = GLFW.GLFW_KEY_I;
    public static final int J = GLFW.GLFW_KEY_J;
    public static final int K = GLFW.GLFW_KEY_K;
    public static final int L = GLFW.GLFW_KEY_L;
    public static final int M = GLFW.GLFW_KEY_M;
    public static final int N = GLFW.GLFW_KEY_N;
    public static final int O = GLFW.GLFW_KEY_O;
    public static final int P = GLFW.GLFW_KEY_P;
    public static final int Q = GLFW.GLFW_KEY_Q;
    public static final int R = GLFW.GLFW_KEY_R;
    public static final int S = GLFW.GLFW_KEY_S;
    public static final int T = GLFW.GLFW_KEY_T;
    public static final int U = GLFW.GLFW_KEY_U;
    public static final int V = GLFW.GLFW_KEY_V;
    public static final int W = GLFW.GLFW_KEY_W;
    public static final int X = GLFW.GLFW_KEY_X;
    public static final int Y = GLFW.GLFW_KEY_Y;
    public static final int Z = GLFW.GLFW_KEY_Z;

    public static final int NUM_0 = GLFW.GLFW_KEY_0;
    public static final int NUM_1 = GLFW.GLFW_KEY_1;
    public static final int NUM_2 = GLFW.GLFW_KEY_2;
    public static final int NUM_3 = GLFW.GLFW_KEY_3;
    public static final int NUM_4 = GLFW.GLFW_KEY_4;
    public static final int NUM_5 = GLFW.GLFW_KEY_5;
    public static final int NUM_6 = GLFW.GLFW_KEY_6;
    public static final int NUM_7 = GLFW.GLFW_KEY_7;
    public static final int NUM_8 = GLFW.GLFW_KEY_8;
    public static final int NUM_9 = GLFW.GLFW_KEY_9;

    public static final int F1 = GLFW.GLFW_KEY_F1;
    public static final int F2 = GLFW.GLFW_KEY_F2;
    public static final int F3 = GLFW.GLFW_KEY_F3;
    public static final int F4 = GLFW.GLFW_KEY_F4;
    public static final int F5 = GLFW.GLFW_KEY_F5;
    public static final int F6 = GLFW.GLFW_KEY_F6;
    public static final int F7 = GLFW.GLFW_KEY_F7;
    public static final int F8 = GLFW.GLFW_KEY_F8;
    public static final int F9 = GLFW.GLFW_KEY_F9;
    public static final int F10 = GLFW.GLFW_KEY_F10;
    public static final int F11 = GLFW.GLFW_KEY_F11;
    public static final int F12 = GLFW.GLFW_KEY_F12;

    public static final int LEFT_SHIFT = GLFW.GLFW_KEY_LEFT_SHIFT;
    public static final int RIGHT_SHIFT = GLFW.GLFW_KEY_RIGHT_SHIFT;
    public static final int LEFT_CONTROL = GLFW.GLFW_KEY_LEFT_CONTROL;
    public static final int RIGHT_CONTROL = GLFW.GLFW_KEY_RIGHT_CONTROL;
    public static final int LEFT_ALT = GLFW.GLFW_KEY_LEFT_ALT;
    public static final int RIGHT_ALT = GLFW.GLFW_KEY_RIGHT_ALT;
    public static final int SPACE = GLFW.GLFW_KEY_SPACE;
    public static final int ENTER = GLFW.GLFW_KEY_ENTER;
    public static final int TAB = GLFW.GLFW_KEY_TAB;
    public static final int ESCAPE = GLFW.GLFW_KEY_ESCAPE;
    public static final int BACKSPACE = GLFW.GLFW_KEY_BACKSPACE;
    public static final int DELETE = GLFW.GLFW_KEY_DELETE;
    public static final int UP = GLFW.GLFW_KEY_UP;
    public static final int DOWN = GLFW.GLFW_KEY_DOWN;
    public static final int LEFT = GLFW.GLFW_KEY_LEFT;
    public static final int RIGHT = GLFW.GLFW_KEY_RIGHT;

    /** Mouse buttons use the client's existing 1000+button encoding. */
    public static final int MOUSE_LEFT = 1000;
    public static final int MOUSE_RIGHT = 1001;
    public static final int MOUSE_MIDDLE = 1002;
    public static final int MOUSE_4 = 1003;
    public static final int MOUSE_5 = 1004;

    private static final Map<String, Integer> BY_NAME = new LinkedHashMap<>();

    static {
        for (java.lang.reflect.Field f : Key.class.getDeclaredFields()) {
            if (f.getType() == int.class && java.lang.reflect.Modifier.isStatic(f.getModifiers())) {
                try {
                    BY_NAME.put(f.getName().toUpperCase(), f.getInt(null));
                } catch (Throwable ignored) {
                }
            }
        }
    }

    /** Resolves a key by name, e.g. "G" or "LEFT_SHIFT". Returns {@link #UNKNOWN} if not found. */
    public static int byName(String name) {
        if (name == null) return UNKNOWN;
        Integer v = BY_NAME.get(name.trim().toUpperCase().replace(' ', '_'));
        return v == null ? UNKNOWN : v;
    }

    /** Human readable name for a key code, used by the bind UI and MCP. */
    public static String nameOf(int key) {
        if (key == UNKNOWN || key == 0) return "UNKNOWN";
        for (Map.Entry<String, Integer> e : BY_NAME.entrySet()) {
            if (e.getValue() == key && !e.getKey().equals("UNKNOWN")) return e.getKey();
        }
        if (key >= 1000) return "MOUSE_" + (key - 1000);
        String glfw = GLFW.glfwGetKeyName(key, 0);
        return glfw != null ? glfw.toUpperCase() : ("KEY_" + key);
    }

    private Key() {}
}

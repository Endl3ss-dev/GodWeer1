package net.skycore.client.gui.decide;

import com.mojang.blaze3d.platform.InputConstants;
import java.util.HashMap;
import java.util.Map;
import net.skycore.client.module.KeyBinds;

/**
 * Human readable key names for the ClickGUI.
 *
 * <p>Called from the render loop for every module row and every bind row, so the result is memoised:
 * {@link KeyBinds#name(int)} builds {@code "F1"} / {@code "M1"} strings on each call, and doing that
 * per frame would allocate on the hot path.
 */
public final class DecideKeyNames {
    private static final int DIRECT_LIMIT = 2048;
    private static final String NONE = "n/a";
    private static final String[] DIRECT = new String[DIRECT_LIMIT];
    private static final Map<Integer, String> EXTRA = new HashMap<>();

    private DecideKeyNames() {
    }

    public static String of(int key) {
        if (key <= 0) {
            return NONE;
        }
        if (key < DIRECT_LIMIT) {
            String cached = DIRECT[key];
            if (cached == null) {
                cached = resolve(key);
                DIRECT[key] = cached;
            }
            return cached;
        }
        String cached = EXTRA.get(key);
        if (cached == null) {
            cached = resolve(key);
            EXTRA.put(key, cached);
        }
        return cached;
    }

    private static String resolve(int key) {
        String known;
        try {
            known = KeyBinds.name(key);
        } catch (Throwable ignored) {
            known = null;
        }
        if (known != null && !known.isBlank() && !known.startsWith("Key")) {
            return known;
        }
        try {
            String name = InputConstants.Type.KEYSYM.getOrCreate(key).getDisplayName().getString();
            if (name == null || name.isBlank() || "NONE".equals(name)) {
                return NONE;
            }
            return name;
        } catch (Throwable ignored) {
            return NONE;
        }
    }
}

package net.skycore.client.gui.decide;

import com.mojang.blaze3d.platform.InputConstants;
import java.util.HashMap;
import java.util.Map;
import net.skycore.client.module.KeyBinds;

public final class DecideKeyNames {
    private static final Map<Integer, String> CACHE = new HashMap<>();

    private DecideKeyNames() {
    }

    public static String of(int key) {
        if (key <= 0) {
            return "n/a";
        }
        if (key < 512 && key != 0) {
            String known = KeyBinds.name(key);
            if (known != null && !known.startsWith("Key")) {
                return known;
            }
        }
        return CACHE.computeIfAbsent(key, k -> {
            try {
                String name = InputConstants.Type.KEYSYM.getOrCreate(k).getDisplayName().getString();
                if (name == null || name.isBlank() || "NONE".equals(name)) {
                    return "n/a";
                }
                return name;
            } catch (Throwable ignored) {
                return "n/a";
            }
        });
    }
}

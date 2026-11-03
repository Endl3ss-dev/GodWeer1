package net.skycore.client.gui.decide;

import net.skycore.client.util.ReadDataHelper;
import net.skycore.client.util.TextHeightHelper;

public final class DecideFonts {
    private static TextHeightHelper sfRegular;
    private static TextHeightHelper decide2;
    private static TextHeightHelper decideIcon;
    private static TextHeightHelper iconGui;

    private DecideFonts() {
    }

    public static TextHeightHelper sfRegular() {
        if (sfRegular == null) {
            sfRegular = load("sf_regular");
        }
        return sfRegular;
    }

    public static TextHeightHelper decide2() {
        if (decide2 == null) {
            decide2 = load("decide_2");
        }
        return decide2;
    }

    public static TextHeightHelper icon() {
        if (decideIcon == null) {
            decideIcon = load("decide");
        }
        return decideIcon;
    }

    public static TextHeightHelper guiIcon() {
        if (iconGui == null) {
            iconGui = load("icongui");
        }
        return iconGui;
    }

    /** Drops the cached font handles so a resource reload cannot leave stale atlases behind. */
    public static void reset() {
        sfRegular = null;
        decide2 = null;
        decideIcon = null;
        iconGui = null;
    }

    private static TextHeightHelper load(String name) {
        return new ReadDataHelper()
                .name("decide:" + name)
                .atlas(name)
                .data(name)
                .build();
    }
}

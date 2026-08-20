/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 */
package minced.core;

import minced.core.HudComponent;

public final class EventPriorities
implements HudComponent {
    @Override
    public void method2818(EventRender2D p0) { }

    static int ф;
    static int Е;
    static int в;
    static int Щ;
    static int д;

    static byte[] field9687;

    public static void method2835() {
        field9687 = new byte[]{0, 1, 2, 3, 4};
    }

    static {
        ф = 0;
        Е = 0;
        в = 0;
        Щ = 0;
        д = 0;
        EventPriorities.method2835();
    }
}


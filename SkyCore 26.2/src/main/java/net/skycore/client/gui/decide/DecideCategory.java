package net.skycore.client.gui.decide;

import net.skycore.client.module.ModuleCategory;

public enum DecideCategory {
    COMBAT("Бой", "V", ModuleCategory.COMBAT),
    MOVEMENT("Движение", "H", ModuleCategory.MOVEMENT),
    RENDER("Визуалы", "Q", ModuleCategory.RENDER),
    PLAYER("Игрок", "N", ModuleCategory.PLAYER),
    MISC("Утилиты", "L", ModuleCategory.MISC),
    COSMETICS("Косметика", "T", null),
    CONFIGS("Конфиги", "R", null),
    MARKERS("Метки", "S", null);

    private static final DecideCategory[] VALUES = values();

    private final String title;
    private final String icon;
    private final ModuleCategory hostCategory;
    public final DecideAnim.SatoshiAnim activeAnim = new DecideAnim.SatoshiAnim(300L, 1.0f, false);
    public final DecideAnim.SatoshiAnim hoverAnim = new DecideAnim.SatoshiAnim(300L, 1.0f, false);

    DecideCategory(String title, String icon, ModuleCategory hostCategory) {
        this.title = title;
        this.icon = icon;
        this.hostCategory = hostCategory;
    }

    public static DecideCategory[] all() {
        return VALUES;
    }

    public String title() {
        return this.title;
    }

    public String icon() {
        return this.icon;
    }

    public ModuleCategory hostCategory() {
        return this.hostCategory;
    }

    /** Maps a host module category onto the rail entry that owns it. */
    public static DecideCategory forHost(ModuleCategory category) {
        if (category != null) {
            for (int i = 0; i < VALUES.length; i++) {
                if (VALUES[i].hostCategory == category) {
                    return VALUES[i];
                }
            }
        }
        return MISC;
    }

    public boolean isSpecial() {
        return this.hostCategory == null;
    }
}

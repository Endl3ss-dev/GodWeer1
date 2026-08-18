package nuclear.module.api;

import nuclear.module.TypeList;

public enum ModuleCategory {
    Combat(TypeList.Combat, "d"),
    Movement(TypeList.Movement, "y"),
    Render(TypeList.Render, "w"),
    Player(TypeList.Player, "U"),
    Other(TypeList.Other, "Q");

    private final TypeList legacyType;
    public final String icon;

    ModuleCategory(TypeList legacyType, String icon) {
        this.legacyType = legacyType;
        this.icon = icon;
    }

    public TypeList toTypeList() {
        return this.legacyType;
    }

    public static ModuleCategory fromTypeList(TypeList type) {
        for (ModuleCategory category : values()) {
            if (category.legacyType == type) {
                return category;
            }
        }
        return Other;
    }
}

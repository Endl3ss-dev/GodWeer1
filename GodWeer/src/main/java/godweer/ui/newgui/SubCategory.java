package godweer.ui.newgui;

import godweer.modules.ModuleCategory;

import java.util.Arrays;

public enum SubCategory {

    COMBAT_RAGE("Rage", ModuleCategory.COMBAT, 0),
    COMBAT_LEGIT("Legit", ModuleCategory.COMBAT, 1),
    COMBAT_OTHER("Other", ModuleCategory.COMBAT, 2),

    MOVEMENT_ELYTRA("Elytra", ModuleCategory.MOVEMENT, 0),
    MOVEMENT_GROUND("Ground", ModuleCategory.MOVEMENT, 1),
    MOVEMENT_OTHER("Other", ModuleCategory.MOVEMENT, 2),

    PLAYER_IMPORTANT("Important", ModuleCategory.PLAYER, 0),
    PLAYER_ITEMS("Items", ModuleCategory.PLAYER, 1),
    PLAYER_OTHER("Other", ModuleCategory.PLAYER, 2),

    RENDER_IMPORTANT("Important", ModuleCategory.RENDER, 0),
    RENDER_PLAYER("Player", ModuleCategory.RENDER, 1),
    RENDER_OTHER("Other", ModuleCategory.RENDER, 2),

    MISC_FARM("Farm", ModuleCategory.MISC, 0),
    MISC_HELPERS("Helpers", ModuleCategory.MISC, 1),
    MISC_OTHER("Other", ModuleCategory.MISC, 2);

    private final String name;
    private final ModuleCategory category;
    private final int index;

    SubCategory(String name, ModuleCategory category, int index) {
        this.name = name;
        this.category = category;
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public ModuleCategory getCategory() {
        return category;
    }

    public int getIndex() {
        return index;
    }

    public static SubCategory[] getSubCategories(ModuleCategory category) {
        return Arrays.stream(values())
                .filter(sc -> sc.category == category)
                .toArray(SubCategory[]::new);
    }

    public static SubCategory getDefault(ModuleCategory category) {
        return Arrays.stream(values())
                .filter(sc -> sc.category == category && sc.index == 0)
                .findFirst()
                .orElse(null);
    }
}

package org.privates.client.screen.newgui;

import lombok.Getter;
import org.privates.client.api.annotations.Category1;
import org.privates.client.api.annotations.Category2;
import org.privates.client.api.annotations.Category3;
import org.privates.client.managers.module.Category;

@Getter
public enum SubCategory {
    COMBAT_RAGE("Rage", Category.COMBAT, 0, Category1.class),
    COMBAT_LEGIT("Legit", Category.COMBAT, 1, Category2.class),
    COMBAT_OTHER("Other", Category.COMBAT, 2, Category3.class),

    MOVEMENT_ELYTRA("Elytra", Category.MOVEMENT, 0, Category1.class),
    MOVEMENT_GROUND("Ground", Category.MOVEMENT, 1, Category2.class),
    MOVEMENT_OTHER("Other", Category.MOVEMENT, 2, Category3.class),

    PLAYER_IMPORTANT("Important", Category.PLAYER, 0, Category1.class),
    PLAYER_ITEMS("Items", Category.PLAYER, 1, Category2.class),
    PLAYER_OTHER("Other", Category.PLAYER, 2, Category3.class),

    RENDER_IMPORTANT("Important", Category.RENDER, 0, Category1.class),
    RENDER_PLAYER("Player", Category.RENDER, 1, Category2.class),
    RENDER_OTHER("Other", Category.RENDER, 2, Category3.class),

    MISC_FARM("Farm", Category.MISC, 0, Category1.class),
    MISC_HELPERS("Helpers", Category.MISC, 1, Category2.class),
    MISC_OTHER("Other", Category.MISC, 2, Category3.class);

    private final String name;
    private final Category category;
    private final int index;
    private final Class<?> annotationClass;

    SubCategory(String name, Category category, int index, Class<?> annotationClass) {
        this.name = name;
        this.category = category;
        this.index = index;
        this.annotationClass = annotationClass;
    }

    public static SubCategory[] getSubCategories(Category category) {
        return java.util.Arrays.stream(values())
                .filter(sc -> sc.category == category)
                .toArray(SubCategory[]::new);
    }

    public static SubCategory getDefault(Category category) {
        return java.util.Arrays.stream(values())
                .filter(sc -> sc.category == category && sc.index == 0)
                .findFirst()
                .orElse(null);
    }
}
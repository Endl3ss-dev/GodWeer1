package minced.enums;

public enum Combat {
    Combat("Combat", "a", "Категория имеет в себе модули для PvP контента"),
    Movement("Movement", "b", "Категория имеет в себе модули, отвечающие за мувмент клиента"),
    Visuals("Visuals", "c", "Категория имеет в себе модули, отвечающие за визуализацию клиента"),
    Player("Player", "d", "Категория имеет в себе модули, отвечающие за PvE контент"),
    Miscellaneous("Misc", "e", "Категория имеет в себе модули, отвечающие за вспомогательные модули клиента"),
    Configs("Configs", "f", "Категория имеет в себе все сохранённые конфигурации клиента");

    public final String field727;
    public final String field728;
    public final String field729;

    Combat(String name, String icon, String description) {
        this.field727 = name;
        this.field728 = icon;
        this.field729 = description;
    }

    public String method168() {
        return this.field727;
    }

    public String method169() {
        return this.field728;
    }

    public String method170() {
        return this.field729;
    }

    public static void method171() {
        // Kept for compatibility with the decompiled API. Enum constants are initialized by Java.
    }
}



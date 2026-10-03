package godweer.modules;

public enum ModuleCategory {

    COMBAT("Combat", "A"),
    MOVEMENT("Movement", "B"),
    RENDER("Visual", "D"),
    MISC("Util", "E"),
    PLAYER("Player", "C"),
    INTERFACE("Interface", "n");

    private final String name;
    private final String icons;

    ModuleCategory(String name, String icons) {
        this.name = name;
        this.icons = icons;
    }

    public String getName() {
        return this.name;
    }

    public String getIcons() {
        return this.icons;
    }
}

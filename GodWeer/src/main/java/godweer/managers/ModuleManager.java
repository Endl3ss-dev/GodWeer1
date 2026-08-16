package godweer.managers;

import godweer.modules.Module;
import godweer.modules.ModuleCategory;
import godweer.modules.impl.render.ClickGui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class ModuleManager {

    private static final List<Module> MODULES = new ArrayList<>();
    private static final List<Module> VIEW = Collections.unmodifiableList(MODULES);
    private static final Map<ModuleCategory, List<Module>> BY_CATEGORY = new EnumMap<>(ModuleCategory.class);

    private ModuleManager() {
    }

    public static void init() {
        MODULES.clear();
        BY_CATEGORY.clear();

        register(new ClickGui());

        rebuildIndex();
    }

    public static void register(Module module) {
        if (module == null) {
            return;
        }
        MODULES.add(module);
    }

    public static void rebuildIndex() {
        BY_CATEGORY.clear();
        for (ModuleCategory category : ModuleCategory.values()) {
            BY_CATEGORY.put(category, new ArrayList<>());
        }
        for (int i = 0, size = MODULES.size(); i < size; i++) {
            Module module = MODULES.get(i);
            BY_CATEGORY.get(module.getCategory()).add(module);
        }
    }

    public static List<Module> getModules() {
        return VIEW;
    }

    public static List<Module> getModules(ModuleCategory category) {
        List<Module> modules = BY_CATEGORY.get(category);
        return modules == null ? List.of() : modules;
    }

    public static Module getByName(String name) {
        for (int i = 0, size = MODULES.size(); i < size; i++) {
            Module module = MODULES.get(i);
            if (module.getName().equalsIgnoreCase(name)) {
                return module;
            }
        }
        return null;
    }

    public static <T extends Module> T get(Class<T> type) {
        for (int i = 0, size = MODULES.size(); i < size; i++) {
            Module module = MODULES.get(i);
            if (type.isInstance(module)) {
                return type.cast(module);
            }
        }
        return null;
    }
}

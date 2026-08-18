package godweer.managers;

import godweer.GodWeer;
import godweer.modules.Module;
import godweer.modules.ModuleCategory;
import godweer.modules.impl.combat.ItemSwap;
import godweer.modules.impl.combat.SexAura;
import godweer.modules.impl.misc.AntiCheatDetector;
import godweer.modules.impl.player.DrinkUse;
import godweer.modules.impl.render.Arrows;
import godweer.modules.impl.render.ChinaHat;
import godweer.modules.impl.render.Trails;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class ModuleManager {

    private static final List<Module> MODULES = new ArrayList<>();
    private static final List<Module> VIEW = Collections.unmodifiableList(MODULES);
    private static final Map<ModuleCategory, List<Module>> BY_CATEGORY = new EnumMap<>(ModuleCategory.class);

    private static boolean tickListenerRegistered;

    private ModuleManager() {
    }

    public static void init() {
        MODULES.clear();
        BY_CATEGORY.clear();

        register(new SexAura());
        register(new ItemSwap());
        register(new AntiCheatDetector());
        register(new DrinkUse());
        register(new Trails());
        register(new ChinaHat());
        register(new Arrows());

        rebuildIndex();
        FriendManager.init();

        if (!tickListenerRegistered) {
            ClientTickEvents.END_CLIENT_TICK.register(client -> tick());
            tickListenerRegistered = true;
        }
    }

    public static void register(Module module) {
        if (module != null) {
            MODULES.add(module);
        }
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

    public static void tick() {
        for (int i = 0, size = MODULES.size(); i < size; i++) {
            Module module = MODULES.get(i);
            if (!module.isEnable()) {
                continue;
            }
            try {
                module.onTick();
            } catch (Exception exception) {
                GodWeer.LOGGER.error("Module '{}' failed during client tick", module.getName(), exception);
                module.setEnabled(false);
            }
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

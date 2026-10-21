package swerve.managers;

import swerve.Swerve;
import swerve.modules.Module;
import swerve.modules.ModuleCategory;
import swerve.modules.impl.combat.AutoGApple;
import swerve.modules.impl.combat.AutoHeal;
import swerve.modules.impl.combat.AutoPotion;
import swerve.modules.impl.combat.ItemSwap;
import swerve.modules.impl.combat.KillAura;
import swerve.modules.impl.combat.SexAura;
import swerve.modules.impl.hud.ArrayListHud;
import swerve.modules.impl.hud.Notifications;
import swerve.modules.impl.hud.Watermark;
import swerve.modules.impl.player.DrinkUse;
import swerve.modules.impl.movement.Speed;
import swerve.modules.impl.movement.ElytraFlight;
import swerve.modules.impl.movement.NoWeb;
import swerve.modules.impl.hud.TargetHud;
import swerve.modules.impl.render.Arrows;
import swerve.modules.impl.render.ChinaHat;
import swerve.modules.impl.render.SpatialGui;
import swerve.modules.impl.render.Trails;
import swerve.modules.impl.render.HandShader;
import swerve.modules.impl.render.ViewModel;
import swerve.ui.hud.HudManager;
import swerve.utils.item.ConsumableUseController;
import swerve.utils.movement.ElytraFlightRotation;
import swerve.utils.rotation.RotationController;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;

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
        ConsumableUseController.clearAll();
        MODULES.clear();
        BY_CATEGORY.clear();

        // Слой HUD инициализируется до модулей: элементы регистрируются в HudManager из своего конструктора.
        HudManager.init();

        register(new AutoHeal());
        register(new AutoGApple());
        register(new AutoPotion());
        register(new SexAura());
        // KillAura: порт из Private; ротации Intave/Matrix/HW, цель ауры читает ElytraFlight.
        register(new KillAura());
        register(new ItemSwap());
        register(new Speed());
        register(new ElytraFlight());
        register(new NoWeb());
        register(new DrinkUse());
        register(new Trails());
        register(new ChinaHat());
        register(new Arrows());
        register(new ViewModel());
        register(new HandShader());
        register(new SpatialGui());
        register(new TargetHud());
        register(new ArrayListHud());
        register(new Watermark());
        register(new Notifications());

        rebuildIndex();
        FriendManager.init();

        if (!tickListenerRegistered) {
            // AlphaDLC EventTick: Fabric START_CLIENT_TICK also runs at Minecraft.tick HEAD.
            ClientTickEvents.START_CLIENT_TICK.register(ElytraFlightRotation::tick);
            // [+] Ротация ауры: компонент тикает всегда (возврат камеры после выключения модуля),
            // запрос ротации — до тика игрока, чтобы пакет позиции ушёл с новыми углами.
            ClientTickEvents.START_CLIENT_TICK.register(RotationController::tick);
            ClientTickEvents.START_CLIENT_TICK.register(KillAura::rotationTick);
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
        ConsumableUseController.tickDisabledControllers(Minecraft.getInstance());
        for (int i = 0, size = MODULES.size(); i < size; i++) {
            Module module = MODULES.get(i);
            if (!module.isEnable()) {
                continue;
            }
            try {
                module.onTick();
            } catch (Exception exception) {
                Swerve.LOGGER.error("Module '{}' failed during client tick", module.getName(), exception);
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

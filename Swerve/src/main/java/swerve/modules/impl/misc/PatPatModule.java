package swerve.modules.impl.misc;

import java.lang.reflect.Method;

import swerve.Swerve;
import swerve.managers.ModuleManager;
import swerve.modules.Module;
import swerve.modules.ModuleCategory;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;

/**
 * Toggle adapter for the bundled PatPat mod.
 *
 * <p>PatPat remains an independent nested Fabric mod so its networking, rendering,
 * configuration, and mixins continue to run through its own entrypoints. This
 * Swerve module only controls PatPat's public client enabled flag; no gameplay
 * packets or private PatPat classes are reimplemented here.
 */
public final class PatPatModule extends Module {

    private static boolean startupSyncRegistered;
    private static boolean startupSyncCompleted;

    public PatPatModule() {
        super("PatPat", "Поглаживание мобов (PatPat)", ModuleCategory.MISC);
        registerStartupSync();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        setPatPatEnabled(true);
    }

    @Override
    public void onDisable() {
        setPatPatEnabled(false);
        super.onDisable();
    }

    private static void registerStartupSync() {
        if (startupSyncRegistered) {
            return;
        }
        startupSyncRegistered = true;

        // ConfigManager restores module state after ModuleManager constructs modules.
        // Sync on the first game tick so this still wins if PatPat's own entrypoint
        // reloads its config later during client initialization.
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (startupSyncCompleted) {
                return;
            }
            startupSyncCompleted = true;
            PatPatModule module = ModuleManager.get(PatPatModule.class);
            if (module != null) {
                setPatPatEnabled(module.isEnable());
            }
        });
    }

    private static void setPatPatEnabled(boolean enabled) {
        if (!FabricLoader.getInstance().isModLoaded("patpat")) {
            Swerve.LOGGER.warn("PatPat integration is unavailable; nested PatPat mod was not loaded");
            return;
        }

        try {
            ClassLoader loader = PatPatModule.class.getClassLoader();
            Class<?> configClass = Class.forName(
                    "net.lopymine.patpat.client.config.PatPatClientConfig", true, loader);
            Object config = configClass.getMethod("getInstance").invoke(null);
            Method getMainConfig = configClass.getMethod("getMainConfig");
            Object mainConfig = getMainConfig.invoke(config);
            mainConfig.getClass().getMethod("setModEnabled", boolean.class).invoke(mainConfig, enabled);
            configClass.getMethod("saveAsync").invoke(config);
        } catch (ReflectiveOperationException | LinkageError | RuntimeException exception) {
            Swerve.LOGGER.warn("Could not set PatPat enabled state to {}", enabled, exception);
        }
    }
}

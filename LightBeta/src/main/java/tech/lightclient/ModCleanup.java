package tech.lightclient;

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

/**
 * Stable Fabric pre-launch entrypoint.
 *
 * <p>This class is deliberately self-contained: Fabric loads pre-launch
 * entrypoints before mixins are applied, so it must not reference Minecraft,
 * mixin, or client classes during class loading.</p>
 */
public final class ModCleanup implements PreLaunchEntrypoint {

    @Override
    public void onPreLaunch() {
        // Intentionally empty. Its presence provides a stable bootstrap class
        // for the protection step and Fabric's pre-launch lifecycle.
    }
}

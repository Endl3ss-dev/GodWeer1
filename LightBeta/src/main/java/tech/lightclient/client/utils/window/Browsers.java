package tech.lightclient.client.utils.window;

import net.dimaskama.mcef.api.MCEFApi;
import tech.lightclient.LightClient;

/**
 * Lazy bootstrap for MCEF Modern used by the in-world Windows browser surfaces.
 */
public final class Browsers {

    private static volatile boolean started;

    private Browsers() {
    }

    public static void ensureInit() {
        if (started) {
            return;
        }
        started = true;
        try {
            MCEFApi.initialize();
            LightClient.LOGGER.info("[Windows] MCEF initialization started");
        } catch (Throwable t) {
            LightClient.LOGGER.error("[Windows] MCEF init failed", t);
        }
    }

    public static MCEFApi api() {
        try {
            return MCEFApi.getInstance();
        } catch (Throwable t) {
            return null;
        }
    }

    public static boolean ready() {
        return api() != null;
    }
}

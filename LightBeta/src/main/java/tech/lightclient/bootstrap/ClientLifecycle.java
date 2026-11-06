package tech.lightclient.bootstrap;

public final class ClientLifecycle {
    private static volatile boolean initialized;

    private ClientLifecycle() {
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static void markInitialized() {
        initialized = true;
    }
}

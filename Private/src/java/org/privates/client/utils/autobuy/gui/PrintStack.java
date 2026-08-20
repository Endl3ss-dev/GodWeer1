package org.privates.client.utils.autobuy.gui;

import java.util.HashMap;
import java.util.Map;

/**
 * A utility class to manage callback functions for stack operations
 */
public class PrintStack {
    private static final Map<Class<?>, Runnable> callbacks = new HashMap<>();

    /**
     * Set a callback for a specific class
     *
     * @param clazz    the class to associate with the callback
     * @param callback the callback to run
     */
    public static void setCallback(Class<?> clazz, Runnable callback) {
        callbacks.put(clazz, callback);
    }

    /**
     * Execute the callback for a specific class
     *
     * @param clazz the class whose callback should be executed
     */
    public static void executeCallback(Class<?> clazz) {
        Runnable callback = callbacks.get(clazz);
        if (callback != null) {
            callback.run();
        }
    }

    /**
     * Clear all callbacks
     */
    public static void clearCallbacks() {
        callbacks.clear();
    }
}
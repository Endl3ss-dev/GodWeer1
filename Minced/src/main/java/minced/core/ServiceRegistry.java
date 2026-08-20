/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 */
package minced.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import minced.core.BotSupplier;
import minced.core.BotManager;
import minced.core.BotView;
import minced.core.EventBus;
import minced.core.CommandSourceUtil;

public final class ServiceRegistry {
    static Map field8130;
    static CommandSourceUtil field8131;
    static BotManager field8132;
    static BotView field8133;

    private ServiceRegistry() {
    }

    public static void method2112() {
        field8130 = new ConcurrentHashMap();
    }

    public static CommandSourceUtil method2113() {
        return field8131;
    }

    public static void method2114() {
        field8132 = new BotManager();
        field8133 = new BotView();
        field8131 = CommandSourceUtil.method422();
        ServiceRegistry.method2117(BotManager.class, field8132);
        ServiceRegistry.method2117(BotView.class, field8133);
        ServiceRegistry.method2117(CommandSourceUtil.class, field8131);
        EventBus.method3013(field8132);
        EventBus.method3013(field8133);
    }

    public static void method2115() {
        if (field8132 != null) {
            field8132.method1860();
            EventBus.method3009(field8132);
        }
        if (field8133 != null) {
            field8133.method1860();
            EventBus.method3009(field8133);
        }
        field8132 = null;
        field8133 = null;
        field8131 = null;
        field8130.clear();
    }

    public static BotManager method2116() {
        return field8132;
    }

    public static void method2117(Class param0, Object param1) {
        field8130.put(param0, param1);
    }

    public static Object method2118(Class param0) {
        return field8130.get(param0);
    }

    public static BotView method2119() {
        return field8133;
    }

    static {
        ServiceRegistry.method2112();
    }
}


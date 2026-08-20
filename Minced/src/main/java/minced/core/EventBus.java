/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 */
package minced.core;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import minced.core.ListenerList;
import minced.core.EventCancellable;
import minced.core.EventListener;
import minced.core.EventPriorities;
import minced.core.EventHandler;
import minced.core.Event;

public final class EventBus {
    static Map field10388;

    private static boolean method3004(Method param0, Class param1) {
        return EventBus.method3007(param0) || !param0.getParameterTypes()[0].equals(param1);
    }

    public static void method3005(boolean param0) {
        Iterator v1 = field10388.entrySet().iterator();
        while (v1.hasNext()) {
            if (param0 && !((List)((java.util.Map.Entry)v1.next()).getValue()).isEmpty()) continue;
            v1.remove();
        }
    }

    private static void method3006(Method param0, Object param1) {
        Class<?> v2 = param0.getParameterTypes()[0];
        EventListener v3 = new EventListener(param1, param0, param0.getAnnotation(EventHandler.class).value());
        if (!v3.method2236().isAccessible()) {
            v3.method2236().setAccessible(true);
        }
        if (field10388.containsKey(v2)) {
            if (!((List)field10388.get(v2)).contains(v3)) {
                ((List)field10388.get(v2)).add(v3);
                EventBus.method3014(v2);
            }
        } else {
            field10388.put(v2, new ListenerList(v3));
        }
    }

    private static boolean method3007(Method param0) {
        return param0.getParameterTypes().length != 1 || !param0.isAnnotationPresent(EventHandler.class);
    }

    private EventBus() {
    }

    private static void method3008(EventListener param0, Object param1) {
        try {
            param0.method2236().invoke(param0.method2235(), param1);
        }
        catch (java.lang.reflect.InvocationTargetException v2) {
            // Surface what the listener threw, not the reflection wrapper.
            Throwable v3 = v2.getCause();
            if (v3 instanceof RuntimeException) {
                throw (RuntimeException)v3;
            }
            if (v3 instanceof Error) {
                throw (Error)v3;
            }
            throw new RuntimeException(v3 != null ? v3 : v2);
        }
        catch (IllegalAccessException v2) {
            throw new RuntimeException(v2);
        }
    }

    public static void method3009(Object param0) {
        for (List v2 : (Iterable<List>)(field10388.values())) {
            for (EventListener v4 : (Iterable<EventListener>)(v2)) {
                if (!v4.method2235().equals(param0)) continue;
                v2.remove(v4);
            }
        }
        EventBus.method3005(true);
    }

    public static void method3010(Object param0, Class param1) {
        for (Method v5 : param0.getClass().getDeclaredMethods()) {
            if (EventBus.method3004(v5, param1)) continue;
            EventBus.method3006(v5, param0);
        }
    }

    public static void method3011() {
        field10388 = new HashMap();
    }

    public static void method3012(Object param0, Class param1) {
        if (field10388.containsKey(param1)) {
            for (EventListener v3 : (Iterable<EventListener>)((List)field10388.get(param1))) {
                if (!v3.method2235().equals(param0)) continue;
                ((List)field10388.get(param1)).remove(v3);
            }
            EventBus.method3005(true);
        }
    }

    public static void method3013(Object param0) {
        for (Method v4 : param0.getClass().getDeclaredMethods()) {
            if (EventBus.method3007(v4)) continue;
            EventBus.method3006(v4, param0);
        }
    }

    private static void method3014(Class param0) {
        CopyOnWriteArrayList<EventListener> v1 = new CopyOnWriteArrayList<EventListener>();
        for (byte v5 : EventPriorities.field9687) {
            for (EventListener v7 : (Iterable<EventListener>)((List)field10388.get(param0))) {
                if (v7.method2234() != v5) continue;
                v1.add(v7);
            }
        }
        field10388.put(param0, v1);
    }

    // Declared against Object: in the shipped bytecode the event objects are passed to a
    // Event-typed parameter without ever implementing it (the JVM verifier does not enforce
    // interface types), so recompiling with a real checkcast here would fail at runtime.
    public static final Object method3015(Object param0) {
        block4: {
            List v1 = (List)field10388.get(param0.getClass());
            if (v1 == null) break block4;
            if (param0 instanceof EventCancellable) {
                EventCancellable v2 = (EventCancellable)((Object)param0);
                for (EventListener v4 : (Iterable<EventListener>)(v1)) {
                    EventBus.method3008(v4, param0);
                    if (!v2.method2052()) continue;
                    break;
                }
            } else {
                for (EventListener v3 : (Iterable<EventListener>)(v1)) {
                    EventBus.method3008(v3, param0);
                }
            }
        }
        return param0;
    }

    public static void method3016(Class param0) {
        Iterator v1 = field10388.entrySet().iterator();
        while (v1.hasNext()) {
            if (!((Class)((java.util.Map.Entry)v1.next()).getKey()).equals(param0)) continue;
            v1.remove();
            break;
        }
    }

    static {
        EventBus.method3011();
    }
}




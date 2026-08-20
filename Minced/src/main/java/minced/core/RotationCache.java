/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 */
package minced.core;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import minced.core.RotationBase;
import minced.core.Minced;

public final class RotationCache
{
    static ConcurrentMap field7993;
    static String field7994;

    public static RotationBase method1997(Class param02) {
        return (RotationBase)param02.cast(field7993.computeIfAbsent(param02, param0 -> Minced.getInstance().getComponentHandler().method2012((Class)param0)));
    }

    private RotationCache() {
        throw new UnsupportedOperationException(field7994);
    }

    public static void method1998() {
        field7993 = new ConcurrentHashMap();
    }

    static {
        field7994 = "This is a utility class and cannot be instantiated";
        RotationCache.method1998();
    }
}




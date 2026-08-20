/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  sg.ec.ТЖ$Ч
 */
package minced.core;

import java.util.ArrayDeque;
import java.util.Deque;
import minced.core.RecordClass229;
import sg.ec.tzh;

public final class ConnectionState {
    static ThreadLocal I;

    public static RecordClass229 method1882() {
        Deque v0 = (Deque)I.get();
        return v0.isEmpty() ? null : (RecordClass229)v0.peek();
    }

    public static tzh.ch method1883(RecordClass229 param0) {
        if (!ConnectionState.method1884(param0)) {
            return tzh.ch.н;
        }
        Deque v1 = (Deque)I.get();
        v1.push(param0);
        return new tzh.ch() {
            @Override
            public void close() {
                ConnectionState.method1886(v1);
            }
        };
    }

    private static boolean method1884(RecordClass229 param0) {
        return param0 != null && param0.world() != null && param0.player() != null && param0.interactionManager() != null;
    }

    private ConnectionState() {
    }

    public static void method1885() {
        I = ThreadLocal.withInitial(ArrayDeque::new);
    }

    private static void method1886(Deque param0) {
        if (param0 == null || param0.isEmpty()) {
            return;
        }
        param0.pop();
        if (param0.isEmpty()) {
            I.remove();
        }
    }

    static {
        ConnectionState.method1885();
    }
}




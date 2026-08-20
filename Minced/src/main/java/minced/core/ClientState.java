/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 */
package minced.core;

import minced.core.AuraModeHandler;

public class ClientState
implements AuraModeHandler {
    long field11042;
    long field11043 = System.currentTimeMillis();

    public boolean method3110(double param1) {
        return (double)this.method3112() >= param1;
    }

    public boolean method3111(long param1, boolean param3) {
        boolean v4;
        boolean bl = v4 = this.method3112() >= param1;
        if (v4 && param3) {
            this.method3114();
        }
        return v4;
    }

    public long method3112() {
        return System.currentTimeMillis() - this.field11042;
    }

    public boolean method3113(long param1) {
        return this.method3112() >= param1;
    }

    public ClientState() {
        this.method3114();
    }

    public void method3114() {
        this.field11042 = System.currentTimeMillis();
    }

    public boolean method3115() {
        return this.field11043 < System.currentTimeMillis();
    }

    public void method3116(long param1) {
        this.field11042 = param1;
    }
}


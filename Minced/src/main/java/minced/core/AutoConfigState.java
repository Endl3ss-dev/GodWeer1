/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.ClientModInitializer
 */
package minced.core;

import net.fabricmc.api.ClientModInitializer;

public class AutoConfigState
implements ClientModInitializer {
    @Override
    public void onInitializeClient() { }

    String field12483;
    String field12484;
    String field12485;

    public String l() {
        return this.field12483;
    }

    public void method3550(String param1) {
        this.field12484 = param1;
    }

    public void method3551(String param1) {
        this.field12483 = param1;
    }

    public String method3552() {
        return this.field12485;
    }

    public void method3553(String param1) {
        this.field12485 = param1;
    }

    public String method3554() {
        return this.field12484;
    }
}




/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.util.Window
 */
package minced.core;

import minced.module.visual.Interface;
import java.io.Closeable;
import java.io.Flushable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;

public interface ClientUtil
extends AutoCloseable,
Appendable,
Closeable,
Flushable {
    public static final MinecraftClient I = MinecraftClient.getInstance();
    public static final Window field9305 = I.getWindow();

    public static void method2704() {
        // Interface constants are initialized eagerly. Kept for compatibility.
    }
}




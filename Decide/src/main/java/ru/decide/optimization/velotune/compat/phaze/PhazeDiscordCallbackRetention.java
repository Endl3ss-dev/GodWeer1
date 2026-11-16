/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.optimization.velotune.compat.phaze;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class PhazeDiscordCallbackRetention {
    private static final Set<Object> CALLBACKS = Collections.newSetFromMap(new IdentityHashMap());

    private PhazeDiscordCallbackRetention() {
    }

    public static synchronized boolean retain(Object callback) {
        return callback != null && CALLBACKS.add(callback);
    }
}


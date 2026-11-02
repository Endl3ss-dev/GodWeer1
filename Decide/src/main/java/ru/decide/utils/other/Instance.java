/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.utils.other;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.Generated;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.Client;
import ru.decide.module.api.Module;

@Environment(value=EnvType.CLIENT)
public final class Instance {
    private static final ConcurrentMap<Class<? extends Module>, Module> instanceModules = new ConcurrentHashMap<Class<? extends Module>, Module>();

    public static <T extends Module> T get(Class<T> clazz) {
        return (T)((Module)clazz.cast(instanceModules.computeIfAbsent(clazz, instance -> Client.get().moduleManager().get(instance))));
    }

    public static <T extends Module> T get(String module) {
        return Client.get().moduleManager().get(module);
    }

    @Generated
    private Instance() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.loader.api.FabricLoader
 */
package ru.decide.utils.render;

import java.lang.reflect.Method;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;

@Environment(value=EnvType.CLIENT)
public final class IrisCompat {
    private static boolean checked = false;
    private static boolean present = false;
    private static Object apiInstance;
    private static Method isShaderPackInUseMethod;

    private IrisCompat() {
    }

    private static void init() {
        String[] apiClasses;
        boolean modPresent;
        if (checked) {
            return;
        }
        checked = true;
        boolean bl = modPresent = FabricLoader.getInstance().isModLoaded("iris") || FabricLoader.getInstance().isModLoaded("oculus");
        if (!modPresent) {
            return;
        }
        for (String className : apiClasses = new String[]{"net.irisshaders.iris.api.v0.IrisApi", "net.coderbot.iris.api.v0.IrisApi"}) {
            try {
                Class<?> apiClass = Class.forName(className);
                Method getInstance = apiClass.getMethod("getInstance", new Class[0]);
                apiInstance = getInstance.invoke(null, new Object[0]);
                isShaderPackInUseMethod = apiClass.getMethod("isShaderPackInUse", new Class[0]);
                present = true;
                return;
            }
            catch (Throwable throwable) {
            }
        }
    }

    public static boolean shadersActive() {
        IrisCompat.init();
        if (!present) {
            return false;
        }
        try {
            Object result = isShaderPackInUseMethod.invoke(apiInstance, new Object[0]);
            return result instanceof Boolean && (Boolean)result != false;
        }
        catch (Throwable ignored) {
            return false;
        }
    }
}


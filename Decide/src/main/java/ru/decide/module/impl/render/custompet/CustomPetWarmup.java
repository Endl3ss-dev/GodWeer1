/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  software.bernie.geckolib.cache.GeckoLibResources
 */
package ru.decide.module.impl.render.custompet;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import software.bernie.geckolib.cache.GeckoLibResources;

@Environment(value=EnvType.CLIENT)
public final class CustomPetWarmup {
    private static final class_2960 TEXTURE = class_2960.method_60655((String)"decide", (String)"textures/entity/frog/custom_pet.png");
    private static final class_2960 MODEL = class_2960.method_60655((String)"decide", (String)"frog/custom_pet");
    private static boolean warmedUp;

    private CustomPetWarmup() {
    }

    public static void warmup() {
        if (warmedUp) {
            return;
        }
        class_310 minecraft = class_310.method_1551();
        if (minecraft == null) {
            return;
        }
        try {
            if (minecraft.method_1531() != null) {
                minecraft.method_1531().method_4619(TEXTURE);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        try {
            GeckoLibResources.getBakedModels().getModel(MODEL);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        warmedUp = true;
    }
}


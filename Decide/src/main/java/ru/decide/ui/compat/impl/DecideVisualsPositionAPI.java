/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_310
 */
package ru.decide.ui.compat.impl;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import ru.decide.ui.compat.PositionAPI;

@Environment(value=EnvType.CLIENT)
public final class DecideVisualsPositionAPI
implements PositionAPI {
    @Override
    public float mouseX() {
        return (float)class_310.method_1551().field_1729.method_1603();
    }

    @Override
    public float mouseY() {
        return (float)class_310.method_1551().field_1729.method_1604();
    }

    @Override
    public float screenWidth() {
        return class_310.method_1551().method_22683().method_4486();
    }

    @Override
    public float screenHeight() {
        return class_310.method_1551().method_22683().method_4502();
    }
}


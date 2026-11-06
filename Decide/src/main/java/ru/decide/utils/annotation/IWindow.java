/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1041
 */
package ru.decide.utils.annotation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1041;
import ru.decide.utils.annotation.IMinecraft;

@Environment(value=EnvType.CLIENT)
public interface IWindow
extends IMinecraft {
    public static final class_1041 mw = mc.method_22683();
}


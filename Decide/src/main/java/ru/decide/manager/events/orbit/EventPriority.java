/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.manager.events.orbit;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public class EventPriority {
    public static final int HIGHEST = 500;
    public static final int HIGH = 250;
    public static final int MEDIUM = 0;
    public static final int LOW = -250;
    public static final int LOWEST = -500;
}


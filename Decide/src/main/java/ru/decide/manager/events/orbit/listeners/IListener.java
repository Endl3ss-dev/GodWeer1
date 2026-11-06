/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.manager.events.orbit.listeners;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public interface IListener {
    public void call(Object var1);

    public Class<?> getTarget();

    public int getPriority();

    public boolean isStatic();
}


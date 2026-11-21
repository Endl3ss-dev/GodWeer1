/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.manager.events;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.Client;

@Environment(value=EnvType.CLIENT)
public abstract class Handler {
    public Handler() {
        Client.eventHandler().subscribe(this);
    }
}


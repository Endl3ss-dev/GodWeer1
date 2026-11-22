/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Callback
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.rpc.callbacks;

import com.sun.jna.Callback;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.rpc.DiscordUser;

@Environment(value=EnvType.CLIENT)
public interface ReadyCallback
extends Callback {
    public void apply(DiscordUser var1);
}


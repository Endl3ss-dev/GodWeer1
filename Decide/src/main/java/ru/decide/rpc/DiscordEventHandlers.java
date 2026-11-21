/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Structure
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.rpc;

import com.sun.jna.Structure;
import java.util.Arrays;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.rpc.callbacks.DisconnectedCallback;
import ru.decide.rpc.callbacks.ErroredCallback;
import ru.decide.rpc.callbacks.JoinGameCallback;
import ru.decide.rpc.callbacks.JoinRequestCallback;
import ru.decide.rpc.callbacks.ReadyCallback;
import ru.decide.rpc.callbacks.SpectateGameCallback;

@Environment(value=EnvType.CLIENT)
public class DiscordEventHandlers
extends Structure {
    public DisconnectedCallback disconnected;
    public JoinRequestCallback joinRequest;
    public SpectateGameCallback spectateGame;
    public ReadyCallback ready;
    public ErroredCallback errored;
    public JoinGameCallback joinGame;

    protected List<String> getFieldOrder() {
        return Arrays.asList("ready", "disconnected", "errored", "joinGame", "spectateGame", "joinRequest");
    }
}


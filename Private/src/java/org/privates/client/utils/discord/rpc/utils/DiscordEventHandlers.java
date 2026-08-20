package org.privates.client.utils.discord.rpc.utils;

import com.sun.jna.Structure;
import org.privates.client.utils.discord.rpc.callbacks.*;

import java.util.Arrays;
import java.util.List;

public class DiscordEventHandlers extends Structure {
    public DisconnectedCallback disconnected;
    public JoinRequestCallback joinRequest;
    public SpectateGameCallback spectateGame;
    public ReadyCallback ready;
    public ErroredCallback errored;
    public JoinGameCallback joinGame;

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList("ready", "disconnected", "errored", "joinGame", "spectateGame", "joinRequest");
    }

    public static class Builder {
        private final DiscordEventHandlers handlers = new DiscordEventHandlers();

        public DiscordEventHandlers build() {
            return handlers;
        }

        public Builder disconnected(DisconnectedCallback callback) {
            handlers.disconnected = callback;
            return this;
        }

        public Builder errored(ErroredCallback callback) {
            handlers.errored = callback;
            return this;
        }

        public Builder ready(ReadyCallback callback) {
            handlers.ready = callback;
            return this;
        }

        public Builder joinRequest(JoinRequestCallback callback) {
            handlers.joinRequest = callback;
            return this;
        }

        public Builder joinGame(JoinGameCallback callback) {
            handlers.joinGame = callback;
            return this;
        }

        public Builder spectateGame(SpectateGameCallback callback) {
            handlers.spectateGame = callback;
            return this;
        }
    }
}


package mods.voicechat.eventforge;


import nuclear.control.events.Event;

public class TickEvent extends Event {
    public final Type type;
    public final Phase phase;

    public TickEvent(Type type, Phase phase) {
        this.type = type;
        this.phase = phase;
    }

    public enum Phase {
        START,
        END;
    }

    public enum Type {
        WORLD,
        PLAYER,
        CLIENT,
        SERVER,
        RENDER;
    }

    public static class ServerTickEvent extends TickEvent {
        public ServerTickEvent(TickEvent.Phase phase) {
            super(Type.SERVER, phase);
        }
    }

    public static class ClientTickEvent extends TickEvent {
        public ClientTickEvent(TickEvent.Phase phase) {
            super(Type.CLIENT, phase);
        }
    }
}

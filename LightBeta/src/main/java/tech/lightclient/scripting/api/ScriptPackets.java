package tech.lightclient.scripting.api;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.Packet;
import tech.lightclient.client.events.api.Event;

/**
 * Packet events split into receive and send, so a script can subscribe to one direction only.
 *
 * <p>These fire on the <em>network</em> thread. Reading packet fields there is fine; touching the
 * world, the player or the inventory is not, and needs {@code onClientThread { }}.</p>
 */
public final class ScriptPackets {

    /** Shared behaviour of both directions. */
    public abstract static class Base extends Event {
        private final Packet<?> packet;
        private final Event origin;

        Base(Packet<?> packet, Event origin) {
            this.packet = packet;
            this.origin = origin;
        }

        /** The raw packet. Its type name tells you what it is, e.g. via {@code is} checks. */
        public Packet<?> packet() {
            return packet;
        }

        /** Simple class name, handy for scripts that only want to match on the name. */
        public String type() {
            return packet == null ? "" : packet.getClass().getSimpleName();
        }

        /** Cancelling here cancels the underlying client event too. */
        @Override
        public void cancel() {
            super.cancel();
            if (origin != null) origin.cancel();
        }
    }

    /** A packet arrived from the server. */
    public static final class Receive extends Base {
        public Receive(Packet<?> packet, Event origin) {
            super(packet, origin);
        }
    }

    /** The client is about to send a packet. */
    public static final class Send extends Base {
        public Send(Packet<?> packet, Event origin) {
            super(packet, origin);
        }
    }

    /** Sending packets, exposed to scripts through {@code game.packets()}. */
    public static final class Sender {
        public boolean send(Packet<?> packet) {
            if (packet == null) return false;
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.getNetworkHandler() == null) return false;
            try {
                mc.getNetworkHandler().sendPacket(packet);
                return true;
            } catch (Throwable t) {
                return false;
            }
        }

        /** Same as {@link #send} here; kept because the docs distinguish the two. */
        public boolean sendSequenced(Packet<?> packet) {
            return send(packet);
        }
    }

    private ScriptPackets() {}
}

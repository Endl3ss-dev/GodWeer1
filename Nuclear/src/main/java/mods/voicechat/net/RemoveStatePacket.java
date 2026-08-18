package mods.voicechat.net;

import mods.voicechat.Voicechat;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

import java.util.UUID;

public class RemoveStatePacket implements Packet<RemoveStatePacket> {

    public static final ResourceLocation REMOVE_STATE = new ResourceLocation(Voicechat.MODID, "remove_state");

    private UUID uuid;

    public RemoveStatePacket() {

    }

    public RemoveStatePacket(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    @Override
    public ResourceLocation getIdentifier() {
        return REMOVE_STATE;
    }

    @Override
    public RemoveStatePacket fromBytes(PacketBuffer buf) {
        uuid = buf.readUniqueId();
        return this;
    }

    @Override
    public void toBytes(PacketBuffer buf) {
        buf.writeUniqueId(uuid);
    }

}

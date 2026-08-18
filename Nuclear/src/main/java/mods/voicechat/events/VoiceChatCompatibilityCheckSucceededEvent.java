package mods.voicechat.events;

import nuclear.control.events.Event;
import net.minecraft.entity.player.ServerPlayerEntity;

public class VoiceChatCompatibilityCheckSucceededEvent extends Event {

    private final ServerPlayerEntity player;

    public VoiceChatCompatibilityCheckSucceededEvent(ServerPlayerEntity player) {
        this.player = player;
    }

    public ServerPlayerEntity getPlayer() {
        return player;
    }

    @Override
    public boolean isCancel() {
        return false;
    }
}

package mods.voicechat.events;

import com.darkmagician6.eventapi.events.Event;
import net.minecraft.entity.player.ServerPlayerEntity;

public class ServerVoiceChatConnectedEvent implements Event {

    private final ServerPlayerEntity player;

    public ServerVoiceChatConnectedEvent(ServerPlayerEntity player) {
        this.player = player;
    }

    public ServerPlayerEntity getPlayer() {
        return player;
    }

}

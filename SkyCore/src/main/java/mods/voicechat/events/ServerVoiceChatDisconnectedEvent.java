package mods.voicechat.events;

import com.darkmagician6.eventapi.events.Event;

import java.util.UUID;

public class ServerVoiceChatDisconnectedEvent implements Event {

    private final UUID playerID;

    public ServerVoiceChatDisconnectedEvent(UUID playerID) {
        this.playerID = playerID;
    }

    public UUID getPlayerID() {
        return playerID;
    }


}

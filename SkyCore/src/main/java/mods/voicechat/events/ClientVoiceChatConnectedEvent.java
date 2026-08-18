package mods.voicechat.events;

import mods.voicechat.voice.client.ClientVoicechatConnection;
import com.darkmagician6.eventapi.events.Event;

public class ClientVoiceChatConnectedEvent implements Event {

    private final ClientVoicechatConnection client;

    public ClientVoiceChatConnectedEvent(ClientVoicechatConnection client) {
        this.client = client;
    }

    public ClientVoicechatConnection getClient() {
        return client;
    }

}

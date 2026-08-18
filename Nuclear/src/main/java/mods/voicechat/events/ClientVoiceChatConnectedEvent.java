package mods.voicechat.events;

import mods.voicechat.voice.client.ClientVoicechatConnection;
import nuclear.control.events.Event;

public class ClientVoiceChatConnectedEvent extends Event {

    private final ClientVoicechatConnection client;

    public ClientVoiceChatConnectedEvent(ClientVoicechatConnection client) {
        this.client = client;
    }

    public ClientVoicechatConnection getClient() {
        return client;
    }

    @Override
    public boolean isCancel() {
        return false;
    }
}

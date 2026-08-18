package mods.voicechat.events;

import nuclear.control.events.Event;

public class ClientVoiceChatDisconnectedEvent extends Event {

    @Override
    public boolean isCancel() {
        return false;
    }

}

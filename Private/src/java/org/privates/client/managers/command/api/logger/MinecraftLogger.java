package org.privates.client.managers.command.api.logger;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.privates.client.managers.command.api.Logger;
import org.privates.client.utils.chat.ChatUtil;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class MinecraftLogger implements Logger {
    @Override
    public void log(String message) {
        ChatUtil.addText(message);
    }
}

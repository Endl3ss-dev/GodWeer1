/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.command.impl;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.Client;
import ru.decide.command.Command;
import ru.decide.utils.math.ChatUtils;

@Environment(value=EnvType.CLIENT)
public class PrefixCommand
extends Command {
    public PrefixCommand() {
        super("prefix", ".prefix <\u0441\u0438\u043c\u0432\u043e\u043b>", "\u0418\u0437\u043c\u0435\u043d\u0438\u0442\u044c \u043f\u0440\u0435\u0444\u0438\u043a\u0441 \u043a\u043e\u043c\u0430\u043d\u0434");
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 0 || args[0].isEmpty()) {
            char cur = Client.get().commandManager().getPrefix();
            ChatUtils.addChatMessage("\u00a77\u0422\u0435\u043a\u0443\u0449\u0438\u0439 \u043f\u0440\u0435\u0444\u0438\u043a\u0441: \u00a7a" + cur);
            ChatUtils.addChatMessage("\u00a77\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: " + cur + "prefix <\u0441\u0438\u043c\u0432\u043e\u043b>");
            return;
        }
        char newPrefix = args[0].charAt(0);
        Client.get().commandManager().setPrefix(newPrefix);
        ChatUtils.addChatMessage("\u00a77\u041f\u0440\u0435\u0444\u0438\u043a\u0441 \u0438\u0437\u043c\u0435\u043d\u0451\u043d \u043d\u0430: \u00a7a" + newPrefix);
    }

    @Override
    public List<String> getSuggestions(String subPrefix) {
        return List.of();
    }
}


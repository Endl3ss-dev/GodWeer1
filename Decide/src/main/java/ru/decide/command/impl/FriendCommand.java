/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.command.impl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.Client;
import ru.decide.command.Command;
import ru.decide.utils.math.ChatUtils;

@Environment(value=EnvType.CLIENT)
public class FriendCommand
extends Command {
    private static final List<String> SUBCOMMANDS = List.of("add", "remove", "list");

    public FriendCommand() {
        super("friend", ".friend <add|remove|list> [name]", "\u0421\u043f\u0438\u0441\u043e\u043a \u0434\u0440\u0443\u0437\u0435\u0439");
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 0) {
            this.showHelp();
            return;
        }
        switch (args[0].toLowerCase()) {
            case "add": {
                if (args.length < 2) {
                    ChatUtils.addChatMessage("\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: .friend add <\u043d\u0438\u043a>");
                    return;
                }
                String name = args[1];
                boolean ok = Client.get().friendManager().add(name);
                if (ok) {
                    ChatUtils.addChatMessage("\u00a7a" + name + "\u00a7r \u0434\u043e\u0431\u0430\u0432\u043b\u0435\u043d \u0432 \u0434\u0440\u0443\u0437\u044c\u044f");
                    break;
                }
                ChatUtils.addChatMessage("\u00a77" + name + " \u0443\u0436\u0435 \u0432 \u0434\u0440\u0443\u0437\u044c\u044f\u0445");
                break;
            }
            case "remove": {
                if (args.length < 2) {
                    ChatUtils.addChatMessage("\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: .friend remove <\u043d\u0438\u043a>");
                    return;
                }
                String name = args[1];
                boolean ok = Client.get().friendManager().remove(name);
                if (ok) {
                    ChatUtils.addChatMessage("\u00a7c" + name + "\u00a7r \u0443\u0434\u0430\u043b\u0451\u043d \u0438\u0437 \u0434\u0440\u0443\u0437\u0435\u0439");
                    break;
                }
                ChatUtils.addChatMessage("\u00a77" + name + " \u043d\u0435 \u0432 \u0434\u0440\u0443\u0437\u044c\u044f\u0445");
                break;
            }
            case "list": {
                Set<String> list = Client.get().friendManager().getFriends();
                if (list.isEmpty()) {
                    ChatUtils.addChatMessage("\u00a77\u0421\u043f\u0438\u0441\u043e\u043a \u0434\u0440\u0443\u0437\u0435\u0439 \u043f\u0443\u0441\u0442");
                    break;
                }
                String names = list.stream().collect(Collectors.joining("\u00a77, \u00a7a"));
                ChatUtils.addChatMessage("\u00a77\u0414\u0440\u0443\u0437\u044c\u044f \u00a78[" + list.size() + "\u00a78]\u00a7r: \u00a7a" + names);
                break;
            }
            default: {
                this.showHelp();
            }
        }
    }

    @Override
    public List<String> getSuggestions(String subPrefix) {
        return SUBCOMMANDS.stream().filter(s -> s.startsWith(subPrefix.toLowerCase())).collect(Collectors.toList());
    }

    private void showHelp() {
        ChatUtils.addChatMessage("\u00a77.friend add/remove \u00a7f<\u043d\u0438\u043a> \u00a78| \u00a77.friend list");
    }
}


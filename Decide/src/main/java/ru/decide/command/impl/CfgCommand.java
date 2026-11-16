/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.command.impl;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.Client;
import ru.decide.command.Command;
import ru.decide.utils.math.ChatUtils;

@Environment(value=EnvType.CLIENT)
public class CfgCommand
extends Command {
    private static final List<String> SUBCOMMANDS = List.of("save", "load", "list", "dir");

    public CfgCommand() {
        super("cfg", ".cfg <save|load|list|dir> [name]", "\u041a\u043e\u043d\u0444\u0438\u0433\u0438");
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 0) {
            this.showHelp();
            return;
        }
        switch (args[0].toLowerCase()) {
            case "save": {
                if (args.length < 2) {
                    ChatUtils.addChatMessage("\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: .cfg save <\u043d\u0430\u0437\u0432\u0430\u043d\u0438\u0435>");
                    return;
                }
                Client.get().configManager().save(args[1]);
                ChatUtils.addChatMessage("\u041a\u043e\u043d\u0444\u0438\u0433 \u00a7a" + args[1] + "\u00a7r \u0441\u043e\u0445\u0440\u0430\u043d\u0451\u043d");
                break;
            }
            case "load": {
                if (args.length < 2) {
                    ChatUtils.addChatMessage("\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: .cfg load <\u043d\u0430\u0437\u0432\u0430\u043d\u0438\u0435>");
                    return;
                }
                boolean ok = Client.get().configManager().load(args[1]);
                if (ok) {
                    ChatUtils.addChatMessage("\u041a\u043e\u043d\u0444\u0438\u0433 \u00a7a" + args[1] + "\u00a7r \u0437\u0430\u0433\u0440\u0443\u0436\u0435\u043d");
                    break;
                }
                ChatUtils.addChatMessage("\u00a7c\u041a\u043e\u043d\u0444\u0438\u0433 \u00a7r" + args[1] + "\u00a7c \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d");
                break;
            }
            case "list": {
                List<String> configs = Client.get().configManager().listConfigs();
                if (configs.isEmpty()) {
                    ChatUtils.addChatMessage("\u00a77\u041d\u0435\u0442 \u0441\u043e\u0445\u0440\u0430\u043d\u0451\u043d\u043d\u044b\u0445 \u043a\u043e\u043d\u0444\u0438\u0433\u043e\u0432");
                    break;
                }
                ChatUtils.addChatMessage("\u00a77\u041a\u043e\u043d\u0444\u0438\u0433\u0438 \u00a78[" + configs.size() + "\u00a78]\u00a7r: " + String.join((CharSequence)"\u00a77, \u00a7r", configs));
                break;
            }
            case "dir": {
                String path = Client.get().configManager().getConfigDir().toAbsolutePath().toString();
                try {
                    new ProcessBuilder("explorer.exe", path).start();
                }
                catch (IOException e) {
                    ChatUtils.addChatMessage("\u00a7c\u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u043e\u0442\u043a\u0440\u044b\u0442\u044c \u043f\u0430\u043f\u043a\u0443: " + path);
                }
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
        ChatUtils.addChatMessage("\u00a77.cfg save/load \u00a7f<name> \u00a78| \u00a77.cfg list \u00a78| \u00a77.cfg dir");
    }
}


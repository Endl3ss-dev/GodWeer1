/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.command;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.command.Command;
import ru.decide.command.impl.BindCommand;
import ru.decide.command.impl.CfgCommand;
import ru.decide.command.impl.FriendCommand;
import ru.decide.command.impl.GpsCommand;
import ru.decide.command.impl.PrefixCommand;
import ru.decide.command.impl.WayCommand;
import ru.decide.utils.math.ChatUtils;

@Environment(value=EnvType.CLIENT)
public class CommandManager {
    private char prefix = (char)46;
    private final Map<String, Command> commands = new LinkedHashMap<String, Command>();

    public void init() {
        this.register(new CfgCommand());
        this.register(new FriendCommand());
        this.register(new PrefixCommand());
        this.register(new BindCommand());
        this.register(new GpsCommand());
        this.register(new WayCommand());
    }

    public void register(Command command) {
        this.commands.put(command.getName().toLowerCase(), command);
    }

    public char getPrefix() {
        return this.prefix;
    }

    public void setPrefix(char prefix) {
        this.prefix = prefix;
    }

    public Collection<String> getCommandNames() {
        return this.commands.keySet();
    }

    public boolean handleMessage(String message) {
        if (message.isEmpty() || message.charAt(0) != this.prefix) {
            return false;
        }
        String[] parts = message.substring(1).trim().split("\\s+");
        if (parts.length == 0 || parts[0].isEmpty()) {
            this.showAll();
            return true;
        }
        String commandName = parts[0].toLowerCase();
        Command command = this.commands.get(commandName);
        if (command == null) {
            ChatUtils.addChatMessage("\u00a7c\u041d\u0435\u0438\u0437\u0432\u0435\u0441\u0442\u043d\u0430\u044f \u043a\u043e\u043c\u0430\u043d\u0434\u0430: \u00a7r" + commandName);
            this.showAll();
            return true;
        }
        command.execute(Arrays.copyOfRange(parts, 1, parts.length));
        return true;
    }

    public List<String> getSuggestions(String input) {
        if (input.isEmpty() || input.charAt(0) != this.prefix) {
            return Collections.emptyList();
        }
        String without = input.substring(1);
        if (!without.contains(" ")) {
            return this.commands.entrySet().stream().filter(e -> ((String)e.getKey()).startsWith(without.toLowerCase())).map(e -> this.prefix + (String)e.getKey()).collect(Collectors.toList());
        }
        String[] parts = without.split(" ", 2);
        String cmdName = parts[0].toLowerCase();
        String subPrefix = parts.length > 1 ? parts[1] : "";
        Command cmd = this.commands.get(cmdName);
        if (cmd == null) {
            return Collections.emptyList();
        }
        return cmd.getSuggestions(subPrefix).stream().map(s -> this.prefix + cmdName + " " + s).collect(Collectors.toList());
    }

    private void showAll() {
        String list = this.commands.keySet().stream().map(n -> "\u00a7a" + this.prefix + n).collect(Collectors.joining("\u00a77, "));
        ChatUtils.addChatMessage("\u00a77\u041a\u043e\u043c\u0430\u043d\u0434\u044b: " + list);
    }
}


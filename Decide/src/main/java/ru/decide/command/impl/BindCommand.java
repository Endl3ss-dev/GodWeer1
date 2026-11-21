/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_3675
 *  net.minecraft.class_3675$class_306
 *  net.minecraft.class_3675$class_307
 */
package ru.decide.command.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3675;
import ru.decide.Client;
import ru.decide.command.Command;
import ru.decide.module.api.Module;
import ru.decide.utils.math.ChatUtils;

@Environment(value=EnvType.CLIENT)
public class BindCommand
extends Command {
    private static final List<String> KEY_SUGGESTIONS;

    public BindCommand() {
        super("bind", ".bind <\u043c\u043e\u0434\u0443\u043b\u044c> <\u043a\u043b\u0430\u0432\u0438\u0448\u0430|none>", "\u0411\u0438\u043d\u0434\u0438\u0442\u044c \u043c\u043e\u0434\u0443\u043b\u044c \u043d\u0430 \u043a\u043b\u0430\u0432\u0438\u0448\u0443");
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 0) {
            this.showHelp();
            return;
        }
        if (args[0].equalsIgnoreCase("list")) {
            List bound = Client.get().moduleManager().values().stream().filter(m -> m.getKey() > 0).collect(Collectors.toList());
            if (bound.isEmpty()) {
                ChatUtils.addChatMessage("\u00a77\u041d\u0438 \u043e\u0434\u0438\u043d \u043c\u043e\u0434\u0443\u043b\u044c \u043d\u0435 \u0437\u0430\u0431\u0438\u043d\u0436\u0435\u043d");
            } else {
                String names = bound.stream().map(m -> "\u00a7a" + m.getName() + "\u00a77[" + BindCommand.keyName(m.getKey()) + "\u00a77]").collect(Collectors.joining("\u00a77, "));
                ChatUtils.addChatMessage("\u00a77\u0411\u0438\u043d\u0434\u044b \u00a78[" + bound.size() + "\u00a78]\u00a7r: " + names);
            }
            return;
        }
        Module module = Client.get().moduleManager().getModule(args[0]);
        if (module == null) {
            ChatUtils.addChatMessage("\u00a7c\u041c\u043e\u0434\u0443\u043b\u044c \u00a7f" + args[0] + "\u00a7c \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d");
            return;
        }
        if (args.length < 2) {
            if (module.getKey() > 0) {
                ChatUtils.addChatMessage("\u00a77" + module.getName() + " \u0437\u0430\u0431\u0438\u043d\u0436\u0435\u043d \u043d\u0430 \u00a7a" + BindCommand.keyName(module.getKey()));
            } else {
                ChatUtils.addChatMessage("\u00a77" + module.getName() + " \u043d\u0435 \u0437\u0430\u0431\u0438\u043d\u0436\u0435\u043d");
            }
            return;
        }
        String keyArg = args[1].toLowerCase();
        if (keyArg.equals("none") || keyArg.equals("reset") || keyArg.equals("-")) {
            module.setKey(-1);
            ChatUtils.addChatMessage("\u00a77\u0411\u0438\u043d\u0434 \u00a7f" + module.getName() + "\u00a77 \u0441\u0431\u0440\u043e\u0448\u0435\u043d");
            return;
        }
        int code = BindCommand.parseKey(keyArg);
        if (code <= 0) {
            ChatUtils.addChatMessage("\u00a7c\u041d\u0435\u0438\u0437\u0432\u0435\u0441\u0442\u043d\u0430\u044f \u043a\u043b\u0430\u0432\u0438\u0448\u0430: \u00a7f" + args[1]);
            return;
        }
        module.setKey(code);
        ChatUtils.addChatMessage("\u00a7a" + module.getName() + "\u00a7r \u0437\u0430\u0431\u0438\u043d\u0436\u0435\u043d \u043d\u0430 \u00a7a" + BindCommand.keyName(code));
    }

    private static int parseKey(String name) {
        try {
            class_3675.class_306 key = class_3675.method_15981((String)("key.keyboard." + name));
            return key.method_1444();
        }
        catch (Exception e) {
            return -1;
        }
    }

    private static String keyName(int code) {
        try {
            return class_3675.class_307.field_1668.method_1447(code).method_27445().getString();
        }
        catch (Exception e) {
            return "?";
        }
    }

    @Override
    public List<String> getSuggestions(String subPrefix) {
        String[] parts = subPrefix.split(" ", 2);
        if (parts.length <= 1) {
            String p = parts[0].toLowerCase();
            ArrayList<String> out = new ArrayList<String>();
            if ("list".startsWith(p)) {
                out.add("list");
            }
            Client.get().moduleManager().values().stream().map(Module::getName).filter(n -> n.toLowerCase().startsWith(p)).forEach(out::add);
            return out;
        }
        String moduleName = parts[0];
        String keyPrefix = parts[1].toLowerCase();
        return KEY_SUGGESTIONS.stream().filter(k -> k.startsWith(keyPrefix)).map(k -> moduleName + " " + k).collect(Collectors.toList());
    }

    private void showHelp() {
        ChatUtils.addChatMessage("\u00a77.bind \u00a7f<\u043c\u043e\u0434\u0443\u043b\u044c> <\u043a\u043b\u0430\u0432\u0438\u0448\u0430>\u00a78 | \u00a77.bind \u00a7f<\u043c\u043e\u0434\u0443\u043b\u044c> none\u00a78 | \u00a77.bind list");
    }

    static {
        char c;
        KEY_SUGGESTIONS = new ArrayList<String>();
        KEY_SUGGESTIONS.add("none");
        for (c = 'a'; c <= 'z'; c = (char)(c + '\u0001')) {
            KEY_SUGGESTIONS.add(String.valueOf(c));
        }
        for (c = '0'; c <= '9'; c = (char)(c + '\u0001')) {
            KEY_SUGGESTIONS.add(String.valueOf(c));
        }
        KEY_SUGGESTIONS.add("space");
        KEY_SUGGESTIONS.add("left.shift");
        KEY_SUGGESTIONS.add("right.shift");
        KEY_SUGGESTIONS.add("left.control");
        KEY_SUGGESTIONS.add("left.alt");
        for (int i = 1; i <= 12; ++i) {
            KEY_SUGGESTIONS.add("f" + i);
        }
    }
}


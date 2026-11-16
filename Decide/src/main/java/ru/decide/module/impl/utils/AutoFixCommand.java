/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_124
 *  net.minecraft.class_2561
 *  net.minecraft.class_310
 *  net.minecraft.class_5250
 */
package ru.decide.module.impl.utils;

import java.util.Locale;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_124;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_5250;
import ru.decide.Client;
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.module.api.ModuleInfo;
import ru.decide.module.api.settings.impl.BooleanSetting;

@ModuleInfo(name="Auto Fix Command", desc="\u0418\u0441\u043f\u0440\u0430\u0432\u043b\u044f\u0435\u0442 \u043a\u043e\u043c\u0430\u043d\u0434\u044b, \u043d\u0430\u0431\u0440\u0430\u043d\u043d\u044b\u0435 \u0432 \u0440\u0443\u0441\u0441\u043a\u043e\u0439 \u0440\u0430\u0441\u043a\u043b\u0430\u0434\u043a\u0435", category=Category.UTILITIES)
@Environment(value=EnvType.CLIENT)
public class AutoFixCommand
extends Module {
    private static final Map<Character, Character> RU_TO_EN = Map.ofEntries(Map.entry(Character.valueOf('\u0439'), Character.valueOf('q')), Map.entry(Character.valueOf('\u0446'), Character.valueOf('w')), Map.entry(Character.valueOf('\u0443'), Character.valueOf('e')), Map.entry(Character.valueOf('\u043a'), Character.valueOf('r')), Map.entry(Character.valueOf('\u0435'), Character.valueOf('t')), Map.entry(Character.valueOf('\u043d'), Character.valueOf('y')), Map.entry(Character.valueOf('\u0433'), Character.valueOf('u')), Map.entry(Character.valueOf('\u0448'), Character.valueOf('i')), Map.entry(Character.valueOf('\u0449'), Character.valueOf('o')), Map.entry(Character.valueOf('\u0437'), Character.valueOf('p')), Map.entry(Character.valueOf('\u0445'), Character.valueOf('[')), Map.entry(Character.valueOf('\u044a'), Character.valueOf(']')), Map.entry(Character.valueOf('\u0444'), Character.valueOf('a')), Map.entry(Character.valueOf('\u044b'), Character.valueOf('s')), Map.entry(Character.valueOf('\u0432'), Character.valueOf('d')), Map.entry(Character.valueOf('\u0430'), Character.valueOf('f')), Map.entry(Character.valueOf('\u043f'), Character.valueOf('g')), Map.entry(Character.valueOf('\u0440'), Character.valueOf('h')), Map.entry(Character.valueOf('\u043e'), Character.valueOf('j')), Map.entry(Character.valueOf('\u043b'), Character.valueOf('k')), Map.entry(Character.valueOf('\u0434'), Character.valueOf('l')), Map.entry(Character.valueOf('\u0436'), Character.valueOf(';')), Map.entry(Character.valueOf('\u044d'), Character.valueOf('\'')), Map.entry(Character.valueOf('\u044f'), Character.valueOf('z')), Map.entry(Character.valueOf('\u0447'), Character.valueOf('x')), Map.entry(Character.valueOf('\u0441'), Character.valueOf('c')), Map.entry(Character.valueOf('\u043c'), Character.valueOf('v')), Map.entry(Character.valueOf('\u0438'), Character.valueOf('b')), Map.entry(Character.valueOf('\u0442'), Character.valueOf('n')), Map.entry(Character.valueOf('\u044c'), Character.valueOf('m')), Map.entry(Character.valueOf('\u0431'), Character.valueOf(',')), Map.entry(Character.valueOf('\u044e'), Character.valueOf('.')), Map.entry(Character.valueOf('\u0451'), Character.valueOf('`')));
    private static AutoFixCommand instance;
    public final BooleanSetting serverCommands = new BooleanSetting((Module)this, "\u0421\u0435\u0440\u0432\u0435\u0440 \u043a\u043e\u043c\u0430\u043d\u0434\u044b", true);
    public final BooleanSetting clientCommands = new BooleanSetting((Module)this, "\u041a\u043b\u0438\u0435\u043d\u0442 \u043a\u043e\u043c\u0430\u043d\u0434\u044b", true);
    public final BooleanSetting notify = new BooleanSetting((Module)this, "\u041f\u043e\u043a\u0430\u0437\u044b\u0432\u0430\u0442\u044c \u0438\u0441\u043f\u0440\u0430\u0432\u043b\u0435\u043d\u0438\u0435", true);

    public AutoFixCommand() {
        instance = this;
    }

    public static AutoFixCommand getActive() {
        return instance != null && instance.isEnabled() ? instance : null;
    }

    public static String processChatInput(String message) {
        AutoFixCommand corrector = AutoFixCommand.getActive();
        if (corrector == null || message == null || message.isEmpty()) {
            return message;
        }
        if (!message.startsWith("/")) {
            DotCorrection dotCorrection = AutoFixCommand.correctDotCommand(message);
            if (dotCorrection.type() == DotCorrection.Type.CLIENT) {
                char prefix = AutoFixCommand.prefixChar();
                String corrected = prefix + dotCorrection.command();
                AutoFixCommand.notifyCorrection(message, corrected);
                return corrected;
            }
            if (dotCorrection.type() == DotCorrection.Type.SERVER) {
                String corrected = "/" + dotCorrection.command();
                AutoFixCommand.notifyCorrection(message, corrected);
                return corrected;
            }
            return message;
        }
        if (!corrector.serverCommands.getValue().booleanValue()) {
            return message;
        }
        String body = message.substring(1);
        String corrected = AutoFixCommand.correctServerCommand(body);
        if (!corrected.equals(body)) {
            AutoFixCommand.notifyCorrection(message, "/" + corrected);
            return "/" + corrected;
        }
        return message;
    }

    public static String processServerCommand(String command) {
        AutoFixCommand corrector = AutoFixCommand.getActive();
        if (corrector == null || command == null || command.isEmpty() || !corrector.serverCommands.getValue().booleanValue()) {
            return command;
        }
        String corrected = AutoFixCommand.correctServerCommand(command);
        if (!corrected.equals(command)) {
            AutoFixCommand.notifyCorrection("/" + command, "/" + corrected);
            return corrected;
        }
        return command;
    }

    public static DotCorrection correctDotCommand(String message) {
        String serverCommand;
        String clientCommand;
        String usedPrefix;
        AutoFixCommand corrector = AutoFixCommand.getActive();
        if (corrector == null || message == null || message.length() <= 1) {
            return DotCorrection.none(message);
        }
        char prefix = AutoFixCommand.prefixChar();
        String prefixStr = String.valueOf(prefix);
        boolean startsWithPrefix = message.startsWith(prefixStr);
        boolean startsWithDot = message.startsWith(".");
        if (!startsWithPrefix && !startsWithDot) {
            return DotCorrection.none(message);
        }
        String string = usedPrefix = startsWithPrefix ? prefixStr : ".";
        if (message.length() <= usedPrefix.length()) {
            return DotCorrection.none(message);
        }
        if (AutoFixCommand.isIrcCommand(message)) {
            return DotCorrection.none(message.substring(usedPrefix.length()));
        }
        String body = message.substring(usedPrefix.length());
        if (AutoFixCommand.isKnownClientCommand(AutoFixCommand.firstToken(body))) {
            return DotCorrection.none(body);
        }
        if (corrector.clientCommands.getValue().booleanValue() && !(clientCommand = AutoFixCommand.correctClientCommand(body)).equals(body)) {
            return DotCorrection.client(clientCommand);
        }
        if (corrector.serverCommands.getValue().booleanValue() && !(serverCommand = AutoFixCommand.correctServerCommand(body)).equals(body)) {
            return DotCorrection.server(serverCommand);
        }
        return DotCorrection.none(body);
    }

    public static String correctServerCommand(String command) {
        if (command == null || command.isEmpty()) {
            return command;
        }
        if (AutoFixCommand.isAhSearchCommand(command)) {
            return AutoFixCommand.fixAhSearchCommand(command);
        }
        String converted = AutoFixCommand.convertRuToEn(command);
        if (converted.equals(command)) {
            return command;
        }
        String originalLabel = AutoFixCommand.firstToken(command);
        String convertedLabel = AutoFixCommand.firstToken(converted);
        return convertedLabel.equals(originalLabel) ? command : converted;
    }

    public static String correctClientCommand(String command) {
        if (command == null || command.isEmpty()) {
            return command;
        }
        String converted = AutoFixCommand.convertRuToEn(command);
        if (converted.equals(command)) {
            return command;
        }
        String label = AutoFixCommand.firstToken(converted).toLowerCase(Locale.ROOT);
        return !AutoFixCommand.isKnownClientCommand(label) ? command : converted;
    }

    public static String convertRuToEn(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        StringBuilder result = new StringBuilder(text.length());
        for (int i = 0; i < text.length(); ++i) {
            char current = text.charAt(i);
            Character mapped = RU_TO_EN.get(Character.valueOf(Character.toLowerCase(current)));
            if (mapped == null) {
                result.append(current);
                continue;
            }
            if (Character.isUpperCase(current)) {
                result.append(Character.toUpperCase(mapped.charValue()));
                continue;
            }
            result.append(mapped);
        }
        return result.toString();
    }

    public static void notifyCorrection(String from, String to) {
        AutoFixCommand corrector = AutoFixCommand.getActive();
        if (corrector == null || !corrector.notify.getValue().booleanValue()) {
            return;
        }
        class_310 client = class_310.method_1551();
        if (client.field_1724 == null || client.field_1705 == null) {
            return;
        }
        class_5250 message = class_2561.method_43470((String)"AutoFixCommand: ").method_27692(class_124.field_1061).method_10852((class_2561)class_2561.method_43470((String)from).method_27692(class_124.field_1068)).method_10852((class_2561)class_2561.method_43470((String)" -> ").method_27692(class_124.field_1054)).method_10852((class_2561)class_2561.method_43470((String)to).method_27692(class_124.field_1060));
        client.field_1724.method_7353((class_2561)message, false);
    }

    private static char prefixChar() {
        try {
            return Client.get().commandManager().getPrefix();
        }
        catch (Throwable ignored) {
            return '.';
        }
    }

    private static boolean isKnownClientCommand(String label) {
        if (label == null || label.isBlank()) {
            return false;
        }
        try {
            String clean = label.toLowerCase(Locale.ROOT);
            for (String known : Client.get().commandManager().getCommandNames()) {
                if (!clean.equals(known.toLowerCase(Locale.ROOT))) continue;
                return true;
            }
        }
        finally {
            return false;
        }
        {
        }
    }

    private static boolean isAhSearchCommand(String command) {
        String body = command.startsWith("/") ? command.substring(1) : command;
        String[] tokens = body.stripLeading().split("\\s+", 3);
        if (tokens.length < 2) {
            return false;
        }
        String first = tokens[0].toLowerCase(Locale.ROOT);
        String second = tokens[1].toLowerCase(Locale.ROOT);
        boolean firstMatches = first.equals("\u0444\u0440") || first.equals("ah");
        boolean secondMatches = second.equals("\u044b\u0443\u0444\u043a\u0441\u0440") || second.equals("search");
        return firstMatches && secondMatches;
    }

    private static String fixAhSearchCommand(String command) {
        boolean hasSlash = command.startsWith("/");
        String body = hasSlash ? command.substring(1) : command;
        String[] tokens = body.stripLeading().split("\\s+", 3);
        String prefix = hasSlash ? "/ah search" : "ah search";
        return tokens.length >= 3 ? prefix + " " + tokens[2] : prefix;
    }

    private static String firstToken(String text) {
        String trimmed = text.stripLeading();
        int space = trimmed.indexOf(32);
        return space == -1 ? trimmed : trimmed.substring(0, space);
    }

    private static boolean isIrcCommand(String message) {
        String lower = message.toLowerCase(Locale.ROOT);
        return lower.equals(".irc") || lower.startsWith(".irc ");
    }

    @Environment(value=EnvType.CLIENT)
    public record DotCorrection(Type type, String command) {
        public static DotCorrection none(String command) {
            return new DotCorrection(Type.NONE, command);
        }

        public static DotCorrection client(String command) {
            return new DotCorrection(Type.CLIENT, command);
        }

        public static DotCorrection server(String command) {
            return new DotCorrection(Type.SERVER, command);
        }

        @Environment(value=EnvType.CLIENT)
        public static enum Type {
            NONE,
            CLIENT,
            SERVER;

        }
    }
}


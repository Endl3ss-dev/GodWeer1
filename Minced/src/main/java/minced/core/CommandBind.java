/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 */
package minced.core;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import minced.core.ChatUtil;
import minced.core.ClientCommand;
import minced.core.Minced;
import minced.core.Module;
import minced.enums.KeyCode;
import minced.enums.TOGGLE;
import net.minecraft.command.CommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import sLM._4ZXYEo;
import sg.ec.sb2;

public class CommandBind
extends ClientCommand {
    static String cfr_renamed_100;
    static String cfr_renamed_101;

    static String field7386;
    static String field7387 = "add";
    static String field7388 = "args";
    static String field7389 = "remove";
    static String field7390 = "module";
    static String field7391 = "list";
    static String field7392 = "clear";
    static String field7393;
    static String field7394;
    static String field7395;
    static String field7396;
    static String field7397;
    static String field7398;
    static String field7399;
    static String field7400;
    static String field7401;
    static String field7402;
    static String field7403;
    static String field7404;
    static String field7405;
    static String field7406;
    static String field7407;
    static String field7408;
    static String field7409;
    static String field7410;
    static String field7411;
    static String field7412;
    static String field7413;
    static String field7414;
    static String field7415;
    static String field7416;

    private String I(String param1) {
        return param1.replaceAll(field7386, "");
    }

    public static LiteralArgumentBuilder Ж(String name) {
        return ClientCommand.method420(name);
    }

    public static RequiredArgumentBuilder Ж(String name, ArgumentType<?> type) {
        return ClientCommand.method417(name, type);
    }

    @Override
    public void run(LiteralArgumentBuilder param12) {
        param12.executes(param1 -> this.method419(param1));
        param12.then(CommandBind.Ж((String)field7387).then(CommandBind.Ж((String)field7388, (ArgumentType)StringArgumentType.greedyString()).suggests(this::method1815).executes(this::method1816)));
        param12.then(CommandBind.Ж((String)field7389).then(CommandBind.Ж((String)field7390, (ArgumentType)StringArgumentType.greedyString()).suggests(this::O).executes(this::method1818)));
        param12.then(CommandBind.Ж((String)field7391).executes(this::method1822));
        param12.then(CommandBind.Ж((String)field7392).executes(this::method1813));
    }

    public int method1813(CommandContext param1) {
        boolean v2 = Minced.getInstance().getModuleManager().method178().stream().anyMatch(param0 -> param0.getKey() != -100);
        if (!v2) {
            MutableText v3 = Text.literal((String)field7393).setStyle(Style.EMPTY.withColor(Formatting.GRAY));
            ChatUtil.method3308(v3, new Object[0]);
            return 1;
        }
        Minced.getInstance().getModuleManager().method178().forEach(param0 -> param0.setKey(-100));
        MutableText v3 = Text.literal((String)field7394).setStyle(Style.EMPTY.withColor(Formatting.GRAY));
        ChatUtil.method3308(v3, new Object[0]);
        return 1;
    }

    private Module method1814(minced.manager.Module param1, String param2) {
        Module v3 = param1.method184(param2).orElse(null);
        if (v3 != null) {
            return v3;
        }
        String v4 = this.I(param2);
        for (Module v6 : param1.method178()) {
            if (!this.I(v6.getName()).equalsIgnoreCase(v4)) continue;
            return v6;
        }
        return null;
    }

    private CompletableFuture method1815(CommandContext param12, SuggestionsBuilder param2) {
        String[] v10;
        String v5;
        String v3 = param2.getInput();
        String v4 = v3.toLowerCase(Locale.ROOT);
        int v6 = v4.indexOf(v5 = ((String)((Object)_4ZXYEo.Ця("makeConcatWithConstants", new Object[]{}, (String)Minced.getInstance().getCommandManager().method74()))).toLowerCase(Locale.ROOT));
        String v7 = v6 == -1 ? "" : v3.substring(v6 + v5.length());
        boolean v8 = v7.endsWith(field7395);
        String v9 = v7.trim();
        String[] stringArray = v10 = v9.isEmpty() ? new String[]{} : v9.split(field7396);
        int v11 = v10.length == 0 ? v3.length() : (v8 ? v3.length() : v3.length() - v10[v10.length - 1].length());
        SuggestionsBuilder v12 = new SuggestionsBuilder(v3, v11);
        if (v10.length == 0 || v10.length == 1 && !v8) {
            String v13 = v10.length == 0 ? "" : this.I(v10[0]);
            List<String> v14 = Minced.getInstance().getModuleManager().method178().stream().map(Module::getName).map(this::I).filter(param1 -> param1.toLowerCase(Locale.ROOT).startsWith(v13.toLowerCase(Locale.ROOT))).collect(Collectors.toList());
            for (String v16 : v14) {
                v12.suggest(v16);
            }
            return v12.buildFuture();
        }
        if (v10.length == 1 || v10.length == 2 && !v8) {
            String v13 = v10.length == 2 ? v10[1].toUpperCase(Locale.ROOT) : "";
            for (KeyCode v17 : KeyCode.values()) {
                if (!v17.name().startsWith(v13)) continue;
                v12.suggest(v17.name());
            }
            return v12.buildFuture();
        }
        if (v10.length == 2 || !v8) {
            String v13;
            String string = v13 = v10.length >= 3 ? v10[2].toLowerCase(Locale.ROOT) : "";
            if (field7397.startsWith(v13)) {
                v12.suggest(field7398);
            }
            if (field7399.startsWith(v13)) {
                v12.suggest(field7400);
            }
            return v12.buildFuture();
        }
        return v12.buildFuture();
    }

    private int method1816(CommandContext param1) {
        TOGGLE v7;
        String v2 = ((String)param1.getArgument(field7401, String.class)).trim();
        if (v2.isEmpty()) {
            return this.method419(param1);
        }
        String[] v3 = v2.split(field7402);
        if (v3.length < 3) {
            return this.method419(param1);
        }
        String v4 = v3[v3.length - 1];
        String v5 = v3[v3.length - 2];
        String v6 = String.join((CharSequence)field7403, Arrays.asList(v3).subList(0, v3.length - 2));
        if (v4.equalsIgnoreCase(field7404)) {
            v7 = TOGGLE.HOLD;
        } else if (v4.equalsIgnoreCase(field7405)) {
            v7 = TOGGLE.TOGGLE;
        } else {
            return this.method419(param1);
        }
        KeyCode v8 = KeyCode.fromName(v5);
        if (v8 == null) {
            return this.method419(param1);
        }
        minced.manager.Module v9 = Minced.getInstance().getModuleManager();
        Module v10 = this.method1814(v9, v6);
        if (v10 == null) {
            return this.method419(param1);
        }
        v10.setKey(v8.getCode());
        v10.setToggleMode(v7);
        MutableText v11 = Text.literal((String)field7406).setStyle(Style.EMPTY.withColor(Formatting.GRAY));
        MutableText v12 = Text.literal((String)KeyCode.getDisplayName(v10.getKey())).setStyle(Style.EMPTY.withColor(Formatting.WHITE));
        MutableText v13 = Text.literal((String)field7407).setStyle(Style.EMPTY.withColor(Formatting.GRAY));
        MutableText v14 = Text.literal((String)v10.getName()).setStyle(Style.EMPTY.withColor(Formatting.WHITE));
        MutableText v15 = Text.literal((String)field7408).setStyle(Style.EMPTY.withColor(Formatting.GRAY));
        ChatUtil.method3308(Text.literal((String)"").append((Text)v11).append((Text)v12).append((Text)v13).append((Text)v14).append((Text)v15), new Object[0]);
        return 1;
    }

    private CompletableFuture O(CommandContext param1, SuggestionsBuilder param2) {
        List<String> v3 = Minced.getInstance().getModuleManager().method178().stream().map(Module::getName).map(this::I).collect(Collectors.toList());
        return CommandSource.suggestMatching(v3, (SuggestionsBuilder)param2);
    }

    public CommandBind() {
        super(field7409);
    }

    private int method1818(CommandContext param1) {
        String v2 = (String)param1.getArgument(field7410, String.class);
        minced.manager.Module v3 = Minced.getInstance().getModuleManager();
        Module v4 = this.method1814(v3, v2);
        if (v4 == null) {
            return this.method419(param1);
        }
        v4.setKey(-100);
        MutableText v5 = Text.literal((String)field7411).setStyle(Style.EMPTY.withColor(Formatting.GRAY));
        MutableText v6 = Text.literal((String)v4.getName()).setStyle(Style.EMPTY.withColor(Formatting.WHITE));
        MutableText v7 = Text.literal((String)field7412).setStyle(Style.EMPTY.withColor(Formatting.GRAY));
        ChatUtil.method3308(Text.literal((String)"").append((Text)v5).append((Text)v6).append((Text)v7), new Object[0]);
        return 1;
    }

    private int method1822(CommandContext param1) {
        List<Module> v2 = Minced.getInstance().getModuleManager().method178().stream().filter(param0 -> param0.getKey() != -100).collect(Collectors.toList());
        if (v2.isEmpty()) {
            MutableText v3 = Text.literal((String)field7413).setStyle(Style.EMPTY.withColor(Formatting.GRAY));
            ChatUtil.method3308(v3, new Object[0]);
            return 1;
        }
        for (Module v4 : v2) {
            String v5 = KeyCode.getDisplayName(v4.getKey());
            MutableText v6 = Text.literal((String)v4.getName()).setStyle(Style.EMPTY.withColor(Formatting.WHITE));
            MutableText v7 = Text.literal((String)field7414).setStyle(Style.EMPTY.withColor(Formatting.GRAY));
            MutableText v8 = Text.literal((String)v5).setStyle(Style.EMPTY.withColor(Formatting.WHITE));
            String v9_str = (String) _4ZXYEo.ЦН("makeConcatWithConstants", new Object[]{}, (String)Minced.getInstance().getCommandManager().method74(), (String)this.method416(), (String)this.I(v4.getName()));
            
            ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, v9_str);
            HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal((String)field7416).setStyle(Style.EMPTY.withColor(Formatting.RED)));
            Style style = Style.EMPTY
                .withColor(Formatting.RED)
                .withClickEvent(clickEvent)
                .withHoverEvent(hoverEvent);
            MutableText v10 = Text.literal((String)field7415).setStyle(style);
            
            ChatUtil.method3308(Text.literal((String)"").append((Text)v6).append((Text)v7).append((Text)v8).append((Text)v10), new Object[0]);
        }
        return 1;
    }

    static {
        field7401 = "args";
        field7402 = "\\s+";
        field7403 = " ";
        field7404 = "hold";
        field7405 = "toggle";
        field7406 = "Установлен бинд ";
        field7407 = " на модуль '";
        field7408 = "'!";
        field7410 = "module";
        field7411 = "Модуль '";
        field7412 = "' теперь не имеет бинда!";
        field7413 = "Список биндoв пуст!";
        field7414 = " клавиша: ";
        cfr_renamed_100 = "\u0001\u0001 remove \u0001";
        field7415 = " [Удалить]";
        field7416 = "Клик для удаления бинда";
        field7393 = "Список биндoв пуст!";
        field7394 = "Список Биндов очищен!";
        cfr_renamed_101 = "\u0001bind add ";
        field7395 = " ";
        field7396 = "\\s+";
        field7397 = "toggle";
        field7398 = "toggle";
        field7399 = "hold";
        field7400 = "hold";
        field7386 = "\\s+";
        field7409 = "bind";
    }
}



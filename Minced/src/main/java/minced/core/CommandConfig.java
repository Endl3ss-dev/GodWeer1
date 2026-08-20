/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 *  net.minecraft.command.CommandSource
 *  net.minecraft.text.ClickEvent
 *  net.minecraft.text.ClickEvent$Action
 *  net.minecraft.text.HoverEvent
 *  net.minecraft.text.HoverEvent$Action
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.Style
 *  net.minecraft.text.Text
 *  net.minecraft.util.Formatting
 *  sLM.4ZXYEo
 *  sg.ec.sb2
 */
package minced.core;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.io.File;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import minced.core.PathUtil;
import minced.core.ChatUtil;
import minced.core.ClientCommand;
import minced.core.Minced;
import net.minecraft.command.CommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import sLM._4ZXYEo;
import sg.ec.sb2;

public class CommandConfig
extends ClientCommand
implements ArgumentType {
    static String cfr_renamed_18;
    static String cfr_renamed_19;
    static String cfr_renamed_20;
    static String cfr_renamed_21;
    static String cfr_renamed_22;
    static String cfr_renamed_23;
    static String cfr_renamed_24;
    static String cfr_renamed_25;
    static String cfr_renamed_26;
    static String cfr_renamed_27;

    static String field7050;
    static String field7051;
    static String field7052;
    static String field7053;
    static String field7054;
    static String field7055;
    static String field7056;
    static String field7057;
    static String field7058 = "config";
    static String field7059 = "cfg";
    static String field7060;
    static String field7061;
    static String field7062;
    static String field7063;
    static String field7064;
    static String field7065;
    static String field7066 = "save";
    static String field7067 = "name";
    static String field7068 = "load";
    static String field7069 = "name";
    static String field7070 = "remove";
    static String field7071 = "name";
    static String field7072 = "list";
    static String field7073 = "dir";
    static String field7074 = "clear";
    static String field7075 = "reset";
    static String field7076;

    public CompletableFuture listSuggestions(CommandContext param1, SuggestionsBuilder param2) {
        List v3 = Minced.getInstance().getConfigManager().method1();
        return CommandSource.suggestMatching((Iterable)v3, (SuggestionsBuilder)param2);
    }

    public String parse(StringReader param1) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        String v2 = param1.readString();
        boolean v3 = Minced.getInstance().getConfigManager().method1().contains(v2);
        if (!v3) {
            throw new DynamicCommandExceptionType(param0 -> Text.literal((String)((Object)_4ZXYEo.фт("makeConcatWithConstants", new Object[]{}, (String)String.valueOf(param0))))).create((Object)v2);
        }
        return v2;
    }

    public CommandConfig() {
        super(field7058, field7059);
    }

    public static com.mojang.brigadier.builder.LiteralArgumentBuilder Ж(String name) {
        return ClientCommand.method420(name);
    }

    public static com.mojang.brigadier.builder.RequiredArgumentBuilder Ж(String name, com.mojang.brigadier.arguments.ArgumentType<?> type) {
        return ClientCommand.method417(name, type);
    }

    @Override
    public void run(LiteralArgumentBuilder param12) {
        param12.executes(param1 -> this.method419(param1));
        param12.then(CommandConfig.Ж((String)field7066).then(CommandConfig.Ж((String)field7067, (ArgumentType)StringArgumentType.word()).suggests((param0, param1) -> CommandSource.suggestMatching((Iterable)Minced.getInstance().getConfigManager().method1(), (SuggestionsBuilder)param1)).executes(param0 -> {
            String v1 = (String)param0.getArgument(field7060, String.class);
            Minced.getInstance().getConfigManager().method9(v1);
            MutableText v2 = Text.literal((String)field7061).setStyle(Style.EMPTY.withColor(Formatting.GRAY));
            MutableText v3 = Text.literal((String)((Object)_4ZXYEo.фм("makeConcatWithConstants", new Object[]{}, (String)v1))).setStyle(Style.EMPTY.withColor(Formatting.WHITE));
            MutableText v4 = Text.literal((String)field7062).setStyle(Style.EMPTY.withColor(Formatting.GRAY));
            ChatUtil.method3308(Text.literal((String)"").append((Text)v2).append((Text)v3).append((Text)v4), new Object[0]);
            return 1;
        })));
        param12.then(CommandConfig.Ж((String)field7068).then(CommandConfig.Ж((String)field7069, (ArgumentType)new CommandConfig()).executes(param1 -> {
            String v2 = (String)param1.getArgument(field7063, String.class);
            if (!Minced.getInstance().getConfigManager().method1().contains(v2)) {
                return this.method419(param1);
            }
            Minced.getInstance().getConfigManager().method31(v2);
            MutableText v3 = Text.literal((String)field7064).setStyle(Style.EMPTY.withColor(Formatting.GRAY));
            MutableText v4 = Text.literal((String)((Object)_4ZXYEo.фа("makeConcatWithConstants", new Object[]{}, (String)v2))).setStyle(Style.EMPTY.withColor(Formatting.WHITE));
            MutableText v5 = Text.literal((String)field7065).setStyle(Style.EMPTY.withColor(Formatting.GRAY));
            ChatUtil.method3308(Text.literal((String)"").append((Text)v3).append((Text)v4).append((Text)v5), new Object[0]);
            return 1;
        })));
        param12.then(CommandConfig.Ж((String)field7070).then(CommandConfig.Ж((String)field7071, (ArgumentType)new CommandConfig()).executes(param1 -> {
            String v2 = (String)param1.getArgument(field7051, String.class);
            if (!Minced.getInstance().getConfigManager().method1().contains(v2)) {
                return this.method419(param1);
            }
            Minced.getInstance().getConfigManager().method3(v2);
            MutableText v3 = Text.literal((String)field7052).setStyle(Style.EMPTY.withColor(Formatting.GRAY));
            MutableText v4 = Text.literal((String)((Object)_4ZXYEo.ф5("makeConcatWithConstants", new Object[]{}, (String)v2))).setStyle(Style.EMPTY.withColor(Formatting.WHITE));
            MutableText v5 = Text.literal((String)field7053).setStyle(Style.EMPTY.withColor(Formatting.GRAY));
            ChatUtil.method3308(Text.literal((String)"").append((Text)v3).append((Text)v4).append((Text)v5), new Object[0]);
            return 1;
        })));
        param12.then(CommandConfig.Ж((String)field7072).executes(param1 -> {
            List<String> v2 = (List<String>)Minced.getInstance().getConfigManager().method1();
            if (v2 == null || v2.isEmpty()) {
                MutableText v3 = Text.literal((String)field7054).setStyle(Style.EMPTY.withColor(Formatting.GRAY));
                ChatUtil.method3308(v3, new Object[0]);
                return 1;
            }
            MutableText v3 = Text.literal((String)field7055).setStyle(Style.EMPTY.withColor(Formatting.GRAY));
            ChatUtil.method3308(v3, new Object[0]);
            for (String v5 : v2) {
                MutableText v6 = Text.literal((String)v5).setStyle(Style.EMPTY.withColor(Formatting.WHITE));
                net.minecraft.text.ClickEvent clickEvent = new net.minecraft.text.ClickEvent(
                    net.minecraft.text.ClickEvent.Action.RUN_COMMAND, 
                    (String)((Object)_4ZXYEo.фэ("makeConcatWithConstants", new Object[]{}, (String)Minced.getInstance().getCommandManager().method74(), (String)this.method416(), (String)v5))
                );
                net.minecraft.text.HoverEvent hoverEvent = new net.minecraft.text.HoverEvent(
                    net.minecraft.text.HoverEvent.Action.SHOW_TEXT, 
                    Text.literal((String)((Object)_4ZXYEo.фо("makeConcatWithConstants", new Object[]{}, (String)v5)))
                );
                Style style = Style.EMPTY
                    .withColor(Formatting.GREEN)
                    .withClickEvent(clickEvent)
                    .withHoverEvent(hoverEvent);
                MutableText v7 = Text.literal((String)field7056).setStyle(style);

                net.minecraft.text.ClickEvent clickEvent2 = new net.minecraft.text.ClickEvent(
                    net.minecraft.text.ClickEvent.Action.RUN_COMMAND, 
                    (String)((Object)_4ZXYEo.фц("makeConcatWithConstants", new Object[]{}, (String)Minced.getInstance().getCommandManager().method74(), (String)this.method416(), (String)v5))
                );
                net.minecraft.text.HoverEvent hoverEvent2 = new net.minecraft.text.HoverEvent(
                    net.minecraft.text.HoverEvent.Action.SHOW_TEXT, 
                    Text.literal((String)((Object)_4ZXYEo.фУ("makeConcatWithConstants", new Object[]{}, (String)v5)))
                );
                Style style2 = Style.EMPTY
                    .withColor(Formatting.RED)
                    .withClickEvent(clickEvent2)
                    .withHoverEvent(hoverEvent2);
                MutableText v8 = Text.literal((String)field7057).setStyle(style2);

                ChatUtil.method3308(Text.literal((String)"").append((Text)v6).append((Text)v7).append((Text)v8), new Object[0]);
            }
            return 1;
        }));
        param12.then(CommandConfig.Ж((String)field7073).executes(param0 -> {
            try {
                File v1 = new File((String)((Object)_4ZXYEo.ф3("makeConcatWithConstants", new Object[]{}, (String)PathUtil.field7558)));
                Runtime.getRuntime().exec((String)((Object)_4ZXYEo.фв("makeConcatWithConstants", new Object[]{}, (String)v1.getAbsolutePath())));
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return 1;
        }));
        param12.then(CommandConfig.Ж((String)field7074).executes(param02 -> {
            List v1 = Minced.getInstance().getConfigManager().method1();
            v1.forEach(param0 -> Minced.getInstance().getConfigManager().method3((String)param0));
            MutableText v2 = Text.literal((String)field7050).setStyle(Style.EMPTY.withColor(Formatting.GRAY));
            ChatUtil.method3308(v2, new Object[0]);
            return 1;
        }));
        param12.then(CommandConfig.Ж((String)field7075).executes(param0 -> {
            Minced.getInstance().getConfigManager().method18();
            MutableText v1 = Text.literal((String)field7076).setStyle(Style.EMPTY.withColor(Formatting.GRAY));
            ChatUtil.method3308(v1, new Object[0]);
            return 1;
        }));
    }

    static {
        cfr_renamed_18 = "Конфиг с именем \u0001 не существует";
        field7076 = "Конфиг сброшен!";
        field7050 = "Список конфигов очищен!";
        cfr_renamed_19 = "\u0001custom\\";
        cfr_renamed_20 = "explorer \u0001";
        field7054 = "Список конфигураций пуст!";
        field7055 = "Список конфигов:";
        field7056 = " [Загрузить]";
        cfr_renamed_21 = "\u0001\u0001 load \u0001";
        cfr_renamed_22 = "Загрузить конфиг \u0001";
        field7057 = " [Удалить]";
        cfr_renamed_23 = "\u0001\u0001 remove \u0001";
        cfr_renamed_24 = "Удалить конфиг \u0001";
        field7051 = "name";
        field7052 = "Конфиг с именем ";
        cfr_renamed_25 = "\"\u0001\"";
        field7053 = " удалён!";
        field7063 = "name";
        field7064 = "Конфиг с именем ";
        cfr_renamed_26 = "\"\u0001\"";
        field7065 = " загружён!";
        field7060 = "name";
        field7061 = "Конфиг с именем ";
        cfr_renamed_27 = "\"\u0001\"";
        field7062 = " сохранён!";
    }
}




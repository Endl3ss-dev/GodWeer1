/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 */
package minced.core;

import com.mojang.brigadier.StringReader;
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
import minced.enums.KeyCode;
import net.minecraft.command.CommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import sLM._4ZXYEo;
import sg.ec.sb2;
import sg.ec.sr;

public class CommandMacro
extends ClientCommand
implements ArgumentType {
    static String cfr_renamed_98;
    static String cfr_renamed_99;

    static String field7358;
    static String field7359;
    static String field7360;
    static String field7361 = "add";
    static String field7362 = "args";
    static String field7363 = "remove";
    static String field7364 = "name";
    static String field7365 = "list";
    static String field7366 = "clear";
    static String field7367;
    static String field7368;
    static String field7369;
    static String field7370;
    static String field7371;
    static String field7372;
    static String field7373;
    static String field7374;
    static String field7375;
    static String field7376;
    static String field7377;
    static String field7378;
    static String field7379;
    static String field7380;
    static String field7381;
    static String field7382 = "macro";
    static String field7383 = "mc";
    static String field7384;
    static String field7385;

    public String parse(StringReader param1) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        return param1.readString();
    }

    public static LiteralArgumentBuilder Ж(String name) {
        return ClientCommand.method420(name);
    }

    public static RequiredArgumentBuilder Ж(String name, ArgumentType<?> type) {
        return ClientCommand.method417(name, type);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void run(LiteralArgumentBuilder param12) {
        param12.executes(param1 -> this.method419(param1));
        param12.then(CommandMacro.Ж((String)field7361).then(CommandMacro.Ж((String)field7362, (ArgumentType)StringArgumentType.greedyString()).suggests(this::suggestAdd).executes(this::executeAdd)));
        param12.then(CommandMacro.Ж((String)field7363).then(CommandMacro.Ж((String)field7364, (ArgumentType)StringArgumentType.word()).suggests(this::suggestMacroNames).executes(param0 -> {
            String v1 = (String)param0.getArgument(field7358, String.class);
            Minced.getInstance().getMacroManager().method1709(v1);
            MutableText v2_text = Text.literal((String)field7359).setStyle(Style.EMPTY.withColor(Formatting.GRAY)).append((Text)Text.literal((String)v1).setStyle(Style.EMPTY.withColor(Formatting.WHITE))).append((Text)Text.literal((String)field7360).setStyle(Style.EMPTY.withColor(Formatting.GRAY)));
            ChatUtil.method3308(v2_text, new Object[0]);
            return 1;
        })));
        param12.then(CommandMacro.Ж((String)field7365).executes(param1 -> {
            List<sr.ch> v2_list = (List<sr.ch>) Minced.getInstance().getMacroManager().method1708();
            if (v2_list.isEmpty()) {
                ChatUtil.method3308(Text.literal((String)field7373).setStyle(Style.EMPTY.withColor(Formatting.GRAY)), new Object[0]);
                return 1;
            }
            ChatUtil.method3308(Text.literal((String)field7374).setStyle(Style.EMPTY.withColor(Formatting.GRAY)), new Object[0]);
            for (sr.ch v4 : v2_list) {
                String v5 = KeyCode.getDisplayName(v4.l());
                MutableText v6 = Text.literal((String)field7375).setStyle(Style.EMPTY.withColor(Formatting.GRAY));
                MutableText v7 = Text.literal((String)((Object)v4.ДЧ())).setStyle(Style.EMPTY.withColor(Formatting.WHITE));
                MutableText v8 = Text.literal((String)field7376).setStyle(Style.EMPTY.withColor(Formatting.GRAY));
                MutableText v9 = Text.literal((String)v5).setStyle(Style.EMPTY.withColor(Formatting.WHITE));
                MutableText v10 = Text.literal((String)field7377).setStyle(Style.EMPTY.withColor(Formatting.GRAY));
                MutableText v11_item = Text.literal((String)((Object)v4.Д5())).setStyle(Style.EMPTY.withColor(Formatting.GREEN));
                String v12_call = (String) _4ZXYEo.д2("makeConcatWithConstants", new Object[]{}, (String)Minced.getInstance().getCommandManager().method74(), (String)this.method416(), (String)((Object)v4.ДЧ()));
                
                ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, v12_call);
                HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal((String)field7379).setStyle(Style.EMPTY.withColor(Formatting.RED)));
                Style style = Style.EMPTY
                    .withColor(Formatting.RED)
                    .withClickEvent(clickEvent)
                    .withHoverEvent(hoverEvent);
                MutableText v13 = Text.literal((String)field7378).setStyle(style);
                
                ChatUtil.method3308(Text.literal((String)"").append((Text)v6).append((Text)v7).append((Text)v8).append((Text)v9).append((Text)v10).append((Text)v11_item).append((Text)v13), new Object[0]);
            }
            ChatUtil.method3308(Text.literal((String)((Object)_4ZXYEo.дш("makeConcatWithConstants", new Object[]{}, (int)v2_list.size()))).setStyle(Style.EMPTY.withColor(Formatting.GRAY)), new Object[0]);
            return 1;
        }));
        param12.then(CommandMacro.Ж((String)field7366).executes(param0 -> {
            List<sr.ch> v1_list = (List<sr.ch>) Minced.getInstance().getMacroManager().method1708();
            if (v1_list.isEmpty()) {
                ChatUtil.method3308(Text.literal((String)field7384).setStyle(Style.EMPTY.withColor(Formatting.GRAY)), new Object[0]);
                return 1;
            }
            Minced.getInstance().getMacroManager().method1710();
            ChatUtil.method3308(Text.literal((String)field7385).setStyle(Style.EMPTY.withColor(Formatting.GRAY)), new Object[0]);
            return 1;
        }));
    }

    private int executeAdd(CommandContext param1) {
        String v2 = ((String)param1.getArgument(field7367, String.class)).trim();
        if (v2.isEmpty()) {
            return this.method419(param1);
        }
        String[] v3 = v2.split(field7368);
        if (v3.length < 3) {
            return this.method419(param1);
        }
        String v4 = v3[0];
        String v5 = v3[1];
        String v6 = String.join((CharSequence)field7369, Arrays.asList(v3).subList(2, v3.length));
        KeyCode v7 = KeyCode.fromName(v5);
        if (v7 == null) {
            return this.method419(param1);
        }
        Minced.getInstance().getMacroManager().cfr_renamed_0(v4, v7.getCode(), v6);
        MutableText v8 = Text.literal((String)field7370).setStyle(Style.EMPTY.withColor(Formatting.GRAY)).append((Text)Text.literal((String)v4).setStyle(Style.EMPTY.withColor(Formatting.WHITE))).append((Text)Text.literal((String)field7371).setStyle(Style.EMPTY.withColor(Formatting.GRAY))).append((Text)Text.literal((String)KeyCode.getDisplayName(v7.getCode())).setStyle(Style.EMPTY.withColor(Formatting.WHITE))).append((Text)Text.literal((String)field7372).setStyle(Style.EMPTY.withColor(Formatting.GRAY)));
        ChatUtil.method3308(v8, new Object[0]);
        return 1;
    }

    private CompletableFuture suggestAdd(CommandContext param1, SuggestionsBuilder param2) {
        String[] v7;
        String v3 = param1.getInput();
        String v4 = param2.getRemaining();
        boolean v5 = v4.endsWith(field7380);
        String v6 = v4.trim();
        String[] stringArray = v7 = v6.isEmpty() ? new String[]{} : v6.split(field7381);
        int v8 = v7.length == 0 ? v3.length() : (v5 ? v3.length() : v3.length() - v7[v7.length - 1].length());
        SuggestionsBuilder v9 = new SuggestionsBuilder(v3, v8);
        if (v7.length == 0 || v7.length == 1 && !v5) {
            return v9.buildFuture();
        }
        if (v7.length == 1 || v7.length == 2 && !v5) {
            String v10 = v7.length == 2 ? v7[1].toUpperCase(Locale.ROOT) : "";
            for (KeyCode v14 : KeyCode.values()) {
                if (!v14.name().startsWith(v10)) continue;
                v9.suggest(v14.name());
            }
            return v9.buildFuture();
        }
        return v9.buildFuture();
    }

    public CommandMacro() {
        super(field7382, field7383);
    }

    @SuppressWarnings("unchecked")
    private CompletableFuture suggestMacroNames(CommandContext param1, SuggestionsBuilder param2) {
        List<String> v3 = ((List<sr.ch>) Minced.getInstance().getMacroManager().method1708()).stream().map(x -> (String) x.ДЧ()).collect(Collectors.toList());
        return CommandSource.suggestMatching(v3, (SuggestionsBuilder)param2);
    }

    static {
        field7367 = "args";
        field7368 = "\\s+";
        field7369 = " ";
        field7370 = "Макрос с именем '";
        field7371 = "' и кнопкой активации: ";
        field7372 = " добавлен!";
        field7380 = " ";
        field7381 = "\\s+";
        field7384 = "Список макросов пуст!";
        field7385 = "Список макросов очищен!";
        field7373 = "Список макросов пуст!";
        field7374 = "Список макросов:";
        field7375 = "Имя: ";
        field7376 = " Клавиша: ";
        field7377 = " Текст: ";
        cfr_renamed_98 = "\u0001\u0001 remove \u0001";
        field7378 = " [Удалить]";
        field7379 = "Клик для удаления макроса";
        cfr_renamed_99 = "Всего: \u0001";
        field7358 = "name";
        field7359 = "Макрос c именем '";
        field7360 = "' удален!";
    }
}



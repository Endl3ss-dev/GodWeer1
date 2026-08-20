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
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.List;
import java.util.concurrent.CompletableFuture;
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

public class CommandInventory
extends ClientCommand
implements ArgumentType {
    static String cfr_renamed_102;
    static String cfr_renamed_103;
    static String cfr_renamed_104;
    static String cfr_renamed_105;
    static String cfr_renamed_106;
    static String cfr_renamed_107;
    static String cfr_renamed_108;
    static String cfr_renamed_109;

    static String field7441;
    static String field7442;
    static String field7443;
    static String field7444;
    static String field7445;
    static String field7446;
    static String field7447;
    static String field7448;
    static String field7449;
    static String field7450;
    static String field7451;
    static String field7452;
    static String field7453;
    static String field7454;
    static String field7455;
    static String field7456 = "save";
    static String field7457 = "name";
    static String field7458 = "load";
    static String field7459 = "name";
    static String field7460 = "remove";
    static String field7461 = "name";
    static String field7462 = "list";
    static String field7463 = "clear";
    static String field7464;

    public CommandInventory(String ... args) {
        super(field7451, field7452);
    }

    @SuppressWarnings("unchecked")
    public CompletableFuture listSuggestions(CommandContext param1, SuggestionsBuilder param2) {
        List<String> v3 = (List<String>) Minced.getInstance().getInventoryManager().method1690();
        return CommandSource.suggestMatching((Iterable<String>)v3, (SuggestionsBuilder)param2);
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
        param12.then(CommandInventory.Ж((String)field7456).then(CommandInventory.Ж((String)field7457, (ArgumentType)StringArgumentType.word()).suggests((param0, param1) -> CommandSource.suggestMatching((Iterable<String>)(List<String>)Minced.getInstance().getInventoryManager().method1690(), (SuggestionsBuilder)param1)).executes(param0 -> {
            String v1 = (String)param0.getArgument(field7441, String.class);
            Minced.getInstance().getInventoryManager().method1697(v1);
            MutableText v2 = Text.literal((String)field7442).setStyle(Style.EMPTY.withColor(Formatting.GRAY));
            MutableText v3 = Text.literal((String)((Object)_4ZXYEo.дЭ("makeConcatWithConstants", new Object[]{}, (String)v1))).setStyle(Style.EMPTY.withColor(Formatting.WHITE));
            MutableText v4 = Text.literal((String)field7443).setStyle(Style.EMPTY.withColor(Formatting.GRAY));
            ChatUtil.method3308(Text.literal((String)"").append((Text)v2).append((Text)v3).append((Text)v4), new Object[0]);
            return 1;
        })));
        param12.then(CommandInventory.Ж((String)field7458).then(CommandInventory.Ж((String)field7459, (ArgumentType)new CommandInventory()).executes(param1 -> {
            String v2 = (String)param1.getArgument(field7448, String.class);
            if (!Minced.getInstance().getInventoryManager().method1690().contains(v2)) {
                return this.method419(param1);
            }
            Minced.getInstance().getInventoryManager().method1694(v2);
            MutableText v3 = Text.literal((String)field7449).setStyle(Style.EMPTY.withColor(Formatting.GRAY));
            MutableText v4 = Text.literal((String)((Object)_4ZXYEo.д7("makeConcatWithConstants", new Object[]{}, (String)v2))).setStyle(Style.EMPTY.withColor(Formatting.WHITE));
            MutableText v5 = Text.literal((String)field7450).setStyle(Style.EMPTY.withColor(Formatting.GRAY));
            ChatUtil.method3308(Text.literal((String)"").append((Text)v3).append((Text)v4).append((Text)v5), new Object[0]);
            return 1;
        })));
        param12.then(CommandInventory.Ж((String)field7460).then(CommandInventory.Ж((String)field7461, (ArgumentType)new CommandInventory()).executes(param1 -> {
            String v2 = (String)param1.getArgument(field7453, String.class);
            if (!Minced.getInstance().getInventoryManager().method1690().contains(v2)) {
                return this.method419(param1);
            }
            Minced.getInstance().getInventoryManager().method1698(v2);
            MutableText v3 = Text.literal((String)field7454).setStyle(Style.EMPTY.withColor(Formatting.GRAY));
            MutableText v4 = Text.literal((String)((Object)_4ZXYEo.дб("makeConcatWithConstants", new Object[]{}, (String)v2))).setStyle(Style.EMPTY.withColor(Formatting.WHITE));
            MutableText v5 = Text.literal((String)field7455).setStyle(Style.EMPTY.withColor(Formatting.GRAY));
            ChatUtil.method3308(Text.literal((String)"").append((Text)v3).append((Text)v4).append((Text)v5), new Object[0]);
            return 1;
        })));
        param12.then(CommandInventory.Ж((String)field7462).executes(param1 -> {
            List<String> v2 = (List<String>) Minced.getInstance().getInventoryManager().method1690();
            if (v2 == null || v2.isEmpty()) {
                MutableText v3 = Text.literal((String)field7444).setStyle(Style.EMPTY.withColor(Formatting.GRAY));
                ChatUtil.method3308(v3, new Object[0]);
                return 1;
            }
            MutableText v3 = Text.literal((String)field7445).setStyle(Style.EMPTY.withColor(Formatting.GRAY));
            ChatUtil.method3308(v3, new Object[0]);
            for (String v5 : v2) {
                MutableText v6 = Text.literal((String)v5).setStyle(Style.EMPTY.withColor(Formatting.WHITE));
                String v12_load = (String) _4ZXYEo.д6("makeConcatWithConstants", new Object[]{}, (String)Minced.getInstance().getCommandManager().method74(), (String)this.method416(), (String)v5);
                ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, v12_load);
                HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal((String)((Object)_4ZXYEo.дБ("makeConcatWithConstants", new Object[]{}, (String)v5))));
                Style style = Style.EMPTY
                    .withColor(Formatting.GREEN)
                    .withClickEvent(clickEvent)
                    .withHoverEvent(hoverEvent);
                MutableText v7 = Text.literal((String)field7446).setStyle(style);

                String v12_del = (String) _4ZXYEo.дГ("makeConcatWithConstants", new Object[]{}, (String)Minced.getInstance().getCommandManager().method74(), (String)this.method416(), (String)v5);
                ClickEvent clickEvent2 = new ClickEvent(ClickEvent.Action.RUN_COMMAND, v12_del);
                HoverEvent hoverEvent2 = new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal((String)((Object)_4ZXYEo.дl("makeConcatWithConstants", new Object[]{}, (String)v5))));
                Style style2 = Style.EMPTY
                    .withColor(Formatting.RED)
                    .withClickEvent(clickEvent2)
                    .withHoverEvent(hoverEvent2);
                MutableText v8 = Text.literal((String)field7447).setStyle(style2);

                ChatUtil.method3308(Text.literal((String)"").append((Text)v6).append((Text)v7).append((Text)v8), new Object[0]);
            }
            return 1;
        }));
        param12.then(CommandInventory.Ж((String)field7463).executes(param02 -> {
            List<String> v1 = (List<String>) Minced.getInstance().getInventoryManager().method1690();
            v1.forEach(param0 -> Minced.getInstance().getInventoryManager().method1698((String)param0));
            MutableText v2 = Text.literal((String)field7464).setStyle(Style.EMPTY.withColor(Formatting.GRAY));
            ChatUtil.method3308(v2, new Object[0]);
            return 1;
        }));
    }

    public String parse(StringReader param1) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        String v2 = param1.readString();
        boolean v3 = Minced.getInstance().getInventoryManager().method1690().contains(v2);
        if (!v3) {
            throw new DynamicCommandExceptionType(param0 -> Text.literal((String)((Object)_4ZXYEo.дЬ("makeConcatWithConstants", new Object[]{}, (String)String.valueOf(param0))))).create((Object)v2);
        }
        return v2;
    }

    static {
        cfr_renamed_102 = "Инвентарь с именем \u0001 не существует";
        field7464 = "Список инвентарей очищен!";
        field7444 = "Список инвентарей пуст!";
        field7445 = "Список сохранённых инвентарей:";
        field7446 = " [Загрузить]";
        cfr_renamed_103 = "\u0001\u0001 load \u0001";
        cfr_renamed_104 = "Загрузить инвентарь \u0001";
        field7447 = " [Удалить]";
        cfr_renamed_105 = "\u0001\u0001 remove \u0001";
        cfr_renamed_106 = "Удалить инвентарь \u0001";
        field7453 = "name";
        field7454 = "Инвентарь с именем ";
        cfr_renamed_107 = "\"\u0001\"";
        field7455 = " удалён!";
        field7448 = "name";
        field7449 = "Инвентарь с именем ";
        cfr_renamed_108 = "\"\u0001\"";
        field7450 = " загружен!";
        field7441 = "name";
        field7442 = "Инвентарь с именем ";
        cfr_renamed_109 = "\"\u0001\"";
        field7443 = " сохранён!";
        field7451 = "inventory";
        field7452 = "inv";
    }
}



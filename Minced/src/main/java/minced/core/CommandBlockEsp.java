/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.Command
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 *  net.minecraft.command.CommandSource
 *  net.minecraft.item.BlockItem
 *  net.minecraft.registry.Registries
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.Style
 *  net.minecraft.text.Text
 *  net.minecraft.util.Formatting
 *  net.minecraft.util.Identifier
 *  sLM.4ZXYEo
 *  sg.ec.chI
 *  sg.ec.Чг$Ч
 */
package minced.core;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.lang.invoke.CallSite;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import minced.core.ColorUtil;
import minced.core.ChatUtil;
import minced.core.ClientCommand;
import minced.core.Minced;
import minced.manager.BlockESP;
import net.minecraft.command.CommandSource;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import sLM._4ZXYEo;
import sg.ec.chI;
import sg.ec.chg;

public class CommandBlockEsp
extends ClientCommand
 {
    static String цс;
    static String цЧ;

    static String field6987;
    static String field6988;
    static String field6989;
    static int field6990;
    static String field6991;
    static String field6992;
    static Map field6993;
    static String field6994 = "field6994";
    static String field6995 = "field6995";
    static String field6996 = "field6996";
    static String field6997 = "field6997";
    static String field6998 = "field6998";
    static String field6999 = "field6999";
    static String field7000 = "field7000";
    static String field7001 = "field7001";
    static String field7002 = "field7002";
    static String field7003;
    static String field7004;
    static String field7005;
    static String field7006;
    static String field7007;
    static String field7008;
    static String field7009;
    static String field7010;
    static String field7011;
    static String field7012;
    static String field7013;
    static String field7014;
    static String field7015;
    static String field7016;
    static String field7017;
    static String field7018;
    String[] field7019;
    static int field7020;
    static String field7021;
    static String field7022;
    static String field7023;
    static String field7024;
    static String field7025;
    static String field7026 = "blockesp";
    static String field7027 = "besp";
    static String field7028;
    static String field7029;
    static int field7030;
    static String field7031;
    static String field7032 = "add";
    static String field7033 = "shulkers";
    static String field7034 = "color";
    static String field7035 = "block";
    static String field7036 = "color";
    static String field7037 = "remove";
    static String field7038 = "block";
    static String field7039 = "clear";
    static String field7040 = "list";
    static String field7041;
    static String field7042;
    static String field7043;
    static String field7044;
    static String field7045;
    static int field7046;
    static String field7047;
    static String field7048;
    static String field7049;

    private CompletableFuture cfr_renamed_0(CommandContext param1, SuggestionsBuilder param2) {
        List v3 = Stream.concat(Registries.BLOCK.getIds().stream(), Registries.ITEM.getIds().stream().filter(param0 -> Registries.ITEM.get(param0) instanceof BlockItem)).map(Identifier::toString).distinct().sorted().collect(Collectors.toList());
        return CommandSource.suggestMatching(v3, (SuggestionsBuilder)param2);
    }

    private int method1750(CommandContext param1) {
        String v2 = Minced.getInstance().getCommandManager().method74();
        String v3 = _4ZXYEo.ит("makeConcatWithConstants", new Object[]{}, (String)v2);
        MutableText v4 = Text.literal((String)field6992).setStyle(Style.EMPTY.withColor(Formatting.GRAY)).append((Text)Text.literal((String)((Object)v3)).setStyle(Style.EMPTY.withColor(Formatting.YELLOW)));
        ChatUtil.method3308(v4, new Object[0]);
        return 1;
    }

    private CompletableFuture method1751(CommandContext param1, SuggestionsBuilder param2) {
        return CommandSource.suggestMatching(field6993.keySet(), (SuggestionsBuilder)param2);
    }

    private BlockESP method1753() {
        return Minced.getInstance().getBlockESPManager();
    }

    private int method1754(CommandContext param1) {
        String v2 = (String)param1.getArgument(field7021, String.class);
        boolean v3 = this.method1753().method1711(v2);
        if (v3) {
            MutableText v4 = Text.literal((String)field7022).setStyle(Style.EMPTY.withColor(Formatting.GRAY)).append((Text)Text.literal((String)v2).setStyle(Style.EMPTY.withColor(Formatting.WHITE))).append((Text)Text.literal((String)field7023).setStyle(Style.EMPTY.withColor(Formatting.GRAY)));
            ChatUtil.method3308(v4, new Object[0]);
        } else {
            MutableText v4 = Text.literal((String)field7024).setStyle(Style.EMPTY.withColor(Formatting.GRAY)).append((Text)Text.literal((String)v2).setStyle(Style.EMPTY.withColor(Formatting.WHITE))).append((Text)Text.literal((String)field7025).setStyle(Style.EMPTY.withColor(Formatting.GRAY)));
            ChatUtil.method3308(v4, new Object[0]);
        }
        return 1;
    }

    private CompletableFuture method1755(CommandContext param1, SuggestionsBuilder param2) {
        List<String> v3 = this.method1753().method1713().stream().map(x -> (String) x.ф2()).collect(Collectors.toList());
        return CommandSource.suggestMatching(v3, (SuggestionsBuilder)param2);
    }

    private int method1757(String param1) {
        if (param1 == null) {
            return field7020;
        }
        String v2 = param1.trim();
        if (v2.isEmpty()) {
            return field7020;
        }
        return ColorUtil.cfr_renamed_0(v2);
    }

    public CommandBlockEsp() {
        super(field7026, field7027);
    }

    private int method1758(CommandContext param1) {
        String v2 = (String)param1.getArgument(field7028, String.class);
        int v3 = this.method1757(v2);
        for (String v7 : field7019) {
            this.method1753().method1714(v7, v3);
        }
        String string = String.format(field7029, v3 & field7030);
        MutableText v5 = Text.literal((String)field7031).setStyle(Style.EMPTY.withColor(Formatting.GRAY)).append((Text)Text.literal((String)string).setStyle(Style.EMPTY.withColor(Formatting.WHITE)));
        ChatUtil.method3308(v5, new Object[0]);
        return 1;
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
        param12.then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandBlockEsp.Ж((String)field7032).executes(this::method1750)).then(((LiteralArgumentBuilder)CommandBlockEsp.Ж((String)field7033).executes(this::method1750)).then(CommandBlockEsp.Ж((String)field7034, (ArgumentType)StringArgumentType.word()).suggests(this::method1751).executes(this::method1758)))).then(((RequiredArgumentBuilder)CommandBlockEsp.Ж((String)field7035, (ArgumentType)StringArgumentType.word()).suggests(this::cfr_renamed_0).executes(this::method1750)).then(CommandBlockEsp.Ж((String)field7036, (ArgumentType)StringArgumentType.word()).suggests(this::method1751).executes(this::method1759))));
        param12.then(CommandBlockEsp.Ж((String)field7037).then(CommandBlockEsp.Ж((String)field7038, (ArgumentType)StringArgumentType.word()).suggests(this::method1755).executes(this::method1754)));
        param12.then(CommandBlockEsp.Ж((String)field7039).executes(param1 -> {
            this.method1753().method1717();
            ChatUtil.method3308(Text.literal((String)field7049).setStyle(Style.EMPTY.withColor(Formatting.GRAY)), new Object[0]);
            return 1;
        }));
        param12.then(CommandBlockEsp.Ж((String)field7040).executes(param1 -> {
            List<chg.ch> v2 = this.method1753().method1713();
            if (v2.isEmpty()) {
                ChatUtil.method3308(Text.literal((String)field6987).setStyle(Style.EMPTY.withColor(Formatting.GRAY)), new Object[0]);
                return 1;
            }
            ChatUtil.method3308(Text.literal((String)field6988).setStyle(Style.EMPTY.withColor(Formatting.GRAY)), new Object[0]);
            for (chg.ch v4 : v2) {
                String v5 = (String) v4.ф2();
                int v6 = ((Number) v4.фВ()).intValue();
                String v7 = String.format(field6989, v6 & field6990);
                MutableText v8 = Text.literal((String)v5).setStyle(Style.EMPTY.withColor(Formatting.WHITE));
                MutableText v9 = Text.literal((String)field6991).setStyle(Style.EMPTY.withColor(Formatting.GRAY));
                MutableText v10 = Text.literal((String)v7).setStyle(Style.EMPTY.withColor(Formatting.WHITE));
                ChatUtil.method3308(Text.literal((String)"").append((Text)v8).append((Text)v9).append((Text)v10), new Object[0]);
            }
            ChatUtil.method3308(Text.literal((String)((Object)_4ZXYEo.и3("makeConcatWithConstants", new Object[]{}, (int)v2.size()))).setStyle(Style.EMPTY.withColor(Formatting.GRAY)), new Object[0]);
            return 1;
        }));
    }

    private int method1759(CommandContext param1) {
        String v2 = (String)param1.getArgument(field7041, String.class);
        String v3 = (String)param1.getArgument(field7042, String.class);
        Identifier v4 = chI.И((String)v2);
        if (v4 == null) {
            MutableText v5 = Text.literal((String)field7043).setStyle(Style.EMPTY.withColor(Formatting.GRAY)).append((Text)Text.literal((String)v2).setStyle(Style.EMPTY.withColor(Formatting.WHITE))).append((Text)Text.literal((String)field7044).setStyle(Style.EMPTY.withColor(Formatting.GRAY)));
            ChatUtil.method3308(v5, new Object[0]);
            return 1;
        }
        int v5 = this.method1757(v3);
        this.method1753().method1714(v4.toString(), v5);
        String v6 = String.format(field7045, v5 & field7046);
        MutableText v7 = Text.literal((String)field7047).setStyle(Style.EMPTY.withColor(Formatting.GRAY)).append((Text)Text.literal((String)v4.toString()).setStyle(Style.EMPTY.withColor(Formatting.WHITE))).append((Text)Text.literal((String)field7048).setStyle(Style.EMPTY.withColor(Formatting.GRAY))).append((Text)Text.literal((String)v6).setStyle(Style.EMPTY.withColor(Formatting.WHITE)));
        ChatUtil.method3308(v7, new Object[0]);
        return 1;
    }

    static {
        field7020 = -1;
        цс = "\u0001blockesp add предмет цвет";
        field6992 = "Неправильный ввод команды, вводи ";
        field7041 = "block";
        field7042 = "color";
        field7043 = "Блок или предмет-блок '";
        field7044 = "' не найден (укажи id блока или предмета вроде chest)";
        field7045 = "#%06X";
        field7046 = 0xFFFFFF;
        field7047 = "Блок '";
        field7048 = "' добавлен с цветом ";
        field7028 = "color";
        field7029 = "#%06X";
        field7030 = 0xFFFFFF;
        field7031 = "Все шалкеры добавлены в BlockESP с цветом ";
        field7021 = "block";
        field7022 = "Блок '";
        field7023 = "' удалён из BlockESP!";
        field7024 = "Блок '";
        field7025 = "' не найден в BlockESP!";
        field6987 = "Список BlockESP пуст!";
        field6988 = "Список блоков для BlockESP:";
        field6989 = "#%06X";
        field6990 = 0xFFFFFF;
        field6991 = " Цвет: ";
        цЧ = "Всего: \u0001";
        field7049 = "Список BlockESP очищен!";
        field7003 = "minecraft:white_shulker_box";
        field7004 = "minecraft:orange_shulker_box";
        field7005 = "minecraft:magenta_shulker_box";
        field7006 = "minecraft:light_blue_shulker_box";
        field7007 = "minecraft:yellow_shulker_box";
        field7008 = "minecraft:lime_shulker_box";
        field7009 = "minecraft:pink_shulker_box";
        field7010 = "minecraft:gray_shulker_box";
        field7011 = "minecraft:light_gray_shulker_box";
        field7012 = "minecraft:cyan_shulker_box";
        field7013 = "minecraft:purple_shulker_box";
        field7014 = "minecraft:blue_shulker_box";
        field7015 = "minecraft:brown_shulker_box";
        field7016 = "minecraft:green_shulker_box";
        field7017 = "minecraft:red_shulker_box";
        field7018 = "minecraft:black_shulker_box";
    }
}



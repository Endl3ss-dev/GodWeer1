/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.Command
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 *  net.minecraft.command.CommandSource
 *  net.minecraft.registry.Registries
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.Style
 *  net.minecraft.text.Text
 *  net.minecraft.util.Formatting
 *  net.minecraft.util.Identifier
 *  sLM.4ZXYEo
 */
package minced.core;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import minced.core.MiningStats;
import minced.core.ChatUtil;
import minced.core.ClientCommand;
import minced.module.misc.MiningCounter;
import net.minecraft.command.CommandSource;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import sLM._4ZXYEo;

public class CommandCounter
extends ClientCommand
 {
    static String cfr_renamed_94;
    static String cfr_renamed_95;
    static String cfr_renamed_96;
    static String cfr_renamed_97;

    static String cfr_renamed_91 = "counter";
    static String field7332 = "ct";
    static String field7333;
    static String field7334;
    static String field7335;
    static String field7336;
    static String field7337;
    static String field7338;
    static String field7339 = "add";
    static String field7340 = "item";
    static String field7341 = "remove";
    static String field7342 = "item";
    static String field7343 = "clear";
    static String field7344 = "list";
    static String field7345;
    static String field7346;
    static String field7347;
    static String field7348;
    static String field7349;
    static String field7350;
    static String cfr_renamed_92;
    static String field7351;
    static String field7352;
    static String field7353;
    static String field7354;
    static String field7355;
    static String field7356;
    static String cfr_renamed_93;
    static String field7357;

    public CommandCounter() {
        super(cfr_renamed_91, field7332);
    }

    private int method1807(CommandContext param1) {
        String v2 = (String)param1.getArgument(field7334, String.class);
        boolean v3 = MiningCounter.method1350(v2);
        if (v3) {
            MutableText v4 = Text.literal((String)field7335).setStyle(Style.EMPTY.withColor(Formatting.GRAY)).append((Text)Text.literal((String)v2).setStyle(Style.EMPTY.withColor(Formatting.WHITE))).append((Text)Text.literal((String)field7336).setStyle(Style.EMPTY.withColor(Formatting.GRAY)));
            ChatUtil.method3308(v4, new Object[0]);
        } else {
            MutableText v4 = Text.literal((String)field7337).setStyle(Style.EMPTY.withColor(Formatting.GRAY)).append((Text)Text.literal((String)v2).setStyle(Style.EMPTY.withColor(Formatting.WHITE))).append((Text)Text.literal((String)field7338).setStyle(Style.EMPTY.withColor(Formatting.GRAY)));
            ChatUtil.method3308(v4, new Object[0]);
        }
        return 1;
    }

    private CompletableFuture method1808(CommandContext param1, SuggestionsBuilder param2) {
        List<String> v3 = ((List<MiningStats>) MiningCounter.method1351()).stream().map(x -> (String) x.method2307()).collect(Collectors.toList());
        return CommandSource.suggestMatching(v3, (SuggestionsBuilder)param2);
    }

    public static com.mojang.brigadier.builder.LiteralArgumentBuilder Ж(String name) {
        return ClientCommand.method420(name);
    }

    public static com.mojang.brigadier.builder.RequiredArgumentBuilder Ж(String name, com.mojang.brigadier.arguments.ArgumentType<?> type) {
        return ClientCommand.method417(name, type);
    }

    @Override
    public void run(LiteralArgumentBuilder param1) {
        param1.executes(param0 -> {
            ChatUtil.method3308(Text.literal((String)field7347).setStyle(Style.EMPTY.withColor(Formatting.GRAY)), new Object[0]);
            ChatUtil.method3308(Text.literal((String)field7348).setStyle(Style.EMPTY.withColor(Formatting.WHITE)), new Object[0]);
            ChatUtil.method3308(Text.literal((String)field7349).setStyle(Style.EMPTY.withColor(Formatting.WHITE)), new Object[0]);
            ChatUtil.method3308(Text.literal((String)field7350).setStyle(Style.EMPTY.withColor(Formatting.WHITE)), new Object[0]);
            ChatUtil.method3308(Text.literal((String)cfr_renamed_92).setStyle(Style.EMPTY.withColor(Formatting.WHITE)), new Object[0]);
            return 1;
        });
        param1.then(CommandCounter.Ж((String)field7339).then(CommandCounter.Ж((String)field7340, (ArgumentType)StringArgumentType.word()).suggests(this::method1811).executes(this::method1812)));
        param1.then(CommandCounter.Ж((String)field7341).then(CommandCounter.Ж((String)field7342, (ArgumentType)StringArgumentType.word()).suggests(this::method1808).executes(this::method1807)));
        param1.then(CommandCounter.Ж((String)field7343).executes(param0 -> {
            MiningCounter.method1355();
            ChatUtil.method3308(Text.literal((String)field7333).setStyle(Style.EMPTY.withColor(Formatting.GRAY)), new Object[0]);
            return 1;
        }));
        param1.then(CommandCounter.Ж((String)field7344).executes(param0 -> {
            List<MiningStats> v1 = (List<MiningStats>) MiningCounter.method1351();
            if (v1.isEmpty()) {
                ChatUtil.method3308(Text.literal((String)field7345).setStyle(Style.EMPTY.withColor(Formatting.GRAY)), new Object[0]);
                return 1;
            }
            ChatUtil.method3308(Text.literal((String)field7346).setStyle(Style.EMPTY.withColor(Formatting.GRAY)), new Object[0]);
            for (MiningStats v3 : v1) {
                ChatUtil.method3308(Text.literal((String)((Object)_4ZXYEo.弟Б("makeConcatWithConstants", new Object[]{}, (String)v3.method2307()))).setStyle(Style.EMPTY.withColor(Formatting.WHITE)), new Object[0]);
            }
            ChatUtil.method3308(Text.literal((String)((Object)_4ZXYEo.弟Г("makeConcatWithConstants", new Object[]{}, (int)v1.size()))).setStyle(Style.EMPTY.withColor(Formatting.GRAY)), new Object[0]);
            return 1;
        }));
    }

    private CompletableFuture method1811(CommandContext param1, SuggestionsBuilder param2) {
        List v3 = Registries.ITEM.getIds().stream().map(Identifier::toString).collect(Collectors.toList());
        List v4 = Registries.BLOCK.getIds().stream().map(Identifier::toString).collect(Collectors.toList());
        v3.addAll(v4);
        return CommandSource.suggestMatching((Iterable)v3.stream().distinct().collect(Collectors.toList()), (SuggestionsBuilder)param2);
    }

    private int method1812(CommandContext param1) {
        Identifier v3;
        Object v2 = (String)param1.getArgument(field7351, String.class);
        if (!((String)v2).contains(field7352)) {
            v2 = _4ZXYEo.弟Ь("makeConcatWithConstants", new Object[]{}, (String)v2);
        }
        if ((v3 = Identifier.tryParse((String)v2)) == null) {
            ChatUtil.method3308(Text.literal((String)((Object)_4ZXYEo.弟6("makeConcatWithConstants", new Object[]{}, (String)v2))).setStyle(Style.EMPTY.withColor(Formatting.RED)), new Object[0]);
            return 1;
        }
        if (Registries.BLOCK.containsId(v3)) {
            MiningCounter.method1352(v3.toString());
            MutableText v4 = Text.literal((String)field7353).setStyle(Style.EMPTY.withColor(Formatting.GRAY)).append((Text)Text.literal((String)v3.toString()).setStyle(Style.EMPTY.withColor(Formatting.WHITE))).append((Text)Text.literal((String)field7354).setStyle(Style.EMPTY.withColor(Formatting.GRAY)));
            ChatUtil.method3308(v4, new Object[0]);
            return 1;
        }
        if (Registries.ITEM.containsId(v3)) {
            MiningCounter.method1356(v3.toString());
            MutableText v4 = Text.literal((String)field7355).setStyle(Style.EMPTY.withColor(Formatting.GRAY)).append((Text)Text.literal((String)v3.toString()).setStyle(Style.EMPTY.withColor(Formatting.WHITE))).append((Text)Text.literal((String)field7356).setStyle(Style.EMPTY.withColor(Formatting.GRAY)));
            ChatUtil.method3308(v4, new Object[0]);
            return 1;
        }
        MutableText v4 = Text.literal((String)cfr_renamed_93).setStyle(Style.EMPTY.withColor(Formatting.GRAY)).append((Text)Text.literal((String)v2).setStyle(Style.EMPTY.withColor(Formatting.WHITE))).append((Text)Text.literal((String)field7357).setStyle(Style.EMPTY.withColor(Formatting.GRAY)));
        ChatUtil.method3308(v4, new Object[0]);
        return 1;
    }

    static {
        field7351 = "item";
        field7352 = ":";
        cfr_renamed_94 = "minecraft:\u0001";
        cfr_renamed_95 = "Неверный формат ID: \u0001";
        field7353 = "Блок '";
        field7354 = "' добавлен в счетчик!";
        field7355 = "Предмет '";
        field7356 = "' добавлен в счетчик!";
        cfr_renamed_93 = "Предмет/блок '";
        field7357 = "' не найден!";
        field7334 = "item";
        field7335 = "Предмет '";
        field7336 = "' удалён из счетчика!";
        field7337 = "Предмет '";
        field7338 = "' не найден в счетчике!";
        field7345 = "Список отслеживаемых предметов пуст!";
        field7346 = "Отслеживаемые предметы:";
        cfr_renamed_96 = "• \u0001";
        cfr_renamed_97 = "Всего: \u0001";
        field7333 = "Список отслеживаемых предметов очищен!";
        field7347 = "Использование:";
        field7348 = ".counter add <block/item>";
        field7349 = ".counter remove <block/item>";
        field7350 = ".counter list";
        cfr_renamed_92 = ".counter clear";
    }
}




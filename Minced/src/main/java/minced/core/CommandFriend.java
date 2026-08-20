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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import minced.core.FriendEntry;
import minced.core.ChatUtil;
import minced.core.ClientCommand;
import minced.core.Minced;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import sLM._4ZXYEo;
import sg.ec.sb2;

public class CommandFriend
extends ClientCommand
implements ArgumentType {
    static String cfr_renamed_112;
    static String cfr_renamed_113;
    static String cfr_renamed_114;
    static String cfr_renamed_115;
    static String cfr_renamed_116;
    static String cfr_renamed_117;

    static String field7465;
    static String field7466;
    static String field7467;
    static String field7468;
    static String field7469;
    static String field7470;
    static String field7471;
    static String field7472;
    static String field7473;
    static String field7474;
    static String field7475;
    static String field7476;
    static String field7477 = "add";
    static String field7478 = "player";
    static String field7479 = "remove";
    static String field7480 = "player";
    static String field7481 = "list";
    static String field7482 = "clear";
    static String field7483;
    static String field7484;
    static String field7485;
    static String field7486;
    static String field7487;
    static String field7488;
    static String field7489;
    static MinecraftClient I = MinecraftClient.getInstance();

    public CommandFriend(String ... args) {
        super(field7466, field7467);
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
        param12.then(CommandFriend.Ж((String)field7477).then(CommandFriend.Ж((String)field7478, (ArgumentType)new CommandFriend()).executes(param0 -> {
            String v1 = (String)param0.getArgument(field7468, String.class);
            boolean v2 = Minced.getInstance().getFriendManager().cfr_renamed_4(v1);
            MutableText v3 = Text.literal((String)field7469).setStyle(Style.EMPTY.withColor(Formatting.GRAY));
            MutableText v4 = Text.literal((String)((Object)_4ZXYEo.cfr_renamed_110("makeConcatWithConstants", new Object[]{}, (String)v1))).setStyle(Style.EMPTY.withColor(Formatting.WHITE));
            MutableText v5 = Text.literal((String)(v2 ? field7470 : field7471)).setStyle(Style.EMPTY.withColor(Formatting.GRAY));
            if (!v2) {
                Minced.getInstance().getFriendManager().method1707(v1);
            }
            ChatUtil.method3308(Text.literal((String)"").append((Text)v3).append((Text)v4).append((Text)v5), new Object[0]);
            return 1;
        })));
        param12.then(CommandFriend.Ж((String)field7479).then(CommandFriend.Ж((String)field7480, (ArgumentType)new CommandFriend()).executes(param1 -> {
            String v2 = (String)param1.getArgument(field7483, String.class);
            boolean v3 = Minced.getInstance().getFriendManager().cfr_renamed_4(v2);
            if (!v3) {
                return this.method419(param1);
            }
            Minced.getInstance().getFriendManager().method1703(v2);
            MutableText v4 = Text.literal((String)field7484).setStyle(Style.EMPTY.withColor(Formatting.GRAY));
            MutableText v5 = Text.literal((String)((Object)_4ZXYEo.cfr_renamed_111("makeConcatWithConstants", new Object[]{}, (String)v2))).setStyle(Style.EMPTY.withColor(Formatting.WHITE));
            MutableText v6 = Text.literal((String)field7485).setStyle(Style.EMPTY.withColor(Formatting.GRAY));
            ChatUtil.method3308(Text.literal((String)"").append((Text)v4).append((Text)v5).append((Text)v6), new Object[0]);
            return 1;
        })));
        param12.then(CommandFriend.Ж((String)field7481).executes(param1 -> {
            List<FriendEntry> v2 = (List<FriendEntry>) Minced.getInstance().getFriendManager().method1705();
            if (v2 == null || v2.isEmpty()) {
                MutableText v3 = Text.literal((String)field7472).setStyle(Style.EMPTY.withColor(Formatting.GRAY));
                ChatUtil.method3308(v3, new Object[0]);
                return 1;
            }
            MutableText v3 = Text.literal((String)field7473).setStyle(Style.EMPTY.withColor(Formatting.GRAY));
            ChatUtil.method3308(v3, new Object[0]);
            DateTimeFormatter v4 = DateTimeFormatter.ofPattern(field7474);
            for (FriendEntry v6 : v2) {
                String v7 = v6.method2633();
                MutableText v8 = Text.literal((String)v7).setStyle(Style.EMPTY.withColor(Formatting.WHITE));
                MutableText v9 = Text.literal((String)field7475).setStyle(Style.EMPTY.withColor(Formatting.GRAY));
                MutableText v10 = Text.literal((String)v6.cfr_underscore().format(v4)).setStyle(Style.EMPTY.withColor(Formatting.WHITE));
                String v12_del = (String) _4ZXYEo.ф衣("makeConcatWithConstants", new Object[]{}, (String)Minced.getInstance().getCommandManager().method74(), (String)this.method416(), (String)v7);
                
                ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, v12_del);
                HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal((String)((Object)_4ZXYEo.фн("makeConcatWithConstants", new Object[]{}, (String)v7))).setStyle(Style.EMPTY.withColor(Formatting.RED)));
                Style style = Style.EMPTY
                    .withColor(Formatting.RED)
                    .withClickEvent(clickEvent)
                    .withHoverEvent(hoverEvent);
                MutableText v11 = Text.literal((String)field7476).setStyle(style);
                
                ChatUtil.method3308(Text.literal((String)"").append((Text)v8).append((Text)v9).append((Text)v10).append((Text)v11), new Object[0]);
            }
            MutableText v5 = Text.literal((String)((Object)_4ZXYEo.фю("makeConcatWithConstants", new Object[]{}, (int)v2.size()))).setStyle(Style.EMPTY.withColor(Formatting.GRAY));
            ChatUtil.method3308(v5, new Object[0]);
            return 1;
        }));
        param12.then(CommandFriend.Ж((String)field7482).executes(param0 -> {
            Minced.getInstance().getFriendManager().method1699();
            MutableText v1 = Text.literal((String)field7465).setStyle(Style.EMPTY.withColor(Formatting.GRAY));
            ChatUtil.method3308(v1, new Object[0]);
            return 1;
        }));
    }

    public String parse(StringReader param1) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        String v2 = param1.readString();
        if (field7486.equals(param1.getString().split(field7487)[1]) && (Minced.getInstance().getFriendManager() == null || !Minced.getInstance().getFriendManager().cfr_renamed_4(v2))) {
            throw new DynamicCommandExceptionType(param0 -> Text.literal((String)((Object)_4ZXYEo.ф9("makeConcatWithConstants", new Object[]{}, (String)String.valueOf(param0))))).create((Object)v2);
        }
        return v2;
    }

    @SuppressWarnings("unchecked")
    public CompletableFuture listSuggestions(CommandContext param1, SuggestionsBuilder param2) {
        String[] v4 = param1.getInput().split(field7488);
        boolean v5 = v4.length > 1 && field7489.equals(v4[1]);
        List<String> v3 = v5 ? ((List<FriendEntry>) Minced.getInstance().getFriendManager().method1705()).stream().map(FriendEntry::method2633).collect(Collectors.toList()) : I.getNetworkHandler().getListedPlayerListEntries().stream().map(param0 -> param0.getProfile().getName()).collect(Collectors.toList());
        return CommandSource.suggestMatching(v3, (SuggestionsBuilder)param2);
    }

    static {
        field7486 = "remove";
        field7487 = " ";
        field7488 = " ";
        field7489 = "remove";
        cfr_renamed_112 = "Друга с именем \u0001 не существует";
        field7465 = "Список друзей очищен!";
        field7472 = "Список друзей пуст!";
        field7473 = "Список друзей:";
        field7474 = "dd.MM.yyyy HH:mm";
        field7475 = " Добавлен: ";
        field7476 = " [Удалить]";
        cfr_renamed_113 = "\u0001\u0001 remove \u0001";
        cfr_renamed_114 = "Клик для удаления\u0001";
        cfr_renamed_115 = "Всего: \u0001";
        field7483 = "friend";
        field7484 = "Друг с именем ";
        cfr_renamed_116 = "\"\u0001\"";
        field7485 = " удалён!";
        field7468 = "player";
        field7469 = "Друг с именем ";
        cfr_renamed_117 = "\"\u0001\"";
        field7470 = " уже существует!";
        field7471 = " добавлен!";
        field7466 = "friend";
        field7467 = "fr";
    }
}



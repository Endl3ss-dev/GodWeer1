/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 */
package minced.core;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import minced.core.StaffEntry;
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

public class CommandStaff
extends ClientCommand
implements ArgumentType {
    static String Рв;
    static String РТ;
    static String РУ;
    static String Рп;

    static String field7417;
    static String field7418;
    static String field7419;
    static String field7420;
    static String field7421;
    static String field7422;
    static String field7423;
    static MinecraftClient I = MinecraftClient.getInstance();
    static String field7424;
    static String field7425;
    static String field7426;
    static String field7427;
    static String field7428;
    static String field7429;
    static String field7430;
    static String field7431;
    static String field7432;
    static String field7433;
    static String field7434;
    static String field7435 = "add";
    static String field7436 = "player";
    static String field7437 = "remove";
    static String field7438 = "player";
    static String field7439 = "list";
    static String field7440 = "clear";

    @SuppressWarnings("unchecked")
    public CompletableFuture listSuggestions(CommandContext param1, SuggestionsBuilder param2) {
        String[] v3 = param1.getInput().split(field7422);
        boolean v4 = v3.length > 1 && field7423.equals(v3[1]);
        List<String> v5 = v4 ? ((List<StaffEntry>) Minced.getInstance().getStaffManager().method1655()).stream().map(x -> (String) x.method1996()).collect(Collectors.toList()) : I.getNetworkHandler().getListedPlayerListEntries().stream().map(param0 -> param0.getProfile().getName()).collect(Collectors.toList());
        return CommandSource.suggestMatching(v5, (SuggestionsBuilder)param2);
    }

    public String parse(StringReader param1) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        String v2 = param1.readString();
        if (field7428.equals(param1.getString().split(field7429)[1]) && (Minced.getInstance().getStaffManager() == null || !Minced.getInstance().getStaffManager().method1650(v2))) {
            throw new DynamicCommandExceptionType(param0 -> Text.literal((String)((Object)_4ZXYEo.cfr_renamed_7("makeConcatWithConstants", new Object[]{}, (String)String.valueOf(param0))))).create((Object)v2);
        }
        return v2;
    }

    public CommandStaff(String ... args) {
        super(field7430);
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
        param12.then(CommandStaff.Ж((String)field7435).then(CommandStaff.Ж((String)field7436, (ArgumentType)new CommandStaff()).executes(param0 -> {
            String v1 = (String)param0.getArgument(field7424, String.class);
            boolean v2 = Minced.getInstance().getStaffManager().method1650(v1);
            if (!v2) {
                Minced.getInstance().getStaffManager().method1649(v1);
            }
            MutableText v3 = Text.literal((String)field7425).setStyle(Style.EMPTY.withColor(Formatting.GRAY));
            MutableText v4 = Text.literal((String)v1).setStyle(Style.EMPTY.withColor(Formatting.WHITE));
            MutableText v5 = Text.literal((String)((Object)_4ZXYEo.т("makeConcatWithConstants", new Object[]{}, (String)(v2 ? field7426 : field7427)))).setStyle(Style.EMPTY.withColor(Formatting.GRAY));
            ChatUtil.method3308(Text.literal((String)"").append((Text)v3).append((Text)v4).append((Text)v5), new Object[0]);
            return 1;
        })));
        param12.then(CommandStaff.Ж((String)field7437).then(CommandStaff.Ж((String)field7438, (ArgumentType)new CommandStaff()).executes(param1 -> {
            String v2 = (String)param1.getArgument(field7419, String.class);
            boolean v3 = Minced.getInstance().getStaffManager().method1650(v2);
            if (!v3) {
                return this.method419(param1);
            }
            Minced.getInstance().getStaffManager().method1652(v2);
            MutableText v4 = Text.literal((String)field7420).setStyle(Style.EMPTY.withColor(Formatting.GRAY));
            MutableText v5 = Text.literal((String)v2).setStyle(Style.EMPTY.withColor(Formatting.WHITE));
            MutableText v6 = Text.literal((String)field7421).setStyle(Style.EMPTY.withColor(Formatting.GRAY));
            ChatUtil.method3308(Text.literal((String)"").append((Text)v4).append((Text)v5).append((Text)v6), new Object[0]);
            return 1;
        })));
        param12.then(CommandStaff.Ж((String)field7439).executes(param1 -> {
            List<StaffEntry> v2 = (List<StaffEntry>) Minced.getInstance().getStaffManager().method1655();
            if (v2 == null || v2.isEmpty()) {
                MutableText v3 = Text.literal((String)field7431).setStyle(Style.EMPTY.withColor(Formatting.GRAY));
                ChatUtil.method3308(v3, new Object[0]);
                return 1;
            }
            MutableText v3 = Text.literal((String)field7432).setStyle(Style.EMPTY.withColor(Formatting.GRAY));
            ChatUtil.method3308(v3, new Object[0]);
            for (StaffEntry v5 : v2) {
                String v6 = (String) v5.method1996();
                MutableText v7 = Text.literal((String)v6).setStyle(Style.EMPTY.withColor(Formatting.WHITE));
                String v12_call = (String) _4ZXYEo.И("makeConcatWithConstants", new Object[]{}, (String)Minced.getInstance().getCommandManager().method74(), (String)this.method416(), (String)v6);
                
                ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, v12_call);
                HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal((String)field7434).setStyle(Style.EMPTY.withColor(Formatting.RED)));
                Style style = Style.EMPTY
                    .withColor(Formatting.RED)
                    .withClickEvent(clickEvent)
                    .withHoverEvent(hoverEvent);
                MutableText v8 = Text.literal((String)field7433).setStyle(style);
                
                ChatUtil.method3308(Text.literal((String)"").append((Text)v7).append((Text)v8), new Object[0]);
            }
            MutableText v4 = Text.literal((String)((Object)_4ZXYEo.с("makeConcatWithConstants", new Object[]{}, (int)v2.size()))).setStyle(Style.EMPTY.withColor(Formatting.GRAY));
            ChatUtil.method3308(v4, new Object[0]);
            return 1;
        }));
        param12.then(CommandStaff.Ж((String)field7440).executes(param0 -> {
            List<StaffEntry> v1 = (List<StaffEntry>) Minced.getInstance().getStaffManager().method1655();
            if (v1 == null || v1.isEmpty()) {
                MutableText v2 = Text.literal((String)field7417).setStyle(Style.EMPTY.withColor(Formatting.GRAY));
                ChatUtil.method3308(v2, new Object[0]);
                return 1;
            }
            Minced.getInstance().getStaffManager().method1653();
            MutableText v2 = Text.literal((String)field7418).setStyle(Style.EMPTY.withColor(Formatting.GRAY));
            ChatUtil.method3308(v2, new Object[0]);
            return 1;
        }));
    }

    static {
        field7428 = "remove";
        field7429 = " ";
        field7422 = " ";
        field7423 = "remove";
        Рв = "Персонала с именем \u0001 не существует";
        field7417 = "Список персонала пуст!";
        field7418 = "Список персонала очищен!";
        field7431 = "Список персонала пуст!";
        field7432 = "Список персонала:";
        field7433 = " [Удалить]";
        РТ = "\u0001\u0001 remove \u0001";
        field7434 = "Клик для удаления персонала";
        РУ = "Всего: \u0001";
        field7419 = "player";
        field7420 = "Персонал с именем '";
        field7421 = "' удалён!";
        field7424 = "player";
        field7425 = "Персонал с именем '";
        field7426 = " уже существует!";
        field7427 = " добавлен!";
        Рп = "'\u0001";
        field7430 = "staff";
    }
}



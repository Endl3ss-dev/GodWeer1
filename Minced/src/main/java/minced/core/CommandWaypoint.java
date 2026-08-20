/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 */
package minced.core;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import minced.core.PathUtil;
import minced.core.ServerDetector;
import minced.core.ChatUtil;
import minced.core.ClientCommand;
import minced.core.Minced;
import minced.manager.Waypoint;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import sLM._4ZXYEo;
import sg.ec._2u;

public class CommandWaypoint
extends ClientCommand
implements ArgumentType {
    static String cfr_renamed_28;
    static String cfr_renamed_29;
    static String cfr_renamed_30;
    static String cfr_renamed_31;
    static String cfr_renamed_32;
    static String cfr_renamed_33;
    static String cfr_renamed_34;
    static String cfr_renamed_35;

    static String field7077;
    static String field7078;
    static String field7079 = "waypoint";
    static String field7080 = "wp";
    static String field7081;
    static String field7082;
    static String field7083;
    static String field7084;
    static String field7085;
    static String field7086;
    static String field7087;
    static String field7088;
    static MinecraftClient I = MinecraftClient.getInstance();
    static String field7089;
    static String field7090;
    static String field7091;
    static String field7092;
    static String field7093;
    static String field7094;
    static String field7095;
    static String field7096 = "add";
    static String field7097 = "name";
    static String field7098 = "x";
    static String field7099 = "y";
    static String field7100 = "z";
    static String field7101 = "remove";
    static String field7102 = "name";
    static String field7103 = "clear";
    static String field7104 = "list";
    static String field7105;
    static String field7106;
    static String field7107;
    static String field7108;

    public String parse(StringReader param1) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        return param1.readString();
    }

    public CommandWaypoint(String ... args) {
        super(field7079, field7080);
    }

    @SuppressWarnings("unchecked")
    public CompletableFuture listSuggestions(CommandContext param1, SuggestionsBuilder param2) {
        boolean v4;
        String[] v3 = param1.getInput().split(field7081);
        boolean bl = v4 = v3.length > 1 && field7082.equalsIgnoreCase(v3[1]);
        if (!v4) {
            return Suggestions.empty();
        }
        List<String> v5 = ((List<_2u.ch>) Minced.getInstance().getWaypointManager().method1729(ServerDetector.method2685())).stream().map(x -> (String) x.弟()).sorted(String.CASE_INSENSITIVE_ORDER).collect(Collectors.toList());
        return CommandSource.suggestMatching(v5, (SuggestionsBuilder)param2);
    }

    private static String fmt1(double param0) {
        return String.format(Locale.US, field7095, param0);
    }

    public static LiteralArgumentBuilder Ж(String name) {
        return ClientCommand.method420(name);
    }

    public static RequiredArgumentBuilder Ж(String name, ArgumentType<?> type) {
        return ClientCommand.method417(name, type);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void run(LiteralArgumentBuilder param13) {
        param13.executes(param1 -> this.method419(param1));
        LiteralArgumentBuilder v2_literal = CommandWaypoint.Ж((String)field7096);
        RequiredArgumentBuilder v3_req = CommandWaypoint.Ж((String)field7097, (ArgumentType)StringArgumentType.word()).suggests((param0, param1) -> {
            List<String> v2_suggests = ((List<_2u.ch>) Minced.getInstance().getWaypointManager().method1729(ServerDetector.method2685())).stream().map(x -> (String) x.弟()).sorted(String.CASE_INSENSITIVE_ORDER).collect(Collectors.toList());
            return CommandSource.suggestMatching(v2_suggests, (SuggestionsBuilder)param1);
        });
        v3_req.executes(param0 -> {
            if (CommandWaypoint.I.player == null) {
                return 1;
            }
            String v1 = (String)param0.getArgument(field7089, String.class);
            double v2_x = CommandWaypoint.I.player.getX();
            double v4 = CommandWaypoint.I.player.getY();
            double v6 = CommandWaypoint.I.player.getZ();
            Minced.getInstance().getWaypointManager().method1727(ServerDetector.method2685(), v1, v2_x, v4, v6);
            ChatUtil.method3308(Text.literal((String)"").append((Text)Text.literal((String)field7090).setStyle(Style.EMPTY.withColor(Formatting.GRAY))).append((Text)Text.literal((String)((Object)_4ZXYEo.дв("makeConcatWithConstants", new Object[]{}, (String)v1))).setStyle(Style.EMPTY.withColor(Formatting.WHITE))).append((Text)Text.literal((String)field7091).setStyle(Style.EMPTY.withColor(Formatting.GRAY))).append((Text)Text.literal((String)((Object)_4ZXYEo.дэ("makeConcatWithConstants", new Object[]{}, (String)CommandWaypoint.fmt1(v2_x), (String)CommandWaypoint.fmt1(v4), (String)CommandWaypoint.fmt1(v6)))).setStyle(Style.EMPTY.withColor(Formatting.WHITE))), new Object[0]);
            return 1;
        });
        v3_req.then(CommandWaypoint.Ж((String)field7098, (ArgumentType)DoubleArgumentType.doubleArg()).then(CommandWaypoint.Ж((String)field7099, (ArgumentType)DoubleArgumentType.doubleArg()).then(CommandWaypoint.Ж((String)field7100, (ArgumentType)DoubleArgumentType.doubleArg()).executes(param0 -> {
            String v1 = (String)param0.getArgument(field7083, String.class);
            double v2_parsed = (Double)param0.getArgument(field7084, Double.class);
            double v4 = (Double)param0.getArgument(field7085, Double.class);
            double v6 = (Double)param0.getArgument(field7086, Double.class);
            Minced.getInstance().getWaypointManager().method1727(ServerDetector.method2685(), v1, v2_parsed, v4, v6);
            ChatUtil.method3308(Text.literal((String)"").append((Text)Text.literal((String)field7087).setStyle(Style.EMPTY.withColor(Formatting.GRAY))).append((Text)Text.literal((String)((Object)_4ZXYEo.дт("makeConcatWithConstants", new Object[]{}, (String)v1))).setStyle(Style.EMPTY.withColor(Formatting.WHITE))).append((Text)Text.literal((String)field7088).setStyle(Style.EMPTY.withColor(Formatting.GRAY))).append((Text)Text.literal((String)((Object)_4ZXYEo.д3("makeConcatWithConstants", new Object[]{}, (String)CommandWaypoint.fmt1(v2_parsed), (String)CommandWaypoint.fmt1(v4), (String)CommandWaypoint.fmt1(v6)))).setStyle(Style.EMPTY.withColor(Formatting.WHITE))), new Object[0]);
            return 1;
        }))));
        v2_literal.then((ArgumentBuilder)v3_req);
        param13.then((ArgumentBuilder)v2_literal);
        param13.then(CommandWaypoint.Ж((String)field7101).then(CommandWaypoint.Ж((String)field7102, (ArgumentType)new CommandWaypoint()).executes(param0 -> {
            String v1 = (String)param0.getArgument(field7105, String.class);
            Waypoint v2_manager = Minced.getInstance().getWaypointManager();
            boolean v3_bool = v2_manager.method1723(ServerDetector.method2685(), v1);
            if (!v3_bool) {
                ChatUtil.method3308(Text.literal((String)field7106).setStyle(Style.EMPTY.withColor(Formatting.GRAY)), new Object[0]);
                return 1;
            }
            ChatUtil.method3308(Text.literal((String)"").append((Text)Text.literal((String)field7107).setStyle(Style.EMPTY.withColor(Formatting.GRAY))).append((Text)Text.literal((String)((Object)_4ZXYEo.дс("makeConcatWithConstants", new Object[]{}, (String)v1))).setStyle(Style.EMPTY.withColor(Formatting.WHITE))).append((Text)Text.literal((String)field7108).setStyle(Style.EMPTY.withColor(Formatting.GRAY))), new Object[0]);
            return 1;
        })));
        param13.then(CommandWaypoint.Ж((String)field7103).executes(param0 -> {
            Minced.getInstance().getWaypointManager().method1728(ServerDetector.method2685());
            ChatUtil.method3308(Text.literal((String)field7094).setStyle(Style.EMPTY.withColor(Formatting.GRAY)), new Object[0]);
            return 1;
        }));
        param13.then(CommandWaypoint.Ж((String)field7104).executes(param12 -> {
            List<_2u.ch> v2_list = (List<_2u.ch>) Minced.getInstance().getWaypointManager().method1729(ServerDetector.method2685());
            if (v2_list == null || v2_list.isEmpty()) {
                ChatUtil.method3308(Text.literal((String)field7092).setStyle(Style.EMPTY.withColor(Formatting.GRAY)), new Object[0]);
                return 1;
            }
            ChatUtil.method3308(Text.literal((String)field7093).setStyle(Style.EMPTY.withColor(Formatting.GRAY)), new Object[0]);
            v2_list.stream().sorted(Comparator.comparing(x -> (String) x.弟(), String.CASE_INSENSITIVE_ORDER)).forEach(param1 -> {
                String v2_waypoint_name = (String) param1.弟();
                MutableText v3_text = Text.literal(v2_waypoint_name).setStyle(Style.EMPTY.withColor(Formatting.WHITE));
                MutableText v4_text = Text.literal((String)((Object)_4ZXYEo.д0("makeConcatWithConstants", new Object[]{}, (String)CommandWaypoint.fmt1(((Number)param1.Б()).doubleValue()), (String)CommandWaypoint.fmt1(((Number)param1.f13()).doubleValue()), (String)CommandWaypoint.fmt1(((Number)param1.С()).doubleValue())))).setStyle(Style.EMPTY.withColor(Formatting.GRAY));
                String v5_prefix = Minced.getInstance().getCommandManager().method74();
                MutableText v6_text = Text.literal((String)field7077).setStyle(Style.EMPTY.withColor(Formatting.RED).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, (String)((Object)_4ZXYEo.дИ("makeConcatWithConstants", new Object[]{}, (String)v5_prefix, (String)this.method416(), (String)v2_waypoint_name)))).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal((String)field7078).setStyle(Style.EMPTY.withColor(Formatting.RED)))));
                ChatUtil.method3308(Text.literal("").append(v3_text).append(v4_text).append(v6_text), new Object[0]);
            });
            ChatUtil.method3308(Text.literal((String)((Object)_4ZXYEo.дВ("makeConcatWithConstants", new Object[]{}, (int)v2_list.size()))).setStyle(Style.EMPTY.withColor(Formatting.GRAY)), new Object[0]);
            return 1;
        }));
    }

    static {
        field7095 = "%.1f";
        field7081 = " ";
        field7082 = "remove";
        field7092 = "Список waypoints пуст!";
        field7093 = "Waypoints:";
        cfr_renamed_28 = "Всего: \u0001";
        cfr_renamed_29 = "  x: \u0001 y: \u0001 z: \u0001";
        field7077 = " [Удалить]";
        cfr_renamed_30 = "\u0001\u0001 remove \u0001";
        field7078 = "Клик чтобы удалить waypoint";
        field7094 = "Waypoints очищены!";
        field7105 = "name";
        field7106 = "Waypoint с таким именем не найден!";
        field7107 = "Waypoint ";
        cfr_renamed_31 = "\"\u0001\"";
        field7108 = " удалён!";
        field7083 = "name";
        field7084 = "x";
        field7085 = "y";
        field7086 = "z";
        field7087 = "Waypoint ";
        cfr_renamed_32 = "\"\u0001\"";
        field7088 = " добавлен: ";
        cfr_renamed_33 = "x: \u0001 y: \u0001 z: \u0001";
        field7089 = "name";
        field7090 = "Waypoint ";
        cfr_renamed_34 = "\"\u0001\"";
        field7091 = " добавлен: ";
        cfr_renamed_35 = "x: \u0001 y: \u0001 z: \u0001";
    }
}



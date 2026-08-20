/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.Command
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  sg.ec.tch
 */
package minced.core;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import minced.core.ClientCommand;
import sg.ec.tch;

public class CommandSay
extends ClientCommand
 {
    static String field7544;
    static String field7545;
    static String field7546 = "say";
    static String field7547 = "chat";
    static String field7548 = "line";

    public CommandSay() {
        super(field7546, field7547);
    }

    public static com.mojang.brigadier.builder.LiteralArgumentBuilder Ж(String name) {
        return ClientCommand.method420(name);
    }

    public static com.mojang.brigadier.builder.RequiredArgumentBuilder Ж(String name, com.mojang.brigadier.arguments.ArgumentType<?> type) {
        return ClientCommand.method417(name, type);
    }

    @Override
    public void run(LiteralArgumentBuilder param1) {
        param1.executes(param0 -> 1);
        param1.then(CommandSay.Ж((String)field7548, (ArgumentType)StringArgumentType.greedyString()).executes(param0 -> {
            String v1 = StringArgumentType.getString((CommandContext)param0, (String)field7544);
            String[] v2 = v1.trim().isEmpty() ? new String[]{} : v1.trim().split(field7545);
            tch.Б((Object)v2);
            return 1;
        }));
    }

    static {
        field7544 = "line";
        field7545 = "\\s+";
    }
}


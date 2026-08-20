/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.Command
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.DoubleArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.Style
 *  net.minecraft.text.Text
 *  net.minecraft.util.Formatting
 *  sLM.4ZXYEo
 */
package minced.core;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import minced.core.ChatUtil;
import minced.core.ClientCommand;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import sLM._4ZXYEo;

public class CommandGps
extends ClientCommand
 {
    static String 必д;
    static String 必Т;

    static String field7515 = "gps";
    public static boolean field7516;
    static String field7517;
    public static double field7518;
    public static double field7519;
    static String field7520;
    static String field7521;
    static String field7522;
    static String field7523;
    static String field7524;
    static String field7525;
    static String field7526;
    static String field7527;
    static String field7528;
    static String field7529;
    static String field7530 = "off";
    static String field7531 = "info";
    static String field7532 = "x";
    static String field7533 = "z";
    static String field7534;
    static String field7535;
    static String field7536;
    static String field7537;
    static String field7538;
    static String field7539;
    static String field7540;
    static String field7541;
    static String field7542;
    static String field7543;

    public CommandGps() {
        super(field7515);
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
        param12.then(CommandGps.Ж((String)field7530).executes(param0 -> {
            if (!field7516) {
                MutableText v1 = Text.literal((String)field7517).setStyle(Style.EMPTY.withColor(Formatting.GRAY));
                ChatUtil.method3308(v1, new Object[0]);
            } else {
                field7516 = false;
                field7518 = 0.0;
                field7519 = 0.0;
                MutableText v1 = Text.literal((String)field7520).setStyle(Style.EMPTY.withColor(Formatting.GRAY));
                ChatUtil.method3308(v1, new Object[0]);
            }
            return 1;
        }));
        param12.then(CommandGps.Ж((String)field7531).executes(param0 -> {
            if (!field7516) {
                MutableText v1 = Text.literal((String)field7534).setStyle(Style.EMPTY.withColor(Formatting.GRAY));
                ChatUtil.method3308(v1, new Object[0]);
            } else {
                MutableText v1 = Text.literal((String)field7535).setStyle(Style.EMPTY.withColor(Formatting.GRAY));
                MutableText v2 = Text.literal((String)field7536).setStyle(Style.EMPTY.withColor(Formatting.GRAY));
                MutableText v3 = Text.literal((String)String.format(field7537, field7518).replace(field7538, field7539)).setStyle(Style.EMPTY.withColor(Formatting.WHITE));
                MutableText v4 = Text.literal((String)field7540).setStyle(Style.EMPTY.withColor(Formatting.GRAY));
                MutableText v5 = Text.literal((String)String.format(field7541, field7519).replace(field7542, field7543)).setStyle(Style.EMPTY.withColor(Formatting.WHITE));
                ChatUtil.method3308(Text.literal((String)"").append((Text)v1).append((Text)v2).append((Text)v3).append((Text)v4).append((Text)v5), new Object[0]);
            }
            return 1;
        }));
        param12.then(((RequiredArgumentBuilder)CommandGps.Ж((String)field7532, (ArgumentType)DoubleArgumentType.doubleArg()).executes(param1 -> this.method419(param1))).then(CommandGps.Ж((String)field7533, (ArgumentType)DoubleArgumentType.doubleArg()).executes(param0 -> {
            double v1 = (Double)param0.getArgument(field7521, Double.class);
            double v3 = (Double)param0.getArgument(field7522, Double.class);
            field7516 = true;
            field7518 = v1;
            field7519 = v3;
            MutableText v5 = Text.literal((String)field7523).setStyle(Style.EMPTY.withColor(Formatting.GRAY));
            MutableText v6 = Text.literal((String)((Object)_4ZXYEo.cfr_renamed_6("makeConcatWithConstants", new Object[]{}, (String)String.format(field7524, field7518).replace(field7525, field7526)))).setStyle(Style.EMPTY.withColor(Formatting.WHITE));
            MutableText v7 = Text.literal((String)((Object)_4ZXYEo.西("makeConcatWithConstants", new Object[]{}, (String)String.format(field7527, field7519).replace(field7528, field7529)))).setStyle(Style.EMPTY.withColor(Formatting.WHITE));
            ChatUtil.method3308(Text.literal((String)"").append((Text)v5).append((Text)v6).append((Text)v7), new Object[0]);
            return 1;
        })));
    }

    public static void method1836() {
        field7516 = false;
        field7518 = 0.0;
        field7519 = 0.0;
    }

    static {
        field7516 = false;
        field7518 = 0.0;
        field7519 = 0.0;
        field7521 = "x";
        field7522 = "z";
        field7523 = "GPS установлен на координаты: ";
        field7524 = "%.1f";
        field7525 = ",";
        field7526 = ".";
        必д = "x: \u0001";
        field7527 = "%.1f";
        field7528 = ",";
        field7529 = ".";
        必Т = " z: \u0001";
        field7534 = "Сейчас GPS отключен!";
        field7535 = "Информация о текущем GPS: ";
        field7536 = "x: ";
        field7537 = "%.1f";
        field7538 = ",";
        field7539 = ".";
        field7540 = " z: ";
        field7541 = "%.1f";
        field7542 = ",";
        field7543 = ".";
        field7517 = "GPS уже отключен!";
        field7520 = "GPS отключен!";
        CommandGps.method1836();
    }
}




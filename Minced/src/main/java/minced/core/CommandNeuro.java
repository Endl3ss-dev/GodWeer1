/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.Command
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.Style
 *  net.minecraft.text.Text
 *  net.minecraft.util.Formatting
 *  sLM.4ZXYEo
 */
package minced.core;

import minced.module.movement.Speed;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import minced.core.PathUtil;
import minced.core.ChatUtil;
import minced.core.ClientCommand;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import sLM._4ZXYEo;

public class CommandNeuro
extends ClientCommand
 {
    static String 弟и;
    static String 弟ш;
    static String 弟Ч;

    static String field7490 = "neuro";
    static String field7491;
    static String field7492;
    static String field7493;
    static String field7494;
    static String field7495;
    static String field7496;
    static String field7497;
    static String field7498;
    static String field7499;
    static String field7500;
    static String field7501;
    static String field7502;
    static String field7503;
    static String field7504;
    static String field7505;
    static String field7506 = "help";

    public CommandNeuro() {
        super(field7490);
    }

    private static void method1825(Formatting param0, String param1) {
        ChatUtil.method3308(Text.literal((String)param1).setStyle(Style.EMPTY.withColor(param0)), new Object[0]);
    }

    private static void method1826() {
        ChatUtil.method3308(Text.literal((String)field7505).setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY)), new Object[0]);
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
        param1.then(CommandNeuro.Ж((String)field7506).executes(param0 -> {
            CommandNeuro.method1827(field7491);
            CommandNeuro.method1826();
            CommandNeuro.method1825(Formatting.YELLOW, field7492);
            CommandNeuro.method1825(Formatting.GRAY, (String)((Object)_4ZXYEo.з("makeConcatWithConstants", new Object[]{}, (int)30000)));
            CommandNeuro.method1825(Formatting.YELLOW, field7493);
            CommandNeuro.method1825(Formatting.GRAY, field7494);
            CommandNeuro.method1825(Formatting.GRAY, field7495);
            CommandNeuro.method1825(Formatting.GRAY, field7496);
            CommandNeuro.method1825(Formatting.GRAY, field7497);
            CommandNeuro.method1825(Formatting.YELLOW, field7498);
            CommandNeuro.method1825(Formatting.GRAY, field7499);
            CommandNeuro.method1825(Formatting.YELLOW, field7500);
            CommandNeuro.method1825(Formatting.GRAY, field7501);
            CommandNeuro.method1825(Formatting.YELLOW, (String)((Object)_4ZXYEo.е("makeConcatWithConstants", new Object[]{}, (int)30000)));
            CommandNeuro.method1825(Formatting.YELLOW, field7502);
            CommandNeuro.method1826();
            CommandNeuro.method1825(Formatting.AQUA, field7503);
            CommandNeuro.method1825(Formatting.WHITE, (String)((Object)_4ZXYEo.t2("makeConcatWithConstants", new Object[]{}, (String)PathUtil.field7558)));
            return 1;
        }));
    }

    private static void method1827(String param0) {
        MutableText v1 = Text.literal((String)param0).setStyle(Style.EMPTY.withColor(Formatting.GOLD).withBold(Boolean.valueOf(true)));
        ChatUtil.method3308(v1, new Object[0]);
    }

    static {
        field7505 = " ";
        field7491 = "Как всё таки обучить эту нейро ротацию?";
        field7492 = "1. Attack Aura включена, в «Обход» режим Neuro.";
        弟и = "   До \u0001 сэмплов ротация ауры отключена — крути камеру сам.";
        field7493 = "2. Neuro Train: для полного профиля включи все пункты.";
        field7494 = "   Yaw/Pitch Speed — сила поворота относительно ошибки угла.";
        field7495 = "   Rotation Point — куда на хитбоксе смотришь при наведении.";
        field7496 = "   Hit Point — точка попадания в момент атаки.";
        field7497 = "   Behavior — дрожь, плавность, короткие «паузы» (стиль мыши).";
        field7498 = "3. Держи цель в радиусе ауры.";
        field7499 = "   Тоесть ты должен сам наводиться, чит будет бить за тебя, мышкой ты просто водишь по цели";
        field7500 = "4. Прогресс на экране: в Attack Aura включи «Прогресс обучения».";
        field7501 = "   Профиль на диск сохраняется реже во время обучения (меньше лагов); при выключении ауры — всегда.";
        弟ш = "5. После \u0001 сэмплов автоматически включается обученная ротация.";
        field7502 = "6. Сброс: настройка Neuro Reset в аурe (или удали файл профиля).";
        field7503 = "Сохранить пресет: скопируй файл (выключи ауру перед копированием):";
        弟Ч = "\u0001temp\\aura_neuro.file";
        field7504 = "Использование: .neuro tutorial";
    }
}




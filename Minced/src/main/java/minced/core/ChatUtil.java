/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.Style
 *  net.minecraft.text.Text
 *  net.minecraft.text.TextColor
 */
package minced.core;

import minced.core.ColorUtil;
import minced.core.ThemeColors;
import minced.enums.MAIN;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;

public class ChatUtil {
    static Object н;

    static MinecraftClient I = MinecraftClient.getInstance();
    static String field11688;
    static String field11689;
    static float field11690;
    static String field11691;
    static String field11692;
    static int field11693;
    static String field11694;
    static int field11695;
    static String field11696;
    static String field11697;
    static int field11698;

    public static void method3308(Object param0, Object ... param1) {
        MutableText v8;
        if (ChatUtil.I.player == null) {
            return;
        }
        if (param0 == null) {
            ChatUtil.method3308(field11688, new Object[0]);
            return;
        }
        int v3 = ThemeColors.method3199(MAIN.LOGO);
        int v4 = ColorUtil.method1851(v3, field11690);
        MutableText v5 = Text.literal((String)"");
        for (int v6 = 0; v6 < field11691.length(); ++v6) {
            Style v9;
            char v7 = field11692.charAt(v6);
            v8 = Text.literal((String)String.valueOf(v7));
            if (v7 == '⇨') {
                v9 = Style.EMPTY.withColor(TextColor.fromRgb((int)field11693));
            } else {
                float v10 = (float)v6 / (float)(field11694.length() - 1);
                int v11 = ColorUtil.method1850(v3, v4, v10);
                v9 = Style.EMPTY.withColor(TextColor.fromRgb((int)(v11 & field11695))).withBold(Boolean.valueOf(true));
            }
            v8.setStyle(v9);
            v5.append((Text)v8);
        }
        v5.append((Text)Text.literal((String)field11696));
        if (param0 instanceof Text) {
            v5.append((Text)param0);
        } else {
            String v6 = String.format(param0.toString(), param1).replace('&', '§');
            String v7 = v6.replaceAll(field11697, "");
            v8 = Text.literal((String)v7);
            v8.setStyle(Style.EMPTY.withColor(TextColor.fromRgb((int)field11698)));
            v5.append((Text)v8);
        }
        ChatUtil.I.inGameHud.getChatHud().addMessage((Text)v5);
    }

    static {
        н = '\u0000';
        field11688 = "Object is null";
        field11689 = "Minced ->";
        field11690 = 0.5f;
        field11691 = "Minced ->";
        field11692 = "Minced ->";
        field11693 = 0x888888;
        field11694 = "Minced ->";
        field11695 = 0xFFFFFF;
        field11696 = " ";
        field11697 = "§[0-9a-fk-or]";
        field11698 = 0xFFFFFF;
    }
}




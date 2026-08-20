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
 *  sg.ec.pt
 *  sg.ec.ТФ$2
 *  sg.ec.ТФ$Ч
 */
package minced.core;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import minced.core.ColorUtil;
import minced.core.ThemeColors;
import minced.core.ChatUtil;
import minced.core.ClientCommand;
import minced.enums.MAIN;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import sLM._4ZXYEo;
import sg.ec.pt;
import sg.ec.tf;

public class CommandHelp
extends ClientCommand
 {
    static String ннх;
    static String ннЮ;
    static String ннl;

    static String field6875 = "help";
    static String field6876;
    static String field6877;
    static float field6878;
    static String field6879;
    static String field6880;
    static String field6881;
    static String field6882;
    static String field6883;
    static String field6884;
    static String field6885;
    static String field6886;
    static String field6887;
    static String field6888;
    static String field6889;
    static String field6890;
    static String field6891;
    static String field6892;
    static String field6893;
    static String field6894;
    static String field6895;
    static String field6896;
    static String field6897;
    static String field6898;
    static String field6899;
    static String field6900;
    static String field6901;
    static String field6902;
    static String field6903;
    static String field6904;
    static String field6905;
    static String field6906;
    static String field6907;
    static String field6908;
    static String field6909;
    static String field6910;
    static String field6911;
    static String field6912;
    static String field6913;
    static String field6914;
    static String field6915;
    static String field6916;
    static String field6917;
    static String field6918;
    static String field6919;
    static String field6920;
    static String field6921;
    static String field6922;
    static String field6923;
    static String field6924;
    static String field6925;
    static String field6926;
    static String field6927;
    static String field6928;
    static String field6929;
    static String field6930;
    static String field6931;
    static String field6932;
    static String field6933;
    static String field6934;
    static String field6935;
    static String field6936;
    static String field6937;
    static String field6938;
    static String field6939;
    static String field6940;
    static String field6941;
    static String field6942;
    static String field6943;
    static String field6944;
    static String field6945;
    static String field6946;
    static String field6947;
    static String field6948;
    static String field6949;
    static String field6950;

    public CommandHelp() {
        super(field6875);
    }

    private void method1734(String param1, String param2, int param3, int param4) {
        if (param1 != null) {
            MutableText command = Text.literal("." + param1).setStyle(Style.EMPTY.withColor(param3));
            MutableText alias = Text.literal(" -> ." + String.valueOf(param2)).setStyle(Style.EMPTY.withColor(Formatting.GRAY));
            ChatUtil.method3308(command.append((Text)alias), new Object[0]);
            return;
        }
        MutableText v5 = pt.Ю((String)((Object)_4ZXYEo.cfr_renamed_13("makeConcatWithConstants", new Object[]{}, (String)param1)), (int)param3, (int)param4);
        MutableText v6 = Text.literal((String)((Object)_4ZXYEo.cfr_renamed_14("makeConcatWithConstants", new Object[]{}, (String)param2))).setStyle(Style.EMPTY.withColor(Formatting.GRAY));
        ChatUtil.method3308(Text.literal((String)"").append((Text)v5).append((Text)v6), new Object[0]);
    }

    @Override
    public void run(LiteralArgumentBuilder param12) {
        param12.executes(param1 -> {
            int v2 = ThemeColors.method3199(MAIN.LOGO);
            int v3 = ColorUtil.method1851(v2, field6878);
            tf._2[] v4 = new tf._2[]{new tf._2(field6879, new String[]{field6880, field6881, field6882, field6883, field6884, field6885, field6886}, tf.ch.PARENTHESES, field6887), new tf._2(field6888, new String[]{field6889, field6890, field6891, field6892}, tf.ch.PARENTHESES, field6893), new tf._2(field6894, new String[]{field6895, field6896, field6897, field6898}, tf.ch.PARENTHESES, field6899), new tf._2(field6900, new String[]{field6901, field6902, field6903, field6904}, tf.ch.PARENTHESES), new tf._2(field6905, new String[]{field6906, field6907, field6908, field6909}, tf.ch.PARENTHESES), new tf._2(field6910, new String[]{field6911, field6912, field6913}, tf.ch.SQUARE), new tf._2(field6914, new String[]{field6915, field6916, field6917, field6918}, tf.ch.SQUARE, field6919), new tf._2(field6920, new String[]{field6921, field6922, field6923, field6924, field6925}, tf.ch.PARENTHESES, field6926), new tf._2(field6927, new String[]{field6928}, tf.ch.ANGLE), new tf._2(field6929, new String[]{field6930}, tf.ch.SQUARE), new tf._2(field6931, new String[]{field6932}, tf.ch.ANGLE, field6933), new tf._2(field6934, new String[]{field6935, field6936, field6937, field6938}, tf.ch.PARENTHESES, field6939), new tf._2(field6940, new String[]{field6941, field6942}, tf.ch.SQUARE, field6943), new tf._2(field6944, new String[]{field6945, field6946, field6947}, tf.ch.PARENTHESES, field6948), new tf._2(field6949, new String[]{field6950}, tf.ch.PARENTHESES)};
            for (int v7 = 0; v7 < 15; ++v7) {
                tf._2 v8 = v4[v7];
                this.method1735(v8.нI, v8.ф, v8.н, v2, v3);
                if (v8.н2 == null) continue;
                this.method1734(v8.н2, v8.нI, v2, v3);
            }
            return 1;
        });
    }

    private void method1735(String param1, String[] param2, tf.ch param3, int param4, int param5) {
        if (param1 != null) {
            String opening = param3 == null ? "" : param3.cfr_renamed_16;
            String closing = param3 == null ? "" : param3.cfr_renamed_17;
            MutableText command = Text.literal("." + param1).setStyle(Style.EMPTY.withColor(param4));
            MutableText arguments = Text.literal(opening).setStyle(Style.EMPTY.withColor(Formatting.GRAY));
            for (int index = 0; index < param2.length; ++index) {
                arguments.append((Text)Text.literal(String.valueOf(param2[index])).setStyle(Style.EMPTY.withColor(Formatting.GRAY)));
                if (index < param2.length - 1) {
                    arguments.append((Text)Text.literal(field6876).setStyle(Style.EMPTY.withColor(Formatting.GRAY)));
                }
            }
            arguments.append((Text)Text.literal(closing).setStyle(Style.EMPTY.withColor(Formatting.GRAY)));
            ChatUtil.method3308(command.append((Text)Text.literal(field6877)).append((Text)arguments), new Object[0]);
            return;
        }
        MutableText v6 = pt.Ю((String)((Object)_4ZXYEo.cfr_renamed_15("makeConcatWithConstants", new Object[]{}, (String)param1)), (int)param4, (int)param5);
        MutableText v7 = Text.literal((String)param3.cfr_renamed_16).setStyle(Style.EMPTY.withColor(Formatting.GRAY));
        for (int v8 = 0; v8 < param2.length; ++v8) {
            v7.append((Text)Text.literal((String)param2[v8]).setStyle(Style.EMPTY.withColor(Formatting.GRAY)));
            if (v8 >= param2.length - 1) continue;
            v7.append((Text)Text.literal((String)field6876).setStyle(Style.EMPTY.withColor(Formatting.GRAY)));
        }
        v7.append((Text)Text.literal((String)param3.cfr_renamed_17).setStyle(Style.EMPTY.withColor(Formatting.GRAY)));
        ChatUtil.method3308(Text.literal((String)"").append((Text)v6).append((Text)Text.literal((String)field6877)).append((Text)v7), new Object[0]);
    }

    static {
        ннх = ".\u0001";
        field6876 = " ¦ ";
        field6877 = " ";
        ннЮ = ".\u0001";
        ннl = " -> \u0001";
        field6878 = 0.5f;
        field6879 = "config";
        field6880 = "clear";
        field6881 = "dir";
        field6882 = "list";
        field6883 = "load";
        field6884 = "remove";
        field6885 = "save";
        field6886 = "reset";
        field6887 = "cfg";
        field6888 = "friend";
        field6889 = "add";
        field6890 = "clear";
        field6891 = "list";
        field6892 = "remove";
        field6893 = "fr";
        field6894 = "macros";
        field6895 = "add";
        field6896 = "clear";
        field6897 = "list";
        field6898 = "remove";
        field6899 = "mac";
        field6900 = "bind";
        field6901 = "add";
        field6902 = "clear";
        field6903 = "list";
        field6904 = "remove";
        field6905 = "staff";
        field6906 = "add";
        field6907 = "clear";
        field6908 = "list";
        field6909 = "remove";
        field6910 = "gps";
        field6911 = "off";
        field6912 = "info";
        field6913 = "<pos>";
        field6914 = "waypoint";
        field6915 = "add";
        field6916 = "remove";
        field6917 = "list";
        field6918 = "clear";
        field6919 = "way";
        field6920 = "inv";
        field6921 = "save";
        field6922 = "load";
        field6923 = "remove";
        field6924 = "list";
        field6925 = "clear";
        field6926 = "inventory";
        field6927 = "prefix";
        field6928 = "<prefix>";
        field6929 = "help";
        field6930 = "<command>";
        field6931 = "autocontract";
        field6932 = "<nickname>";
        field6933 = "act";
        field6934 = "nuker";
        field6935 = "add";
        field6936 = "remove";
        field6937 = "clear";
        field6938 = "list";
        field6939 = "nuk";
        field6940 = "vclip";
        field6941 = "<y>";
        field6942 = "<y> <extra>";
        field6943 = "yclip";
        field6944 = "fakeplayer";
        field6945 = "add";
        field6946 = "del";
        field6947 = "remove";
        field6948 = "fp";
        field6949 = "neuro";
        field6950 = "tutorial";
    }
}



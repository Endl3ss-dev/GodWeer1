/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.Command
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.network.PlayerListEntry
 *  net.minecraft.scoreboard.Scoreboard
 *  net.minecraft.scoreboard.ScoreboardDisplaySlot
 *  net.minecraft.scoreboard.ScoreboardEntry
 *  net.minecraft.scoreboard.ScoreboardObjective
 *  net.minecraft.scoreboard.Team
 *  net.minecraft.text.Style
 *  net.minecraft.text.Text
 *  net.minecraft.util.Formatting
 *  sLM.4ZXYEo
 */
package minced.core;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.io.File;
import java.io.IOException;
import java.lang.invoke.CallSite;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import minced.core.PathUtil;
import minced.core.ColorUtil;
import minced.core.ChatUtil;
import minced.core.ClientCommand;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardEntry;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import sLM._4ZXYEo;

public class CommandParse
extends ClientCommand
 {
    static String cfr_renamed_37;
    static String cfr_renamed_38;
    static String cfr_renamed_39;
    static String cfr_renamed_40;
    static String cfr_renamed_41;
    static String cfr_renamed_42;
    static String cfr_renamed_43;
    static String cfr_renamed_44;
    static String cfr_renamed_45;
    static String cfr_renamed_46;
    static String cfr_renamed_47;
    static String cfr_renamed_48;
    static String cfr_renamed_49;
    static String cfr_renamed_50;
    static String cfr_renamed_51;
    static String cfr_renamed_52;
    static String cfr_renamed_53;
    static String cfr_renamed_54;
    static String cfr_renamed_55;
    static String cfr_renamed_56;
    static String cfr_renamed_57;
    static String cfr_renamed_58;
    static String cfr_renamed_59;
    static String cfr_renamed_60;
    static String cfr_renamed_61;
    static String cfr_renamed_62;
    static String cfr_renamed_63;
    static String cfr_renamed_64;
    static String cfr_renamed_65;
    static String cfr_renamed_66;
    static String cfr_renamed_67;
    static String cfr_renamed_68;
    static String cfr_renamed_69;
    static String cfr_renamed_70;
    static String cfr_renamed_71;
    static String cfr_renamed_72;
    static String cfr_renamed_73;
    static String cfr_renamed_74;
    static String cfr_renamed_75;
    static String cfr_renamed_76;
    static String cfr_renamed_77;
    static String cfr_renamed_78;
    static String cfr_renamed_79;
    static String cfr_renamed_80;
    static String cfr_renamed_81;
    static String cfr_renamed_82;
    static String cfr_renamed_83;
    static String cfr_renamed_84;
    static String cfr_renamed_85;
    static String cfr_renamed_86;
    static String cfr_renamed_87;
    static String cfr_renamed_88;
    static String cfr_renamed_89;
    static String cfr_renamed_90;

    static MinecraftClient I = MinecraftClient.getInstance();
    static String field7109;
    static String field7110;
    static String field7111;
    static String field7112;
    static String field7113;
    static String field7114;
    static String field7115;
    static String field7116;
    static String field7117;
    static String field7118;
    static String field7119;
    static String field7120;
    static String field7121;
    static String field7122;
    static String field7123;
    static String field7124;
    static SimpleDateFormat field7125;
    static String field7126;
    boolean field7127 = true;
    static String field7128;
    static String field7129;
    String field7130 = null;
    static String field7131;
    static String field7132;
    static String field7133;
    static String field7134;
    static String field7135;
    static String field7136;
    static String field7137;
    static String field7138;
    static String field7139;
    static String field7140;
    static String field7141;
    static String field7142;
    static String field7143;
    static String field7144;
    static String field7145;
    static String field7146;
    static String field7147;
    static String field7148;
    static String field7149;
    static String field7150;
    static String field7151;
    static String field7152;
    static String field7153;
    static String field7154;
    static String field7155;
    static String field7156;
    static String field7157;
    static String field7158;
    static String field7159;
    static String field7160;
    static String field7161;
    static String field7162;
    static String field7163;
    static String field7164;
    static String field7165;
    static String field7166;
    static String field7167;
    static Map<Character, String> field7168;
    static Map<Character, Integer> field7169;
    static String field7170;
    static String field7171;
    static String field7172;
    static String field7173;
    static String field7174;
    static String field7175;
    static String field7176;
    static String field7177;
    static String field7178;
    static String field7179;
    static String field7180;
    static String field7181;
    Map field7182 = new HashMap();
    static String field7183;
    static String field7184;
    static String[] field7185;
    static String field7186;
    static String field7187;
    static String field7188;
    static String field7189;
    static String field7190;
    static String field7191;
    static String field7192;
    static String field7193;
    static String field7194;
    static String field7195;
    static String field7196;
    static String field7197;
    static String field7198;
    static String field7199;
    static String field7200;
    static Pattern field7201;
    static String field7202;
    static String field7203;
    static String field7204;
    static String field7205;
    static String field7206;
    static String field7207;
    static String field7208;
    static String field7209;
    static String field7210;
    static String field7211;
    static String field7212;
    static String field7213 = "parse";
    static String field7214 = "parse";
    static String field7215 = "parse";
    static String field7216 = "p";
    static String field7217;
    static String field7218;
    static String field7219;
    static String field7220;
    static String field7221;
    static String field7222;
    static String field7223;
    static String field7224;
    static String field7225;
    static String field7226;
    static int field7227;
    static String field7228;
    static int field7229;
    static String field7230;
    static int field7231;
    static String field7232;
    static int field7233;
    static String field7234;
    static int field7235;
    static String field7236;
    static int field7237;
    static String field7238;
    static int field7239;
    static String field7240;
    static int field7241;
    static String field7242;
    static int field7243;
    static String field7244;
    static int field7245;
    static String field7246;
    static int field7247;
    static String field7248;
    static int field7249;
    static String field7250;
    static int field7251;
    static String field7252;
    static int field7253;
    static String field7254;
    static int field7255;
    static String field7256;
    static int field7257;
    static String field7258;
    static int field7259;
    static String field7260;
    static int field7261;
    static String field7262;
    static int field7263;
    static String field7264;
    static int field7265;
    static String field7266;
    static int field7267;
    static String field7268;
    static int field7269;
    static String field7270;
    static int field7271;
    static String field7272;
    static int field7273;
    static String field7274;
    static int field7275;
    static String field7276;
    static int field7277;
    static String field7278;
    static int field7279;
    static String field7280;
    static int field7281;
    static String field7282;
    static int field7283;
    static int field7284;
    static int field7285;
    static int field7286;
    static int field7287;
    static int field7288;
    static int field7289;
    static int field7290;
    static int field7291;
    static int field7292;
    static int field7293;
    static int field7294;
    static int field7295;
    static int field7296;
    static int field7297;
    static int field7298;
    static int field7299;
    static int field7300;
    static int field7301;
    static int field7302;
    static int field7303;
    static int field7304;
    static int field7305;
    static int field7306;
    static int field7307;
    static int field7308;
    static int field7309;
    static int field7310;
    static String field7311;
    static String field7312;
    static String field7313;
    static String field7314 = "reallyworld";
    static String field7315 = "bravo";
    static String field7316 = "other";
    static String field7317 = "dir";
    static String field7318 = "list";
    static String field7319 = "status";
    static String field7320 = "grief";
    static String field7321 = "name";
    static String field7322 = "info";
    static String field7323 = "reset";
    static String field7324 = "scoreboard";
    static String field7325 = "players";
    static String field7326;
    static String field7327;
    static String field7328;
    static String field7329;
    static String field7330;
    static String field7331;

    private String method1761() {
        String v1 = this.method1763();
        if (v1 == null) {
            return null;
        }
        String v2 = this.method1799();
        if (v2 != null) {
            return _4ZXYEo.д_("makeConcatWithConstants", new Object[]{}, (String)v1, (String)v2);
        }
        return _4ZXYEo.дА("makeConcatWithConstants", new Object[]{}, (String)v1);
    }

    private int method1762(File param12) {
        File[] v2 = param12.listFiles((param0, param1) -> param1.endsWith(field7165) && !param1.equals(field7166));
        return v2 != null ? v2.length : 0;
    }

    private String method1763() {
        if (I.getCurrentServerEntry() == null) {
            return null;
        }
        String v1 = CommandParse.I.getCurrentServerEntry().address;
        if (v1 == null || v1.isEmpty()) {
            return null;
        }
        return v1.toLowerCase().replaceAll(field7109, "").replaceAll(field7110, field7111);
    }

    private int method1764(String param1) {
        int n;
        String v2 = param1.toUpperCase();
        int v3 = -1;
        switch (v2.hashCode()) {
            case -2078024565: {
                if (!v2.equals(field7112)) break;
                v3 = 0;
                break;
            }
            case 62372158: {
                if (!v2.equals(field7113)) break;
                v3 = 1;
                break;
            }
            case 73532175: {
                if (!v2.equals(field7114)) break;
                v3 = 2;
                break;
            }
            case 2127365038: {
                if (!v2.equals(field7115)) break;
                v3 = 3;
                break;
            }
            case 62130991: {
                if (!v2.equals(field7116)) break;
                v3 = 4;
                break;
            }
            case 75627155: {
                if (!v2.equals(field7117)) break;
                v3 = 5;
                break;
            }
            case 84989: {
                if (!v2.equals(field7118)) break;
                v3 = 6;
                break;
            }
            case 399530551: {
                if (!v2.equals(field7119)) break;
                v3 = 7;
                break;
            }
            case 76743: {
                if (!v2.equals(field7120)) break;
                v3 = 8;
                break;
            }
            case 66059891: {
                if (!v2.equals(field7121)) break;
                v3 = 9;
            }
        }
        switch (v3) {
            case 0: {
                n = ColorUtil.method1858(255, 85, 85);
                break;
            }
            case 1: {
                n = ColorUtil.method1858(85, 255, 85);
                break;
            }
            case 2: {
                n = ColorUtil.method1858(85, 85, 255);
                break;
            }
            case 3: {
                n = ColorUtil.method1858(255, 255, 85);
                break;
            }
            case 4: {
                n = ColorUtil.method1858(255, 85, 85);
                break;
            }
            case 5: {
                n = ColorUtil.method1858(170, 0, 0);
                break;
            }
            case 6: {
                n = ColorUtil.method1858(85, 255, 85);
                break;
            }
            case 7: {
                n = ColorUtil.method1858(255, 170, 0);
                break;
            }
            case 8: {
                n = ColorUtil.method1858(85, 255, 255);
                break;
            }
            case 9: {
                n = ColorUtil.method1858(255, 85, 255);
                break;
            }
            default: {
                n = ColorUtil.method1858(170, 170, 170);
            }
        }
        return n;
    }

    private void method1765() {
        File v1 = new File((String)((Object)_4ZXYEo.дь("makeConcatWithConstants", new Object[]{}, (String)PathUtil.field7558)));
        if (!v1.exists() || !v1.isDirectory()) {
            ChatUtil.method3308(Text.literal((String)field7122), new Object[0]);
            return;
        }
        File[] v2 = v1.listFiles(File::isDirectory);
        if (v2 == null || v2.length == 0) {
            ChatUtil.method3308(Text.literal((String)field7123), new Object[0]);
            return;
        }
        ChatUtil.method3308(Text.literal((String)field7124), new Object[0]);
        for (File v6 : v2) {
            int v10;
            String v7 = v6.getName();
            File[] v8 = v6.listFiles(File::isDirectory);
            File[] v9 = v6.listFiles((param0, param1) -> param1.endsWith(field7311));
            if (v8 != null && v8.length > 0) {
                v10 = 0;
                for (File v14 : v8) {
                    v10 += this.method1771(v14);
                }
                ChatUtil.method3308(Text.literal((String)((Object)_4ZXYEo.cfr_renamed_36("makeConcatWithConstants", new Object[]{}, (String)v7, (int)v10, (int)v8.length))), new Object[0]);
                continue;
            }
            if (v9 == null || v9.length <= 0) continue;
            v10 = this.method1771(v6);
            int v11 = this.method1762(v6);
            String v12 = field7125.format(new Date(v6.lastModified()));
            ChatUtil.method3308(Text.literal((String)((Object)_4ZXYEo.дЕ("makeConcatWithConstants", new Object[]{}, (String)v7, (int)v10, (int)v11, (String)v12))), new Object[0]);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void method1766(Map<String, Set<String>> param1, Map<String, Set<String>> param2, String param3) {
        String v4 = this.method1761();
        if (v4 == null) {
            return;
        }
        for (Map.Entry<String, Set<String>> entry : param1.entrySet()) {
            Set<String> v7 = param2.computeIfAbsent(entry.getKey(), param0 -> new HashSet<String>());
            v7.addAll(entry.getValue());
        }
        File v5 = new File((String)((Object)_4ZXYEo.дн("makeConcatWithConstants", new Object[]{}, (String)PathUtil.field7558, (String)v4)));
        v5.mkdirs();
        ArrayList<String> arrayList = new ArrayList<String>();
        for (Map.Entry<String, Set<String>> entry : param2.entrySet()) {
            String v9 = entry.getKey();
            Set<String> v10 = entry.getValue();
            if (!param3.equals(field7126)) {
                String v11_str = this.method1789(v9);
                if (v11_str != null) {
                    File v12 = new File((String)((Object)_4ZXYEo.дю("makeConcatWithConstants", new Object[]{}, (String)PathUtil.field7558, (String)v11_str)));
                    ArrayList<String> v13 = new ArrayList<>(v10);
                    Collections.sort(v13);
                    try {
                        Files.write(v12.toPath(), v13, new OpenOption[0]);
                    } catch (IOException iOException) {
                        // empty catch block
                    }
                }
            }
            java.util.Iterator<String> v11_it = v10.iterator();
            while (v11_it.hasNext()) {
                String v12_str = v11_it.next();
                arrayList.add((String) _4ZXYEo.Ци("makeConcatWithConstants", new Object[]{}, (String)v12_str, (String)v9));
            }
        }
        if (!arrayList.isEmpty()) {
            File v7 = new File((String)((Object)_4ZXYEo.ЦЧ("makeConcatWithConstants", new Object[]{}, (String)PathUtil.field7558, (String)v4)));
            Collections.sort(arrayList);
            try {
                Files.write(v7.toPath(), arrayList, new OpenOption[0]);
            } catch (IOException iOException) {
                // empty catch block
            }
        }
    }

    private void method1767() {
        String v1 = this.method1779();
        if (this.field7127) {
            if (v1 != null) {
                ChatUtil.method3308(Text.literal((String)((Object)_4ZXYEo.дЫ("makeConcatWithConstants", new Object[]{}, (String)v1))), new Object[0]);
            } else {
                ChatUtil.method3308(Text.literal((String)field7128), new Object[0]);
            }
            ChatUtil.method3308(Text.literal((String)field7129), new Object[0]);
        } else {
            ChatUtil.method3308(Text.literal((String)((Object)_4ZXYEo.дх("makeConcatWithConstants", new Object[]{}, (String)(this.field7130 != null ? this.field7130 : field7131)))), new Object[0]);
            ChatUtil.method3308(Text.literal((String)field7132), new Object[0]);
            if (v1 != null) {
                ChatUtil.method3308(Text.literal((String)((Object)_4ZXYEo.дЯ("makeConcatWithConstants", new Object[]{}, (String)v1))), new Object[0]);
            }
        }
    }

    private void method1768(String param1) {
        String v2;
        this.field7130 = v2 = param1.replaceAll(field7133, field7134);
        this.field7127 = false;
        ChatUtil.method3308(Text.literal((String)((Object)_4ZXYEo.дК("makeConcatWithConstants", new Object[]{}, (String)v2))), new Object[0]);
        ChatUtil.method3308(Text.literal((String)field7135), new Object[0]);
        ChatUtil.method3308(Text.literal((String)((Object)_4ZXYEo.д诶("makeConcatWithConstants", new Object[]{}, (String)this.method1763(), (String)v2))), new Object[0]);
    }

    private String method1769() {
        if (I.getCurrentServerEntry() == null) {
            return field7136;
        }
        String v1 = CommandParse.I.getCurrentServerEntry().address.toLowerCase();
        if (v1.contains(field7137) || v1.contains(field7138)) {
            return field7139;
        }
        if (v1.contains(field7140) || v1.contains(field7141)) {
            return field7142;
        }
        if (v1.contains(field7143) || v1.contains(field7144)) {
            return field7145;
        }
        return field7146;
    }

    private String method1770(String param1, String param2) {
        Object v5;
        if (param1 == null || param2 == null) {
            return null;
        }
        String v3 = Formatting.strip((String)param1);
        if (v3 == null) {
            v3 = param1;
        }
        if ((v3 = v3.trim()).equalsIgnoreCase(param2)) {
            return null;
        }
        int v4 = v3.toLowerCase().indexOf(param2.toLowerCase());
        if (v4 > 0) {
            v5 = v3.substring(0, v4).trim();
            String[] v6 = ((String)v5).split(field7147);
            for (int v7 = v6.length - 1; v7 >= 0; --v7) {
                String v9;
                String v8 = v6[v7].trim();
                if (v8.isEmpty() || v8.length() < 2 || (v9 = v8.replaceAll(field7148, "")).length() < 2) continue;
                return v9.toUpperCase();
            }
        }
        String[] v5_split = v3.split(field7149);
        for (String v9 : v5_split) {
            String v10;
            if ((v9 = v9.trim()).isEmpty() || v9.equalsIgnoreCase(param2) || (v10 = v9.replaceAll(field7150, "")).length() < 3 || v10.equalsIgnoreCase(param2)) continue;
            return v10.toUpperCase();
        }
        return null;
    }

    private int method1771(File param12) {
        int v2 = 0;
        File[] v3 = param12.listFiles((param0, param1) -> param1.endsWith(field7312) && !param1.equals(field7313));
        if (v3 == null) {
            return 0;
        }
        for (File v7 : v3) {
            try {
                List<String> v8 = Files.readAllLines(v7.toPath());
                v2 += v8.size();
            } catch (IOException iOException) {
                // empty catch block
            }
        }
        return v2;
    }

    private Map method1773() {
        HashMap<String, HashSet<String>> v1 = new HashMap<String, HashSet<String>>();
        String v2 = this.method1761();
        if (v2 == null) {
            return v1;
        }
        File v3 = new File((String)((Object)_4ZXYEo.д衣("makeConcatWithConstants", new Object[]{}, (String)PathUtil.field7558, (String)v2)));
        if (!v3.exists() || !v3.isDirectory()) {
            return v1;
        }
        File[] v4 = v3.listFiles((param0, param1) -> param1.endsWith(field7179) && !param1.equals(field7180));
        if (v4 == null) {
            return v1;
        }
        for (File v8 : v4) {
            String v9 = v8.getName().replace(field7151, "");
            try {
                List<String> v10 = Files.readAllLines(v8.toPath());
                HashSet<String> v11 = new HashSet<String>(v10);
                v11.removeIf(String::isEmpty);
                v1.put(v9, v11);
            } catch (IOException iOException) {
                // empty catch block
            }
        }
        return v1;
    }

    private void method1775() {
        if (CommandParse.I.world == null) {
            ChatUtil.method3308(Text.literal((String)field7153), new Object[0]);
            return;
        }
        Scoreboard v1 = CommandParse.I.world.getScoreboard();
        if (v1 == null) {
            ChatUtil.method3308(Text.literal((String)field7154), new Object[0]);
            return;
        }
        ScoreboardObjective v2 = v1.getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR);
        if (v2 == null) {
            ChatUtil.method3308(Text.literal((String)field7155), new Object[0]);
            return;
        }
        ChatUtil.method3308(Text.literal((String)field7156), new Object[0]);
        String v3 = v2.getDisplayName().getString();
        String v4 = Formatting.strip((String)v3);
        ChatUtil.method3308(Text.literal((String)((Object)_4ZXYEo.дз("makeConcatWithConstants", new Object[]{}, (String)v3))), new Object[0]);
        ChatUtil.method3308(Text.literal((String)((Object)_4ZXYEo.де("makeConcatWithConstants", new Object[]{}, (String)v4))), new Object[0]);
        String v5 = this.method1788(v3);
        if (v5 == null) {
            v5 = this.method1788(v4);
        }
        ChatUtil.method3308(Text.literal((String)((Object)_4ZXYEo.дТ("makeConcatWithConstants", new Object[]{}, (String)(v5 != null ? v5 : field7157)))), new Object[0]);
        ChatUtil.method3308(Text.literal((String)field7158), new Object[0]);
        Collection<ScoreboardEntry> v6 = (Collection<ScoreboardEntry>) v1.getScoreboardEntries(v2);
        int v7 = 0;
        for (ScoreboardEntry v9 : v6) {
            if (v7++ > 5) {
                ChatUtil.method3308(Text.literal((String)((Object)_4ZXYEo.д4("makeConcatWithConstants", new Object[]{}, (int)(v6.size() - 5)))), new Object[0]);
                break;
            }
            String v10 = this.method1803(v1, v9.owner());
            ChatUtil.method3308(Text.literal((String)((Object)_4ZXYEo.дй("makeConcatWithConstants", new Object[]{}, (String)v10))), new Object[0]);
        }
    }

    private void method1776() {
        if (I.getNetworkHandler() == null) {
            ChatUtil.method3308(Text.literal((String)field7159), new Object[0]);
            return;
        }
        ChatUtil.method3308(Text.literal((String)field7160), new Object[0]);
        Collection<PlayerListEntry> v1 = (Collection<PlayerListEntry>) I.getNetworkHandler().getPlayerList();
        Scoreboard v2 = CommandParse.I.world != null ? CommandParse.I.world.getScoreboard() : null;
        int v3 = 0;
        for (PlayerListEntry v5 : v1) {
            String v11;
            net.minecraft.scoreboard.Team v10_team;
            if (v3++ >= 10) {
                ChatUtil.method3308(Text.literal((String)((Object)_4ZXYEo.до("makeConcatWithConstants", new Object[]{}, (int)(v1.size() - 10)))), new Object[0]);
                break;
            }
            String v6 = v5.getProfile().getName();
            String v7 = v5.getDisplayName() != null ? v5.getDisplayName().getString() : field7161;
            String v8 = "";
            String v9 = "";
            if (v2 != null && (v10_team = v2.getScoreHolderTeam(v6)) != null) {
                v8 = v10_team.getPrefix().getString();
                v9 = v10_team.getSuffix().getString();
            }
            ChatUtil.method3308(Text.literal((String)((Object)_4ZXYEo.дц("makeConcatWithConstants", new Object[]{}, (String)v6))), new Object[0]);
            ChatUtil.method3308(Text.literal((String)((Object)_4ZXYEo.дУ("makeConcatWithConstants", new Object[]{}, (String)v7))), new Object[0]);
            ChatUtil.method3308(Text.literal((String)((Object)_4ZXYEo.д5("makeConcatWithConstants", new Object[]{}, (String)v8))), new Object[0]);
            ChatUtil.method3308(Text.literal((String)((Object)_4ZXYEo.да("makeConcatWithConstants", new Object[]{}, (String)v9))), new Object[0]);
            Object v10 = v7;
            if (v7.equals(field7162) && !v8.isEmpty()) {
                v10 = _4ZXYEo.дм("makeConcatWithConstants", new Object[]{}, (String)v8, (String)v6, (String)v9);
            }
            ChatUtil.method3308(Text.literal((String)((Object)_4ZXYEo.ды("makeConcatWithConstants", new Object[]{}, (String)((v11 = this.method1770((String)v10, v6)) != null ? v11 : field7163)))), new Object[0]);
            ChatUtil.method3308(Text.literal((String)field7164), new Object[0]);
        }
    }

    private String method1779() {
        if (CommandParse.I.world == null) {
            return null;
        }
        Scoreboard v1 = CommandParse.I.world.getScoreboard();
        if (v1 == null) {
            return null;
        }
        ScoreboardObjective v2 = v1.getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR);
        if (v2 == null) {
            return null;
        }
        String v3 = v2.getDisplayName().getString();
        String v4 = this.method1788(v3);
        if (v4 != null) {
            return v4;
        }
        String v5 = Formatting.strip((String)v3);
        v4 = this.method1788(v5);
        if (v4 != null) {
            return v4;
        }
        Collection<ScoreboardEntry> v6 = (Collection<ScoreboardEntry>) v1.getScoreboardEntries(v2);
        for (ScoreboardEntry v8 : v6) {
            String v10;
            String v9 = v8.owner();
            if (v9 == null || (v10 = this.method1803(v1, v9)) == null || v10.isEmpty() || (v4 = this.method1788(v10)) == null) continue;
            return v4;
        }
        return null;
    }

    private int method1780(String param1, String param2) {
        if (param2.equals(field7167)) {
            for (Map.Entry<Character, String> v4 : field7168.entrySet()) {
                if (!v4.getValue().equals(param1)) continue;
                return field7169.getOrDefault(v4.getKey(), -1);
            }
        }
        return this.method1764(param1);
    }

    private String method1782(String param1) {
        if (param1 == null) {
            return null;
        }
        if (!param1.contains(field7171) && !param1.contains(field7172)) {
            return null;
        }
        String v2 = Formatting.strip((String)param1);
        if (v2 == null) {
            return null;
        }
        v2 = v2.replaceAll(field7173, field7174).trim();
        if ((v2 = v2.replaceAll(field7175, field7176)).isEmpty()) {
            return null;
        }
        for (String v7 : v2.split(field7177)) {
            if (v7.length() < 3 || !v7.matches(field7178)) continue;
            return v7.toUpperCase();
        }
        return null;
    }

    private void cfr_renamed_4(String param1) {
        String v20;
        Object v19;
        Object v1722;
        Object v16;
        Object v14;
        String v13;
        if (I.getNetworkHandler() == null) {
            ChatUtil.method3308(Text.literal((String)field7181), new Object[0]);
            return;
        }
        this.field7182.clear();
        LinkedHashMap<String, Set<String>> v2 = new LinkedHashMap<String, Set<String>>();
        Collection<PlayerListEntry> v3 = (Collection<PlayerListEntry>) I.getNetworkHandler().getPlayerList();
        String v4 = this.method1763();
        String v5 = this.method1799();
        Object v6 = v5 != null ? _4ZXYEo.др("makeConcatWithConstants", new Object[]{}, (String)v5) : "";
        ChatUtil.method3308(Text.literal((String)""), new Object[0]);
        ChatUtil.method3308(Text.literal((String)((Object)_4ZXYEo.дО("makeConcatWithConstants", new Object[]{}, (String)(v4 != null ? _4ZXYEo.дЙ("makeConcatWithConstants", new Object[]{}, (String)v4, (String)v6) : param1)))), new Object[0]);
        int v7 = 0;
        int v8 = 0;
        Map v9 = this.method1773();
        Scoreboard v10 = CommandParse.I.world != null ? CommandParse.I.world.getScoreboard() : null;
        for (PlayerListEntry playerListEntry : (Collection<PlayerListEntry>) v3) {
            v13 = playerListEntry.getProfile().getName();
            v14 = playerListEntry.getDisplayName() != null ? playerListEntry.getDisplayName().getString() : null;
            if (v14 == null && v10 != null) {
                net.minecraft.scoreboard.Team v15_team = v10.getScoreHolderTeam(v13);
                if (v15_team != null) {
                    v16 = v15_team.getPrefix().getString();
                    v1722 = v15_team.getSuffix().getString();
                    if (!((String)v16).isEmpty() || !((String)v1722).isEmpty()) {
                        v14 = _4ZXYEo.дЛ("makeConcatWithConstants", new Object[]{}, (String)v16, (String)v13, (String)v1722);
                    }
                }
            }
            if (v14 == null) {
                v14 = v13;
            }
            Object v15 = null;
            if (param1.equals(field7183)) {
                char[] v16_chars = ((String)v14).toCharArray();
                int v1722_len = v16_chars.length;
                for (int v18 = 0; v18 < v1722_len; ++v18) {
                    char v19_char = v16_chars[v18];
                    if (!field7168.containsKey(Character.valueOf(v19_char))) continue;
                    v15 = (String)field7168.get(Character.valueOf(v19_char));
                    break;
                }
            } else if (param1.equals(field7184)) {
                v16 = ((String)v14).toUpperCase();
                String[] v1722_arr = field7185;
                int v18_len = v1722_arr.length;
                for (int v19_idx = 0; v19_idx < v18_len; ++v19_idx) {
                    v20 = v1722_arr[v19_idx];
                    if (!((String)v16).contains(v20)) continue;
                    v15 = v20;
                    break;
                }
                if (v15 == null && (((String)v16).startsWith(field7186) || ((String)v16).startsWith(field7187) || ((String)v16).startsWith(field7188) || ((String)v16).startsWith(field7189) || ((String)v16).startsWith(field7190))) {
                    int v1722_idx = ((String)v14).indexOf(' ');
                    if (v1722_idx > 0 && v1722_idx < ((String)v14).length() - 1) {
                        String v18_str = ((String)v14).substring(v1722_idx + 1);
                        int v19_idx2 = v18_str.indexOf(' ');
                        if (v19_idx2 > 0) {
                            v15 = v18_str.substring(0, v19_idx2).toUpperCase();
                        }
                    }
                }
            } else if (param1.equals(field7191)) {
                v15 = this.method1770((String)v14, v13);
                if (v15 == null && (v15 = this.method1782((String)v14)) != null) {
                    continue;
                }
            } else {
                v15 = this.method1770((String)v14, v13);
            }
            if (v15 == null || ((String)v15).equals(field7192)) continue;
            v2.computeIfAbsent((String)v15, param0 -> new java.util.LinkedHashSet<String>()).add(v13);
            ++v7;
            Set v16_set = (Set)v9.get(v15);
            if (v16_set != null && v16_set.contains(v13)) continue;
            ++v8;
        }
        if (v2.isEmpty()) {
            ChatUtil.method3308(Text.literal((String)field7193), new Object[0]);
        } else {
            for (Map.Entry entry : v2.entrySet()) {
                v13 = (String)entry.getKey();
                List<String> v14_list = new java.util.ArrayList<String>((Set<String>) entry.getValue());
                int v15_val = this.method1780(v13, param1);
                MutableText v16_text = Text.literal((String)field7194).append((Text)Text.literal((String)v13).setStyle(Style.EMPTY.withColor(v15_val))).append((Text)Text.literal((String)((Object)_4ZXYEo.дЪ("makeConcatWithConstants", new Object[]{}, v14_list.size()))));
                v1722 = new StringBuilder();
                Set v18 = (Set)v9.get(v13);
                for (int v19_idx = 0; v19_idx < v14_list.size(); ++v19_idx) {
                    boolean v21;
                    v20 = v14_list.get(v19_idx);
                    boolean bl = v21 = v18 == null || !v18.contains(v20);
                    if (v21) {
                        ((StringBuilder)v1722).append(field7195).append(v20).append(field7196);
                    } else {
                        ((StringBuilder)v1722).append(v20);
                    }
                    if (v19_idx >= v14_list.size() - 1) continue;
                    ((StringBuilder)v1722).append(field7197);
                }
                v16_text.append((Text)Text.literal((String)((StringBuilder)v1722).toString()).setStyle(Style.EMPTY.withFormatting(Formatting.WHITE)));
                ChatUtil.method3308(v16_text, new Object[0]);
            }
        }
        ChatUtil.method3308(Text.literal((String)((Object)_4ZXYEo.дл("makeConcatWithConstants", new Object[]{}, (int)v7, (int)v8, (int)v3.size()))), new Object[0]);
        if (v4 != null) {
            this.method1766(v2, v9, param1);
            String v11 = v5 != null ? (String) _4ZXYEo.дС("makeConcatWithConstants", new Object[]{}, (String)v4, (String)v5) : (String) _4ZXYEo.дж("makeConcatWithConstants", new Object[]{}, (String)v4);
            int n = param1.equals(field7198) ? 1 : v2.size();
            ChatUtil.method3308(Text.literal((String)((Object)_4ZXYEo.д9("makeConcatWithConstants", new Object[]{}, v11, (int)n))), new Object[0]);
        }
        ChatUtil.method3308(Text.literal((String)field7199), new Object[0]);
    }

    private String method1788(String param1) {
        int v6;
        String v5;
        if (param1 == null || param1.isEmpty()) {
            return null;
        }
        Matcher v2 = field7201.matcher(param1);
        if (v2.find()) {
            return _4ZXYEo.д1("makeConcatWithConstants", new Object[]{}, (String)v2.group(1));
        }
        Pattern v3 = Pattern.compile(field7202);
        v2 = v3.matcher(param1);
        if (v2.find()) {
            return _4ZXYEo.д西("makeConcatWithConstants", new Object[]{}, (String)v2.group(1));
        }
        String v4 = param1.toLowerCase();
        if ((v4.contains(field7203) || v4.contains(field7204)) && !(v5 = param1.replaceAll(field7205, "")).isEmpty() && v5.length() <= 2 && (v6 = Integer.parseInt(v5)) > 0 && v6 < 100) {
            return _4ZXYEo.дщ("makeConcatWithConstants", new Object[]{}, (int)v6);
        }
        return null;
    }

    private String method1789(String param1) {
        String v2 = this.method1761();
        if (v2 == null) {
            return null;
        }
        String v3 = param1.replaceAll(field7206, field7207);
        return _4ZXYEo.дМ("makeConcatWithConstants", new Object[]{}, (String)v2, (String)v3);
    }

    public static void method1790() {
        File v0 = new File((String)((Object)_4ZXYEo.дп("makeConcatWithConstants", new Object[]{}, (String)PathUtil.field7558)));
        if (!v0.exists()) {
            v0.mkdirs();
        }
        try {
            Runtime.getRuntime().exec((String)((Object)_4ZXYEo.дO("makeConcatWithConstants", new Object[]{}, (String)v0.getAbsolutePath())));
        } catch (IOException iOException) {
            // empty catch block
        }
        ChatUtil.method3308(Text.literal((String)field7208).setStyle(Style.EMPTY.withFormatting(Formatting.GREEN)), new Object[0]);
    }

    private boolean method1791() {
        if (I.isInSingleplayer()) {
            ChatUtil.method3308(Text.literal((String)field7210), new Object[0]);
            return true;
        }
        return false;
    }

    private void method1792(File param1) {
        File[] v2 = param1.listFiles();
        if (v2 != null) {
            for (File v6 : v2) {
                if (v6.isDirectory()) {
                    this.method1792(v6);
                    continue;
                }
                v6.delete();
            }
        }
        param1.delete();
    }

    public CommandParse() {
        super(field7213);
    }

    @Override
    public void method418(CommandDispatcher param1) {
        LiteralArgumentBuilder v2 = CommandParse.Ж((String)field7214);
        this.run(v2);
        param1.register(v2);
        LiteralArgumentBuilder v3 = CommandParse.Ж((String)field7215);
        this.run(v3);
        param1.register((LiteralArgumentBuilder)CommandParse.Ж((String)field7216).then((ArgumentBuilder)v3));
    }

    private void method1797() {
        String v1 = this.method1761();
        if (v1 == null) {
            ChatUtil.method3308(Text.literal((String)field7217), new Object[0]);
            return;
        }
        File v2 = new File((String)((Object)_4ZXYEo.дч("makeConcatWithConstants", new Object[]{}, (String)PathUtil.field7558, (String)v1)));
        if (v2.exists() && v2.isDirectory()) {
            this.method1792(v2);
            String v3 = this.method1799();
            String v4 = v3 != null ? _4ZXYEo.дг("makeConcatWithConstants", new Object[]{}, (String)this.method1763(), (String)v3) : this.method1763();
            ChatUtil.method3308(Text.literal((String)((Object)_4ZXYEo.дI("makeConcatWithConstants", new Object[]{}, (String)v4))), new Object[0]);
        } else {
            ChatUtil.method3308(Text.literal((String)field7218), new Object[0]);
        }
    }

    public static void method1798() {
        field7125 = new SimpleDateFormat(field7219);
        field7201 = Pattern.compile(field7220, 2);
        field7168 = new LinkedHashMap();
        field7169 = new LinkedHashMap();
        field7185 = new String[]{field7221, field7222, field7223, field7224, field7225, field7226};
        field7168.put(Character.valueOf((char)field7227), field7228);
        field7168.put(Character.valueOf((char)field7229), field7230);
        field7168.put(Character.valueOf((char)field7231), field7232);
        field7168.put(Character.valueOf((char)field7233), field7234);
        field7168.put(Character.valueOf((char)field7235), field7236);
        field7168.put(Character.valueOf((char)field7237), field7238);
        field7168.put(Character.valueOf((char)field7239), field7240);
        field7168.put(Character.valueOf((char)field7241), field7242);
        field7168.put(Character.valueOf((char)field7243), field7244);
        field7168.put(Character.valueOf((char)field7245), field7246);
        field7168.put(Character.valueOf((char)field7247), field7248);
        field7168.put(Character.valueOf((char)field7249), field7250);
        field7168.put(Character.valueOf((char)field7251), field7252);
        field7168.put(Character.valueOf((char)field7253), field7254);
        field7168.put(Character.valueOf((char)field7255), field7256);
        field7168.put(Character.valueOf((char)field7257), field7258);
        field7168.put(Character.valueOf((char)field7259), field7260);
        field7168.put(Character.valueOf((char)field7261), field7262);
        field7168.put(Character.valueOf((char)field7263), field7264);
        field7168.put(Character.valueOf((char)field7265), field7266);
        field7168.put(Character.valueOf((char)field7267), field7268);
        field7168.put(Character.valueOf((char)field7269), field7270);
        field7168.put(Character.valueOf((char)field7271), field7272);
        field7168.put(Character.valueOf((char)field7273), field7274);
        field7168.put(Character.valueOf((char)field7275), field7276);
        field7168.put(Character.valueOf((char)field7277), field7278);
        field7168.put(Character.valueOf((char)field7279), field7280);
        field7168.put(Character.valueOf((char)field7281), field7282);
        field7169.put(Character.valueOf((char)field7283), ColorUtil.method1858(255, 140, 0));
        field7169.put(Character.valueOf((char)field7284), ColorUtil.method1858(120, 120, 120));
        field7169.put(Character.valueOf((char)field7285), ColorUtil.method1858(100, 113, 251));
        field7169.put(Character.valueOf((char)field7286), ColorUtil.method1858(214, 200, 42));
        field7169.put(Character.valueOf((char)field7287), ColorUtil.method1858(101, 189, 56));
        field7169.put(Character.valueOf((char)field7288), ColorUtil.method1858(64, 151, 214));
        field7169.put(Character.valueOf((char)field7289), ColorUtil.method1858(202, 130, 60));
        field7169.put(Character.valueOf((char)field7290), ColorUtil.method1858(202, 60, 60));
        field7169.put(Character.valueOf((char)field7291), ColorUtil.method1858(245, 51, 238));
        field7169.put(Character.valueOf((char)field7292), ColorUtil.method1858(214, 200, 42));
        field7169.put(Character.valueOf((char)field7293), ColorUtil.method1858(121, 81, 202));
        field7169.put(Character.valueOf((char)field7294), ColorUtil.method1858(202, 130, 60));
        field7169.put(Character.valueOf((char)field7295), ColorUtil.method1858(202, 60, 60));
        field7169.put(Character.valueOf((char)field7296), ColorUtil.method1858(68, 65, 66));
        field7169.put(Character.valueOf((char)field7297), ColorUtil.method1858(127, 214, 86));
        field7169.put(Character.valueOf((char)field7298), ColorUtil.method1858(92, 120, 7));
        field7169.put(Character.valueOf((char)field7299), ColorUtil.method1858(120, 120, 120));
        field7169.put(Character.valueOf((char)field7300), ColorUtil.method1858(214, 200, 42));
        field7169.put(Character.valueOf((char)field7301), ColorUtil.method1858(100, 113, 251));
        field7169.put(Character.valueOf((char)field7302), ColorUtil.method1858(100, 113, 251));
        field7169.put(Character.valueOf((char)field7303), ColorUtil.method1858(121, 81, 202));
        field7169.put(Character.valueOf((char)field7304), ColorUtil.method1858(100, 113, 251));
        field7169.put(Character.valueOf((char)field7305), ColorUtil.method1858(121, 81, 202));
        field7169.put(Character.valueOf((char)field7306), ColorUtil.method1858(64, 151, 214));
        field7169.put(Character.valueOf((char)field7307), ColorUtil.method1858(202, 60, 60));
        field7169.put(Character.valueOf((char)field7308), ColorUtil.method1858(121, 81, 202));
        field7169.put(Character.valueOf((char)field7309), ColorUtil.method1858(255, 255, 255));
        field7169.put(Character.valueOf((char)field7310), ColorUtil.method1858(214, 200, 42));
    }

    private String method1799() {
        if (this.field7127) {
            return this.method1779();
        }
        return this.field7130;
    }

    public static com.mojang.brigadier.builder.LiteralArgumentBuilder Ж(String name) {
        return ClientCommand.method420(name);
    }

    public static com.mojang.brigadier.builder.RequiredArgumentBuilder Ж(String name, com.mojang.brigadier.arguments.ArgumentType<?> type) {
        return ClientCommand.method417(name, type);
    }

    @Override
    public void run(LiteralArgumentBuilder param12) {
        param12.executes(param1 -> {
            if (this.method1791()) {
                return 1;
            }
            if (I.getNetworkHandler() == null) {
                ChatUtil.method3308(Text.literal((String)field7212).setStyle(Style.EMPTY.withFormatting(Formatting.RED)), new Object[0]);
                return 1;
            }
            String v2 = this.method1769();
            this.cfr_renamed_4(v2);
            return 1;
        });
        param12.then(CommandParse.Ж((String)field7314).executes(param1 -> {
            if (this.method1791()) {
                return 1;
            }
            this.cfr_renamed_4(field7170);
            return 1;
        }));
        param12.then(CommandParse.Ж((String)field7315).executes(param1 -> {
            if (this.method1791()) {
                return 1;
            }
            this.cfr_renamed_4(field7200);
            return 1;
        }));
        param12.then(CommandParse.Ж((String)field7316).executes(param1 -> {
            if (this.method1791()) {
                return 1;
            }
            this.cfr_renamed_4(field7211);
            return 1;
        }));
        param12.then(CommandParse.Ж((String)field7317).executes(param1 -> {
            if (this.method1791()) {
                return 1;
            }
            CommandParse.method1790();
            return 1;
        }));
        param12.then(CommandParse.Ж((String)field7318).executes(param1 -> {
            if (this.method1791()) {
                return 1;
            }
            this.method1765();
            return 1;
        }));
        param12.then(CommandParse.Ж((String)field7319).executes(param1 -> {
            if (this.method1791()) {
                return 1;
            }
            this.method1797();
            return 1;
        }));
        param12.then(((LiteralArgumentBuilder)CommandParse.Ж((String)field7320).then(RequiredArgumentBuilder.argument((String)field7321, (ArgumentType)StringArgumentType.greedyString()).executes(param1 -> {
            if (this.method1791()) {
                return 1;
            }
            String v2 = StringArgumentType.getString((CommandContext)param1, (String)field7152);
            this.method1768(v2);
            return 1;
        }))).executes(param1 -> {
            if (this.method1791()) {
                return 1;
            }
            this.method1767();
            return 1;
        }));
        param12.then(CommandParse.Ж((String)field7322).executes(param1 -> {
            if (this.method1791()) {
                return 1;
            }
            this.method1805();
            return 1;
        }));
        param12.then(CommandParse.Ж((String)field7323).executes(param1 -> {
            if (this.method1791()) {
                return 1;
            }
            this.field7130 = null;
            this.field7127 = true;
            ChatUtil.method3308(Text.literal((String)field7326), new Object[0]);
            String v2 = this.method1779();
            if (v2 != null) {
                ChatUtil.method3308(Text.literal((String)((Object)_4ZXYEo.Цд("makeConcatWithConstants", new Object[]{}, (String)v2))), new Object[0]);
            }
            return 1;
        }));
        param12.then(CommandParse.Ж((String)field7324).executes(param1 -> {
            if (this.method1791()) {
                return 1;
            }
            this.method1775();
            return 1;
        }));
        param12.then(CommandParse.Ж((String)field7325).executes(param1 -> {
            if (this.method1791()) {
                return 1;
            }
            this.method1776();
            return 1;
        }));
    }

    private String method1803(Scoreboard param1, String param2) {
        Team v3 = param1.getScoreHolderTeam(param2);
        if (v3 != null) {
            String v4 = Formatting.strip((String)v3.getPrefix().getString());
            String v5 = Formatting.strip((String)v3.getSuffix().getString());
            String v6 = Formatting.strip((String)param2);
            return _4ZXYEo.дЩ("makeConcatWithConstants", new Object[]{}, (String)(v4 != null ? v4 : ""), (String)(v6 != null ? v6 : ""), (String)(v5 != null ? v5 : ""));
        }
        return Formatting.strip((String)param2);
    }

    private void method1805() {
        String v1 = this.method1763();
        if (v1 == null) {
            ChatUtil.method3308(Text.literal((String)field7327), new Object[0]);
            return;
        }
        File v2 = new File((String)((Object)_4ZXYEo.дХ("makeConcatWithConstants", new Object[]{}, (String)PathUtil.field7558, (String)v1)));
        if (!v2.exists() || !v2.isDirectory()) {
            ChatUtil.method3308(Text.literal((String)((Object)_4ZXYEo.дФ("makeConcatWithConstants", new Object[]{}, (String)v1))), new Object[0]);
            return;
        }
        File[] v3 = v2.listFiles(File::isDirectory);
        if (v3 == null || v3.length == 0) {
            ChatUtil.method3308(Text.literal((String)((Object)_4ZXYEo.дН("makeConcatWithConstants", new Object[]{}, (String)v1))), new Object[0]);
            return;
        }
        String v4 = this.method1799();
        ChatUtil.method3308(Text.literal((String)((Object)_4ZXYEo.дя("makeConcatWithConstants", new Object[]{}, (String)v1))), new Object[0]);
        for (File v8 : v3) {
            String v9 = v8.getName();
            int v10 = this.method1771(v8);
            int v11 = this.method1762(v8);
            String v12 = field7125.format(new Date(v8.lastModified()));
            String v13 = v9.equals(v4) ? field7328 : field7329;
            ChatUtil.method3308(Text.literal((String)((Object)_4ZXYEo.дШ("makeConcatWithConstants", new Object[]{}, (String)v13, (String)v9, (int)v10, (int)v11, (String)v12))), new Object[0]);
        }
        if (v4 != null) {
            String string = this.field7127 ? field7330 : field7331;
            ChatUtil.method3308(Text.literal((String)((Object)_4ZXYEo.дД("makeConcatWithConstants", new Object[]{}, (String)string, (String)v4))), new Object[0]);
        }
    }

    static {
        field7210 = "§cParse доступен только на сервере.";
        field7159 = "§cНе подключен к серверу!";
        field7160 = "§7═══════ §fDebug Tab List §7═══════";
        cfr_renamed_37 = "§8...и ещё \u0001 игроков";
        field7161 = "NULL";
        cfr_renamed_38 = "§7Name: §f[\u0001]";
        cfr_renamed_39 = "§7Display: §f[\u0001]";
        cfr_renamed_40 = "§7TeamPrefix: §f[\u0001]";
        cfr_renamed_41 = "§7TeamSuffix: §f[\u0001]";
        field7162 = "NULL";
        cfr_renamed_42 = "\u0001\u0001\u0001";
        field7163 = "null";
        cfr_renamed_43 = "§7Extracted: §a\u0001";
        field7164 = "§8---";
        field7153 = "§cМир не загружен";
        field7154 = "§cСкорборд не найден";
        field7155 = "§cSidebar objective не найден";
        field7156 = "§7═══════ §fDebug Scoreboard §7═══════";
        cfr_renamed_44 = "§7Title (raw): §f\u0001";
        cfr_renamed_45 = "§7Title (clean): §f\u0001";
        field7157 = "null";
        cfr_renamed_46 = "§7Detected grief: §f\u0001";
        field7158 = "§7Строки скорборда:";
        cfr_renamed_47 = "§8...и ещё \u0001";
        cfr_renamed_48 = "§8- §f\u0001";
        field7133 = "[^a-zA-Zа-яА-Я0-9._-]";
        field7134 = "_";
        cfr_renamed_49 = "§aГриф установлен: §f\u0001";
        field7135 = "§7Автоопределение грифа: §cвыключено";
        cfr_renamed_50 = "§7Теперь парсинг будет сохраняться в: §f\u0001\\u0001\\";
        cfr_renamed_51 = "§7Текущий гриф (авто): §a\u0001";
        field7128 = "§7Текущий гриф: §fне определён (нет на грифе)";
        field7129 = "§7Автоопределение: §aвключено";
        field7131 = "не установлен";
        cfr_renamed_52 = "§7Текущий гриф (ручной): §f\u0001";
        field7132 = "§7Автоопределение: §cвыключено";
        cfr_renamed_53 = "§7На скорборде: §f\u0001";
        cfr_renamed_54 = "grief-\u0001";
        field7202 = "#\\s*(\\d+)";
        cfr_renamed_55 = "grief-\u0001";
        field7203 = "гриф";
        field7204 = "grief";
        field7205 = "[^0-9]";
        cfr_renamed_56 = "grief-\u0001";
        cfr_renamed_57 = "\u0001\u0001\u0001";
        field7327 = "§cНе подключен к серверу!";
        cfr_renamed_58 = "\u0001parse\\u0001";
        cfr_renamed_59 = "§7Нет сохранённых грифов для \u0001";
        cfr_renamed_60 = "§7Нет сохранённых грифов для \u0001";
        cfr_renamed_61 = "§7═══════ §fГрифы на \u0001 §7═══════";
        field7328 = "§a► ";
        field7329 = "§7  ";
        cfr_renamed_62 = "\u0001§f\u0001 §7- \u0001 донатеров §8(\u0001 рангов, \u0001)";
        field7330 = "авто";
        field7331 = "ручной";
        cfr_renamed_63 = "§7Текущий гриф (\u0001): §a\u0001";
        field7109 = ":\\d+$";
        field7110 = "[^a-z0-9.-]";
        field7111 = "_";
        cfr_renamed_64 = "parse\\u0001\\u0001\\";
        cfr_renamed_65 = "parse\\u0001\\";
        field7206 = "[^a-zA-Z0-9._+-]";
        field7207 = "_";
        cfr_renamed_66 = "\u0001\u0001.txt";
        cfr_renamed_67 = "\u0001parse\\";
        cfr_renamed_68 = "explorer \u0001";
        field7208 = "Папка Parse открыта!";
        field7209 = "Ошибка открытия папки!";
        cfr_renamed_69 = "\u0001parse\\";
        field7122 = "§7Нет сохранённых серверов";
        field7123 = "§7Нет сохранённых серверов";
        field7124 = "§7═══════ §fСохранённые сервера §7═══════";
        cfr_renamed_70 = "§f\u0001 §7- \u0001 донатеров §8(\u0001 грифов)";
        cfr_renamed_71 = "§f\u0001 §7- \u0001 донатеров §8(\u0001 рангов, \u0001)";
        field7217 = "§cНе подключен к серверу!";
        cfr_renamed_72 = "\u0001\u0001";
        cfr_renamed_73 = "\u0001\\u0001";
        cfr_renamed_74 = "§aДанные для \u0001 очищены!";
        field7218 = "§7Нет данных для этого сервера/грифа";
        field7136 = "other";
        field7137 = "reallyworld";
        field7138 = "rw";
        field7139 = "reallyworld";
        field7140 = "bravo";
        field7141 = "hvh";
        field7142 = "bravo";
        field7143 = "holyworld";
        field7144 = "holy";
        field7145 = "holyworld";
        field7146 = "other";
        field7181 = "§cНе подключен к серверу!";
        cfr_renamed_75 = " / \u0001";
        cfr_renamed_76 = "\u0001\u0001";
        cfr_renamed_77 = "§7═══════ §fParse Donates §7[\u0001] §7═══════";
        cfr_renamed_78 = "\u0001\u0001\u0001";
        field7183 = "reallyworld";
        field7184 = "bravo";
        field7186 = "I ";
        field7187 = "II ";
        field7188 = "III ";
        field7189 = "X ";
        field7190 = "V ";
        field7191 = "holyworld";
        field7192 = "PLAYER";
        field7193 = "§7Донатеры не найдены";
        field7194 = "§7[";
        cfr_renamed_79 = "§7] §f(\u0001): ";
        field7195 = "§a";
        field7196 = "§a[NEW]";
        field7197 = "§7, §f";
        cfr_renamed_80 = "§7Всего: §f\u0001 §7(новых: §a\u0001§7) из §f\u0001";
        cfr_renamed_81 = "\u0001\\u0001\\";
        cfr_renamed_82 = "\u0001\\";
        field7198 = "holyworld";
        cfr_renamed_83 = "§7Сохранено в: §f\u0001 §8(\u0001 файлов)";
        field7199 = "§7═══════════════════════════════════";
        cfr_renamed_84 = "\u0001\u0001";
        field7151 = ".txt";
        cfr_renamed_85 = "\u0001\u0001";
        field7126 = "holyworld";
        cfr_renamed_86 = "\u0001\u0001";
        cfr_renamed_87 = "\u0001 [\u0001]";
        cfr_renamed_88 = "\u0001\u0001all.txt";
        cfr_renamed_89 = "§cОшибка сохранения: \u0001";
        field7171 = "---";
        field7172 = "$";
        field7173 = "[$\\-★\\s]";
        field7174 = " ";
        field7175 = "\\s+";
        field7176 = " ";
        field7177 = "\\s+";
        field7178 = "[A-Za-zА-Яа-яЁё]+";
        field7147 = "[\\s\\p{Punct}★$\\-]+";
        field7148 = "[^\\p{L}]";
        field7149 = "[\\s\\p{Punct}★$\\-]+";
        field7150 = "[^\\p{L}]";
        field7112 = "KILLER";
        field7113 = "ALPHA";
        field7114 = "MODER";
        field7115 = "HELPER";
        field7116 = "ADMIN";
        field7117 = "OWNER";
        field7118 = "VIP";
        field7119 = "PREMIUM";
        field7120 = "MVP";
        field7121 = "ELITE";
        field7167 = "reallyworld";
        field7179 = ".txt";
        field7180 = "all.txt";
        field7165 = ".txt";
        field7166 = "all.txt";
        field7312 = ".txt";
        field7313 = "all.txt";
        field7311 = ".txt";
        field7326 = "§aРучной гриф сброшен! Автоопределение включено.";
        cfr_renamed_90 = "§7Текущий гриф (авто): §a\u0001";
        field7152 = "name";
        field7211 = "other";
        field7200 = "bravo";
        field7170 = "reallyworld";
        field7212 = "Не подключен к серверу!";
        field7219 = "dd.MM.yyyy HH:mm";
        field7220 = "(?:ГРИФ|GRIEF|гриф)\\s*#?(\\d+)";
        field7221 = "KILLER";
        field7222 = "ALPHA";
        field7223 = "MODER";
        field7224 = "HELPER";
        field7225 = "ADMIN";
        field7226 = "OWNER";
        field7227 = 42240;
        field7228 = "PLAYER";
        field7229 = 42244;
        field7230 = "HERO";
        field7231 = 42248;
        field7232 = "TITAN";
        field7233 = 42258;
        field7234 = "AVENGER";
        field7235 = 42262;
        field7236 = "OVERLORD";
        field7237 = 42272;
        field7238 = "MAGISTER";
        field7239 = 42276;
        field7240 = "IMPERATOR";
        field7241 = 42280;
        field7242 = "DRAGON";
        field7243 = 42336;
        field7244 = "D.HELPER";
        field7245 = 42290;
        field7246 = "BULL";
        field7247 = 42294;
        field7248 = "TIGER";
        field7249 = 42308;
        field7250 = "DRACULA";
        field7251 = 42326;
        field7252 = "BUNNY";
        field7253 = 42312;
        field7254 = "COBRA";
        field7255 = 42304;
        field7256 = "HYDRA";
        field7257 = 42322;
        field7258 = "RABBIT";
        field7259 = 42249;
        field7260 = "HELPER";
        field7261 = 42259;
        field7262 = "ML.MODER";
        field7263 = 42263;
        field7264 = "MODER";
        field7265 = 42273;
        field7266 = "MODER+";
        field7267 = 42277;
        field7268 = "ST.MODER";
        field7269 = 42281;
        field7270 = "GL.MODER";
        field7271 = 42291;
        field7272 = "ML.ADMIN";
        field7273 = 42295;
        field7274 = "ADMIN";
        field7275 = 42241;
        field7276 = "MEDIA";
        field7277 = 42245;
        field7278 = "YT";
        field7279 = 42305;
        field7280 = "GOD";
        field7281 = 42313;
        field7282 = "PEGAS";
        field7283 = 42313;
        field7284 = 42240;
        field7285 = 42244;
        field7286 = 42248;
        field7287 = 42258;
        field7288 = 42262;
        field7289 = 42272;
        field7290 = 42276;
        field7291 = 42280;
        field7292 = 42336;
        field7293 = 42290;
        field7294 = 42294;
        field7295 = 42308;
        field7296 = 42326;
        field7297 = 42312;
        field7298 = 42304;
        field7299 = 42322;
        field7300 = 42249;
        field7301 = 42259;
        field7302 = 42263;
        field7303 = 42273;
        field7304 = 42277;
        field7305 = 42281;
        field7306 = 42291;
        field7307 = 42295;
        field7308 = 42241;
        field7309 = 42245;
        field7310 = 42305;
        CommandParse.method1798();
    }
}




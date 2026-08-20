/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.network.PlayerListEntry
 *  net.minecraft.scoreboard.Team
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.Text
 *  net.minecraft.world.GameMode
 *  sLM.4ZXYEo
 */
package minced.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import minced.core.ColorUtil;
import minced.core.CancellableEvent;
import minced.core.NotificationManager;
import minced.core.PlayerEntryComparator;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;
import sLM._4ZXYEo;

public class PlayerListUtil
implements CancellableEvent {
    static String cfr_renamed_197;
    static String cfr_renamed_198;
    static String cfr_renamed_199;
    static String cfr_renamed_200;
    static String cfr_renamed_201;
    static String cfr_renamed_202;

    List<PlayerEntryComparator> field8492 = new ArrayList<PlayerEntryComparator>();
    Set<String> field8493 = new HashSet<String>();
    Map<String, PlayerEntryComparator> field8494 = new HashMap<String, PlayerEntryComparator>();
    Set<String> field8495 = new HashSet<String>();
    static String field8496;
    MinecraftClient var_net_minecraft_client_MinecraftClient_I;
    static Set<String> field8497;
    static String field8498;
    static String field8499;
    static String field8500;
    static String field8501;
    static String field8502;
    static String field8503;
    static String field8504;
    static String field8505;
    static Pattern field8506;
    static String field8507;
    boolean field8508;
    long var_long_I;
    static long field8509;
    static String field8510;
    static String field8511;
    static String field8512;
    static List<String> field8513;
    static String field8514;
    static String field8515;
    static String field8516;
    static String field8517;
    static String field8518;
    static String field8519;
    static String field8520;
    static String field8521;
    static String field8522;
    static String field8523;
    static String field8524;
    static String field8525;
    static String field8526;
    static String field8527;
    static String field8528;
    static String field8529;
    static String field8530;
    static String field8531;
    static String field8532;
    static String field8533;
    static String field8534 = "field8534";
    static String field8535 = "field8535";
    static String field8536 = "field8536";
    static String field8537 = "field8537";
    static String field8538 = "field8538";
    static String field8539 = "field8539";
    static String field8540 = "field8540";
    static String field8541 = "field8541";
    static String field8542 = "field8542";
    static String field8543 = "field8543";
    static String field8544 = "field8544";
    static String field8545 = "field8545";
    static String field8546 = "field8546";
    static String field8547 = "field8547";
    static String field8548 = "field8548";
    static String field8549 = "field8549";
    static String field8550 = "field8550";
    static String field8551 = "field8551";
    static String field8552 = "field8552";
    static String field8553 = "field8553";
    static String field8554 = "field8554";
    static String field8555 = "field8555";
    static String field8556 = "field8556";
    static String field8557 = "field8557";
    static String field8558 = "field8558";
    static String field8559 = "field8559";
    static String field8560 = "field8560";
    static String field8561 = "field8561";
    static String field8562;
    static String field8563;

    private String method2360(PlayerListEntry param1, String param2) {
        if (param1.getDisplayName() != null) {
            return param1.getDisplayName().getString();
        }
        Team v3 = this.method2363(param2);
        if (v3 != null) {
            return _4ZXYEo.cfr_renamed_191("makeConcatWithConstants", new Object[]{}, (String)v3.getPrefix().getString(), (String)param2, (String)v3.getSuffix().getString());
        }
        return param2;
    }

    private void method2361(String param12, String param2) {
        boolean v3 = this.field8492.stream().anyMatch(param1 -> param1.method3284().equalsIgnoreCase(param12));
        boolean v4 = this.method2385(param12);
        if (!v3 && !v4) {
            NotificationManager.method2456(field8496, (String)((Object)_4ZXYEo.cfr_renamed_192("makeConcatWithConstants", new Object[]{}, (String)param12)), ColorUtil.method1852(255, 200, 0, 255));
        }
        this.field8495.add(param12);
        PlayerListEntry v5 = this.method2362(param12);
        Text v6 = v5 != null && this.method2363(param12) != null ? this.method2363(param12).getPrefix() : Text.literal((String)(param2 != null ? _4ZXYEo.cfr_renamed_193("makeConcatWithConstants", new Object[]{}, (String)param2) : ""));
        List<String> v7 = this.method2367();
        boolean v8 = this.method2365(param12, v5, v7);
        PlayerEntryComparator v9 = new PlayerEntryComparator(param12, (Text)v6, v8);
        this.field8494.put(param12, v9);
        if (!v8 && this.field8493.stream().noneMatch(param1 -> param1.equalsIgnoreCase(param12))) {
            this.field8493.add(param12);
        }
    }

    private PlayerListEntry method2362(String param1) {
        if (this.var_net_minecraft_client_MinecraftClient_I.getNetworkHandler() == null) {
            return null;
        }
        for (PlayerListEntry v3 : this.var_net_minecraft_client_MinecraftClient_I.getNetworkHandler().getPlayerList()) {
            if (!v3.getProfile().getName().equalsIgnoreCase(param1)) continue;
            return v3;
        }
        return null;
    }

    private Team method2363(String param1) {
        if (this.var_net_minecraft_client_MinecraftClient_I.world == null) {
            return null;
        }
        for (Team v3 : this.var_net_minecraft_client_MinecraftClient_I.world.getScoreboard().getTeams()) {
            if (!v3.getPlayerList().contains(param1)) continue;
            return v3;
        }
        return null;
    }

    private String method2364(String param1) {
        for (String v3 : field8497) {
            String[] v6;
            String v5;
            if (param1.contains(v3)) {
                return v3;
            }
            String v4 = param1.toLowerCase(Locale.ROOT);
            if (v4.contains(v5 = v3.toLowerCase(Locale.ROOT))) {
                return v3;
            }
            for (String v10 : v6 = param1.split(field8498)) {
                String v11 = v10.replaceAll(field8499, "");
                if (!v11.toLowerCase(Locale.ROOT).startsWith(v5) || v11.length() < v5.length()) continue;
                return v3;
            }
        }
        return null;
    }

    private boolean method2365(String param1, PlayerListEntry param2, List<String> param3) {
        if (param2 != null && param2.getGameMode() == GameMode.SPECTATOR) {
            return true;
        }
        boolean v4 = this.method2391(param1, param2, param3);
        return !v4 && this.method2385(param1);
    }

    private List<String> method2367() {
        if (this.var_net_minecraft_client_MinecraftClient_I.getNetworkHandler() == null) {
            return List.of();
        }
        return this.var_net_minecraft_client_MinecraftClient_I.getNetworkHandler().getPlayerList().stream().map(param0 -> param0.getProfile().getName()).filter(param0 -> param0 != null && !param0.isEmpty()).toList();
    }

    public void method2368(String param1) {
        String v4;
        String v2 = param1.replaceAll(field8500, "").replaceAll(field8501, "").replaceAll(field8502, field8503).replaceAll(field8504, field8505).trim();
        Matcher v3 = field8506.matcher(v2);
        if (v3.find()) {
            v4 = v3.group(1);
            String v5 = v3.group(2);
            if (this.method2383(v4)) {
                String v6 = this.method2376(v5);
                this.method2361(v6 != null ? v6 : v5, v4);
            }
        }
        if ((v4 = this.method2364(v2)) != null) {
            this.method2387(v4);
        }
    }

    private void method2369() {
        Text v10;
        String v7;
        if (this.var_net_minecraft_client_MinecraftClient_I.player == null || this.var_net_minecraft_client_MinecraftClient_I.getNetworkHandler() == null || this.var_net_minecraft_client_MinecraftClient_I.world == null) {
            return;
        }
        ArrayList<PlayerEntryComparator> v1 = new ArrayList<PlayerEntryComparator>();
        Collection<PlayerListEntry> v2 = this.var_net_minecraft_client_MinecraftClient_I.getNetworkHandler().getPlayerList();
        List<String> v3 = this.method2367();
        for (PlayerListEntry v5 : v2) {
            String v8;
            String v6;
            if (v5 == null || (v6 = v5.getProfile().getName()) == null || v6.isEmpty() || (v7 = this.method2360(v5, v6)).isEmpty() || v7.equals(v6) || (v8 = this.method2378(v7, v6)).isEmpty() || !this.method2383(v8)) continue;
            Team v9 = this.method2363(v6);
            v10 = v9 != null ? v9.getPrefix() : Text.literal((String)v8);
            boolean v11 = v5.getGameMode() == GameMode.SPECTATOR || v3.stream().noneMatch(param1 -> param1.equalsIgnoreCase(v6));
            PlayerEntryComparator v12 = new PlayerEntryComparator(v6, v10, v11);
            v1.add(v12);
            this.field8494.put(v6, v12);
            this.method2375(v6, !v11, v11);
        }
        for (String v5 : this.field8495) {
            boolean v6 = v1.stream().anyMatch(arg_0 -> PlayerListUtil.method2377(v5, arg_0));
            if (v6) continue;
            PlayerListEntry v7b = this.method2362(v5);
            boolean v8 = this.method2391(v5, v7b, v3);
            boolean v9 = this.method2365(v5, v7b, v3);
            if (!v8 && !v9 && !this.method2390(v5)) continue;
            v10 = v7b != null && this.method2363(v5) != null ? this.method2363(v5).getPrefix() : this.method2386(v5);
            PlayerEntryComparator v11 = new PlayerEntryComparator(v5, v10, v9);
            v1.add(v11);
            this.field8494.put(v5, v11);
            this.method2375(v5, v8, v9);
        }
        HashSet v4 = new HashSet();
        for (PlayerEntryComparator v6 : v1) {
            v4.add(v6.method3284());
        }
        for (String v6 : new HashSet<String>(this.field8493)) {
            if (v4.contains(v6)) continue;
            NotificationManager.method2456(field8507, (String)((Object)_4ZXYEo.cfr_renamed_194("makeConcatWithConstants", new Object[]{}, (String)v6)), ColorUtil.method1852(255, 60, 60, 255));
            this.field8493.remove(v6);
            this.field8494.remove(v6);
            this.field8495.remove(v6);
        }
        v1.sort(Comparator.comparing(PlayerEntryComparator::method3284, String.CASE_INSENSITIVE_ORDER));
        this.field8492.clear();
        this.field8492.addAll(v1);
    }

    public void method2370() {
        long v2;
        boolean v1;
        if (this.var_net_minecraft_client_MinecraftClient_I.player == null || this.var_net_minecraft_client_MinecraftClient_I.isInSingleplayer()) {
            return;
        }
        boolean bl = v1 = this.var_net_minecraft_client_MinecraftClient_I.getNetworkHandler() != null && this.var_net_minecraft_client_MinecraftClient_I.getNetworkHandler().getPlayerListEntry(this.var_net_minecraft_client_MinecraftClient_I.player.getUuid()) != null;
        if (v1 && !this.field8508) {
            this.field8492.clear();
            this.field8494.clear();
            this.field8493.clear();
            this.field8495.clear();
        }
        this.field8508 = v1;
        if (this.field8508 && (v2 = System.currentTimeMillis()) - this.var_long_I > field8509) {
            this.method2369();
            this.var_long_I = v2;
        }
    }

    private void method2375(String param1, boolean param2, boolean param3) {
        if (param2 && !this.field8493.contains(param1)) {
            NotificationManager.method2456(field8510, (String)((Object)_4ZXYEo.cfr_renamed_195("makeConcatWithConstants", new Object[]{}, (String)param1)), ColorUtil.method1852(60, 255, 60, 255));
            this.field8493.add(param1);
        } else if (!param3 || this.field8493.contains(param1)) {
            // empty if block
        }
    }

    private String method2376(String param1) {
        if (param1 == null) {
            return null;
        }
        for (String v3 : field8497) {
            if (!v3.equalsIgnoreCase(param1)) continue;
            return v3;
        }
        return null;
    }

    private static /* synthetic */ boolean method2377(String param0, PlayerEntryComparator param1) {
        return param1.method3284().equalsIgnoreCase(param0);
    }

    private String method2378(String param1, String param2) {
        String v4;
        if (param1 == null || param2 == null || param1.equals(param2)) {
            return "";
        }
        String v3 = this.method2381(param1);
        int v5 = v3.indexOf(v4 = this.method2381(param2));
        if (v5 > 0) {
            int v6 = this.method2379(param1, v5);
            return param1.substring(0, v6).trim();
        }
        return param1.replace(param2, "").trim();
    }

    private int method2379(String param1, int param2) {
        int v3 = 0;
        int v4 = 0;
        for (int v5 = 0; v5 < param1.length() && v4 < param2; ++v5) {
            if (param1.charAt(v5) == '§' && v5 + 1 < param1.length()) {
                ++v5;
            } else {
                ++v4;
            }
            v3 = v5 + 1;
        }
        return v3;
    }

    public List method2380() {
        return this.field8492;
    }

    private String method2381(String param1) {
        return param1 == null ? "" : param1.replaceAll(field8511, "");
    }

    private boolean method2383(String param1) {
        if (param1 == null || param1.isEmpty()) {
            return false;
        }
        String v2 = this.method2381(param1.toLowerCase(Locale.ROOT)).replaceAll(field8512, "");
        for (String v4 : field8513) {
            if (!v2.contains(v4.toLowerCase(Locale.ROOT))) continue;
            return true;
        }
        return false;
    }

    public static void method2384() {
        field8513 = List.of(field8514, field8515, field8516, field8517, field8518, field8519, field8520, field8521, field8522, field8523, field8524, field8525, field8526, field8527, field8528, field8529, field8530, field8531, field8532, field8533);
        field8497 = Set.of(field8534, field8535, field8536, field8537, field8538, field8539, field8540, field8541, field8542, field8543, field8544, field8545, field8546, field8547, field8548, field8549, field8550, field8551, field8552, field8553, field8554, field8555, field8556, field8557, field8558, field8559, field8560, field8561);
        field8506 = Pattern.compile(field8562);
    }

    private boolean method2385(String param1) {
        for (String v3 : this.field8495) {
            if (!v3.equalsIgnoreCase(param1)) continue;
            return true;
        }
        return false;
    }

    private Text method2386(String param1) {
        for (Map.Entry v3 : this.field8494.entrySet()) {
            if (!((String)v3.getKey()).equalsIgnoreCase(param1)) continue;
            return ((PlayerEntryComparator)v3.getValue()).method3286();
        }
        return Text.empty();
    }

    private void method2387(String param12) {
        if (param12 == null) {
            return;
        }
        boolean v2 = this.field8492.stream().anyMatch(param1 -> param1.method3284().equalsIgnoreCase(param12));
        boolean v3 = this.method2385(param12);
        if (!v2 && !v3) {
            NotificationManager.method2456(field8563, (String)((Object)_4ZXYEo.cfr_renamed_196("makeConcatWithConstants", new Object[]{}, (String)param12)), ColorUtil.method1852(255, 200, 0, 255));
        }
        this.field8495.add(param12);
        PlayerListEntry v4 = this.method2362(param12);
        Text v5 = v4 != null && this.method2363(param12) != null ? this.method2363(param12).getPrefix() : Text.empty();
        List v6 = this.method2367();
        boolean v7 = this.method2365(param12, v4, v6);
        PlayerEntryComparator v8 = new PlayerEntryComparator(param12, (Text)v5, v7);
        this.field8494.put(param12, v8);
        if (!v7 && this.field8493.stream().noneMatch(param1 -> param1.equalsIgnoreCase(param12))) {
            this.field8493.add(param12);
        }
    }

    private boolean method2390(String param1) {
        if (param1 == null) {
            return false;
        }
        for (String v3 : this.field8494.keySet()) {
            if (!v3.equalsIgnoreCase(param1)) continue;
            return true;
        }
        return false;
    }

    private boolean method2391(String param12, PlayerListEntry param2, List<String> param3) {
        if (param2 != null && param2.getGameMode() == GameMode.SPECTATOR) {
            return false;
        }
        return param3.stream().anyMatch(param1 -> param1.equalsIgnoreCase(param12));
    }

    static {
        field8509 = 3000L;
        field8500 = "§[0-9a-fk-orA-FK-OR]";
        field8501 = "[♦♥✪✦❖☯✿◆●○■□▪▫★☆]";
        field8502 = "→|->|»|:";
        field8503 = " ";
        field8504 = "\\s+";
        field8505 = " ";
        field8507 = "J";
        cfr_renamed_197 = "\u0001 вышел с сервера!";
        cfr_renamed_198 = "\u0001\u0001\u0001";
        field8510 = "J";
        cfr_renamed_199 = "\u0001 зашел на сервер!";
        field8512 = "[\\[\\](){}«»\\s§]";
        field8511 = "§[0-9a-fk-or]";
        field8498 = "\\s+";
        field8499 = "[^a-zA-Z0-9_]";
        field8563 = "J";
        cfr_renamed_200 = "Обнаружен стафф: \u0001";
        field8496 = "J";
        cfr_renamed_201 = "Обнаружен стафф: \u0001";
        cfr_renamed_202 = "\u0001 ";
        field8514 = "helper";
        field8515 = "moder";
        field8516 = "staff";
        field8517 = "admin";
        field8518 = "curator";
        field8519 = "intern";
        field8520 = "стажёр";
        field8521 = "стажер";
        field8522 = "ɪɴᴛᴇʀɴ";
        field8523 = "сотрудник";
        field8524 = "помощник";
        field8525 = "админ";
        field8526 = "интерн";
        field8527 = "ʜᴇʟᴘᴇʀ";
        field8528 = "ᴍᴏᴅᴇʀ";
        field8529 = "ᴅ.ᴏᴡɴᴇʀ";
        field8530 = "ᴏᴡɴᴇʀ";
        field8531 = "ᴢᴀᴍ.ᴄᴜʀᴀᴛᴏʀ";
        field8532 = "ᴄᴜʀᴀᴛᴏʀ";
        field8533 = "sᴘᴇᴄᴛᴀᴛᴏʀ";
        field8562 = "^[\\[\\(«]?([^\\]\\)»\\s]+)[\\]\\)»]?\\s+(\\w{3,16})";
        PlayerListUtil.method2384();
    }
}




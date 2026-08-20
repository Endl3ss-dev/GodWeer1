/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.network.PlayerListEntry
 *  net.minecraft.scoreboard.Team
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.Text
 *  net.minecraft.util.Formatting
 */
package minced.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import minced.core.EventChatMessage;
import minced.core.StaffEntry;
import minced.core.CancellableEvent;
import minced.core.PlayerListUtil;
import minced.core.EventUpdate;
import minced.core.StaffOnlineHud;
import minced.core.PlayerEntryComparator;
import minced.core.Minced;
import minced.manager.Staff;
import minced.module.player.AutoLeave;
import minced.module.visual.Interface;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class StaffList
implements CancellableEvent {
    static MinecraftClient I = MinecraftClient.getInstance();
    static Locale field9067;
    PlayerListUtil field9068;
    List field9069 = new ArrayList();
    static StaffList field9070;
    static String field9071;
    static Pattern field9072;
    static String field9073;
    static String field9074;
    static String field9075;
    static String field9076;
    static String field9077;
    static String field9078;
    static String field9079;
    static String field9080;
    static String field9081;
    static String field9082;
    static String field9083;
    static String field9084;
    static String field9085;
    static String field9086;
    static String field9087;
    static String field9088;
    static String field9089;
    static String field9090;
    static String field9091;
    static String field9092;
    static String field9093;
    static String field9094;
    static String field9095;
    static String field9096;
    static String field9097;
    static String field9098;
    static String field9099;
    static String field9100;
    static String field9101;
    static String field9102;
    static String field9103;
    static String field9104;
    static String field9105;
    static String field9106;
    static String field9107;

    private List method2569() {
        if (StaffList.I.world == null || I.getNetworkHandler() == null) {
            return Collections.emptyList();
        }
        Staff v1 = Minced.getInstance().getStaffManager();
        Set v2 = v1 != null ? (Set)v1.method1655().stream().map(param0 -> ((StaffEntry)param0).method1996()).filter(Objects::nonNull).map(param0 -> ((String)param0).toLowerCase(field9067)).collect(Collectors.toSet()) : (Set)Collections.emptySet();
        ArrayList<PlayerEntryComparator> v3 = new ArrayList<PlayerEntryComparator>();
        for (PlayerListEntry v5 : I.getNetworkHandler().getPlayerList()) {
            String v6;
            if (v5 == null || (v6 = v5.getProfile().getName()) == null || v6.isEmpty()) continue;
            Text v7 = Text.empty();
            java.util.Iterator v8it = StaffList.I.world.getScoreboard().getTeams().iterator();
            while (v8it.hasNext()) {
                Team v9 = (Team)v8it.next();
                if (!v9.getPlayerList().contains(v6)) continue;
                v7 = v9.getPrefix();
                break;
            }
            Object v8 = v7 != null ? v7.getString() : "";
            if (!v2.contains(v6.toLowerCase(field9067)) && !StaffList.method2576((String)v8)) continue;
            v3.add(new PlayerEntryComparator(v6, (Text)v7, false));
        }
        return v3;
    }

    public PlayerListUtil method2570() {
        return this.field9068;
    }

    public StaffList() {
        this.field9068 = new PlayerListUtil();
        field9070 = this;
    }

    public static void method2571() {
        field9067 = Locale.ROOT;
        field9072 = Pattern.compile(field9071);
    }

    public List method2572() {
        return this.field9069;
    }

    private static List method2573() {
        if (StaffList.I.world == null || I.getNetworkHandler() == null) {
            return Collections.emptyList();
        }
        Set v0 = I.getNetworkHandler().getPlayerList().stream().filter(Objects::nonNull).map(param0 -> param0.getProfile().getName()).filter(Objects::nonNull).collect(Collectors.toSet());
        ArrayList<PlayerEntryComparator> v1 = new ArrayList<PlayerEntryComparator>();
        for (Team v3 : StaffList.I.world.getScoreboard().getTeams().stream().sorted(Comparator.comparing(Team::getName)).toList()) {
            String v5;
            String v4 = v3.getPlayerList().stream().findFirst().orElse(null);
            if (v4 == null || !field9072.matcher(v4).matches() || v0.contains(v4) || (v5 = v3.getDisplayName() != null ? v3.getDisplayName().getString().trim() : "").startsWith(field9073) || v5.startsWith(field9074) || v5.startsWith(field9075) || v5.startsWith(field9076) || v5.startsWith(field9077) || v5.startsWith(field9078) || v5.startsWith(field9079)) continue;
            Text v6 = v3.getPrefix();
            if (v6 == null) {
                v6 = Text.empty();
            }
            v1.add(new PlayerEntryComparator(v4, v6, true));
        }
        return v1;
    }

    @EventHandler(2)
    public void method2574(EventChatMessage param1) {
        if (!StaffOnlineHud.field9868.isMode(field9080)) {
            return;
        }
        if (param1.method76() == null) {
            return;
        }
        this.field9068.method2368(param1.method76());
    }

    /*
     * Enabled aggressive block sorting
     * Converted monitor instructions to comments
     * Lifted jumps to return sites
     */
    public static List method2575() {
        if (field9070 == null) return StaffList.method2573();
        if (!StaffOnlineHud.field9868.isMode(field9081)) return StaffList.method2573();
        List v0 = StaffList.field9070.field9069;
        // MONITORENTER : v0
        // MONITOREXIT : v0
        return (List)StaffList.field9070.field9069.stream().filter(param0 -> ((PlayerEntryComparator)param0).method3285()).collect(Collectors.toCollection(ArrayList::new));
    }

    private static boolean method2576(String param0) {
        String v1 = Formatting.strip((String)param0).toLowerCase();
        return v1.contains(field9082) || v1.contains(field9083) || v1.contains(field9084) || v1.contains(field9085) || v1.contains(field9086) || v1.contains(field9087) || v1.contains(field9088) || v1.contains(field9089) || v1.contains(field9090) || v1.contains(field9091) || v1.contains(field9092) || v1.contains(field9093) || v1.contains(field9094) || v1.contains(field9095) || v1.contains(field9096) || v1.contains(field9097) || v1.contains(field9098) || v1.contains(field9099) || v1.contains(field9100) || v1.contains(field9101) || v1.contains(field9102) || v1.contains(field9103) || v1.contains(field9104) || v1.contains(field9105);
    }

    public static StaffList method2578() {
        return field9070;
    }

    @EventHandler(2)
    public void method2579(EventUpdate param1) {
        if (StaffList.I.player == null || StaffList.I.world == null) {
            return;
        }
        if (StaffOnlineHud.field9868.isMode(field9106)) {
            this.field9068.method2370();
            List v2 = this.field9069;
            synchronized (v2) {
                this.field9069.clear();
                this.field9069.addAll(this.field9068.method2380());
                return;
            }
        }
        if ((StaffList.I.player.age & 9) != 0) {
            return;
        }
        if (Minced.getInstance().getModuleManager().method187(Interface.class).isEnabled() && Interface.field2067.method1898(field9107).booleanValue() || Minced.getInstance().getModuleManager().method187(AutoLeave.class).isEnabled()) {
            this.method2581();
        }
    }

    public void method2581() {
        List v1 = this.method2569();
        List v2 = StaffList.method2573();
        v1.sort(Comparator.comparing(PlayerEntryComparator::method3284, String.CASE_INSENSITIVE_ORDER));
        v2.sort(Comparator.comparing(PlayerEntryComparator::method3284, String.CASE_INSENSITIVE_ORDER));
        List v3 = this.field9069;
        synchronized (v3) {
            this.field9069.clear();
            this.field9069.addAll(v1);
            this.field9069.addAll(v2);
            return;
        }
    }

    static {
        field9106 = "LonyGrief";
        field9107 = "Администрация онлайн";
        field9080 = "LonyGrief";
        field9081 = "LonyGrief";
        field9073 = "npc";
        field9074 = "collideRule_";
        field9075 = "FS_";
        field9076 = "STAFF-";
        field9077 = "HELPER-";
        field9078 = "ADMIN-";
        field9079 = "MODER-";
        field9082 = "helper";
        field9083 = "стажер";
        field9084 = "сотрудник";
        field9085 = "вед";
        field9086 = "спектатор";
        field9087 = "moder";
        field9088 = "admin";
        field9089 = "curator";
        field9090 = "builder";
        field9091 = "yt";
        field9092 = "кур";
        field9093 = "тех.админ";
        field9094 = "s.kurator";
        field9095 = "поддержка";
        field9096 = "сотруд";
        field9097 = "ꔁ";
        field9098 = "ꔅ";
        field9099 = "ꔓ";
        field9100 = "ꔗ";
        field9101 = "ꔡ";
        field9102 = "ꔥ";
        field9103 = "ꔩ";
        field9104 = "ꔳ";
        field9105 = "ꔷ";
        field9071 = "^\\w{3,16}$";
        StaffList.method2571();
    }
}




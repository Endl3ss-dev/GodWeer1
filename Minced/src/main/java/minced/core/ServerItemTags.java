/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtList
 *  net.minecraft.text.CharacterVisitor
 *  sg.ec.23$2
 *  sg.ec.23$П
 *  sg.ec.23$Ф
 *  sg.ec.23$Ч
 */
package minced.core;

import java.util.Map;
import java.util.Set;
import minced.core.ColorUtil;
import minced.core.NbtUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.CharacterVisitor;
import sg.ec._23;

public class ServerItemTags
implements CharacterVisitor {
    @Override
    public boolean accept(int p0, net.minecraft.text.Style p1, int p2) { return false; }

    static String field9928;
    static String field9929;
    static String field9930;
    static String field9931;
    static String field9932 = "field9932";
    static String field9933 = "field9933";
    static String field9934 = "field9934";
    static String field9935 = "field9935";
    static String field9936 = "field9936";
    static String field9937 = "field9937";
    static String field9938 = "field9938";
    static Set field9939;
    static String field9940 = "field9940";
    static String field9941;
    static String field9942 = "field9942";
    static String field9943;
    static String field9944 = "field9944";
    static String field9945;
    static String field9946 = "field9946";
    static String field9947;
    static String field9948 = "field9948";
    static String field9949;
    static String field9950 = "field9950";
    static String field9951;
    static String field9952 = "field9952";
    static String field9953;
    static String field9954 = "field9954";
    static String field9955;
    static String field9956 = "field9956";
    static String field9957;
    static String field9958 = "field9958";
    static String field9959;
    static String field9960 = "field9960";
    static String field9961;
    static String field9962 = "field9962";
    static String field9963;
    static String field9964 = "field9964";
    static String field9965;
    static String field9966 = "field9966";
    static String field9967;
    static String field9968 = "field9968";
    static String field9969;
    static String field9970 = "field9970";
    static String field9971;
    static Map field9972;
    static String field9973 = "field9973";
    static String field9974;
    static String field9975 = "field9975";
    static String field9976;
    static String field9977 = "field9977";
    static String field9978;
    static String field9979 = "field9979";
    static String field9980;
    static String field9981 = "field9981";
    static String field9982;
    static String field9983 = "field9983";
    static String field9984;
    static String field9985 = "field9985";
    static String field9986;
    static String field9987 = "field9987";
    static String field9988;
    static String field9989 = "field9989";
    static String field9990;
    static String field9991 = "field9991";
    static String field9992;
    static String field9993 = "field9993";
    static String field9994;
    static Map field9995;
    static String field9996 = "field9996";
    static String field9997;
    static String field9998 = "field9998";
    static String field9999;
    static String field10000 = "field10000";
    static String field10001;
    static String field10002 = "field10002";
    static String field10003;
    static String field10004 = "field10004";
    static String field10005;
    static String field10006 = "field10006";
    static String field10007;
    static String field10008 = "field10008";
    static String field10009;
    static String field10010 = "field10010";
    static String field10011;
    static String field10012 = "field10012";
    static String field10013;
    static String field10014 = "field10014";
    static String field10015;
    static String field10016 = "field10016";
    static String field10017;
    static String field10018 = "field10018";
    static String field10019;
    static String field10020 = "field10020";
    static String field10021;
    static String field10022 = "field10022";
    static String field10023;
    static String field10024 = "field10024";
    static String field10025;
    static String field10026 = "field10026";
    static String field10027;
    static String field10028 = "field10028";
    static String field10029;
    static Map field10030;
    static String field10031;
    static int field10032;
    static String field10033;
    static int field10034;
    static String field10035;
    static int cfr_underscore;
    static String field10036;
    static int cfr_renamed_0;
    static String field10037;
    static int field10038;
    static String field10039;
    static String field10040;
    static String field10041;
    static String field10042;
    static String field10043;
    static String field10044;
    static String field10045;
    static String field10046;
    static String field10047;
    static String field10048;
    static String field10049;
    static String field10050;
    static String field10051;
    static String field10052;
    static String field10053;
    static String field10054;
    static String field10055;
    static String field10056;
    static String field10057;
    static String field10058;
    static String field10059;
    static String field10060;
    static String field10061;
    static String field10062;
    static String field10063;

    private static String method2869(NbtCompound param0, String param1) {
        if (!param0.contains(param1, 8)) {
            return null;
        }
        String v2 = param0.getString(param1);
        return v2.isEmpty() ? null : v2;
    }

    public static boolean method2870(ItemStack param0) {
        _23.p11 v1 = ServerItemTags.method2873(param0);
        return v1 != null && (Boolean)v1.cfr_renamed_0();
    }

    public static boolean method2871(ItemStack param0) {
        _23.p11 v1 = ServerItemTags.method2873(param0);
        return v1 != null && (Boolean)v1.sh();
    }

    private static _23.p11 method2872(ItemStack param0, NbtCompound param1) {
        String v2 = ServerItemTags.method2883(param1, field9928, field9929);
        if (v2 == null) {
            v2 = ServerItemTags.method2869(param1, field9930);
        }
        if (v2 != null) {
            return new _23.p11(_23.f13.CONSUMABLE, v2, _23.ch.FUNTIME);
        }
        if (ServerItemTags.method2876(param0, param1)) {
            String v3 = ServerItemTags.method2878(param1);
            return new _23.p11(_23.f13.TALISMAN, v3, _23.ch.FUNTIME);
        }
        return null;
    }

    public static _23.p11 method2873(ItemStack param0) {
        if (param0 == null || param0.isEmpty()) {
            return null;
        }
        NbtCompound v1 = NbtUtil.method3506(param0);
        if (v1 == null) {
            return null;
        }
        _23.p11 v2 = ServerItemTags.method2881(v1);
        if (v2 != null) {
            return v2;
        }
        v2 = ServerItemTags.method2879(v1);
        if (v2 != null) {
            return v2;
        }
        return ServerItemTags.method2872(param0, v1);
    }

    public static boolean method2874(ItemStack param0) {
        return ServerItemTags.method2873(param0) != null;
    }

    public static _23.f13 method2875(ItemStack param0) {
        _23.p11 v1 = ServerItemTags.method2873(param0);
        return v1 != null ? v1.Ц() : null;
    }

    private static boolean method2876(ItemStack param0, NbtCompound param1) {
        return param0.getItem() == Items.TOTEM_OF_UNDYING && param1.contains(field9931) && param0.hasEnchantments();
    }

    public static void method2877() {
        field9939 = Set.of(field9932, field9933, field9934, field9935, field9936, field9937, field9938);
        field9972 = Map.ofEntries(Map.entry(field9940, field9941), Map.entry(field9942, field9943), Map.entry(field9944, field9945), Map.entry(field9946, field9947), Map.entry(field9948, field9949), Map.entry(field9950, field9951), Map.entry(field9952, field9953), Map.entry(field9954, field9955), Map.entry(field9956, field9957), Map.entry(field9958, field9959), Map.entry(field9960, field9961), Map.entry(field9962, field9963), Map.entry(field9964, field9965), Map.entry(field9966, field9967), Map.entry(field9968, field9969), Map.entry(field9970, field9971));
        field9995 = Map.ofEntries(Map.entry(field9973, ColorUtil.cfr_renamed_0(field9974)), Map.entry(field9975, ColorUtil.cfr_renamed_0(field9976)), Map.entry(field9977, ColorUtil.cfr_renamed_0(field9978)), Map.entry(field9979, ColorUtil.cfr_renamed_0(field9980)), Map.entry(field9981, ColorUtil.cfr_renamed_0(field9982)), Map.entry(field9983, ColorUtil.cfr_renamed_0(field9984)), Map.entry(field9985, ColorUtil.cfr_renamed_0(field9986)), Map.entry(field9987, ColorUtil.cfr_renamed_0(field9988)), Map.entry(field9989, ColorUtil.cfr_renamed_0(field9990)), Map.entry(field9991, ColorUtil.cfr_renamed_0(field9992)), Map.entry(field9993, ColorUtil.cfr_renamed_0(field9994)));
        field10030 = Map.ofEntries(Map.entry(field9996, field9997), Map.entry(field9998, field9999), Map.entry(field10000, field10001), Map.entry(field10002, field10003), Map.entry(field10004, field10005), Map.entry(field10006, field10007), Map.entry(field10008, field10009), Map.entry(field10010, field10011), Map.entry(field10012, field10013), Map.entry(field10014, field10015), Map.entry(field10016, field10017), Map.entry(field10018, field10019), Map.entry(field10020, field10021), Map.entry(field10022, field10023), Map.entry(field10024, field10025), Map.entry(field10026, field10027), Map.entry(field10028, field10029));
        field10032 = ColorUtil.cfr_renamed_0(field10031);
        field10034 = ColorUtil.cfr_renamed_0(field10033);
        cfr_underscore = ColorUtil.cfr_renamed_0(field10035);
        cfr_renamed_0 = ColorUtil.cfr_renamed_0(field10036);
        field10038 = ColorUtil.cfr_renamed_0(field10037);
    }

    private static String method2878(NbtCompound param0) {
        if (!param0.contains(field10039, 10)) {
            return null;
        }
        NbtCompound v1 = param0.getCompound(field10040);
        if (!v1.contains(field10041, 9)) {
            return null;
        }
        NbtList v2 = v1.getList(field10042, 8);
        if (v2.isEmpty()) {
            return null;
        }
        String v3 = v2.getString(0);
        for (Map.Entry v5 : (Iterable<java.util.Map.Entry>)(field9972.entrySet())) {
            if (!v3.contains((CharSequence)v5.getKey())) continue;
            return (String)v5.getValue();
        }
        return null;
    }

    private static _23.p11 method2879(NbtCompound param0) {
        String v1;
        if (param0.contains(field10043, 10) && !(v1 = param0.getCompound(field10044).getString(field10045)).isEmpty()) {
            return new _23.p11(_23.f13.CONSUMABLE, v1, _23.ch.HOLYWORLD);
        }
        if (param0.contains(field10046, 10) && !(v1 = param0.getCompound(field10047).getString(field10048)).isEmpty()) {
            return new _23.p11(_23.f13.CONSUMABLE, v1, _23.ch.HOLYWORLD);
        }
        if (param0.contains(field10049, 10) && !(v1 = param0.getCompound(field10050).getString(field10051)).isEmpty()) {
            return new _23.p11(_23.f13.ARTIFACT, v1, _23.ch.HOLYWORLD);
        }
        if (param0.contains(field10052, 10)) {
            NbtCompound v1s = param0.getCompound(field10053);
            String v2 = v1s.getString(field10054);
            String v3 = v1s.getString(field10055);
            boolean v4 = v1s.getBoolean(field10056);
            _23._2 v5 = _23._2.В((String)v3);
            _23.f13 v6 = v4 ? _23.f13.TALISMAN : _23.f13.SPHERE;
            return new _23.p11(v6, v2.isEmpty() ? null : v2, v5, _23.ch.HOLYWORLD);
        }
        return null;
    }

    public static boolean method2880(ItemStack param0) {
        _23.p11 v1 = ServerItemTags.method2873(param0);
        return v1 != null && (Boolean)v1.西();
    }

    private static _23.p11 method2881(NbtCompound param0) {
        String v1 = ServerItemTags.method2883(param0, field10057, field10058);
        if (v1 == null) {
            v1 = ServerItemTags.method2869(param0, field10059);
        }
        if (v1 == null) {
            return null;
        }
        _23.f13 v2 = ServerItemTags.method2882(v1);
        return new _23.p11(v2, v1, _23.ch.SPOOKYTIME);
    }

    private static _23.f13 method2882(String param0) {
        if (param0.startsWith(field10060) || param0.startsWith(field10061)) {
            return _23.f13.CONSUMABLE;
        }
        if (param0.startsWith(field10062)) {
            return _23.f13.TALISMAN;
        }
        if (param0.startsWith(field10063)) {
            return _23.f13.SPHERE;
        }
        return _23.f13.UNKNOWN;
    }

    private static String method2883(NbtCompound param0, String param1, String param2) {
        if (!param0.contains(param1, 10)) {
            return null;
        }
        NbtCompound v3 = param0.getCompound(param1);
        if (!v3.contains(param2, 8)) {
            return null;
        }
        String v4 = v3.getString(param2);
        return v4.isEmpty() ? null : v4;
    }

    public static boolean method2884(ItemStack param0) {
        _23.p11 v1 = ServerItemTags.method2873(param0);
        return v1 != null && (Boolean)v1.Д();
    }

    static {
        field10032 = -5650472;
        field10034 = -16744198;
        cfr_underscore = -3342216;
        cfr_renamed_0 = -1638150;
        field10038 = -813062;
        field10057 = "PublicBukkitValues";
        field10058 = "spookyitems:spooky-item";
        field10059 = "spooky-item";
        field10060 = "effect-item-";
        field10061 = "schematic-item-";
        field10062 = "attribute-item-t";
        field10063 = "attribute-item-s";
        field10043 = "pyrotechnic-item";
        field10044 = "pyrotechnic-item";
        field10045 = "name";
        field10046 = "kringeItems";
        field10047 = "kringeItems";
        field10048 = "type";
        field10049 = "kringeEffect";
        field10050 = "kringeEffect";
        field10051 = "type";
        field10052 = "sphereEffect";
        field10053 = "sphereEffect";
        field10054 = "name";
        field10055 = "rank";
        field10056 = "isMascot";
        field9928 = "PublicBukkitValues";
        field9929 = "minecraft:don-item";
        field9930 = "don-item";
        field9931 = "AttributeModifiers";
        field10039 = "display";
        field10040 = "display";
        field10041 = "Lore";
        field10042 = "Lore";
        field9941 = "talisman_vortex";
        field9943 = "talisman_grinch";
        field9945 = "talisman_crusher";
        field9947 = "talisman_punisher";
        field9949 = "talisman_harmony";
        field9951 = "talisman_daedalus";
        field9953 = "talisman_echidna";
        field9955 = "talisman_edge";
        field9957 = "talisman_triton";
        field9959 = "talisman_phoenix";
        field9961 = "talisman_crusher";
        field9963 = "talisman_darkness";
        field9965 = "talisman_demon";
        field9967 = "talisman_discord";
        field9969 = "talisman_tyrant";
        field9971 = "talisman_rage";
        field9974 = "FF0000";
        field9976 = "E4E4E4";
        field9978 = "7F83A5";
        field9980 = "CE7FDA";
        field9982 = "CD0078";
        field9984 = "CD0078";
        field9986 = "CD0078";
        field9988 = "7700DF";
        field9990 = "3E4E73";
        field9992 = "0080FA";
        field9994 = "0080FA";
        field9997 = "Взрывная трапка";
        field9999 = "Стан";
        field10001 = "Трапка";
        field10003 = "Взрывная штучка";
        field10005 = "Снежок";
        field10007 = "Дезориентация";
        field10009 = "Пыль";
        field10011 = "Аура бога";
        field10013 = "Трапка";
        field10015 = "Взрывная трапка";
        field10017 = "Дезориентация";
        field10019 = "Пыль";
        field10021 = "Аура бога";
        field10023 = "Трапка";
        field10025 = "Взрывная трапка";
        field10027 = "Снежок";
        field10029 = "Стан";
        field10031 = "A9C7D8";
        field10033 = "0080FA";
        field10035 = "CD0078";
        field10036 = "E700FA";
        field10037 = "F397FA";
        ServerItemTags.method2877();
    }
}




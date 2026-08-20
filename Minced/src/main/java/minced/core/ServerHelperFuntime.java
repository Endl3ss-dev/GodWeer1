/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.component.type.LoreComponent
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.network.packet.s2c.play.GameMessageS2CPacket
 *  net.minecraft.text.Text
 *  sLM.4ZXYEo
 *  sg.ec.2>
 *  sg.ec.ch8
 */
package minced.core;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import minced.core.BindSetting;
import minced.core.ServerHelperBase;
import minced.core.ItemListSetting;
import minced.core.EventPacket;
import minced.core.ChatUtil;
import minced.enums.KeyCode;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.Text;
import sLM._4ZXYEo;
import sg.ec.CfrRenamed298;
import sg.ec.ch8;

public class ServerHelperFuntime
extends ServerHelperBase {
    static String шц;
    static String шИ;
    static String ш西;
    static String шж;

    static Pattern field7868;
    static Pattern field7869;
    boolean field7870;
    String field7871;
    static String field7872;
    static String field7873;
    boolean field7874;
    static String field7875;
    static String field7876;
    static String field7877;
    static String field7878;
    static double field7879;
    MinecraftClient var_net_minecraft_client_MinecraftClient_I;
    static float field7880;
    static float field7881;
    static float field7882;
    static String field7883;
    Item field7884;
    static String field7885;
    static String field7886;
    static String field7887;
    static String field7888;
    static String field7889;
    static String field7890;
    static String field7891;
    static String field7892;
    static String field7893;
    static String field7894;
    static String field7895;
    static String field7896;
    static String field7897;
    static String field7898;
    static String field7899;
    static String field7900;
    static String field7901;
    static String field7902;
    static String field7903;
    String[] field7904 = new String[]{field7915, field7916, field7917, field7918};
    List field7905;
    static String field7906;
    static String field7907;
    Item[] var_net_minecraft_item_Item_arr_I = new Item[]{Items.MAGMA_CREAM, Items.FEATHER, Items.CLAY_BALL, Items.CRYING_OBSIDIAN};
    static String field7908;
    static float field7909;
    static String field7910;
    static String field7911;
    static String field7912;
    static String field7913;
    static String field7914;
    static String field7915 = "Обычная ливалка";
    static String field7916 = "Уникальное перышко";
    static String field7917 = "Ливалка с платформой";
    static String field7918 = "Уникальная трапка";
    BooleanSupplier field7919;
    static String field7920;
    long field7921;
    static long field7922;
    static String field7923;
    static String field7924;
    static String field7925;
    static String field7926;

    private static String method1949(String param0) {
        Matcher v1 = field7868.matcher(param0);
        if (v1.find()) {
            String v2 = v1.group(2);
            double v3 = Double.parseDouble(v1.group(3));
            double v5 = Double.parseDouble(v1.group(4));
            double v7 = ServerHelperFuntime.method1958(v2, v3, v5);
            return ServerHelperFuntime.method1954(v7);
        }
        Matcher v2 = field7869.matcher(param0);
        if (v2.find()) {
            double v3 = Double.parseDouble(v2.group(1));
            String v5 = v2.group(2);
            double v6 = Double.parseDouble(v2.group(4));
            double v8 = ServerHelperFuntime.method1953(v3, v5, v6);
            return ServerHelperFuntime.method1954(v8);
        }
        return null;
    }

    @Override
    public boolean method1889() {
        return this.field7870 && this.field7871 != null;
    }

    public static void method1950() {
        field7868 = Pattern.compile(field7872);
        field7869 = Pattern.compile(field7873);
    }

    public int method1951() {
        if (this.field7871 == null) {
            return -1;
        }
        ItemListSetting v1 = ch8.А((String)this.field7871);
        if (v1 == null) {
            return -1;
        }
        return this.method1957(v1);
    }

    public void method1952(boolean param1) {
        this.field7874 = param1;
    }

    private static double method1953(double param0, String param2, double param3) {
        double d;
        int v6 = -1;
        switch (param2.hashCode()) {
            case 43: {
                if (!param2.equals(field7875)) break;
                v6 = 0;
                break;
            }
            case 45: {
                if (!param2.equals(field7876)) break;
                v6 = 1;
                break;
            }
            case 42: {
                if (!param2.equals(field7877)) break;
                v6 = 2;
                break;
            }
            case 47: {
                if (!param2.equals(field7878)) break;
                v6 = 3;
            }
        }
        switch (v6) {
            case 0: {
                d = param3 - param0;
                break;
            }
            case 1: {
                d = param0 - param3;
                break;
            }
            case 2: {
                d = param3 / param0;
                break;
            }
            case 3: {
                d = param0 / param3;
                break;
            }
            default: {
                d = Double.NaN;
            }
        }
        return d;
    }

    private static String method1954(double param0) {
        if (Double.isNaN(param0) || Double.isInfinite(param0)) {
            return null;
        }
        long v2 = Math.round(param0);
        if (Math.abs(param0 - (double)v2) < field7879) {
            return Long.toString(v2);
        }
        return Double.toString(param0);
    }

    private void method1955(String param1, Item param2) {
        ItemListSetting v3 = ch8.А((String)param1);
        if (v3 == null) {
            ChatUtil.method3308(_4ZXYEo.O("makeConcatWithConstants", new Object[]{}, (String)param1), new Object[0]);
            return;
        }
        int v4 = this.method1957(v3);
        if (v4 == -1) {
            ChatUtil.method3308(_4ZXYEo.ь("makeConcatWithConstants", new Object[]{}, (String)param1), new Object[0]);
            return;
        }
        if (this.field7874 && v4 >= 9) {
            ChatUtil.method3308(_4ZXYEo.cfr_renamed_0("makeConcatWithConstants", new Object[]{}, (String)param1), new Object[0]);
            return;
        }
        if (this.var_net_minecraft_client_MinecraftClient_I.player.getItemCooldownManager().isCoolingDown(param2.getDefaultStack())) {
            float v5 = this.var_net_minecraft_client_MinecraftClient_I.player.getItemCooldownManager().getCooldownProgress(param2.getDefaultStack(), var_net_minecraft_client_MinecraftClient_I.getRenderTickCounter().getLastDuration());
            float v6 = v5 * field7880 * field7881;
            float v7 = v6 / field7882;
            ChatUtil.method3308(_4ZXYEo.Е("makeConcatWithConstants", new Object[]{}, (String)param1, (String)String.format(field7883, Float.valueOf(v7))), new Object[0]);
            return;
        }
        this.field7870 = true;
        this.field7884 = param2;
        this.field7871 = param1;
    }

    private static String method1956(String param0) {
        String v1 = param0.replaceAll(field7885, "");
        v1 = v1.replace(field7886, field7887).replace(field7888, field7889).replace(field7890, field7891);
        v1 = v1.replace(field7892, field7893).replace(field7894, field7895);
        v1 = v1.replace(',', '.');
        return v1.trim();
    }

    private int method1957(ItemListSetting param1) {
        if (var_net_minecraft_client_MinecraftClient_I == null || this.var_net_minecraft_client_MinecraftClient_I.player == null || param1 == null) {
            return -1;
        }
        Item v2 = param1.method2243().getItem();
        List v3 = param1.method2256();
        String v4 = param1.getName();
        for (int v5 = 0; v5 < 36; ++v5) {
            boolean v11;
            ItemStack v6 = this.var_net_minecraft_client_MinecraftClient_I.player.getInventory().getStack(v5);
            if (v6.isEmpty()) continue;
            String v7 = v6.getName().getString();
            String v8 = v7.replaceAll(field7896, "").replaceAll(field7897, "").replaceAll(field7898, field7899).trim().toLowerCase();
            String v9 = v4.replaceAll(field7900, "").replaceAll(field7901, field7902).trim().toLowerCase();
            Item v10 = v6.getItem();
            boolean bl = v11 = v8.contains(v9) || v9.contains(v8) || v8.equals(v9);
            if (v11 && (v3.isEmpty() || this.method1959(v6, v3))) {
                return v5;
            }
            if (v10 != v2) continue;
            if (v3.isEmpty()) {
                return v5;
            }
            if (!this.method1959(v6, v3)) continue;
            return v5;
        }
        return -1;
    }

    @Override
    public void method1890() {
        this.method1960();
    }

    @Override
    public String method1895() {
        return field7903;
    }

    @Override
    public List method1894(boolean param1) {
        ArrayList<CfrRenamed298> v2 = new ArrayList<CfrRenamed298>();
        for (int v3 = 0; v3 < this.field7904.length && v3 < this.field7905.size(); ++v3) {
            String v6;
            int v5;
            ItemListSetting v4 = ch8.А((String)this.field7904[v3]);
            int n = v5 = v4 != null ? this.method1957(v4) : -1;
            if (v5 == -1) {
                v6 = field7906;
            } else if (param1 && v5 >= 9) {
                v6 = field7907;
            } else if (this.var_net_minecraft_client_MinecraftClient_I.player.getItemCooldownManager().isCoolingDown(this.var_net_minecraft_item_Item_arr_I[v3].getDefaultStack())) {
                float v7 = this.var_net_minecraft_client_MinecraftClient_I.player.getItemCooldownManager().getCooldownProgress(this.var_net_minecraft_item_Item_arr_I[v3].getDefaultStack(), var_net_minecraft_client_MinecraftClient_I.getRenderTickCounter().getLastDuration());
                v6 = String.format(field7908, Float.valueOf(v7 * field7909));
            } else {
                v6 = field7910;
            }
            int v7 = (Integer)((BindSetting)this.field7905.get(v3)).getValue();
            String v8 = v7 == -1 ? "" : KeyCode.getDisplayName(v7);
            String v9 = this.field7904[v3].length() > 8 ? this.field7904[v3].substring(0, 8) : this.field7904[v3];
            v2.add(new CfrRenamed298(this.field7904[v3], v9, v6, this.var_net_minecraft_item_Item_arr_I[v3], v8, -1));
        }
        return v2;
    }

    private static double method1958(String param0, double param1, double param3) {
        double d;
        int v6 = -1;
        switch (param0.hashCode()) {
            case 43: {
                if (!param0.equals(field7911)) break;
                v6 = 0;
                break;
            }
            case 45: {
                if (!param0.equals(field7912)) break;
                v6 = 1;
                break;
            }
            case 42: {
                if (!param0.equals(field7913)) break;
                v6 = 2;
                break;
            }
            case 47: {
                if (!param0.equals(field7914)) break;
                v6 = 3;
            }
        }
        switch (v6) {
            case 0: {
                d = param3 - param1;
                break;
            }
            case 1: {
                d = param3 + param1;
                break;
            }
            case 2: {
                d = param3 / param1;
                break;
            }
            case 3: {
                d = param3 * param1;
                break;
            }
            default: {
                d = Double.NaN;
            }
        }
        return d;
    }

    public ServerHelperFuntime(List param1, BooleanSupplier param2) {
        this.field7905 = param1;
        this.field7919 = param2;
    }

    private boolean method1959(ItemStack param1, List<String> param2) {
        if (param2.isEmpty()) {
            return true;
        }
        LoreComponent v3 = (LoreComponent)param1.get(DataComponentTypes.LORE);
        if (v3 == null) {
            return false;
        }
        StringBuilder v4 = new StringBuilder();
        for (Text v6 : v3.lines()) {
            v4.append(v6.getString().toLowerCase()).append(field7920);
        }
        String v5 = v4.toString();
        for (String v7 : param2) {
            if (v5.contains(v7.toLowerCase())) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean method1888(int param1) {
        for (int v2 = 0; v2 < this.field7904.length && v2 < this.field7905.size(); ++v2) {
            if ((Integer)((BindSetting)this.field7905.get(v2)).getValue() == -1 || (Integer)((BindSetting)this.field7905.get(v2)).getValue() != param1) continue;
            this.method1955(this.field7904[v2], this.var_net_minecraft_item_Item_arr_I[v2]);
            return true;
        }
        return false;
    }

    public void method1960() {
        this.field7870 = false;
        this.field7871 = null;
        this.field7884 = null;
    }

    @EventHandler(2)
    private void method1961(EventPacket param1) {
        boolean v6;
        GameMessageS2CPacket v2;
        Object v3;
        if (param1.method2466() && (v3 = param1.method2467()) instanceof GameMessageS2CPacket) {
            v2 = (GameMessageS2CPacket)v3;
            if (!this.field7919.getAsBoolean()) {
                return;
            }
        } else {
            return;
        }
        if (this.var_net_minecraft_client_MinecraftClient_I.player == null || this.var_net_minecraft_client_MinecraftClient_I.player.networkHandler == null) {
            return;
        }
        if (System.currentTimeMillis() - this.field7921 < field7922) {
            return;
        }
        v3 = v2.content().getString();
        String v4 = ServerHelperFuntime.method1956((String)v3);
        if (v4.isEmpty() || !v4.contains(field7923)) {
            return;
        }
        String v5 = v4.toLowerCase();
        boolean bl = v6 = v5.contains(field7924) || v5.contains(field7925) || v5.contains(field7926);
        if (!v6) {
            return;
        }
        String v7 = ServerHelperFuntime.method1949(v4);
        if (v7 == null) {
            return;
        }
        this.field7921 = System.currentTimeMillis();
        if (this.var_net_minecraft_client_MinecraftClient_I.inGameHud != null) {
            this.var_net_minecraft_client_MinecraftClient_I.inGameHud.getChatHud().addMessage((Text)Text.literal((String)v7));
        }
        this.var_net_minecraft_client_MinecraftClient_I.player.networkHandler.sendChatMessage(v7);
    }

    static {
        field7903 = "LonyGrief";
        field7906 = "—";
        field7907 = "—";
        field7908 = "%.0fс";
        field7909 = 20.0f;
        field7910 = "✓";
        шц = "§c\u0001 не найден в конфигурации";
        шИ = "§c\u0001 не найден в инвентаре";
        ш西 = "§c\u0001 не в хотбаре";
        field7880 = 20.0f;
        field7881 = 20.0f;
        field7882 = 20.0f;
        field7883 = "%.1f";
        шж = "§c\u0001 заблокирован еще \u0001 сек";
        field7896 = "§[0-9a-fk-or]";
        field7897 = "[«»\"'`]";
        field7898 = "\\s+";
        field7899 = " ";
        field7900 = "[«»\"'`]";
        field7901 = "\\s+";
        field7902 = " ";
        field7920 = " ";
        field7922 = 400L;
        field7923 = "=";
        field7924 = "найдите";
        field7925 = "чат-игра";
        field7926 = "решите уравнение";
        field7885 = "§[0-9a-fk-or]";
        field7886 = "⎹";
        field7887 = " ";
        field7888 = "⎧";
        field7889 = " ";
        field7890 = "⎩";
        field7891 = " ";
        field7892 = "⊲";
        field7893 = " ";
        field7894 = "⊳";
        field7895 = " ";
        field7911 = "+";
        field7912 = "-";
        field7913 = "*";
        field7914 = "/";
        field7875 = "+";
        field7876 = "-";
        field7877 = "*";
        field7878 = "/";
        field7879 = 1.0E-4;
        field7872 = "([xXхХ])\\s*([+\\-*/])\\s*([\\d.,]+)\\s*=\\s*([\\d.,]+)";
        field7873 = "([\\d.,]+)\\s*([+\\-*/])\\s*([xXхХ])\\s*=\\s*([\\d.,]+)";
        ServerHelperFuntime.method1950();
    }
}




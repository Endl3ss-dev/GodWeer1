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
 *  sLM.4ZXYEo
 *  sg.ec.2>
 *  sg.ec.ch8
 */
package minced.core;

import java.util.ArrayList;
import java.util.List;
import minced.core.BindSetting;
import minced.core.BooleanSetting;
import minced.core.ServerHelperBase;
import minced.core.ItemListSetting;
import minced.core.ChatUtil;
import minced.enums.KeyCode;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import sLM._4ZXYEo;
import sg.ec.CfrRenamed298;
import sg.ec.ch8;

import net.minecraft.text.Text;

public class ServerHelperHolyworld
extends ServerHelperBase {
    static String cfr_renamed_169;
    static String cfr_renamed_170;
    static String cfr_renamed_171;
    static String cfr_renamed_172;

    boolean field8289;
    String field8290;
    static String field8291;
    boolean field8292;
    static MinecraftClient I = MinecraftClient.getInstance();
    static float field8293;
    static float field8294;
    static float field8295;
    static String field8296;
    Item field8297;
    static String field8298 = "Пузырёк опыта";
    static String field8299 = "Трапка";
    static String field8300 = "Взрывная трапка";
    static String field8301 = "Прощальный гул";
    static String field8302 = "Взрывная штучка";
    static String field8303 = "Стан";
    static String field8304 = "Ком снега";
    String[] field8305 = new String[]{field8298, field8299, field8300, field8301, field8302, field8303, field8304};
    Item[] field8306 = new Item[]{Items.EXPERIENCE_BOTTLE, Items.POPPED_CHORUS_FRUIT, Items.PRISMARINE_SHARD, Items.FIREWORK_STAR, Items.FIRE_CHARGE, Items.NETHER_STAR, Items.SNOWBALL};
    List field8307;
    BooleanSetting field8308;
    static String field8309;
    static String field8310;
    static String field8311;
    static String field8312;
    static String field8313;
    static String field8314;
    static String field8315;
    static String field8316;
    static String field8317;
    static String field8318;
    static float field8319;
    static String field8320;
    static String field8321;
    static String field8322;
    static String field8323;
    static String field8324;
    static String field8325;
    static String field8326;
    static String field8327;
    static String field8328;

    @Override
    public boolean method1889() {
        return this.field8289 && this.field8290 != null;
    }

    @Override
    public String method1895() {
        return field8291;
    }

    private void method2226(String param1, Item param2) {
        ItemListSetting v3 = ch8.А((String)param1);
        int v4 = -1;
        if (v3 != null && (v4 = this.method2231(v3)) == -1) {
            v4 = this.method2227(param1);
        }
        if (v3 == null && v4 == -1) {
            v4 = this.I(param2);
            if (v4 == -1) {
                ChatUtil.method3308(_4ZXYEo.фщ("makeConcatWithConstants", new Object[]{}, (String)param1), new Object[0]);
                return;
            }
        } else if (v4 == -1) {
            v4 = this.I(param2);
        }
        if (v4 == -1) {
            ChatUtil.method3308(_4ZXYEo.фЩ("makeConcatWithConstants", new Object[]{}, (String)param1), new Object[0]);
            return;
        }
        if (this.field8292 && v4 >= 9) {
            ChatUtil.method3308(_4ZXYEo.фХ("makeConcatWithConstants", new Object[]{}, (String)param1), new Object[0]);
            return;
        }
        if (ServerHelperHolyworld.I.player.getItemCooldownManager().isCoolingDown(param2.getDefaultStack())) {
            float v5 = ServerHelperHolyworld.I.player.getItemCooldownManager().getCooldownProgress(param2.getDefaultStack(), I.getRenderTickCounter().getLastDuration());
            float v6 = v5 * field8293 * field8294;
            float v7 = v6 / field8295;
            ChatUtil.method3308(_4ZXYEo.фФ("makeConcatWithConstants", new Object[]{}, (String)param1, (String)String.format(field8296, Float.valueOf(v7))), new Object[0]);
            return;
        }
        this.field8289 = true;
        this.field8297 = param2;
        this.field8290 = param1;
    }

    public ServerHelperHolyworld(List param1, BooleanSetting param2) {
        this.field8307 = param1;
        this.field8308 = param2;
    }

    private int method2227(String param1) {
        if (I == null || ServerHelperHolyworld.I.player == null || param1 == null) {
            return -1;
        }
        String v2 = param1.replaceAll(field8309, "").replaceAll(field8310, field8311).trim().toLowerCase();
        for (int v3 = 0; v3 < 36; ++v3) {
            String v5;
            String v6;
            ItemStack v4 = ServerHelperHolyworld.I.player.getInventory().getStack(v3);
            if (v4.isEmpty() || !(v6 = (v5 = v4.getName().getString()).replaceAll(field8312, "").replaceAll(field8313, "").replaceAll(field8314, field8315).trim().toLowerCase()).contains(v2) && !v2.contains(v6) && !v6.equals(v2)) continue;
            return v3;
        }
        return -1;
    }

    @Override
    public List method1894(boolean param1) {
        ArrayList<CfrRenamed298> v2 = new ArrayList<CfrRenamed298>();
        for (int v3 = 0; v3 < this.field8305.length && v3 < this.field8307.size(); ++v3) {
            String v6;
            int v5;
            ItemListSetting v4 = ch8.А((String)this.field8305[v3]);
            int n = v5 = v4 != null ? this.method2231(v4) : -1;
            if (v5 == -1) {
                v6 = field8316;
            } else if (param1 && v5 >= 9) {
                v6 = field8317;
            } else if (ServerHelperHolyworld.I.player.getItemCooldownManager().isCoolingDown(this.field8306[v3].getDefaultStack())) {
                float v7 = ServerHelperHolyworld.I.player.getItemCooldownManager().getCooldownProgress(this.field8306[v3].getDefaultStack(), I.getRenderTickCounter().getLastDuration());
                v6 = String.format(field8318, Float.valueOf(v7 * field8319));
            } else {
                v6 = field8320;
            }
            int v7 = (Integer)((BindSetting)this.field8307.get(v3)).getValue();
            String v8 = v7 == -1 ? "" : KeyCode.getDisplayName(v7);
            String v9 = this.field8305[v3].length() > 8 ? this.field8305[v3].substring(0, 8) : this.field8305[v3];
            v2.add(new CfrRenamed298(this.field8305[v3], v9, v6, this.field8306[v3], v8, -1));
        }
        return v2;
    }

    public void method2228() {
        this.field8289 = false;
        this.field8290 = null;
        this.field8297 = null;
    }

    public void method2229(boolean param1) {
        this.field8292 = param1;
    }

    private int I(Item param1) {
        if (I == null || ServerHelperHolyworld.I.player == null || param1 == null) {
            return -1;
        }
        for (int v2 = 0; v2 < 36; ++v2) {
            ItemStack v3 = ServerHelperHolyworld.I.player.getInventory().getStack(v2);
            if (v3.isEmpty() || v3.getItem() != param1) continue;
            return v2;
        }
        return -1;
    }

    public BooleanSetting method2230() {
        return this.field8308;
    }

    @Override
    public void method1890() {
        this.method2228();
    }

    private int method2231(ItemListSetting param1) {
        if (I == null || ServerHelperHolyworld.I.player == null || param1 == null) {
            return -1;
        }
        Item v2 = param1.method2243().getItem();
        List v3 = param1.method2256();
        String v4 = param1.getName();
        for (int v5 = 0; v5 < 36; ++v5) {
            boolean v11;
            ItemStack v6 = ServerHelperHolyworld.I.player.getInventory().getStack(v5);
            if (v6.isEmpty()) continue;
            String v7 = v6.getName().getString();
            String v8 = v7.replaceAll(field8321, "").replaceAll(field8322, "").replaceAll(field8323, field8324).trim().toLowerCase();
            String v9 = v4.replaceAll(field8325, "").replaceAll(field8326, field8327).trim().toLowerCase();
            Item v10 = v6.getItem();
            boolean bl = v11 = v8.contains(v9) || v9.contains(v8) || v8.equals(v9);
            if (v11 && (v3.isEmpty() || this.method2233(v6, v3))) {
                return v5;
            }
            if (v10 != v2) continue;
            if (v3.isEmpty()) {
                return v5;
            }
            if (!this.method2233(v6, v3)) continue;
            return v5;
        }
        return -1;
    }

    public int method2232() {
        if (this.field8290 == null) {
            return -1;
        }
        ItemListSetting v1 = ch8.А((String)this.field8290);
        if (v1 != null) {
            int v2 = this.method2231(v1);
            if (v2 != -1) {
                return v2;
            }
            v2 = this.method2227(this.field8290);
            if (v2 != -1) {
                return v2;
            }
        }
        if (this.field8297 != null) {
            return this.I(this.field8297);
        }
        return -1;
    }

    private boolean method2233(ItemStack param1, List<String> param2) {
        if (param2.isEmpty()) {
            return true;
        }
        LoreComponent v3 = (LoreComponent)param1.get(DataComponentTypes.LORE);
        if (v3 == null) {
            return false;
        }
        StringBuilder v4 = new StringBuilder();
        for (Text v6 : v3.lines()) {
            v4.append(v6.getString().toLowerCase()).append(field8328);
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
        for (int v2 = 0; v2 < this.field8305.length && v2 < this.field8307.size(); ++v2) {
            if ((Integer)((BindSetting)this.field8307.get(v2)).getValue() == -1 || (Integer)((BindSetting)this.field8307.get(v2)).getValue() != param1) continue;
            this.method2226(this.field8305[v2], this.field8306[v2]);
            return true;
        }
        return false;
    }

    static {
        field8291 = "HolyWorld";
        field8316 = "—";
        field8317 = "—";
        field8318 = "%.0fс";
        field8319 = 20.0f;
        field8320 = "✓";
        cfr_renamed_169 = "§c\u0001 не найден в конфигурации";
        cfr_renamed_170 = "§c\u0001 не найден в инвентаре";
        cfr_renamed_171 = "§c\u0001 не в хотбаре";
        field8293 = 20.0f;
        field8294 = 20.0f;
        field8295 = 20.0f;
        field8296 = "%.1f";
        cfr_renamed_172 = "§c\u0001 заблокирован еще \u0001 сек";
        field8309 = "[«»\"'`]";
        field8310 = "\\s+";
        field8311 = " ";
        field8312 = "§[0-9a-fk-or]";
        field8313 = "[«»\"'`]";
        field8314 = "\\s+";
        field8315 = " ";
        field8321 = "§[0-9a-fk-or]";
        field8322 = "[«»\"'`]";
        field8323 = "\\s+";
        field8324 = " ";
        field8325 = "[«»\"'`]";
        field8326 = "\\s+";
        field8327 = " ";
        field8328 = " ";
    }
}




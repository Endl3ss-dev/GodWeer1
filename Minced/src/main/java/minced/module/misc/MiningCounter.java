/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.registry.Registries
 *  net.minecraft.util.Identifier
 */
package minced.module.misc;
import minced.core.EventHandler;

import java.util.ArrayList;
import java.util.List;
import minced.core.ColorUtil;
import minced.core.SliderSetting;
import minced.core.MiningStats;
import minced.core.EventRender2D;
import minced.core.AnimatedFloat;
import minced.core.Fonts;
import minced.core.RenderUtil;
import minced.core.Module;
import minced.enums.Combat;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class MiningCounter
extends Module {
    static MinecraftClient I = MinecraftClient.getInstance();
    static List field5639;
    AnimatedFloat field5640;
    static float field5641;
    SliderSetting field5642 = new SliderSetting(field5650, 0.0f, field5651, field5652, 1.0f);
    SliderSetting field5643 = new SliderSetting(field5653, 0.0f, field5654, field5655, 1.0f);
    static float field5644;
    static float field5645;
    static int field5646;
    static float field5647;
    static String field5648;
    static String field5649;
    static String field5650;
    static float field5651;
    static float field5652;
    static String field5653;
    static float field5654;
    static float field5655;
    static float field5656;

    private int method1347(Item param1) {
        if (MiningCounter.I.player == null) {
            return 0;
        }
        int v2 = 0;
        for (int v3 = 0; v3 < MiningCounter.I.player.getInventory().size(); ++v3) {
            ItemStack v4 = MiningCounter.I.player.getInventory().getStack(v3);
            if (v4.isEmpty() || v4.getItem() != param1) continue;
            v2 += v4.getCount();
        }
        return v2;
    }

    public static void method1349() {
        field5639 = new ArrayList();
    }

    public static boolean method1350(String param0) {
        return field5639.removeIf(param1 -> ((MiningStats)param1).method2307().equalsIgnoreCase(param0));
    }

    public static List method1351() {
        return field5639;
    }

    public static void method1352(String param0) {
        Identifier v1 = Identifier.tryParse((String)param0);
        if (v1 == null) {
            return;
        }
        Block v2 = (Block)Registries.BLOCK.get(v1);
        if (v2 == null) {
            return;
        }
        Item v3 = v2.asItem();
        if (v3 == null) {
            return;
        }
        String v4 = Registries.ITEM.getId((Item)v3).toString();
        for (MiningStats v6 : (Iterable<MiningStats>)(field5639)) {
            if (!v6.method2307().equals(v4)) continue;
            return;
        }
        field5639.add(new MiningStats(v4, v3));
    }

    @EventHandler(2)
    public void method1353(EventRender2D param1) {
        if (MiningCounter.I.player == null) {
            return;
        }
        this.field5640.setTarget(field5639.isEmpty() ? 0.0f : 1.0f);
        float v2 = this.field5640.getValue();
        if (v2 <= field5641) {
            return;
        }
        float v3 = (float)(I.getWindow().getScaledWidth() - 55) + ((Float)this.field5642.getValue()).floatValue();
        float v4 = (float)(I.getWindow().getScaledHeight() - 80) + ((Float)this.field5643.getValue()).floatValue();
        this.method1354(param1, v3, v4, v2);
    }

    private void method1354(EventRender2D param1, float param2, float param3, float param4) {
        for (int v5 = 0; v5 < field5639.size(); ++v5) {
            MiningStats v6 = (MiningStats)field5639.get(v5);
            Item v7 = v6.method2308();
            if (v7 == null) continue;
            int v8 = this.method1347(v7);
            String v9 = String.valueOf(v8);
            float v10 = param2 - field5644;
            float v11 = param3 - (float)((field5639.size() - v5) * 20);
            RenderUtil.method398(param1.method2997(), v7.getDefaultStack(), (int)v10, (int)v11, 1.0f, -1, param4);
            Fonts.I[12].method2708(param1.method2997(), v9, v10 + field5645 - Fonts.I[12].method2719(v9) / 2.0f, v11 - 2.0f, ColorUtil.method1853(-1, param4), true, true, true, true, ColorUtil.method1853(field5646, param4 * field5647));
        }
    }

    public MiningCounter() {
        super(field5648, field5649, Combat.Miscellaneous);
        this.field5640 = new AnimatedFloat(0.0f, field5656);
        this.addSettings(this.field5642, this.field5643);
    }

    public static void method1355() {
        field5639.clear();
    }

    public static void method1356(String param0) {
        Identifier v1 = Identifier.tryParse((String)param0);
        if (v1 == null) {
            return;
        }
        Item v2 = (Item)Registries.ITEM.get(v1);
        if (v2 == null) {
            return;
        }
        for (MiningStats v4 : (Iterable<MiningStats>)(field5639)) {
            if (!v4.method2307().equals(param0)) continue;
            return;
        }
        field5639.add(new MiningStats(param0, v2));
    }

    static {
        field5648 = "MiningCounter";
        field5649 = "Показывает количество отслеживаемых предметов";
        field5650 = "Смещение X";
        field5651 = -200.0f;
        field5652 = 200.0f;
        field5653 = "Смещение Y";
        field5654 = -200.0f;
        field5655 = 200.0f;
        field5656 = 10.0f;
        field5641 = 0.01f;
        field5644 = 8.0f;
        field5645 = 8.0f;
        field5646 = -16777216;
        field5647 = 0.8f;
        MiningCounter.method1349();
    }
}




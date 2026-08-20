/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.screen.ingame.GenericContainerScreen
 *  net.minecraft.item.ItemStack
 *  net.minecraft.screen.slot.Slot
 *  net.minecraft.util.collection.DefaultedList
 */
package minced.module.misc;
import minced.core.EventHandler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import minced.core.ItemTooltipInfo;
import minced.core.ColorSetting;
import minced.core.SliderSetting;
import minced.core.EventHandledScreenRenderPre;
import minced.core.Module;
import minced.core.RecordClass233;
import minced.enums.Combat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;

public class AucHelper
extends Module {
    static String field5621 = "Auc Helper";
    static String field5622 = "Отображает самые дешевые/средние по цене предметы";
    static String field5623 = "Цвет выгодного предмета";
    static int field5624 = -16711936;
    ColorSetting field5625 = new ColorSetting(field5623, true, field5624);
    static String field5626 = "Количество выгодных предметов";
    static float field5627 = 3.0f;
    static float field5628 = 10.0f;
    SliderSetting field5629 = new SliderSetting(field5626, field5627, 1.0f, field5628, 1.0f);
    ItemTooltipInfo field5630 = new ItemTooltipInfo();
    static MinecraftClient I = MinecraftClient.getInstance();
    static double field5631;
    static int field5632;
    static long field5633;
    static double field5634;
    static double field5635;
    static double field5636;

    private int method1340(int param1) {
        return param1 & 0xFF;
    }

    public AucHelper() {
        super(field5621, field5622, Combat.Miscellaneous);
        this.addSettings(this.field5629);
    }

    private int O(int param1, int param2, int param3, int param4) {
        return param1 << 24 | param2 << 16 | param3 << 8 | param4;
    }

    private int method1341(int param1) {
        return param1 >> 8 & 0xFF;
    }

    @EventHandler(2)
    public void method1342(EventHandledScreenRenderPre param1) {
        int v82;
        int v9;
        if (!(AucHelper.I.currentScreen instanceof GenericContainerScreen)) {
            return;
        }
        DefaultedList v2 = param1.method2606().slots;
        Slot v3 = this.method1344((List)v2);
        Slot v4 = this.method1343((List)v2);
        ArrayList<RecordClass233> v5 = new ArrayList<RecordClass233>();
        for (Slot v7 : (Iterable<Slot>)(v2)) {
            ItemStack v82_79;
            if (v7 == null || !v7.hasStack() || (v9 = this.field5630.method1859(v82_79 = v7.getStack())) < 0) continue;
            v5.add(new RecordClass233(v7, v9, v82_79.getCount()));
        }
        if (v5.isEmpty()) {
            return;
        }
        v5.sort(Comparator.comparingDouble(param0 -> (double)param0.price / (double)param0.count));
        HashSet v6 = new HashSet();
        if (v3 != null) {
            v6.add(v3);
        }
        if (v4 != null) {
            v6.add(v4);
        }
        int v7 = (int)Math.min((float)v5.size(), ((Float)this.field5629.getValue()).floatValue());
        for (v82 = 0; v82 < v7; ++v82) {
            v6.add(((RecordClass233)v5.get((int)v82)).slot);
        }
        v82 = (Integer)this.field5625.getValue();
        v9 = this.method1345(v82);
        Iterator v10 = v6.iterator();
        while (v10.hasNext()) {
            Slot v11 = (Slot)v10.next();
            int v12 = param1.method2607() + v11.x;
            int v13 = param1.method2608() + v11.y;
            param1.method2609().fill(v12, v13, v12 + 16, v13 + 16, v9);
        }
    }

    private Slot method1343(List param1) {
        Slot v2 = null;
        double v3 = field5631;
        for (Slot v6 : (Iterable<Slot>)(param1)) {
            double v9;
            ItemStack v7;
            int v8;
            if (v6 == null || !v6.hasStack() || (v8 = this.field5630.method1859(v7 = v6.getStack())) < 0 || v7.getCount() <= 1 || !((v9 = (double)v8 / (double)v7.getCount()) < v3)) continue;
            v3 = v9;
            v2 = v6;
        }
        return v2;
    }

    private Slot method1344(List param1) {
        Slot v2 = null;
        int v3 = field5632;
        for (Slot v5 : (Iterable<Slot>)(param1)) {
            int v6;
            if (v5 == null || !v5.hasStack() || (v6 = this.field5630.method1859(v5.getStack())) < 0 || v6 >= v3) continue;
            v3 = v6;
            v2 = v5;
        }
        return v2;
    }

    private int l(int param1) {
        return param1 >> 16 & 0xFF;
    }

    private int method1345(int param1) {
        long v2 = System.currentTimeMillis() / field5633;
        int v4 = (int)(Math.abs(Math.sin((double)v2 * field5634 / field5635)) * field5636);
        return this.O(v4, this.l(param1), this.method1341(param1), this.method1340(param1));
    }

    static {
        field5632 = Integer.MAX_VALUE;
        field5631 = Double.MAX_VALUE;
        field5633 = 10L;
        field5634 = Math.PI;
        field5635 = 180.0;
        field5636 = 170.0;
    }
}




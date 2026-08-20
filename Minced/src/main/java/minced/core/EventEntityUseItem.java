/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.Hand
 *  sg.ec.Фв$Ч
 */
package minced.core;

import java.util.HashMap;
import minced.core.CancellableEvent;
import minced.core.EventBase;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class EventEntityUseItem
extends EventBase
implements CancellableEvent {
    private static class ch {
        long л;
        ItemStack ф;

        ch(long param1, ItemStack param2) {
            this.л = param1;
            this.ф = param2;
        }
    }

    private static HashMap А;
    private static HashMap Я;
    private LivingEntity д;
    private ItemStack Е;
    private boolean cfr_renamed_189;

    public EventEntityUseItem(LivingEntity param1, ItemStack param2, boolean param3) {
        this.д = param1;
        this.Е = param2;
        this.cfr_renamed_189 = param3;
    }

    public static void method2350(int param0, ItemStack param1, long param2) {
        А.put(param0, new ch(param2, param1));
    }

    public LivingEntity method2351() {
        return this.д;
    }

    public static void method2352(int param0, ItemStack param1, long param2) {
        Я.put(param0, new ch(param2, param1));
    }

    public boolean method2353() {
        return this.cfr_renamed_189;
    }

    public ItemStack method2354() {
        return this.Е;
    }

    public static boolean method2355(int param0, Hand param1, long param2, int param4) {
        ch v5;
        ch ч = v5 = param1 == Hand.MAIN_HAND ? (ch)А.get(param0) : (ch)Я.get(param0);
        if (v5 == null) {
            return false;
        }
        return param2 - v5.л <= (long)param4;
    }

    public static void method2356() {
        А = new HashMap();
        Я = new HashMap();
    }

    static {
        EventEntityUseItem.method2356();
    }
}




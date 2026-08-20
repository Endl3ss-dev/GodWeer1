/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 *  net.minecraft.registry.RegistryKey
 */
package minced.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import minced.core.Setting;
import minced.core.CancellableEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;

public class ItemListSetting
extends Setting
implements CancellableEvent {
    public String cfr_renamed_173;
    public ItemStack д;
    public List<String> Я;
    public String cfr_renamed_174;
    public int но;
    public boolean ны;
    public long cfr_renamed_175;
    public Map<String, Integer> cfr_renamed_7;
    public boolean нъ;
    public static final String cfr_renamed_176 = "[^0-9]";
    public static final String cfr_renamed_177 = "[^0-9]";

    public String method2242() {
        return this.cfr_renamed_173;
    }

    public ItemStack method2243() {
        return this.д;
    }

    public ItemListSetting method2244(String ... param1) {
        if (param1 == null) {
            return this;
        }
        for (String v5 : param1) {
            String v6;
            if (v5 == null || (v6 = v5.trim()).isEmpty()) continue;
            this.Я.add(v6);
        }
        return this;
    }

    public void method2245(String param1) {
        this.cfr_renamed_174 = param1;
    }

    public void method2246(int param1) {
        this.но = param1;
    }

    public boolean method2247() {
        return this.ны;
    }

    public ItemListSetting method2248() {
        this.ны = true;
        return this;
    }

    public void method2249(long param1) {
        this.cfr_renamed_175 = param1;
    }

    public void method2250(String param1) {
        if (param1 == null) {
            this.cfr_renamed_175 = 0L;
            return;
        }
        String v2 = param1.replaceAll(cfr_renamed_176, "");
        if (v2.isEmpty()) {
            this.cfr_renamed_175 = 0L;
            return;
        }
        this.cfr_renamed_175 = Long.parseLong(v2);
    }

    public Map method2251() {
        return this.cfr_renamed_7;
    }

    public String method2252() {
        return this.cfr_renamed_174;
    }

    public void method2253(String param1) {
        if (param1 == null) {
            this.но = 0;
            return;
        }
        String v2 = param1.replaceAll(cfr_renamed_177, "");
        if (v2.isEmpty()) {
            this.но = 0;
            return;
        }
        this.но = Integer.parseInt(v2);
    }

    public ItemListSetting method2254(String param1) {
        this.cfr_renamed_173 = param1 == null ? null : param1.trim();
        return this;
    }

    public ItemListSetting(ItemStack param1, String param2, Boolean param3) {
        super(param2, param3);
        this.cfr_renamed_174 = "";
        this.cfr_renamed_175 = 0L;
        this.нъ = false;
        this.но = 0;
        this.Я = new ArrayList();
        this.cfr_renamed_7 = new HashMap();
        this.ны = false;
        this.cfr_renamed_173 = null;
        this.д = param1;
    }

    public boolean method2255() {
        return this.нъ;
    }

    public List method2256() {
        return this.Я;
    }

    public long method2257() {
        return this.cfr_renamed_175;
    }

    public int method2258() {
        return this.но;
    }

    public void method2259(boolean param1) {
        this.нъ = param1;
    }

    public void method2260(boolean param1) {
        this.ны = param1;
    }

    public ItemListSetting method2261(RegistryKey param1, int param2) {
        if (param1 == null) {
            return this;
        }
        this.cfr_renamed_7.put(param1.getValue().toString().toLowerCase(), Math.max(1, param2));
        return this;
    }

    public void method2262(String param1) {
        this.cfr_renamed_173 = param1;
    }
}




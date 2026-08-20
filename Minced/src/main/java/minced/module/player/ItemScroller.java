/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 */
package minced.module.player;
import minced.core.EventHandler;

import java.util.function.BooleanSupplier;
import minced.core.SliderSetting;
import minced.core.EventMouseDrag;
import minced.core.Module;
import minced.enums.Combat;

public class ItemScroller
extends Module {
    SliderSetting field2173 = new SliderSetting(field2175, field2176, 0.0f, field2177, 1.0f);
    static String _3 = "Item Scroller";
    static String field2174 = "Позволяет быстро перекладывать вещи в инвенторе";
    static String field2175 = "Задержка";
    static float field2176 = 8.0f;
    static float field2177 = 10.0f;

    @EventHandler(2)
    public void method595(EventMouseDrag param1) {
        param1.method2618(((Float)this.field2173.getValue()).intValue());
    }

    public ItemScroller() {
        super(_3, field2174, Combat.Player);
        this.addSettings(this.field2173);
    }
}




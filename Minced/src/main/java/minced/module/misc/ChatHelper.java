/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 */
package minced.module.misc;
import minced.core.BooleanSetting;
import minced.core.Module;
import minced.enums.Combat;

public class ChatHelper
extends Module {
    static String field2238 = "Chat Helper";
    static String field2239 = "Вспомогательный набор функций для чата";
    static String field2240 = "Анти Спам";
    public BooleanSetting field2241 = new BooleanSetting(field2240, true);
    public static BooleanSetting field2242;
    static String field2243;

    public ChatHelper() {
        super(field2238, field2239, Combat.Miscellaneous);
        this.addSettings(field2242, this.field2241);
    }

    public static void method606() {
        field2242 = new BooleanSetting(field2243, true);
    }

    static {
        field2243 = "История чата";
        ChatHelper.method606();
    }
}


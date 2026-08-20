/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 */
package minced.core;

import minced.core.BooleanSetting;
import minced.core.HudComponent;
import minced.core.EventRender2D;
import minced.core.HudWindow;

public class Notifications
implements HudComponent {
    HudWindow field9909;
    static String field9910;
    static BooleanSetting field9911;
    static String field9912;
    static BooleanSetting field9913;
    static String field9914;
    static BooleanSetting field9915;
    static String field9916;
    static BooleanSetting field9917;
    static String field9918;
    static BooleanSetting field9919;
    static String field9920;
    static BooleanSetting field9921;

    public Notifications(HudWindow param1) {
        this.field9909 = param1;
    }

    public static void method2868() {
        field9911 = new BooleanSetting(field9910, true);
        field9913 = new BooleanSetting(field9912, true);
        field9915 = new BooleanSetting(field9914, true);
        field9917 = new BooleanSetting(field9916, false);
        field9919 = new BooleanSetting(field9918, false);
        field9921 = new BooleanSetting(field9920, false);
    }

    @Override
    @EventHandler(2)
    public void method2818(EventRender2D param1) {
        this.field9909.method3483(0.0f);
        this.field9909.method3459(0.0f);
    }

    static {
        field9910 = "Состояние модулей/настроек";
        field9912 = "Низкая прочность предметов";
        field9914 = "Закончились важные зелья";
        field9916 = "Упоминание ника";
        field9918 = "Уведомление на ПК";
        field9920 = "Прозрачный фон";
        Notifications.method2868();
    }
}


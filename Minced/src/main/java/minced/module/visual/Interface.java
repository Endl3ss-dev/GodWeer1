/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.util.Window
 */
package minced.module.visual;
import minced.core.EventHandler;

import minced.core.MultiBooleanSetting;
import minced.core.BooleanSetting;
import minced.core.KeybindsHud;
import minced.core.ModuleListHud;
import minced.core.DelaysHud;
import minced.core.Watermark;
import minced.core.PotionsHud;
import minced.core.StaffOnlineHud;
import minced.core.TotemIndicator;
import minced.core.Notifications;
import minced.core.ArmorHud;
import minced.core.EventRender2D;
import minced.core.HudWindow;
import minced.core.Minced;
import minced.core.Module;
import minced.enums.Combat;
import minced.module.visual.TargetHud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;

public class Interface
extends Module {
    static String field2055;
    static String field2056;
    static String field2057;
    static String field2058;
    static String field2059;
    static String field2060;
    static String field2061;
    static String field2062;
    static String field2063;
    static String field2064;
    static String field2065;
    static String field2066;
    public static MultiBooleanSetting field2067;
    static String field2068;
    static String field2069;
    Notifications field2070;
    static MinecraftClient I = MinecraftClient.getInstance();
    static String field2071;
    static String field2072;
    static String field2073;
    static String field2074;
    Watermark field2075;
    static String field2076;
    static String field2077;
    ArmorHud field2078;
    static String field2079;
    static String field2080;
    TotemIndicator field2081;
    static String field2082;
    static String field2083;
    static String field2084;
    static String field2085;
    static String field2086;
    static String field2087;
    TargetHud field2088;
    KeybindsHud field2089;
    StaffOnlineHud field2090;
    DelaysHud field2091;
    PotionsHud field2092;
    ModuleListHud field2093;
    static String field2094;
    static String field2095;
    static float field2096;
    static float field2097;
    static float field2098;
    // Element names must match the toggle names in field2067 (see method590's dispatch table),
    // otherwise MultiBooleanSetting.method1898 returns null and HudWindow.method3497 NPEs.
    static String field2099 = "Уведомления";
    static String field2100 = "Информация о цели";
    static float field2101;
    static float field2102;
    static String field2103 = "Горячие клавиши";
    static float field2104;
    static float field2105;
    static String field2106 = "Администрация онлайн";
    static float field2107;
    static float field2108;
    static String field2109 = "Логотип";
    static float field2110;
    static float field2111;
    static String field2112 = "Задержки";
    static float field2113;
    static float field2114;
    static String field2115 = "Зелья";
    static float field2116;
    static float field2117;
    static String field2118 = "Список модулей";
    static float field2119;
    static float field2120;

    public static void method589() {
        field2067 = new MultiBooleanSetting(field2055, new BooleanSetting(field2056, true), new BooleanSetting(field2057, true), new BooleanSetting(field2058, true), new BooleanSetting(field2059, true), new BooleanSetting(field2060, true), new BooleanSetting(field2061, true), new BooleanSetting(field2062, true), new BooleanSetting(field2063, true), new BooleanSetting(field2064, true), new BooleanSetting(field2065, true), new BooleanSetting(field2066, true));
    }

    @EventHandler(2)
    private void method590(EventRender2D param1) {
        if (field2067.method1898(field2068) != null && field2067.method1898(field2069).booleanValue()) {
            this.field2070.method2818(param1);
        }
        if (I.getDebugHud().shouldShowDebugHud()) {
            return;
        }
        if (field2067.method1898(field2071) != null && field2067.method1898(field2072).booleanValue()) {
            this.field2070.method2818(param1);
        }
        if (field2067.method1898(field2073) != null && field2067.method1898(field2074).booleanValue()) {
            this.field2075.method2818(param1);
        }
        if (field2067.method1898(field2076) != null && field2067.method1898(field2077).booleanValue()) {
            this.field2078.method2818(param1);
        }
        if (field2067.method1898(field2079) != null && field2067.method1898(field2080).booleanValue()) {
            this.field2081.method2818(param1);
        }
        for (HudWindow v3 : (Iterable<HudWindow>)(Minced.getInstance().getDraggingManager().method1662())) {
            String v4 = v3.method3491();
            if (field2067.method1898(v4) == null || !field2067.method1898(v4).booleanValue()) continue;
            String v5 = v4;
            int v6 = -1;
            switch (v5.hashCode()) {
                case 1470504262: {
                    if (!v5.equals(field2082)) break;
                    v6 = 0;
                    break;
                }
                case 1431218319: {
                    if (!v5.equals(field2083)) break;
                    v6 = 1;
                    break;
                }
                case -582764377: {
                    if (!v5.equals(field2084)) break;
                    v6 = 2;
                    break;
                }
                case -949163026: {
                    if (!v5.equals(field2085)) break;
                    v6 = 3;
                    break;
                }
                case 1000087360: {
                    if (!v5.equals(field2086)) break;
                    v6 = 4;
                    break;
                }
                case -1751004271: {
                    if (!v5.equals(field2087)) break;
                    v6 = 5;
                }
            }
            switch (v6) {
                case 0: {
                    this.field2088.method2818(param1);
                    break;
                }
                case 1: {
                    this.field2089.method2818(param1);
                    break;
                }
                case 2: {
                    this.field2090.method2818(param1);
                    break;
                }
                case 3: {
                    this.field2091.method2818(param1);
                    break;
                }
                case 4: {
                    this.field2092.method2818(param1);
                    break;
                }
                case 5: {
                    this.field2093.method2818(param1);
                }
            }
        }
    }

    public Interface() {
        super(field2094, field2095, Combat.Visuals);
        Window v1 = I != null ? I.getWindow() : null;
        float v2 = v1 != null ? (float)v1.getScaledWidth() / 2.0f : field2096;
        float v3 = v1 != null ? (float)v1.getScaledHeight() / 2.0f + field2097 : field2098;
        HudWindow v4 = new HudWindow(field2099, v2, v3, field2067, false);
        HudWindow v5 = new HudWindow(field2100, field2101, field2102, field2067, true);
        HudWindow v6 = new HudWindow(field2103, field2104, field2105, field2067, true);
        HudWindow v7 = new HudWindow(field2106, field2107, field2108, field2067, true);
        HudWindow v8 = new HudWindow(field2109, field2110, field2111, field2067, true);
        HudWindow v9 = new HudWindow(field2112, field2113, field2114, field2067, true);
        HudWindow v10 = new HudWindow(field2115, field2116, field2117, field2067, true);
        HudWindow v11 = new HudWindow(field2118, field2119, field2120, field2067, true);
        this.field2075 = new Watermark(v8);
        this.field2070 = new Notifications(v4);
        this.field2089 = new KeybindsHud(v6);
        this.field2090 = new StaffOnlineHud(v7);
        this.field2088 = new TargetHud(v5);
        this.field2092 = new PotionsHud(v10);
        this.field2091 = new DelaysHud(v9);
        this.field2081 = new TotemIndicator();
        this.field2078 = new ArmorHud();
        this.field2093 = new ModuleListHud(v11);
        this.addSettings(field2067);
    }

    static {
        field2094 = "Interface";
        field2095 = "Отображает интерфейс с информацией о игре и персонаже";
        field2096 = 100.0f;
        field2097 = 21.0f;
        field2098 = 121.0f;
        field2101 = 4.0f;
        field2102 = 100.0f;
        field2104 = 100.0f;
        field2105 = 100.0f;
        field2107 = 100.0f;
        field2108 = 10.0f;
        field2110 = 5.0f;
        field2111 = 5.0f;
        field2113 = 217.0f;
        field2114 = 85.0f;
        field2116 = 100.0f;
        field2117 = 100.0f;
        field2119 = 10.0f;
        field2120 = 40.0f;
        field2068 = "Уведомления";
        field2069 = "Уведомления";
        field2071 = "Уведомления";
        field2072 = "Уведомления";
        field2073 = "Логотип";
        field2074 = "Логотип";
        field2076 = "Броня";
        field2077 = "Броня";
        field2079 = "Индикатор тотемов";
        field2080 = "Индикатор тотемов";
        field2082 = "Информация о цели";
        field2083 = "Горячие клавиши";
        field2084 = "Администрация онлайн";
        field2085 = "Задержки";
        field2086 = "Зелья";
        field2087 = "Список модулей";
        field2055 = "Окна";
        field2056 = "Броня";
        field2057 = "Информация о цели";
        field2058 = "Логотип";
        field2059 = "Администрация онлайн";
        field2060 = "Уведомления";
        field2061 = "Задержки";
        field2062 = "Зелья";
        field2063 = "Индикатор тотемов";
        field2064 = "Горячие клавиши";
        field2065 = "Список модулей";
        field2066 = "Кастомный хотбар";
        Interface.method589();
    }
}




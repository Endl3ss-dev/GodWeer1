/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.screen.Screen
 */
package minced.module.visual;
import minced.core.EventHandler;

import minced.core.BindSetting;
import minced.core.BooleanSetting;
import minced.core.EventKey;
import minced.core.Module;
import minced.enums.Combat;
import minced.gui.ScreenClass621;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

public class Emotions
extends Module {
    String field6613 = null;
    BooleanSetting field6614;
    boolean field6615 = true;
    BooleanSetting field6616;
    String field6617;
    BindSetting field6618 = new BindSetting(field6623);
    public static MinecraftClient I = MinecraftClient.getInstance();
    static Emotions field6619;
    BooleanSetting field6620;
    static String field6621;
    static String field6622;
    static String field6623;
    static String field6624;
    static String field6625;
    static String field6626;
    static String field6627;

    public void method1570(String param1) {
        this.field6613 = param1;
    }

    public BooleanSetting method1571() {
        return this.field6614;
    }

    public boolean method1572() {
        return this.field6615;
    }

    public BooleanSetting method1573() {
        return this.field6616;
    }

    public String method1574() {
        return this.field6617;
    }

    public BindSetting method1575() {
        return this.field6618;
    }

    public void method1576(String param1) {
        this.field6617 = param1;
    }

    @EventHandler(2)
    public void method1577(EventKey param1) {
        if (!this.isEnabled() || (Integer)this.field6618.getValue() == -1) {
            return;
        }
        if (Emotions.I.currentScreen != null && !(Emotions.I.currentScreen instanceof ScreenClass621)) {
            return;
        }
        if (param1.method3595() == ((Integer)this.field6618.getValue()).intValue()) {
            if (param1.method3599()) {
                if (!(Emotions.I.currentScreen instanceof ScreenClass621)) {
                    I.setScreen((Screen)new ScreenClass621());
                }
            } else {
                Screen v32 = Emotions.I.currentScreen;
                if (v32 instanceof ScreenClass621) {
                    String[] v4;
                    ScreenClass621 v2 = (ScreenClass621)v32;
                    int v33 = v2.getHoveredSlot();
                    if (v33 == -2) {
                        this.field6615 = true;
                        this.field6617 = null;
                    } else if (v33 >= 0 && v33 < (v4 = ScreenClass621.I).length) {
                        this.field6617 = v4[v33];
                        this.field6615 = false;
                    }
                    I.setScreen(null);
                }
            }
        }
    }

    public static Emotions method1578() {
        return field6619;
    }

    public BooleanSetting method1579() {
        return this.field6620;
    }

    public void method1580(boolean param1) {
        this.field6615 = param1;
    }

    public Emotions() {
        super(field6621, field6622, Combat.Visuals);
        this.field6614 = new BooleanSetting(field6624, false);
        this.field6616 = new BooleanSetting(field6625, true);
        this.field6620 = new BooleanSetting(field6626, false);
        this.field6617 = null;
        field6619 = this;
        this.addSettings(this.field6618, this.field6620, this.field6614, this.field6616);
    }

    public String method1581() {
        return this.field6613;
    }

    static {
        field6621 = "Emotions";
        field6622 = "Колесо эмоций и позы игрока";
        field6623 = "Кнопка колеса";
        field6624 = "На друзей";
        field6625 = "На себя";
        field6626 = "На всех";
        field6627 = "Приветствие";
    }
}




/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.screen.Screen
 *  sg.ec.2Ь
 *  sg.ec.yu
 */
package minced.module.misc;
import minced.core.EventHandler;

import java.util.function.Function;
import minced.core.EventChatMessage;
import minced.core.MultiBooleanSetting;
import minced.core.BooleanSetting;
import minced.core.SliderSetting;
import minced.core.EventUpdate;
import minced.core.EventKey;
import minced.core.Module;
import minced.enums.Combat;
import net.minecraft.client.gui.screen.Screen;
import sg.ec._2hs;
import sg.ec.yu;

public class ModuleClass114
extends Module
{
    public minced.core.SliderSetting cfr_renamed;
    public minced.core.SliderSetting cfr_underscore;
    public static net.minecraft.client.MinecraftClient I;
    public minced.core.BooleanSetting Ц;
    public sg.ec._2hs sh;
    public minced.core.SliderSetting Ъ;
    public minced.core.MultiBooleanSetting м;
    public minced.core.BooleanSetting ч;
    public static java.lang.String эl;
    public static java.lang.String эБ;
    public static java.lang.String эЙ;
    public static java.lang.String эС;
    public static java.lang.String эа;
    public static java.lang.String эи;
    public static java.lang.String эй;
    public static java.lang.String эл;
    public static java.lang.String эп;
    public static java.lang.String эс;
    public static java.lang.String эу;
    public static java.lang.String эх;
    public static java.lang.String эъ;
    public static java.lang.String эя;
    public static java.lang.String э弟;
    public static java.lang.String э必;
    public minced.core.BooleanSetting 诶;
    static float эО;
    static float эД;
    static float эП;
    static float эO;
    static float эы;
    static float эЮ;
    static float эш;
    static float эА;

    @EventHandler(2)
    public void method929(EventChatMessage param1) {
        this.sh.Л(param1.method76());
    }

    public _2hs method930() {
        return this.sh;
    }

    @EventHandler(2)
    public void method932(EventUpdate param1) {
        this.sh.sh((int)((Float)this.Ъ.getValue()).floatValue(), ((Boolean)this.诶.getValue()).booleanValue(), ((Boolean)this.Ц.getValue()).booleanValue(), (int)((Float)this.cfr_renamed.getValue()).floatValue(), ((Boolean)this.ч.getValue()).booleanValue(), (int)((Float)this.cfr_renamed.getValue()).floatValue(), (int)((Float)this.cfr_underscore.getValue()).floatValue(), ((Boolean)this.м.method1899(0).getValue()).booleanValue(), ((Boolean)this.м.method1899(1).getValue()).booleanValue(), ((Boolean)this.м.method1899(2).getValue()).booleanValue(), ((Boolean)this.м.method1899(3).getValue()).booleanValue(), ((Boolean)this.м.method1899(4).getValue()).booleanValue(), ((Boolean)this.м.method1899(5).getValue()).booleanValue());
    }

    public SliderSetting method933() {
        return this.cfr_underscore;
    }

    public BooleanSetting method934() {
        return this.Ц;
    }

    public SliderSetting method935() {
        return this.Ъ;
    }

    public MultiBooleanSetting method936() {
        return this.м;
    }

    public BooleanSetting method937() {
        return this.ч;
    }

    public BooleanSetting method938() {
        return this.诶;
    }

    public SliderSetting O() {
        return this.cfr_renamed;
    }

    @Override
    public void onDisable() {
        this.sh.Шт();
        if (ModuleClass114.I.currentScreen instanceof yu) {
            I.setScreen(null);
        }
        super.onDisable();
    }

    public SliderSetting method939() {
        return this.cfr_renamed;
    }

    public ModuleClass114() {
        super(эя, эс, Combat.Miscellaneous);
        this.sh = new _2hs();
        this.Ъ = new SliderSetting(эп, эО, 1.0f, эД, 1.0f);
        this.诶 = new BooleanSetting(эа, true);
        this.Ц = new BooleanSetting(эС, false);
        this.cfr_renamed = new SliderSetting(эй, эП, 1.0f, эO, 1.0f);
        this.ч = new BooleanSetting(эл, false);
        this.м = new MultiBooleanSetting(эЙ, () -> (Boolean)this.ч.getValue(), new BooleanSetting(эи, true), new BooleanSetting(э弟, true), new BooleanSetting(эу, true), new BooleanSetting(э必, true), new BooleanSetting(эБ, true), new BooleanSetting(эх, true));
        this.cfr_renamed = new SliderSetting(эъ, эы, 1.0f, эЮ, 1.0f, () -> (Boolean)this.ч.getValue());
        this.cfr_underscore = new SliderSetting(эl, эш, 1.0f, эА, 1.0f, () -> (Boolean)this.ч.getValue());
        this.addSettings(this.Ъ, this.诶, this.Ц, this.cfr_renamed, this.ч, this.м, this.cfr_renamed, this.cfr_underscore);
    }

    @EventHandler(2)
    public void method940(EventKey param1) {
        if (param1.method3594()) {
            return;
        }
        if (!param1.method3599()) {
            return;
        }
        if (param1.method3595() == 96) {
            if (ModuleClass114.I.currentScreen instanceof yu) {
                I.setScreen(null);
            } else {
                I.setScreen((Screen)new yu(this.sh, this));
            }
        }
    }

    static {
        эО = 0.0f;
        эД = 0.0f;
        эП = 0.0f;
        эO = 0.0f;
        эы = 0.0f;
        эЮ = 0.0f;
        эш = 0.0f;
        эА = 0.0f;
    }
}




/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  sLM.4ZXYEo
 */
package minced.core;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import minced.core.SoundPlayer;
import minced.core.Setting;
import minced.core.NotificationManager;
import minced.core.EventBus;
import minced.core.AnimatedFloat;
import minced.enums.Combat;
import minced.enums.TOGGLE;
import minced.module.misc.ToggleSounds;
import sLM._4ZXYEo;

public abstract class Module
{
    static String cfr_renamed_295;
    static String cfr_renamed_296;

    List settings;
    String description;
    TOGGLE toggleMode = TOGGLE.TOGGLE;
    static float toggleAnimationSpeed;
    AnimatedFloat toggleAnimation;
    String name;
    boolean hidden;
    Combat category;
    int key;
    static boolean stateChangeSuppressed;
    static String defaultDescription;
    boolean enabled;
    float field1077;
    float field1078;

    public void addSettings(Setting ... param1) {
        this.settings.addAll(List.of(param1));
    }

    public void setDescription(String param1) {
        this.description = param1;
    }

    public Module(String param1, String param2, boolean param3, Combat param4) {
        this.settings = new ObjectArrayList();
        this.toggleAnimation = new AnimatedFloat(0.0f, toggleAnimationSpeed);
        this.name = param1;
        this.description = param2;
        this.hidden = param3;
        this.category = param4;
        this.key = -100;
    }

    public AnimatedFloat getToggleAnimation() {
        return this.toggleAnimation;
    }

    public void setCategory(Combat param1) {
        this.category = param1;
    }

    public Combat getCategory() {
        return this.category;
    }

    public static boolean isStateChangeSuppressed() {
        return stateChangeSuppressed;
    }

    public void setHidden(boolean param1) {
        this.hidden = param1;
    }

    public void onDisable() {
        this.toggleAnimation.setTarget(0.0f);
        if (!stateChangeSuppressed) {
            SoundPlayer.method2069(ToggleSounds.method1052(false));
            NotificationManager.method2456(this.category.method169(), (String)((Object)_4ZXYEo.弟о("makeConcatWithConstants", new Object[]{}, (String)this.name)), -1);
        }
        EventBus.method3009(this);
    }

    public void setToggleMode(TOGGLE param1) {
        this.toggleMode = param1;
    }

    public String getDescription() {
        return this.description;
    }

    public Module(String param1, Combat param2) {
        this(param1, defaultDescription, false, param2);
    }

    public Module(String param1, String param2, Combat param3) {
        this(param1, param2, false, param3);
    }

    public List<Setting> getSettings() {
        return this.settings;
    }

    public void onEnable() {
        this.toggleAnimation.setTarget(1.0f);
        if (!stateChangeSuppressed) {
            SoundPlayer.method2069(ToggleSounds.method1052(true));
            NotificationManager.method2456(this.category.method169(), (String)((Object)_4ZXYEo.弟э("makeConcatWithConstants", new Object[]{}, (String)this.name)), -1);
        }
        EventBus.method3013(this);
    }

    public static void setStateChangeSuppressed(boolean param0) {
        stateChangeSuppressed = param0;
    }

    public static void resetStateChangeSuppression() {
        stateChangeSuppressed = false;
    }

    public boolean isHidden() {
        return this.hidden;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public float method338() {
        return this.field1077;
    }

    public void method339(float param1) {
        this.field1077 = param1;
    }

    public final void cfr_renamed_4(boolean param1) {
        if (this.enabled == param1) {
            return;
        }
        this.enabled = param1;
        if (this.enabled) {
            this.onEnable();
        } else {
            this.onDisable();
        }
    }

    public void method340(List param1) {
        this.settings = param1;
    }

    public void setKey(int param1) {
        this.key = param1;
    }

    public final void toggle() {
        this.cfr_renamed_4(!this.enabled);
    }

    public TOGGLE getToggleMode() {
        return this.toggleMode;
    }

    public int getKey() {
        return this.key;
    }

    public void method344(float param1) {
        this.field1078 = param1;
    }

    public void setName(String param1) {
        this.name = param1;
    }

    public void setEnabledSilently(boolean param1) {
        this.enabled = param1;
    }

    public String getName() {
        return this.name;
    }

    public float method348() {
        return this.field1078;
    }

    static {
        stateChangeSuppressed = false;
        defaultDescription = "NULL";
        toggleAnimationSpeed = 8.0f;
        cfr_renamed_295 = "\u0001 включен";
        cfr_renamed_296 = "\u0001 выключен";
        Module.resetStateChangeSuppression();
    }
}


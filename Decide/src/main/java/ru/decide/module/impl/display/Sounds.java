/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_310
 */
package ru.decide.module.impl.display;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.module.api.ModuleInfo;
import ru.decide.module.api.settings.impl.BooleanSetting;
import ru.decide.module.api.settings.impl.ModeSetting;
import ru.decide.module.api.settings.impl.SliderSetting;
import ru.decide.utils.other.Instance;
import ru.decide.utils.other.SoundUtil;

@ModuleInfo(name="Sounds", desc="\u0417\u0432\u0443\u043a\u0438 \u0432\u043a\u043b\u044e\u0447\u0435\u043d\u0438\u044f/\u0432\u044b\u043a\u043b\u044e\u0447\u0435\u043d\u0438\u044f \u043c\u043e\u0434\u0443\u043b\u0435\u0439", category=Category.HUD)
@Environment(value=EnvType.CLIENT)
public final class Sounds
extends Module {
    private static final String S1 = "\u0417\u0432\u0443\u043a 1";
    private static final String S2 = "\u0417\u0432\u0443\u043a 2";
    private static final String S3 = "\u0417\u0432\u0443\u043a 3";
    private static final String S4 = "\u0417\u0432\u0443\u043a 4";
    private static final String S5 = "\u0417\u0432\u0443\u043a 5";
    private static final String S6 = "\u0417\u0432\u0443\u043a 6";
    private static final String S7 = "\u0417\u0432\u0443\u043a 7";
    private static final String S8 = "\u0417\u0432\u0443\u043a 8";
    private static final String S9 = "\u0417\u0432\u0443\u043a 9";
    private static final String S10 = "\u0417\u0432\u0443\u043a 10";
    public final BooleanSetting toggleSounds = new BooleanSetting((Module)this, "\u0417\u0432\u0443\u043a\u0438 toggle", true);
    public final ModeSetting soundPack = new ModeSetting((Module)this, "\u041f\u0430\u043a \u0437\u0432\u0443\u043a\u043e\u0432", "\u0417\u0432\u0443\u043a 1", "\u0417\u0432\u0443\u043a 2", "\u0417\u0432\u0443\u043a 3", "\u0417\u0432\u0443\u043a 4", "\u0417\u0432\u0443\u043a 5", "\u0417\u0432\u0443\u043a 6", "\u0417\u0432\u0443\u043a 7", "\u0417\u0432\u0443\u043a 8", "\u0417\u0432\u0443\u043a 9", "\u0417\u0432\u0443\u043a 10");
    public final SliderSetting volume = new SliderSetting(this, "\u0413\u0440\u043e\u043c\u043a\u043e\u0441\u0442\u044c", 0.5f, 0.1f, 1.0f, 0.1f);

    public static Sounds get() {
        return Instance.get(Sounds.class);
    }

    @Override
    protected void onEnable() {
        this.playSelfSound(true);
    }

    @Override
    protected void onDisable() {
        this.playSelfSound(false);
    }

    private void playSelfSound(boolean enable) {
        class_310 mc = class_310.method_1551();
        if (mc.field_1724 == null) {
            return;
        }
        this.playSound(enable);
    }

    public boolean isToggleSoundsEnabled() {
        return this.isEnabled() && this.toggleSounds.getValue() != false;
    }

    public void playToggleSound(boolean enable) {
        class_310 mc = class_310.method_1551();
        if (mc.field_1724 == null) {
            return;
        }
        if (!this.isToggleSoundsEnabled()) {
            return;
        }
        this.playSound(enable);
    }

    private void playSound(boolean enable) {
        String suffix;
        float vol = this.volume.getValue().floatValue();
        String dir = "toggle/";
        String string = suffix = enable ? "_enable" : "_disable";
        if (this.soundPack.is(S1)) {
            SoundUtil.playSound_wav("tone" + suffix, vol);
        } else if (this.soundPack.is(S2)) {
            SoundUtil.playSound_wav("notify" + suffix, vol);
        } else if (this.soundPack.is(S3)) {
            SoundUtil.playSound_wav("akrien" + suffix, vol);
        } else if (this.soundPack.is(S4)) {
            SoundUtil.playSound_wav(dir + "smooth_" + (enable ? "on" : "off"), vol);
        } else if (this.soundPack.is(S5)) {
            SoundUtil.playSound_wav(dir + "celestial_" + (enable ? "on" : "off"), vol);
        } else if (this.soundPack.is(S6)) {
            SoundUtil.playSound_wav(dir + "blop_" + (enable ? "on" : "off"), vol);
        } else if (this.soundPack.is(S7)) {
            SoundUtil.playSound_wav(dir + "module_" + (enable ? "enable" : "disable") + "_5", vol);
        } else if (this.soundPack.is(S8)) {
            SoundUtil.playSound_wav(dir + "module_" + (enable ? "enable" : "disable") + "_6", vol);
        } else if (this.soundPack.is(S9)) {
            SoundUtil.playSound_wav(dir + "module_" + (enable ? "enable" : "disable") + "_7", vol);
        } else {
            SoundUtil.playSound_wav(dir + (enable ? "on" : "off"), vol);
        }
    }
}


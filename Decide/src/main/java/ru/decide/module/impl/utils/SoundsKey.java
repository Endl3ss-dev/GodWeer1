/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
 *  net.minecraft.class_1109
 *  net.minecraft.class_1113
 *  net.minecraft.class_11905
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_3414
 */
package ru.decide.module.impl.utils;

import java.nio.file.Path;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.class_1109;
import net.minecraft.class_1113;
import net.minecraft.class_11905;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3414;
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.module.api.ModuleInfo;
import ru.decide.module.api.settings.impl.BooleanSetting;
import ru.decide.module.api.settings.impl.ButtonSetting;
import ru.decide.module.api.settings.impl.ModeSetting;
import ru.decide.module.api.settings.impl.SliderSetting;
import ru.decide.utils.other.Instance;
import ru.decide.utils.sound.key.KeySoundEngine;
import ru.decide.utils.sound.key.KeySoundPacks;

@ModuleInfo(name="Sounds key", desc="SMR \u0437\u0432\u0443\u043a \u043a\u043b\u0430\u0432\u0438\u0448", category=Category.UTILITIES)
@Environment(value=EnvType.CLIENT)
public final class SoundsKey
extends Module {
    private static final class_2960 FALLBACK_KEY = class_2960.method_60655((String)"decide", (String)"key.press");
    private static final class_2960 FALLBACK_MOUSE = class_2960.method_60655((String)"decide", (String)"key.mouse");
    private static final String[] KEYBOARD_PACKS = (String[])KeySoundPacks.valuesFor("cherrymx_black_pbt").toArray(String[]::new);
    private static final String[] MOUSE_PACKS = (String[])KeySoundPacks.valuesFor("cherrymx_blue_abs_2").toArray(String[]::new);
    public final SliderSetting volume = new SliderSetting(this, "\u0413\u0440\u043e\u043c\u043a\u043e\u0441\u0442\u044c", 100.0f, 0.0f, 100.0f, 5.0f);
    public final BooleanSetting keyboardSounds = new BooleanSetting((Module)this, "\u0417\u0432\u0443\u043a\u0438 \u043a\u043b\u0430\u0432\u0438\u0430\u0442\u0443\u0440\u044b", true);
    public final ModeSetting keyboardPack = new ModeSetting((Module)this, "\u041f\u0430\u043a \u043a\u043b\u0430\u0432\u0438\u0430\u0442\u0443\u0440\u044b", KEYBOARD_PACKS);
    public final SliderSetting keyboardVolume = new SliderSetting(this, "\u0413\u0440\u043e\u043c\u043a\u043e\u0441\u0442\u044c \u043a\u043b\u0430\u0432\u0438\u0430\u0442\u0443\u0440\u044b", 100.0f, 0.0f, 100.0f, 5.0f);
    public final SliderSetting keyboardAmplify = new SliderSetting(this, "\u0423\u0441\u0438\u043b\u0435\u043d\u0438\u0435 \u043a\u043b\u0430\u0432\u0438\u0430\u0442\u0443\u0440\u044b", 1.0f, 1.0f, 16.0f, 1.0f);
    public final BooleanSetting mouseSounds = new BooleanSetting((Module)this, "\u0417\u0432\u0443\u043a\u0438 \u043c\u044b\u0448\u0438", true);
    public final ModeSetting mousePack = new ModeSetting((Module)this, "\u041f\u0430\u043a \u043c\u044b\u0448\u0438", MOUSE_PACKS);
    public final SliderSetting mouseVolume = new SliderSetting(this, "\u0413\u0440\u043e\u043c\u043a\u043e\u0441\u0442\u044c \u043c\u044b\u0448\u0438", 100.0f, 0.0f, 100.0f, 5.0f);
    public final SliderSetting mouseAmplify = new SliderSetting(this, "\u0423\u0441\u0438\u043b\u0435\u043d\u0438\u0435 \u043c\u044b\u0448\u0438", 1.0f, 1.0f, 16.0f, 1.0f);
    public final BooleanSetting playInMenus = new BooleanSetting((Module)this, "\u0417\u0432\u0443\u043a\u0438 \u0432 \u043c\u0435\u043d\u044e", true);
    public final ButtonSetting testKeyboard = new ButtonSetting((Module)this, "\u0422\u0435\u0441\u0442 \u043a\u043b\u0430\u0432\u0438\u0430\u0442\u0443\u0440\u044b", () -> this.test(true));
    public final ButtonSetting testMouse = new ButtonSetting((Module)this, "\u0422\u0435\u0441\u0442 \u043c\u044b\u0448\u0438", () -> this.test(false));
    public final ButtonSetting openPacks = new ButtonSetting((Module)this, "\u041e\u0442\u043a\u0440\u044b\u0442\u044c \u043f\u0430\u043f\u043a\u0443 \u043f\u0430\u043a\u043e\u0432", KeySoundPacks::openFolder);
    public final ButtonSetting rescanPacks = new ButtonSetting((Module)this, "\u041e\u0431\u043d\u043e\u0432\u0438\u0442\u044c \u043f\u0430\u043a\u0438", this::rescan);
    private static int keyCharacters;
    private static int typedCharacters;
    private static boolean valuesChecked;

    public SoundsKey() {
        this.keyboardPack.onAction(() -> {
            KeySoundEngine.clearCache();
            this.warmup();
        });
        this.mousePack.onAction(() -> {
            KeySoundEngine.clearCache();
            this.warmup();
        });
        this.keyboardAmplify.onAction(KeySoundEngine::clearCache);
        this.mouseAmplify.onAction(KeySoundEngine::clearCache);
    }

    public static SoundsKey getInstance() {
        return Instance.get(SoundsKey.class);
    }

    public static void keyEvent(int keyCode, int action) {
        if (keyCode < 0 || action != 1 && action != 2) {
            return;
        }
        SoundsKey module = SoundsKey.getInstance();
        if (module == null || !module.canPlayKeyboard()) {
            return;
        }
        if (SoundsKey.isCharacterKey(keyCode)) {
            ++keyCharacters;
        }
        if (action == 1) {
            module.playSelected(true, false, false);
        }
    }

    public static void noteChar(class_11905 input) {
        if (!input.method_74227()) {
            return;
        }
        SoundsKey module = SoundsKey.getInstance();
        if (module == null || !module.canPlayKeyboard()) {
            return;
        }
        ++typedCharacters;
    }

    public static void finishInputBatch() {
        int extra = Math.max(0, typedCharacters - keyCharacters);
        keyCharacters = 0;
        typedCharacters = 0;
        for (int i = 0; i < extra; ++i) {
            SoundsKey.playKey();
        }
    }

    public static void playKey() {
        SoundsKey module = SoundsKey.getInstance();
        if (module == null) {
            return;
        }
        module.playSelected(true, true, true);
    }

    public static void playMouse() {
        SoundsKey module = SoundsKey.getInstance();
        if (module == null) {
            return;
        }
        module.playSelected(false, true, true);
    }

    private boolean canPlayKeyboard() {
        return this.isEnabled() && this.keyboardSounds.getValue() != false && this.canPlayHere();
    }

    private boolean canPlayHere() {
        if (this.playInMenus.getValue().booleanValue()) {
            return true;
        }
        class_310 client = class_310.method_1551();
        return client.field_1724 != null && client.field_1755 == null;
    }

    private void playSelected(boolean keyboard, boolean respectToggles, boolean respectMenus) {
        float gain;
        if (respectToggles && (!this.isEnabled() || (keyboard ? this.keyboardSounds.getValue() == false : this.mouseSounds.getValue() == false))) {
            return;
        }
        if (respectMenus && !this.canPlayHere()) {
            return;
        }
        if (!valuesChecked) {
            valuesChecked = true;
            this.refreshPackValues();
        }
        if ((gain = this.volume.getValue().floatValue() / 100.0f * (keyboard ? this.keyboardVolume.getValue() : this.mouseVolume.getValue()).floatValue() / 100.0f) <= 0.0f) {
            return;
        }
        String pack = keyboard ? this.keyboardPack.getValue() : this.mousePack.getValue();
        int amplify = Math.round((keyboard ? this.keyboardAmplify.getValue() : this.mouseAmplify.getValue()).floatValue());
        Path sound = KeySoundPacks.pickSound(pack);
        if (sound != null && !KeySoundEngine.isUnavailable()) {
            KeySoundEngine.play(sound, Math.min(1.0f, gain), amplify);
            return;
        }
        SoundsKey.playFallback(keyboard ? FALLBACK_KEY : FALLBACK_MOUSE, gain);
    }

    private static void playFallback(class_2960 sound, float gain) {
        class_310 client = class_310.method_1551();
        if (client.method_1483() == null) {
            return;
        }
        try {
            client.method_1483().method_4873((class_1113)class_1109.method_4757((class_3414)class_3414.method_47908((class_2960)sound), (float)1.0f, (float)Math.min(1.0f, gain)));
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private void test(boolean keyboard) {
        this.playSelected(keyboard, false, false);
    }

    private void rescan() {
        List<String> keyboard = KeySoundPacks.rescan();
        if (keyboard.isEmpty()) {
            return;
        }
        this.refreshPackValues();
        this.warmup();
    }

    private void refreshPackValues() {
        List<String> keyboard = KeySoundPacks.valuesFor("cherrymx_black_pbt");
        List<String> mouse = KeySoundPacks.valuesFor("cherrymx_blue_abs_2");
        this.keyboardPack.values = keyboard;
        this.mousePack.values = mouse;
        if (!keyboard.contains(this.keyboardPack.getValue())) {
            this.keyboardPack.set(keyboard.get(0));
        }
        if (!mouse.contains(this.mousePack.getValue())) {
            this.mousePack.set(mouse.get(0));
        }
    }

    private void warmup() {
        KeySoundPacks.warmup(this.keyboardPack.getValue(), Math.round(this.keyboardAmplify.getValue().floatValue()), this.mousePack.getValue(), Math.round(this.mouseAmplify.getValue().floatValue()));
    }

    private static boolean isCharacterKey(int key) {
        return key >= 32 && key <= 162 || key >= 320 && key <= 336;
    }

    @Override
    protected void onEnable() {
        KeySoundPacks.ensureReady();
        this.refreshPackValues();
        this.warmup();
    }

    @Override
    protected void onDisable() {
        KeySoundEngine.clearCache();
        keyCharacters = 0;
        typedCharacters = 0;
    }

    static {
        ClientTickEvents.START_CLIENT_TICK.register(client -> SoundsKey.finishInputBatch());
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1109
 *  net.minecraft.class_1113
 *  net.minecraft.class_1297
 *  net.minecraft.class_1657
 *  net.minecraft.class_1937
 *  net.minecraft.class_2596
 *  net.minecraft.class_2663
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_3414
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package ru.decide.module.impl.utils;

import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1109;
import net.minecraft.class_1113;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_1937;
import net.minecraft.class_2596;
import net.minecraft.class_2663;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3414;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.decide.config.ConfigManager;
import ru.decide.manager.event_impl.AttackEvent;
import ru.decide.manager.event_impl.EventPacket;
import ru.decide.manager.events.orbit.EventHandler;
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.module.api.ModuleInfo;
import ru.decide.module.api.settings.impl.BooleanSetting;
import ru.decide.module.api.settings.impl.ModeSetting;
import ru.decide.module.api.settings.impl.SliderSetting;

@ModuleInfo(name="Effect Sounds", desc="\u0421\u043e\u0437\u0434\u0430\u0451\u0442 \u0437\u0432\u0443\u043a \u043f\u0440\u0438 \u0443\u0431\u0438\u0439\u0441\u0442\u0432\u0435 \u0438\u0433\u0440\u043e\u043a\u0430 \u043b\u0438\u0431\u043e \u043c\u043e\u0431\u0430", category=Category.UTILITIES, autoEnabled=true)
@Environment(value=EnvType.CLIENT)
public final class EffectSounds
extends Module {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"DecideVisuals:EffectSounds");
    private static final byte STATUS_DEATH = 3;
    private static final byte STATUS_TOTEM = 35;
    private static final long KILL_WINDOW_MS = 10000L;
    private static final long SUPPRESS_MS = 120L;
    private static final long BUSY_MS = 400L;
    private static final long MS_PER_TICK = 50L;
    private static final long AFK_NAG_INTERVAL_MS = 60000L;
    private static final String KILL_1 = "\u043b\u0435\u0433\u043e";
    private static final String KILL_2 = "\u043d\u0430\u0430\u0430\u0430\u0430\u0430\u0430\u0430\u0430";
    private static final String KILL_3 = "\u043e\u043f\u0430 \u043d\u0430\u0445\u0443\u0439";
    private static final String KILL_4 = "\u043e\u043f\u0430 \u0435\u0449\u0451 \u043e\u0434\u0438\u043d \u043d\u0430 \u0441\u0432\u043e";
    private static final String KILL_5 = "\u043f\u0430\u0440\u0430\u043c-\u043c";
    private static final String KILL_6 = "\u043f\u043e\u0432\u0435\u0437\u043b\u043e-\u043f\u043e\u0432\u0435\u0437\u043b\u043e";
    private static final String KILL_7 = "\u043f\u0440\u043e\u0449\u0430\u0439 \u0441\u043b\u0430\u0434\u043a\u0438\u0439";
    private static final String KILL_8 = "\u0432 \u043a\u043e\u043b\u0435\u043d\u0438 \u043f\u0438\u0434\u043e\u0440\u0430\u0441 \u0435\u0431\u0443\u0447\u0438\u0439";
    private static final String KILL_9 = "\u043e\u043f\u0430 \u043f\u0440\u043e\u0435\u0431\u0430\u043b";
    private static final String DEATH_1 = "\u043e\u043f\u0430 \u043d\u0430\u0445\u0443\u0439";
    private static final String DEATH_2 = "\u043e\u043f\u0430 \u0435\u0449\u0451 \u043e\u0434\u0438\u043d \u043d\u0430 \u0441\u0432\u043e";
    private static final String DEATH_3 = "\u043f\u0430\u0440\u0430\u043c-\u043c";
    private static final String DEATH_4 = "\u0441\u044d\u0440, \u044f \u0432\u0430\u0445\u0443\u0435";
    private static final String DEATH_5 = "\u0442\u043e\u043d\u0438, \u0447\u0442\u043e \u044d\u0442\u043e \u0437\u0430 \u0445\u0443\u0439\u043d\u044f";
    private static final String DEATH_6 = "\u0443\u0445\u0445\u0445 \u0431\u043b\u044f";
    private static final String DEATH_7 = "\u044f \u043d\u0435 \u0432\u0435\u0440\u044e";
    private static final String TOTEM_1 = "\u0430 \u0433\u0434\u0435 \u0436\u0435 \u0442\u0432\u043e\u0439 \u0442\u043e\u0442\u0435\u043c";
    private static final String TOTEM_2 = "\u0431\u043e\u0436\u0435 \u043f\u043e\u043c\u0438\u043b\u0443\u0439";
    private static final String TOTEM_3 = "\u043d\u0430\u0430\u0430\u0430\u0430\u0430\u0430\u0430\u0430";
    private static final String TOTEM_4 = "\u043d\u0435 \u043d\u0430\u0434\u043e \u0434\u044f\u0434\u044f";
    private static final String TOTEM_5 = "\u043f\u0430\u0440\u0430\u043c-\u043c";
    private static final String TOTEM_6 = "\u043f\u043e\u0432\u0435\u0437\u043b\u043e-\u043f\u043e\u0432\u0435\u0437\u043b\u043e";
    private static final String TOTEM_7 = "\u0443\u0445\u0445\u0445 \u0431\u043b\u044f";
    private static final String TOTEM_8 = "\u0431\u043b\u044f \u043c\u0438\u043d\u0443\u0441 \u0442\u0430\u043b";
    private static final String TOTEM_9 = "\u044f \u0442\u0435\u0431\u044f \u043c\u043e\u043d\u0433\u043d\u0443\u0443\u0443";
    private static final String AFK_1 = "\u043e\u0436\u0438\u0434\u0430\u043d\u0438\u0435";
    private static final String AFK_2 = "\u0445\u0430\u043f\u0438 \u0445\u0430\u043f\u0438 \u0445\u0430\u043f\u0438";
    private static final String AFK_3 = "\u044f \u0436\u0435 \u0437\u043d\u0430\u044e \u0447\u0442\u043e \u0442\u044b \u0434\u0440\u043e\u0447\u0438\u0448\u044c";
    private static final String AFK_NAG = "\u044f \u0436\u0435 \u0437\u043d\u0430\u044e \u0447\u0442\u043e \u0442\u044b \u0434\u0440\u043e\u0447\u0438\u0448\u044c";
    private static final String GREETING_1 = "\u0434\u043e\u0431\u0440\u043e \u043f\u043e\u0436\u0430\u043b\u043e\u0432\u0430\u0442\u044c";
    private static final String GREETING_2 = "\u0431\u0430\u0442\u044f \u0434\u043e\u043c\u0430";
    private static final String GREETING_3 = "\u044d\u0442\u043e \u0434\u0435\u0441\u0430\u0439\u0434 \u0432\u0438\u0437\u0443\u0430\u043b \u2014 \u043d\u0435\u0442 \u044d\u0442\u043e \u043f\u0430\u0442\u0440\u0438\u043a";
    private static final String GREETING_4 = "\u0434\u043e\u0431\u0440\u043e \u043f\u043e\u0436\u0430\u043b\u043e\u0432\u0430\u0442\u044c \u0432 \u0434\u0435\u0441\u0430\u0439\u0434 \u0432\u0438\u0437\u0443\u0430\u043b";
    private static final String[] KILL_NAMES = new String[]{"\u043b\u0435\u0433\u043e", "\u043d\u0430\u0430\u0430\u0430\u0430\u0430\u0430\u0430\u0430", "\u043e\u043f\u0430 \u043d\u0430\u0445\u0443\u0439", "\u043e\u043f\u0430 \u0435\u0449\u0451 \u043e\u0434\u0438\u043d \u043d\u0430 \u0441\u0432\u043e", "\u043f\u0430\u0440\u0430\u043c-\u043c", "\u043f\u043e\u0432\u0435\u0437\u043b\u043e-\u043f\u043e\u0432\u0435\u0437\u043b\u043e", "\u043f\u0440\u043e\u0449\u0430\u0439 \u0441\u043b\u0430\u0434\u043a\u0438\u0439", "\u0432 \u043a\u043e\u043b\u0435\u043d\u0438 \u043f\u0438\u0434\u043e\u0440\u0430\u0441 \u0435\u0431\u0443\u0447\u0438\u0439", "\u043e\u043f\u0430 \u043f\u0440\u043e\u0435\u0431\u0430\u043b"};
    private static final String[] DEATH_NAMES = new String[]{"\u043e\u043f\u0430 \u043d\u0430\u0445\u0443\u0439", "\u043e\u043f\u0430 \u0435\u0449\u0451 \u043e\u0434\u0438\u043d \u043d\u0430 \u0441\u0432\u043e", "\u043f\u0430\u0440\u0430\u043c-\u043c", "\u0441\u044d\u0440, \u044f \u0432\u0430\u0445\u0443\u0435", "\u0442\u043e\u043d\u0438, \u0447\u0442\u043e \u044d\u0442\u043e \u0437\u0430 \u0445\u0443\u0439\u043d\u044f", "\u0443\u0445\u0445\u0445 \u0431\u043b\u044f", "\u044f \u043d\u0435 \u0432\u0435\u0440\u044e"};
    private static final String[] TOTEM_NAMES = new String[]{"\u0430 \u0433\u0434\u0435 \u0436\u0435 \u0442\u0432\u043e\u0439 \u0442\u043e\u0442\u0435\u043c", "\u0431\u043e\u0436\u0435 \u043f\u043e\u043c\u0438\u043b\u0443\u0439", "\u043d\u0430\u0430\u0430\u0430\u0430\u0430\u0430\u0430\u0430", "\u043d\u0435 \u043d\u0430\u0434\u043e \u0434\u044f\u0434\u044f", "\u043f\u0430\u0440\u0430\u043c-\u043c", "\u043f\u043e\u0432\u0435\u0437\u043b\u043e-\u043f\u043e\u0432\u0435\u0437\u043b\u043e", "\u0443\u0445\u0445\u0445 \u0431\u043b\u044f", "\u0431\u043b\u044f \u043c\u0438\u043d\u0443\u0441 \u0442\u0430\u043b", "\u044f \u0442\u0435\u0431\u044f \u043c\u043e\u043d\u0433\u043d\u0443\u0443\u0443"};
    private static final String[] AFK_NAMES = new String[]{"\u043e\u0436\u0438\u0434\u0430\u043d\u0438\u0435", "\u0445\u0430\u043f\u0438 \u0445\u0430\u043f\u0438 \u0445\u0430\u043f\u0438", "\u044f \u0436\u0435 \u0437\u043d\u0430\u044e \u0447\u0442\u043e \u0442\u044b \u0434\u0440\u043e\u0447\u0438\u0448\u044c"};
    private static final class_2960 SOUND_KILL_RANDOM = class_2960.method_60655((String)"decide", (String)"effect.kill");
    private static final class_2960 SOUND_DEATH_RANDOM = class_2960.method_60655((String)"decide", (String)"effect.death");
    private static final class_2960 SOUND_TOTEM_RANDOM = class_2960.method_60655((String)"decide", (String)"effect.totem");
    private static final class_2960 SOUND_AFK_RANDOM = class_2960.method_60655((String)"decide", (String)"effect.afk");
    private static final class_2960[] KILL_IDS = EffectSounds.sequenceIds("effect.kill", KILL_NAMES.length);
    private static final class_2960[] DEATH_IDS = EffectSounds.sequenceIds("effect.death", DEATH_NAMES.length);
    private static final class_2960[] TOTEM_IDS = EffectSounds.sequenceIds("effect.totem", TOTEM_NAMES.length);
    private static final class_2960[] AFK_IDS = EffectSounds.sequenceIds("effect.afk", AFK_NAMES.length);
    private static final class_2960 SOUND_GREETING_1 = class_2960.method_60655((String)"decide", (String)"effect.greeting_1");
    private static final class_2960 SOUND_GREETING_2 = class_2960.method_60655((String)"decide", (String)"effect.greeting_2");
    private static final class_2960 SOUND_GREETING_4 = class_2960.method_60655((String)"decide", (String)"effect.greeting_4");
    private static final class_2960 SOUND_GREETING_Q = class_2960.method_60655((String)"decide", (String)"effect.greeting_3_question");
    private static final class_2960 SOUND_GREETING_A = class_2960.method_60655((String)"decide", (String)"effect.greeting_3_answer");
    public final BooleanSetting greeting = new BooleanSetting((Module)this, "\u041f\u0440\u0438\u0432\u0435\u0442\u0441\u0442\u0432\u0438\u0435 \u043f\u0440\u0438 \u0437\u0430\u043f\u0443\u0441\u043a\u0435", true);
    public final ModeSetting greetingSound = new ModeSetting((Module)this, "\u0412\u0430\u0440\u0438\u0430\u043d\u0442 \u043f\u0440\u0438\u0432\u0435\u0442\u0441\u0442\u0432\u0438\u044f", "\u0434\u043e\u0431\u0440\u043e \u043f\u043e\u0436\u0430\u043b\u043e\u0432\u0430\u0442\u044c", "\u0431\u0430\u0442\u044f \u0434\u043e\u043c\u0430", "\u044d\u0442\u043e \u0434\u0435\u0441\u0430\u0439\u0434 \u0432\u0438\u0437\u0443\u0430\u043b \u2014 \u043d\u0435\u0442 \u044d\u0442\u043e \u043f\u0430\u0442\u0440\u0438\u043a", "\u0434\u043e\u0431\u0440\u043e \u043f\u043e\u0436\u0430\u043b\u043e\u0432\u0430\u0442\u044c \u0432 \u0434\u0435\u0441\u0430\u0439\u0434 \u0432\u0438\u0437\u0443\u0430\u043b");
    public final SliderSetting greetingVolume = new SliderSetting(this, "\u0413\u0440\u043e\u043c\u043a\u043e\u0441\u0442\u044c \u043f\u0440\u0438\u0432\u0435\u0442\u0441\u0442\u0432\u0438\u044f", 100.0f, 0.0f, 100.0f, 5.0f);
    public final SliderSetting dialogueGap = new SliderSetting(this, "\u041f\u0430\u0443\u0437\u0430 \u0432 \u0434\u0438\u0430\u043b\u043e\u0433\u0435 (\u043c\u0441)", 500.0f, 0.0f, 3000.0f, 50.0f);
    public final BooleanSetting killEnabled = new BooleanSetting((Module)this, "\u0417\u0432\u0443\u043a \u0437\u0430 \u0443\u0431\u0438\u0439\u0441\u0442\u0432\u043e", true);
    public final BooleanSetting killRandom = new BooleanSetting((Module)this, "\u0421\u043b\u0443\u0447\u0430\u0439\u043d\u044b\u0439 \u0437\u0432\u0443\u043a", true);
    public final ModeSetting killSound = new ModeSetting((Module)this, "\u0412\u0430\u0440\u0438\u0430\u043d\u0442 \u0443\u0431\u0438\u0439\u0441\u0442\u0432\u0430", KILL_NAMES).onAction(this::playSelectedKillSound);
    public final ModeSetting killTarget = new ModeSetting((Module)this, "\u0417\u0432\u0443\u043a \u043f\u043e\u0441\u043b\u0435 \u043a\u043e\u0433\u043e", "\u0418\u0433\u0440\u043e\u043a\u0430", "\u041c\u043e\u0431\u0430", "\u041b\u044e\u0431\u043e\u0433\u043e");
    public final SliderSetting killVolume = new SliderSetting(this, "\u0413\u0440\u043e\u043c\u043a\u043e\u0441\u0442\u044c \u0443\u0431\u0438\u0439\u0441\u0442\u0432\u0430", 100.0f, 0.0f, 100.0f, 5.0f);
    public final BooleanSetting deathEnabled = new BooleanSetting((Module)this, "\u0417\u0432\u0443\u043a \u043f\u043e\u0441\u043b\u0435 \u0441\u043c\u0435\u0440\u0442\u0438", true);
    public final BooleanSetting deathRandom = new BooleanSetting((Module)this, "\u0421\u043b\u0443\u0447\u0430\u0439\u043d\u044b\u0439 \u0437\u0432\u0443\u043a", true);
    public final ModeSetting deathSound = new ModeSetting((Module)this, "\u0412\u0430\u0440\u0438\u0430\u043d\u0442 \u0441\u043c\u0435\u0440\u0442\u0438", DEATH_NAMES).onAction(this::playSelectedDeathSound);
    public final SliderSetting deathVolume = new SliderSetting(this, "\u0413\u0440\u043e\u043c\u043a\u043e\u0441\u0442\u044c \u0441\u043c\u0435\u0440\u0442\u0438", 100.0f, 0.0f, 100.0f, 5.0f);
    public final BooleanSetting totemEnabled = new BooleanSetting((Module)this, "\u0417\u0432\u0443\u043a \u043f\u043e\u0441\u043b\u0435 \u0442\u043e\u0442\u0435\u043c\u0430", true);
    public final BooleanSetting totemRandom = new BooleanSetting((Module)this, "\u0421\u043b\u0443\u0447\u0430\u0439\u043d\u044b\u0439 \u0437\u0432\u0443\u043a", true);
    public final ModeSetting totemSound = new ModeSetting((Module)this, "\u0412\u0430\u0440\u0438\u0430\u043d\u0442 \u0442\u043e\u0442\u0435\u043c\u0430", TOTEM_NAMES).onAction(this::playSelectedTotemSound);
    public final SliderSetting totemVolume = new SliderSetting(this, "\u0413\u0440\u043e\u043c\u043a\u043e\u0441\u0442\u044c \u0442\u043e\u0442\u0435\u043c\u0430", 100.0f, 0.0f, 100.0f, 5.0f);
    public final BooleanSetting afkEnabled = new BooleanSetting((Module)this, "\u0417\u0432\u0443\u043a \u043f\u0440\u0438 \u0410\u0424\u041a", true);
    public final BooleanSetting afkRandom = new BooleanSetting((Module)this, "\u0421\u043b\u0443\u0447\u0430\u0439\u043d\u044b\u0439 \u0437\u0432\u0443\u043a", true);
    public final ModeSetting afkSound = new ModeSetting((Module)this, "\u0412\u0430\u0440\u0438\u0430\u043d\u0442 \u0410\u0424\u041a", AFK_NAMES).onAction(this::playSelectedAfkSound);
    public final SliderSetting afkDelay = new SliderSetting(this, "\u0417\u0430\u0434\u0435\u0440\u0436\u043a\u0430 \u0410\u0424\u041a (\u0441\u0435\u043a)", 60.0f, 10.0f, 600.0f, 5.0f);
    public final SliderSetting afkVolume = new SliderSetting(this, "\u0413\u0440\u043e\u043c\u043a\u043e\u0441\u0442\u044c \u0410\u0424\u041a", 100.0f, 0.0f, 100.0f, 5.0f);
    private class_1297 lastTarget;
    private long lastAttackTime;
    private class_2960 pendingTestSound;
    private float pendingTestVolume;
    private boolean greetingPending;
    private boolean deathFired;
    private boolean sawDead;
    private long afkPlayedAtMs;
    private boolean lastGreetingFlag;
    private boolean warnedMissing;
    private class_2960 queuedSound;
    private int queuedTicksLeft;
    private float queuedVolume;
    private static long suppressUntilMs;
    private static long busyUntilMs;
    private static volatile long lastActivityMs;
    private static boolean PLAYING_OWN_SOUND;

    private static class_2960[] sequenceIds(String prefix, int count) {
        class_2960[] out = new class_2960[count];
        for (int i = 0; i < count; ++i) {
            out[i] = class_2960.method_60655((String)"decide", (String)(prefix + "_" + (i + 1)));
        }
        return out;
    }

    private static boolean isBusy() {
        return System.currentTimeMillis() < busyUntilMs;
    }

    public EffectSounds() {
        this.greetingSound.setVisible(() -> this.greeting.getValue());
        this.greetingVolume.setVisible(() -> this.greeting.getValue());
        this.dialogueGap.setVisible(() -> this.greeting.getValue() != false && this.isDialogueVariant());
        this.killRandom.setVisible(() -> this.killEnabled.getValue());
        this.killSound.setVisible(() -> this.killEnabled.getValue() != false && this.killRandom.getValue() == false);
        this.killTarget.setVisible(() -> this.killEnabled.getValue());
        this.killVolume.setVisible(() -> this.killEnabled.getValue());
        this.deathRandom.setVisible(() -> this.deathEnabled.getValue());
        this.deathSound.setVisible(() -> this.deathEnabled.getValue() != false && this.deathRandom.getValue() == false);
        this.deathVolume.setVisible(() -> this.deathEnabled.getValue());
        this.totemRandom.setVisible(() -> this.totemEnabled.getValue());
        this.totemSound.setVisible(() -> this.totemEnabled.getValue() != false && this.totemRandom.getValue() == false);
        this.totemVolume.setVisible(() -> this.totemEnabled.getValue());
        this.afkRandom.setVisible(() -> this.afkEnabled.getValue());
        this.afkSound.setVisible(() -> this.afkEnabled.getValue() != false && this.afkRandom.getValue() == false);
        this.afkDelay.setVisible(() -> this.afkEnabled.getValue());
        this.afkVolume.setVisible(() -> this.afkEnabled.getValue());
        this.pendingTestVolume = 1.0f;
        this.queuedVolume = 1.0f;
        this.greetingPending = this.greeting.getValue();
        this.lastGreetingFlag = this.greeting.getValue();
    }

    @Override
    protected void onEnable() {
        this.pendingTestSound = null;
        this.lastTarget = null;
        this.lastAttackTime = 0L;
        this.deathFired = false;
        this.sawDead = false;
        this.afkPlayedAtMs = 0L;
        this.queuedSound = null;
        this.greetingPending = false;
    }

    public void playGreetingOnStart() {
        if (!this.isEnabled()) {
            this.greetingPending = false;
            return;
        }
        this.greetingPending = this.greeting.getValue();
    }

    private boolean isDialogueVariant() {
        return GREETING_3.equals(this.greetingSound.getValue());
    }

    private boolean playGreeting(float volume) {
        class_2960 sound;
        if (this.isDialogueVariant()) {
            if (!this.tryPlay(SOUND_GREETING_Q, volume)) {
                return false;
            }
            this.queueFollowUp(SOUND_GREETING_A, volume, this.dialogueGap.getValue().intValue());
            return true;
        }
        String selected = this.greetingSound.getValue();
        Object object = GREETING_1.equals(selected) ? SOUND_GREETING_1 : (GREETING_2.equals(selected) ? SOUND_GREETING_2 : (sound = GREETING_4.equals(selected) ? SOUND_GREETING_4 : null));
        if (sound == null) {
            return true;
        }
        if (EffectSounds.isBusy()) {
            return true;
        }
        return this.tryPlay(sound, volume);
    }

    private void playSelectedKillSound() {
        if (EffectSounds.skipTest() || this.killRandom.getValue().booleanValue()) {
            return;
        }
        this.requestTest(EffectSounds.pick(this.killSound, this.killRandom.getValue(), KILL_NAMES, KILL_IDS, SOUND_KILL_RANDOM), this.killVolume.getValue().floatValue() / 100.0f);
    }

    private void playSelectedDeathSound() {
        if (EffectSounds.skipTest() || this.deathRandom.getValue().booleanValue()) {
            return;
        }
        this.requestTest(EffectSounds.pick(this.deathSound, this.deathRandom.getValue(), DEATH_NAMES, DEATH_IDS, SOUND_DEATH_RANDOM), this.deathVolume.getValue().floatValue() / 100.0f);
    }

    private void playSelectedTotemSound() {
        if (EffectSounds.skipTest() || this.totemRandom.getValue().booleanValue()) {
            return;
        }
        this.requestTest(EffectSounds.pick(this.totemSound, this.totemRandom.getValue(), TOTEM_NAMES, TOTEM_IDS, SOUND_TOTEM_RANDOM), this.totemVolume.getValue().floatValue() / 100.0f);
    }

    private void playSelectedAfkSound() {
        if (EffectSounds.skipTest() || this.afkRandom.getValue().booleanValue()) {
            return;
        }
        this.requestTest(EffectSounds.pick(this.afkSound, this.afkRandom.getValue(), AFK_NAMES, AFK_IDS, SOUND_AFK_RANDOM), this.afkVolume.getValue().floatValue() / 100.0f);
    }

    private static boolean skipTest() {
        return ConfigManager.isLoadingConfig();
    }

    private static class_2960 pick(ModeSetting setting, boolean random, String[] names, class_2960[] ids, class_2960 randomSet) {
        if (random) {
            return randomSet;
        }
        String selected = setting.getValue();
        for (int i = 0; i < names.length; ++i) {
            if (!names[i].equals(selected)) continue;
            return ids[i];
        }
        return null;
    }

    private void requestTest(class_2960 sound, float volume) {
        if (sound == null) {
            this.pendingTestSound = null;
            return;
        }
        if (EffectSounds.isBusy()) {
            return;
        }
        if (this.tryPlay(sound, volume)) {
            this.pendingTestSound = null;
        } else {
            this.pendingTestSound = sound;
            this.pendingTestVolume = volume;
        }
    }

    private void queueFollowUp(class_2960 sound, float volume, int gapMs) {
        this.queuedSound = sound;
        this.queuedVolume = volume;
        this.queuedTicksLeft = Math.max(1, (int)Math.round((double)gapMs / 50.0));
    }

    private void tickQueuedSound() {
        if (this.queuedSound == null) {
            return;
        }
        if (--this.queuedTicksLeft > 0) {
            return;
        }
        class_2960 sound = this.queuedSound;
        this.queuedSound = null;
        this.tryPlay(sound, this.queuedVolume);
    }

    public void tickUiSounds() {
        float volume;
        if (!this.isEnabled()) {
            this.greetingPending = false;
            this.pendingTestSound = null;
            this.deathFired = false;
            this.sawDead = false;
            this.afkPlayedAtMs = 0L;
            this.queuedSound = null;
            this.lastTarget = null;
            this.lastGreetingFlag = this.greeting.getValue();
            return;
        }
        this.tickGreetingToggle();
        this.tickQueuedSound();
        if (this.greetingPending && this.playGreeting(volume = this.greetingVolume.getValue().floatValue() / 100.0f)) {
            this.greetingPending = false;
            LOGGER.info("[effect-sounds] \u043f\u0440\u0438\u0432\u0435\u0442\u0441\u0442\u0432\u0438\u0435 \u0441\u044b\u0433\u0440\u0430\u043d\u043e (\u0433\u0440\u043e\u043c\u043a\u043e\u0441\u0442\u044c {})", (Object)EffectSounds.fmtVolume(volume));
        }
        if (this.pendingTestSound != null && this.tryPlay(this.pendingTestSound, this.pendingTestVolume)) {
            this.pendingTestSound = null;
        }
        this.tickDeath();
        this.tickAfk();
    }

    private void tickGreetingToggle() {
        boolean now = this.greeting.getValue();
        if (now == this.lastGreetingFlag) {
            return;
        }
        this.lastGreetingFlag = now;
        if (!now || EffectSounds.skipTest()) {
            return;
        }
        this.playGreeting(this.greetingVolume.getValue().floatValue() / 100.0f);
    }

    private boolean tryPlay(class_2960 sound, float volume) {
        class_310 client = class_310.method_1551();
        if (client == null || client.method_1483() == null) {
            return false;
        }
        if (client.method_1483().method_4869(sound) == null) {
            if (!this.warnedMissing) {
                this.warnedMissing = true;
                LOGGER.warn("[effect-sounds] \u0437\u0432\u0443\u043a {} \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d \u0432 \u0440\u0435\u0435\u0441\u0442\u0440\u0435 - \u043f\u0440\u043e\u0432\u0435\u0440\u044c sounds.json", (Object)sound);
            }
            return false;
        }
        EffectSounds.playCustom(sound, volume);
        return true;
    }

    private static void playCustom(class_2960 sound, float volume) {
        EffectSounds.play(class_3414.method_47908((class_2960)sound), volume);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void play(class_3414 sound, float volume) {
        if (sound == null || volume <= 0.0f) {
            return;
        }
        class_310 client = class_310.method_1551();
        if (client == null || client.method_1483() == null) {
            return;
        }
        suppressUntilMs = System.currentTimeMillis() + 120L;
        busyUntilMs = System.currentTimeMillis() + 400L;
        try {
            PLAYING_OWN_SOUND = true;
            client.method_1483().method_4873((class_1113)class_1109.method_4757((class_3414)sound, (float)1.0f, (float)volume));
        }
        catch (Throwable t) {
            LOGGER.warn("[effect-sounds] \u043d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u043f\u0440\u043e\u0438\u0433\u0440\u0430\u0442\u044c {}", (Object)sound, (Object)t);
        }
        finally {
            PLAYING_OWN_SOUND = false;
        }
    }

    public static boolean isSuppressingGameSounds() {
        return !PLAYING_OWN_SOUND && System.currentTimeMillis() < suppressUntilMs;
    }

    public static void noteUserActivity() {
        lastActivityMs = System.currentTimeMillis();
    }

    @EventHandler
    public void onAttack(AttackEvent event) {
        class_310 client = class_310.method_1551();
        if (client.field_1724 == null) {
            return;
        }
        class_1297 target = event.getTarget();
        if (target == null || target == client.field_1724) {
            return;
        }
        this.lastTarget = target;
        this.lastAttackTime = System.currentTimeMillis();
    }

    @EventHandler
    public void onPacket(EventPacket event) {
        class_2596<?> class_25962;
        if (event.isSend() || !((class_25962 = event.getPacket()) instanceof class_2663)) {
            return;
        }
        class_2663 packet = (class_2663)class_25962;
        byte status = packet.method_11470();
        if (status == 35) {
            this.onTotem(packet);
        } else if (status == 3) {
            this.onDeath(packet);
        }
    }

    private void onTotem(class_2663 packet) {
        class_310 client = class_310.method_1551();
        if (client.field_1687 == null) {
            return;
        }
        if (!this.totemEnabled.getValue().booleanValue()) {
            return;
        }
        class_1297 entity = packet.method_11469((class_1937)client.field_1687);
        if (entity == null) {
            return;
        }
        if (EffectSounds.isBusy()) {
            return;
        }
        class_2960 sound = EffectSounds.pick(this.totemSound, this.totemRandom.getValue(), TOTEM_NAMES, TOTEM_IDS, SOUND_TOTEM_RANDOM);
        if (sound == null) {
            return;
        }
        float volume = this.totemVolume.getValue().floatValue() / 100.0f;
        EffectSounds.playCustom(sound, volume);
        LOGGER.info("[effect-sounds] \u0442\u043e\u0442\u0435\u043c: {} ({})", (Object)sound, (Object)EffectSounds.fmtVolume(volume));
    }

    private void onDeath(class_2663 packet) {
        class_310 client = class_310.method_1551();
        if (client.field_1687 == null || client.field_1724 == null) {
            return;
        }
        class_1297 entity = packet.method_11469((class_1937)client.field_1687);
        if (entity == null) {
            return;
        }
        if (entity == client.field_1724) {
            this.playDeath();
            return;
        }
        this.onKill(entity);
    }

    private void onKill(class_1297 entity) {
        if (!this.killEnabled.getValue().booleanValue()) {
            return;
        }
        if (!this.matchesKillTarget(entity)) {
            return;
        }
        if (EffectSounds.isBusy()) {
            return;
        }
        if (this.lastTarget == null || !this.lastTarget.method_5667().equals(entity.method_5667()) || System.currentTimeMillis() - this.lastAttackTime > 10000L) {
            return;
        }
        this.lastTarget = null;
        class_2960 sound = EffectSounds.pick(this.killSound, this.killRandom.getValue(), KILL_NAMES, KILL_IDS, SOUND_KILL_RANDOM);
        if (sound == null) {
            return;
        }
        float volume = this.killVolume.getValue().floatValue() / 100.0f;
        EffectSounds.playCustom(sound, volume);
        LOGGER.info("[effect-sounds] \u0443\u0431\u0438\u0439\u0441\u0442\u0432\u043e: {} ({})", (Object)sound, (Object)EffectSounds.fmtVolume(volume));
    }

    private void playDeath() {
        if (!this.deathEnabled.getValue().booleanValue()) {
            return;
        }
        if (this.deathFired || EffectSounds.isBusy()) {
            return;
        }
        this.deathFired = true;
        class_2960 sound = EffectSounds.pick(this.deathSound, this.deathRandom.getValue(), DEATH_NAMES, DEATH_IDS, SOUND_DEATH_RANDOM);
        if (sound == null) {
            return;
        }
        float volume = this.deathVolume.getValue().floatValue() / 100.0f;
        EffectSounds.playCustom(sound, volume);
        LOGGER.info("[effect-sounds] \u0441\u043c\u0435\u0440\u0442\u044c: {} ({})", (Object)sound, (Object)EffectSounds.fmtVolume(volume));
    }

    private void tickDeath() {
        class_310 client = class_310.method_1551();
        if (client == null || client.field_1724 == null) {
            return;
        }
        if (client.field_1724.method_6032() <= 0.0f) {
            this.sawDead = true;
            this.playDeath();
            return;
        }
        if (this.sawDead) {
            this.sawDead = false;
            this.deathFired = false;
        }
    }

    private void tickAfk() {
        if (!this.afkEnabled.getValue().booleanValue()) {
            this.afkPlayedAtMs = 0L;
            return;
        }
        class_310 client = class_310.method_1551();
        if (client == null || client.field_1724 == null) {
            this.afkPlayedAtMs = 0L;
            return;
        }
        long now = System.currentTimeMillis();
        long idleMs = now - lastActivityMs;
        if (idleMs < (long)(this.afkDelay.getValue().floatValue() * 1000.0f)) {
            this.afkPlayedAtMs = 0L;
            return;
        }
        if (this.afkPlayedAtMs != 0L && now - this.afkPlayedAtMs < this.afkRepeatIntervalMs()) {
            return;
        }
        if (EffectSounds.isBusy()) {
            return;
        }
        this.afkPlayedAtMs = now;
        class_2960 sound = EffectSounds.pick(this.afkSound, this.afkRandom.getValue(), AFK_NAMES, AFK_IDS, SOUND_AFK_RANDOM);
        if (sound == null) {
            return;
        }
        float volume = this.afkVolume.getValue().floatValue() / 100.0f;
        EffectSounds.playCustom(sound, volume);
        LOGGER.info("[effect-sounds] \u0430\u0444\u043a: {} ({})", (Object)sound, (Object)EffectSounds.fmtVolume(volume));
    }

    private long afkRepeatIntervalMs() {
        long base = (long)(this.afkDelay.getValue().floatValue() * 1000.0f);
        if (!this.afkRandom.getValue().booleanValue() && "\u044f \u0436\u0435 \u0437\u043d\u0430\u044e \u0447\u0442\u043e \u0442\u044b \u0434\u0440\u043e\u0447\u0438\u0448\u044c".equals(this.afkSound.getValue())) {
            return Math.max(base, 60000L);
        }
        return base;
    }

    private boolean matchesKillTarget(class_1297 entity) {
        String target = this.killTarget.getValue();
        if ("\u041b\u044e\u0431\u043e\u0433\u043e".equals(target)) {
            return true;
        }
        boolean isPlayer = entity instanceof class_1657;
        return "\u0418\u0433\u0440\u043e\u043a\u0430".equals(target) == isPlayer;
    }

    private static String fmtVolume(float volume) {
        return String.format(Locale.ROOT, "%.2f", Float.valueOf(volume));
    }

    static {
        lastActivityMs = System.currentTimeMillis();
    }
}


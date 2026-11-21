/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1109
 *  net.minecraft.class_1113
 *  net.minecraft.class_2338
 *  net.minecraft.class_2374
 *  net.minecraft.class_243
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_332
 *  net.minecraft.class_3414
 *  net.minecraft.class_3532
 *  net.minecraft.class_4076
 *  net.minecraft.class_418
 *  net.minecraft.class_437
 *  net.minecraft.class_5498
 *  net.minecraft.class_638
 *  net.minecraft.class_746
 */
package ru.decide.module.impl.render;

import java.awt.Color;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1109;
import net.minecraft.class_1113;
import net.minecraft.class_2338;
import net.minecraft.class_2374;
import net.minecraft.class_243;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_3414;
import net.minecraft.class_3532;
import net.minecraft.class_4076;
import net.minecraft.class_418;
import net.minecraft.class_437;
import net.minecraft.class_5498;
import net.minecraft.class_638;
import net.minecraft.class_746;
import ru.decide.manager.event_impl.EventTick;
import ru.decide.manager.events.orbit.EventHandler;
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.module.api.ModuleInfo;
import ru.decide.module.api.settings.impl.BooleanSetting;
import ru.decide.module.api.settings.impl.ColorSetting;
import ru.decide.module.api.settings.impl.DelimiterSetting;
import ru.decide.module.api.settings.impl.ModeSetting;
import ru.decide.module.api.settings.impl.SliderSetting;
import ru.decide.utils.math.ChatUtils;
import ru.decide.utils.other.Instance;
import ru.decide.utils.render.RenderUtil;
import ru.decide.utils.render.font.Fonts;
import ru.decide.utils.render.wasted.WastedPipeline;
import ru.decide.utils.render.wasted.WastedState;

@ModuleInfo(name="Wasted", desc="\u041a\u0438\u043d\u0435\u043c\u0430\u0442\u043e\u0433\u0440\u0430\u0444\u0438\u0447\u043d\u0430\u044f \u0441\u043c\u0435\u0440\u0442\u044c \u0432 \u0441\u0442\u0438\u043b\u0435 GTA V", category=Category.VISUALS)
@Environment(value=EnvType.CLIENT)
public final class WastedDeath
extends Module {
    private static final String LABEL_WASTED = "WASTED";
    private static final String LABEL_RU = "\u0422\u042b \u0423\u041c\u0415\u0420";
    private static final String LABEL_NONE = "\u041d\u0435\u0442";
    private static final float TEXT_Y_LIFT = 5.0f;
    private static final double CORPSE_EYE_HEIGHT = 0.7;
    private static final double DETACH_DISTANCE_SQ = 9.0;
    private static final long RETRIGGER_GUARD_MS = 4000L;
    private static final long RESPAWN_WAIT_MS = 4000L;
    private static final class_2960 WASTED_SOUND_ID = class_2960.method_60655((String)"decide", (String)"wasted");
    public final DelimiterSetting generalSeparator = new DelimiterSetting(this, "\u042d\u0444\u0444\u0435\u043a\u0442");
    public final SliderSetting duration = new SliderSetting(this, "\u0414\u043b\u0438\u0442\u0435\u043b\u044c\u043d\u043e\u0441\u0442\u044c", 6.0f, 2.0f, 10.0f, 1.0f);
    public final ModeSetting label = new ModeSetting((Module)this, "\u041d\u0430\u0434\u043f\u0438\u0441\u044c", "WASTED", "\u0422\u042b \u0423\u041c\u0415\u0420", "\u041d\u0435\u0442");
    public final ColorSetting textColor = new ColorSetting((Module)this, "\u0426\u0432\u0435\u0442 \u043d\u0430\u0434\u043f\u0438\u0441\u0438", new Color(178, 20, 24, 255).getRGB());
    public final DelimiterSetting cameraSeparator = new DelimiterSetting(this, "\u041a\u0430\u043c\u0435\u0440\u0430");
    public final BooleanSetting orbit = new BooleanSetting((Module)this, "\u041e\u0431\u043b\u0451\u0442 \u043a\u0430\u043c\u0435\u0440\u044b", true);
    public final SliderSetting orbitHeight = new SliderSetting(this, "\u0412\u044b\u0441\u043e\u0442\u0430 \u043f\u043e\u0434\u044a\u0451\u043c\u0430", 45.0f, 15.0f, 90.0f, 5.0f);
    public final BooleanSetting autoRespawn = new BooleanSetting((Module)this, "\u0412\u043e\u0437\u0440\u043e\u0436\u0434\u0430\u0442\u044c \u043f\u043e\u0441\u043b\u0435 \u044d\u0444\u0444\u0435\u043a\u0442\u0430", true);
    public final DelimiterSetting soundSeparator = new DelimiterSetting(this, "\u0417\u0432\u0443\u043a");
    public final BooleanSetting playSound = new BooleanSetting((Module)this, "\u0417\u0432\u0443\u043a \u0441\u043c\u0435\u0440\u0442\u0438", true);
    public final SliderSetting soundVolume = new SliderSetting(this, "\u0413\u0440\u043e\u043c\u043a\u043e\u0441\u0442\u044c", 70.0f, 10.0f, 100.0f, 5.0f);
    private class_5498 savedCameraType;
    private boolean respawnPending;
    private long lastFinishedAtMs;
    private long respawnWaitUntilMs;
    private class_437 pendingDeathScreen;

    public static WastedDeath getInstance() {
        return Instance.get(WastedDeath.class);
    }

    private static WastedDeath getInstanceIfReady() {
        try {
            return WastedDeath.getInstance();
        }
        catch (RuntimeException ignored) {
            return null;
        }
    }

    public static boolean isRunning() {
        WastedDeath module = WastedDeath.getInstanceIfReady();
        return module != null && module.isEnabled() && WastedState.isActive();
    }

    public static boolean interceptDeathScreen(class_437 screen) {
        WastedDeath module = WastedDeath.getInstanceIfReady();
        if (module == null || !module.isEnabled()) {
            return false;
        }
        return module.beginFrom(screen);
    }

    public static boolean blocksDeathScreen() {
        WastedDeath module = WastedDeath.getInstanceIfReady();
        if (module == null || !module.isEnabled()) {
            return false;
        }
        return WastedState.isActive() || module.awaitingRespawn();
    }

    public boolean orbitEnabled() {
        return this.orbit.getValue();
    }

    public class_243 orbitPosition() {
        class_243 anchor = WastedState.anchor();
        float e = WastedDeath.ease();
        double rise = 0.6 + this.riseHeight();
        double back = 0.3 + 1.6 * (double)e;
        double angle = Math.toRadians(WastedState.startYaw());
        double backX = Math.sin(angle) * back;
        double backZ = -Math.cos(angle) * back;
        return new class_243(anchor.field_1352 + backX, anchor.field_1351 + 0.7 + rise, anchor.field_1350 + backZ);
    }

    private double riseHeight() {
        return (double)this.orbitHeight.getValue().floatValue() / 10.0 * (double)WastedDeath.ease();
    }

    public float orbitYaw() {
        class_243 anchor = WastedState.anchor();
        class_243 pos = this.orbitPosition();
        double dx = anchor.field_1352 - pos.field_1352;
        double dz = anchor.field_1350 - pos.field_1350;
        return (float)Math.toDegrees(Math.atan2(-dx, dz));
    }

    public float orbitPitch() {
        class_243 anchor = WastedState.anchor();
        class_243 pos = this.orbitPosition();
        double dy = anchor.field_1351 + 0.7 - pos.field_1351;
        double dx = anchor.field_1352 - pos.field_1352;
        double dz = anchor.field_1350 - pos.field_1350;
        double horizontal = Math.sqrt(dx * dx + dz * dz);
        return (float)Math.toDegrees(-Math.atan2(dy, Math.max(horizontal, 0.001)));
    }

    public float orbitDistanceNow() {
        class_243 anchor = WastedState.anchor();
        return (float)Math.max(0.5, anchor.method_1022(this.orbitPosition()));
    }

    @EventHandler
    public void onTick(EventTick event) {
        class_746 player;
        if (!WastedState.isActive() && !this.respawnPending && this.isEnabled() && (player = WastedDeath.mc.field_1724) != null && !player.method_5805() && WastedDeath.mc.field_1687 != null) {
            this.beginFrom(null);
        }
        if (WastedState.isActive()) {
            if (WastedDeath.mc.field_1724 == null || WastedDeath.mc.field_1687 == null) {
                WastedState.stop();
            } else {
                this.updateDetach();
            }
        }
        if (!WastedState.isActive()) {
            this.restoreCamera();
            this.finishRespawn();
        }
    }

    private void updateDetach() {
        class_746 player = WastedDeath.mc.field_1724;
        class_638 level = WastedDeath.mc.field_1687;
        if (player == null || level == null || !this.orbit.getValue().booleanValue()) {
            WastedState.setDetached(false);
            return;
        }
        class_243 anchor = WastedState.anchor();
        if (!player.method_5805() || anchor == class_243.field_1353) {
            WastedState.setDetached(false);
            return;
        }
        boolean moved = player.method_73189().method_1025(anchor) > 9.0;
        class_2338 pos = class_2338.method_49638((class_2374)anchor);
        boolean loaded = level.method_2935().method_12123(class_4076.method_18675((int)pos.method_10263()), class_4076.method_18675((int)pos.method_10260()));
        WastedState.setDetached(moved && loaded);
    }

    private boolean beginFrom(class_437 screen) {
        class_746 player = WastedDeath.mc.field_1724;
        if (!this.isEnabled() || player == null) {
            return false;
        }
        if (WastedState.isActive()) {
            if (screen != null && this.pendingDeathScreen == null) {
                this.pendingDeathScreen = screen;
            }
            return true;
        }
        if (System.currentTimeMillis() - this.lastFinishedAtMs < 4000L) {
            if (screen != null && this.respawnPending && this.pendingDeathScreen == null) {
                this.pendingDeathScreen = screen;
            }
            return false;
        }
        this.pendingDeathScreen = screen;
        class_243 deathPos = player.method_5805() ? player.method_73189() : player.method_61411();
        float deathYaw = player.method_36454();
        WastedState.begin(deathPos, deathYaw, (long)(this.duration.getValue().floatValue() * 1000.0f));
        this.respawnPending = true;
        this.respawnWaitUntilMs = 0L;
        if (this.orbit.getValue().booleanValue()) {
            this.savedCameraType = WastedDeath.mc.field_1690.method_31044();
            WastedDeath.mc.field_1690.method_31043(class_5498.field_26665);
        }
        if (this.playSound.getValue().booleanValue()) {
            WastedDeath.playWastedSound(this.soundVolume.getValue().floatValue() / 100.0f);
        }
        if (WastedPipeline.deviceReady() && !WastedPipeline.validate()) {
            ChatUtils.addChatMessage("\u00a7c[Wasted] \u043f\u0430\u0439\u043f\u043b\u0430\u0439\u043d \u043d\u0435\u0434\u043e\u0441\u0442\u0443\u043f\u0435\u043d, \u044d\u0444\u0444\u0435\u043a\u0442 \u043d\u0435 \u043e\u0442\u0440\u0438\u0441\u0443\u0435\u0442\u0441\u044f");
        }
        return true;
    }

    private static void playWastedSound(float volume) {
        class_310 client = class_310.method_1551();
        if (client.method_1483() == null) {
            return;
        }
        client.method_1483().method_4873((class_1113)class_1109.method_4757((class_3414)class_3414.method_47908((class_2960)WASTED_SOUND_ID), (float)1.0f, (float)volume));
    }

    private boolean awaitingRespawn() {
        if (this.respawnWaitUntilMs == 0L) {
            return false;
        }
        class_746 player = WastedDeath.mc.field_1724;
        if (player == null || player.method_5805() || System.currentTimeMillis() > this.respawnWaitUntilMs) {
            this.respawnWaitUntilMs = 0L;
            return false;
        }
        return true;
    }

    private void finishRespawn() {
        boolean hardcore;
        if (!this.respawnPending) {
            return;
        }
        this.respawnPending = false;
        this.lastFinishedAtMs = System.currentTimeMillis();
        class_437 deathScreen = this.pendingDeathScreen;
        this.pendingDeathScreen = null;
        class_746 player = WastedDeath.mc.field_1724;
        if (player == null || player.method_5805()) {
            return;
        }
        class_638 level = WastedDeath.mc.field_1687;
        boolean bl = hardcore = level != null && level.method_28104() != null && level.method_28104().method_152();
        if (this.autoRespawn.getValue().booleanValue() && !hardcore) {
            this.respawnWaitUntilMs = System.currentTimeMillis() + 4000L;
            player.method_7331();
        } else if (deathScreen != null) {
            mc.method_1507(deathScreen);
        }
    }

    private void restoreCamera() {
        class_5498 cam = this.savedCameraType;
        if (cam != null && WastedDeath.mc.field_1690 != null) {
            WastedDeath.mc.field_1690.method_31043(cam);
        }
        this.savedCameraType = null;
    }

    @Override
    protected void onEnable() {
        WastedState.stop();
        this.respawnPending = false;
        this.respawnWaitUntilMs = 0L;
        this.pendingDeathScreen = null;
    }

    @Override
    protected void onDisable() {
        WastedState.stop();
        this.restoreCamera();
        this.respawnPending = false;
        this.respawnWaitUntilMs = 0L;
        this.pendingDeathScreen = null;
    }

    public void renderOverlay(class_332 graphics) {
        float alpha = WastedState.textAlpha();
        if (alpha <= 0.01f || this.label.is(LABEL_NONE)) {
            return;
        }
        String text = this.label.is(LABEL_RU) ? LABEL_RU : LABEL_WASTED;
        float screenW = graphics.method_51421();
        float screenH = graphics.method_51443();
        float pop = Math.min(1.0f, alpha * 1.15f);
        float size = 34.0f * (1.06f - 0.06f * pop);
        float textWidth = Fonts.sf_bold.getWidth(text, size);
        float centerY = screenH * 0.5f;
        float bandHeight = size * 1.5f;
        float bandY = centerY - bandHeight * 0.5f;
        RenderUtil.Render2D.rect(0.0f, bandY, screenW, bandHeight, WastedDeath.color(24, 24, 24, 150, alpha));
        RenderUtil.Render2D.rect(0.0f, bandY - 1.2f, screenW, 1.2f, WastedDeath.color(0, 0, 0, 90, alpha));
        RenderUtil.Render2D.rect(0.0f, bandY + bandHeight, screenW, 1.2f, WastedDeath.color(0, 0, 0, 90, alpha));
        int base = this.textColor.getValue();
        int red = base >> 16 & 0xFF;
        int green = base >> 8 & 0xFF;
        int blue = base & 0xFF;
        float textY = centerY - size * 0.5f - 5.0f;
        Fonts.sf_bold.draw(text, (screenW - textWidth) * 0.5f + 1.5f, textY + 1.5f, size, WastedDeath.color(0, 0, 0, 120, alpha));
        Fonts.sf_bold.draw(text, (screenW - textWidth) * 0.5f, textY, size, WastedDeath.color(red, green, blue, 255, alpha));
    }

    private static float ease() {
        float t = class_3532.method_15363((float)((float)WastedState.elapsedMs() / 2200.0f), (float)0.0f, (float)1.0f);
        return t * t * (3.0f - 2.0f * t);
    }

    private static int color(int r, int g, int b, int a, float mult) {
        int fa = class_3532.method_15340((int)Math.round((float)a * mult), (int)0, (int)255);
        return fa <= 0 ? 0 : new Color(r, g, b, fa).getRGB();
    }

    public static boolean shouldSuppressVanillaScreen(class_437 screen) {
        return screen instanceof class_418 && WastedDeath.blocksDeathScreen();
    }
}


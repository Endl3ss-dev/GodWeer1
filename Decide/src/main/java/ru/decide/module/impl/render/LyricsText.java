/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.module.impl.render;

import java.awt.Color;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.manager.event_impl.EventRender3D;
import ru.decide.manager.event_impl.EventTick;
import ru.decide.manager.event_impl.WorldLoadEvent;
import ru.decide.manager.events.orbit.EventHandler;
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.module.api.ModuleInfo;
import ru.decide.module.api.settings.impl.BooleanSetting;
import ru.decide.module.api.settings.impl.DelimiterSetting;
import ru.decide.module.api.settings.impl.ModeSetting;
import ru.decide.module.api.settings.impl.SliderSetting;
import ru.decide.module.impl.render.lyrics.LyricParticles;
import ru.decide.theme.ThemeColor;
import ru.decide.utils.animation.Animation;
import ru.decide.utils.animation.Easings;
import ru.decide.utils.colors.ColorUtil;
import ru.decide.utils.media.MediaLog;
import ru.decide.utils.media.MediaPlayer;
import ru.decide.utils.render.lyrics.LyricText3D;

@ModuleInfo(name="Lyrics Text", desc="\u0421\u0442\u0440\u043e\u043a\u0438 \u0438\u0433\u0440\u0430\u044e\u0449\u0435\u0439 \u043f\u0435\u0441\u043d\u0438 \u0432\u0438\u0441\u044f\u0442 \u0432 \u043c\u0438\u0440\u0435 \u043f\u0435\u0440\u0435\u0434 \u0432\u0430\u043c\u0438", category=Category.VISUALS)
@Environment(value=EnvType.CLIENT)
public class LyricsText
extends Module {
    private static final String EXIT_FADE = "\u0417\u0430\u0442\u0443\u0445\u0430\u043d\u0438\u0435";
    private static final String EXIT_FALL = "\u0420\u0430\u0441\u043f\u0430\u0434";
    private static final String EXIT_SMOOTH = "\u041f\u043b\u0430\u0432\u043d\u044b\u0439";
    private static final String EXIT_TYPEWRITER = "\u041f\u0435\u0447\u0430\u0442\u043d\u0430\u044f \u043c\u0430\u0448\u0438\u043d\u043a\u0430";
    private static final String EXIT_SCALE = "\u041c\u0430\u0441\u0448\u0442\u0430\u0431\u0438\u0440\u043e\u0432\u0430\u043d\u0438\u0435";
    private static final String EXIT_SLIDE = "\u0421\u043a\u043e\u043b\u044c\u0436\u0435\u043d\u0438\u0435";
    private static final String ANIM_PROGRESSIVE = "\u041f\u043e\u0441\u0442\u0435\u043f\u0435\u043d\u043d\u043e";
    private static final String ANIM_SMOOTH = "\u041f\u043b\u0430\u0432\u043d\u044b\u0439";
    private static final String ANIM_TYPEWRITER = "\u041f\u0435\u0447\u0430\u0442\u043d\u0430\u044f \u043c\u0430\u0448\u0438\u043d\u043a\u0430";
    private static final String ANIM_SCALE = "\u041c\u0430\u0441\u0448\u0442\u0430\u0431\u0438\u0440\u043e\u0432\u0430\u043d\u0438\u0435";
    private static final String ANIM_SLIDE = "\u0421\u043a\u043e\u043b\u044c\u0436\u0435\u043d\u0438\u0435";
    private static final String ANIM_FADE = "\u041c\u044f\u0433\u043a\u043e\u0435 \u043f\u043e\u044f\u0432\u043b\u0435\u043d\u0438\u0435";
    private static final String COLOR_THEME = "\u0422\u0435\u043c\u0430";
    private static final String COLOR_GRADIENT = "\u0413\u0440\u0430\u0434\u0438\u0435\u043d\u0442";
    private static final String COLOR_WHITE = "\u0411\u0435\u043b\u044b\u0439";
    private static final String FONT_MANASCO = "Manasco";
    private static final String FONT_SF_PRO = "SF Pro";
    private static final String FONT_BOLD = "Bold";
    private static final String FONT_MONTSERRAT = "Montserrat";
    private String vocalProcess;
    private boolean vocalRunning;
    private final Animation fade = new Animation();
    private final LyricParticles lyrics = new LyricParticles();
    public DelimiterSetting textSeparator = new DelimiterSetting(this, "\u0422\u0435\u043a\u0441\u0442");
    public ModeSetting mode = new ModeSetting((Module)this, "\u041f\u043e\u043a\u0430\u0437\u044b\u0432\u0430\u0442\u044c", "\u0421\u0442\u0440\u043e\u043a\u0438", "\u0421\u043b\u043e\u0432\u0430");
    public ModeSetting layout = new ModeSetting((Module)this, "\u0420\u0430\u0441\u043a\u043b\u0430\u0434\u043a\u0430", "\u0421\u0442\u0430\u043d\u0434\u0430\u0440\u0442", "\u0412\u0440\u0430\u0437\u0431\u0440\u043e\u0441", "360").setVisible(() -> this.mode.is("\u0421\u043b\u043e\u0432\u0430"));
    public ModeSetting font = new ModeSetting((Module)this, "\u0428\u0440\u0438\u0444\u0442", "Manasco", "SF Pro", "Bold", "Montserrat");
    public DelimiterSetting animationsSeparator = new DelimiterSetting(this, "\u0410\u043d\u0438\u043c\u0430\u0446\u0438\u0438");
    public ModeSetting intro = new ModeSetting((Module)this, "\u041f\u043e\u044f\u0432\u043b\u0435\u043d\u0438\u0435", "\u041f\u043e\u0441\u0442\u0435\u043f\u0435\u043d\u043d\u043e", "\u041f\u043b\u0430\u0432\u043d\u044b\u0439", "\u041f\u0435\u0447\u0430\u0442\u043d\u0430\u044f \u043c\u0430\u0448\u0438\u043d\u043a\u0430", "\u041c\u0430\u0441\u0448\u0442\u0430\u0431\u0438\u0440\u043e\u0432\u0430\u043d\u0438\u0435", "\u0421\u043a\u043e\u043b\u044c\u0436\u0435\u043d\u0438\u0435", "\u041c\u044f\u0433\u043a\u043e\u0435 \u043f\u043e\u044f\u0432\u043b\u0435\u043d\u0438\u0435");
    public ModeSetting exit = new ModeSetting((Module)this, "\u0423\u0445\u043e\u0434", "\u0417\u0430\u0442\u0443\u0445\u0430\u043d\u0438\u0435", "\u0420\u0430\u0441\u043f\u0430\u0434", "\u041f\u043b\u0430\u0432\u043d\u044b\u0439", "\u041f\u0435\u0447\u0430\u0442\u043d\u0430\u044f \u043c\u0430\u0448\u0438\u043d\u043a\u0430", "\u041c\u0430\u0441\u0448\u0442\u0430\u0431\u0438\u0440\u043e\u0432\u0430\u043d\u0438\u0435", "\u0421\u043a\u043e\u043b\u044c\u0436\u0435\u043d\u0438\u0435");
    public ModeSetting colorMode = new ModeSetting((Module)this, "\u0426\u0432\u0435\u0442", "\u0422\u0435\u043c\u0430", "\u0413\u0440\u0430\u0434\u0438\u0435\u043d\u0442", "\u0411\u0435\u043b\u044b\u0439");
    public SliderSetting size = new SliderSetting(this, "\u0420\u0430\u0437\u043c\u0435\u0440 \u0441\u0442\u0440\u043e\u043a\u0438", 0.3f, 0.05f, 2.0f, 0.01f);
    public SliderSetting opacity = new SliderSetting(this, "\u041f\u0440\u043e\u0437\u0440\u0430\u0447\u043d\u043e\u0441\u0442\u044c", 1.0f, 0.1f, 1.0f, 0.05f);
    public BooleanSetting throughWalls = new BooleanSetting((Module)this, "\u0421\u043a\u0432\u043e\u0437\u044c \u0441\u0442\u0435\u043d\u044b", false);
    public DelimiterSetting styleSeparator = new DelimiterSetting(this, "\u041a\u0430\u0441\u0442\u043e\u043c\u0438\u0437\u0430\u0446\u0438\u044f");
    public SliderSetting glow = new SliderSetting(this, "\u0421\u0432\u0435\u0447\u0435\u043d\u0438\u0435", 1.0f, 0.0f, 3.0f, 0.05f);
    public DelimiterSetting placementSeparator = new DelimiterSetting(this, "\u0420\u0430\u0437\u043c\u0435\u0449\u0435\u043d\u0438\u0435");
    public SliderSetting radius = new SliderSetting(this, "\u0420\u0430\u0434\u0438\u0443\u0441", 3.5f, 1.0f, 16.0f, 0.1f);
    public SliderSetting height = new SliderSetting(this, "\u0412\u044b\u0441\u043e\u0442\u0430", 1.8f, -2.0f, 8.0f, 0.1f);
    public SliderSetting limit = new SliderSetting(this, "\u041a\u0443\u0441\u043a\u043e\u0432", 3.0f, 1.0f, 8.0f, 1.0f);
    public SliderSetting sync = new SliderSetting(this, "\u0421\u043c\u0435\u0449\u0435\u043d\u0438\u0435", 0.0f, -1500.0f, 1500.0f, 10.0f);

    @Override
    protected void onEnable() {
        this.lyrics.reset();
        this.fade.set(0.0);
        this.fade.run(1.0, 0.8, Easings.SINE_OUT, true);
    }

    @Override
    protected void onDisable() {
        this.lyrics.reset();
        LyricText3D.invalidate();
        MediaPlayer.stopVocalDetect();
        this.vocalProcess = null;
        this.vocalRunning = false;
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        this.lyrics.reset();
    }

    @EventHandler
    public void onTick(EventTick event) {
        MediaPlayer.tick();
        this.syncVocalDetect();
        this.fade.update();
    }

    @EventHandler
    public void onRender3D(EventRender3D event) {
        if (LyricsText.mc.field_1724 == null || LyricsText.mc.field_1687 == null || LyricsText.mc.field_1773 == null) {
            return;
        }
        if (this.fade.get() <= 0.0f) {
            return;
        }
        this.lyrics.render(event, mc, this.options());
    }

    private void syncVocalDetect() {
        if (!MediaPlayer.isPlaying()) {
            return;
        }
        String process = MediaPlayer.vocalProcess();
        if (process == null || process.isEmpty()) {
            return;
        }
        if (process.equals(this.vocalProcess) && this.vocalRunning) {
            return;
        }
        this.vocalRunning = MediaPlayer.startVocalDetect(process);
        this.vocalProcess = process;
        MediaLog.note("vocal", "detect for '" + process + "' -> " + this.vocalRunning);
    }

    private LyricParticles.Options options() {
        float alpha = this.fade.get();
        int[] tint = this.textColor();
        return new LyricParticles.Options(this.mode.is("\u0421\u043b\u043e\u0432\u0430"), this.layout.getValue(), this.fontId(), this.size.getValue().floatValue(), this.opacity.getValue().floatValue() * alpha, this.throughWalls.getValue(), this.glow.getValue().floatValue() * alpha, this.radius.getValue().floatValue(), this.height.getValue().floatValue(), Math.round(this.limit.getValue().floatValue()), Math.round(this.sync.getValue().floatValue()), this.exitId(), this.animationId(), tint[0], tint[1] != 0);
    }

    private String fontId() {
        if (this.font.is(FONT_SF_PRO)) {
            return "sf_pro_medium";
        }
        if (this.font.is(FONT_BOLD)) {
            return "sf_bold";
        }
        if (this.font.is(FONT_MONTSERRAT)) {
            return "montserrat";
        }
        return "manasco";
    }

    private int animationId() {
        String value = this.intro.getValue();
        if ("\u041f\u043b\u0430\u0432\u043d\u044b\u0439".equals(value)) {
            return 1;
        }
        if ("\u041f\u0435\u0447\u0430\u0442\u043d\u0430\u044f \u043c\u0430\u0448\u0438\u043d\u043a\u0430".equals(value)) {
            return 2;
        }
        if ("\u041c\u0430\u0441\u0448\u0442\u0430\u0431\u0438\u0440\u043e\u0432\u0430\u043d\u0438\u0435".equals(value)) {
            return 3;
        }
        if ("\u0421\u043a\u043e\u043b\u044c\u0436\u0435\u043d\u0438\u0435".equals(value)) {
            return 4;
        }
        if (ANIM_FADE.equals(value)) {
            return 5;
        }
        return 0;
    }

    private int exitId() {
        String value = this.exit.getValue();
        if (EXIT_FALL.equals(value)) {
            return 1;
        }
        if ("\u041f\u043b\u0430\u0432\u043d\u044b\u0439".equals(value)) {
            return 2;
        }
        if ("\u041f\u0435\u0447\u0430\u0442\u043d\u0430\u044f \u043c\u0430\u0448\u0438\u043d\u043a\u0430".equals(value)) {
            return 3;
        }
        if ("\u041c\u0430\u0441\u0448\u0442\u0430\u0431\u0438\u0440\u043e\u0432\u0430\u043d\u0438\u0435".equals(value)) {
            return 4;
        }
        if ("\u0421\u043a\u043e\u043b\u044c\u0436\u0435\u043d\u0438\u0435".equals(value)) {
            return 5;
        }
        return 0;
    }

    private int[] textColor() {
        if (this.colorMode.is(COLOR_WHITE)) {
            return new int[]{0xFFFFFF, 1};
        }
        int base = ThemeColor.getVisualColor();
        if (this.colorMode.is(COLOR_GRADIENT)) {
            int shifted = LyricsText.shiftHue(base, 45.0f);
            float ratio = (float)(Math.sin((double)System.currentTimeMillis() / 450.0) * 0.5 + 0.5);
            return new int[]{ColorUtil.interpolateColor(base, shifted, ratio, true), 1};
        }
        return new int[]{base, 1};
    }

    private static int shiftHue(int rgb, float degrees) {
        float[] hsb = Color.RGBtoHSB(rgb >> 16 & 0xFF, rgb >> 8 & 0xFF, rgb & 0xFF, null);
        int shifted = Color.HSBtoRGB((hsb[0] + degrees / 360.0f) % 1.0f, hsb[1], hsb[2]);
        return shifted & 0xFFFFFF;
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1922
 *  net.minecraft.class_2338
 *  net.minecraft.class_2338$class_2339
 *  net.minecraft.class_2350$class_2351
 *  net.minecraft.class_243
 *  net.minecraft.class_265
 *  net.minecraft.class_2680
 *  net.minecraft.class_310
 *  net.minecraft.class_3532
 *  net.minecraft.class_4184
 *  net.minecraft.class_638
 *  net.minecraft.class_746
 */
package ru.decide.module.impl.render.lyrics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1922;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_243;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_4184;
import net.minecraft.class_638;
import net.minecraft.class_746;
import ru.decide.manager.event_impl.EventRender3D;
import ru.decide.utils.media.LyricLine;
import ru.decide.utils.media.LyricWord;
import ru.decide.utils.media.Lyrics;
import ru.decide.utils.media.MediaPlayer;
import ru.decide.utils.render.lyrics.LyricText3D;
import ru.decide.utils.render.lyrics.LyricTextGeometry;

@Environment(value=EnvType.CLIENT)
public final class LyricParticles {
    public static final String MODE_LINES = "\u0421\u0442\u0440\u043e\u043a\u0438";
    public static final String MODE_WORDS = "\u0421\u043b\u043e\u0432\u0430";
    public static final String LAYOUT_ARC = "\u0421\u0442\u0430\u043d\u0434\u0430\u0440\u0442";
    public static final String LAYOUT_SCATTER = "\u0412\u0440\u0430\u0437\u0431\u0440\u043e\u0441";
    public static final String LAYOUT_CIRCLE = "360";
    public static final int ANIM_PROGRESSIVE = 0;
    public static final int ANIM_SMOOTH = 1;
    public static final int ANIM_TYPEWRITER = 2;
    public static final int ANIM_SCALE = 3;
    public static final int ANIM_SLIDE = 4;
    public static final int ANIM_FADE = 5;
    public static final int EXIT_FADE = 0;
    public static final int EXIT_SHATTER = 1;
    public static final int EXIT_SMOOTH = 2;
    public static final int EXIT_TYPEWRITER = 3;
    public static final int EXIT_SCALE = 4;
    public static final int EXIT_SLIDE = 5;
    private static final float RASTER = 32.0f;
    private static final int MAX_DEBRIS = 512;
    private static final int FALL_STEPS = 24;
    private static final float MIN_ALPHA = 0.004f;
    private static final double GROUND_CLEARANCE = 0.012;
    private static final float GRAVITY = 14.0f;
    private static final float BOUNCE = 0.34f;
    private static final float GROUND_DRAG = 0.55f;
    private static final float REST_SPEED = 1.1f;
    private static final float FALL_TIMEOUT = 9.0f;
    private static final float DEBRIS_FADE = 0.5f;
    private static final float SETTLE_SECONDS = 0.9f;
    private static final float SETTLE_RATE = 11.0f;
    private static final float HEAT_LEAD = 60.0f;
    private static final float HEAT_RISE_SHARE = 0.35f;
    private static final float HEAT_RISE_MIN = 90.0f;
    private static final float HEAT_RISE_MAX = 220.0f;
    private static final float HEAT_FALL_SHARE = 0.55f;
    private static final float HEAT_FALL_MIN = 140.0f;
    private static final float HEAT_FALL_MAX = 320.0f;
    private static final long HEAT_TAIL = 420L;
    private static final long LINE_TAIL = 7000L;
    private static final long WORD_TAIL = 140L;
    private static final long WORD_MIN_VISIBLE = 650L;
    private static final long WORD_MAX_VISIBLE = 2600L;
    private static final long SHATTER_GRACE = 1600L;
    private static final long REWIND_GRACE = 500L;
    private static final float FIT_MARGIN = 0.88f;
    private static final float CULL_FACTOR = 8.0f;
    private static final float LINE_STACK = 1.35f;
    private static final float GOLDEN_ANGLE = 137.5f;
    private static final float SCATTER_LIFT = 2.4f;
    private static final float SCATTER_NEAR = 0.7f;
    private static final float SCATTER_FAR = 1.45f;
    private static final long ANNOUNCE_VISIBLE = 2600L;
    private static final long ANNOUNCE_EXIT = 520L;
    private static final float ANNOUNCE_TIMING = 260.0f;
    private static final float[] LINE_BOUNDS = new float[]{140.0f, 300.0f, 110.0f, 340.0f, 180.0f, 380.0f, 110.0f, 340.0f};
    private static final float[] WORD_BOUNDS = new float[]{70.0f, 170.0f, 40.0f, 120.0f, 110.0f, 230.0f, 40.0f, 140.0f};
    private static final float[] WORD_SIDES = new float[]{-0.4f, 0.52f, -0.86f, 1.0f, -0.19f, 0.31f, -0.69f, 0.78f};
    private static final float[] WORD_LIFTS = new float[]{0.0f, 0.5f, -0.35f, 0.65f, 0.25f, -0.2f, 0.55f, -0.45f};
    private static final float[] WORD_REACH = new float[]{1.0f, 0.86f, 1.18f, 0.94f, 1.26f, 0.9f, 1.1f, 0.82f};
    private final List<Held> held = new ArrayList<Held>();
    private final List<Debris> debris = new ArrayList<Debris>();
    private final Map<Integer, LyricTextGeometry.Layout> glyphCache = new HashMap<Integer, LyricTextGeometry.Layout>();
    private final class_2338.class_2339 groundProbe = new class_2338.class_2339();
    private final Random random = new Random();
    private List<Fragment> fragments = Collections.emptyList();
    private float presence;
    private long lastFrameNanos;
    private int lastIndex = -1;
    private int spawnCount;
    private String lastTrack = "";
    private String announcedTrack = "";
    private Lyrics lastLyrics;
    private boolean lastWords;
    private boolean lastModeCaptured;
    private String lastFont;
    private int lastVocalFrames = -1;
    private long vocalFramesStampMs;

    public void render(EventRender3D event, class_310 mc, Options options) {
        if (mc.field_1724 == null || mc.field_1687 == null || event.getProjectionMatrix() == null) {
            return;
        }
        if (!MediaPlayer.init()) {
            return;
        }
        MediaPlayer.tick();
        if (!options.fontName().equals(this.lastFont)) {
            this.lastFont = options.fontName();
            for (Held piece : this.held) {
                piece.invalidate();
            }
        }
        MediaPlayer.setLyricsOffsetMillis(options.syncMillis());
        float delta = this.advance();
        this.presence = LyricParticles.approach(this.presence, MediaPlayer.isPlaying() ? 1.0f : 0.0f, delta);
        class_4184 camera = mc.field_1773.method_19418();
        if (camera == null) {
            return;
        }
        long time = MediaPlayer.getLyricsTimeMillis();
        float partialTicks = event.getTickDelta();
        this.collect(mc, camera, time, partialTicks, options);
        for (int index = this.held.size() - 1; index >= 0; --index) {
            boolean expired;
            Held piece = this.held.get(index);
            boolean bl = options.fall() ? piece.shattered || time >= piece.death + 1600L : (expired = time >= piece.death);
            if (!expired && time >= piece.spawn - 500L) continue;
            this.held.remove(index);
        }
        this.stepDebris(delta, options);
        if (this.held.isEmpty() && this.debris.isEmpty() || this.presence <= 0.004f) {
            return;
        }
        LyricText3D.begin(event.getMatrixStack().method_23760().method_23761(), event.getProjectionMatrix(), camera.method_71156(), 0.66f, 0.35f, options.glow(), 12.8f, options.tintRgb(), options.colored());
        LyricText3D.distanceRange(LyricTextGeometry.distanceRange(options.fontName()));
        class_243 cameraPos = camera.method_71156();
        for (Held piece : this.held) {
            this.paint(piece, time, cameraPos, options, mc);
        }
        this.paintDebris(cameraPos, options);
        LyricText3D.end(options.throughWalls());
    }

    public void reset() {
        this.held.clear();
        this.debris.clear();
        this.glyphCache.clear();
        this.fragments = Collections.emptyList();
        this.presence = 0.0f;
        this.lastFrameNanos = 0L;
        this.lastIndex = -1;
        this.lastTrack = "";
        this.announcedTrack = "";
        this.lastLyrics = null;
        this.lastModeCaptured = false;
        this.lastVocalFrames = -1;
    }

    private void collect(class_310 mc, class_4184 camera, long time, float partialTicks, Options options) {
        boolean fresh;
        String track = MediaPlayer.getTrack().display();
        Lyrics lyrics = MediaPlayer.getLyrics();
        boolean words = options.words();
        boolean bl = fresh = !track.equals(this.lastTrack) || lyrics != this.lastLyrics || !this.lastModeCaptured || this.lastWords != words;
        if (fresh) {
            this.lastTrack = track;
            this.lastLyrics = lyrics;
            this.lastWords = words;
            this.lastModeCaptured = true;
            this.lastIndex = -1;
            this.held.clear();
            List<Object> list = this.fragments = lyrics.isEmpty() || !lyrics.synced() ? Collections.emptyList() : LyricParticles.split(lyrics, words);
        }
        if (!track.equals(this.announcedTrack) && !track.isBlank() && MediaPlayer.getTrack().durationMillis() > 0L) {
            this.announcedTrack = track;
            this.held.add(this.announcement(mc, camera, track, time, partialTicks, options));
        }
        if (this.fragments.isEmpty()) {
            return;
        }
        if (this.lastIndex >= 0 && this.lastIndex < this.fragments.size() && time < this.fragments.get((int)this.lastIndex).startMillis - 500L) {
            this.held.clear();
            this.lastIndex = -1;
        }
        int alive = Math.max(1, options.limit());
        while (this.lastIndex + 1 < this.fragments.size() && time >= this.fragments.get((int)(this.lastIndex + 1)).startMillis) {
            ++this.lastIndex;
            Fragment candidate = this.fragments.get(this.lastIndex);
            Timing timing = LyricParticles.timing(candidate, this.handoff(this.lastIndex), words);
            if (time >= timing.death) continue;
            this.trim(time, alive);
            this.held.add(this.create(mc, camera, candidate, timing, words, partialTicks, options));
        }
    }

    private void trim(long time, int alive) {
        int live = 0;
        for (Held piece : this.held) {
            if (piece.fadeFrom <= time) continue;
            ++live;
        }
        while (live >= alive) {
            Held target = null;
            for (Held piece : this.held) {
                if (piece.fadeFrom <= time || target != null && piece.spawn >= target.spawn) continue;
                target = piece;
            }
            if (target == null) break;
            target.retire(time);
            --live;
        }
        while (this.held.size() > alive * 2) {
            this.held.remove(0);
        }
    }

    private Held announcement(class_310 mc, class_4184 camera, String title, long time, float partialTicks, Options options) {
        Fragment fragment = new Fragment(title, Collections.emptyList(), time, time + 2600L);
        Timing timing = new Timing(time, time + 2600L, time + 2600L + 520L, 260.0f, 260.0f, 260.0f, 260.0f);
        return this.create(mc, camera, fragment, timing, false, partialTicks, options);
    }

    private long handoff(int index) {
        if (index + 1 < this.fragments.size()) {
            return this.fragments.get((int)(index + 1)).startMillis;
        }
        Fragment last = this.fragments.get(index);
        return Math.max(last.endMillis, last.startMillis + 1L);
    }

    private float currentVocalGate() {
        float measured = MediaPlayer.vocalLevel();
        if (measured < 0.0f) {
            return 1.0f;
        }
        int frames = MediaPlayer.vocalFrames();
        long now = System.currentTimeMillis();
        if (frames != this.lastVocalFrames) {
            this.lastVocalFrames = frames;
            this.vocalFramesStampMs = now;
        } else if (now - this.vocalFramesStampMs > 1200L) {
            return 1.0f;
        }
        return Math.min(1.0f, 0.45f + measured * 1.2f);
    }

    private Held create(class_310 mc, class_4184 camera, Fragment fragment, Timing timing, boolean words, float partialTicks, Options options) {
        float offset;
        int index = this.spawnCount++;
        int slot = Math.floorMod(index, WORD_SIDES.length);
        float lift = options.height();
        float reach = options.radius();
        if (!words) {
            int row = Math.floorMod(index, Math.max(1, options.limit()));
            offset = (slot & 1) == 0 ? -1.0f : 1.0f;
            lift += (float)(options.limit() - 1 - row) * options.size() * 1.35f;
        } else if (LAYOUT_CIRCLE.equals(options.layout())) {
            lift += WORD_LIFTS[slot];
            reach *= WORD_REACH[slot];
            offset = Float.NaN;
        } else if (LAYOUT_SCATTER.equals(options.layout())) {
            offset = (this.random.nextFloat() - 0.5f) * 2.0f;
            lift += (this.random.nextFloat() - 0.5f) * 2.4f;
            reach *= 0.7f + this.random.nextFloat() * 0.75000006f;
        } else {
            offset = WORD_SIDES[slot];
            lift += WORD_LIFTS[slot];
            reach *= WORD_REACH[slot];
        }
        offset = Float.isNaN(offset) ? (float)index * 137.5f % 360.0f : offset * this.sideRoom(options, fragment.text, reach);
        float radians = (float)Math.toRadians(camera.method_19330() + offset);
        float directionX = -((float)Math.sin(radians));
        float directionZ = (float)Math.cos(radians);
        class_746 player = mc.field_1724;
        if (player == null) {
            return new Held(fragment, timing, 0.0, 0.0, 0.0, 0.0f, 0.0f, 1.0f);
        }
        class_243 base = player.method_30950(partialTicks);
        return new Held(fragment, timing, base.field_1352 + (double)(directionX * reach), base.field_1351 + (double)lift, base.field_1350 + (double)(directionZ * reach), -directionZ, directionX, Math.max(0.5f, reach));
    }

    private void paint(Held piece, long time, class_243 camera, Options options, class_310 mc) {
        LyricTextGeometry.Layout layout;
        long life = Math.max(1L, piece.death - piece.spawn);
        float progress = LyricParticles.clamp((float)(time - piece.spawn) / (float)life, 0.0f, 1.0f);
        long activeLife = Math.max(1L, piece.fadeFrom - piece.spawn);
        float active = LyricParticles.clamp((float)activeLife / (float)life, 0.05f, 1.0f);
        float exitLife = Math.max(1.0f, piece.timing.outTime + piece.timing.outCascade);
        float exitT = LyricParticles.clamp((float)(time - piece.fadeFrom) / exitLife, 0.0f, 1.0f);
        Anim exit = LyricParticles.exitAnimation(options.exit(), exitT, piece.fragment.text.length());
        String text = piece.fragment.text;
        Anim anim = LyricParticles.animation(options.animation(), progress, life, active, text.length());
        int limit = Math.min(anim.visibleChars, exit.visibleChars);
        if (limit < text.length()) {
            text = text.substring(0, Math.max(0, limit));
        }
        if ((layout = LyricTextGeometry.layout(options.fontName(), text, 32.0f)).empty()) {
            return;
        }
        double dx = piece.x - camera.field_1352;
        double dy = piece.y - camera.field_1351;
        double dz = piece.z - camera.field_1350;
        float cull = options.radius() * 8.0f;
        double range = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (range > (double)cull) {
            return;
        }
        float lineHeight = layout.height();
        float lineWidth = Math.max(1.0f, layout.width());
        float fade = LyricParticles.clamp((float)(((double)cull - range) / (double)(cull * 0.2f)), 0.0f, 1.0f);
        float alpha = options.opacity() * fade * this.presence;
        if (alpha <= 0.004f) {
            return;
        }
        float scale = options.size() / lineHeight;
        float screenWidth = this.visibleWidth(piece.reach) * 0.88f;
        float worldWidth = lineWidth * scale;
        if (screenWidth > 0.0f && worldWidth > screenWidth) {
            scale *= screenWidth / worldWidth;
        }
        scale *= anim.scale * exit.scale;
        float tilt = (float)Math.toRadians(anim.tilt + exit.tilt);
        float cos = (float)Math.cos(tilt);
        float sin = (float)Math.sin(tilt);
        float rightX = piece.rightX * cos;
        float rightY = sin;
        float rightZ = piece.rightZ * cos;
        float upX = -piece.rightX * sin;
        float upY = cos;
        float upZ = -piece.rightZ * sin;
        LyricText3D.plane(piece.x, piece.y, piece.z, rightX, rightY, rightZ, upX, upY, upZ, scale, lineWidth * 0.5f, lineHeight * 0.5f);
        piece.measure(options.fontName());
        float vocalGate = this.currentVocalGate();
        Timing timing = piece.timing;
        float blurScale = lineHeight;
        float lift = 0.16f * lineHeight;
        if (options.fall() && time >= piece.shatterAt()) {
            if (!piece.shattered) {
                piece.shattered = true;
                piece.death = time;
                this.shatter(mc, piece, layout, scale, lineWidth, lineHeight);
            }
            return;
        }
        for (LyricTextGeometry.Quad quad : layout.quads()) {
            float center = (quad.x0() + quad.x1()) * 0.5f;
            float phase = LyricParticles.clamp(center / lineWidth, 0.0f, 1.0f);
            float appear = anim.nativeCascade ? LyricParticles.clamp(((float)time - ((float)piece.spawn + phase * timing.inCascade)) / timing.inTime, 0.0f, 1.0f) : 1.0f;
            float vanish = options.fall() || options.exit() != 0 ? 0.0f : LyricParticles.clamp(((float)time - ((float)piece.fadeFrom + phase * timing.outCascade)) / timing.outTime, 0.0f, 1.0f);
            float fadeIn = 1.0f - (1.0f - appear) * (1.0f - appear) * (1.0f - appear);
            float settled = this.settle(appear);
            float leaving = vanish * vanish;
            float visible = fadeIn * (1.0f - leaving);
            if (visible <= 0.004f) continue;
            float rest = 1.0f - this.presence;
            float heat = piece.heat(center, time) * vocalGate;
            float shift = 0.3f * lineHeight * (1.0f - settled) - 0.34f * lineHeight * leaving + 0.35f * lineHeight * rest + lift * heat * (1.0f - leaving) + (anim.shift + exit.shift) * lineHeight;
            float smear = (0.2f * (1.0f - appear) + 0.26f * vanish + 0.5f * rest) * blurScale;
            LyricText3D.glyph(layout, quad, 0.0f, shift, smear, heat, alpha * visible * anim.alpha * exit.alpha);
        }
    }

    private float settle(float value) {
        float t = value - 1.0f;
        return 1.0f + 2.1f * t * t * t + 1.1f * t * t;
    }

    private static Anim animation(int mode, float progress, long life, float active, int chars) {
        Anim anim = new Anim();
        float intro = Math.min(0.22f, 480.0f / (float)life);
        float typing = Math.min(0.35f, 650.0f / (float)life);
        switch (mode) {
            case 1: {
                float ease;
                if (!(progress < intro)) break;
                float t = progress / intro;
                anim.alpha = ease = LyricParticles.easeOutCubic(t);
                anim.shift = -0.35f * (1.0f - ease);
                anim.scale = 0.88f + 0.12f * LyricParticles.easeOutQuart(t);
                break;
            }
            case 2: {
                if (!(progress < typing)) break;
                float t = progress / typing;
                anim.visibleChars = Math.min(chars, Math.max(1, (int)Math.ceil((float)chars * t)));
                anim.alpha = LyricParticles.easeOutCubic(Math.min(1.0f, progress / Math.min(0.08f, 200.0f / (float)life)));
                break;
            }
            case 3: {
                float ease;
                if (!(progress < intro)) break;
                float t = progress / intro;
                anim.alpha = ease = LyricParticles.easeOutCubic(t);
                anim.scale = 0.65f + 0.35f * ease;
                anim.shift = -0.2f * (1.0f - ease);
                break;
            }
            case 4: {
                float ease;
                float sign;
                float f = sign = progress * 7.0f % 2.0f < 1.0f ? 1.0f : -1.0f;
                if (!(progress < intro)) break;
                float t = progress / intro;
                anim.alpha = ease = LyricParticles.easeOutCubic(t);
                anim.shift = -0.55f * (1.0f - ease);
                anim.scale = 0.9f + 0.1f * ease;
                anim.tilt = (1.0f - ease) * 4.0f * sign;
                break;
            }
            case 5: {
                float ease;
                if (!(progress < intro)) break;
                anim.alpha = ease = LyricParticles.easeOutCubic(progress / intro);
                anim.scale = 0.94f + 0.06f * ease;
                break;
            }
            default: {
                anim.nativeCascade = true;
                if (progress < active) {
                    float t = active > 0.0f ? progress / active : 1.0f;
                    anim.visibleChars = Math.min(chars, Math.max(1, (int)Math.ceil((float)chars * t)));
                    float startFade = Math.min(0.1f, 250.0f / (float)life);
                    anim.alpha = progress < startFade ? LyricParticles.easeOutCubic(progress / startFade) : 1.0f;
                    break;
                }
                anim.visibleChars = chars;
            }
        }
        anim.alpha = LyricParticles.clamp(anim.alpha, 0.0f, 1.0f);
        return anim;
    }

    private static Anim exitAnimation(int mode, float t, int chars) {
        Anim exit = new Anim();
        if (t <= 0.0f) {
            return exit;
        }
        switch (mode) {
            case 3: {
                exit.visibleChars = Math.max(0, (int)Math.ceil((float)chars * (1.0f - t)));
                exit.alpha = t > 0.85f ? 1.0f - LyricParticles.easeInCubic((t - 0.85f) / 0.15f) : 1.0f;
                break;
            }
            case 4: {
                float ease = LyricParticles.easeInCubic(t);
                exit.alpha = 1.0f - ease;
                exit.scale = 1.0f + 0.07f * t;
                exit.shift = 0.12f * ease;
                break;
            }
            case 5: {
                float ease = LyricParticles.easeInCubic(t);
                float sign = t * 7.0f % 2.0f < 1.0f ? 1.0f : -1.0f;
                exit.alpha = 1.0f - ease;
                exit.shift = 0.18f * ease;
                exit.scale = 1.0f - 0.04f * t;
                exit.tilt = t * 2.0f * sign;
                break;
            }
            case 2: {
                float ease = LyricParticles.easeInCubic(t);
                exit.alpha = 1.0f - ease;
                exit.shift = 0.2f * ease;
                exit.scale = 1.0f - 0.04f * t;
                break;
            }
        }
        exit.alpha = LyricParticles.clamp(exit.alpha, 0.0f, 1.0f);
        return exit;
    }

    private static float easeOutCubic(float t) {
        return 1.0f - (float)Math.pow(1.0f - t, 3.0);
    }

    private static float easeInCubic(float t) {
        return (float)Math.pow(t, 3.0);
    }

    private static float easeOutQuart(float t) {
        return 1.0f - (float)Math.pow(1.0f - t, 4.0);
    }

    private void shatter(class_310 mc, Held piece, LyricTextGeometry.Layout layout, float scale, float lineWidth, float lineHeight) {
        class_638 world = mc.field_1687;
        if (world == null) {
            return;
        }
        float outX = -piece.rightZ;
        float outZ = piece.rightX;
        int count = layout.quads().size();
        float step = Math.min(0.06f, 2.2f / (float)Math.max(1, count - 1));
        for (int order = 0; order < count; ++order) {
            if (this.debris.size() >= 512) {
                return;
            }
            LyricTextGeometry.Quad quad = layout.quads().get(order);
            float centerX = (quad.x0() + quad.x1()) * 0.5f;
            float centerY = (quad.y0() + quad.y1()) * 0.5f;
            float localX = centerX - lineWidth * 0.5f;
            float localY = lineHeight * 0.5f - centerY;
            double worldX = piece.x + (double)(piece.rightX * localX * scale);
            double worldY = piece.y + (double)(localY * scale);
            double worldZ = piece.z + (double)(piece.rightZ * localX * scale);
            float sideways = (this.random.nextFloat() - 0.5f) * 0.55f;
            float outward = (0.25f + this.random.nextFloat() * 0.6f) * 0.9f;
            float peel = 3.2f * (0.7f + this.random.nextFloat() * 0.6f) * (this.random.nextBoolean() ? 1.0f : -1.0f);
            this.debris.add(new Debris(quad.codePoint(), worldX, worldY, worldZ, piece.rightX * sideways + outX * outward, -0.05f + this.random.nextFloat() * 0.45f, piece.rightZ * sideways + outZ * outward, piece.rightX, piece.rightZ, peel, (quad.y1() - quad.y0()) * 0.5f * scale, this.groundBelow(world, worldX, worldY, worldZ), scale, (float)order * step + this.random.nextFloat() * step * 0.3f));
        }
    }

    private double groundBelow(class_638 world, double x, double y, double z) {
        int startY;
        int blockX = class_3532.method_15357((double)x);
        int blockZ = class_3532.method_15357((double)z);
        for (int blockY = startY = class_3532.method_15357((double)y); blockY >= startY - 24; --blockY) {
            double top;
            class_265 shape;
            this.groundProbe.method_10103(blockX, blockY, blockZ);
            class_2680 state = world.method_8320((class_2338)this.groundProbe);
            if (state.method_26215() || (shape = state.method_26220((class_1922)world, (class_2338)this.groundProbe)).method_1110() || !((top = (double)blockY + shape.method_1105(class_2350.class_2351.field_11052)) <= y)) continue;
            return top;
        }
        return Double.NEGATIVE_INFINITY;
    }

    private void stepDebris(float delta, Options options) {
        if (this.debris.isEmpty()) {
            return;
        }
        if (!options.fall()) {
            this.debris.clear();
            return;
        }
        for (int index = this.debris.size() - 1; index >= 0; --index) {
            Debris piece = this.debris.get(index);
            if (piece.delay > 0.0f) {
                piece.delay -= delta;
                float ready = LyricParticles.clamp(1.0f - piece.delay / piece.delayFull, 0.0f, 1.0f);
                piece.spin = 0.13f * ready * ready * Math.signum(piece.spinRate);
                continue;
            }
            piece.age += delta;
            if (piece.grounded) {
                piece.rest += delta;
                piece.spin += (piece.spinTarget - piece.spin) * LyricParticles.clamp(11.0f * delta, 0.0f, 1.0f);
                piece.y = piece.ground + piece.restHeight();
            } else {
                piece.velocityY -= 14.0f * delta;
                piece.x += (double)(piece.velocityX * delta);
                piece.y += (double)(piece.velocityY * delta);
                piece.z += (double)(piece.velocityZ * delta);
                piece.spin += piece.spinRate * delta;
                double floor = piece.ground + piece.restHeight();
                if (!Double.isInfinite(piece.ground) && piece.y <= floor) {
                    piece.y = floor;
                    if (-piece.velocityY <= 1.1f) {
                        piece.grounded = true;
                        piece.velocityX = 0.0f;
                        piece.velocityY = 0.0f;
                        piece.velocityZ = 0.0f;
                        piece.spinRate = 0.0f;
                        piece.spinTarget = LyricParticles.flatAngle(piece.spin);
                    } else {
                        piece.velocityY = -piece.velocityY * 0.34f;
                        piece.velocityX *= 0.55f;
                        piece.velocityZ *= 0.55f;
                        piece.spinRate = 0.0f;
                    }
                }
            }
            if (this.debrisFade(piece) <= 0.004f || !piece.grounded && !(piece.age > 9.0f)) continue;
            this.debris.remove(index);
        }
    }

    private float debrisFade(Debris piece) {
        float settled = piece.grounded ? LyricParticles.clamp(1.0f - (piece.rest - 0.9f) / 0.5f, 0.0f, 1.0f) : 1.0f;
        float timeout = piece.grounded ? 1.0f : LyricParticles.clamp((9.0f - piece.age) / 0.5f, 0.0f, 1.0f);
        return Math.min(settled, timeout);
    }

    private void paintDebris(class_243 camera, Options options) {
        if (this.debris.isEmpty()) {
            return;
        }
        this.glyphCache.clear();
        float cull = options.radius() * 8.0f;
        for (Debris piece : this.debris) {
            float alpha;
            double dz;
            double dy;
            double dx;
            double range;
            float fade = this.debrisFade(piece);
            if (fade <= 0.004f || (range = Math.sqrt((dx = piece.x - camera.field_1352) * dx + (dy = piece.y - camera.field_1351) * dy + (dz = piece.z - camera.field_1350) * dz)) > (double)cull || (alpha = options.opacity() * this.presence * fade * LyricParticles.clamp((float)(((double)cull - range) / (double)(cull * 0.2f)), 0.0f, 1.0f)) <= 0.004f) continue;
            LyricTextGeometry.Layout layout = this.glyphCache.get(piece.codePoint);
            if (layout == null) {
                layout = LyricTextGeometry.layout(options.fontName(), new String(Character.toChars(piece.codePoint)), 32.0f);
                this.glyphCache.put(piece.codePoint, layout);
            }
            if (layout.empty()) continue;
            LyricTextGeometry.Quad quad = layout.quads().get(0);
            float cos = (float)Math.cos(piece.spin);
            float sin = (float)Math.sin(piece.spin);
            LyricText3D.plane(piece.x, piece.y, piece.z, piece.rightX, 0.0f, piece.rightZ, -piece.rightZ * sin, cos, piece.rightX * sin, piece.scale, (quad.x0() + quad.x1()) * 0.5f, (quad.y0() + quad.y1()) * 0.5f);
            LyricText3D.glyph(layout, quad, 0.0f, 0.0f, 0.0f, 0.0f, alpha);
        }
    }

    private float halfFov() {
        class_310 mc = class_310.method_1551();
        if (mc == null || mc.method_22683() == null) {
            return 0.0f;
        }
        int width = mc.method_22683().method_4489();
        int height = mc.method_22683().method_4506();
        if (width <= 0 || height <= 0) {
            return 0.0f;
        }
        double vertical = Math.toRadians(Math.max(30.0, (double)((Integer)mc.field_1690.method_41808().method_41753()).intValue()));
        double aspect = (double)width / (double)height;
        return (float)Math.atan(Math.tan(vertical * 0.5) * aspect);
    }

    private float visibleWidth(double distance) {
        float half = this.halfFov();
        return half <= 0.0f ? 0.0f : (float)(2.0 * distance * Math.tan(half));
    }

    private float sideRoom(Options options, String text, float reach) {
        float bound = (float)Math.toDegrees(this.halfFov()) * 0.88f;
        if (bound <= 0.0f || reach <= 0.0f) {
            return 0.0f;
        }
        LyricTextGeometry.Layout layout = LyricTextGeometry.layout(options.fontName(), text, 32.0f);
        if (layout.empty()) {
            return 0.0f;
        }
        float worldWidth = layout.width() * (options.size() / layout.height());
        float visible = this.visibleWidth(reach) * 0.88f;
        if (visible > 0.0f && worldWidth > visible) {
            worldWidth = visible;
        }
        float half = (float)Math.toDegrees(Math.atan(worldWidth * 0.5f / reach));
        return Math.max(0.0f, bound - half);
    }

    private float advance() {
        long now = System.nanoTime();
        float delta = this.lastFrameNanos == 0L ? 0.0f : LyricParticles.clamp((float)(now - this.lastFrameNanos) / 1.0E9f, 0.0f, 0.25f);
        this.lastFrameNanos = now;
        return delta;
    }

    private static float flatAngle(float spin) {
        float quarter = 1.5707964f;
        int steps = Math.round((spin - quarter) / (float)Math.PI);
        return quarter + (float)steps * (float)Math.PI;
    }

    private static List<Fragment> split(Lyrics lyrics, boolean words) {
        ArrayList<Fragment> result = new ArrayList<Fragment>();
        for (LyricLine line : lyrics.lines()) {
            int take;
            String text = line.text();
            if (text.isBlank()) continue;
            List<LyricWord> source = line.words();
            if (!words || source.isEmpty()) {
                result.add(new Fragment(text, source, line.startMillis(), line.endMillis()));
                continue;
            }
            for (int index = 0; index < source.size(); index += take) {
                take = index + 1 < source.size() && source.get(index).text().length() <= 4 ? 2 : 1;
                LyricWord first = source.get(index);
                LyricWord last = source.get(index + take - 1);
                ArrayList<LyricWord> chunk = new ArrayList<LyricWord>(take);
                for (int offset = 0; offset < take; ++offset) {
                    LyricWord word = source.get(index + offset);
                    chunk.add(new LyricWord(word.startMillis(), word.endMillis(), word.text(), word.begin() - first.begin(), word.end() - first.begin()));
                }
                result.add(new Fragment(text.substring(first.begin(), last.end()), List.copyOf(chunk), first.startMillis(), last.endMillis()));
            }
        }
        return List.copyOf(result);
    }

    private static Timing timing(Fragment fragment, long handoff, boolean words) {
        float[] bounds = words ? WORD_BOUNDS : LINE_BOUNDS;
        float span = Math.max(1.0f, (float)(fragment.endMillis - fragment.startMillis));
        float inTime = LyricParticles.clamp(span * 0.22f, bounds[0], bounds[1]);
        float inCascade = LyricParticles.clamp(span * 0.3f, bounds[2], bounds[3]);
        float outTime = LyricParticles.clamp(span * 0.24f, bounds[4], bounds[5]);
        float outCascade = LyricParticles.clamp(span * 0.26f, bounds[6], bounds[7]);
        long spawn = fragment.startMillis;
        int exit = Math.round(outTime + outCascade);
        long entrance = spawn + (long)Math.round(inTime + inCascade);
        long visibleEnd = words ? Math.min(Math.max(fragment.endMillis + 140L, spawn + 650L), spawn + 2600L) : Math.min(handoff - (long)exit, spawn + 7000L);
        visibleEnd = Math.max(visibleEnd, entrance);
        return new Timing(spawn, visibleEnd, visibleEnd + (long)exit, inTime, inCascade, outTime, outCascade);
    }

    private static float clamp(float value, float low, float high) {
        return value < low ? low : (value > high ? high : value);
    }

    private static float approach(float current, float target, float delta) {
        if (delta <= 0.0f) {
            return current;
        }
        return current + (target - current) * (1.0f - (float)Math.exp(-delta * 1000.0f / 420.0f));
    }

    @Environment(value=EnvType.CLIENT)
    public record Options(boolean words, String layout, String fontName, float size, float opacity, boolean throughWalls, float glow, float radius, float height, int limit, long syncMillis, int exit, int animation, int tintRgb, boolean colored) {
        public boolean fall() {
            return this.exit == 1;
        }
    }

    @Environment(value=EnvType.CLIENT)
    private static final class Held {
        final Fragment fragment;
        final Timing timing;
        final double x;
        final double y;
        final double z;
        final float rightX;
        final float rightZ;
        final float reach;
        final long spawn;
        long fadeFrom;
        long death;
        boolean shattered;
        private float[] wordStart;
        private float[] wordEnd;
        private float[] wordRise;
        private float[] wordFall;
        private String measured;

        Held(Fragment fragment, Timing timing, double x, double y, double z, float rightX, float rightZ, float reach) {
            this.fragment = fragment;
            this.timing = timing;
            this.x = x;
            this.y = y;
            this.z = z;
            this.rightX = rightX;
            this.rightZ = rightZ;
            this.reach = reach;
            this.spawn = timing.spawn;
            this.fadeFrom = timing.visibleEnd;
            this.death = timing.death;
        }

        long shatterAt() {
            long sung = this.fragment.endMillis + 420L;
            return Math.max(this.fadeFrom, Math.min(sung, this.fadeFrom + 1600L));
        }

        void invalidate() {
            this.measured = null;
        }

        void retire(long time) {
            long from = Math.max(this.spawn, time);
            if (from >= this.fadeFrom) {
                return;
            }
            this.fadeFrom = from;
            this.death = from + (long)Math.round(this.timing.outTime + this.timing.outCascade);
        }

        void measure(String fontName) {
            if (fontName.equals(this.measured)) {
                return;
            }
            this.measured = fontName;
            List<LyricWord> words = this.fragment.words;
            int count = words.size();
            if (count == 0) {
                return;
            }
            float[] starts = new float[count];
            float[] ends = new float[count];
            float[] rises = new float[count];
            float[] falls = new float[count];
            String text = this.fragment.text;
            for (int index = 0; index < count; ++index) {
                LyricWord word = words.get(index);
                starts[index] = LyricTextGeometry.width(fontName, text.substring(0, word.begin()), 32.0f);
                ends[index] = LyricTextGeometry.width(fontName, text.substring(0, word.end()), 32.0f);
                float span = Math.max(1.0f, (float)word.durationMillis());
                rises[index] = class_3532.method_15363((float)(span * 0.35f), (float)90.0f, (float)220.0f);
                falls[index] = class_3532.method_15363((float)(span * 0.55f), (float)140.0f, (float)320.0f);
            }
            this.wordStart = starts;
            this.wordEnd = ends;
            this.wordRise = rises;
            this.wordFall = falls;
        }

        float heat(float center, long time) {
            float[] starts = this.wordStart;
            float[] ends = this.wordEnd;
            float[] rises = this.wordRise;
            float[] falls = this.wordFall;
            if (starts == null || ends == null || rises == null || falls == null) {
                return 0.0f;
            }
            List<LyricWord> words = this.fragment.words;
            for (int index = 0; index < words.size(); ++index) {
                if (center < starts[index] || center > ends[index]) continue;
                LyricWord word = words.get(index);
                float up = class_3532.method_15363((float)(((float)time - ((float)word.startMillis() - 60.0f)) / rises[index]), (float)0.0f, (float)1.0f);
                float down = class_3532.method_15363((float)((float)(time - word.endMillis()) / falls[index]), (float)0.0f, (float)1.0f);
                return Held.ease(up) * (1.0f - Held.ease(down));
            }
            return 0.0f;
        }

        private static float ease(float value) {
            return value * value * (3.0f - 2.0f * value);
        }
    }

    @Environment(value=EnvType.CLIENT)
    private static final class Fragment {
        final String text;
        final List<LyricWord> words;
        final long startMillis;
        final long endMillis;

        Fragment(String text, List<LyricWord> words, long startMillis, long endMillis) {
            this.text = text;
            this.words = words;
            this.startMillis = startMillis;
            this.endMillis = endMillis;
        }
    }

    @Environment(value=EnvType.CLIENT)
    private record Timing(long spawn, long visibleEnd, long death, float inTime, float inCascade, float outTime, float outCascade) {
    }

    @Environment(value=EnvType.CLIENT)
    private static final class Anim {
        float alpha = 1.0f;
        float shift;
        float scale = 1.0f;
        float tilt;
        int visibleChars = Integer.MAX_VALUE;
        boolean nativeCascade;

        private Anim() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    private static final class Debris {
        final int codePoint;
        final float rightX;
        final float rightZ;
        final float half;
        final float scale;
        final float delayFull;
        final double ground;
        double x;
        double y;
        double z;
        float velocityX;
        float velocityY;
        float velocityZ;
        float spinRate;
        float spin;
        float spinTarget;
        float delay;
        float age;
        float rest;
        boolean grounded;

        Debris(int codePoint, double x, double y, double z, float velocityX, float velocityY, float velocityZ, float rightX, float rightZ, float spinRate, float half, double ground, float scale, float delay) {
            this.codePoint = codePoint;
            this.x = x;
            this.y = y;
            this.z = z;
            this.velocityX = velocityX;
            this.velocityY = velocityY;
            this.velocityZ = velocityZ;
            this.rightX = rightX;
            this.rightZ = rightZ;
            this.spinRate = spinRate;
            this.half = half;
            this.ground = ground;
            this.scale = scale;
            this.delay = delay;
            this.delayFull = Math.max(delay, 1.0E-4f);
        }

        double restHeight() {
            return (double)this.half * Math.abs(Math.cos(this.spin)) + 0.012;
        }
    }
}


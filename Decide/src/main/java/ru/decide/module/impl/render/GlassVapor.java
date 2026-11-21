/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_243
 *  net.minecraft.class_276
 *  net.minecraft.class_3532
 *  net.minecraft.class_4184
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector4f
 */
package ru.decide.module.impl.render;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_243;
import net.minecraft.class_276;
import net.minecraft.class_3532;
import net.minecraft.class_4184;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector4f;
import ru.decide.manager.event_impl.EventRender3D;
import ru.decide.manager.events.orbit.EventHandler;
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.module.api.ModuleInfo;
import ru.decide.module.api.settings.impl.BooleanSetting;
import ru.decide.module.api.settings.impl.ColorSetting;
import ru.decide.module.api.settings.impl.DelimiterSetting;
import ru.decide.module.api.settings.impl.ModeSetting;
import ru.decide.module.api.settings.impl.SliderSetting;
import ru.decide.theme.ThemeColor;
import ru.decide.utils.colors.ColorUtil;
import ru.decide.utils.math.ChatUtils;
import ru.decide.utils.other.Instance;
import ru.decide.utils.render.GlassVaporPipeline;

@ModuleInfo(name="Glass Vapor", desc="\u041f\u0430\u0440 \u043d\u0430\u0434 \u0432\u043e\u0434\u043e\u0439: \u043a\u0430\u043f\u043b\u0438, \u043f\u0440\u0435\u043b\u043e\u043c\u043b\u0435\u043d\u0438\u0435 \u0438 \u0445\u0440\u043e\u043c\u0430\u0442\u0438\u043a\u0430 \u0443 \u0438\u0433\u0440\u043e\u043a\u0430 \u043f\u0440\u0438 \u0434\u0432\u0438\u0436\u0435\u043d\u0438\u0438", category=Category.VISUALS)
@Environment(value=EnvType.CLIENT)
public final class GlassVapor
extends Module {
    private static final String COLOR_RAINBOW = "\u0420\u0430\u0434\u0443\u0433\u0430";
    private static final String COLOR_CLIENT = "\u041a\u043b\u0438\u0435\u043d\u0442";
    private static final String COLOR_CUSTOM = "\u0421\u0432\u043e\u0439";
    private static final int CLIENT_COLOR_FIRST = ColorUtil.getColor2(127, 242, 255, 255);
    private static final int CLIENT_COLOR_SECOND = ColorUtil.getColor2(255, 50, 150, 255);
    private static final int DARK_SECOND_COLOR = new Color(16, 16, 16, 75).getRGB();
    private static final int MAX_ELEMENTS = 240;
    private static final int PALETTE_SIZE = 6;
    private static final int PALETTE_BASE = 3856;
    public final DelimiterSetting dropsSeparator = new DelimiterSetting(this, "\u041a\u0430\u043f\u043b\u0438");
    public final SliderSetting density = new SliderSetting(this, "\u041f\u043b\u043e\u0442\u043d\u043e\u0441\u0442\u044c", 5.0f, 1.0f, 12.0f, 1.0f);
    public final SliderSetting spawnRate = new SliderSetting(this, "\u0427\u0430\u0441\u0442\u043e\u0442\u0430 \u0431\u0440\u044b\u0437\u0433", 14.0f, 4.0f, 30.0f, 1.0f);
    public final SliderSetting blobSize = new SliderSetting(this, "\u0420\u0430\u0437\u043c\u0435\u0440", 0.3f, 0.15f, 0.55f, 0.01f);
    public final SliderSetting riseHeight = new SliderSetting(this, "\u0412\u044b\u0441\u043e\u0442\u0430 \u043f\u043e\u0434\u044a\u0451\u043c\u0430", 1.6f, 0.6f, 3.0f, 0.1f);
    public final SliderSetting lifetime = new SliderSetting(this, "\u0412\u0440\u0435\u043c\u044f \u0436\u0438\u0437\u043d\u0438", 1600.0f, 900.0f, 3000.0f, 50.0f);
    public final DelimiterSetting glassSeparator = new DelimiterSetting(this, "\u0421\u0442\u0435\u043a\u043b\u043e");
    public final SliderSetting distort = new SliderSetting(this, "\u041f\u0440\u0435\u043b\u043e\u043c\u043b\u0435\u043d\u0438\u0435", 1.0f, 0.2f, 3.0f, 0.1f);
    public final SliderSetting ripple = new SliderSetting(this, "\u0412\u043e\u043b\u043d\u044b", 1.0f, 0.0f, 3.0f, 0.1f);
    public final SliderSetting rim = new SliderSetting(this, "\u041a\u0440\u043e\u043c\u043a\u0430", 0.7f, 0.0f, 1.5f, 0.05f);
    public final SliderSetting chroma = new SliderSetting(this, "\u0425\u0440\u043e\u043c\u0430\u0442\u0438\u043a\u0430", 0.35f, 0.0f, 1.0f, 0.05f);
    public final DelimiterSetting colorSeparator = new DelimiterSetting(this, "\u0426\u0432\u0435\u0442");
    public final SliderSetting tintStrength = new SliderSetting(this, "\u0421\u0438\u043b\u0430 \u0442\u043e\u043d\u0438\u0440\u043e\u0432\u043a\u0438", 45.0f, 0.0f, 100.0f, 1.0f);
    public final ModeSetting colorMode = new ModeSetting((Module)this, "\u0426\u0432\u0435\u0442 \u0441\u0442\u0435\u043a\u043b\u0430", "\u041a\u043b\u0438\u0435\u043d\u0442", "\u0420\u0430\u0434\u0443\u0433\u0430", "\u0421\u0432\u043e\u0439");
    public final BooleanSetting useSecondColor = new BooleanSetting((Module)this, "\u0412\u0442\u043e\u0440\u043e\u0439 \u0446\u0432\u0435\u0442", false).setVisible(() -> this.colorMode.is(COLOR_CUSTOM));
    public final ColorSetting customColor = new ColorSetting((Module)this, "\u0426\u0432\u0435\u0442", new Color(160, 220, 255, 255).getRGB()).setVisible(() -> this.colorMode.is(COLOR_CUSTOM));
    public final ColorSetting customSecondColor = new ColorSetting((Module)this, "\u0426\u0432\u0435\u0442 2", DARK_SECOND_COLOR).setVisible(() -> this.colorMode.is(COLOR_CUSTOM) && this.useSecondColor.getValue() != false);
    private final float[] data = new float[3880];
    private final List<Drop> drops = new ArrayList<Drop>(64);
    private final Matrix4f viewProj = new Matrix4f();
    private final Vector4f centerClip = new Vector4f();
    private final Vector4f axisClip = new Vector4f();
    private float centerU;
    private float centerV;
    private float centerLin;
    private float axisV;
    private class_243 lastPos;
    private long nextBurstMs;
    private long seq;
    private long enabledAtMs;
    private class_243 lastSpawnPos;
    private boolean frameDataReady;

    public static GlassVapor getInstance() {
        return Instance.get(GlassVapor.class);
    }

    @Override
    protected void onEnable() {
        this.enabledAtMs = System.currentTimeMillis();
        if (GlassVaporPipeline.deviceReady() && !GlassVaporPipeline.validate()) {
            ChatUtils.addChatMessage("\u00a7c[Glass Vapor] \u043f\u0430\u0439\u043f\u043b\u0430\u0439\u043d \u043d\u0435\u0434\u043e\u0441\u0442\u0443\u043f\u0435\u043d, \u044d\u0444\u0444\u0435\u043a\u0442 \u043d\u0435 \u043e\u0442\u0440\u0438\u0441\u0443\u0435\u0442\u0441\u044f");
        }
    }

    @Override
    protected void onDisable() {
        this.drops.clear();
        this.lastPos = null;
        this.frameDataReady = false;
        GlassVaporPipeline.clear();
    }

    private float visualAlpha() {
        if (!this.isEnabled()) {
            return 0.0f;
        }
        long elapsed = System.currentTimeMillis() - this.enabledAtMs;
        if (elapsed >= 1000L) {
            return 1.0f;
        }
        float t = Math.max(0.0f, (float)elapsed / 1000.0f);
        return t * t * (3.0f - 2.0f * t);
    }

    @EventHandler
    public void onRender(EventRender3D event) {
        class_4184 camera;
        class_276 renderTarget = mc.method_1522();
        class_4184 class_41842 = camera = GlassVapor.mc.field_1773 == null ? null : GlassVapor.mc.field_1773.method_19418();
        if (renderTarget == null || camera == null || GlassVapor.mc.field_1724 == null || GlassVapor.mc.field_1687 == null) {
            return;
        }
        long now = System.currentTimeMillis();
        class_243 pos = GlassVapor.mc.field_1724.method_73189();
        boolean moving = false;
        if (this.lastPos != null) {
            double dx = pos.field_1352 - this.lastPos.field_1352;
            double dz = pos.field_1350 - this.lastPos.field_1350;
            moving = GlassVapor.mc.field_1724.method_24828() && dx * dx + dz * dz > 1.0E-6;
        }
        this.lastPos = pos;
        if (this.isEnabled()) {
            this.spawnDrops(pos, moving, now);
        }
        this.drops.removeIf(d -> (float)(now - d.spawnMs) >= d.delayMs + d.burstMs + d.riseMs);
        if (this.drops.isEmpty()) {
            return;
        }
        this.viewProj.set((Matrix4fc)event.getProjectionMatrix()).mul((Matrix4fc)event.getMatrixStack().method_23760().method_23761());
        class_243 cam = camera.method_71156();
        float far = Math.max(192.0f, (float)((Integer)GlassVapor.mc.field_1690.method_42503().method_41753() + 1) * 16.0f);
        boolean paletteOn = this.colorMode.is(COLOR_CLIENT);
        Arrays.fill(this.data, 0.0f);
        float riseH = this.riseHeight.getValue().floatValue();
        int count = 0;
        for (int i = this.drops.size() - 1; i >= 0 && count < 240; --i) {
            float wobbleT;
            float env;
            float h;
            float ry;
            float rx;
            Drop d2 = this.drops.get(i);
            float age = now - d2.spawnMs;
            if (age < d2.delayMs) continue;
            float dropBase = d2.dropR;
            if (age < d2.delayMs + d2.burstMs) {
                float b = GlassVapor.smooth((age - d2.delayMs) / d2.burstMs);
                rx = dropBase * b;
                ry = rx * (0.55f + 0.3f * b);
                h = ry * 0.55f;
                env = GlassVapor.smooth(Math.min(b * 2.0f, 1.0f));
                wobbleT = 0.15f * b;
            } else {
                float r = (age - d2.delayMs - d2.burstMs) / d2.riseMs;
                rx = dropBase * (1.0f - 0.45f * r);
                float stretchCurve = (float)Math.sin(Math.PI * (double)GlassVapor.smooth(Math.min(r * 1.2f, 1.0f)));
                ry = rx * (0.85f + 1.15f * stretchCurve);
                h = dropBase * 0.85f * 0.55f + riseH * d2.riseMul * (float)Math.pow(r, 1.3);
                env = 1.0f - GlassVapor.smooth(class_3532.method_15363((float)((r - 0.72f) / 0.28f), (float)0.0f, (float)1.0f));
                wobbleT = 0.15f + 0.85f * r;
            }
            if (env <= 0.01f || rx <= 0.01f) continue;
            float wobT = (float)now * 0.001f;
            float wx = (float)Math.sin(wobT * d2.wobSpeed + d2.phase) * d2.wobAmp * wobbleT;
            float wz = (float)Math.cos(wobT * d2.wobSpeed * 0.83f + d2.phase * 1.7f) * d2.wobAmp * wobbleT;
            float rxS = rx * 1.85f;
            float ryS = ry * 1.85f;
            count = this.packEllipsoid(count, cam, d2.x + (double)wx, d2.y + (double)h, d2.z + (double)wz, rxS, ryS, rxS, env, Math.max(rxS, ryS) * 0.55f, d2.colorIndex, paletteOn, d2.phase, 0.1f, 1.0f, far);
        }
        if (count == 0) {
            return;
        }
        this.data[0] = count;
        this.data[1] = (float)renderTarget.field_1482 / (float)Math.max(1, renderTarget.field_1481);
        this.data[2] = (float)(now % 100000L) / 1000.0f;
        float moduleFade = this.visualAlpha();
        this.data[3] = 0.016f * this.distort.getValue().floatValue() * moduleFade;
        this.data[4] = this.rim.getValue().floatValue() * moduleFade;
        this.data[5] = class_3532.method_15363((float)(this.tintStrength.getValue().floatValue() / 100.0f), (float)0.0f, (float)1.0f) * moduleFade;
        this.data[6] = 0.008f * this.ripple.getValue().floatValue() * moduleFade;
        this.data[7] = 1.0f;
        this.data[8] = 0.05f;
        this.data[9] = far;
        this.data[10] = this.chroma.getValue().floatValue() * 0.6f;
        this.data[11] = paletteOn ? 1.0f : 0.0f;
        this.data[12] = this.writePalette();
        this.data[13] = 0.0f;
        this.data[14] = 0.0f;
        this.data[15] = 0.0f;
        this.frameDataReady = true;
    }

    public static void applyPending() {
        GlassVapor module = GlassVapor.getInstance();
        if (module == null || !module.frameDataReady) {
            return;
        }
        module.frameDataReady = false;
        class_276 target = mc.method_1522();
        if (target == null) {
            return;
        }
        GlassVaporPipeline.apply(target, module.data);
    }

    private float writePalette() {
        int base = ThemeColor.getVisualColor();
        int first = CLIENT_COLOR_FIRST;
        int second = CLIENT_COLOR_SECOND;
        for (int i = 0; i < 6; ++i) {
            int argb = ColorUtil.interpolateColor(first, second, (float)i / 5.0f);
            int o = 3856 + i * 4;
            this.data[o] = (float)(argb >> 16 & 0xFF) / 255.0f;
            this.data[o + 1] = (float)(argb >> 8 & 0xFF) / 255.0f;
            this.data[o + 2] = (float)(argb & 0xFF) / 255.0f;
            this.data[o + 3] = 1.0f;
        }
        this.data[3856] = (float)(base >> 16 & 0xFF) / 255.0f;
        this.data[3857] = (float)(base >> 8 & 0xFF) / 255.0f;
        this.data[3858] = (float)(base & 0xFF) / 255.0f;
        return 6.0f;
    }

    private int packEllipsoid(int count, class_243 cam, double wx, double wy, double wz, float ax, float ay, float az, float envIn, float halfExtent, int colorIndex, boolean paletteOn, float phase, float wobble, float shine, float far) {
        float env = envIn;
        float relX = (float)(wx - cam.field_1352);
        float relY = (float)(wy - cam.field_1351);
        float relZ = (float)(wz - cam.field_1350);
        if ((env *= GlassVapor.nearFade(relX, relY, relZ, halfExtent)) <= 0.01f) {
            return count;
        }
        if (!this.projectCenter(relX, relY, relZ, far)) {
            return count;
        }
        float cu = this.centerU;
        float cv = this.centerV;
        float lin = this.centerLin;
        float e1x = this.axisU(ax, 0.0f, 0.0f);
        float e1y = this.axisV;
        float e2x = this.axisU(0.0f, ay, 0.0f);
        float e2y = this.axisV;
        float e3x = this.axisU(0.0f, 0.0f, az);
        float e3y = this.axisV;
        float bxx = e1x * e1x + e2x * e2x + e3x * e3x;
        float bxy = e1x * e1y + e2x * e2y + e3x * e3y;
        float byy = e1y * e1y + e2y * e2y + e3y * e3y;
        float mid = 0.5f * (bxx + byy);
        float disc = (float)Math.sqrt(0.25f * (bxx - byy) * (bxx - byy) + bxy * bxy);
        float l1sq = mid + disc;
        float l2sq = Math.max(mid - disc, 1.0E-12f);
        float l1 = (float)Math.sqrt(l1sq);
        float l2 = (float)Math.sqrt(l2sq);
        if (l1 < 1.0E-5f || l2 < 1.0E-5f) {
            return count;
        }
        if ((env *= GlassVapor.sizeFade(l1, l2)) <= 0.01f) {
            return count;
        }
        float vx = 0.0f;
        float vy = 0.0f;
        if (Math.abs(bxy) > 1.0E-10f) {
            vx = bxy;
            vy = l1sq - bxx;
        } else if (bxx >= byy) {
            vx = 1.0f;
            vy = 0.0f;
        } else {
            vx = 0.0f;
            vy = 1.0f;
        }
        float vl = (float)Math.sqrt(vx * vx + vy * vy);
        if (vl < 1.0E-12f) {
            vx = 1.0f;
            vy = 0.0f;
            vl = 1.0f;
        }
        float p1x = (vx /= vl) * l1;
        float p1y = (vy /= vl) * l1;
        float p2x = -vy * l2;
        float p2y = vx * l2;
        if (!GlassVapor.cullCenter(cu, cv, l1, l2)) {
            return count;
        }
        return this.write(count, cu, cv, lin, env, p1x, p1y, p2x, p2y, colorIndex, paletteOn, phase, wobble, shine);
    }

    private boolean projectCenter(float relX, float relY, float relZ, float far) {
        this.centerClip.set(relX, relY, relZ, 1.0f);
        this.viewProj.transform(this.centerClip);
        if (this.centerClip.w <= 0.05f) {
            return false;
        }
        float iw = 1.0f / this.centerClip.w;
        this.centerU = this.centerClip.x * iw * 0.5f + 0.5f;
        this.centerV = this.centerClip.y * iw * 0.5f + 0.5f;
        this.centerLin = GlassVapor.linDepth(this.centerClip.z * iw * 0.5f + 0.5f, 0.05f, far);
        return true;
    }

    private float axisU(float ax, float ay, float az) {
        this.axisClip.set(ax, ay, az, 0.0f);
        this.viewProj.transform(this.axisClip);
        float iw = 1.0f / (this.centerClip.w * this.centerClip.w);
        float u = 0.5f * (this.axisClip.x * this.centerClip.w - this.centerClip.x * this.axisClip.w) * iw;
        this.axisV = 0.5f * (this.axisClip.y * this.centerClip.w - this.centerClip.y * this.axisClip.w) * iw;
        return u;
    }

    private int write(int count, float cu, float cv, float lin, float env, float e1x, float e1y, float e2x, float e2y, int colorIndex, boolean paletteOn, float phase, float wobble, float shine) {
        int base = 16 + count * 16;
        this.data[base] = cu;
        this.data[base + 1] = cv;
        this.data[base + 2] = lin;
        this.data[base + 3] = env;
        this.data[base + 4] = e1x;
        this.data[base + 5] = e1y;
        this.data[base + 6] = e2x;
        this.data[base + 7] = e2y;
        this.data[base + 8] = paletteOn ? GlassVapor.paletteFadeT(8, colorIndex) : 0.0f;
        this.data[base + 9] = phase;
        this.data[base + 10] = wobble;
        this.data[base + 11] = shine;
        if (!paletteOn) {
            int color = this.getColor(colorIndex);
            this.data[base + 12] = (float)(color >> 16 & 0xFF) / 255.0f;
            this.data[base + 13] = (float)(color >> 8 & 0xFF) / 255.0f;
            this.data[base + 14] = (float)(color & 0xFF) / 255.0f;
        }
        return count + 1;
    }

    private void spawnDrops(class_243 pos, boolean moving, long now) {
        if (!moving) {
            return;
        }
        float spacing = class_3532.method_15363((float)(1.5f / Math.max(1.0f, this.density.getValue().floatValue())), (float)0.1f, (float)1.5f);
        if (this.lastSpawnPos != null && this.lastSpawnPos.method_1025(pos) < (double)(spacing * spacing)) {
            return;
        }
        this.lastSpawnPos = pos;
        long seed = this.seq++;
        long minStep = (long)(1000.0f / Math.max(1.0f, this.spawnRate.getValue().floatValue()));
        long burstAt = Math.max(now + (long)(40.0f + 160.0f * GlassVapor.hashf(seed + 2L)), this.nextBurstMs);
        if (burstAt - now > 350L) {
            return;
        }
        this.nextBurstMs = burstAt + (long)((float)minStep * (0.85f + 0.3f * GlassVapor.hashf(seed + 11L)));
        Drop d = new Drop();
        d.x = pos.field_1352 + (double)(GlassVapor.hashf(seed) - 0.5f) * 0.9;
        d.y = pos.field_1351 + 0.02;
        d.z = pos.field_1350 + (double)(GlassVapor.hashf(seed + 1L) - 0.5f) * 0.9;
        d.spawnMs = now;
        d.delayMs = burstAt - now;
        d.burstMs = 320.0f * (0.8f + 0.4f * GlassVapor.hashf(seed + 3L));
        d.riseMs = this.lifetime.getValue().floatValue() * (0.85f + 0.3f * GlassVapor.hashf(seed + 4L));
        d.dropR = this.blobSize.getValue().floatValue() * (0.75f + 0.5f * GlassVapor.hashf(seed + 6L));
        d.colorIndex = (int)(seed * 30L % 360L);
        d.riseMul = 0.85f + GlassVapor.hashf(seed + 7L) * 0.35f;
        d.wobAmp = 0.1f + GlassVapor.hashf(seed + 8L) * 0.12f;
        d.wobSpeed = 1.6f + GlassVapor.hashf(seed + 9L) * 1.8f;
        d.phase = GlassVapor.hashf(seed + 10L) * ((float)Math.PI * 2);
        this.drops.add(d);
    }

    private int getColor(int index) {
        if (this.colorMode.is(COLOR_RAINBOW)) {
            return GlassVapor.rainbow(8, index, 1.0f, 1.0f);
        }
        if (this.colorMode.is(COLOR_CLIENT)) {
            int first = GlassVapor.clientColor();
            int second = ColorUtil.interpolateColor(first, DARK_SECOND_COLOR, 0.7f);
            return GlassVapor.fade(8, index, first, second);
        }
        int first = this.customColor.getValue();
        int second = this.useSecondColor.getValue() != false ? this.customSecondColor.getValue() : this.customColor.getValue();
        return GlassVapor.fade(8, index, first, second);
    }

    private static int clientColor() {
        float wave = (class_3532.method_15374((double)((float)System.currentTimeMillis() / 520.0f)) + 1.0f) / 2.0f;
        return ColorUtil.interpolateColor(CLIENT_COLOR_FIRST, CLIENT_COLOR_SECOND, wave);
    }

    private static int rainbow(int speed, int index, float saturation, float brightness) {
        int angle = (int)((System.currentTimeMillis() / (long)Math.max(1, speed) + (long)index) % 360L);
        return Color.HSBtoRGB((float)angle / 360.0f, saturation, brightness) | 0xFF000000;
    }

    private static int fade(int speed, int index, int first, int second) {
        int angle = (int)((System.currentTimeMillis() / (long)Math.max(1, speed) + (long)index) % 360L);
        angle = angle >= 180 ? 360 - angle : angle;
        return ColorUtil.interpolateColor(first, second, (float)angle / 180.0f);
    }

    private static float paletteFadeT(int speed, int index) {
        int angle = (int)((System.currentTimeMillis() / (long)Math.max(1, speed) + (long)index) % 360L);
        return (float)angle / 360.0f;
    }

    private static boolean cullCenter(float cu, float cv, float l1, float l2) {
        float ext = (l1 + l2) * 1.5f + 0.05f;
        return cu >= -ext && cu <= 1.0f + ext && cv >= -ext && cv <= 1.0f + ext;
    }

    private static float nearFade(float relX, float relY, float relZ, float halfExtent) {
        float dist = (float)Math.sqrt(relX * relX + relY * relY + relZ * relZ) - halfExtent;
        return GlassVapor.smooth(class_3532.method_15363((float)((dist - 0.6f) / 0.75f), (float)0.0f, (float)1.0f));
    }

    private static float sizeFade(float l1, float l2) {
        float largest = Math.max(l1, l2);
        return 1.0f - GlassVapor.smooth(class_3532.method_15363((float)((largest - 0.5f) / 0.35f), (float)0.0f, (float)1.0f));
    }

    private static float linDepth(float d, float near, float far) {
        float z = d * 2.0f - 1.0f;
        return 2.0f * near * far / (far + near - z * (far - near));
    }

    private static float smooth(float x) {
        return x * x * (3.0f - 2.0f * x);
    }

    private static float hashf(long nIn) {
        long n = nIn;
        n = (n ^ n >>> 33) * -336448155523654707L;
        n ^= n >>> 33;
        return (float)(n >>> 8 & 0xFFFFFFL) / 1.6777216E7f;
    }

    @Environment(value=EnvType.CLIENT)
    private static final class Drop {
        double x;
        double y;
        double z;
        long spawnMs;
        float delayMs;
        float burstMs;
        float riseMs;
        float dropR;
        int colorIndex;
        float riseMul;
        float wobAmp;
        float wobSpeed;
        float phase;

        private Drop() {
        }
    }
}


package never.lose.modules.impl.Render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.SimpleFramebuffer;
import never.lose.NeverLose;
import never.lose.modules.api.Module;
import never.lose.modules.api.ModuleCategory;
import never.lose.modules.api.ModuleInfo;
import never.lose.modules.api.setting.BooleanSetting;
import never.lose.modules.api.setting.ColorSetting;
import never.lose.modules.api.setting.ModeSetting;
import never.lose.modules.api.setting.SliderSetting;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ModuleInfo(name = "Hands", desc = "Накладывает визуальные эффекты и шейдер заливки на руки", category = ModuleCategory.RENDER)
public class Hands extends Module {

    public final BooleanSetting fillRainbow = new BooleanSetting("Радужная", false);
    public final SliderSetting rainbowSpeed = new SliderSetting("Скорость радуги", 0.4, 0.0, 2.0, 0.05);
    public final SliderSetting rainbowScale = new SliderSetting("Масштаб радуги", 1.0, 0.2, 3.0, 0.1);
    public final ModeSetting fillColorMode = new ModeSetting("Цвет заливки", "Свой", "Интерфейс", "Свой");
    public final ColorSetting fillColor = new ColorSetting("Цвет заливки (свой)", 0xFFFF4444);
    public final SliderSetting fillAlpha = new SliderSetting("Прозрачность заливки", 0.8, 0.0, 1.0, 0.05);
    public final BooleanSetting keepShading = new BooleanSetting("Сохранить тени", true);
    public final SliderSetting shadingStrength = new SliderSetting("Сила теней", 0.3, 0.0, 1.0, 0.05);

    public final BooleanSetting outlineEnabled = new BooleanSetting("Обводка", false);
    public final SliderSetting outlineWidth = new SliderSetting("Толщина обводки", 1.0, 0.5, 3.0, 0.5);
    public final ColorSetting outlineColor = new ColorSetting("Цвет обводки", 0xFF8A98FF);

    public final BooleanSetting glowEnabled = new BooleanSetting("Глов", true);
    public final SliderSetting glowRadius = new SliderSetting("Размытие", 4.0, 1.0, 6.0, 1.0);
    public final BooleanSetting outerGlow = new BooleanSetting("Внешний глов", true);
    public final SliderSetting glowExposure = new SliderSetting("Яркость", 2.0, 0.5, 5.0, 0.1);

    public final BooleanSetting autoColor = new BooleanSetting("Авто цвет", false);
    public final SliderSetting saturation = new SliderSetting("Насыщенность", 1.4, 0.5, 3.0, 0.1);
    public final ModeSetting effectColorMode = new ModeSetting("Цвет эффекта", "Свой", "Интерфейс", "Свой");
    public final ColorSetting glowColor1 = new ColorSetting("Цвет глова 1", 0xFF8A98FF);
    public final ColorSetting glowColor2 = new ColorSetting("Цвет глова 2", 0xFFFF6BAC);

    public final BooleanSetting trailEnabled = new BooleanSetting("Шлейф", true);
    public final SliderSetting trailFade = new SliderSetting("Скорость затухания", 0.009, 0.002, 0.2, 0.002);
    public final SliderSetting trailRise = new SliderSetting("Подъём", 0.14, 0.0, 1.5, 0.05);
    public final SliderSetting trailSway = new SliderSetting("Качание", 0.025, 0.0, 0.2, 0.005);
    public final SliderSetting trailTurb = new SliderSetting("Турбулентность", 0.0, 0.0, 0.6, 0.01);
    public final SliderSetting trailFlicker = new SliderSetting("Мерцание", 0.0, 0.0, 0.2, 0.01);
    public final BooleanSetting trailBurst = new BooleanSetting("Сдув при ударе", true);
    public final SliderSetting trailBurstPower = new SliderSetting("Сила сдува", 2.5, 1.0, 10.0, 0.5);
    public final BooleanSetting trailModel = new BooleanSetting("Шлейф модели", true);
    public final SliderSetting trailModelAlpha = new SliderSetting("Прозрачность модели", 0.4, 0.1, 1.0, 0.05);

    private SimpleFramebuffer handsBuffer;
    private SimpleFramebuffer filledBuffer;
    private SimpleFramebuffer trailRead;
    private SimpleFramebuffer trailWrite;
    private final List<SimpleFramebuffer> bloomBuffers = new ArrayList<>();

    private int blitProg = -1;
    private int fillProg = -1;
    private int downProg = -1;
    private int upProg = -1;
    private int glowProg = -1;
    private int outlineProg = -1;
    private int trailFadeProg = -1;
    private int trailColorProg = -1;

    private int quadVAO = -1;
    private int quadVBO = -1;

    private long lastTrailTime = 0L;
    private float smoothDt = 1f / 60f;
    private float smoothTrailRise;
    private float smoothTrailSway;
    private float smoothBurst;
    private long lastSwingMs = -10000L;
    private boolean wasSwinging = false;
    private final float[] autoCol = {1f, 1f, 1f, 1f};
    private final float[] colorBuf1 = new float[4];
    private final float[] colorBuf2 = new float[4];
    private int autoTick = 0;
    private int lastBloomTex = -1;
    private ByteBuffer autoColorBuffer;
    private final Map<Long, Integer> uniformCache = new HashMap<>();

    private final MinecraftClient mc = MinecraftClient.getInstance();

    public Hands() {
        rainbowSpeed.setVisible(fillRainbow::getValue);
        rainbowScale.setVisible(fillRainbow::getValue);
        fillColorMode.setVisible(() -> !fillRainbow.getValue());
        fillColor.setVisible(() -> !fillRainbow.getValue() && fillColorMode.is("Свой"));
        shadingStrength.setVisible(keepShading::getValue);

        outlineWidth.setVisible(outlineEnabled::getValue);
        outlineColor.setVisible(() -> outlineEnabled.getValue() && effectColorMode.is("Свой"));

        glowRadius.setVisible(glowEnabled::getValue);
        outerGlow.setVisible(glowEnabled::getValue);
        glowExposure.setVisible(() -> glowEnabled.getValue() && outerGlow.getValue());

        autoColor.setVisible(() -> glowEnabled.getValue() || outlineEnabled.getValue());
        saturation.setVisible(() -> (glowEnabled.getValue() || outlineEnabled.getValue()) && autoColor.getValue());
        effectColorMode.setVisible(() -> (glowEnabled.getValue() || outlineEnabled.getValue()) && !autoColor.getValue());
        glowColor1.setVisible(() -> glowEnabled.getValue() && !autoColor.getValue() && effectColorMode.is("Свой"));
        glowColor2.setVisible(() -> glowEnabled.getValue() && !autoColor.getValue() && effectColorMode.is("Свой"));

        trailEnabled.setVisible(() -> glowEnabled.getValue() && outerGlow.getValue());
        trailFade.setVisible(() -> glowEnabled.getValue() && outerGlow.getValue() && trailEnabled.getValue());
        trailRise.setVisible(() -> glowEnabled.getValue() && outerGlow.getValue() && trailEnabled.getValue());
        trailSway.setVisible(() -> glowEnabled.getValue() && outerGlow.getValue() && trailEnabled.getValue());
        trailTurb.setVisible(() -> glowEnabled.getValue() && outerGlow.getValue() && trailEnabled.getValue());
        trailFlicker.setVisible(() -> glowEnabled.getValue() && outerGlow.getValue() && trailEnabled.getValue());
        trailBurst.setVisible(() -> glowEnabled.getValue() && outerGlow.getValue() && trailEnabled.getValue());
        trailBurstPower.setVisible(() -> glowEnabled.getValue() && outerGlow.getValue() && trailEnabled.getValue() && trailBurst.getValue());
        trailModel.setVisible(() -> glowEnabled.getValue() && outerGlow.getValue() && trailEnabled.getValue());
        trailModelAlpha.setVisible(() -> glowEnabled.getValue() && outerGlow.getValue() && trailEnabled.getValue() && trailModel.getValue());
    }

    private int uloc(int p, String n) {
        long key = ((long) p << 32) | ((long) n.hashCode() & 0xFFFFFFFFL);
        Integer c = uniformCache.get(key);
        if (c != null) return c;
        int l = GL20.glGetUniformLocation(p, n);
        uniformCache.put(key, l);
        return l;
    }

    private void u1i(int p, String n, int v) { int l = uloc(p, n); if (l >= 0) GL20.glUniform1i(l, v); }
    private void u1f(int p, String n, float v) { int l = uloc(p, n); if (l >= 0) GL20.glUniform1f(l, v); }
    private void u2f(int p, String n, float a, float b) { int l = uloc(p, n); if (l >= 0) GL20.glUniform2f(l, a, b); }
    private void u3f(int p, String n, float a, float b, float c) { int l = uloc(p, n); if (l >= 0) GL20.glUniform3f(l, a, b, c); }
    private void u4f(int p, String n, float a, float b, float c, float d) { int l = uloc(p, n); if (l >= 0) GL20.glUniform4f(l, a, b, c, d); }

    private void bindTex(int unit, int tex) {
        GlStateManager._activeTexture(unit);
        GlStateManager._bindTexture(tex);
    }

    private int themeColor() {
        return NeverLose.getInstance().getThemeManager().getCurrentTheme().colorClient;
    }

    private void decodeColor(int argb, float[] out) {
        out[0] = ((argb >> 16) & 0xFF) / 255.0f;
        out[1] = ((argb >> 8) & 0xFF) / 255.0f;
        out[2] = (argb & 0xFF) / 255.0f;
        out[3] = ((argb >> 24) & 0xFF) / 255.0f;
    }

    private SimpleFramebuffer ensureBuffer(SimpleFramebuffer buf, boolean depth, int divisor) {
        int fw = mc.getWindow().getFramebufferWidth();
        int fh = mc.getWindow().getFramebufferHeight();
        int w = Math.max(2, fw / divisor);
        int h = Math.max(2, fh / divisor);
        if (buf == null) {
            buf = new SimpleFramebuffer(w, h, depth, MinecraftClient.IS_SYSTEM_MAC);
            buf.setTexFilter(GL11.GL_LINEAR);
            buf.setClearColor(0f, 0f, 0f, 0f);
            buf.clear(MinecraftClient.IS_SYSTEM_MAC);
        } else if (buf.textureWidth != w || buf.textureHeight != h) {
            buf.resize(w, h, MinecraftClient.IS_SYSTEM_MAC);
            buf.setTexFilter(GL11.GL_LINEAR);
            buf.setClearColor(0f, 0f, 0f, 0f);
            buf.clear(MinecraftClient.IS_SYSTEM_MAC);
        }
        return buf;
    }

    @Override
    public void onRenderHandPre() {
        if (blitProg == -1) {
            initShaders();
        }

        handsBuffer = ensureBuffer(handsBuffer, true, 1);
        handsBuffer.clear(MinecraftClient.IS_SYSTEM_MAC);
        handsBuffer.beginWrite(true);
    }

    @Override
    public void onRenderHandPost() {
        if (handsBuffer == null) return;

        mc.getFramebuffer().beginWrite(true);
        renderEffects();
    }

    private void renderEffects() {
        int fc = fillColorMode.is("Интерфейс") ? themeColor() : fillColor.getValue();
        decodeColor(fc, colorBuf1);

        filledBuffer = ensureBuffer(filledBuffer, false, 1);
        filledBuffer.clear(MinecraftClient.IS_SYSTEM_MAC);
        filledBuffer.beginWrite(true);

        RenderSystem.disableBlend();
        GL20.glUseProgram(fillProg);
        u1i(fillProg, "originalTexture", 0);
        u3f(fillProg, "fillColor", colorBuf1[0], colorBuf1[1], colorBuf1[2]);
        u1f(fillProg, "fillAlpha", fillAlpha.getFloatValue());
        u1i(fillProg, "keepShading", keepShading.getValue() ? 1 : 0);
        u1f(fillProg, "shadingStrength", shadingStrength.getFloatValue());
        u1i(fillProg, "rainbow", fillRainbow.getValue() ? 1 : 0);
        u1f(fillProg, "rainbowTime", (System.currentTimeMillis() % 100000L) / 1000f);
        u1f(fillProg, "rainbowSpeed", rainbowSpeed.getFloatValue());
        u1f(fillProg, "rainbowScale", rainbowScale.getFloatValue());
        u1f(fillProg, "screenH", (float) mc.getWindow().getFramebufferHeight());
        bindTex(GL13.GL_TEXTURE0, handsBuffer.getColorAttachment());
        drawFullscreenQuad();
        GL20.glUseProgram(0);
        bindTex(GL13.GL_TEXTURE0, 0);

        lastBloomTex = -1;
        boolean doBloom = glowEnabled.getValue() && outerGlow.getValue();
        if (doBloom) {
            doBloomChain(filledBuffer);
        } else if (outlineEnabled.getValue() && autoColor.getValue()) {
            doSmallBlurForAuto(filledBuffer);
        }

        mc.getFramebuffer().beginWrite(true);
        RenderSystem.enableBlend();
        RenderSystem.disableDepthTest();

        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        GL20.glUseProgram(blitProg);
        u1i(blitProg, "inTex", 0);
        u4f(blitProg, "color", 1f, 1f, 1f, 1f);
        bindTex(GL13.GL_TEXTURE0, filledBuffer.getColorAttachment());
        drawFullscreenQuad();
        GL20.glUseProgram(0);
        bindTex(GL13.GL_TEXTURE0, 0);

        if (outlineEnabled.getValue()) {
            renderOutline(filledBuffer);
        }

        if (doBloom) {
            int g1 = effectColorMode.is("Интерфейс") ? themeColor() : glowColor1.getValue();
            int g2 = effectColorMode.is("Интерфейс") ? themeColor() : glowColor2.getValue();
            decodeColor(g1, colorBuf1);
            decodeColor(g2, colorBuf2);

            if (trailEnabled.getValue()) {
                renderTrail(lastBloomTex, filledBuffer, colorBuf1, colorBuf2);
            } else {
                mc.getFramebuffer().beginWrite(true);
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE);
                GL20.glUseProgram(glowProg);
                u1i(glowProg, "bloomTexture", 0);
                u1i(glowProg, "maskTexture", 1);
                u3f(glowProg, "glowColor1", colorBuf1[0], colorBuf1[1], colorBuf1[2]);
                u3f(glowProg, "glowColor2", colorBuf2[0], colorBuf2[1], colorBuf2[2]);
                u1f(glowProg, "exposure", glowExposure.getFloatValue());
                u1i(glowProg, "autoColor", autoColor.getValue() ? 1 : 0);
                u1f(glowProg, "saturation", saturation.getFloatValue());
                bindTex(GL13.GL_TEXTURE1, filledBuffer.getColorAttachment());
                bindTex(GL13.GL_TEXTURE0, lastBloomTex);
                drawFullscreenQuad();
                GL20.glUseProgram(0);
                bindTex(GL13.GL_TEXTURE1, 0);
                bindTex(GL13.GL_TEXTURE0, 0);
                RenderSystem.defaultBlendFunc();
            }
        }

        RenderSystem.enableDepthTest();
        RenderSystem.defaultBlendFunc();
    }

    private void doBloomChain(SimpleFramebuffer inputBuffer) {
        int iter = (int) glowRadius.getFloatValue();
        int cur = inputBuffer.getColorAttachment();
        setupBloomBuffers(iter);

        GL20.glUseProgram(downProg);
        u1i(downProg, "inTexture", 0);
        for (int i = 0; i < iter; i++) {
            SimpleFramebuffer b = bloomBuffers.get(i);
            b.clear(MinecraftClient.IS_SYSTEM_MAC);
            b.beginWrite(true);
            u2f(downProg, "uSize", b.textureWidth, b.textureHeight);
            u2f(downProg, "uOffset", 1f + i, 1f + i);
            u2f(downProg, "uHalfPixel", 0.5f / b.textureWidth, 0.5f / b.textureHeight);
            bindTex(GL13.GL_TEXTURE0, cur);
            drawFullscreenQuad();
            cur = b.getColorAttachment();
        }

        GL20.glUseProgram(upProg);
        u1i(upProg, "inTexture", 0);
        for (int i = iter - 1; i >= 1; i--) {
            SimpleFramebuffer b = bloomBuffers.get(i - 1);
            b.beginWrite(true);
            u2f(upProg, "uSize", b.textureWidth, b.textureHeight);
            u2f(upProg, "uOffset", 1f + i, 1f + i);
            u2f(upProg, "uHalfPixel", 0.5f / b.textureWidth, 0.5f / b.textureHeight);
            u3f(upProg, "color", 1f, 1f, 1f);
            bindTex(GL13.GL_TEXTURE0, cur);
            drawFullscreenQuad();
            cur = b.getColorAttachment();
        }
        GL20.glUseProgram(0);
        lastBloomTex = cur;

        if (autoColor.getValue()) {
            updateAutoColor();
        }
    }

    private void doSmallBlurForAuto(SimpleFramebuffer inputBuffer) {
        int iter = 3;
        setupBloomBuffers(iter);
        int cur = inputBuffer.getColorAttachment();

        GL20.glUseProgram(downProg);
        u1i(downProg, "inTexture", 0);
        for (int i = 0; i < iter; i++) {
            SimpleFramebuffer b = bloomBuffers.get(i);
            b.clear(MinecraftClient.IS_SYSTEM_MAC);
            b.beginWrite(true);
            u2f(downProg, "uSize", b.textureWidth, b.textureHeight);
            u2f(downProg, "uOffset", 1f + i, 1f + i);
            u2f(downProg, "uHalfPixel", 0.5f / b.textureWidth, 0.5f / b.textureHeight);
            bindTex(GL13.GL_TEXTURE0, cur);
            drawFullscreenQuad();
            cur = b.getColorAttachment();
        }

        GL20.glUseProgram(upProg);
        u1i(upProg, "inTexture", 0);
        for (int i = iter - 1; i >= 1; i--) {
            SimpleFramebuffer b = bloomBuffers.get(i - 1);
            b.beginWrite(true);
            u2f(upProg, "uSize", b.textureWidth, b.textureHeight);
            u2f(upProg, "uOffset", 1f + i, 1f + i);
            u2f(upProg, "uHalfPixel", 0.5f / b.textureWidth, 0.5f / b.textureHeight);
            u3f(upProg, "color", 1f, 1f, 1f);
            bindTex(GL13.GL_TEXTURE0, cur);
            drawFullscreenQuad();
            cur = b.getColorAttachment();
        }
        GL20.glUseProgram(0);
        lastBloomTex = cur;
        updateAutoColor();
        mc.getFramebuffer().beginWrite(true);
    }

    private void renderTrail(int bloomTex, SimpleFramebuffer inputBuffer, float[] c1, float[] c2) {
        trailRead = ensureBuffer(trailRead, false, 2);
        trailWrite = ensureBuffer(trailWrite, false, 2);

        long now = System.currentTimeMillis();
        float rawDt = lastTrailTime > 0L ? (now - lastTrailTime) / 1000f : 1f / 60f;
        lastTrailTime = now;
        if (rawDt <= 0.0f || rawDt > 0.05f) {
            rawDt = smoothDt;
        }
        rawDt = Math.max(1f / 144f, Math.min(1f / 60f, rawDt));
        smoothDt += (rawDt - smoothDt) * 0.08f;
        float dt = smoothDt;
        float t = (now % 100000L) / 1000f;

        float swayFreq = 3.6f;
        float upDelta = trailRise.getFloatValue() * dt;
        float amp = trailSway.getFloatValue();
        float swayDelta = (float) (Math.sin(t * swayFreq) - Math.sin((t - dt) * swayFreq)) * amp;
        float trailSmooth = 1.0f - (float) Math.exp(-dt * 8.0f);
        smoothTrailRise += (upDelta - smoothTrailRise) * trailSmooth;
        smoothTrailSway += (swayDelta - smoothTrailSway) * trailSmooth;

        boolean swinging = mc.player != null && (mc.player.handSwinging || mc.player.isUsingItem());
        if (trailBurst.getValue() && swinging && !wasSwinging) {
            lastSwingMs = now;
        }
        wasSwinging = swinging;

        float burstAge = (now - lastSwingMs) / 1000f;
        float burst = Math.max(0f, 1f - burstAge / 0.45f);
        smoothBurst += (burst - smoothBurst) * (1.0f - (float) Math.exp(-dt * 6.0f));
        float fadeAdd = smoothBurst * trailBurstPower.getFloatValue() * 0.012f;

        trailWrite.clear(MinecraftClient.IS_SYSTEM_MAC);
        trailWrite.beginWrite(true);
        RenderSystem.disableBlend();

        GL20.glUseProgram(trailFadeProg);
        u1i(trailFadeProg, "inTex", 0);
        u2f(trailFadeProg, "offset", smoothTrailSway, smoothTrailRise);
        u1f(trailFadeProg, "fade", trailFade.getFloatValue() + fadeAdd);
        u1f(trailFadeProg, "t", t);
        u1f(trailFadeProg, "dt", dt);
        u1f(trailFadeProg, "turb", trailTurb.getFloatValue());
        u1f(trailFadeProg, "flickAmp", trailFlicker.getFloatValue());
        u2f(trailFadeProg, "texSize", trailRead.textureWidth, trailRead.textureHeight);
        bindTex(GL13.GL_TEXTURE0, trailRead.getColorAttachment());
        drawFullscreenQuad();
        GL20.glUseProgram(0);
        bindTex(GL13.GL_TEXTURE0, 0);

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        GL20.glUseProgram(trailColorProg);
        u1i(trailColorProg, "bloomTexture", 0);
        u3f(trailColorProg, "glowColor1", c1[0], c1[1], c1[2]);
        u3f(trailColorProg, "glowColor2", c2[0], c2[1], c2[2]);
        u1f(trailColorProg, "exposure", glowExposure.getFloatValue());
        u1i(trailColorProg, "autoColor", autoColor.getValue() ? 1 : 0);
        u1f(trailColorProg, "saturation", saturation.getFloatValue());
        bindTex(GL13.GL_TEXTURE0, bloomTex);
        drawFullscreenQuad();
        GL20.glUseProgram(0);
        bindTex(GL13.GL_TEXTURE0, 0);

        if (trailModel.getValue()) {
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
            GL20.glUseProgram(blitProg);
            u1i(blitProg, "inTex", 0);
            u4f(blitProg, "color", 1f, 1f, 1f, trailModelAlpha.getFloatValue());
            bindTex(GL13.GL_TEXTURE0, inputBuffer.getColorAttachment());
            drawFullscreenQuad();
            GL20.glUseProgram(0);
            bindTex(GL13.GL_TEXTURE0, 0);
        }

        mc.getFramebuffer().beginWrite(true);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        GL20.glUseProgram(blitProg);
        u1i(blitProg, "inTex", 0);
        u4f(blitProg, "color", 1f, 1f, 1f, 1f);
        bindTex(GL13.GL_TEXTURE0, trailWrite.getColorAttachment());
        drawFullscreenQuad();
        GL20.glUseProgram(0);
        bindTex(GL13.GL_TEXTURE0, 0);
        RenderSystem.defaultBlendFunc();

        SimpleFramebuffer tmp = trailRead;
        trailRead = trailWrite;
        trailWrite = tmp;
    }

    private void renderOutline(SimpleFramebuffer inputBuffer) {
        boolean rainbow = fillRainbow.getValue();
        boolean auto = autoColor.getValue() && lastBloomTex != -1;
        int colorMode = rainbow ? 1 : (auto ? 2 : 0);

        int oc = effectColorMode.is("Интерфейс") ? themeColor() : outlineColor.getValue();
        decodeColor(oc, colorBuf1);

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        GL20.glUseProgram(outlineProg);
        u1i(outlineProg, "inTex", 0);
        u1i(outlineProg, "bloomTex", 1);
        u1i(outlineProg, "colorMode", colorMode);
        u1f(outlineProg, "width", outlineWidth.getFloatValue());
        u2f(outlineProg, "texelSize", 1f / mc.getWindow().getFramebufferWidth(), 1f / mc.getWindow().getFramebufferHeight());
        u1f(outlineProg, "alpha", colorBuf1[3]);
        u1f(outlineProg, "rainbowTime", (System.currentTimeMillis() % 100000L) / 1000f);
        u1f(outlineProg, "rainbowSpeed", rainbowSpeed.getFloatValue());
        u1f(outlineProg, "rainbowScale", rainbowScale.getFloatValue());
        u1f(outlineProg, "screenH", (float) mc.getWindow().getFramebufferHeight());
        u1f(outlineProg, "saturation", saturation.getFloatValue());
        u3f(outlineProg, "solidColor", colorBuf1[0], colorBuf1[1], colorBuf1[2]);

        if (auto) bindTex(GL13.GL_TEXTURE1, lastBloomTex);
        bindTex(GL13.GL_TEXTURE0, inputBuffer.getColorAttachment());
        drawFullscreenQuad();
        if (auto) bindTex(GL13.GL_TEXTURE1, 0);
        bindTex(GL13.GL_TEXTURE0, 0);
        GL20.glUseProgram(0);
    }

    private void updateAutoColor() {
        if (bloomBuffers.isEmpty()) return;
        if (autoTick++ % 6 != 0) return;

        SimpleFramebuffer small = bloomBuffers.get(bloomBuffers.size() - 1);
        int w = small.textureWidth;
        int h = small.textureHeight;
        int size = w * h * 4;
        if (autoColorBuffer == null || autoColorBuffer.capacity() < size) {
            autoColorBuffer = BufferUtils.createByteBuffer(size);
        } else {
            autoColorBuffer.clear();
        }

        int prev = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, small.fbo);
        GL11.glReadPixels(0, 0, w, h, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, autoColorBuffer);
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, prev);

        float sumR = 0, sumG = 0, sumB = 0, sumA = 0;
        for (int i = 0; i < w * h; i++) {
            int o = i * 4;
            sumR += (autoColorBuffer.get(o) & 0xFF) / 255.0f;
            sumG += (autoColorBuffer.get(o + 1) & 0xFF) / 255.0f;
            sumB += (autoColorBuffer.get(o + 2) & 0xFF) / 255.0f;
            sumA += (autoColorBuffer.get(o + 3) & 0xFF) / 255.0f;
        }
        if (sumA < 0.001f) return;

        float r = sumR / sumA, g = sumG / sumA, b = sumB / sumA;
        float m = Math.max(r, Math.max(g, b));
        if (m > 0.001f) { r /= m; g /= m; b /= m; }
        float sat = saturation.getFloatValue();
        float l = 0.299f * r + 0.587f * g + 0.114f * b;
        autoCol[0] = clamp01(l + (r - l) * sat);
        autoCol[1] = clamp01(l + (g - l) * sat);
        autoCol[2] = clamp01(l + (b - l) * sat);
        autoCol[3] = 1.0f;
    }

    private static float clamp01(float v) {
        return v < 0f ? 0f : Math.min(v, 1f);
    }

    private void setupBloomBuffers(int n) {
        int fw = mc.getWindow().getFramebufferWidth();
        int fh = mc.getWindow().getFramebufferHeight();
        if (bloomBuffers.size() < n) {
            for (SimpleFramebuffer b : bloomBuffers) {
                b.delete();
            }
            bloomBuffers.clear();
            for (int i = 0; i < n; i++) {
                SimpleFramebuffer f = new SimpleFramebuffer(
                        Math.max(2, fw >> (i + 1)),
                        Math.max(2, fh >> (i + 1)),
                        false, MinecraftClient.IS_SYSTEM_MAC);
                f.setTexFilter(GL11.GL_LINEAR);
                f.setClearColor(0f, 0f, 0f, 0f);
                f.clear(MinecraftClient.IS_SYSTEM_MAC);
                bloomBuffers.add(f);
            }
        }
        for (int i = 0; i < n; i++) {
            int w = Math.max(2, fw >> (i + 1));
            int h = Math.max(2, fh >> (i + 1));
            SimpleFramebuffer b = bloomBuffers.get(i);
            if (b.textureWidth != w || b.textureHeight != h) {
                b.resize(w, h, MinecraftClient.IS_SYSTEM_MAC);
                b.setTexFilter(GL11.GL_LINEAR);
                b.setClearColor(0f, 0f, 0f, 0f);
                b.clear(MinecraftClient.IS_SYSTEM_MAC);
            }
        }
    }

    private void drawFullscreenQuad() {
        if (quadVAO == -1) {
            quadVAO = GL30.glGenVertexArrays();
            quadVBO = GL15.glGenBuffers();
            GL30.glBindVertexArray(quadVAO);
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, quadVBO);
            float[] quadData = {
                -1.0f,  1.0f, 0.0f, 1.0f,
                -1.0f, -1.0f, 0.0f, 0.0f,
                 1.0f, -1.0f, 1.0f, 0.0f,
                -1.0f,  1.0f, 0.0f, 1.0f,
                 1.0f, -1.0f, 1.0f, 0.0f,
                 1.0f,  1.0f, 1.0f, 1.0f
            };
            FloatBuffer buffer = BufferUtils.createFloatBuffer(quadData.length);
            buffer.put(quadData).flip();
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
            GL20.glEnableVertexAttribArray(0);
            GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 4 * Float.BYTES, 0);
            GL20.glEnableVertexAttribArray(1);
            GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 4 * Float.BYTES, 2 * Float.BYTES);
            GL30.glBindVertexArray(0);
        }
        GL30.glBindVertexArray(quadVAO);
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);
        GL30.glBindVertexArray(0);
    }

    private void initShaders() {
        String V = "#version 150\n" +
                "in vec2 Position;\n" +
                "in vec2 UV;\n" +
                "out vec2 texCoord;\n" +
                "void main() {\n" +
                "  gl_Position = vec4(Position, 0.0, 1.0);\n" +
                "  texCoord = UV;\n" +
                "}";

        blitProg = createProgram(V,
                "#version 150\n" +
                "uniform sampler2D inTex;\n" +
                "uniform vec4 color;\n" +
                "in vec2 texCoord;\n" +
                "out vec4 fragColor;\n" +
                "void main() {\n" +
                "  vec4 c = texture(inTex, texCoord);\n" +
                "  if (c.a < 0.001) discard;\n" +
                "  fragColor = c * color;\n" +
                "}");

        downProg = createProgram(V,
                "#version 150\n" +
                "uniform sampler2D inTexture;\n" +
                "uniform vec2 uOffset, uHalfPixel, uSize;\n" +
                "in vec2 texCoord;\n" +
                "out vec4 fragColor;\n" +
                "void main() {\n" +
                "  vec2 u = texCoord, h = uHalfPixel * uOffset;\n" +
                "  fragColor = (texture(inTexture, u) * 4.0 + texture(inTexture, u - h) + texture(inTexture, u + h) + texture(inTexture, u + vec2(h.x, -h.y)) + texture(inTexture, u - vec2(h.x, -h.y))) / 8.0;\n" +
                "}");

        upProg = createProgram(V,
                "#version 150\n" +
                "uniform sampler2D inTexture;\n" +
                "uniform vec2 uOffset, uHalfPixel, uSize;\n" +
                "uniform vec3 color;\n" +
                "in vec2 texCoord;\n" +
                "out vec4 fragColor;\n" +
                "void main() {\n" +
                "  vec2 u = texCoord, h = uHalfPixel * uOffset;\n" +
                "  vec4 s = texture(inTexture, u + vec2(-h.x * 2.0, 0.0)) + texture(inTexture, u + vec2(-h.x, h.y)) * 2.0 + texture(inTexture, u + vec2(0.0, h.y * 2.0)) + texture(inTexture, u + vec2(h.x, h.y)) * 2.0 + texture(inTexture, u + vec2(h.x * 2.0, 0.0)) + texture(inTexture, u + vec2(h.x, -h.y)) * 2.0 + texture(inTexture, u + vec2(0.0, -h.y * 2.0)) + texture(inTexture, u + vec2(-h.x, -h.y)) * 2.0;\n" +
                "  vec4 r = s / 12.0;\n" +
                "  fragColor = vec4(r.rgb * color, r.a);\n" +
                "}");

        glowProg = createProgram(V,
                "#version 150\n" +
                "uniform sampler2D bloomTexture, maskTexture;\n" +
                "uniform vec3 glowColor1, glowColor2;\n" +
                "uniform float exposure, saturation;\n" +
                "uniform int autoColor;\n" +
                "in vec2 texCoord;\n" +
                "out vec4 fragColor;\n" +
                "void main() {\n" +
                "  vec2 u = texCoord;\n" +
                "  vec4 b = texture(bloomTexture, u);\n" +
                "  if (b.a < 0.001) discard;\n" +
                "  float maskA = texture(maskTexture, u).a;\n" +
                "  float i = b.a * (1.0 - maskA) * exposure;\n" +
                "  if (i < 0.001) discard;\n" +
                "  vec3 col;\n" +
                "  if (autoColor == 1) {\n" +
                "    vec3 c = b.rgb / max(b.a, 0.001);\n" +
                "    float m = max(c.r, max(c.g, c.b));\n" +
                "    if (m > 0.001) c /= m;\n" +
                "    float l = dot(c, vec3(0.299, 0.587, 0.114));\n" +
                "    col = clamp(mix(vec3(l), c, saturation), 0.0, 1.0);\n" +
                "  } else {\n" +
                "    col = mix(glowColor1, glowColor2, u.y);\n" +
                "  }\n" +
                "  fragColor = vec4(col * i, i);\n" +
                "}");

        outlineProg = createProgram(V,
                "#version 150\n" +
                "uniform sampler2D inTex, bloomTex;\n" +
                "uniform vec2 texelSize;\n" +
                "uniform float width, alpha, rainbowTime, rainbowSpeed, rainbowScale, screenH, saturation;\n" +
                "uniform int colorMode;\n" +
                "uniform vec3 solidColor;\n" +
                "in vec2 texCoord;\n" +
                "out vec4 fragColor;\n" +
                "vec3 hue2rgb(float h) {\n" +
                "  vec3 p = abs(fract(vec3(h) + vec3(0.0, 2.0/3.0, 1.0/3.0)) * 6.0 - 3.0);\n" +
                "  return clamp(p - 1.0, 0.0, 1.0);\n" +
                "}\n" +
                "void main() {\n" +
                "  vec2 uv = texCoord;\n" +
                "  vec4 c = texture(inTex, uv);\n" +
                "  if (c.a > 0.01) discard;\n" +
                "  float maxA = 0.0;\n" +
                "  for (int x = -2; x <= 2; x++) {\n" +
                "    for (int y = -2; y <= 2; y++) {\n" +
                "      if (x == 0 && y == 0) continue;\n" +
                "      vec2 d = vec2(float(x), float(y)) * texelSize * width;\n" +
                "      maxA = max(maxA, texture(inTex, uv + d).a);\n" +
                "    }\n" +
                "  }\n" +
                "  if (maxA < 0.01) discard;\n" +
                "  vec3 col;\n" +
                "  if (colorMode == 1) {\n" +
                "    float yN = gl_FragCoord.y / max(screenH, 1.0);\n" +
                "    float h = fract(yN * rainbowScale + rainbowTime * rainbowSpeed);\n" +
                "    col = hue2rgb(h);\n" +
                "  } else if (colorMode == 2) {\n" +
                "    vec4 b = texture(bloomTex, uv);\n" +
                "    vec3 cb = b.rgb / max(b.a, 0.001);\n" +
                "    float m = max(cb.r, max(cb.g, cb.b));\n" +
                "    if (m > 0.001) cb /= m;\n" +
                "    float l = dot(cb, vec3(0.299, 0.587, 0.114));\n" +
                "    col = clamp(mix(vec3(l), cb, saturation), 0.0, 1.0);\n" +
                "  } else {\n" +
                "    col = solidColor;\n" +
                "  }\n" +
                "  fragColor = vec4(col, maxA * alpha);\n" +
                "}");

        fillProg = createProgram(V,
                "#version 150\n" +
                "uniform sampler2D originalTexture;\n" +
                "uniform vec3 fillColor;\n" +
                "uniform float fillAlpha, shadingStrength, rainbowTime, rainbowSpeed, rainbowScale, screenH;\n" +
                "uniform int keepShading, rainbow;\n" +
                "in vec2 texCoord;\n" +
                "out vec4 fragColor;\n" +
                "vec3 hue2rgb(float h) {\n" +
                "  vec3 p = abs(fract(vec3(h) + vec3(0.0, 2.0/3.0, 1.0/3.0)) * 6.0 - 3.0);\n" +
                "  return clamp(p - 1.0, 0.0, 1.0);\n" +
                "}\n" +
                "void main() {\n" +
                "  vec4 s = texture(originalTexture, texCoord);\n" +
                "  if (s.a < 0.01) discard;\n" +
                "  vec3 f;\n" +
                "  if (rainbow == 1) {\n" +
                "    float yN = gl_FragCoord.y / max(screenH, 1.0);\n" +
                "    float h = fract(yN * rainbowScale + rainbowTime * rainbowSpeed);\n" +
                "    f = hue2rgb(h);\n" +
                "  } else {\n" +
                "    f = fillColor;\n" +
                "  }\n" +
                "  if (keepShading == 1) f *= mix(1.0, dot(s.rgb, vec3(0.299, 0.587, 0.114)), shadingStrength);\n" +
                "  fragColor = vec4(mix(s.rgb, f, fillAlpha), s.a);\n" +
                "}");

        trailFadeProg = createProgram(V,
                "#version 150\n" +
                "uniform sampler2D inTex;\n" +
                "uniform vec2 offset, texSize;\n" +
                "uniform float fade, t, dt, turb, flickAmp;\n" +
                "in vec2 texCoord;\n" +
                "out vec4 fragColor;\n" +
                "float wob(float y, float tt) {\n" +
                "  return sin(y * 9.0 + tt * 4.3) * 0.6 + sin(y * 17.0 - tt * 2.1) * 0.3 + sin(y * 4.0 + tt * 1.3) * 0.4 + sin(y * 28.0 + tt * 5.7) * 0.15;\n" +
                "}\n" +
                "vec4 blurSample(vec2 p) {\n" +
                "  if (p.x < 0.0 || p.x > 1.0 || p.y < 0.0 || p.y > 1.0) return vec4(0.0);\n" +
                "  vec2 px = 1.0 / max(texSize, vec2(1.0));\n" +
                "  vec4 c = texture(inTex, p) * 0.36;\n" +
                "  if (p.x + px.x <= 1.0) c += texture(inTex, p + vec2( px.x, 0.0)) * 0.16;\n" +
                "  if (p.x - px.x >= 0.0) c += texture(inTex, p + vec2(-px.x, 0.0)) * 0.16;\n" +
                "  if (p.y + px.y <= 1.0) c += texture(inTex, p + vec2(0.0,  px.y)) * 0.16;\n" +
                "  if (p.y - px.y >= 0.0) c += texture(inTex, p + vec2(0.0, -px.y)) * 0.16;\n" +
                "  return c;\n" +
                "}\n" +
                "void main() {\n" +
                "  vec2 uv = texCoord;\n" +
                "  float dxTurb = (wob(uv.y, t) - wob(uv.y, t - dt)) * turb;\n" +
                "  float yFactor = 0.7 + uv.y * 0.8;\n" +
                "  vec2 disp = vec2(offset.x + dxTurb, offset.y * yFactor);\n" +
                "  vec4 c = blurSample(uv - disp);\n" +
                "  if (c.a < 0.001) {\n" +
                "    fragColor = vec4(0.0);\n" +
                "    return;\n" +
                "  }\n" +
                "  float flick = 1.0 - flickAmp + flickAmp * (0.5 + 0.5 * sin(t * 14.0) + 0.25 * sin(t * 9.3));\n" +
                "  float oldA = c.a;\n" +
                "  float newA = max(0.0, oldA * (0.985 * flick) - fade);\n" +
                "  float scale = oldA > 0.001 ? newA / oldA : 0.0;\n" +
                "  fragColor = vec4(c.rgb * scale, newA);\n" +
                "}");

        trailColorProg = createProgram(V,
                "#version 150\n" +
                "uniform sampler2D bloomTexture;\n" +
                "uniform vec3 glowColor1, glowColor2;\n" +
                "uniform float exposure, saturation;\n" +
                "uniform int autoColor;\n" +
                "in vec2 texCoord;\n" +
                "out vec4 fragColor;\n" +
                "void main() {\n" +
                "  vec2 u = texCoord;\n" +
                "  vec4 b = texture(bloomTexture, u);\n" +
                "  if (b.a < 0.001) discard;\n" +
                "  float a = clamp(b.a * exposure, 0.0, 1.0);\n" +
                "  if (a < 0.001) discard;\n" +
                "  vec3 col;\n" +
                "  if (autoColor == 1) {\n" +
                "    vec3 c = b.rgb / max(b.a, 0.001);\n" +
                "    float m = max(c.r, max(c.g, c.b));\n" +
                "    if (m > 0.001) c /= m;\n" +
                "    float l = dot(c, vec3(0.299, 0.587, 0.114));\n" +
                "    col = clamp(mix(vec3(l), c, saturation), 0.0, 1.0);\n" +
                "  } else {\n" +
                "    col = mix(glowColor1, glowColor2, u.y);\n" +
                "  }\n" +
                "  fragColor = vec4(col * a, a);\n" +
                "}");
    }

    private int createProgram(String vs, String fs) {
        int v = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
        int f = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
        int p = GL20.glCreateProgram();

        GL20.glShaderSource(v, vs);
        GL20.glCompileShader(v);
        if (GL20.glGetShaderi(v, GL20.GL_COMPILE_STATUS) == 0) {
            System.err.println("[Hands] VS error: " + GL20.glGetShaderInfoLog(v, 1024));
        }

        GL20.glShaderSource(f, fs);
        GL20.glCompileShader(f);
        if (GL20.glGetShaderi(f, GL20.GL_COMPILE_STATUS) == 0) {
            System.err.println("[Hands] FS error: " + GL20.glGetShaderInfoLog(f, 1024));
        }

        GL20.glAttachShader(p, v);
        GL20.glAttachShader(p, f);
        GL20.glBindAttribLocation(p, 0, "Position");
        GL20.glBindAttribLocation(p, 1, "UV");
        GL20.glLinkProgram(p);

        if (GL20.glGetProgrami(p, GL20.GL_LINK_STATUS) == 0) {
            System.err.println("[Hands] Program link error: " + GL20.glGetProgramInfoLog(p, 1024));
        }

        GL20.glDeleteShader(v);
        GL20.glDeleteShader(f);
        return p;
    }

    @Override
    public void onDisable() {
        if (handsBuffer != null) {
            handsBuffer.delete();
            handsBuffer = null;
        }
        if (filledBuffer != null) {
            filledBuffer.delete();
            filledBuffer = null;
        }
        if (trailRead != null) {
            trailRead.delete();
            trailRead = null;
        }
        if (trailWrite != null) {
            trailWrite.delete();
            trailWrite = null;
        }
        for (SimpleFramebuffer b : bloomBuffers) {
            b.delete();
        }
        bloomBuffers.clear();

        lastTrailTime = 0L;
        smoothDt = 1f / 60f;
        smoothTrailRise = 0f;
        smoothTrailSway = 0f;
        smoothBurst = 0f;

        if (blitProg != -1) {
            GL20.glDeleteProgram(blitProg);
            GL20.glDeleteProgram(downProg);
            GL20.glDeleteProgram(upProg);
            GL20.glDeleteProgram(glowProg);
            GL20.glDeleteProgram(fillProg);
            GL20.glDeleteProgram(trailFadeProg);
            GL20.glDeleteProgram(trailColorProg);
            if (outlineProg != -1) GL20.glDeleteProgram(outlineProg);
            blitProg = downProg = upProg = glowProg = fillProg = outlineProg = trailFadeProg = trailColorProg = -1;
            uniformCache.clear();
        }

        if (quadVAO != -1) {
            GL30.glDeleteVertexArrays(quadVAO);
            GL15.glDeleteBuffers(quadVBO);
            quadVAO = -1;
            quadVBO = -1;
        }
    }
}
package fun.volon.client.modules.impl.render.base.implement;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import fun.volon.Volon;
import fun.volon.api.events.implement.EventRender;
import fun.volon.api.storages.implement.helpertstorages.enumvar.ModuleClass;
import fun.volon.api.utils.animation.AnimationUtils;
import fun.volon.api.utils.animation.Easings;
import fun.volon.api.utils.color.ColorUtils;
import fun.volon.api.utils.draggable.Draggable;
import fun.volon.api.utils.render.RenderUtils;
import fun.volon.api.utils.render.fonts.msdf.Font;
import fun.volon.api.utils.render.fonts.msdf.Fonts;
import fun.volon.client.modules.impl.combat.Aura;
import fun.volon.client.modules.impl.misc.ScoreboardHP;
import fun.volon.client.modules.impl.render.Interface;
import fun.volon.client.modules.impl.render.base.InterfaceProcessing;

import java.awt.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class TargetHud extends InterfaceProcessing {
    private final AnimationUtils alphaAnimation = new AnimationUtils(0.0f, 9.0f, Easings.QUAD_OUT);
    private final AnimationUtils unusualRevealAnimation = new AnimationUtils(0.0f, 4.6f, Easings.QUAD_OUT);
    private final AnimationUtils hpAnimation = new AnimationUtils(1.0f, 9.2f, Easings.QUAD_OUT);
    private final AnimationUtils hpTrailAnimation = new AnimationUtils(1.0f, 7.4f, Easings.QUAD_OUT);
    private final AnimationUtils hpValueAnimation = new AnimationUtils(20.0f, 7.0f, Easings.QUAD_OUT);
    private final AnimationUtils ABValueAnimation = new AnimationUtils(0.0f, 7.0f, Easings.QUAD_OUT);
    private final AnimationUtils goldenHpAnimation = new AnimationUtils(0.0f, 9.2f, Easings.QUAD_OUT);
    private final AnimationUtils goldenHpTrailAnimation = new AnimationUtils(0.0f, 7.4f, Easings.QUAD_OUT);
    private final AnimationUtils goldenAlphaAnimation = new AnimationUtils(0.0f, 9.0f, Easings.QUAD_OUT);
    private final List<HeadParticle> headParticles = new ObjectArrayList<>();

    private static float targetHudAlphaMul() {
        if (Interface.INSTANCE == null) return 1f;
        float p = Interface.INSTANCE.targetHudAlpha.get() / 100f;
        if (p < 0f) return 0f;
        if (p > 1f) return 1f;
        return p;
    }

    private LivingEntity lastTarget;
    private float maxAbsorption = 20.0f;
    private boolean headParticlesEnabled = true;
    private boolean healthBarStyleEnabled = false;
    private boolean nursultanStyleEnabled = false;
    private boolean outlineEnabled = false;
    private float nursultanAnimatedHealth = 0f;
    private float nursultanTrailHealth = 0f;
    private long lastParticleUpdateNs = System.nanoTime();
    private LivingEntity particleTarget;
    private int lastTargetHurtTime = 0;
    private int cachedBarThemeColor = ColorUtils.rgba(124, 91, 242, 255);
    private int cachedBarThemeColor2 = ColorUtils.rgba(93, 67, 175, 255);
    private Framebuffer burnBuffer;
    private int burnBufferWidth = -1;
    private int burnBufferHeight = -1;
    private final ItemStack[] displayItems = new ItemStack[6];
    private final ItemStack[] armorScratch = new ItemStack[4];

    // Флаг для отслеживания скрытого HP
    private boolean isHpUnknown = false;

    public TargetHud(Draggable draggable) {
        super(draggable);
    }

    private Font issue(int size) {
        return Fonts.getFont("suisse", size);
    }

    public boolean isHeadParticlesEnabled() {
        return headParticlesEnabled;
    }

    public void setHeadParticlesEnabled(boolean headParticlesEnabled) {
        this.headParticlesEnabled = headParticlesEnabled;
        if (!headParticlesEnabled) {
            headParticles.clear();
        }
    }

    public boolean isHealthBarStyleEnabled() {
        return healthBarStyleEnabled;
    }

    public void setHealthBarStyleEnabled(boolean healthBarStyleEnabled) {
        this.healthBarStyleEnabled = healthBarStyleEnabled;
    }

    public boolean isNursultanStyleEnabled() {
        return nursultanStyleEnabled;
    }

    public void setNursultanStyleEnabled(boolean nursultanStyleEnabled) {
        this.nursultanStyleEnabled = nursultanStyleEnabled;
    }

    public boolean isOutlineEnabled() {
        return outlineEnabled;
    }

    public void setOutlineEnabled(boolean outlineEnabled) {
        this.outlineEnabled = outlineEnabled;
    }

    private int interpolateColor(int color1, int color2, float ratio) {
        ratio = MathHelper.clamp(ratio, 0.0f, 1.0f);
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;
        int a1 = (color1 >> 24) & 0xFF;
        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;
        int a2 = (color2 >> 24) & 0xFF;
        int r = (int)(r1 + (r2 - r1) * ratio);
        int g = (int)(g1 + (g2 - g1) * ratio);
        int b = (int)(b1 + (b2 - b1) * ratio);
        int a = (int)(a1 + (a2 - a1) * ratio);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private void ensureBurnBuffer() {
        if (mc == null || mc.getWindow() == null) {
            return;
        }

        int w = mc.getWindow().getFramebufferWidth();
        int h = mc.getWindow().getFramebufferHeight();
        if (burnBuffer == null || burnBufferWidth != w || burnBufferHeight != h) {
            if (burnBuffer != null) {
                burnBuffer.delete();
            }
            burnBuffer = new SimpleFramebuffer(w, h, true);
            burnBufferWidth = w;
            burnBufferHeight = h;
        }
    }

    private int collectDisplayItems(LivingEntity target) {
        int armorCount = 0;
        for (ItemStack stack : target.getArmorItems()) {
            if (armorCount < armorScratch.length) {
                armorScratch[armorCount++] = stack;
            }
        }

        int count = 0;
        for (int i = armorCount - 1; i >= 0; i--) {
            displayItems[count++] = armorScratch[i];
            armorScratch[i] = ItemStack.EMPTY;
        }

        ItemStack mainHand = target.getMainHandStack();
        if (!mainHand.isEmpty()) {
            displayItems[count++] = mainHand;
        }

        ItemStack offHand = target.getOffHandStack();
        if (!offHand.isEmpty()) {
            displayItems[count++] = offHand;
        }

        return count;
    }

    private boolean beginBurnPassIfNeeded(boolean unusualAnimation) {
        if (!unusualAnimation) {
            return false;
        }
        ensureBurnBuffer();
        if (burnBuffer == null) {
            return false;
        }

        burnBuffer.setClearColor(0f, 0f, 0f, 0f);
        burnBuffer.clear();
        burnBuffer.beginWrite(false);
        return true;
    }

    private void applyHudTransform(MatrixStack matrices, float x, float y, float width, float height, float scaleX, float scaleY) {
        float centerX = x + width * 0.5f;
        float centerY = y + height * 0.5f;
        matrices.translate(centerX, centerY, 0.0f);
        matrices.scale(scaleX, scaleY, 1.0f);
        matrices.translate(-centerX, -centerY, 0.0f);
    }

    static final class HeadParticle {
        float x;
        float y;
        float vx;
        float vy;
        float size;
        float age;
        float maxAge;
    }

    private void updateAndRenderHeadParticles(MatrixStack matrices, LivingEntity target, float headX, float headY, float headSize, float alpha, int themeColor) {
        if (target == null || alpha <= 0.02f) {
            headParticles.clear();
            particleTarget = target;
            lastTargetHurtTime = 0;
            return;
        }

        long now = System.nanoTime();
        float deltaTicks = MathHelper.clamp((now - lastParticleUpdateNs) / 1_000_000_000.0f * 60.0f, 0.2f, 3.0f);
        lastParticleUpdateNs = now;

        if (particleTarget != target) {
            headParticles.clear();
            particleTarget = target;
            lastTargetHurtTime = Math.max(0, target.hurtTime);
        }

        ThreadLocalRandom random = ThreadLocalRandom.current();
        float centerX = headX + headSize * 0.5f;
        float centerY = headY + headSize * 0.5f;
        int hurtTime = Math.max(0, target.hurtTime);
        boolean spawnBurst = hurtTime > 0 && (hurtTime > lastTargetHurtTime || hurtTime % 3 == 0);
        lastTargetHurtTime = hurtTime;

        if (spawnBurst) {
            int burstCount = 1 + random.nextInt(2);
            for (int n = 0; n < burstCount && headParticles.size() < 14; n++) {
                float angle = (float) (random.nextDouble() * Math.PI * 2.0);
                float radius = random.nextFloat() * headSize * 0.24f;
                float spreadAngle = (float) (random.nextDouble() * Math.PI * 2.0);
                float speed = 0.58f + random.nextFloat() * 0.9f;

                HeadParticle p = new HeadParticle();
                p.x = centerX + MathHelper.cos(angle) * radius;
                p.y = centerY + MathHelper.sin(angle) * radius;
                p.vx = MathHelper.cos(spreadAngle) * speed + (p.x - centerX) * 0.025f;
                p.vy = MathHelper.sin(spreadAngle) * speed + (p.y - centerY) * 0.025f;
                p.size = 3.8f + random.nextFloat() * 1.4f;
                p.age = 0.0f;
                p.maxAge = 74.0f + random.nextFloat() * 42.0f;
                headParticles.add(p);
            }
        }

        for (int i = headParticles.size() - 1; i >= 0; i--) {
            HeadParticle p = headParticles.get(i);
            p.age += deltaTicks;
            if (p.age >= p.maxAge) {
                headParticles.remove(i);
                continue;
            }

            p.x += p.vx * deltaTicks;
            p.y += p.vy * deltaTicks;
            p.vx *= (float) Math.pow(0.975f, deltaTicks);
            p.vy *= (float) Math.pow(0.975f, deltaTicks);
            p.vy += 0.0012f * deltaTicks;

            float life = 1.0f - (p.age / p.maxAge);
            float smoothLife = life * life * (3.0f - 2.0f * life);
            float particleAlpha = alpha * smoothLife;
            if (particleAlpha <= 0.02f) {
                continue;
            }

            float drawX = p.x - p.size * 0.5f;
            float drawY = p.y - p.size * 0.5f;
            int coreColor = ColorUtils.applyAlpha(themeColor, particleAlpha * 0.58f);

            RenderUtils.drawRoundedRect(matrices, drawX, drawY, p.size, p.size, p.size * 0.45f, coreColor);
        }
    }

    private void drawTargetHudItem(EventRender.Default eventRender, MatrixStack matrices, ItemStack stack,
                                   float slotX, float slotY, float itemScale) {
        if (stack.isEmpty()) return;

        matrices.push();
        // Центрируем относительно размера иконки (16x16) после масштабирования
        float scaledSize = 16.0f * itemScale;
        matrices.translate(slotX, slotY, 0);
        matrices.scale(itemScale, itemScale, 1f);
        eventRender.getContext().drawItem(stack, 0, 0);
        matrices.pop();
    }

    /**
     * Получает отображаемый текст HP с учётом скрытия
     */
    private String getHpDisplayText(LivingEntity target, float animatedHealthValue) {
        if (ScoreboardHP.shouldShowUnknownInTargetHud(target)) {
            isHpUnknown = true;
            return "???";
        }
        isHpUnknown = false;
        return String.format("%.1f", animatedHealthValue);
    }

    @Override
    public void onRender(EventRender.Default eventRender) {
        if (nursultanStyleEnabled) {
            NursultanStyle(eventRender);
        } else {
            DefaultStyle(eventRender);
        }
        super.onRender(eventRender);
    }

    public void DefaultStyle(EventRender.Default eventRender) {
        if (mc.player == null) {
            headParticles.clear();
            lastTargetHurtTime = 0;
            draggable.setWidth(0);
            draggable.setHeight(0);
            return;
        }

        Aura aura = ModuleClass.INSTANCE.aura;
        boolean chatOpen = mc.currentScreen instanceof ChatScreen;
        LivingEntity auraTarget = aura != null ? aura.getTarget() : null;
        boolean showTargetHud = chatOpen || auraTarget != null;

        alphaAnimation.setSpeed(showTargetHud ? 9.0f : 5.0f);
        alphaAnimation.update(showTargetHud ? 1.0f : 0.0f);
        float alpha = MathHelper.clamp(alphaAnimation.getValue(), 0.0f, 1.0f);

        if (showTargetHud) {
            lastTarget = chatOpen ? mc.player : auraTarget;
        }

        LivingEntity target = showTargetHud ? (chatOpen ? mc.player : auraTarget) : lastTarget;
        if (target == null || alpha <= 0.01f) {
            headParticles.clear();
            lastTargetHurtTime = 0;
            draggable.setWidth(0);
            draggable.setHeight(0);
            goldenAlphaAnimation.setValue(0.0f);
            ABValueAnimation.setValue(0.0f);
            goldenHpAnimation.setValue(0.0f);
            goldenHpTrailAnimation.setValue(0.0f);
            return;
        }

        // ── Цвет темы ─────────────────────────────────────────────────────────
        int colorTheme;
        if (!Volon.INSTANCE.themeStorage.getThemes().getTheme().getName().equals("Rainbow")) {
            colorTheme = Volon.INSTANCE.themeStorage.getThemes().getTheme().color[0];
        } else {
            colorTheme = ColorUtils.getThemeColor();
        }
        int colorThemeDark = ColorUtils.darken(colorTheme, 0.4f);
        if (showTargetHud) {
            cachedBarThemeColor = colorTheme;
            cachedBarThemeColor2 = colorThemeDark;
        }
        int barThemeColor = showTargetHud ? colorTheme : cachedBarThemeColor;
        int barThemeDark  = showTargetHud ? colorThemeDark : cachedBarThemeColor2;

        // ── HP ────────────────────────────────────────────────────────────────
        float currentAbsorption = target.getAbsorptionAmount();
        boolean hasAbsorption = currentAbsorption > 0;
        goldenAlphaAnimation.setSpeed(hasAbsorption ? 9.0f : 5.0f);
        goldenAlphaAnimation.update(hasAbsorption ? 1.0f : 0.0f);
        float goldenAlpha = MathHelper.clamp(goldenAlphaAnimation.getValue(), 0.0f, 1.0f);

        float maxHealth = Math.max(1.0f, target.getMaxHealth());
        float maxAB     = Math.max(1.0f, maxAbsorption);

        float targetHealthForAnim = ScoreboardHP.getHealth(target);
        boolean hpIsUnknown = targetHealthForAnim == ScoreboardHP.UNKNOWN_HP;
        float animHealthValue = hpIsUnknown ? 0.0f : targetHealthForAnim;

        hpValueAnimation.update(animHealthValue);
        ABValueAnimation.update(showTargetHud ? currentAbsorption : 0.0f);
        float animatedHealthValue = MathHelper.clamp(hpValueAnimation.getValue(), 0.0f, maxHealth);
        float animatedABValue     = MathHelper.clamp(ABValueAnimation.getValue(), 0.0f, maxAB);

        float healthProgress = hpIsUnknown ? 0.0f : MathHelper.clamp(targetHealthForAnim / maxHealth, 0.0f, 1.0f);
        hpAnimation.update(healthProgress);
        float hpAnim = MathHelper.clamp(hpAnimation.getValue(), 0.0f, 1.0f);

        // Trail логика: trail обновляется только при снижении HP, создавая эффект "призрака" старого значения
        if (hpAnim < hpTrailAnimation.getValue()) {
            // HP упало — trail медленно "сползает" вниз, создавая эффект "тёмного остатка"
            hpTrailAnimation.update(hpAnim);
        }
        // Если HP выросло или равно — trail НЕ меняется (остаётся на старом значении)
        float hpTrail = MathHelper.clamp(hpTrailAnimation.getValue(), 0.0f, 1.0f);
        boolean hidingHud = !showTargetHud;
        if (hidingHud) hpTrail = hpAnim;

        // ── Размеры ───────────────────────────────────────────────────────────
        float x        = draggable.getX();
        float y        = draggable.getY();
        float height   = 32.0f;             // фиксированная высота как раньше
        float headSize = 18f;               // маленькая голова
        float padLeft  = 3f;
        float padTop   = (height - headSize) * 0.5f - 3.5f; // центрируем голову по вертикали
        float gap      = 5f;
        float padRight = 5f;
        float barH     = 5.9f;             // полоска чуть шире
        float barY     = y + height - 7.5f; // полоска снизу

        String name = target.getName().getString();
        String hpLabel = hpIsUnknown ? "HP: ???" : "HP: " + String.format("%.1f", animatedHealthValue);

        Font fontName = issue(13);
        Font fontHp   = issue(12);

        float textAreaX = x + padLeft + headSize + gap;
        float nameW     = fontName.getStringWidth(name);
        float hpBaseW   = fontHp.getStringWidth(hpLabel);
        // Учитываем место под абсорбцию
        String abText = hasAbsorption && goldenAlpha > 0.01f
                ? " (+" + String.format("%.0f", animatedABValue) + ")" : "";
        float hpW = hpBaseW + fontHp.getStringWidth(abText);
        float minTextW  = Math.max(nameW, hpW);
        float width     = Math.max(90f, padLeft + headSize + gap + minTextW + padRight);

        float headX  = x + padLeft;
        float headY  = y + padTop;
        float textX  = textAreaX;
        float barX   = x;
        float barW   = width;

        int alphaInt = (int)(255 * alpha);
        MatrixStack matrices = eventRender.getContext().getMatrices();

        float scaleMul = 1f;
        if (Interface.INSTANCE != null) {
            scaleMul = Interface.INSTANCE.targetHudScale.get() / 100f;
            if (scaleMul < 0.5f) scaleMul = 0.5f;
            if (scaleMul > 2.0f) scaleMul = 2.0f;
        }

        matrices.push();

        if (scaleMul != 1f) {
            float cx = x + width * 0.5f;
            float cy = y + height * 0.5f;
            matrices.translate(cx, cy, 0);
            matrices.scale(scaleMul, scaleMul, 1f);
            matrices.translate(-cx, -cy, 0);
        }

        // ── Броня над TargetHud ───────────────────────────────────────────────
        {
            int armorCount = 0;
            for (ItemStack stack : target.getArmorItems()) {
                if (armorCount < armorScratch.length) {
                    armorScratch[armorCount++] = stack;
                }
            }
            ItemStack mainHand = target.getMainHandStack();
            ItemStack offHand  = target.getOffHandStack();

            float itemSpacing = 10f;
            float itemScale   = 0.5f * alpha;
            float armorBgH    = 11f;
            float armorBlockY = y - armorBgH - 2.5f;

            // Считаем сколько слотов брони (в порядке helmet→boots)
            int mainArmorCount = 0;
            for (int i = armorCount - 1; i >= 0; i--) {
                if (armorScratch[i] != null && !armorScratch[i].isEmpty()) mainArmorCount++;
            }
            int extraCount = 0;
            if (!mainHand.isEmpty()) extraCount++;
            if (!offHand.isEmpty()) extraCount++;

            if (mainArmorCount > 0) {
                float bgW = mainArmorCount * itemSpacing;
                drawHudBackground(matrices, x, armorBlockY, bgW, armorBgH, barThemeColor);
                float itemSize = 16f * itemScale;
                for (int i = armorCount - 1, slot = 0; i >= 0; i--) {
                    ItemStack stack = armorScratch[i];
                    if (stack != null && !stack.isEmpty()) {
                        float ix = x + slot * itemSpacing + (itemSpacing - itemSize) * 0.5f;
                        float iy = armorBlockY + (armorBgH - itemSize) * 0.5f;
                        drawTargetHudItem(eventRender, matrices, stack, ix, iy, itemScale);
                        slot++;
                    }
                }
            }

            if (extraCount > 0) {
                float extraBgX = x + mainArmorCount * itemSpacing + (mainArmorCount > 0 ? 2.5f : 0f);
                float bgW = extraCount * itemSpacing;
                drawHudBackground(matrices, extraBgX, armorBlockY, bgW, armorBgH, barThemeColor);
                float itemSize = 16f * itemScale;
                int slot = 0;
                if (!mainHand.isEmpty()) {
                    float ix = extraBgX + slot * itemSpacing + (itemSpacing - itemSize) * 0.5f;
                    float iy = armorBlockY + (armorBgH - itemSize) * 0.5f;
                    drawTargetHudItem(eventRender, matrices, mainHand, ix, iy, itemScale);
                    slot++;
                }
                if (!offHand.isEmpty()) {
                    float ix = extraBgX + slot * itemSpacing + (itemSpacing - itemSize) * 0.5f;
                    float iy = armorBlockY + (armorBgH - itemSize) * 0.5f;
                    drawTargetHudItem(eventRender, matrices, offHand, ix, iy, itemScale);
                }
            }

            // Очищаем scratch
            for (int i = 0; i < armorCount; i++) armorScratch[i] = ItemStack.EMPTY;
        }

        // ── Основной фон ──────────────────────────────────────────────────────
        int bgAlpha = (int) (255f * targetHudAlphaMul());
        if (bgAlpha < 0) bgAlpha = 0;
        if (bgAlpha > 255) bgAlpha = 255;
        if (bgAlpha > 0) {
            drawHudBlurOrRect(matrices, x, y, width, height, 4f,
                    ColorUtils.rgba(7, 7, 9, bgAlpha));
        }
        if (Interface.INSTANCE != null && Interface.INSTANCE.targetHudOutline.isState()) {
            int oa = (int) (220 * alpha);
            if (oa < 0) oa = 0; if (oa > 255) oa = 255;
            RenderUtils.drawRoundedRectOutline(matrices, x, y, width, height, 4f, 4f, 4f, 4f, 0.6f,
                    ColorUtils.rgba(0, 0, 0, oa));
        }
        if (Interface.INSTANCE != null && Interface.INSTANCE.targetHudShadow.isState()) {
            float strength = Interface.INSTANCE.targetHudShadowStrength.get();
            int sa = (int) (255f * strength / 100f * alpha);
            if (sa < 0) sa = 0; if (sa > 255) sa = 255;
            RenderUtils.drawShadowOuter(matrices, x, y, width, height, 4f, 10f,
                    ColorUtils.rgba(0, 0, 0, sa));
        }

        // ── Голова слева ──────────────────────────────────────────────────────
        if (headParticlesEnabled) {
            updateAndRenderHeadParticles(matrices, target, headX, headY, headSize, alpha, barThemeColor);
        } else {
            headParticles.clear();
        }
        if (target instanceof PlayerEntity playerEntity) {
            RenderUtils.drawPlayerHead(matrices, playerEntity.getUuid(), headX, headY, headSize, 2f, alpha, 0f);
        } else {
            RenderUtils.drawTargetHudDefaultPlaceholder(matrices, headX, headY, alpha);
            issue(14).drawCenteredString(matrices, "?", headX + headSize / 2f, headY + 3f,
                    ColorUtils.rgba(220, 220, 220, alphaInt));
        }

        // ── Ник ───────────────────────────────────────────────────────────────
        float maxTextW = Math.max(8f, width - padLeft - headSize - gap - padRight);
        fontName.drawStringWithFade(matrices, name, textX, y + 5.5f, maxTextW,
                ColorUtils.rgba(255, 255, 255, alphaInt));

        // ── Текст HP ──────────────────────────────────────────────────────────
        float hpTextY = y + 14.5f;
        if (hpIsUnknown) {
            fontHp.drawString(matrices, "HP: ", textX, hpTextY, ColorUtils.rgba(232, 232, 232, alphaInt));
            fontHp.drawString(matrices, "???", textX + fontHp.getStringWidth("HP: "), hpTextY,
                    ColorUtils.rgba(255, 200, 100, alphaInt));
        } else {
            fontHp.drawString(matrices, "HP: ", textX, hpTextY, ColorUtils.rgba(232, 232, 232, alphaInt));
            float hpValX = textX + fontHp.getStringWidth("HP: ");
            fontHp.drawString(matrices, String.format("%.1f", animatedHealthValue), hpValX, hpTextY,
                    ColorUtils.rgba(232, 232, 232, alphaInt));
            // Абсорбция золотым рядом
            if (goldenAlpha > 0.01f && !abText.isEmpty()) {
                float abX = hpValX + fontHp.getStringWidth(String.format("%.1f", animatedHealthValue));
                int goldenAlphaInt = (int)(255 * goldenAlpha * alpha);
                fontHp.drawGradientStringHorizontal(matrices, abText, abX, hpTextY,
                        ColorUtils.rgba(236, 183, 39, goldenAlphaInt),
                        ColorUtils.rgba(147, 108, 16, goldenAlphaInt));
            }
        }

        // ── HP полоска на всю ширину фона ─────────────────────────────────────
        int barBgColor = ColorUtils.rgba(30, 30, 38, (int)(200 * alpha));
        RenderUtils.drawRoundedRect(matrices, barX + 2f, barY, barW - 4f, barH, 2f, barBgColor);

        if (!hpIsUnknown) {
            // Trail
            if (!hidingHud && hpTrail > hpAnim + 0.01f) {
                float trailW = (barW - 4f) * hpTrail;
                if (trailW > 1f) {
                    int trailColor = ColorUtils.applyAlpha(ColorUtils.darken(barThemeDark, 0.6f), alpha * 0.55f);
                    RenderUtils.drawRoundedRect(matrices, barX + 2f, barY, trailW, barH, 2f, trailColor);
                }
            }
            // Заполнение
            float fillW = (barW - 4f) * hpAnim;
            if (fillW > 1f) {
                int fillColorDark, fillColorBright;
                
                // Режим "Вид полоски: Здоровье" - цвет от красного к зелёному
                if (healthBarStyleEnabled) {
                    // 0% HP = красный, 100% HP = зелёный
                    int redColor = ColorUtils.rgba(220, 50, 50, 255);      // красный
                    int yellowColor = ColorUtils.rgba(220, 200, 50, 255);  // жёлтый
                    int greenColor = ColorUtils.rgba(80, 220, 80, 255);    // зелёный
                    
                    if (hpAnim < 0.5f) {
                        // 0-50%: от красного к жёлтому
                        float ratio = hpAnim / 0.5f;
                        fillColorBright = interpolateColor(redColor, yellowColor, ratio);
                    } else {
                        // 50-100%: от жёлтого к зелёному
                        float ratio = (hpAnim - 0.5f) / 0.5f;
                        fillColorBright = interpolateColor(yellowColor, greenColor, ratio);
                    }
                    fillColorDark = ColorUtils.darken(fillColorBright, 0.4f);
                } else {
                    // Режим "Вид полоски: От темы" - используем цвет темы
                    fillColorDark = barThemeDark;
                    fillColorBright = barThemeColor;
                }
                
                RenderUtils.drawGradientRect(matrices, barX + 2f, barY, fillW, barH, 2f,
                        ColorUtils.applyAlpha(fillColorDark, alpha),
                        ColorUtils.applyAlpha(fillColorBright, alpha),
                        true);
            }
            // Абсорбция поверх — золотая
            if (goldenAlpha > 0.01f) {
                float abProgress = MathHelper.clamp(currentAbsorption / maxAB, 0f, 1f);
                goldenHpAnimation.update(abProgress);
                float goldenW = (barW - 4f) * MathHelper.clamp(goldenHpAnimation.getValue(), 0f, 1f);
                if (goldenW > 1f) {
                    RenderUtils.drawGradientRect(matrices, barX + 2f, barY, goldenW, barH, 2f,
                            ColorUtils.applyAlpha(ColorUtils.rgba(147, 108, 16, 255), goldenAlpha * alpha),
                            ColorUtils.applyAlpha(ColorUtils.rgba(236, 183, 39, 255), goldenAlpha * alpha),
                            true);
                }
            }
        } else {
            RenderUtils.drawRoundedRect(matrices, barX + 2f, barY, (barW - 4f) * 0.5f, barH, 2f,
                    ColorUtils.applyAlpha(ColorUtils.rgba(80, 80, 80, 255), alpha * 0.5f));
        }

        matrices.pop();

        draggable.setWidth(width * scaleMul);
        draggable.setHeight(height * scaleMul);
    }

    // ── Nursultan Style ───────────────────────────────────────────────────────

    public void NursultanStyle(EventRender.Default eventRender) {
        if (mc.player == null) {
            draggable.setWidth(0);
            draggable.setHeight(0);
            return;
        }

        Aura aura = ModuleClass.INSTANCE.aura;
        boolean chatOpen = mc.currentScreen instanceof ChatScreen;
        LivingEntity auraTarget = aura != null ? aura.getTarget() : null;
        boolean showTargetHud = chatOpen || auraTarget != null;

        alphaAnimation.setSpeed(showTargetHud ? 9.0f : 5.0f);
        alphaAnimation.update(showTargetHud ? 1.0f : 0.0f);
        float alpha = MathHelper.clamp(alphaAnimation.getValue(), 0.0f, 1.0f);

        if (showTargetHud) {
            lastTarget = chatOpen ? mc.player : auraTarget;
        }

        LivingEntity target = showTargetHud ? (chatOpen ? mc.player : auraTarget) : lastTarget;
        if (target == null || alpha <= 0.01f) {
            draggable.setWidth(0);
            draggable.setHeight(0);
            return;
        }

        int themeColor;
        if (!Volon.INSTANCE.themeStorage.getThemes().getTheme().getName().equals("Rainbow")) {
            themeColor = Volon.INSTANCE.themeStorage.getThemes().getTheme().color[0];
        } else {
            themeColor = ColorUtils.getThemeColor();
        }
        int themeColorDark = ColorUtils.darken(themeColor, 0.5f);

        float x = draggable.getX();
        float y = draggable.getY();
        float w = 112f;
        float h = 44f; // увеличена для строки брони

        float hp = ScoreboardHP.getHealth(target);
        boolean hpUnknown = hp == ScoreboardHP.UNKNOWN_HP;
        float maxHp = Math.max(1f, target.getMaxHealth());
        float currentAbsorption = target.getAbsorptionAmount();
        boolean hasAbsorption = currentAbsorption > 0;

        // Анимация абсорбции
        goldenAlphaAnimation.setSpeed(hasAbsorption ? 9.0f : 5.0f);
        goldenAlphaAnimation.update(hasAbsorption ? 1.0f : 0.0f);
        float goldenAlpha = MathHelper.clamp(goldenAlphaAnimation.getValue(), 0.0f, 1.0f);

        // Анимируем ширину полоски HP (68px макс)
        float barMaxW = 68f;
        float barH = 4f; // шире
        float targetBarW = hpUnknown ? 2f : MathHelper.clamp((hp / maxHp) * barMaxW, 2f, barMaxW);
        nursultanAnimatedHealth += (targetBarW - nursultanAnimatedHealth) * 0.1f;
        nursultanAnimatedHealth = MathHelper.clamp(nursultanAnimatedHealth, 2f, barMaxW);

        // Trail логика: trail обновляется только при снижении HP, создавая эффект "призрака" старого значения
        if (nursultanAnimatedHealth < nursultanTrailHealth) {
            // HP упало — trail медленно "сползает" вниз, создавая эффект "тёмного остатка"
            nursultanTrailHealth += (nursultanAnimatedHealth - nursultanTrailHealth) * 0.05f;
        }
        // Если HP выросло или равно — trail НЕ меняется (остаётся на старом значении)
        nursultanTrailHealth = MathHelper.clamp(nursultanTrailHealth, 2f, barMaxW);

        // Анимируем ширину полоски абсорбции
        float maxAB = Math.max(1f, maxAbsorption);
        float absorptionProgress = MathHelper.clamp(currentAbsorption / maxAB, 0f, 1f);
        goldenHpAnimation.update(absorptionProgress);
        float goldenBarW = MathHelper.clamp(goldenHpAnimation.getValue(), 0f, 1f) * barMaxW;

        int alphaInt = (int)(255 * alpha);
        MatrixStack matrices = eventRender.getContext().getMatrices();

        matrices.push();

        // Масштаб появления
        float cx = x + w / 2f;
        float cy = y + h / 2f;
        matrices.translate(cx, cy, 0f);
        matrices.scale(alpha, alpha, 1f);
        matrices.translate(-cx, -cy, 0f);

        // Фон панели
        if (isBlurEnabled()) {
            BLUR.render(matrices, x, y, w, h, 4f, BLUR_STRENGTH);
        }
        float bgAlphaMul = alpha * targetHudAlphaMul();
        if (bgAlphaMul > 0.001f) {
            RenderUtils.drawDefaultHudPanel(matrices, x, y, w, h, 4f, 4.5f,
                    ColorUtils.applyAlpha(ColorUtils.rgba(50, 50, 50, 255), bgAlphaMul),
                    ColorUtils.applyAlpha(ColorUtils.darken(themeColor, 0.15f), bgAlphaMul),
                    ColorUtils.applyAlpha(ColorUtils.darken(themeColor, 0.05f), bgAlphaMul),
                    true,
                    getHudRectStyle());
        }
        if (Interface.INSTANCE != null && Interface.INSTANCE.targetHudOutline.isState()) {
            int oa = (int) (220 * alpha);
            if (oa < 0) oa = 0; if (oa > 255) oa = 255;
            RenderUtils.drawRoundedRectOutline(matrices, x, y, w, h, 4f, 4f, 4f, 4f, 0.6f,
                    ColorUtils.rgba(0, 0, 0, oa));
        }
        if (Interface.INSTANCE != null && Interface.INSTANCE.targetHudShadow.isState()) {
            float strength = Interface.INSTANCE.targetHudShadowStrength.get();
            int sa = (int) (255f * strength / 100f * alpha);
            if (sa < 0) sa = 0; if (sa > 255) sa = 255;
            RenderUtils.drawShadowOuter(matrices, x, y, w, h, 4f, 10f,
                    ColorUtils.rgba(0, 0, 0, sa));
        }

        // Голова игрока (22x22, центрирована по высоте панели)
        float headX = x + 6f;
        float headY = y + (h - 22f) / 2f;
        if (target instanceof PlayerEntity) {
            RenderUtils.drawPlayerHead(matrices, target.getUuid(), headX, headY, 22f, 2f, alpha, 0f);
        } else {
            RenderUtils.drawTargetHudDefaultPlaceholder(matrices, headX, headY, alpha);
            issue(20).drawCenteredString(matrices, "?", headX + 11f, headY + 5f,
                    ColorUtils.rgba(220, 220, 220, alphaInt));
        }

        // Имя (строка 1)
        String name = target.getName().getString();
        float maxNameW = 50f;
        if (issue(13).getWidth(name) > maxNameW) {
            while (issue(13).getWidth(name + "...") > maxNameW && name.length() > 0)
                name = name.substring(0, name.length() - 1);
            name += "...";
        }
        issue(13).drawString(matrices, name, x + 35f, y + 7f,
                ColorUtils.rgba(255, 255, 255, alphaInt));

        // HP текст (правый край, строка 1)
        String hpText = hpUnknown ? "???" : ((int) Math.round(hp)) + ".0";
        Font f12 = issue(12);
        float hpTextW = f12.getStringWidth(hpText);
        f12.drawString(matrices, hpText, x + w - 7f - hpTextW, y + 7f,
                ColorUtils.rgba(255, 255, 255, alphaInt));

        // Абсорбция рядом с HP
        if (goldenAlpha > 0.01f && !hpUnknown) {
            String abText = "+" + (int) Math.round(currentAbsorption);
            float abTextX = x + w - 7f - hpTextW - f12.getStringWidth(abText) - 2f;
            int goldenAlphaInt = (int)(255 * goldenAlpha * alpha);
            f12.drawGradientStringHorizontal(matrices, abText, abTextX, y + 7f,
                    ColorUtils.rgba(236, 183, 39, goldenAlphaInt),
                    ColorUtils.rgba(147, 108, 16, goldenAlphaInt));
        }

        // ── Строка 2: Иконка + Броня ──────────────────────────────────────────
        float row2Y = y + 18f;

        // Иконка сердца под HP (правый край)
        Font iconFont = Fonts.getFont("iconnew", 11);
        if (iconFont != null) {
            iconFont.drawGradientStringHorizontal(matrices, "m",
                    x + w - 9f, row2Y,
                    ColorUtils.applyAlpha(themeColor, alphaInt),
                    ColorUtils.applyAlpha(themeColorDark, alphaInt));
        }

        // Броня: 4 слота по 8px, начиная от x+35
        float itemSize  = 8f;
        float itemGap   = 2f;
        float itemScale = 0.5f * alpha;
        float itemY     = row2Y - 1f;

        // armorItems порядок: [feet, legs, chest, head] — рисуем [head, chest, legs, feet]
        int armorIdx = 0;
        for (ItemStack stack : target.getArmorItems()) {
            if (armorIdx < armorScratch.length) {
                armorScratch[armorIdx++] = stack;
            }
        }
        for (int i = 0; i < 4; i++) {
            ItemStack stack = (armorIdx > 0) ? armorScratch[3 - i] : ItemStack.EMPTY;
            if (stack == null) stack = ItemStack.EMPTY;
            float slotX = x + 35f + i * (itemSize + itemGap);
            if (!stack.isEmpty()) {
                drawTargetHudItem(eventRender, matrices, stack, slotX, itemY, itemScale);
            }
            if (armorIdx > 0) armorScratch[3 - i] = ItemStack.EMPTY;
        }

        // ── Строка 3: Полоска HP ──────────────────────────────────────────────
        float barY = y + h - 8f;

        // Фон полоски HP
        RenderUtils.drawRoundedRect(matrices, x + 34f, barY, barMaxW, barH, 1.5f,
                ColorUtils.rgba(38, 34, 50, (int)(235 * alpha)));

        // Trail (тёмный остаток предыдущего HP)
        if (nursultanTrailHealth > nursultanAnimatedHealth + 1f) {
            RenderUtils.drawGradientRect(matrices, x + 34f, barY, nursultanTrailHealth, barH, 1.5f,
                    ColorUtils.applyAlpha(ColorUtils.darken(themeColorDark, 0.5f), alpha * 0.6f),
                    ColorUtils.applyAlpha(ColorUtils.darken(themeColor, 0.5f), alpha * 0.6f),
                    true);
        }

        // Заполненная полоска HP с градиентом темы или по здоровью
        if (nursultanAnimatedHealth > 1f) {
            int fillColorDark, fillColorBright;
            
            // Режим "Вид полоски: Здоровье" - цвет от красного к зелёному
            if (healthBarStyleEnabled) {
                // Вычисляем процент HP
                float hpPercent = hpUnknown ? 0f : MathHelper.clamp(nursultanAnimatedHealth / barMaxW, 0f, 1f);
                
                // 0% HP = красный, 100% HP = зелёный
                int redColor = ColorUtils.rgba(220, 50, 50, 255);      // красный
                int yellowColor = ColorUtils.rgba(220, 200, 50, 255);  // жёлтый
                int greenColor = ColorUtils.rgba(80, 220, 80, 255);    // зелёный
                
                if (hpPercent < 0.5f) {
                    // 0-50%: от красного к жёлтому
                    float ratio = hpPercent / 0.5f;
                    fillColorBright = interpolateColor(redColor, yellowColor, ratio);
                } else {
                    // 50-100%: от жёлтого к зелёному
                    float ratio = (hpPercent - 0.5f) / 0.5f;
                    fillColorBright = interpolateColor(yellowColor, greenColor, ratio);
                }
                fillColorDark = ColorUtils.darken(fillColorBright, 0.4f);
            } else {
                // Режим "Вид полоски: От темы" - используем цвет темы
                fillColorDark = themeColorDark;
                fillColorBright = themeColor;
            }
            
            RenderUtils.drawGradientRect(matrices, x + 34f, barY, nursultanAnimatedHealth, barH, 1.5f,
                    ColorUtils.applyAlpha(fillColorDark, alpha),
                    ColorUtils.applyAlpha(fillColorBright, alpha),
                    true);
        }

        // Полоска абсорбции (золотая, поверх HP)
        if (goldenAlpha > 0.01f && goldenBarW > 1f) {
            float goldenBarAlpha = goldenAlpha * alpha;
            RenderUtils.drawGradientRect(matrices, x + 34f, barY, goldenBarW, barH, 1.5f,
                    ColorUtils.applyAlpha(ColorUtils.rgba(147, 108, 16, 255), goldenBarAlpha),
                    ColorUtils.applyAlpha(ColorUtils.rgba(236, 183, 39, 255), goldenBarAlpha),
                    true);
        }

        matrices.pop();

        draggable.setWidth(w);
        draggable.setHeight(h);
    }

    private final AnimationUtils scaleAnimation = new AnimationUtils(0.0f, 12.0f, Easings.BACK_OUT);
    private final AnimationUtils hideScaleAnimation = new AnimationUtils(1.0f, 8.0f, Easings.BACK_IN);
    private boolean wasVisible = false;

    public void WaveStyle(EventRender.Default eventRender) {
        if (mc.player == null) {
            draggable.setWidth(0);
            draggable.setHeight(0);
            return;
        }

        Aura aura = ModuleClass.INSTANCE.aura;
        boolean chatOpen = mc.currentScreen instanceof ChatScreen;
        LivingEntity auraTarget = aura != null ? aura.getTarget() : null;
        boolean showTargetHud = chatOpen || auraTarget != null;
        alphaAnimation.update(showTargetHud ? 1.0f : 0.0f);

        if (showTargetHud && !wasVisible) {
            scaleAnimation.setValue(0.0f);
            hideScaleAnimation.setValue(1.0f);
            wasVisible = true;
        } else if (!showTargetHud && wasVisible) {
            hideScaleAnimation.setValue(1.0f);
            wasVisible = false;
        }

        if (showTargetHud) {
            scaleAnimation.update(1.0f);
            lastTarget = chatOpen ? mc.player : auraTarget;
        } else {
            hideScaleAnimation.update(0.0f);
        }

        float scale;
        if (showTargetHud) {
            scale = MathHelper.clamp(scaleAnimation.getValue(), 0.0f, 1.0f);
        } else {
            scale = MathHelper.clamp(hideScaleAnimation.getValue(), 0.0f, 1.0f);
        }
        float progress = scale;

        LivingEntity target = showTargetHud ? (chatOpen ? mc.player : auraTarget) : lastTarget;
        if (target == null || progress <= 0.01f) {
            draggable.setWidth(0);
            draggable.setHeight(0);
            return;
        }

        float x = draggable.getX(), y = draggable.getY();
        float padding = 3f;
        float width = 110;
        float height = 46;

        float health = ScoreboardHP.getHealth(target);
        boolean hpIsUnknown = health == ScoreboardHP.UNKNOWN_HP;
        float maxHealth = Math.max(1.0f, target.getMaxHealth());

        float animHealthValue = hpIsUnknown ? 0.0f : health;
        hpValueAnimation.update(animHealthValue);
        float animatedHealthValue = MathHelper.clamp(hpValueAnimation.getValue(), 0.0f, maxHealth);
        float healthProgress = hpIsUnknown ? 0.0f : MathHelper.clamp(health / maxHealth, 0.0f, 1.0f);
        hpAnimation.update(healthProgress);
        float hpProgressAnimated = MathHelper.clamp(hpAnimation.getValue(), 0.0f, 1.0f);

        if (hpProgressAnimated > hpTrailAnimation.getValue()) {
            hpTrailAnimation.setValue(hpProgressAnimated);
        } else {
            hpTrailAnimation.update(hpProgressAnimated);
        }

        MatrixStack matrices = eventRender.getContext().getMatrices();
        matrices.push();
        applyHudTransform(matrices, x, y, width, height, scale, scale);
        float visualAlpha = progress;
        int alphaInt = (int)(255 * visualAlpha);

        float entityBoxSize = height - padding * 2 - 4;

        RenderUtils.drawTargetHudWaveFrame(matrices, x, y, width, height, padding, entityBoxSize, visualAlpha);

        matrices.pop();

        int entityX = (int)(x + padding + 3f);
        int entityY = (int)(y + padding + 3f);
        int entityX2 = (int)(x + padding + 3f + entityBoxSize - 2);
        int entityY2 = (int)(y + padding + 3f + entityBoxSize - 2);
        int entitySize = (int)(15 * progress);

        float entityCenterX = x + padding + 3f + (entityBoxSize - 2) / 2;
        float entityCenterY = y + padding + 3f + (entityBoxSize - 2) / 3;

        float yaw = target.bodyYaw;
        float pitch = target.getPitch();

        double yawRadians = Math.toRadians(yaw + 180);
        float lookX = entityCenterX + (float)(Math.sin(yawRadians) * 50);
        float lookY = entityCenterY - pitch;

        if (progress > 0.5f) {
            InventoryScreen.drawEntity(
                    eventRender.getContext(),
                    entityX,
                    entityY,
                    entityX2,
                    entityY2,
                    entitySize,
                    0.0f,
                    lookX,
                    lookY,
                    target
            );
        }

        matrices.push();
        applyHudTransform(matrices, x, y, width, height, scale, scale);

        String name = target.getName().getString();
        float textX = x + padding + entityBoxSize + 6;
        float waveNameFadeMaxWidth = Math.max(8.0f, (x + width - padding - 4.0f) - textX);

        issue(14).drawStringWithFade(matrices, name, textX, y + padding + 5, waveNameFadeMaxWidth, ColorUtils.rgba(255, 255, 255, alphaInt));

        // В WaveStyle тоже учитываем скрытие HP
        if (hpIsUnknown) {
            issue(14).draw(matrices, "HP: Неизвестно | Dist: " + (int) target.distanceTo(mc.player), textX, y + padding + 20,
                    ColorUtils.rgba(255, 200, 100, alphaInt));
        } else {
            issue(14).draw(matrices, "HP: " + String.format("%.1f", animatedHealthValue) + " | Dist: " + (int) target.distanceTo(mc.player), textX, y + padding + 20,
                    ColorUtils.rgba(255, 255, 255, alphaInt));
        }

        float heartsX = textX;
        float heartsY = y + padding + 15;
        float heartSize = 5f;
        float heartSpacing = 0.5f;
        int totalHearts = 10;
        float healthPerHeart = maxHealth / totalHearts;
        float currentHealth = hpIsUnknown ? 0 : health;

        int heartColor;
        if (hpIsUnknown) {
            heartColor = ColorUtils.rgba(128, 128, 128, alphaInt); // Серый для неизвестного HP
        } else if (health <= maxHealth * 0.25f) {
            heartColor = ColorUtils.rgba(255, 50, 50, alphaInt);
        } else if (health <= maxHealth * 0.5f) {
            heartColor = ColorUtils.rgba(255, 220, 0, alphaInt);
        } else {
            heartColor = ColorUtils.rgba(0, 255, 0, alphaInt);
        }

        int shadowColor = ColorUtils.applyAlpha(heartColor, visualAlpha * 0.5f);

        for (int i = 0; i < totalHearts; i++) {
            float hx = heartsX + i * (heartSize + heartSpacing);
            float hy = heartsY;

            RenderUtils.drawTargetHudHeartBase(matrices, hx, hy - 3, visualAlpha);

            if (currentHealth > 0 && !hpIsUnknown) {
                float fillAmount = MathHelper.clamp(currentHealth / healthPerHeart, 0.0f, 1.0f);
                float filledWidth = heartSize * fillAmount;

                if (filledWidth > 0) {
                    RenderUtils.drawTargetHudHeartFill(matrices, hx, hy - 3, filledWidth, heartColor, shadowColor);
                }

                currentHealth -= healthPerHeart;
            }
        }

        float itemX = textX - 1;
        float itemY = y + padding + 28;
        float itemSpacing = 10;
        int waveThemeColor = !Volon.INSTANCE.themeStorage.getThemes().getTheme().getName().equals("Rainbow")
                ? Volon.INSTANCE.themeStorage.getThemes().getTheme().color[0]
                : ColorUtils.getThemeColor();
        int waveSlotBorderColor = ColorUtils.applyAlpha(ColorUtils.rgba(50, 50, 50, 255), visualAlpha);
        int waveSlotTopColor = ColorUtils.applyAlpha(ColorUtils.darken(waveThemeColor, 0.15f), visualAlpha);
        int waveSlotBottomColor = ColorUtils.applyAlpha(ColorUtils.darken(waveThemeColor, 0.05f), visualAlpha);

        int totalSlots = collectDisplayItems(target);

        if (totalSlots > 0) {
            float containerX = itemX - 0.85f;
            float containerY = itemY - 0.85f;
            float containerW = (totalSlots - 1) * itemSpacing + 9.8f;
            float containerH = 9.8f;
        }

        float itemScale = 0.5f;
        for (int itemIndex = 0; itemIndex < totalSlots; itemIndex++) {
            ItemStack stack = displayItems[itemIndex];
            if (!stack.isEmpty()) {
                float slotX = itemX + itemIndex * itemSpacing;
                drawTargetHudItem(eventRender, matrices, stack, slotX, itemY, itemScale);
            }
            displayItems[itemIndex] = ItemStack.EMPTY;
        }

        matrices.pop();

        draggable.setWidth(width);
        draggable.setHeight(height);
    }
}
package antileak.base.client.modules.impl.render.base.implement;

import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import antileak.base.Punch;
import antileak.base.api.events.implement.EventRender;
import antileak.base.api.storages.implement.helpertstorages.enumvar.ModuleClass;
import antileak.base.api.utils.animation.AnimationUtils;
import antileak.base.api.utils.animation.Easings;
import antileak.base.api.utils.color.ColorUtils;
import antileak.base.api.utils.draggable.Draggable;
import antileak.base.api.utils.render.RenderUtils;
import antileak.base.api.utils.render.fonts.msdf.Font;
import antileak.base.api.utils.render.fonts.msdf.Fonts;
import antileak.base.client.modules.impl.combat.Aura;
import antileak.base.api.utils.scissor.ScissorUtils;
import antileak.base.client.modules.impl.render.base.InterfaceProcessing;

public class TargetHudV2 extends InterfaceProcessing {

    private final AnimationUtils alphaAnimation = new AnimationUtils(0.0f, 9.0f, Easings.QUAD_OUT);
    private final AnimationUtils hpAnimation = new AnimationUtils(1.0f, 9.2f, Easings.QUAD_OUT);
    private final AnimationUtils hpValueAnimation = new AnimationUtils(20.0f, 7.0f, Easings.QUAD_OUT);
    private final AnimationUtils absorptionAnimation = new AnimationUtils(0.0f, 9.2f, Easings.QUAD_OUT);

    private static final int MAX_DIGITS = 3;
    private final float[] digitOffsetY = new float[MAX_DIGITS];
    private final float[] digitAlpha = new float[MAX_DIGITS];
    private final long[] digitChangeTime = new long[MAX_DIGITS];
    private final char[] lastDigits = new char[MAX_DIGITS];
    private int lastRoundedHp = -1;

    private LivingEntity lastTarget;
    private final ItemStack[] displayItems = new ItemStack[6];
    private final ItemStack[] armorScratch = new ItemStack[4];

    private boolean headParticlesEnabled = true;
    private boolean healthBarStyleEnabled = false;

    public TargetHudV2(Draggable draggable) {
        super(draggable);
    }

    public boolean isHeadParticlesEnabled() {
        return headParticlesEnabled;
    }

    public void setHeadParticlesEnabled(boolean enabled) {
        this.headParticlesEnabled = enabled;
    }

    public boolean isHealthBarStyleEnabled() {
        return healthBarStyleEnabled;
    }

    public void setHealthBarStyleEnabled(boolean enabled) {
        this.healthBarStyleEnabled = enabled;
    }

    private Font iconFont(int size) {
        return Fonts.getFont("icon1", size);
    }

    private Font issue(int size) {
        return Fonts.getFont("suisse", size);
    }

    private int getThemeColor() {
        if (!Punch.INSTANCE.themeStorage.getThemes().getTheme().getName().equals("Rainbow")) {
            return Punch.INSTANCE.themeStorage.getThemes().getTheme().color[0];
        }
        return ColorUtils.getThemeColor();
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
        while (count < 4) {
            displayItems[count++] = ItemStack.EMPTY;
        }

        displayItems[count++] = target.getOffHandStack();
        displayItems[count++] = target.getMainHandStack();

        return count;
    }

    private float getDurabilityProgress(ItemStack stack) {
        if (stack.isEmpty() || !stack.isDamageable()) {
            return 1.0f;
        }
        int maxDamage = stack.getMaxDamage();
        if (maxDamage <= 0) {
            return 1.0f;
        }
        return MathHelper.clamp(1.0f - (float) stack.getDamage() / maxDamage, 0.0f, 1.0f);
    }

    private int getDurabilityColor(float progress, int alpha) {
        int r, g, b;

        if (progress > 0.6f) {
            float t = (progress - 0.6f) / 0.4f;
            r = (int) (255 * (1f - t));
            g = 200;
            b = 60;
        } else if (progress > 0.3f) {
            float t = (progress - 0.3f) / 0.3f;
            r = 255;
            g = (int) (200 * t);
            b = 0;
        } else {
            r = 220;
            g = (int) (60 * (progress / 0.3f));
            b = 0;
        }

        return ColorUtils.rgba(r, g, b, alpha);
    }

    private void drawItem(EventRender.Default eventRender, MatrixStack matrices, ItemStack stack,
                          float slotX, float slotY, float slotSize, float itemScale) {
        if (stack.isEmpty()) {
            return;
        }

        float itemScreenSize = 16f * itemScale;
        float offsetX = slotX + (slotSize - itemScreenSize) / 2f;
        float offsetY = slotY + (slotSize - itemScreenSize) / 2f;

        matrices.push();
        matrices.translate(offsetX, offsetY, 0);
        matrices.scale(itemScale, itemScale, 1f);
        eventRender.getContext().drawItem(stack, 0, 0);
        matrices.pop();
    }

    private void drawHealthRing(MatrixStack matrices, float centerX, float centerY, float size,
                                float progress, int ringColor, int bgRingColor, float alpha) {
        float ringSize = size;
        float ringX = centerX - ringSize * 0.5f;
        float ringY = centerY - ringSize * 0.5f;
        float thickness = 2.2f;

        RenderUtils.drawRingArc(matrices, ringX, ringY, ringSize, thickness, 0f, 360f,
                ColorUtils.applyAlpha(bgRingColor, alpha));

        float sweep = MathHelper.clamp(progress, 0.0f, 1.0f) * 360f;
        if (sweep > 0.5f) {
            RenderUtils.drawRingArc(matrices, ringX, ringY, ringSize, thickness, 90f, 90f + sweep,
                    ColorUtils.applyAlpha(ringColor, alpha));
        }
    }

    @Override
    public void onRender(EventRender.Default eventRender) {
        if (mc.player == null) {
            draggable.setWidth(0);
            draggable.setHeight(0);
            return;
        }

        Aura aura = ModuleClass.aura;
        boolean chatOpen = mc.currentScreen instanceof ChatScreen;
        LivingEntity auraTarget = aura != null ? aura.getTarget() : null;
        boolean showHud = chatOpen || auraTarget != null;

        alphaAnimation.setSpeed(showHud ? 9.0f : 5.0f);
        alphaAnimation.update(showHud ? 1.0f : 0.0f);
        float alpha = MathHelper.clamp(alphaAnimation.getValue(), 0.0f, 1.0f);

        if (showHud) {
            lastTarget = chatOpen ? mc.player : auraTarget;
        }

        LivingEntity target = showHud ? (chatOpen ? mc.player : auraTarget) : lastTarget;

        if (target == null || alpha <= 0.01f) {
            draggable.setWidth(0);
            draggable.setHeight(0);
            return;
        }

        float maxHealth = Math.max(1.0f, target.getMaxHealth());
        float targetHealth = showHud ? target.getHealth() : 0.0f;

        hpValueAnimation.update(targetHealth);
        float animatedHealth = MathHelper.clamp(hpValueAnimation.getValue(), 0.0f, maxHealth);

        float healthProgress = MathHelper.clamp(targetHealth / maxHealth, 0.0f, 1.0f);
        hpAnimation.update(healthProgress);
        float hpProgressAnimated = MathHelper.clamp(hpAnimation.getValue(), 0.0f, 1.0f);

        float absorptionAmount = showHud ? target.getAbsorptionAmount() : 0.0f;
        float absorptionProgress = absorptionAmount > 0.0f
                ? MathHelper.clamp(absorptionAmount / Math.max(1.0f, maxHealth), 0.0f, 1.0f)
                : 0.0f;

        absorptionAnimation.update(absorptionProgress);
        float absorptionProgressAnimated = MathHelper.clamp(absorptionAnimation.getValue(), 0.0f, 1.0f);

        int themeColor = getThemeColor();
        String name = target.getName().getString();

        int roundedHp = Math.round(animatedHealth);
        long now = System.currentTimeMillis();

        String hpText = String.valueOf(roundedHp);
        char[] curDigits = new char[MAX_DIGITS];
        for (int i = 0; i < MAX_DIGITS; i++) curDigits[i] = ' ';

        for (int i = 0; i < hpText.length(); i++) {
            curDigits[MAX_DIGITS - hpText.length() + i] = hpText.charAt(i);
        }

        if (lastRoundedHp == -1) {
            System.arraycopy(curDigits, 0, lastDigits, 0, MAX_DIGITS);
            for (int i = 0; i < MAX_DIGITS; i++) {
                digitOffsetY[i] = 0f;
                digitAlpha[i] = 1f;
                digitChangeTime[i] = now - 1000L;
            }
        } else if (roundedHp != lastRoundedHp) {
            boolean isDamage = roundedHp < lastRoundedHp;

            for (int i = 0; i < MAX_DIGITS; i++) {
                if (curDigits[i] != lastDigits[i]) {
                    digitOffsetY[i] = isDamage ? 6f : -6f;
                    digitAlpha[i] = 0f;
                    digitChangeTime[i] = now;
                }
            }

            System.arraycopy(curDigits, 0, lastDigits, 0, MAX_DIGITS);
        }

        lastRoundedHp = roundedHp;

        for (int i = 0; i < MAX_DIGITS; i++) {
            float el = (now - digitChangeTime[i]) / 1000f;
            float t = Math.min(1f, el / 0.28f);
            float ease = 1f - (1f - t) * (1f - t) * (1f - t);
            float fadeT = Math.min(1f, el / 0.22f);

            digitOffsetY[i] = digitOffsetY[i] * (1f - ease);
            digitAlpha[i] = (float) Math.sin(fadeT * Math.PI * 0.5f);
        }

        float x = draggable.getX();
        float y = draggable.getY();

        float padding = 4.5f;
        float headSize = 20.0f;
        float healthRingSize = 18.0f;
        float healthGap = 6.0f;
        float itemSize = 10.0f;
        float itemGap = 0.8f;
        float durBarH = 2.5f;
        float nameFontSize = 13.0f;
        float hpFontSize = 12.0f;

        int itemCount = collectDisplayItems(target);
        float itemsRowW = itemCount > 0 ? itemCount * itemSize + (itemCount - 1) * itemGap : 0.0f;

        float nameWidth = issue((int) nameFontSize).getWidth(name);
        float contentW = Math.max(Math.max(nameWidth, itemsRowW), 55.0f);

        float width = padding + headSize + 2.0f + contentW + healthGap + healthRingSize;

        float contentTop = y + padding;
        float headX = x + padding;
        float textX = headX + headSize + 2.0f;

        float nameY = contentTop + 4.0f;
        float itemsY = nameY + 5.0f;
        float durBarY = itemsY + itemSize;

        float contentHeight = Math.max(headSize, durBarY + durBarH - contentTop);
        float height = padding * 2.0f + contentHeight;

        float headY = y + height * 0.5f - headSize * 0.5f;

        float healthCenterX = x + width - padding - healthRingSize * 0.5f;
        float healthCenterY = y + height * 0.5f;

        int drawAlpha = (int) (255 * alpha);

        MatrixStack matrices = eventRender.getContext().getMatrices();
        matrices.push();

        // Используем стиль фона как у других таргет худов
        RenderUtils.drawShadow(matrices, x, y, width, height, 6f, 12f, ColorUtils.setAlphaColor(themeColor, (int)(80 * alpha)));
        RenderUtils.drawRoundedRect(matrices, x, y, width, height, 6f, ColorUtils.rgba(20, 20, 20, (int)(230 * alpha)));
        RenderUtils.drawRoundedRect(matrices, x + width / 2f - 12f, y + height - 2f, 24f, 2f, 1f, ColorUtils.setAlphaColor(themeColor, (int)(255 * alpha)));
        
        // Добавляем квадратный паттерн если включен необычный режим
        boolean drawSquares = isUnusualRectType();
        if (drawSquares && alpha > 0.06f) {
            RenderUtils.drawHudSquarePattern(matrices, x + 5.0f, y, width, height, ColorUtils.applyAlpha(themeColor, alpha));
        }

        float hurtPercent = 0f;
        if (target.hurtTime > 0) {
            hurtPercent = MathHelper.clamp((target.hurtTime / 10.0f) * 0.55f, 0f, 0.55f);
        }

        if (target instanceof PlayerEntity playerEntity) {
            RenderUtils.drawPlayerHead(matrices, playerEntity.getUuid(), headX, headY, headSize, 2.5f, alpha, hurtPercent);
        } else {
            RenderUtils.drawTargetHudDefaultPlaceholder(matrices, headX, headY, alpha);
        }

        // Сдвигаем никнейм чуть правее (на 3 пикселя) для выравнивания с первой иконкой
        issue((int) nameFontSize).drawString(matrices, name, textX + 3.0f, nameY,
                ColorUtils.rgba(255, 255, 255, drawAlpha));

        for (int i = 0; i < itemCount; i++) {
            float slotX = textX + i * (itemSize + itemGap);
            ItemStack stack = displayItems[i];
            displayItems[i] = ItemStack.EMPTY;

            if (stack == null || stack.isEmpty()) {
                Font iFont = iconFont(8);
                if (iFont != null) {
                    String glyph = "u";
                    float gw = iFont.getWidth(glyph);
                    int emptyColor = ColorUtils.rgba(80, 85, 100, (int) (140 * alpha));
                    iFont.drawString(matrices, glyph,
                            slotX + (itemSize - gw) * 0.5f,
                            itemsY + 5.0f,
                            emptyColor);
                }
                continue;
            }

            drawItem(eventRender, matrices, stack, slotX, itemsY, itemSize, 0.45f);

            float durProgress = getDurabilityProgress(stack);
            if (stack.isDamageable() && stack.getDamage() > 0) {
                float durW = itemSize * 0.75f;
                float durOffsetX = (itemSize - durW) * 0.5f;

                int durBg = ColorUtils.rgba(45, 48, 58, (int) (200 * alpha));
                int durFill = getDurabilityColor(durProgress, drawAlpha);

                RenderUtils.drawRoundedRect(matrices, slotX + durOffsetX, durBarY, durW, durBarH, 0.5f, durBg);

                if (durProgress > 0.01f) {
                    RenderUtils.drawRoundedRect(matrices, slotX + durOffsetX, durBarY, durW * durProgress, durBarH, 0.5f, durFill);
                }
            }
        }

        int tr = (themeColor >> 16) & 0xFF;
        int tg = (themeColor >> 8) & 0xFF;
        int tb = themeColor & 0xFF;

        int ringBg = ColorUtils.rgba(tr / 5, tg / 5, tb / 5, drawAlpha);

        drawHealthRing(matrices, healthCenterX, healthCenterY, healthRingSize, hpProgressAnimated, themeColor, ringBg, alpha);

        if (absorptionProgressAnimated > 0.01f) {
            float ringX = healthCenterX - healthRingSize * 0.5f;
            float ringY = healthCenterY - healthRingSize * 0.5f;
            float absorbSweep = absorptionProgressAnimated * 360f;

            int goldColor = ColorUtils.applyAlpha(ColorUtils.rgba(255, 200, 50, 255), alpha);
            RenderUtils.drawRingArc(matrices, ringX, ringY, healthRingSize, 2.2f, 90f, 90f + absorbSweep, goldColor);
        }

        float hpTextH = hpFontSize * 0.08f;
        float totalW = issue((int) hpFontSize).getWidth(hpText);
        float startX = healthCenterX - totalW * 0.5f;
        float baseY = healthCenterY - hpTextH;

        float clipPad = 1.0f;
        ScissorUtils.push();
        ScissorUtils.setFromComponentCoordinates(
                healthCenterX - healthRingSize * 0.5f - clipPad,
                healthCenterY - healthRingSize * 0.5f - clipPad,
                healthRingSize + clipPad * 2,
                healthRingSize + clipPad * 2);

        for (int i = 0; i < hpText.length(); i++) {
            int slot = MAX_DIGITS - hpText.length() + i;
            String ch = String.valueOf(hpText.charAt(i));
            float charW = issue((int) hpFontSize).getWidth(ch);

            int charAlpha = (int) (drawAlpha * digitAlpha[slot]);

            issue((int) hpFontSize).drawString(matrices, ch,
                    startX,
                    baseY + digitOffsetY[slot],
                    ColorUtils.rgba(255, 255, 255, charAlpha));

            startX += charW;
        }

        ScissorUtils.pop();

        matrices.pop();

        draggable.setWidth(width);
        draggable.setHeight(height);
    }
}

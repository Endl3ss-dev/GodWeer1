package fun.volon.client.modules.impl.render.base.implement;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
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

/**
 * TargetHudV2 — стандартный TargetHud, но вместо текста "HP: "
 * используется иконка сердца из шрифта "energy".
 */
public class TargetHudV2 extends InterfaceProcessing {

    // Глиф сердца в шрифте energy
    private static final String HEART_GLYPH = "v";

    private final AnimationUtils alphaAnimation  = new AnimationUtils(0f, 9f,   Easings.QUAD_OUT);
    private final AnimationUtils hpAnimation     = new AnimationUtils(1f, 9.2f, Easings.QUAD_OUT);
    private final AnimationUtils hpTrailAnimation= new AnimationUtils(1f, 7.4f, Easings.QUAD_OUT);
    private final AnimationUtils hpValueAnimation= new AnimationUtils(20f, 7f,  Easings.QUAD_OUT);
    private final AnimationUtils ABValueAnimation= new AnimationUtils(0f, 7f,   Easings.QUAD_OUT);
    private final AnimationUtils goldenHpAnimation      = new AnimationUtils(0f, 9.2f, Easings.QUAD_OUT);
    private final AnimationUtils goldenAlphaAnimation   = new AnimationUtils(0f, 9f,   Easings.QUAD_OUT);
    private final List<Particle> particles = new ObjectArrayList<>();

    private LivingEntity lastTarget;
    private float maxAbsorption = 20f;
    private boolean headParticlesEnabled = true;
    private boolean healthBarStyleEnabled = false;
    private long   lastParticleUpdateNs   = System.nanoTime();
    private LivingEntity particleTarget;
    private int    lastTargetHurtTime     = 0;
    private int    cachedBarThemeColor    = ColorUtils.rgba(124, 91, 242, 255);
    private int    cachedBarThemeColor2   = ColorUtils.rgba(93,  67, 175, 255);
    private final ItemStack[] armorScratch = new ItemStack[4];

    private static final class Particle {
        float x, y, vx, vy, size, age, maxAge;
    }

    public TargetHudV2(Draggable draggable) {
        super(draggable);
    }

    public boolean isHeadParticlesEnabled() { return headParticlesEnabled; }
    public void setHeadParticlesEnabled(boolean v) {
        headParticlesEnabled = v;
        if (!v) particles.clear();
    }
    public boolean isHealthBarStyleEnabled() { return healthBarStyleEnabled; }
    public void setHealthBarStyleEnabled(boolean v) { healthBarStyleEnabled = v; }

    private static float targetHudAlphaMul() {
        if (Interface.INSTANCE == null) return 1f;
        float p = Interface.INSTANCE.targetHudAlpha.get() / 100f;
        return MathHelper.clamp(p, 0f, 1f);
    }

    private Font issue(int size)  { return Fonts.getFont("suisse", size); }
    private Font energy(int size) { return Fonts.getFont("energy", size); }

    private int interpolateColor(int c1, int c2, float r) {
        r = MathHelper.clamp(r, 0f, 1f);
        int r1=(c1>>16)&0xFF, g1=(c1>>8)&0xFF, b1=c1&0xFF, a1=(c1>>24)&0xFF;
        int r2=(c2>>16)&0xFF, g2=(c2>>8)&0xFF, b2=c2&0xFF, a2=(c2>>24)&0xFF;
        return ((int)(a1+(a2-a1)*r)<<24)|((int)(r1+(r2-r1)*r)<<16)|((int)(g1+(g2-g1)*r)<<8)|(int)(b1+(b2-b1)*r);
    }

    private void updateParticles(MatrixStack m, LivingEntity target,
                                  float headX, float headY, float headSize,
                                  float alpha, int themeColor) {
        if (target == null || alpha <= 0.02f) {
            particles.clear(); particleTarget = target; lastTargetHurtTime = 0; return;
        }
        long now = System.nanoTime();
        float dt = MathHelper.clamp((now - lastParticleUpdateNs) / 1e9f * 60f, 0.2f, 3f);
        lastParticleUpdateNs = now;
        if (particleTarget != target) { particles.clear(); particleTarget = target; lastTargetHurtTime = Math.max(0, target.hurtTime); }
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        float cx = headX + headSize * 0.5f, cy = headY + headSize * 0.5f;
        int hurtTime = Math.max(0, target.hurtTime);
        boolean burst = hurtTime > 0 && (hurtTime > lastTargetHurtTime || hurtTime % 3 == 0);
        lastTargetHurtTime = hurtTime;
        if (burst) {
            int n = 1 + rnd.nextInt(2);
            for (int i = 0; i < n && particles.size() < 14; i++) {
                float angle = (float)(rnd.nextDouble()*Math.PI*2);
                float radius = rnd.nextFloat()*headSize*0.24f;
                float sa = (float)(rnd.nextDouble()*Math.PI*2);
                float speed = 0.58f + rnd.nextFloat()*0.9f;
                Particle p = new Particle();
                p.x = cx + MathHelper.cos(angle)*radius;
                p.y = cy + MathHelper.sin(angle)*radius;
                p.vx = MathHelper.cos(sa)*speed + (p.x-cx)*0.025f;
                p.vy = MathHelper.sin(sa)*speed + (p.y-cy)*0.025f;
                p.size = 3.8f + rnd.nextFloat()*1.4f;
                p.maxAge = 74f + rnd.nextFloat()*42f;
                particles.add(p);
            }
        }
        for (int i = particles.size()-1; i >= 0; i--) {
            Particle p = particles.get(i);
            p.age += dt;
            if (p.age >= p.maxAge) { particles.remove(i); continue; }
            p.x += p.vx*dt; p.y += p.vy*dt;
            p.vx *= (float)Math.pow(0.975f, dt);
            p.vy *= (float)Math.pow(0.975f, dt);
            p.vy += 0.0012f*dt;
            float life = 1f - p.age/p.maxAge;
            float pa = alpha * life*life*(3f-2f*life);
            if (pa <= 0.02f) continue;
            RenderUtils.drawRoundedRect(m, p.x-p.size*0.5f, p.y-p.size*0.5f, p.size, p.size,
                    p.size*0.45f, ColorUtils.applyAlpha(themeColor, pa*0.58f));
        }
    }

    private void drawItem(EventRender.Default ev, MatrixStack m, ItemStack stack, float sx, float sy, float scale) {
        if (stack.isEmpty()) return;
        m.push();
        m.translate(sx, sy, 0);
        m.scale(scale, scale, 1f);
        ev.getContext().drawItem(stack, 0, 0);
        m.pop();
    }

    @Override
    public void onRender(EventRender.Default ev) {
        render(ev);
        super.onRender(ev);
    }

    private void render(EventRender.Default ev) {
        if (mc.player == null) { particles.clear(); lastTargetHurtTime = 0; draggable.setWidth(0); draggable.setHeight(0); return; }

        Aura aura = ModuleClass.INSTANCE.aura;
        boolean chatOpen = mc.currentScreen instanceof ChatScreen;
        LivingEntity auraTarget = aura != null ? aura.getTarget() : null;
        boolean show = chatOpen || auraTarget != null;

        alphaAnimation.setSpeed(show ? 9f : 5f);
        alphaAnimation.update(show ? 1f : 0f);
        float alpha = MathHelper.clamp(alphaAnimation.getValue(), 0f, 1f);
        if (show) lastTarget = chatOpen ? mc.player : auraTarget;

        LivingEntity target = show ? (chatOpen ? mc.player : auraTarget) : lastTarget;
        if (target == null || alpha <= 0.01f) {
            particles.clear(); lastTargetHurtTime = 0;
            draggable.setWidth(0); draggable.setHeight(0);
            goldenAlphaAnimation.setValue(0f); ABValueAnimation.setValue(0f);
            goldenHpAnimation.setValue(0f); return;
        }

        // Цвет темы
        int colorTheme = Volon.INSTANCE.themeStorage.getThemes().getTheme().getName().equals("Rainbow")
                ? ColorUtils.getThemeColor()
                : Volon.INSTANCE.themeStorage.getThemes().getTheme().color[0];
        int colorThemeDark = ColorUtils.darken(colorTheme, 0.4f);
        if (show) { cachedBarThemeColor = colorTheme; cachedBarThemeColor2 = colorThemeDark; }
        int barThemeColor = show ? colorTheme : cachedBarThemeColor;
        int barThemeDark  = show ? colorThemeDark : cachedBarThemeColor2;

        // HP / Absorption
        float curAbs = target.getAbsorptionAmount();
        boolean hasAbs = curAbs > 0;
        goldenAlphaAnimation.setSpeed(hasAbs ? 9f : 5f);
        goldenAlphaAnimation.update(hasAbs ? 1f : 0f);
        float goldenAlpha = MathHelper.clamp(goldenAlphaAnimation.getValue(), 0f, 1f);

        float maxHealth = Math.max(1f, target.getMaxHealth());
        float maxAB     = Math.max(1f, maxAbsorption);

        float targetHp = ScoreboardHP.getHealth(target);
        boolean hpUnknown = targetHp == ScoreboardHP.UNKNOWN_HP;
        float animHpVal   = hpUnknown ? 0f : targetHp;

        hpValueAnimation.update(animHpVal);
        ABValueAnimation.update(show ? curAbs : 0f);
        float animHp = MathHelper.clamp(hpValueAnimation.getValue(), 0f, maxHealth);
        float animAB = MathHelper.clamp(ABValueAnimation.getValue(), 0f, maxAB);

        float hpProgress = hpUnknown ? 0f : MathHelper.clamp(targetHp / maxHealth, 0f, 1f);
        hpAnimation.update(hpProgress);
        float hpAnim = MathHelper.clamp(hpAnimation.getValue(), 0f, 1f);

        if (hpAnim < hpTrailAnimation.getValue()) hpTrailAnimation.update(hpAnim);
        float hpTrail  = MathHelper.clamp(hpTrailAnimation.getValue(), 0f, 1f);
        boolean hiding = !show;
        if (hiding) hpTrail = hpAnim;

        // Геометрия
        float x = draggable.getX(), y = draggable.getY();
        float height   = 32f;
        float headSize = 18f;
        float padLeft  = 3f;
        float padTop   = (height - headSize) * 0.5f - 3.5f;
        float gap      = 5f;
        float padRight = 5f;
        float barH     = 5.9f;
        float barY     = y + height - 7.5f;

        String name   = target.getName().getString();
        Font fontName = issue(13);
        Font fontHp   = issue(12);
        Font fontHeart= energy(16);

        // Ширина иконки сердца
        float heartW = fontHeart != null ? fontHeart.getStringWidth(HEART_GLYPH) + 2f : 0f;

        String hpValStr = hpUnknown ? "???" : String.format("%.1f", animHp);
        String abText   = (hasAbs && goldenAlpha > 0.01f)
                ? " (+" + String.format("%.0f", animAB) + ")" : "";

        float nameW = fontName.getStringWidth(name);
        float hpW   = heartW + fontHp.getStringWidth(hpValStr) + fontHp.getStringWidth(abText);
        float width = Math.max(90f, padLeft + headSize + gap + Math.max(nameW, hpW) + padRight);

        float headX = x + padLeft, headY = y + padTop;
        float textX = x + padLeft + headSize + gap;
        float barX  = x, barW = width;
        int alphaInt = (int)(255 * alpha);

        float scaleMul = 1f;
        if (Interface.INSTANCE != null) {
            scaleMul = Interface.INSTANCE.targetHudScale.get() / 100f;
            scaleMul = MathHelper.clamp(scaleMul, 0.5f, 2f);
        }

        MatrixStack m = ev.getContext().getMatrices();
        m.push();
        if (scaleMul != 1f) {
            float cx = x + width*0.5f, cy = y + height*0.5f;
            m.translate(cx, cy, 0); m.scale(scaleMul, scaleMul, 1f); m.translate(-cx, -cy, 0);
        }

        // Броня сверху
        {
            int armorCount = 0;
            for (ItemStack s : target.getArmorItems()) if (armorCount < 4) armorScratch[armorCount++] = s;
            ItemStack mainHand = target.getMainHandStack();
            ItemStack offHand  = target.getOffHandStack();
            float spacing = 10f, scale = 0.5f * alpha;
            float armorBgH = 11f, armorBgY = y - armorBgH - 2.5f;
            int mainCnt = 0;
            for (int i = armorCount-1; i >= 0; i--) if (armorScratch[i] != null && !armorScratch[i].isEmpty()) mainCnt++;
            int extraCnt = (!mainHand.isEmpty() ? 1 : 0) + (!offHand.isEmpty() ? 1 : 0);
            if (mainCnt > 0) {
                drawHudBackground(m, x, armorBgY, mainCnt * spacing, armorBgH, barThemeColor);
                float sz = 16f * scale;
                for (int i = armorCount-1, slot = 0; i >= 0; i--) {
                    ItemStack s = armorScratch[i];
                    if (s != null && !s.isEmpty()) {
                        drawItem(ev, m, s, x + slot*spacing + (spacing-sz)*0.5f, armorBgY + (armorBgH-sz)*0.5f, scale);
                        slot++;
                    }
                }
            }
            if (extraCnt > 0) {
                float ex = x + mainCnt*spacing + (mainCnt > 0 ? 2.5f : 0f);
                drawHudBackground(m, ex, armorBgY, extraCnt * spacing, armorBgH, barThemeColor);
                float sz = 16f * scale; int slot = 0;
                if (!mainHand.isEmpty()) { drawItem(ev, m, mainHand, ex + slot*spacing + (spacing-sz)*0.5f, armorBgY + (armorBgH-sz)*0.5f, scale); slot++; }
                if (!offHand.isEmpty())  { drawItem(ev, m, offHand,  ex + slot*spacing + (spacing-sz)*0.5f, armorBgY + (armorBgH-sz)*0.5f, scale); }
            }
            for (int i = 0; i < armorCount; i++) armorScratch[i] = ItemStack.EMPTY;
        }

        // Фон
        int bgAlpha = MathHelper.clamp((int)(255f * targetHudAlphaMul()), 0, 255);
        if (bgAlpha > 0) drawHudBlurOrRect(m, x, y, width, height, 4f, ColorUtils.rgba(7, 7, 9, bgAlpha));
        if (Interface.INSTANCE != null && Interface.INSTANCE.targetHudOutline.isState()) {
            int oa = MathHelper.clamp((int)(220*alpha), 0, 255);
            RenderUtils.drawRoundedRectOutline(m, x, y, width, height, 4f, 4f, 4f, 4f, 0.6f, ColorUtils.rgba(0,0,0,oa));
        }
        if (Interface.INSTANCE != null && Interface.INSTANCE.targetHudShadow.isState()) {
            float str = Interface.INSTANCE.targetHudShadowStrength.get();
            int sa = MathHelper.clamp((int)(255f*str/100f*alpha), 0, 255);
            RenderUtils.drawShadowOuter(m, x, y, width, height, 4f, 10f, ColorUtils.rgba(0,0,0,sa));
        }

        // Голова
        if (headParticlesEnabled) updateParticles(m, target, headX, headY, headSize, alpha, barThemeColor);
        else particles.clear();
        if (target instanceof PlayerEntity pe) {
            RenderUtils.drawPlayerHead(m, pe.getUuid(), headX, headY, headSize, 2f, alpha, 0f);
        } else {
            RenderUtils.drawTargetHudDefaultPlaceholder(m, headX, headY, alpha);
            issue(14).drawCenteredString(m, "?", headX + headSize/2f, headY + 3f, ColorUtils.rgba(220,220,220,alphaInt));
        }

        // Ник
        float maxTW = Math.max(8f, width - padLeft - headSize - gap - padRight);
        fontName.drawStringWithFade(m, name, textX, y + 5.5f, maxTW, ColorUtils.rgba(255,255,255,alphaInt));

        // HP строка: иконка сердца (energy) + значение
            float hpTextY = y + 15.5f;
        float curX = textX;

        // Иконка сердца из energy шрифта
        if (fontHeart != null) {
            // Цвет иконки: красный если HP низкое, тематический если нормальное
            int heartColor;
            if (hpUnknown) {
                heartColor = ColorUtils.rgba(255, 200, 100, alphaInt);
            } else {
                float hpRatio = MathHelper.clamp(animHp / maxHealth, 0f, 1f);
                if (hpRatio < 0.3f) {
                    heartColor = ColorUtils.rgba(230, 60, 60, alphaInt);   // красный при низком HP
                } else if (hpRatio < 0.6f) {
                    heartColor = ColorUtils.rgba(230, 180, 60, alphaInt);  // жёлтый при среднем
                } else {
                    heartColor = ColorUtils.applyAlpha(barThemeColor, alpha); // цвет темы при полном
                }
            }
            float heartOffsetY = hpTextY - 1.0f; // чуть выше т.к. шрифт больше
            fontHeart.drawString(m, HEART_GLYPH, curX - 0f, heartOffsetY, heartColor);
            curX += heartW;
        } else {
            // fallback — текст HP: если energy шрифт не загрузился
            fontHp.drawString(m, "HP: ", curX, hpTextY, ColorUtils.rgba(232,232,232,alphaInt));
            curX += fontHp.getStringWidth("HP: ");
        }

        // Значение HP
        if (hpUnknown) {
            fontHp.drawString(m, "???", curX, hpTextY, ColorUtils.rgba(255,200,100,alphaInt));
        } else {
            fontHp.drawString(m, hpValStr, curX, hpTextY, ColorUtils.rgba(232,232,232,alphaInt));
            float abX = curX + fontHp.getStringWidth(hpValStr);
            if (goldenAlpha > 0.01f && !abText.isEmpty()) {
                int gai = (int)(255 * goldenAlpha * alpha);
                fontHp.drawGradientStringHorizontal(m, abText, abX, hpTextY,
                        ColorUtils.rgba(236,183,39,gai), ColorUtils.rgba(147,108,16,gai));
            }
        }

        // HP полоска
        int barBgColor = ColorUtils.rgba(30,30,38,(int)(200*alpha));
        RenderUtils.drawRoundedRect(m, barX+2f, barY, barW-4f, barH, 2f, barBgColor);
        if (!hpUnknown) {
            if (!hiding && hpTrail > hpAnim + 0.01f) {
                float tw = (barW-4f)*hpTrail;
                if (tw > 1f) RenderUtils.drawRoundedRect(m, barX+2f, barY, tw, barH, 2f,
                        ColorUtils.applyAlpha(ColorUtils.darken(barThemeDark, 0.6f), alpha*0.55f));
            }
            float fw = (barW-4f)*hpAnim;
            if (fw > 1f) {
                int fc1, fc2;
                if (healthBarStyleEnabled) {
                    int red = ColorUtils.rgba(220,50,50,255), yel = ColorUtils.rgba(220,200,50,255), grn = ColorUtils.rgba(80,220,80,255);
                    fc2 = hpAnim < 0.5f ? interpolateColor(red, yel, hpAnim/0.5f) : interpolateColor(yel, grn, (hpAnim-0.5f)/0.5f);
                    fc1 = ColorUtils.darken(fc2, 0.4f);
                } else { fc1 = barThemeDark; fc2 = barThemeColor; }
                RenderUtils.drawGradientRect(m, barX+2f, barY, fw, barH, 2f,
                        ColorUtils.applyAlpha(fc1, alpha), ColorUtils.applyAlpha(fc2, alpha), true);
            }
            if (goldenAlpha > 0.01f) {
                float ap = MathHelper.clamp(curAbs / maxAB, 0f, 1f);
                goldenHpAnimation.update(ap);
                float gw = (barW-4f)*MathHelper.clamp(goldenHpAnimation.getValue(), 0f, 1f);
                if (gw > 1f) RenderUtils.drawGradientRect(m, barX+2f, barY, gw, barH, 2f,
                        ColorUtils.applyAlpha(ColorUtils.rgba(147,108,16,255), goldenAlpha*alpha),
                        ColorUtils.applyAlpha(ColorUtils.rgba(236,183,39,255), goldenAlpha*alpha), true);
            }
        } else {
            RenderUtils.drawRoundedRect(m, barX+2f, barY, (barW-4f)*0.5f, barH, 2f,
                    ColorUtils.applyAlpha(ColorUtils.rgba(80,80,80,255), alpha*0.5f));
        }

        m.pop();
        draggable.setWidth(width  * scaleMul);
        draggable.setHeight(height * scaleMul);
    }
}

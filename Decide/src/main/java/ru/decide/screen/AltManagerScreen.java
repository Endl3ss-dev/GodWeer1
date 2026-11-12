/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_11905
 *  net.minecraft.class_11908
 *  net.minecraft.class_11909
 *  net.minecraft.class_2561
 *  net.minecraft.class_2960
 *  net.minecraft.class_332
 *  net.minecraft.class_437
 */
package ru.decide.screen;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_11905;
import net.minecraft.class_11908;
import net.minecraft.class_11909;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_332;
import net.minecraft.class_437;
import ru.decide.alt.Account;
import ru.decide.alt.AltManager;
import ru.decide.lang.Lang;
import ru.decide.utils.animation.Animation;
import ru.decide.utils.animation.Easings;
import ru.decide.utils.annotation.IMinecraft;
import ru.decide.utils.colors.ColorUtil;
import ru.decide.utils.math.MathUtil;
import ru.decide.utils.other.ClipboardPaste;
import ru.decide.utils.render.Render2D;
import ru.decide.utils.render.RenderUtil;
import ru.decide.utils.render.Scissor;
import ru.decide.utils.render.ScreenBlur;
import ru.decide.utils.render.font.Font;
import ru.decide.utils.render.font.Fonts;

@Environment(value=EnvType.CLIENT)
public class AltManagerScreen
extends class_437
implements IMinecraft {
    private final class_437 parent;
    private final Animation alpha = new Animation();
    private float scaleFix = 1.0f;
    private float mouseX;
    private float mouseY;
    private String inputText = "";
    private boolean inputFocused = false;
    private float scroll = 0.0f;
    private float scrollTarget = 0.0f;
    private boolean settingsOpen = false;
    private String randomBase = "";
    private boolean baseFocused = false;
    private final Animation addAnim = new Animation();
    private final Animation genAnim = new Animation();
    private final Animation inputAnim = new Animation();
    private final Animation gearAnim = new Animation();
    private final Animation settingsAnim = new Animation();
    private final Animation baseAnim = new Animation();
    private float gearX;
    private float gearY;
    private float gearW;
    private float gearH;
    private float sideX;
    private float sideY;
    private float sideW;
    private float sideH;
    private float baseX;
    private float baseY;
    private float baseW;
    private float baseH;
    private final Map<Account, RowAnims> rowAnims = new IdentityHashMap<Account, RowAnims>();
    private final Set<Account> removing = Collections.newSetFromMap(new IdentityHashMap());
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    private float panelX;
    private float panelY;
    private float panelW;
    private float panelH;
    private float inputX;
    private float inputY;
    private float inputW;
    private float inputH;
    private float addX;
    private float addY;
    private float addW;
    private float addH;
    private float genX;
    private float genY;
    private float genW;
    private float genH;
    private float listX;
    private float listY;
    private float listW;
    private float listH;
    private final List<float[]> rowRects = new ArrayList<float[]>();
    private final List<float[]> delRects = new ArrayList<float[]>();
    private final List<float[]> favRects = new ArrayList<float[]>();
    private final List<Account> rowRefs = new ArrayList<Account>();
    private static final float ROW_H = 20.0f;
    private static final float ROW_GAP = 4.0f;

    public AltManagerScreen(class_437 parent) {
        super((class_2561)class_2561.method_43470((String)"AltManager"));
        this.parent = parent;
    }

    protected void method_25426() {
        this.alpha.set(0.0);
        this.alpha.run(1.0, 0.5, Easings.BACK_OUT);
    }

    public void method_25420(class_332 context, int mouseX, int mouseY, float delta) {
    }

    public void method_25394(class_332 context, int mouseX, int mouseY, float delta) {
        boolean blink;
        this.mouseX = (float)mouseX / this.scaleFix;
        this.mouseY = (float)mouseY / this.scaleFix;
        this.alpha.update();
        this.settingsAnim.update();
        float targetScale = 2.0f;
        float currentScale = mc.method_22683().method_4495();
        this.scaleFix = targetScale / currentScale;
        int screenWidth = (int)((float)mc.method_22683().method_4486() / this.scaleFix);
        int screenHeight = (int)((float)mc.method_22683().method_4502() / this.scaleFix);
        if (context != null) {
            context.method_51448().pushMatrix();
        }
        Render2D.beginOverlay();
        float a = this.alpha.get();
        float parallaxStrength = 0.07f;
        float offsetX = ((float)screenWidth / 2.0f - (float)mouseX) * parallaxStrength;
        float offsetY = ((float)screenHeight / 2.0f - (float)mouseY) * parallaxStrength;
        float bgScale = 1.1f;
        float bgW = (float)screenWidth * bgScale;
        float bgH = (float)screenHeight * bgScale;
        float bgX = ((float)screenWidth - bgW) / 2.0f + offsetX;
        float bgY = ((float)screenHeight - bgH) / 2.0f + offsetY;
        RenderUtil.Images.texture(class_2960.method_60655((String)"decide", (String)"textures/frame/menu.png"), bgX, bgY, bgW, bgH, ColorUtil.getColor(255, a));
        ScreenBlur.capture(2);
        RenderUtil.Blur.blur(0.0f, 0.0f, screenWidth, screenHeight, a, ColorUtil.getColor(0, a * 0.2f));
        ScreenBlur.capture(4);
        Font font = Fonts.sf_regular;
        this.panelW = 280.0f;
        this.panelH = 274.0f;
        this.panelX = (float)screenWidth / 2.0f - this.panelW / 2.0f;
        this.panelY = (float)screenHeight / 2.0f - this.panelH / 2.0f + 16.0f - 16.0f * a;
        float pad = 5.0f;
        float btnH = 20.0f;
        float gap = 5.0f;
        this.addY = this.genY = this.panelY + this.panelH - pad - btnH;
        this.addH = this.genH = btnH;
        this.addW = this.genW = (this.panelW - pad * 2.0f - gap) / 2.0f;
        this.addX = this.panelX + pad;
        this.genX = this.addX + this.addW + gap;
        this.gearW = this.gearH = (this.inputH = 20.0f);
        this.inputX = this.panelX + pad;
        this.inputW = this.panelW - pad * 2.0f - this.gearW - 6.0f;
        this.inputY = this.addY - gap - this.inputH;
        this.gearX = this.inputX + this.inputW + 6.0f;
        this.gearY = this.inputY;
        this.listX = this.panelX + pad;
        this.listY = this.panelY + pad;
        this.listW = this.panelW - pad * 2.0f;
        this.listH = this.inputY - gap - this.listY;
        this.renderSidePanel(a, font);
        RenderUtil.Blur.blur(this.panelX, this.panelY, this.panelW, this.panelH, a, 5.0f, ColorUtil.getColor(20, a * 0.4f));
        this.rowRects.clear();
        this.delRects.clear();
        this.favRects.clear();
        this.rowRefs.clear();
        List<Account> all = AltManager.get().getAccounts();
        List<Account> accs = AltManager.get().getSorted();
        float contentH = (float)accs.size() * 24.0f;
        float maxScroll = Math.max(0.0f, contentH - this.listH);
        this.scrollTarget = MathUtil.clamp(this.scrollTarget, 0.0f, maxScroll);
        this.scroll += (this.scrollTarget - this.scroll) * 0.4f;
        this.rowAnims.keySet().retainAll(all);
        Scissor.enable(this.listX, this.listY, this.listW, this.listH, 2.0f);
        float off = 0.0f;
        String current = AltManager.get().currentName();
        ArrayList<Account> finished = null;
        for (Account acc : accs) {
            RowAnims an = this.rowAnims.computeIfAbsent(acc, k -> new RowAnims());
            boolean leaving = this.removing.contains(acc);
            an.appear.run(leaving ? 0.0 : 1.0, leaving ? 0.22 : 0.35, Easings.QUAD_OUT, true);
            float ap = MathUtil.clamp(an.appear.get(), 0.0f, 1.0f);
            float slotH = 24.0f * ap;
            if (!an.hasOff) {
                an.curOff = off;
                an.hasOff = true;
            } else {
                an.curOff += (off - an.curOff) * 0.35f;
            }
            float ry = this.listY - this.scroll + an.curOff;
            if (RenderUtil.Images.isUrlLoaded(acc.headUrl())) {
                an.headFade.run(1.0, 0.3, Easings.QUAD_OUT, true);
            }
            boolean visible = ry + slotH >= this.listY && ry <= this.listY + this.listH;
            boolean inside = this.mouseY() >= this.listY && this.mouseY() <= this.listY + this.listH;
            float dw = 16.0f;
            float dh = 16.0f;
            float dx = this.listX + this.listW - dw - 4.0f;
            float dy = ry + (20.0f - dh) / 2.0f;
            float fx = dx - dw - 4.0f;
            float fy = dy;
            boolean hovered = !leaving && visible && inside && MathUtil.isHovered(this.mouseX(), this.mouseY(), this.listX, ry, this.listW, 20.0f);
            boolean delHover = !leaving && visible && inside && MathUtil.isHovered(this.mouseX(), this.mouseY(), dx, dy, dw, dh);
            boolean favHover = !leaving && visible && inside && MathUtil.isHovered(this.mouseX(), this.mouseY(), fx, fy, dw, dh);
            boolean active = acc.name.equalsIgnoreCase(current);
            an.hover.run(hovered && !delHover && !favHover ? 1.0 : 0.0, 0.18, Easings.QUAD_OUT, true);
            an.del.run(delHover ? 1.0 : 0.0, 0.18, Easings.QUAD_OUT, true);
            an.favHover.run(favHover ? 1.0 : 0.0, 0.18, Easings.QUAD_OUT, true);
            an.fav.run(acc.favorite ? 1.0 : 0.0, 0.25, Easings.QUAD_OUT, true);
            an.active.run(active ? 1.0 : 0.0, 0.25, Easings.QUAD_OUT, true);
            an.hover.update();
            an.del.update();
            an.favHover.update();
            an.fav.update();
            an.active.update();
            an.appear.update();
            an.headFade.update();
            float hp = MathUtil.clamp(an.hover.get(), 0.0f, 1.0f);
            float dp = MathUtil.clamp(an.del.get(), 0.0f, 1.0f);
            float fhp = MathUtil.clamp(an.favHover.get(), 0.0f, 1.0f);
            float fv = MathUtil.clamp(an.fav.get(), 0.0f, 1.0f);
            float acp = MathUtil.clamp(an.active.get(), 0.0f, 1.0f);
            float hf = MathUtil.clamp(an.headFade.get(), 0.0f, 1.0f);
            if (visible) {
                float ra = a * ap;
                float slide = (1.0f - ap) * 10.0f;
                float rx = this.listX + slide;
                float rw = this.listW;
                dx = rx + rw - dw - 3.0f;
                int rowBg = ColorUtil.getColor(255, ra * (0.015f + 0.05f * hp + 0.03f * acp));
                RenderUtil.Render2D.rect(rx, ry, rw, 20.0f, rowBg, 4.0f);
                float asz = 12.0f;
                float axx = rx + 5.0f;
                float ay = ry + (20.0f - asz) / 2.0f;
                RenderUtil.Render2D.rect(axx, ay, asz, asz, ColorUtil.getColor(255, ra * 0.06f), 3.0f);
                RenderUtil.Images.url(acc.headUrl(), axx, ay, asz, asz, 1.0f, 3.0f, ColorUtil.getColor(255, ra * hf));
                float tx = axx + asz + 4.0f;
                font.draw(acc.name, tx, ry + 5.0f, 8.0f, ColorUtil.getColor(255, ra * (0.7f + 0.3f * acp)));
                Fonts.sf_regular.draw(this.dateFormat.format(new Date(acc.created)), tx + 6.0f + font.getWidth(acc.name, 8.0f), ry + 5.0f, 5.0f, ColorUtil.getColor(255, ra * (0.15f + 0.15f * acp)));
                dy = ry + (20.0f - dh) / 2.0f;
                fx = dx - dw - 2.0f;
                fy = dy;
                RenderUtil.Render2D.rect(fx, fy, dw, dh, ColorUtil.getColor(255, ra * (0.015f + 0.025f * fhp)), 3.0f);
                int starCol = ColorUtil.overCol(ColorUtil.getColor(255, 255, 255, ra * (0.45f + 0.25f * fhp)), ColorUtil.replAlpha(ColorUtil.getClientColor(1), ra), fv);
                Fonts.icon.drawCentered("o", fx + dw / 2.0f, fy + dh / 2.0f - 2.7f, 6.0f, starCol);
                RenderUtil.Render2D.rect(dx, dy, dw, dh, ColorUtil.getColor(255, ra * (0.015f + 0.025f * dp)), 3.0f);
                Fonts.icon.drawCentered("W", dx + dw / 2.0f + 0.15f, dy + 4.6f, 7.0f, ColorUtil.overCol(ColorUtil.getColor(255, 255, 255, ra * 0.5f), ColorUtil.getColor(255, 125, 125, ra * 0.9f), dp));
                if (!leaving) {
                    this.rowRects.add(new float[]{rx, ry, rw - dw * 2.0f - 16.0f, 20.0f});
                    this.delRects.add(new float[]{dx, dy, dw, dh});
                    this.favRects.add(new float[]{fx, fy, dw, dh});
                    this.rowRefs.add(acc);
                }
            }
            if (leaving && ap <= 0.01f) {
                if (finished == null) {
                    finished = new ArrayList<Account>();
                }
                finished.add(acc);
            }
            off += slotH;
        }
        if (finished != null) {
            for (Account acc : finished) {
                AltManager.get().remove(acc);
                this.rowAnims.remove(acc);
                this.removing.remove(acc);
            }
        }
        if (accs.isEmpty()) {
            font.drawCentered(Lang.pick("\u041f\u043e\u043a\u0430 \u043d\u0435\u0442 \u0430\u043a\u043a\u0430\u0443\u043d\u0442\u043e\u0432", "\u041f\u043e\u043a\u0430 \u043d\u0435\u0442 \u0430\u043a\u043a\u0430\u0443\u043d\u0442\u043e\u0432"), this.listX + this.listW / 2.0f, this.listY + this.listH / 2.0f - 4.0f, 12.0f, ColorUtil.getColor(255, a * 0.15f));
        }
        Scissor.disable();
        this.inputAnim.run(this.inputFocused ? 1.0 : 0.0, 0.2, Easings.QUAD_OUT, true);
        this.inputAnim.update();
        float fp = MathUtil.clamp(this.inputAnim.get(), 0.0f, 1.0f);
        RenderUtil.Render2D.rect(this.inputX, this.inputY, this.inputW, this.inputH, ColorUtil.getColor(255, a * (0.015f + 0.03f * fp)), 4.0f);
        boolean bl = blink = this.inputFocused && System.currentTimeMillis() / 450L % 2L == 0L;
        String shown = this.inputText.isEmpty() && !this.inputFocused ? "\u0412\u0432\u0435\u0434\u0438\u0442\u0435 \u043d\u0438\u043a" : this.inputText + (blink ? "_" : "");
        font.draw(shown, this.inputX + 10.0f, this.inputY + 5.4f, 7.0f, this.inputText.isEmpty() && !this.inputFocused ? ColorUtil.getColor(140, a) : ColorUtil.getColor(185, a));
        this.gearAnim.run(MathUtil.isHovered(this.mouseX(), this.mouseY(), this.gearX, this.gearY, this.gearW, this.gearH) || this.settingsOpen ? 1.0 : 0.0, 0.18, Easings.QUAD_OUT, true);
        this.gearAnim.update();
        float gp = MathUtil.clamp(this.gearAnim.get(), 0.0f, 1.0f);
        RenderUtil.Render2D.rect(this.gearX, this.gearY, this.gearW, this.gearH, ColorUtil.getColor(255, a * (0.015f + 0.04f * gp)), 4.0f);
        Fonts.icon.draw("l", this.gearX + 6.25f, this.gearY + 6.3f, 8.0f, ColorUtil.getColor(255, a * (0.35f + 0.3f * gp)));
        this.addAnim.run(MathUtil.isHovered(this.mouseX(), this.mouseY(), this.addX, this.addY, this.addW, this.addH) ? 1.0 : 0.0, 0.18, Easings.QUAD_OUT, true);
        this.genAnim.run(MathUtil.isHovered(this.mouseX(), this.mouseY(), this.genX, this.genY, this.genW, this.genH) ? 1.0 : 0.0, 0.18, Easings.QUAD_OUT, true);
        this.addAnim.update();
        this.genAnim.update();
        this.drawButton(this.addX, this.addY, this.addW, this.addH, Lang.pick("\u0414\u043e\u0431\u0430\u0432\u0438\u0442\u044c \u0430\u043a\u043a\u0430\u0443\u043d\u0442", "\u0414\u043e\u0431\u0430\u0432\u0438\u0442\u044c \u0430\u043a\u043a\u0430\u0443\u043d\u0442"), a, MathUtil.clamp(this.addAnim.get(), 0.0f, 1.0f), false);
        this.drawButton(this.genX, this.genY, this.genW, this.genH, Lang.pick("\u0421\u043b\u0443\u0447\u0430\u0439\u043d\u044b\u0439 \u0430\u043a\u043a\u0430\u0443\u043d\u0442", "\u0421\u043b\u0443\u0447\u0430\u0439\u043d\u044b\u0439 \u0430\u043a\u043a\u0430\u0443\u043d\u0442"), a, MathUtil.clamp(this.genAnim.get(), 0.0f, 1.0f), false);
        Render2D.endOverlay();
        if (context != null) {
            context.method_51448().popMatrix();
        }
    }

    private void drawButton(float x, float y, float w, float h, String label, float a, float hp, boolean accent) {
        int bg = accent ? ColorUtil.replAlpha(ColorUtil.getClientColor(1), a * (0.18f + 0.12f * hp)) : ColorUtil.getColor(255, a * (0.015f + 0.03f * hp));
        RenderUtil.Render2D.rect(x, y, w, h, bg, 4.0f);
        int border = ColorUtil.getColor(255, 255, 255, 1.0f);
        Fonts.sf_regular.drawCentered(label, x + w / 2.0f, y + h / 2.0f - 4.75f, 7.0f, ColorUtil.getColor(175, a));
    }

    private void renderSidePanel(float a, Font font) {
        boolean blink;
        this.settingsAnim.run(this.settingsOpen ? 1.0 : 0.0, 0.25, Easings.SINE_OUT);
        float sp = this.settingsAnim.get();
        float sa = a * sp;
        this.sideW = 168.0f;
        this.sideH = 96.0f;
        this.sideY = this.inputY + this.inputH - this.sideH;
        float hiddenX = this.panelX + this.panelW - this.sideW + 24.0f;
        float shownX = this.panelX + this.panelW + 8.0f;
        this.sideX = hiddenX + (shownX - hiddenX) - 8.0f + 8.0f * sa;
        RenderUtil.Blur.blur(this.sideX, this.sideY, this.sideW, this.sideH, sa, 6.0f, ColorUtil.getColor(20, sa * 0.3f));
        float pad = 12.0f;
        font.draw(Lang.pick("\u041d\u0430\u0441\u0442\u0440\u043e\u0439\u043a\u0438 \u0440\u0430\u043d\u0434\u043e\u043c\u0430", "Random settings"), this.sideX + pad, this.sideY + 9.0f, 8.0f, ColorUtil.getColor(185, sa));
        font.draw(Lang.pick("\u041d\u0430\u0447\u0430\u043b\u044c\u043d\u044b\u0439 \u043d\u0438\u043a", "Initial nickname"), this.sideX + pad, this.sideY + 26.0f, 6.5f, ColorUtil.getColor(185, sa));
        this.baseX = this.sideX + pad;
        this.baseY = this.sideY + 37.0f;
        this.baseW = this.sideW - pad * 2.0f;
        this.baseH = 20.0f;
        this.baseAnim.run(this.baseFocused ? 1.0 : 0.0, 0.2, Easings.QUAD_OUT, true);
        this.baseAnim.update();
        float bf = MathUtil.clamp(this.baseAnim.get(), 0.0f, 1.0f);
        RenderUtil.Render2D.rect(this.baseX, this.baseY, this.baseW, this.baseH, ColorUtil.getColor(255, sa * (0.015f + 0.02f * bf)), 5.0f);
        boolean bl = blink = this.baseFocused && System.currentTimeMillis() / 450L % 2L == 0L;
        Object shown = this.randomBase.isEmpty() && !this.baseFocused ? Lang.pick("\u043d\u0430\u043f\u0440. night", "e.g. night") : this.randomBase + (blink ? "_" : "");
        font.draw((String)shown, this.baseX + 8.0f, this.baseY + 6.0f, 6.5f, this.randomBase.isEmpty() && !this.baseFocused ? ColorUtil.getColor(140, sa) : ColorUtil.getColor(185, sa));
        font.draw(Lang.pick("+ 4-12 \u0441\u043b\u0443\u0447\u0430\u0439\u043d\u044b\u0445 \u0441\u0438\u043c\u0432\u043e\u043b\u043e\u0432", "+ 4-12 random chars"), this.sideX + pad, this.sideY + this.sideH - 16.0f, 7.0f, ColorUtil.getColor(185, sa * 0.5f));
    }

    private float mouseX() {
        return this.mouseX;
    }

    private float mouseY() {
        return this.mouseY;
    }

    public boolean method_25402(class_11909 click, boolean doubled) {
        float[] r;
        int i;
        boolean inSide;
        float mx = (float)(click.comp_4798() / (double)this.scaleFix);
        float my = (float)(click.comp_4799() / (double)this.scaleFix);
        if (click.method_74245() != 0) {
            return super.method_25402(click, doubled);
        }
        boolean bl = inSide = this.settingsOpen && MathUtil.isHovered(mx, my, this.sideX, this.sideY, this.sideW, this.sideH);
        if (inSide) {
            if (MathUtil.isHovered(mx, my, this.baseX, this.baseY, this.baseW, this.baseH)) {
                this.baseFocused = true;
                this.inputFocused = false;
            } else {
                this.baseFocused = false;
            }
            return true;
        }
        if (MathUtil.isHovered(mx, my, this.gearX, this.gearY, this.gearW, this.gearH)) {
            this.baseFocused = this.settingsOpen = !this.settingsOpen;
            this.inputFocused = false;
            return true;
        }
        if (MathUtil.isHovered(mx, my, this.inputX, this.inputY, this.inputW, this.inputH)) {
            this.inputFocused = true;
            this.baseFocused = false;
            return true;
        }
        this.inputFocused = false;
        if (MathUtil.isHovered(mx, my, this.addX, this.addY, this.addW, this.addH)) {
            this.tryAdd();
            return true;
        }
        if (MathUtil.isHovered(mx, my, this.genX, this.genY, this.genW, this.genH)) {
            this.inputText = AltManager.get().randomName(this.randomBase);
            this.inputFocused = true;
            this.baseFocused = false;
            return true;
        }
        for (i = 0; i < this.favRects.size(); ++i) {
            r = this.favRects.get(i);
            if (!MathUtil.isHovered(mx, my, r[0], r[1], r[2], r[3])) continue;
            AltManager.get().toggleFavorite(this.rowRefs.get(i));
            return true;
        }
        for (i = 0; i < this.delRects.size(); ++i) {
            r = this.delRects.get(i);
            if (!MathUtil.isHovered(mx, my, r[0], r[1], r[2], r[3])) continue;
            this.removing.add(this.rowRefs.get(i));
            return true;
        }
        for (i = 0; i < this.rowRects.size(); ++i) {
            r = this.rowRects.get(i);
            if (!MathUtil.isHovered(mx, my, r[0], r[1], r[2], r[3])) continue;
            AltManager.get().login(this.rowRefs.get(i));
            return true;
        }
        return super.method_25402(click, doubled);
    }

    private void tryAdd() {
        Account added = AltManager.get().add(this.inputText);
        if (added != null) {
            this.inputText = "";
            this.scrollTarget = Float.MAX_VALUE;
        }
    }

    public boolean method_25404(class_11908 keyInput) {
        int key = keyInput.comp_4795();
        if (this.baseFocused) {
            if (key == 257 || key == 256) {
                this.baseFocused = false;
            } else if (ClipboardPaste.pasteCombo(key)) {
                this.randomBase = ClipboardPaste.append(this.randomBase, 24);
            } else if (key == 259 && !this.randomBase.isEmpty()) {
                this.randomBase = this.randomBase.substring(0, this.randomBase.length() - 1);
            }
            return true;
        }
        if (this.inputFocused) {
            if (key == 257) {
                this.tryAdd();
            } else if (key == 256) {
                this.inputFocused = false;
            } else if (ClipboardPaste.pasteCombo(key)) {
                this.inputText = ClipboardPaste.append(this.inputText, 64);
            } else if (key == 259 && !this.inputText.isEmpty()) {
                this.inputText = this.inputText.substring(0, this.inputText.length() - 1);
            }
            return true;
        }
        if (key == 256) {
            if (this.settingsOpen) {
                this.settingsOpen = false;
                return true;
            }
            mc.method_1507(this.parent);
            return true;
        }
        return super.method_25404(keyInput);
    }

    public boolean method_25400(class_11905 input) {
        if (this.baseFocused && input.method_74227() && this.randomBase.length() < 12) {
            String s = input.method_74226();
            for (int i = 0; i < s.length(); ++i) {
                char c = s.charAt(i);
                if (!Character.isLetterOrDigit(c) && c != '_') continue;
                this.randomBase = this.randomBase + c;
            }
            return true;
        }
        if (this.inputFocused && input.method_74227() && this.inputText.length() < 16) {
            String s = input.method_74226();
            for (int i = 0; i < s.length(); ++i) {
                char c = s.charAt(i);
                if (!Character.isLetterOrDigit(c) && c != '_') continue;
                this.inputText = this.inputText + c;
            }
            return true;
        }
        return super.method_25400(input);
    }

    public boolean method_25401(double mx, double my, double horizontal, double vertical) {
        this.scrollTarget -= (float)vertical * 22.0f;
        return true;
    }

    public boolean method_25421() {
        return false;
    }

    public boolean method_25422() {
        return false;
    }

    @Environment(value=EnvType.CLIENT)
    private static final class RowAnims {
        final Animation hover = new Animation();
        final Animation del = new Animation();
        final Animation active = new Animation();
        final Animation appear = new Animation();
        final Animation headFade = new Animation();
        final Animation fav = new Animation();
        final Animation favHover = new Animation();
        float curOff;
        boolean hasOff;

        RowAnims() {
            this.appear.set(0.0);
            this.headFade.set(0.0);
        }
    }
}


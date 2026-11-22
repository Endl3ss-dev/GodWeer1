/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_11908
 *  net.minecraft.class_11909
 *  net.minecraft.class_2561
 *  net.minecraft.class_2960
 *  net.minecraft.class_332
 *  net.minecraft.class_437
 */
package ru.decide.screen;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_11908;
import net.minecraft.class_11909;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_332;
import net.minecraft.class_437;
import ru.decide.module.impl.render.emotions.EmoteCatalog;
import ru.decide.module.impl.render.emotions.EmotePreviews;
import ru.decide.module.impl.render.emotions.EmotionSlots;
import ru.decide.utils.colors.ColorUtil;
import ru.decide.utils.render.Draw;
import ru.decide.utils.render.ScreenBlur;
import ru.decide.utils.render.font.Font;
import ru.decide.utils.render.font.Fonts;

@Environment(value=EnvType.CLIENT)
public final class EmotionSettingsScreen
extends class_437 {
    private static final Font TITLE = Fonts.sf_bold;
    private static final Font SUB = Fonts.sf_regular;
    private static final Font NAME = Fonts.sf_medium;
    private static final float SLOT_D = 44.0f;
    private static final float SLOT_GAP = 10.0f;
    private static final float CARD_W = 60.0f;
    private static final float CARD_GAP = 6.0f;
    private final class_437 parent;
    private final boolean closeOnPick;
    private int selectedSlot;
    private int page;
    private float panelX;
    private float panelY;
    private float panelW;
    private float panelH;
    private final float[] slotCx = new float[7];
    private final float[] slotCy = new float[7];
    private float titleY;
    private float subY;
    private float labelY;
    private float footerY;
    private float gridX;
    private float gridY;
    private float cardW;
    private float cardH;
    private int cols;
    private int rows;
    private int perPage;
    private int pageCount;

    public EmotionSettingsScreen(class_437 parent) {
        this(parent, 0, false);
    }

    public EmotionSettingsScreen(class_437 parent, int initialSlot, boolean closeOnPick) {
        super((class_2561)class_2561.method_43470((String)"\u042d\u043c\u043e\u0446\u0438\u0438"));
        this.parent = parent;
        this.closeOnPick = closeOnPick;
        this.selectedSlot = Math.max(0, Math.min(6, initialSlot));
    }

    protected void method_25426() {
        EmotePreviews.beginFrame();
        this.layout();
    }

    public boolean method_25421() {
        return false;
    }

    public void method_25420(class_332 context, int mouseX, int mouseY, float delta) {
    }

    private void layout() {
        int total = EmoteCatalog.instance().entries().size();
        float pad = 18.0f;
        this.panelW = Math.max(320.0f, (float)this.field_22789 - 24.0f);
        this.panelH = Math.max(220.0f, (float)this.field_22790 - 24.0f);
        this.panelX = ((float)this.field_22789 - this.panelW) / 2.0f;
        this.panelY = ((float)this.field_22790 - this.panelH) / 2.0f;
        this.titleY = this.panelY + 14.0f;
        this.subY = this.titleY + 16.0f;
        float slotsW = 368.0f;
        float first = this.panelX + (this.panelW - slotsW) / 2.0f + 22.0f;
        float slotTop = this.subY + 20.0f;
        for (int i = 0; i < 7; ++i) {
            this.slotCx[i] = first + (float)i * 54.0f;
            this.slotCy[i] = slotTop + 22.0f;
        }
        this.labelY = slotTop + 44.0f + 20.0f;
        this.footerY = this.panelY + this.panelH - pad - 10.0f;
        float gridTop = this.labelY + 16.0f;
        float gridH = this.footerY - 24.0f - gridTop;
        this.cardW = 60.0f;
        float imgW = this.cardW - 8.0f;
        float imgH = imgW * 1.25f;
        this.cardH = imgH + 24.0f;
        this.cols = Math.max(1, (int)Math.floor((this.panelW - pad * 2.0f + 6.0f) / (this.cardW + 6.0f)));
        this.rows = Math.max(1, (int)Math.floor((Math.max(1.0f, gridH) + 6.0f) / (this.cardH + 6.0f)));
        this.perPage = Math.max(1, this.cols * this.rows);
        this.pageCount = Math.max(1, (int)Math.ceil((double)total / (double)this.perPage));
        if (this.page >= this.pageCount) {
            this.page = this.pageCount - 1;
        }
        if (this.page < 0) {
            this.page = 0;
        }
        float usedW = (float)this.cols * this.cardW + (float)(this.cols - 1) * 6.0f;
        this.gridX = this.panelX + (this.panelW - usedW) / 2.0f;
        this.gridY = gridTop;
    }

    public void method_25394(class_332 context, int mouseX, int mouseY, float delta) {
        EmotePreviews.beginFrame();
        EmotionSlots slots = EmotionSlots.instance();
        List<EmoteCatalog.Entry> all = EmoteCatalog.instance().entries();
        Draw.rect(0.0f, 0.0f, this.field_22789, this.field_22790, ColorUtil.getColor(0, 0.5f));
        ScreenBlur.capture(1);
        Draw.glow(this.panelX, this.panelY, this.panelW, this.panelH, ColorUtil.replAlpha(ColorUtil.client(), 0.18f), 14.0f, 14.0f, 1.0f);
        Draw.glass(this.panelX, this.panelY, this.panelW, this.panelH, 1.0f, 14.0f, 14.0f, 14.0f, 14.0f, ColorUtil.multAlpha(ColorUtil.multDark(ColorUtil.background(), 0.6f), 1.0f));
        Draw.glassOutline(this.panelX, this.panelY, this.panelW, this.panelH, 0.5f, 14.0f, 1.0f, 0.8f);
        float centerX = this.panelX + this.panelW / 2.0f;
        TITLE.drawCentered("\u041d\u0430\u0441\u0442\u0440\u043e\u0439\u043a\u0438 \u044d\u043c\u043e\u0446\u0438\u0439", centerX, this.titleY, 15.0f, ColorUtil.getColor(255));
        SUB.drawCentered("\u041d\u0430\u0441\u0442\u0440\u043e\u0439\u043a\u0430 \u043a\u043e\u043b\u0435\u0441\u0430 \u044d\u043c\u043e\u0446\u0438\u0439 \u00b7 \u0432\u0441\u0435\u0433\u043e " + all.size(), centerX, this.subY, 8.5f, ColorUtil.getColor(150));
        for (int i = 0; i < 7; ++i) {
            float cx = this.slotCx[i];
            float cy = this.slotCy[i];
            boolean selected = i == this.selectedSlot;
            boolean hovered = EmotionSettingsScreen.hover(mouseX, mouseY, cx - 22.0f, cy - 22.0f, 44.0f, 44.0f);
            EmoteCatalog.Entry entry = slots.get(i);
            int plate = selected ? ColorUtil.replAlpha(ColorUtil.client(), 0.55f) : ColorUtil.getColor(40, 0.6f);
            Draw.rect(cx - 22.0f, cy - 22.0f, 44.0f, 44.0f, ColorUtil.replAlpha(plate, hovered ? 1.0f : 0.85f), 22.0f);
            Draw.outline(cx - 22.0f, cy - 22.0f, 44.0f, 44.0f, 0.6f, ColorUtil.replAlpha(ColorUtil.client(), selected ? 1.0f : (hovered ? 0.8f : 0.35f)), 22.0f);
            if (entry != null) {
                this.drawPreview(entry, cx, cy, 30.0f, 255);
            } else {
                EmotionSettingsScreen.drawPlus(cx, cy, 10.559999f, 1.5f, ColorUtil.getColor(selected ? 215 : 150));
            }
            NAME.drawCentered(String.valueOf(i + 1), cx, cy + 22.0f + 4.0f, 7.5f, ColorUtil.getColor(selected ? 225 : 120));
        }
        NAME.draw("\u0412\u0441\u0435 \u044d\u043c\u043e\u0446\u0438\u0438", this.panelX + 20.0f, this.labelY, 8.5f, ColorUtil.getColor(180));
        int first = this.page * this.perPage;
        float nameSize = 7.5f;
        for (int i = 0; i < this.perPage && first + i < all.size(); ++i) {
            boolean used;
            EmoteCatalog.Entry entry = all.get(first + i);
            float x = this.gridX + (float)(i % this.cols) * (this.cardW + 6.0f);
            float y = this.gridY + (float)(i / this.cols) * (this.cardH + 6.0f);
            boolean hovered = EmotionSettingsScreen.hover(mouseX, mouseY, x, y, this.cardW, this.cardH);
            int usedSlot = slots.indexOf(entry);
            boolean bl = used = usedSlot >= 0;
            int fill = used ? ColorUtil.replAlpha(ColorUtil.client(), hovered ? 0.26f : 0.18f) : ColorUtil.replAlpha(ColorUtil.getColor(40, 0.7f), hovered ? 1.0f : 0.9f);
            Draw.rect(x, y, this.cardW, this.cardH, fill, 8.0f);
            Draw.outline(x, y, this.cardW, this.cardH, used ? 1.1f : 0.6f, ColorUtil.replAlpha(used ? ColorUtil.client() : ColorUtil.getColor(90), hovered ? 1.0f : 0.5f), 8.0f);
            float prevH = this.cardH;
            this.drawPreview(entry, x + this.cardW / 2.0f, y + (this.cardH - 24.0f) / 2.0f + 2.0f, this.cardW - 12.0f, hovered ? 255 : 235);
            NAME.drawCentered(this.trim(entry.name(), this.cardW - 4.0f, nameSize), x + this.cardW / 2.0f, y + prevH - 13.0f, nameSize, ColorUtil.getColor(hovered ? 255 : 190));
            if (!used) continue;
            float bs = 14.0f;
            float bx = x + this.cardW - bs - 3.0f;
            float by = y + 3.0f;
            Draw.rect(bx, by, bs, bs, ColorUtil.replAlpha(ColorUtil.client(), 0.95f), bs / 2.0f);
            NAME.drawCentered(String.valueOf(usedSlot + 1), bx + bs / 2.0f, by + 3.0f, 7.5f, ColorUtil.getColor(255));
        }
        this.drawPager(centerX, this.footerY, mouseX, mouseY);
        Draw.flush();
    }

    private void drawPager(float centerX, float y, int mouseX, int mouseY) {
        float size = 14.0f;
        float px = centerX - 52.0f;
        float nx = centerX + 38.0f;
        boolean hoverPrev = EmotionSettingsScreen.hover(mouseX, mouseY, px, y, size, size);
        boolean hoverNext = EmotionSettingsScreen.hover(mouseX, mouseY, nx, y, size, size);
        NAME.drawCentered("<", px + size / 2.0f, y + 3.0f, 10.0f, ColorUtil.getColor(this.page > 0 ? (hoverPrev ? 255 : 200) : 90));
        NAME.drawCentered(">", nx + size / 2.0f, y + 3.0f, 10.0f, ColorUtil.getColor(this.page < this.pageCount - 1 ? (hoverNext ? 255 : 200) : 90));
        NAME.drawCentered(this.page + 1 + " / " + this.pageCount, centerX, y + 3.0f, 8.5f, ColorUtil.getColor(200));
        NAME.drawCentered("\u041b\u041a\u041c \u2014 \u0432\u044b\u0431\u0440\u0430\u0442\u044c \u00b7 \u041f\u041a\u041c \u2014 \u0443\u0431\u0440\u0430\u0442\u044c \u00b7 \u043a\u043e\u043b\u0435\u0441\u043e \u2014 \u043b\u0438\u0441\u0442\u0430\u0442\u044c", centerX + this.panelW * 0.28f, y + 3.0f, 8.0f, ColorUtil.getColor(150));
    }

    public boolean method_25402(class_11909 click, boolean doubled) {
        double mouseX = click.comp_4798();
        double mouseY = click.comp_4799();
        boolean right = click.method_74245() == 1;
        EmotionSlots slots = EmotionSlots.instance();
        List<EmoteCatalog.Entry> all = EmoteCatalog.instance().entries();
        float centerX = this.panelX + this.panelW / 2.0f;
        float size = 14.0f;
        if (EmotionSettingsScreen.hover(mouseX, mouseY, centerX - 52.0f, this.footerY, size, size)) {
            if (this.page > 0) {
                --this.page;
            }
            return true;
        }
        if (EmotionSettingsScreen.hover(mouseX, mouseY, centerX + 38.0f, this.footerY, size, size)) {
            if (this.page < this.pageCount - 1) {
                ++this.page;
            }
            return true;
        }
        for (int i = 0; i < 7; ++i) {
            if (!EmotionSettingsScreen.hover(mouseX, mouseY, this.slotCx[i] - 22.0f, this.slotCy[i] - 22.0f, 44.0f, 44.0f)) continue;
            if (right) {
                slots.clear(i);
            } else {
                this.selectedSlot = i;
            }
            return true;
        }
        int first = this.page * this.perPage;
        for (int i = 0; i < this.perPage && first + i < all.size(); ++i) {
            float x = this.gridX + (float)(i % this.cols) * (this.cardW + 6.0f);
            float y = this.gridY + (float)(i / this.cols) * (this.cardH + 6.0f);
            if (!EmotionSettingsScreen.hover(mouseX, mouseY, x, y, this.cardW, this.cardH)) continue;
            EmoteCatalog.Entry entry = all.get(first + i);
            if (right) {
                slots.remove(entry);
            } else {
                int existing = slots.indexOf(entry);
                if (existing >= 0 && existing != this.selectedSlot) {
                    slots.clear(existing);
                }
                slots.set(this.selectedSlot, entry);
                if (this.closeOnPick) {
                    this.method_25419();
                }
            }
            return true;
        }
        return super.method_25402(click, doubled);
    }

    public boolean method_25401(double mouseX, double mouseY, double horizontal, double vertical) {
        if (vertical > 0.0) {
            if (this.page > 0) {
                --this.page;
            }
        } else if (vertical < 0.0 && this.page < this.pageCount - 1) {
            ++this.page;
        }
        return true;
    }

    public boolean method_25404(class_11908 input) {
        if (input.comp_4795() == 256) {
            this.method_25419();
            return true;
        }
        if (input.comp_4795() == 262 || input.comp_4795() == 267) {
            if (this.page < this.pageCount - 1) {
                ++this.page;
            }
            return true;
        }
        if (input.comp_4795() == 263 || input.comp_4795() == 266) {
            if (this.page > 0) {
                --this.page;
            }
            return true;
        }
        return super.method_25404(input);
    }

    public void method_25419() {
        EmotePreviews.clear();
        if (this.field_22787 != null) {
            this.field_22787.method_1507(this.parent);
        }
    }

    private void drawPreview(EmoteCatalog.Entry entry, float cx, float cy, float maxW, int alpha) {
        class_2960 preview = EmotePreviews.preview(entry.id());
        if (preview != null) {
            float w = maxW;
            float h = w * 1.25f;
            Draw.texture(preview, cx - w / 2.0f, cy - h / 2.0f, w, h, ColorUtil.getColor(255, (float)alpha / 255.0f));
        } else {
            NAME.drawCentered(entry.name().isEmpty() ? "?" : entry.name().substring(0, 1).toUpperCase(), cx, cy - 5.0f, maxW * 0.7f, ColorUtil.getColor(220, (float)alpha / 255.0f));
        }
    }

    private String trim(String text, float maxWidth, float size) {
        if (NAME.getWidth(text, size) <= maxWidth) {
            return text;
        }
        String result = text;
        while (result.length() > 1 && NAME.getWidth(result + "\u2026", size) > maxWidth) {
            result = result.substring(0, result.length() - 1);
        }
        return result + "\u2026";
    }

    private static void drawPlus(float cx, float cy, float size, float thickness, int color) {
        float half = size * 0.5f;
        Draw.rect(cx - half, cy - thickness * 0.5f, size, thickness, color, thickness * 0.5f);
        Draw.rect(cx - thickness * 0.5f, cy - half, thickness, size, color, thickness * 0.5f);
    }

    private static boolean hover(double mouseX, double mouseY, float x, float y, float w, float h) {
        return mouseX >= (double)x && mouseX < (double)(x + w) && mouseY >= (double)y && mouseY < (double)(y + h);
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_11908
 *  net.minecraft.class_11909
 *  net.minecraft.class_1799
 *  net.minecraft.class_2561
 *  net.minecraft.class_332
 *  net.minecraft.class_437
 *  org.joml.Matrix3x2fStack
 *  org.lwjgl.glfw.GLFW
 */
package ru.decide.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_11908;
import net.minecraft.class_11909;
import net.minecraft.class_1799;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_437;
import org.joml.Matrix3x2fStack;
import org.lwjgl.glfw.GLFW;
import ru.decide.module.impl.utils.AutoSwapModule;
import ru.decide.utils.animation.satoshi.Animation;
import ru.decide.utils.animation.satoshi.Direction;
import ru.decide.utils.animation.satoshi.EaseInOutQuad;
import ru.decide.utils.annotation.IMinecraft;
import ru.decide.utils.colors.ColorUtil;
import ru.decide.utils.render.Draw;
import ru.decide.utils.render.RenderUtil;
import ru.decide.utils.render.ScreenBlur;

@Environment(value=EnvType.CLIENT)
public final class AutoSwapScreen
extends class_437
implements IMinecraft {
    private static final float RADIUS = 72.0f;
    private static final float DEAD_ZONE = 40.0f;
    private static final float SLOT = 40.0f;
    private final AutoSwapModule module;
    private final int heldKey;
    private final int slotCount;
    private final boolean[] available;
    private final EaseInOutQuad[] hoverAnims;

    public AutoSwapScreen(AutoSwapModule module, int heldKey) {
        super((class_2561)class_2561.method_43470((String)"Auto Swap"));
        this.module = module;
        this.heldKey = heldKey;
        this.slotCount = module.getSlotCount();
        this.available = new boolean[this.slotCount];
        this.hoverAnims = new EaseInOutQuad[this.slotCount];
        for (int i = 0; i < this.slotCount; ++i) {
            this.hoverAnims[i] = new EaseInOutQuad(200, 1.0);
        }
        this.refresh();
    }

    public boolean belongsTo(AutoSwapModule other) {
        return this.module == other;
    }

    private void refresh() {
        for (int i = 0; i < this.slotCount; ++i) {
            this.available[i] = this.module.available(i);
        }
    }

    protected void method_25426() {
    }

    public boolean method_25421() {
        return false;
    }

    public void method_25420(class_332 context, int mouseX, int mouseY, float delta) {
    }

    public void method_25393() {
        if (this.field_22787 == null || this.field_22787.field_1724 == null || !this.module.isEnabled() || !this.module.mode.is("\u041c\u0443\u043b\u044c\u0442\u0438")) {
            this.method_25419();
        }
        if (this.field_22787 != null && this.field_22787.field_1690 != null) {
            this.field_22787.field_1690.field_1894.method_23481(GLFW.glfwGetKey((long)this.field_22787.method_22683().method_4490(), (int)87) == 1);
            this.field_22787.field_1690.field_1881.method_23481(GLFW.glfwGetKey((long)this.field_22787.method_22683().method_4490(), (int)83) == 1);
            this.field_22787.field_1690.field_1913.method_23481(GLFW.glfwGetKey((long)this.field_22787.method_22683().method_4490(), (int)65) == 1);
            this.field_22787.field_1690.field_1849.method_23481(GLFW.glfwGetKey((long)this.field_22787.method_22683().method_4490(), (int)68) == 1);
            this.field_22787.field_1690.field_1903.method_23481(GLFW.glfwGetKey((long)this.field_22787.method_22683().method_4490(), (int)32) == 1);
            this.field_22787.field_1690.field_1867.method_23481(GLFW.glfwGetKey((long)this.field_22787.method_22683().method_4490(), (int)341) == 1);
        }
    }

    private int sector(double mouseX, double mouseY) {
        double dx = mouseX - (double)this.field_22789 / 2.0;
        double dy = mouseY - (double)this.field_22790 / 2.0;
        if (Math.hypot(dx, dy) <= 40.0) {
            return -1;
        }
        double angle = (Math.toDegrees(Math.atan2(-dx, dy)) + 180.0) % 360.0;
        int index = (int)Math.floor(angle / (360.0 / (double)this.slotCount));
        return index >= this.slotCount ? 0 : index;
    }

    public void method_25394(class_332 context, int mouseX, int mouseY, float delta) {
        if (this.field_22787 == null || this.field_22787.field_1724 == null) {
            return;
        }
        float alpha = 1.0f;
        Draw.rect(0.0f, 0.0f, this.field_22789, this.field_22790, ColorUtil.getColor(0, 0.45f * alpha));
        ScreenBlur.capture(1);
        float size = 240.0f;
        float px = (float)this.field_22789 / 2.0f - size / 2.0f;
        float py = (float)this.field_22790 / 2.0f - size / 2.0f;
        float half = size / 2.0f;
        Draw.glow(px, py, size, size, ColorUtil.replAlpha(ColorUtil.client(), 0.2f * alpha), half, 14.0f, 1.0f);
        Draw.glass(px, py, size, size, alpha, half, half, half, half, ColorUtil.multAlpha(ColorUtil.multDark(ColorUtil.background(), 0.6f), alpha));
        Draw.glassOutline(px, py, size, size, 0.5f, half, alpha, 0.8f);
        int selected = this.sector(mouseX, mouseY);
        for (int i = 0; i < this.slotCount; ++i) {
            String name;
            float y;
            double angle = ((double)i + 0.5) * Math.PI * 2.0 / (double)this.slotCount;
            float x = (float)((double)this.field_22789 / 2.0 + Math.sin(angle) * 72.0);
            boolean hovered = this.inside(mouseX, mouseY, x - 20.0f, (y = (float)((double)this.field_22790 / 2.0 - Math.cos(angle) * 72.0)) - 20.0f, 40.0f, 40.0f) || selected == i;
            this.hoverAnims(i).setDirection(hovered ? Direction.FORWARDS : Direction.BACKWARDS);
            float sel = this.hoverAnims(i).getOutput();
            class_1799 item = this.module.savedItems().get(i);
            boolean missing = !item.method_7960() && !this.available[i];
            RenderUtil.Render2D.rect(x - 20.0f, y - 20.0f, 40.0f, 40.0f, ColorUtil.overCol(ColorUtil.getColor(40, 0.5f * alpha), ColorUtil.replAlpha(ColorUtil.client(), 0.55f * alpha), sel), 9.0f);
            RenderUtil.Render2D.outline(x - 20.0f, y - 20.0f, 40.0f, 40.0f, 0.5f, missing ? ColorUtil.getColor(235, 70, 70, alpha * 0.8f) : ColorUtil.replAlpha(ColorUtil.client(), alpha * Math.max(hovered ? 0.9f : 0.25f, sel)), 9.0f);
            Matrix3x2fStack matrices = context.method_51448();
            matrices.pushMatrix();
            matrices.translate(x, y);
            matrices.scale(2.0f, 2.0f);
            if (item.method_7960()) {
                matrices.popMatrix();
                context.method_25300(this.field_22787.field_1772, "+", (int)x, (int)y - 4, ColorUtil.getColor(255, 0.7f * alpha));
                continue;
            }
            context.method_51427(item, -8, -8);
            matrices.popMatrix();
            if (!hovered || !((float)this.field_22787.field_1772.method_1727(name = item.method_7964().getString()) <= size - 30.0f)) continue;
            context.method_25300(this.field_22787.field_1772, name, this.field_22789 / 2, (int)(py + size - 18.0f), ColorUtil.getColor(220, alpha));
        }
        context.method_25300(this.field_22787.field_1772, "\u041e\u0442\u043f\u0443\u0441\u0442\u0438 \u043a\u043b\u0430\u0432\u0438\u0448\u0443 \u2014 \u0441\u0432\u0430\u043f \u00b7 \u041f\u041a\u041c \u043f\u043e \u043f\u0443\u0441\u0442\u043e\u043c\u0443 \u2014 \u0432\u044b\u0431\u0440\u0430\u0442\u044c \u043f\u0440\u0435\u0434\u043c\u0435\u0442 \u00b7 Esc \u2014 \u0432\u044b\u0445\u043e\u0434", this.field_22789 / 2, this.field_22790 - 24, ColorUtil.getColor(170, 0.8f * alpha));
    }

    private Animation hoverAnims(int i) {
        return this.hoverAnims[i];
    }

    private boolean inside(double mx, double my, float x, float y, float w, float h) {
        return mx >= (double)x && mx < (double)(x + w) && my >= (double)y && my < (double)(y + h);
    }

    private void finishSelection() {
        if (this.field_22787 == null || this.field_22787.field_1755 != this) {
            return;
        }
        int index = this.sector(this.field_22787.field_1729.method_1603() * (double)this.field_22789 / (double)this.field_22787.method_22683().method_4480(), this.field_22787.field_1729.method_1604() * (double)this.field_22790 / (double)this.field_22787.method_22683().method_4507());
        this.method_25419();
        if (index >= 0 && index < this.slotCount && this.available[index]) {
            this.module.select(index);
        }
    }

    public boolean method_16803(class_11908 input) {
        if (input.comp_4795() == this.heldKey) {
            this.finishSelection();
            return true;
        }
        return super.method_16803(input);
    }

    public boolean method_25404(class_11908 input) {
        if (input.comp_4795() == 256) {
            this.method_25419();
            return true;
        }
        return true;
    }

    public boolean method_25406(class_11909 click) {
        if (click.method_74245() == this.heldKey) {
            this.finishSelection();
        }
        return true;
    }

    public boolean method_25402(class_11909 click, boolean doubled) {
        if (this.field_22787 == null || this.field_22787.field_1724 == null) {
            return true;
        }
        int index = this.sector(click.comp_4798(), click.comp_4799());
        if (click.method_74245() == 1 && index >= 0) {
            if (this.module.savedItems().get(index).method_7960()) {
                this.method_25419();
                this.module.startPick(index);
            } else {
                this.module.removeAt(index);
                this.refresh();
            }
        } else if (click.method_74245() == 0 && index >= 0 && this.available[index]) {
            this.finishSelection();
        }
        return true;
    }

    public void method_25419() {
        if (this.field_22787 != null && this.field_22787.field_1755 == this) {
            this.field_22787.method_1507(null);
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_11909
 *  net.minecraft.class_1657
 *  net.minecraft.class_1713
 *  net.minecraft.class_1735
 *  net.minecraft.class_310
 *  net.minecraft.class_332
 *  net.minecraft.class_490
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package ru.decide.mixin;

import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_11909;
import net.minecraft.class_1657;
import net.minecraft.class_1713;
import net.minecraft.class_1735;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_490;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.decide.Client;
import ru.decide.mixin.HandledScreenAccessor;
import ru.decide.module.impl.player.LockSlot;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_490.class})
public abstract class InventoryScreenMixin {
    @Unique
    private static final int BTN_W = 52;
    @Unique
    private static final int BTN_H = 12;
    @Unique
    private static final int BTN_OFFSET_X = 3;
    @Unique
    private static final int BTN_OFFSET_Y = -15;
    @Unique
    private int btnX;
    @Unique
    private int btnY;
    @Unique
    private boolean hovered = false;

    @Inject(method={"method_25394"}, at={@At(value="TAIL")})
    private void onRender(class_332 ctx, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        class_490 self = (class_490)this;
        class_310 mc = class_310.method_1551();
        if (mc.field_1724 == null) {
            return;
        }
        int screenX = ((HandledScreenAccessor)self).getScreenX();
        int screenY = ((HandledScreenAccessor)self).getScreenY();
        this.btnX = screenX + 3;
        this.btnY = screenY + -15;
        this.hovered = mouseX >= this.btnX && mouseX <= this.btnX + 52 && mouseY >= this.btnY && mouseY <= this.btnY + 12;
        int bg = this.hovered ? -3394765 : -7855582;
        ctx.method_25294(this.btnX, this.btnY, this.btnX + 52, this.btnY + 12, bg);
        String label = "\u0412\u044b\u043a\u0438\u043d\u0443\u0442\u044c \u0432\u0441\u0451";
        int textW = mc.field_1772.method_1727(label);
        int textX = this.btnX + (52 - textW) / 2;
        Objects.requireNonNull(mc.field_1772);
        int textY = this.btnY + (12 - 9) / 2 + 1;
        ctx.method_51433(mc.field_1772, label, textX, textY, -1, false);
    }

    @Inject(method={"method_25406"}, at={@At(value="HEAD")}, cancellable=true)
    private void onMouseClicked(class_11909 click, CallbackInfoReturnable<Boolean> cir) {
        if (click.method_74245() != 0) {
            return;
        }
        if (!this.hovered) {
            return;
        }
        class_310 mc = class_310.method_1551();
        if (mc.field_1724 == null || mc.field_1761 == null) {
            return;
        }
        int syncId = mc.field_1724.field_7512.field_7763;
        int slotCount = mc.field_1724.field_7512.field_7761.size();
        LockSlot lockSlot = Client.get().moduleManager().get(LockSlot.class);
        for (int i = 0; i < slotCount; ++i) {
            if (!((class_1735)mc.field_1724.field_7512.field_7761.get(i)).method_7681() || lockSlot != null && lockSlot.isDropProtected(((class_1735)mc.field_1724.field_7512.field_7761.get(i)).method_7677(), InventoryScreenMixin.toInventorySlot(i))) continue;
            mc.field_1761.method_2906(syncId, i, 1, class_1713.field_7795, (class_1657)mc.field_1724);
        }
        cir.setReturnValue((Object)true);
        cir.cancel();
    }

    @Unique
    private static int toInventorySlot(int screenSlot) {
        if (screenSlot >= 36 && screenSlot <= 44) {
            return screenSlot - 36;
        }
        if (screenSlot >= 9 && screenSlot <= 35) {
            return screenSlot;
        }
        return -1;
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1703
 *  net.minecraft.class_1735
 *  net.minecraft.class_1799
 *  net.minecraft.class_310
 *  net.minecraft.class_332
 *  net.minecraft.class_465
 *  net.minecraft.class_490
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package ru.decide.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1703;
import net.minecraft.class_1735;
import net.minecraft.class_1799;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_465;
import net.minecraft.class_490;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.decide.Client;
import ru.decide.inventorypreset.InventoryPreset;
import ru.decide.inventorypreset.InventoryPresetManager;
import ru.decide.module.impl.render.BetterMinecraft;
import ru.decide.utils.render.ItemMoveAnimator;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_465.class})
public abstract class HandledScreenMixin {
    @Shadow
    @Final
    protected class_1703 field_2797;
    @Shadow
    protected int field_2776;
    @Shadow
    protected int field_2800;
    @Unique
    private boolean decide$panelAnimated;
    @Unique
    private boolean decide$contentsAnimated;

    @Shadow
    public abstract class_1703 method_17577();

    @Inject(method={"method_25426"}, at={@At(value="TAIL")})
    private void decide$trackInventoryOpen(CallbackInfo ci) {
        BetterMinecraft.markInventoryOpen();
    }

    @Inject(method={"method_25420"}, at={@At(value="INVOKE", target="Lnet/minecraft/class_437;method_25420(Lnet/minecraft/class_332;IIF)V", shift=At.Shift.AFTER)})
    private void decide$animatePanel(class_332 context, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        this.decide$panelAnimated = BetterMinecraft.inventoryAnimationEnabled();
        if (!this.decide$panelAnimated) {
            return;
        }
        context.method_51448().pushMatrix();
        context.method_51448().translate(0.0f, BetterMinecraft.inventorySlideOffset());
    }

    @Inject(method={"method_25420"}, at={@At(value="RETURN")})
    private void decide$endPanelAnimation(class_332 context, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (!this.decide$panelAnimated) {
            return;
        }
        this.decide$panelAnimated = false;
        context.method_51448().popMatrix();
    }

    @Inject(method={"method_71085"}, at={@At(value="HEAD")})
    private void decide$beginContentsAnimation(class_332 context, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        this.decide$contentsAnimated = BetterMinecraft.inventoryAnimationEnabled();
        if (!this.decide$contentsAnimated) {
            return;
        }
        context.method_51448().pushMatrix();
        context.method_51448().translate(0.0f, BetterMinecraft.inventorySlideOffset());
    }

    @Inject(method={"method_71085"}, at={@At(value="RETURN")})
    private void decide$endContentsAnimation(class_332 context, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (!this.decide$contentsAnimated) {
            return;
        }
        this.decide$contentsAnimated = false;
        context.method_51448().popMatrix();
    }

    @Inject(method={"method_64508"}, at={@At(value="HEAD")})
    private void decide$beginSlotAnim(class_332 context, int mouseX, int mouseY, CallbackInfo ci) {
        if (!BetterMinecraft.itemMoveAnimationEnabled()) {
            return;
        }
        ItemMoveAnimator.beginFrame(this.field_2797, mouseX, mouseY, this.field_2776, this.field_2800);
    }

    @WrapOperation(method={"method_64508"}, at={@At(value="INVOKE", target="Lnet/minecraft/class_465;method_2385(Lnet/minecraft/class_332;Lnet/minecraft/class_1735;II)V")})
    private void decide$animateSlot(class_465<?> instance, class_332 context, class_1735 slot, int mouseX, int mouseY, Operation<Void> original) {
        float[] off;
        float[] fArray = off = BetterMinecraft.itemMoveAnimationEnabled() ? ItemMoveAnimator.offset(slot) : null;
        if (off == null) {
            original.call(new Object[]{instance, context, slot, mouseX, mouseY});
            return;
        }
        context.method_51448().pushMatrix();
        if (off[2] != 1.0f) {
            float cx = (float)slot.field_7873 + 8.0f;
            float cy = (float)slot.field_7872 + 8.0f;
            context.method_51448().translate(cx, cy);
            context.method_51448().scale(off[2], off[2]);
            context.method_51448().translate(-cx, -cy);
        } else {
            context.method_51448().translate(off[0], off[1]);
        }
        original.call(new Object[]{instance, context, slot, mouseX, mouseY});
        context.method_51448().popMatrix();
    }

    @Inject(method={"method_2385"}, at={@At(value="TAIL")})
    private void onDrawPresetSlot(class_332 context, class_1735 slot, int mouseX, int mouseY, CallbackInfo ci) {
        class_1799 ghost;
        int color;
        if (!(this instanceof class_490) || Client.get() == null || Client.get().inventoryPresetManager() == null) {
            return;
        }
        InventoryPresetManager manager = Client.get().inventoryPresetManager();
        InventoryPreset preset = manager.activePreset().orElse(null);
        if (preset == null) {
            return;
        }
        int logical = InventoryPresetManager.screenToLogicalSlot(slot.field_7874);
        if (logical < 0) {
            return;
        }
        InventoryPreset.Entry expected = preset.slot(logical);
        if (expected.isEmpty()) {
            return;
        }
        InventoryPresetManager.SlotState state = manager.stateFor(logical);
        switch (state) {
            case CORRECT: {
                int n = -12400277;
                break;
            }
            case WRONG_SLOT: {
                int n = -1787569;
                break;
            }
            case MISSING: {
                int n = -2073503;
                break;
            }
            default: {
                int n = color = 0;
            }
        }
        if (state != InventoryPresetManager.SlotState.CORRECT && !(ghost = expected.toStack()).method_7960()) {
            context.method_51427(ghost, slot.field_7873, slot.field_7872);
            context.method_25294(slot.field_7873, slot.field_7872, slot.field_7873 + 16, slot.field_7872 + 16, 1695156746);
            context.method_51431(class_310.method_1551().field_1772, ghost, slot.field_7873, slot.field_7872);
        }
        if (color != 0) {
            context.method_25294(slot.field_7873, slot.field_7872, slot.field_7873 + 16, slot.field_7872 + 1, color);
            context.method_25294(slot.field_7873, slot.field_7872 + 15, slot.field_7873 + 16, slot.field_7872 + 16, color);
            context.method_25294(slot.field_7873, slot.field_7872, slot.field_7873 + 1, slot.field_7872 + 16, color);
            context.method_25294(slot.field_7873 + 15, slot.field_7872, slot.field_7873 + 16, slot.field_7872 + 16, color);
        }
    }
}


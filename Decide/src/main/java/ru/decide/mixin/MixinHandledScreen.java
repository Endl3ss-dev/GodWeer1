/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1041
 *  net.minecraft.class_11908
 *  net.minecraft.class_11909
 *  net.minecraft.class_1657
 *  net.minecraft.class_1703
 *  net.minecraft.class_1707
 *  net.minecraft.class_1713
 *  net.minecraft.class_1735
 *  net.minecraft.class_1792
 *  net.minecraft.class_1799
 *  net.minecraft.class_2561
 *  net.minecraft.class_310
 *  net.minecraft.class_364
 *  net.minecraft.class_3675
 *  net.minecraft.class_4185
 *  net.minecraft.class_437
 *  net.minecraft.class_465
 *  net.minecraft.class_476
 *  net.minecraft.class_490
 *  org.jetbrains.annotations.Nullable
 *  org.lwjgl.glfw.GLFW
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package ru.decide.mixin;

import java.util.ArrayList;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1041;
import net.minecraft.class_11908;
import net.minecraft.class_11909;
import net.minecraft.class_1657;
import net.minecraft.class_1703;
import net.minecraft.class_1707;
import net.minecraft.class_1713;
import net.minecraft.class_1735;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_364;
import net.minecraft.class_3675;
import net.minecraft.class_4185;
import net.minecraft.class_437;
import net.minecraft.class_465;
import net.minecraft.class_476;
import net.minecraft.class_490;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.decide.Client;
import ru.decide.inventorypreset.InventoryPresetManager;
import ru.decide.mixin.HandledScreenAccessor;
import ru.decide.module.impl.player.LockSlot;
import ru.decide.module.impl.utils.AutoSwapModule;
import ru.decide.module.impl.utils.ItemScroller;
import ru.decide.screen.InventoryPresetScreen;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_465.class})
public abstract class MixinHandledScreen<T extends class_1703>
extends class_437 {
    @Shadow
    @Final
    protected T field_2797;
    @Shadow
    @Nullable
    protected class_1735 field_2787;
    private long time = 0L;
    @Unique
    private static final int PRESET_BUTTON_SIZE = 18;
    @Unique
    private class_4185 presetButton;

    @Shadow
    protected abstract void method_2383(class_1735 var1, int var2, int var3, class_1713 var4);

    protected MixinHandledScreen(class_2561 title) {
        super(title);
    }

    @Inject(method={"method_25426"}, at={@At(value="TAIL")})
    private void initPresetButton(CallbackInfo ci) {
        if (!(this instanceof class_490) || this.field_22787 == null) {
            return;
        }
        HandledScreenAccessor accessor = (HandledScreenAccessor)((Object)this);
        this.presetButton = class_4185.method_46430((class_2561)class_2561.method_43473(), button -> this.field_22787.method_1507((class_437)new InventoryPresetScreen(this))).method_46434(accessor.getScreenX() + 151, accessor.getScreenY() - 20, 18, 18).method_46431();
        this.method_37063((class_364)this.presetButton);
    }

    @Inject(method={"method_25402"}, at={@At(value="HEAD")}, cancellable=true)
    private void clickPresetUi(class_11909 click, boolean doubled, CallbackInfoReturnable<Boolean> cir) {
        int panelH;
        int arrangeY;
        if (!(this instanceof class_490) || this.field_22787 == null || this.field_22787.field_1724 == null || Client.get() == null) {
            return;
        }
        AutoSwapModule autoSwap = Client.get().moduleManager().get(AutoSwapModule.class);
        if (autoSwap != null && autoSwap.isPicking() && click.method_74245() == 0 && this.field_2787 != null && this.field_2787.method_7681()) {
            class_1799 stack = this.field_2787.method_7677().method_7972();
            this.field_22787.field_1761.method_2906(this.field_22787.field_1724.field_7512.field_7763, this.field_2787.field_7874, 40, class_1713.field_7791, (class_1657)this.field_22787.field_1724);
            autoSwap.finishPick(stack);
            this.field_22787.method_1507(null);
            cir.setReturnValue((Object)true);
            return;
        }
        if (click.method_74245() != 0) {
            return;
        }
        InventoryPresetManager manager = Client.get().inventoryPresetManager();
        if (manager == null || manager.activePreset().isEmpty()) {
            return;
        }
        HandledScreenAccessor accessor = (HandledScreenAccessor)((Object)this);
        int guiX = accessor.getScreenX();
        int guiY = accessor.getScreenY();
        double mouseX = click.comp_4798();
        double mouseY = click.comp_4799();
        Map<String, InventoryPresetManager.MissingItem> missing = manager.missingItems();
        ArrayList<InventoryPresetManager.MissingItem> missingItems = new ArrayList<InventoryPresetManager.MissingItem>(missing.values());
        int visibleRows = Math.min(8, Math.max(1, missingItems.size()));
        int missingScroll = manager.missingScroll(missingItems.size(), visibleRows);
        int panelX = guiX + 182;
        int panelY = guiY + 8;
        int panelW = 118;
        int rowH = 21;
        int rowY = panelY + 25;
        for (int index = missingScroll; index < Math.min(missingItems.size(), missingScroll + visibleRows); ++index) {
            InventoryPresetManager.MissingItem item = (InventoryPresetManager.MissingItem)missingItems.get(index);
            if (MixinHandledScreen.inside(mouseX, mouseY, panelX + 4, rowY, panelW - 8, rowH - 2)) {
                manager.searchAuction(item);
                cir.setReturnValue((Object)true);
                return;
            }
            rowY += rowH;
        }
        if (missing.isEmpty()) {
            rowY += rowH;
        }
        if (MixinHandledScreen.inside(mouseX, mouseY, panelX + 5, arrangeY = panelY + (panelH = 31 + visibleRows * rowH + 27) - 23, panelW - 10, 18)) {
            if (manager.isArranging()) {
                manager.cancelArrange(true);
            } else {
                manager.startArrange();
            }
            cir.setReturnValue((Object)true);
        }
    }

    @Inject(method={"method_25401"}, at={@At(value="HEAD")}, cancellable=true)
    private void scrollPresetMissing(double mouseX, double mouseY, double horizontal, double vertical, CallbackInfoReturnable<Boolean> cir) {
        int panelH;
        int panelY;
        if (!(this instanceof class_490) || Client.get() == null) {
            return;
        }
        InventoryPresetManager manager = Client.get().inventoryPresetManager();
        if (manager == null || manager.activePreset().isEmpty()) {
            return;
        }
        int size = manager.missingItems().size();
        if (size <= 8) {
            return;
        }
        HandledScreenAccessor accessor = (HandledScreenAccessor)((Object)this);
        int panelX = accessor.getScreenX() + 182;
        if (!MixinHandledScreen.inside(mouseX, mouseY, panelX, panelY = accessor.getScreenY() + 8, 118, panelH = 226)) {
            return;
        }
        manager.scrollMissing(vertical < 0.0 ? 1 : -1, size, 8);
        cir.setReturnValue((Object)true);
    }

    @Unique
    private static boolean inside(double mouseX, double mouseY, int x, int y, int w, int h) {
        return mouseX >= (double)x && mouseX < (double)(x + w) && mouseY >= (double)y && mouseY < (double)(y + h);
    }

    @Inject(method={"method_25394"}, at={@At(value="HEAD")})
    private void onRender(CallbackInfo ci) {
        ItemScroller itemScroller = ItemScroller.getInstance();
        if (this.field_22787 == null || !itemScroller.isEnabled()) {
            return;
        }
        boolean isShiftDown = this.hasShiftDown();
        if (GLFW.glfwGetMouseButton((long)this.field_22787.method_22683().method_4490(), (int)0) == 1 && isShiftDown && this.field_2787 != null && this.field_2787.method_7681() && (float)(System.currentTimeMillis() - this.time) > itemScroller.delay.getValue().floatValue()) {
            this.method_2383(this.field_2787, this.field_2787.field_7874, 0, class_1713.field_7794);
            this.time = System.currentTimeMillis();
        }
    }

    @Unique
    private boolean hasShiftDown() {
        return class_3675.method_15987((class_1041)class_310.method_1551().method_22683(), (int)340) || class_3675.method_15987((class_1041)class_310.method_1551().method_22683(), (int)344);
    }

    @Unique
    private boolean hasControlDown() {
        return class_3675.method_15987((class_1041)class_310.method_1551().method_22683(), (int)341) || class_3675.method_15987((class_1041)class_310.method_1551().method_22683(), (int)345);
    }

    @Inject(method={"method_25404"}, at={@At(value="HEAD")}, cancellable=true)
    private void onKeyPressed(class_11908 input, CallbackInfoReturnable<Boolean> cir) {
        if (this.field_22787 == null || this.field_22787.field_1724 == null) {
            return;
        }
        LockSlot lockSlot = Client.get().moduleManager().get(LockSlot.class);
        if (this.field_22787.field_1690.field_1869.method_1417(input) && this.field_2787 != null && this.field_2787.method_7681() && lockSlot != null && lockSlot.isDropProtected(this.field_2787.method_7677(), this.toInventorySlot(this.field_2787.field_7874))) {
            cir.setReturnValue((Object)true);
            cir.cancel();
            return;
        }
        ItemScroller itemScroller = ItemScroller.getInstance();
        if (itemScroller == null || !itemScroller.isEnabled()) {
            return;
        }
        if (this.field_22787.field_1690.field_1869.method_1417(input) && this.hasControlDown() && this.hasShiftDown() && this.field_2787 != null && this.field_2787.method_7681()) {
            class_1799 hoveredStack = this.field_2787.method_7677();
            class_1792 itemToDrop = hoveredStack.method_7909();
            for (class_1735 slot : ((class_1703)this.field_2797).field_7761) {
                int inventorySlot = this.toInventorySlot(slot.field_7874);
                if (!slot.method_7681() || slot.method_7677().method_7909() != itemToDrop || lockSlot != null && lockSlot.isDropProtected(slot.method_7677(), inventorySlot)) continue;
                this.method_2383(slot, slot.field_7874, 1, class_1713.field_7795);
            }
            cir.setReturnValue((Object)true);
            cir.cancel();
        }
    }

    @Unique
    private int toInventorySlot(int screenSlot) {
        if (this instanceof class_490) {
            return InventoryPresetManager.screenToLogicalSlot(screenSlot);
        }
        MixinHandledScreen mixinHandledScreen = this;
        if (mixinHandledScreen instanceof class_476) {
            class_476 screen = (class_476)mixinHandledScreen;
            int containerSlots = ((class_1707)screen.method_17577()).method_17388() * 9;
            int relative = screenSlot - containerSlots;
            if (relative >= 0 && relative < 27) {
                return 9 + relative;
            }
            if (relative >= 27 && relative < 36) {
                return relative - 27;
            }
        }
        return -1;
    }
}


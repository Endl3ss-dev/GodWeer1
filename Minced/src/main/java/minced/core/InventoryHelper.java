/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.ingame.InventoryScreen
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.screen.slot.Slot
 *  net.minecraft.screen.slot.SlotActionType
 */
package minced.core;

import minced.core.EventInventoryClose;
import minced.core.EventSlotClick;
import minced.core.ChatUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

public class InventoryHelper {
    static int ШБ;

    int field12252 = -1;
    boolean field12253 = false;
    int field12254 = -1;
    int field12255 = -1;
    static String field12256;
    ItemStack[] field12257 = new ItemStack[3];
    static MinecraftClient I = MinecraftClient.getInstance();
    boolean field12258 = false;

    public int method3511() {
        return this.field12252;
    }

    @EventHandler(2)
    public void O(EventInventoryClose param1) {
        if (!this.field12253) {
            return;
        }
        param1.method2298(true);
        this.field12253 = false;
        this.field12254 = -1;
    }

    public void method3512(int param1) {
        if (!this.method3521(param1)) {
            this.field12255 = -1;
            return;
        }
        this.field12255 = this.field12255 == param1 ? -1 : param1;
    }

    @EventHandler(2)
    public void method3513(EventSlotClick param1) {
        if (this.field12254 < 0) {
            return;
        }
        Slot v2 = param1.net_minecraft_screen_slot_Slot_I();
        if (v2 == null) {
            return;
        }
        if ((param1.net_minecraft_screen_slot_SlotActionType_I() != SlotActionType.PICKUP || param1.method2526() != 0 && param1.method2526() != 1) && param1.net_minecraft_screen_slot_SlotActionType_I() != SlotActionType.QUICK_MOVE) {
            return;
        }
        param1.method2298(true);
        if (this.method3521(this.field12254)) {
            ItemStack v3 = v2.getStack();
            if (v3.isEmpty()) {
                return;
            }
            if (this.method3518(v3, this.field12254)) {
                ChatUtil.method3308(field12256, new Object[0]);
            } else {
                this.field12257[this.field12254] = v3.copy();
                if (this.method3515() && this.method3516(InventoryHelper.I.player.getOffHandStack(), v3)) {
                    this.field12252 = this.field12254;
                    this.field12258 = false;
                }
            }
        }
        this.field12254 = -1;
        if (InventoryHelper.I.currentScreen instanceof InventoryScreen) {
            I.setScreen(null);
        }
    }

    public void method3514(int param1) {
        if (!this.method3521(param1)) {
            return;
        }
        this.field12257[param1] = null;
        if (this.field12252 == param1) {
            this.field12252 = -1;
        }
        if (this.field12255 == param1) {
            this.field12255 = -1;
        }
    }

    private boolean method3515() {
        return InventoryHelper.I.player != null && InventoryHelper.I.world != null;
    }

    private boolean method3516(ItemStack param1, ItemStack param2) {
        if (param1 == null || param2 == null || param1.isEmpty() || param2.isEmpty()) {
            return false;
        }
        if (!ItemStack.areItemsAndComponentsEqual((ItemStack)param1, (ItemStack)param2)) {
            return false;
        }
        return param1.getName().getString().equals(param2.getName().getString());
    }

    public boolean method3517() {
        return this.field12258;
    }

    private boolean method3518(ItemStack param1, int param2) {
        for (int v3 = 0; v3 < this.field12257.length; ++v3) {
            ItemStack v4;
            if (v3 == param2 || !this.method3516(v4 = this.field12257[v3], param1)) continue;
            return true;
        }
        return false;
    }

    public int method3519() {
        return this.field12255;
    }

    public ItemStack O() {
        if (!this.method3521(this.field12255) || !this.method3515()) {
            return ItemStack.EMPTY;
        }
        return this.method3520(this.field12255);
    }

    public ItemStack method3520(int param1) {
        if (!this.method3521(param1) || !this.method3515()) {
            return ItemStack.EMPTY;
        }
        ItemStack v2 = this.field12257[param1];
        if (v2 == null || v2.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return this.method3523(v2) ? v2 : ItemStack.EMPTY;
    }

    private boolean method3521(int param1) {
        return param1 >= 0 && param1 < this.field12257.length;
    }

    public boolean method3522() {
        if (!this.field12258) {
            return false;
        }
        this.field12258 = false;
        return true;
    }

    private boolean method3523(ItemStack param1) {
        if (!this.method3515() || param1.isEmpty()) {
            return false;
        }
        for (int v2 = 0; v2 < InventoryHelper.I.player.getInventory().size(); ++v2) {
            ItemStack v3 = InventoryHelper.I.player.getInventory().getStack(v2);
            if (!this.method3516(v3, param1)) continue;
            return true;
        }
        return false;
    }

    public void method3524(int param1) {
        if (!this.method3515() || !this.method3521(param1)) {
            return;
        }
        this.field12254 = param1;
        this.field12253 = true;
        I.setScreen((Screen)new InventoryScreen((PlayerEntity)InventoryHelper.I.player));
    }

    public void method3525(int param1) {
        if (!this.method3521(param1)) {
            this.field12252 = -1;
            return;
        }
        if (this.field12252 != param1) {
            this.field12252 = param1;
            this.field12258 = true;
        }
    }

    static {
        ШБ = 0;
        field12256 = "Этот предмет уже добавлен в другое деление колеса";
    }
}




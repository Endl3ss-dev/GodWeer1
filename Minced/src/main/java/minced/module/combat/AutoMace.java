/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 */
package minced.module.combat;
import minced.core.EventHandler;

import java.util.function.BooleanSupplier;
import minced.core.EventUpdate;
import minced.core.EventAttack;
import minced.core.InventoryUtil;
import minced.core.Module;
import minced.enums.Combat;
import minced.mixin.accessor.IClientPlayerInteractionManagerMixin;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class AutoMace
extends Module {
    static String field5231 = "Auto Mace";
    static String field5232 = "При попытке удара кого либо, автоматически свапает предмет на булаву";
    int field5233 = -1;
    boolean field5234 = false;
    static MinecraftClient I = MinecraftClient.getInstance();

    public AutoMace() {
        super(field5231, field5232, Combat.Combat);
    }

    @EventHandler(2)
    public void method1254(EventUpdate param1) {
        if (AutoMace.I.player.isOnGround()) {
            return;
        }
        if (this.field5234 && this.field5233 != -1) {
            AutoMace.I.player.getInventory().selectedSlot = this.field5233;
            ((IClientPlayerInteractionManagerMixin)AutoMace.I.interactionManager).invokeSyncSelectedSlot();
            this.field5233 = -1;
            this.field5234 = false;
        }
    }

    @Override
    public void onDisable() {
        this.field5233 = -1;
        this.field5234 = false;
        super.onDisable();
    }

    @EventHandler(2)
    public void method1255(EventAttack param1) {
        if (AutoMace.I.player == null || AutoMace.I.world == null) {
            return;
        }
        if (AutoMace.I.player.isOnGround()) {
            return;
        }
        ItemStack v2 = AutoMace.I.player.getMainHandStack();
        if (v2.getItem() != Items.MACE) {
            int v3 = InventoryUtil.method3336(Items.MACE);
            if (v3 == -1 || v3 >= 9) {
                return;
            }
            this.field5233 = AutoMace.I.player.getInventory().selectedSlot;
            AutoMace.I.player.getInventory().selectedSlot = v3;
            ((IClientPlayerInteractionManagerMixin)AutoMace.I.interactionManager).invokeSyncSelectedSlot();
            this.field5234 = true;
        }
    }
}




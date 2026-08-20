/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.ingame.InventoryScreen
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EquipmentSlot
 *  net.minecraft.entity.effect.StatusEffects
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket
 *  net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket$Mode
 *  net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket
 *  net.minecraft.screen.slot.SlotActionType
 */
package minced.module.misc;
import minced.core.EventHandler;

import minced.core.BindSetting;
import minced.core.BooleanSetting;
import minced.core.SliderSetting;
import minced.core.EventInput;
import minced.core.EventUpdate;
import minced.core.NotificationManager;
import minced.core.ClientState;
import minced.core.ChatUtil;
import minced.core.InventoryUtil;
import minced.core.EventKey;
import minced.core.Minced;
import minced.core.Module;
import minced.enums.Combat;
import minced.module.movement.GuiMove;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.screen.slot.SlotActionType;

public class ElytraHelper
extends Module {
    static String cfr_renamed_380;

    BindSetting field2653 = new BindSetting(field2677);
    int l = 0;
    boolean cfr_renamed_378 = false;
    BindSetting field2654 = new BindSetting(field2678);
    static MinecraftClient I = MinecraftClient.getInstance();
    BooleanSetting field2655 = new BooleanSetting(field2679, false, () -> (Integer)this.field2654.getValue() != -1);
    SliderSetting field2656 = new SliderSetting(field2680, 1.0f, 1.0f, field2681, 1.0f, () -> (Boolean)this.field2655.getValue());
    static String field2657;
    static String field2658;
    static String field2659;
    static String field2660;
    static int field2661;
    static String field2662;
    static String field2663;
    static String field2664;
    static int field2665;
    int field2666 = 0;
    boolean field2667 = false;
    ClientState field2668;
    static long field2669;
    SliderSetting field2670 = new SliderSetting(field2682, 1.0f, 0.0f, field2683, 1.0f);
    boolean field2671 = false;
    boolean field2672 = false;
    BooleanSetting cfr_renamed_379 = new BooleanSetting(field2684, false, () -> (Integer)this.field2653.getValue() != -1);
    BooleanSetting field2673 = new BooleanSetting(field2685, false);
    BooleanSetting field2674 = new BooleanSetting(field2686, false);
    static String field2675;
    static String field2676;
    static String field2677;
    static String field2678;
    static String field2679;
    static String field2680;
    static float field2681;
    static String field2682;
    static float field2683;
    static String field2684;
    static String field2685;
    static String field2686;
    static long field2687;
    static String field2688;

    @EventHandler(2)
    public void method710(EventKey param1) {
        boolean v3;
        boolean v2 = InventoryUtil.method3336(Items.ELYTRA) != -1;
        boolean bl = v3 = InventoryUtil.method3315() != -1;
        if (param1.method3595() == ((Integer)this.field2653.getValue()).intValue() && param1.method3599() && !param1.method3594() && (v2 || v3) && this.l == 0) {
            this.cfr_renamed_378 = true;
        }
        if (param1.method3595() == ((Integer)this.field2654.getValue()).intValue() && param1.method3599() && !param1.method3594() && ElytraHelper.I.player.isGliding()) {
            if (((Boolean)this.field2655.getValue()).booleanValue()) {
                int v4 = InventoryUtil.method3336(Items.FIREWORK_ROCKET);
                if (v4 >= 0 && v4 < 9) {
                    InventoryUtil.method3325(Items.FIREWORK_ROCKET);
                } else if (v4 >= 9) {
                    int v5 = Math.max(1, Math.min(9, ((Float)this.field2656.getValue()).intValue())) - 1;
                    int v6 = v4 < 9 ? v4 + 36 : v4;
                    ElytraHelper.I.interactionManager.clickSlot(ElytraHelper.I.player.currentScreenHandler.syncId, v6, v5, SlotActionType.SWAP, (PlayerEntity)ElytraHelper.I.player);
                    InventoryUtil.method3325(Items.FIREWORK_ROCKET);
                } else {
                    ChatUtil.method3308(field2657, new Object[0]);
                }
            } else {
                InventoryUtil.method3325(Items.FIREWORK_ROCKET);
            }
        }
    }

    private void OO() {
        boolean v2 = ElytraHelper.I.player.getEquippedStack(EquipmentSlot.CHEST).getItem() == Items.ELYTRA;
        int v1 = v2 ? InventoryUtil.method3315() : InventoryUtil.method3336(Items.ELYTRA);
        if (v1 == -1) {
            return;
        }
        InventoryUtil.method3317(v1, 6);
        if (v2) {
            ElytraHelper.I.player.getInventory().getStack(v1).getName().getString();
            ChatUtil.method3308(field2658, new Object[0]);
            NotificationManager.method2456(field2659, field2660, field2661);
        } else {
            ChatUtil.method3308(field2662, new Object[0]);
            NotificationManager.method2456(field2663, field2664, field2665);
        }
    }

    @EventHandler(2)
    public void method711(EventUpdate param1) {
        GuiMove v2;
        if (this.field2666 > 0) {
            --this.field2666;
        }
        if (this.l > 0) {
            --this.l;
        }
        if (this.field2667 && this.field2668.method3113(field2669) && this.l == 0) {
            I.setScreen((Screen)new InventoryScreen((PlayerEntity)ElytraHelper.I.player));
            this.OO();
            ElytraHelper.I.player.networkHandler.sendPacket((Packet)new CloseHandledScreenC2SPacket(ElytraHelper.I.player.currentScreenHandler.syncId));
            I.setScreen(null);
            this.field2667 = false;
            this.cfr_renamed_378 = false;
            this.l = ((Float)this.field2670.getValue()).intValue();
        }
        if (this.field2671 && this.field2666 == 0 && this.l == 0) {
            v2 = Minced.getInstance().getModuleManager().method182();
            if (v2 != null && v2.method549()) {
                v2.setInventoryActionInProgress(true);
            }
            this.OO();
            ElytraHelper.I.player.networkHandler.sendPacket((Packet)new CloseHandledScreenC2SPacket(ElytraHelper.I.player.currentScreenHandler.syncId));
            if (v2 != null && v2.method549()) {
                v2.setInventoryActionInProgress(false);
            }
            this.field2671 = false;
            this.cfr_renamed_378 = false;
            this.l = ((Float)this.field2670.getValue()).intValue();
        }
        if (this.field2667 || this.field2671) {
            this.field2672 = ElytraHelper.I.player.isOnGround();
            return;
        }
        if (this.cfr_renamed_378 && this.l == 0) {
            if (this.method718()) {
                this.field2668.method3114();
                this.field2667 = true;
                this.field2666 = 2;
            } else if (this.cfr_renamed_365() || this.method712()) {
                this.field2666 = 2;
                this.field2671 = true;
            } else {
                if (this.method714()) {
                    v2 = Minced.getInstance().getModuleManager().method182();
                    v2.method548();
                }
                this.OO();
                ElytraHelper.I.player.networkHandler.sendPacket((Packet)new CloseHandledScreenC2SPacket(ElytraHelper.I.player.currentScreenHandler.syncId));
                this.cfr_renamed_378 = false;
                this.l = ((Float)this.field2670.getValue()).intValue();
            }
        }
        if (((Boolean)this.cfr_renamed_379.getValue()).booleanValue() && !this.field2672 && ElytraHelper.I.player.isOnGround() && ElytraHelper.I.player.getEquippedStack(EquipmentSlot.CHEST).getItem() == Items.ELYTRA && InventoryUtil.method3315() != -1 && ElytraHelper.I.player.isGliding() && this.l == 0) {
            this.cfr_renamed_378 = true;
        }
        if (((Boolean)this.field2673.getValue()).booleanValue() && ElytraHelper.I.player.getEquippedStack(EquipmentSlot.CHEST).getItem() == Items.ELYTRA && !ElytraHelper.I.player.isGliding() && !ElytraHelper.I.player.isTouchingWater() && !ElytraHelper.I.player.hasStatusEffect(StatusEffects.LEVITATION)) {
            if (ElytraHelper.I.player.isOnGround() && !ElytraHelper.I.options.jumpKey.isPressed()) {
                ElytraHelper.I.player.jump();
            } else if (this.method716(ElytraHelper.I.player.getEquippedStack(EquipmentSlot.CHEST)) && !ElytraHelper.I.player.getAbilities().flying) {
                ElytraHelper.I.player.startGliding();
                ElytraHelper.I.player.networkHandler.sendPacket((Packet)new ClientCommandC2SPacket((Entity)ElytraHelper.I.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
                if (((Boolean)this.field2674.getValue()).booleanValue()) {
                    InventoryUtil.method3325(Items.FIREWORK_ROCKET);
                }
            }
        }
        this.field2672 = ElytraHelper.I.player.isOnGround();
    }

    private boolean method712() {
        GuiMove v1 = Minced.getInstance().getModuleManager().method182();
        return v1 != null && v1.method549();
    }

    public ElytraHelper() {
        super(field2675, field2676, Combat.Miscellaneous);
        this.field2668 = new ClientState();
        this.addSettings(this.field2653, this.field2654, this.field2655, this.field2656, this.field2670, this.cfr_renamed_379, this.field2673, this.field2674);
    }

    private boolean cfr_renamed_365() {
        GuiMove v1 = Minced.getInstance().getModuleManager().method182();
        return v1 != null && v1.method561();
    }

    private boolean method714() {
        GuiMove v1 = Minced.getInstance().getModuleManager().method182();
        return v1 != null && v1.method556();
    }

    @EventHandler(2)
    private void method715(EventInput param1) {
        if (this.field2666 > 0) {
            param1.method1973(0.0f);
            param1.method1972(0.0f);
            param1.method1970(false);
            param1.method1974(false);
        }
        if (this.field2667 && !this.field2668.method3113(field2687)) {
            param1.method1973(0.0f);
            param1.method1972(0.0f);
            param1.method1970(false);
            param1.method1974(false);
        }
    }

    private boolean method716(ItemStack param1) {
        return param1.getDamage() < param1.getMaxDamage() - 1;
    }

    private boolean method718() {
        GuiMove v1 = Minced.getInstance().getModuleManager().method182();
        return v1 != null && v1.isEnabled() && v1.isBypassMode(field2688);
    }

    static {
        field2675 = "ElytraHelper";
        field2676 = "Автоматически свапает элитру";
        field2677 = "Кнопка свапа";
        field2678 = "Кнопка фейера";
        field2679 = "Фейеверки только с хотбара";
        field2680 = "Слот хотбара (1-9)";
        field2681 = 9.0f;
        field2682 = "Задержка свапа";
        field2683 = 9.0f;
        field2684 = "Снимать элитры при приземлении";
        field2685 = "Автоматически взлетать";
        field2686 = "Фейеверк при взлете";
        field2688 = "Funtime";
        field2669 = 100L;
        cfr_renamed_380 = "§cНе удалось переместить фейерверк в хотбар: \u0001";
        field2657 = "§cФейверки не найдены в инвентаре";
        field2687 = 190L;
        field2658 = "§aСвапнул на нагрудник";
        field2659 = "J";
        field2660 = "Свапнул на нагрудник";
        field2661 = -10158236;
        field2662 = "§aСвапнул на элитру";
        field2663 = "J";
        field2664 = "Свапнул на элитру";
        field2665 = -10172161;
    }
}




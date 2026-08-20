/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.item.Items
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket
 *  net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket$Action
 *  net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket
 *  net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket
 *  net.minecraft.util.Hand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 */
package minced.module.movement;
import minced.core.EventHandler;

import minced.core.ModeSetting;
import minced.core.EventUpdate;
import minced.core.EventNoSlow;
import minced.core.Module;
import minced.enums.Combat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class NoSlow
extends Module {
    static String field1237 = "No Slow";
    static String field1238 = "Убирает замедление при использовании предмета";
    static String field1239 = "Мод";
    static String field1240 = "Grim Tick";
    static String field1241 = "Обычный";
    static String field1242 = "Grim";
    static String field1243 = "Grim2";
    static String field1244 = "Grim Tick";
    static String field1245 = "Grim Latest";
    static String field1246 = "Lony/Holy";
    ModeSetting field1247 = new ModeSetting(field1239, field1240, field1241, field1242, field1243, field1244, field1245, field1246);
    static MinecraftClient I = MinecraftClient.getInstance();
    static String field1248;
    static String field1249;
    static String field1250;
    static String field1251;
    static String field1252;
    static String field1253;
    static String field1254;

    public NoSlow() {
        super(field1237, field1238, Combat.Movement);
        this.addSettings(this.field1247);
    }

    @EventHandler(2)
    public void method428(EventUpdate param1) {
        if (NoSlow.I.player == null || NoSlow.I.player.isGliding()) {
            return;
        }
        if (this.field1247.isMode(field1248)) {
            this.method432();
        }
    }

    private void method429(EventNoSlow param1) {
        if (NoSlow.I.player.age % 2 == 0 && !NoSlow.I.player.isSneaking()) {
            param1.method2298(true);
        }
    }

    private void O(EventNoSlow param1) {
        if (NoSlow.I.player.getItemUseTime() % 2 == 0) {
            param1.method2298(true);
        }
    }

    private void method430(EventNoSlow param1) {
        if (NoSlow.I.player.getActiveHand() == Hand.OFF_HAND) {
            NoSlow.I.player.networkHandler.sendPacket((Packet)new UpdateSelectedSlotC2SPacket(NoSlow.I.player.getInventory().selectedSlot % 8 + 1));
            NoSlow.I.player.networkHandler.sendPacket((Packet)new UpdateSelectedSlotC2SPacket(NoSlow.I.player.getInventory().selectedSlot));
        } else if (NoSlow.I.player.getItemUseTime() <= 3 || NoSlow.I.player.age % 2 == 0) {
            NoSlow.I.player.networkHandler.sendPacket((Packet)new PlayerInteractItemC2SPacket(Hand.OFF_HAND, 0, NoSlow.I.player.getYaw(), NoSlow.I.player.getPitch()));
        }
        param1.method2298(true);
    }

    private void method431(EventNoSlow param1) {
        if (NoSlow.I.player.getActiveHand() == Hand.OFF_HAND) {
            this.method429(param1);
            return;
        }
        if (NoSlow.I.player.getItemUseTime() > 0) {
            param1.method2298(true);
        }
    }

    private void method432() {
        if (NoSlow.I.player.networkHandler == null) {
            return;
        }
        if (NoSlow.I.player.isUsingItem() && NoSlow.I.player.getItemUseTime() == 0) {
            NoSlow.I.player.networkHandler.sendPacket((Packet)new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.DROP_ALL_ITEMS, (BlockPos)BlockPos.ZERO, NoSlow.I.player.getHorizontalFacing()));
        }
    }

    private void method433(EventNoSlow param1) {
        if (NoSlow.I.player.getActiveHand() == Hand.OFF_HAND) {
            NoSlow.I.player.networkHandler.sendPacket((Packet)new UpdateSelectedSlotC2SPacket(NoSlow.I.player.getInventory().selectedSlot % 8 + 1));
            NoSlow.I.player.networkHandler.sendPacket((Packet)new UpdateSelectedSlotC2SPacket(NoSlow.I.player.getInventory().selectedSlot % 7 + 2));
            NoSlow.I.player.networkHandler.sendPacket((Packet)new UpdateSelectedSlotC2SPacket(NoSlow.I.player.getInventory().selectedSlot));
        } else if (NoSlow.I.player.getItemUseTime() <= 3 || NoSlow.I.player.age % 2 == 0) {
            NoSlow.I.player.networkHandler.sendPacket((Packet)new PlayerInteractItemC2SPacket(Hand.OFF_HAND, 0, NoSlow.I.player.getYaw(), NoSlow.I.player.getPitch()));
        }
        param1.method2298(true);
    }

    @EventHandler(2)
    public void method434(EventNoSlow param1) {
        if (NoSlow.I.player == null || NoSlow.I.player.isGliding()) {
            return;
        }
        if (NoSlow.I.player.isUsingItem()) {
            String v2 = (String)this.field1247.getValue();
            int v3 = -1;
            switch (v2.hashCode()) {
                case -811990066: {
                    if (!v2.equals(field1249)) break;
                    v3 = 0;
                    break;
                }
                case 828965756: {
                    if (!v2.equals(field1250)) break;
                    v3 = 1;
                    break;
                }
                case 2228079: {
                    if (!v2.equals(field1251)) break;
                    v3 = 2;
                    break;
                }
                case 69070499: {
                    if (!v2.equals(field1252)) break;
                    v3 = 3;
                    break;
                }
                case 1125677432: {
                    if (!v2.equals(field1253)) break;
                    v3 = 4;
                    break;
                }
                case -1693189195: {
                    if (!v2.equals(field1254)) break;
                    v3 = 5;
                }
            }
            switch (v3) {
                case 0: {
                    this.O(param1);
                    break;
                }
                case 1: {
                    param1.method2298(true);
                    break;
                }
                case 2: {
                    this.method430(param1);
                    break;
                }
                case 3: {
                    this.method433(param1);
                    break;
                }
                case 4: {
                    this.method435(param1);
                    break;
                }
                case 5: {
                    this.method431(param1);
                }
            }
        }
    }

    private void method435(EventNoSlow param1) {
        if (NoSlow.I.player.getActiveHand() != Hand.MAIN_HAND) {
            return;
        }
        if (NoSlow.I.player.getStackInHand(NoSlow.I.player.getActiveHand()).getItem() == Items.GOLDEN_APPLE || NoSlow.I.player.getStackInHand(NoSlow.I.player.getActiveHand()).getItem() == Items.ENCHANTED_GOLDEN_APPLE) {
            return;
        }
        if (NoSlow.I.player.getItemUseTime() + 2 > NoSlow.I.player.getItemUseTime()) {
            NoSlow.I.player.networkHandler.sendPacket((Packet)new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.DROP_ALL_ITEMS, new BlockPos((int)NoSlow.I.player.getX(), (int)NoSlow.I.player.getY(), (int)NoSlow.I.player.getZ()), Direction.getFacing((float)((float)NoSlow.I.player.getX()), (float)((float)NoSlow.I.player.getY()), (float)((float)NoSlow.I.player.getZ()))));
        }
        param1.method2298(true);
    }

    static {
        field1249 = "Grim Tick";
        field1250 = "Обычный";
        field1251 = "Grim";
        field1252 = "Grim2";
        field1253 = "Grim Latest";
        field1254 = "Lony/Holy";
        field1248 = "LonyGrief";
    }
}




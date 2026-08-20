/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.screen.ChatScreen
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.ingame.HandledScreen
 *  net.minecraft.client.gui.screen.ingame.HangingSignEditScreen
 *  net.minecraft.client.gui.screen.ingame.InventoryScreen
 *  net.minecraft.client.gui.screen.ingame.SignEditScreen
 *  net.minecraft.client.option.KeyBinding
 *  net.minecraft.client.util.InputUtil
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket
 *  net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket
 *  net.minecraft.network.packet.s2c.play.CloseScreenS2CPacket
 *  net.minecraft.screen.slot.SlotActionType
 */
package minced.module.movement;
import minced.core.EventHandler;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import minced.core.EventInput;
import minced.core.ModeSetting;
import minced.core.EventClickSlot;
import minced.core.EventUpdate;
import minced.core.EventPacket;
import minced.core.EventInventoryClose;
import minced.core.ServerDetector;
import minced.core.ClientState;
import minced.core.Module;
import minced.enums.Combat;
import minced.manager.ClickGui;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.HangingSignEditScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.s2c.play.CloseScreenS2CPacket;
import net.minecraft.screen.slot.SlotActionType;

public class GuiMove
extends Module {
    static String field1743 = "GuiMove";
    static String field1744 = "Позволяет вам двигаться с открытым инвентарем и перемещать предметы при беге";
    static String field1745 = "Обход";
    static String field1746 = "Vanilla";
    static String field1747 = "Vanilla";
    static String field1748 = "Reallyworld";
    static String field1749 = "Funtime";
    static String field1750 = "Holyworld";
    static String field1751 = "SpookyTime";
    static String field1752 = "LonyGrief";
    ModeSetting field1753 = new ModeSetting(field1745, field1746, field1747, field1748, field1749, field1750, field1751, field1752);
    List<net.minecraft.network.packet.Packet<?>> field1754 = new ArrayList<>();
    int field1755 = 0;
    boolean field1756 = false;
    ClientState field1757 = new ClientState();
    int field1758 = 0;
    boolean field1759 = false;
    int field1760 = 0;
    boolean field1761 = false;
    boolean field1762 = false;
    int field1763 = 0;
    boolean field1764 = false;

    public boolean isBypassMode(String mode) {
        return this.field1753.isMode(mode);
    }

    public void setInventoryActionInProgress(boolean inventoryActionInProgress) {
        this.field1764 = inventoryActionInProgress;
    }
    Queue field1765 = new LinkedList();
    static MinecraftClient I = MinecraftClient.getInstance();
    static String field1766;
    static String field1767;
    static String field1768;
    static long field1769;
    static String field1770;
    static long field1771;
    static String field1772;
    static String field1773;
    static long field1774;
    static String field1775;
    static String field1776;
    static String field1777;
    static String field1778;
    static long field1779;
    static String field1780;
    static String field1781;
    static String field1782;
    static String field1783;
    static long field1784;
    static String field1785;
    static long field1786;
    static String field1787;
    long field1788;
    static long field1789;
    static String field1790;
    static long field1791;
    static String field1792;
    static String field1793;
    static String field1794;
    static String field1795;
    static String field1796;
    static String field1797;
    static String field1798;
    static String field1799;
    static String field1800;
    static String field1801;
    static String field1802;
    static String field1803;
    static String field1804;
    static String field1805;
    static String field1806;
    static String field1807;
    static String field1808;
    static String field1809;

    public GuiMove() {
        super(field1743, field1744, Combat.Movement);
        this.addSettings(this.field1753);
    }

    public boolean method545() {
        return !this.isEnabled() || this.field1763 > 0;
    }

    private boolean method546() {
        if (!(GuiMove.I.currentScreen instanceof HandledScreen)) {
            return false;
        }
        if (!this.method560()) {
            return false;
        }
        return !GuiMove.I.player.currentScreenHandler.getCursorStack().isEmpty();
    }

    @EventHandler(2)
    public void method547(EventUpdate param1) {
        Object v3;
        if (this.field1755 > 0) {
            --this.field1755;
        }
        if (this.field1758 > 0) {
            --this.field1758;
        }
        if (this.field1753.isMode(field1766)) {
            if (this.field1762 && this.field1761 && this.field1760 > 0) {
                this.method555();
            }
            this.field1762 = this.field1761;
            if (this.method546()) {
                this.method551(null);
            } else if (this.field1760 > 0) {
                --this.field1760;
            }
            this.field1761 = this.field1760 > 0;
            ++this.field1763;
            if (this.field1761) {
                this.field1763 = 0;
            }
        }
        KeyBinding[] v2 = new KeyBinding[]{GuiMove.I.options.forwardKey, GuiMove.I.options.backKey, GuiMove.I.options.leftKey, GuiMove.I.options.rightKey, GuiMove.I.options.jumpKey};
        if (this.field1753.isMode(field1767) && this.field1755 > 0) {
            return;
        }
        if (this.field1753.isMode(field1768) && !this.field1757.method3113(field1769)) {
            return;
        }
        if (this.field1753.isMode(field1770) && this.field1758 > 0 && !this.field1757.method3113(field1771)) {
            return;
        }
        if (this.field1753.isMode(field1772) && this.field1761) {
            return;
        }
        if (this.field1753.isMode(field1773) && this.field1756 && !this.field1757.method3113(field1774)) {
            return;
        }
        if (GuiMove.I.currentScreen instanceof ChatScreen || GuiMove.I.currentScreen instanceof SignEditScreen || GuiMove.I.currentScreen instanceof HangingSignEditScreen) {
            return;
        }
        Screen v42 = GuiMove.I.currentScreen;
        if (v42 instanceof ClickGui && ((ClickGui)((Object)(v3 = (ClickGui)v42))).isTextInputFocused()) {
            return;
        }
        for (KeyBinding v6 : v2) {
            boolean v7 = InputUtil.isKeyPressed((long)I.getWindow().getHandle(), (int)v6.getDefaultKey().getCode());
            v6.setPressed(v7);
        }
        if (this.field1756 && this.field1753.isMode(field1775)) {
            for (net.minecraft.network.packet.Packet<?> v42_p : this.field1754) {
                GuiMove.I.player.networkHandler.sendPacket(v42_p);
            }
            GuiMove.I.player.networkHandler.sendPacket((Packet)new CloseHandledScreenC2SPacket(GuiMove.I.player.currentScreenHandler.syncId));
            this.field1754.clear();
            this.field1756 = false;
        }
        if (this.field1756 && this.field1753.isMode(field1776)) {
            I.setScreen((Screen)new InventoryScreen((PlayerEntity)GuiMove.I.player));
            for (net.minecraft.network.packet.Packet<?> v42_p : this.field1754) {
                GuiMove.I.player.networkHandler.sendPacket(v42_p);
            }
            this.field1754.clear();
            this.field1756 = false;
            GuiMove.I.player.networkHandler.sendPacket((Packet)new CloseHandledScreenC2SPacket(GuiMove.I.player.currentScreenHandler.syncId));
            I.setScreen(null);
        }
        if (this.field1759 && this.field1753.isMode(field1777) && this.field1758 == 0) {
            for (net.minecraft.network.packet.Packet<?> v42_p : this.field1754) {
                GuiMove.I.player.networkHandler.sendPacket(v42_p);
            }
            GuiMove.I.player.networkHandler.sendPacket((Packet)new CloseHandledScreenC2SPacket(GuiMove.I.player.currentScreenHandler.syncId));
            this.field1754.clear();
            this.field1759 = false;
            this.field1756 = false;
        }
        if (this.field1756 && this.field1753.isMode(field1778) && this.field1757.method3113(field1779)) {
            for (net.minecraft.network.packet.Packet<?> v42_p : this.field1754) {
                GuiMove.I.player.networkHandler.sendPacket(v42_p);
            }
            GuiMove.I.player.networkHandler.sendPacket((Packet)new CloseHandledScreenC2SPacket(GuiMove.I.player.currentScreenHandler.syncId));
            this.field1754.clear();
            this.field1756 = false;
            if (GuiMove.I.currentScreen != null) {
                I.setScreen(null);
            }
        }
    }

    @Override
    public void onDisable() {
        this.method555();
        this.field1763 = 1;
        this.field1754.clear();
        this.field1755 = 0;
        this.field1758 = 0;
        this.field1760 = 0;
        this.field1761 = false;
        this.field1756 = false;
        this.field1759 = false;
        this.field1764 = false;
        this.field1765.clear();
        super.onDisable();
    }

    public void method548() {
        if (this.isEnabled() && this.field1753.isMode(field1780)) {
            this.field1760 = 8;
        }
    }

    public boolean method549() {
        return this.isEnabled() && this.field1753.isMode(field1781);
    }

    @EventHandler(2)
    public void method550(EventInput param1) {
        if (this.field1753.isMode(field1782) && this.field1755 > 0) {
            param1.method1973(0.0f);
            param1.method1972(0.0f);
            param1.method1974(false);
            param1.method1970(false);
        }
        if (this.field1753.isMode(field1783) && !this.field1757.method3113(field1784)) {
            param1.method1973(0.0f);
            param1.method1972(0.0f);
            param1.method1974(false);
            param1.method1970(false);
        }
        if (this.field1753.isMode(field1785) && this.field1758 > 0 && !this.field1757.method3113(field1786)) {
            param1.method1973(0.0f);
            param1.method1972(0.0f);
            param1.method1974(false);
            param1.method1970(false);
        }
        if (this.field1753.isMode(field1787)) {
            if (System.currentTimeMillis() - this.field1788 <= field1789) {
                param1.method1973(0.0f);
                param1.method1972(0.0f);
                param1.method1974(false);
                param1.method1970(false);
            } else if (this.field1761) {
                param1.method1973(0.0f);
                param1.method1972(0.0f);
                param1.method1974(false);
                param1.method1970(false);
            }
        }
        if (this.field1753.isMode(field1790) && this.field1756 && !this.field1757.method3113(field1791)) {
            param1.method1973(0.0f);
            param1.method1972(0.0f);
            param1.method1974(false);
            param1.method1970(false);
        }
    }

    private void method551(SlotActionType param1) {
        if (this.isEnabled() && this.field1753.isMode(field1792)) {
            this.field1760 = this.method552(param1 == null ? SlotActionType.PICKUP : param1) + 1;
        }
    }

    private int method552(SlotActionType param1) {
        if (this.field1753.isMode(field1793)) {
            return param1.equals((Object)SlotActionType.PICKUP) ? 2 : (ServerDetector.method2684(field1794) ? 6 : 3);
        }
        return param1.equals((Object)SlotActionType.PICKUP) ? 1 : (this.field1760 > 1 ? 2 : 3);
    }

    @EventHandler(2)
    public void method553(EventPacket param1) {
        if (this.field1753.isMode(field1795)) {
            Packet v3;
            if (!this.field1764 && !param1.method2466() && (v3 = param1.method2467()) instanceof ClickSlotC2SPacket) {
                ClickSlotC2SPacket v2 = (ClickSlotC2SPacket)v3;
                if (GuiMove.I.currentScreen instanceof InventoryScreen) {
                    if (v2.getSlot() == -999 || v2.getActionType() == SlotActionType.THROW) {
                        param1.method2298(true);
                        return;
                    }
                    this.field1754.add(v2);
                    param1.method2298(true);
                }
            }
            return;
        }
        if (!this.field1753.isMode(field1796)) {
            return;
        }
        if (param1.method2466()) {
            if (param1.method2467() instanceof CloseScreenS2CPacket) {
                param1.method2298(true);
            }
        } else {
            Packet v3 = param1.method2467();
            if (v3 instanceof ClickSlotC2SPacket) {
                ClickSlotC2SPacket v2 = (ClickSlotC2SPacket)v3;
                this.field1788 = System.currentTimeMillis();
                if (!this.field1764 && this.method560() && v2.getSlot() != -1) {
                    this.method551(v2.getActionType());
                    if (!this.field1761 && this.method558(v2)) {
                        this.field1763 = 0;
                        param1.method2298(true);
                    }
                }
            }
        }
    }

    private boolean method554() {
        return this.field1753.isMode(field1797) || this.field1753.isMode(field1798) || this.field1753.isMode(field1799) || this.field1753.isMode(field1800) || this.field1753.isMode(field1801);
    }

    private void method555() {
        if (this.field1765.isEmpty()) {
            return;
        }
        while (!this.field1765.isEmpty()) {
            ClickSlotC2SPacket v1 = (ClickSlotC2SPacket)this.field1765.poll();
            if (v1 == null) continue;
            GuiMove.I.player.networkHandler.sendPacket((Packet)v1);
        }
        this.field1763 = 0;
    }

    public boolean method556() {
        return this.isEnabled() && this.field1753.isMode(field1802);
    }

    @EventHandler(2)
    public void method557(EventInventoryClose param1) {
        if (!this.method554()) {
            return;
        }
        if (this.field1753.isMode(field1803)) {
            return;
        }
        if (this.field1753.isMode(field1804)) {
            if (GuiMove.I.currentScreen instanceof InventoryScreen) {
                this.field1756 = true;
                this.field1757.method3114();
                param1.method2298(true);
            }
            return;
        }
        if (GuiMove.I.currentScreen instanceof InventoryScreen) {
            if (!this.field1754.isEmpty()) {
                this.field1756 = true;
                if (this.field1753.isMode(field1805)) {
                    this.field1757.method3114();
                } else if (this.field1753.isMode(field1806)) {
                    this.field1758 = 2;
                    this.field1759 = true;
                } else {
                    this.field1755 = 3;
                }
            }
            param1.method2298(true);
        }
    }

    private boolean method558(ClickSlotC2SPacket param1) {
        return !this.field1765.contains(param1) && this.field1765.add(param1);
    }

    @EventHandler(2)
    public void method559(EventClickSlot param1) {
        if (!this.method554()) {
            return;
        }
        if (this.field1753.isMode(field1807)) {
            return;
        }
        if (this.field1753.isMode(field1808)) {
            return;
        }
        if (this.field1764) {
            return;
        }
        if (GuiMove.I.currentScreen instanceof InventoryScreen) {
            this.field1754.add(new ClickSlotC2SPacket(param1.method2315(), param1.method2319(), param1.method2320(), param1.method2317(), param1.method2313(), param1.method2323(), param1.method2314()));
            param1.method2298(true);
        }
    }

    private boolean method560() {
        return GuiMove.I.player.input.movementForward != 0.0f || GuiMove.I.player.input.movementSideways != 0.0f || this.field1761;
    }

    @Override
    public void onEnable() {
        this.method555();
        this.field1765.clear();
        this.field1763 = 1;
        super.onEnable();
    }

    public boolean method561() {
        return this.isEnabled() && this.field1753.isMode(field1809);
    }

    static {
        field1809 = "Holyworld";
        field1802 = "SpookyTime";
        field1781 = "LonyGrief";
        field1797 = "Reallyworld";
        field1798 = "Funtime";
        field1799 = "Holyworld";
        field1800 = "SpookyTime";
        field1801 = "LonyGrief";
        field1793 = "SpookyTime";
        field1794 = "holyworld";
        field1792 = "SpookyTime";
        field1780 = "SpookyTime";
        field1807 = "SpookyTime";
        field1808 = "LonyGrief";
        field1803 = "SpookyTime";
        field1804 = "LonyGrief";
        field1805 = "Funtime";
        field1806 = "Holyworld";
        field1795 = "LonyGrief";
        field1796 = "SpookyTime";
        field1782 = "Reallyworld";
        field1783 = "Funtime";
        field1784 = 190L;
        field1785 = "Holyworld";
        field1786 = 222L;
        field1787 = "SpookyTime";
        field1789 = 170L;
        field1790 = "LonyGrief";
        field1791 = 50L;
        field1766 = "SpookyTime";
        field1767 = "Reallyworld";
        field1768 = "Funtime";
        field1769 = 100L;
        field1770 = "Holyworld";
        field1771 = 125L;
        field1772 = "SpookyTime";
        field1773 = "LonyGrief";
        field1774 = 50L;
        field1775 = "Reallyworld";
        field1776 = "Funtime";
        field1777 = "Holyworld";
        field1778 = "LonyGrief";
        field1779 = 50L;
    }
}




package org.privates.client.managers.module.impl.movement;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.EditSignScreen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CClickWindowPacket;
import net.minecraft.network.play.server.SCloseWindowPacket;
import org.privates.client.api.annotations.Category2;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.component.impl.inventory.InvComponent;
import org.privates.client.managers.events.input.MovementInputKeysEvent;
import org.privates.client.managers.events.other.PacketEvent;
import org.privates.client.managers.events.other.ScreenCloseEvent;
import org.privates.client.managers.events.player.MovementManager;
import org.privates.client.managers.events.player.SetSprintEvent;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.impl.combat.AutoSwap;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.player.MoveUtil;
import org.privates.lib.util.time.StopWatch;

import java.util.*;

@Category2
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "GuiMove", category = Category.MOVEMENT, description = "Позволяет двигаться при открытом инвентаре")
public class ScreenWalk extends Module {
    public static ScreenWalk getInstance() {
        return Instance.get(ScreenWalk.class);
    }

    public final ModeSetting type = new ModeSetting(this, "Мод", "Обычный", "Funtime", "Обход", "Спуки Тайм");

    private final List<IPacket<?>> packetQueue = new ArrayList<>();
    private final StopWatch timer = new StopWatch();
    public int tick = 0;

    public boolean isMoving() {
        return mc.player != null && (mc.player.movementInput.moveForward != 0f || mc.player.movementInput.moveStrafe != 0f);
    }

    final KeyBinding[] moveKeys = {
            mc.gameSettings.keyBindForward, mc.gameSettings.keyBindBack,
            mc.gameSettings.keyBindLeft, mc.gameSettings.keyBindRight,
            mc.gameSettings.keyBindJump
    };

    @EventHandler
    public void onUpdate(UpdateEvent e) {
        if (mc.player == null) return;

        if (shouldSkipMovementControl()) {
            return;
        }

        if (type.is("Обычный")) {
            final KeyBinding[] pressedKeys = {mc.gameSettings.keyBindForward, mc.gameSettings.keyBindBack,
                    mc.gameSettings.keyBindLeft, mc.gameSettings.keyBindRight, mc.gameSettings.keyBindJump,
                    mc.gameSettings.keyBindSprint};

            if (tick > 0) {
                for (KeyBinding keyBinding : pressedKeys) {
                    keyBinding.setPressed(false);
                }
                tick--;
                return;
            }

            if (mc.currentScreen instanceof ChatScreen || mc.currentScreen instanceof EditSignScreen) {
                return;
            }

            updateKeyBindingState(pressedKeys);
        } else if (type.is("Обход")) {
            if (!timer.finished(200)) {
                for (KeyBinding keyBinding : moveKeys) {
                    keyBinding.setPressed(false);
                }
                MovementManager.getInstance().lockMovement("GuiMove");
                return;
            }

            if (mc.currentScreen instanceof ChatScreen || mc.currentScreen instanceof EditSignScreen) {
                MovementManager.getInstance().unlockMovement("GuiMove");
                return;
            }

            if (mc.currentScreen != null) {
                MovementManager.getInstance().unlockMovement("GuiMove");
                updateKeyBindingState(moveKeys);
            } else {
                MovementManager.getInstance().unlockMovement("GuiMove");
            }
        } else if (type.is("Funtime")) {
            if (mc.currentScreen == null || mc.currentScreen instanceof ChatScreen || mc.currentScreen instanceof EditSignScreen) {
                return;
            }

            Arrays.stream(moveKeys).forEach(keyBinding -> keyBinding.setPressed(InputMappings.isKeyDown(mw.getHandle(), keyBinding.getDefault().getKeyCode())));
        }
    }

    private boolean shouldSkipMovementControl() {
        AutoSwap autoSwap = AutoSwap.getInstance();
        if (autoSwap != null && autoSwap.isEnabled()) {
            if (autoSwap.isBlockingMovement()) {
                return true;
            }
            return autoSwap.isTripleGuiOpen();
        }
        return false;
    }

    @EventHandler
    public void onPacket(PacketEvent e) {
        if (type.is("Обычный")) {
            if (e.getPacket() instanceof CClickWindowPacket) {
                tick = 1;
            }
        } else if (type.is("Обход")) {
            if (e.getPacket() instanceof CClickWindowPacket p && mc.currentScreen instanceof InventoryScreen) {
                packetQueue.add(p);
                e.cancel();
            }
        } else if (type.is("Funtime")) {
            final IPacket<?> packet = e.getPacket();
            if (!(mc.currentScreen instanceof InventoryScreen)) return;
            if (packet instanceof CClickWindowPacket wrapper && MoveUtil.isMoving()) {
                packetQueue.add(wrapper);
                e.cancel();
            }

            if (packet instanceof SCloseWindowPacket) {
                e.cancel();
            }
        }
    }

    @EventHandler
    public void onClose(ScreenCloseEvent e) {
        if (type.is("Обычный")) {

        } else if (type.is("Обход")) {
            if (mc.currentScreen instanceof InventoryScreen) {
                if (!packetQueue.isEmpty()) {
                    e.cancel();
                    new Thread(() -> {
                        timer.reset();
                        for (KeyBinding key : moveKeys) {
                            key.setPressed(false);
                        }
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }
                        for (IPacket<?> p : packetQueue) {
                            mc.player.connection.sendPacketWithoutEvent(p);
                        }
                        packetQueue.clear();
                        mc.player.closeScreen();
                    }).start();
                } else {
                    e.cancel();
                    new Thread(() -> {
                        timer.reset();
                        for (KeyBinding key : moveKeys) {
                            key.setPressed(false);
                        }
                        MovementManager.getInstance().lockMovement("GuiMove");
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }
                        mc.player.closeScreen();
                        MovementManager.getInstance().unlockMovement("GuiMove");
                    }).start();
                }
            }
        } else if (type.is("Funtime")) {
            InvComponent.addTask(() -> {
                packetQueue.forEach(packet -> mc.player.connection.sendPacket(packet));
                packetQueue.clear();
            }, 80);
        }
    }

    private void updateKeyBindingState(KeyBinding[] keyBindings) {
        for (KeyBinding keyBinding : keyBindings) {
            boolean isKeyPressed = InputMappings.isKeyDown(mc.getMainWindow().getHandle(), keyBinding.getDefault().getKeyCode());
            keyBinding.setPressed(isKeyPressed);
        }
    }

    final boolean stop = true;

    private boolean canStoppingOnWindowClick() {
        return this.stop && (mc.player.movementInput.moveForward != 0 || mc.player.movementInput.moveStrafe != 0) || this.stoppedStatus;
    }

    private void setStop(ClickType clickType) {
        if (this.isEnabled())
            this.stopTicksOut = this.ticksWindowClickOffset(clickType == null ? ClickType.PICKUP : clickType) + (type.is("Спуки Тайм") ? 1 : 0);
    }

    public void setTempStop() {
        if (this.isEnabled()) this.stopTicksOut = 2;
    }

    public int ticksPostOnStop;

    public boolean canHandlePostWindowClickAction() {
        return !this.isEnabled() || this.ticksPostOnStop > 0;
    }

    private int stopTicksOut;
    private boolean stoppedStatus, previousStoppedStatus;

    private int ticksWindowClickOffset(ClickType clickType) {
        return clickType.equals(ClickType.PICKUP) ? 1 : this.stopTicksOut > 1 ? 2 : 3;
    }

    private final Queue<CClickWindowPacket> windowClickPacketQueue = new LinkedList<>();

    private void useAccumulatedPackets() {
        if (this.windowClickPacketQueue.isEmpty()) return;
        if (this.windowClickPacketQueue.removeIf(CClickWindowPacket::sendSilent)) {
            this.ticksPostOnStop = 0;
        }
    }

    private boolean rememberCClickWindowPacket(final CClickWindowPacket packetIn) {
        return !this.windowClickPacketQueue.contains(packetIn) && this.windowClickPacketQueue.add(packetIn);
    }

    private boolean getHasItemStackDragged() {
        return mc.currentScreen instanceof ContainerScreen screenContainer && this.canStoppingOnWindowClick() && screenContainer.getHasDraggingStack;
    }

    @EventHandler
    public void onKeyMove(final MovementInputKeysEvent event) {
        if (!type.is("Спуки Тайм")) return;
        if (!(mc.currentScreen instanceof ChatScreen)) event.spoofAsKeyboard(false);
        if (this.stoppedStatus) event.stopWASD();
    }

    @EventHandler
    public void onSprintAction(final SetSprintEvent event) {
        if (!type.is("Спуки Тайм")) return;
        if (this.stoppedStatus) event.setState(false);
    }

    @EventHandler
    public void onPlayerUpdate(final UpdateEvent event) {
        if (!type.is("Спуки Тайм")) return;
        if (this.previousStoppedStatus && this.stoppedStatus && this.stopTicksOut > 0) this.useAccumulatedPackets();
        this.previousStoppedStatus = this.stoppedStatus;
        if (this.getHasItemStackDragged()) this.setStop(null);
        else if (this.stopTicksOut > 0) --this.stopTicksOut;
        this.stoppedStatus = this.stopTicksOut > 0;
        ++this.ticksPostOnStop;
        if (this.stoppedStatus) this.ticksPostOnStop = 0;
    }

    @EventHandler
    public void onSendPacket(PacketEvent event) {
        if (!type.is("Спуки Тайм")) return;
        if (event.isReceive()) {
            if (event.getPacket() instanceof SCloseWindowPacket) {
                event.cancel();
            }
        } else {
            if (event.getPacket() instanceof CClickWindowPacket toSend) {
                if (this.canStoppingOnWindowClick() && toSend.getSlotId() != -1) {
                    this.setStop(toSend.getClickType());
                    if (!this.stoppedStatus && this.rememberCClickWindowPacket(toSend)) {
                        this.ticksPostOnStop = 0;
                        event.cancel();
                    }
                }
            }
        }
    }

    @Override
    public void onDisable() {
        this.useAccumulatedPackets();
        this.ticksPostOnStop = 1;
        MovementManager.getInstance().unlockMovement("GuiMove");
        packetQueue.clear();
        for (KeyBinding key : moveKeys) {
            key.setPressed(false);
        }
        super.onDisable();
    }

    @Override
    public void onEnable() {
        this.useAccumulatedPackets();
        this.windowClickPacketQueue.clear();
        this.ticksPostOnStop = 1;
        timer.reset();
    }
}
package antileak.base.client.modules.impl.movement;

import antileak.base.api.events.EventLink;
import antileak.base.api.events.implement.EventPacket;
import antileak.base.api.events.implement.EventUpdate;
import antileak.base.client.modules.Module;
import antileak.base.client.modules.settings.implement.BooleanSetting;
import antileak.base.client.modules.settings.implement.ModeSetting;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.util.PlayerInput;

import java.util.ArrayList;
import java.util.List;

public class GuiMove extends Module {

    public static final GuiMove INSTANCE = new GuiMove();

    private final ModeSetting mode = new ModeSetting("Режим", "SpookyTime", "SpookyTime");
    private final BooleanSetting syncSwap = new BooleanSetting("Синхронизация Свапов", true);

    private final List<Packet<?>> packets = new ArrayList<>();
    private Screen lastScreen;
    private boolean wasSprinting;
    private boolean processing;
    private boolean movedInGui;
    private int taskTick;
    private int taskStage;
    private int freezeTicks;

    public GuiMove() {
        super("GuiMove", "Движение в GUI", ModuleCategory.MOVEMENT);
        addSettings(mode, syncSwap);
    }

    @EventLink
    private void onPacket(EventPacket e) {
        if (e.getType() != EventPacket.Type.SEND || mc.player == null) return;

        if (mode.is("Default")) return;

        Screen screen = mc.currentScreen;
        if (screen == null || screen instanceof ChatScreen) return;

        boolean moving = movedInGui || hasPlayerMovement();
        movedInGui |= moving && !packets.isEmpty();

        Packet<?> packet = e.getPacket();
        if (packet instanceof ClickSlotC2SPacket && moving && screen instanceof InventoryScreen) {
            packets.add(packet);
            e.cancel();
        } else if (packet instanceof CloseHandledScreenC2SPacket) {
            if (moving && !processing) {
                if (packets.isEmpty()) {
                    e.cancel();
                } else {
                    packets.add(packet);
                    e.cancel();
                    startProcessing();
                }
            }
        }
    }

    @EventLink
    private void onTick(EventUpdate e) {
        if (mc.player == null || mc.world == null) {
            cleanup();
            return;
        }

        if (freezeTicks > 0) {
            freezeTicks--;
            releaseMovementKeys();
            lastScreen = mc.currentScreen;
            return;
        }

        if (processing) {
            processPacketTask();
            lastScreen = mc.currentScreen;
            return;
        }

        Screen screen = mc.currentScreen;
        if (screen instanceof InventoryScreen || screen instanceof CreativeInventoryScreen) {
            updateMovementKeys();
        }

        if (mode.is("Default")) {
            handleDefaultMode();
        }

        lastScreen = screen;
    }

    private void handleDefaultMode() {
        if (mc.currentScreen == null || mc.currentScreen instanceof ChatScreen) {
            if (lastScreen instanceof InventoryScreen && !packets.isEmpty()) {
                startProcessing();
            }
            return;
        }

        if (!(mc.currentScreen instanceof InventoryScreen)) return;

        if (hasPlayerMovement()) {
            for (KeyBinding key : new KeyBinding[]{mc.options.forwardKey, mc.options.backKey, mc.options.leftKey, mc.options.rightKey, mc.options.jumpKey}) {
                key.setPressed(InputUtil.isKeyPressed(mc.getWindow().getHandle(), key.getDefaultKey().getCode()));
            }
        }
    }

    private void startProcessing() {
        if (packets.isEmpty()) return;
        processing = true;
        taskStage = 0;
        taskTick = 0;

        if (wasSprinting) {
            mc.player.setSprinting(false);
        }

        lockMovement();
    }

    private void processPacketTask() {
        taskTick++;

        if (mode.is("Bypass")) {
            processBypass();
        } else if (mode.is("Legit")) {
            processLegit();
        } else {
            processDefaultPackets();
        }
    }

    private void processBypass() {
        switch (taskStage) {
            case 0:
                if (taskTick >= 1) {
                    lockMovement();
                    taskStage = 1;
                    taskTick = 0;
                }
                break;
            case 1:
                if (taskTick >= 1) {
                    List<Packet<?>> copy = new ArrayList<>(packets);
                    packets.clear();
                    for (Packet<?> p : copy) {
                        sendSilentPacket(p);
                    }
                    processing = false;
                    movedInGui = false;
                    taskStage = 2;
                    taskTick = 0;
                }
                break;
            case 2:
                if (taskTick >= 1) {
                    for (Packet<?> p : packets) {
                        if (p instanceof CloseHandledScreenC2SPacket) sendSilentPacket(p);
                    }
                    unlockMovement();
                    processing = false;
                    movedInGui = false;
                    cleanup();
                }
                break;
        }
    }

    private void processLegit() {
        switch (taskStage) {
            case 0:
                if (taskTick >= 1) {
                    lockMovement();
                    taskStage = 1;
                    taskTick = 0;
                }
                break;
            case 1:
                if (taskTick >= 2) {
                    taskStage = 2;
                    taskTick = 0;
                }
                break;
            case 2:
                if (taskTick >= 2) {
                    taskStage = 3;
                    taskTick = 0;
                }
                break;
            case 3:
                if (taskTick >= 1) {
                    for (Packet<?> p : packets) {
                        if (!(p instanceof CloseHandledScreenC2SPacket)) sendSilentPacket(p);
                    }
                    taskStage = 4;
                    taskTick = 0;
                }
                break;
            case 4:
                if (taskTick >= 1) {
                    taskStage = 5;
                    taskTick = 0;
                }
                break;
            case 5:
                if (taskTick >= 1) {
                    for (Packet<?> p : packets) {
                        if (p instanceof CloseHandledScreenC2SPacket) sendSilentPacket(p);
                    }
                    packets.clear();
                    taskStage = 6;
                    taskTick = 0;
                }
                break;
            case 6:
                if (taskTick >= 1) {
                    taskStage = 7;
                    taskTick = 0;
                }
                break;
            case 7:
                if (taskTick >= 1) {
                    unlockMovement();
                    processing = false;
                    movedInGui = false;
                    cleanup();
                }
                break;
        }
    }

    private void processDefaultPackets() {
        if (taskTick >= 1) {
            List<Packet<?>> copy = new ArrayList<>(packets);
            packets.clear();
            for (Packet<?> p : copy) {
                sendSilentPacket(p);
            }
            sendSilentPacket(new CloseHandledScreenC2SPacket(0));
            if (wasSprinting) {
                mc.player.setSprinting(true);
            }
            wasSprinting = false;
            processing = false;
            unlockMovement();
            cleanup();
        }
    }

    public boolean isSyncSwapEnabled() {
        return isEnable() && syncSwap.isState();
    }

    public long getSyncSwapDelayMillis() {
        return 50L;
    }

    public float getSyncSwapStopSeconds() {
        return Math.max(0.06F, (float)(getSyncSwapDelayMillis() + 50L) / 1000.0F);
    }

    public void stopMovementTemporarily(float timeInSeconds) {
        int ticks = Math.max(1, (int)Math.ceil(timeInSeconds * 20.0F));
        freezeTicks = Math.max(freezeTicks, ticks);
    }

    private void lockMovement() {
        releaseMovementKeys();
        if (mc.player != null && mc.getNetworkHandler() != null) {
            mc.player.input.movementForward = 0;
            mc.player.input.movementSideways = 0;
            mc.getNetworkHandler().sendPacket(new PlayerInputC2SPacket(new PlayerInput(false, false, false, false, false, false, false)));
        }
    }

    private void unlockMovement() {
        if (mc.player != null) {
            mc.player.input.movementForward = 0;
            mc.player.input.movementSideways = 0;
        }
        restoreMovementKeys();
    }

    private void updateMovementKeys() {
        long handle = mc.getWindow().getHandle();
        for (KeyBinding key : new KeyBinding[]{mc.options.forwardKey, mc.options.backKey, mc.options.leftKey, mc.options.rightKey, mc.options.jumpKey}) {
            key.setPressed(InputUtil.isKeyPressed(handle, key.getDefaultKey().getCode()));
        }
    }

    private void sendSilentPacket(Packet<?> packet) {
        if (packet != null && mc.player != null && mc.getNetworkHandler() != null) {
            mc.getNetworkHandler().sendPacket(packet);
        }
    }

    private void releaseMovementKeys() {
        for (KeyBinding key : new KeyBinding[]{mc.options.forwardKey, mc.options.backKey, mc.options.leftKey, mc.options.rightKey, mc.options.jumpKey, mc.options.sprintKey}) {
            key.setPressed(false);
        }
    }

    private void restoreMovementKeys() {
        long handle = mc.getWindow().getHandle();
        for (KeyBinding key : new KeyBinding[]{mc.options.forwardKey, mc.options.backKey, mc.options.leftKey, mc.options.rightKey, mc.options.jumpKey, mc.options.sprintKey}) {
            key.setPressed(InputUtil.isKeyPressed(handle, key.getDefaultKey().getCode()));
        }
    }

    private boolean hasPlayerMovement() {
        if (mc.player == null) return false;
        return mc.player.input.movementForward != 0 || mc.player.input.movementSideways != 0;
    }

    private void cleanup() {
        packets.clear();
        wasSprinting = false;
        processing = false;
        movedInGui = false;
        taskTick = 0;
        taskStage = 0;
        freezeTicks = 0;
        lastScreen = null;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        cleanup();
    }
}

package org.privates.client.managers.module.impl.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.gui.screen.inventory.ChestScreen;
import net.minecraft.client.gui.screen.inventory.CraftingScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import org.privates.client.api.annotations.Category1;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.chat.ChatUtil;
import org.privates.client.utils.other.Instance;
import org.privates.lib.util.time.StopWatch;

@Category1
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "AntiAFK", category = Category.MISC, description = "Не дает блядскому фантайму и спукитайму выключить тебе автоселл & autobuy")
public class AntiAFK extends Module {

    public static AntiAFK getInstance() {
        return Instance.get(AntiAFK.class);
    }

    private final SliderSetting forwardTime = new SliderSetting(this, "Время вперед (мс)", 500, 200, 1000, 50);
    private final SliderSetting leftTime = new SliderSetting(this, "Время влево (мс)", 1000, 500, 2000, 50);
    private final SliderSetting jumpDelay = new SliderSetting(this, "Задержка прыжка (мс)", 300, 100, 800, 50);
    private final BooleanSetting autoBrewMode = new BooleanSetting(this, "Режим для AutoBrew", false);

    private final StopWatch timer = new StopWatch();
    private final StopWatch cycleTimer = new StopWatch();

    private enum MovementState {
        IDLE,
        MOVE_FORWARD,
        MOVE_LEFT,
        JUMP
    }

    private MovementState currentState = MovementState.IDLE;
    private boolean forwardPressed = false;
    private boolean leftPressed = false;
    private boolean jumpPressed = false;
    private boolean useForward = true;
    private boolean useLeft = true;
    private double startX, startY, startZ;
    private boolean hasStartPos = false;

    @Override
    public void onEnable() {
        super.onEnable();
        currentState = MovementState.IDLE;
        useForward = true;
        useLeft = true;
        hasStartPos = false;
        cycleTimer.reset();
        ChatUtil.addText("§cОБХОДИМ МЕГА АНТИЧИТ СПУКИ ЗАСРАЙМА НУ КАК АНТИЧИТ ЗАЛУПУ");
    }

    @Override
    public void onDisable() {
        mc.gameSettings.keyBindForward.setPressed(false);
        mc.gameSettings.keyBindBack.setPressed(false);
        mc.gameSettings.keyBindLeft.setPressed(false);
        mc.gameSettings.keyBindRight.setPressed(false);
        mc.gameSettings.keyBindJump.setPressed(false);
        ChatUtil.addText("§cОТХОД ЗАВЕРШЕН УБИВАЮ ДАЛБАЕБА ЧТОБЫ ВНОВЬ ЗАПОЛНИТЬ СБАК И ПОД ЗВУКИ РЕКВИЕМА Я ТОНЦУЮ НА КОСТЯХ ");
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null || mc.world == null) return;
        if (mc.currentScreen instanceof ChestScreen) return;
        if (mc.currentScreen instanceof InventoryScreen) return;

        if (mc.currentScreen instanceof CraftingScreen) return;

        if (autoBrewMode.getValue()) {
            AutoSellPotion autoSell = AutoSellPotion.getInstance();
            if (!autoSell.isEnabled() || autoSell.sellState() != AutoSellPotion.SellState.SELL_ITEM) {
                resetMovement();
                return;
            }

            if (!hasStartPos) {
                startX = mc.player.getPosX();
                startY = mc.player.getPosY();
                startZ = mc.player.getPosZ();
                hasStartPos = true;
            }

            handleBrewMovement();
        } else {
            handleNormalMovement();
        }
    }

    private void handleBrewMovement() {
        if (currentState == MovementState.IDLE) {
            if (cycleTimer.finished(5000)) {
                currentState = MovementState.MOVE_FORWARD;
                timer.reset();
            }
            return;
        }

        switch (currentState) {
            case MOVE_FORWARD -> {
                if (!timer.finished(500)) {
                    mc.gameSettings.keyBindForward.setPressed(true);
                } else {
                    mc.gameSettings.keyBindForward.setPressed(false);
                    currentState = MovementState.MOVE_LEFT;
                    timer.reset();
                }
            }
            case MOVE_LEFT -> {
                if (!timer.finished(500)) {
                    mc.gameSettings.keyBindForward.setPressed(true);
                } else {
                    mc.gameSettings.keyBindForward.setPressed(false);
                    returnToStart();
                    currentState = MovementState.IDLE;
                    cycleTimer.reset();
                }
            }
        }
    }

    private void returnToStart() {
        if (!hasStartPos) return;

        double dx = startX - mc.player.getPosX();
        double dz = startZ - mc.player.getPosZ();
        double distance = Math.sqrt(dx * dx + dz * dz);

        if (distance > 0.5) {
            float targetYaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90.0f;
            mc.player.rotationYaw = targetYaw;

            StopWatch returnTimer = new StopWatch();
            while (distance > 0.5 && returnTimer.finished(2000)) {
                mc.gameSettings.keyBindForward.setPressed(true);
                dx = startX - mc.player.getPosX();
                dz = startZ - mc.player.getPosZ();
                distance = Math.sqrt(dx * dx + dz * dz);
            }
            mc.gameSettings.keyBindForward.setPressed(false);
        }
    }

    private void handleNormalMovement() {
        if (currentState == MovementState.IDLE) {
            if (cycleTimer.finished(10000)) {
                currentState = MovementState.MOVE_FORWARD;
                timer.reset();
            }
            return;
        }

        switch (currentState) {
            case MOVE_FORWARD -> {
                if (!timer.finished(forwardTime.getValue().longValue())) {
                    if (useForward) {
                        mc.gameSettings.keyBindForward.setPressed(true);
                    } else {
                        mc.gameSettings.keyBindBack.setPressed(true);
                    }
                    forwardPressed = true;
                } else {
                    if (useForward) {
                        mc.gameSettings.keyBindForward.setPressed(false);
                    } else {
                        mc.gameSettings.keyBindBack.setPressed(false);
                    }
                    forwardPressed = false;
                    currentState = MovementState.MOVE_LEFT;
                    timer.reset();
                }
            }
            case MOVE_LEFT -> {
                if (!timer.finished(leftTime.getValue().longValue())) {
                    if (useLeft) {
                        mc.gameSettings.keyBindLeft.setPressed(true);
                    } else {
                        mc.gameSettings.keyBindRight.setPressed(true);
                    }
                    leftPressed = true;
                } else {
                    if (useLeft) {
                        mc.gameSettings.keyBindLeft.setPressed(false);
                    } else {
                        mc.gameSettings.keyBindRight.setPressed(false);
                    }
                    leftPressed = false;
                    currentState = MovementState.JUMP;
                    timer.reset();
                }
            }
            case JUMP -> {
                if (!timer.finished(jumpDelay.getValue().longValue())) {
                    mc.gameSettings.keyBindJump.setPressed(true);
                    jumpPressed = true;
                } else {
                    mc.gameSettings.keyBindJump.setPressed(false);
                    jumpPressed = false;
                    useForward = !useForward;
                    useLeft = !useLeft;
                    currentState = MovementState.IDLE;
                    cycleTimer.reset();
                }
            }
        }
    }

    private void resetMovement() {
        mc.gameSettings.keyBindForward.setPressed(false);
        mc.gameSettings.keyBindBack.setPressed(false);
        mc.gameSettings.keyBindLeft.setPressed(false);
        mc.gameSettings.keyBindRight.setPressed(false);
        mc.gameSettings.keyBindJump.setPressed(false);
        currentState = MovementState.IDLE;
        hasStartPos = false;
    }
}
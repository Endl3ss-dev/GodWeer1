package tech.lightclient.modules.impl.movement;

import tech.lightclient.client.utils.client.IMinecraft;
import tech.lightclient.client.utils.eventbus.EventHandler;
import tech.lightclient.client.utils.player.PlayerUtils;
import tech.lightclient.client.utils.player.TimerController;
import tech.lightclient.mixins.accessors.ClientPlayerEntityAccessor;
import tech.lightclient.modules.api.Category;
import tech.lightclient.modules.api.Function;
import tech.lightclient.modules.api.FunctionAdd;
import tech.lightclient.modules.api.settings.api.Setting;
import tech.lightclient.modules.api.settings.impl.BindSetting;
import tech.lightclient.modules.api.settings.impl.BooleanSetting;
import tech.lightclient.modules.api.settings.impl.ModeSetting;
import tech.lightclient.modules.api.settings.impl.SliderSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.s2c.common.CommonPingS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;
import tech.lightclient.client.events.impl.client.EventPacket;
import tech.lightclient.client.events.impl.client.UpdateEvent;
import tech.lightclient.client.events.impl.player.MoveInputEvent;
import tech.lightclient.client.events.impl.player.SprintEvent;


@FunctionAdd(name = "Timer", alias = "Timer", category = Category.Movement, description = "Speeds up client tick flow")
public class Timer extends Function implements IMinecraft {

    public final ModeSetting mode = new ModeSetting("Mode", "HolyWorld", "HolyWorld", "Matrix", "Shift", "Normal", "Test");
    public final BooleanSetting oldMatrix = new BooleanSetting("Old Matrix", false).setVisible(() -> mode.is("Matrix"));
    public final SliderSetting speed = new SliderSetting("Speed", 2.0f, 0.1f, 10.0f, 0.1f).setVisible(() -> !mode.is("Shift") && !mode.is("Test"));
    public final SliderSetting shiftTicks = new SliderSetting("Shift Ticks", 10f, 1f, 40f, 1f).setVisible(() -> mode.is("HolyWorld"));
    public final BindSetting boostKey = new BindSetting("HolyWorld Key", 0).setVisible(() -> mode.is("HolyWorld"));
    public final ModeSetting onFlag = new ModeSetting("On Flag", "Reset", "Reset", "Disable", "None").setVisible(() -> !mode.is("Test"));

    private float energy;
    private float tickTimer = 1.0f;
    private long cancelTime;
    private long lastSetbackTime;
    private double prevPosX;
    private double prevPosY;
    private double prevPosZ;
    private float prevYaw;
    private float prevPitch;

    private static final float TEST_CHARGE_TIMER = 0.05F;
    private static final float TEST_BOOST_TIMER = 1.7F;
    private static final long TEST_CHARGE_DURATION_NANOS = 1_250_000_000L;
    private static final long TEST_MAX_BOOST_DURATION_NANOS = 2_400_000_000L;
    private static final int TEST_FULL_BOOST_JUMPS = 4;

    private enum TestPhase {
        CHARGING,
        BOOSTING
    }

    private TestPhase testPhase = TestPhase.CHARGING;
    private boolean testCycleActive;
    private boolean testAirborne;
    private int testCompletedJumps;
    private int testGroundTicks;
    private long testPhaseStartedAt;

    public Timer() {
        addSettings(mode, oldMatrix, speed, shiftTicks, boostKey, onFlag);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        tickTimer = 1.0f;
        TimerController.reset();
        if (!mode.is("Matrix")) {
            energy = 0.0f;
        }
        if (mode.is("HolyWorld")) {
            cancelTime = System.currentTimeMillis();
        }
        if (mode.is("Test")) {
            stopTestCycle();
        }
    }

    @Override
    public void onDisable() {
        stopTestCycle();
        super.onDisable();
        tickTimer = 1.0f;
        TimerController.reset();
    }

    @EventHandler
    public void onTick(UpdateEvent event) {
        if (mc.player == null || mc.world == null) {
            tickTimer = 1.0f;
            TimerController.reset();
            return;
        }

        if (!mode.is("Test") && testCycleActive) {
            stopTestCycle();
        }

        if (mode.is("Matrix")) {
            energy = MathHelper.clamp(
                    isStandingStill() ? energy + 0.025f : energy - (oldMatrix.get() ? 0.005f : 0.0f),
                    0.0f, 1.0f
            );
        }

        if (mode.is("HolyWorld")) {
            long setbackDelay = System.currentTimeMillis() - lastSetbackTime;
            if (setbackDelay > 2000L && !PlayerUtils.hasPlayerMovement()) {
                energy = MathHelper.clamp(energy + 0.01f, 0.0f, 1.0f);
            }
        }

        prevPosX = mc.player.getX();
        prevPosY = mc.player.getY();
        prevPosZ = mc.player.getZ();
        prevYaw = mc.player.getYaw();
        prevPitch = mc.player.getPitch();

        String currentMode = mode.get();
        if (currentMode.equals("Normal")) {
            tickTimer = speed.get();
        } else if (currentMode.equals("Matrix")) {
            handleMatrixMode();
        } else if (currentMode.equals("Shift")) {
            handleShiftMode();
        } else if (currentMode.equals("HolyWorld")) {
            handleHolyWorldMode();
        } else if (currentMode.equals("Test")) {
            handleTestMode();
        } else {
            tickTimer = 1.0f;
        }

        TimerController.setTimerSpeed(isState() ? tickTimer : 1.0f);
    }

    @EventHandler
    public void onPacket(EventPacket event) {
        if (mc.player == null || mc.world == null || !event.isReceive()) {
            return;
        }

        if (mode.is("HolyWorld") && event.getPacket() instanceof CommonPingS2CPacket) {
            long setbackDelay = System.currentTimeMillis() - lastSetbackTime;
            if (setbackDelay > 2000L) {
                if (System.currentTimeMillis() - cancelTime > 25_000L) {
                    cancelTime = System.currentTimeMillis();
                    energy = 0.0f;
                    return;
                }

                if (!PlayerUtils.hasPlayerMovement()) {
                    energy = MathHelper.clamp(energy + 0.005f, 0.0f, 1.0f);
                }

                event.setCancelled(true);
            }
        }

        if (event.getPacket() instanceof PlayerPositionLookS2CPacket) {
            lastSetbackTime = System.currentTimeMillis();
            if (mode.is("Test") && testCycleActive) {
                beginTestCharging();
                return;
            }
            String flagAction = onFlag.get();
            if (flagAction.equals("Reset")) {
                tickTimer = 1.0f;
                energy = 0.0f;
                TimerController.reset();
            } else if (flagAction.equals("Disable")) {
                energy = 0.0f;
                disableWithMessage("Disabled after setback.");
            }
        }

        if (mode.is("HolyWorld")
                && event.getPacket() instanceof EntityVelocityUpdateS2CPacket velocityPacket
                && velocityPacket.getEntityId() == mc.player.getId()) {
            tickTimer = 1.0f;
            energy = 0.0f;
            TimerController.reset();
        }
    }

    private void handleMatrixMode() {
        if (!PlayerUtils.hasPlayerMovement()) {
            tickTimer = 1.0f;
            return;
        }

        tickTimer = Math.max(speed.get(), 1.0f);
        if (energy > 0.0f) {
            energy = MathHelper.clamp(energy - (float) ((0.1f * speed.get()) - 0.1f), 0.0f, 1.0f);
            return;
        }

        disableWithMessage("Timer charge depleted.");
    }

    private void handleShiftMode() {
        tickTimer = 1.0f;
        if (energy < 0.9f) {
            disableWithMessage("Stand still before using Shift again.");
            return;
        }

        if (mc.player instanceof ClientPlayerEntity clientPlayer
                && clientPlayer instanceof ClientPlayerEntityAccessor invoker) {
            for (int i = 0; i < Math.round(shiftTicks.get()); i++) {
                invoker.callSendMovementPackets();
            }
        }

        disableWithMessage("Shift burst completed.");
    }

    private void handleHolyWorldMode() {
        long setbackDelay = System.currentTimeMillis() - lastSetbackTime;
        int key = boostKey.get();
        boolean boostActive = (key == 0 || key == GLFW.GLFW_KEY_UNKNOWN) ? energy > 0.0f : isKeyPressed(key);
        if (energy <= 0.0f || !boostActive || setbackDelay < 2000L) {
            tickTimer = 1.0f;
            return;
        }

        tickTimer = Math.max(speed.get(), 1.0f);
        energy = MathHelper.clamp(energy - (float) ((0.0025f * speed.get()) - 0.0025f), 0.0f, 1.0f);
    }

    private boolean isStandingStill() {
        if (mc.player == null) {
            return true;
        }
        return prevPosX == mc.player.getX()
                && prevPosY == mc.player.getY()
                && prevPosZ == mc.player.getZ()
                && prevYaw == mc.player.getYaw()
                && prevPitch == mc.player.getPitch();
    }

    public float getHudFillProgress() {
        if (mode.is("Normal")) {
            return 1.0f;
        }
        if (mode.is("Test")) {
            if (!isState() || !testCycleActive) {
                return 0.0f;
            }
            if (testPhase == TestPhase.BOOSTING) {
                return 1.0f;
            }
            long elapsed = System.nanoTime() - testPhaseStartedAt;
            return MathHelper.clamp((float) elapsed / TEST_CHARGE_DURATION_NANOS, 0.0f, 1.0f);
        }
        return MathHelper.clamp(energy, 0.0f, 1.0f);
    }

    public int getHudFillPercent() {
        return Math.round(getHudFillProgress() * 100.0f);
    }

    @EventHandler
    public void onTestMoveInput(MoveInputEvent event) {
        if (!isState() || !mode.is("Test") || mc.player == null) {
            return;
        }

        if (!isTestMoving()) {
            testGroundTicks = 0;
            stopTestCycle();
            return;
        }

        if (testPhase == TestPhase.BOOSTING) {
            testGroundTicks = mc.player.verticalCollision ? testGroundTicks + 1 : 0;
            if (testGroundTicks > 0) {
                event.setOverrideJump(true);
                event.setJumpPressed(true);
            }
        }
    }

    @EventHandler
    public void onTestSprint(SprintEvent event) {
        if (!isState() || !mode.is("Test")) {
            return;
        }
        if (testPhase == TestPhase.BOOSTING) {
            event.setSprinting(true);
        }
    }

    private void handleTestMode() {
        if (!isTestMoving()) {
            stopTestCycle();
            return;
        }

        long now = System.nanoTime();

        if (!testCycleActive) {
            testCycleActive = true;
            beginTestCharging();
            return;
        }

        if (testPhase == TestPhase.BOOSTING) {
            if (mc.player.verticalCollision) {
                if (testAirborne) {
                    testAirborne = false;
                    testCompletedJumps++;
                }
            } else {
                testAirborne = true;
                if (testCompletedJumps >= TEST_FULL_BOOST_JUMPS && mc.player.getVelocity().y <= 0.0D) {
                    beginTestCharging();
                    return;
                }
            }
        }

        if (testPhase == TestPhase.CHARGING && now - testPhaseStartedAt >= TEST_CHARGE_DURATION_NANOS) {
            beginTestBoosting();
        } else if (testPhase == TestPhase.BOOSTING && now - testPhaseStartedAt >= TEST_MAX_BOOST_DURATION_NANOS) {
            beginTestCharging();
        }
    }

    private void beginTestCharging() {
        testPhase = TestPhase.CHARGING;
        testAirborne = false;
        testCompletedJumps = 0;
        testGroundTicks = 0;
        testPhaseStartedAt = System.nanoTime();
        tickTimer = TEST_CHARGE_TIMER;
    }

    private void beginTestBoosting() {
        testPhase = TestPhase.BOOSTING;
        testAirborne = false;
        testCompletedJumps = 0;
        testGroundTicks = 0;
        testPhaseStartedAt = System.nanoTime();
        tickTimer = TEST_BOOST_TIMER;
    }

    private void stopTestCycle() {
        testCycleActive = false;
        testPhase = TestPhase.CHARGING;
        testAirborne = false;
        testCompletedJumps = 0;
        testGroundTicks = 0;
        testPhaseStartedAt = 0L;
        tickTimer = 1.0f;
    }

    private boolean isTestMoving() {
        if (mc.player == null || mc.player.input == null) {
            return false;
        }
        PlayerInput input = mc.player.input.playerInput;
        return input.forward() || input.backward() || input.left() || input.right();
    }

    private boolean isKeyPressed(int keyCode) {
        if (keyCode == GLFW.GLFW_KEY_UNKNOWN || keyCode == 0) {
            return false;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        return GLFW.glfwGetKey(client.getWindow().getHandle(), keyCode) == GLFW.GLFW_PRESS;
    }

    private void disableWithMessage(String message) {
        TimerController.reset();
        tickTimer = 1.0f;
        if (mc.player != null) {
            mc.player.sendMessage(
                    Text.literal("Light » ").formatted(Formatting.GRAY)
                            .append(Text.literal("Timer: ").formatted(Formatting.RED))
                            .append(Text.literal(message).formatted(Formatting.WHITE)),
                    false
            );
        }
        setState(false);
    }
}

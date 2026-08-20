package org.privates.client.managers.module.impl.movement;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.block.Blocks;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.util.math.BlockPos;
import org.privates.client.api.annotations.Category3;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.other.Instance;
import org.privates.lib.util.time.StopWatch;

@Category3
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "WaterSpeed", category = Category.MOVEMENT, description = "Увеличивает скорость в воде")
public class WaterSpeed extends Module {
    public static WaterSpeed getInstance() {
        return Instance.get(WaterSpeed.class);
    }

    private final ModeSetting mode = new ModeSetting(this, "Режим", "Ванилла", "Скольжение", "Грация дельфина", "JumpBoost");

    private final SliderSetting forwardSpeed = new SliderSetting(this, "Скорость вперед", 1.15f, 1.0f, 2.0f, 0.01f)
            .setVisible(() -> mode.is("Ванилла"));
    private final SliderSetting strafeSpeed = new SliderSetting(this, "Скорость влево/вправо", 1.1f, 1.0f, 2.0f, 0.01f)
            .setVisible(() -> mode.is("Ванилла"));
    private final SliderSetting upSpeed = new SliderSetting(this, "Скорость вверх", 0.05f, 0.0f, 0.5f, 0.01f)
            .setVisible(() -> mode.is("Ванилла"));
    private final SliderSetting downSpeed = new SliderSetting(this, "Скорость вниз", 0.05f, 0.0f, 0.5f, 0.01f)
            .setVisible(() -> mode.is("Ванилла"));

    private final SliderSetting glideSpeed = new SliderSetting(this, "Скорость скольжения", 0.8f, 0.1f, 2.0f, 0.1f)
            .setVisible(() -> mode.is("Скольжение"));
    private final SliderSetting glideFriction = new SliderSetting(this, "Трение", 0.99f, 0.8f, 0.99f, 0.01f)
            .setVisible(() -> mode.is("Скольжение"));

    private final SliderSetting dolphinSpeed = new SliderSetting(this, "Скорость дельфина", 1.5f, 1.0f, 3.0f, 0.1f)
            .setVisible(() -> mode.is("Грация дельфина"));
    private final SliderSetting dolphinJumpHeight = new SliderSetting(this, "Высота прыжка", 0.3f, 0.1f, 1.0f, 0.05f)
            .setVisible(() -> mode.is("Грация дельфина"));
    private final SliderSetting dolphinDiveSpeed = new SliderSetting(this, "Скорость погружения", 0.2f, 0.05f, 0.5f, 0.05f)
            .setVisible(() -> mode.is("Грация дельфина"));

    private final SliderSetting jumpBoostPower = new SliderSetting(this, "Сила прыжка", 0.5f, 0.1f, 2.0f, 0.1f)
            .setVisible(() -> mode.is("JumpBoost"));
    private final SliderSetting jumpBoostSpeed = new SliderSetting(this, "Скорость после прыжка", 1.3f, 1.0f, 2.5f, 0.1f)
            .setVisible(() -> mode.is("JumpBoost"));

    private final BooleanSetting blockBoost = new BooleanSetting(this, "Буст от блоков", true);
    private final SliderSetting blockBoostRadius = new SliderSetting(this, "Радиус поиска блоков", 3, 1, 5, 1)
            .setVisible(blockBoost::getValue);
    private final SliderSetting blockBoostPower = new SliderSetting(this, "Сила буста от блоков", 1.2f, 1.0f, 2.0f, 0.1f)
            .setVisible(blockBoost::getValue);

    private final BooleanSetting magmaBoost = new BooleanSetting(this, "Буст возле магмы", true);
    private final SliderSetting magmaBoostRadius = new SliderSetting(this, "Радиус магмы", 4, 1, 6, 1)
            .setVisible(magmaBoost::getValue);
    private final SliderSetting magmaBoostPower = new SliderSetting(this, "Сила буста от магмы", 1.5f, 1.0f, 3.0f, 0.1f)
            .setVisible(magmaBoost::getValue);

    private final BooleanSetting soulSandBoost = new BooleanSetting(this, "Буст возле песка душ", true);
    private final SliderSetting soulSandRadius = new SliderSetting(this, "Радиус песка душ", 3, 1, 5, 1)
            .setVisible(soulSandBoost::getValue);
    private final SliderSetting soulSandBoostPower = new SliderSetting(this, "Сила буста от песка душ", 1.3f, 1.0f, 2.5f, 0.1f)
            .setVisible(soulSandBoost::getValue);

    private final StopWatch time = new StopWatch();
    private boolean wasInWater = false;
    private int dolphinCycle = 0;
    private long lastJumpTime = 0;

    @EventHandler
    public void onUpdate(UpdateEvent e) {
        if (!mc.player.isInWater()) {
            time.reset();
            wasInWater = false;
            return;
        }

        if ((mc.world.getBlockState(mc.player.getPosition().up()).getBlock() != Blocks.WATER && mc.gameSettings.keyBindJump.isKeyDown())) {
            time.reset();
        }

        if (!wasInWater) {
            wasInWater = true;
            time.reset();
        }

        float boostMultiplier = 1.0f;

        if (blockBoost.getValue()) {
            boostMultiplier *= checkBlockBoost();
        }

        if (magmaBoost.getValue()) {
            boostMultiplier *= checkMagmaBoost();
        }

        if (soulSandBoost.getValue()) {
            boostMultiplier *= checkSoulSandBoost();
        }

        switch (mode.getValue()) {
            case "Ванилла":
                handleVanillaMode(boostMultiplier);
                break;
            case "Скольжение":
                handleGlideMode(boostMultiplier);
                break;
            case "Грация дельфина":
                handleDolphinMode(boostMultiplier);
                break;
            case "JumpBoost":
                handleJumpBoostMode(boostMultiplier);
                break;
        }
    }

    private void handleVanillaMode(float boostMultiplier) {
        if (!time.finished(160)) return;

        mc.player.movementInput.sneaking = false;
        mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.PRESS_SHIFT_KEY));
        mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.RELEASE_SHIFT_KEY));

        float ySpeed = 0;
        if (mc.gameSettings.keyBindJump.isKeyDown()) {
            ySpeed = upSpeed.getValue();
        } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
            ySpeed = -downSpeed.getValue();
        }

        float horizontalSpeed = forwardSpeed.getValue() * boostMultiplier;
        float strafing = strafeSpeed.getValue() * boostMultiplier;

        if (mc.player.movementInput.moveStrafe != 0) {
            horizontalSpeed = strafing;
        }

        mc.player.setVelocity(
                mc.player.getMotion().x * horizontalSpeed,
                mc.player.getMotion().y + ySpeed,
                mc.player.getMotion().z * horizontalSpeed
        );
    }

    private void handleGlideMode(float boostMultiplier) {
        mc.player.movementInput.sneaking = false;

        float friction = glideFriction.getValue();
        float speed = glideSpeed.getValue() * boostMultiplier;

        if (mc.player.movementInput.moveForward != 0 || mc.player.movementInput.moveStrafe != 0) {
            double yaw = Math.toRadians(mc.player.rotationYaw);
            double moveX = -Math.sin(yaw) * mc.player.movementInput.moveForward * speed;
            double moveZ = Math.cos(yaw) * mc.player.movementInput.moveForward * speed;

            mc.player.setVelocity(
                    mc.player.getMotion().x * friction + moveX,
                    mc.player.getMotion().y * 0.95,
                    mc.player.getMotion().z * friction + moveZ
            );
        } else {
            mc.player.setVelocity(
                    mc.player.getMotion().x * friction,
                    mc.player.getMotion().y * 0.95,
                    mc.player.getMotion().z * friction
            );
        }

        if (mc.gameSettings.keyBindJump.isKeyDown()) {
            mc.player.setVelocity(mc.player.getMotion().x, 0.1, mc.player.getMotion().z);
        } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
            mc.player.setVelocity(mc.player.getMotion().x, -dolphinDiveSpeed.getValue(), mc.player.getMotion().z);
        }
    }

    private void handleDolphinMode(float boostMultiplier) {
        mc.player.movementInput.sneaking = false;
        dolphinCycle++;

        float speed = dolphinSpeed.getValue() * boostMultiplier;

        if (mc.player.movementInput.moveForward != 0) {
            double yaw = Math.toRadians(mc.player.rotationYaw);
            double moveX = -Math.sin(yaw) * speed * 0.1;
            double moveZ = Math.cos(yaw) * speed * 0.1;

            mc.player.setVelocity(
                    mc.player.getMotion().x * 0.98 + moveX,
                    mc.player.getMotion().y,
                    mc.player.getMotion().z * 0.98 + moveZ
            );
        }

        if (dolphinCycle % 20 == 0 && mc.player.movementInput.moveForward > 0) {
            mc.player.setVelocity(
                    mc.player.getMotion().x * 1.2,
                    dolphinJumpHeight.getValue(),
                    mc.player.getMotion().z * 1.2
            );
        }

        if (mc.gameSettings.keyBindSneak.isKeyDown()) {
            mc.player.setVelocity(
                    mc.player.getMotion().x,
                    -dolphinDiveSpeed.getValue(),
                    mc.player.getMotion().z
            );
        }
    }

    private void handleJumpBoostMode(float boostMultiplier) {
        mc.player.movementInput.sneaking = false;

        if (mc.gameSettings.keyBindJump.isKeyDown()) {
            long currentTime = System.currentTimeMillis();

            if (mc.player.getMotion().y < 0.1 && currentTime - lastJumpTime > 500) {
                double yaw = Math.toRadians(mc.player.rotationYaw);
                float jumpSpeed = jumpBoostSpeed.getValue() * boostMultiplier;

                double moveX = -Math.sin(yaw) * mc.player.movementInput.moveForward * jumpSpeed * 0.2;
                double moveZ = Math.cos(yaw) * mc.player.movementInput.moveForward * jumpSpeed * 0.2;

                mc.player.setVelocity(
                        mc.player.getMotion().x + moveX,
                        jumpBoostPower.getValue(),
                        mc.player.getMotion().z + moveZ
                );

                lastJumpTime = currentTime;
            }
        } else {
            float baseSpeed = 1.1f * boostMultiplier;
            mc.player.setVelocity(
                    mc.player.getMotion().x * baseSpeed,
                    mc.player.getMotion().y,
                    mc.player.getMotion().z * baseSpeed
            );
        }
    }

    private float checkBlockBoost() {
        int radius = blockBoostRadius.getValue().intValue();
        BlockPos playerPos = mc.player.getPosition();

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos checkPos = playerPos.add(x, y, z);
                    if (mc.world.getBlockState(checkPos).getBlock() == Blocks.KELP ||
                            mc.world.getBlockState(checkPos).getBlock() == Blocks.KELP_PLANT ||
                            mc.world.getBlockState(checkPos).getBlock() == Blocks.SEAGRASS ||
                            mc.world.getBlockState(checkPos).getBlock() == Blocks.TALL_SEAGRASS) {
                        return blockBoostPower.getValue();
                    }
                }
            }
        }
        return 1.0f;
    }

    private float checkMagmaBoost() {
        int radius = magmaBoostRadius.getValue().intValue();
        BlockPos playerPos = mc.player.getPosition();

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos checkPos = playerPos.add(x, y, z);
                    if (mc.world.getBlockState(checkPos).getBlock() == Blocks.MAGMA_BLOCK ||
                            mc.world.getBlockState(checkPos).getBlock() == Blocks.LAVA) {
                        return magmaBoostPower.getValue();
                    }
                }
            }
        }
        return 1.0f;
    }

    private float checkSoulSandBoost() {
        int radius = soulSandRadius.getValue().intValue();
        BlockPos playerPos = mc.player.getPosition();

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos checkPos = playerPos.add(x, y, z);
                    if (mc.world.getBlockState(checkPos).getBlock() == Blocks.SOUL_SAND ||
                            mc.world.getBlockState(checkPos).getBlock() == Blocks.SOUL_SOIL) {
                        return soulSandBoostPower.getValue();
                    }
                }
            }
        }
        return 1.0f;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        wasInWater = false;
        dolphinCycle = 0;
        lastJumpTime = 0;
    }
}
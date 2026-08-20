package org.privates.client.managers.module.impl.movement;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import org.privates.client.api.annotations.Category3;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.player.MoveUtil;

@Category3
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "Jesus", category = Category.MOVEMENT, description = "Вы можете ходить по воде")
public class Jesus extends Module {

    public static Jesus getInstance() {
        return Instance.get(Jesus.class);
    }

    private final ModeSetting mode = new ModeSetting(this, "Режим", "Обычный", "Grim Old", "MetaHvH", "Matrix", "NCP", "Vulcan");
    private final SliderSetting speed = new SliderSetting(this, "Скорость", 1.0f, 0.5f, 1.5f, 0.05f)
            .setVisible(() -> mode.is("Grim Old"));

    private int matrixTicks = 0;
    private int matrixPhase = 0;
    private int ncpTicks = 0;
    private boolean ncpSpoofGround = false;
    private int vulcanStage = 0;
    private double vulcanStartY = 0;
    private boolean vulcanInitialized = false;

    @Override
    protected void onDisable() {
        super.onDisable();
        matrixTicks = 0;
        matrixPhase = 0;
        ncpTicks = 0;
        ncpSpoofGround = false;
        vulcanStage = 0;
        vulcanStartY = 0;
        vulcanInitialized = false;
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        switch (mode.getValue()) {
            case "Обычный":
                handleDefault();
                break;
            case "MetaHvH":
                handleMetaHvH();
                break;
            case "Grim Old":
                handleGrimOld();
                break;
            case "Matrix":
                handleMatrix();
                break;
            case "NCP":
                handleNCP();
                break;
            case "Vulcan":
                handleVulcan();
                break;
        }
    }

    private void handleDefault() {
        if (mc.player.isInWater() || mc.player.isInLava()) {
            mc.player.getMotion().y = 0.011;

            if (MoveUtil.isMoving()) {
                double currentSpeed = Math.sqrt(mc.player.getMotion().x * mc.player.getMotion().x + mc.player.getMotion().z * mc.player.getMotion().z);
                if (currentSpeed < 1.0f) {
                    mc.player.getMotion().x *= 0.7 * 1.74;
                    mc.player.getMotion().z *= 0.7 * 1.74;
                }
            }
        }
    }

    private void handleMetaHvH() {
        if (mc.player.isInWater() || mc.player.isInLava()) {
            EffectInstance speedEffect = mc.player.getActivePotionEffect(Effects.SPEED);
            EffectInstance slowEffect = mc.player.getActivePotionEffect(Effects.SLOWNESS);
            float appliedSpeed = getAppliedSpeed(speedEffect, slowEffect);

            MoveUtil.setSpeed(appliedSpeed);

            boolean isMoving = mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown()
                    || mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown();

            if (!isMoving) {
                mc.player.getMotion().x = 0.0;
                mc.player.getMotion().z = 0.0;
            }

            mc.player.getMotion().y = mc.gameSettings.keyBindJump.isKeyDown() ? 0.019 : 0.003;
        }
    }

    private void handleGrimOld() {
        mc.player.setVelocity(
                mc.player.getMotion().x * speed.getValue(),
                0.01D,
                mc.player.getMotion().z * speed.getValue()
        );

        mc.player.setVelocity(
                mc.player.getMotion().x * speed.getValue(),
                0.00D,
                mc.player.getMotion().z * speed.getValue()
        );

        mc.player.setVelocity(
                mc.player.getMotion().x * speed.getValue(),
                -0.1D,
                mc.player.getMotion().z * speed.getValue()
        );

        mc.player.setVelocity(
                mc.player.getMotion().x * speed.getValue(),
                0.035D,
                mc.player.getMotion().z * speed.getValue()
        );

        mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
        mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.STOP_SPRINTING));
    }

    private void handleMatrix() {
        if (mc.player.isInWater() || mc.player.isInLava()) {
            matrixTicks++;

            switch (matrixPhase) {
                case 0:
                    mc.player.getMotion().y = 0.0;

                    int slot = mc.player.inventory.currentItem;
                    mc.player.connection.sendPacket(new CHeldItemChangePacket(slot < 8 ? slot + 1 : 0));
                    mc.player.connection.sendPacket(new CHeldItemChangePacket(slot));

                    matrixPhase = 1;
                    break;

                case 1:
                    if (matrixTicks % 4 == 0) {
                        BlockPos fakePos = new BlockPos(mc.player.getPosX(), mc.player.getPosY() - 1, mc.player.getPosZ());
                        mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(
                                mc.player.getPosX(),
                                mc.player.getPosY(),
                                mc.player.getPosZ(),
                                true
                        ));
                    }

                    if (mc.gameSettings.keyBindJump.isKeyDown()) {
                        mc.player.getMotion().y = 0.42;
                        matrixPhase = 2;
                    } else {
                        mc.player.getMotion().y = 0.0;
                    }
                    break;

                case 2:
                    mc.player.getMotion().y -= 0.08;

                    if (mc.player.getMotion().y < 0) {
                        matrixPhase = 0;
                    }
                    break;
            }

            if (MoveUtil.isMoving() && matrixTicks % 2 == 0) {
                mc.player.connection.sendPacket(new CPlayerDiggingPacket(
                        CPlayerDiggingPacket.Action.ABORT_DESTROY_BLOCK,
                        BlockPos.ZERO,
                        Direction.DOWN
                ));
            }
        } else {
            matrixTicks = 0;
            matrixPhase = 0;
        }
    }

    private void handleNCP() {
        if (mc.player.isInWater() || mc.player.isInLava()) {
            ncpTicks++;

            if (ncpTicks == 1) {
                mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_SPRINTING));
            }

            if (ncpTicks % 3 == 0) {
                mc.player.connection.sendPacket(new CPlayerPacket.PositionRotationPacket(
                        mc.player.getPosX(),
                        mc.player.getPosY() + (ncpSpoofGround ? 0.0 : 0.001),
                        mc.player.getPosZ(),
                        mc.player.rotationYaw,
                        mc.player.rotationPitch,
                        true
                ));
                ncpSpoofGround = !ncpSpoofGround;
            }

            if (mc.gameSettings.keyBindJump.isKeyDown()) {
                mc.player.getMotion().y = 0.42;
            } else {
                mc.player.getMotion().y = 0.0;
            }

            if (MoveUtil.isMoving()) {
                Vector3d motion = mc.player.getMotion();
                double yaw = Math.toRadians(mc.player.rotationYaw);
                mc.player.getMotion().x = -Math.sin(yaw) * 0.2;
                mc.player.getMotion().z = Math.cos(yaw) * 0.2;

                if (ncpTicks % 5 == 0) {
                    mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.STOP_SPRINTING));
                    mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_SPRINTING));
                }
            }
        } else {
            ncpTicks = 0;
            ncpSpoofGround = false;
        }
    }

    private void handleVulcan() {
        if (mc.player.isInWater() || mc.player.isInLava()) {
            if (!vulcanInitialized) {
                vulcanStartY = mc.player.getPosY();
                vulcanStage = 0;
                vulcanInitialized = true;
            }

            switch (vulcanStage) {
                case 0:
                    mc.player.getMotion().y = 0.0;
                    mc.player.setPosition(mc.player.getPosX(), vulcanStartY, mc.player.getPosZ());

                    BlockPos pos = new BlockPos(mc.player.getPosX(), mc.player.getPosY() - 1, mc.player.getPosZ());
                    mc.player.connection.sendPacket(new CPlayerDiggingPacket(
                            CPlayerDiggingPacket.Action.START_DESTROY_BLOCK,
                            pos,
                            Direction.UP
                    ));
                    mc.player.connection.sendPacket(new CPlayerDiggingPacket(
                            CPlayerDiggingPacket.Action.ABORT_DESTROY_BLOCK,
                            pos,
                            Direction.UP
                    ));

                    vulcanStage = 1;
                    break;

                case 1:
                    if (mc.gameSettings.keyBindJump.isKeyDown()) {
                        mc.player.getMotion().y = 0.42;
                        vulcanStage = 2;
                    } else {
                        mc.player.getMotion().y = 0.0;

                        mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(
                                mc.player.getPosX(),
                                vulcanStartY + 0.015,
                                mc.player.getPosZ(),
                                false
                        ));
                    }
                    break;

                case 2:
                    mc.player.getMotion().y -= 0.08;

                    if (mc.player.getPosY() <= vulcanStartY) {
                        vulcanStage = 0;
                    }
                    break;
            }

            if (MoveUtil.isMoving()) {
                double yaw = Math.toRadians(mc.player.rotationYaw);
                mc.player.getMotion().x = -Math.sin(yaw) * 0.18;
                mc.player.getMotion().z = Math.cos(yaw) * 0.18;
            }
        } else {
            vulcanStage = 0;
            vulcanStartY = 0;
            vulcanInitialized = false;
        }
    }

    private static float getAppliedSpeed(EffectInstance speedEffect, EffectInstance slowEffect) {
        ItemStack offHandItem = mc.player.getHeldItemOffhand();
        String itemName = offHandItem.getDisplayName().getString();
        float appliedSpeed = 0F;

        if (itemName.contains("Ломтик Дыни") && speedEffect != null && speedEffect.getAmplifier() == 2) {
            appliedSpeed = 0.4283F * 1.15F;
        } else {
            if (speedEffect != null) {
                if (speedEffect.getAmplifier() == 2) appliedSpeed = 0.44f * 1.15F;
                else if (speedEffect.getAmplifier() == 1) appliedSpeed = 0.44f;
            } else appliedSpeed = 0.44f * 0.68F;
        }

        if (slowEffect != null) appliedSpeed *= 0.85f;

        return appliedSpeed;
    }
}
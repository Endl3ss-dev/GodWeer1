package org.privates.client.managers.module.impl.movement;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.AxisAlignedBB;
import org.privates.client.api.annotations.Category2;
import org.privates.client.api.annotations.Rage;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.player.MoveUtil;
import org.privates.common.impl.taskript.Script;
import org.privates.lib.util.time.StopWatch;

@Category2
@Rage
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "Speed", category = Category.MOVEMENT, description = "Увеличение скорости передвижения игрока")
public class Speed extends Module {
    public final ModeSetting mode = new ModeSetting(this, "Режим", "HvH", "Entity", "SP-DUEL", "NCP", "Matrix703", "AAC5");
    public final BooleanSetting entityOvertake = new BooleanSetting(this, "Перегон игрока", false).setVisible(() -> mode.is("Entity"));
    public final SliderSetting overtakeDistance = new SliderSetting(this, "Дистанция перегона", 0.3f, 0.1f, 1.0f, 0.05f).setVisible(() -> mode.is("Entity") && entityOvertake.getValue());
    public final SliderSetting chaseDistance = new SliderSetting(this, "Дистанция догона", 0.15f, 0.05f, 0.5f, 0.05f).setVisible(() -> mode.is("Entity") && entityOvertake.getValue());

    private final StopWatch stopWatch = new StopWatch();
    private final Script script = new Script();
    private int stage = 0;
    private double lastDist = 0;

    @Override
    public void onDisable() {
        stage = 0;
        lastDist = 0;
    }

    @EventHandler
    public void onUpdate(UpdateEvent e) {
        if (!mc.player.isOnGround() && mode.is("HvH") && !mc.player.isElytraFlying()) {
            float melonBallSpeed = 0.36F;
            ItemStack offHandItem = mc.player.getHeldItemOffhand();
            EffectInstance speedEffect = mc.player.getActivePotionEffect(Effects.SPEED);
            EffectInstance DeEffect = mc.player.getActivePotionEffect(Effects.SLOWNESS);
            String itemName = offHandItem.getDisplayName().getString();

            float appliedSpeed = 0;

            if (speedEffect != null) {
                if (speedEffect.getAmplifier() == 2) {
                    appliedSpeed = melonBallSpeed * 1.155F;
                    if (itemName.contains("Ломтик Дыни")) {
                        if (speedEffect != null && speedEffect.getAmplifier() == 2) {
                            appliedSpeed = 0.41755F;
                        } else {
                            appliedSpeed = 0.41755F * 0.52F;
                        }
                    }
                } else if (speedEffect.getAmplifier() == 1) {
                    appliedSpeed = melonBallSpeed;
                }
            } else {
                appliedSpeed = melonBallSpeed * 0.68F;
            }

            if (DeEffect != null) {
                appliedSpeed *= 0.835f;
            }

            if (!mc.player.isOnGround()) {
                appliedSpeed *= 1.435F;
            }

            MoveUtil.setSpeed(appliedSpeed);
        }

        if (mode.is("Entity")) {
            float expandValue = 0.3f;

            if (entityOvertake.getValue()) {
                PlayerEntity nearestPlayer = getNearestPlayer();
                if (nearestPlayer != null) {
                    double distanceToPlayer = mc.player.getDistance(nearestPlayer);

                    if (distanceToPlayer <= 0.25) {
                        expandValue = overtakeDistance.getValue();
                    } else if (distanceToPlayer <= 0.25) {
                        expandValue = chaseDistance.getValue();
                    }
                }
            }

            AxisAlignedBB aabb = mc.player.getBoundingBox().grow(expandValue);
            int armorstans = mc.world.getEntitiesWithinAABB(ArmorStandEntity.class, aabb).size();
            boolean canBoost = armorstans > 0.3 || mc.world.getEntitiesWithinAABB(LivingEntity.class, aabb).size() > 0.3;
            if (canBoost && !mc.player.isOnGround()) {
                mc.player.jumpMovementFactor = armorstans > 1 ? 1f / (float) armorstans : 0.05f;
            }
        }

        if (mode.is("Test")) {
            AxisAlignedBB aabb = mc.player.getBoundingBox().grow(0.085f);
            int armorstans = mc.world.getEntitiesWithinAABB(ArmorStandEntity.class, aabb).size();
            boolean canBoost = armorstans > 1 || mc.world.getEntitiesWithinAABB(LivingEntity.class, aabb).size() > 1;
            if (canBoost && !mc.player.isOnGround()) {
                mc.player.jumpMovementFactor = armorstans > 1 ? 1f / (float) armorstans : 0.03f;
            }
        }

        if (mode.is("SP-DUEL")) {

            AxisAlignedBB aabb = mc.player.getBoundingBox().grow(0.35);
            int armorstans = mc.world.getEntitiesWithinAABB(ArmorStandEntity.class, aabb).size();
            boolean canBoost = armorstans > 1 || mc.world.getEntitiesWithinAABB(LivingEntity.class, aabb).size() > 1;
            if (canBoost && !mc.player.isOnGround()) {
                mc.player.jumpMovementFactor = armorstans > 1 ? 1f / (float) armorstans : (float) (0.05);
            }
        }

        if (mode.is("VerusB17")) {
            if (mc.player.fallDistance > 2.5f) {
                stage = 1;
            }
            if (stage == 0 && mc.player.isOnGround()) {
                mc.player.motion.y = 0.42f;
                sendPacket(new CPlayerPacket.PositionPacket(mc.player.getPosX(), mc.player.getPosY() + 0.42f, mc.player.getPosZ(), false));
                stage = 1;
            } else if (stage == 1) {
                double[] dir = MoveUtil.forward(0.66);
                mc.player.motion.x = dir[0];
                mc.player.motion.z = dir[1];
                sendPacket(new CPlayerPacket.PositionPacket(mc.player.getPosX() + dir[0], mc.player.getPosY(), mc.player.getPosZ() + dir[1], false));
                stage = 2;
            } else if (stage == 2) {
                double currentSpeed = Math.sqrt(mc.player.motion.x * mc.player.motion.x + mc.player.motion.z * mc.player.motion.z);
                double speed = 0.66 - (currentSpeed - 0.33) * 0.3;
                double[] dir = MoveUtil.forward(speed);
                mc.player.motion.x = dir[0];
                mc.player.motion.z = dir[1];
                sendPacket(new CPlayerPacket.PositionPacket(mc.player.getPosX() + dir[0], mc.player.getPosY(), mc.player.getPosZ() + dir[1], true));
                stage = 3;
            } else if (stage == 3) {
                double speed = 0.66;
                double[] dir = MoveUtil.forward(speed);
                mc.player.motion.x = dir[0];
                mc.player.motion.z = dir[1];
                stage = 0;
            }
        }

        if (mode.is("NCP")) {
            if (mc.player.isOnGround() && MoveUtil.isMoving()) {
                mc.player.jump();
                mc.player.motion.x *= 1.68;
                mc.player.motion.z *= 1.68;
                sendPacket(new CPlayerPacket(true));
            } else if (!mc.player.isOnGround()) {
                double speed = 0.489;
                if (MoveUtil.speed() < speed) {
                    MoveUtil.setSpeed(speed);
                } else {
                    MoveUtil.setSpeed(MoveUtil.speed() - 0.006);
                }
            }
        }

        if (mode.is("Spartan422")) {
            if (mc.player.isOnGround() && MoveUtil.isMoving()) {
                mc.player.jump();
                mc.player.motion.x *= 1.6;
                mc.player.motion.z *= 1.6;
                sendPacket(new CPlayerPacket.PositionPacket(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ(), true));
            } else if (!mc.player.isOnGround()) {
                double dist = MoveUtil.speedSqrt();
                if (dist > lastDist) {
                    MoveUtil.setSpeed(lastDist = dist);
                } else {
                    MoveUtil.setSpeed(lastDist = dist - 0.005);
                }
            }
        }

        if (mode.is("Matrix703")) {
            if (mc.player.isOnGround() && MoveUtil.isMoving()) {
                mc.player.jump();
                mc.player.motion.y = 0.419;
                sendPacket(new CPlayerPacket.PositionPacket(mc.player.getPosX(), mc.player.getPosY() + 0.419, mc.player.getPosZ(), false));
                double[] dir = MoveUtil.forward(1.98);
                mc.player.motion.x = dir[0];
                mc.player.motion.z = dir[1];
                sendPacket(new CPlayerPacket.PositionPacket(mc.player.getPosX() + dir[0], mc.player.getPosY(), mc.player.getPosZ() + dir[1], true));
            } else if (!mc.player.isOnGround()) {
                double currentSpeed = MoveUtil.speed();
                if (currentSpeed > 0.5) {
                    MoveUtil.setSpeed(currentSpeed - 0.005);
                }
            }
        }

        if (mode.is("AAC5")) {
            if (mc.player.isOnGround() && MoveUtil.isMoving()) {
                mc.player.jump();
                mc.player.motion.y = 0.42;
                sendPacket(new CPlayerPacket.PositionPacket(mc.player.getPosX(), mc.player.getPosY() + 0.42, mc.player.getPosZ(), false));
                double[] dir = MoveUtil.forward(2.1);
                mc.player.motion.x = dir[0] * 0.86;
                mc.player.motion.z = dir[1] * 0.86;
                sendPacket(new CPlayerPacket.PositionPacket(mc.player.getPosX() + dir[0], mc.player.getPosY(), mc.player.getPosZ() + dir[1], true));
            } else if (!mc.player.isOnGround()) {
                MoveUtil.setSpeed(MoveUtil.speed() - 0.01);
            }
        }
    }

    private PlayerEntity getNearestPlayer() {
        PlayerEntity nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player || player.isSpectator() || !player.isAlive()) {
                continue;
            }

            double distance = mc.player.getDistance(player);
            if (distance < minDistance) {
                minDistance = distance;
                nearest = player;
            }
        }

        return nearest;
    }

    private void sendPacket(CPlayerPacket packet) {
        if (mc.player != null && mc.getConnection() != null) {
            mc.getConnection().sendPacket(packet);
        }
    }
}
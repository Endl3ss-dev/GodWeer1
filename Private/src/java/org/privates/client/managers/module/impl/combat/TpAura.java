package org.privates.client.managers.module.impl.combat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import org.privates.client.api.annotations.Category1;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.component.impl.target.TargetComponent;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.other.Instance;
import org.privates.lib.util.time.StopWatch;

@Category1

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "TPAura", category = Category.COMBAT, description = "Телепортируется к цели для атаки на большом расстоянии")
public class TpAura extends Module {

    public static TpAura getInstance() {
        return Instance.get(TpAura.class);
    }

    private final SliderSetting range = new SliderSetting(this, "Дистанция", 15.0f, 10.0f, 100.0f, 1.0f);
    private final SliderSetting attackRange = new SliderSetting(this, "Дистанция атаки", 3.5f, 3.0f, 6.0f, 0.1f);
    private final SliderSetting offsetDistance = new SliderSetting(this, "Отступ от цели", 2.0f, 1.0f, 5.0f, 0.1f);
    private final SliderSetting groundSearchRadius = new SliderSetting(this, "Поиск земли", 5.0f, 1.0f, 10.0f, 1.0f);

    private final SliderSetting delayBeforeReturn = new SliderSetting(this, "Задержка до возврата (мс)", 100, 0, 500, 10);
    private final SliderSetting delayBeforeNextTP = new SliderSetting(this, "Задержка до след. ТП (мс)", 500, 100, 2000, 50);

    private final BooleanSetting autoReturn = new BooleanSetting(this, "Авто возврат", true);
    private final BooleanSetting waitForAura = new BooleanSetting(this, "Ждать атаки", true);

    private LivingEntity currentTarget = null;
    private Vector3d originalPosition = Vector3d.ZERO;
    private Vector3d lastTpPosition = Vector3d.ZERO;

    private TPState currentState = TPState.IDLE;
    private final StopWatch returnTimer = new StopWatch();
    private final StopWatch nextTPTimer = new StopWatch();

    private boolean hasAttacked = false;

    private enum TPState {
        IDLE,
        TELEPORTING_TO_TARGET,
        AT_TARGET,
        RETURNING,
        WAITING_FOR_NEXT_TP
    }

    @Override
    public void onEnable() {
        super.onEnable();
        currentTarget = null;
        originalPosition = mc.player != null ? mc.player.getPositionVec() : Vector3d.ZERO;
        lastTpPosition = Vector3d.ZERO;
        currentState = TPState.IDLE;
        hasAttacked = false;
        returnTimer.reset();
        nextTPTimer.reset();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        currentTarget = null;
        currentState = TPState.IDLE;
        hasAttacked = false;
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null || mc.world == null) return;

        LivingEntity target = findTarget();

        switch (currentState) {
            case IDLE:
                if (target != null) {
                    currentTarget = target;
                    originalPosition = mc.player.getPositionVec();
                    performTeleportToTarget();
                    currentState = TPState.TELEPORTING_TO_TARGET;
                }
                break;

            case TELEPORTING_TO_TARGET:
                currentState = TPState.AT_TARGET;
                returnTimer.reset();
                hasAttacked = false;
                break;

            case AT_TARGET:
                if (waitForAura.getValue()) {
                    AuraNew aura = AuraNew.getInstance();
                    if (aura != null && aura.isEnabled() && aura.target() == currentTarget) {
                        if (System.currentTimeMillis() - aura.lastAttackTime < 100) {
                            hasAttacked = true;
                        }
                    }
                } else {
                    hasAttacked = true;
                }

                if (hasAttacked && returnTimer.finished(delayBeforeReturn.getValue().longValue())) {
                    if (autoReturn.getValue()) {
                        performReturnToOriginal();
                        currentState = TPState.RETURNING;
                    } else {
                        currentState = TPState.WAITING_FOR_NEXT_TP;
                        nextTPTimer.reset();
                    }
                }
                break;

            case RETURNING:
                currentState = TPState.WAITING_FOR_NEXT_TP;
                nextTPTimer.reset();
                break;

            case WAITING_FOR_NEXT_TP:
                if (nextTPTimer.finished(delayBeforeNextTP.getValue().longValue())) {
                    if (target != null) {
                        currentTarget = target;
                        originalPosition = mc.player.getPositionVec();
                        performTeleportToTarget();
                        currentState = TPState.TELEPORTING_TO_TARGET;
                    } else {
                        currentState = TPState.IDLE;
                    }
                }
                break;
        }
    }

    private void performTeleportToTarget() {
        if (currentTarget == null || mc.player == null) return;

        double distance = mc.player.getDistance(currentTarget);
        if (distance > range.getValue()) return;

        Vector3d targetPos = calculateTargetPosition(currentTarget);
        executeTeleport(mc.player.getPositionVec(), targetPos);
        lastTpPosition = targetPos;
    }

    private void performReturnToOriginal() {
        if (mc.player == null) return;
        executeTeleport(mc.player.getPositionVec(), originalPosition);
    }

    private void executeTeleport(Vector3d from, Vector3d to) {
        double dx = Math.abs(from.x - to.x);
        double dy = Math.abs(from.y - to.y);
        double dz = Math.abs(from.z - to.z);
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

        float distanceDensity = 1.0f;
        int packetCount = (int) (distance / (9.64 * distanceDensity)) + 1;

        for (int i = 0; i < packetCount; i++) {
            sendGroundPacket(false);
        }

        sendPositionPacket(to.x, to.y, to.z, false);
        mc.player.setPosition(to.x, to.y, to.z);
    }

    private Vector3d calculateTargetPosition(LivingEntity target) {
        Vector3d targetPos = target.getPositionVec();
        Vector3d playerPos = mc.player.getPositionVec();
        Vector3d direction = targetPos.subtract(playerPos).normalize();

        double offset = offsetDistance.getValue();
        Vector3d teleportPos = targetPos.subtract(direction.scale(offset));

        BlockPos groundPos = findNearestSolidBlock(teleportPos, groundSearchRadius.getValue());
        if (groundPos != null) {
            teleportPos = new Vector3d(teleportPos.x, groundPos.getY() + 0.15, teleportPos.z);
        }

        return teleportPos;
    }

    private BlockPos findNearestSolidBlock(Vector3d pos, double radius) {
        BlockPos center = new BlockPos(pos.x, pos.y, pos.z);

        for (int y = 0; y < radius; y++) {
            BlockPos checkPos = center.down(y);
            if (mc.world.getBlockState(checkPos).isSolid()) {
                return checkPos;
            }
        }

        return null;
    }

    private LivingEntity findTarget() {
        if (mc.world == null || mc.player == null) return null;

        LivingEntity target = TargetComponent.getTarget(range.getValue());
        if (target != null && target.isAlive() && mc.player.getDistance(target) <= range.getValue()) {
            return target;
        }

        return null;
    }

    private void sendPositionPacket(double x, double y, double z, boolean onGround) {
        if (mc.player.connection != null) {
            mc.player.connection.sendPacket(
                    new CPlayerPacket.PositionPacket(x, y, z, onGround)
            );
        }
    }

    private void sendGroundPacket(boolean onGround) {
        if (mc.player.connection != null) {
            mc.player.connection.sendPacket(
                    new CPlayerPacket(onGround)
            );
        }
    }

    public TPState getCurrentState() {
        return currentState;
    }

    public boolean isAtTarget() {
        return currentState == TPState.AT_TARGET;
    }
}
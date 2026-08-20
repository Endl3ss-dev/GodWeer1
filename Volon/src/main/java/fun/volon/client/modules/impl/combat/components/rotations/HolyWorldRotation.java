package fun.volon.client.modules.impl.combat.components.rotations;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import fun.volon.api.storages.implement.RotationStorage;
import fun.volon.api.utils.combat.AntiCheatEvasion;
import fun.volon.api.utils.combat.RayTraceUtil;
import fun.volon.api.utils.rotate.Rotation;
import fun.volon.api.utils.rotate.RotationUtils;
import fun.volon.client.modules.impl.combat.Aura;
import fun.volon.client.modules.impl.combat.components.RotationsSystem;
import fun.volon.client.modules.impl.combat.components.gcd.GCDUtil;
import fun.volon.client.modules.impl.combat.components.interpolation.BestPoint;

public class HolyWorldRotation extends RotationsSystem {

    private final Aura aura;
    private float acceleration;
    private boolean isBack;
    private LivingEntity lastTarget;
    private float lastYaw;
    private float lastPitch;
    private boolean initialized;

    public HolyWorldRotation(Aura aura) {
        this.aura = aura;
    }

    public void reset() {
        this.acceleration = 0.0F;
        this.isBack = false;
        this.lastTarget = null;
        this.initialized = false;
        AntiCheatEvasion.INSTANCE.reset();
    }

    @Override
    public void updateRotations(LivingEntity target) {
        if (target == null || mc.player == null) return;
        if (AntiCheatEvasion.INSTANCE.handleTransitions()) return;

        if (!initialized) {
            lastYaw = mc.player.getYaw();
            lastPitch = mc.player.getPitch();
            initialized = true;
        }

        if (target != this.lastTarget) {
            this.lastTarget = target;
            this.acceleration = 0.0F;
            this.isBack = false;
        }

        Vec3d aimPoint = getAimPoint(target);
        Vec2f targetRot = RotationUtils.getRotations(aimPoint);
        Rotation angle = new Rotation(targetRot.x, targetRot.y);

        boolean hasTrace = RayTraceUtil.rayTrace(
                mc.player.getRotationVector(),
                6.0D,
                target.getBoundingBox().expand(-0.12D)
        );

        if (mc.player.isGliding()) {
            this.updateElytra(angle, hasTrace);
        } else {
            this.updateGround(angle, hasTrace, target);
        }
    }

    private void updateGround(Rotation angle, boolean hasTrace, LivingEntity target) {
        float yawDiff = Math.abs(MathHelper.wrapDegrees(angle.getYaw() - this.lastYaw));
        double distance = mc.player.distanceTo(target);

        float speedConstraint = 1.0F;
        if ((distance < 0.7D || yawDiff > 85.0F) && yawDiff > 40.0F) {
            speedConstraint = 0.5F;
        }

        if (!hasTrace) {
            float speedMul = MathHelper.clamp(yawDiff / 20.0F, 0.6F, 1.0F);
            this.acceleration += 0.0045F * speedMul;
            if (this.acceleration >= 0.16F) {
                this.isBack = true;
            }
        } else if (this.isBack) {
            this.acceleration -= 0.0095F;
            if (this.acceleration <= 0.0F) {
                this.isBack = false;
                this.acceleration = 0.0F;
            }
        } else {
            this.acceleration = MathHelper.clamp(this.acceleration - 0.0065F, 0.08F, 0.16F);
        }

        float smoothFactor = MathHelper.clamp(this.acceleration, 0.14F, 0.8F);
        if (hasTrace) {
            smoothFactor = MathHelper.clamp(smoothFactor * 0.48F, 0.1F, 0.38F);
        }

        if (mc.player.hurtTime > 0) {
            smoothFactor *= 0.75F;
        }

        boolean strafing = Math.abs(mc.player.input.movementSideways) > 0.08F;
        if (!hasTrace && strafing) {
            smoothFactor = MathHelper.clamp(smoothFactor * 1.3F, smoothFactor, 0.58F);
        }

        smoothFactor *= speedConstraint;

        float deltaYaw = MathHelper.wrapDegrees(angle.getYaw() - this.lastYaw);
        float rawPitchDelta = angle.getPitch() - this.lastPitch;

        float pitchStep = 0.68F;
        float pitchSteps = (float) Math.floor(Math.abs(rawPitchDelta) / pitchStep);
        float pitchMove = pitchSteps * pitchStep * Math.signum(rawPitchDelta);

        float newYaw = this.lastYaw + deltaYaw * smoothFactor;
        float newPitch = this.lastPitch + pitchMove * smoothFactor;

        if (!mc.player.isOnGround()) {
            newPitch = this.lastPitch + (newPitch - this.lastPitch) * 0.45F;
        }

        float gcd = GCDUtil.getGCDValue();
        newYaw -= (newYaw - this.lastYaw) % gcd;
        newPitch -= (newPitch - this.lastPitch) % gcd;
        newPitch = MathHelper.clamp(newPitch, -89.0F, 89.0F);

        this.applyRotation(newYaw, newPitch, hasTrace, yawDiff, speedConstraint);
    }

    private void updateElytra(Rotation angle, boolean hasTrace) {
        float yawDiff = Math.abs(MathHelper.wrapDegrees(angle.getYaw() - this.lastYaw));

        if (!hasTrace) {
            this.acceleration += yawDiff > 45.0F ? 0.003F : 0.0055F;
        } else if (this.isBack) {
            this.acceleration -= yawDiff > 45.0F ? 0.035F : 0.009F;
            if (this.acceleration <= 0.0F) {
                this.isBack = false;
                this.acceleration = 0.0F;
            }
        } else {
            this.acceleration = MathHelper.clamp(this.acceleration - 0.005F, 0.06F, 0.2F);
            if (this.acceleration >= 0.18F) {
                this.isBack = true;
            }
        }

        float smoothFactor = MathHelper.clamp(this.acceleration, 0.1F, 0.85F);
        if (hasTrace) {
            smoothFactor *= 0.55F;
        }

        float deltaYaw = MathHelper.wrapDegrees(angle.getYaw() - this.lastYaw);
        float deltaPitch = angle.getPitch() - this.lastPitch;

        float newYaw = this.lastYaw + deltaYaw * smoothFactor;
        float newPitch = this.lastPitch + deltaPitch * (smoothFactor * 0.55F);

        float gcd = GCDUtil.getGCDValue();
        newYaw -= (newYaw - this.lastYaw) % gcd;
        newPitch -= (newPitch - this.lastPitch) % gcd;
        newPitch = MathHelper.clamp(newPitch, -89.0F, 89.0F);

        float rotSpeed = hasTrace ? 55.0F : MathHelper.clamp(75.0F + yawDiff * 0.4F, 75.0F, 120.0F);
        this.sendRotation(newYaw, newPitch, rotSpeed, rotSpeed);
    }

    private void applyRotation(float yaw, float pitch, boolean hasTrace, float yawDiff, float speedConstraint) {
        float rotSpeed = hasTrace
                ? 52.0F
                : MathHelper.clamp(68.0F + yawDiff * 0.5F, 68.0F, 108.0F);

        if (speedConstraint < 1.0F) {
            rotSpeed *= 0.7F;
        }

        this.sendRotation(yaw, pitch, rotSpeed, rotSpeed);
    }

    private void sendRotation(float yaw, float pitch, float rotSpeed, float pitchSpeed) {
        float[] out = AntiCheatEvasion.INSTANCE.process(yaw, pitch, lastTarget, "holyworld");
        RotationStorage.update(
                new Rotation(out[0], out[1]),
                rotSpeed, pitchSpeed, rotSpeed, pitchSpeed,
                0, 1,
                Aura.clientLook.isState()
        );
        this.lastYaw = out[0];
        this.lastPitch = out[1];
        rotate = new Vec2f(out[0], out[1]);
    }

    private Vec3d getAimPoint(LivingEntity target) {
        Vec3d point = BestPoint.getMultipoint(target, 128.0);
        if (point == null) {
            point = target.getBoundingBox().getCenter();
        }
        if (shouldUseElytraPredict(target)) {
            return getPredictedPoint(target, point);
        }
        return point;
    }
}

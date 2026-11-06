/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1297
 *  net.minecraft.class_1299
 *  net.minecraft.class_1313
 *  net.minecraft.class_1922
 *  net.minecraft.class_1937
 *  net.minecraft.class_2338
 *  net.minecraft.class_2338$class_2339
 *  net.minecraft.class_2350$class_2351
 *  net.minecraft.class_243
 *  net.minecraft.class_265
 *  net.minecraft.class_2680
 *  net.minecraft.class_2960
 *  net.minecraft.class_3414
 *  net.minecraft.class_3532
 *  net.minecraft.class_3619
 *  net.minecraft.class_4048
 *  net.minecraft.class_4050
 *  net.minecraft.class_7102
 *  software.bernie.geckolib.animatable.GeoAnimatable
 *  software.bernie.geckolib.animatable.GeoEntity
 *  software.bernie.geckolib.animatable.instance.AnimatableInstanceCache
 *  software.bernie.geckolib.animatable.manager.AnimatableManager$ControllerRegistrar
 *  software.bernie.geckolib.animation.AnimationController
 *  software.bernie.geckolib.animation.RawAnimation
 *  software.bernie.geckolib.animation.object.PlayState
 *  software.bernie.geckolib.animation.state.AnimationTest
 *  software.bernie.geckolib.util.GeckoLibUtil
 */
package ru.decide.module.impl.render.custompet.entity;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1313;
import net.minecraft.class_1922;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_243;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_2960;
import net.minecraft.class_3414;
import net.minecraft.class_3532;
import net.minecraft.class_3619;
import net.minecraft.class_4048;
import net.minecraft.class_4050;
import net.minecraft.class_7102;
import ru.decide.module.impl.render.custompet.CustomPetVariant;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.object.PlayState;
import software.bernie.geckolib.animation.state.AnimationTest;
import software.bernie.geckolib.util.GeckoLibUtil;

@Environment(value=EnvType.CLIENT)
public class CustomPetEntity
extends class_7102
implements GeoEntity {
    private static final AtomicInteger NEXT_ENTITY_ID = new AtomicInteger(-20000);
    private static final class_4048 ZERO_DIMENSIONS = class_4048.method_18385((float)0.01f, (float)0.01f);
    private static final float GROUND_FULL_SPEED_ANGLE = 6.0f;
    private static final float GROUND_MIN_SPEED_ANGLE = 84.0f;
    private static final float AIR_START_MOVE_ANGLE = 9.0f;
    private static final double MOVEMENT_ANIMATION_THRESHOLD_SQR = 2.5E-5;
    private static final double MOVEMENT_VERTICAL_THRESHOLD = 0.003;
    private static class_3414 ambientSound;
    private static class_3414 stepSound;
    private static boolean soundsResolved;
    private static final RawAnimation IDLE;
    private static final RawAnimation WALK;
    private static final RawAnimation RAIN_IDLE;
    private static final RawAnimation RAIN_WALK;
    private static final RawAnimation HAT_IDLE;
    private static final RawAnimation HAT_WALK;
    private static final RawAnimation FISHERMAN_IDLE;
    private static final RawAnimation FISHERMAN_WALK;
    private static final RawAnimation AIR_IDLE;
    private static final RawAnimation AIR_WALK;
    private static final RawAnimation ROBOT_IDLE;
    private static final RawAnimation ROBOT_FLY;
    private static final RawAnimation OWL_IDLE;
    private static final RawAnimation OWL_WALK;
    private static final RawAnimation OWL_RUN;
    private static final RawAnimation OWL_FLY;
    private static final double OWL_RUN_ANIMATION_SPEED = 1.4;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache((GeoAnimatable)this);
    private RawAnimation cachedAnimation;
    private class_243 desiredPosition = class_243.field_1353;
    private double desiredSpeed;
    private boolean desiredMoving;
    private boolean desiredUmbrella;
    private boolean desiredAirborne;
    private CustomPetVariant variant = CustomPetVariant.NITWIT;
    private boolean owl;
    private int robotType;
    private boolean moving;
    private boolean usingUmbrella;
    private boolean airborneMode;
    private int movingTicks;
    private int ambientSoundCooldown;
    private int stepSoundCooldown;
    private int jumpCooldown;
    private int blockedTicks;
    private int pauseTicks;
    private int sideStepDir;
    private int sideStepTicks;
    private float targetYaw;
    private float smoothedYaw;
    private float lookOverrideYaw;
    private float lookOverrideWeight;
    private double animationSpeed = 1.0;
    private double currentAnimationSpeed = 1.0;
    private double currentGroundSpeed;
    private double verticalVelocity;
    private double lastHorizontalDistance = Double.MAX_VALUE;
    private class_243 airMotion = class_243.field_1353;
    private int animationTick;

    public CustomPetEntity(class_1937 world) {
        super(class_1299.field_37419, world);
        this.field_5960 = false;
        this.method_5875(false);
        this.method_5977(true);
        this.method_5838(NEXT_ENTITY_ID.getAndDecrement());
        this.method_5826(UUID.randomUUID());
        this.method_18799(class_243.field_1353);
        this.targetYaw = this.method_36454();
        this.smoothedYaw = this.method_36454();
        this.ambientSoundCooldown = 70;
        this.verticalVelocity = 0.0;
    }

    private <E extends GeoAnimatable> PlayState predicate(AnimationTest<E> animationTest) {
        RawAnimation rawAnimation;
        AnimationController animationController = animationTest.controller();
        animationController.setAnimationSpeed(this.currentAnimationSpeed);
        if (this.owl) {
            rawAnimation = this.airborneMode ? OWL_FLY : (!this.moving ? OWL_IDLE : (this.currentAnimationSpeed >= 1.4 ? OWL_RUN : OWL_WALK));
        } else if (this.variant.isRobot()) {
            rawAnimation = !this.moving && !this.airborneMode ? ROBOT_IDLE : ROBOT_FLY;
        } else if (this.airborneMode) {
            rawAnimation = AIR_IDLE;
        } else {
            switch (this.variant) {
                default: {
                    throw new MatchException(null, null);
                }
                case FISHERMAN: {
                    RawAnimation rawAnimation2;
                    if (this.usingUmbrella) {
                        if (this.moving) {
                            rawAnimation2 = FISHERMAN_WALK;
                            break;
                        }
                        rawAnimation2 = FISHERMAN_IDLE;
                        break;
                    }
                    if (this.moving) {
                        rawAnimation2 = AIR_WALK;
                        break;
                    }
                    rawAnimation2 = AIR_IDLE;
                    break;
                }
                case GARDENER: 
                case SORCERER: {
                    RawAnimation rawAnimation2;
                    if (this.usingUmbrella) {
                        if (this.moving) {
                            rawAnimation2 = HAT_WALK;
                            break;
                        }
                        rawAnimation2 = HAT_IDLE;
                        break;
                    }
                    if (this.moving) {
                        rawAnimation2 = WALK;
                        break;
                    }
                    rawAnimation2 = IDLE;
                    break;
                }
                case DEFAULT: 
                case NITWIT: 
                case MERCHANT: {
                    RawAnimation rawAnimation2;
                    if (this.usingUmbrella) {
                        if (this.moving) {
                            rawAnimation2 = RAIN_WALK;
                            break;
                        }
                        rawAnimation2 = RAIN_IDLE;
                        break;
                    }
                    if (this.moving) {
                        rawAnimation2 = WALK;
                        break;
                    }
                    rawAnimation2 = IDLE;
                    break;
                }
                case ROBOT: {
                    RawAnimation rawAnimation2 = rawAnimation = this.moving ? ROBOT_FLY : ROBOT_IDLE;
                }
            }
        }
        if (rawAnimation != this.cachedAnimation) {
            this.cachedAnimation = rawAnimation;
            animationController.setAnimation(rawAnimation);
        }
        return PlayState.CONTINUE;
    }

    private double square(double value) {
        return value * value;
    }

    public boolean isOwl() {
        return this.owl;
    }

    public void setOwl(boolean owl) {
        this.owl = owl;
    }

    private static class_3414 stepSound() {
        CustomPetEntity.resolveSounds();
        return stepSound;
    }

    public void snapTo(class_243 position, float yaw) {
        this.method_23327(position.field_1352, position.field_1351, position.field_1350);
        this.field_6014 = position.field_1352;
        this.field_6036 = position.field_1351;
        this.field_5969 = position.field_1350;
        this.field_5982 = yaw;
        this.field_6004 = 0.0f;
        this.moving = false;
        this.usingUmbrella = false;
        this.airborneMode = false;
        this.movingTicks = 0;
        this.stepSoundCooldown = 0;
        this.ambientSoundCooldown = 40;
        this.jumpCooldown = 0;
        this.blockedTicks = 0;
        this.pauseTicks = 0;
        this.sideStepTicks = 0;
        this.targetYaw = yaw;
        this.smoothedYaw = yaw;
        this.animationSpeed = 1.0;
        this.currentAnimationSpeed = 1.0;
        this.currentGroundSpeed = 0.0;
        this.verticalVelocity = 0.0;
        this.lastHorizontalDistance = Double.MAX_VALUE;
        this.airMotion = class_243.field_1353;
        this.method_18799(class_243.field_1353);
        this.method_36456(yaw);
        this.method_5847(yaw);
        this.method_5636(yaw);
        this.lookOverrideYaw = 0.0f;
        this.lookOverrideWeight = 0.0f;
        this.cachedAnimation = null;
    }

    protected float method_53964() {
        return 0.0f;
    }

    public void method_5773() {
        ++this.animationTick;
        this.method_5875(this.desiredAirborne);
        super.method_5773();
        this.tickCustomMovement();
    }

    public float method_49476() {
        return this.airborneMode ? 0.0f : 1.0f;
    }

    protected class_4048 method_55694(class_4050 pose) {
        return ZERO_DIMENSIONS;
    }

    public boolean method_5640(double distance) {
        return true;
    }

    public boolean method_73187() {
        return false;
    }

    public float method_5705(float tickDelta) {
        return class_3532.method_17821((float)tickDelta, (float)this.field_5982, (float)this.method_36454());
    }

    public boolean method_5810() {
        return false;
    }

    public void method_5697(class_1297 entity) {
    }

    public boolean method_30948(class_1297 entity) {
        return false;
    }

    public boolean method_5732() {
        return false;
    }

    public class_3619 method_5657() {
        return class_3619.field_15975;
    }

    public boolean isPetMoving() {
        return this.moving;
    }

    public int getAnimationAge() {
        return this.animationTick;
    }

    public double getGroundSurfaceY(double x, double y, double z) {
        class_1937 world = this.method_73183();
        if (world == null) {
            return y;
        }
        int floorX = class_3532.method_15357((double)x);
        int floorZ = class_3532.method_15357((double)z);
        int floorY = class_3532.method_15357((double)y);
        class_2338.class_2339 mutable = new class_2338.class_2339(floorX, floorY + 1, floorZ);
        for (int i = floorY + 1; i >= floorY - 5; --i) {
            mutable.method_10103(floorX, i, floorZ);
            class_2680 blockState = world.method_8320((class_2338)mutable);
            class_265 voxelShape = blockState.method_26220((class_1922)world, (class_2338)mutable);
            if (voxelShape.method_1110()) continue;
            return (double)i + voxelShape.method_1105(class_2350.class_2351.field_11052);
        }
        return y;
    }

    public double getCurrentAnimationSpeed() {
        return this.currentAnimationSpeed;
    }

    public void setRobotType(int type) {
        this.robotType = type;
    }

    public boolean shouldUseUmbrella() {
        return this.usingUmbrella;
    }

    public boolean isAirborneMode() {
        return this.airborneMode;
    }

    public void setPetVariant(CustomPetVariant variant) {
        this.variant = variant == null ? CustomPetVariant.NITWIT : variant;
    }

    private static class_3414 ambientSound() {
        CustomPetEntity.resolveSounds();
        return ambientSound;
    }

    public int getRobotType() {
        return this.robotType;
    }

    public boolean isMovementBlocked() {
        return this.pauseTicks > 0 || this.blockedTicks >= 3;
    }

    private void tickClientAudio(boolean moving, double distanceSqr) {
        class_1937 world = this.method_73183();
        if (world != null && !this.owl && !this.variant.isRobot()) {
            class_3414 sound;
            if (this.ambientSoundCooldown > 0) {
                --this.ambientSoundCooldown;
            }
            if (this.stepSoundCooldown > 0) {
                --this.stepSoundCooldown;
            }
            if (moving && distanceSqr > 4.0E-4 && this.method_24828() && this.stepSoundCooldown <= 0) {
                sound = CustomPetEntity.stepSound();
                if (sound != null) {
                    world.method_8486(this.method_23317(), this.method_23318(), this.method_23321(), sound, this.method_5634(), 0.55f, 0.94f + this.field_5974.method_43057() * 0.12f, false);
                }
                this.stepSoundCooldown = 8 + this.field_5974.method_43048(4);
            }
            if (!moving && this.ambientSoundCooldown <= 0) {
                sound = CustomPetEntity.ambientSound();
                if (sound != null) {
                    world.method_8486(this.method_23317(), this.method_23318(), this.method_23321(), sound, this.method_5634(), 0.75f, 0.94f + this.field_5974.method_43057() * 0.14f, false);
                }
                this.ambientSoundCooldown = 120 + this.field_5974.method_43048(100);
            }
        }
    }

    public CustomPetVariant getPetVariant() {
        return this.variant;
    }

    private void snapToGroundIfClose() {
        class_1937 world = this.method_73183();
        if (world != null) {
            double x = this.method_23317();
            double y = this.method_23318();
            double z = this.method_23321();
            int floorX = class_3532.method_15357((double)x);
            int floorZ = class_3532.method_15357((double)z);
            int floorY = class_3532.method_15357((double)(y + 0.2));
            class_2338.class_2339 mutable = new class_2338.class_2339(floorX, floorY + 1, floorZ);
            for (int i = floorY + 1; i >= floorY - 4; --i) {
                double surfaceY;
                double delta;
                mutable.method_10103(floorX, i, floorZ);
                class_2680 blockState = world.method_8320((class_2338)mutable);
                class_265 voxelShape = blockState.method_26220((class_1922)world, (class_2338)mutable);
                if (voxelShape.method_1110() || !((delta = y - (surfaceY = (double)i + voxelShape.method_1105(class_2350.class_2351.field_11052))) >= -0.45) || !(delta <= 0.72)) continue;
                this.method_23327(x, surfaceY, z);
                this.verticalVelocity = 0.0;
                this.field_6017 = 0.0;
                this.method_18799(class_243.field_1353);
                return;
            }
        }
    }

    private static void resolveSounds() {
        if (!soundsResolved) {
            soundsResolved = true;
            try {
                ambientSound = class_3414.method_47908((class_2960)class_2960.method_60655((String)"decide", (String)"entity.ribbit.ambient"));
                stepSound = class_3414.method_47908((class_2960)class_2960.method_60655((String)"decide", (String)"entity.ribbit.step"));
            }
            catch (Throwable throwable) {
                ambientSound = null;
                stepSound = null;
            }
        }
    }

    public void setBehavior(class_243 position, double speed, boolean moving, boolean umbrella, boolean airborne, double animationSpeed) {
        if (position.method_1025(this.desiredPosition) > 0.2) {
            this.blockedTicks = 0;
            this.pauseTicks = 0;
            this.lastHorizontalDistance = Double.MAX_VALUE;
        }
        this.desiredPosition = position;
        this.desiredSpeed = speed;
        this.desiredMoving = moving;
        this.desiredUmbrella = umbrella;
        this.desiredAirborne = airborne;
        this.animationSpeed = animationSpeed;
    }

    private void tickAirMovement() {
        boolean verticalOnly;
        boolean wantsMove;
        class_243 startPos = this.method_73189();
        class_243 toTarget = this.desiredPosition.method_1020(startPos);
        class_243 horizontal = new class_243(toTarget.field_1352, 0.0, toTarget.field_1350);
        double horizontalDistance = horizontal.method_37267();
        double targetDistance = toTarget.method_1033();
        double verticalDelta = Math.abs(toTarget.field_1351);
        boolean bl = wantsMove = this.desiredMoving && targetDistance > 0.02;
        if (horizontalDistance > 1.0E-4) {
            float targetYaw = (float)Math.toDegrees(class_3532.method_15349((double)horizontal.field_1350, (double)horizontal.field_1352)) - 90.0f;
            this.targetYaw = class_3532.method_15388((float)this.targetYaw, (float)targetYaw, (float)10.5f);
        }
        if (this.lookOverrideWeight > 0.001f) {
            float weight = wantsMove ? 0.15f : 1.0f;
            float step = 4.5f * this.lookOverrideWeight * weight;
            this.targetYaw = class_3532.method_15388((float)this.targetYaw, (float)this.lookOverrideYaw, (float)step);
        }
        this.smoothedYaw = class_3532.method_15388((float)this.smoothedYaw, (float)this.targetYaw, (float)(wantsMove ? 14.0f : 6.0f));
        float yawDelta = Math.abs(class_3532.method_15393((float)(this.targetYaw - this.smoothedYaw)));
        boolean movingForward = wantsMove && (yawDelta <= 9.0f || targetDistance <= 0.35);
        this.movingTicks = 0;
        this.jumpCooldown = 0;
        this.blockedTicks = 0;
        this.pauseTicks = 0;
        this.verticalVelocity = 0.0;
        boolean hoverIdle = horizontalDistance <= 0.16 && verticalDelta <= 0.012;
        boolean hoverApproach = horizontalDistance <= 0.42 && verticalDelta <= 0.09;
        class_243 motion = class_243.field_1353;
        if (movingForward && targetDistance > 1.0E-5 && !hoverIdle) {
            double speedMultiplier = class_3532.method_15350((double)((targetDistance - 0.2) / 1.6), (double)0.08, (double)1.0);
            double speed = Math.clamp(this.desiredSpeed * speedMultiplier, 0.012, 6.5);
            motion = toTarget.method_1029().method_1021(Math.min(targetDistance, speed));
        } else if (!this.desiredMoving && verticalDelta > 0.001) {
            double speed = class_3532.method_15350((double)(verticalDelta * 0.38), (double)0.0025, (double)0.018);
            motion = new class_243(0.0, Math.copySign(Math.min(verticalDelta, speed), toTarget.field_1351), 0.0);
        }
        double lerpFactor = Math.clamp(0.12 + targetDistance * 0.06 + this.desiredSpeed * 0.028, 0.12, 0.38);
        this.airMotion = this.airMotion.method_35590(motion, lerpFactor);
        if (!this.desiredMoving) {
            this.airMotion = new class_243(this.airMotion.field_1352 * 0.4, this.airMotion.field_1351, this.airMotion.field_1350 * 0.4);
        }
        double damp = hoverIdle ? 0.18 : (hoverApproach ? 0.42 : (targetDistance < 1.5 ? 0.78 : 0.9));
        boolean bl2 = verticalOnly = !this.desiredMoving && verticalDelta > 0.001;
        if (!(movingForward && !(motion.method_1027() < 1.0E-6) || verticalOnly)) {
            this.airMotion = this.airMotion.method_1021(damp);
        } else if (verticalOnly) {
            this.airMotion = new class_243(this.airMotion.field_1352, this.airMotion.field_1351 * 0.96, this.airMotion.field_1350);
        }
        if (hoverIdle && verticalDelta <= 0.0015 || this.airMotion.method_1027() < 4.0E-5) {
            this.airMotion = class_243.field_1353;
        }
        class_243 movement = this.airMotion.method_1027() > 0.0 ? (this.airMotion.method_1033() > targetDistance ? toTarget : this.airMotion) : class_243.field_1353;
        this.method_5784(class_1313.field_6308, movement);
        if (this.field_5976 && this.desiredMoving) {
            this.method_5784(class_1313.field_6308, new class_243(0.0, 0.12, 0.0));
        }
        class_243 newPos = this.method_73189();
        double movedSquared = this.square(newPos.field_1352 - startPos.field_1352) + this.square(newPos.field_1350 - startPos.field_1350);
        this.moving = wantsMove && (movedSquared > 2.5E-5 || Math.abs(newPos.field_1351 - startPos.field_1351) > 0.003);
        this.lastHorizontalDistance = wantsMove ? horizontalDistance : Double.MAX_VALUE;
        double targetAnimSpeed = this.moving ? Math.max(1.0, this.animationSpeed) : 1.0;
        this.currentAnimationSpeed += (targetAnimSpeed - this.currentAnimationSpeed) * (this.moving ? 0.2 : 0.16);
        this.method_18799(class_243.field_1353);
        this.method_36456(this.smoothedYaw);
        this.method_5847(this.smoothedYaw);
        this.method_5636(this.smoothedYaw);
        this.tickClientAudio(false, 0.0);
    }

    public void overrideLookYaw(float yaw, float weight) {
        this.lookOverrideYaw = yaw;
        this.lookOverrideWeight = Math.max(0.0f, Math.min(1.0f, weight));
    }

    private void tickCustomMovement() {
        boolean wasAirborne = this.airborneMode;
        this.airborneMode = this.desiredAirborne;
        this.usingUmbrella = this.desiredUmbrella && !this.airborneMode && !this.variant.isRobot();
        this.method_5875(this.airborneMode);
        if (!(this.airborneMode || !wasAirborne && this.desiredMoving)) {
            this.snapToGroundIfClose();
        }
        if (this.jumpCooldown > 0) {
            --this.jumpCooldown;
        }
        if (this.pauseTicks > 0) {
            --this.pauseTicks;
            this.moving = false;
            this.movingTicks = 0;
            this.currentGroundSpeed = 0.0;
            this.verticalVelocity = 0.0;
            this.airMotion = class_243.field_1353;
            this.method_18799(class_243.field_1353);
            this.currentAnimationSpeed += (1.0 - this.currentAnimationSpeed) * 0.28;
            this.field_5982 = this.smoothedYaw;
            this.method_36456(this.smoothedYaw);
            this.method_5847(this.smoothedYaw);
            this.method_5636(this.smoothedYaw);
            this.tickClientAudio(false, 0.0);
        } else if (this.airborneMode) {
            this.tickAirMovement();
        } else {
            boolean makingProgress;
            boolean hasMoved;
            class_243 motion;
            boolean groundMoving;
            boolean wantsMove;
            this.airMotion = class_243.field_1353;
            class_243 startPos = this.method_73189();
            class_243 toTarget = this.desiredPosition.method_1020(startPos);
            class_243 horizontal = new class_243(toTarget.field_1352, 0.0, toTarget.field_1350);
            double horizontalDistance = horizontal.method_37267();
            boolean bl = wantsMove = this.desiredMoving && horizontalDistance > 0.045;
            if (wantsMove) {
                float targetYaw = (float)Math.toDegrees(class_3532.method_15349((double)horizontal.field_1350, (double)horizontal.field_1352)) - 90.0f;
                float turnRate = (float)Math.clamp(9.5 + horizontalDistance * 2.4 + this.desiredSpeed * 68.0, 9.5, 24.0);
                this.targetYaw = class_3532.method_15388((float)this.targetYaw, (float)targetYaw, (float)turnRate);
            } else if (this.lookOverrideWeight > 0.001f) {
                this.targetYaw = class_3532.method_15388((float)this.targetYaw, (float)this.lookOverrideYaw, (float)(6.0f + 6.0f * this.lookOverrideWeight));
            }
            float yawTurnRate = wantsMove ? (float)Math.clamp(8.5 + horizontalDistance * 1.8 + this.desiredSpeed * 86.0, 9.0, 22.0) : 4.8f;
            this.smoothedYaw = class_3532.method_15388((float)this.smoothedYaw, (float)this.targetYaw, (float)yawTurnRate);
            float yawDelta = Math.abs(class_3532.method_15393((float)(this.targetYaw - this.smoothedYaw)));
            double speedMultiplier = wantsMove ? class_3532.method_15350((double)(1.0 - Math.max(0.0, (double)(yawDelta - 6.0f)) / 78.0), (double)0.18, (double)1.0) : 0.0;
            double distanceMultiplier = wantsMove ? class_3532.method_15350((double)((horizontalDistance - 0.04) / 1.6), (double)0.34, (double)1.0) : 0.0;
            double desiredGroundSpeed = wantsMove ? Math.min(horizontalDistance, this.desiredSpeed * distanceMultiplier * speedMultiplier) : 0.0;
            double lerpFactor = wantsMove ? Math.clamp(0.18 + this.desiredSpeed * 0.95 + horizontalDistance * 0.06, 0.18, 0.52) : 0.24;
            this.currentGroundSpeed += (desiredGroundSpeed - this.currentGroundSpeed) * lerpFactor;
            if (Math.abs(this.currentGroundSpeed) < 1.0E-4) {
                this.currentGroundSpeed = 0.0;
            }
            boolean bl2 = groundMoving = this.currentGroundSpeed > 0.012;
            this.moving = wantsMove ? groundMoving || horizontalDistance > 0.18 : groundMoving;
            class_243 class_2432 = motion = this.currentGroundSpeed > 1.0E-5 && horizontalDistance > 1.0E-5 ? horizontal.method_1029().method_1021(Math.min(horizontalDistance, this.currentGroundSpeed)) : class_243.field_1353;
            if (this.field_5976 && wantsMove && this.blockedTicks >= 1 && horizontalDistance > 0.25 && this.currentGroundSpeed > 0.02) {
                if (this.sideStepTicks <= 0) {
                    this.sideStepDir = this.field_5974.method_43056() ? 1 : -1;
                    this.sideStepTicks = 12;
                }
                class_243 facing = horizontal.method_1027() > 1.0E-6 ? horizontal.method_1029() : new class_243(0.0, 0.0, 1.0);
                class_243 side = new class_243(-facing.field_1350 * (double)this.sideStepDir, 0.0, facing.field_1352 * (double)this.sideStepDir);
                class_243 slideMotion = facing.method_1021(0.45).method_1019(side.method_1021(0.9));
                if (slideMotion.method_1027() > 1.0E-6) {
                    double speed = Math.max(this.currentGroundSpeed, this.desiredSpeed * 0.7);
                    motion = slideMotion.method_1029().method_1021(Math.min(horizontalDistance, speed));
                }
            }
            if (this.sideStepTicks > 0) {
                --this.sideStepTicks;
            }
            double targetHeightDelta = this.desiredPosition.field_1351 - this.method_23318();
            if (this.method_24828()) {
                if (targetHeightDelta > 1.15 && horizontalDistance <= 1.6 && wantsMove && this.currentGroundSpeed > 0.035 && this.jumpCooldown <= 0 && this.blockedTicks < 3) {
                    this.verticalVelocity = 0.42;
                    this.jumpCooldown = 11;
                } else {
                    this.verticalVelocity = targetHeightDelta < -0.35 ? Math.max(-0.16, targetHeightDelta) : 0.0;
                }
            } else {
                this.verticalVelocity = Math.max(this.verticalVelocity - 0.08, -0.36);
            }
            this.method_5784(class_1313.field_6308, new class_243(motion.field_1352, this.verticalVelocity, motion.field_1350));
            class_243 newPos = this.method_73189();
            double movedSquared = this.square(newPos.field_1352 - startPos.field_1352) + this.square(newPos.field_1350 - startPos.field_1350);
            boolean bl3 = hasMoved = movedSquared > 2.5E-5 || Math.abs(newPos.field_1351 - startPos.field_1351) > 0.003;
            if (this.method_24828() && this.verticalVelocity < 0.0) {
                this.verticalVelocity = 0.0;
            }
            if (this.method_24828() && this.verticalVelocity <= 0.0 && this.desiredPosition.field_1351 <= this.method_23318() && Math.abs(this.desiredPosition.field_1351 - this.method_23318()) < 0.18) {
                this.method_23327(this.method_23317(), this.desiredPosition.field_1351, this.method_23321());
            }
            this.moving = wantsMove && hasMoved;
            class_243 remaining = this.desiredPosition.method_1020(this.method_73189());
            double remainingDistance = Math.hypot(remaining.field_1352, remaining.field_1350);
            boolean bl4 = makingProgress = remainingDistance + 0.025 < this.lastHorizontalDistance;
            if (this.currentGroundSpeed > 0.02 && this.field_5976 && this.method_24828()) {
                ++this.blockedTicks;
            } else if (!wantsMove || makingProgress || remainingDistance < 0.8) {
                this.blockedTicks = 0;
            }
            if (this.blockedTicks >= 7) {
                this.pauseTicks = 8;
                this.blockedTicks = 0;
                this.sideStepTicks = 0;
                this.moving = false;
                this.movingTicks = 0;
                this.currentGroundSpeed = 0.0;
                this.verticalVelocity = 0.0;
            }
            this.lastHorizontalDistance = wantsMove ? remainingDistance : Double.MAX_VALUE;
            double targetAnimSpeed = this.moving ? Math.max(1.0, this.animationSpeed) : 1.0;
            this.currentAnimationSpeed += (targetAnimSpeed - this.currentAnimationSpeed) * (this.moving ? 0.22 : 0.18);
            this.method_18799(class_243.field_1353);
            this.method_36456(this.smoothedYaw);
            this.method_5847(this.smoothedYaw);
            this.method_5636(this.smoothedYaw);
            this.tickClientAudio(this.moving, horizontal.method_37268());
        }
    }

    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController("controller", 5, this::predicate));
    }

    static {
        IDLE = RawAnimation.begin().thenPlay("idle");
        WALK = RawAnimation.begin().thenPlay("walk");
        RAIN_IDLE = RawAnimation.begin().thenPlay("idle_holding_1");
        RAIN_WALK = RawAnimation.begin().thenPlay("walk_holding_1");
        HAT_IDLE = RawAnimation.begin().thenPlay("idle_holding_hat");
        HAT_WALK = RawAnimation.begin().thenPlay("walk_holding_hat");
        FISHERMAN_IDLE = RawAnimation.begin().thenPlay("idle_holding_fisherman");
        FISHERMAN_WALK = RawAnimation.begin().thenPlay("walk_holding_fisherman");
        AIR_IDLE = RawAnimation.begin().thenPlay("idle_holding_2");
        AIR_WALK = RawAnimation.begin().thenPlay("walk_holding_2");
        ROBOT_IDLE = RawAnimation.begin().thenPlay("robot_idle");
        ROBOT_FLY = RawAnimation.begin().thenPlay("robot_fly");
        OWL_IDLE = RawAnimation.begin().thenPlay("animation.owl_jump_rope.skip_idle");
        OWL_WALK = RawAnimation.begin().thenPlay("animation.owl_jump_rope.walk_skip");
        OWL_RUN = RawAnimation.begin().thenPlay("animation.owl_jump_rope.run_skip");
        OWL_FLY = RawAnimation.begin().thenPlay("animation.owl_jump_rope.fly");
    }
}


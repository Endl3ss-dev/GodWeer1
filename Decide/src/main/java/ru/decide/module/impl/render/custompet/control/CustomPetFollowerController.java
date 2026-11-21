/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1293
 *  net.minecraft.class_1294
 *  net.minecraft.class_1657
 *  net.minecraft.class_1922
 *  net.minecraft.class_1937
 *  net.minecraft.class_2338
 *  net.minecraft.class_2338$class_2339
 *  net.minecraft.class_2350$class_2351
 *  net.minecraft.class_243
 *  net.minecraft.class_265
 *  net.minecraft.class_2680
 *  net.minecraft.class_310
 *  net.minecraft.class_3532
 */
package ru.decide.module.impl.render.custompet.control;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1293;
import net.minecraft.class_1294;
import net.minecraft.class_1657;
import net.minecraft.class_1922;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_243;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import ru.decide.module.impl.render.custompet.CustomPetVariant;
import ru.decide.module.impl.render.custompet.entity.CustomPetEntity;

@Environment(value=EnvType.CLIENT)
public final class CustomPetFollowerController {
    private static final double MAX_WANDER_DISTANCE = 3.0;
    private static final double SNAP_BACK_DISTANCE = 8.0;
    private static final double FOLLOW_BREAK_DISTANCE = 7.0;
    private static final double FOLLOW_NEAR_DISTANCE = 3.0;
    private static final double TARGET_REACHED_DISTANCE = 0.9;
    private static final double PLAYER_PERSONAL_SPACE = 0.65;
    private static final double OWL_HOVER_RADIUS_MIN = 1.6;
    private static final double OWL_HOVER_RADIUS_MAX = 2.8;
    private static final double OWL_HOVER_HEIGHT_MIN = 1.6;
    private static final double OWL_HOVER_HEIGHT_MAX = 3.2;
    private static final int OWL_FLIGHT_COOLDOWN_MIN = 300;
    private static final int OWL_FLIGHT_COOLDOWN_MAX = 640;
    private static final int OWL_FLIGHT_DURATION_MIN = 90;
    private static final int OWL_FLIGHT_DURATION_MAX = 190;
    private final class_310 mc = class_310.method_1551();
    private final Random random = new Random();
    private CustomPetEntity pet;
    private boolean petIsOwl;
    private class_243 roamTarget;
    private class_243 airHoverOffset;
    private class_243 airSmoothedTarget;
    private class_243 airFlightDirection;
    private boolean airChasing;
    private int idleTicks;
    private int followRefreshTicks;
    private int followSide = 1;
    private int ticks;
    private int flightTicksRemaining;
    private int nextFlightAtTick;
    private int gazeBuildTicks;
    private int gazeLookActiveTicks;
    private int gazeCooldownTicks;

    public void reset() {
        this.pet = null;
        this.roamTarget = null;
        this.airHoverOffset = null;
        this.airSmoothedTarget = null;
        this.airFlightDirection = null;
        this.airChasing = false;
        this.idleTicks = 0;
        this.followRefreshTicks = 0;
        this.followSide = 1;
        this.flightTicksRemaining = 0;
    }

    private void ensurePet(class_1657 playerEntity, CustomPetVariant customPetVariant, boolean bl) {
        if (this.pet != null && this.pet.method_73183() == this.mc.field_1687 && !this.pet.method_31481()) {
            this.pet.setPetVariant(customPetVariant);
            this.pet.setOwl(bl);
            this.petIsOwl = bl;
        } else {
            this.reset();
            this.petIsOwl = bl;
            this.pet = new CustomPetEntity((class_1937)this.mc.field_1687);
            this.pet.setPetVariant(customPetVariant);
            this.pet.setOwl(bl);
            this.nextFlightAtTick = this.ticks + this.randomBetween(300, 640);
            this.roamTarget = this.pickRoamTarget(playerEntity, true);
            this.airHoverOffset = null;
            this.airSmoothedTarget = null;
            this.airFlightDirection = null;
            this.airChasing = false;
            this.idleTicks = this.randomBetween(18, 34);
            this.followRefreshTicks = 0;
            this.followSide = this.random.nextBoolean() ? 1 : -1;
            this.pet.snapTo(this.roamTarget, this.random.nextFloat() * 360.0f);
        }
    }

    private void tickPet(class_1657 playerEntity, CustomPetVariant customPetVariant) {
        if (this.pet != null) {
            boolean bl;
            this.pet.setPetVariant(customPetVariant);
            class_243 vec3d = this.pet.method_73189();
            class_243 vec3d2 = this.snapCurrentToGround(vec3d, playerEntity.method_23318());
            class_243 vec3d3 = playerEntity.method_73189();
            double d2 = vec3d.method_1025(vec3d3);
            double d3 = Math.sqrt(d2);
            double d4 = playerEntity.method_18798().method_37267();
            boolean bl2 = bl = customPetVariant != null && customPetVariant.isRobot();
            if (bl) {
                this.tickRobotFlight(playerEntity, vec3d, vec3d3, d3, d4);
            } else {
                ++this.ticks;
                boolean bl3 = playerEntity.method_6128() || playerEntity.method_31549().field_7479;
                boolean bl4 = false;
                if (this.petIsOwl) {
                    if (this.flightTicksRemaining > 0) {
                        --this.flightTicksRemaining;
                        bl4 = true;
                        if (this.flightTicksRemaining == 0) {
                            this.nextFlightAtTick = this.ticks + this.randomBetween(300, 640);
                        }
                    } else if (!bl3 && this.ticks >= this.nextFlightAtTick && d4 < 0.12 && d3 < 8.0) {
                        this.flightTicksRemaining = this.randomBetween(90, 190);
                        this.nextFlightAtTick = this.ticks + this.flightTicksRemaining + this.randomBetween(300, 640);
                        bl4 = true;
                    }
                }
                boolean bl5 = bl3 || bl4;
                boolean bl6 = d4 > 0.025 || Math.abs(playerEntity.method_18798().field_1351) > 0.05;
                boolean bl8 = d2 > 49.0 || bl6 && d2 > 9.0;
                boolean bl9 = this.isRainingAbove(vec3d);
                if (d2 > 64.0) {
                    this.roamTarget = this.pickRoamTarget(playerEntity, true);
                    this.airFlightDirection = null;
                    this.airChasing = false;
                    this.flightTicksRemaining = 0;
                    this.idleTicks = this.randomBetween(12, 22);
                    this.followRefreshTicks = 0;
                    this.pet.snapTo(this.roamTarget, this.pet.method_36454());
                    this.pet.setBehavior(this.roamTarget, 0.0, false, bl9, false, 1.0);
                } else if (this.pet.isMovementBlocked() && d2 <= 49.0) {
                    this.roamTarget = null;
                    this.airFlightDirection = null;
                    this.airChasing = false;
                    this.followRefreshTicks = 0;
                    if (this.idleTicks <= 0) {
                        this.idleTicks = this.randomBetween(18, 38);
                    }
                    --this.idleTicks;
                    this.pet.setBehavior(vec3d2, 0.0, false, bl9, false, 1.0);
                } else if (!bl5 && !bl8 && d4 < 0.02 && d2 < 7.29) {
                    this.followRefreshTicks = 0;
                    this.roamTarget = null;
                    this.airFlightDirection = null;
                    this.airChasing = false;
                    this.pet.overrideLookYaw((float)Math.toDegrees(class_3532.method_15349((double)(playerEntity.method_23321() - vec3d.field_1350), (double)(playerEntity.method_23317() - vec3d.field_1352))) - 90.0f, 1.0f);
                    if (this.idleTicks <= 0) {
                        this.idleTicks = this.randomBetween(24, 52);
                    }
                    --this.idleTicks;
                    this.pet.setBehavior(vec3d2, 0.0, false, bl9, false, 1.0);
                } else {
                    double d;
                    if (bl5) {
                        class_243 vec3d4;
                        boolean isMovingAir;
                        this.idleTicks = 0;
                        boolean bl7 = isMovingAir = d4 > 0.08 || Math.abs(playerEntity.method_18798().field_1351) > 0.08;
                        if (isMovingAir) {
                            this.followRefreshTicks = 0;
                            this.airHoverOffset = null;
                            vec3d4 = this.computeAirFollowTarget(playerEntity, d3, d4);
                        } else {
                            if (this.airHoverOffset != null && this.followRefreshTicks > 0) {
                                --this.followRefreshTicks;
                            } else {
                                this.airHoverOffset = this.pickAirHoverOffset(playerEntity, vec3d);
                                this.followRefreshTicks = this.randomBetween(20, 42);
                            }
                            vec3d4 = this.computeAirIdleTarget(playerEntity);
                        }
                        if (this.airSmoothedTarget == null) {
                            this.airSmoothedTarget = vec3d;
                        }
                        d = Math.clamp(0.07 + playerEntity.method_18798().method_1033() * (playerEntity.method_6128() ? 0.025 : 0.05) + Math.max(0.0, d3 - 1.5) * 0.012, 0.07, playerEntity.method_6128() ? 0.2 : 0.28);
                        this.roamTarget = this.airSmoothedTarget = this.airSmoothedTarget.method_35590(vec3d4, d);
                    } else if (bl8) {
                        this.airHoverOffset = null;
                        this.airSmoothedTarget = null;
                        this.airFlightDirection = null;
                        this.airChasing = false;
                        this.idleTicks = 0;
                        if (this.followRefreshTicks > 0 && this.roamTarget != null) {
                            --this.followRefreshTicks;
                        } else {
                            if (d3 < 4.5 && this.random.nextFloat() < 0.08f) {
                                this.followSide *= -1;
                            }
                            class_243 vec3d5 = this.computeFollowTarget(playerEntity, d3, d4);
                            float f = (float)Math.clamp(0.22 + Math.max(0.0, d3 - 2.0) * 0.05 + d4 * 0.35, 0.22, 0.62);
                            this.roamTarget = this.roamTarget == null ? vec3d5 : this.roamTarget.method_35590(vec3d5, (double)f);
                            this.followRefreshTicks = d3 > 7.0 ? 1 : 2;
                        }
                    } else {
                        this.airHoverOffset = null;
                        this.airSmoothedTarget = null;
                        this.airFlightDirection = null;
                        this.airChasing = false;
                        this.followRefreshTicks = 0;
                        if (this.idleTicks > 0) {
                            --this.idleTicks;
                            this.pet.setBehavior(vec3d2, 0.0, false, bl9, false, 1.0);
                            return;
                        }
                        if (this.roamTarget == null || this.shouldPickNewTarget(playerEntity, vec3d)) {
                            if (this.random.nextFloat() < 0.24f) {
                                this.roamTarget = null;
                                this.idleTicks = this.randomBetween(18, 42);
                                this.pet.setBehavior(vec3d2, 0.0, false, bl9, false, 1.0);
                                return;
                            }
                            this.followSide = this.random.nextBoolean() ? 1 : -1;
                            boolean fast = vec3d.method_1025(vec3d3) > 16.0 || playerEntity.method_18798().method_37268() > 0.04;
                            this.roamTarget = this.pickRoamTarget(playerEntity, fast);
                        }
                    }
                    if (this.roamTarget == null) {
                        this.idleTicks = this.randomBetween(18, 36);
                        this.pet.setBehavior(vec3d2, 0.0, false, bl9, false, 1.0);
                    } else {
                        class_243 vec3d6 = this.roamTarget;
                        class_243 vec3d7 = vec3d6.method_1020(vec3d);
                        d = Math.hypot(vec3d7.field_1352, vec3d7.field_1350);
                        double d5 = vec3d7.method_1033();
                        if (bl5) {
                            boolean bl11;
                            class_1293 statusEffectInstance = playerEntity.method_6112(class_1294.field_5904);
                            int n = statusEffectInstance != null ? statusEffectInstance.method_5578() + 1 : 0;
                            double d6 = playerEntity.method_18798().method_1033();
                            double d7 = playerEntity.method_6128() ? 0.82 : 0.48;
                            double d8 = playerEntity.method_6128() ? 1.28 : 0.78;
                            boolean bl10 = bl11 = d3 > 4.0 || d6 > 1.2 || d4 > 0.55;
                            if (bl11 || d5 >= d8) {
                                this.airChasing = true;
                            } else if (d5 <= d7) {
                                this.airChasing = false;
                            }
                            double d9 = Math.clamp(0.22 + d6 * (playerEntity.method_6128() ? 2.45 : 1.65) + Math.max(0.0, d3 - 1.0) * (playerEntity.method_6128() ? 0.24 : 0.18) + (double)n * 0.07 + (playerEntity.method_5624() ? 0.08 : 0.0), 0.16, playerEntity.method_6128() ? (d3 > 18.0 ? 6.8 : (d3 > 12.0 ? 5.0 : (d3 > 7.0 ? 3.6 : 1.9))) : (d3 > 16.0 ? 3.2 : (d3 > 10.0 ? 2.4 : (d3 > 6.0 ? 1.55 : 0.95))));
                            double d10 = Math.clamp(1.15 + Math.max(0.0, d3 - 1.2) * 0.22 + d6 * (playerEntity.method_6128() ? 1.8 : 1.35) + (double)n * 0.16, 1.0, playerEntity.method_6128() ? 3.1 : 2.4);
                            this.pet.setBehavior(vec3d6, d9, this.airChasing, false, true, d10);
                        } else if (d <= 0.9 && Math.abs(vec3d7.field_1351) <= 0.45 && !bl8) {
                            this.roamTarget = null;
                            this.idleTicks = this.randomBetween(18, 42);
                            this.pet.setBehavior(vec3d2, 0.0, false, bl9, false, 1.0);
                        } else {
                            class_1293 statusEffectInstance = playerEntity.method_6112(class_1294.field_5904);
                            int n = statusEffectInstance != null ? statusEffectInstance.method_5578() + 1 : 0;
                            double d11 = bl8 ? Math.clamp((d - 0.4) / 1.8, 0.12, 1.0) : 1.0;
                            double d12 = Math.clamp(0.055 + d4 * 0.31 + Math.max(0.0, d3 - 1.8) * 0.072 + (double)n * 0.028 + (playerEntity.method_5624() ? 0.028 : 0.0), 0.045, d3 > 8.5 ? 0.52 : (d3 > 6.0 ? 0.42 : (d3 > 4.0 ? 0.31 : 0.22))) * d11;
                            double d13 = Math.clamp(1.0 + Math.max(0.0, d3 - 1.4) * 0.14 + d4 * 0.88 + (double)n * 0.12 + (playerEntity.method_5624() ? 0.06 : 0.0), 0.95, 1.85) * (bl8 ? Math.clamp(d11 + 0.3, 0.4, 1.0) : 1.0);
                            this.pet.setBehavior(vec3d6, d12, d12 > 0.02, bl9, false, d13);
                            this.pushOutOfPlayer(playerEntity);
                        }
                    }
                }
            }
        }
    }

    public CustomPetEntity getPet() {
        return this.pet;
    }

    private class_243 computeFollowTarget(class_1657 playerEntity, double d, double d2) {
        class_243 vec3d = playerEntity.method_18798();
        class_243 vec3d2 = new class_243(vec3d.field_1352, 0.0, vec3d.field_1350);
        if (vec3d2.method_1027() < 1.0E-4) {
            float f = playerEntity.method_36454() * ((float)Math.PI / 180);
            vec3d2 = new class_243((double)(-class_3532.method_15374((double)f)), 0.0, (double)class_3532.method_15362((double)f));
        }
        vec3d2 = vec3d2.method_1029();
        double d3 = Math.clamp(0.6 + d2 * 7.0 + Math.max(0.0, d - 3.0) * 0.35, 0.6, 3.0);
        class_243 vec3d3 = playerEntity.method_73189().method_1019(playerEntity.method_18798().method_1021(d3));
        double d4 = d > 5.5 ? 0.65 : 1.45;
        double d5 = d > 5.5 ? 0.0 : 0.55 * (double)this.followSide;
        class_243 vec3d4 = new class_243(-vec3d2.field_1350, 0.0, vec3d2.field_1352);
        class_243 vec3d5 = vec3d3.method_1020(vec3d2.method_1021(d4)).method_1019(vec3d4.method_1021(d5));
        double d6 = this.findGroundY(vec3d5.field_1352, vec3d5.field_1350, playerEntity.method_23318());
        return CustomPetFollowerController.enforcePersonalSpace(new class_243(vec3d5.field_1352, d6, vec3d5.field_1350), playerEntity);
    }

    private boolean isRainingAbove(class_243 vec3d) {
        return this.mc.field_1687 != null && this.mc.field_1687.method_8520(class_2338.method_49637((double)vec3d.field_1352, (double)(vec3d.field_1351 + 1.1), (double)vec3d.field_1350));
    }

    private class_243 pickRobotRoamTarget(class_1657 playerEntity) {
        double d = this.random.nextDouble() * 6.2831854820251465;
        double d2 = 2.0 + this.random.nextDouble() * 4.0;
        double d3 = playerEntity.method_23317() + Math.cos(d) * d2;
        double d4 = playerEntity.method_23321() + Math.sin(d) * d2;
        double d5 = playerEntity.method_23318() + 1.0 + this.random.nextDouble() * 3.0;
        double d6 = this.ceilingClearance(d3, d4, d5, playerEntity.method_23318());
        return CustomPetFollowerController.enforcePersonalSpace(new class_243(d3, Math.max(d5, d6), d4), playerEntity);
    }

    private double ceilingClearance(double d, double d2, double d3, double d4) {
        if (this.mc.field_1687 == null) {
            return d3;
        }
        int n = class_3532.method_15357((double)d);
        int n2 = class_3532.method_15357((double)d2);
        int n3 = class_3532.method_15357((double)d3);
        class_2338.class_2339 mutable = new class_2338.class_2339(n, n3, n2);
        for (int i = n3; i <= n3 + 3; ++i) {
            mutable.method_33098(i);
            class_2680 blockState = this.mc.field_1687.method_8320((class_2338)mutable);
            class_265 voxelShape = blockState.method_26220((class_1922)this.mc.field_1687, (class_2338)mutable);
            if (voxelShape.method_1110()) continue;
            return (double)i + voxelShape.method_1105(class_2350.class_2351.field_11052) + 0.6;
        }
        return d3;
    }

    private static class_243 enforcePersonalSpace(class_243 vec3d, class_1657 playerEntity) {
        double d;
        double d2 = vec3d.field_1352 - playerEntity.method_23317();
        double d3 = d2 * d2 + (d = vec3d.field_1350 - playerEntity.method_23321()) * d;
        if (d3 >= 0.42250000000000004) {
            return vec3d;
        }
        double d4 = Math.sqrt(d3);
        if (d4 < 1.0E-4) {
            float f = playerEntity.method_36454() * ((float)Math.PI / 180);
            d2 = -class_3532.method_15374((double)f);
            d = class_3532.method_15362((double)f);
            d4 = 1.0;
        }
        double d5 = 0.65 / d4;
        return new class_243(playerEntity.method_23317() + d2 * d5, vec3d.field_1351, playerEntity.method_23321() + d * d5);
    }

    private void tickRobotFlight(class_1657 playerEntity, class_243 vec3d, class_243 vec3d2, double d, double d2) {
        if (d > 8.0) {
            class_243 vec3d5;
            this.airSmoothedTarget = vec3d5 = this.pickRobotRoamTarget(playerEntity);
            this.roamTarget = vec3d5;
            this.airFlightDirection = null;
            this.followRefreshTicks = 0;
            this.idleTicks = this.randomBetween(30, 60);
            this.pet.snapTo(vec3d5, this.pet.method_36454());
            this.pet.setBehavior(vec3d5, 0.0, false, false, true, 1.0);
        } else {
            boolean bl;
            class_243 vec3d3;
            class_243 vec3d4;
            double d3;
            double d4;
            double d5;
            boolean bl2;
            class_243 vec3d6 = playerEntity.method_18798();
            class_243 vec3d7 = new class_243(vec3d6.field_1352, 0.0, vec3d6.field_1350);
            if (vec3d7.method_1027() < 1.0E-4) {
                float f = playerEntity.method_36454() * ((float)Math.PI / 180);
                vec3d7 = new class_243((double)(-class_3532.method_15374((double)f)), 0.0, (double)class_3532.method_15362((double)f));
            }
            vec3d7 = vec3d7.method_1029();
            this.airFlightDirection = this.airFlightDirection == null ? vec3d7 : this.airFlightDirection.method_35590(vec3d7, 0.18).method_1029();
            boolean bl3 = d2 > 0.08;
            boolean bl4 = bl2 = bl3 && d > 4.0 || d > 15.0;
            if (bl2) {
                this.roamTarget = null;
                this.idleTicks = 0;
            }
            if (bl2) {
                class_243 vec3d8 = this.airFlightDirection;
                class_243 vec3d9 = new class_243(-vec3d8.field_1350, 0.0, vec3d8.field_1352);
                d5 = d > 5.0 ? 0.5 : 1.2;
                d4 = 1.3 * (double)this.followSide;
                d3 = Math.clamp(d2 * 4.5, 0.0, 2.2);
                vec3d4 = vec3d2.method_1019(vec3d8.method_1021(d3 - d5)).method_1019(vec3d9.method_1021(d4));
                double d6 = playerEntity.method_23318() + 1.5;
                double d7 = this.ceilingClearance(vec3d4.field_1352, vec3d4.field_1350, d6, playerEntity.method_23318());
                vec3d3 = CustomPetFollowerController.enforcePersonalSpace(new class_243(vec3d4.field_1352, Math.max(d6, d7), vec3d4.field_1350), playerEntity);
                bl = true;
            } else if (this.idleTicks > 0) {
                --this.idleTicks;
                if (this.idleTicks <= 0) {
                    this.roamTarget = null;
                }
                vec3d3 = this.roamTarget != null ? this.roamTarget : vec3d;
                bl = false;
            } else if (this.roamTarget != null) {
                double d8 = vec3d.method_1022(this.roamTarget);
                if (d8 < 1.5) {
                    this.roamTarget = vec3d;
                    this.idleTicks = this.randomBetween(60, 140);
                    vec3d3 = vec3d;
                    bl = false;
                } else {
                    vec3d3 = this.roamTarget;
                    bl = true;
                }
            } else {
                vec3d3 = this.roamTarget = this.pickRobotRoamTarget(playerEntity);
                bl = true;
            }
            double d9 = Math.sin((double)playerEntity.field_6012 * 0.075 + (double)this.followSide * 1.7) * 0.55;
            d5 = Math.sin((double)playerEntity.field_6012 * 0.125 + (double)this.followSide * 0.4) * 0.2;
            d4 = d9 + d5;
            d3 = bl ? 0.0 : Math.sin((double)playerEntity.field_6012 * 0.018 + (double)this.followSide * 0.9) * 0.1;
            vec3d4 = new class_243(-this.airFlightDirection.field_1350, 0.0, this.airFlightDirection.field_1352);
            class_243 vec3d10 = new class_243(vec3d3.field_1352 + vec3d4.field_1352 * d3, vec3d3.field_1351 + d4, vec3d3.field_1350 + vec3d4.field_1350 * d3);
            if (this.airSmoothedTarget == null) {
                this.airSmoothedTarget = vec3d;
            }
            double d10 = bl ? Math.clamp(0.1 + d2 * 0.08 + Math.max(0.0, d - 1.5) * 0.018, 0.1, 0.34) : 0.07;
            this.airSmoothedTarget = this.airSmoothedTarget.method_35590(vec3d10, d10);
            class_243 vec3d11 = this.airSmoothedTarget.method_1020(vec3d2);
            if (vec3d11.method_1033() > 17.5) {
                this.airSmoothedTarget = vec3d2.method_1019(vec3d11.method_1029().method_1021(17.5));
            }
            double d11 = vec3d.method_1022(this.airSmoothedTarget);
            double d12 = bl ? Math.clamp(0.08 + d11 * 0.06 + d2 * 0.8 + (playerEntity.method_5624() ? 0.06 : 0.0), 0.06, bl2 ? 1.2 : 0.25) : 0.04;
            double d13 = Math.clamp(1.0 + d11 * 0.08 + d2 * 0.6, 1.0, 1.8);
            this.tickGazeReaction(playerEntity, vec3d);
            this.pet.setBehavior(this.airSmoothedTarget, d12, bl || d11 > 0.3, false, true, d13);
            this.pushOutOfPlayer(playerEntity);
        }
    }

    private class_243 pickAirHoverOffset(class_1657 playerEntity, class_243 vec3d) {
        if (this.petIsOwl) {
            double d = this.random.nextDouble() * 6.2831854820251465;
            double d2 = class_3532.method_16436((double)this.random.nextDouble(), (double)1.6, (double)2.8);
            double d3 = class_3532.method_16436((double)this.random.nextDouble(), (double)1.6, (double)3.2);
            return new class_243(Math.cos(d) * d2, d3, Math.sin(d) * d2);
        }
        class_243 vec3d2 = vec3d.method_1020(playerEntity.method_73189());
        class_243 vec3d3 = new class_243(vec3d2.field_1352, 0.0, vec3d2.field_1350);
        double d = vec3d3.method_1033();
        if (!(d < 0.9) && !(d > 2.5)) {
            class_243 vec3d4 = vec3d3.method_1029().method_1021(class_3532.method_15350((double)d, (double)1.25, (double)1.95));
            double d6 = class_3532.method_15350((double)vec3d2.field_1351, (double)0.24, (double)0.46);
            return new class_243(vec3d4.field_1352, d6, vec3d4.field_1350);
        }
        double d4 = this.random.nextDouble() * 6.2831854820251465;
        double d5 = class_3532.method_16436((double)this.random.nextDouble(), (double)1.35, (double)1.95);
        return new class_243(Math.cos(d4) * d5, class_3532.method_16436((double)this.random.nextDouble(), (double)0.28, (double)0.44), Math.sin(d4) * d5);
    }

    private int randomBetween(int n, int n2) {
        return n + this.random.nextInt(n2 - n + 1);
    }

    private class_243 pickRoamTarget(class_1657 playerEntity, boolean bl) {
        class_243 vec3d = playerEntity.method_18798();
        double d = vec3d.method_37268() > 0.0025 ? Math.atan2(vec3d.field_1350, vec3d.field_1352) : this.random.nextDouble() * 6.2831854820251465;
        double d2 = d + class_3532.method_16436((double)this.random.nextDouble(), (double)-1.65, (double)1.65);
        double d3 = bl ? class_3532.method_16436((double)this.random.nextDouble(), (double)1.3, (double)2.4) : class_3532.method_16436((double)this.random.nextDouble(), (double)1.8, (double)3.0);
        double d4 = playerEntity.method_23317() + Math.cos(d2) * d3;
        double d5 = playerEntity.method_23321() + Math.sin(d2) * d3;
        double d6 = this.findGroundY(d4, d5, playerEntity.method_23318());
        return CustomPetFollowerController.enforcePersonalSpace(new class_243(d4, d6, d5), playerEntity);
    }

    private class_243 snapCurrentToGround(class_243 vec3d, double d) {
        double d2 = this.findGroundY(vec3d.field_1352, vec3d.field_1350, Math.max(vec3d.field_1351, d));
        return new class_243(vec3d.field_1352, d2, vec3d.field_1350);
    }

    private boolean shouldPickNewTarget(class_1657 playerEntity, class_243 vec3d) {
        if (this.roamTarget == null) {
            return true;
        }
        return this.roamTarget.method_1025(playerEntity.method_73189()) > 9.0 || vec3d.method_1025(playerEntity.method_73189()) > 49.0 && vec3d.method_1025(this.roamTarget) > 25.0;
    }

    private class_243 computeAirFollowTarget(class_1657 playerEntity, double d, double d2) {
        class_243 vec3d = playerEntity.method_18798();
        double d3 = vec3d.method_1033();
        class_243 vec3d2 = new class_243(vec3d.field_1352, 0.0, vec3d.field_1350);
        if (vec3d2.method_1027() < 1.0E-4) {
            float f = playerEntity.method_36454() * ((float)Math.PI / 180);
            vec3d2 = new class_243((double)(-class_3532.method_15374((double)f)), 0.0, (double)class_3532.method_15362((double)f));
        }
        vec3d2 = vec3d2.method_1029();
        if (this.airFlightDirection != null && !(this.airFlightDirection.method_1027() < 1.0E-4)) {
            double d4 = playerEntity.method_6128() ? 0.14 : 0.26;
            this.airFlightDirection = this.airFlightDirection.method_35590(vec3d2, d4);
            if (this.airFlightDirection.method_1027() < 1.0E-4) {
                this.airFlightDirection = vec3d2;
            }
        } else {
            this.airFlightDirection = vec3d2;
        }
        class_243 vec3d3 = this.airFlightDirection.method_1029();
        class_243 vec3d4 = new class_243(-vec3d3.field_1350, 0.0, vec3d3.field_1352);
        boolean bl = this.mc.field_1690.method_31044().method_31034();
        double d5 = (playerEntity.method_6128() ? 0.82 : 0.65) * (double)this.followSide;
        double d6 = (playerEntity.method_6128() ? 1.45 : 1.05) + Math.min(d * 0.08, playerEntity.method_6128() ? 0.55 : 0.35);
        if (playerEntity.method_6128() && bl) {
            d5 *= 1.18;
            d6 += 0.42;
        }
        double d7 = playerEntity.method_6128() ? 0.2 : 0.45;
        double d8 = Math.clamp(0.45 + d2 * (playerEntity.method_6128() ? 5.8 : 4.8) + Math.max(0.0, d3 - 0.8) * (playerEntity.method_6128() ? 0.45 : 0.35), 0.45, playerEntity.method_6128() ? 2.8 : 1.6);
        class_243 vec3d5 = playerEntity.method_73189().method_1019(vec3d3.method_1021(d8));
        double d9 = Math.sin((double)(playerEntity.field_6012 + this.followSide * 7) * 0.1) * 0.028;
        return vec3d5.method_1020(vec3d3.method_1021(d6)).method_1019(vec3d4.method_1021(d5)).method_1031(0.0, playerEntity.method_23318() - vec3d5.field_1351 + d7 + d9, 0.0);
    }

    private void pushOutOfPlayer(class_1657 playerEntity) {
        double d;
        double d2;
        double d3;
        if (this.pet != null && !((d3 = (d2 = this.pet.method_23317() - playerEntity.method_23317()) * d2 + (d = this.pet.method_23321() - playerEntity.method_23321()) * d) >= 0.42250000000000004)) {
            double d4 = Math.sqrt(d3);
            if (d4 < 1.0E-4) {
                float f = playerEntity.method_36454() * ((float)Math.PI / 180);
                d2 = -class_3532.method_15374((double)f);
                d = class_3532.method_15362((double)f);
                d4 = 1.0;
            }
            double d5 = 0.65 / d4;
            double d6 = playerEntity.method_23317() + d2 * d5;
            double d7 = playerEntity.method_23321() + d * d5;
            this.pet.method_23327(d6, this.pet.method_23318(), d7);
        }
    }

    private class_243 computeAirIdleTarget(class_1657 playerEntity) {
        class_243 vec3d = this.petIsOwl ? new class_243(1.9 * (double)this.followSide, 1.6, 0.0) : new class_243(1.6 * (double)this.followSide, 0.35, 0.0);
        class_243 vec3d2 = this.airHoverOffset != null ? this.airHoverOffset : vec3d;
        double d = this.petIsOwl ? 0.25 : 0.09;
        double d2 = Math.sin((double)(playerEntity.field_6012 + this.followSide * 7) * 0.085) * d;
        return playerEntity.method_73189().method_1031(vec3d2.field_1352, vec3d2.field_1351 + d2, vec3d2.field_1350);
    }

    private void tickGazeReaction(class_1657 playerEntity, class_243 vec3d) {
        if (this.pet != null) {
            class_243 vec3d2 = playerEntity.method_5828(1.0f);
            class_243 vec3d3 = vec3d.method_1020(playerEntity.method_73189());
            double d = Math.hypot(vec3d3.field_1352, vec3d3.field_1350);
            boolean bl = false;
            if (d > 0.05 && d < 3.5) {
                class_243 vec3d4 = new class_243(vec3d2.field_1352, 0.0, vec3d2.field_1350);
                class_243 vec3d5 = new class_243(vec3d3.field_1352, 0.0, vec3d3.field_1350);
                if (vec3d4.method_1027() > 1.0E-4 && vec3d5.method_1027() > 1.0E-4) {
                    vec3d4 = vec3d4.method_1029();
                    vec3d5 = vec3d5.method_1029();
                    double d2 = vec3d4.field_1352 * vec3d5.field_1352 + vec3d4.field_1350 * vec3d5.field_1350;
                    boolean bl2 = bl = d2 > 0.88;
                }
            }
            if (this.gazeCooldownTicks > 0) {
                --this.gazeCooldownTicks;
            }
            if (this.gazeLookActiveTicks > 0) {
                --this.gazeLookActiveTicks;
                float f = (float)Math.toDegrees(class_3532.method_15349((double)(playerEntity.method_23321() - vec3d.field_1350), (double)(playerEntity.method_23317() - vec3d.field_1352))) - 90.0f;
                this.pet.overrideLookYaw(f, 1.0f);
                if (this.gazeLookActiveTicks == 0) {
                    this.gazeCooldownTicks = this.randomBetween(80, 160);
                }
            } else {
                this.pet.overrideLookYaw(0.0f, 0.0f);
                if (bl && this.gazeCooldownTicks <= 0) {
                    ++this.gazeBuildTicks;
                    if (this.gazeBuildTicks >= 25) {
                        this.gazeBuildTicks = 0;
                        if (this.random.nextFloat() < 0.65f) {
                            this.gazeLookActiveTicks = this.randomBetween(40, 70);
                        } else {
                            this.gazeCooldownTicks = this.randomBetween(40, 90);
                        }
                    }
                } else if (!bl) {
                    this.gazeBuildTicks = 0;
                }
            }
        }
    }

    private double findGroundY(double d, double d2, double d3) {
        if (this.mc.field_1687 == null) {
            return d3;
        }
        int n = class_3532.method_15357((double)d);
        int n2 = class_3532.method_15357((double)d2);
        int n3 = class_3532.method_15357((double)d3);
        class_2338.class_2339 mutable = new class_2338.class_2339(n, n3 + 2, n2);
        for (int i = n3 + 2; i >= n3 - 6; --i) {
            mutable.method_33098(i);
            class_2680 blockState = this.mc.field_1687.method_8320((class_2338)mutable);
            class_265 voxelShape = blockState.method_26220((class_1922)this.mc.field_1687, (class_2338)mutable);
            if (voxelShape.method_1110()) continue;
            return (double)i + voxelShape.method_1105(class_2350.class_2351.field_11052);
        }
        return d3;
    }

    public void tick(class_1657 playerEntity, CustomPetVariant customPetVariant, boolean bl) {
        if (playerEntity != null && playerEntity.method_73183() != null && this.mc.field_1687 != null) {
            this.ensurePet(playerEntity, customPetVariant, bl);
            this.tickPet(playerEntity, customPetVariant);
        } else {
            this.reset();
        }
    }
}


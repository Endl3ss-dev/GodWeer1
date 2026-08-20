/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.decoration.ArmorStandEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.SwordItem
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 */
package minced.module.combat;
import minced.core.EventHandler;

import java.util.Comparator;
import java.util.Random;
import java.util.function.BooleanSupplier;
import java.util.stream.StreamSupport;
import minced.core.MultiBooleanSetting;
import minced.core.BooleanSetting;
import minced.core.SliderSetting;
import minced.core.EventUpdate;
import minced.core.EntityFilter;
import minced.core.Minced;
import minced.core.Module;
import minced.enums.Combat;
import minced.module.combat.AntiBot;
import minced.module.combat.AttackAura;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.SwordItem;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class AimAssist
extends Module {
    static MinecraftClient client = MinecraftClient.getInstance();
    BooleanSetting visibleOnly;
    SliderSetting aimRange;
    SliderSetting fov;
    static float FOV_HALF_SCALE;
    MultiBooleanSetting targetTypes = new MultiBooleanSetting(TARGETS_SETTING_NAME, new BooleanSetting(PLAYERS_TARGET_NAME, true), new BooleanSetting(FRIENDS_TARGET_NAME, false), new BooleanSetting(UNARMORED_TARGET_NAME, true), new BooleanSetting(VILLAGERS_TARGET_NAME, false), new BooleanSetting(ANIMALS_TARGET_NAME, false), new BooleanSetting(MOBS_TARGET_NAME, false));
    static double SENSITIVITY_SCALE;
    static double SENSITIVITY_OFFSET;
    static double GCD_MULTIPLIER;
    static double GCD_INPUT_SCALE;
    static double GCD_OUTPUT_SCALE;
    static String MODULE_NAME;
    static String MODULE_DESCRIPTION;
    static String TARGETS_SETTING_NAME;
    static String PLAYERS_TARGET_NAME;
    static String FRIENDS_TARGET_NAME;
    static String UNARMORED_TARGET_NAME;
    static String VILLAGERS_TARGET_NAME;
    static String ANIMALS_TARGET_NAME;
    static String MOBS_TARGET_NAME;
    static String TARGET_LOCK_TIME_SETTING_NAME;
    static float DEFAULT_TARGET_LOCK_SECONDS;
    static float MAX_TARGET_LOCK_SECONDS;
    static float TARGET_LOCK_STEP;
    SliderSetting targetLockTime = new SliderSetting(TARGET_LOCK_TIME_SETTING_NAME, DEFAULT_TARGET_LOCK_SECONDS, 0.0f, MAX_TARGET_LOCK_SECONDS, TARGET_LOCK_STEP);
    static String AIM_RANGE_SETTING_NAME;
    static float DEFAULT_AIM_RANGE;
    static float MAX_AIM_RANGE;
    static float AIM_RANGE_STEP;
    static String AIM_STRENGTH_SETTING_NAME;
    static float DEFAULT_AIM_STRENGTH;
    static float MIN_AIM_STRENGTH;
    static float AIM_STRENGTH_STEP;
    SliderSetting aimStrength;
    static String AIM_SPEED_SETTING_NAME;
    static float DEFAULT_AIM_SPEED;
    static float MIN_AIM_SPEED;
    static float MAX_AIM_SPEED;
    static float AIM_SPEED_STEP;
    SliderSetting aimSpeed;
    static String SMOOTHNESS_SETTING_NAME;
    static float DEFAULT_SMOOTHNESS;
    static float MAX_SMOOTHNESS;
    static float SMOOTHNESS_STEP;
    SliderSetting smoothness;
    static String FOV_SETTING_NAME;
    static float DEFAULT_FOV;
    static float MIN_FOV;
    static float MAX_FOV;
    static String HUMANIZED_SETTING_NAME;
    BooleanSetting humanized;
    static String VERTICAL_AIM_SETTING_NAME;
    BooleanSetting verticalAim;
    static String VERTICAL_SPEED_SETTING_NAME;
    static float DEFAULT_VERTICAL_SPEED;
    static float MIN_VERTICAL_SPEED;
    static float VERTICAL_SPEED_STEP;
    SliderSetting verticalSpeed;
    static String VERTICAL_NOISE_SETTING_NAME;
    BooleanSetting verticalNoise;
    static String NOISE_AMOUNT_SETTING_NAME;
    static float DEFAULT_NOISE_AMOUNT;
    static float MAX_NOISE_AMOUNT;
    static float NOISE_AMOUNT_STEP;
    SliderSetting noiseAmount;
    static String HESITATION_SETTING_NAME;
    BooleanSetting hesitation;
    static String WEAPON_ONLY_SETTING_NAME;
    BooleanSetting weaponOnly;
    static String STOP_NEAR_TARGET_SETTING_NAME;
    BooleanSetting stopNearTarget;
    static String REQUIRE_INPUT_SETTING_NAME;
    BooleanSetting requireAttackInput;
    static String POSITION_PREDICTION_SETTING_NAME;
    static float MAX_POSITION_PREDICTION;
    static float POSITION_PREDICTION_STEP;
    SliderSetting positionPrediction;
    static String KNOCKBACK_PREDICTION_SETTING_NAME;
    static float MAX_KNOCKBACK_PREDICTION;
    static float KNOCKBACK_PREDICTION_STEP;
    SliderSetting knockbackPrediction;
    static String MULTIPOINT_SETTING_NAME;
    BooleanSetting multipoint;
    static String FOLLOW_MOUSE_SETTING_NAME;
    BooleanSetting followMouse;
    static String VISIBLE_ONLY_SETTING_NAME;
    static String DISABLE_WITH_AURA_SETTING_NAME;
    BooleanSetting disableWithAura;
    Random random;
    static double PRIMARY_AIM_HEIGHT_FACTOR;
    static double SNEAKING_HEIGHT_OFFSET;
    static double YAW_OFFSET;
    static double SECONDARY_AIM_HEIGHT_FACTOR;
    static float MULTIPOINT_BLEND_FACTOR;
    static double BASE_VELOCITY_PREDICTION;
    static double BASE_STRAFE_PREDICTION;
    LivingEntity lockedTarget;
    long targetLockExpiresAt;
    static float MILLISECONDS_PER_SECOND;
    static String AURA_COMPATIBLE_MODE_MARKER;
    int hesitationTicks;
    static float HESITATION_CHANCE;
    static float FOV_VALIDATION_SCALE;
    static float YAW_DEAD_ZONE;
    static float PITCH_DEAD_ZONE;
    static float SMOOTHNESS_BASE;
    static float SMOOTHNESS_DIVISOR;
    static float MIN_SMOOTHNESS_FACTOR;
    static float RANDOM_CENTER;
    static float HUMANIZED_YAW_NOISE;
    static float VERTICAL_NOISE_YAW_THRESHOLD;
    static float VERTICAL_RANDOM_CENTER;
    static float PITCH_RANDOM_CENTER;
    static float HUMANIZED_PITCH_NOISE;
    static float MIN_PITCH;
    static float MAX_PITCH;

    private boolean isValidTarget(Entity param1) {
        PlayerEntity v3;
        LivingEntity v2;
        if (param1 instanceof LivingEntity) {
            v2 = (LivingEntity)param1;
            if (param1 == AimAssist.client.player || !param1.isAlive() || param1 instanceof ArmorStandEntity) {
                return false;
            }
        } else {
            return false;
        }
        if (param1 instanceof PlayerEntity && AntiBot.method1033(v3 = (PlayerEntity)param1)) {
            return false;
        }
        if (((Boolean)this.visibleOnly.getValue()).booleanValue() && !AimAssist.client.player.canSee(param1)) {
            return false;
        }
        if (AimAssist.client.player.distanceTo(param1) > ((Float)this.aimRange.getValue()).floatValue()) {
            return false;
        }
        if (this.getYawDifference(v2) > (double)(((Float)this.fov.getValue()).floatValue() * FOV_HALF_SCALE)) {
            return false;
        }
        return EntityFilter.method3435((Entity)v2, this.targetTypes, true) || EntityFilter.method3436(v2, this.targetTypes) || EntityFilter.method3439(v2, this.targetTypes) || EntityFilter.method3431(v2, this.targetTypes);
    }

    private static float applyMouseGcd(float param0, float param1) {
        double v2 = (Double)AimAssist.client.options.getMouseSensitivity().getValue() * SENSITIVITY_SCALE + SENSITIVITY_OFFSET;
        double v4 = v2 * v2 * v2 * GCD_MULTIPLIER;
        return (float)((double)param0 + Math.ceil((double)(param1 - param0) / v4 / GCD_INPUT_SCALE) * v4 * GCD_OUTPUT_SCALE);
    }

    public AimAssist() {
        super(MODULE_NAME, MODULE_DESCRIPTION, Combat.Combat);
        this.aimRange = new SliderSetting(AIM_RANGE_SETTING_NAME, DEFAULT_AIM_RANGE, 2.0f, MAX_AIM_RANGE, AIM_RANGE_STEP);
        this.aimStrength = new SliderSetting(AIM_STRENGTH_SETTING_NAME, DEFAULT_AIM_STRENGTH, MIN_AIM_STRENGTH, 2.0f, AIM_STRENGTH_STEP);
        this.aimSpeed = new SliderSetting(AIM_SPEED_SETTING_NAME, DEFAULT_AIM_SPEED, MIN_AIM_SPEED, MAX_AIM_SPEED, AIM_SPEED_STEP);
        this.smoothness = new SliderSetting(SMOOTHNESS_SETTING_NAME, DEFAULT_SMOOTHNESS, 0.0f, MAX_SMOOTHNESS, SMOOTHNESS_STEP);
        this.fov = new SliderSetting(FOV_SETTING_NAME, DEFAULT_FOV, MIN_FOV, MAX_FOV, 1.0f);
        this.humanized = new BooleanSetting(HUMANIZED_SETTING_NAME, false);
        this.verticalAim = new BooleanSetting(VERTICAL_AIM_SETTING_NAME, true);
        this.verticalSpeed = new SliderSetting(VERTICAL_SPEED_SETTING_NAME, DEFAULT_VERTICAL_SPEED, MIN_VERTICAL_SPEED, 1.0f, VERTICAL_SPEED_STEP, (BooleanSupplier)() -> Boolean.TRUE.equals(this.verticalAim.getValue()));
        this.verticalNoise = new BooleanSetting(VERTICAL_NOISE_SETTING_NAME, false, this.verticalAim::getValue);
        this.noiseAmount = new SliderSetting(NOISE_AMOUNT_SETTING_NAME, DEFAULT_NOISE_AMOUNT, 0.0f, MAX_NOISE_AMOUNT, NOISE_AMOUNT_STEP, (BooleanSupplier)() -> Boolean.TRUE.equals(this.verticalNoise.getValue()));
        this.hesitation = new BooleanSetting(HESITATION_SETTING_NAME, false);
        this.weaponOnly = new BooleanSetting(WEAPON_ONLY_SETTING_NAME, true);
        this.stopNearTarget = new BooleanSetting(STOP_NEAR_TARGET_SETTING_NAME, true);
        this.requireAttackInput = new BooleanSetting(REQUIRE_INPUT_SETTING_NAME, false);
        this.positionPrediction = new SliderSetting(POSITION_PREDICTION_SETTING_NAME, 0.0f, 0.0f, MAX_POSITION_PREDICTION, POSITION_PREDICTION_STEP);
        this.knockbackPrediction = new SliderSetting(KNOCKBACK_PREDICTION_SETTING_NAME, 0.0f, 0.0f, MAX_KNOCKBACK_PREDICTION, KNOCKBACK_PREDICTION_STEP);
        this.multipoint = new BooleanSetting(MULTIPOINT_SETTING_NAME, false);
        this.followMouse = new BooleanSetting(FOLLOW_MOUSE_SETTING_NAME, true);
        this.visibleOnly = new BooleanSetting(VISIBLE_ONLY_SETTING_NAME, true);
        this.disableWithAura = new BooleanSetting(DISABLE_WITH_AURA_SETTING_NAME, true);
        this.random = new Random();
        this.addSettings(this.targetTypes, this.targetLockTime, this.aimRange, this.aimStrength, this.aimSpeed, this.smoothness, this.fov, this.humanized, this.verticalAim, this.verticalSpeed, this.verticalNoise, this.noiseAmount, this.hesitation, this.weaponOnly, this.stopNearTarget, this.followMouse, this.requireAttackInput, this.positionPrediction, this.knockbackPrediction, this.multipoint, this.visibleOnly, this.disableWithAura);
    }

    private float[] calculateRotation(LivingEntity param1) {
        Vec3d v2 = this.predictTargetPosition(param1);
        double v3 = v2.x - AimAssist.client.player.getX();
        double v5 = v2.z - AimAssist.client.player.getZ();
        double v7 = param1.getY() + (double)param1.getHeight() * PRIMARY_AIM_HEIGHT_FACTOR;
        if (param1.isSneaking()) {
            v7 -= SNEAKING_HEIGHT_OFFSET;
        }
        double v9 = v7 - AimAssist.client.player.getEyeY();
        double v11 = Math.sqrt(v3 * v3 + v5 * v5);
        float v13 = (float)(Math.toDegrees(Math.atan2(v5, v3)) - YAW_OFFSET);
        float v14 = (float)(-Math.toDegrees(Math.atan2(v9, v11)));
        if (((Boolean)this.multipoint.getValue()).booleanValue()) {
            double v15 = param1.getY() + (double)param1.getHeight() * SECONDARY_AIM_HEIGHT_FACTOR;
            double v17 = v15 - AimAssist.client.player.getEyeY();
            float v19 = (float)(-Math.toDegrees(Math.atan2(v17, v11)));
            v14 = (v14 + v19) * MULTIPOINT_BLEND_FACTOR;
        }
        return new float[]{v13, v14};
    }

    private Vec3d predictTargetPosition(LivingEntity param1) {
        double v2 = ((Float)this.positionPrediction.getValue()).floatValue();
        double v4 = ((Float)this.knockbackPrediction.getValue()).floatValue();
        double v6 = param1.getVelocity().x * (BASE_VELOCITY_PREDICTION + v2);
        double v8 = param1.getVelocity().z * (BASE_STRAFE_PREDICTION + v2);
        if (v4 > 0.0) {
            v6 += (param1.getX() - param1.prevX) * v4;
            v8 += (param1.getZ() - param1.prevZ) * v4;
        }
        return new Vec3d(param1.getX() + v6, param1.getY(), param1.getZ() + v8);
    }

    private LivingEntity getLockedTarget() {
        LivingEntity v3;
        long v1 = System.currentTimeMillis();
        if (this.lockedTarget != null && v1 <= this.targetLockExpiresAt && this.isValidTarget((Entity)this.lockedTarget)) {
            return this.lockedTarget;
        }
        this.lockedTarget = v3 = this.findBestTarget();
        this.targetLockExpiresAt = v3 != null ? v1 + (long)(((Float)this.targetLockTime.getValue()).floatValue() * MILLISECONDS_PER_SECOND) : 0L;
        return v3;
    }

    private LivingEntity findBestTarget() {
        return StreamSupport.stream(AimAssist.client.world.getEntities().spliterator(), false).filter(this::isValidTarget).map(param0 -> (LivingEntity)param0).min(Comparator.comparingDouble(this::getYawDifference).thenComparingDouble(param0 -> AimAssist.client.player.squaredDistanceTo((Entity)param0))).orElse(null);
    }

    @EventHandler(2)
    public void onUpdate(EventUpdate param1) {
        float v11;
        Object v3;
        AttackAura v2;
        if (AimAssist.client.player == null || AimAssist.client.world == null) {
            return;
        }
        if (AimAssist.client.currentScreen != null) {
            return;
        }
        if (!AimAssist.client.options.getPerspective().isFirstPerson()) {
            return;
        }
        if (((Boolean)this.disableWithAura.getValue()).booleanValue() && (v2 = Minced.getInstance().getModuleManager().method177()) != null && v2.isEnabled() && AttackAura.method861() != null) {
            boolean v4;
            v3 = v2.method799() != null ? (String)v2.method799().getValue() : "";
            boolean bl = v4 = v3 != null && ((String)v3).toLowerCase().contains(AURA_COMPATIBLE_MODE_MARKER);
            if (!v4) {
                return;
            }
        }
        if (((Boolean)this.weaponOnly.getValue()).booleanValue() && !(AimAssist.client.player.getMainHandStack().getItem() instanceof SwordItem)) {
            return;
        }
        if (((Boolean)this.requireAttackInput.getValue()).booleanValue() && !AimAssist.client.options.attackKey.isPressed()) {
            return;
        }
        LivingEntity v2s = this.getLockedTarget();
        if (v2s == null) {
            return;
        }
        if (((Boolean)this.hesitation.getValue()).booleanValue()) {
            if (this.hesitationTicks > 0) {
                --this.hesitationTicks;
                return;
            }
            if (this.random.nextFloat() < HESITATION_CHANCE) {
                this.hesitationTicks = 1 + this.random.nextInt(2);
                return;
            }
        }
        float[] v3s = this.calculateRotation((LivingEntity)v2s);
        float v4 = MathHelper.wrapDegrees((float)(v3s[0] - AimAssist.client.player.getYaw()));
        float v5 = MathHelper.wrapDegrees((float)(v3s[1] - AimAssist.client.player.getPitch()));
        if (Math.abs(v4) > ((Float)this.fov.getValue()).floatValue() * FOV_VALIDATION_SCALE) {
            return;
        }
        if (((Boolean)this.stopNearTarget.getValue()).booleanValue() && !((Boolean)this.followMouse.getValue()).booleanValue() && Math.abs(v4) < YAW_DEAD_ZONE && Math.abs(v5) < PITCH_DEAD_ZONE) {
            return;
        }
        float v6 = MathHelper.clamp((float)((SMOOTHNESS_BASE - ((Float)this.smoothness.getValue()).floatValue()) / SMOOTHNESS_DIVISOR), (float)MIN_SMOOTHNESS_FACTOR, (float)1.0f);
        float v7 = ((Float)this.aimStrength.getValue()).floatValue();
        float v8 = ((Float)this.aimSpeed.getValue()).floatValue();
        float v9 = MathHelper.clamp((float)(v4 * v7 * v6), (float)(-v8), (float)v8);
        if (((Boolean)this.humanized.getValue()).booleanValue()) {
            v9 += (this.random.nextFloat() - RANDOM_CENTER) * HUMANIZED_YAW_NOISE;
        }
        float v10 = 0.0f;
        if (((Boolean)this.verticalAim.getValue()).booleanValue()) {
            v11 = ((Float)this.verticalSpeed.getValue()).floatValue();
            v10 = MathHelper.clamp((float)(v5 * v7 * v6), (float)(-v11), (float)v11);
            if (((Boolean)this.verticalNoise.getValue()).booleanValue() && Math.abs(v9) > VERTICAL_NOISE_YAW_THRESHOLD) {
                v10 += (this.random.nextFloat() - VERTICAL_RANDOM_CENTER) * ((Float)this.noiseAmount.getValue()).floatValue();
            }
            if (((Boolean)this.humanized.getValue()).booleanValue()) {
                v10 += (this.random.nextFloat() - PITCH_RANDOM_CENTER) * HUMANIZED_PITCH_NOISE;
            }
        }
        v11 = AimAssist.client.player.getYaw() + v9;
        float v12 = MathHelper.clamp((float)(AimAssist.client.player.getPitch() + v10), (float)MIN_PITCH, (float)MAX_PITCH);
        float v13 = AimAssist.applyMouseGcd(AimAssist.client.player.getYaw(), v11);
        float v14 = AimAssist.applyMouseGcd(AimAssist.client.player.getPitch(), v12);
        AimAssist.client.player.setYaw(v13);
        AimAssist.client.player.headYaw = v13;
        AimAssist.client.player.bodyYaw = v13;
        AimAssist.client.player.setPitch(v14);
    }

    private double getYawDifference(LivingEntity param1) {
        float[] v2 = this.calculateRotation(param1);
        return Math.abs(MathHelper.wrapDegrees((float)(v2[0] - AimAssist.client.player.getYaw())));
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.lockedTarget = null;
        this.targetLockExpiresAt = 0L;
        this.hesitationTicks = 0;
    }

    static {
        MODULE_NAME = "AimAssist";
        MODULE_DESCRIPTION = "Помощь в наводке";
        TARGETS_SETTING_NAME = "Кого наводить";
        PLAYERS_TARGET_NAME = "Игроков";
        FRIENDS_TARGET_NAME = "Друзей";
        UNARMORED_TARGET_NAME = "Голых";
        VILLAGERS_TARGET_NAME = "Жителей";
        ANIMALS_TARGET_NAME = "Животных";
        MOBS_TARGET_NAME = "Мобов";
        TARGET_LOCK_TIME_SETTING_NAME = "Время блокировки";
        DEFAULT_TARGET_LOCK_SECONDS = 2.7f;
        MAX_TARGET_LOCK_SECONDS = 8.0f;
        TARGET_LOCK_STEP = 0.1f;
        AIM_RANGE_SETTING_NAME = "Дистанция наводки";
        DEFAULT_AIM_RANGE = 4.0f;
        MAX_AIM_RANGE = 8.0f;
        AIM_RANGE_STEP = 0.1f;
        AIM_STRENGTH_SETTING_NAME = "Сила наводки";
        DEFAULT_AIM_STRENGTH = 1.25f;
        MIN_AIM_STRENGTH = 0.1f;
        AIM_STRENGTH_STEP = 0.01f;
        AIM_SPEED_SETTING_NAME = "Скорость прицеливания";
        DEFAULT_AIM_SPEED = 10.0f;
        MIN_AIM_SPEED = 0.1f;
        MAX_AIM_SPEED = 20.0f;
        AIM_SPEED_STEP = 0.1f;
        SMOOTHNESS_SETTING_NAME = "Значение плавности";
        DEFAULT_SMOOTHNESS = 8.5f;
        MAX_SMOOTHNESS = 10.0f;
        SMOOTHNESS_STEP = 0.1f;
        FOV_SETTING_NAME = "FOV";
        DEFAULT_FOV = 70.0f;
        MIN_FOV = 15.0f;
        MAX_FOV = 180.0f;
        HUMANIZED_SETTING_NAME = "Более \"человеческий\"";
        VERTICAL_AIM_SETTING_NAME = "Включить вертикальную наводку";
        VERTICAL_SPEED_SETTING_NAME = "Скорость вертикальн.";
        DEFAULT_VERTICAL_SPEED = 0.23f;
        MIN_VERTICAL_SPEED = 0.01f;
        VERTICAL_SPEED_STEP = 0.01f;
        VERTICAL_NOISE_SETTING_NAME = "Шум вертикальной оси при горизонтальном движении";
        NOISE_AMOUNT_SETTING_NAME = "Значение шума";
        DEFAULT_NOISE_AMOUNT = 0.13f;
        MAX_NOISE_AMOUNT = 0.5f;
        NOISE_AMOUNT_STEP = 0.01f;
        HESITATION_SETTING_NAME = "Генерировать \"затуп\"";
        WEAPON_ONLY_SETTING_NAME = "Только при оружии";
        STOP_NEAR_TARGET_SETTING_NAME = "Не двигать при противнике";
        REQUIRE_INPUT_SETTING_NAME = "От ввода";
        POSITION_PREDICTION_SETTING_NAME = "Предугадывание позиции";
        MAX_POSITION_PREDICTION = 1.5f;
        POSITION_PREDICTION_STEP = 0.05f;
        KNOCKBACK_PREDICTION_SETTING_NAME = "Предикт отталкивания";
        MAX_KNOCKBACK_PREDICTION = 1.5f;
        KNOCKBACK_PREDICTION_STEP = 0.05f;
        MULTIPOINT_SETTING_NAME = "Система мультипоинт";
        FOLLOW_MOUSE_SETTING_NAME = "Доводить при движении мыши";
        VISIBLE_ONLY_SETTING_NAME = "Только видимых";
        DISABLE_WITH_AURA_SETTING_NAME = "Выключать с AttackAura";
        AURA_COMPATIBLE_MODE_MARKER = "fov90";
        HESITATION_CHANCE = 0.045f;
        FOV_VALIDATION_SCALE = 0.5f;
        YAW_DEAD_ZONE = 0.35f;
        PITCH_DEAD_ZONE = 0.3f;
        SMOOTHNESS_BASE = 10.0f;
        SMOOTHNESS_DIVISOR = 10.0f;
        MIN_SMOOTHNESS_FACTOR = 0.03f;
        RANDOM_CENTER = 0.5f;
        HUMANIZED_YAW_NOISE = 0.22f;
        VERTICAL_NOISE_YAW_THRESHOLD = 0.01f;
        VERTICAL_RANDOM_CENTER = 0.5f;
        PITCH_RANDOM_CENTER = 0.5f;
        HUMANIZED_PITCH_NOISE = 0.08f;
        MIN_PITCH = -89.0f;
        MAX_PITCH = 89.0f;
        MILLISECONDS_PER_SECOND = 1000.0f;
        FOV_HALF_SCALE = 0.5f;
        PRIMARY_AIM_HEIGHT_FACTOR = 0.62;
        SNEAKING_HEIGHT_OFFSET = 0.08;
        YAW_OFFSET = 90.0;
        SECONDARY_AIM_HEIGHT_FACTOR = 0.45;
        MULTIPOINT_BLEND_FACTOR = 0.5f;
        BASE_VELOCITY_PREDICTION = 0.5;
        BASE_STRAFE_PREDICTION = 0.5;
        SENSITIVITY_SCALE = 0.6f;
        SENSITIVITY_OFFSET = 0.2f;
        GCD_MULTIPLIER = 8.0;
        GCD_INPUT_SCALE = 0.15f;
        GCD_OUTPUT_SCALE = 0.15f;
    }
}




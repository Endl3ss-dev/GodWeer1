/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gl.ShaderProgramKey
 *  net.minecraft.client.gl.ShaderProgramKeys
 *  net.minecraft.client.render.BufferBuilder
 *  net.minecraft.client.render.BufferRenderer
 *  net.minecraft.client.render.BuiltBuffer
 *  net.minecraft.client.render.Tessellator
 *  net.minecraft.client.render.VertexFormat$DrawMode
 *  net.minecraft.client.render.VertexFormats
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.decoration.ArmorStandEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.BowItem
 *  net.minecraft.item.CrossbowItem
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  org.joml.Matrix4f
 *  org.lwjgl.opengl.GL11
 *  sg.ec.t0
 */
package minced.module.combat;
import minced.core.EventHandler;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Comparator;
import java.util.function.BooleanSupplier;
import java.util.stream.StreamSupport;
import minced.core.ColorUtil;
import minced.core.MultiBooleanSetting;
import minced.core.BooleanSetting;
import minced.core.SliderSetting;
import minced.core.EventUpdate;
import minced.core.EventSwapWorld;
import minced.core.EventRender2D;
import minced.core.Rotation;
import minced.core.ThemeColors;
import minced.core.EntityFilter;
import minced.core.AimRotation;
import minced.core.Module;
import minced.enums.Combat;
import minced.enums.MAIN;
import minced.module.combat.AntiBot;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

public class AimBot
extends Module
implements BooleanSupplier {
    static MinecraftClient client = MinecraftClient.getInstance();
    BooleanSetting renderFov;
    SliderSetting fov;
    static float FULL_FOV;
    static double HALF_ANGLE_DIVISOR;
    static double FOV_DIVISOR;
    static float FOV_CIRCLE_ALPHA;
    static float FOV_CIRCLE_LINE_WIDTH;
    static double FULL_CIRCLE_RADIANS;
    LivingEntity target;
    SliderSetting range = new SliderSetting(RANGE_SETTING_NAME, DEFAULT_RANGE, MIN_RANGE, MAX_RANGE, 1.0f);
    BooleanSetting visibleOnly;
    static float UNRESTRICTED_FOV;
    static double HALF_FOV_DIVISOR;
    MultiBooleanSetting targets;
    static float PROJECTILE_GRAVITY;
    static float PROJECTILE_DRAG;
    static double TARGET_CENTER_DIVISOR;
    static double YAW_OFFSET;
    static String MODULE_NAME;
    static String MODULE_DESCRIPTION;
    static String RANGE_SETTING_NAME;
    static float DEFAULT_RANGE;
    static float MIN_RANGE;
    static float MAX_RANGE;
    static String AIM_SPEED_SETTING_NAME;
    static float DEFAULT_AIM_SPEED;
    static float MIN_AIM_SPEED;
    static float MAX_AIM_SPEED;
    SliderSetting aimSpeed = new SliderSetting(AIM_SPEED_SETTING_NAME, DEFAULT_AIM_SPEED, MIN_AIM_SPEED, MAX_AIM_SPEED, 1.0f);
    static String FOV_SETTING_NAME;
    static float DEFAULT_FOV;
    static float MIN_FOV;
    static float MAX_FOV;
    static String RENDER_FOV_SETTING_NAME;
    static String PREDICT_PROJECTILE_SETTING_NAME;
    BooleanSetting predictProjectile;
    static String TARGETS_SETTING_NAME;
    static String PLAYERS_TARGET_NAME;
    static String FRIENDS_TARGET_NAME;
    static String UNARMORED_TARGET_NAME;
    static String ANIMALS_TARGET_NAME;
    static String MONSTERS_TARGET_NAME;
    static String VISIBLE_ONLY_SETTING_NAME;
    static double DIRECT_AIM_HEIGHT_DIVISOR;
    static double DIRECT_AIM_YAW_OFFSET;
    static float ROTATION_PRIORITY;
    static float BOW_PROJECTILE_SPEED;
    static float CROSSBOW_PROJECTILE_SPEED;
    static float TRIDENT_PROJECTILE_SPEED;
    static float DEFAULT_PROJECTILE_SPEED;

    private boolean isWeaponReady() {
        ItemStack v1 = AimBot.client.player.getMainHandStack();
        Item v2 = v1.getItem();
        if (v2 instanceof BowItem) {
            return AimBot.client.player.isUsingItem() && AimBot.client.player.getActiveItem() == v1;
        }
        if (v2 instanceof CrossbowItem) {
            return CrossbowItem.isCharged((ItemStack)v1);
        }
        if (v2 == Items.TRIDENT) {
            return AimBot.client.player.isUsingItem() && AimBot.client.player.getActiveItem() == v1;
        }
        return false;
    }

    @EventHandler(2)
    private void renderFovCircle(EventRender2D param1) {
        if (!((Boolean)this.renderFov.getValue()).booleanValue() || AimBot.client.player == null || ((Float)this.fov.getValue()).floatValue() >= FULL_FOV || !this.isHoldingProjectileWeapon()) {
            return;
        }
        float v2 = (float)client.getWindow().getWidth() / 2.0f;
        float v3 = (float)client.getWindow().getHeight() / 2.0f;
        float v4 = v2 / 2.0f;
        float v5 = v3 / 2.0f;
        float v6 = ((Integer)AimBot.client.options.getFov().getValue()).intValue();
        float v7 = v3 / 2.0f;
        float v8 = (float)((double)v7 * Math.tan(Math.toRadians((double)((Float)this.fov.getValue()).floatValue() / HALF_ANGLE_DIVISOR)) / Math.tan(Math.toRadians((double)v6 / FOV_DIVISOR)));
        if (v8 <= 0.0f || v8 > v2 || v8 > v3) {
            return;
        }
        int v9 = Math.clamp((long)((int)(v8 * 2.0f)), 32, 360);
        int v10 = ThemeColors.method3199(MAIN.TEXT);
        int v11 = ColorUtil.method1848(v10, FOV_CIRCLE_ALPHA);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader((ShaderProgramKey)ShaderProgramKeys.POSITION_COLOR);
        GL11.glEnable((int)2848);
        GL11.glHint((int)3154, (int)4354);
        RenderSystem.lineWidth((float)FOV_CIRCLE_LINE_WIDTH);
        Matrix4f v12 = param1.method2997().getMatrices().peek().getPositionMatrix();
        BufferBuilder v13 = Tessellator.getInstance().begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
        for (int v14 = 0; v14 <= v9; ++v14) {
            double v15 = FULL_CIRCLE_RADIANS * (double)v14 / (double)v9;
            float v17 = v4 + (float)Math.cos(v15) * v8;
            float v18 = v5 + (float)Math.sin(v15) * v8;
            v13.vertex(v12, v17, v18, 0.0f).color(v11);
        }
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)v13.end());
        GL11.glDisable((int)2848);
        RenderSystem.lineWidth((float)2.0f);
        RenderSystem.disableBlend();
    }

    private boolean isHoldingProjectileWeapon() {
        Item v1 = AimBot.client.player.getMainHandStack().getItem();
        return v1 instanceof BowItem || v1 instanceof CrossbowItem || v1 == Items.TRIDENT;
    }

    @Override
    public void onDisable() {
        this.target = null;
        super.onDisable();
    }

    private boolean isValidTarget(LivingEntity param1) {
        if (param1 == null || !param1.isAlive() || param1 == AimBot.client.player || param1 instanceof ArmorStandEntity) {
            return false;
        }
        if (AimBot.client.player.distanceTo((Entity)param1) > ((Float)this.range.getValue()).floatValue()) {
            return false;
        }
        if (((Boolean)this.visibleOnly.getValue()).booleanValue() && !AimBot.client.player.canSee((Entity)param1)) {
            return false;
        }
        if (((Float)this.fov.getValue()).floatValue() < UNRESTRICTED_FOV) {
            Vec3d toTarget = param1.getPos().add(0.0, (double)(param1.getHeight() / 2.0f), 0.0).subtract(AimBot.client.player.getEyePos()).normalize();
            Vec3d lookVector = AimBot.client.player.getRotationVec(1.0f);
            double angle = Math.toDegrees(Math.acos(Math.min(1.0, lookVector.dotProduct(toTarget))));
            if (angle > (double)((Float)this.fov.getValue()).floatValue() / HALF_FOV_DIVISOR) {
                return false;
            }
        }
        if (param1 instanceof PlayerEntity player && AntiBot.method1033(player)) {
            return false;
        }
        return EntityFilter.method3435((Entity)param1, this.targets, true) || EntityFilter.method3439(param1, this.targets) || EntityFilter.method3437(param1, this.targets);
    }

    private int estimateFlightTicks(double param1, float param3, float param4) {
        double v5 = 0.0;
        double v7 = param3;
        for (int v9 = 0; v9 < 300; ++v9) {
            if ((v5 += v7) >= param1) {
                return v9 + 1;
            }
            v7 *= (double)param4;
        }
        return 300;
    }

    @EventHandler(2)
    private void onWorldChange(EventSwapWorld param1) {
        this.target = null;
    }

    private float[] calculatePredictedRotation(LivingEntity param1, Vec3d param2) {
        float v3 = this.getProjectileSpeed();
        float v4 = PROJECTILE_GRAVITY;
        float v5 = PROJECTILE_DRAG;
        Vec3d v6 = param1.getPos().add(0.0, (double)param1.getHeight() / TARGET_CENTER_DIVISOR, 0.0);
        Vec3d v7 = new Vec3d(param1.getX() - param1.prevX, param1.getY() - param1.prevY, param1.getZ() - param1.prevZ);
        Vec3d v8 = v6;
        int v9 = 0;
        for (int v10 = 0; v10 < 5; ++v10) {
            double v11 = v8.x - param2.x;
            double v13 = v8.z - param2.z;
            v9 = this.estimateFlightTicks(Math.sqrt(v11 * v11 + v13 * v13), v3, v5);
            v8 = v6.add(v7.multiply((double)v9));
        }
        Vec3d v10 = v8.add(0.0, this.calculateProjectileDrop(v9, v4, v5), 0.0).subtract(param2);
        return new float[]{(float)MathHelper.wrapDegrees((double)(Math.toDegrees(Math.atan2(v10.z, v10.x)) - YAW_OFFSET)), (float)(-Math.toDegrees(Math.atan2(v10.y, Math.hypot(v10.x, v10.z))))};
    }

    public AimBot() {
        super(MODULE_NAME, MODULE_DESCRIPTION, Combat.Combat);
        this.fov = new SliderSetting(FOV_SETTING_NAME, DEFAULT_FOV, MIN_FOV, MAX_FOV, 1.0f);
        this.renderFov = new BooleanSetting(RENDER_FOV_SETTING_NAME, true);
        this.predictProjectile = new BooleanSetting(PREDICT_PROJECTILE_SETTING_NAME, true);
        this.targets = new MultiBooleanSetting(TARGETS_SETTING_NAME, new BooleanSetting(PLAYERS_TARGET_NAME, true), new BooleanSetting(FRIENDS_TARGET_NAME, false), new BooleanSetting(UNARMORED_TARGET_NAME, true), new BooleanSetting(ANIMALS_TARGET_NAME, false), new BooleanSetting(MONSTERS_TARGET_NAME, false));
        this.visibleOnly = new BooleanSetting(VISIBLE_ONLY_SETTING_NAME, true);
        this.addSettings(this.range, this.aimSpeed, this.fov, this.renderFov, this.predictProjectile, this.targets, this.visibleOnly);
    }

    @EventHandler(2)
    private void onUpdate(EventUpdate param1) {
        float v4;
        float v3;
        if (AimBot.client.player == null || AimBot.client.world == null || !this.isHoldingProjectileWeapon() || !this.isWeaponReady()) {
            this.target = null;
            return;
        }
        this.target = this.findNearestTarget();
        if (this.target == null) {
            return;
        }
        Vec3d v2 = AimBot.client.player.getEyePos();
        if (((Boolean)this.predictProjectile.getValue()).booleanValue()) {
            float[] v5 = this.calculatePredictedRotation(this.target, v2);
            v3 = v5[0];
            v4 = v5[1];
        } else {
            Vec3d v5 = this.target.getPos().add(0.0, (double)this.target.getHeight() / DIRECT_AIM_HEIGHT_DIVISOR, 0.0).subtract(v2);
            v3 = (float)MathHelper.wrapDegrees((double)(Math.toDegrees(Math.atan2(v5.z, v5.x)) - DIRECT_AIM_YAW_OFFSET));
            v4 = (float)(-Math.toDegrees(Math.atan2(v5.y, Math.hypot(v5.x, v5.z))));
        }
        AimRotation.method369(new Rotation(v3, v4), ((Float)this.aimSpeed.getValue()).floatValue(), ROTATION_PRIORITY, 2, 1);
    }

    private LivingEntity findNearestTarget() {
        return StreamSupport.stream(AimBot.client.world.getEntities().spliterator(), false).filter(param0 -> param0 instanceof LivingEntity).map(param0 -> (LivingEntity)param0).filter(this::isValidTarget).min(Comparator.comparingDouble(param0 -> param0.distanceTo((Entity)AimBot.client.player))).orElse(null);
    }

    private double calculateProjectileDrop(int param1, float param2, float param3) {
        double v4 = 0.0;
        double v6 = 0.0;
        for (int v8 = 0; v8 < param1; ++v8) {
            v4 *= (double)param3;
            v6 += (v4 -= (double)param2);
        }
        return -v6;
    }

    private float getProjectileSpeed() {
        ItemStack v1 = AimBot.client.player.getMainHandStack();
        Item v2 = v1.getItem();
        if (v2 instanceof BowItem) {
            return BOW_PROJECTILE_SPEED;
        }
        if (v2 instanceof CrossbowItem) {
            return CROSSBOW_PROJECTILE_SPEED;
        }
        if (v2 == Items.TRIDENT) {
            return TRIDENT_PROJECTILE_SPEED;
        }
        return DEFAULT_PROJECTILE_SPEED;
    }

    @Override
    public boolean getAsBoolean() {
        return AimBot.client != null && AimBot.client.player != null && this.isHoldingProjectileWeapon();
    }

    static {
        MODULE_NAME = "AimBot";
        MODULE_DESCRIPTION = "Автоматически наводится на цель при использовании лука, арбалета или трезубца";
        RANGE_SETTING_NAME = "Дистанция";
        DEFAULT_RANGE = 64.0f;
        MIN_RANGE = 8.0f;
        MAX_RANGE = 128.0f;
        AIM_SPEED_SETTING_NAME = "Скорость наводки";
        DEFAULT_AIM_SPEED = 80.0f;
        MIN_AIM_SPEED = 10.0f;
        MAX_AIM_SPEED = 180.0f;
        FOV_SETTING_NAME = "FOV";
        DEFAULT_FOV = 90.0f;
        MIN_FOV = 10.0f;
        MAX_FOV = 360.0f;
        RENDER_FOV_SETTING_NAME = "Отрисовка FOV";
        PREDICT_PROJECTILE_SETTING_NAME = "Предикт снаряда";
        TARGETS_SETTING_NAME = "Цели";
        PLAYERS_TARGET_NAME = "Игроков";
        FRIENDS_TARGET_NAME = "Друзей";
        UNARMORED_TARGET_NAME = "Голых";
        ANIMALS_TARGET_NAME = "Животных";
        MONSTERS_TARGET_NAME = "Монстров";
        VISIBLE_ONLY_SETTING_NAME = "Только видимых";
        DIRECT_AIM_HEIGHT_DIVISOR = 2.0;
        DIRECT_AIM_YAW_OFFSET = 90.0;
        ROTATION_PRIORITY = 60.0f;
        FULL_FOV = 360.0f;
        HALF_ANGLE_DIVISOR = 2.0;
        FOV_DIVISOR = 2.0;
        FOV_CIRCLE_ALPHA = 0.45f;
        FOV_CIRCLE_LINE_WIDTH = 2.5f;
        FULL_CIRCLE_RADIANS = Math.PI * 2;
        PROJECTILE_GRAVITY = 0.05f;
        PROJECTILE_DRAG = 0.99f;
        TARGET_CENTER_DIVISOR = 2.0;
        YAW_OFFSET = 90.0;
        BOW_PROJECTILE_SPEED = 3.0f;
        CROSSBOW_PROJECTILE_SPEED = 3.15f;
        TRIDENT_PROJECTILE_SPEED = 2.5f;
        DEFAULT_PROJECTILE_SPEED = 1.5f;
        UNRESTRICTED_FOV = 360.0f;
        HALF_FOV_DIVISOR = 2.0;
    }
}




/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.entity.projectile.ProjectileUtil
 *  net.minecraft.entity.projectile.thrown.EnderPearlEntity
 *  net.minecraft.item.Items
 *  net.minecraft.predicate.entity.EntityPredicates
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.hit.EntityHitResult
 *  net.minecraft.util.hit.HitResult$Type
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.RaycastContext
 *  net.minecraft.world.RaycastContext$FluidHandling
 *  net.minecraft.world.RaycastContext$ShapeType
 */
package minced.module.player;
import minced.core.EventHandler;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import minced.core.BooleanSetting;
import minced.core.EventUpdate;
import minced.core.EventSwapWorld;
import minced.core.ClientState;
import minced.core.Rotation;
import minced.core.InventoryUtil;
import minced.core.AimRotation;
import minced.core.Minced;
import minced.core.Module;
import minced.enums.Combat;
import minced.module.combat.AttackAura;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.item.Items;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

public class PearlTarget
extends Module {
    static double 弟;
    static double у;
    static double 必;

    UUID field6078;
    Set field6079;
    static MinecraftClient I = MinecraftClient.getInstance();
    boolean field6080;
    UUID field6081;
    Vec3d field6082;
    boolean field6083;
    ClientState field6084;
    BooleanSetting field6085 = new BooleanSetting(field6097, false);
    static long field6086;
    static float field6087;
    static float field6088;
    static float field6089;
    static float field6090;
    static double field6091;
    static double field6092;
    static double field6093;
    static double field6094;
    static String field6095;
    static String field6096;
    static String field6097;
    static double field6098;
    static float field6099;
    static float field6100;
    static float field6101;
    static double field6102;
    static float field6103;
    static float field6104;
    static double field6105;
    static float field6106;
    static float field6107;

    @EventHandler(2)
    public void method1443(EventSwapWorld param1) {
        this.O9();
        this.field6078 = null;
        this.field6079.clear();
    }

    private Vec3d method1444(EnderPearlEntity param1) {
        return this.method1447(param1.getPos(), param1.getVelocity(), (Entity)param1);
    }

    @EventHandler(2)
    public void method1445(EventUpdate param1) {
        Object v3;
        if (PearlTarget.I.player == null || PearlTarget.I.world == null) {
            this.O9();
            return;
        }
        LivingEntity v2 = this.method1455();
        if (v2 == null || !v2.isAlive()) {
            if (this.field6080) {
                this.O9();
            }
            return;
        }
        if (!this.field6080) {
            v3 = this.method1450(v2);
            if (v3 == null) {
                return;
            }
            this.field6080 = true;
            this.field6081 = v2.getUuid();
            this.field6082 = (Vec3d)v3;
            this.field6083 = false;
            this.field6084.method3114();
        }
        if (this.field6081 == null || !this.field6081.equals(v2.getUuid()) || this.field6082 == null) {
            this.O9();
            return;
        }
        if (PearlTarget.I.player.getItemCooldownManager().isCoolingDown(Items.ENDER_PEARL.getDefaultStack()) || !this.method1448()) {
            this.O9();
            return;
        }
        if (((Boolean)this.field6085.getValue()).booleanValue() && this.method1451((Entity)v2, PearlTarget.I.player.getEyePos(), this.field6082)) {
            if (!this.field6083) {
                this.field6083 = true;
                this.field6084.method3114();
            }
            if (!this.field6084.method3113(field6086)) {
                return;
            }
            this.O9();
            return;
        }
        this.field6083 = false;
        this.field6084.method3114();
        v3 = this.method1453(this.field6082);
        AimRotation.method376((Rotation)v3, field6087, field6088, field6089, field6090, 1, 7, false);
        Rotation v4 = new Rotation(PearlTarget.I.player.getYaw(), PearlTarget.I.player.getPitch());
        if (v4.method3141((Rotation)v3) > 1.0f) {
            return;
        }
        InventoryUtil.method3323(Items.ENDER_PEARL);
        this.O9();
    }

    private LivingEntity method1446(UUID param1) {
        if (param1 == null || PearlTarget.I.world == null) {
            return null;
        }
        for (Entity v3 : PearlTarget.I.world.getEntities()) {
            LivingEntity v4;
            if (!(v3 instanceof LivingEntity) || !param1.equals((v4 = (LivingEntity)v3).getUuid())) continue;
            return v4;
        }
        return null;
    }

    private Vec3d method1447(Vec3d param1, Vec3d param22, Entity param3) {
        Entity entity;
        Vec3d v4 = param1;
        Vec3d v5 = param22;
        if (param3 instanceof EnderPearlEntity) {
            EnderPearlEntity v7 = (EnderPearlEntity)param3;
            entity = v7.getOwner();
        } else {
            entity = null;
        }
        Entity v6 = entity;
        for (int v7 = 0; v7 <= 600; ++v7) {
            Box v12;
            EntityHitResult v13;
            Vec3d v9;
            Vec3d v8 = v4;
            Vec3d v10 = v9 = v4.add(v5);
            BlockHitResult v11 = PearlTarget.I.world.raycast(new RaycastContext(v8.add(0.0, field6091, 0.0), v9, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, (Entity)PearlTarget.I.player));
            if (v11.getType() == HitResult.Type.BLOCK) {
                v10 = v11.getPos();
            }
            if ((v13 = ProjectileUtil.raycast((Entity)param3, (Vec3d)v8, (Vec3d)v10, (Box)(v12 = new Box(v8, v10).expand(field6092)), param2 -> EntityPredicates.CAN_HIT.test(param2) && param2 != param3 && param2 != v6, (double)v8.squaredDistanceTo(v10))) != null) {
                return v13.getPos();
            }
            if (v11.getType() == HitResult.Type.BLOCK) {
                return v11.getPos();
            }
            v4 = v9;
            v5 = v5.multiply(field6093).subtract(0.0, field6094, 0.0);
        }
        return v4;
    }

    private boolean method1448() {
        if (PearlTarget.I.player.getMainHandStack().isOf(Items.ENDER_PEARL) || PearlTarget.I.player.getOffHandStack().isOf(Items.ENDER_PEARL)) {
            return true;
        }
        for (int v1 = 0; v1 < 36; ++v1) {
            if (!PearlTarget.I.player.getInventory().getStack(v1).isOf(Items.ENDER_PEARL)) continue;
            return true;
        }
        return false;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.O9();
        this.field6078 = null;
        this.field6079.clear();
    }

    public PearlTarget() {
        super(field6095, field6096, Combat.Player);
        this.field6079 = new HashSet();
        this.field6084 = new ClientState();
        this.addSettings(this.field6085);
    }

    private Entity method1449(EnderPearlEntity param1) {
        Entity v2 = param1.getOwner();
        if (v2 != null) {
            return v2;
        }
        PlayerEntity v3 = null;
        for (PlayerEntity v5 : PearlTarget.I.world.getPlayers()) {
            if (v3 != null && !(v5.squaredDistanceTo((Entity)param1) < v3.squaredDistanceTo((Entity)param1))) continue;
            v3 = v5;
        }
        return v3;
    }

    private Vec3d method1450(LivingEntity param1) {
        if (this.field6079.size() > 256) {
            this.field6079.clear();
        }
        for (Entity v3 : PearlTarget.I.world.getEntities()) {
            Entity v6;
            UUID v5;
            if (!(v3 instanceof EnderPearlEntity)) continue;
            EnderPearlEntity v4 = (EnderPearlEntity)v3;
            if (v4.age > 2 || this.field6079.contains(v5 = v4.getUuid()) || (v6 = this.method1449(v4)) == null || !v6.getUuid().equals(param1.getUuid())) continue;
            this.field6079.add(v5);
            return this.method1444(v4);
        }
        return null;
    }

    private boolean method1451(Entity param12, Vec3d param2, Vec3d param3) {
        Box v4 = new Box(param2, param3).expand(field6098);
        EntityHitResult v5 = ProjectileUtil.raycast((Entity)PearlTarget.I.player, (Vec3d)param2, (Vec3d)param3, (Box)v4, param1 -> EntityPredicates.CAN_HIT.test(param1) && param1 != PearlTarget.I.player && param1 != param12 && !(param1 instanceof EnderPearlEntity), (double)param2.squaredDistanceTo(param3));
        return v5 != null;
    }

    private Rotation method1453(Vec3d param1) {
        float v7;
        Vec3d v2 = PearlTarget.I.player.getEyePos();
        Vec3d v3 = param1.subtract(v2);
        double v4 = Math.sqrt(v3.x * v3.x + v3.z * v3.z);
        float v6 = MathHelper.wrapDegrees((float)((float)Math.toDegrees(Math.atan2(v3.z, v3.x)) - field6099));
        float v8 = v7 = MathHelper.clamp((float)((float)(-Math.toDegrees(Math.atan2(v3.y, v4)))), (float)field6100, (float)field6101);
        double v9 = field6102;
        for (float v11 = field6103; v11 <= field6104; v11 += 1.0f) {
            Vec3d v12 = Rotation.method3137(v11, v6);
            Vec3d v13 = v12.multiply(field6105).add(PearlTarget.I.player.getVelocity());
            Vec3d v14 = this.method1447(v2, v13, (Entity)PearlTarget.I.player);
            double v15 = v14.squaredDistanceTo(param1);
            if (!(v15 < v9)) continue;
            v9 = v15;
            v8 = v11;
        }
        return new Rotation(v6, MathHelper.clamp((float)v8, (float)field6106, (float)field6107));
    }

    private LivingEntity method1455() {
        LivingEntity v3;
        LivingEntity v2;
        AttackAura v1;
        LivingEntity v1s;
        if (this.field6081 != null && (v1s = this.method1446(this.field6081)) != null && v1s.isAlive()) {
            return v1s;
        }
        v1 = Minced.getInstance().getModuleManager().method177();
        LivingEntity livingEntity = v2 = v1 != null && v1.isEnabled() ? AttackAura.method861() : null;
        if (v2 != null && v2.isAlive()) {
            this.field6078 = v2.getUuid();
            return v2;
        }
        if (this.field6078 != null && (v3 = this.method1446(this.field6078)) != null && v3.isAlive()) {
            return v3;
        }
        return null;
    }

    private void O9() {
        this.field6080 = false;
        this.field6081 = null;
        this.field6082 = null;
        this.field6083 = false;
        this.field6084.method3114();
    }

    static {
        弟 = 0.0;
        у = 0.0;
        必 = 0.0;
        field6095 = "Pearl Target";
        field6096 = "Throws an ender pearl after aura target pearl";
        field6097 = "No Through Entities";
        field6086 = 500L;
        field6087 = 360.0f;
        field6088 = 360.0f;
        field6089 = 360.0f;
        field6090 = 360.0f;
        field6098 = 0.3;
        field6099 = 90.0f;
        field6100 = -89.0f;
        field6101 = 89.0f;
        field6102 = Double.MAX_VALUE;
        field6103 = -89.0f;
        field6104 = 89.0f;
        field6105 = 1.5;
        field6106 = -89.0f;
        field6107 = 89.0f;
        field6091 = 0.1;
        field6092 = 0.3;
        field6093 = 0.99;
        field6094 = 0.031415926535897934;
    }
}




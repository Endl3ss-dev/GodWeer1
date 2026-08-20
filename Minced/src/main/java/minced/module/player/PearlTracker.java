/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Blocks
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.entity.projectile.thrown.EnderPearlEntity
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.Style
 *  net.minecraft.text.Text
 *  net.minecraft.util.Formatting
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.hit.HitResult$Type
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Position
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.RaycastContext
 *  net.minecraft.world.RaycastContext$FluidHandling
 *  net.minecraft.world.RaycastContext$ShapeType
 *  sLM.4ZXYEo
 */
package minced.module.player;
import minced.core.EventHandler;

import java.util.function.BooleanSupplier;
import java.util.UUID;
import minced.core.CommandGps;
import minced.core.BooleanSetting;
import minced.core.EventUpdate;
import minced.core.ChatUtil;
import minced.core.Minced;
import minced.core.Module;
import minced.enums.Combat;
import minced.module.combat.AttackAura;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import sLM._4ZXYEo;

public class PearlTracker
extends Module {
    static String cfr_renamed_395;

    boolean field6737 = true;
    PlayerEntity field6738 = null;
    boolean field6739 = false;
    double field6740 = 0.0;
    double field6741 = 0.0;
    UUID lastPearlId = null;
    static double field6742;
    static double field6743;
    static MinecraftClient I = MinecraftClient.getInstance();
    static float field6744;
    static float field6745;
    static double field6746;
    static double field6747;
    static double field6748;
    static String field6749;
    static String field6750;
    static String field6751;
    BooleanSetting field6752 = new BooleanSetting(field6751, false);
    static String field6753;
    BooleanSetting field6754 = new BooleanSetting(field6753, false);
    static String field6755;
    static String field6756;
    static double field6757;
    static double field6758;
    static double field6759;

    @Override
    public void onEnable() {
        this.field6737 = true;
        this.field6738 = null;
        this.field6739 = false;
        this.field6740 = 0.0;
        this.field6741 = 0.0;
        this.lastPearlId = null;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        if (this.field6739 && CommandGps.field7516 && Math.abs(CommandGps.field7518 - this.field6740) < field6742 && Math.abs(CommandGps.field7519 - this.field6741) < field6743) {
            CommandGps.field7516 = false;
            CommandGps.field7518 = 0.0;
            CommandGps.field7519 = 0.0;
        }
        this.field6739 = false;
        super.onDisable();
    }

    private Vec3d method1622(EnderPearlEntity param1) {
        Vec3d v2 = param1.getPos();
        Vec3d v3 = param1.getVelocity();
        Vec3d v4 = v2;
        for (int v5 = 0; v5 <= 300; ++v5) {
            v4 = v2;
            v2 = v2.add(v3);
            Vec3d v6 = v3;
            if (param1.isTouchingWater() || PearlTracker.I.world.getFluidState(BlockPos.ofFloored(v2)).isIn(FluidTags.WATER)) {
                float v7 = param1 instanceof EnderPearlEntity ? field6744 : field6745;
                v6 = v6.multiply((double)v7);
            } else {
                v6 = v6.multiply(field6746);
            }
            if (!param1.hasNoGravity()) {
                v6 = new Vec3d(v6.x, v6.y - (param1 instanceof EnderPearlEntity ? field6747 : field6748), v6.z);
            }
            v3 = v6;
            RaycastContext v7 = new RaycastContext(v4, v2, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, (Entity)PearlTracker.I.player);
            BlockHitResult v8 = PearlTracker.I.world.raycast(v7);
            if (v8.getType() != HitResult.Type.BLOCK && !(v2.y <= 0.0)) continue;
            return v8.getPos();
        }
        return v4;
    }

    public PearlTracker() {
        super(field6749, field6750, Combat.Player);
        this.addSettings(this.field6752, this.field6754);
    }

    @EventHandler(2)
    public void method1623(EventUpdate param1) {
        LivingEntity v12;
        AttackAura v11;
        Object v62;
        Entity v42;
        if (PearlTracker.I.world == null || PearlTracker.I.player == null) {
            return;
        }
        EnderPearlEntity v2 = null;
        for (Entity v42_144 : PearlTracker.I.world.getEntities()) {
            if (!(v42_144 instanceof EnderPearlEntity) || v42_144.getUuid().equals(this.lastPearlId)) continue;
            v2 = (EnderPearlEntity)v42_144;
            break;
        }
        if (v2 == null) {
            return;
        }
        this.field6737 = true;
        this.lastPearlId = v2.getUuid();
        PlayerEntity v3 = null;
        v42 = v2.getOwner();
        if (v42 instanceof PlayerEntity) {
            v3 = (PlayerEntity)v42;
        } else {
            for (Object v62_159 : PearlTracker.I.world.getPlayers()) {
                if (v3 != null && !(((PlayerEntity)v62_159).squaredDistanceTo((Entity)v2) < v3.squaredDistanceTo((Entity)v2))) continue;
                v3 = (PlayerEntity)v62_159;
            }
        }
        if (v3 == null) {
            return;
        }
        if (v3 == PearlTracker.I.player && !((Boolean)this.field6752.getValue()).booleanValue()) {
            return;
        }
        Vec3d v5 = this.method1622(v2);
        v62 = _4ZXYEo.cfr_renamed_394("makeConcatWithConstants", new Object[]{}, (int)((int)v5.getX()), (int)((int)v5.getY()), (int)((int)v5.getZ()));
        MutableText v7 = v3.getDisplayName().copy();
        MutableText v8 = Text.literal((String)field6755).setStyle(Style.EMPTY.withColor(Formatting.WHITE));
        MutableText v9 = Text.literal((String)field6756).setStyle(Style.EMPTY.withColor(Formatting.WHITE));
        MutableText v10 = Text.literal((String)v62).setStyle(Style.EMPTY.withColor(Formatting.GREEN));
        if (this.field6737) {
            ChatUtil.method3308(Text.literal((String)"").append((Text)v7).append((Text)v8).append((Text)v9).append((Text)v10), new Object[0]);
            this.field6737 = false;
            this.field6738 = v3;
            if (((Boolean)this.field6754.getValue()).booleanValue()) {
                v11 = Minced.getInstance().getModuleManager().method177();
                v12 = AttackAura.method861();
                if (v11 != null && v11.isEnabled() && v12 != null && v3 == v12) {
                    this.field6740 = v5.getX();
                    this.field6741 = v5.getZ();
                    CommandGps.field7516 = true;
                    CommandGps.field7518 = this.field6740;
                    CommandGps.field7519 = this.field6741;
                    this.field6739 = true;
                }
            }
        }
        if (this.field6739 && ((Boolean)this.field6754.getValue()).booleanValue() && CommandGps.field7516) {
            v11 = Minced.getInstance().getModuleManager().method177();
            v12 = AttackAura.method861();
            if (v11 == null || !v11.isEnabled() || v12 == null || this.field6738 == null || v12 != this.field6738) {
                CommandGps.field7516 = false;
                CommandGps.field7518 = 0.0;
                CommandGps.field7519 = 0.0;
                this.field6739 = false;
                return;
            }
            double v13 = PearlTracker.I.player.getX();
            double v15 = PearlTracker.I.player.getZ();
            double v17 = Math.sqrt(Math.pow(this.field6740 - v13, field6757) + Math.pow(this.field6741 - v15, field6758));
            if (v17 <= field6759) {
                CommandGps.field7516 = false;
                CommandGps.field7518 = 0.0;
                CommandGps.field7519 = 0.0;
                this.field6739 = false;
            }
        }
    }

    static {
        field6749 = "Pearl Tracker";
        field6750 = "Логирует броски и падения эндер-жемчужин с координатами";
        field6751 = "Реагировать на вас";
        field6753 = "Авто GPS до пёрла";
        field6742 = 0.1;
        field6743 = 0.1;
        cfr_renamed_395 = "\u0001 \u0001 \u0001";
        field6755 = " - кинул пёрл на координаты ";
        field6756 = "XYZ: ";
        field6757 = 2.0;
        field6758 = 2.0;
        field6759 = 5.0;
        field6744 = 0.8f;
        field6745 = 0.6f;
        field6746 = 0.99f;
        field6747 = 0.03;
        field6748 = 0.05;
    }
}




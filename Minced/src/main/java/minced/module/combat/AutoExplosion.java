/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.RespawnAnchorBlock
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.ItemEntity
 *  net.minecraft.entity.decoration.EndCrystalEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.Item
 *  net.minecraft.item.Items
 *  net.minecraft.util.Hand
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Position
 *  net.minecraft.util.math.Vec3d
 *  sg.ec.t0
 */
package minced.module.combat;
import minced.core.EventHandler;

import minced.core.BooleanSetting;
import minced.core.EventClickBlockRight;
import minced.core.EventBlockPlace;
import minced.core.EventUpdate;
import minced.core.EventSwapWorld;
import minced.core.Rotation;
import minced.core.AimRotation;
import minced.core.Module;
import minced.enums.Combat;
import minced.mixin.accessor.IClientPlayerInteractionManagerMixin;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import sg.ec.t0;

public class AutoExplosion
extends Module {
    static MinecraftClient I = MinecraftClient.getInstance();
    BooleanSetting field6167;
    BlockPos field6168;
    int field6169;
    boolean field6170;
    boolean field6171;
    static double field6172;
    Box field6173;
    static String field6174;
    static String field6175;
    static String field6176;
    BooleanSetting field6177 = new BooleanSetting(field6176, true);
    static String field6178;
    static String field6179;
    BooleanSetting field6180;
    static double field6181;
    static float field6182;
    boolean field6183;
    int field6184;
    static double field6185;
    static float field6186;

    private boolean minced$isReady() {
        return AutoExplosion.I.player != null && AutoExplosion.I.world != null && AutoExplosion.I.interactionManager != null;
    }

    private void minced$reset() {
        this.field6168 = null;
        this.field6169 = -1;
        this.field6170 = false;
        this.field6171 = false;
        this.field6173 = null;
        this.field6183 = false;
        this.field6184 = -1;
    }

    @EventHandler(2)
    public void method1465(EventBlockPlace param1) {
        if (!this.minced$isReady()) {
            this.minced$reset();
            return;
        }
        if (param1.method2395() == Blocks.OBSIDIAN) {
            if (AutoExplosion.I.player.getItemCooldownManager().isCoolingDown(Items.END_CRYSTAL.getDefaultStack())) {
                return;
            }
            if (((Boolean)this.field6167.getValue()).booleanValue() && this.method1466(param1.method2394())) {
                return;
            }
            int v2 = this.method1470(Items.END_CRYSTAL, true);
            if (v2 == -1) {
                return;
            }
            this.field6168 = param1.method2394();
            this.field6169 = v2;
        } else if (param1.method2395() instanceof RespawnAnchorBlock) {
            int v2 = this.method1470(Items.GLOWSTONE, true);
            if (v2 == -1) {
                return;
            }
            this.field6168 = param1.method2394();
            this.field6169 = v2;
            this.field6170 = true;
        }
        this.field6171 = true;
    }

    private boolean method1466(BlockPos param1) {
        Box v2 = new Box(param1).expand(1.0);
        for (Entity v4 : AutoExplosion.I.world.getNonSpectatingEntities(Entity.class, v2)) {
            if (!(v4 instanceof ItemEntity)) continue;
            return true;
        }
        return false;
    }

    @EventHandler(2)
    private void method1467(EventClickBlockRight param1) {
        if (!this.minced$isReady()) {
            this.minced$reset();
            return;
        }
        Block v2;
        if (!(AutoExplosion.I.player.getItemCooldownManager().isCoolingDown(Items.END_CRYSTAL.getDefaultStack()) || (v2 = param1.method2306().getBlockState(param1.method2303().getBlockPos()).getBlock()) != Blocks.OBSIDIAN && v2 != Blocks.BEDROCK)) {
            this.field6173 = new Box(param1.method2303().getBlockPos().up()).expand(field6172);
        }
    }

    public AutoExplosion() {
        super(field6174, field6175, Combat.Combat);
        this.field6167 = new BooleanSetting(field6178, false);
        this.field6180 = new BooleanSetting(field6179, false);
        this.addSettings(this.field6177, this.field6167, this.field6180);
    }

    @EventHandler(2)
    private void method1468(EventUpdate param1) {
        if (!this.minced$isReady()) {
            this.minced$reset();
            return;
        }
        if (this.field6173 != null) {
            for (Entity v3 : AutoExplosion.I.world.getEntities()) {
                double v62;
                double v82;
                if (!(v3 instanceof EndCrystalEntity) || !this.field6173.contains(v3.getPos())) continue;
                if (((Boolean)this.field6177.getValue()).booleanValue() && Math.abs((v82 = AutoExplosion.I.player.getY()) - (v62 = v3.getY())) < 1.0) {
                    this.field6173 = null;
                    return;
                }
                BlockPos v82_139 = BlockPos.ofFloored((Position)this.field6173.getCenter()).down();
                if (((Boolean)this.field6167.getValue()).booleanValue() && this.method1466(v82_139)) {
                    this.field6173 = null;
                    return;
                }
                if (!v3.getBoundingBox().contains(AutoExplosion.I.player.getCameraPosVec(I.getRenderTickCounter().getTickDelta(true)))) {
                    Vec3d v5 = t0.cfr_renamed_214((Entity)v3);
                    if (v5 == null) {
                        v5 = v3.getBoundingBox().getCenter().subtract(AutoExplosion.I.player.getEyePos());
                    }
                    float v62_146 = (float)MathHelper.wrapDegrees((double)(Math.toDegrees(Math.atan2(v5.z, v5.x)) - field6181));
                    float v7 = (float)(-Math.toDegrees(Math.atan2(v5.y, Math.hypot(v5.x, v5.z))));
                    AimRotation.method367(new Rotation(v62_146, v7), field6182, 1, 6);
                }
                AutoExplosion.I.interactionManager.attackEntity((PlayerEntity)AutoExplosion.I.player, v3);
                AutoExplosion.I.player.swingHand(Hand.MAIN_HAND);
                this.field6173 = null;
                return;
            }
        }
    }

    @EventHandler(2)
    private void method1469(EventUpdate param1) {
        if (!this.minced$isReady()) {
            this.minced$reset();
            return;
        }
        if (this.field6183) {
            this.field6183 = false;
            AutoExplosion.I.player.getInventory().selectedSlot = this.field6184;
            if (AutoExplosion.I.interactionManager instanceof IClientPlayerInteractionManagerMixin) {
                ((IClientPlayerInteractionManagerMixin)AutoExplosion.I.interactionManager).invokeSyncSelectedSlot();
            }
        } else if (this.field6168 != null) {
            if (AutoExplosion.I.world.getBlockState(this.field6168).isAir()) {
                this.field6168 = null;
            } else if (this.field6171) {
                this.field6171 = false;
            } else {
                Vec3d v2 = AutoExplosion.I.player.getCameraPosVec(I.getRenderTickCounter().getTickDelta(true));
                Vec3d v3 = t0.cfr_renamed_214((Vec3d)v2, (Box)new Box(this.field6168));
                if (v3 == null) {
                    v3 = this.field6168.toCenterPos();
                }
                Vec3d v4 = v3.subtract(v2);
                float v5 = (float)MathHelper.wrapDegrees((double)(Math.toDegrees(Math.atan2(v4.z, v4.x)) - field6185));
                float v6 = (float)(-Math.toDegrees(Math.atan2(v4.y, Math.hypot(v4.x, v4.z))));
                AimRotation.method367(new Rotation(v5, v6), field6186, 1, 6);
                this.field6184 = AutoExplosion.I.player.getInventory().selectedSlot;
                AutoExplosion.I.player.getInventory().selectedSlot = this.field6169;
                if (AutoExplosion.I.interactionManager instanceof IClientPlayerInteractionManagerMixin) {
                    ((IClientPlayerInteractionManagerMixin)AutoExplosion.I.interactionManager).invokeSyncSelectedSlot();
                }
                v4 = v4.negate();
                AutoExplosion.I.interactionManager.interactBlock(AutoExplosion.I.player, Hand.MAIN_HAND, new BlockHitResult(v3, Direction.getFacing((float)((float)v4.x), (float)((float)v4.y), (float)((float)v4.z)), this.field6168, false));
                AutoExplosion.I.player.swingHand(Hand.MAIN_HAND);
                this.field6183 = true;
                if (this.field6170) {
                    this.field6169 = this.field6184;
                    this.field6170 = false;
                } else {
                    this.field6168 = null;
                    if (((Boolean)this.field6180.getValue()).booleanValue()) {
                        this.field6183 = false;
                    }
                }
            }
        }
    }

    public int method1470(Item param1, boolean param2) {
        int v4 = param2 ? 9 : 36;
        int v5 = -1;
        for (int v6 = 0; v6 < v4; ++v6) {
            if (AutoExplosion.I.player.getInventory().getStack(v6).getItem() != param1) continue;
            v5 = v6;
        }
        return v5;
    }

    @EventHandler(2)
    public void minced$resetOnWorldSwap(EventSwapWorld event) {
        this.minced$reset();
    }

    @Override
    public void onDisable() {
        this.minced$reset();
        super.onDisable();
    }

    static {
        field6174 = "Auto Explosion";
        field6175 = "Автоматичeски взрывает кристаллы";
        field6176 = "Не взрывать себя";
        field6178 = "Не взрывать, если рядом предметы";
        field6179 = "Оставлять кристал в руке после того как поставил";
        field6185 = 90.0;
        field6186 = 180.0f;
        field6181 = 90.0;
        field6182 = 180.0f;
        field6172 = 0.1;
    }
}




/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.Items
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket
 *  net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket
 *  net.minecraft.screen.slot.SlotActionType
 *  net.minecraft.util.Hand
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.hit.HitResult$Type
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Direction$Axis
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Position
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.RaycastContext
 *  net.minecraft.world.RaycastContext$FluidHandling
 *  net.minecraft.world.RaycastContext$ShapeType
 */
package minced.module.player;
import minced.core.EventHandler;

import minced.core.ColorUtil;
import minced.core.BindSetting;
import minced.core.BooleanSetting;
import minced.core.EventRender3D;
import minced.core.EventUpdate;
import minced.core.Render3D;
import minced.core.RotationManager;
import minced.core.EventSelectSlot;
import minced.core.Rotation;
import minced.core.InventoryUtil;
import minced.core.EventKey;
import minced.core.AimRotation;
import minced.core.Module;
import minced.enums.Combat;
import minced.mixin.accessor.IClientPlayerInteractionManagerMixin;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

public class PearlPhase
extends Module {
    static MinecraftClient var_net_minecraft_client_MinecraftClient_I = MinecraftClient.getInstance();
    int field5235 = 0;
    int field5236 = 0;
    int field5237 = 0;
    int field5238 = -1;
    Rotation field5239;
    int field5240 = -1;
    boolean field5241;
    Rotation var_minced_core_ClsClass547_I;
    boolean field5242;
    static float field5243;
    BooleanSetting field5244;
    static double field5245;
    static double field5246;
    static double field5247;
    int field5248 = -1;
    BlockHitResult field5249;
    BooleanSetting field5250;
    static double field5251;
    static double field5252;
    static String field5253;
    static String field5254;
    static String field5255;
    BindSetting field5256 = new BindSetting(field5255);
    static String field5257;
    static String field5258;
    static double field5259;
    static float field5260;
    static float field5261;
    static double field5262;
    static float field5263;
    static float field5264;
    static float field5265;

    @EventHandler(2)
    public void method1256(EventUpdate param1) {
        if (PearlPhase.var_net_minecraft_client_MinecraftClient_I.player == null || PearlPhase.var_net_minecraft_client_MinecraftClient_I.world == null || PearlPhase.var_net_minecraft_client_MinecraftClient_I.interactionManager == null) {
            this.method1264();
            return;
        }
        if (this.field5235 > 0) {
            --this.field5235;
        }
        if (this.field5236 == 6 && PearlPhase.var_net_minecraft_client_MinecraftClient_I.player.age >= this.field5237) {
            if (!this.method1258()) {
                return;
            }
            PearlPhase.var_net_minecraft_client_MinecraftClient_I.interactionManager.interactItem((PlayerEntity)PearlPhase.var_net_minecraft_client_MinecraftClient_I.player, Hand.MAIN_HAND);
            this.field5236 = 5;
            this.field5237 = PearlPhase.var_net_minecraft_client_MinecraftClient_I.player.age + 2;
        }
        if (this.field5236 == 5 && PearlPhase.var_net_minecraft_client_MinecraftClient_I.player.age >= this.field5237) {
            PearlPhase.var_net_minecraft_client_MinecraftClient_I.player.getInventory().selectedSlot = this.field5238;
            ((IClientPlayerInteractionManagerMixin)PearlPhase.var_net_minecraft_client_MinecraftClient_I.interactionManager).invokeSyncSelectedSlot();
            this.field5238 = -1;
            this.field5236 = 0;
            this.field5239 = null;
        }
        if (this.field5236 == 1 && this.field5235 == 0) {
            this.field5236 = 2;
        }
        if (this.field5236 == 2 && this.field5240 != -1) {
            PearlPhase.var_net_minecraft_client_MinecraftClient_I.interactionManager.clickSlot(PearlPhase.var_net_minecraft_client_MinecraftClient_I.player.currentScreenHandler.syncId, this.field5240, this.field5238, SlotActionType.SWAP, (PlayerEntity)PearlPhase.var_net_minecraft_client_MinecraftClient_I.player);
            PearlPhase.var_net_minecraft_client_MinecraftClient_I.player.networkHandler.sendPacket((Packet)new CloseHandledScreenC2SPacket(PearlPhase.var_net_minecraft_client_MinecraftClient_I.player.currentScreenHandler.syncId));
            this.field5236 = 3;
            this.field5237 = PearlPhase.var_net_minecraft_client_MinecraftClient_I.player.age + 2;
        }
        if (this.field5236 == 3 && PearlPhase.var_net_minecraft_client_MinecraftClient_I.player.age >= this.field5237) {
            if (!this.method1258()) {
                return;
            }
            PearlPhase.var_net_minecraft_client_MinecraftClient_I.interactionManager.interactItem((PlayerEntity)PearlPhase.var_net_minecraft_client_MinecraftClient_I.player, Hand.MAIN_HAND);
            this.field5236 = 4;
            this.field5237 = PearlPhase.var_net_minecraft_client_MinecraftClient_I.player.age + 3;
        }
        if (this.field5236 == 4 && PearlPhase.var_net_minecraft_client_MinecraftClient_I.player.age >= this.field5237) {
            PearlPhase.var_net_minecraft_client_MinecraftClient_I.interactionManager.clickSlot(PearlPhase.var_net_minecraft_client_MinecraftClient_I.player.currentScreenHandler.syncId, this.field5240, this.field5238, SlotActionType.SWAP, (PlayerEntity)PearlPhase.var_net_minecraft_client_MinecraftClient_I.player);
            PearlPhase.var_net_minecraft_client_MinecraftClient_I.player.networkHandler.sendPacket((Packet)new CloseHandledScreenC2SPacket(PearlPhase.var_net_minecraft_client_MinecraftClient_I.player.currentScreenHandler.syncId));
            this.field5236 = 0;
            this.field5240 = -1;
            this.field5238 = -1;
            this.field5239 = null;
        }
        if (this.field5236 != 0 || this.field5235 > 0) {
            return;
        }
        if (!this.field5241) {
            return;
        }
        if (PearlPhase.var_net_minecraft_client_MinecraftClient_I.player.getItemCooldownManager().isCoolingDown(Items.ENDER_PEARL.getDefaultStack())) {
            this.method1264();
            return;
        }
        int v2 = InventoryUtil.method3336(Items.ENDER_PEARL);
        if (v2 == -1) {
            this.method1264();
            return;
        }
        if (this.field5239 == null) {
            this.method1259();
            this.field5239 = this.var_minced_core_ClsClass547_I;
            if (this.field5239 == null) {
                this.method1264();
                return;
            }
            this.field5242 = false;
        }
        if (!this.field5242) {
            AimRotation.method367(this.field5239, field5243, 8, 1000);
            this.field5242 = true;
        }
        if (!this.method1258()) {
            return;
        }
        int v3 = PearlPhase.var_net_minecraft_client_MinecraftClient_I.player.getInventory().selectedSlot;
        if (v2 < 9) {
            if (((Boolean)this.field5244.getValue()).booleanValue()) {
                PearlPhase.var_net_minecraft_client_MinecraftClient_I.player.getInventory().selectedSlot = v2;
                ((IClientPlayerInteractionManagerMixin)PearlPhase.var_net_minecraft_client_MinecraftClient_I.interactionManager).invokeSyncSelectedSlot();
                this.field5238 = v3;
                this.field5236 = 6;
                this.field5237 = PearlPhase.var_net_minecraft_client_MinecraftClient_I.player.age + 2;
            } else {
                PearlPhase.var_net_minecraft_client_MinecraftClient_I.player.networkHandler.sendPacket((Packet)new UpdateSelectedSlotC2SPacket(v2));
                PearlPhase.var_net_minecraft_client_MinecraftClient_I.interactionManager.interactItem((PlayerEntity)PearlPhase.var_net_minecraft_client_MinecraftClient_I.player, Hand.MAIN_HAND);
                PearlPhase.var_net_minecraft_client_MinecraftClient_I.player.networkHandler.sendPacket((Packet)new UpdateSelectedSlotC2SPacket(v3));
                this.field5239 = null;
            }
        } else if (((Boolean)this.field5244.getValue()).booleanValue()) {
            this.field5235 = 2;
            this.field5236 = 1;
            this.field5240 = v2;
            this.field5238 = v3;
        } else {
            PearlPhase.var_net_minecraft_client_MinecraftClient_I.interactionManager.clickSlot(PearlPhase.var_net_minecraft_client_MinecraftClient_I.player.currentScreenHandler.syncId, v2, v3, SlotActionType.SWAP, (PlayerEntity)PearlPhase.var_net_minecraft_client_MinecraftClient_I.player);
            PearlPhase.var_net_minecraft_client_MinecraftClient_I.interactionManager.interactItem((PlayerEntity)PearlPhase.var_net_minecraft_client_MinecraftClient_I.player, Hand.MAIN_HAND);
            PearlPhase.var_net_minecraft_client_MinecraftClient_I.interactionManager.clickSlot(PearlPhase.var_net_minecraft_client_MinecraftClient_I.player.currentScreenHandler.syncId, v2, v3, SlotActionType.SWAP, (PlayerEntity)PearlPhase.var_net_minecraft_client_MinecraftClient_I.player);
            this.field5239 = null;
        }
        this.field5241 = false;
    }

    private BlockHitResult method1257() {
        Vec3d v3;
        Vec3d v4;
        if (PearlPhase.var_net_minecraft_client_MinecraftClient_I.player == null || PearlPhase.var_net_minecraft_client_MinecraftClient_I.world == null) {
            return null;
        }
        float v1 = RotationManager.method255();
        Vec3d v2 = PearlPhase.var_net_minecraft_client_MinecraftClient_I.player.getPos().add(0.0, field5245, 0.0);
        BlockHitResult v5 = PearlPhase.var_net_minecraft_client_MinecraftClient_I.world.raycast(new RaycastContext(v2, v4 = v2.add((v3 = Rotation.method3137(0.0f, v1)).multiply(1.0)), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, (Entity)PearlPhase.var_net_minecraft_client_MinecraftClient_I.player));
        if (v5.getType() == HitResult.Type.BLOCK && !v5.getSide().getAxis().isVertical()) {
            return v5;
        }
        Vec3d v6 = v2.add(v3.multiply(1.0));
        BlockPos v7 = BlockPos.ofFloored((Position)v6);
        if (PearlPhase.var_net_minecraft_client_MinecraftClient_I.world.getBlockState(v7).isAir()) {
            return null;
        }
        Direction v8 = Direction.getFacing((float)((float)(-v3.x)), (float)0.0f, (float)((float)(-v3.z)));
        if (v8.getAxis().isVertical()) {
            return null;
        }
        Vec3d v9 = new Vec3d((double)v7.getX() + field5246, v6.y, (double)v7.getZ() + field5247);
        return new BlockHitResult(v9, v8, v7, false);
    }

    private boolean method1258() {
        if (this.field5239 == null) {
            return false;
        }
        return Rotation.method3142().method3141(this.field5239) <= 1.0f;
    }

    private void method1259() {
        if (PearlPhase.var_net_minecraft_client_MinecraftClient_I.player == null) {
            this.field5248 = -1;
            this.field5249 = null;
            this.var_minced_core_ClsClass547_I = null;
            return;
        }
        if (this.field5248 == PearlPhase.var_net_minecraft_client_MinecraftClient_I.player.age) {
            return;
        }
        this.field5248 = PearlPhase.var_net_minecraft_client_MinecraftClient_I.player.age;
        this.field5249 = this.method1257();
        this.var_minced_core_ClsClass547_I = this.method1262(this.field5249);
    }

    @EventHandler(2)
    public void method1260(EventSelectSlot param1) {
        if (this.field5235 > 0 || this.field5236 >= 1 && this.field5236 <= 6) {
            param1.method2298(true);
        }
    }

    @EventHandler(2)
    public void method1261(EventRender3D param1) {
        if (!this.isEnabled() || PearlPhase.var_net_minecraft_client_MinecraftClient_I.player == null || PearlPhase.var_net_minecraft_client_MinecraftClient_I.world == null) {
            return;
        }
        if (!((Boolean)this.field5250.getValue()).booleanValue()) {
            return;
        }
        if (InventoryUtil.method3336(Items.ENDER_PEARL) == -1) {
            return;
        }
        this.method1259();
        if (this.field5249 == null || this.field5249.getType() != HitResult.Type.BLOCK) {
            return;
        }
        Direction v2 = this.field5249.getSide();
        BlockPos v3 = this.field5249.getBlockPos();
        boolean v4 = this.var_minced_core_ClsClass547_I != null;
        int v5 = v4 ? ColorUtil.method1852(0, 255, 0, 200) : ColorUtil.method1852(255, 0, 0, 200);
        int v6 = v4 ? ColorUtil.method1852(0, 255, 0, 90) : ColorUtil.method1852(255, 0, 0, 90);
        Vec3d v7 = PearlPhase.var_net_minecraft_client_MinecraftClient_I.gameRenderer.getCamera().getPos();
        param1.method2348().push();
        param1.method2348().translate(-v7.x, -v7.y, -v7.z);
        Box v8 = new Box((double)v3.getX(), (double)v3.getY(), (double)v3.getZ(), (double)(v3.getX() + 1), (double)(v3.getY() + 1), (double)(v3.getZ() + 1)).expand(field5251);
        Render3D.method2407(param1.method2348(), v8, v5, 2.0f);
        if (v2.getAxis().isHorizontal()) {
            BlockPos v9 = v3.offset(v2.getOpposite());
            Box v10 = new Box((double)v9.getX(), (double)v9.getY(), (double)v9.getZ(), (double)(v9.getX() + 1), (double)(v9.getY() + 1), (double)(v9.getZ() + 1)).expand(field5252);
            Render3D.method2407(param1.method2348(), v10, v6, 1.0f);
        }
        param1.method2348().pop();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.field5241 = false;
        this.field5239 = null;
        this.field5242 = false;
        this.field5236 = 0;
        this.field5238 = -1;
        this.field5240 = -1;
        this.field5237 = 0;
        this.field5235 = 0;
        this.field5248 = -1;
        this.field5249 = null;
        this.var_minced_core_ClsClass547_I = null;
    }

    public PearlPhase() {
        super(field5253, field5254, Combat.Player);
        this.field5244 = new BooleanSetting(field5257, true);
        this.field5250 = new BooleanSetting(field5258, true);
        this.addSettings(this.field5256, this.field5244, this.field5250);
    }

    private Rotation method1262(BlockHitResult param1) {
        if (PearlPhase.var_net_minecraft_client_MinecraftClient_I.player == null || PearlPhase.var_net_minecraft_client_MinecraftClient_I.world == null) {
            return null;
        }
        if (param1 == null || param1.getType() != HitResult.Type.BLOCK) {
            return null;
        }
        float v2 = RotationManager.method255();
        Direction v3 = param1.getSide();
        if (v3.getAxis().isVertical()) {
            return null;
        }
        BlockPos v4 = param1.getBlockPos();
        float v5 = 0.0f;
        boolean v6 = false;
        double v7 = field5259;
        for (float v9 = field5260; v9 <= field5261; v9 += field5263) {
            double v15;
            double v17;
            BlockHitResult v10 = new Rotation(0.0f, 0.0f).method3132(field5262, v2, v9, (Entity)PearlPhase.var_net_minecraft_client_MinecraftClient_I.player, RaycastContext.FluidHandling.NONE);
            if (v10.getType() != HitResult.Type.BLOCK || !v10.getBlockPos().equals((Object)v4) || v10.getSide() != v3) continue;
            Vec3d v11 = v10.getPos();
            Vec3d v12 = v11.subtract((double)v4.getX(), (double)v4.getY(), (double)v4.getZ());
            double v13 = v12.y;
            if (v13 < 0.0 || v13 > 1.0) continue;
            if (v3.getAxis() == Direction.Axis.Z) {
                v17 = v12.x;
                if (v17 < 0.0 || v17 > 1.0) continue;
                v15 = Math.min(v17, 1.0 - v17);
            } else {
                v17 = v12.z;
                if (v17 < 0.0 || v17 > 1.0) continue;
                v15 = Math.min(v17, 1.0 - v17);
            }
            v17 = v13 + v15;
            if (v6 && !(v17 < v7) && (v17 != v7 || !(v9 > v5))) continue;
            v6 = true;
            v7 = v17;
            v5 = v9;
        }
        if (!v6) {
            return null;
        }
        return new Rotation(v2, MathHelper.clamp((float)v5, (float)field5264, (float)field5265));
    }

    @EventHandler(2)
    public void method1263(EventKey param1) {
        if (this.field5236 != 0 || this.field5235 > 0) {
            return;
        }
        boolean bl = this.field5241 = ((Integer)this.field5256.getValue()).intValue() == param1.method3595() && param1.method3599() && !param1.method3594() && PearlPhase.var_net_minecraft_client_MinecraftClient_I.player != null && !PearlPhase.var_net_minecraft_client_MinecraftClient_I.player.getItemCooldownManager().isCoolingDown(Items.ENDER_PEARL.getDefaultStack()) && InventoryUtil.method3336(Items.ENDER_PEARL) != -1;
        if (this.field5241) {
            this.field5239 = null;
        }
    }

    private void method1264() {
        this.field5241 = false;
        this.field5239 = null;
        this.field5242 = false;
    }

    static {
        field5253 = "Pearl Phase";
        field5254 = "Автоматически подбирает pitch для прокида эндер-жемчуга через блок";
        field5255 = "Кнопка бросания";
        field5257 = "Обход HolyWorld";
        field5258 = "Рендер";
        field5243 = 360.0f;
        field5251 = 0.001;
        field5252 = 0.001;
        field5245 = 0.2;
        field5246 = 0.5;
        field5247 = 0.5;
        field5259 = Double.MAX_VALUE;
        field5260 = -89.0f;
        field5261 = 89.0f;
        field5262 = 4.5;
        field5263 = 0.5f;
        field5264 = -89.0f;
        field5265 = 89.0f;
    }
}




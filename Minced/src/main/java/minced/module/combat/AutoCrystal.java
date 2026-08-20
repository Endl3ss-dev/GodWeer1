/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager$DstFactor
 *  com.mojang.blaze3d.platform.GlStateManager$SrcFactor
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.block.Blocks
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gl.ShaderProgramKey
 *  net.minecraft.client.gl.ShaderProgramKeys
 *  net.minecraft.client.render.BufferBuilder
 *  net.minecraft.client.render.BufferRenderer
 *  net.minecraft.client.render.BuiltBuffer
 *  net.minecraft.client.render.Tessellator
 *  net.minecraft.client.render.VertexFormat$DrawMode
 *  net.minecraft.client.render.VertexFormats
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.decoration.EndCrystalEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.Items
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket
 *  net.minecraft.util.Hand
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.Vec3i
 *  org.joml.Matrix4f
 *  sg.ec.t0
 */
package minced.module.combat;
import minced.core.EventHandler;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.ThreadLocalRandom;
import minced.core.BooleanSetting;
import minced.core.SliderSetting;
import minced.core.RotationTargetUtil;
import minced.core.ModeSetting;
import minced.core.EventRender3D;
import minced.core.EventUpdate;
import minced.core.EventSwapWorld;
import minced.core.Rotation;
import minced.core.InventoryUtil;
import minced.core.AimRotation;
import minced.core.Minced;
import minced.core.Module;
import minced.enums.Combat;
import minced.mixin.accessor.IClientPlayerInteractionManagerMixin;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.joml.Matrix4f;
import sg.ec.t0;

public class AutoCrystal
extends Module {
    BlockPos field5559;
    EndCrystalEntity field5560;
    int field5561;
    int field5562;
    PlayerEntity field5563;
    int field5564 = -1;
    boolean field5565 = false;
    long field5566 = 0L;
    static MinecraftClient I = MinecraftClient.getInstance();
    float field5567;
    float field5568;
    ModeSetting field5569;
    BooleanSetting field5570;
    static String field5571;
    static String field5572;
    BooleanSetting field5573;
    static String field5574;
    static long field5575;
    SliderSetting field5576;
    SliderSetting field5577;
    static float field5578;
    static float field5579;
    static float field5580;
    static float field5581;
    static float field5582;
    static float field5583;
    static float field5584;
    static float field5585;
    static float field5586;
    static float field5587;
    static double field5588;
    SliderSetting field5589 = new SliderSetting(field5592, field5593, 1.0f, field5594, field5595);
    static String field5590;
    static String field5591;
    static String field5592;
    static float field5593;
    static float field5594;
    static float field5595;
    static String field5596;
    static float field5597;
    static String field5598;
    static float field5599;
    static String field5600;
    static String field5601;
    static String field5602;
    static String field5603;
    static String field5604;
    static String field5605;
    static double field5606;
    static double field5607;
    static double field5608;
    static double field5609;
    static double field5610;
    static double field5611;
    static float field5612;
    static float field5613;
    static float field5614;
    static float field5615;
    static float field5616;
    static double field5617;
    static double field5618;
    static double field5619;
    static double field5620;

    private void method1305() {
        this.field5559 = null;
        this.field5560 = null;
        this.field5561 = 0;
        this.field5562 = 0;
        this.field5563 = null;
        this.field5564 = -1;
        this.field5565 = false;
        this.field5566 = 0L;
        if (AutoCrystal.I.player != null) {
            this.field5567 = AutoCrystal.I.player.getYaw();
            this.field5568 = AutoCrystal.I.player.getPitch();
        }
    }

    public int method1306() {
        return this.field5561;
    }

    private void method1307(MatrixStack param1, Box param2, float param3, float param4, float param5, float param6) {
        Matrix4f v7 = param1.peek().getPositionMatrix();
        BufferBuilder v8 = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        float v9 = (float)param2.minX;
        float v10 = (float)param2.minY;
        float v11 = (float)param2.minZ;
        float v12 = (float)param2.maxX;
        float v13 = (float)param2.maxY;
        float v14 = (float)param2.maxZ;
        v8.vertex(v7, v9, v10, v11).color(param3, param4, param5, param6);
        v8.vertex(v7, v12, v10, v11).color(param3, param4, param5, param6);
        v8.vertex(v7, v12, v10, v14).color(param3, param4, param5, param6);
        v8.vertex(v7, v9, v10, v14).color(param3, param4, param5, param6);
        v8.vertex(v7, v9, v13, v11).color(param3, param4, param5, param6);
        v8.vertex(v7, v9, v13, v14).color(param3, param4, param5, param6);
        v8.vertex(v7, v12, v13, v14).color(param3, param4, param5, param6);
        v8.vertex(v7, v12, v13, v11).color(param3, param4, param5, param6);
        v8.vertex(v7, v9, v10, v11).color(param3, param4, param5, param6);
        v8.vertex(v7, v9, v13, v11).color(param3, param4, param5, param6);
        v8.vertex(v7, v12, v13, v11).color(param3, param4, param5, param6);
        v8.vertex(v7, v12, v10, v11).color(param3, param4, param5, param6);
        v8.vertex(v7, v9, v10, v14).color(param3, param4, param5, param6);
        v8.vertex(v7, v12, v10, v14).color(param3, param4, param5, param6);
        v8.vertex(v7, v12, v13, v14).color(param3, param4, param5, param6);
        v8.vertex(v7, v9, v13, v14).color(param3, param4, param5, param6);
        v8.vertex(v7, v9, v10, v11).color(param3, param4, param5, param6);
        v8.vertex(v7, v9, v10, v14).color(param3, param4, param5, param6);
        v8.vertex(v7, v9, v13, v14).color(param3, param4, param5, param6);
        v8.vertex(v7, v9, v13, v11).color(param3, param4, param5, param6);
        v8.vertex(v7, v12, v10, v11).color(param3, param4, param5, param6);
        v8.vertex(v7, v12, v13, v11).color(param3, param4, param5, param6);
        v8.vertex(v7, v12, v13, v14).color(param3, param4, param5, param6);
        v8.vertex(v7, v12, v10, v14).color(param3, param4, param5, param6);
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)v8.end());
    }

    public PlayerEntity method1308() {
        return this.field5563;
    }

    public ModeSetting method1309() {
        return this.field5569;
    }

    public BooleanSetting method1310() {
        return this.field5570;
    }

    private void method1311(BlockPos param1) {
        int v2 = this.method1334();
        if (v2 == -1) {
            return;
        }
        this.method1331(param1);
        int v3 = AutoCrystal.I.player.getInventory().selectedSlot;
        if (this.field5569.isMode(field5571)) {
            AutoCrystal.I.player.getInventory().selectedSlot = v2;
            this.method1335();
        } else {
            I.getNetworkHandler().sendPacket((Packet)new UpdateSelectedSlotC2SPacket(v2));
        }
        Vec3d v4 = AutoCrystal.I.player.getEyePos();
        Vec3d v5 = t0.cfr_renamed_214((Vec3d)v4, (Box)new Box(param1));
        if (v5 == null) {
            v5 = param1.toCenterPos();
        }
        Vec3d v6 = v5.subtract(v4).negate();
        AutoCrystal.I.interactionManager.interactBlock(AutoCrystal.I.player, Hand.MAIN_HAND, new BlockHitResult(v5, Direction.getFacing((float)((float)v6.x), (float)((float)v6.y), (float)((float)v6.z)), param1, false));
        AutoCrystal.I.player.swingHand(Hand.MAIN_HAND);
        if (this.field5569.isMode(field5572)) {
            this.field5564 = v3;
            this.field5565 = true;
            this.field5566 = System.currentTimeMillis();
        } else {
            I.getNetworkHandler().sendPacket((Packet)new UpdateSelectedSlotC2SPacket(v3));
        }
    }

    private boolean method1312(BlockPos param1) {
        if (!this.method1323(param1)) {
            return false;
        }
        BlockPos v2 = param1.up();
        if (!AutoCrystal.I.world.getBlockState(v2).isAir()) {
            return false;
        }
        if (!AutoCrystal.I.world.getBlockState(v2.up()).isAir()) {
            return false;
        }
        Box v3 = new Box((double)v2.getX(), (double)v2.getY(), (double)v2.getZ(), (double)(v2.getX() + 1), (double)(v2.getY() + 2), (double)(v2.getZ() + 1));
        for (Entity v5 : AutoCrystal.I.world.getEntities()) {
            if (v5 == AutoCrystal.I.player || !v5.getBoundingBox().intersects(v3)) continue;
            return false;
        }
        return true;
    }

    private void method1313(EndCrystalEntity param1) {
        if (param1 == null) {
            return;
        }
        AutoCrystal.I.interactionManager.attackEntity((PlayerEntity)AutoCrystal.I.player, (Entity)param1);
        AutoCrystal.I.player.swingHand(Hand.MAIN_HAND);
    }

    @Override
    public void onDisable() {
        if (AutoCrystal.I.player != null && AutoCrystal.I.world != null && this.field5564 != -1 && this.field5564 < 9) {
            AutoCrystal.I.player.getInventory().selectedSlot = this.field5564;
            this.method1335();
        }
        this.method1305();
        super.onDisable();
    }

    public float method1314() {
        return this.field5568;
    }

    public BooleanSetting method1315() {
        return this.field5573;
    }

    @EventHandler(2)
    public void method1316(EventUpdate param1) {
        long v2;
        if (AutoCrystal.I.player == null || AutoCrystal.I.world == null || AutoCrystal.I.interactionManager == null) {
            this.method1305();
            return;
        }
        ++this.field5561;
        ++this.field5562;
        if (this.field5565 && this.field5569.isMode(field5574) && (v2 = System.currentTimeMillis() - this.field5566) >= field5575) {
            if (this.field5564 != -1 && this.field5564 < 9) {
                AutoCrystal.I.player.getInventory().selectedSlot = this.field5564;
                this.method1335();
                this.field5564 = -1;
            }
            this.field5565 = false;
        }
        this.field5563 = this.method1325();
        if (this.field5563 == null) {
            this.field5559 = null;
            this.field5567 = AutoCrystal.I.player.getYaw();
            this.field5568 = AutoCrystal.I.player.getPitch();
            return;
        }
        if (((Boolean)this.field5570.getValue()).booleanValue() && (float)this.field5561 >= ((Float)this.field5576.getValue()).floatValue()) {
            this.field5559 = this.method1320();
            if (this.field5559 != null) {
                this.method1311(this.field5559);
                this.field5561 = 0;
            }
        }
        if ((float)this.field5562 >= ((Float)this.field5577.getValue()).floatValue()) {
            this.field5560 = this.method1319();
            if (this.field5560 != null) {
                this.method1338(this.field5560);
                this.method1313(this.field5560);
                this.field5562 = 0;
            }
        }
    }

    private void method1317(float param1, float param2) {
        float v3 = MathHelper.wrapDegrees((float)(param1 - this.field5567));
        float v4 = param2 - this.field5568;
        float v5 = this.field5567 + v3;
        float v6 = this.field5568 + v4;
        float v7 = RotationTargetUtil.method1975();
        v5 -= (v5 - this.field5567) % v7;
        if ((v6 -= (v6 - this.field5568) % v7) > field5578) {
            v6 = field5579;
        }
        if (v6 < field5580) {
            v6 = field5581;
        }
        if (v5 == this.field5567 && v6 == this.field5568) {
            int v8 = ThreadLocalRandom.current().nextInt(1, 4);
            if (ThreadLocalRandom.current().nextBoolean()) {
                v5 += v7 * (float)v8 * (float)(ThreadLocalRandom.current().nextBoolean() ? 1 : -1);
            } else {
                v6 += v7 * (float)v8 * (float)(ThreadLocalRandom.current().nextBoolean() ? 1 : -1);
            }
            v6 = MathHelper.clamp((float)v6, (float)field5582, (float)field5583);
        }
        AimRotation.method376(new Rotation(v5, v6), field5584, field5585, field5586, field5587, 1, 2, false);
        this.field5567 = v5;
        this.field5568 = v6;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (AutoCrystal.I.player != null) {
            this.field5567 = AutoCrystal.I.player.getYaw();
            this.field5568 = AutoCrystal.I.player.getPitch();
        }
        this.method1305();
    }

    public long method1318() {
        return this.field5566;
    }

    private EndCrystalEntity method1319() {
        EndCrystalEntity v1 = null;
        double v2 = field5588;
        for (Entity v5 : AutoCrystal.I.world.getEntities()) {
            double v9;
            if (!(v5 instanceof EndCrystalEntity)) continue;
            EndCrystalEntity v6 = (EndCrystalEntity)v5;
            double v7 = AutoCrystal.I.player.distanceTo(v5);
            if (v7 > (double)((Float)this.field5589.getValue()).floatValue() || this.field5563 == null || !((v9 = (double)this.field5563.distanceTo((Entity)v6)) < v2)) continue;
            v2 = v9;
            v1 = v6;
        }
        return v1;
    }

    private BlockPos method1320() {
        if (this.field5563 == null) {
            return null;
        }
        ArrayList<BlockPos> v1 = new ArrayList<BlockPos>();
        BlockPos v2 = AutoCrystal.I.player.getBlockPos();
        int v3 = (int)Math.ceil(((Float)this.field5589.getValue()).floatValue());
        for (int v4 = -v3; v4 <= v3; ++v4) {
            for (int v5 = -v3; v5 <= v3; ++v5) {
                for (int v6 = -v3; v6 <= v3; ++v6) {
                    BlockPos v7 = v2.add(v4, v5, v6);
                    double v8 = AutoCrystal.I.player.getPos().distanceTo(Vec3d.ofCenter((Vec3i)v7));
                    if (v8 > (double)((Float)this.field5589.getValue()).floatValue() || !this.method1312(v7)) continue;
                    v1.add(v7);
                }
            }
        }
        if (v1.isEmpty()) {
            return null;
        }
        v1.sort(Comparator.comparingDouble(param1 -> {
            Vec3d v2_405 = new Vec3d((double)param1.getX() + field5618, (double)(param1.getY() + 1), (double)param1.getZ() + field5619);
            return this.field5563.getPos().distanceTo(v2_405);
        }));
        return (BlockPos)v1.get(0);
    }

    public int method1321() {
        return this.field5562;
    }

    public SliderSetting method1322() {
        return this.field5589;
    }

    private boolean method1323(BlockPos param1) {
        return AutoCrystal.I.world.getBlockState(param1).getBlock() == Blocks.OBSIDIAN || AutoCrystal.I.world.getBlockState(param1).getBlock() == Blocks.BEDROCK;
    }

    private void method1324(MatrixStack param1, Box param2, float param3, float param4, float param5, float param6) {
        Matrix4f v7 = param1.peek().getPositionMatrix();
        BufferBuilder v8 = Tessellator.getInstance().begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
        float v9 = (float)param2.minX;
        float v10 = (float)param2.minY;
        float v11 = (float)param2.minZ;
        float v12 = (float)param2.maxX;
        float v13 = (float)param2.maxY;
        float v14 = (float)param2.maxZ;
        v8.vertex(v7, v9, v10, v11).color(param3, param4, param5, param6);
        v8.vertex(v7, v12, v10, v11).color(param3, param4, param5, param6);
        v8.vertex(v7, v12, v10, v11).color(param3, param4, param5, param6);
        v8.vertex(v7, v12, v10, v14).color(param3, param4, param5, param6);
        v8.vertex(v7, v12, v10, v14).color(param3, param4, param5, param6);
        v8.vertex(v7, v9, v10, v14).color(param3, param4, param5, param6);
        v8.vertex(v7, v9, v10, v14).color(param3, param4, param5, param6);
        v8.vertex(v7, v9, v10, v11).color(param3, param4, param5, param6);
        v8.vertex(v7, v9, v13, v11).color(param3, param4, param5, param6);
        v8.vertex(v7, v12, v13, v11).color(param3, param4, param5, param6);
        v8.vertex(v7, v12, v13, v11).color(param3, param4, param5, param6);
        v8.vertex(v7, v12, v13, v14).color(param3, param4, param5, param6);
        v8.vertex(v7, v12, v13, v14).color(param3, param4, param5, param6);
        v8.vertex(v7, v9, v13, v14).color(param3, param4, param5, param6);
        v8.vertex(v7, v9, v13, v14).color(param3, param4, param5, param6);
        v8.vertex(v7, v9, v13, v11).color(param3, param4, param5, param6);
        v8.vertex(v7, v9, v10, v11).color(param3, param4, param5, param6);
        v8.vertex(v7, v9, v13, v11).color(param3, param4, param5, param6);
        v8.vertex(v7, v12, v10, v11).color(param3, param4, param5, param6);
        v8.vertex(v7, v12, v13, v11).color(param3, param4, param5, param6);
        v8.vertex(v7, v12, v10, v14).color(param3, param4, param5, param6);
        v8.vertex(v7, v12, v13, v14).color(param3, param4, param5, param6);
        v8.vertex(v7, v9, v10, v14).color(param3, param4, param5, param6);
        v8.vertex(v7, v9, v13, v14).color(param3, param4, param5, param6);
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)v8.end());
    }

    public AutoCrystal() {
        super(field5590, field5591, Combat.Combat);
        this.field5576 = new SliderSetting(field5596, 0.0f, 0.0f, field5597, 1.0f);
        this.field5577 = new SliderSetting(field5598, 0.0f, 0.0f, field5599, 1.0f);
        this.field5570 = new BooleanSetting(field5600, true);
        this.field5573 = new BooleanSetting(field5601, true);
        this.field5569 = new ModeSetting(field5602, field5603, field5604, field5605);
        this.addSettings(this.field5589, this.field5576, this.field5577, this.field5570, this.field5573, this.field5569);
    }

    private PlayerEntity method1325() {
        PlayerEntity v1 = null;
        double v2 = field5606;
        for (PlayerEntity v5 : AutoCrystal.I.world.getPlayers()) {
            double v6;
            if (v5 == AutoCrystal.I.player || v5.isDead() || v5.getHealth() <= 0.0f || Minced.getInstance().getFriendManager().cfr_renamed_4(v5.getName().getString()) || (v6 = (double)AutoCrystal.I.player.distanceTo((Entity)v5)) > (double)(((Float)this.field5589.getValue()).floatValue() * 2.0f) || !(v6 < v2)) continue;
            v2 = v6;
            v1 = v5;
        }
        return v1;
    }

    public boolean method1326() {
        return this.field5565;
    }

    public SliderSetting method1327() {
        return this.field5576;
    }

    private static int method1328(int param0, int param1, int param2, int param3) {
        return param3 << 24 | param0 << 16 | param1 << 8 | param2;
    }

    public EndCrystalEntity method1329() {
        return this.field5560;
    }

    @EventHandler(2)
    public void method1330(EventRender3D param1) {
        if (!((Boolean)this.field5573.getValue()).booleanValue() || AutoCrystal.I.player == null || AutoCrystal.I.world == null) {
            return;
        }
        if (this.field5559 == null || this.field5563 == null) {
            return;
        }
        MatrixStack v2 = param1.method2348();
        Vec3d v3 = AutoCrystal.I.gameRenderer.getCamera().getPos();
        Vec3d v4 = new Vec3d((double)this.field5559.getX() + field5607, (double)(this.field5559.getY() + 1), (double)this.field5559.getZ() + field5608);
        double v5 = this.field5563.getPos().distanceTo(v4);
        double v7 = AutoCrystal.I.player.getPos().distanceTo(v4);
        int v9 = v5 <= field5609 && v7 > v5 ? -1275003086 : (v5 <= field5610 && v7 >= v5 * field5611 ? -1593835776 : 2029998080);
        v2.push();
        v2.translate(-v3.x, -v3.y, -v3.z);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc((GlStateManager.SrcFactor)GlStateManager.SrcFactor.SRC_ALPHA, (GlStateManager.DstFactor)GlStateManager.DstFactor.ONE);
        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.setShader((ShaderProgramKey)ShaderProgramKeys.POSITION_COLOR);
        float v10 = (float)(v9 >> 16 & 0xFF) / field5612;
        float v11 = (float)(v9 >> 8 & 0xFF) / field5613;
        float v12 = (float)(v9 & 0xFF) / field5614;
        float v13 = (float)(v9 >> 24 & 0xFF) / field5615;
        Box v14 = new Box(this.field5559);
        this.method1307(v2, v14, v10, v11, v12, v13 * field5616);
        this.method1324(v2, v14, v10, v11, v12, v13);
        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        v2.pop();
    }

    private void method1331(BlockPos param1) {
        Vec3d v2 = AutoCrystal.I.player.getEyePos();
        Vec3d v3 = t0.cfr_renamed_214((Vec3d)v2, (Box)new Box(param1));
        if (v3 == null) {
            v3 = param1.toCenterPos();
        }
        Vec3d v4 = v3.subtract(v2);
        float v5 = (float)MathHelper.wrapDegrees((double)(Math.toDegrees(Math.atan2(v4.z, v4.x)) - field5617));
        float v6 = (float)(-Math.toDegrees(Math.atan2(v4.y, Math.hypot(v4.x, v4.z))));
        this.method1317(v5, v6);
    }

    public BlockPos method1332() {
        return this.field5559;
    }

    private int method1334() {
        int v1;
        for (v1 = 0; v1 < 9; ++v1) {
            if (AutoCrystal.I.player.getInventory().getStack(v1).getItem() != Items.END_CRYSTAL) continue;
            return v1;
        }
        v1 = InventoryUtil.method3336(Items.END_CRYSTAL);
        if (v1 != -1 && v1 < 9) {
            return v1;
        }
        return -1;
    }

    private void method1335() {
        if (AutoCrystal.I.world != null && AutoCrystal.I.interactionManager instanceof IClientPlayerInteractionManagerMixin) {
            ((IClientPlayerInteractionManagerMixin)AutoCrystal.I.interactionManager).invokeSyncSelectedSlot();
        }
    }

    @EventHandler(2)
    public void minced$resetOnWorldSwap(EventSwapWorld event) {
        this.method1305();
    }

    public int method1336() {
        return this.field5564;
    }

    public float method1337() {
        return this.field5567;
    }

    private void method1338(EndCrystalEntity param1) {
        Vec3d v2 = t0.cfr_renamed_214((Entity)param1);
        if (v2 == null) {
            v2 = param1.getBoundingBox().getCenter().subtract(AutoCrystal.I.player.getEyePos());
        }
        float v3 = (float)MathHelper.wrapDegrees((double)(Math.toDegrees(Math.atan2(v2.z, v2.x)) - field5620));
        float v4 = (float)(-Math.toDegrees(Math.atan2(v2.y, Math.hypot(v2.x, v2.z))));
        this.method1317(v3, v4);
    }

    public SliderSetting method1339() {
        return this.field5577;
    }

    static {
        field5590 = "Auto Crystal";
        field5591 = "Автоматически ставит и ломает кристаллы";
        field5592 = "Радиус";
        field5593 = 5.0f;
        field5594 = 6.0f;
        field5595 = 0.1f;
        field5596 = "Задержка установки";
        field5597 = 20.0f;
        field5598 = "Задержка ломания";
        field5599 = 20.0f;
        field5600 = "Авто установка";
        field5601 = "Рендер";
        field5602 = "Режим свапа";
        field5603 = "Рука";
        field5604 = "Рука";
        field5605 = "Пакет";
        field5574 = "Рука";
        field5575 = 100L;
        field5607 = 0.5;
        field5608 = 0.5;
        field5609 = 3.5;
        field5610 = 6.0;
        field5611 = 0.7;
        field5612 = 255.0f;
        field5613 = 255.0f;
        field5614 = 255.0f;
        field5615 = 255.0f;
        field5616 = 0.3f;
        field5606 = Double.MAX_VALUE;
        field5571 = "Рука";
        field5572 = "Рука";
        field5588 = Double.MAX_VALUE;
        field5617 = 90.0;
        field5620 = 90.0;
        field5578 = 89.0f;
        field5579 = 89.0f;
        field5580 = -89.0f;
        field5581 = -89.0f;
        field5582 = -89.0f;
        field5583 = 89.0f;
        field5584 = 360.0f;
        field5585 = 360.0f;
        field5586 = 360.0f;
        field5587 = 360.0f;
        field5618 = 0.5;
        field5619 = 0.5;
    }
}




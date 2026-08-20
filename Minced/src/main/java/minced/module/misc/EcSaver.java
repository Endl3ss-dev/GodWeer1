/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.EnderChestBlockEntity
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.util.Formatting
 *  net.minecraft.util.Hand
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.world.chunk.WorldChunk
 *  sLM.4ZXYEo
 */
package minced.module.misc;
import minced.core.EventHandler;

import minced.core.BindSetting;
import minced.core.EventUpdate;
import minced.core.Rotation;
import minced.core.ChatUtil;
import minced.core.EventKey;
import minced.core.AimRotation;
import minced.core.Module;
import minced.enums.Combat;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.chunk.WorldChunk;
import sLM._4ZXYEo;

public class EcSaver
extends Module {
    static String lд;
    static String l7;

    static MinecraftClient I = MinecraftClient.getInstance();
    static double field3545;
    BindSetting field3546 = new BindSetting(field3558);
    BlockPos field3547;
    static double field3548;
    static double field3549;
    static double field3550;
    static double field3551;
    static float field3552;
    static double field3553;
    static double field3554;
    static double field3555;
    static String field3556 = "EcSaver";
    static String field3557 = "Автоматически открывает ближайший эндер-сундук";
    static String field3558 = "Открыть EC";

    private BlockPos method974() {
        if (EcSaver.I.world == null || EcSaver.I.player == null) {
            return null;
        }
        BlockPos v1 = null;
        double v2 = field3545;
        BlockPos v4 = EcSaver.I.player.getBlockPos();
        int v5 = (Integer)EcSaver.I.options.getViewDistance().getValue();
        int v6 = EcSaver.I.player.getChunkPos().x;
        int v7 = EcSaver.I.player.getChunkPos().z;
        for (int v8 = v6 - v5; v8 <= v6 + v5; ++v8) {
            for (int v9 = v7 - v5; v9 <= v7 + v5; ++v9) {
                if (!EcSaver.I.world.getChunkManager().isChunkLoaded(v8, v9)) continue;
                WorldChunk v10 = EcSaver.I.world.getChunk(v8, v9);
                for (BlockEntity v12 : v10.getBlockEntities().values()) {
                    BlockPos v13;
                    double v14;
                    if (!(v12 instanceof EnderChestBlockEntity) || !((v14 = v4.getSquaredDistance((Vec3i)(v13 = v12.getPos()))) < v2)) continue;
                    v2 = v14;
                    v1 = v13;
                }
            }
        }
        return v1;
    }

    @EventHandler(2)
    public void method975(EventKey param1) {
        if (param1.method3595() == ((Integer)this.field3546.getValue()).intValue() && !param1.method3599()) {
            this.field3547 = this.method974();
            if (this.field3547 != null) {
                ChatUtil.method3308(_4ZXYEo.я("makeConcatWithConstants", new Object[]{}, (String)String.valueOf(Formatting.GREEN), (int)this.field3547.getX(), (int)this.field3547.getY(), (int)this.field3547.getZ()), new Object[0]);
            } else {
                ChatUtil.method3308(_4ZXYEo.sh("makeConcatWithConstants", new Object[]{}, (String)String.valueOf(Formatting.RED)), new Object[0]);
            }
        }
    }

    @EventHandler(2)
    public void method976(EventUpdate param1) {
        if (this.field3547 != null && EcSaver.I.player != null && EcSaver.I.world != null) {
            Vec3d v2 = EcSaver.I.player.getEyePos();
            Vec3d v3 = new Vec3d((double)this.field3547.getX() + field3548, (double)this.field3547.getY() + field3549, (double)this.field3547.getZ() + field3550);
            Vec3d v4 = v3.subtract(v2);
            double v5 = Math.sqrt(v4.x * v4.x + v4.z * v4.z);
            float v7 = (float)(Math.toDegrees(Math.atan2(v4.z, v4.x)) - field3551);
            float v8 = (float)(-Math.toDegrees(Math.atan2(v4.y, v5)));
            AimRotation.method367(new Rotation(v7, v8), field3552, 1, 5);
            if (EcSaver.I.interactionManager != null) {
                Vec3d v9 = new Vec3d((double)this.field3547.getX() + field3553, (double)this.field3547.getY() + field3554, (double)this.field3547.getZ() + field3555);
                Direction v10 = Direction.UP;
                BlockHitResult v11 = new BlockHitResult(v9, v10, this.field3547, false);
                EcSaver.I.interactionManager.interactBlock(EcSaver.I.player, Hand.MAIN_HAND, v11);
            }
            this.field3547 = null;
        }
    }

    public EcSaver() {
        super(field3556, field3557, Combat.Miscellaneous);
        this.addSettings(this.field3546);
    }

    static {
        lд = "\u0001Найден эндер-сундук на позиции: \u0001, \u0001, \u0001";
        l7 = "\u0001Эндер-сундук не найден!";
        field3548 = 0.5;
        field3549 = 0.5;
        field3550 = 0.5;
        field3551 = 90.0;
        field3552 = 180.0f;
        field3553 = 0.5;
        field3554 = 0.5;
        field3555 = 0.5;
        field3545 = Double.MAX_VALUE;
    }
}




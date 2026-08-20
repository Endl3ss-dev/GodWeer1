/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.ChestBlockEntity
 *  net.minecraft.block.entity.MobSpawnerBlockEntity
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.vehicle.ChestMinecartEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.ChunkPos
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.chunk.WorldChunk
 *  sLM.4ZXYEo
 *  sg.ec.Ты$2
 *  sg.ec.Ты$Ч
 */
package minced.module.misc;
import minced.core.EventHandler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import minced.core.ColorUtil;
import minced.core.ProjectionUtil;
import minced.core.EventUpdate;
import minced.core.EventRender2D;
import minced.core.Fonts;
import minced.core.RenderUtil;
import minced.core.Module;
import minced.enums.Combat;
import minced.enums.NOTLOOTED;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.ChestMinecartEntity;
import org.joml.Vector2f;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.WorldChunk;
import sLM._4ZXYEo;
import sg.ec.ty;

public class KeyFinder
extends Module {
    static String Ю_;

    Set field3492;
    List field3493 = new CopyOnWriteArrayList();
    static MinecraftClient I = MinecraftClient.getInstance();
    static int field3494;
    static int field3495;
    static int field3496;
    static String field3497;
    static String field3498;
    static double field3499;
    static double field3500;
    static float field3501;
    static float field3502;
    static String field3503;
    static String field3504;
    static float field3505;
    static float field3506;
    static float field3507;
    static float field3508;
    static float field3509;
    static float l5;
    static float field3510;
    static int field3511;
    static String field3512;
    static String lI;
    static String l2;

    private void method956(ChestMinecartEntity param1) {
        BlockPos v2 = param1.getBlockPos();
        boolean v3 = this.method961(param1);
        boolean v4 = this.method963(param1);
        NOTLOOTED v5 = this.field3492.contains(v2) || v3 ? NOTLOOTED.LOOTED : (v4 ? NOTLOOTED.HAS_NAMETAG : NOTLOOTED.NOT_LOOTED);
        this.field3493.add(new ty._2(new Vec3d(param1.getX(), param1.getY(), param1.getZ()), param1.getId(), ty.ch.MINECART, v5, 0));
    }

    private boolean method957(BlockPos param1, List param2) {
        for (BlockPos v4 : (Iterable<BlockPos>)(param2)) {
            if (!this.method965(v4)) continue;
            return true;
        }
        return false;
    }

    private void method958(BlockPos param1) {
        List v2 = this.method959(param1);
        if (v2.isEmpty()) {
            return;
        }
        boolean v3 = this.method957(param1, v2);
        boolean v4 = this.method962(v2);
        NOTLOOTED v5 = this.field3492.contains(param1) || v3 ? NOTLOOTED.LOOTED : (v4 ? NOTLOOTED.HAS_NAMETAG : NOTLOOTED.NOT_LOOTED);
        this.field3493.add(new ty._2(param1, -1, ty.ch.SPAWNER, v5, v2.size()));
    }

    private List method959(BlockPos param1) {
        ArrayList<BlockPos> v2 = new ArrayList<BlockPos>();
        for (int v3 = -5; v3 <= 5; ++v3) {
            for (int v4 = -3; v4 <= 3; ++v4) {
                for (int v5 = -5; v5 <= 5; ++v5) {
                    BlockPos v6 = param1.add(v3, v4, v5);
                    BlockEntity v7 = KeyFinder.I.world.getBlockEntity(v6);
                    if (!(v7 instanceof ChestBlockEntity)) continue;
                    v2.add(v6);
                }
            }
        }
        return v2;
    }

    private void method960() {
        this.field3493.clear();
        int v1 = (Integer)KeyFinder.I.options.getViewDistance().getValue();
        int v2 = KeyFinder.I.player.getChunkPos().x;
        int v3 = KeyFinder.I.player.getChunkPos().z;
        for (int v4 = v2 - v1; v4 <= v2 + v1; ++v4) {
            for (int v5 = v3 - v1; v5 <= v3 + v1; ++v5) {
                new ChunkPos(v4, v5);
                if (!KeyFinder.I.world.getChunkManager().isChunkLoaded(v4, v5)) continue;
                WorldChunk v7 = KeyFinder.I.world.getChunk(v4, v5);
                for (BlockEntity v9 : v7.getBlockEntities().values()) {
                    if (!(v9 instanceof MobSpawnerBlockEntity)) continue;
                    BlockPos v10 = v9.getPos();
                    this.method958(v10);
                }
            }
        }
        for (Entity v5 : KeyFinder.I.world.getEntities()) {
            if (!(v5 instanceof ChestMinecartEntity)) continue;
            ChestMinecartEntity v6 = (ChestMinecartEntity)v5;
            this.method956(v6);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.field3493.clear();
        this.field3492.clear();
    }

    private boolean method961(ChestMinecartEntity param1) {
        int v5;
        int v4;
        BlockPos v2 = param1.getBlockPos();
        int v3 = 0;
        for (v4 = -3; v4 <= 3; ++v4) {
            for (v5 = -2; v5 <= 2; ++v5) {
                for (int v6 = -3; v6 <= 3; ++v6) {
                    BlockPos v7 = v2.add(v4, v5, v6);
                    BlockState v8 = KeyFinder.I.world.getBlockState(v7);
                    if (v8.getBlock() != Blocks.TORCH && v8.getBlock() != Blocks.WALL_TORCH && v8.getBlock() != Blocks.COBBLESTONE && v8.getBlock() != Blocks.LADDER) continue;
                    ++v3;
                }
            }
        }
        v4 = 1;
        for (v5 = 0; v5 < param1.size(); ++v5) {
            if (param1.getStack(v5).isEmpty()) continue;
            v4 = 0;
            break;
        }
        return v3 >= 2 || v4 != 0;
    }

    private boolean method962(List param1) {
        for (BlockPos v3 : (Iterable<BlockPos>)(param1)) {
            BlockEntity v4 = KeyFinder.I.world.getBlockEntity(v3);
            if (!(v4 instanceof ChestBlockEntity)) continue;
            ChestBlockEntity v5 = (ChestBlockEntity)v4;
            for (int v6 = 0; v6 < v5.size(); ++v6) {
                ItemStack v7 = v5.getStack(v6);
                if (v7.isEmpty() || v7.getItem() != Items.NAME_TAG) continue;
                return true;
            }
        }
        return false;
    }

    private boolean method963(ChestMinecartEntity param1) {
        for (int v2 = 0; v2 < param1.size(); ++v2) {
            ItemStack v3 = param1.getStack(v2);
            if (v3.isEmpty() || v3.getItem() != Items.NAME_TAG) continue;
            return true;
        }
        return false;
    }

    private int method964(NOTLOOTED param1) {
        int n;
        switch (param1) {
            default: {
                throw new MatchException(null, null);
            }
            case LOOTED: {
                n = field3494;
                break;
            }
            case NOT_LOOTED: {
                n = field3495;
                break;
            }
            case HAS_NAMETAG: {
                n = field3496;
            }
        }
        return n;
    }

    private boolean method965(BlockPos param1) {
        int v2 = 0;
        BlockPos[] v3 = new BlockPos[]{param1.up(), param1.down(), param1.north(), param1.south(), param1.east(), param1.west()};
        for (int v6 = 0; v6 < 6; ++v6) {
            BlockPos v7 = v3[v6];
            BlockState v8 = KeyFinder.I.world.getBlockState(v7);
            if (!v8.isAir() && v8.getBlock() != Blocks.TORCH && v8.getBlock() != Blocks.WALL_TORCH && v8.getBlock() != Blocks.RAIL && v8.getBlock() != Blocks.POWERED_RAIL && v8.getBlock() != Blocks.DETECTOR_RAIL && v8.getBlock() != Blocks.ACTIVATOR_RAIL) continue;
            ++v2;
        }
        return v2 >= 3;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.field3493.clear();
    }

    @EventHandler(2)
    public void method966(EventUpdate param1) {
        if (KeyFinder.I.world == null || KeyFinder.I.player == null) {
            return;
        }
        this.method960();
    }

    public KeyFinder() {
        super(field3497, field3498, Combat.Miscellaneous);
        this.field3492 = new HashSet();
    }

    @EventHandler(2)
    public void method967(EventRender2D param1) {
        if (KeyFinder.I.world == null || KeyFinder.I.player == null) {
            return;
        }
        float v2 = param1.method2996();
        for (ty._2 v4 : (Iterable<ty._2>)(this.field3493)) {
            double v9;
            double v7;
            double v5;
            Vector2f v11;
            if (v4.н == ty.ch.MINECART && v4.cfr_renamed != -1) {
                Entity v10 = KeyFinder.I.world.getEntityById(v4.cfr_renamed);
                if (v10 == null) continue;
                Vec3d v12x = v10.getPos();
                double v13x = v12x.x + (v10.getX() - v12x.x) * (double)v2;
                double v15 = v12x.y + (v10.getY() - v12x.y) * (double)v2;
                double v17 = v12x.z + (v10.getZ() - v12x.z) * (double)v2;
                v5 = v13x;
                v7 = v15 + field3499;
                v9 = v17;
            } else {
                Vec3d v11x = v4.а();
                v5 = v11x.x;
                v7 = v11x.y + field3500;
                v9 = v11x.z;
            }
            if ((v11 = ProjectionUtil.method2005(new Vec3d(v5, v7, v9))) == null || v11.x == field3501 || v11.y == field3502) continue;
            float v12 = v11.x;
            float v13 = v11.y;
            Object v14 = v4.н == ty.ch.SPAWNER ? _4ZXYEo.n4("makeConcatWithConstants", new Object[]{}, (int)v4.cfr_renamed) : field3503;
            String v15 = this.method968(v4.lootState);
            double v16 = KeyFinder.I.player.getPos().distanceTo(v4.а());
            String v18 = String.format(field3504, v16);
            float v19 = Fonts.field12486[15].method2719((String)v14);
            float v20 = Fonts.field12486[15].method2719(v15);
            float v21 = Fonts.field12486[15].method2719(v18);
            float v22 = Math.max(Math.max(v19, v20), v21);
            float v23 = v12 - v22 / 2.0f - field3505;
            float v24 = v13 - field3506;
            float v25 = v22 + field3507;
            float v26 = field3508;
            RenderUtil.method390(v23, v24, v25, v26, 2.0f, ColorUtil.method1852(20, 20, 20, 180), field3509);
            Fonts.field12486[15].method2711(param1.method2997(), (String)v14, v12 - v19 / 2.0f, v13, -256);
            Fonts.field12486[15].method2711(param1.method2997(), v15, v12 - v20 / 2.0f, v13 + l5, this.method964(v4.lootState));
            Fonts.field12486[15].method2711(param1.method2997(), v18, v12 - v21 / 2.0f, v13 + field3510, field3511);
        }
    }

    private String method968(NOTLOOTED param1) {
        String string;
        switch (param1) {
            default: {
                throw new MatchException(null, null);
            }
            case LOOTED: {
                string = field3512;
                break;
            }
            case NOT_LOOTED: {
                string = lI;
                break;
            }
            case HAS_NAMETAG: {
                string = l2;
            }
        }
        return string;
    }

    static {
        field3497 = "Key Finder";
        field3498 = "Находит места с бирками (спавнеры, вагонетки)";
        field3499 = 1.5;
        field3500 = 1.5;
        field3501 = Float.MAX_VALUE;
        field3502 = Float.MAX_VALUE;
        Ю_ = "Спавнер (\u0001 сундуков)";
        field3503 = "Вагонетка";
        field3504 = "%.1f m";
        field3505 = 4.0f;
        field3506 = 4.0f;
        field3507 = 8.0f;
        field3508 = 38.0f;
        field3509 = 0.7f;
        l5 = 12.0f;
        field3510 = 24.0f;
        field3511 = -5592406;
        field3512 = "Залутано";
        lI = "Не залутано";
        l2 = "Есть бирка!";
        field3494 = -43691;
        field3495 = -11141291;
        field3496 = -11141121;
    }
}




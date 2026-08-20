/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.Blocks
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.registry.Registries
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Vec3d
 *  sg.ec.ТГ$Ч
 *  sg.ec.chI
 *  sg.ec.Чг$Ч
 */
package minced.module.visual;
import minced.core.EventHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import minced.core.BooleanSetting;
import minced.core.SliderSetting;
import minced.core.EventRender3D;
import minced.core.EventUpdate;
import minced.core.Render2DBatch;
import minced.core.Minced;
import minced.core.Module;
import minced.enums.Combat;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import sg.ec.tg;
import sg.ec.chI;
import sg.ec.chg;

public class BlockESP
extends Module {
    static String field4679 = "Block ESP";
    static String field4680 = "Подсвечивает выбранные блоки через стены";
    static String field4681 = "Радиус";
    static float field4682 = 32.0f;
    static float field4683 = 8.0f;
    static float field4684 = 48.0f;
    SliderSetting field4685 = new SliderSetting(field4681, field4682, field4683, field4684, 1.0f);
    static String field4686 = "Заливка";
    BooleanSetting field4687 = new BooleanSetting(field4686, true);
    List field4688 = new ArrayList();
    int field4689 = 0;
    static MinecraftClient I = MinecraftClient.getInstance();
    static double field4690;

    public BlockESP() {
        super(field4679, field4680, Combat.Visuals);
        this.addSettings(this.field4685, this.field4687);
    }

    @EventHandler(2)
    public void method1181(EventUpdate param1) {
        chg.ch v62;
        if (BlockESP.I.world == null || BlockESP.I.player == null) {
            this.field4688.clear();
            return;
        }
        if (++this.field4689 < 5) {
            return;
        }
        this.field4689 = 0;
        minced.manager.BlockESP v2 = Minced.getInstance().getBlockESPManager();
        if (v2 == null) {
            this.field4688.clear();
            return;
        }
        List v3 = v2.method1713();
        if (v3.isEmpty()) {
            this.field4688.clear();
            return;
        }
        HashMap<Block, Integer> v4 = new HashMap<Block, Integer>();
        for (chg.ch v62_86 : (Iterable<chg.ch>)(v3)) {
            Block v9;
            Identifier v8;
            String v7 = v62_86.ф2();
            if (v7 == null || v7.isEmpty() || (v8 = chI.И((String)v7)) == null || (v9 = (Block)Registries.BLOCK.get(v8)) == null || v9 == Blocks.AIR) continue;
            v4.put(v9, v62_86.фВ());
        }
        if (v4.isEmpty()) {
            this.field4688.clear();
            return;
        }
        int v5 = ((Float)this.field4685.getValue()).intValue();
        BlockPos v62s = BlockESP.I.player.getBlockPos();
        this.field4688.clear();
        for (int v7 = -v5; v7 <= v5; ++v7) {
            for (int v8 = -v5; v8 <= v5; ++v8) {
                for (int v9 = -v5; v9 <= v5; ++v9) {
                    Block v11;
                    Integer v12;
                    BlockPos v10 = v62s.add(v7, v8, v9);
                    if (!BlockESP.I.world.isChunkLoaded(v10) || (v12 = (Integer)v4.get(v11 = BlockESP.I.world.getBlockState(v10).getBlock())) == null) continue;
                    Box v13 = new Box((double)v10.getX(), (double)v10.getY(), (double)v10.getZ(), (double)(v10.getX() + 1), (double)(v10.getY() + 1), (double)(v10.getZ() + 1)).expand(field4690);
                    this.field4688.add(new tg.ch(v13, v12.intValue()));
                }
            }
        }
    }

    @EventHandler(2)
    public void method1182(EventRender3D param1) {
        if (!this.isEnabled() || BlockESP.I.world == null || BlockESP.I.player == null) {
            return;
        }
        MatrixStack v2 = param1.method2348();
        Vec3d v3 = BlockESP.I.gameRenderer.getCamera().getPos();
        if (this.field4688.isEmpty()) {
            return;
        }
        v2.push();
        v2.translate(-v3.x, -v3.y, -v3.z);
        for (tg.ch v5 : (Iterable<tg.ch>)(this.field4688)) {
            Render2DBatch.method2661(v5.box(), v5.color(), (Boolean)this.field4687.getValue(), true);
        }
        Render2DBatch.method2662(v2);
        v2.pop();
    }

    static {
        field4690 = 0.001;
    }
}




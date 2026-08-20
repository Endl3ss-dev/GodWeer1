/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.item.ItemConvertible
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Vec3d
 *  org.joml.Vector2f
 *  sLM.4ZXYEo
 *  sg.ec.Пв$2
 */
package minced.module.visual;
import minced.core.EventHandler;

import java.lang.invoke.CallSite;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import minced.core.ColorUtil;
import minced.core.ProjectionUtil;
import minced.core.EventRender3D;
import minced.core.EventSwapWorld;
import minced.core.EventPacket;
import minced.core.Render2DBatch;
import minced.core.ObjectItemPos;
import minced.core.EventRender2D;
import minced.core.ThemeColors;
import minced.core.Fonts;
import minced.core.RenderUtil;
import minced.core.Module;
import minced.enums.Combat;
import minced.enums.MAIN;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector2f;
import sLM._4ZXYEo;
import sg.ec.pv;

public class ObjectItem
extends Module {
    static String cfr_renamed_392;
    static String cfr_renamed_393;

    public static MinecraftClient I = MinecraftClient.getInstance();
    Map field2619 = new ConcurrentHashMap();
    static double field2620;
    static double field2621;
    static double field2622;
    static float field2623;
    static float field2624;
    static double field2625;
    static float field2626;
    static float field2627;
    static float field2628;
    static float field2629;
    static float field2630;
    static float field2631;
    static float field2632;
    static String field2633;
    static String field2634;
    static float field2635;
    static float field2636;
    static String field2637;
    static String field2638;
    static float field2639;
    static float field2640;
    static String field2641;
    static double field2642;
    static String field2643;
    static float field2644;
    static double field2645;
    static double field2646;
    static double field2647;
    static double field2648;

    @EventHandler(2)
    public void method701(EventRender2D param1) {
        if (!this.isEnabled()) {
            return;
        }
        if (ObjectItem.I.player == null || ObjectItem.I.world == null) {
            return;
        }
        if (this.field2619.isEmpty()) {
            return;
        }
        for (ObjectItemPos v3 : (Iterable<ObjectItemPos>)(this.field2619.values())) {
            long v4 = v3.I.method3112();
            long v6 = v3.н.cfr_renamed - v4;
            if (v6 <= 0L) continue;
            Vec3d v8 = new Vec3d((double)v3.cfr_renamed_136.getX() + field2620, (double)v3.cfr_renamed_136.getY(), (double)v3.cfr_renamed_136.getZ() + field2621);
            double v9 = v8.x;
            double v11 = v8.z;
            double v13 = v8.y;
            double v15 = v13 + Math.max(0.0, v3.н.cfr_renamed);
            double v17 = (v13 + v15) / field2622;
            Vector2f v19 = ProjectionUtil.method2008(v9, v17, v11);
            if (v19.x == field2623 && v19.y == field2624) continue;
            ItemStack v20 = new ItemStack((ItemConvertible)v3.н.ь);
            int v21 = (int)Math.ceil((double)v6 / field2625);
            String v22 = _4ZXYEo.弟И("makeConcatWithConstants", new Object[]{}, (String)String.valueOf(v21 < 10 ? _4ZXYEo.弟0("makeConcatWithConstants", new Object[]{}, (int)v21) : Integer.valueOf(v21)));
            float v23 = field2626;
            float v24 = Fonts.field12486[14].method2719((String)((Object)v22)) + field2627;
            float v25 = v19.x - v24 / 2.0f;
            float v26 = v19.y - v23 / 2.0f;
            Render2DBatch.method2668(param1.method2997());
            Render2DBatch.method2672(v25, v26, v24, v23, ColorUtil.method1852(0, 0, 0, 120));
            Render2DBatch.method2651();
            RenderUtil.method398(param1.method2997(), v20, v25 + field2628, v26 + field2629, field2630, -1, 1.0f);
            Fonts.field12486[14].method2711(param1.method2997(), (String)((Object)v22), v25 + field2631, v26 + field2632, -1);
        }
    }

    public ObjectItem() {
        super(field2633, field2634, Combat.Visuals);
    }

    @EventHandler(2)
    public void method703(EventRender3D param1) {
        if (ObjectItem.I.player == null || ObjectItem.I.world == null) {
            return;
        }
        if (!this.isEnabled()) {
            return;
        }
        MatrixStack v2 = param1.method2348();
        Vec3d v3 = ObjectItem.I.gameRenderer.getCamera().getPos();
        this.field2619.entrySet().removeIf(param0 -> ((ObjectItemPos)((java.util.Map.Entry)param0).getValue()).I.method3110(((ObjectItemPos)((java.util.Map.Entry)param0).getValue()).н.cfr_renamed));
        if (this.field2619.isEmpty()) {
            return;
        }
        int v4 = ThemeColors.method3199(MAIN.MODULE_VISUAL);
        int v5 = ColorUtil.method1853(v4, (float)ColorUtil.cfr_renamed_0(v4) / field2635 * field2636);
        v2.push();
        v2.translate(-v3.x, -v3.y, -v3.z);
        for (ObjectItemPos v7 : (Iterable<ObjectItemPos>)(this.field2619.values())) {
            this.method708(v2, v7, v5);
        }
        Render2DBatch.method2662(v2);
        v2.pop();
    }

    private void method704(String param1, double param2, double param4, double param6, float param8, float param9) {
        BlockPos v11;
        BlockPos v10 = BlockPos.ofFloored((double)param2, (double)param4, (double)param6);
        if ((param1.contains(field2637) || param1.contains(field2638)) && param8 == field2639 || param8 == field2640 && param9 == 1.0f) {
            this.field2619.put(v10, new ObjectItemPos(v10, pv._2.TRAP));
        }
        if (param1.contains(field2641) && (param8 == 1.0f || param9 == 1.0f)) {
            v11 = v10.down((int)Math.round(pv._2.STAN.cfr_renamed / field2642));
            this.field2619.put(v11, new ObjectItemPos(v11, pv._2.BOOM_TRAP));
        }
        if (param1.contains(field2643) && (param8 == field2644 || param9 == 1.0f)) {
            v11 = v10.down((int)Math.round(pv._2.STAN.cfr_renamed / field2645));
            this.field2619.put(v11, new ObjectItemPos(v11, pv._2.STAN));
        }
    }

    @EventHandler(2)
    public void method705(EventPacket param1) {
        if (!this.isEnabled()) {
            return;
        }
        if (!param1.method2466()) {
            return;
        }
        Packet v3 = param1.method2467();
        if (v3 instanceof PlaySoundS2CPacket) {
            PlaySoundS2CPacket v2 = (PlaySoundS2CPacket)v3;
            this.method704(v2.getSound().getIdAsString(), v2.getX(), v2.getY(), v2.getZ(), v2.getVolume(), v2.getPitch());
        }
    }

    public Map method706() {
        return this.field2619;
    }

    @EventHandler(2)
    public void method707(EventSwapWorld param1) {
        this.field2619.clear();
    }

    private void method708(MatrixStack param1, ObjectItemPos param2, int param3) {
        Vec3d v4 = new Vec3d((double)param2.cfr_renamed_136.getX() + field2646, (double)param2.cfr_renamed_136.getY(), (double)param2.cfr_renamed_136.getZ() + field2647);
        double v5 = param2.н.cfr_renamed;
        double v7 = v4.y;
        double v9 = v7 + Math.max(0.0, param2.н.cfr_renamed);
        double v11 = v4.x - v5;
        double v13 = v4.x + v5;
        double v15 = v4.z - v5;
        double v17 = v4.z + v5;
        Box v19 = new Box(v11, v7, v15, v13, v9, v17).expand(field2648);
        Render2DBatch.method2661(v19, param3, true, true);
    }

    @Override
    public void onDisable() {
        this.field2619.clear();
        super.onDisable();
    }

    static {
        field2633 = "Object Item";
        field2634 = "Рисует объекты по звукам (трапка/пласт/стан/взрыв)";
        field2620 = 0.5;
        field2621 = 0.5;
        field2622 = 2.0;
        field2623 = Float.MAX_VALUE;
        field2624 = Float.MAX_VALUE;
        field2625 = 1000.0;
        cfr_renamed_392 = "0\u0001";
        cfr_renamed_393 = "0:\u0001";
        field2626 = 11.0f;
        field2627 = 14.0f;
        field2628 = 0.5f;
        field2629 = 1.5f;
        field2630 = 0.5f;
        field2631 = 11.0f;
        field2632 = 4.0f;
        field2637 = "minecraft:entity.wither.break_block";
        field2638 = "minecraft:block.anvil.place";
        field2639 = 0.7f;
        field2640 = 0.2f;
        field2641 = "minecraft:entity.generic.explode";
        field2642 = 2.0;
        field2643 = "minecraft:block.beacon.deactivate";
        field2644 = 1.5f;
        field2645 = 2.0;
        field2635 = 255.0f;
        field2636 = 0.9f;
        field2646 = 0.5;
        field2647 = 0.5;
        field2648 = 0.001;
    }
}




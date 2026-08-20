/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.sound.SoundEvents
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.Vec3d
 *  org.joml.Vector2f
 */
package minced.module.visual;
import minced.core.EventHandler;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import minced.core.ColorUtil;
import minced.core.BooleanSetting;
import minced.core.ProjectionUtil;
import minced.core.EventSwapWorld;
import minced.core.EventPacket;
import minced.core.FireworkPoint;
import minced.core.Render2DBatch;
import minced.core.EventRender2D;
import minced.core.Fonts;
import minced.core.Module;
import minced.enums.Combat;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector2f;

public class FireworkESP
extends Module
{
    static double field889;
    List field890;
    static float field891;
    static String field892;
    static double field893;
    static float field894;
    static float field895;
    static float field896;
    static float field897;
    static String field898;
    static String field899;
    static float field900;
    static float field901;
    static float field902;
    static float field903;
    static float field904;
    static String field905;
    static double field906;
    static float field907;
    static float field908;
    static float field909;
    static double field910;
    static float field911;
    static float field912;
    BooleanSetting field913 = new BooleanSetting(field916, true);
    static String field914 = "Firework ESP";
    static String field915 = "Показывает тег на позиции использованного фейерверка";
    static String field916 = "Отображать время";

    @Override
    public void onDisable() {
        this.field890.clear();
        super.onDisable();
    }

    private void method294(EventRender2D param1, FireworkPoint param2, Vector2f param3, boolean param4) {
        float v5 = param3.x - field891;
        float v6 = param3.y;
        float v7 = param4 ? Fonts.field12486[14].method2719(String.format(field892, Math.max(0.0, (double)(param2.method2632() - param2.method2630().method3112()) / field893))) + field894 : field895;
        Render2DBatch.method2668(param1.method2997());
        Render2DBatch.method2672(v5, v6, v7, field896, ColorUtil.method1852(0, 0, 0, (int)(field897 * param2.method2628())));
        Render2DBatch.method2651();
        DrawContext context = param1.method2997();
        if (context != null) {
            context.getMatrices().push();
            context.getMatrices().translate(v5 + field900, v6 + field901 - 2.5f, 0.0f);
            context.getMatrices().scale(0.7f, 0.7f, 1.0f);
            context.drawItem(Items.FIREWORK_ROCKET.getDefaultStack(), 0, 0);
            context.getMatrices().pop();
        }
        if (param4) {
            Fonts.field12486[14].method2711(param1.method2997(), String.format(field905, Math.max(0.0, (double)(param2.method2632() - param2.method2630().method3112()) / field906)), v5 + field907, v6 + field908, ColorUtil.method1852(255, 255, 255, (int)(field909 * param2.method2628())));
        }
    }

    private boolean method295(Vec3d param12) {
        return this.field890.stream().filter(param0 -> ((FireworkPoint)param0).method2631()).anyMatch(param1 -> {
            double v2 = param12.squaredDistanceTo(((FireworkPoint)param1).method2629());
            return v2 <= field889;
        });
    }

    @EventHandler(2)
    public void method296(EventPacket param1) {
        if (!param1.method2466()) {
            return;
        }
        Packet v3 = param1.method2467();
        if (v3 instanceof PlaySoundS2CPacket) {
            Vec3d v4;
            PlaySoundS2CPacket v2 = (PlaySoundS2CPacket)v3;
            RegistryEntry<SoundEvent> v3s = v2.getSound();
            if (v3s.value().equals(SoundEvents.ENTITY_FIREWORK_ROCKET_LAUNCH) && !this.method295(v4 = new Vec3d(v2.getX(), v2.getY(), v2.getZ()))) {
                this.field890.add(new FireworkPoint(v4));
            }
        }
    }

    @EventHandler(2)
    public void method297(EventRender2D param1) {
        if (this.field890.isEmpty()) {
            return;
        }
        this.method298();
        for (FireworkPoint v3 : (Iterable<FireworkPoint>)(this.field890)) {
            v3.method2625();
            Vec3d v4 = v3.method2629();
            Vector2f v5 = ProjectionUtil.method2008(v4.x, v4.y, v4.z);
            if (!Float.isFinite(v5.x) || !Float.isFinite(v5.y) || v5.x == field911 || v5.y == field912) continue;
            this.method294(param1, v3, v5, (Boolean)this.field913.getValue());
        }
    }

    public FireworkESP() {
        super(field914, field915, Combat.Visuals);
        this.field890 = new CopyOnWriteArrayList();
        this.addSettings(this.field913);
    }

    private void method298() {
        this.field890.removeIf(param0 -> {
            if (((FireworkPoint)param0).method2630().method3112() >= ((FireworkPoint)param0).method2632()) {
                if (!((FireworkPoint)param0).method2624()) {
                    ((FireworkPoint)param0).method2626();
                }
                return ((FireworkPoint)param0).method2627();
            }
            return false;
        });
    }

    @EventHandler(2)
    public void O(EventSwapWorld param1) {
        this.field890.clear();
    }

    static {
        field911 = Float.MAX_VALUE;
        field912 = Float.MAX_VALUE;
        field891 = 20.0f;
        field892 = "%.1f сек.";
        field893 = 1000.0;
        field894 = 14.0f;
        field895 = 10.0f;
        field896 = 11.0f;
        field897 = 120.0f;
        field898 = "minecraft";
        field899 = "textures/item/firework_rocket.png";
        field900 = 0.5f;
        field901 = 1.5f;
        field902 = 8.0f;
        field903 = 8.0f;
        field904 = 255.0f;
        field905 = "%.1f сек.";
        field906 = 1000.0;
        field907 = 11.0f;
        field908 = 4.0f;
        field909 = 255.0f;
        field910 = 25.0;
        field889 = 25.0;
    }
}




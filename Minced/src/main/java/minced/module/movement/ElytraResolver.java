/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.util.math.Vec3d
 */
package minced.module.movement;
import minced.core.BooleanSetting;
import minced.core.SliderSetting;
import minced.core.ModeSetting;
import minced.core.Module;
import minced.enums.APPROACHING;
import minced.enums.Combat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;

public class ElytraResolver
extends Module
{
    public static long и;
    APPROACHING field806;
    Vec3d field807;
    long field808;
    static MinecraftClient I = MinecraftClient.getInstance();
    LivingEntity field809;
    SliderSetting field810 = new SliderSetting(field828, field829, field830, field831, field832);
    SliderSetting field811 = new SliderSetting(field833, field834, field835, field836, field837);
    static long field812;
    static double field813;
    static double field814;
    static double field815;
    static double field816;
    SliderSetting field817 = new SliderSetting(field838, field839, 0.0f, field840, 1.0f);
    BooleanSetting field818;
    boolean field819;
    ModeSetting field820 = new ModeSetting(field841, field842, field843, field844, field845);
    static String field821;
    static String field822;
    static double field823;
    static double field824;
    static double field825;
    static String field826;
    static String field827;
    static String field828;
    static float field829;
    static float field830;
    static float field831;
    static float field832;
    static String field833;
    static float field834;
    static float field835;
    static float field836;
    static float field837;
    static String field838;
    static float field839;
    static float field840;
    static String field841;
    static String field842;
    static String field843;
    static String field844;
    static String field845;
    static String field846;

    @Override
    public void onDisable() {
        super.onDisable();
        this.method265();
    }

    private void method260() {
        this.field806 = APPROACHING.APPROACHING;
        this.field807 = null;
        this.field808 = System.currentTimeMillis();
    }

    private void method261() {
        this.field806 = APPROACHING.RETREATING;
        this.field807 = null;
        this.field808 = System.currentTimeMillis();
    }

    public Vec3d method262(LivingEntity param1, Vec3d param2) {
        if (ElytraResolver.I.player == null || param1 == null || param2 == null) {
            return param2;
        }
        if (!ElytraResolver.I.player.isGliding()) {
            this.method265();
            return param2;
        }
        if (param1 != this.field809) {
            this.field809 = param1;
            this.field806 = APPROACHING.APPROACHING;
            this.field807 = null;
            this.field808 = System.currentTimeMillis();
        }
        double v3 = ElytraResolver.I.player.getEyePos().distanceTo(param2);
        if (this.field806 == APPROACHING.APPROACHING && v3 <= (double)((Float)this.field810.getValue()).floatValue()) {
            this.method261();
        } else if (this.field806 == APPROACHING.RETREATING) {
            boolean v6;
            boolean v5 = v3 >= (double)((Float)this.field811.getValue()).floatValue();
            boolean bl = v6 = System.currentTimeMillis() - this.field808 > field812;
            if (v5 || v6) {
                this.method260();
            }
        }
        if (this.field806 == APPROACHING.APPROACHING) {
            return param2;
        }
        if (this.field807 == null || this.field807.lengthSquared() < field813) {
            this.field807 = this.method266(param2, ElytraResolver.I.player.getPos());
        }
        return param2.add(this.field807.multiply((double)((Float)this.field811.getValue()).floatValue()));
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.method265();
    }

    public void method264(LivingEntity param1) {
        if (param1 == null) {
            return;
        }
        if (param1 != this.field809) {
            this.field809 = param1;
        }
        if (this.field806 == APPROACHING.APPROACHING) {
            this.method261();
        }
    }

    public void method265() {
        this.field806 = APPROACHING.APPROACHING;
        this.field809 = null;
        this.field807 = null;
        this.field808 = 0L;
    }

    private Vec3d method266(Vec3d param1, Vec3d param2) {
        double d;
        Vec3d v3 = param2.subtract(param1);
        v3 = new Vec3d(v3.x, 0.0, v3.z);
        if (v3.lengthSquared() < field814) {
            Vec3d v4 = ElytraResolver.I.player.getRotationVector().multiply(field815);
            v3 = new Vec3d(v4.x, 0.0, v4.z);
        }
        if (v3.lengthSquared() < field816) {
            v3 = new Vec3d(0.0, 0.0, 1.0);
        }
        v3 = v3.normalize();
        double v4 = ((Float)this.field817.getValue()).floatValue();
        if (((Boolean)this.field818.getValue()).booleanValue()) {
            v4 = this.field819 ? v4 : -v4;
            this.field819 = !this.field819;
        }
        double v6 = Math.toRadians(v4);
        double v8 = v3.x * Math.cos(v6) - v3.z * Math.sin(v6);
        double v10 = v3.x * Math.sin(v6) + v3.z * Math.cos(v6);
        String v14 = (String)this.field820.getValue();
        int v15 = -1;
        switch (v14.hashCode()) {
            case 995374234: {
                if (!v14.equals(field821)) break;
                v15 = 0;
                break;
            }
            case -783225797: {
                if (!v14.equals(field822)) break;
                v15 = 1;
            }
        }
        switch (v15) {
            case 0: {
                d = field823;
                break;
            }
            case 1: {
                d = field824;
                break;
            }
            default: {
                d = field825;
            }
        }
        double v12 = d;
        return new Vec3d(v8, v12, v10).normalize();
    }

    public ElytraResolver() {
        super(field826, field827, Combat.Movement);
        this.field818 = new BooleanSetting(field846, true);
        this.field806 = APPROACHING.APPROACHING;
        this.addSettings(this.field810, this.field811, this.field817, this.field820, this.field818);
    }

    static {
        и = 0L;
        field826 = "Elytra Resolver";
        field827 = "Долет и отлет для элитра-ротки";
        field828 = "Долет";
        field829 = 3.0f;
        field830 = 1.5f;
        field831 = 6.0f;
        field832 = 0.1f;
        field833 = "Отлет";
        field834 = 8.0f;
        field835 = 4.0f;
        field836 = 16.0f;
        field837 = 0.5f;
        field838 = "Угол отлета";
        field839 = 50.0f;
        field840 = 90.0f;
        field841 = "Режим отлета";
        field842 = "В сторону";
        field843 = "В сторону";
        field844 = "Вверх";
        field845 = "Диагональ";
        field846 = "Чередовать стороны";
        field812 = 2000L;
        field813 = 1.0E-6;
        field814 = 1.0E-6;
        field815 = -1.0;
        field816 = 1.0E-6;
        field821 = "Вверх";
        field822 = "Диагональ";
        field823 = 0.55;
        field824 = 0.25;
        field825 = 0.06;
    }
}




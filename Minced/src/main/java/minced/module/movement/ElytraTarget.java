/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.util.math.Vec3d
 */
package minced.module.movement;
import minced.core.BooleanSetting;
import minced.core.SliderSetting;
import minced.core.ModeSetting;
import minced.core.TimerUtil;
import minced.core.Module;
import minced.enums.Combat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;

public class ElytraTarget
extends Module {
    static double и;

    BooleanSetting field4717;
    static MinecraftClient I = MinecraftClient.getInstance();
    static double field4718;
    static double field4719;
    static double field4720;
    static double field4721;
    static double field4722;
    BooleanSetting field4723;
    static String field4724;
    static String field4725;
    static String field4726;
    static String field4727;
    static String field4728;
    static String field4729;
    ModeSetting field4730 = new ModeSetting(field4726, field4727, field4728, field4729);

    public boolean isPriorityMode(String mode) {
        return this.field4730.isMode(mode);
    }
    static String field4731;
    static String field4732;
    static String field4733;
    static float field4734;
    static float field4735;
    static float field4736;
    public SliderSetting field4737;

    public boolean cfr_renamed_381() {
        return this.isEnabled() && (Boolean)this.field4717.getValue() != false;
    }

    public boolean method1185(LivingEntity param1) {
        if (ElytraTarget.I.player == null || param1 == null) {
            return false;
        }
        Vec3d v2 = ElytraTarget.I.player.getVelocity();
        Vec3d v3 = new Vec3d(v2.x, 0.0, v2.z);
        if (v3.lengthSquared() < field4718) {
            return false;
        }
        v3 = v3.normalize();
        Vec3d v4 = ElytraTarget.I.player.getEyePos();
        Vec3d v5 = param1.getPos().add(0.0, (double)param1.getHeight() * field4719, 0.0).subtract(v4);
        Vec3d v6 = new Vec3d(v5.x, 0.0, v5.z);
        if (v6.lengthSquared() < field4720) {
            return false;
        }
        return v3.dotProduct(v6 = v6.normalize()) < field4721;
    }

    public boolean method1186(Entity param1) {
        LivingEntity v2;
        return this.cfr_renamed_381() && param1 instanceof LivingEntity && (v2 = (LivingEntity)param1).isGliding() && TimerUtil.method3547(v2) > field4722;
    }

    public boolean cfr_renamed_382() {
        return this.isEnabled() && (Boolean)this.field4723.getValue() != false && (Boolean)this.field4717.getValue() != false;
    }

    public ElytraTarget() {
        super(field4724, field4725, Combat.Movement);
        this.field4717 = new BooleanSetting(field4731, true);
        this.field4723 = new BooleanSetting(field4732, false, this.field4717::getValue);
        this.field4737 = new SliderSetting(field4733, field4734, 1.0f, field4735, field4736);
        this.addSettings(this.field4730, this.field4717, this.field4723, this.field4737);
    }

    static {
        и = 0.0;
        field4724 = "ElytraTarget";
        field4725 = "Топовый перегон на элитре";
        field4726 = "Приоритет цели";
        field4727 = "Обычно";
        field4728 = "Обычно";
        field4729 = "Сперва элитры";
        field4731 = "Перегон";
        field4732 = "BravoFFA";
        field4733 = "Сила предикта";
        field4734 = 3.0f;
        field4735 = 5.0f;
        field4736 = 0.1f;
        field4722 = 5.0;
        field4718 = 0.0064;
        field4719 = 0.5;
        field4720 = 1.0E-6;
        field4721 = -0.1;
    }
}




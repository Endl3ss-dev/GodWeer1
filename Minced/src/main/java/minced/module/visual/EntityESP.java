/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  org.joml.Vector2f
 *  org.joml.Vector4f
 */
package minced.module.visual;
import minced.core.EventHandler;

import java.util.function.BooleanSupplier;
import minced.core.ColorUtil;
import minced.core.MultiBooleanSetting;
import minced.core.BooleanSetting;
import minced.core.ProjectionUtil;
import minced.core.ModeSetting;
import minced.core.EventRender3D;
import minced.core.Render3D;
import minced.core.Render2DBatch;
import minced.core.ServerDetector;
import minced.core.EventRender2D;
import minced.core.ThemeColors;
import minced.core.EntityFilter;
import minced.core.Minced;
import minced.core.Module;
import minced.enums.Combat;
import minced.enums.MAIN;
import minced.module.misc.ScoreboardHealth;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class EntityESP
extends Module {
    MultiBooleanSetting field4072 = new MultiBooleanSetting(field4145, new BooleanSetting(field4146, true), new BooleanSetting(field4147, false), new BooleanSetting(field4148, false), new BooleanSetting(field4149, false), new BooleanSetting(field4150, false), new BooleanSetting(field4151, false), new BooleanSetting(field4152, false), new BooleanSetting(field4153, true));
    static String field4073;
    ModeSetting field4074 = new ModeSetting(field4154, field4155, field4156, field4157, field4158, field4159);
    static String field4075;
    static String field4076;
    static String field4077;
    ModeSetting cfr_renamed_4 = new ModeSetting(field4160, field4161, field4162, field4163, field4164);
    static String field4078;
    static String field4079;
    static String field4080;
    static MinecraftClient I = MinecraftClient.getInstance();
    static double field4081;
    static double field4082;
    static float field4083;
    static String field4084;
    static float field4085;
    static float field4086;
    static float field4087;
    static float field4088;
    static float field4089;
    static float field4090;
    static String field4091;
    static int field4092;
    static float field4093;
    static float field4094;
    static float field4095;
    static float field4096;
    static float field4097;
    static float field4098;
    static float field4099;
    static float field4100;
    static float field4101;
    static float field4102;
    static float field4103;
    static float field4104;
    static float field4105;
    static float field4106;
    static float field4107;
    static float field4108;
    static float field4109;
    static float field4110;
    static float field4111;
    static float field4112;
    static float field4113;
    static float field4114;
    static float field4115;
    static float field4116;
    static float field4117;
    static float field4118;
    static float field4119;
    static float field4120;
    static float field4121;
    static float field4122;
    static float field4123;
    static float field4124;
    static float field4125;
    static float field4126;
    static float field4127;
    static float field4128;
    static float field4129;
    static float field4130;
    static float field4131;
    static float field4132;
    static float field4133;
    static float field4134;
    static float field4135;
    static float field4136;
    static float field4137;
    static float field4138;
    static float field4139;
    static float field4140;
    static float field4141;
    static float field4142;
    static String field4143;
    static String field4144;
    static String field4145;
    static String field4146;
    static String field4147;
    static String field4148;
    static String field4149;
    static String field4150;
    static String field4151;
    static String field4152;
    static String field4153;
    static String field4154;
    static String field4155;
    static String field4156;
    static String field4157;
    static String field4158;
    static String field4159;
    static String field4160;
    static String field4161;
    static String field4162;
    static String field4163;
    static String field4164;
    static float field4165;
    static float field4166;
    static float field4167;
    static float field4168;
    static float field4169;
    static float field4170;
    static float field4171;
    static float field4172;
    static float field4173;
    static float field4174;
    static float field4175;
    static float field4176;
    static float field4177;
    static float field4178;
    static float field4179;
    static float field4180;
    static float field4181;
    static float field4182;
    static float field4183;
    static float field4184;
    static float field4185;
    static float field4186;
    static float field4187;
    static float field4188;
    static float field4189;
    static float field4190;
    static float field4191;
    static float field4192;
    static String field4193;
    static String field4194;
    static double field4195;
    static double field4196;
    static String field4197;
    static String field4198;
    static float field4199;
    static float field4200;
    static float field4201;
    static float field4202;

    private int method1055(Entity param1) {
        if (param1 instanceof PlayerEntity) {
            PlayerEntity v2 = (PlayerEntity)param1;
            boolean v3 = Minced.getInstance().getFriendManager().cfr_renamed_4(v2.getGameProfile().getName());
            if (v3 && this.field4072.method1898(field4073).booleanValue() && (this.field4074.isMode(field4075) || this.field4074.isMode(field4076) || this.field4074.isMode(field4077))) {
                return ColorUtil.method1858(0, 255, 0);
            }
        }
        return ThemeColors.method3199(MAIN.MODULE_VISUAL);
    }

    private void method1056(EventRender2D param1) {
        if (!(this.cfr_renamed_4.isMode(field4078) || this.cfr_renamed_4.isMode(field4079) || this.cfr_renamed_4.isMode(field4080))) {
            return;
        }
        if (EntityESP.I.world == null) {
            return;
        }
        for (Entity v3 : EntityESP.I.world.getEntities()) {
            int v24;
            int v23;
            int v26;
            int v25;
            LivingEntity v4;
            if (!(v3 instanceof LivingEntity) || !(v4 = (LivingEntity)v3).isAlive() || !ProjectionUtil.method2003((Entity)v4) || this.method1058((Entity)v4)) continue;
            Vec3d v5 = v4.getLerpedPos(param1.method2996());
            Box v6 = ProjectionUtil.method2004((Entity)v4, v5);
            double v7 = field4081 - (v4.isSneaking() && !EntityESP.I.player.getAbilities().flying ? field4082 : 0.0);
            Box v9 = new Box(v6.minX, v6.minY, v6.minZ, v6.maxX, v6.maxY - v7, v6.maxZ);
            Vector4f v10 = this.method1063(v9);
            float v11 = v10.x;
            float v12 = v10.y;
            float v13 = v10.w;
            float v14 = field4083;
            float v15 = v13 - v12;
            float v16 = v11 - 2.0f - v14;
            boolean v17 = Minced.getInstance().getModuleManager().method187(ScoreboardHealth.class).isEnabled();
            float v18 = Math.max(0.0f, v17 ? (float)ServerDetector.method2681(v4) : v4.getHealth());
            float v19 = Math.max(1.0f, v4.getMaxHealth());
            float v20 = Math.min(v18 / v19, 1.0f);
            float v21 = v15 * v20;
            float v22 = v12 + (v15 - v21);
            if (this.cfr_renamed_4.isMode(field4084)) {
                v25 = ThemeColors.method3199(MAIN.MODULE_VISUAL);
                v26 = ColorUtil.method1851(v25, field4085);
                v23 = v25;
                v24 = v26;
            } else {
                int v27;
                v25 = ColorUtil.method1858(255, 0, 0);
                v26 = ColorUtil.method1858(0, 255, 0);
                v23 = v27 = ColorUtil.method1850(v25, v26, v20);
                v24 = ColorUtil.method1851(v27, field4086);
            }
            Render2DBatch.method2672(v16 - field4087, v12 - field4088, v14 + 1.0f, v15 + field4089, ColorUtil.method1852(0, 0, 0, 255));
            Render2DBatch.method2655(v16, v22, v14, v21 + field4090, v23, v24);
        }
    }

    @EventHandler(2)
    public void method1057(EventRender3D param1) {
        if (!this.field4074.isMode(field4091)) {
            return;
        }
        if (EntityESP.I.world == null || EntityESP.I.player == null) {
            return;
        }
        MatrixStack v2 = param1.method2348();
        Vec3d v3 = EntityESP.I.gameRenderer.getCamera().getPos();
        v2.push();
        v2.translate(-v3.x, -v3.y, -v3.z);
        float v4 = param1.O();
        for (Entity v6 : EntityESP.I.world.getEntities()) {
            if (!v6.isAlive() || !ProjectionUtil.method2003(v6) || this.method1058(v6)) continue;
            double v7 = MathHelper.lerp((double)v4, (double)v6.prevX, (double)v6.getX());
            double v9 = MathHelper.lerp((double)v4, (double)v6.prevY, (double)v6.getY());
            double v11 = MathHelper.lerp((double)v4, (double)v6.prevZ, (double)v6.getZ());
            Box v13 = v6.getBoundingBox().offset(v7 - v6.getX(), v9 - v6.getY(), v11 - v6.getZ());
            int v14 = this.method1055(v6);
            int v15 = v14 >> 16 & 0xFF;
            int v16 = v14 >> 8 & 0xFF;
            int v17 = v14 & 0xFF;
            int v18 = field4092 | v15 << 16 | v16 << 8 | v17;
            Render3D.method2410(v2, v13, v18, field4093, true, true);
            Render3D.method2410(v2, v13, v14, field4094, true, true);
        }
        v2.pop();
    }

    private boolean method1058(Entity param1) {
        return !EntityFilter.method3438(param1, this.field4072);
    }

    @EventHandler(2)
    public void method1059(EventRender2D param1) {
        Render2DBatch.method2668(param1.method2997());
        this.method1056(param1);
        this.method1062(param1);
        Render2DBatch.method2651();
    }

    private void method1060(DrawContext param1, float param2, float param3, float param4, float param5, int param6) {
        float v7 = Math.min((param4 - param2) * field4095, (param5 - param3) * field4096);
        float v8 = field4097;
        float v9 = field4098;
        int v10 = ColorUtil.method1857(5, 0, param6, ColorUtil.method1851(param6, field4099));
        int v11 = ColorUtil.method1857(5, 90, param6, ColorUtil.method1851(param6, field4100));
        int v12 = ColorUtil.method1857(5, 180, param6, ColorUtil.method1851(param6, field4101));
        int v13 = ColorUtil.method1857(5, 270, param6, ColorUtil.method1851(param6, field4102));
        Render2DBatch.method2655(param2, param3, v7, v8, v12, v11);
        Render2DBatch.method2655(param2, param3, v8, v7, v11, v10);
        Render2DBatch.method2655(param4 - v7 + field4103, param3, v7 - field4104, v8, v12, v11);
        Render2DBatch.method2655(param4, param3, v8, v7, v12, v13);
        Render2DBatch.method2655(param2, param5, v7, v8, v13, v10);
        Render2DBatch.method2655(param2, param5 - v7 + field4105, v8, v7 - field4106, v11, v10);
        Render2DBatch.method2655(param4 - v7 + v8, param5, v7, v8, v13, v10);
        Render2DBatch.method2655(param4, param5 - v7 + field4107, v8, v7 - field4108, v12, v13);
        Render2DBatch.method2672(param2 - field4109, param3 - v9, v7 + field4110, v9, ColorUtil.method1858(0, 0, 0));
        Render2DBatch.method2672(param2 - v9, param3, v9, v7, ColorUtil.method1858(0, 0, 0));
        Render2DBatch.method2672(param2 + field4111, param3 + field4112, v7 - field4113, v9, ColorUtil.method1858(0, 0, 0));
        Render2DBatch.method2672(param2 + field4114, param3 + field4115, v9, v7 - field4116, ColorUtil.method1858(0, 0, 0));
        Render2DBatch.method2672(param4 - v9, param3 + field4117, v9, v7 - field4118, ColorUtil.method1858(0, 0, 0));
        Render2DBatch.method2672(param4 + field4119, param3, v9, v7, ColorUtil.method1858(0, 0, 0));
        Render2DBatch.method2672(param4 - v7 + field4120, param3 - v9, v7 + field4121, v9, ColorUtil.method1858(0, 0, 0));
        Render2DBatch.method2672(param4 - v7 + field4122, param3 + field4123, v7 - field4124, v9, ColorUtil.method1858(0, 0, 0));
        Render2DBatch.method2672(param2 - v9, param5 - v7 + field4125, v9, v7, ColorUtil.method1858(0, 0, 0));
        Render2DBatch.method2672(param2 + field4126, param5 - v7 + field4127, v9, v7 - field4128, ColorUtil.method1858(0, 0, 0));
        Render2DBatch.method2672(param2 + field4129, param5 - v9, v7 - field4130, v9, ColorUtil.method1858(0, 0, 0));
        Render2DBatch.method2672(param2 - field4131, param5 + field4132, v7 + field4133, v9, ColorUtil.method1858(0, 0, 0));
        Render2DBatch.method2672(param4 - v9, param5 - v7 + field4134, v9, v7 - field4135, ColorUtil.method1858(0, 0, 0));
        Render2DBatch.method2672(param4 + field4136, param5 - v7 + field4137, v9, v7, ColorUtil.method1858(0, 0, 0));
        Render2DBatch.method2672(param4 - v7 + field4138, param5 - v9, v7 - field4139, v9, ColorUtil.method1858(0, 0, 0));
        Render2DBatch.method2672(param4 - v7 + field4140, param5 + field4141, v7 + field4142, v9, ColorUtil.method1858(0, 0, 0));
    }

    public EntityESP() {
        super(field4143, field4144, Combat.Visuals);
        this.addSettings(this.field4072, this.field4074, this.cfr_renamed_4);
    }

    private void method1061(DrawContext param1, float param2, float param3, float param4, float param5, int param6) {
        float v7 = field4165;
        int v8 = ColorUtil.method1857(5, 0, param6, ColorUtil.method1851(param6, field4166));
        int v9 = ColorUtil.method1857(5, 90, param6, ColorUtil.method1851(param6, field4167));
        int v10 = ColorUtil.method1857(5, 180, param6, ColorUtil.method1851(param6, field4168));
        int v11 = ColorUtil.method1857(5, 270, param6, ColorUtil.method1851(param6, field4169));
        Render2DBatch.method2655(param2, param3, field4170, param5 - param3, v9, v8);
        Render2DBatch.method2655(param2, param5, param4 - param2, field4171, v11, v8);
        Render2DBatch.method2655(param4, param3, field4172, param5 - param3 + field4173, v10, v11);
        Render2DBatch.method2655(param2, param3, param4 - param2, field4174, v10, v9);
        Render2DBatch.method2672(param2 - field4175, param3 - v7, param4 - param2 + field4176, v7, ColorUtil.method1858(0, 0, 0));
        Render2DBatch.method2672(param2 - v7, param3, v7, param5 - param3 + field4177, ColorUtil.method1858(0, 0, 0));
        Render2DBatch.method2672(param2 - field4178, param5 + field4179, param4 - param2 + field4180, v7, ColorUtil.method1858(0, 0, 0));
        Render2DBatch.method2672(param4 + field4181, param3, v7, param5 - param3 + field4182, ColorUtil.method1858(0, 0, 0));
        Render2DBatch.method2672(param2 + field4183, param3 + field4184, param4 - param2 - field4185, v7, ColorUtil.method1858(0, 0, 0));
        Render2DBatch.method2672(param2 + field4186, param3 + field4187, v7, param5 - param3 - field4188, ColorUtil.method1858(0, 0, 0));
        Render2DBatch.method2672(param2 + field4189, param5 - v7, param4 - param2 - field4190, v7, ColorUtil.method1858(0, 0, 0));
        Render2DBatch.method2672(param4 - v7, param3 + field4191, v7, param5 - param3 - field4192, ColorUtil.method1858(0, 0, 0));
    }

    private void method1062(EventRender2D param1) {
        if (!this.field4074.isMode(field4193) && !this.field4074.isMode(field4194)) {
            return;
        }
        if (EntityESP.I.world == null) {
            return;
        }
        for (Entity v3 : EntityESP.I.world.getEntities()) {
            if (!v3.isAlive() || !ProjectionUtil.method2003(v3) || this.method1058(v3)) continue;
            Vec3d v4 = v3.getLerpedPos(param1.method2996());
            Box v5 = ProjectionUtil.method2004(v3, v4);
            double v6 = field4195 - (v3.isSneaking() && !EntityESP.I.player.getAbilities().flying ? field4196 : 0.0);
            Box v8 = new Box(v5.minX, v5.minY, v5.minZ, v5.maxX, v5.maxY - v6, v5.maxZ);
            Vector4f v9 = this.method1063(v8);
            float v10 = v9.x;
            float v11 = v9.y;
            float v12 = v9.z;
            float v13 = v9.w;
            if (v12 <= v10 || v13 <= v11) continue;
            int v14 = this.method1055(v3);
            if (this.field4074.isMode(field4197)) {
                this.method1061(param1.method2997(), v10, v11, v12, v13, v14);
                continue;
            }
            if (!this.field4074.isMode(field4198)) continue;
            this.method1060(param1.method2997(), v10, v11, v12, v13, v14);
        }
    }

    private Vector4f method1063(Box param1) {
        Vec3d[] v2 = ProjectionUtil.method2000(param1);
        Vector2f v3 = new Vector2f(field4199, field4200);
        Vector2f v4 = new Vector2f(field4201, field4202);
        for (Vec3d v8 : v2) {
            Vector2f v9 = ProjectionUtil.method2005(v8);
            if (v9.x < v3.x) {
                v3.x = v9.x;
            }
            if (v9.y < v3.y) {
                v3.y = v9.y;
            }
            if (v9.x > v4.x) {
                v4.x = v9.x;
            }
            if (!(v9.y > v4.y)) continue;
            v4.y = v9.y;
        }
        return new Vector4f(v3.x, v3.y, v4.x, v4.y);
    }

    static {
        field4143 = "Entity ESP";
        field4144 = "Подсвечивает сущностей через стены, упрощая их обнаружение";
        field4145 = "Отображать";
        field4146 = "Игроков";
        field4147 = "Монстров";
        field4148 = "Друзей";
        field4149 = "Животных";
        field4150 = "Себя";
        field4151 = "Жителей";
        field4152 = "Голых";
        field4153 = "Предметы";
        field4154 = "Режим";
        field4155 = "Отключено";
        field4156 = "Квадрат";
        field4157 = "Углы";
        field4158 = "Бокс";
        field4159 = "Отключено";
        field4160 = "Бар здоровья";
        field4161 = "Отключен";
        field4162 = "Состояние здоровья";
        field4163 = "Клиентский";
        field4164 = "Отключен";
        field4193 = "Квадрат";
        field4194 = "Углы";
        field4195 = 0.2;
        field4196 = 0.1;
        field4197 = "Квадрат";
        field4198 = "Углы";
        field4073 = "Друзей";
        field4075 = "Квадрат";
        field4076 = "Углы";
        field4077 = "Бокс";
        field4165 = 0.5f;
        field4166 = 0.5f;
        field4167 = 0.5f;
        field4168 = 0.5f;
        field4169 = 0.5f;
        field4170 = 0.5f;
        field4171 = 0.5f;
        field4172 = 0.5f;
        field4173 = 0.5f;
        field4174 = 0.5f;
        field4175 = 0.5f;
        field4176 = 1.5f;
        field4177 = 0.5f;
        field4178 = 0.5f;
        field4179 = 0.5f;
        field4180 = 1.5f;
        field4181 = 0.5f;
        field4182 = 0.5f;
        field4183 = 0.5f;
        field4184 = 0.5f;
        field4185 = 0.5f;
        field4186 = 0.5f;
        field4187 = 0.5f;
        field4188 = 0.5f;
        field4189 = 0.5f;
        field4190 = 0.5f;
        field4191 = 0.5f;
        field4192 = 0.5f;
        field4095 = 0.25f;
        field4096 = 0.25f;
        field4097 = 0.5f;
        field4098 = 0.5f;
        field4099 = 0.5f;
        field4100 = 0.5f;
        field4101 = 0.5f;
        field4102 = 0.5f;
        field4103 = 0.5f;
        field4104 = 0.5f;
        field4105 = 0.5f;
        field4106 = 0.5f;
        field4107 = 0.5f;
        field4108 = 0.5f;
        field4109 = 0.5f;
        field4110 = 0.5f;
        field4111 = 0.5f;
        field4112 = 0.5f;
        field4113 = 0.5f;
        field4114 = 0.5f;
        field4115 = 0.5f;
        field4116 = 0.5f;
        field4117 = 0.5f;
        field4118 = 0.5f;
        field4119 = 0.5f;
        field4120 = 0.5f;
        field4121 = 0.5f;
        field4122 = 0.5f;
        field4123 = 0.5f;
        field4124 = 0.5f;
        field4125 = 0.5f;
        field4126 = 0.5f;
        field4127 = 0.5f;
        field4128 = 0.5f;
        field4129 = 0.5f;
        field4130 = 0.5f;
        field4131 = 0.5f;
        field4132 = 0.5f;
        field4133 = 0.5f;
        field4134 = 0.5f;
        field4135 = 0.5f;
        field4136 = 0.5f;
        field4137 = 0.5f;
        field4138 = 0.5f;
        field4139 = 0.5f;
        field4140 = 0.5f;
        field4141 = 0.5f;
        field4142 = 0.5f;
        field4078 = "Состояние здоровья";
        field4079 = "Индивидуальный";
        field4080 = "Клиентский";
        field4081 = 0.2;
        field4082 = 0.1;
        field4083 = 0.5f;
        field4084 = "Клиентский";
        field4085 = 0.6f;
        field4086 = 0.6f;
        field4087 = 0.5f;
        field4088 = 0.5f;
        field4089 = 1.5f;
        field4090 = 0.5f;
        field4091 = "Бокс";
        field4092 = -1308622848;
        field4093 = 1.5f;
        field4094 = 1.5f;
        field4199 = Float.MAX_VALUE;
        field4200 = Float.MAX_VALUE;
        field4201 = Float.MIN_VALUE;
        field4202 = Float.MIN_VALUE;
    }
}




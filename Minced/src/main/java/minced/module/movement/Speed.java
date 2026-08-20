/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.render.RenderTickCounter
 *  net.minecraft.client.render.RenderTickCounter$Dynamic
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.component.type.NbtComponent
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EquipmentSlot
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.effect.StatusEffectInstance
 *  net.minecraft.entity.effect.StatusEffects
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket
 *  net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket$Mode
 *  net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Vec3d
 *  sg.ec.t0
 */
package minced.module.movement;
import minced.core.EventHandler;

import java.util.function.BooleanSupplier;
import minced.core.BooleanSetting;
import minced.core.SliderSetting;
import minced.core.EventInput;
import minced.core.EventStrafe;
import minced.core.ModeSetting;
import minced.core.EventVelocity;
import minced.core.EventUpdate;
import minced.core.EventPacket;
import minced.core.EventPostTick;
import minced.core.ClientState;
import minced.core.PacketQueue;
import minced.core.TimerUtil;
import minced.core.VelocityUtil;
import minced.core.RenderTickAccess;
import minced.core.Minced;
import minced.core.Module;
import minced.enums.Combat;
import minced.module.combat.AttackAura;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import sg.ec.t0;

public class Speed
extends Module {
    ClientState field5063;
    boolean field5064;
    boolean field5065;
    ModeSetting field5066 = new ModeSetting(field5178, field5179, field5180, field5181, field5182, field5183, field5184, field5185, field5186);
    static String field5067;
    int field5068;
    int field5069;
    boolean field5070;
    int field5071;
    int field5072;
    boolean field5073;
    boolean field5074;
    static String field5075;
    static String field5076;
    static String field5077;
    static String field5078;
    BooleanSetting field5079;
    static String field5080;
    static String field5081;
    static String field5082;
    ModeSetting field5083;
    static String field5084;
    static MinecraftClient I = MinecraftClient.getInstance();
    static String field5085;
    BooleanSetting field5086;
    VelocityUtil field5087;
    static String field5088;
    static String field5089;
    static String field5090;
    static String field5091;
    static String field5092;
    static String field5093;
    static String field5094;
    static double field5095;
    static float field5096;
    static double field5097;
    static double field5098;
    static double field5099;
    static double field5100;
    static double field5101;
    static String field5102;
    SliderSetting field5103 = new SliderSetting(field5187, field5188, field5189, field5190, field5191, () -> this.field5066.isMode(field5119) || this.field5066.isMode(field5120));
    int field5104;
    SliderSetting field5105;
    static String field5106;
    static String field5107;
    static String field5108;
    static String field5109;
    static String field5110;
    static String field5111;
    static String field5112;
    static String field5113;
    static String field5114;
    static String field5115;
    static String field5116;
    static String field5117;
    static String field5118;
    static String field5119;
    static String field5120;
    static String field5121;
    static String field5122;
    static String field5123;
    static String field5124;
    static String field5125;
    static float field5126;
    static float field5127;
    static float field5128;
    static float field5129;
    static float field5130;
    static float field5131;
    static float field5132;
    static float field5133;
    static String field5134;
    SliderSetting field5135;
    static float field5136;
    static float field5137;
    SliderSetting field5138;
    SliderSetting field5139;
    static float field5140;
    static float field5141;
    static String field5142;
    static String field5143;
    static String field5144;
    SliderSetting field5145 = new SliderSetting(field5192, field5193, field5194, 1.0f, field5195, () -> this.field5066.isMode(field5080) || this.field5066.isMode(field5081));
    static double field5146;
    static long field5147;
    static long field5148;
    static float field5149;
    static float field5150;
    SliderSetting field5151;
    static double field5152;
    SliderSetting field5153 = new SliderSetting(field5196, field5197, field5198, 1.0f, field5199, () -> this.field5066.isMode(field5121));
    static double field5154;
    static String field5155;
    static float field5156;
    static float field5157;
    static float field5158;
    static String field5159;
    BooleanSetting field5160;
    static float field5161;
    static String field5162;
    static float field5163;
    static String field5164;
    static String field5165;
    static String field5166;
    static String field5167;
    static String field5168;
    static String field5169;
    static float field5170;
    static double field5171;
    static double field5172;
    static double field5173;
    static double field5174;
    static String field5175;
    static String field5176;
    static String field5177;
    static String field5178;
    static String field5179 = "Grim New";
    static String field5180 = "Grim New";
    static String field5181;
    static String field5182;
    static String field5183 = "HolyWorld";
    static String field5184;
    static String field5185;
    static String field5186 = "Reallyworld";
    static String field5187;
    static float field5188;
    static float field5189;
    static float field5190;
    static float field5191;
    static String field5192;
    static float field5193;
    static float field5194;
    static float field5195;
    static String field5196;
    static float field5197;
    static float field5198;
    static float field5199;
    static String field5200;
    static float field5201;
    static float field5202;
    static float field5203;
    static float field5204;
    static String field5205;
    static String field5206;
    static String field5207;
    static String field5208;
    static String field5209;
    static float field5210;
    static float field5211;
    static float field5212;
    static float field5213;
    static String field5214;
    static String field5215;
    static float field5216;
    static float field5217;
    static float field5218;
    static float field5219;
    static String field5220;
    static float field5221;
    static float field5222;
    static float field5223;
    static float field5224;
    static String field5225;
    static String field5226;
    static String field5227;
    static float field5228;
    static float field5229;
    static float field5230;

    @Override
    public void onEnable() {
        this.field5063.method3114();
        this.field5064 = false;
        this.field5065 = false;
        if (this.field5066.isMode(field5067)) {
            this.method1227(1.0f);
        }
        this.field5068 = 0;
        this.field5069 = 0;
        this.field5070 = false;
        this.field5071 = 0;
        this.field5072 = 0;
        this.field5073 = false;
        this.field5074 = false;
        super.onEnable();
    }

    private void method1227(float param1) {
        RenderTickCounter v3 = I.getRenderTickCounter();
        if (v3 instanceof RenderTickCounter.Dynamic) {
            RenderTickCounter.Dynamic v2 = (RenderTickCounter.Dynamic)v3;
            ((RenderTickAccess)v2).setSpeedMultiplier(param1);
        }
    }

    @EventHandler(2)
    public void method1228(EventVelocity param1) {
        if (this.field5066.isMode(field5085) && ((Boolean)this.field5086.getValue()).booleanValue()) {
            this.field5087.method3575(param1);
        }
    }

    @EventHandler(2)
    public void cfr_underscore(EventInput param1) {
        if (this.field5066.isMode(field5088) && Speed.I.player != null) {
            this.field5072 = Speed.I.player.verticalCollision ? ++this.field5072 : 0;
            if (this.field5072 >= 1) {
                Speed.I.player.jump();
            }
            return;
        }
        if (!this.field5066.isMode(field5089) || Speed.I.player == null) {
            return;
        }
        this.field5069 = Speed.I.player.verticalCollision ? ++this.field5069 : 0;
        if (this.field5069 >= 1) {
            Speed.I.player.jump();
        }
    }

    @Override
    public void onDisable() {
        if (this.field5065 || this.field5066.isMode(field5090)) {
            this.method1227(1.0f);
        }
        this.field5064 = false;
        this.field5065 = false;
        if (this.field5070 || this.field5066.isMode(field5091)) {
            this.method1236(1.0f);
        }
        this.field5068 = 0;
        this.field5069 = 0;
        this.field5070 = false;
        if (this.field5073 || this.field5066.isMode(field5092)) {
            this.method1236(1.0f);
        }
        this.method1233(false);
        this.field5071 = 0;
        this.field5072 = 0;
        this.field5073 = false;
        super.onDisable();
    }

    @EventHandler(2)
    public void method1229(EventStrafe param1) {
        if (this.field5066.isMode(field5093)) {
            this.method1252();
            return;
        }
        if (!this.field5066.isMode(field5094) || Speed.I.player == null) {
            return;
        }
        if (this.field5068 > 3) {
            double v2 = field5095;
            if (this.field5068 % 2 == 0) {
                this.method1236(field5096);
                this.field5070 = true;
                Speed.I.player.addVelocityInternal(new Vec3d(0.0, field5097, 0.0));
                if (Speed.I.player.isOnGround()) {
                    v2 = field5098;
                }
            }
            double v4 = Math.toRadians(TimerUtil.method3545(false));
            double v6 = -Math.sin(v4);
            double v8 = Math.cos(v4);
            Speed.I.player.addVelocityInternal(new Vec3d(v6 * v2, 0.0, v8 * v2));
        }
        ++this.field5068;
    }

    private void method1230() {
        if (Speed.I.player.isOnGround()) {
            Speed.I.player.jump();
        }
        if (Speed.I.player.getVelocity().y == field5099) {
            Speed.I.player.setVelocity(Speed.I.player.getVelocity().x * field5100, Speed.I.player.getVelocity().y, Speed.I.player.getVelocity().z * field5101);
        }
    }

    private void method1232() {
        Vec3d v3;
        LivingEntity v2;
        AttackAura v1 = (AttackAura)Minced.getInstance().getModuleManager().method187(AttackAura.class);
        LivingEntity livingEntity = v2 = v1 != null && v1.isEnabled() ? AttackAura.method861() : null;
        if (Speed.I.player.hurtTime > 0) {
            return;
        }
        if (Speed.I.player.isOnGround()) {
            return;
        }
        if (v2 != null && v2 != Speed.I.player && (v3 = t0.cfr_renamed_214((Entity)v2)).length() <= (double)((Float)this.field5103.getValue()).floatValue()) {
            this.method1243(v3);
            return;
        }
        v3 = Speed.I.player.getRotationVector();
        this.method1243(new Vec3d(v3.x, 0.0, v3.z));
    }

    private void method1233(boolean param1) {
        if (param1 && !this.field5074) {
            this.field5104 = (Integer)Speed.I.options.getMaxFps().getValue();
            Speed.I.options.getMaxFps().setValue((Integer)((int)((Float)this.field5105.getValue()).floatValue()));
            this.field5074 = true;
        } else if (param1 && this.field5074) {
            int v2 = (int)((Float)this.field5105.getValue()).floatValue();
            if ((Integer)Speed.I.options.getMaxFps().getValue() != v2) {
                Speed.I.options.getMaxFps().setValue((Integer)v2);
            }
        } else if (!param1 && this.field5074) {
            Speed.I.options.getMaxFps().setValue((Integer)this.field5104);
            this.field5074 = false;
        }
    }

    private boolean method1234(ItemStack param1) {
        if (param1.getItem() != Items.PLAYER_HEAD || !param1.contains(DataComponentTypes.CUSTOM_DATA)) {
            return false;
        }
        String v2 = ((NbtComponent)param1.get(DataComponentTypes.CUSTOM_DATA)).copyNbt().toString();
        if (!v2.contains(field5106)) {
            return false;
        }
        return v2.split(field5107, 2)[1].startsWith(field5108);
    }

    @EventHandler(2)
    public void method1235(EventUpdate param1) {
        if (!this.field5066.isMode(field5109) && this.field5065) {
            this.method1227(1.0f);
            this.field5065 = false;
        }
        if (!this.field5066.isMode(field5110) && this.field5070) {
            this.method1236(1.0f);
            this.field5070 = false;
            this.field5068 = 0;
            this.field5069 = 0;
        }
        boolean v2 = this.field5066.isMode(field5111) && Speed.I.player != null && Speed.I.world != null;
        this.method1233(v2 && (Boolean)this.field5079.getValue() != false);
        if (!this.field5066.isMode(field5112) && this.field5073) {
            this.method1236(1.0f);
            this.field5073 = false;
            this.field5071 = 0;
            this.field5072 = 0;
        }
        String v3 = (String)this.field5066.getValue();
        int v4 = -1;
        switch (v3.hashCode()) {
            case -1997372447: {
                if (!v3.equals(field5113)) break;
                v4 = 0;
                break;
            }
            case -403347694: {
                if (!v3.equals(field5114)) break;
                v4 = 1;
                break;
            }
            case 381123424: {
                if (!v3.equals(field5115)) break;
                v4 = 2;
                break;
            }
            case -495240450: {
                if (!v3.equals(field5116)) break;
                v4 = 3;
                break;
            }
            case -1675025259: {
                if (!v3.equals(field5117)) break;
                v4 = 4;
                break;
            }
            case -472241956: {
                if (!v3.equals(field5118)) break;
                v4 = 5;
            }
        }
        switch (v4) {
            case 0: {
                this.method1230();
                break;
            }
            case 1: {
                this.method1240();
                break;
            }
            case 2: {
                this.method1232();
                break;
            }
            case 3: {
                this.method1246();
                break;
            }
            case 4: {
                this.method1241();
                break;
            }
            case 5: {
                this.method1245();
            }
        }
    }

    private void method1236(float param1) {
        RenderTickCounter v3 = I.getRenderTickCounter();
        if (v3 instanceof RenderTickCounter.Dynamic) {
            RenderTickCounter.Dynamic v2 = (RenderTickCounter.Dynamic)v3;
            ((RenderTickAccess)v2).setSpeedMultiplier(param1);
        }
    }

    @EventHandler(2)
    public void method1239(EventPacket param1) {
        if (this.field5066.isMode(field5122) && ((Boolean)this.field5086.getValue()).booleanValue() && param1.method2466()) {
            this.field5087.method3571(param1);
        }
        if (this.field5066.isMode(field5123) && param1.method2466() && param1.method2467() instanceof PlayerPositionLookS2CPacket) {
            if (this.field5068 % 2 == 1) {
                ++this.field5068;
            }
            this.method1236(1.0f);
            this.field5070 = true;
        }
        if (this.field5066.isMode(field5124) && param1.method2466() && param1.method2467() instanceof PlayerPositionLookS2CPacket) {
            if (this.field5071 % 2 == 1) {
                ++this.field5071;
            }
            this.method1236(1.0f);
            this.field5073 = true;
        }
    }

    private void method1240() {
        AttackAura v1 = (AttackAura)Minced.getInstance().getModuleManager().method187(AttackAura.class);
        if (v1 == null || !v1.isEnabled()) {
            return;
        }
        LivingEntity v2 = AttackAura.method861();
        if (v2 == null || v2 == Speed.I.player) {
            return;
        }
        if (Speed.I.player.hurtTime > 0) {
            return;
        }
        if (Speed.I.player.isOnGround()) {
            return;
        }
        Vec3d v3 = t0.cfr_renamed_214((Entity)v2);
        if (v3.length() > (double)((Float)this.field5103.getValue()).floatValue()) {
            return;
        }
        this.method1243(v3);
    }

    private void method1241() {
        if (Speed.I.player == null) {
            return;
        }
        if (Speed.I.player.isGliding()) {
            return;
        }
        ItemStack v1 = Speed.I.player.getOffHandStack();
        StatusEffectInstance v2 = Speed.I.player.getStatusEffect(StatusEffects.SPEED);
        StatusEffectInstance v3 = Speed.I.player.getStatusEffect(StatusEffects.SLOWNESS);
        String v4 = v1.getName().getString();
        float v5 = 0.0f;
        if (this.field5083.isMode(field5125)) {
            int v6;
            v5 = v2 != null ? ((v6 = v2.getAmplifier()) == 2 ? (this.method1251(v4) ? field5126 : field5127) : (v6 == 1 ? (this.method1251(v4) ? field5128 : field5129) : (this.method1251(v4) ? field5130 : field5131))) : (this.method1251(v4) ? field5132 : field5133);
        } else if (this.field5083.isMode(field5134)) {
            v5 = ((Float)this.field5135.getValue()).floatValue();
        }
        if (v3 != null) {
            v5 *= field5136;
        }
        if (!Speed.I.player.isOnGround()) {
            v5 *= field5137;
        }
        if (((Boolean)this.field5086.getValue()).booleanValue()) {
            this.field5087.method3583(((Float)this.field5138.getValue()).longValue());
            if (this.field5087.method3574()) {
                v5 += ((Float)this.field5139.getValue()).floatValue() / field5140;
            }
        }
        if (this.method1244()) {
            v5 /= field5141;
        }
        TimerUtil.method3546(v5);
    }

    private boolean method1242(ItemStack param1) {
        if (param1.getItem() != Items.PLAYER_HEAD || !param1.contains(DataComponentTypes.CUSTOM_DATA)) {
            return false;
        }
        String v2 = ((NbtComponent)param1.get(DataComponentTypes.CUSTOM_DATA)).copyNbt().toString();
        if (!v2.contains(field5142)) {
            return false;
        }
        return v2.split(field5143, 2)[1].startsWith(field5144);
    }

    private void method1243(Vec3d param1) {
        double v2 = param1.x;
        double v4 = param1.z;
        double v6 = Math.sqrt(v2 * v2 + v4 * v4);
        if (v6 <= 0.0) {
            return;
        }
        double v8 = (double)((Float)this.field5145.getValue()).floatValue() * field5146;
        double v10 = v8 / v6;
        Speed.I.player.addVelocity(v2 * v10, 0.0, v4 * v10);
    }

    private boolean method1244() {
        ItemStack v1 = Speed.I.player.getEquippedStack(EquipmentSlot.HEAD);
        return this.method1234(v1) || this.method1242(v1);
    }

    private void method1245() {
        if (Speed.I.player == null || Speed.I.world == null) {
            return;
        }
        if (this.field5063.method3113(field5147)) {
            this.field5064 = true;
        }
        if (this.field5063.method3113(field5148)) {
            this.field5064 = false;
            this.field5063.method3114();
        }
        this.field5065 = true;
        if (this.field5064) {
            if (Speed.I.player.isOnGround() && !Speed.I.options.jumpKey.isPressed()) {
                Speed.I.player.jump();
            }
            this.method1227(Speed.I.player.age % 2 == 0 ? field5149 : field5150);
        } else {
            this.method1227(1.0f);
        }
    }

    private void method1246() {
        if (Speed.I.player == null || Speed.I.world == null) {
            return;
        }
        if (Speed.I.player.getAbilities().flying) {
            return;
        }
        if (Speed.I.player.isOnGround()) {
            return;
        }
        float v1 = ((Float)this.field5151.getValue()).floatValue();
        Box v2 = Speed.I.player.getBoundingBox().expand((double)v1, field5152, (double)v1);
        int v3 = 0;
        for (PlayerEntity v5 : Speed.I.world.getPlayers()) {
            if (v5 == Speed.I.player || !v5.isAlive() || !v2.intersects(v5.getBoundingBox())) continue;
            ++v3;
        }
        if (v3 <= 0) {
            return;
        }
        double v4 = Math.max(0.0, (double)((Float)this.field5153.getValue()).floatValue() / field5154);
        double[] v6 = Speed.method1248(v4);
        Speed.I.player.addVelocity(v6[0], 0.0, v6[1]);
    }

    private static double[] method1248(double param0) {
        float v2 = Speed.I.player.input.movementForward;
        float v3 = Speed.I.player.input.movementSideways;
        float v4 = Speed.I.player.getYaw();
        if (v2 != 0.0f) {
            if (v3 > 0.0f) {
                v4 += (float)(v2 > 0.0f ? -45 : 45);
            } else if (v3 < 0.0f) {
                v4 += (float)(v2 > 0.0f ? 45 : -45);
            }
            v3 = 0.0f;
            if (v2 > 0.0f) {
                v2 = 1.0f;
            } else if (v2 < 0.0f) {
                v2 = field5156;
            }
        }
        double v5 = Math.sin(Math.toRadians(v4 + field5157));
        double v7 = Math.cos(Math.toRadians(v4 + field5158));
        double v9 = (double)v2 * param0 * v7 + (double)v3 * param0 * v5;
        double v11 = (double)v2 * param0 * v5 - (double)v3 * param0 * v7;
        return new double[]{v9, v11};
    }

    @EventHandler(2)
    public void method1249(EventPostTick param1) {
        if (this.field5066.isMode(field5159) && Speed.I.player != null && Speed.I.player.networkHandler != null) {
            if (((Boolean)this.field5160.getValue()).booleanValue() && this.field5071 % 2 == 0) {
                this.method1236(field5161);
                this.field5073 = true;
                PacketQueue.method3505((Packet)new ClientCommandC2SPacket((Entity)Speed.I.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
                PacketQueue.method3505((Packet)new ClientCommandC2SPacket((Entity)Speed.I.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
            }
            return;
        }
        if (!this.field5066.isMode(field5162) || Speed.I.player == null) {
            return;
        }
        if (this.field5068 % 2 == 0) {
            this.method1236(field5163);
            this.field5070 = true;
            PacketQueue.method3505((Packet)new ClientCommandC2SPacket((Entity)Speed.I.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
            PacketQueue.method3505((Packet)new ClientCommandC2SPacket((Entity)Speed.I.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
        }
    }

    private boolean method1251(String param1) {
        return param1.contains(field5165) || param1.contains(field5166) || param1.contains(field5167) || param1.contains(field5168) || param1.contains(field5169);
    }

    private void method1252() {
        if (Speed.I.player == null || Speed.I.world == null) {
            return;
        }
        if (((Boolean)this.field5160.getValue()).booleanValue()) {
            this.method1236(field5170);
            this.field5073 = true;
        }
        if (this.field5071 > 3) {
            double v1 = field5171;
            if (this.field5071 % 2 == 0) {
                Speed.I.player.addVelocityInternal(new Vec3d(0.0, field5172, 0.0));
                v1 = Speed.I.player.isOnGround() ? field5173 : field5174;
            }
            double v3 = Math.toRadians(TimerUtil.method3545(false));
            double v5 = -Math.sin(v3);
            double v7 = Math.cos(v3);
            Speed.I.player.addVelocityInternal(new Vec3d(v5 * v1, 0.0, v7 * v1));
        }
        ++this.field5071;
    }

    public Speed() {
        super(field5176, field5177, Combat.Movement);
        this.field5151 = new SliderSetting(field5200, field5201, field5202, field5203, field5204, () -> this.field5066.isMode(field5077));
        this.field5083 = new ModeSetting(field5205, field5206, () -> this.field5066.isMode(field5175), field5207, field5208);
        this.field5135 = new SliderSetting(field5209, field5210, field5211, field5212, field5213, () -> this.field5066.isMode(field5082) && this.field5083.isMode(field5084));
        this.field5086 = new BooleanSetting(field5214, false, () -> this.field5066.isMode(field5155));
        this.field5139 = new SliderSetting(field5215, field5216, field5217, field5218, field5219, () -> this.field5066.isMode(field5164) && (Boolean)this.field5086.getValue() != false);
        this.field5138 = new SliderSetting(field5220, field5221, field5222, field5223, field5224, () -> this.field5066.isMode(field5102) && (Boolean)this.field5086.getValue() != false);
        this.field5087 = new VelocityUtil();
        this.field5063 = new ClientState();
        this.field5160 = new BooleanSetting(field5225, true, () -> this.field5066.isMode(field5076));
        this.field5079 = new BooleanSetting(field5226, false, () -> this.field5066.isMode(field5075));
        this.field5105 = new SliderSetting(field5227, field5228, field5229, field5230, 1.0f, () -> this.field5066.isMode(field5078) && (Boolean)this.field5079.getValue() != false);
        this.addSettings(this.field5066, this.field5103, this.field5145, this.field5153, this.field5151, this.field5083, this.field5135, this.field5086, this.field5139, this.field5138, this.field5160, this.field5079, this.field5105);
    }

    static {
        field5176 = "Speed";
        field5177 = "Увеличивает скорость передвижения персонажа";
        field5178 = "Режим";
        field5181 = "Collision";
        field5182 = "Collision2";
        field5184 = "MetaHvH";
        field5185 = "Grim Teleport";
        field5187 = "Дистанция";
        field5188 = 1.5f;
        field5189 = 0.1f;
        field5190 = 3.0f;
        field5191 = 0.01f;
        field5192 = "Скорость";
        field5193 = 0.15f;
        field5194 = 0.1f;
        field5195 = 0.01f;
        field5196 = "Скорость";
        field5197 = 0.35f;
        field5198 = 0.3f;
        field5199 = 0.05f;
        field5200 = "Дистанция";
        field5201 = 0.35f;
        field5202 = 0.2f;
        field5203 = 0.95f;
        field5204 = 0.01f;
        field5205 = "Meta режим";
        field5206 = "Default";
        field5207 = "Default";
        field5208 = "Custom";
        field5209 = "Meta скорость";
        field5210 = 0.2f;
        field5211 = 0.2f;
        field5212 = 1.05f;
        field5213 = 0.01f;
        field5214 = "Meta буст с дамагом";
        field5215 = "Meta значение буста";
        field5216 = 0.7f;
        field5217 = 0.1f;
        field5218 = 5.0f;
        field5219 = 0.1f;
        field5220 = "Meta длительность буста";
        field5221 = 700.0f;
        field5222 = 100.0f;
        field5223 = 2000.0f;
        field5224 = 100.0f;
        field5225 = "Disabler Reallyworld";
        field5226 = "Понижать ФПС";
        field5227 = "ФПС";
        field5228 = 30.0f;
        field5229 = 15.0f;
        field5230 = 30.0f;
        field5109 = "Grim Teleport";
        field5110 = "Grim New";
        field5111 = "Reallyworld";
        field5112 = "Reallyworld";
        field5113 = "Matrix";
        field5114 = "Collision";
        field5115 = "Collision2";
        field5116 = "HolyWorld";
        field5117 = "MetaHvH";
        field5118 = "Grim Teleport";
        field5125 = "Default";
        field5126 = 0.49665f;
        field5127 = 0.41598004f;
        field5128 = 0.43f;
        field5129 = 0.36f;
        field5130 = 0.2924f;
        field5131 = 0.24480002f;
        field5132 = 0.2924f;
        field5133 = 0.24480002f;
        field5134 = "Custom";
        field5136 = 0.835f;
        field5137 = 1.435f;
        field5140 = 10.0f;
        field5141 = 1.3f;
        field5165 = "Шар Геракла 2";
        field5166 = "Шар CHAMPION";
        field5167 = "Шар GOD";
        field5168 = "Талисман Венома";
        field5169 = "КУБИК-РУБИК";
        field5106 = "AttributeModifiers";
        field5107 = "AttributeModifiers";
        field5108 = ":[{Amount:3.0d,Slot:\"head\",AttributeName:\"minecraft:generic.armor\",Operation:0,UUID:[I;427683850,761809167,-1124274585,-962634053]";
        field5142 = "AttributeModifiers";
        field5143 = "AttributeModifiers";
        field5144 = ":[{Amount:3.5d,Slot:\"head\",AttributeName:\"minecraft:generic.armor\",Operation:0,UUID:[I;-368453572,-1112977890,-1779712266,-159547550]";
        field5122 = "MetaHvH";
        field5123 = "Grim New";
        field5124 = "Reallyworld";
        field5170 = 1.7f;
        field5171 = 0.03;
        field5172 = 0.03;
        field5173 = 0.085;
        field5174 = 0.03;
        field5085 = "MetaHvH";
        field5093 = "Reallyworld";
        field5094 = "Grim New";
        field5095 = 0.03;
        field5096 = 1.6f;
        field5097 = 0.03f;
        field5098 = 0.0855f;
        field5088 = "Reallyworld";
        field5089 = "Grim New";
        field5159 = "Reallyworld";
        field5161 = 0.3f;
        field5162 = "Grim New";
        field5163 = 0.4f;
        field5147 = 100L;
        field5148 = 1400L;
        field5149 = 1.5f;
        field5150 = 1.2f;
        field5099 = -0.4448259643949201;
        field5100 = 2.0;
        field5101 = 2.0;
        field5146 = 0.2;
        field5152 = 0.15;
        field5154 = 10.0;
        field5156 = -1.0f;
        field5157 = 90.0f;
        field5158 = 90.0f;
        field5067 = "Grim Teleport";
        field5090 = "Grim Teleport";
        field5091 = "Grim New";
        field5092 = "Reallyworld";
        field5078 = "Reallyworld";
        field5075 = "Reallyworld";
        field5076 = "Reallyworld";
        field5102 = "MetaHvH";
        field5164 = "MetaHvH";
        field5155 = "MetaHvH";
        field5082 = "MetaHvH";
        field5084 = "Custom";
        field5175 = "MetaHvH";
        field5077 = "HolyWorld";
        field5121 = "HolyWorld";
        field5080 = "Collision";
        field5081 = "Collision2";
        field5119 = "Collision";
        field5120 = "Collision2";
    }
}



/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.RespawnAnchorBlock
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.ingame.InventoryScreen
 *  net.minecraft.entity.EntityType
 *  net.minecraft.entity.TntEntity
 *  net.minecraft.entity.decoration.EndCrystalEntity
 *  net.minecraft.entity.effect.StatusEffects
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.entity.projectile.TridentEntity
 *  net.minecraft.entity.vehicle.TntMinecartEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket
 *  net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket
 *  net.minecraft.screen.slot.Slot
 *  net.minecraft.screen.slot.SlotActionType
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.Hand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package minced.module.combat;
import minced.core.EventHandler;

import minced.core.MultiBooleanSetting;
import minced.core.BooleanSetting;
import minced.core.SliderSetting;
import minced.core.EventInput;
import minced.core.EventUpdate;
import minced.core.EventPacket;
import minced.core.ClientState;
import minced.core.Minced;
import minced.core.Module;
import minced.enums.Combat;
import minced.module.movement.GuiMove;
import net.minecraft.block.Blocks;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.state.property.Property;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AutoTotem
extends Module {
    static int cfr_renamed_374;

    MultiBooleanSetting field3743;
    static String field3744;
    static MinecraftClient I = MinecraftClient.getInstance();
    static String field3745;
    SliderSetting field3746;
    static String field3747;
    SliderSetting field3748;
    static String field3749;
    SliderSetting field3750;
    static String field3751;
    int field3752;
    static String field3753;
    static String field3754;
    static float field3755;
    BooleanSetting field3756;
    MultiBooleanSetting field3757;
    static String field3758;
    static String field3759;
    static String field3760;
    static String field3761;
    int field3762 = 0;
    boolean field3763 = false;
    int field3764 = 0;
    int field3765 = -1;
    boolean field3766 = false;
    int field3767 = -1;
    int field3768;
    boolean field3769 = false;
    boolean field3770 = false;
    int field3771 = -1;
    ClientState field3772;
    boolean field3773 = false;
    static long field3774;
    static long field3775;
    BooleanSetting field3776;
    static boolean field3777;
    int field3778 = 0;
    int field3779 = -1;
    BooleanSetting field3780;
    static String field3781;
    static String field3782;
    static String field3783;
    static String field3784;
    static String field3785;
    static String field3786;
    SliderSetting field3787;
    static double field3788;
    static double field3789;
    static double field3790;
    static String field3791;
    static String field3792;
    BooleanSetting field3793;
    static String field3794;
    static String field3795;
    static String field3796;
    static String field3797;
    static String field3798;
    BooleanSetting field3799;
    static String field3800;
    static String field3801;
    static String field3802;
    static String field3803;
    static double field3804;
    static double field3805;
    static double field3806;
    static String field3807;
    static String field3808;
    static String field3809;
    static String field3810;
    static String field3811;
    BooleanSetting field3812;
    static String field3813;
    SliderSetting field3814;
    static String field3815;
    static String field3816;
    static String field3817;
    static String field3818;
    static String field3819;
    static String field3820;
    static String field3821;
    static String field3822;
    static String field3823;
    static String field3824;
    static String field3825;
    SliderSetting field3826;
    static String field3827;
    SliderSetting field3828;
    static String field3829;
    SliderSetting field3830;
    static String field3831;
    SliderSetting field3832;
    static String field3833;
    SliderSetting field3834;
    static String field3835;
    SliderSetting field3836;
    static String field3837;
    SliderSetting field3838;
    static String field3839;
    SliderSetting field3840;
    static String field3841;
    SliderSetting field3842;
    static String field3843;
    SliderSetting field3844;
    static String field3845;
    static String field3846;
    static String field3847;
    static String field3848;
    static long field3849;
    static String field3850;
    static String field3851;
    static String field3852;
    static String field3853;
    static String field3854;
    static String field3855;
    static String field3856;
    static String field3857;
    static String field3858;
    static String field3859;
    static String field3860;
    static String field3861;
    static String field3862;
    static String field3863;
    static String field3864;
    static String field3865;
    static String field3866;
    static String field3867;
    static String field3868;
    static String field3869;
    static String field3870;
    static String field3871;
    static String field3872;
    static String field3873;
    static String field3874;
    static float field3875;
    static float field3876;
    static float field3877;
    SliderSetting field3878 = new SliderSetting(field3874, field3875, 1.0f, field3876, field3877);
    static String field3879;
    static float field3880;
    static float field3881;
    static float field3882;
    SliderSetting field3883 = new SliderSetting(field3879, field3880, 1.0f, field3881, field3882);
    static String field3884;
    static String field3885;
    BooleanSetting field3886;
    static String field3887;
    static String field3888;
    static String field3889;
    static String field3890;
    static String field3891;
    static String field3892;
    static String field3893;
    static String field3894;
    static String field3895;
    static String field3896;
    static String field3897;
    static String field3898;
    static String field3899;
    static float field3900;
    static float field3901;
    static String field3902;
    static float field3903;
    static float field3904;
    static String field3905;
    static float field3906;
    static float field3907;
    static String field3908;
    static float field3909;
    static float field3910;
    static String field3911;
    static float field3912;
    static float field3913;
    static String field3914;
    static String field3915;
    static String field3916;
    static String field3917;
    static String field3918;
    static String field3919;
    static String field3920;
    static String field3921;
    static String field3922;
    static String field3923;
    static String field3924;
    static String field3925;
    static String field3926;
    static float field3927;
    static float field3928;
    static String field3929;
    static float field3930;
    static float field3931;
    static float field3932;
    SliderSetting field3933;
    static String field3934;
    static float field3935;
    static float field3936;
    static String field3937;
    static float field3938;
    static float field3939;
    static float field3940;
    SliderSetting field3941;
    static String field3942;
    static float field3943;
    static float field3944;
    static String field3945;
    static float field3946;
    static float field3947;
    static float field3948;
    SliderSetting field3949;
    static String field3950;
    static float field3951;
    static float field3952;
    static String field3953;
    static float field3954;
    static float field3955;
    static float field3956;
    SliderSetting field3957;
    static String field3958;
    static float field3959;
    static float field3960;
    static String field3961;
    static float field3962;
    static float field3963;
    static float field3964;
    SliderSetting field3965;
    static String field3966;
    static float field3967;
    static float field3968;
    static String field3969;
    static float field3970;
    static float field3971;
    static float field3972;
    SliderSetting field3973;
    static String field3974;
    static float field3975;
    static float field3976;
    static String field3977;
    static float field3978;
    static float field3979;
    static float field3980;
    SliderSetting field3981;
    static String field3982;
    static float field3983;
    static float field3984;
    static String field3985;
    static float field3986;
    static float field3987;
    static float field3988;
    SliderSetting field3989;
    static String field3990;
    static float field3991;
    static float field3992;
    static String field3993;
    static float field3994;
    static float field3995;
    static float field3996;
    SliderSetting field3997;
    static String field3998;
    static float field3999;
    static float field4000;
    static String field4001;
    static float field4002;
    static float field4003;
    static float field4004;
    SliderSetting field4005;
    static String field4006;
    static String field4007;
    static String field4008;
    static String field4009;
    static String field4010;
    static String field4011;
    static String field4012;
    static String field4013;
    static String field4014;
    static String field4015;
    static String field4016;
    static String field4017;
    static String field4018;
    static String field4019;
    static String field4020;
    static String field4021;
    static String field4022;
    static String field4023;
    static String field4024;
    static String field4025;
    static String field4026;
    static float field4027;

    private boolean cfr_renamed_368() {
        if (Boolean.TRUE.equals(this.field3743.method1898(field3744)) && !AutoTotem.I.world.getNonSpectatingEntities(EndCrystalEntity.class, AutoTotem.I.player.getBoundingBox().expand((double)this.method1048())).isEmpty()) {
            return true;
        }
        if (Boolean.TRUE.equals(this.field3743.method1898(field3745)) && !AutoTotem.I.world.getNonSpectatingEntities(TntEntity.class, AutoTotem.I.player.getBoundingBox().expand((double)((Float)this.field3746.getValue()).floatValue())).isEmpty()) {
            return true;
        }
        if (Boolean.TRUE.equals(this.field3743.method1898(field3747)) && !AutoTotem.I.world.getNonSpectatingEntities(TntMinecartEntity.class, AutoTotem.I.player.getBoundingBox().expand((double)((Float)this.field3748.getValue()).floatValue())).isEmpty()) {
            return true;
        }
        if (Boolean.TRUE.equals(this.field3743.method1898(field3749))) {
            for (TridentEntity v2 : AutoTotem.I.world.getNonSpectatingEntities(TridentEntity.class, AutoTotem.I.player.getBoundingBox().expand((double)((Float)this.field3750.getValue()).floatValue()))) {
                if (v2.getOwner() == AutoTotem.I.player) continue;
                return true;
            }
        }
        return Boolean.TRUE.equals(this.field3743.method1898(field3751)) && AutoTotem.I.world.getRegistryKey() != World.NETHER && this.cfr_renamed_369();
    }

    private Slot cfr_renamed_0() {
        this.field3752 = (int)AutoTotem.I.player.currentScreenHandler.slots.stream().filter(param0 -> param0.getStack().getItem() == Items.TOTEM_OF_UNDYING && !param0.getStack().hasEnchantments()).count();
        return AutoTotem.I.player.currentScreenHandler.slots.stream().filter(param1 -> param1.getStack().getItem() == Items.TOTEM_OF_UNDYING && ((Boolean)this.field3812.getValue() == false || !param1.getStack().hasEnchantments() || this.field3752 <= 0)).filter(param1 -> (Boolean)this.field3799.getValue() == false || !param1.getStack().getName().getString().equals(field3800)).findFirst().orElse(null);
    }

    private boolean cfr_renamed_370() {
        boolean v2;
        float v1 = AutoTotem.I.player.getHealth();
        if (Boolean.TRUE.equals(this.field3743.method1898(field3753)) && AutoTotem.I.player.hasStatusEffect(StatusEffects.ABSORPTION)) {
            v1 += AutoTotem.I.player.getAbsorptionAmount();
        }
        if (!(v2 = this.cfr_renamed_371()) && Boolean.TRUE.equals(this.field3743.method1898(field3754)) && !AutoTotem.I.player.isTouchingWater() && !AutoTotem.I.player.isGliding() && AutoTotem.I.player.fallDistance > field3755) {
            return true;
        }
        float v3 = this.method1051();
        return v1 <= v3;
    }

    private boolean cfr_renamed_372() {
        GuiMove v1 = Minced.getInstance().getModuleManager().method182();
        return v1 != null && v1.isEnabled() && Boolean.TRUE.equals(v1.isBypassMode(field3761));
    }

    @EventHandler(2)
    private void method1039(EventUpdate param1) {
        GuiMove v4;
        Slot v22;
        if (this.field3762 > 0) {
            --this.field3762;
        }
        if (this.field3763) {
            if (this.field3764 > 0) {
                --this.field3764;
                return;
            }
            if (this.field3765 >= 0 && AutoTotem.I.player != null && AutoTotem.I.player.currentScreenHandler != null) {
                boolean v22_411;
                boolean bl = v22_411 = this.field3766 && (this.cfr_renamed_368() || this.cfr_renamed_370());
                if (!v22_411) {
                    int v3 = AutoTotem.I.player.currentScreenHandler.syncId;
                    AutoTotem.I.interactionManager.clickSlot(v3, this.field3765, 40, SlotActionType.SWAP, (PlayerEntity)AutoTotem.I.player);
                    AutoTotem.I.player.networkHandler.sendPacket((Packet)new CloseHandledScreenC2SPacket(v3));
                    if (this.field3766) {
                        this.field3767 = -1;
                    }
                }
            }
            this.field3763 = false;
            this.field3765 = -1;
            this.field3766 = false;
            this.field3764 = 0;
            this.field3768 = 0;
            return;
        }
        if (this.field3769 && !this.cfr_renamed_371() && !this.cfr_renamed_373() && this.cfr_renamed_368() && this.field3770 && (v22 = this.cfr_renamed_0()) != null) {
            this.field3771 = v22.id;
            this.field3770 = false;
            this.field3772.method3114();
        }
        long l2 = this.field3773 ? field3774 : field3775;
        if (this.field3769 && this.field3771 != -1 && this.field3772.method3113(l2)) {
            if (AutoTotem.I.player.isUsingItem() && !this.cfr_renamed_368() && !this.cfr_renamed_370()) {
                return;
            }
            if (this.field3770 && (this.cfr_renamed_368() || this.cfr_renamed_370())) {
                this.field3769 = false;
                this.field3770 = false;
                this.field3771 = -1;
                this.field3768 = 0;
                return;
            }
            if (Boolean.TRUE.equals(this.field3776.getValue())) {
                field3777 = false;
            }
            I.setScreen((Screen)new InventoryScreen((PlayerEntity)AutoTotem.I.player));
            AutoTotem.I.interactionManager.clickSlot(AutoTotem.I.player.currentScreenHandler.syncId, this.field3771, 40, SlotActionType.SWAP, (PlayerEntity)AutoTotem.I.player);
            AutoTotem.I.player.networkHandler.sendPacket((Packet)new CloseHandledScreenC2SPacket(AutoTotem.I.player.currentScreenHandler.syncId));
            I.setScreen(null);
            this.field3769 = false;
            this.field3773 = false;
            field3777 = false;
            if (this.field3770) {
                this.field3767 = -1;
                this.field3770 = false;
            }
            this.field3771 = -1;
            this.field3768 = 0;
        }
        if (this.field3769) {
            return;
        }
        if (this.field3778 == 1) {
            if (this.cfr_renamed_368() || this.cfr_renamed_370()) {
                this.field3778 = 0;
                this.field3779 = -1;
                return;
            }
            v4 = Minced.getInstance().getModuleManager().method182();
            if (v4 != null && v4.method549()) {
                v4.setInventoryActionInProgress(true);
            }
            AutoTotem.I.interactionManager.clickSlot(AutoTotem.I.player.currentScreenHandler.syncId, this.field3779, 40, SlotActionType.SWAP, (PlayerEntity)AutoTotem.I.player);
            AutoTotem.I.player.networkHandler.sendPacket((Packet)new CloseHandledScreenC2SPacket(AutoTotem.I.player.currentScreenHandler.syncId));
            if (v4 != null && v4.method549()) {
                v4.setInventoryActionInProgress(false);
            }
            this.field3778 = 0;
            this.field3779 = -1;
        }
        if (this.field3778 == 2 && this.field3762 == 0) {
            this.field3778 = 3;
        }
        if (this.field3778 == 3 && this.field3779 != -1) {
            v4 = Minced.getInstance().getModuleManager().method182();
            if (v4 != null && v4.method549()) {
                v4.setInventoryActionInProgress(true);
            }
            AutoTotem.I.interactionManager.clickSlot(AutoTotem.I.player.currentScreenHandler.syncId, this.field3779, 40, SlotActionType.SWAP, (PlayerEntity)AutoTotem.I.player);
            AutoTotem.I.player.networkHandler.sendPacket((Packet)new CloseHandledScreenC2SPacket(AutoTotem.I.player.currentScreenHandler.syncId));
            if (v4 != null && v4.method549()) {
                v4.setInventoryActionInProgress(false);
            }
            this.field3778 = 0;
            this.field3779 = -1;
            return;
        }
        if (this.field3778 > 0) {
            return;
        }
        ++this.field3768;
        Slot v4s = this.cfr_renamed_0();
        if (this.field3768 >= 15) {
            if (!this.cfr_renamed_371() && !this.cfr_renamed_373() && this.cfr_renamed_368() && v4s != null) {
                if (this.method1044()) {
                    this.field3773 = true;
                }
                this.method1043(((Slot)v4s).id);
                return;
            }
            if (this.cfr_renamed_370()) {
                if (v4s != null && !this.cfr_renamed_373()) {
                    this.method1043(((Slot)v4s).id);
                }
            } else if (this.field3767 != -1 && ((Boolean)this.field3780.getValue()).booleanValue()) {
                if (this.cfr_renamed_368()) {
                    return;
                }
                if (this.method1044()) {
                    this.field3772.method3114();
                    this.field3769 = true;
                    this.field3770 = true;
                    this.field3771 = this.field3767;
                    this.field3768 = 0;
                } else if (this.cfr_renamed_372()) {
                    this.field3763 = true;
                    this.field3765 = this.field3767;
                    this.field3766 = true;
                    this.field3764 = 2;
                    this.field3768 = 0;
                } else if (this.method1040() || this.method1047()) {
                    this.field3762 = 1;
                    this.field3778 = 1;
                    this.field3779 = this.field3767;
                    this.field3767 = -1;
                } else {
                    AutoTotem.I.interactionManager.clickSlot(AutoTotem.I.player.currentScreenHandler.syncId, this.field3767, 40, SlotActionType.SWAP, (PlayerEntity)AutoTotem.I.player);
                    AutoTotem.I.player.networkHandler.sendPacket((Packet)new CloseHandledScreenC2SPacket(AutoTotem.I.player.currentScreenHandler.syncId));
                    this.field3767 = -1;
                    this.field3768 = 0;
                }
            }
        }
    }

    @Override
    public void onDisable() {
        this.field3767 = -1;
        this.field3768 = 0;
        this.field3778 = 0;
        this.field3779 = -1;
        this.field3762 = 0;
        this.field3769 = false;
        this.field3771 = -1;
        this.field3770 = false;
        this.field3773 = false;
        this.field3763 = false;
        this.field3765 = -1;
        this.field3766 = false;
        this.field3764 = 0;
        field3777 = false;
        super.onDisable();
    }

    private boolean method1040() {
        GuiMove v1 = Minced.getInstance().getModuleManager().method182();
        return v1 != null && v1.method561();
    }

    private boolean cfr_renamed_369() {
        BlockPos v1 = AutoTotem.I.player.getBlockPos();
        int v2 = (int)Math.ceil(((Float)this.field3787.getValue()).floatValue());
        for (int v3 = -v2; v3 <= v2; ++v3) {
            for (int v4 = -v2; v4 <= v2; ++v4) {
                for (int v5 = -v2; v5 <= v2; ++v5) {
                    double v8;
                    int v7;
                    BlockPos v6 = v1.add(v3, v4, v5);
                    if (AutoTotem.I.world.getBlockState(v6).getBlock() != Blocks.RESPAWN_ANCHOR || (v7 = ((Integer)AutoTotem.I.world.getBlockState(v6).get((Property)RespawnAnchorBlock.CHARGES)).intValue()) <= 0 || !((v8 = Math.sqrt(AutoTotem.I.player.squaredDistanceTo((double)v6.getX() + field3788, (double)v6.getY() + field3789, (double)v6.getZ() + field3790))) <= (double)(((Float)this.field3787.getValue()).floatValue() * ((Float)this.field3787.getValue()).floatValue()))) continue;
                    return true;
                }
            }
        }
        return false;
    }

    private boolean method1042() {
        GuiMove v1 = Minced.getInstance().getModuleManager().method182();
        return v1 != null && v1.method556();
    }

    public static void cfr_renamed_96() {
        field3777 = false;
    }

    private void method1043(int param1) {
        if (((Boolean)this.field3793.getValue()).booleanValue() && AutoTotem.I.player.isUsingItem()) {
            return;
        }
        if (!AutoTotem.I.player.getOffHandStack().isEmpty() && this.field3767 == -1) {
            this.field3767 = param1;
        }
        if (this.method1044()) {
            this.field3772.method3114();
            this.field3769 = true;
            this.field3771 = param1;
            this.field3768 = 0;
            return;
        }
        if (this.cfr_renamed_372()) {
            this.field3763 = true;
            this.field3765 = param1;
            this.field3766 = false;
            this.field3764 = 2;
            this.field3768 = 0;
            return;
        }
        if (this.method1040() || this.method1047()) {
            this.field3762 = 2;
            this.field3778 = 2;
            this.field3779 = param1;
            this.field3768 = 0;
            return;
        }
        if (this.method1042()) {
            GuiMove v2 = Minced.getInstance().getModuleManager().method182();
            v2.method548();
        }
        AutoTotem.I.interactionManager.clickSlot(AutoTotem.I.player.currentScreenHandler.syncId, param1, 40, SlotActionType.SWAP, (PlayerEntity)AutoTotem.I.player);
        AutoTotem.I.player.networkHandler.sendPacket((Packet)new CloseHandledScreenC2SPacket(AutoTotem.I.player.currentScreenHandler.syncId));
        this.field3768 = 0;
    }

    private boolean method1044() {
        GuiMove v1 = Minced.getInstance().getModuleManager().method182();
        return v1 != null && v1.isEnabled() && v1.isBypassMode(field3795);
    }

    @EventHandler(2)
    private void method1046(EventPacket param1) {
        if (!param1.method2466()) {
            return;
        }
        Packet v3 = param1.method2467();
        if (v3 instanceof EntitySpawnS2CPacket) {
            EntitySpawnS2CPacket v2 = (EntitySpawnS2CPacket)v3;
            EntityType v3e = v2.getEntityType();
            double v4 = Math.sqrt(Math.pow(AutoTotem.I.player.getX() - v2.getX(), field3804) + Math.pow(AutoTotem.I.player.getY() - v2.getY(), field3805) + Math.pow(AutoTotem.I.player.getZ() - v2.getZ(), field3806));
            boolean v6 = false;
            if (v3e == EntityType.END_CRYSTAL && Boolean.TRUE.equals(this.field3743.method1898(field3807)) && v4 <= (double)this.method1048()) {
                v6 = true;
            } else if (v3e == EntityType.TNT && Boolean.TRUE.equals(this.field3743.method1898(field3808)) && v4 <= (double)((Float)this.field3746.getValue()).floatValue()) {
                v6 = true;
            } else if (v3e == EntityType.TNT_MINECART && Boolean.TRUE.equals(this.field3743.method1898(field3809)) && v4 <= (double)((Float)this.field3748.getValue()).floatValue()) {
                v6 = true;
            } else if (v3e == EntityType.TRIDENT && Boolean.TRUE.equals(this.field3743.method1898(field3810)) && v4 <= (double)((Float)this.field3750.getValue()).floatValue()) {
                v6 = true;
            }
            if (!v6) {
                return;
            }
            if (this.cfr_renamed_371() || this.cfr_renamed_373()) {
                return;
            }
            Slot v7 = this.cfr_renamed_0();
            if (v7 == null) {
                return;
            }
            if (this.method1044()) {
                this.field3773 = true;
            }
            this.method1043(v7.id);
        }
    }

    private boolean method1047() {
        GuiMove v1 = Minced.getInstance().getModuleManager().method182();
        return v1 != null && v1.method549();
    }

    private float method1048() {
        float f;
        if (!((Boolean)this.field3756.getValue()).booleanValue()) {
            return ((Float)this.field3814.getValue()).floatValue();
        }
        String v1 = this.method1050();
        if (v1 == null) {
            return ((Float)this.field3814.getValue()).floatValue();
        }
        int v3 = -1;
        switch (v1.hashCode()) {
            case -2008703697: {
                if (!v1.equals(field3815)) break;
                v3 = 0;
                break;
            }
            case -2009050901: {
                if (!v1.equals(field3816)) break;
                v3 = 1;
                break;
            }
            case -2146678029: {
                if (!v1.equals(field3817)) break;
                v3 = 2;
                break;
            }
            case -2137538710: {
                if (!v1.equals(field3818)) break;
                v3 = 3;
                break;
            }
            case 1693761396: {
                if (!v1.equals(field3819)) break;
                v3 = 4;
                break;
            }
            case -2133895590: {
                if (!v1.equals(field3820)) break;
                v3 = 5;
                break;
            }
            case -1262953155: {
                if (!v1.equals(field3821)) break;
                v3 = 6;
                break;
            }
            case 1211763582: {
                if (!v1.equals(field3822)) break;
                v3 = 7;
                break;
            }
            case -657790833: {
                if (!v1.equals(field3823)) break;
                v3 = 8;
                break;
            }
            case -2009080750: {
                if (!v1.equals(field3824)) break;
                v3 = 9;
            }
        }
        switch (v3) {
            case 0: {
                f = (Boolean.TRUE.equals(this.field3757.method1898(field3825)) ? (Float)this.field3826.getValue() : (Float)this.field3814.getValue()).floatValue();
                break;
            }
            case 1: {
                f = (Boolean.TRUE.equals(this.field3757.method1898(field3827)) ? (Float)this.field3828.getValue() : (Float)this.field3814.getValue()).floatValue();
                break;
            }
            case 2: {
                f = (Boolean.TRUE.equals(this.field3757.method1898(field3829)) ? (Float)this.field3830.getValue() : (Float)this.field3814.getValue()).floatValue();
                break;
            }
            case 3: {
                f = (Boolean.TRUE.equals(this.field3757.method1898(field3831)) ? (Float)this.field3832.getValue() : (Float)this.field3814.getValue()).floatValue();
                break;
            }
            case 4: {
                f = (Boolean.TRUE.equals(this.field3757.method1898(field3833)) ? (Float)this.field3834.getValue() : (Float)this.field3814.getValue()).floatValue();
                break;
            }
            case 5: {
                f = (Boolean.TRUE.equals(this.field3757.method1898(field3835)) ? (Float)this.field3836.getValue() : (Float)this.field3814.getValue()).floatValue();
                break;
            }
            case 6: {
                f = (Boolean.TRUE.equals(this.field3757.method1898(field3837)) ? (Float)this.field3838.getValue() : (Float)this.field3814.getValue()).floatValue();
                break;
            }
            case 7: {
                f = (Boolean.TRUE.equals(this.field3757.method1898(field3839)) ? (Float)this.field3840.getValue() : (Float)this.field3814.getValue()).floatValue();
                break;
            }
            case 8: {
                f = (Boolean.TRUE.equals(this.field3757.method1898(field3841)) ? (Float)this.field3842.getValue() : (Float)this.field3814.getValue()).floatValue();
                break;
            }
            case 9: {
                f = (Boolean.TRUE.equals(this.field3757.method1898(field3843)) ? (Float)this.field3844.getValue() : (Float)this.field3814.getValue()).floatValue();
                break;
            }
            default: {
                f = ((Float)this.field3814.getValue()).floatValue();
            }
        }
        return f;
    }

    @EventHandler(2)
    public void method1049(EventInput param1) {
        boolean v2 = AutoTotem.I.player.isUsingItem();
        if (this.field3762 > 0 && !v2) {
            param1.method1973(0.0f);
            param1.method1972(0.0f);
            param1.method1974(false);
            param1.method1970(false);
        }
        if (this.field3769 && !this.field3772.method3113(field3849) && !v2) {
            param1.method1973(0.0f);
            param1.method1972(0.0f);
            param1.method1974(false);
            param1.method1970(false);
        }
        if (this.field3763 && !v2) {
            param1.method1973(0.0f);
            param1.method1972(0.0f);
            param1.method1974(false);
            param1.method1970(false);
        }
    }

    private boolean cfr_renamed_373() {
        for (Hand v4 : Hand.values()) {
            ItemStack v5 = AutoTotem.I.player.getStackInHand(v4);
            if (v5.getItem() != Items.TOTEM_OF_UNDYING || ((Boolean)this.field3812.getValue()).booleanValue() && v5.hasEnchantments() && this.field3752 > 0 || ((Boolean)this.field3799.getValue()).booleanValue() && v5.getName().getString().equals(field3850)) continue;
            return true;
        }
        return false;
    }

    private String method1050() {
        if (AutoTotem.I.player == null) {
            return null;
        }
        ItemStack v1 = AutoTotem.I.player.getOffHandStack();
        if (v1.isEmpty() || v1.getItem() != Items.PLAYER_HEAD) {
            return null;
        }
        String v2 = v1.getName().getString().toLowerCase();
        if (v2.contains(field3852)) {
            return field3853;
        }
        if (v2.contains(field3854)) {
            return field3855;
        }
        if (v2.contains(field3856)) {
            return field3857;
        }
        if (v2.contains(field3858)) {
            return field3859;
        }
        if (v2.contains(field3860)) {
            return field3861;
        }
        if (v2.contains(field3862)) {
            return field3863;
        }
        if (v2.contains(field3864)) {
            return field3865;
        }
        if (v2.contains(field3866)) {
            return field3867;
        }
        if (v2.contains(field3868)) {
            return field3869;
        }
        if (v2.contains(field3870)) {
            return field3871;
        }
        return null;
    }

    public AutoTotem() {
        super(field3872, field3873, Combat.Combat);
        this.field3780 = new BooleanSetting(field3884, true);
        this.field3886 = new BooleanSetting(field3885, true);
        this.field3793 = new BooleanSetting(field3887, false);
        this.field3776 = new BooleanSetting(field3888, true, this::method1044);
        this.field3812 = new BooleanSetting(field3889, true);
        this.field3799 = new BooleanSetting(field3890, false);
        this.field3743 = new MultiBooleanSetting(field3891, new BooleanSetting(field3892, true), new BooleanSetting(field3893, true), new BooleanSetting(field3894, true), new BooleanSetting(field3895, true), new BooleanSetting(field3896, true), new BooleanSetting(field3897, true), new BooleanSetting(field3898, true));
        this.field3814 = new SliderSetting(field3899, field3900, 1.0f, field3901, 1.0f, () -> this.field3743.method1898(field3813));
        this.field3746 = new SliderSetting(field3902, field3903, 1.0f, field3904, 1.0f, () -> this.field3743.method1898(field3796));
        this.field3748 = new SliderSetting(field3905, field3906, 1.0f, field3907, 1.0f, () -> this.field3743.method1898(field3847));
        this.field3750 = new SliderSetting(field3908, field3909, 1.0f, field3910, 1.0f, () -> this.field3743.method1898(field3802));
        this.field3787 = new SliderSetting(field3911, field3912, 1.0f, field3913, 1.0f, () -> this.field3743.method1898(field3848));
        this.field3756 = new BooleanSetting(field3914, false);
        this.field3757 = new MultiBooleanSetting(field3915, this.field3756::getValue, new BooleanSetting(field3916, false), new BooleanSetting(field3917, false), new BooleanSetting(field3918, false), new BooleanSetting(field3919, false), new BooleanSetting(field3920, false), new BooleanSetting(field3921, false), new BooleanSetting(field3922, false), new BooleanSetting(field3923, false), new BooleanSetting(field3924, false), new BooleanSetting(field3925, false));
        this.field3826 = new SliderSetting(field3926, field3927, 1.0f, field3928, 1.0f, () -> (Boolean)this.field3756.getValue() != false && Boolean.TRUE.equals(this.field3757.method1898(field3781)));
        this.field3933 = new SliderSetting(field3929, field3930, 1.0f, field3931, field3932, () -> (Boolean)this.field3756.getValue() != false && Boolean.TRUE.equals(this.field3757.method1898(field3803)));
        this.field3828 = new SliderSetting(field3934, field3935, 1.0f, field3936, 1.0f, () -> (Boolean)this.field3756.getValue() != false && Boolean.TRUE.equals(this.field3757.method1898(field3851)));
        this.field3941 = new SliderSetting(field3937, field3938, 1.0f, field3939, field3940, () -> (Boolean)this.field3756.getValue() != false && Boolean.TRUE.equals(this.field3757.method1898(field3798)));
        this.field3830 = new SliderSetting(field3942, field3943, 1.0f, field3944, 1.0f, () -> (Boolean)this.field3756.getValue() != false && Boolean.TRUE.equals(this.field3757.method1898(field3846)));
        this.field3949 = new SliderSetting(field3945, field3946, 1.0f, field3947, field3948, () -> (Boolean)this.field3756.getValue() != false && Boolean.TRUE.equals(this.field3757.method1898(field3792)));
        this.field3832 = new SliderSetting(field3950, field3951, 1.0f, field3952, 1.0f, () -> (Boolean)this.field3756.getValue() != false && Boolean.TRUE.equals(this.field3757.method1898(field3801)));
        this.field3957 = new SliderSetting(field3953, field3954, 1.0f, field3955, field3956, () -> (Boolean)this.field3756.getValue() != false && Boolean.TRUE.equals(this.field3757.method1898(field3760)));
        this.field3834 = new SliderSetting(field3958, field3959, 1.0f, field3960, 1.0f, () -> (Boolean)this.field3756.getValue() != false && Boolean.TRUE.equals(this.field3757.method1898(field3791)));
        this.field3965 = new SliderSetting(field3961, field3962, 1.0f, field3963, field3964, () -> (Boolean)this.field3756.getValue() != false && Boolean.TRUE.equals(this.field3757.method1898(field3784)));
        this.field3836 = new SliderSetting(field3966, field3967, 1.0f, field3968, 1.0f, () -> (Boolean)this.field3756.getValue() != false && Boolean.TRUE.equals(this.field3757.method1898(field3811)));
        this.field3973 = new SliderSetting(field3969, field3970, 1.0f, field3971, field3972, () -> (Boolean)this.field3756.getValue() != false && Boolean.TRUE.equals(this.field3757.method1898(field3797)));
        this.field3838 = new SliderSetting(field3974, field3975, 1.0f, field3976, 1.0f, () -> (Boolean)this.field3756.getValue() != false && Boolean.TRUE.equals(this.field3757.method1898(field3845)));
        this.field3981 = new SliderSetting(field3977, field3978, 1.0f, field3979, field3980, () -> (Boolean)this.field3756.getValue() != false && Boolean.TRUE.equals(this.field3757.method1898(field3782)));
        this.field3840 = new SliderSetting(field3982, field3983, 1.0f, field3984, 1.0f, () -> (Boolean)this.field3756.getValue() != false && Boolean.TRUE.equals(this.field3757.method1898(field3758)));
        this.field3989 = new SliderSetting(field3985, field3986, 1.0f, field3987, field3988, () -> (Boolean)this.field3756.getValue() != false && Boolean.TRUE.equals(this.field3757.method1898(field3785)));
        this.field3842 = new SliderSetting(field3990, field3991, 1.0f, field3992, 1.0f, () -> (Boolean)this.field3756.getValue() != false && Boolean.TRUE.equals(this.field3757.method1898(field3783)));
        this.field3997 = new SliderSetting(field3993, field3994, 1.0f, field3995, field3996, () -> (Boolean)this.field3756.getValue() != false && Boolean.TRUE.equals(this.field3757.method1898(field3786)));
        this.field3844 = new SliderSetting(field3998, field3999, 1.0f, field4000, 1.0f, () -> (Boolean)this.field3756.getValue() != false && Boolean.TRUE.equals(this.field3757.method1898(field3794)));
        this.field4005 = new SliderSetting(field4001, field4002, 1.0f, field4003, field4004, () -> (Boolean)this.field3756.getValue() != false && Boolean.TRUE.equals(this.field3757.method1898(field3759)));
        this.field3772 = new ClientState();
        this.addSettings(this.field3878, this.field3883, this.field3780, this.field3886, this.field3793, this.field3776, this.field3812, this.field3799, this.field3743, this.field3814, this.field3746, this.field3748, this.field3750, this.field3787, this.field3756, this.field3757, this.field3826, this.field3933, this.field3828, this.field3941, this.field3830, this.field3949, this.field3832, this.field3957, this.field3834, this.field3965, this.field3836, this.field3973, this.field3838, this.field3981, this.field3840, this.field3989, this.field3842, this.field3997, this.field3844, this.field4005);
    }

    private float method1051() {
        float f;
        if (!((Boolean)this.field3756.getValue()).booleanValue()) {
            return (AutoTotem.I.player.isGliding() ? (Float)this.field3883.getValue() : (Float)this.field3878.getValue()).floatValue();
        }
        String v1 = this.method1050();
        if (v1 == null) {
            return (AutoTotem.I.player.isGliding() ? (Float)this.field3883.getValue() : (Float)this.field3878.getValue()).floatValue();
        }
        int v3 = -1;
        switch (v1.hashCode()) {
            case -2008703697: {
                if (!v1.equals(field4006)) break;
                v3 = 0;
                break;
            }
            case -2009050901: {
                if (!v1.equals(field4007)) break;
                v3 = 1;
                break;
            }
            case -2146678029: {
                if (!v1.equals(field4008)) break;
                v3 = 2;
                break;
            }
            case -2137538710: {
                if (!v1.equals(field4009)) break;
                v3 = 3;
                break;
            }
            case 1693761396: {
                if (!v1.equals(field4010)) break;
                v3 = 4;
                break;
            }
            case -2133895590: {
                if (!v1.equals(field4011)) break;
                v3 = 5;
                break;
            }
            case -1262953155: {
                if (!v1.equals(field4012)) break;
                v3 = 6;
                break;
            }
            case 1211763582: {
                if (!v1.equals(field4013)) break;
                v3 = 7;
                break;
            }
            case -657790833: {
                if (!v1.equals(field4014)) break;
                v3 = 8;
                break;
            }
            case -2009080750: {
                if (!v1.equals(field4015)) break;
                v3 = 9;
            }
        }
        switch (v3) {
            case 0: {
                f = (Boolean.TRUE.equals(this.field3757.method1898(field4016)) ? (Float)this.field3933.getValue() : (AutoTotem.I.player.isGliding() ? (Float)this.field3883.getValue() : (Float)this.field3878.getValue())).floatValue();
                break;
            }
            case 1: {
                f = (Boolean.TRUE.equals(this.field3757.method1898(field4017)) ? (Float)this.field3941.getValue() : (AutoTotem.I.player.isGliding() ? (Float)this.field3883.getValue() : (Float)this.field3878.getValue())).floatValue();
                break;
            }
            case 2: {
                f = (Boolean.TRUE.equals(this.field3757.method1898(field4018)) ? (Float)this.field3949.getValue() : (AutoTotem.I.player.isGliding() ? (Float)this.field3883.getValue() : (Float)this.field3878.getValue())).floatValue();
                break;
            }
            case 3: {
                f = (Boolean.TRUE.equals(this.field3757.method1898(field4019)) ? (Float)this.field3957.getValue() : (AutoTotem.I.player.isGliding() ? (Float)this.field3883.getValue() : (Float)this.field3878.getValue())).floatValue();
                break;
            }
            case 4: {
                f = (Boolean.TRUE.equals(this.field3757.method1898(field4020)) ? (Float)this.field3965.getValue() : (AutoTotem.I.player.isGliding() ? (Float)this.field3883.getValue() : (Float)this.field3878.getValue())).floatValue();
                break;
            }
            case 5: {
                f = (Boolean.TRUE.equals(this.field3757.method1898(field4021)) ? (Float)this.field3973.getValue() : (AutoTotem.I.player.isGliding() ? (Float)this.field3883.getValue() : (Float)this.field3878.getValue())).floatValue();
                break;
            }
            case 6: {
                f = (Boolean.TRUE.equals(this.field3757.method1898(field4022)) ? (Float)this.field3981.getValue() : (AutoTotem.I.player.isGliding() ? (Float)this.field3883.getValue() : (Float)this.field3878.getValue())).floatValue();
                break;
            }
            case 7: {
                f = (Boolean.TRUE.equals(this.field3757.method1898(field4023)) ? (Float)this.field3989.getValue() : (AutoTotem.I.player.isGliding() ? (Float)this.field3883.getValue() : (Float)this.field3878.getValue())).floatValue();
                break;
            }
            case 8: {
                f = (Boolean.TRUE.equals(this.field3757.method1898(field4024)) ? (Float)this.field3997.getValue() : (AutoTotem.I.player.isGliding() ? (Float)this.field3883.getValue() : (Float)this.field3878.getValue())).floatValue();
                break;
            }
            case 9: {
                f = (Boolean.TRUE.equals(this.field3757.method1898(field4025)) ? (Float)this.field4005.getValue() : (AutoTotem.I.player.isGliding() ? (Float)this.field3883.getValue() : (Float)this.field3878.getValue())).floatValue();
                break;
            }
            default: {
                f = (AutoTotem.I.player.isGliding() ? (Float)this.field3883.getValue() : (Float)this.field3878.getValue()).floatValue();
            }
        }
        return f;
    }

    private boolean cfr_renamed_371() {
        return !((Boolean)this.field3886.getValue() == false || AutoTotem.I.player.getOffHandStack().getItem() != Items.PLAYER_HEAD || Boolean.TRUE.equals(this.field3743.method1898(field4026)) && AutoTotem.I.player.fallDistance > field4027);
    }

    static {
        field3777 = false;
        cfr_renamed_374 = 0;
        field3872 = "AutoTotem";
        field3873 = "Автоматически свапает тотем при выбранной значении HP";
        field3874 = "Уровень здоровья";
        field3875 = 4.0f;
        field3876 = 20.0f;
        field3877 = 0.5f;
        field3879 = "Здоровье на элитре";
        field3880 = 8.0f;
        field3881 = 20.0f;
        field3882 = 0.5f;
        field3884 = "Возвращать предмет";
        field3885 = "Не менять шар";
        field3887 = "Не свапать, если рука активна";
        field3888 = "Игнорировать действия";
        field3889 = "Сохранять зачарованный";
        field3890 = "Не брать тотем драконида";
        field3891 = "Проверки на";
        field3892 = "Золотые сердца";
        field3893 = "Кристаллы";
        field3894 = "Динамит";
        field3895 = "Вагонетка с TNT";
        field3896 = "Трезубец";
        field3897 = "Якорь";
        field3898 = "Падение";
        field3899 = "Дистанция до кристалла";
        field3900 = 6.0f;
        field3901 = 10.0f;
        field3902 = "Дистанция до динамита";
        field3903 = 6.0f;
        field3904 = 50.0f;
        field3905 = "Дистанция до ТНТВагонетки";
        field3906 = 6.0f;
        field3907 = 10.0f;
        field3908 = "Дистанция трезубца";
        field3909 = 8.0f;
        field3910 = 15.0f;
        field3911 = "Дистанция до якоря";
        field3912 = 6.0f;
        field3913 = 10.0f;
        field3914 = "Настройки для шаров";
        field3915 = "Список шаров";
        field3916 = "Шар Огня";
        field3917 = "Шар Воды";
        field3918 = "Шар Земли";
        field3919 = "Шар Света";
        field3920 = "Шар Порядка";
        field3921 = "Шар Хаоса";
        field3922 = "Шар Воздуха";
        field3923 = "Шар BUNNY";
        field3924 = "Шар Д.Хелпера";
        field3925 = "Шар Бога";
        field3926 = "Огонь: дистанция кристалла";
        field3927 = 6.0f;
        field3928 = 10.0f;
        field3929 = "Огонь: хп тотема";
        field3930 = 4.0f;
        field3931 = 20.0f;
        field3932 = 0.5f;
        field3934 = "Вода: дистанция кристалла";
        field3935 = 6.0f;
        field3936 = 10.0f;
        field3937 = "Вода: хп тотема";
        field3938 = 4.0f;
        field3939 = 20.0f;
        field3940 = 0.5f;
        field3942 = "Земля: дистанция кристалла";
        field3943 = 6.0f;
        field3944 = 10.0f;
        field3945 = "Земля: хп тотема";
        field3946 = 4.0f;
        field3947 = 20.0f;
        field3948 = 0.5f;
        field3950 = "Свет: дистанция кристалла";
        field3951 = 6.0f;
        field3952 = 10.0f;
        field3953 = "Свет: хп тотема";
        field3954 = 4.0f;
        field3955 = 20.0f;
        field3956 = 0.5f;
        field3958 = "Порядок: дистанция кристалла";
        field3959 = 6.0f;
        field3960 = 10.0f;
        field3961 = "Порядок: хп тотема";
        field3962 = 4.0f;
        field3963 = 20.0f;
        field3964 = 0.5f;
        field3966 = "Хаос: дистанция кристалла";
        field3967 = 6.0f;
        field3968 = 10.0f;
        field3969 = "Хаос: хп тотема";
        field3970 = 4.0f;
        field3971 = 20.0f;
        field3972 = 0.5f;
        field3974 = "Воздух: дистанция кристалла";
        field3975 = 6.0f;
        field3976 = 10.0f;
        field3977 = "Воздух: хп тотема";
        field3978 = 4.0f;
        field3979 = 20.0f;
        field3980 = 0.5f;
        field3982 = "BUNNY: дистанция кристалла";
        field3983 = 6.0f;
        field3984 = 10.0f;
        field3985 = "BUNNY: хп тотема";
        field3986 = 4.0f;
        field3987 = 20.0f;
        field3988 = 0.5f;
        field3990 = "Д.Хелпер: дистанция кристалла";
        field3991 = 6.0f;
        field3992 = 10.0f;
        field3993 = "Д.Хелпер: хп тотема";
        field3994 = 4.0f;
        field3995 = 20.0f;
        field3996 = 0.5f;
        field3998 = "Бог: дистанция кристалла";
        field3999 = 6.0f;
        field4000 = 10.0f;
        field4001 = "Бог: хп тотема";
        field4002 = 4.0f;
        field4003 = 20.0f;
        field4004 = 0.5f;
        field3795 = "Funtime";
        field3761 = "Reallyworld";
        field3774 = 50L;
        field3775 = 100L;
        field3849 = 150L;
        field3804 = 2.0;
        field3805 = 2.0;
        field3806 = 2.0;
        field3807 = "Кристаллы";
        field3808 = "Динамит";
        field3809 = "Вагонетка с TNT";
        field3810 = "Трезубец";
        field3850 = "Тотем Драконида";
        field3753 = "Золотые сердца";
        field3754 = "Падение";
        field3755 = 10.0f;
        field4026 = "Падение";
        field4027 = 5.0f;
        field3744 = "Кристаллы";
        field3745 = "Динамит";
        field3747 = "Вагонетка с TNT";
        field3749 = "Трезубец";
        field3751 = "Якорь";
        field3815 = "Шар Огня";
        field3816 = "Шар Воды";
        field3817 = "Шар Земли";
        field3818 = "Шар Света";
        field3819 = "Шар Порядка";
        field3820 = "Шар Хаоса";
        field3821 = "Шар Воздуха";
        field3822 = "Шар BUNNY";
        field3823 = "Шар Д.Хелпера";
        field3824 = "Шар Бога";
        field3825 = "Шар Огня";
        field3827 = "Шар Воды";
        field3829 = "Шар Земли";
        field3831 = "Шар Света";
        field3833 = "Шар Порядка";
        field3835 = "Шар Хаоса";
        field3837 = "Шар Воздуха";
        field3839 = "Шар BUNNY";
        field3841 = "Шар Д.Хелпера";
        field3843 = "Шар Бога";
        field4006 = "Шар Огня";
        field4007 = "Шар Воды";
        field4008 = "Шар Земли";
        field4009 = "Шар Света";
        field4010 = "Шар Порядка";
        field4011 = "Шар Хаоса";
        field4012 = "Шар Воздуха";
        field4013 = "Шар BUNNY";
        field4014 = "Шар Д.Хелпера";
        field4015 = "Шар Бога";
        field4016 = "Шар Огня";
        field4017 = "Шар Воды";
        field4018 = "Шар Земли";
        field4019 = "Шар Света";
        field4020 = "Шар Порядка";
        field4021 = "Шар Хаоса";
        field4022 = "Шар Воздуха";
        field4023 = "Шар BUNNY";
        field4024 = "Шар Д.Хелпера";
        field4025 = "Шар Бога";
        field3852 = "шар огня";
        field3853 = "Шар Огня";
        field3854 = "шар воды";
        field3855 = "Шар Воды";
        field3856 = "шар земли";
        field3857 = "Шар Земли";
        field3858 = "шар света";
        field3859 = "Шар Света";
        field3860 = "шар порядка";
        field3861 = "Шар Порядка";
        field3862 = "шар хаоса";
        field3863 = "Шар Хаоса";
        field3864 = "шар воздуха";
        field3865 = "Шар Воздуха";
        field3866 = "шар bunny";
        field3867 = "Шар BUNNY";
        field3868 = "шар д.хелпера";
        field3869 = "Шар Д.Хелпера";
        field3870 = "шар бога";
        field3871 = "Шар Бога";
        field3788 = 0.5;
        field3789 = 0.5;
        field3790 = 0.5;
        field3800 = "Тотем Драконида";
        field3759 = "Шар Бога";
        field3794 = "Шар Бога";
        field3786 = "Шар Д.Хелпера";
        field3783 = "Шар Д.Хелпера";
        field3785 = "Шар BUNNY";
        field3758 = "Шар BUNNY";
        field3782 = "Шар Воздуха";
        field3845 = "Шар Воздуха";
        field3797 = "Шар Хаоса";
        field3811 = "Шар Хаоса";
        field3784 = "Шар Порядка";
        field3791 = "Шар Порядка";
        field3760 = "Шар Света";
        field3801 = "Шар Света";
        field3792 = "Шар Земли";
        field3846 = "Шар Земли";
        field3798 = "Шар Воды";
        field3851 = "Шар Воды";
        field3803 = "Шар Огня";
        field3781 = "Шар Огня";
        field3848 = "Якорь";
        field3802 = "Трезубец";
        field3847 = "Вагонетка с TNT";
        field3796 = "Динамит";
        field3813 = "Кристаллы";
        AutoTotem.cfr_renamed_96();
    }
}




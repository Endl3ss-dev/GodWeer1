/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.network.OtherClientPlayerEntity
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.decoration.ArmorStandEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.Items
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket
 *  net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket$Mode
 *  net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket
 *  net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket
 *  net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket$Action
 *  net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket
 *  net.minecraft.screen.slot.SlotActionType
 *  net.minecraft.util.Hand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Vec3d
 *  sg.ec.t0
 *  sg.ec.f14
 */
package minced.module.combat;
import minced.core.EventHandler;

import java.util.Comparator;
import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.StreamSupport;
import minced.core.MathUtil;
import minced.core.MultiBooleanSetting;
import minced.core.BooleanSetting;
import minced.core.SliderSetting;
import minced.core.EventInput;
import minced.core.ModeSetting;
import minced.core.EventInteractEntity;
import minced.core.EventUpdate;
import minced.core.TriggerRaytrace;
import minced.core.EventSwapWorld;
import minced.core.EventPostUpdate;
import minced.core.TpsUtil;
import minced.core.ClientState;
import minced.core.WeaponUtil;
import minced.core.InventoryUtil;
import minced.core.EntityFilter;
import minced.core.Minced;
import minced.core.Module;
import minced.enums.Combat;
import minced.mixin.accessor.IClientPlayerEntityMixin;
import minced.module.movement.AirStuck;
import minced.module.combat.AntiBot;
import minced.module.movement.ElytraResolver;
import minced.module.combat.PacketCriticals;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import sg.ec.t0;
import sg.ec.f14;

public class TriggerBot
extends Module {
    SliderSetting field3632;
    static MinecraftClient I = MinecraftClient.getInstance();
    MultiBooleanSetting field3633 = new MultiBooleanSetting(field3655, new BooleanSetting(field3656, true), new BooleanSetting(field3657, false), new BooleanSetting(field3658, true), new BooleanSetting(field3659, false), new BooleanSetting(field3660, false), new BooleanSetting(field3661, false));
    BooleanSetting field3634;
    static LivingEntity field3635;
    BooleanSetting field3636;
    boolean field3637;
    BooleanSetting field3638;
    MultiBooleanSetting field3639;
    BooleanSetting field3640;
    BooleanSetting field3641;
    ClientState field3642;
    static long field3643;
    BooleanSetting field3644;
    static float I8;
    static float field3645;
    static String field3646;
    static String field3647;
    WeaponUtil field3648;
    float field3649;
    static double field3650;
    static double field3651;
    boolean field3652;
    BooleanSetting field3653;
    static String field3654;
    static String Il;
    static String field3655;
    static String field3656;
    static String field3657;
    static String field3658;
    static String field3659;
    static String field3660;
    static String field3661;
    static String field3662;
    static String field3663;
    static String I4;
    static String field3664;
    ModeSetting field3665 = new ModeSetting(field3662, field3663, I4, field3664);
    static String field3666;
    static float field3667;
    static float I1;
    static float I0;
    static String cfr_renamed_404;
    static String field3668;
    static String field3669;
    static String field3670;
    static String field3671;
    static String I3;
    static String field3672;
    static String field3673;
    static String field3674;
    static String field3675;
    BooleanSetting field3676;
    static String field3677;
    static float field3678;
    int field3679 = 0;
    static float field3680;
    static float field3681;
    int field3682;
    static float I6;
    static float I9;
    static float I_;
    static float cfr_renamed_405;

    public SliderSetting method989() {
        return this.field3632;
    }

    private boolean method990(LivingEntity param1) {
        if (param1 == null || !param1.isAlive() || param1 == TriggerBot.I.player || param1 instanceof ArmorStandEntity) {
            return false;
        }
        if (param1 instanceof PlayerEntity) {
            PlayerEntity v2 = (PlayerEntity)param1;
            AntiBot cfr_ignored_0 = (AntiBot)Minced.getInstance().getModuleManager().method187(AntiBot.class);
            if (AntiBot.method1033(v2)) {
                return false;
            }
        }
        return EntityFilter.method3435((Entity)param1, this.field3633, true) || EntityFilter.method3436(param1, this.field3633) || EntityFilter.method3439(param1, this.field3633) || EntityFilter.method3431(param1, this.field3633);
    }

    @EventHandler(2)
    public void method992(EventInteractEntity param1) {
        if (field3635 != null) {
            param1.method2298(true);
        }
    }

    public BooleanSetting method993() {
        return this.field3636;
    }

    public boolean method996() {
        return this.field3637;
    }

    public void method997() {
        if (TriggerBot.I.world == null) {
            return;
        }
        for (PlayerEntity v2 : TriggerBot.I.world.getPlayers()) {
            if (!(v2 instanceof OtherClientPlayerEntity)) continue;
            ((f14)v2).releaseResolver();
        }
    }

    public BooleanSetting method998() {
        return this.field3638;
    }

    public MultiBooleanSetting method999() {
        return this.field3639;
    }

    public BooleanSetting method1001() {
        return this.field3634;
    }

    @EventHandler(2)
    public void method1002(EventInput param1) {
        if (field3635 != null && this.field3637) {
            param1.method1973(0.0f);
            param1.method1972(0.0f);
            this.field3637 = false;
        }
    }

    public BooleanSetting method1003() {
        return this.field3640;
    }

    private void method1005() {
        if (!((Boolean)this.field3641.getValue()).booleanValue()) {
            return;
        }
        int v1 = InventoryUtil.method3326();
        if (v1 == -1) {
            return;
        }
        if (field3635.getOffHandStack().getItem() != Items.SHIELD && field3635.getMainHandStack().getItem() != Items.SHIELD) {
            return;
        }
        if (v1 >= 9) {
            TriggerBot.I.interactionManager.clickSlot(TriggerBot.I.player.currentScreenHandler.syncId, v1, TriggerBot.I.player.getInventory().selectedSlot, SlotActionType.SWAP, (PlayerEntity)TriggerBot.I.player);
            TriggerBot.I.player.networkHandler.sendPacket((Packet)new CloseHandledScreenC2SPacket(TriggerBot.I.player.currentScreenHandler.syncId));
            TriggerBot.I.interactionManager.attackEntity((PlayerEntity)TriggerBot.I.player, (Entity)field3635);
            TriggerBot.I.player.swingHand(Hand.MAIN_HAND);
            TriggerBot.I.interactionManager.clickSlot(TriggerBot.I.player.currentScreenHandler.syncId, v1, TriggerBot.I.player.getInventory().selectedSlot, SlotActionType.SWAP, (PlayerEntity)TriggerBot.I.player);
            TriggerBot.I.player.networkHandler.sendPacket((Packet)new CloseHandledScreenC2SPacket(TriggerBot.I.player.currentScreenHandler.syncId));
        } else {
            TriggerBot.I.player.networkHandler.sendPacket((Packet)new UpdateSelectedSlotC2SPacket(v1));
            TriggerBot.I.interactionManager.attackEntity((PlayerEntity)TriggerBot.I.player, (Entity)field3635);
            TriggerBot.I.player.swingHand(Hand.MAIN_HAND);
            TriggerBot.I.player.networkHandler.sendPacket((Packet)new UpdateSelectedSlotC2SPacket(TriggerBot.I.player.getInventory().selectedSlot));
        }
    }

    /*
     * Unable to fully structure code
     */
    public boolean method1006() {
        if (TriggerBot.I.player == null || TriggerBot.I.world == null) {
            return false;
        }
        boolean ready = false;
        if (this.field3642.method3113(TriggerBot.field3643)) {
            PlayerEntity player = TriggerBot.I.player;
            float tickDelta = (Boolean)this.field3644.getValue() != false ? TpsUtil.method2600() : TriggerBot.I8;
            ready = player.getAttackCooldownProgress(tickDelta) > TriggerBot.field3645;
        }
        if (this.field3639.method1898(TriggerBot.field3646).booleanValue() && Boolean.TRUE.equals(t0.Д弟()) || this.field3639.method1898(TriggerBot.field3647).booleanValue() && TriggerBot.I.currentScreen != null && TriggerBot.I.currentScreen != Minced.getInstance().getDropDown()) {
            return false;
        }
        if (Boolean.TRUE.equals(t0.Дй())) {
            return ready;
        }
        AirStuck airStuck = (AirStuck)Minced.getInstance().getModuleManager().method187(AirStuck.class);
        if (airStuck != null && airStuck.isEnabled()) {
            return ready;
        }
        return ready && this.field3648.method3151((Boolean)this.field3634.getValue(), (Boolean)this.field3638.getValue(), (Boolean)this.field3644.getValue(), this.field3649) != false;
    }

    public BooleanSetting minced_core_ClsClass271_I() {
        return this.field3644;
    }

    public void method1007() {
        if (TriggerBot.I.world == null) {
            return;
        }
        for (PlayerEntity v2 : TriggerBot.I.world.getPlayers()) {
            if (!(v2 instanceof OtherClientPlayerEntity)) continue;
            ((f14)v2).resolve();
        }
    }

    @EventHandler(2)
    public void method1008(EventPostUpdate param1) {
        PacketCriticals packetCriticals = (PacketCriticals)Minced.getInstance().getModuleManager().method187(PacketCriticals.class);
        if (packetCriticals != null && packetCriticals.isEnabled()) {
            this.method1025();
        }
    }

    private void method1009() {
        if (field3635 == null) {
            return;
        }
        Vec3d v1 = TriggerBot.I.player.getEyePos();
        Vec3d v2 = field3635.getEyePos();
        Vec3d v3 = v2.subtract(v1);
        double v4 = v3.length();
        if (v4 < field3650) {
            return;
        }
        int v6 = Math.max(1, (int)Math.ceil(v4 * field3651));
        HashSet<BlockPos> v7 = new HashSet<BlockPos>();
        for (int v8 = 0; v8 <= v6; ++v8) {
            double v9 = (double)v8 / (double)v6;
            BlockPos v11 = BlockPos.ofFloored((double)(v1.x + v3.x * v9), (double)(v1.y + v3.y * v9), (double)(v1.z + v3.z * v9));
            if (!v7.add(v11) || TriggerBot.I.world.getBlockState(v11).isAir()) continue;
            Vec3d v12 = v1.subtract(v11.toCenterPos());
            double v13 = Math.abs(v12.x);
            double v15 = Math.abs(v12.y);
            double v17 = Math.abs(v12.z);
            Direction v19 = v15 >= v13 && v15 >= v17 ? (v12.y > 0.0 ? Direction.UP : Direction.DOWN) : (v13 >= v17 ? (v12.x > 0.0 ? Direction.EAST : Direction.WEST) : (v12.z > 0.0 ? Direction.SOUTH : Direction.NORTH));
            TriggerBot.I.player.networkHandler.sendPacket((Packet)new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, v11, v19, 0));
            TriggerBot.I.player.networkHandler.sendPacket((Packet)new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, v11, v19, 0));
        }
    }

    @EventHandler(2)
    public void method1010(EventUpdate param1) {
        if (TriggerBot.I.player == null || TriggerBot.I.world == null || TriggerBot.I.interactionManager == null) {
            field3635 = null;
            return;
        }
        field3635 = this.method1024();
        if (field3635 == null) {
            this.field3652 = true;
        } else {
            boolean bl = this.field3652 = !TriggerRaytrace.O(TriggerBot.I.player.getYaw(), TriggerBot.I.player.getPitch(), ((Float)this.field3632.getValue()).floatValue(), (Entity)field3635);
        }
        PacketCriticals packetCriticals = (PacketCriticals)Minced.getInstance().getModuleManager().method187(PacketCriticals.class);
        if (packetCriticals == null || !packetCriticals.isEnabled()) {
            this.method1025();
        }
    }

    public BooleanSetting method1011() {
        return this.field3653;
    }

    public ClientState method1012() {
        return this.field3642;
    }

    public WeaponUtil method1013() {
        return this.field3648;
    }

    @Override
    public void onDisable() {
        this.field3637 = false;
        field3635 = null;
        super.onDisable();
    }

    public float method1014() {
        return this.field3649;
    }

    @EventHandler(2)
    public void method1015(EventSwapWorld param1) {
        field3635 = null;
        this.field3637 = false;
    }

    public BooleanSetting method1016() {
        return this.field3641;
    }

    public MultiBooleanSetting method1017() {
        return this.field3633;
    }

    public TriggerBot() {
        super(field3654, Il, Combat.Combat);
        this.field3632 = new SliderSetting(field3666, field3667, 2.0f, I1, I0);
        this.field3639 = new MultiBooleanSetting(cfr_renamed_404, new BooleanSetting(field3668, true), new BooleanSetting(field3669, false));
        this.field3634 = new BooleanSetting(field3670, true);
        this.field3641 = new BooleanSetting(field3671, true);
        this.field3638 = new BooleanSetting(I3, false, () -> (Boolean)this.field3634.getValue());
        this.field3636 = new BooleanSetting(field3672, false);
        this.field3653 = new BooleanSetting(field3673, true, () -> (Boolean)this.field3636.getValue() == false);
        this.field3644 = new BooleanSetting(field3674, false);
        this.field3676 = new BooleanSetting(field3675, false);
        this.field3640 = new BooleanSetting(field3677, false);
        this.field3642 = new ClientState();
        this.field3648 = new WeaponUtil();
        this.field3649 = field3678;
        this.field3682 = (int)MathUtil.method1864(field3680, field3681);
        this.addSettings(this.field3633, this.field3639, this.field3665, this.field3632, this.field3636, this.field3653, this.field3634, this.field3638, this.field3644, this.field3676, this.field3640, this.field3641);
    }

    public static void method1018() {
        field3635 = null;
    }

    public int method1019() {
        return this.field3682;
    }

    public ModeSetting method1021() {
        return this.field3665;
    }

    public BooleanSetting method1022() {
        return this.field3676;
    }

    public int method1023() {
        return this.field3679;
    }

    private LivingEntity method1024() {
        return StreamSupport.stream(TriggerBot.I.world.getEntities().spliterator(), false).filter(param0 -> param0 instanceof LivingEntity).map(param0 -> (LivingEntity)param0).filter(this::method990).filter(param1 -> TriggerRaytrace.O(TriggerBot.I.player.getYaw(), TriggerBot.I.player.getPitch(), ((Float)this.field3632.getValue()).floatValue(), (Entity)param1)).sorted(Comparator.comparingDouble(param0 -> param0.distanceTo((Entity)TriggerBot.I.player))).findFirst().orElse(null);
    }

    private void method1025() {
        boolean v1;
        if (field3635 == null || !field3635.isAlive()) {
            return;
        }
        if (TriggerBot.I.player == null || TriggerBot.I.world == null || TriggerBot.I.interactionManager == null) {
            return;
        }
        if (((Boolean)this.field3636.getValue()).booleanValue() && !TriggerRaytrace.method2401(TriggerBot.I.player.getYaw(), TriggerBot.I.player.getPitch(), ((Float)this.field3632.getValue()).floatValue(), (Entity)field3635, true)) {
            return;
        }
        if (this.field3652) {
            return;
        }
        Vec3d targetDelta = t0.cfr_renamed_214((Entity)field3635);
        if (targetDelta == null) {
            targetDelta = field3635.getBoundingBox().getCenter().subtract(TriggerBot.I.player.getEyePos());
        }
        if (!this.method1006() || targetDelta.length() > (double)((Float)this.field3632.getValue()).floatValue()) {
            return;
        }
        if (((Boolean)this.field3640.getValue()).booleanValue()) {
            ++this.field3679;
            if (this.field3679 >= this.field3682) {
                this.field3682 = (int)MathUtil.method1864(I6, I9);
                this.field3679 = 0;
                TriggerBot.I.player.swingHand(Hand.MAIN_HAND);
                return;
            }
        }
        if (!((Boolean)this.field3636.getValue()).booleanValue() && ((Boolean)this.field3653.getValue()).booleanValue() && !TriggerBot.I.player.canSee((Entity)field3635)) {
            this.method1009();
        }
        boolean bl = v1 = TriggerBot.I.player.isTouchingWater() || TriggerBot.I.player.isInLava() || TriggerBot.I.player.isSwimming() || TriggerBot.I.player.isGliding();
        if (!v1 && TriggerBot.I.player.isSprinting()) {
            if (this.field3665.getSelectedIndex() == 0) {
                if (!(TriggerBot.I.player instanceof IClientPlayerEntityMixin)) {
                    return;
                }
                IClientPlayerEntityMixin playerAccessor = (IClientPlayerEntityMixin)TriggerBot.I.player;
                playerAccessor.setLastSprinting(false);
                TriggerBot.I.player.setSprinting(false);
                TriggerBot.I.player.networkHandler.sendPacket((Packet)new ClientCommandC2SPacket((Entity)TriggerBot.I.player, ClientCommandC2SPacket.Mode.STOP_SPRINTING));
            }
            this.field3637 = true;
            if (TriggerBot.I.player instanceof IClientPlayerEntityMixin && ((IClientPlayerEntityMixin)TriggerBot.I.player).getLastSprinting()) {
                return;
            }
        }
        this.method1007();
        TriggerBot.I.interactionManager.attackEntity((PlayerEntity)TriggerBot.I.player, (Entity)field3635);
        ElytraResolver v2 = (ElytraResolver)Minced.getInstance().getModuleManager().method187(ElytraResolver.class);
        if (v2 != null && v2.isEnabled()) {
            v2.method264(field3635);
        }
        TriggerBot.I.player.swingHand(Hand.MAIN_HAND);
        if (((Boolean)this.field3641.getValue()).booleanValue()) {
            this.method1005();
        }
        this.method997();
        this.field3649 = ThreadLocalRandom.current().nextFloat(I_, cfr_renamed_405);
        this.field3642.method3114();
    }

    public static LivingEntity method1026() {
        return field3635;
    }

    public boolean method1027() {
        return this.field3652;
    }

    static {
        field3654 = "Trigger Bot";
        Il = "Автоматически атакует существ в радиусе";
        field3655 = "Кого атаковать";
        field3656 = "Игроков";
        field3657 = "Друзей";
        field3658 = "Голых";
        field3659 = "Жителей";
        field3660 = "Животных";
        field3661 = "Мобов";
        field3662 = "Сброс спринта";
        field3663 = "Легит";
        I4 = "Рейдж";
        field3664 = "Легит";
        field3666 = "Радиус атаки";
        field3667 = 3.0f;
        I1 = 5.0f;
        I0 = 0.1f;
        cfr_renamed_404 = "Не бить если";
        field3668 = "Открыт контейнер";
        field3669 = "Используешь еду";
        field3670 = "Бить только критами";
        field3671 = "Ломать щит";
        I3 = "Только при зажатом пробеле";
        field3672 = "Не бить через стены";
        field3673 = "Обход бить через стены ReallyWorld";
        field3674 = "Синхронизация с ТПС";
        field3675 = "Обход идеального FallDistance";
        field3677 = "Обход идеальной атаки";
        field3678 = 0.95f;
        field3680 = 30.0f;
        field3681 = 41.0f;
        I6 = 30.0f;
        I9 = 41.0f;
        I_ = 0.88f;
        cfr_renamed_405 = 0.94f;
        field3643 = 460L;
        I8 = 0.5f;
        field3645 = 0.9f;
        field3646 = "Используешь еду";
        field3647 = "Открыт контейнер";
        field3650 = 0.01;
        field3651 = 2.0;
        TriggerBot.method1018();
    }
}




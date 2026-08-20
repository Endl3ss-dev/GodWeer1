/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.brigadier.Command
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.network.OtherClientPlayerEntity
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.Entity$RemovalReason
 *  net.minecraft.entity.EquipmentSlot
 *  net.minecraft.entity.MovementType
 *  net.minecraft.entity.effect.StatusEffectInstance
 *  net.minecraft.entity.effect.StatusEffects
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.sound.SoundEvents
 *  net.minecraft.text.Style
 *  net.minecraft.text.Text
 *  net.minecraft.util.Formatting
 *  net.minecraft.util.math.Vec3d
 */
package minced.core;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.UUID;
import minced.core.EventUpdate;
import minced.core.EventAttack;
import minced.core.EventBus;
import minced.core.ChatUtil;
import minced.core.EventKey;
import minced.core.ClientCommand;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;

public class CommandFakePlayer
extends ClientCommand
 {
    OtherClientPlayerEntity field6951;
    static MinecraftClient I = MinecraftClient.getInstance();
    static float field6952;
    static float field6953;
    static float field6954;
    static float field6955;
    static float field6956;
    float field6957;
    static float field6958;
    float field6959;
    static float field6960;
    static String field6961 = "fakeplayer";
    static String field6962 = "fp";
    static CommandFakePlayer field6963;
    static String field6964;
    static UUID field6965;
    static double field6966;
    static float field6967;
    static double field6968;
    static String field6969 = "add";
    static String field6970 = "del";
    static String field6971 = "list";
    static String field6972;
    static String field6973;
    static String field6974;
    static String field6975;
    static String field6976;
    static String field6977;
    static int field6978;
    static int field6979;
    static int field6980;
    static float field6981;
    static float field6982;
    boolean field6983;
    static String field6984;
    static String field6985;
    static String field6986;

    @EventHandler(2)
    public void method1736(EventAttack param1) {
        if (this.field6951 == null || CommandFakePlayer.I.player == null) {
            return;
        }
        if (param1.method2471() == this.field6951 && this.field6951.hurtTime == 0) {
            CommandFakePlayer.I.world.playSound((PlayerEntity)CommandFakePlayer.I.player, this.field6951.getX(), this.field6951.getY(), this.field6951.getZ(), SoundEvents.ENTITY_PLAYER_HURT, SoundCategory.PLAYERS, 1.0f, 1.0f);
            if (CommandFakePlayer.I.player.fallDistance > 0.0f) {
                CommandFakePlayer.I.world.playSound((PlayerEntity)CommandFakePlayer.I.player, this.field6951.getX(), this.field6951.getY(), this.field6951.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, SoundCategory.PLAYERS, 1.0f, 1.0f);
            } else {
                CommandFakePlayer.I.world.playSound((PlayerEntity)CommandFakePlayer.I.player, this.field6951.getX(), this.field6951.getY(), this.field6951.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS, 1.0f, 1.0f);
            }
            this.field6951.hurtTime = 10;
            this.field6951.maxHurtTime = 10;
            float v2 = this.field6951.getHealth() + this.field6951.getAbsorptionAmount() - 2.0f;
            if (v2 <= 0.0f) {
                this.field6951.setHealth(field6952);
                this.field6951.setAbsorptionAmount(field6953);
                CommandFakePlayer.I.world.playSound((PlayerEntity)CommandFakePlayer.I.player, this.field6951.getX(), this.field6951.getY(), this.field6951.getZ(), SoundEvents.ITEM_TOTEM_USE, SoundCategory.PLAYERS, 1.0f, 1.0f);
            } else if (v2 > field6954) {
                this.field6951.setAbsorptionAmount(v2 - field6955);
                this.field6951.setHealth(field6956);
            } else {
                this.field6951.setAbsorptionAmount(0.0f);
                this.field6951.setHealth(v2);
            }
        }
    }

    @EventHandler(2)
    public void method1738(EventKey param1) {
        if (this.field6951 == null || CommandFakePlayer.I.currentScreen != null) {
            return;
        }
        int v2 = param1.method3595();
        boolean v3 = param1.method3599();
        if (v2 == 265) {
            this.field6957 = v3 ? 1.0f : 0.0f;
        } else if (v2 == 264) {
            this.field6957 = v3 ? field6958 : 0.0f;
        } else if (v2 == 263) {
            this.field6959 = v3 ? 1.0f : 0.0f;
        } else if (v2 == 262) {
            this.field6959 = v3 ? field6960 : 0.0f;
        }
    }

    public CommandFakePlayer() {
        super(field6961, field6962);
        field6963 = this;
    }

    public static void method1739() {
        field6965 = UUID.fromString(field6964);
    }

    @EventHandler(2)
    public void method1740(EventUpdate param1) {
        if (this.field6951 == null || CommandFakePlayer.I.player == null) {
            return;
        }
        if (this.field6957 != 0.0f || this.field6959 != 0.0f) {
            float v2 = CommandFakePlayer.I.player.getYaw();
            double v3 = field6966;
            double v5 = (double)this.field6959 * Math.cos(Math.toRadians(v2)) - (double)this.field6957 * Math.sin(Math.toRadians(v2));
            double v7 = (double)this.field6957 * Math.cos(Math.toRadians(v2)) + (double)this.field6959 * Math.sin(Math.toRadians(v2));
            Vec3d v9 = new Vec3d(v5 * v3, this.field6951.getVelocity().y, v7 * v3);
            this.field6951.setVelocity(v9);
            this.field6951.move(MovementType.SELF, v9);
            this.field6951.setSprinting(true);
            if (this.field6957 > 0.0f) {
                this.field6951.setYaw(v2);
            } else if (this.field6957 < 0.0f) {
                this.field6951.setYaw(v2 + field6967);
            }
        } else {
            this.field6951.setSprinting(false);
            this.field6951.setVelocity(0.0, this.field6951.getVelocity().y, 0.0);
        }
        if (!this.field6951.isOnGround()) {
            this.field6951.setVelocity(this.field6951.getVelocity().add(0.0, field6968, 0.0));
        }
    }

    public static com.mojang.brigadier.builder.LiteralArgumentBuilder Ж(String name) {
        return ClientCommand.method420(name);
    }

    @Override
    public void run(LiteralArgumentBuilder param12) {
        param12.executes(param0 -> {
            ChatUtil.method3308(Text.literal((String)field6972).setStyle(Style.EMPTY.withColor(Formatting.GRAY)), new Object[0]);
            ChatUtil.method3308(Text.literal((String)field6973).setStyle(Style.EMPTY.withColor(Formatting.GRAY)), new Object[0]);
            ChatUtil.method3308(Text.literal((String)field6974).setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY)), new Object[0]);
            return 1;
        });
        param12.then(CommandFakePlayer.Ж((String)field6969).executes(param1 -> 1));
        param12.then(CommandFakePlayer.Ж((String)field6970).executes(param1 -> 1));
        param12.then(CommandFakePlayer.Ж((String)field6971).executes(param1 -> 1));
    }

    public static CommandFakePlayer method1741() {
        return field6963;
    }

    public OtherClientPlayerEntity method1742() {
        return this.field6951;
    }

    public void method1744() {
        if (CommandFakePlayer.I.player == null || CommandFakePlayer.I.world == null) {
            ChatUtil.method3308(Text.literal((String)field6975).setStyle(Style.EMPTY.withColor(Formatting.RED)), new Object[0]);
            return;
        }
        if (this.field6951 != null) {
            this.field6951.remove(Entity.RemovalReason.DISCARDED);
            this.field6951 = null;
        }
        GameProfile v1 = new GameProfile(UUID.fromString(field6976), field6977);
        this.field6951 = new OtherClientPlayerEntity(CommandFakePlayer.I.world, v1);
        this.field6951.refreshPositionAndAngles(CommandFakePlayer.I.player.getX(), CommandFakePlayer.I.player.getY(), CommandFakePlayer.I.player.getZ(), CommandFakePlayer.I.player.getYaw(), CommandFakePlayer.I.player.getPitch());
        for (EquipmentSlot v5 : EquipmentSlot.values()) {
            this.field6951.equipStack(v5, CommandFakePlayer.I.player.getEquippedStack(v5).copy());
        }
        this.field6951.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, field6978, 2));
        this.field6951.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, field6979, 4));
        this.field6951.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, field6980, 1));
        this.field6951.setHealth(field6981);
        this.field6951.setAbsorptionAmount(field6982);
        CommandFakePlayer.I.world.addEntity((Entity)this.field6951);
        if (!this.field6983) {
            EventBus.method3013(this);
            this.field6983 = true;
        }
        ChatUtil.method3308(Text.literal((String)field6984).setStyle(Style.EMPTY.withColor(Formatting.GREEN)), new Object[0]);
    }

    public void method1747() {
        if (this.field6951 == null) {
            ChatUtil.method3308(Text.literal((String)field6985).setStyle(Style.EMPTY.withColor(Formatting.RED)), new Object[0]);
            return;
        }
        this.field6951.remove(Entity.RemovalReason.DISCARDED);
        this.field6951 = null;
        this.field6957 = 0.0f;
        this.field6959 = 0.0f;
        ChatUtil.method3308(Text.literal((String)field6986).setStyle(Style.EMPTY.withColor(Formatting.GREEN)), new Object[0]);
    }

    static {
        field6975 = "Мир не загружен!";
        field6976 = "66123666-6666-6666-6666-666666666600";
        field6977 = "FakePlayer";
        field6978 = 999999;
        field6979 = 999999;
        field6980 = 999999;
        field6981 = 20.0f;
        field6982 = 20.0f;
        field6984 = "Фейк игрок создан!";
        field6985 = "Фейк игрок не существует!";
        field6986 = "Фейк игрок удалён!";
        field6952 = 20.0f;
        field6953 = 20.0f;
        field6954 = 20.0f;
        field6955 = 20.0f;
        field6956 = 20.0f;
        field6958 = -1.0f;
        field6960 = -1.0f;
        field6966 = 0.2;
        field6967 = 180.0f;
        field6968 = -0.08;
        field6972 = ".fakeplayer add — создать фейк игрока";
        field6973 = ".fakeplayer del — удалить фейк игрока";
        field6974 = "Управление стрелками ← ↑ → ↓";
        field6964 = "66123666-6666-6666-6666-666666666600";
        CommandFakePlayer.method1739();
    }
}




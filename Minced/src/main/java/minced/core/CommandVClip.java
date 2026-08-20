/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.Command
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.BoolArgumentType
 *  com.mojang.brigadier.arguments.DoubleArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket$PositionAndOnGround
 */
package minced.core;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import minced.core.ClientCommand;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class CommandVClip
extends ClientCommand
 {
    static String field7507 = "vclip";
    static String field7508 = "clip";
    static MinecraftClient I = MinecraftClient.getInstance();
    static String field7509 = "y";
    static String field7510 = "y";
    static String field7511 = "extra";
    static String field7512;
    static String field7513;
    static String field7514;

    public CommandVClip() {
        super(field7507, field7508);
    }

    private void method1828(float param1, boolean param2) {
        if (param2) {
            int v3;
            for (v3 = 0; v3 < 10; ++v3) {
                CommandVClip.I.player.networkHandler.sendPacket((Packet)new PlayerMoveC2SPacket.PositionAndOnGround(CommandVClip.I.player.getX(), CommandVClip.I.player.getY(), CommandVClip.I.player.getZ(), false, false));
            }
            for (v3 = 0; v3 < 10; ++v3) {
                CommandVClip.I.player.networkHandler.sendPacket((Packet)new PlayerMoveC2SPacket.PositionAndOnGround(CommandVClip.I.player.getX(), CommandVClip.I.player.getY() + (double)param1, CommandVClip.I.player.getZ(), false, false));
            }
        }
        CommandVClip.I.player.setPosition(CommandVClip.I.player.getX(), CommandVClip.I.player.getY() + (double)param1, CommandVClip.I.player.getZ());
    }

    public static com.mojang.brigadier.builder.LiteralArgumentBuilder Ж(String name) {
        return ClientCommand.method420(name);
    }

    public static com.mojang.brigadier.builder.RequiredArgumentBuilder Ж(String name, com.mojang.brigadier.arguments.ArgumentType<?> type) {
        return ClientCommand.method417(name, type);
    }

    @Override
    public void run(LiteralArgumentBuilder param12) {
        param12.executes(param1 -> this.method419(param1));
        param12.then(CommandVClip.Ж((String)field7509, (ArgumentType)DoubleArgumentType.doubleArg()).executes(param1 -> {
            double v2 = (Double)param1.getArgument(field7514, Double.class);
            this.method1828((float)v2, false);
            return 1;
        }));
        param12.then(CommandVClip.Ж((String)field7510, (ArgumentType)DoubleArgumentType.doubleArg()).then(CommandVClip.Ж((String)field7511, (ArgumentType)BoolArgumentType.bool()).executes(param1 -> {
            double v2 = (Double)param1.getArgument(field7512, Double.class);
            boolean v4 = (Boolean)param1.getArgument(field7513, Boolean.class);
            this.method1828((float)v2, v4);
            return 1;
        })));
    }

    static {
        field7512 = "y";
        field7513 = "extra";
        field7514 = "y";
    }
}




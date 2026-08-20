/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.network.ClientCommandSource
 *  net.minecraft.client.network.ClientPlayNetworkHandler
 *  net.minecraft.client.network.ClientPlayerEntity
 *  net.minecraft.client.network.ClientPlayerInteractionManager
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.screen.ScreenHandler
 */
package minced.core;

import com.mojang.brigadier.CommandDispatcher;
import minced.core.ServiceRegistry;
import minced.core.BotView;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.screen.ScreenHandler;

public final class ChatCommandHandler
{
    public static ClientWorld method378(MinecraftClient param0) {
        BotView v1 = (BotView)ServiceRegistry.method2118(BotView.class);
        ClientWorld v2 = ChatCommandHandler.method387(v1) ? v1.method2200() : null;
        return v2 != null ? v2 : param0.world;
    }

    private ChatCommandHandler() {
    }

    public static boolean method379() {
        BotView v0 = (BotView)ServiceRegistry.method2118(BotView.class);
        return ChatCommandHandler.method387(v0);
    }

    public static boolean method380() {
        BotView v0 = (BotView)ServiceRegistry.method2118(BotView.class);
        return v0 != null && v0.method2198();
    }

    public static CommandDispatcher method381(MinecraftClient param0) {
        ClientPlayNetworkHandler v1 = ChatCommandHandler.method383(param0);
        return v1 != null ? v1.getCommandDispatcher() : null;
    }

    public static ClientCommandSource method382(MinecraftClient param0) {
        ClientPlayNetworkHandler v1 = ChatCommandHandler.method383(param0);
        return v1 != null ? v1.getCommandSource() : null;
    }

    public static ClientPlayNetworkHandler method383(MinecraftClient param0) {
        ClientPlayerEntity v1 = ChatCommandHandler.method385(param0);
        if (v1 != null && v1.networkHandler != null) {
            return v1.networkHandler;
        }
        return param0.getNetworkHandler();
    }

    public static ScreenHandler method384(MinecraftClient param0) {
        ClientPlayerEntity v1 = ChatCommandHandler.method385(param0);
        return v1 != null ? v1.currentScreenHandler : null;
    }

    public static ClientPlayerEntity method385(MinecraftClient param0) {
        BotView v1 = (BotView)ServiceRegistry.method2118(BotView.class);
        ClientPlayerEntity v2 = ChatCommandHandler.method387(v1) ? v1.method2217() : null;
        return v2 != null ? v2 : param0.player;
    }

    public static ClientPlayerInteractionManager method386(MinecraftClient param0) {
        BotView v1 = (BotView)ServiceRegistry.method2118(BotView.class);
        ClientPlayerInteractionManager v2 = ChatCommandHandler.method387(v1) ? v1.method2209() : null;
        return v2 != null ? v2 : param0.interactionManager;
    }

    private static boolean method387(BotView param0) {
        return param0 != null && param0.method2190();
    }
}




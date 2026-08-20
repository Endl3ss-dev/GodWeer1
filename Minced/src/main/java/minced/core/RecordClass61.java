package minced.core;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import sg.ec.tp;
import sg.ec.fyu;

public final class RecordClass61 {
    public final MinecraftClient client;
    public final fyu session;
    public final BotContext context;
    public final tp controller;
    public final ClientPlayerEntity player;
    public final ClientWorld world;
    public final ClientPlayerInteractionManager interactionManager;

    public RecordClass61(MinecraftClient client, fyu session, BotContext context, tp controller, ClientPlayerEntity player, ClientWorld world, ClientPlayerInteractionManager interactionManager) {
        this.client = client;
        this.session = session;
        this.context = context;
        this.controller = controller;
        this.player = player;
        this.world = world;
        this.interactionManager = interactionManager;
    }

    public MinecraftClient client() { return client; }
    public fyu session() { return session; }
    public BotContext context() { return context; }
    public tp controller() { return controller; }
    public ClientPlayerEntity player() { return player; }
    public ClientWorld world() { return world; }
    public ClientPlayerInteractionManager interactionManager() { return interactionManager; }
}



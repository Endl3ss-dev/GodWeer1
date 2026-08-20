/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen
 *  net.minecraft.client.network.ClientPlayerEntity
 *  net.minecraft.client.network.ClientPlayerInteractionManager
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 */
package minced.mixin.screen;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import minced.core.ChatCommandHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value={CreativeInventoryScreen.class})
public abstract class CreativeInventoryScreenMixin {
    @WrapOperation(method={"render", "handledScreenTick", "keyPressed", "mouseClicked", "drawForeground", "drawBackground", "setSelectedTab"}, at={@At(value="FIELD", target="Lnet/minecraft/client/MinecraftClient;player:Lnet/minecraft/client/network/ClientPlayerEntity;")}, require=0)
    private ClientPlayerEntity mincedbots$useActiveCreativePlayer(MinecraftClient param1, Operation<ClientPlayerEntity> param2) {
        ClientPlayerEntity v3 = ChatCommandHandler.method385(param1);
        return v3 != null ? v3 : (ClientPlayerEntity)param2.call(new Object[]{param1});
    }

    @WrapOperation(method={"render", "handledScreenTick", "keyPressed", "mouseClicked", "drawForeground", "drawBackground", "setSelectedTab"}, at={@At(value="FIELD", target="Lnet/minecraft/client/MinecraftClient;interactionManager:Lnet/minecraft/client/network/ClientPlayerInteractionManager;")}, require=0)
    private ClientPlayerInteractionManager mincedbots$useActiveCreativeInteractionManager(MinecraftClient param1, Operation<ClientPlayerInteractionManager> param2) {
        ClientPlayerInteractionManager v3 = ChatCommandHandler.method386(param1);
        return v3 != null ? v3 : (ClientPlayerInteractionManager)param2.call(new Object[]{param1});
    }
}




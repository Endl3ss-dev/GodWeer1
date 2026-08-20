/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.network.ClientPlayNetworkHandler
 *  net.minecraft.client.network.WorldLoadingState
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.client.world.ClientWorld$Properties
 *  net.minecraft.registry.RegistryKey
 *  net.minecraft.world.World
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 *  org.spongepowered.asm.mixin.gen.Invoker
 */
package minced.mixin.accessor;
import java.util.Set;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.WorldLoadingState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value={ClientPlayNetworkHandler.class})
public interface ClientPlayNetworkHandlerAccessor {
    @Accessor(value="world")
    public void setWorld(ClientWorld var1);

    @Accessor(value="worldProperties")
    public void setWorldProperties(ClientWorld.Properties var1);

    @Accessor(value="worldKeys")
    public void setWorldKeys(Set<RegistryKey<World>> var1);

    @Accessor(value="chunkLoadDistance")
    public void setChunkLoadDistance(int var1);

    @Accessor(value="simulationDistance")
    public void setSimulationDistance(int var1);

    @Accessor(value="worldLoadingState")
    public void setWorldLoadingState(WorldLoadingState var1);

    @Accessor(value="secureChatEnforced")
    public void setSecureChatEnforced(boolean var1);

    @Invoker(value="sendAcknowledgment")
    public void invokeSendAcknowledgment();
}




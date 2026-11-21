package releon.ru.mixin.accessor;

import java.util.Set;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.world.ClientWorld.Properties;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({ClientPlayNetworkHandler.class})
public interface ClientPacketListenerAccessor {
   @Accessor("world")
   void releon$setLevel(ClientWorld var1);

   @Accessor("worldProperties")
   void releon$setLevelData(Properties var1);

   @Accessor("worldProperties")
   Properties releon$getLevelData();

   @Accessor("chunkLoadDistance")
   void releon$setServerChunkRadius(int var1);

   @Accessor("chunkLoadDistance")
   int releon$getServerChunkRadius();

   @Accessor("simulationDistance")
   void releon$setServerSimulationDistance(int var1);

   @Accessor("simulationDistance")
   int releon$getServerSimulationDistance();

   @Accessor("worldKeys")
   void releon$setLevels(Set<RegistryKey<World>> var1);
}

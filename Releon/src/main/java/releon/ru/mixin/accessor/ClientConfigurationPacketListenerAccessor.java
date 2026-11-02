package releon.ru.mixin.accessor;

import net.minecraft.client.gui.hud.ChatHud.ChatState;
import net.minecraft.client.network.ClientConfigurationNetworkHandler;
import net.minecraft.client.network.ClientRegistries;
import net.minecraft.client.resource.ClientDataPackManager;
import net.minecraft.registry.DynamicRegistryManager.Immutable;
import net.minecraft.resource.featuretoggle.FeatureSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({ClientConfigurationNetworkHandler.class})
public interface ClientConfigurationPacketListenerAccessor {
   @Accessor("clientRegistries")
   ClientRegistries releon$getRegistryDataCollector();

   @Accessor("registryManager")
   Immutable releon$getReceivedRegistries();

   @Accessor("enabledFeatures")
   FeatureSet releon$getEnabledFeatures();

   @Accessor("dataPackManager")
   ClientDataPackManager releon$getKnownPacks();

   @Accessor("chatState")
   ChatState releon$getChatState();
}

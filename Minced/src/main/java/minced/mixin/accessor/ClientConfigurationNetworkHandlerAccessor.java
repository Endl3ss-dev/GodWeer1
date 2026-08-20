/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.hud.ChatHud$ChatState
 *  net.minecraft.client.network.ClientConfigurationNetworkHandler
 *  net.minecraft.client.network.ClientRegistries
 *  net.minecraft.client.resource.ClientDataPackManager
 *  net.minecraft.resource.featuretoggle.FeatureSet
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package minced.mixin.accessor;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.network.ClientConfigurationNetworkHandler;
import net.minecraft.client.network.ClientRegistries;
import net.minecraft.client.resource.ClientDataPackManager;
import net.minecraft.resource.featuretoggle.FeatureSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={ClientConfigurationNetworkHandler.class})
public interface ClientConfigurationNetworkHandlerAccessor {
    @Accessor(value="enabledFeatures")
    public FeatureSet getEnabledFeaturesValue();

    @Accessor(value="clientRegistries")
    public ClientRegistries getClientRegistriesValue();

    @Accessor(value="dataPackManager")
    public ClientDataPackManager getDataPackManagerValue();

    @Accessor(value="chatState")
    public ChatHud.ChatState getChatStateValue();
}


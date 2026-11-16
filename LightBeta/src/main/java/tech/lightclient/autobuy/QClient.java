package tech.lightclient.autobuy;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;

/** Minecraft access adapter for the Winlend AutoBuy port. */
public interface QClient {
    MinecraftClient mc = MinecraftClient.getInstance();
    Window mw = mc.getWindow();
}

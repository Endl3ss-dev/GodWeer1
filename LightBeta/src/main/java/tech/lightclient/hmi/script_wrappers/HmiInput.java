package tech.lightclient.hmi.script_wrappers;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;

/**
 * Small, Lua-safe bridge for querying configured HMI keys.
 *
 * <p>The original resource-pack scripts used {@code luajava.bindClass} to get
 * KeyBindManager. That binding is deliberately unavailable in the embedded
 * Lua runtime, so trying to inspect an item disabled the corresponding script
 * cache on its first frame.</p>
 */
public class HmiInput {
    public boolean isKeyPressed(int keyCode) {
        MinecraftClient client = MinecraftClient.getInstance();
        return keyCode > 0
                && client != null
                && client.getWindow() != null
                && InputUtil.isKeyPressed(client.getWindow(), keyCode);
    }
}

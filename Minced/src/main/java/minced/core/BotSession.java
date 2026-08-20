/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 */
package minced.core;

import java.util.UUID;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import minced.core.BotAction;

public interface BotSession
{
    public BotAction method56();

    default ClientPlayerEntity player() { return null; }
    default MinecraftClient client() { return MinecraftClient.getInstance(); }
    default ClientPlayerInteractionManager interactionManager() { return null; }

    default public void method57(String param1) {
        if (param1 == null || param1.isBlank()) {
            return;
        }
        if (param1.startsWith("/")) {
            this.method59(param1);
            return;
        }
        throw new UnsupportedOperationException("Plain chat is not supported for this bot session");
    }

    public boolean method58();

    public void method59(String var1);

    public void method60(BotAction var1);

    public UUID method61();

    public String method62();

    public void method63();

    public void method64(String var1);

    default public boolean method65() {
        return this.method58();
    }

    default public String method66() {
        return null;
    }

    default public String method67() {
        return this.method58() ? "online" : "offline";
    }
}




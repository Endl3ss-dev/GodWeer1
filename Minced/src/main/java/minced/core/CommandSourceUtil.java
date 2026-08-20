/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 */
package minced.core;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import minced.core.Minced;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.client.network.ClientPlayNetworkHandler;

public interface CommandSourceUtil {
    public void method421(String var1);

    public static void method423(String param1) {
        method422().method421(param1);
    }

    public static CommandSourceUtil method422() {
        return param0 -> {
            if (param0 == null || param0.isBlank()) {
                return;
            }
            String v1 = param0.trim();
            MinecraftClient v2 = MinecraftClient.getInstance();
            ClientPlayNetworkHandler v3 = v2.getNetworkHandler();
            ClientCommandSource v4 = new ClientCommandSource(v3, v2);
            String v5 = Minced.getInstance().getCommandManager().method74();
            String v6 = v1.startsWith(v5) ? v1.substring(v5.length()) : v1;
            try {
                Minced.getInstance().getCommandManager().method72().execute(v6, (Object)v4);
            } catch (CommandSyntaxException e) {
                e.printStackTrace();
            }
        };
    }
}



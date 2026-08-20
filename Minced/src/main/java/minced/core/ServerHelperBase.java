/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.item.Items
 */
package minced.core;

import java.util.List;
import java.util.function.BooleanSupplier;
import minced.core.ChatUtil;
import minced.core.InventoryUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;

public abstract class ServerHelperBase {
    static MinecraftClient I = MinecraftClient.getInstance();
    static String field7927;

    public static void method1962() {
        if (ServerHelperBase.I.player == null) {
            return;
        }
        if (InventoryUtil.method3336(Items.FIREWORK_STAR) == -1) {
            ChatUtil.method3308(field7927, new Object[0]);
        } else {
            InventoryUtil.method3325(Items.FIREWORK_STAR);
        }
    }

    public List method1894(boolean param1) {
        return List.of();
    }

    public abstract boolean method1888(int var1);

    public abstract void method1890();

    public abstract String method1895();

    public abstract boolean method1889();

    static {
        field7927 = "У вас отсутствует анти полет!";
    }
}




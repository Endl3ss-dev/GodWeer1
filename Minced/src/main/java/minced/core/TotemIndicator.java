/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 */
package minced.core;

import java.awt.Color;
import minced.core.HudComponent;
import minced.core.EventRender2D;
import minced.core.Fonts;
import minced.core.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class TotemIndicator
implements HudComponent {
    static MinecraftClient I = MinecraftClient.getInstance();
    static float field9908;

    @Override
    @EventHandler(2)
    public void method2818(EventRender2D param1) {
        int v2 = I.getWindow().getScaledWidth() / 2 - 8;
        int v3 = I.getWindow().getScaledHeight() - 55;
        int v4 = this.cfr_renamed_0();
        if (v4 > 0) {
            RenderUtil.method398(param1.method2997(), Items.TOTEM_OF_UNDYING.getDefaultStack(), v2, v3 - 1, 1.0f, -1, 1.0f);
            Fonts.I[16].method2708(param1.method2997(), String.valueOf(v4), (float)v2 - Fonts.I[16].method2719(String.valueOf(v4)) / 2.0f + field9908, v3 - 10, -1, true, true, true, true, Color.BLACK.getRGB());
        }
    }

    private int cfr_renamed_0() {
        int v1 = 0;
        for (int v2 = 0; v2 < TotemIndicator.I.player.getInventory().size(); ++v2) {
            ItemStack v3 = TotemIndicator.I.player.getInventory().getStack(v2);
            if (!v3.getItem().equals(Items.TOTEM_OF_UNDYING)) continue;
            ++v1;
        }
        return v1;
    }

    static {
        field9908 = 8.0f;
    }
}




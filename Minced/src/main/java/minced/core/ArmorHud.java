/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.item.ItemStack
 */
package minced.core;

import java.util.ArrayList;
import java.util.Objects;
import minced.core.HudComponent;
import minced.core.EventRender2D;
import minced.core.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;

public class ArmorHud
implements HudComponent {
    static MinecraftClient I = MinecraftClient.getInstance();
    static float field9922;
    static float field9923;
    static float field9924;
    static float field9925;
    static float field9926;
    static float field9927;

    @Override
    @EventHandler(2)
    public void method2818(EventRender2D param1) {
        DrawContext v2 = param1.method2997();
        ArrayList v3 = new ArrayList();
        Iterable iterable = ArmorHud.I.player.getArmorItems();
        Objects.requireNonNull(v3);
        iterable.forEach(v3::add);
        if (v3.isEmpty()) {
            return;
        }
        float v5 = field9922;
        float v6 = field9923;
        v2.getMatrices().push();
        if (ArmorHud.I.player.getAir() < ArmorHud.I.player.getMaxAir()) {
            v2.getMatrices().translate(0.0f, field9924, 0.0f);
        } else if (ArmorHud.I.player.getAbilities().creativeMode) {
            v2.getMatrices().translate(0.0f, field9925, 0.0f);
        }
        int v7 = I.getWindow().getScaledWidth() / 2 + 90;
        int v8 = I.getWindow().getScaledHeight() - 40;
        for (int v9 = 0; v9 < 4; ++v9) {
            ItemStack v10 = (ItemStack)v3.get(v9);
            if (v10.isEmpty()) continue;
            float v11 = (float)(v7 - 90) + v5 + (float)(v9 * 18);
            float v12 = (float)v8 + v6;
            RenderUtil.method398(v2, v10, v11 + field9926, v12, field9927, -1, 1.0f);
        }
        v2.getMatrices().pop();
    }

    static {
        field9922 = 20.0f;
        field9923 = -16.0f;
        field9924 = -10.0f;
        field9925 = 7.5f;
        field9926 = 0.5f;
        field9927 = 0.95f;
    }
}




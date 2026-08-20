/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.screen.DeathScreen
 *  net.minecraft.util.Formatting
 *  sLM.4ZXYEo
 */
package minced.module.misc;
import minced.core.EventHandler;

import java.lang.invoke.CallSite;
import minced.core.EventUpdate;
import minced.core.ChatUtil;
import minced.core.Module;
import minced.enums.Combat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.util.Formatting;
import sLM._4ZXYEo;

public class DeathCoords
extends Module {
    static String 衣р;

    static String field5755 = "Death Coords";
    static String field5756 = "Отправляет координаты при смерти";
    static MinecraftClient I = MinecraftClient.getInstance();

    public DeathCoords() {
        super(field5755, field5756, Combat.Miscellaneous);
    }

    @EventHandler(2)
    public void method1385(EventUpdate param1) {
        if (DeathCoords.I.player.getHealth() < 1.0f && DeathCoords.I.currentScreen instanceof DeathScreen) {
            int v2 = (int)DeathCoords.I.player.getX();
            int v3 = (int)DeathCoords.I.player.getY();
            int v4 = (int)DeathCoords.I.player.getZ();
            if (DeathCoords.I.player.deathTime < 1) {
                String v5 = _4ZXYEo.ЧЫ("makeConcatWithConstants", new Object[]{}, (String)String.valueOf(Formatting.GRAY), (int)v2, (int)v3, (int)v4, (String)String.valueOf(Formatting.RESET));
                ChatUtil.method3308(v5, new Object[0]);
            }
        }
    }

    static {
        衣р = "Координаты: \u0001X: \u0001 Y: \u0001 Z: \u0001\u0001";
    }
}




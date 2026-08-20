/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.screen.DeathScreen
 */
package minced.module.player;
import minced.core.EventHandler;

import java.util.function.BooleanSupplier;
import minced.core.BooleanSetting;
import minced.core.EventUpdate;
import minced.core.Minced;
import minced.core.Module;
import minced.enums.Combat;
import minced.module.combat.AttackAura;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DeathScreen;

public class AutoRespawn
extends Module {
    static MinecraftClient I = MinecraftClient.getInstance();
    BooleanSetting field6733 = new BooleanSetting(field6736, true);
    static String field6734;
    static String field6735;
    static String field6736;

    @EventHandler(2)
    public void method1621(EventUpdate param1) {
        if (!(AutoRespawn.I.currentScreen instanceof DeathScreen)) {
            return;
        }
        if (((Boolean)this.field6733.getValue()).booleanValue() && Minced.getInstance().getModuleManager().method187(AttackAura.class).isEnabled()) {
            Minced.getInstance().getModuleManager().method187(AttackAura.class).toggle();
        }
        AutoRespawn.I.player.requestRespawn();
        I.setScreen(null);
    }

    public AutoRespawn() {
        super(field6734, field6735, Combat.Player);
        this.addSettings(this.field6733);
    }

    static {
        field6734 = "Auto Respawn";
        field6735 = "Автоматически возраждает вас после смерти";
        field6736 = "Отключать AttackAura";
    }
}




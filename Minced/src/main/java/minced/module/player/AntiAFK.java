/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 */
package minced.module.player;
import minced.core.EventHandler;

import minced.core.MultiBooleanSetting;
import minced.core.BooleanSetting;
import minced.core.EventUpdate;
import minced.core.ClientState;
import minced.core.Module;
import minced.enums.Combat;
import net.minecraft.client.MinecraftClient;

public class AntiAFK
extends Module {
    static String е4;

    static String field3480 = "Anti AFK";
    static String field3481 = "Предотвращает кик сервером за AFK";
    static String field3482 = "Действия";
    static String field3483 = "Прыжок";
    static String field3484 = "Команда";
    static String field3485 = "Качание рукой";
    MultiBooleanSetting field3486 = new MultiBooleanSetting(field3482, new BooleanSetting(field3483, false), new BooleanSetting(field3484, true), new BooleanSetting(field3485, false));
    ClientState field3487 = new ClientState();
    static MinecraftClient I = MinecraftClient.getInstance();
    static double field3488;
    static String field3489;
    static String field3490;
    static String field3491;

    public AntiAFK() {
        super(field3480, field3481, Combat.Player);
        this.addSettings(this.field3486);
    }

    @EventHandler(2)
    public void method955(EventUpdate param1) {
        if (AntiAFK.I.player == null || AntiAFK.I.world == null) {
            return;
        }
        this.field3487.method3114();
    }

    @Override
    public void onDisable() {
        this.field3487.method3114();
        super.onDisable();
    }

    static {
        field3488 = 20000.0;
        field3489 = "Прыжок";
        field3490 = "Команда";
        е4 = "/\u0001";
        field3491 = "Качание рукой";
    }
}




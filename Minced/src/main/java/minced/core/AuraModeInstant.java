/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.entity.LivingEntity
 *  sg.ec.sd
 */
package minced.core;

import minced.core.AuraModeHandler;
import minced.module.combat.AttackAura;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import sg.ec.sd;

public class AuraModeInstant
implements AuraModeHandler {
    static String field10765 = "Instant";
    static MinecraftClient I = MinecraftClient.getInstance();

    @Override
    public String getName() {
        return field10765;
    }

    @Override
    public void updateRotation(AttackAura param1, LivingEntity param2) {
        if (AuraModeInstant.I.player == null || param2 == null) {
            return;
        }
        boolean v3 = param1.method782();
        sd.Ъ((LivingEntity)param2, (boolean)v3, (boolean)false, (int)param1.method731());
    }
}




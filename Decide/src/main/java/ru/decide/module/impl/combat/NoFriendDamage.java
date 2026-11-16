/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.module.impl.combat;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.Client;
import ru.decide.manager.event_impl.AttackEvent;
import ru.decide.manager.events.orbit.EventHandler;
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.module.api.ModuleInfo;

@ModuleInfo(name="No Friend Damage", desc="\u0423\u0431\u0438\u0440\u0430\u0435\u0442 \u0443\u0434\u0430\u0440\u044b \u043f\u043e \u0434\u0440\u0443\u0437\u044c\u044f\u043c", category=Category.UTILITIES)
@Environment(value=EnvType.CLIENT)
public class NoFriendDamage
extends Module {
    @EventHandler
    public void onEvent(AttackEvent event) {
        if (Client.get().friendManager().isFriend(event.getTarget().method_5477().getString())) {
            event.cancel();
        }
    }
}


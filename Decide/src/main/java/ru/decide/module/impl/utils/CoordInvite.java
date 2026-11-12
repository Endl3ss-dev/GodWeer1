/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2338
 */
package ru.decide.module.impl.utils;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2338;
import ru.decide.manager.event_impl.EventKey;
import ru.decide.manager.events.orbit.EventHandler;
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.module.api.ModuleInfo;
import ru.decide.module.api.settings.impl.BindSetting;
import ru.decide.module.api.settings.impl.StringSetting;

@ModuleInfo(name="Coord Invite", desc="\u041e\u0442\u043f\u0440\u0430\u0432\u043b\u044f\u0435\u0442 \u043a\u043e\u043e\u0440\u0434\u0438\u043d\u0430\u0442\u044b \u0438\u0433\u0440\u043e\u043a\u0443 \u043f\u043e \u0431\u0438\u043d\u0434\u0443", category=Category.UTILITIES)
@Environment(value=EnvType.CLIENT)
public class CoordInvite
extends Module {
    private static final long INVITE_COOLDOWN_MS = 400L;
    public BindSetting inviteBind = new BindSetting((Module)this, "\u0411\u0438\u043d\u0434 \u043f\u0440\u0438\u0433\u043b\u0430\u0448\u0435\u043d\u0438\u044f", -1);
    public StringSetting playerName = new StringSetting((Module)this, "\u041d\u0438\u043a \u0438\u0433\u0440\u043e\u043a\u0430", "Aka_xxx");
    private long lastInviteMs;

    @Override
    protected void onEnable() {
        this.lastInviteMs = 0L;
    }

    @EventHandler
    public void onKey(EventKey event) {
        if (CoordInvite.mc.field_1724 == null || CoordInvite.mc.field_1687 == null || CoordInvite.mc.field_1755 != null) {
            return;
        }
        if (CoordInvite.mc.field_1724.field_3944 == null) {
            return;
        }
        int key = this.inviteBind.get();
        if (key <= 0 || event.getKey() != key) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now - this.lastInviteMs < 400L) {
            return;
        }
        this.lastInviteMs = now;
        this.sendInvite();
    }

    private void sendInvite() {
        String nick;
        String string = nick = this.playerName.getValue() == null ? "" : this.playerName.getValue().trim();
        if (nick.isEmpty()) {
            return;
        }
        class_2338 pos = CoordInvite.mc.field_1724.method_24515();
        CoordInvite.mc.field_1724.field_3944.method_45730("msg " + nick + " " + pos.method_10263() + " " + pos.method_10264() + " " + pos.method_10260());
    }
}


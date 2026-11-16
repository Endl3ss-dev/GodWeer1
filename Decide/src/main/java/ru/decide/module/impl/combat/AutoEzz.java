/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1297
 *  net.minecraft.class_1657
 *  net.minecraft.class_1937
 *  net.minecraft.class_2596
 *  net.minecraft.class_2663
 *  net.minecraft.class_310
 */
package ru.decide.module.impl.combat;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_1937;
import net.minecraft.class_2596;
import net.minecraft.class_2663;
import net.minecraft.class_310;
import ru.decide.manager.event_impl.AttackEvent;
import ru.decide.manager.event_impl.EventPacket;
import ru.decide.manager.events.orbit.EventHandler;
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.module.api.ModuleInfo;
import ru.decide.module.api.settings.impl.BooleanSetting;
import ru.decide.module.api.settings.impl.StringSetting;

@ModuleInfo(name="AutoEzz", desc="\u041e\u0442\u043f\u0440\u0430\u0432\u043b\u044f\u0435\u0442 \u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u044f \u0432 \u0447\u0430\u0442 \u043f\u043e\u0441\u043b\u0435 \u0441\u043d\u044f\u0442\u0438\u044f \u0442\u043e\u0442\u0435\u043c\u0430 \u0438 \u0443\u0431\u0438\u0439\u0441\u0442\u0432", category=Category.UTILITIES)
@Environment(value=EnvType.CLIENT)
public final class AutoEzz
extends Module {
    private static final byte TOTEM_STATUS = 35;
    private static final byte DEATH_STATUS = 3;
    private static final long KILL_WINDOW_MS = 10000L;
    private static final long COOLDOWN_MS = 1000L;
    private final BooleanSetting writeOnTotem = new BooleanSetting((Module)this, "\u041f\u0438\u0441\u0430\u0442\u044c \u0437\u0430 \u0442\u043e\u0442\u0435\u043c", true);
    private final StringSetting textOnTotem = new StringSetting((Module)this, "\u0422\u0435\u043a\u0441\u0442 \u0437\u0430 \u0442\u043e\u0442\u0435\u043c", "\u043f\u043e\u043f\u043d\u0443\u043b \u0442\u043e\u0442\u0435\u043c");
    private final BooleanSetting writeOnKill = new BooleanSetting((Module)this, "\u041f\u0438\u0441\u0430\u0442\u044c \u0437\u0430 \u043a\u0438\u043b\u043b", true);
    private final StringSetting textOnKill = new StringSetting((Module)this, "\u0422\u0435\u043a\u0441\u0442 \u0437\u0430 \u043a\u0438\u043b\u043b", "\u0443\u0431\u0438\u0442");
    private final Map<UUID, Long> cooldowns = new HashMap<UUID, Long>();
    private UUID lastTargetUuid;
    private String lastTargetName;
    private long lastAttackTime;

    @Override
    protected void onDisable() {
        this.cooldowns.clear();
        this.lastTargetUuid = null;
        this.lastTargetName = null;
        this.lastAttackTime = 0L;
    }

    @EventHandler
    public void onAttack(AttackEvent e) {
        if (!this.isEnabled()) {
            return;
        }
        class_310 mc = class_310.method_1551();
        if (mc.field_1724 == null) {
            return;
        }
        class_1297 target = e.getTarget();
        if (!(target instanceof class_1657)) {
            return;
        }
        class_1657 player = (class_1657)target;
        if (player == mc.field_1724) {
            return;
        }
        this.lastTargetUuid = player.method_5667();
        this.lastTargetName = player.method_5477().getString();
        this.lastAttackTime = System.currentTimeMillis();
    }

    @EventHandler
    public void onPacket(EventPacket e) {
        class_2596<?> class_25962;
        if (!this.isEnabled()) {
            return;
        }
        class_310 mc = class_310.method_1551();
        if (mc.field_1724 == null || mc.field_1687 == null) {
            return;
        }
        if (!e.isSend() && (class_25962 = e.getPacket()) instanceof class_2663) {
            class_2663 packet = (class_2663)class_25962;
            this.handleIncomingPacket(packet, mc);
        }
    }

    private void handleIncomingPacket(class_2663 packet, class_310 mc) {
        class_1657 player;
        class_1297 entity;
        byte status = packet.method_11470();
        if (status == 35 && this.writeOnTotem.getValue().booleanValue()) {
            entity = packet.method_11469((class_1937)mc.field_1687);
            if (entity == null || !(entity instanceof class_1657)) {
                return;
            }
            player = (class_1657)entity;
            if (player == mc.field_1724) {
                return;
            }
            this.sendTrashTalk(player.method_5667(), player.method_5477().getString(), this.textOnTotem.getValue());
        }
        if (status == 3 && this.writeOnKill.getValue().booleanValue()) {
            entity = packet.method_11469((class_1937)mc.field_1687);
            if (entity == null || !(entity instanceof class_1657)) {
                return;
            }
            player = (class_1657)entity;
            if (player == mc.field_1724) {
                return;
            }
            if (this.lastTargetUuid != null && player.method_5667().equals(this.lastTargetUuid) && System.currentTimeMillis() - this.lastAttackTime < 10000L) {
                this.sendTrashTalk(player.method_5667(), player.method_5477().getString(), this.textOnKill.getValue());
                this.lastTargetUuid = null;
                this.lastTargetName = null;
            }
        }
    }

    private void sendTrashTalk(UUID uuid, String playerName, String message) {
        class_310 mc = class_310.method_1551();
        if (mc.field_1724 == null || mc.field_1724.field_3944 == null) {
            return;
        }
        if (message == null || message.isBlank()) {
            return;
        }
        if (playerName == null || playerName.isBlank()) {
            return;
        }
        long now = System.currentTimeMillis();
        Long lastTime = this.cooldowns.get(uuid);
        if (lastTime != null && now - lastTime < 1000L) {
            return;
        }
        this.cooldowns.put(uuid, now);
        mc.field_1724.field_3944.method_45729(message.trim());
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1294
 *  net.minecraft.class_1297
 *  net.minecraft.class_1657
 *  net.minecraft.class_1799
 *  net.minecraft.class_1935
 */
package ru.decide.module.impl.combat;

import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1294;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_1935;
import ru.decide.manager.event_impl.AttackEvent;
import ru.decide.manager.event_impl.EventUpdate;
import ru.decide.manager.event_impl.WorldLoadEvent;
import ru.decide.manager.events.orbit.EventHandler;
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.module.api.ModuleInfo;
import ru.decide.module.api.settings.impl.BooleanSetting;
import ru.decide.module.api.settings.impl.MultiBooleanSetting;
import ru.decide.module.api.settings.impl.SliderSetting;
import ru.decide.utils.notification.NotificationManager;
import ru.decide.utils.other.Instance;
import ru.decide.utils.other.UseCooldowns;

@ModuleInfo(name="Use Tracker", desc="\u041a\u0443\u043b\u0434\u0430\u0443\u043d\u044b \u0445\u0438\u043b\u043e\u043a \u0446\u0435\u043b\u0438 \u0438 \u0435\u0451 \u0431\u0430\u0444\u0444\u044b", category=Category.UTILITIES)
@Environment(value=EnvType.CLIENT)
public class UseTracker
extends Module {
    public final MultiBooleanSetting items;
    public final MultiBooleanSetting buffs;
    public final BooleanSetting notifications;
    public final SliderSetting forget;
    private UUID target;
    private String targetName;
    private long lastAttack;
    private boolean wasNausea;

    public static UseTracker getInstance() {
        return Instance.get(UseTracker.class);
    }

    public UseTracker() {
        this.items = new MultiBooleanSetting((Module)this, "\u041f\u0440\u0435\u0434\u043c\u0435\u0442\u044b", new BooleanSetting(UseCooldowns.Item.NOTCH.label, true), new BooleanSetting(UseCooldowns.Item.GAPPLE.label, true), new BooleanSetting(UseCooldowns.Item.HEAL.label, true), new BooleanSetting(UseCooldowns.Item.NAUSEA.label, true), new BooleanSetting(UseCooldowns.Item.CHORUS.label, true), new BooleanSetting(UseCooldowns.Item.KELP.label, false), new BooleanSetting(UseCooldowns.Item.SCRAP.label, false));
        this.buffs = new MultiBooleanSetting((Module)this, "\u0411\u0430\u0444\u0444\u044b \u0446\u0435\u043b\u0438", new BooleanSetting(UseCooldowns.Buff.STRENGTH.label, true), new BooleanSetting(UseCooldowns.Buff.SPEED.label, true), new BooleanSetting(UseCooldowns.Buff.RESISTANCE.label, true), new BooleanSetting(UseCooldowns.Buff.REGENERATION.label, true), new BooleanSetting(UseCooldowns.Buff.FIRE_RESISTANCE.label, true), new BooleanSetting(UseCooldowns.Buff.ABSORPTION.label, true));
        this.notifications = new BooleanSetting((Module)this, "\u0423\u0432\u0435\u0434\u043e\u043c\u043b\u0435\u043d\u0438\u044f", true);
        this.forget = new SliderSetting(this, "\u0417\u0430\u0431\u044b\u0432\u0430\u0442\u044c \u0446\u0435\u043b\u044c, \u0441\u0435\u043a", 60.0f, 10.0f, 300.0f, 10.0f);
        UseCooldowns.listen(this::onUse);
    }

    @Override
    protected void onDisable() {
        this.target = null;
        this.targetName = null;
        this.lastAttack = 0L;
        this.wasNausea = false;
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        this.onDisable();
        UseCooldowns.clear();
    }

    @EventHandler
    public void onAttack(AttackEvent event) {
        class_1657 player;
        class_1297 class_12972 = event.getTarget();
        if (!(class_12972 instanceof class_1657) || (player = (class_1657)class_12972) == UseTracker.mc.field_1724) {
            return;
        }
        this.target = player.method_5667();
        this.targetName = player.method_5477().getString();
        this.lastAttack = System.currentTimeMillis();
    }

    @EventHandler
    public void onUpdate(EventUpdate event) {
        class_1657 player;
        UseCooldowns.tick(event);
        if (UseTracker.mc.field_1724 == null || UseTracker.mc.field_1687 == null) {
            return;
        }
        boolean nausea = UseTracker.mc.field_1724.method_6059(class_1294.field_5916);
        if (nausea && !this.wasNausea && this.hasTarget() && (player = this.getTargetPlayer()) != null) {
            UseCooldowns.trigger(player, UseCooldowns.Item.NAUSEA);
        }
        this.wasNausea = nausea;
    }

    private void onUse(class_1657 player, UseCooldowns.Item item) {
        if (!this.isEnabled() || !this.notifications.getValue().booleanValue()) {
            return;
        }
        if (!player.method_5667().equals(this.target)) {
            return;
        }
        if (!this.items.getValue(item.label)) {
            return;
        }
        NotificationManager.send(player.method_5477().getString() + " \u2014 " + item.label, NotificationManager.Type.INFO, new class_1799((class_1935)item.icon));
    }

    public boolean hasTarget() {
        return this.isEnabled() && this.target != null && System.currentTimeMillis() - this.lastAttack < (long)(this.forget.getValue().floatValue() * 1000.0f);
    }

    public String getTargetName() {
        return this.targetName == null ? "" : this.targetName;
    }

    public UUID getTargetUuid() {
        return this.target;
    }

    public class_1657 getTargetPlayer() {
        return this.target == null || UseTracker.mc.field_1687 == null ? null : UseTracker.mc.field_1687.method_18470(this.target);
    }

    public int remaining(UseCooldowns.Item item) {
        if (!this.items.getValue(item.label)) {
            return 0;
        }
        return UseCooldowns.remaining(this.target, item);
    }
}


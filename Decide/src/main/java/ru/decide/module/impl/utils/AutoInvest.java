/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2561
 *  net.minecraft.class_266
 *  net.minecraft.class_268
 *  net.minecraft.class_269
 *  net.minecraft.class_270
 *  net.minecraft.class_8646
 *  net.minecraft.class_9011
 */
package ru.decide.module.impl.utils;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2561;
import net.minecraft.class_266;
import net.minecraft.class_268;
import net.minecraft.class_269;
import net.minecraft.class_270;
import net.minecraft.class_8646;
import net.minecraft.class_9011;
import ru.decide.manager.event_impl.EventKey;
import ru.decide.manager.event_impl.EventTick;
import ru.decide.manager.events.orbit.EventHandler;
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.module.api.ModuleInfo;
import ru.decide.module.api.settings.impl.BindSetting;
import ru.decide.module.api.settings.impl.ModeSetting;
import ru.decide.module.api.settings.impl.SliderSetting;
import ru.decide.module.api.settings.impl.StringSetting;
import ru.decide.utils.math.ChatUtils;
import ru.decide.utils.notification.NotificationManager;

@ModuleInfo(name="Auto Invest", desc="\u0410\u0432\u0442\u043e\u043c\u0430\u0442\u0438\u0447\u0435\u0441\u043a\u0438\u0439 \u0438\u043d\u0432\u0435\u0441\u0442\u0438\u0446\u0438\u0438 \u0432 \u043a\u043b\u0430\u043d \u043f\u043e \u0431\u0438\u043d\u0434\u0443 \u0438\u043b\u0438 \u0430\u0432\u0442\u043e-\u043f\u043e\u0440\u043e\u0433\u0443", category=Category.UTILITIES)
@Environment(value=EnvType.CLIENT)
public class AutoInvest
extends Module {
    public final ModeSetting mode = new ModeSetting((Module)this, "\u0420\u0435\u0436\u0438\u043c", "\u0411\u0438\u043d\u0434", "\u0410\u0432\u0442\u043e");
    public final BindSetting investKey = new BindSetting((Module)this, "\u041a\u043b\u0430\u0432\u0438\u0448\u0430 \u0438\u043d\u0432\u0435\u0441\u0442\u0430", -1).setVisible(() -> this.mode.is("\u0411\u0438\u043d\u0434"));
    public final SliderSetting bindPercent = new SliderSetting(this, "% \u043e\u0442 \u0431\u0430\u043b\u0430\u043d\u0441\u0430 (\u0411\u0438\u043d\u0434)", 50.0f, 1.0f, 100.0f, 1.0f).setVisible(() -> this.mode.is("\u0411\u0438\u043d\u0434"));
    public final StringSetting triggerAmount = new StringSetting((Module)this, "\u0422\u0440\u0438\u0433\u0433\u0435\u0440 \u043c\u043e\u043d\u0435\u0442", "10000000").setVisible(() -> this.mode.is("\u0410\u0432\u0442\u043e"));
    public final SliderSetting autoPercent = new SliderSetting(this, "% \u043e\u0442 \u0431\u0430\u043b\u0430\u043d\u0441\u0430 (\u0410\u0432\u0442\u043e)", 50.0f, 1.0f, 100.0f, 1.0f).setVisible(() -> this.mode.is("\u0410\u0432\u0442\u043e"));
    private int cooldownTicks = 0;

    @EventHandler
    public void onKey(EventKey e) {
        if (AutoInvest.mc.field_1724 == null || e.getKey() == -1) {
            return;
        }
        if (!this.mode.is("\u0411\u0438\u043d\u0434")) {
            return;
        }
        if (e.getKey() == this.investKey.get().intValue()) {
            int balance = this.getBalanceFromScoreboard();
            if (balance <= 0) {
                this.sendMsg("\u00a7c\u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u043e\u043f\u0440\u0435\u0434\u0435\u043b\u0438\u0442\u044c \u0431\u0430\u043b\u0430\u043d\u0441!");
                return;
            }
            int amount = (int)((float)balance * (this.bindPercent.getValue().floatValue() / 100.0f));
            if (amount <= 0) {
                this.sendMsg("\u00a7c\u0421\u0443\u043c\u043c\u0430 \u0438\u043d\u0432\u0435\u0441\u0442\u0430 = 0!");
                return;
            }
            AutoInvest.mc.field_1724.field_3944.method_45730("clan invest " + amount);
            this.sendMsg("\u00a7a\u0418\u043d\u0432\u0435\u0441\u0442\u0438\u0440\u043e\u0432\u0430\u043d\u043e \u00a7f" + amount + " \u00a7a\u043c\u043e\u043d\u0435\u0442 (" + this.bindPercent.getValue().intValue() + "% \u043e\u0442 " + balance + ")");
        }
    }

    @EventHandler
    public void onTick(EventTick e) {
        int trigger;
        if (AutoInvest.mc.field_1724 == null || AutoInvest.mc.field_1687 == null) {
            return;
        }
        if (!this.mode.is("\u0410\u0432\u0442\u043e")) {
            return;
        }
        if (this.cooldownTicks > 0) {
            --this.cooldownTicks;
            return;
        }
        int balance = this.getBalanceFromScoreboard();
        if (balance <= 0) {
            return;
        }
        try {
            String tv = this.triggerAmount.getValue().trim();
            trigger = tv.isEmpty() ? 0 : Integer.parseInt(tv);
        }
        catch (NumberFormatException ex) {
            return;
        }
        if (balance < trigger) {
            return;
        }
        int amount = (int)((float)balance * (this.autoPercent.getValue().floatValue() / 100.0f));
        if (amount <= 0) {
            return;
        }
        AutoInvest.mc.field_1724.field_3944.method_45730("clan invest " + amount);
        this.sendMsg("\u00a7a\u0410\u0432\u0442\u043e-\u0438\u043d\u0432\u0435\u0441\u0442: \u00a7f" + amount + " \u00a7a\u043c\u043e\u043d\u0435\u0442 (" + this.autoPercent.getValue().intValue() + "% \u043e\u0442 " + balance + ")");
        this.cooldownTicks = 100;
    }

    private int getBalanceFromScoreboard() {
        if (AutoInvest.mc.field_1687 == null || AutoInvest.mc.field_1724 == null) {
            return -1;
        }
        class_269 scoreboard = AutoInvest.mc.field_1687.method_8428();
        class_266 objective = scoreboard.method_1189(class_8646.field_45157);
        if (objective == null) {
            return -1;
        }
        for (class_9011 entry : scoreboard.method_1184(objective)) {
            String line = class_268.method_1142((class_270)scoreboard.method_1164(entry.comp_2127()), (class_2561)entry.method_55387()).getString();
            int balance = this.parseMoneyLine(line);
            if (balance <= 0) continue;
            return balance;
        }
        return -1;
    }

    private int parseMoneyLine(String line) {
        if (line == null) {
            return -1;
        }
        String text = line.replaceAll("\u00a7.", "").replaceAll("[\\u00A7].", "").toLowerCase();
        if (!text.contains("\u043c\u043e\u043d\u0435\u0442") && !text.contains("\u0431\u0430\u043b\u0430\u043d\u0441")) {
            return -1;
        }
        String digits = text.replaceAll("[^0-9]", "");
        if (digits.isEmpty()) {
            return -1;
        }
        try {
            return Integer.parseInt(digits);
        }
        catch (NumberFormatException ex) {
            return -1;
        }
    }

    private void sendMsg(String text) {
        ChatUtils.addChatMessage("\u00a76[AutoInvest] " + text);
        NotificationManager.send(text, NotificationManager.Type.INFO);
    }

    @Override
    public void onDisable() {
        this.cooldownTicks = 0;
        super.onDisable();
    }
}


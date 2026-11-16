/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2558
 *  net.minecraft.class_2558$class_10609
 *  net.minecraft.class_2558$class_10610
 *  net.minecraft.class_2561
 *  net.minecraft.class_2596
 *  net.minecraft.class_7439
 */
package ru.decide.module.impl.player;

import java.util.Arrays;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2558;
import net.minecraft.class_2561;
import net.minecraft.class_2596;
import net.minecraft.class_7439;
import ru.decide.Client;
import ru.decide.manager.event_impl.EventPacket;
import ru.decide.manager.event_impl.EventUpdate;
import ru.decide.manager.events.orbit.EventHandler;
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.module.api.ModuleInfo;
import ru.decide.module.api.settings.impl.BooleanSetting;
import ru.decide.module.api.settings.impl.ModeSetting;
import ru.decide.module.api.settings.impl.MultiBooleanSetting;

@ModuleInfo(name="Auto Accept", category=Category.UTILITIES, desc="\u0410\u0432\u0442\u043e\u043c\u0430\u0442\u0438\u0447\u0435\u0441\u043a\u0438 \u043f\u0440\u0438\u043d\u0438\u043c\u0430\u0435\u0442 \u0437\u0430\u043f\u0440\u043e\u0441\u044b \u043d\u0430 \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0430\u0446\u0438\u044e \u0438 \u0432 \u043a\u043b\u0430\u043d")
@Environment(value=EnvType.CLIENT)
public class AutoAccept
extends Module {
    public final MultiBooleanSetting type = new MultiBooleanSetting((Module)this, "\u041f\u0440\u0438\u043d\u0438\u043c\u0430\u0442\u044c", new BooleanSetting("\u0417\u0430\u043f\u0440\u043e\u0441 \u043d\u0430 \u0422\u041f", true), new BooleanSetting("\u0417\u0430\u043f\u0440\u043e\u0441 \u0432 \u043a\u043b\u0430\u043d", true));
    public final ModeSetting mode = new ModeSetting((Module)this, "\u041f\u0440\u0438\u043d\u0438\u043c\u0430\u0442\u044c \u0422\u041f \u043e\u0442", "\u0414\u0440\u0443\u0437\u0435\u0439", "\u0412\u0441\u0435\u0445").setVisible(() -> this.type.get("\u0417\u0430\u043f\u0440\u043e\u0441 \u043d\u0430 \u0422\u041f").getValue());
    public final BooleanSetting onlyFriends = new BooleanSetting("\u041f\u0440\u0438\u043d\u0438\u043c\u0430\u0442\u044c \u0437\u0430\u043f\u0440\u043e\u0441 \u0432 \u043a\u043b\u0430\u043d \u0442\u043e\u043b\u044c\u043a\u043e \u043e\u0442 \u0434\u0440\u0443\u0437\u0435\u0439", true).setVisible(() -> this.type.get("\u0417\u0430\u043f\u0440\u043e\u0441 \u0432 \u043a\u043b\u0430\u043d").getValue());
    private boolean canAccept;
    private boolean waitingForClan = false;
    private long clanInviteTime = 0L;
    private final String[] teleportMessages = new String[]{"has requested teleport", "\u043f\u0440\u043e\u0441\u0438\u0442 \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0438\u0440\u043e\u0432\u0430\u0442\u044c\u0441\u044f", "\u0445\u043e\u0447\u0435\u0442 \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0438\u0440\u043e\u0432\u0430\u0442\u044c\u0441\u044f \u043a \u0432\u0430\u043c", "\u043f\u0440\u043e\u0441\u0438\u0442 \u043a \u0432\u0430\u043c \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0438\u0440\u043e\u0432\u0430\u0442\u044c\u0441\u044f"};
    private final String[] clanMessage = new String[]{"\u043f\u0440\u0438\u0433\u043b\u0430\u0448\u0430\u0435\u0442 \u0432\u0430\u0441 \u0432 \u043a\u043b\u0430\u043d", "\u043f\u0440\u0438\u0433\u043b\u0430\u0448\u0430\u0435\u0442 \u0412\u0430\u0441 \u0432 \u043a\u043b\u0430\u043d", "invited you to clan"};

    @EventHandler
    public void onPacket(EventPacket packetEvent) {
        class_2596<?> class_25962;
        if (AutoAccept.mc.field_1724 != null && AutoAccept.mc.field_1687 != null && (class_25962 = packetEvent.getPacket()) instanceof class_7439) {
            class_7439 m = (class_7439)class_25962;
            class_2561 content = m.comp_763();
            String message = content.getString();
            if (this.waitingForClan) {
                if (System.currentTimeMillis() - this.clanInviteTime > 5000L) {
                    this.waitingForClan = false;
                } else {
                    String cmd = this.findClickCommand(content, "\u0412\u0441\u0442\u0443\u043f\u0438\u0442\u044c");
                    if (cmd != null) {
                        this.runCommand(cmd);
                        this.waitingForClan = false;
                        return;
                    }
                }
            }
            if (this.type.getValue("\u0417\u0430\u043f\u0440\u043e\u0441 \u043d\u0430 \u0422\u041f") && this.isTeleportMessage(message)) {
                if (this.mode.is("\u0412\u0441\u0435\u0445")) {
                    this.canAccept = true;
                } else {
                    String lowerMessage = message.toLowerCase();
                    for (String friendName : Client.get().friendManager().getFriends()) {
                        if (!lowerMessage.contains(friendName.toLowerCase())) continue;
                        this.canAccept = true;
                        break;
                    }
                }
            }
            if (this.type.getValue("\u0417\u0430\u043f\u0440\u043e\u0441 \u0432 \u043a\u043b\u0430\u043d") && this.isClanMessage(message)) {
                String playerName = this.extractName(message, "\u043f\u0440\u0438\u0433\u043b\u0430\u0448\u0430\u0435\u0442");
                if (playerName != null && !this.checkFriend(playerName)) {
                    return;
                }
                String cmd = this.findClickCommand(content, "\u0412\u0441\u0442\u0443\u043f\u0438\u0442\u044c");
                if (cmd != null) {
                    this.runCommand(cmd);
                } else {
                    this.waitingForClan = true;
                    this.clanInviteTime = System.currentTimeMillis();
                }
            }
        }
    }

    @EventHandler
    public void onTick(EventUpdate e) {
        if (this.canAccept && AutoAccept.mc.field_1724 != null) {
            AutoAccept.mc.field_1724.field_3944.method_45730("tpaccept");
            this.canAccept = false;
        }
    }

    private String findClickCommand(class_2561 text, String buttonText) {
        List siblings;
        if (text == null) {
            return null;
        }
        if (text.method_10866() != null && text.method_10866().method_10970() != null) {
            class_2558 ce = text.method_10866().method_10970();
            if (text.getString().contains(buttonText)) {
                if (ce instanceof class_2558.class_10609) {
                    class_2558.class_10609 runCommand = (class_2558.class_10609)ce;
                    return runCommand.comp_3506();
                }
                if (ce instanceof class_2558.class_10610) {
                    class_2558.class_10610 suggestCommand = (class_2558.class_10610)ce;
                    return suggestCommand.comp_3507();
                }
            }
        }
        if ((siblings = text.method_10855()) != null) {
            for (class_2561 sibling : siblings) {
                String cmd = this.findClickCommand(sibling, buttonText);
                if (cmd == null) continue;
                return cmd;
            }
        }
        return null;
    }

    private void runCommand(String cmd) {
        if (AutoAccept.mc.field_1724 == null || AutoAccept.mc.field_1724.field_3944 == null) {
            return;
        }
        if (cmd.startsWith("/")) {
            AutoAccept.mc.field_1724.field_3944.method_45730(cmd.substring(1));
        } else {
            AutoAccept.mc.field_1724.field_3944.method_45730(cmd);
        }
    }

    private boolean checkFriend(String playerName) {
        if (!this.onlyFriends.getValue().booleanValue()) {
            return true;
        }
        return Client.get().friendManager().isFriend(playerName);
    }

    private String extractName(String message, String keyword) {
        String clean = message.replaceAll("\u00a7.", "");
        int index = clean.indexOf(keyword);
        if (index <= 0) {
            return null;
        }
        String before = clean.substring(0, index).trim();
        int lastSpace = before.lastIndexOf(32);
        if (lastSpace >= 0) {
            return before.substring(lastSpace + 1).trim();
        }
        return before.trim();
    }

    private boolean isTeleportMessage(String message) {
        String lowerCaseMessage = message.toLowerCase();
        return Arrays.stream(this.teleportMessages).anyMatch(trigger -> lowerCaseMessage.contains(trigger.toLowerCase()));
    }

    private boolean isClanMessage(String message) {
        String lowerCaseMessage = message.toLowerCase();
        return Arrays.stream(this.clanMessage).anyMatch(trigger -> lowerCaseMessage.contains(trigger.toLowerCase()));
    }
}


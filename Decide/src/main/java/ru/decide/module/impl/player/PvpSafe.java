/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2596
 *  net.minecraft.class_2797
 *  net.minecraft.class_7472
 *  net.minecraft.class_9449
 */
package ru.decide.module.impl.player;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2596;
import net.minecraft.class_2797;
import net.minecraft.class_7472;
import net.minecraft.class_9449;
import ru.decide.manager.event_impl.EventPacket;
import ru.decide.manager.events.orbit.EventHandler;
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.module.api.ModuleInfo;
import ru.decide.utils.math.ServerUtil;
import ru.decide.utils.other.Instance;

@ModuleInfo(name="Pvp Safe", category=Category.UTILITIES, desc="\u0412\u043e \u0432\u0440\u0435\u043c\u044f \u041f\u0412\u041f \u0431\u043b\u043e\u043a\u0438\u0440\u0443\u0435\u0442 \u0432\u044b\u0445\u043e\u0434 \u0441 \u0441\u0435\u0440\u0432\u0435\u0440\u0430 \u0438 \u043a\u043e\u043c\u0430\u043d\u0434\u044b \u0432\u044b\u0445\u043e\u0434\u0430 (/hub /limbo /an /anarchy)")
@Environment(value=EnvType.CLIENT)
public class PvpSafe
extends Module {
    public static PvpSafe get() {
        return Instance.get(PvpSafe.class);
    }

    public static boolean isActive() {
        PvpSafe module = PvpSafe.get();
        return module != null && module.isEnabled() && ServerUtil.isPvp();
    }

    @EventHandler
    public void onPacket(EventPacket event) {
        if (!(this.isEnabled() && event.isSend() && ServerUtil.isPvp())) {
            return;
        }
        String text = null;
        class_2596<?> class_25962 = event.getPacket();
        if (class_25962 instanceof class_2797) {
            class_2797 packet = (class_2797)class_25962;
            text = packet.comp_945();
        } else {
            class_25962 = event.getPacket();
            if (class_25962 instanceof class_9449) {
                class_9449 packet = (class_9449)class_25962;
                text = packet.comp_2532();
            } else {
                class_25962 = event.getPacket();
                if (class_25962 instanceof class_7472) {
                    class_7472 packet = (class_7472)class_25962;
                    text = packet.comp_808();
                }
            }
        }
        if (text != null && PvpSafe.isBlocked(text)) {
            event.cancel();
        }
    }

    public static boolean isBlocked(String raw) {
        String cmd;
        if (raw == null || raw.isEmpty()) {
            return false;
        }
        String s = raw.toLowerCase().trim();
        if (s.startsWith("/")) {
            s = s.substring(1).trim();
        }
        if ((cmd = s.split("\\s+", 2)[0]).equals("hub") || cmd.equals("limbo") || cmd.equals("anarchy") || cmd.startsWith("an")) {
            return true;
        }
        return s.matches(".*\\b(hub|limbo|anarchy)\\b.*");
    }
}


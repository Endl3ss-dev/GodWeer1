/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1291
 *  net.minecraft.class_1293
 *  net.minecraft.class_1294
 *  net.minecraft.class_1309
 *  net.minecraft.class_1657
 *  net.minecraft.class_1792
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_1844
 *  net.minecraft.class_6880
 *  net.minecraft.class_9334
 */
package ru.decide.utils.other;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1291;
import net.minecraft.class_1293;
import net.minecraft.class_1294;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1844;
import net.minecraft.class_6880;
import net.minecraft.class_9334;
import ru.decide.manager.event_impl.EventUpdate;
import ru.decide.utils.annotation.IMinecraft;
import ru.decide.utils.notification.NotificationManager;

@Environment(value=EnvType.CLIENT)
public final class UseCooldowns
implements IMinecraft {
    private static final int USE_TOLERANCE = 8;
    public static boolean debug;
    private static final UseCooldowns INSTANCE;
    private static final Map<UUID, Map<Item, Long>> COOLDOWNS;
    private static final Map<UUID, Use> USING;
    private static final List<BiConsumer<class_1657, Item>> LISTENERS;
    private static EventUpdate lastTick;

    private UseCooldowns() {
    }

    public static void listen(BiConsumer<class_1657, Item> listener) {
        LISTENERS.add(listener);
    }

    public static void tick(EventUpdate event) {
        if (event == lastTick) {
            return;
        }
        lastTick = event;
        if (UseCooldowns.mc.field_1724 == null || UseCooldowns.mc.field_1687 == null) {
            return;
        }
        HashSet<UUID> seen = new HashSet<UUID>();
        for (class_1657 player : UseCooldowns.mc.field_1687.method_18456()) {
            if (player == UseCooldowns.mc.field_1724) continue;
            seen.add(player.method_5667());
            Use use = USING.computeIfAbsent(player.method_5667(), u -> new Use());
            boolean now = player.method_6115();
            if (now && !use.active) {
                class_1799 stack = player.method_6030();
                if (stack.method_7960()) {
                    stack = player.method_6047();
                }
                if (stack.method_7960()) {
                    stack = player.method_6079();
                }
                use.stack = stack.method_7972();
                use.ticks = 0;
            }
            if (now) {
                ++use.ticks;
            }
            if (!now && use.active) {
                int max;
                int n = max = use.stack.method_7960() ? 0 : use.stack.method_7935((class_1309)player);
                if (debug && max > 0) {
                    NotificationManager.send(player.method_5477().getString() + ": " + use.stack.method_7909().method_63680().getString() + " " + use.ticks + "/" + max, NotificationManager.Type.INFO);
                }
                if (max > 0 && use.ticks >= max - 8) {
                    UseCooldowns.consumed(player, use.stack);
                }
                use.stack = class_1799.field_8037;
                use.ticks = 0;
            }
            use.active = now;
        }
        USING.keySet().retainAll(seen);
        UseCooldowns.cleanup();
    }

    private static void consumed(class_1657 player, class_1799 stack) {
        if (stack.method_31574(class_1802.field_8367)) {
            UseCooldowns.trigger(player, Item.NOTCH);
        } else if (stack.method_31574(class_1802.field_8463)) {
            UseCooldowns.trigger(player, Item.GAPPLE);
        } else if (stack.method_31574(class_1802.field_8233)) {
            UseCooldowns.trigger(player, Item.CHORUS);
        } else if (stack.method_31574(class_1802.field_8551)) {
            UseCooldowns.trigger(player, Item.KELP);
        } else if (stack.method_31574(class_1802.field_22021)) {
            UseCooldowns.trigger(player, Item.SCRAP);
        } else if (stack.method_31574(class_1802.field_8574)) {
            class_1844 contents = (class_1844)stack.method_58694(class_9334.field_49651);
            if (contents == null) {
                return;
            }
            for (class_1293 effect : contents.method_57397()) {
                if (effect.method_5579().comp_349() == class_1294.field_5915.comp_349()) {
                    UseCooldowns.trigger(player, Item.HEAL);
                }
                if (effect.method_5579().comp_349() != class_1294.field_5916.comp_349()) continue;
                UseCooldowns.trigger(player, Item.NAUSEA);
            }
        }
    }

    public static void trigger(class_1657 player, Item item) {
        COOLDOWNS.computeIfAbsent(player.method_5667(), u -> new EnumMap(Item.class)).put(item, System.currentTimeMillis() + (long)item.seconds * 1000L);
        for (BiConsumer<class_1657, Item> listener : LISTENERS) {
            listener.accept(player, item);
        }
    }

    public static int remaining(UUID uuid, Item item) {
        if (uuid == null) {
            return 0;
        }
        Map<Item, Long> map = COOLDOWNS.get(uuid);
        if (map == null) {
            return 0;
        }
        Long end = map.get((Object)item);
        if (end == null) {
            return 0;
        }
        return (int)Math.max(0.0, Math.ceil((double)(end - System.currentTimeMillis()) / 1000.0));
    }

    public static Map<Item, Long> of(UUID uuid) {
        Map<Item, Long> map = uuid == null ? null : COOLDOWNS.get(uuid);
        return map == null ? Map.of() : map;
    }

    public static int players() {
        return COOLDOWNS.size();
    }

    public static String format(int seconds) {
        return String.format("%d:%02d", seconds / 60, seconds % 60);
    }

    private static void cleanup() {
        long now = System.currentTimeMillis();
        COOLDOWNS.values().forEach(map -> map.values().removeIf(end -> end <= now));
        COOLDOWNS.entrySet().removeIf(entry -> ((Map)entry.getValue()).isEmpty());
    }

    public static void clear() {
        COOLDOWNS.clear();
        USING.clear();
    }

    static {
        INSTANCE = new UseCooldowns();
        COOLDOWNS = new HashMap<UUID, Map<Item, Long>>();
        USING = new HashMap<UUID, Use>();
        LISTENERS = new ArrayList<BiConsumer<class_1657, Item>>();
    }

    @Environment(value=EnvType.CLIENT)
    private static class Use {
        boolean active;
        class_1799 stack = class_1799.field_8037;
        int ticks;

        private Use() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static enum Item {
        NOTCH("\u0427\u0430\u0440\u043a\u0430", 150, class_1802.field_8367),
        GAPPLE("\u0413\u0435\u043f\u043b", 30, class_1802.field_8463),
        HEAL("\u0425\u0438\u043b\u043a\u0430", 20, class_1802.field_8574),
        NAUSEA("\u0422\u043e\u0448\u043d\u043e\u0442\u043a\u0430", 60, class_1802.field_8574),
        CHORUS("\u0425\u043e\u0440\u0443\u0441", 20, class_1802.field_8233),
        KELP("\u041f\u043b\u0430\u0441\u0442", 20, class_1802.field_8551),
        SCRAP("\u0422\u0440\u0430\u043f\u043a\u0430", 15, class_1802.field_22021);

        public final String label;
        public final int seconds;
        public final class_1792 icon;

        private Item(String label, int seconds, class_1792 icon) {
            this.label = label;
            this.seconds = seconds;
            this.icon = icon;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static enum Buff {
        STRENGTH("\u0421\u0438\u043b\u0430", (class_6880<class_1291>)class_1294.field_5910, -43691),
        SPEED("\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c", (class_6880<class_1291>)class_1294.field_5904, -11141121),
        RESISTANCE("\u0420\u0435\u0437\u0438\u0441\u0442", (class_6880<class_1291>)class_1294.field_5907, -7829249),
        REGENERATION("\u0420\u0435\u0433\u0435\u043d\u0430", (class_6880<class_1291>)class_1294.field_5924, -43521),
        FIRE_RESISTANCE("\u041e\u0433\u043d\u0435\u0443\u043f\u043e\u0440", (class_6880<class_1291>)class_1294.field_5918, -22016),
        ABSORPTION("\u0410\u0431\u0441\u043e\u0440\u0431", (class_6880<class_1291>)class_1294.field_5898, -171);

        public final String label;
        public final class_6880<class_1291> effect;
        public final int color;

        private Buff(String label, class_6880<class_1291> effect, int color) {
            this.label = label;
            this.effect = effect;
            this.color = color;
        }
    }
}


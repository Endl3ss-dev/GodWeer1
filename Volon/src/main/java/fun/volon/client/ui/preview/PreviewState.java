package fun.volon.client.ui.preview;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Источник мок-данных для HUD-превью.
 * Когда active=true, виджеты тянут эффекты отсюда вместо mc.player.getStatusEffects().
 */
public final class PreviewState {

    private static volatile boolean active = false;
    private static final List<StatusEffectInstance> EFFECTS = new CopyOnWriteArrayList<>();

    private PreviewState() {}

    public static boolean isActive() {
        return active;
    }

    public static void setActive(boolean a) {
        active = a;
        if (!a) {
            EFFECTS.clear();
        }
    }

    public static List<StatusEffectInstance> getEffects() {
        return Collections.unmodifiableList(EFFECTS);
    }

    public static void addEffect(RegistryEntry<StatusEffect> type, int amplifier, int duration, boolean infinite) {
        if (type == null) return;
        removeEffect(type);
        int dur = infinite ? -1 : Math.max(1, duration);
        EFFECTS.add(new StatusEffectInstance(type, dur, Math.max(0, amplifier)));
    }

    public static void removeEffect(RegistryEntry<StatusEffect> type) {
        if (type == null) return;
        EFFECTS.removeIf(e -> e.getEffectType().value() == type.value());
    }

    public static void clearEffects() {
        EFFECTS.clear();
    }

    /**
     * Обновляет таймеры — вызывается каждый клиентский тик из HudPreviewScreen.
     */
    public static void tick() {
        if (!active) return;
        List<StatusEffectInstance> updated = new ArrayList<>(EFFECTS.size());
        boolean changed = false;
        for (StatusEffectInstance inst : EFFECTS) {
            if (inst.isInfinite()) {
                updated.add(inst);
                continue;
            }
            int remaining = inst.getDuration() - 1;
            if (remaining <= 0) {
                changed = true;
                continue;
            }
            updated.add(new StatusEffectInstance(inst.getEffectType(), remaining, inst.getAmplifier()));
            changed = true;
        }
        if (changed) {
            EFFECTS.clear();
            EFFECTS.addAll(updated);
        }
    }
}

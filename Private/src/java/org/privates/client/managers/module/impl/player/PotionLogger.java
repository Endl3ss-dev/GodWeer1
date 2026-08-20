package org.privates.client.managers.module.impl.player;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.text.TextFormatting;
import org.privates.client.api.annotations.Funtime;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.utils.chat.ChatUtil;

import java.util.HashMap;
import java.util.Map;

@Funtime
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "PotionLogger", category = Category.MISC, description = "Пишет какие последние были получены эффекты противника")
public class PotionLogger extends Module {

    private static final Map<Effect, String> EFFECT_TRANSLATIONS = new HashMap<>() {{
        put(Effects.SPEED, "Скорость");
        put(Effects.STRENGTH, "Сила");
        put(Effects.INSTANT_HEALTH, "Прилив Здоровья");
        put(Effects.RESISTANCE, "Сопротивление");
        put(Effects.HASTE, "Спешка");
        put(Effects.REGENERATION, "Регенерация");
        put(Effects.WITHER, "Иссушение");
        put(Effects.POISON, "Отравление");
    }};

    private final BooleanSetting speedSetting = new BooleanSetting(this, "Скорость", true);
    private final BooleanSetting strengthSetting = new BooleanSetting(this, "Сила", true);
    private final BooleanSetting instantHealthSetting = new BooleanSetting(this, "Прилив Здоровья", true);
    private final BooleanSetting resistanceSetting = new BooleanSetting(this, "Сопротивление", true);
    private final BooleanSetting hasteSetting = new BooleanSetting(this, "Спешка", true);
    private final BooleanSetting regenerationSetting = new BooleanSetting(this, "Регенерация", true);
    private final BooleanSetting witherSetting = new BooleanSetting(this, "Иссушение", true);
    private final BooleanSetting poisonSetting = new BooleanSetting(this, "Отравление", true);

    private final Map<String, Map<Effect, Integer>> playerPotionEffects = new HashMap<>();

    public PotionLogger() {

    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (mc.world == null || mc.player == null) return;

        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player) continue; // кто спросит зачем эта строчка А НАДО ТАК ПЕДИКИ УДАЛЯТЬ СЕБЯ ТО НУЖНО

            Map<Effect, Integer> currentEffects = new HashMap<>();
            for (EffectInstance activeEffect : player.getActivePotionEffects()) {
                Effect effect = activeEffect.getPotion();
                int amplifier = activeEffect.getAmplifier();
                currentEffects.put(effect, amplifier);
                Map<Effect, Integer> previousEffects = playerPotionEffects.getOrDefault(player.getName().getString(), new HashMap<>());
                if (isEffectEnabled(effect) &&
                        (!previousEffects.containsKey(effect) ||
                                previousEffects.get(effect) != amplifier)) {
                    String effectName = EFFECT_TRANSLATIONS.getOrDefault(effect, effect.getName());

                    String message = String.format("%s%s%s получил эффект %s%s%s (Уровень %d)", // можете заменить на свой текст
                            TextFormatting.RED, player.getName().getString(),
                            TextFormatting.WHITE,
                            TextFormatting.RED, effectName,
                            TextFormatting.WHITE,
                            amplifier + 1);

                    ChatUtil.addText(message);
                }
            }
            playerPotionEffects.put(player.getName().getString(), currentEffects);
        }
    }

    private boolean isEffectEnabled(Effect effect) {

        if (effect == Effects.SPEED) return speedSetting.getValue();
        if (effect == Effects.STRENGTH) return strengthSetting.getValue();
        if (effect == Effects.INSTANT_HEALTH) return instantHealthSetting.getValue();
        if (effect == Effects.RESISTANCE) return resistanceSetting.getValue();
        if (effect == Effects.HASTE) return hasteSetting.getValue();
        if (effect == Effects.REGENERATION) return regenerationSetting.getValue();
        if (effect == Effects.WITHER) return witherSetting.getValue();
        if (effect == Effects.POISON) return poisonSetting.getValue();
        return false;
    }
}
package org.privates.client.screen.notification;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Effects;
import org.privates.client.utils.render.color.ColorUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper class for notification color highlighting
 */
public class NotificationColors {

    private static final Map<Item, Integer> ITEM_COLORS = new HashMap<>();
    private static final Map<Effect, Integer> EFFECT_COLORS = new HashMap<>();

    static {
        // Item colors - ЯРКИЕ и ЗАМЕТНЫЕ цвета для каждого предмета
        ITEM_COLORS.put(Items.GOLDEN_APPLE, ColorUtil.getColor(0, 255, 255, 255)); // Яркий cyan
        ITEM_COLORS.put(Items.ENCHANTED_GOLDEN_APPLE, ColorUtil.getColor(255, 215, 0, 255)); // Золотой
        ITEM_COLORS.put(Items.TOTEM_OF_UNDYING, ColorUtil.getColor(255, 215, 0, 255)); // Золотой
        ITEM_COLORS.put(Items.ENDER_PEARL, ColorUtil.getColor(0, 255, 127, 255)); // Яркий зеленый
        ITEM_COLORS.put(Items.CHORUS_FRUIT, ColorUtil.getColor(218, 112, 214, 255)); // Яркий orchid
        ITEM_COLORS.put(Items.POTION, ColorUtil.getColor(186, 85, 211, 255)); // Яркий фиолетовый
        ITEM_COLORS.put(Items.SPLASH_POTION, ColorUtil.getColor(186, 85, 211, 255)); // Яркий фиолетовый
        ITEM_COLORS.put(Items.LINGERING_POTION, ColorUtil.getColor(186, 85, 211, 255)); // Яркий фиолетовый
        ITEM_COLORS.put(Items.EXPERIENCE_BOTTLE, ColorUtil.getColor(127, 255, 0, 255)); // Яркий зеленый
        ITEM_COLORS.put(Items.DIAMOND_SWORD, ColorUtil.getColor(0, 191, 255, 255)); // Яркий голубой
        ITEM_COLORS.put(Items.DIAMOND_AXE, ColorUtil.getColor(0, 191, 255, 255)); // Яркий голубой
        ITEM_COLORS.put(Items.BOW, ColorUtil.getColor(210, 105, 30, 255)); // Яркий коричневый
        ITEM_COLORS.put(Items.CROSSBOW, ColorUtil.getColor(210, 105, 30, 255)); // Яркий коричневый
        ITEM_COLORS.put(Items.SHIELD, ColorUtil.getColor(220, 220, 220, 255)); // Яркий серебряный
        ITEM_COLORS.put(Items.ELYTRA, ColorUtil.getColor(186, 85, 211, 255)); // Яркий фиолетовый
        ITEM_COLORS.put(Items.TRIDENT, ColorUtil.getColor(0, 255, 255, 255)); // Яркий бирюзовый

        // Effect colors - ЯРКИЕ цвета: положительные эффекты - зеленые/синие, отрицательные - красные/оранжевые
        EFFECT_COLORS.put(Effects.SPEED, ColorUtil.getColor(124, 255, 255, 255)); // Яркий голубой
        EFFECT_COLORS.put(Effects.SLOWNESS, ColorUtil.getColor(90, 150, 200, 255)); // Яркий синий
        EFFECT_COLORS.put(Effects.HASTE, ColorUtil.getColor(255, 255, 0, 255)); // Яркий желтый
        EFFECT_COLORS.put(Effects.MINING_FATIGUE, ColorUtil.getColor(150, 130, 50, 255)); // Яркий темно-желтый
        EFFECT_COLORS.put(Effects.STRENGTH, ColorUtil.getColor(255, 50, 50, 255)); // Яркий красный
        EFFECT_COLORS.put(Effects.INSTANT_HEALTH, ColorUtil.getColor(255, 50, 50, 255)); // Яркий красный
        EFFECT_COLORS.put(Effects.INSTANT_DAMAGE, ColorUtil.getColor(150, 20, 20, 255)); // Темно-красный
        EFFECT_COLORS.put(Effects.JUMP_BOOST, ColorUtil.getColor(34, 255, 76, 255)); // Яркий зеленый
        EFFECT_COLORS.put(Effects.NAUSEA, ColorUtil.getColor(170, 60, 150, 255)); // Яркий фиолетовый
        EFFECT_COLORS.put(Effects.REGENERATION, ColorUtil.getColor(255, 120, 220, 255)); // Яркий розовый
        EFFECT_COLORS.put(Effects.RESISTANCE, ColorUtil.getColor(220, 100, 80, 255)); // Яркий коричневый
        EFFECT_COLORS.put(Effects.FIRE_RESISTANCE, ColorUtil.getColor(255, 170, 0, 255)); // Яркий оранжевый
        EFFECT_COLORS.put(Effects.WATER_BREATHING, ColorUtil.getColor(46, 150, 255, 255)); // Яркий синий
        EFFECT_COLORS.put(Effects.INVISIBILITY, ColorUtil.getColor(180, 180, 200, 255)); // Яркий серый
        EFFECT_COLORS.put(Effects.BLINDNESS, ColorUtil.getColor(60, 60, 70, 255)); // Темно-серый
        EFFECT_COLORS.put(Effects.NIGHT_VISION, ColorUtil.getColor(50, 50, 255, 255)); // Яркий синий
        EFFECT_COLORS.put(Effects.HUNGER, ColorUtil.getColor(120, 180, 100, 255)); // Яркий зеленый
        EFFECT_COLORS.put(Effects.WEAKNESS, ColorUtil.getColor(120, 120, 120, 255)); // Серый
        EFFECT_COLORS.put(Effects.POISON, ColorUtil.getColor(100, 220, 60, 255)); // Яркий зеленый
        EFFECT_COLORS.put(Effects.WITHER, ColorUtil.getColor(100, 80, 70, 255)); // Темно-коричневый
        EFFECT_COLORS.put(Effects.HEALTH_BOOST, ColorUtil.getColor(255, 150, 50, 255)); // Яркий оранжевый
        EFFECT_COLORS.put(Effects.ABSORPTION, ColorUtil.getColor(50, 120, 255, 255)); // Яркий синий
        EFFECT_COLORS.put(Effects.SATURATION, ColorUtil.getColor(255, 50, 50, 255)); // Яркий красный
        EFFECT_COLORS.put(Effects.GLOWING, ColorUtil.getColor(200, 255, 130, 255)); // Яркий желто-зеленый
        EFFECT_COLORS.put(Effects.LEVITATION, ColorUtil.getColor(206, 255, 255, 255)); // Яркий cyan
        EFFECT_COLORS.put(Effects.LUCK, ColorUtil.getColor(80, 255, 80, 255)); // Яркий зеленый
        EFFECT_COLORS.put(Effects.UNLUCK, ColorUtil.getColor(255, 220, 100, 255)); // Яркий желтый
        EFFECT_COLORS.put(Effects.SLOW_FALLING, ColorUtil.getColor(206, 255, 255, 255)); // Яркий cyan
        EFFECT_COLORS.put(Effects.CONDUIT_POWER, ColorUtil.getColor(50, 255, 255, 255)); // Яркий cyan
        EFFECT_COLORS.put(Effects.DOLPHINS_GRACE, ColorUtil.getColor(136, 200, 255, 255)); // Яркий голубой
        EFFECT_COLORS.put(Effects.BAD_OMEN, ColorUtil.getColor(20, 150, 140, 255)); // Темный cyan
        EFFECT_COLORS.put(Effects.HERO_OF_THE_VILLAGE, ColorUtil.getColor(100, 255, 100, 255)); // Яркий зеленый
    }

    /**
     * Get highlight color for an item
     */
    public static Integer getItemColor(Item item) {
        return ITEM_COLORS.get(item);
    }

    /**
     * Get highlight color for an effect
     */
    public static Integer getEffectColor(Effect effect) {
        return EFFECT_COLORS.get(effect);
    }

    /**
     * Get default color for notification type
     */
    public static Integer getDefaultColor(NotificationType type) {
        switch (type) {
            case WARNING:
                return ColorUtil.getColor(255, 220, 0, 255); // Яркий желтый
            case ERROR:
                return ColorUtil.getColor(255, 85, 85, 255); // Яркий красный
            case INFO:
                return ColorUtil.getColor(100, 200, 255, 255); // Яркий голубой
            case EFFECT_END:
                return ColorUtil.getColor(255, 150, 150, 255); // Яркий светло-красный
            case ITEM_PICKUP:
                return ColorUtil.getColor(150, 255, 150, 255); // Яркий светло-зеленый
            case SWAP:
                return ColorUtil.getColor(150, 200, 255, 255); // Яркий светло-голубой
            default:
                return ColorUtil.getColor(255, 255, 255, 255); // Белый по умолчанию
        }
    }
}

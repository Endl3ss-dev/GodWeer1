package antileak.base.client.modules.impl.misc;

import antileak.base.api.events.EventLink;
import antileak.base.api.events.implement.EventUpdate;
import antileak.base.api.utils.color.ColorUtils;
import antileak.base.client.modules.Module;
import antileak.base.client.modules.settings.implement.BooleanSetting;
import antileak.base.client.modules.settings.implement.FloatSetting;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.math.Box;

import java.awt.Color;
import java.util.*;

public class PotionTracker extends Module {

    public static PotionTracker INSTANCE = new PotionTracker();

    private final FloatSetting radius = new FloatSetting("Радиус", 50.0f, 10.0f, 100.0f, 1.0f);
    private final BooleanSetting showHitChance = new BooleanSetting("Показывать шанс попадания", true);
    private final BooleanSetting showEffects = new BooleanSetting("Показывать эффекты", true);

    private static final double SPLASH_RADIUS = 4.0;
    private static final double SPLASH_HEIGHT = 2.0;
    private static final int GRAY = new Color(200, 200, 200).getRGB();
    private static final int WHITE = new Color(255, 255, 255).getRGB();
    private static final int DARK_GRAY = new Color(85, 85, 85).getRGB();
    private static final int GREEN = new Color(85, 255, 85).getRGB();
    private static final int RED = new Color(255, 85, 85).getRGB();

    private final Map<Integer, PotionData> trackedPotions = new HashMap<>();

    public PotionTracker() {
        super("PotionTracker", "Видит что накинули на игрока", ModuleCategory.MISC);
        addSettings(radius, showHitChance, showEffects);
    }

    @Override
    public void onDisable() {
        trackedPotions.clear();
        super.onDisable();
    }

    @EventLink
    public void onUpdate(EventUpdate event) {
        if (mc.world == null || mc.player == null) {
            trackedPotions.clear();
            return;
        }

        Set<Integer> currentPotions = new HashSet<>();
        double radiusValue = radius.getValue().doubleValue();

        // Сканируем все зелья в радиусе
        for (Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof PotionEntity potionEntity)) continue;

            double distance = mc.player.distanceTo(entity);
            if (distance > radiusValue) continue;

            int entityId = entity.getId();
            currentPotions.add(entityId);

            if (!trackedPotions.containsKey(entityId)) {
                ItemStack potionStack = potionEntity.getStack().copy();
                trackedPotions.put(entityId, new PotionData(
                        potionStack,
                        entity.getX(),
                        entity.getY(),
                        entity.getZ()
                ));
            } else {
                PotionData data = trackedPotions.get(entityId);
                data.lastX = entity.getX();
                data.lastY = entity.getY();
                data.lastZ = entity.getZ();
            }
        }

        // Проверяем какие зелья исчезли (разбились)
        Set<Integer> removedPotions = new HashSet<>(trackedPotions.keySet());
        removedPotions.removeAll(currentPotions);

        for (int entityId : removedPotions) {
            PotionData data = trackedPotions.get(entityId);
            handlePotionSplash(data);
            trackedPotions.remove(entityId);
        }
    }

    private void handlePotionSplash(PotionData data) {
        Box potionBox = new Box(
                data.lastX - SPLASH_RADIUS,
                data.lastY - SPLASH_HEIGHT,
                data.lastZ - SPLASH_RADIUS,
                data.lastX + SPLASH_RADIUS,
                data.lastY + SPLASH_HEIGHT,
                data.lastZ + SPLASH_RADIUS
        );

        List<LivingEntity> entities = mc.world.getEntitiesByClass(
                LivingEntity.class,
                potionBox,
                entity -> entity instanceof PlayerEntity
        );

        for (LivingEntity hitEntity : entities) {
            if (!(hitEntity instanceof PlayerEntity player)) continue;

            double dx = player.getX() - data.lastX;
            double dz = player.getZ() - data.lastZ;
            double distance = Math.sqrt(dx * dx + dz * dz);

            if (distance <= SPLASH_RADIUS) {
                ItemStack potionStack = data.stack;
                PotionContentsComponent potionContents = potionStack.get(DataComponentTypes.POTION_CONTENTS);

                if (potionContents == null) continue;

                double proximity = Math.max(0.0, 1.0 - distance / SPLASH_RADIUS);
                double hitChance = proximity * 100.0;

                int potionColorInt = potionContents.getColor();
                int potionColor = getColorFromInt(potionColorInt);

                String playerName = player.getName().getString();
                String potionName = potionStack.getName().getString();

                // Основное сообщение
                MutableText mainMessage = Text.literal("");
                mainMessage.append(gradientText("Punch", ColorUtils.getThemeColor(0), ColorUtils.getThemeColor(90)));
                mainMessage.append(Text.literal(" >> ").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(DARK_GRAY))));
                mainMessage.append(Text.literal(playerName).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(WHITE))));
                mainMessage.append(Text.literal(" получил ").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(GRAY))));
                mainMessage.append(Text.literal(potionName).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(potionColor))));

                mc.player.sendMessage(mainMessage, false);

                // Показываем шанс попадания
                if (showHitChance.isState()) {
                    MutableText hitMessage = Text.literal("");
                    hitMessage.append(Text.literal("• ").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(DARK_GRAY))));
                    hitMessage.append(Text.literal("Успешность: ").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(WHITE))));
                    hitMessage.append(Text.literal(String.format("%.0f%%", hitChance)).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(GREEN))));

                    mc.player.sendMessage(hitMessage, false);
                }

                // Показываем эффекты
                if (showEffects.isState() && potionContents.hasEffects()) {
                    for (StatusEffectInstance effect : potionContents.getEffects()) {
                        String effectName = effect.getEffectType().value().getName().getString();
                        int amplifier = effect.getAmplifier() + 1;
                        int duration = effect.getDuration() / 20;
                        int adjustedDuration = (int) (duration * (hitChance / 100.0));
                        int minutes = adjustedDuration / 60;
                        int seconds = adjustedDuration % 60;

                        MutableText effectMessage = Text.literal("");
                        effectMessage.append(Text.literal("• ").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(DARK_GRAY))));
                        effectMessage.append(Text.literal(effectName + " ").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(RED))));
                        effectMessage.append(Text.literal(toRoman(amplifier) + " ").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(RED))));
                        effectMessage.append(Text.literal("(" + minutes + ":" + String.format("%02d", seconds) + ")").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(GRAY))));

                        mc.player.sendMessage(effectMessage, false);
                    }
                }
            }
        }
    }

    private int getColorFromInt(int color) {
        int red = (color >> 16) & 0xFF;
        int green = (color >> 8) & 0xFF;
        int blue = color & 0xFF;

        if (red > green && red > blue) {
            return 0xFF5555; // RED
        } else if (green > red && green > blue) {
            return 0x55FF55; // GREEN
        } else if (blue > red && blue > green) {
            return 0x5555FF; // BLUE
        } else if (red > 240 && green > 100 && blue < 100) {
            return 0xFFAA00; // GOLD
        } else if (red > 174 && green < 100 && blue > 174) {
            return 0xFF55FF; // LIGHT_PURPLE
        } else if (red < 100 && green > 174 && blue > 174) {
            return 0x55FFFF; // AQUA
        } else {
            return 0xFFFFFF; // WHITE
        }
    }

    private String toRoman(int number) {
        return switch (number) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            case 6 -> "VI";
            case 7 -> "VII";
            case 8 -> "VIII";
            case 9 -> "IX";
            case 10 -> "X";
            default -> String.valueOf(number);
        };
    }

    private MutableText gradientText(String text, int startColor, int endColor) {
        MutableText result = Text.literal("");

        for (int i = 0; i < text.length(); i++) {
            float progress = text.length() <= 1 ? 0.0f : (float) i / (text.length() - 1);
            int color = ColorUtils.gradient(startColor, endColor, progress);
            result.append(Text.literal(String.valueOf(text.charAt(i)))
                    .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(color))));
        }

        return result;
    }

    private static class PotionData {
        private final ItemStack stack;
        private double lastX;
        private double lastY;
        private double lastZ;

        private PotionData(ItemStack stack, double lastX, double lastY, double lastZ) {
            this.stack = stack;
            this.lastX = lastX;
            this.lastY = lastY;
            this.lastZ = lastZ;
        }
    }
}

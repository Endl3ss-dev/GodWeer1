package godweer.utils.item;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;

public final class ConsumableItemUtil {

    public static final int NOT_FOUND = -1;
    public static final int OFFHAND_SLOT = 40;

    private ConsumableItemUtil() {
    }

    public static int findItemSlot(Player player, Item item) {
        if (player == null || item == null) {
            return NOT_FOUND;
        }

        if (player.getOffhandItem().is(item)) {
            return OFFHAND_SLOT;
        }

        Inventory inventory = player.getInventory();
        for (int slot = 0; slot < 36; slot++) {
            if (inventory.getItem(slot).is(item)) {
                return slot;
            }
        }
        return NOT_FOUND;
    }

    public static int findPotionSlot(Player player, Item potionItem, Holder<MobEffect> effect) {
        if (player == null || potionItem == null || effect == null) {
            return NOT_FOUND;
        }

        if (hasPotionEffect(player.getOffhandItem(), potionItem, effect)) {
            return OFFHAND_SLOT;
        }

        Inventory inventory = player.getInventory();
        for (int slot = 0; slot < 36; slot++) {
            if (hasPotionEffect(inventory.getItem(slot), potionItem, effect)) {
                return slot;
            }
        }
        return NOT_FOUND;
    }

    public static int findBestFoodSlot(Player player) {
        if (player == null) {
            return NOT_FOUND;
        }

        int hunger = player.getFoodData().getFoodLevel();
        int bestSlot = NOT_FOUND;
        float bestScore = -1.0F;

        ItemStack offhandStack = player.getOffhandItem();
        FoodProperties offhandFood = isApple(offhandStack) ? null : offhandStack.get(DataComponents.FOOD);
        if (isUsableFood(offhandFood, hunger)) {
            bestSlot = OFFHAND_SLOT;
            bestScore = foodScore(offhandFood);
        }

        Inventory inventory = player.getInventory();
        for (int slot = 0; slot < 36; slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (isApple(stack)) {
                continue;
            }
            FoodProperties food = stack.get(DataComponents.FOOD);
            if (!isUsableFood(food, hunger)) {
                continue;
            }

            float score = foodScore(food);
            if (score > bestScore) {
                bestSlot = slot;
                bestScore = score;
            }
        }
        return bestSlot;
    }

    private static boolean hasPotionEffect(ItemStack stack, Item potionItem, Holder<MobEffect> effect) {
        if (stack.isEmpty() || !stack.is(potionItem)) {
            return false;
        }

        PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
        if (contents == null) {
            return false;
        }

        for (MobEffectInstance instance : contents.getAllEffects()) {
            if (instance.getEffect().equals(effect)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isApple(ItemStack stack) {
        return stack.is(Items.GOLDEN_APPLE) || stack.is(Items.ENCHANTED_GOLDEN_APPLE);
    }

    private static boolean isUsableFood(FoodProperties food, int hunger) {
        return food != null && (hunger < 20 || food.canAlwaysEat());
    }

    private static float foodScore(FoodProperties food) {
        return food.nutrition() + food.saturation();
    }
}

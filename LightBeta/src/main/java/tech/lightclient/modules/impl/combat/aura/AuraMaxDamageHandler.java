package tech.lightclient.modules.impl.combat.aura;

import tech.lightclient.client.events.impl.client.EventPacket;
import tech.lightclient.client.utils.player.PlayerInventoryUtil;
import tech.lightclient.modules.impl.combat.AuraModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

/**
 * Свап предметов урона/хила в левую руку для "Максимального урона".
 * Главное отличие от старой версии: слоты НЕ кэшируются между шагами.
 * Каждый шаг ищет свой предмет заново по актуальному инвентарю, потому что
 * первый свап смещает предметы (хил, лежавший в левой руке, после свапа
 * урона оказывается на месте предмета урона).
 *
 * Цикл одного удара, всё в одном тике:
 * SWAP(урон -> оффхенд) -> атака -> SWAP(хил -> оффхенд).
 * Оба предмета проходят через левую руку подряд без задержек, поэтому
 * бафы обоих предметов складываются.
 */
public final class AuraMaxDamageHandler {
    private static final int OFFHAND_BUTTON = 40;
    private static final int NO_SLOT = -1;

    private AuraMaxDamageHandler() {
    }

    public static Swap prepare(MinecraftClient mc, AuraModule aura) {
        if (!aura.isMaximumDamageEnabled()
                || mc.player == null
                || mc.interactionManager == null
                || mc.player.currentScreenHandler != mc.player.playerScreenHandler) {
            return Swap.NONE;
        }

        long now = System.currentTimeMillis();

        String damageName = aura.getMaxDamageItemName();
        if (damageName != null && !damageName.trim().isEmpty()) {
            int damageSlot = findItemByName(mc, damageName);
            if (damageSlot != NO_SLOT) {
                int damageSlotId = toScreenHandlerId(damageSlot);
                PlayerInventoryUtil.clickSlot(damageSlotId, OFFHAND_BUTTON, SlotActionType.SWAP);
                return new Swap(damageSlotId, now);
            }

            // Предмет урона может уже стоять в левой руке после прерванного
            // цикла: он надет, свапать нечего, хил вернём сразу после удара
            if (matchesName(mc.player.getOffHandStack(), damageName)) {
                return new Swap(NO_SLOT, now);
            }

            return Swap.NONE;
        }

        // Имя не задано — старое поведение: лучший тотем/голова по урону
        int legacySlot = findBestSlot(mc);
        if (legacySlot == NO_SLOT) return Swap.NONE;

        int legacySlotId = toScreenHandlerId(legacySlot);
        PlayerInventoryUtil.clickSlot(legacySlotId, OFFHAND_BUTTON, SlotActionType.SWAP);
        return new Swap(legacySlotId, now);
    }

    public static boolean isReadyToAttack(MinecraftClient mc, Swap swap) {
        // Бьём сразу после свапа: пауза между свапом и ударом рвёт склейку бафов
        return swap != null && swap.isActive();
    }

    public static boolean isSwapStalled(MinecraftClient mc, Swap swap) {
        return false;
    }

    public static boolean isExpired(MinecraftClient mc, Swap swap) {
        return swap != null && swap.isActive()
                && System.currentTimeMillis() - swap.swappedAt >= 5000L;
    }

    public static void onPacket(EventPacket event, Swap swap) {
        // Подтверждения сервера не ждём: свапы уходят подряд в одном тике
    }

    /**
     * Мгновенно возвращает предмет здоровья в левую руку после удара.
     * Слот хила ищется ЗАНОВО: первый свап мог передвинуть его со своего
     * места (если хил был в левой руке, он сейчас стоит на месте предмета
     * урона). Поэтому старая логика с кэшированным слотом ломалась.
     */
    public static boolean processAfterAttack(MinecraftClient mc, Swap swap) {
        if (swap == null || !swap.isActive()
                || mc.player == null || mc.interactionManager == null) {
            return true;
        }

        sendRestore(mc, swap);
        return true;
    }

    public static boolean cancel(MinecraftClient mc, Swap swap) {
        if (swap == null || !swap.isActive()) return true;
        if (swap.restoreSent) return true;
        if (mc.player == null || mc.interactionManager == null) return false;

        sendRestore(mc, swap);
        return true;
    }

    private static void sendRestore(MinecraftClient mc, Swap swap) {
        if (swap.restoreSent) return;
        swap.restoreSent = true;

        String healName = healItemName();
        int healSlot = healName.isEmpty() ? NO_SLOT : findItemByName(mc, healName);

        if (healSlot != NO_SLOT) {
            // Свап хила в оффхенд: предмет урона автоматически возвращается
            // в инвентарь на место хила
            PlayerInventoryUtil.clickSlot(toScreenHandlerId(healSlot), OFFHAND_BUTTON, SlotActionType.SWAP);
        } else if (swap.damageSlotId != NO_SLOT) {
            // Хил не задан или пропал из инвентаря — просто откатываем первый свап
            PlayerInventoryUtil.clickSlot(swap.damageSlotId, OFFHAND_BUTTON, SlotActionType.SWAP);
        }
    }

    private static String healItemName() {
        AuraModule aura = AuraModule.getInstance();
        if (aura == null) return "";
        String name = aura.getMaxHealItemName();
        return name == null ? "" : name.trim();
    }

    private static int toScreenHandlerId(int inventoryIndex) {
        return inventoryIndex < 9 ? inventoryIndex + 36 : inventoryIndex;
    }

    private static int findBestSlot(MinecraftClient mc) {
        int selectedSlot = mc.player.getInventory().getSelectedSlot();
        int bestSlot = NO_SLOT;
        double bestDamage = 0.0;

        for (int inventoryIndex = 0; inventoryIndex < 36; inventoryIndex++) {
            if (inventoryIndex == selectedSlot) continue;

            ItemStack stack = mc.player.getInventory().getStack(inventoryIndex);
            if (!isTotemOrHead(stack)) continue;

            double damage = getAttackDamage(stack);
            if (damage > bestDamage) {
                bestDamage = damage;
                bestSlot = inventoryIndex;
            }
        }
        return bestSlot;
    }

    /**
     * Поиск по всему инвентарю (хотбар 0-8 + основной 9-35).
     * Левая рука тут не участвует: её содержимое всегда доступно через
     * getOffHandStack, и после первого свапа там лежит предмет урона.
     */
    private static int findItemByName(MinecraftClient mc, String searchName) {
        if (mc.player == null) return NO_SLOT;

        String query = normalize(searchName);
        if (query.isEmpty()) return NO_SLOT;

        int selectedSlot = mc.player.getInventory().getSelectedSlot();
        for (int inventoryIndex = 0; inventoryIndex < 36; inventoryIndex++) {
            if (inventoryIndex == selectedSlot) continue;

            ItemStack stack = mc.player.getInventory().getStack(inventoryIndex);
            if (!stack.isEmpty() && normalize(stack.getName().getString()).contains(query)) {
                return inventoryIndex;
            }
        }
        return NO_SLOT;
    }

    private static boolean matchesName(ItemStack stack, String searchName) {
        return !stack.isEmpty() && normalize(stack.getName().getString()).contains(normalize(searchName));
    }

    private static String normalize(String value) {
        return value == null ? "" : value.toLowerCase().trim().replace(" ", "");
    }

    private static double getAttackDamage(ItemStack stack) {
        AttributeModifiersComponent modifiers = stack.get(DataComponentTypes.ATTRIBUTE_MODIFIERS);
        if (modifiers == null) return 0.0;

        double damage = 0.0;
        for (AttributeModifiersComponent.Entry entry : modifiers.modifiers()) {
            if (entry.attribute().matches(EntityAttributes.ATTACK_DAMAGE)) {
                damage += entry.modifier().value();
            }
        }
        return damage;
    }

    private static boolean isTotemOrHead(ItemStack stack) {
        return !stack.isEmpty()
                && (stack.isOf(Items.TOTEM_OF_UNDYING)
                || stack.isOf(Items.PLAYER_HEAD)
                || stack.isOf(Items.ZOMBIE_HEAD)
                || stack.isOf(Items.SKELETON_SKULL)
                || stack.isOf(Items.WITHER_SKELETON_SKULL)
                || stack.isOf(Items.CREEPER_HEAD)
                || stack.isOf(Items.DRAGON_HEAD)
                || stack.isOf(Items.PIGLIN_HEAD));
    }

    public static final class Swap {
        private static final Swap NONE = new Swap(NO_SLOT, 0L);

        private final int damageSlotId;
        private final long swappedAt;
        private volatile boolean restoreSent;

        private Swap(int damageSlotId, long swappedAt) {
            this.damageSlotId = damageSlotId;
            this.swappedAt = swappedAt;
        }

        public boolean isActive() {
            return this != NONE;
        }
    }
}

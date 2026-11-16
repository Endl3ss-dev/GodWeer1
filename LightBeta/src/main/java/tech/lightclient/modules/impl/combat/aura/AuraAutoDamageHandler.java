package tech.lightclient.modules.impl.combat.aura;

import tech.lightclient.client.utils.player.PlayerInventoryUtil;
import tech.lightclient.modules.impl.combat.AuraModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

/**
 * Авто Урон: держит в левой руке правильный предмет в зависимости от того,
 * что находится в руке цели Aura.
 *  - у цели меч/арбалет/булава -> ставим предмет здоровья (h)
 *  - у цели что-то другое      -> ставим предмет урона (d)
 *
 * Если игрок сам положил предмет урона в левую руку (через инвентарь или
 * функцию AutoSwap), автоматика не трогает руку, пока он сам не вернёт хил.
 */
public final class AuraAutoDamageHandler {
    private static final int OFFHAND_BUTTON = 40;

    /** Пауза между автосвапами, чтобы не спамить кликами каждый тик. */
    private static final long SWAP_COOLDOWN_MS = 100L;
    /** Сколько миллисекунд после нашего свапа изменение оффхенда считается нашим, а не ручным. */
    private static final long SYSTEM_SWAP_WINDOW_MS = 600L;
    /** Пауза атаки сразу после клика свапа: только чтобы клик ушёл раньше удара. */
    private static final long ATTACK_PAUSE_MS = 40L;

    private static volatile boolean isSwapping = false;
    private static volatile long swapStartTime = 0;
    private static volatile long lastSwapTime = 0;

    /** Ручная блокировка: игрок сам поставил урон в оффхенд. */
    private static volatile boolean manualDamageLock = false;

    /** Наш последний системный свап (чтобы не принять его за ручной). */
    private static volatile long systemSwapAt = 0;
    private static volatile String systemSwapExpected = "";

    /** Нормализованное содержимое левой руки на прошлом тике. */
    private static String lastOffhandKey = null;

    private AuraAutoDamageHandler() {
    }

    public static void checkAndSwap(MinecraftClient mc, AuraModule aura) {
        if (!aura.isAutoDamageEnabled()
                || mc.player == null
                || mc.interactionManager == null) {
            return;
        }

        LivingEntity target = AuraModule.target;
        if (target == null || !target.isAlive()) return;

        trackOffhandChanges(mc, aura);

        // Игрок сам взял урон в руку — ждём, пока он сам вернёт хил
        if (manualDamageLock) return;

        long now = System.currentTimeMillis();
        if (now - lastSwapTime < SWAP_COOLDOWN_MS) return;

        String damageName = aura.getAutoDamageItemName();
        String healName = aura.getAutoHealItemName();
        ItemStack offhand = mc.player.getOffHandStack();

        if (!isThreatWeapon(target.getMainHandStack())) {
            // У противника не меч/арбалет/булава — нужен предмет урона
            if (matches(offhand, damageName)) return;
            int slot = findItemByName(mc, damageName);
            if (slot != -1) performSwap(mc, slot, damageName);
        } else {
            // Противник вооружился — нужен предмет здоровья
            if (matches(offhand, healName)) return;
            int slot = findItemByName(mc, healName);
            if (slot != -1) performSwap(mc, slot, healName);
        }
    }

    /**
     * Детект ручных изменений левой руки. Наши собственные свапы тоже меняют
     * содержимое оффхенда, поэтому они помечаются ожидаемым именем предмета
     * и окном времени и не считаются ручными.
     */
    private static void trackOffhandChanges(MinecraftClient mc, AuraModule aura) {
        String current = normalize(mc.player.getOffHandStack().getName().getString());
        if (lastOffhandKey == null) {
            lastOffhandKey = current;
            return;
        }
        if (current.equals(lastOffhandKey)) return;
        lastOffhandKey = current;

        boolean ownSwap = System.currentTimeMillis() - systemSwapAt <= SYSTEM_SWAP_WINDOW_MS
                && !systemSwapExpected.isEmpty()
                && current.equals(systemSwapExpected);
        if (ownSwap) return;

        String damageName = normalize(aura.getAutoDamageItemName());
        String healName = normalize(aura.getAutoHealItemName());

        boolean isDamage = !damageName.isEmpty() && current.contains(damageName);
        boolean isHeal = !healName.isEmpty() && current.contains(healName);

        if (isDamage && !isHeal) {
            // Игрок сам свапнул на урон: блокируем автоматику,
            // пока он сам не вернёт предмет здоровья
            manualDamageLock = true;
        } else if (isHeal && !isDamage) {
            // Игрок вручную вернул хил — снимаем блокировку
            manualDamageLock = false;
        }
    }

    private static void performSwap(MinecraftClient mc, int inventorySlot, String itemName) {
        int slotId = inventorySlot < 9 ? inventorySlot + 36 : inventorySlot;
        PlayerInventoryUtil.clickSlot(slotId, OFFHAND_BUTTON, SlotActionType.SWAP);

        long now = System.currentTimeMillis();
        // Ожидаемый результат - ПОЛНОЕ имя предмета, а не поисковая подстрока:
        // иначе собственный свап не пройдёт проверку ниже и будет ошибочно
        // посчитан ручным действием игрока
        systemSwapAt = now;
        systemSwapExpected = normalize(mc.player.getInventory().getStack(inventorySlot)
                .getName().getString());
        isSwapping = true;
        swapStartTime = now;
        lastSwapTime = now;
        // Клиент применяет клик локально сразу - фиксируем ожидаемый результат,
        // чтобы следующий тик не принял наш же свап за ручное действие
        lastOffhandKey = systemSwapExpected;
    }

    /**
     * Пауза атаки во время отправки свапа, чтобы удар не ушёл, пока предмет
     * ещё переезжает в левую руку. Пауза минимальная: клик применяется
     * клиентом мгновенно, поэтому достаточно пары миллисекунд.
     */
    public static boolean canAttack(AuraModule aura) {
        if (!aura.isAutoDamageEnabled()) return true;
        if (!aura.shouldPauseAttackDuringAutoSwap() || !isSwapping) return true;
        if (System.currentTimeMillis() - swapStartTime > ATTACK_PAUSE_MS) {
            isSwapping = false;
            return true;
        }
        return false;
    }

    public static void reset() {
        isSwapping = false;
        swapStartTime = 0;
        lastSwapTime = 0;
        manualDamageLock = false;
        systemSwapAt = 0;
        systemSwapExpected = "";
        lastOffhandKey = null;
    }

    /**
     * Возвращает предмет здоровья в левую руку при выключении Aura,
     * если автоматика оставила там предмет урона.
     */
    public static void restoreHealItem(MinecraftClient mc, AuraModule aura) {
        if (mc.player == null || mc.interactionManager == null) return;
        if (mc.player.currentScreenHandler != mc.player.playerScreenHandler) return;

        String healQuery = normalize(aura.getAutoHealItemName());
        if (healQuery.isEmpty()) return;

        ItemStack offhand = mc.player.getOffHandStack();
        String offhandName = offhand.isEmpty() ? "" : normalize(offhand.getName().getString());

        // Хил уже в руке
        if (!offhandName.isEmpty() && offhandName.contains(healQuery)) return;

        // Трогаем руку только если там остался наш предмет урона или пусто;
        // посторонние предметы игрока не трогаем
        if (!offhand.isEmpty()) {
            String damageQuery = normalize(aura.getAutoDamageItemName());
            boolean offhandHasDamage = !damageQuery.isEmpty() && offhandName.contains(damageQuery);
            if (!offhandHasDamage) return;
        }

        int slot = findItemByName(mc, aura.getAutoHealItemName());
        if (slot != -1) {
            int slotId = slot < 9 ? slot + 36 : slot;
            PlayerInventoryUtil.clickSlot(slotId, OFFHAND_BUTTON, SlotActionType.SWAP);
        }
    }

    private static boolean isThreatWeapon(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return stack.isOf(Items.WOODEN_SWORD)
                || stack.isOf(Items.STONE_SWORD)
                || stack.isOf(Items.IRON_SWORD)
                || stack.isOf(Items.GOLDEN_SWORD)
                || stack.isOf(Items.DIAMOND_SWORD)
                || stack.isOf(Items.NETHERITE_SWORD)
                || stack.isOf(Items.MACE)
                || stack.isOf(Items.CROSSBOW);
    }

    private static int findItemByName(MinecraftClient mc, String searchName) {
        String query = normalize(searchName);
        if (query.isEmpty() || mc.player == null) return -1;

        int selectedSlot = mc.player.getInventory().getSelectedSlot();
        for (int inventoryIndex = 0; inventoryIndex < 36; inventoryIndex++) {
            if (inventoryIndex == selectedSlot) continue;
            ItemStack stack = mc.player.getInventory().getStack(inventoryIndex);
            if (!stack.isEmpty() && normalize(stack.getName().getString()).contains(query)) {
                return inventoryIndex;
            }
        }
        return -1;
    }

    private static boolean matches(ItemStack stack, String searchName) {
        String query = normalize(searchName);
        return !stack.isEmpty() && !query.isEmpty()
                && normalize(stack.getName().getString()).contains(query);
    }

    private static String normalize(String value) {
        return value == null ? "" : value.toLowerCase().trim().replace(" ", "");
    }
}

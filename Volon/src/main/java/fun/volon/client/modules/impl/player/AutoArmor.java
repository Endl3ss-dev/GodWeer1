package fun.volon.client.modules.impl.player;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import fun.volon.api.events.EventLink;
import fun.volon.api.events.implement.EventUpdate;
import fun.volon.client.modules.Module;
import fun.volon.client.modules.settings.implement.BooleanSetting;
import fun.volon.client.modules.settings.implement.FloatSetting;

public class AutoArmor extends Module {

    public static AutoArmor INSTANCE = new AutoArmor();

    private final FloatSetting delay        = new FloatSetting("Задержка", 25.0F, 1.0F, 1000.0F, 1.0F);
    private final BooleanSetting autoSword  = new BooleanSetting("Меч в руку", false);
    private final BooleanSetting autoOffhand = new BooleanSetting("Шар во вторую руку", false);
    private long lastEquipTime = 0L;

    public AutoArmor() {
        super("AutoArmor", "Автоматически одевает броню", ModuleCategory.PLAYER);
        addSettings(delay, autoSword, autoOffhand);
    }

    @EventLink
    public void onEvent(final EventUpdate event) {
        if (mc.player == null || mc.world == null) return;
        if (isMoving()) return;

        long currentTime = System.currentTimeMillis();
        if ((float) (currentTime - lastEquipTime) < delay.get()) return;

        // --- Броня ---
        for (int i = 0; i < 4; ++i) {
            ItemStack currentArmor = mc.player.getInventory().getArmorStack(i);
            ItemStack betterArmor  = null;
            int betterArmorSlot    = -1;
            float currentProtection = getArmorProtection(currentArmor);

            for (int j = 0; j < 36; ++j) {
                ItemStack stack = mc.player.getInventory().getStack(j);
                if (!stack.isEmpty() && stack.getItem() instanceof ArmorItem armorItem) {
                    if (getArmorSlotIndex(armorItem) == i) {
                        float stackProtection = getArmorProtection(stack);
                        if (currentArmor.isEmpty() || stackProtection > currentProtection) {
                            if (betterArmor == null || stackProtection > getArmorProtection(betterArmor)) {
                                betterArmor     = stack;
                                betterArmorSlot = j;
                            }
                        }
                    }
                }
            }

            if (betterArmorSlot != -1) {
                int slotToEquip = betterArmorSlot < 9 ? betterArmorSlot + 36 : betterArmorSlot;
                if (!currentArmor.isEmpty()) {
                    int armorSlot = 8 - i;
                    mc.interactionManager.clickSlot(0, armorSlot, 0, SlotActionType.QUICK_MOVE, mc.player);
                    lastEquipTime = currentTime;
                    return;
                }
                mc.interactionManager.clickSlot(0, slotToEquip, 0, SlotActionType.QUICK_MOVE, mc.player);
                lastEquipTime = currentTime;
                return;
            }
        }

        // --- Меч в первый хотбар-слот ---
        if (autoSword.isState()) {
            ItemStack slot0 = mc.player.getInventory().getStack(0);
            if (slot0.isEmpty() || !(slot0.getItem() instanceof SwordItem)) {
                int swordSlot = findSwordSlot();
                if (swordSlot != -1 && swordSlot != 0) {
                    // Гримовский bypass: SWAP между инвентарем и слотом 0 хотбара
                    int screenSlot = swordSlot < 9 ? swordSlot + 36 : swordSlot;
                    // Меняем местами с хотбар-слотом 0 (button = 0)
                    mc.interactionManager.clickSlot(0, screenSlot, 0, SlotActionType.SWAP, mc.player);
                    lastEquipTime = currentTime;
                    return;
                }
            }
        }

        // --- Шар (player_head) во вторую руку, grim bypass ---
        if (autoOffhand.isState()) {
            ItemStack offhand = mc.player.getOffHandStack();
            if (offhand.isEmpty() || offhand.getItem() != Items.PLAYER_HEAD) {
                int headSlot = findPlayerHeadSlot();
                if (headSlot != -1) {
                    swapToOffhand(headSlot);
                    lastEquipTime = currentTime;
                    return;
                }
            }
        }
    }

    /**
     * Кладёт предмет из слота инвентаря в offhand.
     * slot 40 = offhand в сетке экрана (grim bypass как в AutoTotem).
     */
    private void swapToOffhand(int invSlot) {
        // invSlot 0-8  → hotbar → screenSlot = invSlot + 36
        // invSlot 9-35 → main inventory → screenSlot = invSlot
        int screenSlot = invSlot < 9 ? invSlot + 36 : invSlot;
        mc.interactionManager.clickSlot(0, screenSlot, 40, SlotActionType.SWAP, mc.player);
        mc.player.networkHandler.sendPacket(new CloseHandledScreenC2SPacket(0));
    }

    /** Ищет первый меч в инвентаре (слоты 0-35) */
    private int findSwordSlot() {
        for (int i = 0; i < 36; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (!stack.isEmpty() && stack.getItem() instanceof SwordItem) return i;
        }
        return -1;
    }

    /** Ищет первый player_head (шар) в инвентаре */
    private int findPlayerHeadSlot() {
        for (int i = 0; i < 36; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (!stack.isEmpty() && stack.getItem() == Items.PLAYER_HEAD) return i;
        }
        return -1;
    }

    private float getArmorProtection(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ArmorItem armorItem)) return 0.0f;
        float baseProtection = getArmorMaterialPriority(armorItem);
        float durabilityBonus = 0.0f;
        if (stack.isDamageable() && stack.getMaxDamage() > 0) {
            int remainingDurability = stack.getMaxDamage() - stack.getDamage();
            durabilityBonus = (float) remainingDurability / (float) stack.getMaxDamage() * 0.5f;
        }
        float enchantBonus = stack.hasEnchantments() ? stack.getEnchantments().getEnchantments().size() * 0.3f : 0.0f;
        return baseProtection + durabilityBonus + enchantBonus;
    }

    private float getArmorMaterialPriority(ArmorItem armor) {
        String itemName = armor.toString().toLowerCase();
        if (itemName.contains("netherite")) return 5.0f;
        if (itemName.contains("diamond"))   return 4.0f;
        if (itemName.contains("iron"))      return 3.0f;
        if (itemName.contains("golden") || itemName.contains("gold")) return 2.0f;
        if (itemName.contains("chainmail")) return 2.0f;
        if (itemName.contains("leather"))   return 1.0f;
        return 0.0f;
    }

    private boolean isMoving() {
        return mc.player.input.movementForward != 0.0F || mc.player.input.movementSideways != 0.0F;
    }

    private int getArmorSlotIndex(ArmorItem armor) {
        String itemName = armor.toString().toLowerCase();
        if (itemName.contains("helmet")     || itemName.contains("skull"))    return 3;
        if (itemName.contains("chestplate") || itemName.contains("tunic"))    return 2;
        if (itemName.contains("leggings")   || itemName.contains("pants"))    return 1;
        if (itemName.contains("boots")      || itemName.contains("shoes"))    return 0;
        return 0;
    }
}

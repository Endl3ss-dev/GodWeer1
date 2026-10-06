package godweer.modules.impl.combat;

import godweer.modules.Module;
import godweer.modules.ModuleCategory;
import godweer.modules.api.setting.BooleanSetting;
import godweer.modules.api.setting.FloatSetting;
import godweer.modules.api.setting.ListSetting;
import godweer.utils.item.ConsumableItemUtil;
import godweer.utils.item.ConsumableUseController;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;

public final class AutoHeal extends Module {

    private final ListSetting items = new ListSetting(
            "Предметы",
            new BooleanSetting("Исцеление", true),
            new BooleanSetting("Чарка", true),
            new BooleanSetting("Гэпл", true),
            new BooleanSetting("Еда", false)
    );
    private final FloatSetting healingHealth = new FloatSetting("ХП для Исцеления", 12.0F, 1.0F, 20.0F, 0.5F)
            .visible(() -> items.is("Исцеление"));
    private final FloatSetting enchantedAppleHealth = new FloatSetting("ХП для Чарки", 8.0F, 1.0F, 20.0F, 0.5F)
            .visible(() -> items.is("Чарка"));
    private final FloatSetting goldenAppleHealth = new FloatSetting("ХП для Гэпла", 16.0F, 1.0F, 20.0F, 0.5F)
            .visible(() -> items.is("Гэпл"));
    private final FloatSetting foodLevel = new FloatSetting("Голод для Еды", 18.0F, 1.0F, 20.0F, 0.5F)
            .visible(() -> items.is("Еда"));

    private final ConsumableUseController useController;

    public AutoHeal() {
        super("AutoHeal", "Автоматически использует лечение, яблоки и еду", ModuleCategory.COMBAT);
        useController = new ConsumableUseController(this);
        addSettings(items, healingHealth, enchantedAppleHealth, goldenAppleHealth, foodLevel);
    }

    @Override
    public void onDisable() {
        Minecraft minecraft = Minecraft.getInstance();
        useController.cancel(minecraft, minecraft.player);
    }

    @Override
    public void onTick() {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || minecraft.level == null || minecraft.gameMode == null) {
            useController.reset();
            return;
        }

        if (useController.tick(minecraft, player)) {
            return;
        }
        if (minecraft.screen != null || player.isUsingItem() || !player.isAlive()
                || player.isCreative() || player.isSpectator()) {
            return;
        }

        float effectiveHealth = player.getHealth() + player.getAbsorptionAmount();
        if (items.is("Исцеление") && effectiveHealth <= healingHealth.get()) {
            int slot = ConsumableItemUtil.findPotionSlot(player, Items.POTION, MobEffects.HEAL);
            if (useAndHold(minecraft, player, slot)) {
                return;
            }
        }

        if (items.is("Чарка") && effectiveHealth <= enchantedAppleHealth.get()) {
            int slot = ConsumableItemUtil.findItemSlot(player, Items.ENCHANTED_GOLDEN_APPLE);
            if (useAndHold(minecraft, player, slot)) {
                return;
            }
        }

        if (items.is("Гэпл") && effectiveHealth <= goldenAppleHealth.get()) {
            int slot = ConsumableItemUtil.findItemSlot(player, Items.GOLDEN_APPLE);
            if (useAndHold(minecraft, player, slot)) {
                return;
            }
        }

        if (items.is("Еда") && player.getFoodData().getFoodLevel() <= foodLevel.get()) {
            int slot = ConsumableItemUtil.findBestFoodSlot(player);
            useAndHold(minecraft, player, slot);
        }
    }

    private boolean useAndHold(Minecraft minecraft, LocalPlayer player, int slot) {
        return slot != ConsumableItemUtil.NOT_FOUND
                && useController.useAndHold(minecraft, player, slot, true);
    }
}
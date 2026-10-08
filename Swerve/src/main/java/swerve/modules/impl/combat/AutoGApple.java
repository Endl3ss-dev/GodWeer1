package swerve.modules.impl.combat;

import swerve.modules.Module;
import swerve.modules.ModuleCategory;
import swerve.modules.api.setting.BooleanSetting;
import swerve.modules.api.setting.FloatSetting;
import swerve.utils.item.ConsumableItemUtil;
import swerve.utils.item.ConsumableUseController;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.Items;

public final class AutoGApple extends Module {

    private final FloatSetting healthThreshold = new FloatSetting("Уровень здоровья", 14.0F, 1.0F, 20.0F, 0.5F);
    private final BooleanSetting eatOnEnable = new BooleanSetting("Съесть в начале", false);
    private final BooleanSetting hvhMode = new BooleanSetting("ХвХ", false);

    private final ConsumableUseController useController;
    private boolean startPending;

    public AutoGApple() {
        super("AutoGApple", "Автоматически ест золотые яблоки при низком здоровье", ModuleCategory.COMBAT);
        useController = new ConsumableUseController(this);
        addSettings(healthThreshold, eatOnEnable, hvhMode);
    }

    @Override
    public void onEnable() {
        startPending = eatOnEnable.get();
    }

    @Override
    public void onDisable() {
        Minecraft minecraft = Minecraft.getInstance();
        useController.cancel(minecraft, minecraft.player);
        startPending = false;
    }

    @Override
    public void onTick() {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || minecraft.level == null || minecraft.gameMode == null) {
            useController.reset();
            return;
        }

        if (useController.tick(minecraft, player) && useController.isActive()) {
            return;
        }
        if (minecraft.screen != null || player.isUsingItem() || !player.isAlive()
                || player.isCreative() || player.isSpectator()) {
            return;
        }

        float effectiveHealth = player.getHealth() + player.getAbsorptionAmount();
        if (!startPending && effectiveHealth > healthThreshold.get()) {
            return;
        }

        int slot = findGoldenAppleSlot(player);
        if (slot == ConsumableItemUtil.NOT_FOUND) {
            return;
        }

        if (useController.useAndHold(minecraft, player, slot, true)) {
            startPending = false;
        }
    }

    private int findGoldenAppleSlot(LocalPlayer player) {
        int preferredSlot = ConsumableItemUtil.findItemSlot(
                player,
                hvhMode.get() ? Items.ENCHANTED_GOLDEN_APPLE : Items.GOLDEN_APPLE
        );
        if (preferredSlot != ConsumableItemUtil.NOT_FOUND) {
            return preferredSlot;
        }
        return ConsumableItemUtil.findItemSlot(
                player,
                hvhMode.get() ? Items.GOLDEN_APPLE : Items.ENCHANTED_GOLDEN_APPLE
        );
    }
}
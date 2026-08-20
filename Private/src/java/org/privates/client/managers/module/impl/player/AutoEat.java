package org.privates.client.managers.module.impl.player;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.AirItem;
import net.minecraft.item.UseAction;
import org.privates.client.api.annotations.Category2;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.impl.combat.AuraNew;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.other.Instance;

@Category2
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "AutoEat", category = Category.PLAYER, description = "Авто кушает еду при выборе минимального голода")
public class AutoEat extends Module {

    public static AutoEat getInstance() {
        return Instance.get(AutoEat.class);
    }

    private final SliderSetting feedLevel = new SliderSetting(this, "Голод", 15f, 1f, 19f, 0.5f);
    private final BooleanSetting swap = new BooleanSetting(this, "Брать в руку", false);
    private final BooleanSetting auraNo = new BooleanSetting(this, "Не использовать с Aura", false);

    private boolean isUse;

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (!shouldEat()) return;

        AuraNew aura = AuraNew.getInstance();
        if (aura != null && aura.isEnabled() && aura.target() != null && auraNo.getValue()) return;

        mc.gameSettings.keyBindUseItem.setPressed(isUse);

        if (mc.player.getFoodStats().getFoodLevel() < feedLevel.getValue()) {
            handleEating();
            isUse = true;
        } else {
            isUse = mc.player.getFoodStats().needFood();
        }
    }

    private boolean shouldEat() {
        if (mc.player == null) return false;

        return mc.player.getHeldItemOffhand().getUseAction() == UseAction.EAT ||
                mc.player.getHeldItemMainhand().getUseAction() == UseAction.EAT ||
                swap.getValue();
    }

    private void handleEating() {
        int hotbarSlot = findEatInHotbar();
        if (hotbarSlot != -1) {
            mc.player.inventory.currentItem = hotbarSlot;
        } else {
            int inventorySlot = findEatInInventory();
            if (inventorySlot != -1) {
                swapItemToOffhand(inventorySlot);
            }
        }
    }

    private int findEatInHotbar() {
        for (int i = 0; i < 9; i++) {
            if (mc.player.inventory.getStackInSlot(i).getUseAction() == UseAction.EAT) {
                return i;
            }
        }
        return -1;
    }

    private int findEatInInventory() {
        for (int i = 9; i < 36; i++) {
            if (mc.player.inventory.getStackInSlot(i).getUseAction() == UseAction.EAT) {
                return i;
            }
        }
        return -1;
    }

    private void swapItemToOffhand(int inventorySlot) {
        mc.playerController.windowClick(0, inventorySlot, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, mc.player);
        if (!(mc.player.getHeldItemOffhand().getItem() instanceof AirItem)) {
            mc.playerController.windowClick(0, inventorySlot, 0, ClickType.PICKUP, mc.player);
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        isUse = false;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        isUse = false;
        if (mc.gameSettings != null) {
            mc.gameSettings.keyBindUseItem.setPressed(false);
        }
    }
}
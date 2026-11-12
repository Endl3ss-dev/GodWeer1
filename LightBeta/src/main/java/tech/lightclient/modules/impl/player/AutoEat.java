package tech.lightclient.modules.impl.player;

import tech.lightclient.client.events.impl.client.UpdateEvent;
import tech.lightclient.client.utils.eventbus.EventHandler;
import tech.lightclient.modules.api.Category;
import tech.lightclient.modules.api.Function;
import tech.lightclient.modules.api.FunctionAdd;
import net.minecraft.client.util.InputUtil;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;

@FunctionAdd(name = "AutoEat", alias = "Auto Eat", category = Category.Player, description = "Автоматически ест еду из оффхенда при голоде ниже 8")
public class AutoEat extends Function {
    private boolean autoPressedUse;

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (nullCheck()) return;

        ItemStack offhand = mc.player.getOffHandStack();
        boolean hasFood = offhand.contains(DataComponentTypes.FOOD);

        if (mc.player.getHungerManager().getFoodLevel() < 8 && hasFood) {
            mc.options.useKey.setPressed(true);
            autoPressedUse = true;
        } else if (autoPressedUse && !mc.player.isUsingItem()) {
            restoreUseKey();
            autoPressedUse = false;
        }
    }

    @Override
    public void onDisable() {
        restoreUseKey();
        autoPressedUse = false;
        super.onDisable();
    }

    private void restoreUseKey() {
        if (mc.options == null || mc.getWindow() == null) {
            return;
        }
        mc.options.useKey.setPressed(InputUtil.isKeyPressed(mc.getWindow(), mc.options.useKey.getDefaultKey().getCode()));
    }
}

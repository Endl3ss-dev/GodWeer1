package org.privates.client.managers.module.impl.combat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import org.privates.client.api.annotations.Category1;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.other.Instance;

@Category1

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "AutoGapple", category = Category.COMBAT, description = "Авто заедание гепла по здоровью")
public class AutoGapple extends Module {
    public static AutoGapple getInstance() {
        return Instance.get(AutoGapple.class);
    }

    private final SliderSetting healthThreshold = new SliderSetting(this, "Уровень здоровья", 18, 4, 20, 1);
    private boolean active;

    @Override
    public void toggle() {
        super.toggle();
        active = false;
    }

    @EventHandler
    public void onEvent(UpdateEvent event) {
        if (shouldEatGoldenApple()) {
            startEating();
        } else if (active && mc.player.isHandActive() && mc.player.getActiveHand().equals(Hand.OFF_HAND)) {
            stopEating();
        }
    }


    private void startEating() {
        active = true;
        if (!mc.gameSettings.keyBindUseItem.isKeyDown()) {
            mc.gameSettings.keyBindUseItem.setPressed(true);
            mc.playerController.processRightClick(mc.player, mc.world, Hand.OFF_HAND);
            mc.gameSettings.keyBindUseItem.setPressed(true);
        }
    }


    private void stopEating() {
        mc.gameSettings.keyBindUseItem.setPressed(false);
        mc.playerController.onStoppedUsingItem(mc.player);
        mc.gameSettings.keyBindUseItem.setPressed(false);
        active = false;
    }

    private boolean shouldEatGoldenApple() {
        return mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE && mc.player.getHealth() <= healthThreshold.getValue();
    }
}

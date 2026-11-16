package tech.lightclient.modules.impl.player;

import tech.lightclient.client.events.impl.client.UpdateEvent;
import tech.lightclient.client.utils.eventbus.EventHandler;
import tech.lightclient.modules.api.Category;
import tech.lightclient.modules.api.Function;
import tech.lightclient.modules.api.FunctionAdd;
import tech.lightclient.modules.api.settings.impl.SliderSetting;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

import java.util.concurrent.ThreadLocalRandom;

@FunctionAdd(name = "FastExp", alias = "Fast Exp", category = Category.Player, description = "Ускоряет выкидывание пузырьков опыта")
public class FastExp extends Function {
    public SliderSetting delay = new SliderSetting("Задержка", 50f, 25f, 100f, 5f);
    private long lastThrowTime = 0;

    public FastExp() {
        addSettings(delay);
    }

    @EventHandler
    public void onFastest(UpdateEvent e) {
        if (mc.player == null) return;

        boolean hasExpBottle = mc.player.getMainHandStack().getItem() == Items.EXPERIENCE_BOTTLE ||
                mc.player.getOffHandStack().getItem() == Items.EXPERIENCE_BOTTLE;

        if (mc.options.useKey.isPressed() && hasExpBottle) {
            long currentTime = System.currentTimeMillis();
            float randomDelay = ThreadLocalRandom.current().nextFloat(delay.get() - 3, delay.get() + 11);
            long delayMs = (long) (randomDelay);

            if (currentTime - lastThrowTime >= delayMs) {
                if (mc.player != null && mc.interactionManager != null) {
                    mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                    lastThrowTime = currentTime;
                }
            }
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}

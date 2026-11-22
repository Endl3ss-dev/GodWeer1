package tech.lightclient.modules.impl.combat;

import tech.lightclient.client.events.impl.client.TickEvent;
import tech.lightclient.client.utils.client.other.Script;
import tech.lightclient.client.utils.eventbus.EventHandler;
import tech.lightclient.client.utils.math.time.NewStopWatch;
import tech.lightclient.client.utils.player.PlayerInventoryUtil;
import tech.lightclient.client.utils.player.PlayerUtils;
import tech.lightclient.modules.api.Category;
import tech.lightclient.modules.api.Function;
import tech.lightclient.modules.api.FunctionAdd;
import tech.lightclient.modules.api.settings.impl.BooleanSetting;
import tech.lightclient.modules.api.settings.impl.ModeListSetting;
import tech.lightclient.modules.api.settings.impl.SliderSetting;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Hand;

import java.util.Comparator;

@FunctionAdd(name = "AutoTotem", alias = "Auto Totem", category = Category.Combat, description = "Автоматически использует тотем бессмертия")
public class AutoTotem extends Function {
    SliderSetting healthSetting = new SliderSetting("Максимальное здоровье", 4, 2, 16, 0.5f);
    SliderSetting elytraHealthSetting = new SliderSetting("Здоровье при полёте", 6, 2, 16, 0.5f);

    ModeListSetting triggerSetting = new ModeListSetting("Триггеры",
            new BooleanSetting("Кристалл", false),
            new BooleanSetting("ТНТ", false));

    BooleanSetting dontTakeIfUsing = new BooleanSetting("Не брать если ешь", true);

    NewStopWatch stopWatch = new NewStopWatch();
    NewStopWatch safetyTimer = new NewStopWatch();
    Script script = new Script();

    private Slot lastTotemSlot = null;
    private boolean totemPlacedThisDanger = false;

    public AutoTotem() {
        addSettings(healthSetting, elytraHealthSetting, triggerSetting, dontTakeIfUsing);
    }

    @Override
    public void onDisable() {
        script.cleanup();
        super.onDisable();
    }

    @EventHandler
    public void onTick(TickEvent e) {
        if (nullCheck()) return;
        if (dontTakeIfUsing.get() && mc.player.isUsingItem()) return;

        boolean isDanger = trigger();

        ItemStack off = mc.player.getOffHandStack();
        boolean hasTotem = off.isOf(Items.TOTEM_OF_UNDYING);

        if (isDanger) {
            safetyTimer.reset();

            if (!hasTotem || totemPlacedThisDanger == false) {
                Slot slot = PlayerInventoryUtil.getSlot(
                        Items.TOTEM_OF_UNDYING,
                        Comparator.comparing(s -> !s.getStack().hasEnchantments()),
                        s -> s.id != 45 && s.id != 46
                );

                if (slot != null && stopWatch.every(75)) {
                    PlayerInventoryUtil.swapHand(slot, Hand.OFF_HAND, true, true);

                    lastTotemSlot = slot;
                    totemPlacedThisDanger = true;

                    stopWatch.reset();
                }
            }
        }
        else {
            if (safetyTimer.finished(300)) {
                if (totemPlacedThisDanger && lastTotemSlot != null) {

                    totemPlacedThisDanger = false;
                    lastTotemSlot = null;
                }

                if (!script.isFinished() && stopWatch.every(150)) {
                    script.update();
                }
            }
        }
    }

    public boolean trigger() {
        if (mc.player == null) return false;
        float healthThreshold = mc.player.isGliding() ? elytraHealthSetting.get() : healthSetting.get();
        float health = mc.player.getHealth();

        if (health < healthThreshold) return true;

        if (triggerSetting.getByName("Кристалл").get() && PlayerUtils.streamEntities().anyMatch(e -> e instanceof EndCrystalEntity && mc.player.distanceTo(e) < 6 && e.getY() > mc.player.getEyeY()))
            return true;
        return triggerSetting.getByName("ТНТ").get() && PlayerUtils.streamEntities().anyMatch(e -> e instanceof TntEntity && mc.player.distanceTo(e) < 7);
    }
}

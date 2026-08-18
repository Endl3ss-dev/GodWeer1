package nuclear.module.impl.player;

import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import nuclear.control.events.Event;
import nuclear.control.events.impl.player.EventUpdate;
import nuclear.module.api.Module;
import nuclear.module.api.ModuleCategory;
import nuclear.module.api.ModuleDescriptor;
import nuclear.module.settings.imp.ModeSetting;
import nuclear.module.settings.imp.SliderSetting;

import java.util.List;

@ModuleDescriptor(
    name = "PotionSwap",
    type = ModuleCategory.Player,
    desc = "Moves a fresh potion into the consumed slot after drinking"
)
public class PotionSwap extends Module {
    private final ModeSetting mode = new ModeSetting(
        "Mode",
        "Same potion",
        "Same potion",
        "Healing",
        "Strength",
        "Speed",
        "Fire resistance"
    );
    private final SliderSetting swapDelay = new SliderSetting("Delay", 150.0f, 0.0f, 1000.0f, 50.0f);

    private boolean wasDrinking;
    private int trackedHotbarSlot = -1;
    private Effect trackedEffect;
    private long lastSwapTime;

    public PotionSwap() {
        addSettings(mode, swapDelay);
    }

    @Override
    public boolean onEvent(Event event) {
        if (!(event instanceof EventUpdate) || mc.player == null) {
            return false;
        }

        trackDrinkingState();
        return false;
    }

    private void trackDrinkingState() {
        ItemStack heldStack = mc.player.getHeldItemMainhand();
        boolean drinkingNow = mc.player.isHandActive() && heldStack.getItem() instanceof PotionItem;

        if (drinkingNow) {
            wasDrinking = true;
            trackedHotbarSlot = mc.player.inventory.currentItem;
            trackedEffect = getPreferredEffect(heldStack);
            return;
        }

        if (!wasDrinking) {
            return;
        }

        wasDrinking = false;
        if (trackedHotbarSlot == -1 || System.currentTimeMillis() - lastSwapTime < swapDelay.getValue().longValue()) {
            return;
        }

        ItemStack current = mc.player.inventory.getStackInSlot(trackedHotbarSlot);
        if (!(current.isEmpty() || current.getItem() instanceof PotionItem)) {
            return;
        }

        int replacementSlot = findReplacementSlot();
        if (replacementSlot == -1) {
            return;
        }

        int containerSlot = toContainerSlot(replacementSlot);
        mc.playerController.windowClick(0, containerSlot, trackedHotbarSlot, ClickType.SWAP, mc.player);
        lastSwapTime = System.currentTimeMillis();
    }

    private Effect getPreferredEffect(ItemStack stack) {
        if (!mode.is("Same potion")) {
            if (mode.is("Healing")) {
                return Effects.INSTANT_HEALTH;
            }
            if (mode.is("Strength")) {
                return Effects.STRENGTH;
            }
            if (mode.is("Speed")) {
                return Effects.SPEED;
            }
            if (mode.is("Fire resistance")) {
                return Effects.FIRE_RESISTANCE;
            }
        }

        List<EffectInstance> effects = net.minecraft.potion.PotionUtils.getEffectsFromStack(stack);
        return effects.isEmpty() ? null : effects.get(0).getPotion();
    }

    private int findReplacementSlot() {
        for (int slot = 0; slot < 36; slot++) {
            if (slot == trackedHotbarSlot) {
                continue;
            }

            ItemStack stack = mc.player.inventory.getStackInSlot(slot);
            if (!(stack.getItem() instanceof PotionItem)) {
                continue;
            }

            if (matchesEffect(stack, trackedEffect)) {
                return slot;
            }
        }
        return -1;
    }

    private boolean matchesEffect(ItemStack stack, Effect effect) {
        if (effect == null) {
            return true;
        }

        for (EffectInstance instance : net.minecraft.potion.PotionUtils.getEffectsFromStack(stack)) {
            if (instance.getPotion() == effect) {
                return true;
            }
        }
        return false;
    }

    private int toContainerSlot(int inventorySlot) {
        return inventorySlot < 9 ? inventorySlot + 36 : inventorySlot;
    }
}

package tech.lightclient.modules.impl.combat;

import tech.lightclient.client.events.impl.client.TickEvent;
import tech.lightclient.client.utils.eventbus.EventHandler;
import tech.lightclient.client.utils.player.PlayerInventoryUtil;
import tech.lightclient.client.utils.player.PlayerUtils;
import tech.lightclient.modules.api.Category;
import tech.lightclient.modules.api.Function;
import tech.lightclient.modules.api.FunctionAdd;
import tech.lightclient.modules.api.settings.impl.SliderSetting;
import tech.lightclient.ui.hud.NotificationsOverlay;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/**
 * Кидает взрывное зелье "Исцеление 1 уровня" из инвентаря под цель Aura
 * в момент, когда та начинает пить своё Исцеление.
 */
@FunctionAdd(name = "PotionThrower", alias = "Potion Thrower", category = Category.Combat,
        description = "Кидает исцеление когда ваш таргет пытается запить исцел")
public class PotionThrower extends Function {

    /** Защита от спама: не чаще одного зелья за этот интервал. */
    private static final long THROW_COOLDOWN_MS = 500L;

    private final SliderSetting throwRange = new SliderSetting("Дистанция", 6.0f, 2.0f, 15.0f, 0.5f);

    /** Цель на прошлом тике пила хил - для детекта именно момента НАЧАЛА питья. */
    private boolean targetWasHealing;
    private long lastThrowMs;

    public PotionThrower() {
        addSettings(throwRange);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        targetWasHealing = false;
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (!isState()) return;
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;

        LivingEntity target = AuraModule.target;
        boolean healing = target != null && target.isAlive() && isDrinkingHeal(target);

        // Реагируем только на старт питья, а не на каждый тик использования
        boolean startedHealing = healing && !targetWasHealing;
        targetWasHealing = healing;

        if (startedHealing) {
            tryThrowAt(target);
        }
    }

    /**
     * Цель использует предмет с эффектом мгновенного здоровья/регенерации -
     * это и есть "Исцеление".
     */
    private boolean isDrinkingHeal(LivingEntity target) {
        if (!target.isUsingItem()) return false;

        ItemStack active = target.getActiveItem();
        if (active.isEmpty()) return false;

        PotionContentsComponent contents = active.getOrDefault(
                DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT);
        for (StatusEffectInstance effect : contents.getEffects()) {
            if (effect.getEffectType() == StatusEffects.INSTANT_HEALTH
                    || effect.getEffectType() == StatusEffects.REGENERATION) {
                return true;
            }
        }
        return false;
    }

    private void tryThrowAt(LivingEntity target) {
        long now = System.currentTimeMillis();
        if (now - lastThrowMs < THROW_COOLDOWN_MS) return;
        if (mc.currentScreen != null) return;
        if (mc.player.distanceTo(target) > throwRange.get()) return;

        Slot slot = findHealSplashSlot();
        if (slot == null) return;
        if (PlayerInventoryUtil.isItemOnCooldown(slot.getStack())) return;

        lastThrowMs = now;

        float[] rotation = calcThrowRotation(target);
        PlayerUtils.setNextInteractRotation(rotation[0], rotation[1]);

        NotificationsOverlay.push(Text.literal("Кинул исцеление под ").append(target.getName()),
                1200, NotificationsOverlay.Kind.INFORMATION, slot.getStack());
        throwFromSlot(slot);
    }

    /**
     * Ищет взрывное зелье Исцеления именного 1 уровня (мгновенное здоровье,
     * усилитель 0) в инвентаре, оффхенд не трогаем.
     */
    private Slot findHealSplashSlot() {
        for (Slot slot : mc.player.currentScreenHandler.slots) {
            if (slot.id == 45) continue;

            ItemStack stack = slot.getStack();
            if (!stack.isOf(Items.SPLASH_POTION)) continue;

            PotionContentsComponent contents = stack.getOrDefault(
                    DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT);
            for (StatusEffectInstance effect : contents.getEffects()) {
                if (effect.getEffectType() == StatusEffects.INSTANT_HEALTH
                        && effect.getAmplifier() == 0) {
                    return slot;
                }
            }
        }
        return null;
    }

    /**
     * Поворот на ноги цели с компенсацией гравитации зелья: чем дальше цель,
     * тем сильнее бросок уходит вверх.
     */
    private float[] calcThrowRotation(LivingEntity target) {
        Vec3d eyes = mc.player.getEyePos();
        Vec3d aimPoint = target.getBoundingBox().getCenter()
                .add(0, -target.getHeight() * 0.25, 0);

        double dx = aimPoint.x - eyes.x;
        double dy = aimPoint.y - eyes.y;
        double dz = aimPoint.z - eyes.z;
        double horizontal = Math.sqrt(dx * dx + dz * dz);

        float yaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
        float pitch = (float) (-Math.toDegrees(Math.atan2(dy, horizontal)) - horizontal * 4.0);

        return new float[]{yaw, MathHelper.clamp(pitch, -89.0f, 89.0f)};
    }

    /**
     * Кидает зелье из указанного слота и возвращает прежний предмет в руку.
     * Поворот на цель берётся из setNextInteractRotation внутри useMainHandItem.
     */
    private void throwFromSlot(Slot slot) {
        int selected = mc.player.getInventory().getSelectedSlot();
        PlayerUtils.disableMoveKeys();

        if (isHotbar(slot.id)) {
            int hotbar = slot.id - 36;
            mc.player.getInventory().setSelectedSlot(hotbar);
            PlayerInventoryUtil.sendSelectedSlotPacket(hotbar);
            PlayerInventoryUtil.useMainHandItem(true);
            PlayerUtils.postScript.addTickStep(3, () -> {
                mc.player.getInventory().setSelectedSlot(selected);
                PlayerInventoryUtil.sendSelectedSlotPacket(selected);
                PlayerInventoryUtil.updateSlots();
                PlayerUtils.enableMoveKeys();
            });
        } else {
            // Зелье в инвентаре: свапаем в текущий слот хотбара, кидаем, свапаем обратно
            PlayerInventoryUtil.clickSlot(slot.id, selected, SlotActionType.SWAP);
            PlayerInventoryUtil.useMainHandItem(true);
            PlayerUtils.postScript.addTickStep(3, () -> {
                PlayerInventoryUtil.clickSlot(slot.id, selected, SlotActionType.SWAP);
                PlayerInventoryUtil.updateSlots();
                PlayerUtils.enableMoveKeys();
            });
        }
    }

    private boolean isHotbar(int slotId) {
        return slotId >= 36 && slotId <= 44;
    }
}

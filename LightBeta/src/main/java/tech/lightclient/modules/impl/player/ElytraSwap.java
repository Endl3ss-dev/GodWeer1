package tech.lightclient.modules.impl.player;

import tech.lightclient.client.events.impl.client.EventKey;
import tech.lightclient.client.events.impl.client.TickEvent;
import tech.lightclient.client.utils.client.other.Script;
import tech.lightclient.client.utils.eventbus.EventHandler;
import tech.lightclient.client.utils.player.PlayerInventoryUtil;
import tech.lightclient.client.utils.player.swap.InvBypassMode;
import tech.lightclient.client.utils.player.swap.InventoryTasks;
import tech.lightclient.client.utils.player.swap.SwapConfig;
import tech.lightclient.client.utils.player.swap.SwapInventory;
import tech.lightclient.modules.api.Category;
import tech.lightclient.modules.api.Function;
import tech.lightclient.modules.api.FunctionAdd;
import tech.lightclient.modules.api.settings.impl.BindSetting;
import tech.lightclient.modules.api.settings.impl.BooleanSetting;
import tech.lightclient.modules.api.settings.impl.ModeSetting;
import tech.lightclient.modules.api.settings.impl.SliderSetting;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.screen.slot.Slot;

@FunctionAdd(name = "ElytraSwap", alias = "Elytra Swap", category = Category.Player, description = "Быстрый свап на элитры и авто-использование фейерверков")
public class ElytraSwap extends Function {
    BindSetting elytraSetting = new BindSetting("Свап на элитры", -1);
    BindSetting fireworkSetting = new BindSetting("Использовать фейерверк", -1);
    BooleanSetting startSetting = new BooleanSetting("Быстрый старт", false);
    BooleanSetting autoFirework = new BooleanSetting("Авто-фейерверк", false);
    SliderSetting autoFireworkSetting = new SliderSetting("Авто фейерверк", 200, 100, 500, 25).setVisible(() -> autoFirework.get());
    ModeSetting swapMode = new ModeSetting("Режим свапа", "FunTime", "FunTime", "HolyWorld");

    Script script = new Script();
    long lastFireworkTime = 0;

    public ElytraSwap() {
        addSettings(elytraSetting, fireworkSetting, startSetting, autoFirework, autoFireworkSetting, swapMode);
    }

    @EventHandler
    public void onKey(EventKey e) {
        if (!script.isFinished() || e.getAction() != 1) return;

        if (e.isKeyDown(elytraSetting.get())) {
            Slot slot = chestPlate();
            if (slot != null) {
                Slot fireWork = PlayerInventoryUtil.getSlot(Items.FIREWORK_ROCKET);
                boolean shouldStart = slot.getStack().getItem().equals(Items.ELYTRA);

                swapArmor(slot);

                if (startSetting.get() && fireWork != null && shouldStart) script.cleanup().addTickStep(4, () -> {
                    if (mc.player.isOnGround()) mc.player.jump();
                }).addTickStep(3, () -> {
                    mc.player.startGliding();
                    mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
                    useFireworkWithoutSwap(false);
                });
            }
        } else if (e.isKeyDown(fireworkSetting.get()) && mc.player.isGliding()) {
            useFireworkWithoutSwap(false);
        }
    }

    @EventHandler
    public void onTick(TickEvent e) {
        if (nullCheck()) return;
        script.update();

        float fireworkUseTime = autoFireworkSetting.get();

        if (autoFirework.get() && mc.player.isGliding()) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastFireworkTime >= fireworkUseTime) {
                if (PlayerInventoryUtil.getSlot(Items.FIREWORK_ROCKET) != null) {
                    useFireworkWithoutSwap(true);
                }
            }
        }
    }

    /**
     * FunTime - фазовый свап с короткой остановкой движения.
     * HolyWorld - мгновенный свап через очередь, без остановки.
     */
    void swapArmor(Slot slot) {
        if (slot == null) return;

        if (swapMode.is("HolyWorld")) {
            SwapConfig.invBypassMode = InvBypassMode.HOLY_WORLD;
            SwapConfig.inventoryStopMoving = false;
            if (!InventoryTasks.isRealCompleted(ElytraSwap.class)) return;
            int from = slot.id;
            InventoryTasks.addTask(ElytraSwap.class, () -> SwapInventory.swapSlots(from, 6, true));
        } else {
            PlayerInventoryUtil.swapItemsPhasedDirect(slot.id, 6, true);
        }
    }

    Slot chestPlate() {
        ItemStack chest = mc.player.getEquippedStack(EquipmentSlot.CHEST);
        if (chest.getItem().equals(Items.ELYTRA)) {
            return PlayerInventoryUtil.slots()
                    .filter(s -> s.getStack().getItem().toString().contains("chestplate"))
                    .findFirst().orElse(null);
        } else {
            return PlayerInventoryUtil.getSlot(Items.ELYTRA);
        }
    }

    public void useFireworkWithoutSwap(boolean setTime) {
        if (!mc.player.isGliding()) return;
        if (PlayerInventoryUtil.getSlot(Items.FIREWORK_ROCKET) == null) return;

        long now = System.currentTimeMillis();
        if (autoFirework.get() && now - lastFireworkTime < autoFireworkSetting.get()) return;

        PlayerInventoryUtil.swapAndUseFromHotbar(Items.FIREWORK_ROCKET);

        if (setTime) lastFireworkTime = now;
    }
}

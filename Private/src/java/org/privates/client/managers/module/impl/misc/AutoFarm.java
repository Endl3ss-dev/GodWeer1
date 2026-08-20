package org.privates.client.managers.module.impl.misc;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.network.play.client.CPlayerTryUseItemOnBlockPacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.scoreboard.Score;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import org.privates.client.api.annotations.Category1;
import org.privates.client.api.annotations.Funtime;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.component.impl.rotation.Rotation;
import org.privates.client.managers.component.impl.rotation.RotationComponent;
import org.privates.client.managers.events.other.ScoreBoardEvent;
import org.privates.client.managers.events.player.StopUseItemEvent;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.managers.module.settings.impl.StringSetting;
import org.privates.client.utils.player.InvUtil;
import org.privates.client.utils.player.MoveUtil;
import org.privates.lib.util.time.StopWatch;

import java.util.List;
import java.util.stream.IntStream;

@Category1
@Funtime
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "AutoFarm", category = Category.MISC, description = "Старый фарм с озеленителем на фт и спуки")
public class AutoFarm extends Module {
    private final StopWatch watchClose = new StopWatch();
    private final StopWatch watchOther = new StopWatch();
    private final StopWatch watchExperienceDelay = new StopWatch();
    private final StopWatch watchSyncItems = new StopWatch();
    private final StopWatch watchBalanceCheck = new StopWatch();
    private boolean autoRepair;
    private final String lastBalanceMessage = null;
    private long balance = 0;

    private final BooleanSetting experimental = new BooleanSetting(this, "Экспериментальный", false);
    private final BooleanSetting clanInvestor = new BooleanSetting(this, "Клан Инвестор", false)
            .setVisible(() -> experimental.getValue());
    private final BooleanSetting autoPay = new BooleanSetting(this, "Автопей", false)
            .setVisible(() -> experimental.getValue() && !clanInvestor.getValue());
    private final StringSetting payNickname = new StringSetting(this, "Ник для автопея", "player")
            .setVisible(() -> experimental.getValue() && autoPay.getValue());
    private final SliderSetting value = new SliderSetting(this, "Порог баланса (млн)", 5F, 1.0F, 50F, 1F)
            .setVisible(() -> experimental.getValue() && (clanInvestor.getValue() || autoPay.getValue()));

    @Override
    public void toggle() {
        super.toggle();
        autoRepair = false;
        balance = 0;
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @EventHandler
    public void onStopUseItem(StopUseItemEvent e) {
        e.setCancelled(mc.player.getFoodStats().needFood());
    }

    @EventHandler
    public void onScoreboardUpdate(ScoreBoardEvent event) {
        for (Pair<Score, ITextComponent> pair : event.getList()) {
            String component = TextFormatting.removeFormatting(pair.getSecond().getString());
            if (component.contains("Монет:")) {
                String[] splitted = component.split(":");
                if (splitted.length > 1) {
                    try {
                        this.balance = Long.parseLong(splitted[1].trim());
                        break;
                    } catch (NumberFormatException ignored) {
                        resetBalance();
                    }
                } else {
                    resetBalance();
                }
            }
        }
    }

    @EventHandler
    public void onUpdate(UpdateEvent e) {
        List<Item> hoeItems = List.of(Items.NETHERITE_HOE, Items.DIAMOND_HOE);
        List<Item> plantsItems = List.of(Items.CARROT, Items.POTATO);
        List<Item> expItems = List.of(Items.EXPERIENCE_BOTTLE);
        Slot expSlot = InvUtil.getInventorySlot(expItems);
        Slot plantSlot = InvUtil.getInventorySlot(plantsItems);
        Slot hoeSlot = InvUtil.getInventorySlot(hoeItems);
        Slot foodSlot = InvUtil.getFoodMaxSaturationSlot();
        Item mainHandItem = mc.player.getHeldItemMainhand().getItem();
        Item offHandItem = mc.player.getHeldItemOffhand().getItem();
        if (hoeSlot == null || MoveUtil.isMoving() || !watchClose.finished(400)) return;

        float itemStrength = 1 - MathHelper.clamp((float) hoeSlot.getStack().getDamage() / (float) hoeSlot.getStack().getMaxDamage(), 0, 1);
        autoRepair = itemStrength < 0.05 || (itemStrength != 1 && autoRepair);

        RotationComponent.update(new Rotation(Rotation.cameraYaw(), 82), 360, 360, 0, 5);
        if (watchSyncItems.finished(60000)) {
            mc.playerController.syncCurrentPlayItem();
            watchSyncItems.reset();
        }

        if (experimental.getValue() && watchBalanceCheck.finished(60000)) {
            long threshold = (long) (value.getValue() * 1_000_000);
            if (balance >= threshold + 1_000_000) {
                long amount = balance - 1_000_000;
                if (clanInvestor.getValue()) {
                    mc.player.sendChatMessage("/clan invest " + amount);
                } else if (autoPay.getValue()) {
                    mc.player.sendChatMessage("/pay " + payNickname.getValue() + " " + amount);
                }
                balance = 1_000_000;
            }
            watchBalanceCheck.reset();
        }

        if (!autoRepair && offHandItem.equals(Items.EXPERIENCE_BOTTLE) && hoeItems.contains(mainHandItem) && watchExperienceDelay.finished(50)) {
            mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.OFF_HAND));
            watchExperienceDelay.reset();
            return;
        }

        if (!plantsItems.contains(offHandItem) && !offHandItem.equals(Items.EXPERIENCE_BOTTLE) && plantSlot != null && !containerScreen() && !autoRepair && watchOther.finished(1000)) {
            InvUtil.clickSlot(plantSlot, 40, ClickType.SWAP, false);
            watchOther.reset();
            return;
        }

        if (plantSlot == null && hoeItems.contains(mainHandItem) && !autoRepair) {
            mc.rightClickMouse();
            return;
        }

        if (mc.player.getFoodStats().needFood() && foodSlot != null) {
            if (!offHandItem.equals(foodSlot.getStack().getItem()) && !containerScreen()) {
                InvUtil.swapHand(foodSlot, Hand.OFF_HAND, false);
            } else {
                mc.playerController.processRightClick(mc.player, mc.world, Hand.OFF_HAND);
            }
        } else if (mc.player.inventory.getFirstEmptyStack() == -1) {
            if (!plantsItems.contains(offHandItem) && !containerScreen()) {
                InvUtil.swapHand(plantSlot.getSlotStackLimit(), Hand.OFF_HAND, false);
            }
            if (mc.currentScreen instanceof ContainerScreen<?> screen) {
                if (screen.getTitle().getString().equals("● Выберите секцию")) {
                    InvUtil.clickSlotId(21, 0, ClickType.PICKUP, true);
                    return;
                }
                if (screen.getTitle().getString().equals("Скупщик еды")) {
                    InvUtil.clickSlotId(offHandItem.equals(Items.CARROT) ? 10 : 11, 0, ClickType.PICKUP, true);
                    mc.player.closeScreen();
                    return;
                }
            }
            if (watchOther.every(1000)) {
                mc.player.sendChatMessage("/buyer");
            }
        } else if (autoRepair) {
            if (InvUtil.getInventoryCount(Items.EXPERIENCE_BOTTLE) * 7 > hoeSlot.getStack().getDamage()) {
                if (containerScreen()) return;

                if (!offHandItem.equals(Items.EXPERIENCE_BOTTLE) && watchExperienceDelay.finished(1000)) {
                    InvUtil.clickSlot(expSlot, 40, ClickType.SWAP, false);
                    watchExperienceDelay.reset();
                    return;
                }
                if (offHandItem.equals(Items.EXPERIENCE_BOTTLE) && hoeItems.contains(mainHandItem) && watchExperienceDelay.finished(40)) {
                    mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.OFF_HAND));
                    watchExperienceDelay.reset();
                    return;
                }
            } else if (watchOther.finished(1000)) {
                if (mc.currentScreen instanceof ContainerScreen<?> screen) {
                    String title = screen.getTitle().getString();
                    if (title.contains("Пузырёк опыта") || title.contains("Пузырек опыта")) {
                        Slot cheapestSlot = cheapestexp(screen);
                        if (cheapestSlot != null) {
                            InvUtil.clickSlot(cheapestSlot, 0, ClickType.QUICK_MOVE, true);
                            watchOther.setMs(500);
                            return;
                        }
                    } else if (title.contains("Подозрительная цена")) {
                        InvUtil.clickSlotId(0, 0, ClickType.QUICK_MOVE, true);
                        watchOther.setMs(500);
                        return;
                    }
                }
                mc.player.sendChatMessage("/ah search Пузырёк Опыта");
                watchOther.reset();
            }
        } else if (watchOther.finished(500)) {
            BlockPos pos = mc.player.getPosition();
            if (mc.world.getBlockState(pos).getBlock().equals(Blocks.FARMLAND) && !autoRepair) {
                if (hoeItems.contains(mainHandItem) && plantsItems.contains(offHandItem)) {
                    mc.player.connection.sendPacket(new CPlayerTryUseItemOnBlockPacket(Hand.OFF_HAND, new BlockRayTraceResult(mc.player.getPositionVec(), Direction.UP, pos, false)));
                    IntStream.range(0, 3).forEach(i -> mc.player.connection.sendPacket(new CPlayerTryUseItemOnBlockPacket(Hand.MAIN_HAND, new BlockRayTraceResult(mc.player.getPositionVec(), Direction.UP, pos.up(), false))));
                    mc.player.connection.sendPacket(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.START_DESTROY_BLOCK, pos.up(), Direction.UP));
                } else if (!containerScreen()) {
                    if (!plantsItems.contains(offHandItem) && !offHandItem.equals(Items.EXPERIENCE_BOTTLE))
                        InvUtil.clickSlot(plantSlot, 40, ClickType.SWAP, false);
                    if (!hoeItems.contains(mainHandItem))
                        InvUtil.swapHand(hoeSlot.getSlotStackLimit(), Hand.MAIN_HAND, false);
                }
            }
        }

        if (!autoRepair && !plantsItems.contains(offHandItem) && !offHandItem.equals(Items.EXPERIENCE_BOTTLE) && plantSlot != null && !containerScreen() && watchOther.finished(500)) {
            InvUtil.clickSlot(plantSlot, 40, ClickType.SWAP, false);
        }
    }

    private Slot cheapestexp(ContainerScreen<?> screen) {
        Slot cheapestSlot = null;
        int cheapestPrice = Integer.MAX_VALUE;

        for (Slot slot : screen.getContainer().inventorySlots) {
            if (slot.slotNumber > 44) continue;
            ItemStack stack = slot.getStack();

            if (stack.getItem() == Items.EXPERIENCE_BOTTLE && stack.getCount() == 64) {
                int price = pricestack(stack);
                if (price != -1 && price < cheapestPrice) {
                    cheapestPrice = price;
                    cheapestSlot = slot;
                }
            }
        }

        return cheapestSlot;
    }

    private int pricestack(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        if (tag != null && tag.contains("display", 10)) {
            CompoundNBT display = tag.getCompound("display");
            if (display.contains("Lore", 9)) {
                ListNBT lore = display.getList("Lore", 8);
                for (int j = 0; j < lore.size(); ++j) {
                    JsonObject object = new JsonParser().parse(lore.getString(j)).getAsJsonObject();
                    if (object.has("extra")) {
                        JsonArray array = object.getAsJsonArray("extra");
                        if (array.size() > 2) {
                            JsonObject title = array.get(1).getAsJsonObject();
                            if (title.get("text").getAsString().trim().toLowerCase().contains("ценa")) {
                                String line = array.get(2).getAsJsonObject().get("text").getAsString().trim().substring(1).replaceAll("[ ,]", "");
                                try {
                                    return Integer.parseInt(line);
                                } catch (NumberFormatException e) {
                                    return -1;
                                }
                            }
                        }
                    }
                }
            }
        }
        return -1;
    }

    private void resetBalance() {
        this.balance = 0;
    }

    public boolean containerScreen() {
        if (mc.currentScreen instanceof ContainerScreen<?>) {
            mc.player.closeScreen();
            watchClose.reset();
            return true;
        }
        return false;
    }
}
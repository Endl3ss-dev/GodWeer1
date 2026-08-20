package org.privates.client.managers.module.impl.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import org.privates.client.api.annotations.Category1;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.chat.ChatUtil;
import org.privates.client.utils.other.Instance;

import java.util.ArrayList;
import java.util.List;
@Category1
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "SoulFarm", category = Category.MISC, description = "Ломает тыквы на варпах")
public class SoulFarm extends Module {
    public static SoulFarm getInstance() {
        return Instance.get(SoulFarm.class);
    }

    public ModeSetting serv = new ModeSetting(this, "Сервер", "CakeWorld");
    public ModeSetting griefMode = new ModeSetting(this, "Собрать на", "3 Грифах", "На данном грифе");
    public SliderSetting headsCount = new SliderSetting(this, "Кол-во тыкв", 1f, 2f, 27f, 1f);
    public SliderSetting maxRetries = new SliderSetting(this, "Макс. попыток", 3f, 1f, 10f, 1f);
    public BooleanSetting skipGrief2 = new BooleanSetting(this, "Пропускать 2 гриф", false);
    public BooleanSetting avoidPvP = new BooleanSetting(this, "Избегать PvP", true);

    private String currentWarp = "zaharovfunction1";
    private int currentGrif = 1;
    private int stage = 0;
    private int delay = 0;
    private BlockPos targetHead = null;
    private int breakTicks = 0;
    private boolean isBreaking = false;
    private boolean isOnGrief = false;
    private int breakAttempts = 0;
    private int breakTimeout = 0;
    private int retryCount = 0;
    private final List<String> failedHeads = new ArrayList<>();
    private boolean pvpDetected = false;

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (delay > 0) {
            delay--;
            return;
        }

        if (avoidPvP.getValue() && isPvP()) {
            if (!pvpDetected) {
                ChatUtil.addText("pvp");
                pvpDetected = true;
            }
            mc.player.sendChatMessage("/hub");
            stage = 9;
            delay = 60;
            return;
        } else if (pvpDetected) {
            pvpDetected = false;
        }

        switch (stage) {
            case 0:
                if (griefMode.is("На данном грифе")) {
                    isOnGrief = true;
                    stage = 3;
                } else {
                    if (!isOnGrief) {
                        useCompass();
                        stage = 1;
                        delay = 20;
                    } else {
                        stage = 3;
                    }
                }
                break;

            case 1:
                if (clickCraftingTable()) {
                    stage = 2;
                    delay = 20;
                } else {
                    stage = 0;
                    delay = 10;
                }
                break;

            case 2:
                if (clickCurrentGrief()) {
                    isOnGrief = true;
                    stage = 3;
                    delay = 30;
                } else {
                    stage = 2;
                    delay = 10;
                }
                break;

            case 3:
                if (!isOnGrief) {
                    stage = 0;
                    delay = 10;
                    break;
                }
                String warpCommand = currentWarp;
                if (currentWarp.equals("zaharovfunction8")) {
                    warpCommand = "zaharovfunction8_";
                }
                mc.player.sendChatMessage("/warp " + warpCommand);
                stage = 5;
                delay = 10;
                break;

            case 4:
                stage = 5;
                delay = 10;
                break;

            case 5:
                targetHead = findNearestHead();
                if (targetHead != null) {
                    stage = 6;
                    delay = 2;
                    breakAttempts = 0;
                    retryCount = 0;
                } else {
                    String failedHeadInfo = "Гриф " + currentGrif + ", Варп " + currentWarp;
                    if (!failedHeads.contains(failedHeadInfo)) {
                        failedHeads.add(failedHeadInfo);
                    }
                    stage = 8;
                    delay = 5;
                }
                break;

            case 6:
                lookAtHead(targetHead);
                stage = 7;
                delay = 1;
                break;

            case 7:
                if (!isBreaking) {
                    startBreakingHead(targetHead);
                    isBreaking = true;
                    breakTicks = 0;
                }

                breakTicks++;

                if (targetHead != null && mc.world.getBlockState(targetHead).isAir()) {
                    stopBreaking();
                    stage = 8;
                    delay = 5;
                    retryCount = 0;
                } else if (breakTicks >= 40) {
                    stopBreaking();
                    retryCount++;

                    if (retryCount < maxRetries.getValue()) {
                        ChatUtil.addText("Попытка " + retryCount + " сломать тыкву на варпе " + currentWarp);
                        stage = 6;
                        delay = 5;
                    } else {
                        String failedHeadInfo = "Гриф " + currentGrif + ", Варп " + currentWarp;
                        if (!failedHeads.contains(failedHeadInfo)) {
                            failedHeads.add(failedHeadInfo);
                        }
                        ChatUtil.addText("Не удалось сломать тыкву после " + retryCount + " попыток, переходим к следующей");
                        stage = 8;
                        delay = 5;
                        retryCount = 0;
                    }
                }
                break;

            case 8:
                stopBreaking();
                switchToNextWarp();
                break;

            case 9:
                stage = 0;
                delay = 10;
                break;
        }
    }

    private void switchToNextWarp() {
        int currentNumber = Integer.parseInt(currentWarp.replace("zaharovfunction", ""));
        int nextNumber = currentNumber + 1;
        float maxHeads = headsCount.getValue();

        float actualMaxHeads = maxHeads;

        if (nextNumber <= actualMaxHeads) {
            currentWarp = "zaharovfunction" + nextNumber;
            stage = 3;
            delay = 10;
        } else {
            if (griefMode.is("3 Грифах")) {
                if (skipGrief2.getValue() && currentGrif == 1) {
                    currentGrif = 3;
                } else {
                    currentGrif++;
                }

                if (currentGrif <= 3) {
                    printFailedHeads();
                    mc.player.sendChatMessage("/hub");
                    currentWarp = "zaharovfunction1";
                    isOnGrief = false;
                    stage = 9;
                    delay = 60;
                } else {
                    printFailedHeads();
                    this.toggle();
                }
            } else {
                printFailedHeads();
                this.toggle();
            }
        }
    }

    private void printFailedHeads() {
        if (!failedHeads.isEmpty()) {
            ChatUtil.addText("Не удалось собрать тыквы на:");
            for (String failedHead : failedHeads) {
                ChatUtil.addText("- " + failedHead);
            }
            failedHeads.clear();
        }
    }

    private void useCompass() {
        for (int i = 0; i < 9; i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == Items.COMPASS) {
                mc.player.connection.sendPacket(new CHeldItemChangePacket(i));
                mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
                mc.player.connection.sendPacket(new CHeldItemChangePacket(mc.player.inventory.currentItem));
                break;
            }
        }
    }

    private boolean clickCraftingTable() {
        if (mc.player.openContainer != null) {
            for (int i = 0; i < mc.player.openContainer.inventorySlots.size(); i++) {
                String displayName = mc.player.openContainer.getSlot(i).getStack().getDisplayName().getString();
                if (displayName.contains("ГРИФЕРСКОЕ ВЫЖИВАНИЕ")) {
                    mc.playerController.windowClick(mc.player.openContainer.windowId, i, 0, ClickType.PICKUP, mc.player);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean clickCurrentGrief() {
        if (mc.player.openContainer != null) {
            String griefName = "ГРИФ #" + currentGrif;
            for (int i = 0; i < mc.player.openContainer.inventorySlots.size(); i++) {
                try {
                    String displayName = mc.player.openContainer.getSlot(i).getStack().getDisplayName().getString();
                    if (displayName.contains(griefName)) {
                        mc.playerController.windowClick(mc.player.openContainer.windowId, i, 0, ClickType.PICKUP, mc.player);
                        return true;
                    }
                } catch (Exception e) {
                }
            }
        }
        return false;
    }

    private BlockPos findNearestHead() {
        BlockPos playerPos = new BlockPos(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ());
        int radius = 3;
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos checkPos = playerPos.add(x, y, z);
                    BlockState state = mc.world.getBlockState(checkPos);
                    Block block = state.getBlock();
                    if ((block == Blocks.PLAYER_HEAD || block == Blocks.PLAYER_WALL_HEAD) && !state.isAir()) {
                        return checkPos;
                    }
                }
            }
        }
        return null;
    }

    private void lookAtHead(BlockPos pos) {
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.5;
        double z = pos.getZ() + 0.5;
        Vector3d eyePos = mc.player.getEyePosition(1.0F);
        double deltaX = x - eyePos.x;
        double deltaY = y - eyePos.y;
        double deltaZ = z - eyePos.z;
        double horizontalDistance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
        float yaw = (float) (Math.atan2(deltaZ, deltaX) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float) (-(Math.atan2(deltaY, horizontalDistance) * 180.0D / Math.PI));
        mc.player.rotationYaw = yaw;
        mc.player.rotationPitch = pitch;
    }

    private void startBreakingHead(BlockPos pos) {
        if (mc.currentScreen != null) {
            mc.displayScreen(null);
        }
        mc.gameSettings.keyBindAttack.setPressed(true);
    }

    private void stopBreaking() {
        mc.gameSettings.keyBindAttack.setPressed(false);
        isBreaking = false;
    }

    private boolean isPvP() {
        if (mc.ingameGUI.overlayPlayerList.header != null) {
            String header = mc.ingameGUI.overlayPlayerList.header.getString();
            return header.contains("PvP");
        }
        return false;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        currentWarp = "zaharovfunction1";
        currentGrif = 1;
        stage = 0;
        delay = 0;
        targetHead = null;
        breakTicks = 0;
        isBreaking = false;
        isOnGrief = false;
        breakAttempts = 0;
        breakTimeout = 0;
        retryCount = 0;
        failedHeads.clear();
        pvpDetected = false;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        stopBreaking();
        currentWarp = "zaharovfunction1";
        currentGrif = 1;
        stage = 0;
        delay = 0;
        targetHead = null;
        breakTicks = 0;
        isBreaking = false;
        isOnGrief = false;
        breakAttempts = 0;
        breakTimeout = 0;
        retryCount = 0;
        failedHeads.clear();
        pvpDetected = false;
    }
}
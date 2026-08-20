package org.privates.client.managers.module.impl.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.block.Blocks;
import net.minecraft.network.play.client.CConfirmTeleportPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import org.privates.client.api.annotations.Category3;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.other.PacketEvent;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.utils.other.Instance;
import org.privates.lib.util.time.StopWatch;

@Category3
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "PortalGodMode", category = Category.PLAYER, description = "Год мод после входа в портал")
public class PortalGodMode extends Module {
    public static PortalGodMode getInstance() {
        return Instance.get(PortalGodMode.class);
    }

    private final BooleanSetting autoDisable = new BooleanSetting(this, "Авто выключение", true);
    private final BooleanSetting onlyNether = new BooleanSetting(this, "Только незер порталы", true);

    private final StopWatch confirmTimer = new StopWatch();
    private boolean teleported = false;
    private boolean nearPortal = false;

    @Override
    public void onEnable() {
        super.onEnable();
        confirmTimer.setMs(99999);
        teleported = false;
        nearPortal = false;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        teleported = false;
        nearPortal = false;
    }

    @EventHandler

    public void onPacket(PacketEvent event) {
        if (event.isSend() && event.getPacket() instanceof CConfirmTeleportPacket) {
            if (confirmTimer.elapsedTime() < 5000 && nearPortal) {
                teleported = true;
                event.cancel();
            }
        }
    }

    @EventHandler

    public void onUpdate(UpdateEvent event) {
        if (mc.player == null || mc.world == null) return;

        checkNearPortal();

        if (autoDisable.getValue() && teleported && !nearPortal) {
            this.toggle();
        }
    }

    private void checkNearPortal() {
        nearPortal = false;
        BlockPos playerPos = mc.player.getPosition();

        for (int x = -2; x <= 2; x++) {
            for (int y = -2; y <= 2; y++) {
                for (int z = -2; z <= 2; z++) {
                    BlockPos checkPos = playerPos.add(x, y, z);

                    if (onlyNether.getValue()) {
                        if (mc.world.getBlockState(checkPos).getBlock() == Blocks.NETHER_PORTAL) {
                            nearPortal = true;
                            confirmTimer.reset();
                            return;
                        }
                    } else {
                        // Проверяем любые порталы
                        if (mc.world.getBlockState(checkPos).getBlock() == Blocks.NETHER_PORTAL ||
                                mc.world.getBlockState(checkPos).getBlock() == Blocks.END_PORTAL) {
                            nearPortal = true;
                            confirmTimer.reset();
                            return;
                        }
                    }
                }
            }
        }
    }

    public String getDisplayInfo() {
        if (teleported) {
            return TextFormatting.GREEN + "God";
        } else if (confirmTimer.elapsedTime() < 5000 && nearPortal) {
            return TextFormatting.YELLOW + "Ready";
        } else {
            return TextFormatting.GRAY + "Waiting";
        }
    }

    public boolean isTeleported() {
        return teleported;
    }

    public boolean isNearPortal() {
        return nearPortal;
    }

    public boolean isReady() {
        return confirmTimer.elapsedTime() < 5000 && nearPortal;
    }
}
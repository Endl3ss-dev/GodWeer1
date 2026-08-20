package org.privates.client.managers.module.impl.movement;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.settings.KeyBinding;
import org.privates.client.api.annotations.Category2;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.other.PacketEvent;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.impl.player.FreeCam;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.utils.math.PerfectDelay;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.player.PlayerUtil;

@Category2
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "Sprint", category = Category.MOVEMENT, description = "Авто спринтит то есть автобег ")
public class Sprint extends Module {

    public static Sprint getInstance() {
        return Instance.get(Sprint.class);
    }

    @Setter
    boolean canSprint = true;

    final BooleanSetting ignoreHunger = new BooleanSetting(this, "Ignore Hunger", false);
    final PerfectDelay timerUtility = new PerfectDelay();
    final boolean isResetting = false;

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null || FreeCam.getInstance().isEnabled()) {
            return;
        }
        if (mc.player.isSprinting()) {
            timerUtility.reset();
        }
        boolean sprintCondition = canSprint && (ignoreHunger.getValue() || mc.player.getFoodStats().getFoodLevel() > 6);
        mc.gameSettings.keyBindSprint.setPressed(sprintCondition);
    }

    @EventHandler
    public void onPacket(PacketEvent event) {
        if (mc.player == null || !event.isSend()) {
        }

    }

    public void setCanSprint(boolean canSprint) {
        this.canSprint = canSprint;
    }

    public void resetTimer() {
        timerUtility.reset();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (PlayerUtil.isHoly() || PlayerUtil.isConnectedToServer("spookytime")) {
            toggle();
            return;
        }
        canSprint = true;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        canSprint = true;
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.keyCode, false);
    }
}
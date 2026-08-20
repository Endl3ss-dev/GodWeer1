package org.privates.client.managers.module.impl.player;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.network.play.server.SPlaySoundEffectPacket;
import net.minecraft.util.Hand;
import org.privates.client.api.annotations.Category2;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.other.PacketEvent;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.utils.other.Instance;
import org.privates.lib.util.time.StopWatch;

@Category2
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "AutoFish", category = Category.PLAYER, description = "Андрюха рыбалка цытата <Лучше плохой день на рыбалке чем хороший день на работе>")
public class AutoFish extends Module {
    public static AutoFish getInstance() {
        return Instance.get(AutoFish.class);
    }

    private final StopWatch delay = new StopWatch();
    private boolean isHooked = false;
    private boolean needToHook = false;

    @Override
    public void toggle() {
        super.toggle();
        delay.reset();
        isHooked = false;
        needToHook = false;
    }

    @EventHandler
    public void onEvent(PacketEvent event) {
        if (mc.player == null) return;
        if (event.getPacket() instanceof SPlaySoundEffectPacket wrapper) {
            if (wrapper.getSound().getName().getPath().equals("entity.fishing_bobber.splash")) {
                mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
                needToHook = true;
                delay.reset();
            }
        }
    }

    @EventHandler
    public void onEvent(UpdateEvent event) {
        if (delay.finished(200) && needToHook) {
            mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
            needToHook = false;
            delay.reset();
        }
    }
}
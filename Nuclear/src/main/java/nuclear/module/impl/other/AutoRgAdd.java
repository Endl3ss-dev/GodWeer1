package nuclear.module.impl.other;

import net.minecraft.network.play.server.SChatPacket;
import nuclear.control.Manager;
import nuclear.control.events.Event;
import nuclear.control.events.impl.packet.EventPacket;
import nuclear.control.events.impl.player.EventUpdate;
import nuclear.control.friend.Friend;
import nuclear.module.api.Module;
import nuclear.module.api.ModuleCategory;
import nuclear.module.api.ModuleDescriptor;
import nuclear.module.settings.imp.BooleanSetting;
import nuclear.module.settings.imp.SliderSetting;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

@ModuleDescriptor(
    name = "AutoRgAdd",
    type = ModuleCategory.Other,
    desc = "Queues friend names after region creation and adds them one by one"
)
public class AutoRgAdd extends Module {
    private final BooleanSetting onlyFriends = new BooleanSetting("Only friends", true);
    private final BooleanSetting skipSelf = new BooleanSetting("Skip self", true);
    private final SliderSetting delayMs = new SliderSetting("Delay", 500.0f, 100.0f, 3000.0f, 100.0f);

    private final Set<String> pendingNames = new LinkedHashSet<String>();
    private long lastCommandTime;

    public AutoRgAdd() {
        addSettings(onlyFriends, skipSelf, delayMs);
    }

    @Override
    public boolean onEvent(Event event) {
        if (mc.player == null || mc.world == null) {
            pendingNames.clear();
            return false;
        }

        if (event instanceof EventPacket) {
            handlePacket((EventPacket) event);
        } else if (event instanceof EventUpdate) {
            processQueue();
        }
        return false;
    }

    private void handlePacket(EventPacket event) {
        if (!event.isReceivePacket() || !(event.getPacket() instanceof SChatPacket)) {
            return;
        }

        SChatPacket packet = (SChatPacket) event.getPacket();
        String message = packet.getChatComponent().getString().toLowerCase(Locale.ROOT);
        if (!looksLikeRegionCreation(message)) {
            return;
        }

        if (onlyFriends.get() && Manager.FRIEND_MANAGER != null) {
            for (Friend friend : Manager.FRIEND_MANAGER.getFriends()) {
                if (friend == null || friend.getName() == null) {
                    continue;
                }
                String name = friend.getName().trim();
                if (name.isEmpty()) {
                    continue;
                }
                if (skipSelf.get() && name.equalsIgnoreCase(mc.player.getName().getString())) {
                    continue;
                }
                pendingNames.add(name);
            }
            return;
        }

        mc.world.getPlayers().stream()
            .filter(player -> player != mc.player)
            .map(player -> player.getName().getString())
            .filter(name -> !skipSelf.get() || !name.equalsIgnoreCase(mc.player.getName().getString()))
            .forEach(pendingNames::add);
    }

    private boolean looksLikeRegionCreation(String message) {
        return message.contains("регион")
            || message.contains("region")
            || message.contains("приват")
            || message.contains("создан")
            || message.contains("created");
    }

    private void processQueue() {
        if (pendingNames.isEmpty()) {
            return;
        }

        long now = System.currentTimeMillis();
        if (now - lastCommandTime < delayMs.getValue().longValue()) {
            return;
        }

        Iterator<String> iterator = pendingNames.iterator();
        if (!iterator.hasNext()) {
            return;
        }

        String nextName = iterator.next();
        iterator.remove();

        mc.player.sendChatMessage("/rg addmember " + nextName);
        lastCommandTime = now;
    }
}

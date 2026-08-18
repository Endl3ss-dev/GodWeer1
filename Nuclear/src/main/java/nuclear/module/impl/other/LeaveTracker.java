package nuclear.module.impl.other;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import nuclear.control.events.Event;
import nuclear.control.events.impl.player.EventUpdate;
import nuclear.module.api.Module;
import nuclear.module.api.ModuleCategory;
import nuclear.module.api.ModuleDescriptor;
import nuclear.utils.ClientUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@ModuleDescriptor(name = "LeaveTracker", type = ModuleCategory.Other, desc = "Пишет кординаты ливнутых игроков из прогрузки")
public class LeaveTracker extends Module {
    private final Map<UUID, TrackedPlayer> trackedPlayers = new HashMap<>();
    private ClientWorld lastWorld;
    private boolean initialized;

    @Override
    public boolean onEvent(Event event) {
        if (!(event instanceof EventUpdate) || mc.player == null || mc.world == null) {
            return false;
        }

        if (mc.world != lastWorld) {
            lastWorld = mc.world;
            trackedPlayers.clear();
            initialized = false;
        }

        if (!initialized) {
            snapshotPlayers();
            initialized = true;
            return false;
        }

        Set<UUID> seenPlayers = new HashSet<>();
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player || !player.isAlive()) {
                continue;
            }

            UUID uuid = player.getUniqueID();
            seenPlayers.add(uuid);
            trackedPlayers.put(uuid, new TrackedPlayer(player.getScoreboardName(), player.getPosition()));
        }

        Iterator<Map.Entry<UUID, TrackedPlayer>> iterator = trackedPlayers.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, TrackedPlayer> entry = iterator.next();
            if (seenPlayers.contains(entry.getKey())) {
                continue;
            }

            TrackedPlayer tracked = entry.getValue();
            if (mc.player.getDistanceSq(tracked.pos.getX(), tracked.pos.getY(), tracked.pos.getZ()) < 65536.0) {
                ClientUtils.sendMessage(
                    TextFormatting.GRAY + tracked.name
                        + TextFormatting.WHITE + " вышел из прогрузки на "
                        + TextFormatting.GRAY + tracked.pos.getX() + " "
                        + tracked.pos.getY() + " "
                        + tracked.pos.getZ()
                );
            }
            iterator.remove();
        }
        return false;
    }

    @Override
    protected void onDisable() {
        trackedPlayers.clear();
        initialized = false;
    }

    private void snapshotPlayers() {
        trackedPlayers.clear();
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player || !player.isAlive()) {
                continue;
            }
            trackedPlayers.put(player.getUniqueID(), new TrackedPlayer(player.getScoreboardName(), player.getPosition()));
        }
    }

    private record TrackedPlayer(String name, BlockPos pos) {
    }
}

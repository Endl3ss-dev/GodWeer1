package org.privates.client.managers.module.impl.movement;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.block.BlockState;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CPlayerPacket;
import org.privates.client.api.annotations.Category3;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.other.PacketEvent;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.other.Instance;

import java.util.ArrayList;
import java.util.List;

@Category3
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "NoClip", description = "Позволяет вам проходить сквозь стены", category = Category.MOVEMENT)
public class NoClip extends Module {

    public static NoClip getInstance() {
        return Instance.get(NoClip.class);
    }

    private final List<IPacket<?>> bufferedPackets = new ArrayList<>();
    private final SliderSetting semiPackets = new SliderSetting(this, "Количество пакетов", 1, 1f, 30, 1f);

    private boolean semiPacketSent;
    private boolean skipReleaseOnDisable;

    @EventHandler
    public void onPacket(PacketEvent event) {
        if (mc.player == null || mc.player.connection == null) return;

        IPacket<?> packet = event.getPacket();
        if (packet instanceof CPlayerPacket) {
            bufferedPackets.add(packet);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null || mc.world == null) return;

        boolean noSolidInAABB = mc.world.getStatesInArea(mc.player.getBoundingBox().shrink(0.001D))
                .noneMatch(BlockState::isSolid);
        long totalStates = mc.world.getStatesInArea(mc.player.getBoundingBox().shrink(0.001D)).count();
        long solidStates = mc.world.getStatesInArea(mc.player.getBoundingBox().shrink(0.001D)).filter(BlockState::isSolid).count();
        boolean semiInsideBlock = solidStates > 0 && solidStates < totalStates;

        if (!semiPacketSent && semiInsideBlock) {
            double x = mc.player.getPosX();
            double y = mc.player.getPosY();
            double z = mc.player.getPosZ();
            float yaw = mc.player.rotationYaw;
            float pitch = mc.player.rotationPitch;
            boolean onGround = mc.player.isOnGround();
            for (int i = 0; i < semiPackets.getValue(); i++) {
                mc.player.connection.sendPacket(new CPlayerPacket.PositionRotationPacket(x, y, z, yaw, pitch, onGround));
            }
            semiPacketSent = true;
            return;
        }

        if (semiPacketSent && noSolidInAABB) {
            skipReleaseOnDisable = true;
            setEnabled(false);
        }
    }

    @Override
    protected void onDisable() {
        if (!skipReleaseOnDisable && semiPacketSent) {
            runReleaseSequence();
        }

        if (mc.player != null && mc.player.connection != null && !bufferedPackets.isEmpty()) {
            for (IPacket<?> packet : bufferedPackets) {
                mc.player.connection.sendPacket(packet);
            }
            bufferedPackets.clear();
        }

        super.onDisable();
    }

    @Override
    protected void onEnable() {
        bufferedPackets.clear();
        semiPacketSent = false;
        skipReleaseOnDisable = false;
        super.onEnable();
    }

    private void runReleaseSequence() {
        if (mc.player == null || mc.player.connection == null) return;

        double x = mc.player.getPosX();
        double y = mc.player.getPosY();
        double z = mc.player.getPosZ();
        float yaw = mc.player.rotationYaw;
        float pitch = mc.player.rotationPitch;

        mc.player.connection.sendPacket(new CPlayerPacket.PositionRotationPacket(x - 5000, y, z - 5000, yaw, pitch, false));
        mc.player.connection.sendPacket(new CPlayerPacket.PositionRotationPacket(x, y, z, yaw, pitch, mc.player.isOnGround()));
    }
}
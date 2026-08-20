package org.privates.client.managers.module.impl.movement;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.util.math.vector.Vector3d;
import org.privates.client.api.annotations.Category2;
import org.privates.client.api.annotations.Funtime;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.player.PlayerUtil;

@Category2
@Getter
@Funtime
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "Phase", category = Category.PLAYER)
public class Phase extends Module {
    static final double CLIP_DISTANCE = 0.00001;

    public static Phase getInstance() {
        return Instance.get(Phase.class);
    }

    @Override
    public void onEnable() {
        if (PlayerUtil.nullCheck()) return;

// Calculate movement based on player's look direction
        Vector3d lookVector = Minecraft.getInstance().player.getLook(1.0F).mul(CLIP_DISTANCE, 0, CLIP_DISTANCE);

// Calculate new position
        double newX = mc.player.getPosX() + lookVector.getX();
        double newZ = mc.player.getPosZ() + lookVector.getZ();
        double posY = mc.player.getPosY();

// Send multiple position packets to confirm the new position
        for (int i = 0; i < 5; i++) {
            mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(newX, posY, newZ, false));
        }

// Update client-side position
        mc.player.setPosition(newX, posY, newZ);

// Send additional position packets to ensure server synchronization
        for (int i = 0; i < 5; i++) {
            mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(newX, posY, newZ, false));
        }
    }
}
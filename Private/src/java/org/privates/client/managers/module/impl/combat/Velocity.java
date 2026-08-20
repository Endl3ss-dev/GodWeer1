package org.privates.client.managers.module.impl.combat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CConfirmTeleportPacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.network.play.server.SEntityVelocityPacket;
import net.minecraft.network.play.server.SPlayerPositionLookPacket;
import org.privates.client.api.annotations.Category1;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.other.PacketEvent;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.player.PlayerUtil;
import org.privates.common.impl.taskript.Script;

@Category1

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "Velocity", category = Category.COMBAT, description = "Ну кароче тебя не откидывает ты крутой")
public class Velocity extends Module {

    public static Velocity getInstance() {
        return Instance.get(Velocity.class);
    }

    private final ModeSetting mode = new ModeSetting(this, "Режим"
            , "Matrix", "Vulcan", "Intave", "AAC v4", "NCP", "Reverse");

    private final SliderSetting horizontal = new SliderSetting(this, "Горизонталь %", 0, 0, 100, 1);
    private final SliderSetting vertical = new SliderSetting(this, "Вертикаль %", 0, 0, 100, 1);

    private final Script script = new Script();
    boolean receivedTeleport;
    int teleportId;

    @Override
    public void onDisable() {
        super.onDisable();
        receivedTeleport = false;
        teleportId = 0;
    }

    @EventHandler
    public void onPacket(PacketEvent e) {
        if (PlayerUtil.nullCheck()) return;

        final IPacket<?> packet = e.getPacket();

        if (packet instanceof SEntityVelocityPacket wrapper && wrapper.getEntityID() == mc.player.getEntityId()) {
            float horizontalModifier = horizontal.getValue().floatValue() / 100f;
            float verticalModifier = vertical.getValue().floatValue() / 100f;

            int motionX = (int) (wrapper.getMotionX() * horizontalModifier);
            int motionY = (int) (wrapper.getMotionY() * verticalModifier);
            int motionZ = (int) (wrapper.getMotionZ() * horizontalModifier);

            switch (mode.getValue()) {
                case "Packet":
                    if (horizontal.getValue() == 0 && vertical.getValue() == 0) {
                        e.cancel();
                    } else {
                        wrapper.motionX = motionX;
                        wrapper.motionY = motionY;
                        wrapper.motionZ = motionZ;
                    }
                    break;
                case "Matrix":
                    wrapper.motionX *= 0.6;
                    wrapper.motionZ *= 0.6;
                    break;
                case "Vulcan":
                    if (mc.player.isOnGround()) {
                        mc.player.connection.sendPacket(new CConfirmTeleportPacket(++teleportId));
                        receivedTeleport = true;
                    }
                    e.cancel();
                    break;
                case "AAC Push":
                    if (mc.player.hurtTime > 0) {
                        mc.player.setMotion(mc.player.getMotion().mul(-0.5, 1, -0.5));
                    }
                    e.cancel();
                    break;
                case "AAC v4":
                    if (mc.player.hurtTime > 0 && mc.player.ticksExisted % 2 == 0) {
                        mc.player.setMotion(mc.player.getMotion().x, mc.player.getMotion().getY() + 0.01, mc.player.getMotion().z);
                    }
                    e.cancel();
                    break;
                case "NCP":
                    if (mc.player.hurtTime > 0) {
                        mc.player.setMotion(0, mc.player.getMotion().getY(), 0);
                    }
                    e.cancel();
                    break;
                case "Reverse":
                    wrapper.motionX = -wrapper.getMotionX();
                    wrapper.motionY = 0;
                    wrapper.motionZ = -wrapper.getMotionZ();
                    break;
            }
        }

        if (packet instanceof SPlayerPositionLookPacket posPacket && receivedTeleport) {
            if (posPacket.getTeleportId() == teleportId) {
                e.cancel();
                receivedTeleport = false;
            }
        }
    }

    @EventHandler
    public void onUpdate(UpdateEvent e) {
        script.update();

        switch (mode.getValue()) {
            case "Intave":
                if (mc.player.hurtTime > 0) {
                    mc.player.getMotion().y = 0;
                    mc.player.setOnGround(true);
                }
                break;
            case "Grim":
                if (mc.player.hurtTime > 0) {
                    mc.player.connection.sendPacket(new CPlayerPacket(true));
                }
                break;
        }
    }
}
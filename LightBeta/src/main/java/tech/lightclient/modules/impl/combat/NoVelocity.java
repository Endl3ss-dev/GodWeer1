package tech.lightclient.modules.impl.combat;

import tech.lightclient.client.events.impl.client.EventPacket;
import tech.lightclient.client.events.impl.client.UpdateEvent;
import tech.lightclient.client.utils.eventbus.EventHandler;
import tech.lightclient.modules.api.Category;
import tech.lightclient.modules.api.Function;
import tech.lightclient.modules.api.FunctionAdd;
import tech.lightclient.modules.api.settings.impl.ModeSetting;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

@FunctionAdd(name = "NoVelocity", alias = "No Velocity", category = Category.Combat, description = "Отменяет отбрасывание от ударов и взрывов")
public class NoVelocity extends Function {
    private static final int CC_COOLDOWN_TICKS = 20;

    public ModeSetting mode = new ModeSetting("Mode", "Cancel", "Cancel", "Jump", "HwTest");
    private float lastHealth = -1f;

    private boolean needJump;
    private boolean flag;
    private int ccCooldown;

    public NoVelocity() {
        addSettings(mode);
    }

    @EventHandler
    public void onPacket(EventPacket event) {
        if (nullCheck()) return;
        if (!event.isReceive()) return;
        if (!mode.is("Cancel") && !mode.is("HwTest")) return;

        boolean velocity = event.getPacket() instanceof EntityVelocityUpdateS2CPacket packet
                && packet.getEntityId() == mc.player.getId();
        if (!velocity && !(event.getPacket() instanceof ExplosionS2CPacket)) return;

        if (mode.is("Cancel")) {
            event.cancel();
        } else if (mode.is("HwTest")) {
            event.cancel();
            flag = true;
        }
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (nullCheck()) return;

        if (mode.is("HwTest")) {
            lastHealth = -1f;
            if (flag) handleNewGrimTick();
            return;
        }

        flag = false;

        if (!mode.is("Jump")) {
            lastHealth = -1f;
            return;
        }

        float health = mc.player.getHealth();
        if (lastHealth < 0f) {
            lastHealth = health;
            return;
        }

        if (health < lastHealth && mc.player.isOnGround()) {
            mc.options.jumpKey.setPressed(true);
        }
        lastHealth = health;
    }

    private void handleNewGrimTick() {
        if (ccCooldown <= 0) {
            mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.Full(
                    mc.player.getX(),
                    mc.player.getY(),
                    mc.player.getZ(),
                    mc.player.getYaw(),
                    mc.player.getPitch(),
                    mc.player.isOnGround(),
                    false
            ));
            mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(
                    PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK,
                    BlockPos.ofFloored(mc.player.getEntityPos()),
                    Direction.DOWN
            ));
            ccCooldown = CC_COOLDOWN_TICKS;
        } else {
            ccCooldown--;
        }
        flag = false;
    }
}
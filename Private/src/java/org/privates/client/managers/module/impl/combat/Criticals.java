package org.privates.client.managers.module.impl.combat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.potion.Effects;
import org.privates.client.api.annotations.Category1;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.AttackEvent;
import org.privates.client.managers.events.player.MotionEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.chat.ChatUtil;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.other.ViaUtil;
import org.privates.lib.util.time.StopWatch;

import java.util.concurrent.ThreadLocalRandom;

@Category1

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "Criticals", category = Category.COMBAT, description = "Криты")
public class Criticals extends Module {
    public static Criticals getInstance() {
        return Instance.get(Criticals.class);
    }

    private final ModeSetting mode = new ModeSetting(this, "Режим",
            "Grim", "Packet", "GroundSpoof", "Visual", "UpdatedNCP", "SpartanX", "Плавное падение", "New");

    private final BooleanSetting onlyAura = new BooleanSetting(this, "Только с Aura", false);
    private final BooleanSetting ignoreWater = new BooleanSetting(this, "Игнорировать воду", true);

    private final SliderSetting packets = new SliderSetting(this, "Кол-во пакетов", 2, 1, 5, 1)
            .setVisible(() -> mode.is("Packet") || mode.is("UpdatedNCP"));

    private final SliderSetting jumpHeight = new SliderSetting(this, "Высота прыжка", 0.1f, 0.05f, 0.42f, 0.01f)
            .setVisible(() -> mode.is("MiniJump"));

    private final SliderSetting motionY = new SliderSetting(this, "Motion Y", 0.25f, 0.1f, 0.5f, 0.01f)
            .setVisible(() -> mode.is("Motion"));

    private final BooleanSetting randomize = new BooleanSetting(this, "Рандомизация", true)
            .setVisible(() -> mode.is("Packet") || mode.is("UpdatedNCP") || mode.is("GroundSpoof") || mode.is("New"));

    private final SliderSetting newJumpHeight = new SliderSetting(this, "Высота прыжка New", 0.42f, 0.2f, 0.5f, 0.01f)
            .setVisible(() -> mode.is("New"));

    private final SliderSetting newFallHeight = new SliderSetting(this, "Высота падения New", 0.2f, 0.1f, 0.4f, 0.01f)
            .setVisible(() -> mode.is("New"));

    private final StopWatch timer = new StopWatch();
    private boolean waitingForCrit = false;
    private int attackTicks = 0;
    private boolean spoofGround = false;
    private boolean blockGroundPacket = false;

    @Override
    public void onEnable() {
        if (mode.is("Grim") && !ViaUtil.allowedBypass()) {
            ChatUtil.addTextWithError("Нужно зайти на сервер с версии 1.17 и выше!");
            toggle();
        }
    }

    @EventHandler
    public void onAttack(AttackEvent event) {
        if (mc.player == null || !isEnabled()) return;

        if (onlyAura.getValue() && AuraNew.getInstance() != null && !AuraNew.getInstance().isEnabled()) {
            return;
        }

        if (!canCrit()) return;

        switch (mode.getValue()) {
            case "Grim" -> doGrimCrit();
            case "Packet" -> doPacketCrit();
            case "Jump" -> doJumpCrit();
            case "MiniJump" -> doMiniJumpCrit();
            case "GroundSpoof" -> doGroundSpoofCrit();
            case "Motion" -> doMotionCrit();
            case "Visual" -> doVisualCrit();
            case "UpdatedNCP" -> doUpdatedNCPCrit();
            case "SpartanX" -> doSpartanXCrit();
            case "Плавное падение" -> doSlowFallingCrit();
            case "New" -> doNewCrit();
        }
    }

    @EventHandler
    public void onMotion(MotionEvent event) {
        if (mc.player == null) return;

        if (blockGroundPacket && mode.is("New")) {
            event.setOnGround(false);
            blockGroundPacket = false;
        }

        if (spoofGround && mode.is("GroundSpoof")) {
            event.setOnGround(true);
            spoofGround = false;
        }

        if (event.isOnGround()) {
            waitingForCrit = false;
        }

        if (waitingForCrit) {
            attackTicks++;

            if (mode.is("Jump") && mc.player.isOnGround() && attackTicks > 1) {
                mc.player.jump();
                waitingForCrit = false;
            } else if (mode.is("MiniJump") && mc.player.isOnGround() && attackTicks > 1) {
                mc.player.setMotion(mc.player.getMotion().x, jumpHeight.getValue(), mc.player.getMotion().z);
                mc.player.isAirBorne = true;
                waitingForCrit = false;
            }
        }
    }

    private boolean canCrit() {
        if (mode.is("Плавное падение")) {
            return !mc.player.isOnLadder() && !mc.player.isPassenger();
        }

        if (ignoreWater.getValue() && (mc.player.isInWater() || mc.player.isInLava())) {
            return false;
        }

        return !mc.player.isOnLadder() && !mc.player.isPassenger();
    }

    private void doGrimCrit() {
        if (!mc.player.isOnGround() && mc.player.fallDistance == 0) {
            double yOffset = ThreadLocalRandom.current().nextDouble(1.0E-5, 1.0E-4);
            float rotationOffset = (float) (ThreadLocalRandom.current().nextDouble(-1.0E-3, 1.0E-3));

            ViaUtil.sendPositionPacket(mc.player.getPosX(), mc.player.getPosY() - yOffset, mc.player.getPosZ(),
                    mc.player.rotationYaw + rotationOffset, mc.player.rotationPitch + rotationOffset, false);
            ViaUtil.sendPositionPacket(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ(),
                    mc.player.rotationYaw, mc.player.rotationPitch, false);
        }
    }

    private void doPacketCrit() {
        if (!mc.player.isOnGround()) return;

        double x = mc.player.getPosX();
        double y = mc.player.getPosY();
        double z = mc.player.getPosZ();

        Float packetCount = packets.getValue();

        for (int i = 0; i < packetCount; i++) {
            double offset = getRandomOffset(0.0625, 0.001);
            mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(x, y + offset, z, false));
            mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(x, y, z, false));
        }
    }

    private void doJumpCrit() {
        if (mc.player.isOnGround()) {
            waitingForCrit = true;
            attackTicks = 0;
        }
    }

    private void doMiniJumpCrit() {
        if (mc.player.isOnGround()) {
            waitingForCrit = true;
            attackTicks = 0;
        }
    }

    private void doGroundSpoofCrit() {
        if (!mc.player.isOnGround()) return;

        double x = mc.player.getPosX();
        double y = mc.player.getPosY();
        double z = mc.player.getPosZ();

        double offset = getRandomOffset(0.05, 0.001);
        mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(x, y + offset, z, false));
        mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(x, y, z, false));

        spoofGround = true;
    }

    private void doMotionCrit() {
        if (!mc.player.isOnGround()) return;

        double motionValue = motionY.getValue();
        if (randomize.getValue()) {
            motionValue += ThreadLocalRandom.current().nextDouble(-0.02, 0.02);
        }

        mc.player.setMotion(mc.player.getMotion().x, motionValue, mc.player.getMotion().z);
        mc.player.isAirBorne = true;
    }

    private void doVisualCrit() {
        if (!mc.player.isOnGround()) return;

        mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(
                mc.player.getPosX(),
                mc.player.getPosY() + 0.0625,
                mc.player.getPosZ(),
                false));
        mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(
                mc.player.getPosX(),
                mc.player.getPosY(),
                mc.player.getPosZ(),
                false));
    }

    private void doUpdatedNCPCrit() {
        if (!mc.player.isOnGround()) return;

        double x = mc.player.getPosX();
        double y = mc.player.getPosY();
        double z = mc.player.getPosZ();

        Float packetCount = packets.getValue();
        double[] offsets = {0.0625, 0.0433, 0.0236, 0.0145, 0.0087};

        for (int i = 0; i < Math.min(packetCount, offsets.length); i++) {
            double offset = offsets[i];
            if (randomize.getValue()) {
                offset += ThreadLocalRandom.current().nextDouble(-0.001, 0.001);
            }
            mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(x, y + offset, z, false));
            mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(x, y, z, false));
        }
    }

    private void doSpartanXCrit() {
        if (!mc.player.isOnGround()) return;

        double x = mc.player.getPosX();
        double y = mc.player.getPosY();
        double z = mc.player.getPosZ();

        mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(x, y + 0.0625, z, false));
        mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(x, y, z, false));
        mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(x, y + 0.0125, z, false));
        mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(x, y, z, false));

        mc.player.setMotion(mc.player.getMotion().x, 0.08, mc.player.getMotion().z);
    }

    private void doSlowFallingCrit() {
        if (mc.player.isPotionActive(Effects.SLOW_FALLING)) {
            if (!mc.player.isOnGround() && mc.player.fallDistance > 0) {
                double x = mc.player.getPosX();
                double y = mc.player.getPosY();
                double z = mc.player.getPosZ();

                double offset = getRandomOffset(0.0625, 0.001);
                mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(x, y + offset, z, false));
                mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(x, y, z, false));
            } else if (mc.player.isOnGround()) {
                mc.player.jump();
            }
        }
    }

    private void doNewCrit() {
        if (!mc.player.isOnGround()) return;

        double x = mc.player.getPosX();
        double y = mc.player.getPosY();
        double z = mc.player.getPosZ();

        double jumpHeight = newJumpHeight.getValue();
        double fallHeight = newFallHeight.getValue();

        if (randomize.getValue()) {
            jumpHeight += ThreadLocalRandom.current().nextDouble(-0.02, 0.02);
            fallHeight += ThreadLocalRandom.current().nextDouble(-0.01, 0.01);
        }

        mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(x, y + jumpHeight, z, false));
        mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(x, y + fallHeight, z, false));

        blockGroundPacket = true;
    }

    private double getRandomOffset(double base, double variance) {
        if (!randomize.getValue()) {
            return base;
        }
        return base + ThreadLocalRandom.current().nextDouble(-variance, variance);
    }
}
package org.privates.client.managers.module.impl.movement;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.util.math.vector.Vector3d;
import org.privates.client.api.annotations.Category3;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.other.PacketEvent;
import org.privates.client.managers.events.player.MotionEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.player.MoveUtil;

@Category3
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "Flight", category = Category.MOVEMENT, description = "Вы теперь можете летать")
public class Flight extends Module {
    public static Flight getInstance() {
        return Instance.get(Flight.class);
    }

    public final ModeSetting mode = new ModeSetting(this, "Режим", "Vanilla", "Vulcan", "Matrix", "NCP", "SunRise", "Elytra", "Jetpack");

    private final SliderSetting speed = new SliderSetting(this, "Скорость", 1.5F, 0.1F, 10F, 0.1F);
    private final SliderSetting vulcanSpeed = new SliderSetting(this, "Vulcan Скорость", 0.5F, 0.1F, 2.0F, 0.1F).setVisible(() -> mode.is("Vulcan"));
    private final SliderSetting vulcanGlideSpeed = new SliderSetting(this, "Vulcan Глайд", 0.05F, 0.01F, 0.2F, 0.01F).setVisible(() -> mode.is("Vulcan"));
    private final SliderSetting sunriseSpeed = new SliderSetting(this, "SunRise Скорость", 1.2F, 0.5F, 5.0F, 0.1F).setVisible(() -> mode.is("SunRise"));

    private final BooleanSetting antiKick = new BooleanSetting(this, "Анти-кик", true);
    private final SliderSetting antiKickFrequency = new SliderSetting(this, "Частота Анти-кика", 15, 5, 40, 1).setVisible(antiKick::getValue);
    private final BooleanSetting startDamage = new BooleanSetting(this, "Начать с урона", false).setVisible(() -> mode.is("NCP"));

    int tick;

    @Override
    public void onEnable() {
        super.onEnable();
        tick = 0;

        if (startDamage.getValue() && mode.is("NCP") && mc.player.isOnGround()) {
            mc.player.jump();
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        mc.player.abilities.isFlying = false;
        mc.player.abilities.allowFlying = false;
        resetTimer();
    }

    @EventHandler
    public void onEvent(PacketEvent event) {
        // Тут можно будет добавить пакетные обходы, если потребуется
    }

    @EventHandler
    public void onEvent(MotionEvent event) {
        tick++;

        if (antiKick.getValue() && tick % antiKickFrequency.getValue().intValue() == 0) {
            mc.player.setMotion(mc.player.getMotion().x, -0.04, mc.player.getMotion().z);
        }

        switch (mode.getValue()) {
            case "Vanilla":
                handleVanilla();
                break;
            case "Vulcan":
                handleVulcan();
                break;
            case "Matrix":
                handleMatrix();
                break;
            case "NCP":
                handleNCP();
                break;
            case "SunRise":
                handleSunRise();
                break;
            case "Elytra":
                handleElytra();
                break;
            case "Jetpack":
                handleJetpack();
                break;
        }
    }

    private void handleVanilla() {
        mc.player.abilities.isFlying = true;
        mc.player.abilities.setFlySpeed(speed.getValue() * 0.1f);
    }

    private void handleVulcan() {
        MoveUtil.setSpeed(vulcanSpeed.getValue());

        if (tick % 2 == 0) {
            mc.player.getMotion().y = -vulcanGlideSpeed.getValue();
        } else {
            mc.player.getMotion().y = vulcanGlideSpeed.getValue();
        }

        setTimerSpeed(1.1f);
    }

    private void handleMatrix() {
        if (mc.player.fallDistance > 0) {
            mc.player.getMotion().y = 0;
            mc.player.fallDistance = 0;
        }

        if (mc.gameSettings.keyBindJump.isKeyDown()) {
            mc.player.getMotion().y = 0.5;
        } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
            mc.player.getMotion().y = -0.5;
        } else {
            if (tick % 2 == 0) {
                mc.player.getMotion().y = 0.01;
            } else {
                mc.player.getMotion().y = -0.01;
            }
        }
        MoveUtil.setSpeed(speed.getValue() * 0.5);
    }

    private void handleNCP() {
        mc.player.getMotion().y = 0;
        MoveUtil.setSpeed(speed.getValue());

        if (antiKick.getValue() && mc.player.ticksExisted % 20 == 0) {
            mc.player.setPosition(mc.player.getPosX(), mc.player.getPosY() - 0.03125, mc.player.getPosZ());
        }
    }

    private void handleSunRise() {
        if (mc.player.fallDistance > 0.1) {
            mc.player.getMotion().y = 0;
        }

        if (mc.player.ticksExisted % 3 == 0) {
            mc.player.setMotion(mc.player.getMotion().x, -0.1, mc.player.getMotion().z);
        } else {
            MoveUtil.setSpeed(sunriseSpeed.getValue());
        }
    }

    private void handleElytra() {
        if (!mc.player.isElytraFlying()) {
            if (mc.player.getMotion().y < 0 && !mc.player.isOnGround()) {
                mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
            }
            return;
        }

        mc.player.getMotion().y = 0;
        if (mc.gameSettings.keyBindJump.isKeyDown()) {
            mc.player.getMotion().y = speed.getValue() * 0.5;
        } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
            mc.player.getMotion().y = -speed.getValue() * 0.5;
        }

        Vector3d forward = Vector3d.fromPitchYaw(0, mc.player.rotationYaw);
        mc.player.setMotion(
                forward.x * speed.getValue(),
                mc.player.getMotion().y,
                forward.z * speed.getValue()
        );
    }

    private void handleJetpack() {
        mc.player.getMotion().y = 0;
        if (mc.gameSettings.keyBindJump.isKeyDown()) {
            mc.player.getMotion().y = speed.getValue() * 0.4;
        }
        MoveUtil.setSpeed(speed.getValue() * 0.8);
    }

    private void setTimerSpeed(float speed) {
        try {
            java.lang.reflect.Field field = mc.timer.getClass().getDeclaredField("field_71428_T"); // MCP field name
            field.setAccessible(true);
            field.setFloat(mc.timer, speed);
        } catch (Exception e) {
            try {
                java.lang.reflect.Field field = mc.timer.getClass().getDeclaredField("timerSpeed"); // SRG field name
                field.setAccessible(true);
                field.setFloat(mc.timer, speed);
            } catch (Exception ignored) {
            }
        }
    }

    private void resetTimer() {
        setTimerSpeed(1.0f);
    }
}
package org.privates.client.managers.module.impl.movement;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.vector.Vector2f;
import org.privates.client.api.annotations.Category1;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.MoveEvent;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.impl.combat.AuraNew;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.lib.util.time.StopWatch;

@Category1

@ModuleInfo(name = "Elytra Motion", description = "Дополнение для Элитра-Таргета", category = Category.MOVEMENT)
public class ElytraMotion extends Module {

    private final SliderSetting attackDistance = new SliderSetting(this, "Дистанция работы", 3.0F, 0.1F, 5.0F, 0.01F);

    private final StopWatch timer = new StopWatch();
    public boolean freeze;
    public Vector2f direction = Vector2f.ZERO;

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null) return;

        if (!mc.player.isElytraFlying()) {
            freeze = false;
            return;
        }

        AuraNew aura = AuraNew.getInstance();

        if (check(aura)) {
            mc.gameSettings.keyBindForward.setPressed(false);
            freeze = true;
        } else {
            mc.gameSettings.keyBindForward.setPressed(true);
            freeze = false;
        }
    }

    @EventHandler
    public void onMove(MoveEvent event) {
        if (freeze) {
            event.getMotion().x = 0;
            event.getMotion().y = 0;
            event.getMotion().z = 0;
        }
    }

    public boolean check(AuraNew aura) {
        if (aura == null || !aura.isEnabled()) return false;

        LivingEntity target = aura.target();
        if (target == null || !mc.player.isElytraFlying()) return false;

        boolean elytraTargetEnabled = aura.elytraTargetMode().getValue();
        if (!elytraTargetEnabled) return false;

        float elytraDistance = aura.getElytraDistance();
        double distance = target.getDistance(mc.player);

        return distance < attackDistance.getValue() && distance < elytraDistance;
    }

    @Override
    public void onDisable() {
        freeze = false;
        super.onDisable();
    }
}
package antileak.base.client.modules.impl.render;

import antileak.base.api.events.EventLink;
import antileak.base.api.events.implement.EventUpdate;
import antileak.base.api.storages.implement.FreeLookStorage;
import antileak.base.api.storages.implement.helpertstorages.enumvar.ModuleClass;
import antileak.base.client.modules.Module;
import net.minecraft.client.option.Perspective;

/**
 * FreeLook module - allows free camera rotation without moving player
 * Based on CustomF5 from original code
 */
public class FreeLook extends Module {

    public static FreeLook INSTANCE = new FreeLook();

    private float startYaw, startPitch;
    private boolean wasInFirstPerson = false;

    public FreeLook() {
        super("FreeLook", "Свободная камера без поворота игрока", ModuleCategory.RENDER);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        
        if (mc.player == null) return;

        // Check if Aura is active - disable FreeLook if so
        if (ModuleClass.aura != null && ModuleClass.aura.isEnable() && ModuleClass.aura.getTarget() != null) {
            toggle();
            return;
        }

        startYaw = mc.player.getYaw();
        startPitch = mc.player.getPitch();
        
        wasInFirstPerson = mc.options.getPerspective().isFirstPerson();
        
        // Activate free look
        FreeLookStorage.setActive(true);
        FreeLookStorage.setFreeYaw(startYaw);
        FreeLookStorage.setFreePitch(startPitch);
        
        // Switch to third person if in first person
        if (wasInFirstPerson) {
            mc.options.setPerspective(Perspective.THIRD_PERSON_BACK);
        }
    }

    @Override
    public void onDisable() {
        if (mc.player == null) {
            super.onDisable();
            return;
        }

        // Deactivate free look
        FreeLookStorage.setActive(false);
        
        // Restore perspective
        if (wasInFirstPerson) {
            mc.options.setPerspective(Perspective.FIRST_PERSON);
        }
        
        // Restore rotation
        mc.player.setYaw(startYaw);
        mc.player.setPitch(startPitch);
        mc.player.setHeadYaw(startYaw);
        mc.player.setBodyYaw(startYaw);
        
        super.onDisable();
    }

    @EventLink
    public void onUpdate(EventUpdate event) {
        if (mc.player == null || mc.world == null) return;

        // Check if Aura is active - disable FreeLook if so
        if (ModuleClass.aura != null && ModuleClass.aura.isEnable()) {
            if (ModuleClass.aura.getTarget() != null) {
                toggle();
                return;
            }
        }

        // Keep player rotation locked
        mc.player.setHeadYaw(startYaw);
        mc.player.setBodyYaw(startYaw);
    }

    /**
     * Gets the starting yaw when module was enabled
     * @return Start yaw
     */
    public float getStartYaw() {
        return startYaw;
    }

    /**
     * Gets the starting pitch when module was enabled
     * @return Start pitch
     */
    public float getStartPitch() {
        return startPitch;
    }
}

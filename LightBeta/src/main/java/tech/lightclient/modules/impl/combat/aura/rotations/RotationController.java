package tech.lightclient.modules.impl.combat.aura.rotations;

import tech.lightclient.client.utils.player.rotation.RotateVector;
import net.minecraft.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;

public class RotationController {

    private final Map<String, RotateModel> rotations = new HashMap<>();
    private RotateModel currentRotation = null;
    private String currentMode = "";

    public RotationController() {
        registerRotation("ReallyWorld", new ReallyWorldRotation());
        registerRotation("Legit", new LegitRotation());
        registerRotation("SpookyTime", new SpookyTimeRotation());
        registerRotation("FT", new FunTimeRotation());
        registerRotation("FuntimeNew", new FuntimeNewRotation());
        registerRotation("FunTime Snap", new FunTimeSnapRotation());
        registerRotation("Neuro", new NeuroRotation());
        registerRotation("NeuroTest", new NeuroTestRotation());
        registerRotation("NeuroTestV2", new TNeuroRotation());
        registerRotation("Снап", new SnapRotation());
        registerRotation("Фт360", new Ft360Rotation());
    }
    public void registerRotation(String name, RotateModel rotation) {
        rotations.put(name, rotation);
    }
    public void setRotationMode(String mode) {
        if (currentMode.equals(mode)) {
            return;
        }

        if (currentRotation != null) {
            currentRotation.onDisable();
        }

        currentRotation = rotations.get(mode);
        if (currentRotation != null) {
            currentRotation.onEnable();
            currentMode = mode;
        }
    }

    public RotateVector update(RotateVector currentRotation, LivingEntity target) {
        if (this.currentRotation == null) {
            return currentRotation;
        }

        return this.currentRotation.update(currentRotation, target);
    }

    public String getCurrentMode() {
        return currentMode;
    }

    public RotateModel getCurrentRotation() {
        return currentRotation;
    }

    public boolean hasRotation(String name) {
        return rotations.containsKey(name);
    }

    public RotateModel getRotation(String name) {
        return rotations.get(name);
    }

    public void onEnable() {
        if (currentRotation != null) {
            currentRotation.onEnable();
        }
    }

    public void onDisable() {
        if (currentRotation != null) {
            currentRotation.onDisable();
        }
    }

    public void reset() {
        if (currentRotation != null) {
            currentRotation.onDisable();
        }
        currentRotation = null;
        currentMode = "";
    }
}

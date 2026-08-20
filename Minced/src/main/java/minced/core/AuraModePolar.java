/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.LivingEntity
 */
package minced.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import minced.core.AuraModeSimple;
import minced.core.AuraModeHandler;
import minced.core.AuraModeAdvanced;
import minced.core.AuraModeInstant;
import minced.core.AuraModeSmooth;
import minced.core.AuraModeGrim;
import minced.core.AuraModeNeuro;
import minced.core.AuraModeMatrix;
import minced.core.AuraModeVanilla;
import minced.module.combat.AttackAura;
import minced.module.combat.AttackAuraRotation;
import net.minecraft.entity.LivingEntity;

public class AuraModePolar
implements AuraModeHandler {
    Map modes = new HashMap();
    String activeModeName;
    static String POLAR_MODE_NAME = "Polar";
    static String VANILLA_HANDLER_KEY = "Polar";

    public AuraModePolar() {
        this.registerMode(new AuraModeSmooth());
        this.registerMode(new AuraModeMatrix());
        this.registerMode(new AuraModeGrim());
        this.registerMode(new AttackAuraRotation());
        this.registerMode(new AuraModeNeuro());
        this.registerMode(new AuraModeAdvanced());
        this.registerMode(new AuraModeVanilla());
        this.registerMode(new AuraModeInstant());
        this.registerMode(new AuraModeSimple());
    }

    public void beforeAttack(AttackAura param1, LivingEntity param2) {
        AuraModeHandler v3 = this.getSelectedMode(param1);
        if (v3 == null || param2 == null) {
            return;
        }
        v3.beforeAttack(param1, param2);
    }

    public void activateSelectedMode(AttackAura param1) {
        this.activeModeName = (String)param1.method799().getValue();
        AuraModeHandler v2 = (AuraModeHandler)this.modes.get(this.activeModeName);
        if (v2 != null) {
            v2.onActivate(param1);
        }
    }

    public void resetSelectedMode(AttackAura param1) {
        AuraModeHandler v2 = this.getSelectedMode(param1);
        if (v2 != null) {
            v2.reset(param1);
        }
    }

    public boolean isSelectedModeReady(AttackAura param1, LivingEntity param2) {
        AuraModeHandler v3 = this.getSelectedMode(param1);
        return v3 != null && param2 != null && v3.isReadyToAttack(param1, param2);
    }

    private AuraModeHandler getSelectedMode(AttackAura param1) {
        String v2 = (String)param1.method799().getValue();
        if (!Objects.equals(this.activeModeName, v2)) {
            AuraModeVanilla v4;
            AuraModeHandler v3 = (AuraModeHandler)this.modes.get(this.activeModeName);
            if (v3 != null) {
                v3.reset(param1);
            }
            this.activeModeName = v2;
            if (POLAR_MODE_NAME.equals(v2) && (v4 = this.getVanillaHandler()) != null) {
                v4.onActivate(param1);
            }
        }
        return (AuraModeHandler)this.modes.get(v2);
    }

    public AuraModeVanilla getVanillaHandler() {
        return (AuraModeVanilla)this.modes.get(VANILLA_HANDLER_KEY);
    }

    private void registerMode(AuraModeHandler param1) {
        this.modes.put(param1.getName(), param1);
    }

    public void deactivateCurrentMode(AttackAura param1) {
        AuraModeHandler v2 = (AuraModeHandler)this.modes.get(this.activeModeName);
        if (v2 != null) {
            v2.onDeactivate(param1);
        }
        this.activeModeName = null;
    }

    public void updateSelectedRotation(AttackAura param1, LivingEntity param2) {
        AuraModeHandler v3 = this.getSelectedMode(param1);
        if (v3 == null || param2 == null) {
            return;
        }
        v3.updateRotation(param1, param2);
    }

    public void notifyAttack(AttackAura param1, LivingEntity param2) {
        AuraModeHandler v3 = this.getSelectedMode(param1);
        if (v3 != null && param2 != null) {
            v3.onAttack(param1, param2);
        }
    }
}




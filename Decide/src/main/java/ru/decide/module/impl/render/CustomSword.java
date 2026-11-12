/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.module.impl.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.manager.event_impl.EventTick;
import ru.decide.manager.events.orbit.EventHandler;
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.module.api.ModuleInfo;
import ru.decide.module.api.settings.impl.BooleanSetting;
import ru.decide.module.api.settings.impl.ModeSetting;
import ru.decide.module.impl.render.swordreplacer.SwordReplacer;
import ru.decide.module.impl.render.swordreplacer.WeaponCatalog;

@ModuleInfo(name="Custom Sword", desc="Replace diamond and netherite sword models", category=Category.VISUALS)
@Environment(value=EnvType.CLIENT)
public class CustomSword
extends Module {
    public BooleanSetting enabled = new BooleanSetting((Module)this, "Enable", true);
    public ModeSetting diamondModel = new ModeSetting((Module)this, "Diamond", WeaponCatalog.allModels().toArray(new String[0]));
    public ModeSetting netheriteModel = new ModeSetting((Module)this, "Netherite", WeaponCatalog.allModels().toArray(new String[0]));

    @Override
    protected void onEnable() {
        SwordReplacer.setEnabled(true);
        this.applyModels();
    }

    @Override
    protected void onDisable() {
        SwordReplacer.setEnabled(false);
    }

    @EventHandler
    public void onTick(EventTick e) {
        SwordReplacer.setEnabled(this.enabled.getValue());
        this.applyModels();
    }

    private void applyModels() {
        SwordReplacer.setDiamondModel(this.diamondModel.getValue());
        SwordReplacer.setNetheriteModel(this.netheriteModel.getValue());
    }
}


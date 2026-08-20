package org.privates.client.managers.module.impl.combat.auraelement;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.vector.Vector2f;
import org.privates.client.managers.module.impl.combat.AuraNew;

public interface IRotationElement {

    Vector2f updateRotation(AuraNew parentModule, LivingEntity target, Minecraft mc, Vector2f currentLerpRotation);

    void onEnable(AuraNew parentModule);

    void onDisable(AuraNew parentModule);
}
/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.entity.attribute.EntityAttributes
 *  net.minecraft.util.math.MathHelper
 */
package minced.core;

import minced.core.AuraModeHandler;
import minced.mixin.accessor.ILivingEntityMixin;
import minced.module.combat.AttackAura;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.math.MathHelper;

public class AuraAttributeUtil
implements AuraModeHandler {
    static MinecraftClient I = MinecraftClient.getInstance();

    public float method3051(AttackAura param1, double param2) {
        if (AuraAttributeUtil.I.player == null || AuraAttributeUtil.I.world == null) {
            return 0.0f;
        }
        return (float)(1.0 / AuraAttributeUtil.I.player.getAttributeValue(EntityAttributes.ATTACK_SPEED) * param2);
    }

    public float method3052(AttackAura param1, float param2, double param3) {
        if (AuraAttributeUtil.I.player == null || AuraAttributeUtil.I.world == null) {
            return 0.0f;
        }
        return MathHelper.clamp((float)(((float)((ILivingEntityMixin)AuraAttributeUtil.I.player).getLastAttackedTicks() + param2) / this.method3051(param1, param3)), (float)0.0f, (float)1.0f);
    }
}




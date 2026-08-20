/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.item.Items
 */
package minced.core;

import minced.core.AuraModeHandler;
import minced.core.AuraAttributeUtil;
import minced.core.AuraDistanceUtil;
import minced.core.EntityPoseUtil;
import minced.module.combat.AttackAura;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Items;

public class WeaponUtil
implements AuraModeHandler {
    AuraAttributeUtil field11162 = new AuraAttributeUtil();
    EntityPoseUtil field11163 = new EntityPoseUtil();
    AuraDistanceUtil field11164 = new AuraDistanceUtil(this.field11163);
    static MinecraftClient I = MinecraftClient.getInstance();

    public float method3148(AttackAura param1, double param2) {
        return this.field11162.method3051(param1, param2);
    }

    public boolean method3149(AttackAura param1) {
        return this.field11164.method3063(param1);
    }

    public boolean l(AttackAura param1) {
        return this.field11163.method3146();
    }

    public float method3150(AttackAura param1, float param2, double param3) {
        return this.field11162.method3052(param1, param2, param3);
    }

    public boolean method3151(boolean param1, boolean param2, boolean param3, float param4) {
        return this.field11164.method3064(param1, param2, param3, param4);
    }

    public boolean l(AttackAura param1, LivingEntity param2) {
        if (WeaponUtil.I.player == null || WeaponUtil.I.world == null || param2 == null) {
            return false;
        }
        if (((Boolean)param1.method908().getValue()).booleanValue() && WeaponUtil.I.player.isUsingItem() && !WeaponUtil.I.player.getOffHandStack().isOf(Items.SHIELD)) {
            return false;
        }
        return this.method3149(param1) && param1.method800() <= System.currentTimeMillis();
    }
}




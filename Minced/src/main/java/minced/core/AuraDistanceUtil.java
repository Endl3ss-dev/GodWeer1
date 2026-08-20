/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.entity.Entity
 */
package minced.core;

import java.util.List;
import minced.core.BooleanSetting;
import minced.core.PacketFlowState;
import minced.core.TpsUtil;
import minced.core.AuraModeHandler;
import minced.core.EntityPoseUtil;
import minced.mixin.accessor.IClientPlayerEntityMixin;
import minced.module.combat.AttackAura;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;

public class AuraDistanceUtil
implements AuraModeHandler {
    static MinecraftClient I = MinecraftClient.getInstance();
    EntityPoseUtil field10797;
    static float field10798;
    static float field10799;
    static double field10800;
    static double field10801;

    public boolean method3063(AttackAura param1) {
        if (AuraDistanceUtil.I.player == null || AuraDistanceUtil.I.world == null) {
            return false;
        }
        boolean v2 = this.I(param1);
        return this.method3064(v2, (Boolean)param1.method826().getValue(), (Boolean)param1.cfr_renamed_267().getValue(), param1.method855());
    }

    public AuraDistanceUtil(EntityPoseUtil param1) {
        this.field10797 = param1;
    }

    public boolean method3064(boolean param1, boolean param2, boolean param3, float param4) {
        boolean v11;
        if (AuraDistanceUtil.I.player == null || AuraDistanceUtil.I.world == null) {
            return false;
        }
        boolean v5 = !param1 || !this.field10797.method3146();
        boolean v6 = PacketFlowState.field8033;
        boolean v7 = param2 && !AuraDistanceUtil.I.player.input.playerInput.jump();
        float v8 = v6 ? field10798 : param4;
        float v9 = AuraDistanceUtil.I.player.getAttackCooldownProgress(param3 ? TpsUtil.method2600() : field10799);
        if (v9 < v8) {
            return false;
        }
        boolean v10 = ((IClientPlayerEntityMixin)AuraDistanceUtil.I.player).lastOnGround();
        if (!v7 && (v10 || AuraDistanceUtil.I.player.isOnGround()) && param1) {
            return false;
        }
        boolean bl = v11 = AuraDistanceUtil.I.player.fallDistance > 0.0f;
        if (param1 && !v7 && v6) {
            v11 = v11 || AuraDistanceUtil.I.player.isOnGround() && AuraDistanceUtil.I.world.isSpaceEmpty((Entity)AuraDistanceUtil.I.player, AuraDistanceUtil.I.player.getBoundingBox().offset(0.0, field10800, 0.0)) || AuraDistanceUtil.I.player.getVelocity().y < field10801;
        }
        return v5 || v7 || v11;
    }

    private boolean I(AttackAura param1) {
        if (param1 == null || param1.method894() == null || param1.method894().getValue() == null || ((List)param1.method894().getValue()).isEmpty()) {
            return true;
        }
        return (Boolean)((BooleanSetting)((List)param1.method894().getValue()).getFirst()).getValue();
    }

    static {
        field10798 = 0.1f;
        field10799 = 0.5f;
        field10800 = -0.08;
        field10801 = -0.05;
    }
}




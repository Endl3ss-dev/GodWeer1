package minced.core;

import minced.mixin.accessor.ClientPlayerInteractionManagerAccessor;
import minced.mixin.accessor.IClientPlayerInteractionManagerMixin;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.HitResult;

public final class RecordClass229 implements IClientPlayerInteractionManagerMixin, ClientPlayerInteractionManagerAccessor {
    public final ClientWorld world;
    public final ClientPlayerEntity player;
    public final ClientPlayerInteractionManager interactionManager;
    public final Entity cameraEntity;
    public final HitResult crosshairTarget;
    public final Entity targetedEntity;

    public RecordClass229(ClientWorld world, ClientPlayerEntity player, ClientPlayerInteractionManager interactionManager, Entity cameraEntity, HitResult crosshairTarget, Entity targetedEntity) {
        this.world = world;
        this.player = player;
        this.interactionManager = interactionManager;
        this.cameraEntity = cameraEntity;
        this.crosshairTarget = crosshairTarget;
        this.targetedEntity = targetedEntity;
    }

    public ClientWorld world() { return world; }
    public ClientPlayerEntity player() { return player; }
    public ClientPlayerInteractionManager interactionManager() { return interactionManager; }
    public Entity cameraEntity() { return cameraEntity; }
    public HitResult crosshairTarget() { return crosshairTarget; }
    public Entity targetedEntity() { return targetedEntity; }

    @Override
    public void invokeSyncSelectedSlot() { if (interactionManager != null) ((IClientPlayerInteractionManagerMixin)interactionManager).invokeSyncSelectedSlot(); }
    @Override
    public void setblockBreakingCooldown(int var1) { if (interactionManager != null) ((IClientPlayerInteractionManagerMixin)interactionManager).setblockBreakingCooldown(var1); }
    @Override
    public void setcurrentBreakingProgress(float var1) { if (interactionManager != null) ((IClientPlayerInteractionManagerMixin)interactionManager).setcurrentBreakingProgress(var1); }
    @Override
    public float getcurrentBreakingProgress() { return interactionManager != null ? ((IClientPlayerInteractionManagerMixin)interactionManager).getcurrentBreakingProgress() : 0f; }
    @Override
    public int getBlockBreakingCooldown() { return interactionManager != null ? ((ClientPlayerInteractionManagerAccessor)interactionManager).getBlockBreakingCooldown() : 0; }
    @Override
    public void setBlockBreakingCooldown(int var1) { if (interactionManager != null) ((ClientPlayerInteractionManagerAccessor)interactionManager).setBlockBreakingCooldown(var1); }
    @Override
    public float getCurrentBreakingProgress() { return interactionManager != null ? ((ClientPlayerInteractionManagerAccessor)interactionManager).getCurrentBreakingProgress() : 0f; }
    @Override
    public void setCurrentBreakingProgress(float var1) { if (interactionManager != null) ((ClientPlayerInteractionManagerAccessor)interactionManager).setCurrentBreakingProgress(var1); }
    @Override
    public net.minecraft.util.math.BlockPos getCurrentBreakingPos() { return interactionManager != null ? ((ClientPlayerInteractionManagerAccessor)interactionManager).getCurrentBreakingPos() : null; }
}



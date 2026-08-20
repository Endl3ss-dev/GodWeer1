/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.network.ClientPlayerEntity
 *  net.minecraft.client.network.ClientPlayerInteractionManager
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.entity.Entity
 */
package minced.core;

import java.util.UUID;
import minced.core.HudAccess;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;

public interface BotContext {
    public ClientPlayerEntity getPlayer();

    default public boolean isReadyForView() {
        return this.method_2890() != null && this.getPlayer() != null && this.getInteractionManager() != null;
    }

    public String getName();

    public ClientWorld method_2890();

    default public boolean isActiveView() {
        return false;
    }

    public ClientPlayerInteractionManager getInteractionManager();

    public UUID getId();

    default public HudAccess getHudState() {
        return null;
    }

    default public Entity method3584() {
        return this.getPlayer();
    }
}




/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.item.ItemStack
 */
package minced.core;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;

public class EventEntityRemove
extends EventBase
implements CancellableEvent {
    private final LivingEntity entity;
    private final ItemStack stack;
    private final double factor;
    private final Iterable<StatusEffectInstance> effects;

    public EventEntityRemove(LivingEntity entity, ItemStack stack, double factor, Iterable<StatusEffectInstance> effects) {
        this.entity = entity;
        this.stack = stack;
        this.factor = factor;
        this.effects = effects;
    }

    public Iterable<StatusEffectInstance> method2413() {
        return this.effects;
    }

    public ItemStack method2414() {
        return this.stack;
    }

    public LivingEntity method2415() {
        return this.entity;
    }

    public double method2416() {
        return this.factor;
    }
}




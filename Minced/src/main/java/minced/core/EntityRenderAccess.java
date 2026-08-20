/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.LivingEntity
 */
package minced.core;

import java.util.function.BiConsumer;
import net.minecraft.entity.LivingEntity;

public final class EntityRenderAccess
{
    static ThreadLocal field12151;

    public static void method3441(LivingEntity param0) {
        field12151.set(param0);
    }

    public static void method3442() {
        field12151 = new ThreadLocal();
    }

    private EntityRenderAccess() {
    }

    public static LivingEntity method3443() {
        return (LivingEntity)field12151.get();
    }

    public static void method3444() {
        field12151.remove();
    }

    static {
        EntityRenderAccess.method3442();
    }
}


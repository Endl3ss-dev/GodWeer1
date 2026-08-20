/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.entity.Entity
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package minced.mixin.world;
import minced.core.EventAddEntity;
import minced.core.Event;
import minced.core.EventBus;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ClientWorld.class})
public abstract class ClientWorldMixin {
    @Inject(method={"addEntity"}, at={@At(value="TAIL")})
    private void minced$onAddEntity(Entity param1, CallbackInfo param2) {
        EventBus.method3015(((Object)new EventAddEntity(param1)));
    }
}




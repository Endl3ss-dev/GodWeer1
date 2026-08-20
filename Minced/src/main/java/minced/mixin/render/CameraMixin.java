/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.render.Camera
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.math.Vec2f
 *  net.minecraft.world.BlockView
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package minced.mixin.render;
import minced.core.EventRotation;
import minced.core.EventThirdPersonDistance;
import minced.core.Event;
import minced.core.EventBus;
import minced.mixin.accessor.ICameraMixin;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec2f;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={Camera.class})
public class CameraMixin {
    @Inject(method={"update"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/render/Camera;setPos(DDD)V", shift=At.Shift.AFTER)})
    private void onUpdateInject(BlockView param1, Entity param2, boolean param3, boolean param4, float param5, CallbackInfo param6) {
        EventRotation v7 = new EventRotation(((Camera)(Object)this).getPos(), new Vec2f(param2.getYaw(param5), param2.getPitch(param5)));
        EventBus.method3015(((Object)v7));
        Vec2f v8 = v7.method2528();
        ((ICameraMixin)((Object)this)).invokeSetRotation(v8.x, v8.y);
        ((ICameraMixin)((Object)this)).invokeSetPos(v7.method2529());
    }

    @Inject(method={"clipToSpace"}, at={@At(value="RETURN")}, cancellable=true)
    private void onClipToSpace(float param1, CallbackInfoReturnable<Float> param2) {
        EventThirdPersonDistance v3 = new EventThirdPersonDistance(((Float)param2.getReturnValue()).floatValue());
        EventBus.method3015(((Object)v3));
        param2.setReturnValue((Float)Float.valueOf(v3.method2534()));
    }
}




/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.render.BackgroundRenderer
 *  net.minecraft.client.render.BackgroundRenderer$FogType
 *  net.minecraft.client.render.Camera
 *  net.minecraft.client.render.Fog
 *  net.minecraft.client.render.FogShape
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.effect.StatusEffects
 *  org.joml.Vector4f
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package minced.mixin.render;
import minced.core.EventNoRender;
import minced.core.EventFogColor;
import minced.core.Event;
import minced.core.EventBus;
import minced.core.EventFogDistance;
import minced.enums.fire;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Fog;
import net.minecraft.client.render.FogShape;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.dreamix.fabricloader.VMBridge;

@Mixin(value={BackgroundRenderer.class})
public class BackgroundRendererMixin {
    @Unique
    private static boolean minced$fogColorModified;
    private static float field12675 = 1.0f;

    @Inject(method={"getFogColor"}, at={@At(value="RETURN")}, cancellable=true)
    private static void onGetFogColor(Camera param0, float param1, ClientWorld param2, int param3, float param4, CallbackInfoReturnable<Vector4f> param5) {
        boolean v8;
        Vector4f v6 = (Vector4f)param5.getReturnValue();
        EventFogColor v7 = new EventFogColor(v6.x, v6.y, v6.z);
        EventBus.method3015(((Object)v7));
        boolean bl = v8 = v7.method304() != v6.x || v7.method303() != v6.y || v7.method302() != v6.z;
        if (v8) {
            param5.setReturnValue((Vector4f)new Vector4f(v7.method304(), v7.method303(), v7.method302(), v6.w));
        }
        minced$fogColorModified = v8;
    }

    @Inject(method={"applyFog"}, at={@At(value="HEAD")}, cancellable=true)
    private static void onApplyFogBadEffects(Camera param0, BackgroundRenderer.FogType param1, Vector4f param2, float param3, boolean param4, float param5, CallbackInfoReturnable<Fog> param6) {
        if (minced$fogColorModified) {
            return;
        }
        Entity v7 = param0.getFocusedEntity();
        if (v7 instanceof LivingEntity v8 && (v8.hasStatusEffect(StatusEffects.BLINDNESS) || v8.hasStatusEffect(StatusEffects.DARKNESS))) {
            EventNoRender v9 = new EventNoRender(fire.bad_effects);
            EventBus.method3015(((Object)v9));
            if (v9.method2297()) {
                param6.setReturnValue((Fog)new Fog(param3 * field12675, param3, FogShape.CYLINDER, param2.x, param2.y, param2.z, param2.w));
            }
        }
    }

    @Inject(method={"applyFog"}, at={@At(value="RETURN")}, cancellable=true)
    private static void onApplyFog(Camera param0, BackgroundRenderer.FogType param1, Vector4f param2, float param3, boolean param4, float param5, CallbackInfoReturnable<Fog> param6) {
        Fog v7 = (Fog)param6.getReturnValue();
        EventFogDistance v8 = new EventFogDistance(v7.start(), v7.end());
        EventBus.method3015(((Object)v8));
        if (v8.method3017() != v7.start() || v8.method3021() != v7.end()) {
            param6.setReturnValue((Fog)new Fog(v8.method3017(), v8.method3021(), v7.shape(), v7.red(), v7.green(), v7.blue(), v7.alpha()));
        }
    }

    static {
        VMBridge.identifyClass(BackgroundRendererMixin.class, "XCBdV5hw");
    }
}




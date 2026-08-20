/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.sugar.Local
 *  net.minecraft.block.BlockState
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.option.Perspective
 *  net.minecraft.client.render.BufferBuilderStorage
 *  net.minecraft.client.render.GameRenderer
 *  net.minecraft.client.render.RenderTickCounter
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.projectile.ProjectileUtil
 *  net.minecraft.predicate.entity.EntityPredicates
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.hit.EntityHitResult
 *  net.minecraft.util.hit.HitResult
 *  net.minecraft.util.hit.HitResult$Type
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Position
 *  net.minecraft.util.math.Vec2f
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.RaycastContext
 *  net.minecraft.world.RaycastContext$FluidHandling
 *  net.minecraft.world.RaycastContext$ShapeType
 *  org.joml.Matrix4f
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.ModifyArg
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package minced.mixin.render;
import com.llamalad7.mixinextras.sugar.Local;
import java.util.function.Predicate;
import minced.core.EventRenderWorldPre;
import minced.core.EventRenderWorldPost;
import minced.core.EventBlockRayTrace;
import minced.core.EventRender3D;
import minced.core.EventSwapWorld;
import minced.core.EventAspectRatio;
import minced.core.EventNoRender;
import minced.core.EventThirdPersonRender;
import minced.core.ClientUtil;
import minced.core.Event;
import minced.core.EventBus;
import minced.core.RaytraceAccess;
import minced.core.EventMouseOver;
import minced.enums.LOAD;
import minced.enums.fire;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.dreamix.fabricloader.VMBridge;

@Mixin(value={GameRenderer.class})
public class GameRendererMixin {
    @Shadow
    private MinecraftClient field_4015;
    @Shadow
    private BufferBuilderStorage field_20948;
    @Unique
    private ClientWorld minced$updatedWorld = null;
    // Шаг "протыкания" блока при отменённом BlockRayTrace: стартовый сдвиг, предел и множитель.
    private static double field12657 = 0.01;
    private static double field12658 = 1.0;
    private static double field12659 = 2.0;
    // Ванильная формула вектора взгляда: cos/sin от -yaw*PI/180 - PI и -pitch*PI/180.
    // Раньше все шесть были заглушены единицами, из-за чего луч прицела смотрел в противоположную
    // сторону: визуально игрок смотрел вперёд, а удар/разрушение шли по блоку за спиной.
    private static float field12660 = 0.017453292f;
    private static float field12661 = 3.1415927f;
    private static float field12662 = 0.017453292f;
    private static float field12663 = 3.1415927f;
    private static float field12664 = 0.017453292f;
    private static float field12665 = 0.017453292f;

    @ModifyArg(method={"getBasicProjectionMatrix"}, at=@At(value="INVOKE", target="Lorg/joml/Matrix4f;perspective(FFFF)Lorg/joml/Matrix4f;"), index=1)
    private float modifyAspectRatio(float param1) {
        EventAspectRatio v2 = new EventAspectRatio(param1);
        EventBus.method3015(((Object)v2));
        if (!v2.method2297()) {
            return v2.method2449();
        }
        return param1;
    }

    @ModifyArg(method={"findCrosshairTarget"}, at=@At(value="INVOKE", target="Lnet/minecraft/entity/projectile/ProjectileUtil;raycast(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Box;Ljava/util/function/Predicate;D)Lnet/minecraft/util/hit/EntityHitResult;"), index=4)
    private Predicate<Entity> modifyEntityPredicate(Predicate<Entity> param12) {
        return param1 -> param12.test((Entity)param1) && ((RaytraceAccess)param1).canBeRaytracing();
    }

    @Inject(method={"findCrosshairTarget"}, at={@At(value="HEAD")}, cancellable=true)
    private void onFindCrosshairTarget(Entity param1, double param2, double param4, float param6, CallbackInfoReturnable<HitResult> param7) {
        BlockPos v24;
        EventBlockRayTrace v23;
        Vec3d v22;
        Vec3d v8 = param1.getCameraPosVec(param6);
        Vec2f v9 = new Vec2f(param1.getYaw(param6), param1.getPitch(param6));
        EventMouseOver v10 = new EventMouseOver(v8, v9);
        EventBus.method3015(((Object)v10));
        v8 = v10.method3561();
        Vec2f v11 = v10.method3563();
        double v12 = Math.max(param2, param4);
        double v14 = MathHelper.square((double)v12);
        Vec3d v16 = GameRendererMixin.getRotationVector(v11).normalize();
        Vec3d v17 = v8.add(v16.multiply(v12));
        BlockHitResult v18 = param1.getWorld().raycast(new RaycastContext(v8, v17, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, param1));
        for (int v20 = 0; v20 < 32 && v18.getType() == HitResult.Type.BLOCK; ++v20) {
            BlockHitResult v21 = v18;
            BlockState v22s = param1.getWorld().getBlockState(v21.getBlockPos());
            v23 = new EventBlockRayTrace((BlockState)v22s, v21.getBlockPos());
            EventBus.method3015(((Object)v23));
            if (!v23.method2297()) break;
            v24 = v21.getBlockPos();
            double v25 = field12657;
            Vec3d v27 = v21.getPos().add(v16.multiply(v25));
            while (v25 < field12658 && BlockPos.ofFloored((Position)v27).equals((Object)v24)) {
                v27 = v21.getPos().add(v16.multiply(v25 *= field12659));
            }
            Vec3d v19 = v27;
            v18 = param1.getWorld().raycast(new RaycastContext(v19, v17, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, param1));
        }
        double v20 = v18.getPos().squaredDistanceTo(v8);
        if (v18.getType() != HitResult.Type.MISS) {
            v14 = v20;
            v12 = Math.sqrt(v20);
        }
        v22 = GameRendererMixin.getRotationVector(v11);
        Vec3d v23s = v8.add(v22.x * v12, v22.y * v12, v22.z * v12);
        EntityHitResult v25 = ProjectileUtil.raycast((Entity)param1, (Vec3d)v8, (Vec3d)v23s, param1.getBoundingBox().stretch(v22.multiply(v12)).expand(1.0, 1.0, 1.0), param0 -> EntityPredicates.CAN_HIT.test(param0) && ((RaytraceAccess)param0).canBeRaytracing(), (double)v14);
        HitResult v26 = v25 != null && v25.getPos().squaredDistanceTo(v8) < v20 ? GameRendererMixin.ensureTargetInRange((HitResult)v25, v8, param4) : GameRendererMixin.ensureTargetInRange((HitResult)v18, v8, param2);
        param7.setReturnValue((HitResult)v26);
    }

    private static Vec3d getRotationVector(Vec2f param0) {
        float v1 = param0.x;
        float v2 = param0.y;
        float v3 = MathHelper.cos((float)(-v1 * field12660 - field12661));
        float v4 = MathHelper.sin((float)(-v1 * field12662 - field12663));
        float v5 = -MathHelper.cos((float)(-v2 * field12664));
        float v6 = MathHelper.sin((float)(-v2 * field12665));
        return new Vec3d((double)(v4 * v5), (double)v6, (double)(v3 * v5));
    }

    private static HitResult ensureTargetInRange(HitResult param0, Vec3d param1, double param2) {
        Vec3d v4 = param0.getPos();
        if (!v4.isInRange((Position)param1, param2)) {
            Vec3d v5 = param0.getPos();
            Direction v6 = Direction.getFacing((double)(v5.x - param1.x), (double)(v5.y - param1.y), (double)(v5.z - param1.z));
            return BlockHitResult.createMissed((Vec3d)v5, (Direction)v6, (BlockPos)BlockPos.ofFloored((Position)v5));
        }
        return param0;
    }

    @Inject(method={"tiltViewWhenHurt"}, at={@At(value="HEAD")}, cancellable=true)
    private void onTiltViewWhenHurt(MatrixStack param1, float param2, CallbackInfo param3) {
        if (ClientUtil.I.player == null || ClientUtil.I.world == null) {
            return;
        }
        EventNoRender v4 = new EventNoRender(fire.hurttime);
        EventBus.method3015(((Object)v4));
        if (v4.method2297()) {
            param3.cancel();
        }
    }

    @Inject(method={"renderWorld"}, at={@At(value="FIELD", target="Lnet/minecraft/client/render/GameRenderer;renderHand:Z", opcode=180, ordinal=0)})
    public void hookWorldRender(RenderTickCounter param1, CallbackInfo param2, @Local(ordinal=2) Matrix4f param3) {
        MatrixStack v4 = new MatrixStack();
        v4.multiplyPositionMatrix(param3);
        EventRender3D v5 = new EventRender3D(v4, param1.getTickDelta(true));
        EventBus.method3015(((Object)v5));
    }

    @Redirect(method={"renderHand"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/option/Perspective;isFirstPerson()Z", ordinal=0))
    private boolean onRenderHand(Perspective param1) {
        EventThirdPersonRender v2 = new EventThirdPersonRender(param1.isFirstPerson());
        EventBus.method3015(((Object)v2));
        return v2.method2536();
    }

    @Inject(method={"renderWorld"}, at={@At(value="HEAD")})
    private void onRenderWorldStart(RenderTickCounter param1, CallbackInfo param2) {
        EventBus.method3015(((Object)new EventRenderWorldPre()));
        ClientWorld v3 = this.field_4015.world;
        if (this.minced$updatedWorld != v3) {
            this.minced$updatedWorld = v3;
            EventBus.method3015(((Object)new EventSwapWorld(LOAD.CHANGED)));
        }
    }

    @Inject(method={"renderWorld"}, at={@At(value="TAIL")})
    private void onRenderWorldTail(RenderTickCounter param1, CallbackInfo param2) {
        EventBus.method3015(((Object)new EventRenderWorldPost()));
    }

    static {
        VMBridge.identifyClass(GameRendererMixin.class, "grL5kHwl");
    }
}




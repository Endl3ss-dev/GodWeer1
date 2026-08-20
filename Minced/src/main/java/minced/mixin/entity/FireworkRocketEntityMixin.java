/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.ModifyExpressionValue
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.network.ClientPlayerEntity
 *  net.minecraft.entity.EntityType
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.projectile.FireworkRocketEntity
 *  net.minecraft.entity.projectile.ProjectileEntity
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.World
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package minced.mixin.entity;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import minced.core.Event;
import minced.core.EventBus;
import minced.core.EventMoveYaw;
import minced.core.EventFireworkBoost;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import ru.dreamix.fabricloader.VMBridge;

@Mixin(value={FireworkRocketEntity.class})
public abstract class FireworkRocketEntityMixin
extends ProjectileEntity {
    @Unique
    private Vec3d rotation;
    @Shadow
    private LivingEntity field_7616;
    private static double field12602 = 1.5;
    private static double field12603 = 1.5;
    private static double field12604 = 1.5;
    private static double field12605 = 1.5;
    private static double field12606 = 1.5;
    private static double field12607 = 1.5;
    private static double field12608 = 0.1;
    private static double field12609 = 0.5;
    private static double field12610 = 0.1;
    private static double field12611 = 0.5;
    private static double field12612 = 0.1;
    private static double field12613 = 0.5;

    public FireworkRocketEntityMixin(EntityType<? extends ProjectileEntity> param1, World param2) {
        super(param1, param2);
    }

    @ModifyExpressionValue(method={"tick"}, at={@At(value="INVOKE", target="Lnet/minecraft/entity/LivingEntity;getRotationVector()Lnet/minecraft/util/math/Vec3d;")})
    public Vec3d captureRotation(Vec3d param1) {
        if (this.field_7616 == MinecraftClient.getInstance().player) {
            EventMoveYaw v2 = new EventMoveYaw(this.field_7616.getYaw(), this.field_7616.getPitch());
            EventBus.method3015(((Object)v2));
            this.rotation = this.getRotationVector(v2.method3535(), v2.method3536());
        } else {
            this.rotation = param1;
        }
        return this.rotation;
    }

    @Redirect(method={"tick"}, at=@At(value="INVOKE", target="Lnet/minecraft/util/math/Vec3d;add(DDD)Lnet/minecraft/util/math/Vec3d;", ordinal=0))
    public Vec3d redirectAdd(Vec3d param1, double param2, double param4, double param6) {
        if (this.rotation == null) {
            return param1.add(param2, param4, param6);
        }
        MinecraftClient v8 = MinecraftClient.getInstance();
        if (v8.player != null && v8.player.isGliding() && this.field_7616 instanceof ClientPlayerEntity && this.field_7616 == v8.player) {
            EventFireworkBoost v9 = new EventFireworkBoost(this.rotation, param1, new Vec3d(field12602, field12603, field12604));
            EventBus.method3015(((Object)v9));
            Vec3d v10 = v9.method3589();
            return this.addBoost(param1, v10.x, v10.y, v10.z);
        }
        return this.addBoost(param1, field12605, field12606, field12607);
    }

    @Unique
    private Vec3d addBoost(Vec3d param1, double param2, double param4, double param6) {
        return param1.add(this.rotation.x * field12608 + (this.rotation.x * param2 - param1.x) * field12609, this.rotation.y * field12610 + (this.rotation.y * param4 - param1.y) * field12611, this.rotation.z * field12612 + (this.rotation.z * param6 - param1.z) * field12613);
    }

    static {
        VMBridge.identifyClass(FireworkRocketEntityMixin.class, "ryYsixMi");
    }
}




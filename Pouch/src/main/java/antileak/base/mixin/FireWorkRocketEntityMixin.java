package antileak.base.mixin;

import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import antileak.base.api.events.implement.EventFireWork;

@Mixin(FireworkRocketEntity.class)
public abstract class FireWorkRocketEntityMixin extends ProjectileEntity {

    public FireWorkRocketEntityMixin(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        new EventFireWork((FireworkRocketEntity) (Object) this).call();
    }
}
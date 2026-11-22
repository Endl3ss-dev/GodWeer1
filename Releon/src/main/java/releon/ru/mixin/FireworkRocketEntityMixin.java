package releon.ru.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import releon.ru.api.events.impl.FireworkEvent;
import releon.ru.api.module.ModuleManager;
import releon.ru.api.module.impl.combat.aura.AngleConnection;
import releon.ru.api.module.impl.movement.SuperFireWork;
import releon.ru.manager.Manager;

@Mixin({FireworkRocketEntity.class})
public abstract class FireworkRocketEntityMixin extends Entity {
   @Shadow
   @Nullable
   private LivingEntity shooter;

   private FireworkRocketEntityMixin(World level) {
      super(EntityType.FIREWORK_ROCKET, level);
   }

   @WrapOperation(
      method = {"tick"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/entity/LivingEntity;getRotationVector()Lnet/minecraft/util/math/Vec3d;"
      )},
      require = 0
   )
   private Vec3d releon$getRotationVectorHook(LivingEntity instance, Operation<Vec3d> original) {
      return this.releon$shouldModifyFireworkBoost() && this.shooter == MinecraftClient.getInstance().player && this.shooter.isGliding()
         ? AngleConnection.INSTANCE.getMoveRotation().toVector()
         : (Vec3d)original.call(new Object[]{instance});
   }

   @WrapOperation(
      method = {"tick"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/entity/LivingEntity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V"
      )},
      require = 0
   )
   private void releon$setVelocityHook(LivingEntity instance, Vec3d velocity, Operation<Void> original) {
      if (this.releon$shouldModifyFireworkBoost() && this.shooter == MinecraftClient.getInstance().player && this.shooter.isGliding()) {
         FireworkEvent event = Manager.postEvent(new FireworkEvent(velocity));
         original.call(new Object[]{instance, event.getVector()});
      } else {
         original.call(new Object[]{instance, velocity});
      }
   }

   @Unique
   private boolean releon$shouldModifyFireworkBoost() {
      ModuleManager modules = Manager.getModules();
      return modules != null && modules.isEnabled(SuperFireWork.class);
   }
}

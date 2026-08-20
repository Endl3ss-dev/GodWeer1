/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.ModifyExpressionValue
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.component.type.PotionContentsComponent
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.entity.projectile.thrown.PotionEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.potion.Potions
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.World
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Overwrite
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package minced.mixin.entity;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import java.util.List;
import minced.core.EventStrafe;
import minced.core.EventEntityRemove;
import minced.core.EventEntityRayTrace;
import minced.core.EventVelocityMultiplier;
import minced.core.EventNoRender;
import minced.core.Event;
import minced.core.EventBus;
import minced.core.RaytraceAccess;
import minced.core.EventMoveYaw;
import minced.core.Minced;
import minced.enums.fire;
import minced.module.visual.SeeInvisibles;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.dreamix.fabricloader.VMBridge;

@Mixin(value={Entity.class})
public class EntityMixin
implements RaytraceAccess {
    private static double field11708 = 4.0;
    private static double field11709 = 2.0;
    private static double field11710 = 4.0;
    private static double field11711 = 16.0;
    private static double field11712 = 4.0;

    @ModifyExpressionValue(method={"move"}, at={@At(value="INVOKE", target="Lnet/minecraft/entity/Entity;isControlledByPlayer()Z")})
    private boolean fixFallDistanceCalculation(boolean param1) {
        if ((Object)this == MinecraftClient.getInstance().player) {
            return false;
        }
        return param1;
    }

    @Overwrite
    public float method_5871() {
        EventVelocityMultiplier v1 = new EventVelocityMultiplier((Entity)(Object)this, 0.0f);
        EventBus.method3015(((Object)v1));
        return v1.method2442();
    }

    @Override
    public boolean canBeRaytracing() {
        EventEntityRayTrace v1 = new EventEntityRayTrace((Entity)(Object)this);
        EventBus.method3015(((Object)v1));
        return !v1.method2297();
    }

    @Inject(method={"isInvisibleTo"}, at={@At(value="HEAD")}, cancellable=true)
    private void isInvisibleTo(PlayerEntity param1, CallbackInfoReturnable<Boolean> param2) {
        SeeInvisibles v3 = (SeeInvisibles)Minced.getInstance().getModuleManager().method187(SeeInvisibles.class);
        if (v3.isEnabled()) {
            param2.setReturnValue((Boolean)false);
        }
    }

    @Inject(method={"updateVelocity"}, at={@At(value="TAIL")})
    private void inject2(float param1, Vec3d param2, CallbackInfo param3) {
        if (MinecraftClient.getInstance().player != null && ((Entity)(Object)this).getId() == MinecraftClient.getInstance().player.getId()) {
            EventStrafe v4 = new EventStrafe();
            EventBus.method3015(((Object)v4));
        }
    }

    @Inject(method={"isGlowing"}, at={@At(value="HEAD")}, cancellable=true)
    private void isGlowing(CallbackInfoReturnable<Boolean> param1) {
        EventNoRender v2 = new EventNoRender(fire.glowing);
        EventBus.method3015(((Object)v2));
        if (v2.method2297()) {
            param1.setReturnValue((Boolean)false);
        }
    }

    @Inject(method={"onRemoved"}, at={@At(value="TAIL")})
    private void onRemovedClient(CallbackInfo param1) {
        PotionEntity v2;
        if (!MinecraftClient.getInstance().isRunning()) {
            return;
        }
        Object v3 = this;
        if (v3 instanceof PotionEntity) {
            v2 = (PotionEntity)v3;
            if ((v3 = v2.getWorld()) == null || !((World)v3).isClient) {
                return;
            }
        } else {
            return;
        }
        ItemStack v4 = v2.getStack();
        PotionContentsComponent v5 = (PotionContentsComponent)v4.getOrDefault(DataComponentTypes.POTION_CONTENTS, (Object)PotionContentsComponent.DEFAULT);
        if (!v5.hasEffects() || v5.matches(Potions.WATER) || v4.isOf(Items.LINGERING_POTION)) {
            return;
        }
        Box v6 = v2.getBoundingBox().expand(field11708, field11709, field11710);
        List v7 = ((World)v3).getNonSpectatingEntities(LivingEntity.class, v6);
        if (v7.isEmpty()) {
            return;
        }
        Iterable v8 = v5.getEffects();
        for (LivingEntity v10 : (Iterable<LivingEntity>)(v7)) {
            double v11;
            if (!v10.isAffectedBySplashPotions() || (v11 = v2.squaredDistanceTo((Entity)v10)) >= field11711) continue;
            double v13 = 1.0 - Math.sqrt(v11) / field11712;
            try {
                EventEntityRemove v15 = new EventEntityRemove(v10, v4, v13, v8);
                EventBus.method3015(((Object)v15));
            } catch (Throwable t) {
                // Ignore potion tracking event errors
            }
        }
    }

    @Redirect(method={"updateVelocity"}, at=@At(value="INVOKE", target="Lnet/minecraft/entity/Entity;getYaw()F"))
    public float updateVelocity(Entity param1) {
        if (param1 == MinecraftClient.getInstance().player) {
            EventMoveYaw v2 = new EventMoveYaw(param1.getYaw(), param1.getPitch());
            EventBus.method3015(((Object)v2));
            return v2.method3536();
        }
        return param1.getYaw();
    }

    static {
        VMBridge.identifyClass(EntityMixin.class, "4nPm1zSo");
    }
}




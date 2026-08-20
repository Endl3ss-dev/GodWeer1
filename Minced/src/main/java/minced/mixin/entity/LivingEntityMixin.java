/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.network.ClientPlayerEntity
 *  net.minecraft.component.type.ItemEnchantmentsComponent
 *  net.minecraft.enchantment.EnchantmentHelper
 *  net.minecraft.enchantment.Enchantments
 *  net.minecraft.entity.EquipmentSlot
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.attribute.EntityAttribute
 *  net.minecraft.entity.attribute.EntityAttributes
 *  net.minecraft.entity.damage.DamageSource
 *  net.minecraft.entity.data.TrackedData
 *  net.minecraft.item.ItemStack
 *  net.minecraft.registry.RegistryKeys
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.util.Hand
 *  net.minecraft.util.math.Vec3d
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 *  sg.ec.shh
 *  sg.ec.Чы$Ч
 */
package minced.mixin.entity;
import minced.core.EventJump;
import minced.core.EventEntityUseItem;
import minced.core.EventVelocity;
import minced.core.EventNoPush;
import minced.core.EventSwingSpeed;
import minced.core.EventTravel;
import minced.core.Event;
import minced.core.EventBus;
import minced.core.EntityInterpolation;
import minced.core.Minced;
import minced.enums.Block;
import minced.module.movement.ViaWaterFix;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.dreamix.fabricloader.VMBridge;
import sg.ec.shh;
import sg.ec.chy;

@Mixin(value={LivingEntity.class})
public abstract class LivingEntityMixin
implements EntityInterpolation {
    @Unique
    private double serverX = 0.0;
    @Unique
    private double serverY = 0.0;
    @Unique
    private double serverZ = 0.0;
    @Unique
    double prevServerX;
    @Unique
    double prevServerY;
    @Unique
    double prevServerZ;
    @Unique
    private ItemStack minced$useItem = ItemStack.EMPTY;
    @Shadow
    public Hand field_6266;
    private static double field11687 = 1.0;

    @Shadow
    public abstract Hand method_6058();

    @Shadow
    public abstract boolean method_6115();

    @Override
    public double getServerX() {
        return this.serverX;
    }

    @Override
    public double getServerY() {
        return this.serverY;
    }

    @Override
    public double getServerZ() {
        return this.serverZ;
    }

    @Override
    public void setServerPos(double param1, double param3, double param5) {
        this.serverX = param1;
        this.serverY = param3;
        this.serverZ = param5;
    }

    @Override
    public double getPrevServerX() {
        return this.prevServerX;
    }

    @Override
    public double getPrevServerY() {
        return this.prevServerY;
    }

    @Override
    public double getPrevServerZ() {
        return this.prevServerZ;
    }

    @Inject(method={"travel"}, at={@At(value="HEAD")}, cancellable=true)
    private void ontravel(Vec3d param1, CallbackInfo param2) {
        LivingEntity v3 = (LivingEntity)(Object)this;
        EventTravel v4 = new EventTravel(v3);
        EventBus.method3015(((Object)v4));
        if (v4.method2297()) {
            param2.cancel();
        }
    }

    @Inject(method={"getHandSwingDuration"}, at={@At(value="RETURN")}, cancellable=true)
    private void onHandSwingDuration(CallbackInfoReturnable<Integer> param1) {
        if ((LivingEntity)(Object)this instanceof ClientPlayerEntity) {
            int v2 = 6;
            EventSwingSpeed v3 = new EventSwingSpeed(6, this.field_6266);
            EventBus.method3015(((Object)v3));
            if (v3.method2297()) {
                param1.setReturnValue((Integer)v3.method2504());
            }
        }
    }

    @Inject(method={"isPushable"}, at={@At(value="HEAD")}, cancellable=true)
    private void onIsPushedByFluid(CallbackInfoReturnable<Boolean> param1) {
        EventNoPush v2 = new EventNoPush(Block.Player);
        EventBus.method3015(((Object)v2));
        if (v2.method2297()) {
            param1.setReturnValue((Boolean)false);
        }
    }

    @Inject(method={"onTrackedDataSet"}, at={@At(value="HEAD")})
    private void onTrackedDataSet(TrackedData<?> param1, CallbackInfo param2) {
        LivingEntity v3 = (LivingEntity)(Object)this;
        if (v3.getWorld().isClient) {
            // LivingEntity's constructor calls setHealth(), which fires onTrackedDataSet before the
            // mixin field initialisers have run, so this field can still be null here.
            if (this.minced$useItem == null) {
                this.minced$useItem = ItemStack.EMPTY;
            }
            if (this.method_6115() && this.minced$useItem.isEmpty()) {
                this.minced$useItem = v3.getStackInHand(this.method_6058());
            } else if (!this.method_6115() && !this.minced$useItem.isEmpty()) {
                EventBus.method3015(((Object)new EventEntityUseItem(v3, this.minced$useItem.copy(), EventEntityUseItem.method2355(v3.getId(), this.method_6058(), v3.getWorld().getTime(), 6))));
                this.minced$useItem = ItemStack.EMPTY;
            }
        }
    }

    @Inject(method={"handleFallDamage"}, at={@At(value="INVOKE", target="Lnet/minecraft/entity/LivingEntity;serverDamage(Lnet/minecraft/entity/damage/DamageSource;F)V", shift=At.Shift.AFTER)})
    private void onHandleFallDamage(float param1, float param2, DamageSource param3, CallbackInfoReturnable<Boolean> param4) {
        if ((Object)this instanceof ClientPlayerEntity) {
            EventBus.method3015(((Object)new EventVelocity(chy.ch.FALL)));
        }
    }

    @Unique
    private int minced$getDepthStriderLevelOnFeet(LivingEntity param1) {
        ItemStack v2 = param1.getEquippedStack(EquipmentSlot.FEET);
        if (v2.isEmpty()) {
            return 0;
        }
        RegistryEntry v3 = param1.getWorld().getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getOptional(Enchantments.DEPTH_STRIDER).orElse(null);
        if (v3 == null) {
            return 0;
        }
        ItemEnchantmentsComponent v4 = EnchantmentHelper.getEnchantments((ItemStack)v2);
        if (v4.isEmpty()) {
            return 0;
        }
        return Math.min(3, v4.getLevel(v3));
    }

    @Redirect(method={"travelInFluid"}, at=@At(value="INVOKE", target="Lnet/minecraft/entity/LivingEntity;getAttributeValue(Lnet/minecraft/registry/entry/RegistryEntry;)D"))
    private double onTravelInFluidGetAttributeValue(LivingEntity param1, RegistryEntry<EntityAttribute> param2) {
        double v3 = param1.getAttributeValue(param2);
        if (!param2.equals((Object)EntityAttributes.WATER_MOVEMENT_EFFICIENCY)) {
            return v3;
        }
        MinecraftClient v5 = MinecraftClient.getInstance();
        // shh.* are inert stubs that return null; unboxing would NPE, so treat "unknown" as false
        // (feature off) and fall through to the vanilla attribute value.
        if (!Boolean.TRUE.equals(shh.щч()) || v5.player != param1) {
            return v3;
        }
        ViaWaterFix v6 = (ViaWaterFix)Minced.getInstance().getModuleManager().method187(ViaWaterFix.class);
        if (v6 == null || !v6.isEnabled()) {
            return v3;
        }
        int v7 = this.minced$getDepthStriderLevelOnFeet(param1);
        if (v7 <= 0) {
            return v3;
        }
        return Math.min(v3, (double)v7 / field11687);
    }

    @Inject(method={"baseTick"}, at={@At(value="INVOKE", target="Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/damage/DamageSource;F)Z", shift=At.Shift.AFTER, ordinal=0)})
    private void onbaseTick(CallbackInfo param1) {
        if ((Object)this instanceof ClientPlayerEntity) {
            EventBus.method3015(((Object)new EventVelocity(chy.ch.BLOCK)));
        }
    }

    @Inject(method={"jump"}, at={@At(value="HEAD")})
    private void onJump(CallbackInfo param1) {
        if ((Object)this instanceof ClientPlayerEntity) {
            EventBus.method3015(((Object)new EventJump()));
        }
    }

    static {
        VMBridge.identifyClass(LivingEntityMixin.class, "86s5DEEF");
    }
}




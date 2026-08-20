package org.privates.client.managers.module.impl.combat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CAnimateHandPacket;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.potion.Effects;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Hand;
import org.privates.client.Excellent;
import org.privates.client.api.annotations.Category2;
import org.privates.client.api.annotations.Legit;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.other.PacketEvent;
import org.privates.client.managers.events.player.MoveEvent;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.events.world.WorldChangeEvent;
import org.privates.client.managers.events.world.WorldLoadEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.player.PlayerUtil;
import org.privates.client.utils.rotation.AuraUtil;
import org.privates.client.utils.rotation.RayTraceUtil;


@Category2

@Legit
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "TriggerBot", category = Category.COMBAT, description = "Ну кароче пиздит игроков ток самому надо навестись")
public class TriggerBot extends Module {

    public static TriggerBot getInstance() {
        return Instance.get(TriggerBot.class);
    }

    private final BooleanSetting onlyCrits = new BooleanSetting(this, "Только криты", true);
    private final BooleanSetting smartCrits = new BooleanSetting(this, "Умные криты", false).setVisible(onlyCrits::getValue);

    private boolean canCrit;
    private boolean hasAttacked;
    private long jumpTime;

    @Override
    public void toggle() {
        super.toggle();
        reset();
    }

    @EventHandler
    public void onEvent(PacketEvent event) {
        IPacket<?> packet = event.getPacket();
        if (packet instanceof CHeldItemChangePacket || packet instanceof CAnimateHandPacket) {
            canCrit = false;
            hasAttacked = false;
            jumpTime = 0;
        }
    }

    @EventHandler
    public void onEvent(WorldLoadEvent event) {
        reset();
    }

    @EventHandler
    public void onEvent(WorldChangeEvent event) {
        reset();
    }

    @EventHandler
    public void onEvent(MoveEvent event) {
        if (mc.player == null || mc.world == null) {
            canCrit = false;
            hasAttacked = false;
            return;
        }

        if (mc.player.isOnGround() && mc.player.movementInput.jump) {
            jumpTime = System.currentTimeMillis();
        }

        if (!mc.player.isOnGround() && jumpTime > 0 && System.currentTimeMillis() - jumpTime >= 255) {
            canCrit = true;
        }

        if (event.isToGround()) {
            hasAttacked = false;
            canCrit = false;
            jumpTime = 0;
        }
    }

    @EventHandler
    public void onEvent(UpdateEvent event) {
        if (mc.player == null || mc.world == null) {
            reset();
            return;
        }
        if (mc.pointedEntity instanceof LivingEntity target && !(mc.pointedEntity instanceof ArmorStandEntity)) {
            if (target instanceof PlayerEntity player && Excellent.inst().friendManager().isFriend(player.getGameProfile().getName())) {
                return;
            }
            updateAttack(target);
        }
    }


    private void updateAttack(LivingEntity target) {
        if (shouldAttack() && !hasAttacked && (RayTraceUtil.rayTraceEntity(mc.player.rotationYaw, mc.player.rotationPitch, attackDistance(), target) || mc.player.isElytraFlying()) && AuraUtil.getStrictDistance(target) < attackDistance()) {
            boolean isInLiquid = mc.player.isActualySwimming() || mc.player.isSwimming() && mc.player.areEyesInFluid(FluidTags.WATER) || mc.player.areEyesInFluid(FluidTags.LAVA);
            boolean sprinting = mc.player.isSprinting();
            if (!isInLiquid && sprinting) {
                mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.STOP_SPRINTING));
                mc.player.setServerSprintState(false);
                mc.player.setSprinting(false);
            }
            attackEntity(target);
            canCrit = false;
            hasAttacked = true;
        }
    }


    private void attackEntity(Entity entity) {
        mc.playerController.attackEntity(mc.player, entity);
        mc.player.swingArm(Hand.MAIN_HAND);
    }


    private boolean shouldAttack() {
        boolean isBlockAboveHead = PlayerUtil.isBlockAboveHead();

        boolean isInLiquid = mc.player.isActualySwimming() || mc.player.isSwimming() && mc.player.areEyesInFluid(FluidTags.WATER) || mc.player.areEyesInFluid(FluidTags.LAVA);

        if (isInLiquid) return true;

        if (onlyCrits.getValue() && !smartCrits.getValue()) {
            return shouldCritical()
                    && canCrit
                    && isBlockAboveHead;
        }

        if (smartCrits.getValue()) {
            boolean canSmartCrit = isBlockAboveHead && !mc.player.movementInput.jump && !canCrit
                    && mc.player.isOnGround()
                    && mc.player.collidedVertically;

            return (shouldCritical() && canCrit && isBlockAboveHead) || canSmartCrit;
        }

        return true;
    }


    private boolean shouldCritical() {
        boolean isDeBuffed = mc.player.isPotionActive(Effects.LEVITATION) || mc.player.isPotionActive(Effects.BLINDNESS) || mc.player.isPotionActive(Effects.SLOW_FALLING);
        boolean isInLiquid = mc.player.isActualySwimming() || mc.player.isSwimming() && mc.player.areEyesInFluid(FluidTags.WATER) || mc.player.areEyesInFluid(FluidTags.LAVA);
        boolean isFlying = mc.player.abilities.isFlying || mc.player.isElytraFlying();
        boolean isClimbing = mc.player.isOnLadder();
        boolean isCantJump = mc.player.isPassenger();
        boolean isOnWeb = PlayerUtil.isPlayerInWeb();

        return !(isDeBuffed || isInLiquid || isFlying || isClimbing || isCantJump || isOnWeb);
    }

    public double attackDistance() {
        return mc.playerController.extendedReach() ? 6.0D : 3.0D;
    }

    private void reset() {
        canCrit = false;
        hasAttacked = false;
        jumpTime = 0;
    }
}
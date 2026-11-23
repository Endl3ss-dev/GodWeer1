// deobf by pusia
package su.catlean.client.features.modules.basic;

import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.MutablePropertyReference1Impl;
import kotlin.reflect.KProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction.Plane;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.network.ReceivePacket;
import su.catlean.api.event.events.player.EntityPushEvent;
import su.catlean.api.event.events.player.PushOutOfBlocksEvent;
import su.catlean.api.event.events.world.WaterPushEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;

public final class NoPush extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new MutablePropertyReference1Impl(NoPush.class, "blocks", "getBlocks()Z", 0), new MutablePropertyReference1Impl(NoPush.class, "entities", "getEntities()Z", 0), new MutablePropertyReference1Impl(NoPush.class, "water", "getWater()Z", 0), new MutablePropertyReference1Impl(NoPush.class, "fishingHook", "getFishingHook()Z", 0) };
   @NotNull
   public static final NoPush INSTANCE = new NoPush();
   @NotNull
   private static final BooleanSetting blocks = SettingFactory.defaultImpl(INSTANCE, "blocks", true, null, null, 12, null);
   @NotNull
   private static final BooleanSetting entities = SettingFactory.defaultImpl(INSTANCE, "entities", true, null, null, 12, null);
   @NotNull
   private static final BooleanSetting liquids = SettingFactory.defaultImpl(INSTANCE, "liquids", true, null, null, 12, null);
   @NotNull
   private static final BooleanSetting fishingHook = SettingFactory.defaultImpl(INSTANCE, "fishing-hook", true, null, null, 12, null);

   private NoPush() {
      super("no-push", SubCategories.getSubCategory4(), null, 4, null);
   }

   private final boolean getBlocks() {
      return (Boolean)blocks.computeObject(this, $$delegatedProperties[0]);
   }

   private final boolean getEntities() {
      return (Boolean)entities.computeObject(this, $$delegatedProperties[1]);
   }

   private final boolean getWater() {
      return (Boolean)liquids.computeObject(this, $$delegatedProperties[2]);
   }

   private final boolean getFishingHook() {
      return (Boolean)fishingHook.computeObject(this, $$delegatedProperties[3]);
   }

   @Flow
   private final void onReceivePacket(ReceivePacket receivePacket) {
      if (receivePacket.getPacket() instanceof ClientboundEntityEventPacket
         && this.getFishingHook()
         && ((ClientboundEntityEventPacket)receivePacket.getPacket()).getEventId() == 31
         && ((ClientboundEntityEventPacket)receivePacket.getPacket()).getEntity((Level)MinecraftHolder.getClientLevel()) instanceof FishingHook) {
         FishingHook entity = (FishingHook)((ClientboundEntityEventPacket)receivePacket.getPacket()).getEntity((Level)MinecraftHolder.getClientLevel());
         if (Intrinsics.areEqual(entity.getHookedIn(), MinecraftHolder.getLocalPlayer())) {
            receivePacket.cancel();
         }
      }
   }

   @Flow
   private final void onWaterPush(WaterPushEvent waterPushEvent) {
      if (this.getWater() && MinecraftHolder.getMinecraft().level != null && MinecraftHolder.getMinecraft().player != null) {
         MutableBlockPos mutableBlockPos = new MutableBlockPos();
         Vec3 zero = Vec3.ZERO;
         if ((Boolean)waterPushEvent.getState().getValue((Property)FlowingFluid.FALLING)) {
            for (Direction horizontalItem : Plane.HORIZONTAL) {
               mutableBlockPos.setWithOffset((Vec3i)waterPushEvent.getPos(), horizontalItem);
               if (this.checkGetBlock(waterPushEvent.getFluid(), (BlockPos)mutableBlockPos, horizontalItem) || this.checkGetBlock(waterPushEvent.getFluid(), mutableBlockPos.above(), horizontalItem)
                  )
                {
                  zero = zero.normalize().add(0.0, -6.0, 0.0);
                  break;
               }
            }
         }

         waterPushEvent.setVec(zero.normalize());
         waterPushEvent.cancel();
      }
   }

   @Flow
   private final void onPushOutOfBlocks(PushOutOfBlocksEvent pushOutOfBlocksEvent) {
      if (this.getBlocks()) {
         pushOutOfBlocksEvent.cancel();
      }
   }

   @Flow
   private final void onEntityPush(EntityPushEvent entityPushEvent) {
      if (this.getEntities()) {
         entityPushEvent.cancel();
      }
   }

   private final boolean checkGetBlock(Fluid fluid, BlockPos blockPos, Direction direction) {
      BlockState blockState = MinecraftHolder.getClientLevel().getBlockState(blockPos);
      return MinecraftHolder.getClientLevel().getFluidState(blockPos).getType().isSame(fluid)
         ? false
         : (
            direction == Direction.UP
               ? true
               : (blockState.getBlock() instanceof IceBlock ? false : blockState.isFaceSturdy((BlockGetter)MinecraftHolder.getClientLevel(), blockPos, direction))
         );
   }

   static {
   }
}
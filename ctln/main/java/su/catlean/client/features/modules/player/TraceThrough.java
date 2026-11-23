// deobf by pusia
package su.catlean.client.features.modules.player;

import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.reflect.KProperty;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.client.InputEvent;
import su.catlean.api.event.events.player.AttackEvent;
import su.catlean.api.event.events.player.UpdateCrosshairTarget;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.services.InteractionManager;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.math.Vec3iUtil;
import su.catlean.utils.render.Render3D;
import su.catlean.utils.world.WorldUtil;

public final class TraceThrough extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(TraceThrough.class, "attackEntities", "getAttackEntities()Z", 0), new PropertyReference1Impl(TraceThrough.class, "ignoreEntities", "getIgnoreEntities()Z", 0), new PropertyReference1Impl(TraceThrough.class, "openContainers", "getOpenContainers()Z", 0) };
   @NotNull
   public static final TraceThrough INSTANCE = new TraceThrough();
   @NotNull
   private static final BooleanSetting attackEntities = SettingFactory.defaultImpl(INSTANCE, "attack-entities", false, null, null, 12, null);
   @NotNull
   private static final BooleanSetting ignoreEntities = SettingFactory.defaultImpl(INSTANCE, "ignore-entities", true, null, null, 12, null);
   @NotNull
   private static final BooleanSetting openContainers = SettingFactory.defaultImpl(INSTANCE, "open-containers", false, null, null, 12, null);

   private TraceThrough() {
      super("trace-through", SubCategories.getSubCategory10(), null, 4, null);
   }

   private final boolean getAttackEntities() {
      return (Boolean)attackEntities.computeObject(this, $$delegatedProperties[0]);
   }

   private final boolean getIgnoreEntities() {
      return (Boolean)ignoreEntities.computeObject(this, $$delegatedProperties[1]);
   }

   private final boolean getOpenContainers() {
      return (Boolean)openContainers.computeObject(this, $$delegatedProperties[2]);
   }

   @Flow
   private final void onUpdateCrosshairTarget(UpdateCrosshairTarget updateCrosshairTarget) {
      if (this.getIgnoreEntities()) {
         Profiler.get().pop();
         updateCrosshairTarget.cancel();
         Minecraft minecraft = MinecraftHolder.getMinecraft();
         Entity cameraEntity = MinecraftHolder.getMinecraft().getCameraEntity();
         double blockInteractionRange = MinecraftHolder.getLocalPlayer().blockInteractionRange();
         float amount4 = Render3D.INSTANCE.getAmount4();
         double value = blockInteractionRange;
         Entity entity = cameraEntity;
         minecraft.hitResult = this.fetchLocation(entity, value, amount4);
         MinecraftHolder.getMinecraft().crosshairPickEntity = MinecraftHolder.getMinecraft().hitResult instanceof EntityHitResult
            ? ((EntityHitResult)MinecraftHolder.getMinecraft().hitResult).getEntity()
            : null;
      }
   }

   private final HitResult fetchLocation(Entity entity, double amount, float amount2) {
      HitResult pick = entity.pick(amount, amount2, false);
      Vec3 eyePosition = entity.getEyePosition(amount2);
      Vec3 location = pick.getLocation();
      if (!location.closerThan((Position)eyePosition, amount)) {
         Vec3 location2 = pick.getLocation();
         Direction approximateNearest = Direction.getApproximateNearest(location2.x - eyePosition.x, location2.y - eyePosition.y, location2.z - eyePosition.z);
         return (HitResult)BlockHitResult.miss(location2, approximateNearest, BlockPos.containing((Position)location2));
      } else {
         return pick;
      }
   }

   @Flow
   public final void onAttack(AttackEvent e) {
      if (this.getAttackEntities() && MinecraftHolder.getMinecraft().hitResult instanceof BlockHitResult) {
         Entity entity = WorldUtil.INSTANCE
            .computeEntity(
               MinecraftHolder.getLocalPlayer().getYRot(),
               MinecraftHolder.getLocalPlayer().getXRot(),
               (float)MinecraftHolder.getLocalPlayer().entityInteractionRange(),
               true,
               false
            );
         if (entity != null) {
            MinecraftHolder.getMultiPlayerGameMode().attack((Player)MinecraftHolder.getLocalPlayer(), entity);
            MinecraftHolder.getLocalPlayer().swing(InteractionHand.MAIN_HAND);
            e.cancel();
         }
      }
   }

   @Flow
   public final void onInput(InputEvent e) {
      if (this.getOpenContainers()
         && MinecraftHolder.getMinecraft().player != null
         && e.getDevice() == InputEvent.Device.Mouse
         && e.getAction() == InputEvent.Action.Press
         && e.getKey() == 1
         && !MinecraftHolder.getLocalPlayer().isShiftKeyDown()
         && MinecraftHolder.getMinecraft().gui.screen() == null) {
         if (MinecraftHolder.getMinecraft().hitResult instanceof BlockHitResult
            && MinecraftHolder.getClientLevel().getBlockState(((BlockHitResult)MinecraftHolder.getMinecraft().hitResult).getBlockPos()).hasBlockEntity()) {
            return;
         }

         int value = 1;
         int blockInteractionRange = (int)MinecraftHolder.getLocalPlayer().blockInteractionRange();
         if (value <= blockInteractionRange) {
            while (true) {
               BlockPos containing = BlockPos.containing(
                  (Position)MinecraftHolder.getLocalPlayer()
                     .getEyePosition()
                     .add(WorldUtil.INSTANCE.computeVec3(WorldUtil.INSTANCE.getAmount5(), WorldUtil.INSTANCE.getAmount4()).scale(value))
               );
               if (MinecraftHolder.getClientLevel().getBlockState(containing).hasBlockEntity()) {
                  BlockHitResult from = InteractionManager.INSTANCE
                     .fetchFrom(
                        new ClipContext(
                           MinecraftHolder.getLocalPlayer().getEyePosition(),
                           Vec3iUtil.computeVec3(containing),
                           Block.COLLIDER,
                           Fluid.NONE,
                           (Entity)MinecraftHolder.getLocalPlayer()
                        ),
                        containing,
                        MinecraftHolder.getClientLevel().getBlockState(containing)
                     );
                  MinecraftHolder.getMultiPlayerGameMode().useItemOn(MinecraftHolder.getLocalPlayer(), InteractionHand.MAIN_HAND, from);
                  MinecraftHolder.getLocalPlayer().swing(InteractionHand.MAIN_HAND);
                  return;
               }

               if (value == blockInteractionRange) {
                  break;
               }

               value++;
            }
         }
      }
   }

   static {
   }
}
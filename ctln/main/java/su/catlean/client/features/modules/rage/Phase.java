// deobf by pusia
package su.catlean.client.features.modules.rage;

import java.util.List;
import kotlin.Unit;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.MutablePropertyReference1Impl;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.jvm.internal.Ref.ObjectRef;
import kotlin.math.MathKt;
import kotlin.reflect.KProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.Pos;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket.Action;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ScaffoldingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.shapes.Shapes;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.Rotation;
import su.catlean.api.data.SearchInvResult;
import su.catlean.api.enums.TargetFilter;
import su.catlean.api.event.events.player.CollisionEvent;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.PlaceModule;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.features.modules.misc.AutoSoup;
import su.catlean.client.services.CrystalPredictor;
import su.catlean.client.services.InteractionManager;
import su.catlean.client.services.InventoryManager;
import su.catlean.client.services.RotationManager;
import su.catlean.client.services.rotation.RotationAction;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.StopWatch;
import su.catlean.utils.inventory.ContainerUtil;
import su.catlean.utils.math.Vec3iUtil;
import su.catlean.utils.network.PacketUtil;
import su.catlean.utils.world.WorldUtil;

public final class Phase extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(Phase.class, "mode", "getMode()Lsu/catlean/o8;", 0), new PropertyReference1Impl(Phase.class, "onlyOnGround", "getOnlyOnGround()Z", 0), new PropertyReference1Impl(Phase.class, "autoDisable", "getAutoDisable()Z", 0), new PropertyReference1Impl(Phase.class, "antiSwim", "getAntiSwim()Z", 0), new PropertyReference1Impl(Phase.class, "fill", "getFill()Z", 0), new PropertyReference1Impl(Phase.class, "grim", "getGrim()Z", 0), new MutablePropertyReference1Impl(Phase.class, "switchMode", "getSwitchMode()Lsu/catlean/uv;", 0) };
   @NotNull
   public static final Phase INSTANCE = new Phase();
   @NotNull
   private static final EnumSetting mode = SettingFactory.defaultImpl(INSTANCE, "mode", Phase.Mode.VANILLA, null, null, 12, null);
   @NotNull
   private static final BooleanSetting onlyOnGround = SettingFactory.defaultImpl(
      INSTANCE, "only-on-ground", false, null, () -> INSTANCE.getMode() == Phase.Mode.PEARL, 4, null
   );
   @NotNull
   private static final BooleanSetting autoDisable = SettingFactory.defaultImpl(
      INSTANCE, "auto-disable", false, null, () -> INSTANCE.getMode() == Phase.Mode.PEARL, 4, null
   );
   @NotNull
   private static final BooleanSetting antiSwim = SettingFactory.defaultImpl(
      INSTANCE, "anti-swim", true, null, () -> INSTANCE.getMode() == Phase.Mode.PEARL, 4, null
   );
   @NotNull
   private static final BooleanSetting fill = SettingFactory.defaultImpl(INSTANCE, "fill", true, null, () -> INSTANCE.getMode() == Phase.Mode.PEARL, 4, null);
   @NotNull
   private static final BooleanSetting grim = SettingFactory.defaultImpl(
      INSTANCE, "grim", true, null, () -> INSTANCE.getMode() == Phase.Mode.PEARL && INSTANCE.getFill(), 4, null
   );
   @NotNull
   private static final EnumSetting switchValue = SettingFactory.defaultImpl(
      INSTANCE, "switch", AutoSoup.Switch.SILENT_FULL, null, () -> INSTANCE.getMode() == Phase.Mode.PEARL, 4, null
   );
   @NotNull
   private static final StopWatch stopWatch = new StopWatch();

   private Phase() {
      super("phase", SubCategories.getSubCategory5(), null, 4, null);
   }

   private final Phase.Mode getMode() {
      return (Phase.Mode)mode.computeObject(this, $$delegatedProperties[0]);
   }

   private final boolean getOnlyOnGround() {
      return (Boolean)onlyOnGround.computeObject(this, $$delegatedProperties[1]);
   }

   private final boolean getAutoDisable() {
      return (Boolean)autoDisable.computeObject(this, $$delegatedProperties[2]);
   }

   private final boolean getAntiSwim() {
      return (Boolean)antiSwim.computeObject(this, $$delegatedProperties[3]);
   }

   private final boolean getFill() {
      return (Boolean)fill.computeObject(this, $$delegatedProperties[4]);
   }

   private final boolean getGrim() {
      return (Boolean)grim.computeObject(this, $$delegatedProperties[5]);
   }

   private final AutoSoup.Switch getSwitchMode() {
      return (AutoSoup.Switch)switchValue.computeObject(this, $$delegatedProperties[6]);
   }

   @Flow
   private final void onCollision(CollisionEvent collisionEvent) {
      BlockPos containing = BlockPos.containing((Position)this.computeVec3((Entity)MinecraftHolder.getLocalPlayer()));
      if (this.getMode() == Phase.Mode.VANILLA
         && (!Intrinsics.areEqual(collisionEvent.getPos(), containing.below()) || MinecraftHolder.getMinecraft().options.keyShift.isDown())) {
         collisionEvent.setState(Blocks.AIR.defaultBlockState());
      }
   }

   @Override
   public void update2() {
      if (MinecraftHolder.getMinecraft().player != null && MinecraftHolder.getLocalPlayer().onGround() && this.getMode() == Phase.Mode.STEP) {
         double[] amounts = WorldUtil.INSTANCE.computeAmounts(0.44);
         float yRot = MinecraftHolder.getLocalPlayer().getYRot() % 90;
         boolean flag = 35.0F <= yRot ? yRot <= 55.0F : false;
         Packet serverboundPlayerCommandPacket = (Packet)(new ServerboundPlayerCommandPacket((Entity)MinecraftHolder.getLocalPlayer(), Action.START_SPRINTING));
         PacketUtil.applySend(serverboundPlayerCommandPacket);
         if (flag) {
            double[] amounts2 = WorldUtil.INSTANCE.computeAmounts(0.51);
            INSTANCE.applyGetEyePosition(amounts, amounts2);
            INSTANCE.applyGetX(amounts, amounts2);
         } else {
            double[] amounts3 = WorldUtil.INSTANCE.computeAmounts(0.57);
            INSTANCE.applyGetEyePosition(amounts, amounts3);
            MinecraftHolder.getLocalPlayer()
               .setPos(
                  MinecraftHolder.getLocalPlayer().getX() + amounts3[0],
                  MinecraftHolder.getLocalPlayer().getY(),
                  MinecraftHolder.getLocalPlayer().getZ() + amounts3[1]
               );
            serverboundPlayerCommandPacket = (Packet)(
               new Pos(
                  MinecraftHolder.getLocalPlayer().getX(),
                  MinecraftHolder.getLocalPlayer().getY(),
                  MinecraftHolder.getLocalPlayer().getZ(),
                  true,
                  MinecraftHolder.getLocalPlayer().horizontalCollision
               )
            );
            PacketUtil.applySend(serverboundPlayerCommandPacket);
            INSTANCE.applyGetX(amounts, amounts3);
         }
      }
   }

   private final void applyGetEyePosition(double[] amounts, double[] amounts2) {
      CrystalPredictor instance = CrystalPredictor.INSTANCE;
      Vec3 eyePosition = MinecraftHolder.getLocalPlayer().getEyePosition();
      Vec3 amount = MinecraftHolder.getLocalPlayer().getEyePosition().add(amounts[0], 0.0, amounts[1]);
      Packet pos = null;
      byte byteValue = 28;
      Object object = null;
      Object object2 = null;
      boolean flag = false;
      Vec3 vec3 = amount;
      BlockHitResult defaultImpl = CrystalPredictor.defaultImpl(instance, eyePosition, vec3, flag, (List)object2, (List)object, byteValue, pos);
      int value = (defaultImpl != null ? defaultImpl.equals(Type.MISS) : false) ? 1 : 2;
      MinecraftHolder.getLocalPlayer()
         .setPos(
            MinecraftHolder.getLocalPlayer().getX() + amounts2[0],
            MinecraftHolder.getLocalPlayer().getY() + value,
            MinecraftHolder.getLocalPlayer().getZ() + amounts2[1]
         );
      pos = (Packet)(
         new Pos(
            MinecraftHolder.getLocalPlayer().getX(),
            MinecraftHolder.getLocalPlayer().getY(),
            MinecraftHolder.getLocalPlayer().getZ(),
            true,
            MinecraftHolder.getLocalPlayer().horizontalCollision
         )
      );
      PacketUtil.applySend((Packet)pos);
   }

   private final void applyGetX(double[] amounts, double[] amounts2) {
      int value = MinecraftHolder.getClientLevel()
            .isEmptyBlock(BlockPos.containing((Position)this.computeVec3((Entity)MinecraftHolder.getLocalPlayer()).add(amounts[0], -2.0, amounts[1])))
         ? 2
         : 1;
      MinecraftHolder.getLocalPlayer()
         .setPos(
            MinecraftHolder.getLocalPlayer().getX() + amounts2[0],
            MinecraftHolder.getLocalPlayer().getY() - value,
            MinecraftHolder.getLocalPlayer().getZ() + amounts2[1]
         );
      Packet pos = (Packet)(
         new Pos(
            MinecraftHolder.getLocalPlayer().getX(),
            MinecraftHolder.getLocalPlayer().getY(),
            MinecraftHolder.getLocalPlayer().getZ(),
            true,
            MinecraftHolder.getLocalPlayer().horizontalCollision
         )
      );
      PacketUtil.applySend(pos);
   }

   @Flow(
      priority = -10
   )
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      if (this.getMode() == Phase.Mode.PEARL) {
         boolean flag = this.getAntiSwim() && MinecraftHolder.getLocalPlayer().getPose() == Pose.SWIMMING;
         boolean flag2 = stopWatch.testValue(500)
            && !MinecraftHolder.getLocalPlayer().getCooldowns().isOnCooldown(Items.ENDER_PEARL.getDefaultInstance())
            && MinecraftHolder.getLocalPlayer().tickCount > 60;
         if (MinecraftHolder.getLocalPlayer().onGround() && flag) {
            MinecraftHolder.getLocalPlayer().jumpFromGround();
         }

         if (MinecraftHolder.getLocalPlayer().horizontalCollision
            && !this.isFlag()
            && flag2
            && (WorldUtil.INSTANCE.getAmount3() > 0.0 || !flag)
            && (MinecraftHolder.getLocalPlayer().onGround() || !this.getOnlyOnGround() || flag)) {
            if (MinecraftHolder.getClientLevel()
               .getBlockState(BlockPos.containing((Position)this.computeVec3((Entity)MinecraftHolder.getLocalPlayer())))
               .getBlock() instanceof ScaffoldingBlock) {
               MinecraftHolder.getMultiPlayerGameMode()
                  .startDestroyBlock(BlockPos.containing((Position)this.computeVec3((Entity)MinecraftHolder.getLocalPlayer())), Direction.UP);
            }

            double[] defaultImpl = WorldUtil.defaultImpl(WorldUtil.INSTANCE, 0.5F, true, 0.0F, 0.0F, 0.0F, 28, null);
            BlockPos containing = BlockPos.containing(
               MinecraftHolder.getLocalPlayer().getX() + defaultImpl[0], MinecraftHolder.getLocalPlayer().getY(), MinecraftHolder.getLocalPlayer().getZ() + defaultImpl[1]
            );
            if (MinecraftHolder.getMinecraft().options.keyShift.isDown()) {
               return;
            }

            ObjectRef objectRef = new ObjectRef();
            objectRef.element = Rotation.vectorUtil.computeRotation(Vec3iUtil.computeVec3(containing));
            objectRef.element = new Rotation(((Rotation)objectRef.element).getAmount(), this.getAmount(), false, null, 12, null);
            SearchInvResult searchInvResult = InventoryManager.INSTANCE
               .computeSearchInvResult(arg -> Intrinsics.areEqual(arg.getItem(), Items.ENDER_PEARL), this.getSwitchMode());
            if (searchInvResult.isFlag()) {
               RotationManager.INSTANCE
                  .updateRotationAction(
                     new RotationAction(
                        (Rotation)objectRef.element,
                        201,
                        () -> {
                           if (INSTANCE.getFill()) {
                              INSTANCE.applyBlockInteractionRange(containing.below());
                              INSTANCE.applyBlockInteractionRange(containing);
                           }

                           Entity entity = WorldUtil.INSTANCE
                              .computeEntity(((Rotation)objectRef.element).getAmount(), ((Rotation)objectRef.element).getAmount2(), 3.0F, false, false);
                           if (entity != null) {
                              Entity entity2 = entity;
                              if (entity2 instanceof ItemFrame && !((ItemFrame)entity2).getItem().isEmpty()) {
                                 MinecraftHolder.getMultiPlayerGameMode().attack((Player)MinecraftHolder.getLocalPlayer(), entity2);
                                 Packet serverboundSwingPacket = (Packet)(new ServerboundSwingPacket(InteractionHand.MAIN_HAND));
                                 PacketUtil.applySend(serverboundSwingPacket);
                              }

                              if (entity2 instanceof ItemFrame || entity2 instanceof EndCrystal) {
                                 MinecraftHolder.getMultiPlayerGameMode().attack((Player)MinecraftHolder.getLocalPlayer(), entity2);
                                 Packet serverboundSwingPacket2 = (Packet)(new ServerboundSwingPacket(InteractionHand.MAIN_HAND));
                                 PacketUtil.applySend(serverboundSwingPacket2);
                              }
                           }

                           InventoryManager.INSTANCE.applyGetInventory(searchInvResult.getValue(), INSTANCE.getSwitchMode(), () -> {
                              ContainerUtil.defaultImpl(InteractionHand.MAIN_HAND, 0.0F, 0.0F, 6, null);
                              Packet serverboundSwingPacket3 = (Packet)(new ServerboundSwingPacket(InteractionHand.MAIN_HAND));
                              PacketUtil.applySend(serverboundSwingPacket3);
                              return Unit.INSTANCE;
                           });
                           if (INSTANCE.getAutoDisable()) {
                              INSTANCE.applyUnplug();
                           }

                           stopWatch.update();
                           return Unit.INSTANCE;
                        }
                     )
                  );
            }
         }
      }
   }

   private final void applyBlockInteractionRange(BlockPos blockPos) {
      BlockState blockState = MinecraftHolder.getClientLevel().getBlockState(blockPos);
      if (blockState.canBeReplaced()) {
         SearchInvResult searchInvResult = InventoryManager.INSTANCE
            .computeSearchInvResult(arg -> Intrinsics.areEqual(arg.getItem(), Items.OBSIDIAN), this.getSwitchMode());
         if (searchInvResult.isFlag()) {
            BlockHitResult defaultImpl = InteractionManager.defaultImpl(
               InteractionManager.INSTANCE,
               blockPos,
               -1,
               this.getGrim() ? PlaceModule.Interact.GRIM : PlaceModule.Interact.Strict,
               TargetFilter.ALL,
               (float)MinecraftHolder.getLocalPlayer().blockInteractionRange(),
               (float)MinecraftHolder.getLocalPlayer().blockInteractionRange(),
               null,
               64,
               null
            );
            if (defaultImpl != null) {
               InventoryManager.INSTANCE.applyGetInventory(searchInvResult.getValue(), this.getSwitchMode(), () -> {
                  MinecraftHolder.getMultiPlayerGameMode().useItemOn(MinecraftHolder.getLocalPlayer(), InteractionHand.MAIN_HAND, defaultImpl);
                  MinecraftHolder.getLocalPlayer().swing(InteractionHand.MAIN_HAND);
                  return Unit.INSTANCE;
               });
            }
         }
      }
   }

   private final float getAmount() {
      if (Math.abs(MinecraftHolder.getLocalPlayer().getY()) - Math.floor(MinecraftHolder.getLocalPlayer().getY()) > 0.1) {
         return 81.0F;
      } else {
         return Intrinsics.areEqual(
               MinecraftHolder.getClientLevel().getBlockState(BlockPos.containing((Position)this.computeVec3((Entity)MinecraftHolder.getLocalPlayer()))),
               Blocks.LADDER
            )
            ? 80.0F
            : 85.0F;
      }
   }

   private final boolean isFlag() {
      BlockPos containing = BlockPos.containing(
         MinecraftHolder.getLocalPlayer().getX(), MathKt.roundToInt(MinecraftHolder.getLocalPlayer().getY()), MinecraftHolder.getLocalPlayer().getZ()
      );
      BlockState blockState = MinecraftHolder.getClientLevel().getBlockState(containing);
      BlockState blockState2 = MinecraftHolder.getClientLevel().getBlockState(containing);
      boolean flag = !Intrinsics.areEqual(blockState.getCollisionShape((BlockGetter)MinecraftHolder.getClientLevel(), containing), Shapes.empty());
      boolean flag2 = !Intrinsics.areEqual(blockState2.getCollisionShape((BlockGetter)MinecraftHolder.getClientLevel(), containing.above()), Shapes.empty());
      return flag && !flag2 && !Intrinsics.areEqual(blockState.getBlock(), Blocks.LADDER);
   }

   static {
   }

   public static enum Mode {
      VANILLA,
      PEARL,
      STEP;

      private static final Phase.Mode[] modes = $values();
      private static final EnumEntries<Phase.Mode> enumEntries = EnumEntriesKt.enumEntries(modes);

      private Mode() {
      }

      @NotNull
      public static EnumEntries<Phase.Mode> getEnumEntries() {
         return enumEntries;
      }

      private static final Phase.Mode[] $values() {
         return new Phase.Mode[]{VANILLA, PEARL, STEP};
      }
   }
}
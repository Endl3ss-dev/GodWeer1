// deobf by pusia
package su.catlean.client.features.modules.rage;

import com.google.common.collect.Queues;
import java.util.concurrent.ConcurrentLinkedQueue;
import kotlin.NoWhenBranchMatchedException;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.jvm.internal.Ref.ObjectRef;
import kotlin.ranges.ClosedRange;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetHealthPacket;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.PosRot;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3fc;
import su.catlean.api.data.BTPacket;
import su.catlean.api.data.BlockPosWithFacing;
import su.catlean.api.data.Rotation;
import su.catlean.api.data.SearchInvResult;
import su.catlean.api.event.events.network.PostTasksProcessEvent;
import su.catlean.api.event.events.network.ReceivePacket;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.features.modules.player.AirPlace;
import su.catlean.client.services.InteractionManager;
import su.catlean.client.services.InventoryManager;
import su.catlean.client.services.RotationManager;
import su.catlean.client.services.rotation.LookRotationAction;
import su.catlean.client.services.rotation.RotationAction;
import su.catlean.client.services.rotation.SilentRotationAction;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.StopWatch;
import su.catlean.utils.input.InputUtil;
import su.catlean.utils.inventory.ContainerUtil;
import su.catlean.utils.math.MathUtil;
import su.catlean.utils.math.Vec3iUtil;
import su.catlean.utils.network.PacketUtil;
import su.catlean.utils.player.InputPacketUtil;
import su.catlean.utils.world.WorldUtil;

public final class Scaffold extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(Scaffold.class, "timing", "getTiming()Lsu/catlean/lp;", 0), new PropertyReference1Impl(Scaffold.class, "autoSwitch", "getAutoSwitch()Lsu/catlean/ib;", 0), new PropertyReference1Impl(Scaffold.class, "tower", "getTower()Z", 0), new PropertyReference1Impl(Scaffold.class, "safeWalk", "getSafeWalk()Z", 0), new PropertyReference1Impl(Scaffold.class, "allowShift", "getAllowShift()Z", 0), new PropertyReference1Impl(Scaffold.class, "legit", "getLegit()Z", 0), new PropertyReference1Impl(Scaffold.class, "saveAngles", "getSaveAngles()Z", 0), new PropertyReference1Impl(Scaffold.class, "telly", "getTelly()Z", 0), new PropertyReference1Impl(Scaffold.class, "clamp", "getClamp()Z", 0), new PropertyReference1Impl(Scaffold.class, "airPlace", "getAirPlace()Z", 0), new PropertyReference1Impl(Scaffold.class, "grimDelay", "getGrimDelay()Z", 0), new PropertyReference1Impl(Scaffold.class, "keepY", "getKeepY()Z", 0), new PropertyReference1Impl(Scaffold.class, "rotate", "getRotate()Z", 0), new PropertyReference1Impl(Scaffold.class, "grimSilent", "getGrimSilent()Z", 0) };
   @NotNull
   public static final Scaffold INSTANCE = new Scaffold();
   @NotNull
   private static final EnumSetting timing = SettingFactory.defaultImpl(INSTANCE, "timing", Scaffold.Timing.GRIM, null, null, 12, null);
   @NotNull
   private static final EnumSetting switchValue = SettingFactory.defaultImpl(INSTANCE, "switch", Scaffold.Switch.Silent, null, null, 12, null);
   @NotNull
   private static final BooleanSetting tower = SettingFactory.defaultImpl(INSTANCE, "tower", true, null, null, 12, null);
   @NotNull
   private static final BooleanSetting safeWalk = SettingFactory.defaultImpl(INSTANCE, "safe-walk", false, null, null, 12, null);
   @NotNull
   private static final BooleanSetting allowShift = SettingFactory.defaultImpl(INSTANCE, "allow-shift", false, null, () -> !INSTANCE.getSafeWalk(), 4, null);
   @NotNull
   private static final BooleanSetting legit = SettingFactory.defaultImpl(INSTANCE, "legit", true, null, null, 12, null);
   @NotNull
   private static final BooleanSetting saveAngles = SettingFactory.defaultImpl(INSTANCE, "save-angles", true, null, null, 12, null);
   @NotNull
   private static final BooleanSetting telly = SettingFactory.defaultImpl(INSTANCE, "telly", false, null, null, 12, null);
   @NotNull
   private static final BooleanSetting clamp = SettingFactory.defaultImpl(INSTANCE, "clamp", true, null, null, 12, null);
   @NotNull
   private static final BooleanSetting airPlace = SettingFactory.defaultImpl(INSTANCE, "air-place", false, null, null, 12, null);
   @NotNull
   private static final BooleanSetting grimBlockFly = SettingFactory.defaultImpl(INSTANCE, "grim-block-fly", false, null, null, 12, null);
   @NotNull
   private static final BooleanSetting keepHeight = SettingFactory.defaultImpl(INSTANCE, "keep-height", false, null, null, 12, null);
   @NotNull
   private static final BooleanSetting rotate = SettingFactory.defaultImpl(INSTANCE, "rotate", true, null, null, 12, null);
   @NotNull
   private static final BooleanSetting grimSilent = SettingFactory.defaultImpl(INSTANCE, "grim-silent", false, null, null, 12, null);
   @NotNull
   private static final StopWatch stopWatch = new StopWatch();
   @Nullable
   private static BlockPosWithFacing blockPosWithFacing;
   private static int value;
   @Nullable
   private static Rotation rotation;
   private static boolean flag;
   @NotNull
   private static final ConcurrentLinkedQueue<Object> concurrentLinkedQueue = Queues.newConcurrentLinkedQueue();
   @NotNull
   private static final ConcurrentLinkedQueue<Object> concurrentLinkedQueue2 = Queues.newConcurrentLinkedQueue();
   @NotNull
   private static final StopWatch stopWatch2 = new StopWatch();
   @NotNull
   private static final BlockPos[] blockPoss;

   private Scaffold() {
      super("scaffold", SubCategories.getSubCategory5(), null, 4, null);
   }

   private final Scaffold.Timing getTiming() {
      return (Scaffold.Timing)timing.computeObject(this, $$delegatedProperties[0]);
   }

   private final Scaffold.Switch getAutoSwitch() {
      return (Scaffold.Switch)switchValue.computeObject(this, $$delegatedProperties[1]);
   }

   private final boolean getTower() {
      return (Boolean)tower.computeObject(this, $$delegatedProperties[2]);
   }

   private final boolean getSafeWalk() {
      return (Boolean)safeWalk.computeObject(this, $$delegatedProperties[3]);
   }

   private final boolean getAllowShift() {
      return (Boolean)allowShift.computeObject(this, $$delegatedProperties[4]);
   }

   private final boolean getLegit() {
      return (Boolean)legit.computeObject(this, $$delegatedProperties[5]);
   }

   private final boolean getSaveAngles() {
      return (Boolean)saveAngles.computeObject(this, $$delegatedProperties[6]);
   }

   private final boolean getTelly() {
      return (Boolean)telly.computeObject(this, $$delegatedProperties[7]);
   }

   private final boolean getClamp() {
      return (Boolean)clamp.computeObject(this, $$delegatedProperties[8]);
   }

   private final boolean getAirPlace() {
      return (Boolean)airPlace.computeObject(this, $$delegatedProperties[9]);
   }

   private final boolean getGrimDelay() {
      return (Boolean)grimBlockFly.computeObject(this, $$delegatedProperties[10]);
   }

   private final boolean getKeepY() {
      return (Boolean)keepHeight.computeObject(this, $$delegatedProperties[11]);
   }

   private final boolean getRotate() {
      return (Boolean)rotate.computeObject(this, $$delegatedProperties[12]);
   }

   private final boolean getGrimSilent() {
      return (Boolean)grimSilent.computeObject(this, $$delegatedProperties[13]);
   }

   @Override
   public void update2() {
      value = -999;
   }

   @Override
   public void update4() {
      if (flag && !InputUtil.checkContains(340)) {
         MinecraftHolder.getMinecraft().options.keyShift.setDown(false);
      }

      this.applyRemoveIf();
   }

   @Flow(
      priority = -10
   )
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      this.applyGetY();
   }

   private final void applyGetY() {
      if (!MinecraftHolder.getMinecraft().options.keyJump.isDown() || !(WorldUtil.INSTANCE.getAmount3() <= 0.0) || !this.getTelly()) {
         blockPosWithFacing = null;
         int inventory = this.fetchInventory(false);
         if ((this.getSafeWalk() || !MinecraftHolder.getLocalPlayer().isShiftKeyDown() || this.getAllowShift()) && inventory != -1) {
            if (MinecraftHolder.getMinecraft().options.keyJump.isDown() && !WorldUtil.INSTANCE.isFlag4()) {
               value = (int)Math.floor(MinecraftHolder.getLocalPlayer().getY() - 1);
            }

            if (this.computeVec3((Entity)MinecraftHolder.getLocalPlayer()).y < value) {
               value = -999;
            }

            boolean checkGetItem = this.checkGetItem(inventory);
            double value2 = checkGetItem ? MinecraftHolder.getLocalPlayer().getY() : Math.ceil(MinecraftHolder.getLocalPlayer().getY());
            BlockPos containing = (this.getKeepY() || this.getTelly()) && value != -999
               ? BlockPos.containing(MinecraftHolder.getLocalPlayer().getX(), value, MinecraftHolder.getLocalPlayer().getZ())
               : BlockPos.containing(MinecraftHolder.getLocalPlayer().getX(), value2, MinecraftHolder.getLocalPlayer().getZ()).below();
            blockPosWithFacing = this.getAirPlace() ? new BlockPosWithFacing(containing, Direction.DOWN) : this.computeBlockPosWithFacing(containing);
            if (this.getSafeWalk() && WorldUtil.INSTANCE.isFlag4()) {
               if (!MinecraftHolder.getLocalPlayer().isShiftKeyDown() && MinecraftHolder.getClientLevel().getBlockState(containing).canBeReplaced()) {
                  MinecraftHolder.getMinecraft().options.keyShift.setDown(true);
                  flag = true;
               }

               if (MinecraftHolder.getLocalPlayer().isShiftKeyDown() && !MinecraftHolder.getClientLevel().getBlockState(containing).canBeReplaced()) {
                  MinecraftHolder.getMinecraft().options.keyShift.setDown(false);
                  flag = false;
               }
            }

            double value3 = this.getTiming() == Scaffold.Timing.VANILLA ? -0.1 : -0.25;
            BlockPosWithFacing blockPosWithFacing2 = blockPosWithFacing;
            if (blockPosWithFacing2 == null
               || this.getLegit()
                  && MinecraftHolder.getClientLevel()
                     .getBlockCollisions(
                        (Entity)MinecraftHolder.getLocalPlayer(),
                        MinecraftHolder.getLocalPlayer().getBoundingBox().inflate(value3, 0.0, value3).move(0.0, -0.1, 0.0)
                     )
                     .iterator()
                     .hasNext()) {
               if (rotation != null && this.getSaveAngles()) {
                  RotationManager.defaultImpl(
                     RotationManager.INSTANCE,
                     this.getRotate() && !this.getGrimSilent()
                        ? rotation
                        : new Rotation(MinecraftHolder.getLocalPlayer().getYRot(), MinecraftHolder.getLocalPlayer().getXRot(), false, null, 12, null),
                     0,
                     2,
                     null
                  );
               }
            } else {
               BlockPos position = blockPosWithFacing2.getPosition();
               Direction facing = blockPosWithFacing2.getFacing();
               ObjectRef objectRef = new ObjectRef();
               objectRef.element = new BlockHitResult(Vec3iUtil.computeVec3(position).add(new Vec3((Vector3fc)facing.step()).scale(0.5)), facing, position, false);
               double defaultImpl = MinecraftHolder.getLocalPlayer().getY() - Math.floor(MinecraftHolder.getLocalPlayer().getY()) == 0.5
                  ? 0.39
                  : 0.82 + MathUtil.defaultImpl((ClosedRange)RangesKt.rangeTo(0.0, 0.16), false, 2, null);
               double coerceIn = this.getClamp()
                  ? RangesKt.coerceIn(
                     MinecraftHolder.getLocalPlayer().getX() + MathUtil.defaultImpl((ClosedRange)RangesKt.rangeTo(-0.01, 0.01), false, 2, null),
                     ((BlockHitResult)objectRef.element).getBlockPos().getX(),
                     ((BlockHitResult)objectRef.element).getBlockPos().getX() + 1.0
                  )
                  : ((BlockHitResult)objectRef.element).getLocation().x;
               double coerceIn2 = this.getClamp()
                  ? RangesKt.coerceIn(
                     MinecraftHolder.getLocalPlayer().getZ() + MathUtil.defaultImpl((ClosedRange)RangesKt.rangeTo(-0.01, 0.01), false, 2, null),
                     ((BlockHitResult)objectRef.element).getBlockPos().getZ(),
                     ((BlockHitResult)objectRef.element).getBlockPos().getZ() + 1.0
                  )
                  : ((BlockHitResult)objectRef.element).getLocation().z;
               objectRef.element = new BlockHitResult(new Vec3(coerceIn, position.getY() + defaultImpl, coerceIn2), facing, position, false);
               Rotation rotation2 = Rotation.vectorUtil.computeRotation(((BlockHitResult)objectRef.element).getLocation());
               if (this.getRotate() && this.getGrimSilent()) {
                  Packet posRot = (Packet)(
                     new PosRot(
                        MinecraftHolder.getLocalPlayer().getX(),
                        MinecraftHolder.getLocalPlayer().getY(),
                        MinecraftHolder.getLocalPlayer().getZ(),
                        rotation2.getAmount(),
                        rotation2.getAmount2(),
                        MinecraftHolder.getLocalPlayer().onGround(),
                        MinecraftHolder.getLocalPlayer().horizontalCollision
                     )
                  );
                  PacketUtil.applySend(posRot);
               }

               Rotation rotation3 = this.getRotate() && !this.getGrimSilent()
                  ? rotation2
                  : new Rotation(MinecraftHolder.getLocalPlayer().getYRot(), MinecraftHolder.getLocalPlayer().getXRot(), false, null, 12, null);

               RotationAction rotationAction = switch (Scaffold.WhenMappings.values[this.getTiming().ordinal()]) {
                  case 1 -> new RotationAction(rotation3, 0, () -> {
                     INSTANCE.applyGetBlockPos((BlockHitResult)objectRef.element);
                     return Unit.INSTANCE;
                  }, 2, null);
                  case 2 -> (RotationAction)(new LookRotationAction(rotation3, 0, () -> {
                     INSTANCE.applyGetBlockPos((BlockHitResult)objectRef.element);
                     return Unit.INSTANCE;
                  }, 2, null));
                  case 3 -> (RotationAction)(new SilentRotationAction(rotation3, 0, () -> {
                     INSTANCE.applyGetBlockPos((BlockHitResult)objectRef.element);
                     return Unit.INSTANCE;
                  }, 2, null));
                  default -> throw new NoWhenBranchMatchedException();
               };
               RotationManager.INSTANCE.updateRotationAction(rotationAction);
               rotation = rotation2;
            }
         }
      }
   }

   private final boolean checkGetItem(int value) {
      if (value == -2) {
         if (MinecraftHolder.getLocalPlayer().getOffhandItem().getItem() instanceof BlockItem) {
            return Block.isShapeFullBlock(
               ((BlockItem)MinecraftHolder.getLocalPlayer().getOffhandItem().getItem())
                  .getBlock()
                  .defaultBlockState()
                  .getCollisionShape((BlockGetter)MinecraftHolder.getClientLevel(), BlockPos.ZERO)
            );
         }
      } else if (value != -1 && MinecraftHolder.getLocalPlayer().getInventory().getItem(value).getItem() instanceof BlockItem) {
         return Block.isShapeFullBlock(
            ((BlockItem)MinecraftHolder.getLocalPlayer().getInventory().getItem(value).getItem())
               .getBlock()
               .defaultBlockState()
               .getCollisionShape((BlockGetter)MinecraftHolder.getClientLevel(), BlockPos.ZERO)
         );
      }

      return true;
   }

   private final void applyGetBlockPos(BlockHitResult blockHitResult) {
      if (!MinecraftHolder.getMinecraft().options.keyJump.isDown() || !(WorldUtil.INSTANCE.getAmount3() <= 0.0) || !this.getTelly()) {
         int inventory = this.fetchInventory(true);
         if (inventory != -1) {
            if (MinecraftHolder.getMinecraft().options.keyJump.isDown() && !WorldUtil.INSTANCE.isFlag4() && this.getTower()) {
               MinecraftHolder.getLocalPlayer()
                  .setDeltaMovement(
                     MinecraftHolder.getLocalPlayer().getDeltaMovement().x,
                     stopWatch.testValue2(1500) ? -0.28 : 0.42,
                     MinecraftHolder.getLocalPlayer().getDeltaMovement().z
                  );
            } else {
               stopWatch.update();
            }

            InteractionManager instance = InteractionManager.INSTANCE;
            Block block = MinecraftHolder.getClientLevel().getBlockState(blockHitResult.getBlockPos()).getBlock();
            boolean flag2 = instance.testBlock(block) && !MinecraftHolder.getLocalPlayer().isShiftKeyDown();
            if (flag2) {
               InputPacketUtil.INSTANCE.applyWrite(true);
            }

            boolean flag3 = inventory != -2;
            if (this.getAirPlace() && AirPlace.INSTANCE.getEnabled() && AirPlace.INSTANCE.getBypass()) {
               ContainerUtil.defaultImpl(flag3 ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND, 0.0F, 0.0F, 6, null);
               PacketUtil.applyCurrentSequence(
                  arg -> (Packet)(
                     new ServerboundUseItemPacket(
                        flag3 ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND, arg, WorldUtil.INSTANCE.getAmount4(), WorldUtil.INSTANCE.getAmount5()
                     )
                  )
               );
               Packet serverboundPlayerActionPacket = (Packet)(new ServerboundPlayerActionPacket(Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ZERO, Direction.DOWN));
               PacketUtil.applySend(serverboundPlayerActionPacket);
               PacketUtil.applyCurrentSequence(
                  arg2 -> (Packet)(new ServerboundUseItemOnPacket(flag3 ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND, blockHitResult, arg2))
               );
               MinecraftHolder.getLocalPlayer().swing(flag3 ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
               Packet serverboundPlayerActionPacket2 = (Packet)(new ServerboundPlayerActionPacket(Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ZERO, Direction.DOWN));
               PacketUtil.applySend(serverboundPlayerActionPacket2);
               MinecraftHolder.getLocalPlayer().swing(flag3 ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
            } else {
               int count = MinecraftHolder.getLocalPlayer().getMainHandItem().getCount();
               if (this.getGrimDelay()) {
                  Packet serverboundPlayerActionPacket3 = (Packet)(new ServerboundPlayerActionPacket(Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ZERO, Direction.DOWN));
                  PacketUtil.applySend(serverboundPlayerActionPacket3);
               }

               MinecraftHolder.getMultiPlayerGameMode()
                  .useItemOn(MinecraftHolder.getLocalPlayer(), flag3 ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND, blockHitResult);
               Packet serverboundSwingPacket = (Packet)(new ServerboundSwingPacket(flag3 ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND));
               PacketUtil.applySend(serverboundSwingPacket);
               if (this.getGrimDelay()) {
                  Packet serverboundPlayerActionPacket4 = (Packet)(new ServerboundPlayerActionPacket(Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ZERO, Direction.DOWN));
                  PacketUtil.applySend(serverboundPlayerActionPacket4);
                  MinecraftHolder.getLocalPlayer().getMainHandItem().setCount(count);
               }
            }

            stopWatch2.update();
            value = blockHitResult.getBlockPos().getY();
            if (flag2) {
               InputPacketUtil.INSTANCE.applyWrite(false);
            }

            this.applyGetInventory(inventory);
            if (this.getRotate() && this.getGrimSilent()) {
               Packet posRot = (Packet)(
                  new PosRot(
                     MinecraftHolder.getLocalPlayer().getX(),
                     MinecraftHolder.getLocalPlayer().getY(),
                     MinecraftHolder.getLocalPlayer().getZ(),
                     MinecraftHolder.getLocalPlayer().getYRot(),
                     MinecraftHolder.getLocalPlayer().getXRot(),
                     MinecraftHolder.getLocalPlayer().onGround(),
                     MinecraftHolder.getLocalPlayer().horizontalCollision
                  )
               );
               PacketUtil.applySend(posRot);
            }
         }
      }
   }

   private final BlockPosWithFacing computeBlockPosWithFacing(BlockPos blockPos) {
      if (!MinecraftHolder.getClientLevel().getBlockState(blockPos).canBeReplaced()) {
         return null;
      } else {
         BlockPos[] blockPoss2 = blockPoss;
         int value2 = 0;

         for (int i = blockPoss2.length; value2 < i; value2++) {
            BlockPos blockPos2 = blockPoss2[value2];
            InteractionManager instance = InteractionManager.INSTANCE;
            BlockPos offset = blockPos.offset((Vec3i)blockPos2);
            BlockPosWithFacing blockPosWithFacing2 = instance.computeBlockPosWithFacing(offset);
            if (blockPosWithFacing2 != null) {
               return blockPosWithFacing2;
            }
         }

         return null;
      }
   }

   private final boolean checkGetItem(ItemStack itemStack) {
      return itemStack.getItem() instanceof BlockItem
         && !((BlockItem)itemStack.getItem()).getBlock().defaultBlockState().canBeReplaced()
         && !(((BlockItem)itemStack.getItem()).getBlock() instanceof FallingBlock);
   }

   private final int fetchInventory(boolean flag) {
      int selectedSlot = -1;
      if (this.checkGetItem(MinecraftHolder.getLocalPlayer().getOffhandItem())) {
         return -2;
      } else if (this.checkGetItem(MinecraftHolder.getLocalPlayer().getMainHandItem())) {
         return MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot();
      } else {
         SearchInvResult inventory = InventoryManager.INSTANCE.fetchInventory(arg -> INSTANCE.checkGetItem(arg));
         SearchInvResult inventory2 = InventoryManager.INSTANCE.fetchInventory2(arg2 -> INSTANCE.checkGetItem(arg2));
         if (flag) {
            switch (Scaffold.WhenMappings.values2[this.getAutoSwitch().ordinal()]) {
               case 1:
                  if (inventory2.isFlag()) {
                     ContainerUtil.applyHandleContainerInput(inventory2.getValue(), selectedSlot, ContainerInput.SWAP, true);
                     selectedSlot = MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot();
                  }
                  break;
               case 2:
               case 3:
                  if (inventory.isFlag()) {
                     selectedSlot = MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot();
                     inventory.update();
                  }
            }
         } else {
            switch (Scaffold.WhenMappings.values2[this.getAutoSwitch().ordinal()]) {
               case 1:
                  if (inventory2.isFlag()) {
                     selectedSlot = MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot();
                  }
                  break;
               case 2:
               case 3:
                  if (inventory.isFlag()) {
                     selectedSlot = MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot();
                  }
            }
         }

         return selectedSlot;
      }
   }

   @Flow
   private final void onReceivePacket(ReceivePacket receivePacket) {
      if (this.getGrimDelay()) {
         Packet<? extends PacketListener> packet = receivePacket.getPacket();
         if (!(packet instanceof ServerboundChatPacket)
            && !(packet instanceof ClientboundSystemChatPacket)
            && !(packet instanceof ClientboundSetTimePacket)
            && !(packet instanceof ServerboundChatCommandPacket)
            && !(packet instanceof ClientboundSoundPacket)
            && !(packet instanceof ClientboundStopSoundPacket)
            && !(packet instanceof ClientboundSetActionBarTextPacket)
            && !(packet instanceof ClientboundSetHealthPacket)
            && !(packet instanceof ClientboundBossEventPacket)) {
            if (!(packet instanceof ClientboundPlayerPositionPacket) && !(packet instanceof ClientboundDisconnectPacket)) {
               concurrentLinkedQueue.add(new BTPacket(receivePacket.getPacket(), MinecraftHolder.getTime()));
               receivePacket.cancel();
            } else {
               this.applyRemoveIf();
            }
         }
      }
   }

   @Flow
   private final void onPostTasksProcess(PostTasksProcessEvent postTasksProcessEvent) {
      if (MinecraftHolder.getMinecraft().level != null && MinecraftHolder.getMinecraft().player != null) {
         defaultImpl(this, false, 1, null);
         concurrentLinkedQueue2.removeIf(
            arg -> {
               Scaffold instance = INSTANCE;

               try {
                  String text = "null cannot be cast to non-null type net.minecraft.network.protocol.Packet<net.minecraft.network.protocol.game.ClientGamePacketListener>";
                  ((net.minecraft.network.protocol.Packet)arg).handle((PacketListener)MinecraftHolder.getClientPacketListener());
                  Unit instance2 = Unit.INSTANCE;
               } catch (Throwable exception) {
                  Object failure = ResultKt.createFailure(exception);
               }

               return true;
            }
         );
      } else {
         concurrentLinkedQueue.clear();
      }
   }

   private final void applyRemoveIf(boolean flag) {
      concurrentLinkedQueue.removeIf(arg -> {
         boolean flag2 = flag || ((BTPacket)arg).getTime() <= MinecraftHolder.getTime() - 500;
         if (flag2) {
            concurrentLinkedQueue2.add(((BTPacket)arg).getPacket());
         }

         return flag2;
      });
   }

   public static void defaultImpl(Scaffold scaffold, boolean flag, int value, Object object) {
      if ((value & 1) != 0) {
         flag = false;
      }

      scaffold.applyRemoveIf(flag);
   }

   private final void applyRemoveIf() {
      concurrentLinkedQueue.removeIf(arg -> {
         Scaffold instance = INSTANCE;

         try {
            ((BTPacket)arg).getPacket().handle((PacketListener)MinecraftHolder.getClientPacketListener());
            Unit instance2 = Unit.INSTANCE;
         } catch (Throwable exception) {
            Object failure = ResultKt.createFailure(exception);
         }

         return true;
      });
   }

   private final void applyGetInventory(int value) {
      switch (value) {
         case -2:
         case -1:
            return;
         default:
            switch (Scaffold.WhenMappings.values2[this.getAutoSwitch().ordinal()]) {
               case 1:
                  int selectedSlot = MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot();
                  boolean flag2 = true;
                  ContainerInput swap = ContainerInput.SWAP;
                  int value2 = selectedSlot;
                  ContainerUtil.applyHandleContainerInput(value, value2, swap, flag2);
               case 2:
               default:
                  break;
               case 3:
                  InventoryManager.INSTANCE.applyGetInventory2(value);
            }
      }
   }

   static {
      BlockPos[] blockPoss2 = new BlockPos[]{
         new BlockPos(0, 0, 0),
         new BlockPos(-1, 0, 0),
         new BlockPos(1, 0, 0),
         new BlockPos(0, 0, 1),
         new BlockPos(0, 0, -1),
         new BlockPos(-2, 0, 0),
         new BlockPos(2, 0, 0),
         new BlockPos(0, 0, 2),
         new BlockPos(0, 0, -2),
         new BlockPos(0, -1, 0),
         new BlockPos(1, -1, 0),
         new BlockPos(-1, -1, 0),
         new BlockPos(0, -1, 1),
         new BlockPos(0, -1, -1)
      };
      blockPoss = blockPoss2;
   }

   public static enum Switch {
      Normal,
      Silent,
      Inventory,
      None;

      private static final Scaffold.Switch[] switchValues = $values();
      private static final EnumEntries<Scaffold.Switch> enumEntries = EnumEntriesKt.enumEntries(switchValues);

      private Switch() {
      }

      @NotNull
      public static EnumEntries<Scaffold.Switch> getEnumEntries() {
         return enumEntries;
      }

      private static final Scaffold.Switch[] $values() {
         return new Scaffold.Switch[]{Normal, Silent, Inventory, None};
      }
   }

   public static enum Timing {
      SEQUENTIAL,
      VANILLA,
      GRIM;

      private static final Scaffold.Timing[] timings = $values();
      private static final EnumEntries<Scaffold.Timing> enumEntries = EnumEntriesKt.enumEntries(timings);

      private Timing() {
      }

      @NotNull
      public static EnumEntries<Scaffold.Timing> getEnumEntries() {
         return enumEntries;
      }

      private static final Scaffold.Timing[] $values() {
         return new Scaffold.Timing[]{SEQUENTIAL, VANILLA, GRIM};
      }
   }

   public static final class WhenMappings {
      public static final int[] values;
      public static final int[] values2;

      static {
         int[] values3 = new int[Scaffold.Timing.values().length];

         try {
            values3[Scaffold.Timing.SEQUENTIAL.ordinal()] = 1;
         } catch (NoSuchFieldError exception) {
         }

         try {
            values3[Scaffold.Timing.VANILLA.ordinal()] = 2;
         } catch (NoSuchFieldError exception2) {
         }

         try {
            values3[Scaffold.Timing.GRIM.ordinal()] = 3;
         } catch (NoSuchFieldError exception3) {
         }

         values = values3;
         values3 = new int[Scaffold.Switch.values().length];

         try {
            values3[Scaffold.Switch.Inventory.ordinal()] = 1;
         } catch (NoSuchFieldError exception4) {
         }

         try {
            values3[Scaffold.Switch.Normal.ordinal()] = 2;
         } catch (NoSuchFieldError exception5) {
         }

         try {
            values3[Scaffold.Switch.Silent.ordinal()] = 3;
         } catch (NoSuchFieldError exception6) {
         }

         values2 = values3;
      }
   }
}
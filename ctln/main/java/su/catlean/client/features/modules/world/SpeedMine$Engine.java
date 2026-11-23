// deobf by pusia
package su.catlean.client.features.modules.world;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.Rotation;
import su.catlean.api.enums.AutoSwitchMode;
import su.catlean.client.features.modules.attack.AutoCrystal;
import su.catlean.client.features.modules.player.SpeedMine;
import su.catlean.client.services.BreakingBlocksService;
import su.catlean.client.services.InteractionManager;
import su.catlean.client.services.RotationManager;
import su.catlean.client.services.rotation.LookRotationAction;
import su.catlean.client.services.rotation.RotationAction;
import su.catlean.client.services.rotation.SilentRotationAction;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.StopWatch;
import su.catlean.utils.async.TaskScheduler;
import su.catlean.utils.math.Vec3iUtil;
import su.catlean.utils.network.PacketUtil;

public final class SpeedMine$Engine {
   @NotNull
   private final BlockPos pos;
   private float amount;
   private float amount2;
   @NotNull
   private final ArrayDeque<Boolean> arrayDeque;
   private int value;
   @NotNull
   private final StopWatch stopWatch;

   public SpeedMine$Engine(BlockPos pos, Direction direction) {
      this.pos = pos;
      this.arrayDeque = new ArrayDeque();
      this.stopWatch = new StopWatch();
      this.applyGetDirection(direction);
   }

   @NotNull
   public final BlockPos getPos() {
      return this.pos;
   }

   public final float getAmount() {
      return this.amount;
   }

   public final float getAmount2() {
      return this.amount2;
   }

   public final int getValue() {
      return this.value;
   }

   public final void setValue(int value) {
      this.value = value;
   }

   public final void applyGetDirection(Direction direction) {
      Direction direction2 = direction;
      if (direction == null) {
         direction2 = MinecraftHolder.getLocalPlayer().getDirection();
      }

      Direction direction3 = direction2;
      int selectedSlot = MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot();
      int value2 = SpeedMine.computeValue(SpeedMine.INSTANCE, this.pos);
      SpeedMine instance = SpeedMine.INSTANCE;
      if (SpeedMine.computeSwapTiming(instance) == SpeedMine.SwapTiming.ALWAYS) {
         if (value2 == -1) {
            return;
         }

         SpeedMine.updateSpeedMine(SpeedMine.INSTANCE, value2, -1);
      }

      if (SpeedMine.INSTANCE.getDoubleMine()) {
         if (SpeedMine.computePacketOrder(SpeedMine.INSTANCE) == SpeedMine.PacketOrder.NEW) {
            PacketUtil.applyCurrentSequence(arg -> (Packet)(new ServerboundPlayerActionPacket(Action.START_DESTROY_BLOCK, this.pos, direction3, arg)));
            Packet serverboundPlayerActionPacket = (Packet)(new ServerboundPlayerActionPacket(Action.ABORT_DESTROY_BLOCK, this.pos.offset(0, 500, 0), Direction.DOWN));
            PacketUtil.applySend(serverboundPlayerActionPacket);
         } else {
            PacketUtil.applyCurrentSequence(arg2 -> (Packet)(new ServerboundPlayerActionPacket(Action.STOP_DESTROY_BLOCK, this.pos, direction3, arg2)));
            PacketUtil.applyCurrentSequence(arg3 -> (Packet)(new ServerboundPlayerActionPacket(Action.START_DESTROY_BLOCK, this.pos, direction3, arg3)));
         }

         PacketUtil.applyCurrentSequence(arg4 -> (Packet)(new ServerboundPlayerActionPacket(Action.STOP_DESTROY_BLOCK, this.pos, direction3, arg4)));
      } else {
         PacketUtil.applyCurrentSequence(arg5 -> (Packet)(new ServerboundPlayerActionPacket(Action.START_DESTROY_BLOCK, this.pos, direction3, arg5)));
         instance = SpeedMine.INSTANCE;
         if (SpeedMine.computeStartMode(instance) != SpeedMine.StartMode.NONE) {
            if (SpeedMine.computeStartMode(SpeedMine.INSTANCE) == SpeedMine.StartMode.ABORT) {
               Packet serverboundPlayerActionPacket2 = (Packet)(new ServerboundPlayerActionPacket(Action.ABORT_DESTROY_BLOCK, this.pos, Direction.DOWN));
               PacketUtil.applySend(serverboundPlayerActionPacket2);
            } else {
               PacketUtil.applyCurrentSequence(arg6 -> (Packet)(new ServerboundPlayerActionPacket(Action.STOP_DESTROY_BLOCK, this.pos, direction3, arg6)));
            }
         }
      }

      instance = SpeedMine.INSTANCE;
      if (SpeedMine.computeSwapTiming(instance) == SpeedMine.SwapTiming.ALWAYS) {
         SpeedMine.INSTANCE.getAtomicBoolean().set(true);
         if (SpeedMine.testSpeedMine(SpeedMine.INSTANCE)) {
            TaskScheduler.defaultImpl(TaskScheduler.INSTANCE, 1, (byte)0, false, () -> {
               SpeedMine.updateSpeedMine(SpeedMine.INSTANCE, selectedSlot, value2);
               SpeedMine.INSTANCE.getAtomicBoolean().set(false);
            }, 6, null);
         } else {
            SpeedMine instance2 = SpeedMine.INSTANCE;
            SpeedMine.updateSpeedMine(instance2, selectedSlot, value2);
         }
      }
   }

   public final boolean checkGetBlockState(boolean flag) {
      Direction firstOrNull = (Direction)CollectionsKt.firstOrNull(InteractionManager.INSTANCE.computeItems(this.pos));
      if (firstOrNull == null) {
         firstOrNull = MinecraftHolder.getLocalPlayer().getDirection();
      }

      Direction direction = firstOrNull;
      Iterable<SpeedMine$Engine> linkedList = SpeedMine.INSTANCE.getLinkedList();
      int value2 = 0;
      Iterator<SpeedMine$Engine> iterator = linkedList.iterator();

      while (true) {
         if (iterator.hasNext()) {
            SpeedMine$Engine object = iterator.next();
            SpeedMine$Engine engine = (SpeedMine$Engine)object;
            if (MinecraftHolder.getClientLevel().isEmptyBlock(engine.pos)) {
               continue;
            }

            firstOrNull = (Direction)(Object)object;
            break;
         }

         firstOrNull = null;
         break;
      }

      SpeedMine$Engine engine2 = (SpeedMine$Engine)(Object)firstOrNull;
      boolean isFlag = this.isFlag();
      value2 = SpeedMine.computeValue(SpeedMine.INSTANCE, this.pos);
      if (this.value > 0 && SpeedMine.INSTANCE.getDoubleMine() && !Intrinsics.areEqual(this.pos, SpeedMine.getBlockPos2())) {
         this.applyClear();
         return true;
      } else if (!SpeedMine.testSpeedMine(SpeedMine.INSTANCE, this.pos)) {
         this.applyClear();
         return true;
      } else if (MinecraftHolder.getClientLevel().isEmptyBlock(this.pos)) {
         this.amount = 0.0F;
         this.amount2 = -1.0F;
         this.arrayDeque.add(MinecraftHolder.getLocalPlayer().onGround());
         return !Intrinsics.areEqual(this.pos, SpeedMine.getBlockPos2()) && SpeedMine.INSTANCE.getLinkedList().size() > 1;
      } else if (this.amount != 0.0F
         || this.amount2 != -1.0F
         || SpeedMine.INSTANCE.getMode() != SpeedMine.Mode.PACKET
         || !this.stopWatch.testValue2(500)
         || this.value > 0
            && SpeedMine.INSTANCE.getRebreak() == SpeedMine.Rebreak.GRIM
            && (Intrinsics.areEqual(this.pos, SpeedMine.getBlockPos2()) || SpeedMine.INSTANCE.getLinkedList().size() <= 1)) {
         if (value2 == -1) {
            return false;
         } else if (MinecraftHolder.getLocalPlayer().isUsingItem() && SpeedMine.computeOnEating(SpeedMine.INSTANCE) == SpeedMine.OnEating.GRIM) {
            this.amount2 = this.amount;
            float amount3 = this.amount;
            SpeedMine instance = SpeedMine.INSTANCE;
            BlockState blockState = MinecraftHolder.getClientLevel().getBlockState(this.pos);
            BlockPos pos2 = this.pos;
            boolean onGround = MinecraftHolder.getLocalPlayer().onGround();
            BlockPos blockPos = pos2;
            BlockState blockState2 = blockState;
            this.amount = amount3 + SpeedMine.computeAmount(instance, blockState2, blockPos, value2, onGround);
            return false;
         } else {
            if (!(this.amount >= 1.0F) && !isFlag) {
               this.amount2 = this.amount;
               float amount4 = this.amount;
               SpeedMine instance2 = SpeedMine.INSTANCE;
               BlockState blockState3 = MinecraftHolder.getClientLevel().getBlockState(this.pos);
               BlockPos pos3 = this.pos;
               boolean onGround2 = MinecraftHolder.getLocalPlayer().onGround();
               BlockPos blockPos2 = pos3;
               BlockState blockState4 = blockState3;
               this.amount = amount4 + SpeedMine.computeAmount(instance2, blockState4, blockPos2, value2, onGround2);
            } else {
               if (engine2 != null && Intrinsics.areEqual(engine2, this)) {
                  var linkedList2 = SpeedMine.INSTANCE.getLinkedList();
                  int value3;
                  if (linkedList2 instanceof Collection && linkedList2.isEmpty()) {
                     value3 = 0;
                  } else {
                     int value4 = 0;

                     for (Object object2 : linkedList2) {
                        SpeedMine$Engine engine3 = (SpeedMine$Engine)object2;
                        if (!MinecraftHolder.getClientLevel().isEmptyBlock(engine3.pos)) {
                           if (++value4 < 0) {
                              CollectionsKt.throwCountOverflow();
                           }
                        }
                     }

                     value3 = value4;
                  }

                  if (value3 == 2) {
                     return false;
                  }
               }

               BlockState blockState5 = MinecraftHolder.getClientLevel().getBlockState(this.pos);
               if (!blockState5.isAir() && !blockState5.liquid() && (!SpeedMine.getMap().containsKey(this.pos) || !SpeedMine.testSpeedMine2(SpeedMine.INSTANCE))) {
                  if (SpeedMine.testSpeedMine3(SpeedMine.INSTANCE)) {
                     final int value2F = value2;
                     Function0 function0 = () -> {
                        if (!flag) {
                           this.applyPut(value2F, MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot(), direction);
                        }

                        return Unit.INSTANCE;
                     };
                     Rotation rotation = Rotation.vectorUtil.computeRotation(Vec3iUtil.computeVec3(this.pos).add(0.0, 0.5, 0.0));

                     RotationAction rotationAction = switch (SpeedMine$Engine.WhenMappings.values[SpeedMine.INSTANCE.getTiming().ordinal()]) {
                        case 1 -> (RotationAction)(new LookRotationAction(rotation, 150, function0));
                        case 2 -> new RotationAction(rotation, 150, function0);
                        case 3 -> (RotationAction)(new SilentRotationAction(rotation, 150, function0));
                        default -> throw new NoWhenBranchMatchedException();
                     };
                     RotationManager.INSTANCE.updateRotationAction(rotationAction);
                  }

                  if (flag || !SpeedMine.testSpeedMine3(SpeedMine.INSTANCE)) {
                     this.applyPut(value2, MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot(), direction);
                  }
               }
            }

            return false;
         }
      } else {
         if (this.arrayDeque.isEmpty() || SpeedMine.INSTANCE.getRebreak() != SpeedMine.Rebreak.NCP_AIR) {
            this.applyGetDirection(direction);
            MinecraftHolder.getLocalPlayer().swing(InteractionHand.MAIN_HAND);
         } else if (!this.arrayDeque.isEmpty()) {
            Iterable<Boolean> arrayDeque2 = this.arrayDeque;

            for (Object object3 : arrayDeque2) {
               Boolean booleanValue = (Boolean)object3;
               this.amount2 = this.amount;
               float amount5 = this.amount;
               SpeedMine instance3 = SpeedMine.INSTANCE;
               BlockState blockState6 = MinecraftHolder.getClientLevel().getBlockState(this.pos);
               BlockPos pos4 = this.pos;
               boolean flag6 = booleanValue;
               BlockPos blockPos3 = pos4;
               BlockState blockState7 = blockState6;
               this.amount = amount5 + SpeedMine.computeAmount(instance3, blockState7, blockPos3, value2, flag6);
            }
         }

         return false;
      }
   }

   public static boolean defaultImpl(SpeedMine$Engine engine, boolean flag, int value, Object object) {
      if ((value & 1) != 0) {
         flag = false;
      }

      return engine.checkGetBlockState(flag);
   }

   public final boolean isFlag() {
      return this.value > 0
         && SpeedMine.INSTANCE.getRebreak() == SpeedMine.Rebreak.GRIM
         && (
            !SpeedMine.testSpeedMine2(SpeedMine.INSTANCE, this.pos)
               || !SpeedMine.testSpeedMine4(SpeedMine.INSTANCE)
               || BreakingBlocksService.INSTANCE.checkContainsKey(this.pos)
         )
         && (Intrinsics.areEqual(this.pos, SpeedMine.getBlockPos2()) || SpeedMine.INSTANCE.getLinkedList().size() <= 1);
   }

   private final void applyPut(int value, int value2, Direction direction) {
      SpeedMine.updateSpeedMine(SpeedMine.INSTANCE, value, -1);
      SpeedMine.INSTANCE.getAtomicBoolean().set(true);
      PacketUtil.applyCurrentSequence(arg -> (Packet)(new ServerboundPlayerActionPacket(Action.STOP_DESTROY_BLOCK, this.pos, direction, arg)));
      if (SpeedMine.INSTANCE.getRebreak() == SpeedMine.Rebreak.NCP_AIR) {
         Packet serverboundPlayerActionPacket = (Packet)(new ServerboundPlayerActionPacket(Action.ABORT_DESTROY_BLOCK, this.pos, Direction.DOWN));
         PacketUtil.applySend(serverboundPlayerActionPacket);
         PacketUtil.applyCurrentSequence(arg2 -> (Packet)(new ServerboundPlayerActionPacket(Action.STOP_DESTROY_BLOCK, this.pos, direction, arg2)));
         PacketUtil.applyCurrentSequence(arg3 -> (Packet)(new ServerboundPlayerActionPacket(Action.START_DESTROY_BLOCK, this.pos, direction, arg3)));
      }

      AutoCrystal.INSTANCE.getMap().put(this.pos, MinecraftHolder.getTime());
      this.arrayDeque.clear();
      Packet serverboundSwingPacket = (Packet)(new ServerboundSwingPacket(InteractionHand.MAIN_HAND));
      PacketUtil.applySend(serverboundSwingPacket);
      int value3 = (!SpeedMine.INSTANCE.getDoubleMine() || this.value >= 1) && !SpeedMine.testSpeedMine(SpeedMine.INSTANCE) ? 0 : 1;
      if (value3 != 0) {
         TaskScheduler.defaultImpl(TaskScheduler.INSTANCE, value3, (byte)0, false, () -> SpeedMine.updateSpeedMine(SpeedMine.INSTANCE, value2, value), 6, null);
      } else {
         SpeedMine.updateSpeedMine(SpeedMine.INSTANCE, value2, value);
      }

      SpeedMine.getStopWatch().update();
      SpeedMine.setBlockPos(this.pos);
      int value4 = this.value++;
      SpeedMine.getMap().put(this.pos, MinecraftHolder.getTime());
      this.amount2 = 0.0F;
      this.amount = this.amount2;
   }

   public final void applyClear(AutoSwitchMode reason) {
      this.arrayDeque.clear();
      Direction firstOrNull = (Direction)CollectionsKt.firstOrNull(InteractionManager.INSTANCE.computeItems(this.pos));
      if (firstOrNull == null) {
         firstOrNull = MinecraftHolder.getLocalPlayer().getDirection();
      }

      Direction direction = firstOrNull;
      if (reason == AutoSwitchMode.SWITCH
         && SpeedMine.INSTANCE.getRebreak() == SpeedMine.Rebreak.NCP_AIR
         && MinecraftHolder.getClientLevel().isEmptyBlock(this.pos)) {
         this.amount = 0.0F;
         this.amount2 = this.amount;
         PacketUtil.applyCurrentSequence(arg -> (Packet)(new ServerboundPlayerActionPacket(Action.START_DESTROY_BLOCK, this.pos, direction, arg)));
      } else if (this.amount != 0.0F) {
         this.amount = 0.0F;
         this.amount2 = this.amount;
         Packet serverboundPlayerActionPacket = (Packet)(new ServerboundPlayerActionPacket(Action.ABORT_DESTROY_BLOCK, this.pos, Direction.DOWN));
         PacketUtil.applySend(serverboundPlayerActionPacket);
         this.applyGetDirection(direction);
      }
   }

   public final void applyClear() {
      this.arrayDeque.clear();
      if (this.amount != 0.0F) {
         Packet serverboundPlayerActionPacket = (Packet)(new ServerboundPlayerActionPacket(Action.ABORT_DESTROY_BLOCK, this.pos, Direction.DOWN));
         PacketUtil.applySend(serverboundPlayerActionPacket);
      }
   }

   public static final class WhenMappings {
      public static final int[] values;

      static {
         int[] values2 = new int[SpeedMine.Timing.values().length];

         try {
            values2[SpeedMine.Timing.VANILLA.ordinal()] = 1;
         } catch (NoSuchFieldError exception) {
         }

         try {
            values2[SpeedMine.Timing.SEQUENTIAL.ordinal()] = 2;
         } catch (NoSuchFieldError exception2) {
         }

         try {
            values2[SpeedMine.Timing.GRIM.ordinal()] = 3;
         } catch (NoSuchFieldError exception3) {
         }

         values = values2;
      }
   }
}
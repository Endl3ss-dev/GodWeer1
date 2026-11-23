// deobf by pusia
package su.catlean.client.features.modules.attack;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.MutablePropertyReference1Impl;
import kotlin.random.Random;
import kotlin.ranges.IntRange;
import kotlin.reflect.KProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.Rotation;
import su.catlean.api.data.SearchInvResult;
import su.catlean.api.enums.TargetFilter;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.PlaceModule;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.features.modules.misc.AutoSoup;
import su.catlean.client.services.InteractionManager;
import su.catlean.client.services.InventoryManager;
import su.catlean.client.services.RotationManager;
import su.catlean.client.services.rotation.RotationAction;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.math.Vec3iUtil;
import su.catlean.utils.network.PacketUtil;
import su.catlean.utils.player.InputPacketUtil;
import su.catlean.utils.player.PlayerUtil;

public final class AutoDripstope extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new MutablePropertyReference1Impl(AutoDripstope.class, "delay", "getDelay()I", 0), new MutablePropertyReference1Impl(AutoDripstope.class, "switchMode", "getSwitchMode()Lsu/catlean/uv;", 0), new MutablePropertyReference1Impl(AutoDripstope.class, "fastOpen", "getFastOpen()Z", 0) };
   @NotNull
   public static final AutoDripstope INSTANCE = new AutoDripstope();
   @NotNull
   private static final IntSetting delay = SettingFactory.defaultImpl(INSTANCE, "delay", 0, new IntRange(0, 15), null, null, 24, null);
   @NotNull
   private static final EnumSetting switchValue = SettingFactory.defaultImpl(INSTANCE, "switch", AutoSoup.Switch.SILENT_FULL, null, null, 12, null);
   @NotNull
   private static final BooleanSetting fastOpen = SettingFactory.defaultImpl(INSTANCE, "fast-open", true, null, null, 12, null);
   @NotNull
   private static PlayerUtil playerUtil = new PlayerUtil();
   @NotNull
   private static PlayerUtil playerUtil2 = new PlayerUtil();

   private AutoDripstope() {
      super("auto-dripstope", SubCategories.getSubCategory2(), null, 4, null);
   }

   private final int getDelay() {
      return ((Number)delay.computeObject(this, $$delegatedProperties[0])).intValue();
   }

   private final AutoSoup.Switch getSwitchMode() {
      return (AutoSoup.Switch)switchValue.computeObject(this, $$delegatedProperties[1]);
   }

   private final boolean getFastOpen() {
      return (Boolean)fastOpen.computeObject(this, $$delegatedProperties[2]);
   }

   @Flow
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      if (playerUtil.testValue(this.getDelay())) {
         BlockPos containing = BlockPos.containing((Position)this.computeVec3((Entity)MinecraftHolder.getLocalPlayer()));
         int blockInteractionRange = (int)Math.ceil(MinecraftHolder.getLocalPlayer().blockInteractionRange());
         Map<BlockPos, BlockState> linkedHashMap = new LinkedHashMap();
         int value = -blockInteractionRange;
         if (value <= blockInteractionRange) {
            while (true) {
               int value2 = -blockInteractionRange;
               if (value2 <= blockInteractionRange) {
                  while (true) {
                     int value3 = -blockInteractionRange;
                     if (value3 <= blockInteractionRange) {
                        while (true) {
                           BlockPos offset = containing.offset(value, value2, value3);
                           BlockState blockState = MinecraftHolder.getClientLevel().getBlockState(offset);
                           BlockState blockState2 = MinecraftHolder.getClientLevel().getBlockState(offset.below());
                           if (blockState.getBlock() instanceof TrapDoorBlock
                              && blockState.getValue((Property)TrapDoorBlock.HALF) == Half.BOTTOM
                              && (blockState2.isAir() || Intrinsics.areEqual(blockState2.getBlock(), Blocks.POINTED_DRIPSTONE))) {
                              Vec3 vec3 = Vec3iUtil.computeVec3(offset);
                              Iterable<Entity> entitiesForRendering = MinecraftHolder.getClientLevel().entitiesForRendering();
                              boolean flag;
                              if (entitiesForRendering instanceof Collection && ((Collection)entitiesForRendering).isEmpty()) {
                                 flag = false;
                              } else {
                                 Iterator<Entity> iterator = entitiesForRendering.iterator();

                                 while (true) {
                                    if (!iterator.hasNext()) {
                                       flag = false;
                                       break;
                                    }

                                    Entity object = iterator.next();
                                    Entity entity = (Entity)object;
                                    if (entity instanceof LivingEntity
                                       && !((LivingEntity)entity).isDeadOrDying()
                                       && Math.hypot(vec3.x - ((LivingEntity)entity).getX(), vec3.z - ((LivingEntity)entity).getZ()) < 0.8F
                                       && !Intrinsics.areEqual(entity, MinecraftHolder.getLocalPlayer())
                                       && ((LivingEntity)entity).getY() < offset.getY()) {
                                       flag = true;
                                       break;
                                    }
                                 }
                              }

                              if (flag) {
                                 linkedHashMap.put(offset, blockState);
                              }
                           }

                           if (value3 == blockInteractionRange) {
                              break;
                           }

                           value3++;
                        }
                     }

                     if (value2 == blockInteractionRange) {
                        break;
                     }

                     value2++;
                  }
               }

               if (value == blockInteractionRange) {
                  break;
               }

               value++;
            }
         }

         Iterable<Map.Entry<BlockPos, BlockState>> entrySet = linkedHashMap.entrySet();
         Iterator<Map.Entry<BlockPos, BlockState>> iterator2 = entrySet.iterator();
         Map.Entry<BlockPos, BlockState> object2;
         if (!iterator2.hasNext()) {
            object2 = null;
         } else {
            Map.Entry<BlockPos, BlockState> object3 = iterator2.next();
            if (!iterator2.hasNext()) {
               object2 = object3;
            } else {
               Entry entry = (Entry)object3;
               float amount = INSTANCE.computeAmount((BlockPos)entry.getKey());

               do {
                  Map.Entry<BlockPos, BlockState> object4 = iterator2.next();
                  Entry entry2 = (Entry)object4;
                  float amount2 = INSTANCE.computeAmount((BlockPos)entry2.getKey());
                  if (Float.compare(amount, amount2) > 0) {
                     object3 = object4;
                     amount = amount2;
                  }
               } while (iterator2.hasNext());

               object2 = object3;
            }
         }

         Entry entry3 = (Entry)object2;
         if (entry3 != null) {
            if ((Boolean)((BlockState)entry3.getValue()).getValue((Property)TrapDoorBlock.OPEN)
               || !MinecraftHolder.getClientLevel().getBlockState(((BlockPos)entry3.getKey()).below()).isAir()) {
               double nextDouble = Random.Default.nextDouble() / 10.0 + 0.5;
               Vec3 vec32 = new Vec3(((BlockPos)entry3.getKey()).getX() + nextDouble, ((BlockPos)entry3.getKey()).getY(), ((BlockPos)entry3.getKey()).getZ() + nextDouble);
               if (playerUtil2.testValue(5)) {
                  RotationManager.INSTANCE.updateRotationAction(new RotationAction(Rotation.vectorUtil.computeRotation(vec32), 100, () -> {
                     INSTANCE.applyGetX((BlockPos)entry3.getKey());
                     return Unit.INSTANCE;
                  }));
               }

               playerUtil.update();
            } else if (this.checkBlockInteractionRange((BlockPos)entry3.getKey())) {
               playerUtil.update();
            }
         }
      }
   }

   private final boolean checkBlockInteractionRange(BlockPos blockPos) {
      SearchInvResult searchInvResult = InventoryManager.INSTANCE
         .computeSearchInvResult(arg -> Intrinsics.areEqual(arg.getItem(), Items.POINTED_DRIPSTONE), this.getSwitchMode());
      if (searchInvResult.isFlag()) {
         BlockHitResult defaultImpl = InteractionManager.defaultImpl(
            InteractionManager.INSTANCE,
            blockPos.below(),
            -1,
            PlaceModule.Interact.Strict,
            TargetFilter.ALL,
            (float)MinecraftHolder.getLocalPlayer().blockInteractionRange(),
            (float)MinecraftHolder.getLocalPlayer().blockInteractionRange(),
            null,
            64,
            null
         );
         if (defaultImpl != null) {
            RotationManager.INSTANCE.updateRotationAction(new RotationAction(Rotation.vectorUtil.computeRotation(defaultImpl.getLocation()), 100, () -> {
               InventoryManager.INSTANCE.applyGetInventory(searchInvResult.getValue(), INSTANCE.getSwitchMode(), () -> {
                  InputPacketUtil.INSTANCE.applyWrite(true);
                  PacketUtil.applyCurrentSequence(arg2 -> (Packet)(new ServerboundUseItemOnPacket(InteractionHand.MAIN_HAND, defaultImpl, arg2)));
                  MinecraftHolder.getLocalPlayer().swing(InteractionHand.MAIN_HAND);
                  InputPacketUtil.INSTANCE.applyWrite(false);
                  INSTANCE.applyGetX(blockPos);
                  if (INSTANCE.getFastOpen()) {
                     INSTANCE.applyGetX(blockPos);
                  }

                  playerUtil2.update();
                  return Unit.INSTANCE;
               });
               return Unit.INSTANCE;
            }));
         }
      }

      return true;
   }

   private final void applyGetX(BlockPos blockPos) {
      double nextDouble = Random.Default.nextDouble() / 10.0 + 0.5;
      BlockHitResult blockHitResult = new BlockHitResult(new Vec3(blockPos.getX() + nextDouble, blockPos.getY(), blockPos.getZ() + nextDouble), Direction.DOWN, blockPos, false);
      MinecraftHolder.getMultiPlayerGameMode().useItemOn(MinecraftHolder.getLocalPlayer(), InteractionHand.MAIN_HAND, blockHitResult);
      MinecraftHolder.getLocalPlayer().swing(InteractionHand.MAIN_HAND);
   }

   public final float computeAmount(BlockPos pos) {
      return (float)Math.abs(
         Mth.wrapDegrees(
               Math.toDegrees(
                     Math.atan2(
                        Vec3iUtil.computeVec3(pos).z - MinecraftHolder.getLocalPlayer().getZ(),
                        Vec3iUtil.computeVec3(pos).x - MinecraftHolder.getLocalPlayer().getX()
                     )
                  )
                  - 90.0
            )
            - Mth.wrapDegrees(MinecraftHolder.getLocalPlayer().getYRot())
      );
   }

   static {
   }
}
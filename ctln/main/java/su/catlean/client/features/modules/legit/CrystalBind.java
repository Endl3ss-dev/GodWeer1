// deobf by pusia
package su.catlean.client.features.modules.legit;

import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.IntRange;
import kotlin.reflect.KProperty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.Rotation;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.event.events.world.EntitySpawn;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BindSetting;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.api.setting.values.Bind;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.services.InventoryManager;
import su.catlean.gofra.Flow;
import su.catlean.mixins.accessors.MinecraftAccessor;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.StopWatch;
import su.catlean.utils.async.TaskScheduler;
import su.catlean.utils.input.InputUtil;

public final class CrystalBind extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(CrystalBind.class, "crystalDelay", "getCrystalDelay()I", 0), new PropertyReference1Impl(CrystalBind.class, "crystalBind", "getCrystalBind()Lsu/catlean/dd;", 0), new PropertyReference1Impl(CrystalBind.class, "changePitch", "getChangePitch()Z", 0), new PropertyReference1Impl(CrystalBind.class, "crystalOptimizer", "getCrystalOptimizer()Z", 0), new PropertyReference1Impl(CrystalBind.class, "switchBack", "getSwitchBack()Z", 0) };
   @NotNull
   public static final CrystalBind INSTANCE = new CrystalBind();
   @NotNull
   private static final IntSetting delay = SettingFactory.defaultImpl(INSTANCE, "delay", 0, new IntRange(0, 250), null, null, 24, null);
   @NotNull
   private static final BindSetting crystalBind = SettingFactory.defaultImpl(INSTANCE, "crystal-bind", new Bind(0, false, false, 7, null), null, null, 12, null);
   @NotNull
   private static final BooleanSetting changePitch = SettingFactory.defaultImpl(INSTANCE, "change-pitch", false, null, null, 12, null);
   @NotNull
   private static final BooleanSetting optimizer = SettingFactory.defaultImpl(INSTANCE, "optimizer", false, null, null, 12, null);
   @NotNull
   private static final BooleanSetting switchBack = SettingFactory.defaultImpl(INSTANCE, "switch-back", false, null, null, 12, null);
   @NotNull
   private static Vec3 vec3 = Vec3.ZERO;
   @NotNull
   private static StopWatch stopWatch = new StopWatch();

   private CrystalBind() {
      super("crystal-bind", SubCategories.getSubCategory3(), null, 4, null);
   }

   private final int getCrystalDelay() {
      return ((Number)delay.computeObject(this, $$delegatedProperties[0])).intValue();
   }

   private final Bind getCrystalBind() {
      return (Bind)crystalBind.computeObject(this, $$delegatedProperties[1]);
   }

   private final boolean getChangePitch() {
      return (Boolean)changePitch.computeObject(this, $$delegatedProperties[2]);
   }

   private final boolean getCrystalOptimizer() {
      return (Boolean)optimizer.computeObject(this, $$delegatedProperties[3]);
   }

   private final boolean getSwitchBack() {
      return (Boolean)switchBack.computeObject(this, $$delegatedProperties[4]);
   }

   @Flow
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      boolean flag = MinecraftHolder.getMinecraft().hitResult instanceof EntityHitResult
         && ((EntityHitResult)MinecraftHolder.getMinecraft().hitResult).getEntity() instanceof EndCrystal;
      boolean flag2 = MinecraftHolder.getMinecraft().hitResult instanceof BlockHitResult
         && (
            Intrinsics.areEqual(
                  MinecraftHolder.getClientLevel().getBlockState(((BlockHitResult)MinecraftHolder.getMinecraft().hitResult).getBlockPos()).getBlock(),
                  Blocks.OBSIDIAN
               )
               || Intrinsics.areEqual(
                  MinecraftHolder.getClientLevel().getBlockState(((BlockHitResult)MinecraftHolder.getMinecraft().hitResult).getBlockPos()).getBlock(),
                  Blocks.BEDROCK
               )
         );
      if (InputUtil.checkContains(this.getCrystalBind().getValue()) && stopWatch.testValue2(this.getCrystalDelay() * (flag ? 1 : (flag2 ? 2 : 4)))) {
         InventoryManager instance = InventoryManager.INSTANCE;
         Item[] items = new Item[]{Items.END_CRYSTAL};
         int value = instance.computeSearchInvResult(items).getValue();
         instance = InventoryManager.INSTANCE;
         Block[] blocks = new Block[]{Blocks.OBSIDIAN};
         int value2 = instance.computeSearchInvResult(blocks).getValue();
         if (value2 == -1 && !flag2 && !flag || value == -1 || value >= 9 || value2 >= 9) {
            return;
         }

         if (flag) {
            MinecraftHolder.getMultiPlayerGameMode()
               .attack((Player)MinecraftHolder.getLocalPlayer(), ((EntityHitResult)MinecraftHolder.getMinecraft().hitResult).getEntity());
            MinecraftHolder.getLocalPlayer().swing(InteractionHand.MAIN_HAND);
            return;
         }

         int selectedSlot = MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot();
         if (!flag2) {
            if (MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot() != value2) {
               InventoryManager.INSTANCE.applyGetInventory2(value2);
            }

            ((MinecraftAccessor)MinecraftHolder.getMinecraft()).idoItemUse();
            TaskScheduler.defaultImpl(
               TaskScheduler.INSTANCE,
               this.getCrystalDelay(),
               (byte)0,
               false,
               () -> {
                  InventoryManager.INSTANCE.applyGetInventory2(value);
                  TaskScheduler.defaultImpl(
                     TaskScheduler.INSTANCE,
                     INSTANCE.getCrystalDelay(),
                     (byte)0,
                     false,
                     () -> {
                        Vec3 location;
                        label15: {
                           ((MinecraftAccessor)MinecraftHolder.getMinecraft()).idoItemUse();
                           HitResult hitResult = MinecraftHolder.getMinecraft().hitResult;
                           if (hitResult != null) {
                              location = hitResult.getLocation();
                              if (location != null) {
                                 break label15;
                              }
                           }

                           location = Vec3.ZERO;
                        }

                        vec3 = location;
                        if (INSTANCE.getSwitchBack()) {
                           TaskScheduler.defaultImpl(
                              TaskScheduler.INSTANCE,
                              INSTANCE.getCrystalDelay(),
                              (byte)0,
                              false,
                              () -> InventoryManager.INSTANCE.applyGetInventory2(selectedSlot),
                              2,
                              null
                           );
                        }
                     },
                     2,
                     null
                  );
               },
               2,
               null
            );
         } else {
            InventoryManager.INSTANCE.applyGetInventory2(value);
            TaskScheduler.defaultImpl(
               TaskScheduler.INSTANCE,
               this.getCrystalDelay(),
               (byte)0,
               false,
               () -> {
                  Vec3 location2;
                  label15: {
                     ((MinecraftAccessor)MinecraftHolder.getMinecraft()).idoItemUse();
                     HitResult hitResult2 = MinecraftHolder.getMinecraft().hitResult;
                     if (hitResult2 != null) {
                        location2 = hitResult2.getLocation();
                        if (location2 != null) {
                           break label15;
                        }
                     }

                     location2 = Vec3.ZERO;
                  }

                  vec3 = location2;
                  if (INSTANCE.getSwitchBack()) {
                     TaskScheduler.defaultImpl(
                        TaskScheduler.INSTANCE, INSTANCE.getCrystalDelay(), (byte)0, false, () -> InventoryManager.INSTANCE.applyGetInventory2(selectedSlot), 2, null
                     );
                  }
               },
               2,
               null
            );
         }
      }
   }

   @Flow
   private final void onEntitySpawn(EntitySpawn entitySpawn) {
      if (entitySpawn.getEntity() instanceof EndCrystal && !Intrinsics.areEqual(vec3, Vec3.ZERO) && entitySpawn.getEntity().distanceToSqr(vec3) < 4.0) {
         vec3 = Vec3.ZERO;
         if (this.getChangePitch()) {
            MinecraftHolder.getLocalPlayer()
               .setXRot(Rotation.vectorUtil.computeRotation(this.computeVec3(entitySpawn.getEntity()).add(0.0, 0.15, 0.0)).getRotation().getAmount2());
         }

         MinecraftHolder.getMultiPlayerGameMode().attack((Player)MinecraftHolder.getLocalPlayer(), entitySpawn.getEntity());
         MinecraftHolder.getLocalPlayer().swing(InteractionHand.MAIN_HAND);
         if (this.getCrystalOptimizer()) {
            MinecraftHolder.getClientLevel().removeEntity(((EndCrystal)entitySpawn.getEntity()).getId(), RemovalReason.KILLED);
         }
      }
   }

   static {
   }
}
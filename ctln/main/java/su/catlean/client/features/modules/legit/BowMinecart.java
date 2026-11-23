// deobf by pusia
package su.catlean.client.features.modules.legit;

import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.MutablePropertyReference1Impl;
import kotlin.reflect.KProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.Rotation;
import su.catlean.api.data.SearchInvResult;
import su.catlean.api.event.events.network.AfterSendPacket;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.features.modules.misc.AutoSoup;
import su.catlean.client.services.CrystalPredictor;
import su.catlean.client.services.InventoryManager;
import su.catlean.client.services.RotationManager;
import su.catlean.client.services.rotation.RotationAction;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.inventory.EnchantmentUtil;
import su.catlean.utils.math.Vec3iUtil;
import su.catlean.utils.math.VectorUtil;

public final class BowMinecart extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new MutablePropertyReference1Impl(BowMinecart.class, "switchMode", "getSwitchMode()Lsu/catlean/uv;", 0), new MutablePropertyReference1Impl(BowMinecart.class, "selfDamageCheck", "getSelfDamageCheck()Z", 0) };
   @NotNull
   public static final BowMinecart INSTANCE = new BowMinecart();
   @NotNull
   private static final EnumSetting switchValue = SettingFactory.defaultImpl(INSTANCE, "switch", AutoSoup.Switch.SILENT_FULL, null, null, 12, null);
   @NotNull
   private static final BooleanSetting checkSelfDamage = SettingFactory.defaultImpl(INSTANCE, "check-self-damage", false, null, null, 12, null);

   private BowMinecart() {
      super("bow-minecart", SubCategories.getSubCategory3(), null, 4, null);
   }

   private final AutoSoup.Switch getSwitchMode() {
      return (AutoSoup.Switch)switchValue.computeObject(this, $$delegatedProperties[0]);
   }

   private final boolean getSelfDamageCheck() {
      return (Boolean)checkSelfDamage.computeObject(this, $$delegatedProperties[1]);
   }

   @Flow
   private final void onAfterSendPacket(AfterSendPacket afterSendPacket) {
      if (afterSendPacket.getPacket() instanceof ServerboundPlayerActionPacket
         && ((ServerboundPlayerActionPacket)afterSendPacket.getPacket()).getAction() == Action.RELEASE_USE_ITEM
         && MinecraftHolder.getLocalPlayer().getTicksUsingItem() > 2
         && Intrinsics.areEqual(MinecraftHolder.getLocalPlayer().getMainHandItem().getItem(), Items.BOW)
         && EnchantmentUtil.checkContains(MinecraftHolder.getLocalPlayer().getMainHandItem(), Enchantments.FLAME)) {
         BlockPos blockPos = this.getBlockPos();
         if (blockPos != null) {
            float blockInteractionRange = (float)MinecraftHolder.getLocalPlayer().blockInteractionRange();
            double distanceTo = MinecraftHolder.getLocalPlayer().getEyePosition().distanceTo(Vec3iUtil.computeVec3(blockPos));
            if (2.3F <= distanceTo ? distanceTo <= blockInteractionRange : false) {
               boolean flag = this.getSwitchMode() == AutoSoup.Switch.SILENT || this.getSwitchMode() == AutoSoup.Switch.NORMAL;
               SearchInvResult searchInvResult;
               if (flag) {
                  InventoryManager instance = InventoryManager.INSTANCE;
                  Item[] items = new Item[]{Items.RAIL, Items.POWERED_RAIL, Items.DETECTOR_RAIL, Items.ACTIVATOR_RAIL};
                  searchInvResult = instance.computeSearchInvResult(items);
               } else {
                  InventoryManager instance2 = InventoryManager.INSTANCE;
                  Item[] items2 = new Item[]{Items.RAIL, Items.POWERED_RAIL, Items.DETECTOR_RAIL, Items.ACTIVATOR_RAIL};
                  searchInvResult = instance2.computeSearchInvResult2(items2);
               }

               SearchInvResult searchInvResult2 = searchInvResult;
               if (flag) {
                  InventoryManager instance3 = InventoryManager.INSTANCE;
                  Item[] items3 = new Item[]{Items.TNT_MINECART};
                  searchInvResult = instance3.computeSearchInvResult(items3);
               } else {
                  InventoryManager instance4 = InventoryManager.INSTANCE;
                  Item[] items4 = new Item[]{Items.TNT_MINECART};
                  searchInvResult = instance4.computeSearchInvResult2(items4);
               }

               SearchInvResult searchInvResult3 = searchInvResult;
               if (searchInvResult2.isFlag() && searchInvResult3.isFlag()) {
                  RotationManager instance5 = RotationManager.INSTANCE;
                  VectorUtil vectorUtil = Rotation.vectorUtil;
                  BlockPos above = blockPos.above();
                  instance5.updateRotationAction(
                     new RotationAction(
                        vectorUtil.computeRotation(Vec3iUtil.computeVec3(above)),
                        100,
                        () -> {
                           InventoryManager.INSTANCE
                              .applyGetInventory(
                                 searchInvResult2.getValue(),
                                 INSTANCE.getSwitchMode(),
                                 () -> {
                                    MinecraftHolder.getMultiPlayerGameMode()
                                       .useItemOn(
                                          MinecraftHolder.getLocalPlayer(),
                                          InteractionHand.MAIN_HAND,
                                          new BlockHitResult(new Vec3(blockPos.getX() + 0.5, blockPos.above().getY(), blockPos.getZ() + 0.0), Direction.UP, blockPos, false)
                                       );
                                    return Unit.INSTANCE;
                                 }
                              );
                           InventoryManager.INSTANCE
                              .checkOrdinal(
                                 Items.TNT_MINECART,
                                 INSTANCE.getSwitchMode(),
                                 () -> {
                                    MinecraftHolder.getMultiPlayerGameMode()
                                       .useItemOn(
                                          MinecraftHolder.getLocalPlayer(),
                                          InteractionHand.MAIN_HAND,
                                          new BlockHitResult(
                                             new Vec3(blockPos.getX() + 0.5, blockPos.above().getY() + 0.125, blockPos.getZ() + 0.5), Direction.UP, blockPos.above(), false
                                          )
                                       );
                                    return Unit.INSTANCE;
                                 }
                              );
                           return Unit.INSTANCE;
                        }
                     )
                  );
               }
            }
         }
      }
   }

   private final BlockPos getBlockPos() {
      double value = MinecraftHolder.getLocalPlayer().getX();
      double value2 = MinecraftHolder.getLocalPlayer().getEyePosition().y - 0.1000000014901161;
      double value3 = MinecraftHolder.getLocalPlayer().getZ();
      double radians = Math.toRadians(MinecraftHolder.getLocalPlayer().getYRot());
      double radians2 = Math.toRadians(MinecraftHolder.getLocalPlayer().getXRot());
      double value4 = -Math.sin(radians) * Math.cos(radians2);
      double value5 = -Math.sin(radians2);
      double value6 = Math.cos(radians) * Math.cos(radians2);
      float ticksUsingItem = MinecraftHolder.getLocalPlayer().getTicksUsingItem() / 20.0F;
      ticksUsingItem = (ticksUsingItem * ticksUsingItem + ticksUsingItem * 2.0F) / 3.0F;
      if (ticksUsingItem > 1.0F || ticksUsingItem == 0.0F) {
         ticksUsingItem = 1.0F;
      }

      double value7 = Math.sqrt(value4 * value4 + value5 * value5 + value6 * value6);
      value4 /= value7;
      value5 /= value7;
      value6 /= value7;
      float value8 = ticksUsingItem * 3;
      value4 *= value8;
      value5 *= value8;
      value6 *= value8;
      if (!MinecraftHolder.getLocalPlayer().onGround()) {
         value5 += MinecraftHolder.getLocalPlayer().getDeltaMovement().y();
      }

      Vec3 vec3 = null;
      int value9 = 0;

      BlockHitResult clip;
      while (true) {
         label69: {
            if (value9 < 300) {
               vec3 = new Vec3(value, value2, value3);
               value += value4;
               value2 += value5;
               value3 += value6;
               value4 *= 0.99;
               value5 *= 0.99;
               value6 *= 0.99;
               value5 -= 0.05F;
               Vec3 vec32 = new Vec3(value, value2, value3);

               for (Entity entitiesForRenderingItem : MinecraftHolder.getClientLevel().entitiesForRendering()) {
                  if (!(entitiesForRenderingItem instanceof Arrow)
                     && !entitiesForRenderingItem.equals(MinecraftHolder.getLocalPlayer())
                     && entitiesForRenderingItem.getBoundingBox().intersects(new AABB(value - 0.3, value2 - 0.3, value3 - 0.3, value + 0.3, value2 + 0.3, value3 + 0.3))) {
                     return null;
                  }
               }

               clip = MinecraftHolder.getClientLevel()
                  .clip(new ClipContext((Vec3)vec3, vec32, Block.COLLIDER, Fluid.NONE, (Entity)MinecraftHolder.getLocalPlayer()));
               if (clip.getType() == Type.BLOCK) {
                  if (!this.getSelfDamageCheck()) {
                     break;
                  }

                  CrystalPredictor instance = CrystalPredictor.INSTANCE;
                  BlockPos above = clip.getBlockPos().above();
                  if (!(
                     CrystalPredictor.defaultImpl(instance, Vec3iUtil.computeVec3(above), null, 0, false, null, null, 62, null)
                        > MinecraftHolder.getLocalPlayer().getHealth() + MinecraftHolder.getLocalPlayer().getAbsorptionAmount()
                  )) {
                     break;
                  }
                  break label69;
               }

               if (!(value2 <= -65.0)) {
                  break label69;
               }
            }

            return null;
         }

         value9++;
      }

      return clip.getBlockPos();
   }

   public final boolean isFlag() {
      return this.getEnabled()
         && Intrinsics.areEqual(MinecraftHolder.getLocalPlayer().getMainHandItem().getItem(), Items.BOW)
         && EnchantmentUtil.checkContains(MinecraftHolder.getLocalPlayer().getMainHandItem(), Enchantments.FLAME);
   }

   static {
   }
}
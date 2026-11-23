// deobf by pusia
package su.catlean.client.features.modules.player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.IntRange;
import kotlin.reflect.KProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.DamageData;
import su.catlean.api.data.Rotation;
import su.catlean.api.data.SearchInvResult;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.GroupSetting;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.api.setting.values.GroupValue;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.PlaceModule;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.features.modules.attack.AutoCrystal;
import su.catlean.client.services.ChunkScanner;
import su.catlean.client.services.InventoryManager;
import su.catlean.client.services.PlayerPredictionService;
import su.catlean.client.services.RotationManager;
import su.catlean.client.services.ServerInfoService;
import su.catlean.client.services.rotation.RotationAction;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.StopWatch;
import su.catlean.utils.inventory.EnchantmentUtil;
import su.catlean.utils.inventory.InventoryUtil;
import su.catlean.utils.world.WorldUtil;

public final class AutoMend extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(AutoMend.class, "minDurability", "getMinDurability()I", 0), new PropertyReference1Impl(AutoMend.class, "maxDurability", "getMaxDurability()I", 0), new PropertyReference1Impl(AutoMend.class, "overrideOnLoot", "getOverrideOnLoot()Z", 0), new PropertyReference1Impl(AutoMend.class, "xpFactor", "getXpFactor()I", 0), new PropertyReference1Impl(AutoMend.class, "pauseGroup", "getPauseGroup()Lsu/catlean/nl;", 0), new PropertyReference1Impl(AutoMend.class, "ifMove", "getIfMove()Z", 0), new PropertyReference1Impl(AutoMend.class, "ifMining", "getIfMining()Z", 0), new PropertyReference1Impl(AutoMend.class, "ifEating", "getIfEating()Z", 0), new PropertyReference1Impl(AutoMend.class, "ifNotInHole", "getIfNotInHole()Z", 0) };
   @NotNull
   public static final AutoMend INSTANCE = new AutoMend();
   @NotNull
   private static final IntSetting minDurability = SettingFactory.defaultImpl(INSTANCE, "min-durability", 30, new IntRange(0, 100), null, null, 24, null);
   @NotNull
   private static final IntSetting maxDurability = SettingFactory.defaultImpl(INSTANCE, "max-durability", 90, new IntRange(0, 100), null, null, 24, null);
   @NotNull
   private static final BooleanSetting overrideOnLoot = SettingFactory.defaultImpl(INSTANCE, "override-on-loot", true, null, null, 12, null);
   @NotNull
   private static final IntSetting xpFactor = SettingFactory.defaultImpl(INSTANCE, "xp-factor", 1, new IntRange(1, 16), null, null, 24, null);
   @NotNull
   private static final GroupSetting pause = SettingFactory.defaultImpl((su.catlean.api.setting.SettingHolder)INSTANCE, "pause", PlaceModule.Pause.BOOLS, null, 4, null);
   @NotNull
   private static final BooleanSetting ifMove = SettingFactory.defaultImpl(INSTANCE, "if-move", true, INSTANCE.getPauseGroup(), null, 8, null);
   @NotNull
   private static final BooleanSetting ifMining = SettingFactory.defaultImpl(INSTANCE, "if-mining", false, INSTANCE.getPauseGroup(), null, 8, null);
   @NotNull
   private static final BooleanSetting ifEating = SettingFactory.defaultImpl(INSTANCE, "if-eating", false, INSTANCE.getPauseGroup(), null, 8, null);
   @NotNull
   private static final BooleanSetting ifNotInHole = SettingFactory.defaultImpl(INSTANCE, "if-not-in-hole", false, INSTANCE.getPauseGroup(), null, 8, null);
   @NotNull
   private static final StopWatch stopWatch = new StopWatch();
   private static boolean flag;

   private AutoMend() {
      super("auto-mend", SubCategories.getSubCategory10(), null, 4, null);
   }

   private final int getMinDurability() {
      return ((Number)minDurability.computeObject(this, $$delegatedProperties[0])).intValue();
   }

   private final int getMaxDurability() {
      return ((Number)maxDurability.computeObject(this, $$delegatedProperties[1])).intValue();
   }

   private final boolean getOverrideOnLoot() {
      return (Boolean)overrideOnLoot.computeObject(this, $$delegatedProperties[2]);
   }

   private final int getXpFactor() {
      return ((Number)xpFactor.computeObject(this, $$delegatedProperties[3])).intValue();
   }

   private final GroupValue getPauseGroup() {
      return (GroupValue)pause.computeObject(this, $$delegatedProperties[4]);
   }

   private final boolean getIfMove() {
      return (Boolean)ifMove.computeObject(this, $$delegatedProperties[5]);
   }

   private final boolean getIfMining() {
      return (Boolean)ifMining.computeObject(this, $$delegatedProperties[6]);
   }

   private final boolean getIfEating() {
      return (Boolean)ifEating.computeObject(this, $$delegatedProperties[7]);
   }

   private final boolean getIfNotInHole() {
      return (Boolean)ifNotInHole.computeObject(this, $$delegatedProperties[8]);
   }

   @NotNull
   public final StopWatch getStopWatch() {
      return stopWatch;
   }

   public final boolean isFlag() {
      return flag;
   }

   public final void setFlag(boolean flag) {
      AutoMend.flag = flag;
   }

   @Override
   public void update4() {
      flag = false;
   }

   @Flow(
      priority = -10
   )
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      InventoryManager instance = InventoryManager.INSTANCE;
      Item[] items = new Item[]{Items.EXPERIENCE_BOTTLE};
      SearchInvResult searchInvResult = instance.computeSearchInvResult(items);
      if ((
            !this.getIfMining()
               || !MinecraftHolder.getMultiPlayerGameMode().isDestroying()
                  && (SpeedMine.INSTANCE.getLinkedList().isEmpty() || !SpeedMine.INSTANCE.getResetOnSwitch())
         )
         && stopWatch.testValue(500)) {
         if (!this.getIfMove() || !WorldUtil.INSTANCE.isFlag4()) {
            if (!this.getIfEating() || !MinecraftHolder.getLocalPlayer().isUsingItem()) {
               if (this.getIfNotInHole()) {
                  ChunkScanner instance2 = ChunkScanner.INSTANCE;
                  BlockPos containing = BlockPos.containing((Position)this.computeVec3((Entity)MinecraftHolder.getLocalPlayer()));
                  if (!instance2.checkOffset(containing)) {
                     return;
                  }
               }

               if (AutoCrystal.INSTANCE.getEnabled()
                  && AutoCrystal.INSTANCE.getPlayer() != null
                  && AutoCrystal.INSTANCE.getAtomicReference().get() != null
                  && ((DamageData)AutoCrystal.INSTANCE.getAtomicReference().get()).getDamageData().getAmount() > 4.0F) {
                  flag = false;
               } else {
                  Iterable<Entity> entitiesForRendering = MinecraftHolder.getClientLevel().entitiesForRendering();
                  boolean flag2 = false;
                  var arrayList = new ArrayList();
                  boolean flag3 = false;

                  for (Object object : entitiesForRendering) {
                     if (object instanceof ItemEntity) {
                        arrayList.add(object);
                     }
                  }

                  entitiesForRendering = arrayList;
                  flag2 = false;
                  boolean flag4;
                  if (entitiesForRendering instanceof Collection && ((Collection)entitiesForRendering).isEmpty()) {
                     flag4 = false;
                  } else {
                     Iterator<Entity> iterator = entitiesForRendering.iterator();

                     while (true) {
                        if (!iterator.hasNext()) {
                           flag4 = false;
                           break;
                        }

                        Entity object2 = iterator.next();
                        ItemEntity itemEntity = (ItemEntity)object2;
                        if (Intrinsics.areEqual(itemEntity.getItem().getItem(), Items.EXPERIENCE_BOTTLE)
                           && INSTANCE.computeVec3((Entity)itemEntity).distanceTo(INSTANCE.computeVec3((Entity)MinecraftHolder.getLocalPlayer())) < 1.0) {
                           flag4 = true;
                           break;
                        }
                     }
                  }

                  boolean flag6 = flag4;
                  float maxDurability2 = flag6 && this.getOverrideOnLoot() ? 100.0F : this.getMaxDurability();
                  float minDurability2 = flag6 && this.getOverrideOnLoot() ? 100.0F : this.getMinDurability();
                  Iterable<ItemStack> inventory = InventoryUtil.fetchInventory(MinecraftHolder.getLocalPlayer());
                  flag3 = false;
                  var arrayList2 = new ArrayList();
                  boolean flag7 = false;

                  for (Object object3 : inventory) {
                     ItemStack itemStack = (ItemStack)object3;
                     if (EnchantmentUtil.checkContains(itemStack, Enchantments.MENDING)) {
                        arrayList2.add(object3);
                     }
                  }

                  List list = arrayList2;
                  if (!flag) {
                     Iterator iterator2 = list.iterator();
                     Float floatValue;
                     if (!iterator2.hasNext()) {
                        floatValue = null;
                     } else {
                        ItemStack itemStack2 = (ItemStack)iterator2.next();
                        flag7 = false;
                        float maxDamage = INSTANCE.fetchMaxDamage(itemStack2);

                        while (iterator2.hasNext()) {
                           ItemStack itemStack3 = (ItemStack)iterator2.next();
                           float maxDamage2 = INSTANCE.fetchMaxDamage(itemStack3);
                           maxDamage = Math.min(maxDamage, maxDamage2);
                        }

                        floatValue = maxDamage;
                     }

                     if ((floatValue != null ? floatValue : 100.0F) < minDurability2) {
                        flag = true;
                     }
                  }

                  if (flag) {
                     Iterator iterator3 = list.iterator();
                     Float floatValue2;
                     if (!iterator3.hasNext()) {
                        floatValue2 = null;
                     } else {
                        ItemStack itemStack4 = (ItemStack)iterator3.next();
                        flag7 = false;
                        float maxDamage3 = INSTANCE.fetchMaxDamage(itemStack4);

                        while (iterator3.hasNext()) {
                           ItemStack itemStack5 = (ItemStack)iterator3.next();
                           float maxDamage4 = INSTANCE.fetchMaxDamage(itemStack5);
                           maxDamage3 = Math.min(maxDamage3, maxDamage4);
                        }

                        floatValue2 = maxDamage3;
                     }

                     if ((floatValue2 != null ? floatValue2 : 100.0F) >= maxDurability2) {
                        flag = false;
                     }
                  }

                  if (MinecraftHolder.getLocalPlayer().isFallFlying()
                     || MinecraftHolder.getClientLevel()
                           .getBlockState(BlockPos.containing((Position)this.computeVec3((Entity)MinecraftHolder.getLocalPlayer())).below())
                           .canBeReplaced()
                        && MinecraftHolder.getClientLevel()
                           .getBlockState(BlockPos.containing((Position)this.computeVec3((Entity)MinecraftHolder.getLocalPlayer())).below(2))
                           .canBeReplaced()
                     || !searchInvResult.isFlag()) {
                     flag = false;
                  }

                  if (flag) {
                     Vec3 boundingBox = PlayerPredictionService.INSTANCE
                        .fetchBoundingBox(
                           (Player)MinecraftHolder.getLocalPlayer(),
                           (int)((float)Math.ceil(ServerInfoService.defaultImpl(ServerInfoService.INSTANCE, null, 1, null) / 50.0F))
                        );
                     RotationManager.INSTANCE.updateRotationAction(new RotationAction(Rotation.vectorUtil.computeRotation(boundingBox), 150, () -> {
                        InventoryManager.INSTANCE.applyGetInventory(searchInvResult.getValue(), INSTANCE.getXpFactor());
                        return Unit.INSTANCE;
                     }));
                  }
               }
            }
         }
      }
   }

   private final float fetchMaxDamage(ItemStack itemStack) {
      return !itemStack.isEmpty() && itemStack.getMaxDamage() != 0
         ? (float)(itemStack.getMaxDamage() - itemStack.getDamageValue()) / itemStack.getMaxDamage() * 100.0F
         : 100.0F;
   }

   static {
   }
}
// deobf by pusia
package su.catlean.client.features.modules.protect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import su.catlean.api.data.ConfigState;
import su.catlean.api.data.DamageComparatorResult;
import su.catlean.api.data.DamageData;
import su.catlean.client.features.modules.attack.Aura;
import su.catlean.client.features.modules.attack.AutoCrystal;
import su.catlean.client.features.modules.basic.AntiKnockBack;
import su.catlean.client.features.modules.player.SpeedMine;
import su.catlean.client.services.CrystalPredictor;
import su.catlean.client.services.ServerConfigProfile;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.inventory.InventoryUtil;
import su.catlean.utils.math.Vec3iUtil;
import su.catlean.utils.player.PlayerNameUtil;

public final class SurroundPlanner {
   @Nullable
   public static final DamageData computeDamageData(ConfigState configState, List clearedList) {
      if (clearedList.isEmpty()) {
         return null;
      } else {
         Iterable clearedList2 = clearedList;
         Iterator iterator = clearedList2.iterator();
         if (!iterator.hasNext()) {
            throw new NoSuchElementException();
         } else {
            Object object = iterator.next();
            Object object2;
            if (!iterator.hasNext()) {
               object2 = object;
            } else {
               DamageData damageData = (DamageData)object;
               float amount = damageData.getDamageData().getAmount();

               do {
                  Object object3 = iterator.next();
                  DamageData damageData2 = (DamageData)object3;
                  float amount2 = damageData2.getDamageData().getAmount();
                  if (Float.compare(amount, amount2) < 0) {
                     object = object3;
                     amount = amount2;
                  }
               } while (iterator.hasNext());

               object2 = object;
            }

            DamageData damageData3 = (DamageData)object2;
            if (configState.isFlag6() && clearedList.size() > 1) {
               for (DamageData cleared : (Iterable<DamageData>)clearedList) {
                  if (!Intrinsics.areEqual(cleared, damageData3)) {
                     float amount3 = damageData3.getDamageData().getAmount() - cleared.getDamageData().getAmount();
                     float amount22 = damageData3.getDamageData().getAmount2() - cleared.getDamageData().getAmount2();
                     boolean flag4 = damageData3.getDamageData().getAmount2() > cleared.getDamageData().getAmount2();
                     if (flag4
                        && amount22 > configState.getAmount8()
                        && (amount3 <= configState.getAmount8() || cleared.getDamageData().isFlag2())
                        && (!damageData3.getDamageData().isFlag2() || cleared.getDamageData().isFlag2())) {
                        damageData3 = cleared;
                        break;
                     }
                  }
               }
            }

            if (damageData3.getObject() instanceof BlockHitResult && clearedList.size() > 1) {
               BlockPos blockPos = ((BlockHitResult)damageData3.getObject()).getBlockPos();
               List<Object> defaultImpl = SpeedMine.defaultImpl(SpeedMine.INSTANCE, false, 1, null);
               BlockPos blockPosition = configState.getTarget().blockPosition();
               boolean flag5 = blockPos.getX() != blockPosition.getX() || blockPos.getZ() != blockPosition.getZ();
               if (defaultImpl.contains(blockPos) && flag5) {
                  Iterable clearedList3 = clearedList;
                  boolean flag6 = false;
                  var arrayList = new ArrayList();

                  for (Object object4 : clearedList3) {
                     DamageData damageData4 = (DamageData)object4;
                     if (damageData4.getObject() instanceof BlockHitResult && !defaultImpl.contains(((BlockHitResult)damageData4.getObject()).getBlockPos())) {
                        arrayList.add(object4);
                     }
                  }

                  clearedList3 = arrayList;
                  flag6 = false;
                  Iterator iterator2 = clearedList3.iterator();
                  if (!iterator2.hasNext()) {
                     object2 = null;
                  } else {
                     Object object5 = iterator2.next();
                     if (!iterator2.hasNext()) {
                        object2 = object5;
                     } else {
                        DamageData damageData5 = (DamageData)object5;
                        float amount4 = damageData5.getDamageData().getAmount();

                        do {
                           Object object6 = iterator2.next();
                           DamageData damageData6 = (DamageData)object6;
                           float amount5 = damageData6.getDamageData().getAmount();
                           if (Float.compare(amount4, amount5) < 0) {
                              object5 = object6;
                              amount4 = amount5;
                           }
                        } while (iterator2.hasNext());

                        object2 = object5;
                     }
                  }

                  DamageData damageData7 = (DamageData)object2;
                  if (damageData7 != null) {
                     return damageData7;
                  }
               }
            }

            return damageData3;
         }
      }
   }

   private static final boolean checkGetAbsorptionAmount(float amount, float amount2, ConfigState configState) {
      if (configState.isFlag2()) {
         float health = configState.getTarget().getHealth() + configState.getTarget().getAbsorptionAmount();
         float health2 = MinecraftHolder.getLocalPlayer().getHealth() + MinecraftHolder.getLocalPlayer().getAbsorptionAmount();
         boolean flag = amount * configState.getValue() > health;
         boolean flag2 = amount2 < health2;
         return amount2 > configState.getAmount3() && flag && flag2;
      } else {
         return false;
      }
   }

   private static final boolean checkGetMaxDamage(float amount, ConfigState configState) {
      float health = configState.getTarget().getHealth() + configState.getTarget().getAbsorptionAmount();
      if (!(amount < 0.6) && configState.isFlag13()) {
         if (!(amount * configState.getValue() > health) || !(amount > 4.0F) && Aura.INSTANCE.isFlag()) {
            for (ItemStack inventoryItem : (Iterable<ItemStack>)InventoryUtil.fetchInventory(configState.getTarget())) {
               if (!Intrinsics.areEqual(inventoryItem.getItem(), Items.AIR)
                  && (float)(inventoryItem.getMaxDamage() - inventoryItem.getDamageValue()) / inventoryItem.getMaxDamage() * 100 < configState.getAmount5()) {
                  return true;
               }
            }

            if (configState.isFlag11()
               && Intrinsics.areEqual(
                  MinecraftHolder.getClientLevel()
                     .getBlockState(BlockPos.containing((Position)AntiKnockBack.INSTANCE.computeVec3((Entity)configState.getTarget())))
                     .getBlock(),
                  Blocks.NETHER_PORTAL
               )) {
               return true;
            } else if (configState.isFlag10() && AutoCrystal.INSTANCE.isFlag2()) {
               return true;
            } else {
               if (configState.isFlag12() && configState.getTarget().isHolding(Items.BOW)) {
                  Iterable entities = configState.getEntities();
                  boolean flag = false;
                  var arrayList = new ArrayList();

                  for (Object object : entities) {
                     if (object instanceof Arrow) {
                        arrayList.add(object);
                     }
                  }

                  entities = arrayList;
                  flag = false;
                  boolean flag3;
                  if (entities instanceof Collection && ((Collection)entities).isEmpty()) {
                     flag3 = false;
                  } else {
                     Iterator iterator = entities.iterator();

                     while (true) {
                        if (!iterator.hasNext()) {
                           flag3 = false;
                           break;
                        }

                        Object object2 = iterator.next();
                        Arrow arrow = (Arrow)object2;
                        if (arrow.tickCount < 5
                           && AntiKnockBack.INSTANCE.computeVec3((Entity)arrow).y > configState.getTarget().getY()
                           && configState.getTarget().getEyePosition().distanceToSqr(AntiKnockBack.INSTANCE.computeVec3((Entity)arrow)) < 9.0) {
                           flag3 = true;
                           break;
                        }
                     }
                  }

                  if (flag3) {
                     return true;
                  }
               }

               return health <= configState.getAmount6() && !Aura.INSTANCE.isFlag();
            }
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   @Nullable
   public static final DamageComparatorResult fetchAbsorptionAmount(
      AutoCrystal self, BlockPos pos, ConfigState configState, boolean flag, boolean flag2, boolean flag3
   ) {
      Vec3 vec3 = Vec3iUtil.computeVec3(pos).add(0.0, 0.5, 0.0);
      List defaultImpl = flag2 ? SpeedMine.defaultImpl(SpeedMine.INSTANCE, false, 1, null) : CollectionsKt.emptyList();
      List listOf = flag3 ? CollectionsKt.listOf(pos) : defaultImpl;
      CrystalPredictor instance = CrystalPredictor.INSTANCE;
      Player target = configState.getTarget();
      int value = flag2 ? self.getValue() : 0;
      int isFlag5 = (configState.isFlag5() ? 1 : 0);
      List emptyList = flag3 ? CollectionsKt.emptyList() : listOf;
      List emptyList2 = flag3 ? listOf : CollectionsKt.emptyList();
      List list = emptyList;
      boolean flag4 = isFlag5 != 0;
      int value2 = value;
      Player player = target;
      float amount = instance.computeAmount(vec3, player, value2, flag4, list, emptyList2);
      boolean flag5 = checkGetMaxDamage(amount, configState) || amount > configState.getAmount4();
      if (!flag5 && !flag) {
         return null;
      } else {
         ServerConfigProfile instance2 = ServerConfigProfile.INSTANCE;
         CrystalPredictor instance3 = CrystalPredictor.INSTANCE;
         Player localPlayer = (Player)MinecraftHolder.getLocalPlayer();
         isFlag5 = self.computeValue(flag2);
         boolean isFlag52 = configState.isFlag5();
         List emptyList3 = flag3 ? CollectionsKt.emptyList() : listOf;
         emptyList2 = flag3 ? listOf : CollectionsKt.emptyList();
         list = emptyList3;
         flag4 = isFlag52;
         value2 = isFlag5;
         player = localPlayer;
         float amount2 = instance2.computeAmount(instance3.computeAmount(vec3, player, value2, flag4, list, emptyList2));
         boolean checkGetAbsorptionAmount2 = checkGetAbsorptionAmount(amount, amount2, configState);
         if (configState.getFriendProtection() != AutoCrystal.FriendProtection.NONE) {
            Iterable entities = configState.getEntities();
            boolean flag6 = false;
            var arrayList = new ArrayList();

            for (Object object : entities) {
               if (object instanceof Player) {
                  arrayList.add(object);
               }
            }

            entities = arrayList;
            flag6 = false;
            arrayList = new ArrayList();

            for (Object object2 : entities) {
               Player player2 = (Player)object2;
               if (player2.isAlive() && PlayerNameUtil.checkGetGameProfile(player2)) {
                  arrayList.add(object2);
               }
            }

            for (Player player3 : (Iterable<Player>)arrayList) {
               instance = CrystalPredictor.INSTANCE;
               value = flag2 ? self.getValue() : 0;
               boolean isFlag53 = configState.isFlag5();
               emptyList = flag3 ? CollectionsKt.emptyList() : listOf;
               emptyList2 = flag3 ? listOf : CollectionsKt.emptyList();
               list = emptyList;
               flag4 = isFlag53;
               value2 = value;
               float amount3 = instance.computeAmount(vec3, player3, value2, flag4, list, emptyList2);
               if (configState.getFriendProtection() == AutoCrystal.FriendProtection.ONLY_LETHAL) {
                  if (amount3 >= player3.getHealth() + player3.getAbsorptionAmount()) {
                     return null;
                  }
               } else {
                  amount2 = Math.max(amount2, amount3);
               }
            }
         }

         if (!checkGetAbsorptionAmount(amount, amount2, checkGetAbsorptionAmount2, configState)) {
            return null;
         } else {
            return amount2 > configState.getAmount3() && !checkGetAbsorptionAmount2
               ? null
               : new DamageComparatorResult(amount, amount2, checkGetAbsorptionAmount2, amount >= configState.getTarget().getHealth() + configState.getTarget().getAbsorptionAmount());
         }
      }
   }

   private static final boolean checkGetAbsorptionAmount(float amount, float amount2, boolean flag, ConfigState configState) {
      if (flag) {
         return true;
      } else {
         return amount2 > MinecraftHolder.getLocalPlayer().getHealth() + MinecraftHolder.getLocalPlayer().getAbsorptionAmount()
            ? false
            : amount / amount2 >= configState.getAmount7();
      }
   }
}
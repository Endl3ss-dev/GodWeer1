// deobf by pusia
package su.catlean.client.features.modules.attack.aura;

import net.minecraft.client.player.AbstractClientPlayer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import kotlin.NoWhenBranchMatchedException;
import kotlin.collections.CollectionsKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.zombie.ZombifiedPiglin;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.entity.projectile.hurtingprojectile.LargeFireball;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.catlean.client.features.modules.attack.Aura;
import su.catlean.client.features.modules.rage.ElytraTarget;
import su.catlean.client.features.modules.rage.MaceElytraTarget;
import su.catlean.interfaces.Resolvable;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.inventory.InventoryUtil;
import su.catlean.utils.player.PlayerNameUtil;
import su.catlean.utils.world.EntityPositionUtil;
import su.catlean.utils.world.WorldUtil;

public final class AuraTargetSelector {
   @NotNull
   public static final AuraTargetSelector INSTANCE = new AuraTargetSelector();
   @NotNull
   private static final Set items;

   private AuraTargetSelector() {
   }

   @NotNull
   public final Set getItems() {
      return items;
   }

   @Nullable
   public final Entity computeEntity(Entity entity) {
      Entity entity2 = this.getEntity();
      return entity != null && Aura.INSTANCE.getSort() != Aura.Sort.FOV && !(entity2 instanceof Projectile) && !this.checkGetTeamColor(entity) ? entity : entity2;
   }

   private final Entity getEntity() {
      List<Entity> arrayList = new ArrayList();

      for (Entity entitiesForRenderingItem : MinecraftHolder.getClientLevel().entitiesForRendering()) {
         if ((entitiesForRenderingItem instanceof ShulkerBullet || entitiesForRenderingItem instanceof LargeFireball)
            && ((Projectile)entitiesForRenderingItem).isAlive()
            && this.testEntity(entitiesForRenderingItem)
            && Aura.INSTANCE.getProjectiles()) {
            return entitiesForRenderingItem;
         }

         if (!this.checkGetTeamColor(entitiesForRenderingItem) && entitiesForRenderingItem instanceof LivingEntity) {
            arrayList.add(entitiesForRenderingItem);
         }
      }
      return switch (AuraTargetSelector.WhenMappings.values[Aura.INSTANCE.getSort().ordinal()]) {
         case 1 -> {
            Iterable<Entity> iterable = arrayList;
            Iterator<Entity> iterator = iterable.iterator();
            Entity object;
            if (!iterator.hasNext()) {
               object = null;
            } else {
               Entity object2 = iterator.next();
               if (!iterator.hasNext()) {
                  object = object2;
               } else {
                  LivingEntity livingEntity = (LivingEntity)object2;
                  double distanceToSqr = MinecraftHolder.getLocalPlayer().distanceToSqr(EntityPositionUtil.computeVec3((Entity)livingEntity));

                  do {
                     Entity object3 = iterator.next();
                     LivingEntity livingEntity2 = (LivingEntity)object3;
                     double distanceToSqr2 = MinecraftHolder.getLocalPlayer().distanceToSqr(EntityPositionUtil.computeVec3((Entity)livingEntity2));
                     if (Double.compare(distanceToSqr, distanceToSqr2) > 0) {
                        object2 = object3;
                        distanceToSqr = distanceToSqr2;
                     }
                  } while (iterator.hasNext());

                  object = object2;
               }
            }

            yield (Entity)object;
         }
         case 2 -> {
            Iterable<Entity> iterable2 = arrayList;
            Iterator<Entity> iterator2 = iterable2.iterator();
            Entity object4;
            if (!iterator2.hasNext()) {
               object4 = null;
            } else {
               Entity object5 = iterator2.next();
               if (!iterator2.hasNext()) {
                  object4 = object5;
               } else {
                  LivingEntity livingEntity3 = (LivingEntity)object5;
                  float value = INSTANCE.fetchX(livingEntity3);

                  do {
                     Entity object6 = iterator2.next();
                     LivingEntity livingEntity4 = (LivingEntity)object6;
                     float value2 = INSTANCE.fetchX(livingEntity4);
                     if (Float.compare(value, value2) > 0) {
                        object5 = object6;
                        value = value2;
                     }
                  } while (iterator2.hasNext());

                  object4 = object5;
               }
            }

            yield (Entity)object4;
         }
         case 3 -> {
            Iterable<Entity> iterable3 = arrayList;
            Iterator<Entity> iterator3 = iterable3.iterator();
            Entity object7;
            if (!iterator3.hasNext()) {
               object7 = null;
            } else {
               Entity object8 = iterator3.next();
               if (!iterator3.hasNext()) {
                  object7 = object8;
               } else {
                  LivingEntity livingEntity5 = (LivingEntity)object8;
                  float health = livingEntity5.getHealth() + livingEntity5.getAbsorptionAmount();

                  do {
                     Entity object9 = iterator3.next();
                     LivingEntity livingEntity6 = (LivingEntity)object9;
                     float health2 = livingEntity6.getHealth() + livingEntity6.getAbsorptionAmount();
                     if (Float.compare(health, health2) > 0) {
                        object8 = object9;
                        health = health2;
                     }
                  } while (iterator3.hasNext());

                  object7 = object8;
               }
            }

            yield (Entity)object7;
         }
         default -> throw new NoWhenBranchMatchedException();
      };
   }

   private final boolean checkGetTeamColor(Entity entity) {
      if (this.checkIsAlive(entity)) {
         return false;
      } else if (!(entity instanceof LivingEntity)) {
         return true;
      } else if (((LivingEntity)entity).isDeadOrDying() || !((LivingEntity)entity).isAlive()) {
         return true;
      } else if (entity instanceof ArmorStand) {
         return true;
      } else if (entity instanceof Cat) {
         return true;
      } else if (entity.tickCount < 0) {
         return true;
      } else if (!WorldUtil.INSTANCE.checkMainCamera(EntityPositionUtil.computeVec3(entity), Aura.INSTANCE.getFov() == 1 ? 180 : Aura.INSTANCE.getFov())) {
         return true;
      } else if (this.checkIsAggressive(entity)) {
         return true;
      } else {
         if (entity instanceof Player) {
            if (Intrinsics.areEqual(entity, MinecraftHolder.getLocalPlayer()) || PlayerNameUtil.checkGetGameProfile((Player)entity)) {
               return true;
            }

            if (((Player)entity).isCreative() && Aura.INSTANCE.getIgnoreCreative()) {
               return true;
            }

            if (((Player)entity).getArmorValue() == 0 && Aura.INSTANCE.getIgnoreNaked()) {
               return true;
            }

            if (((Player)entity).isInvisible() && Aura.INSTANCE.getIgnoreInvisible()) {
               return true;
            }

            if (((Player)entity).getTeamColor() == MinecraftHolder.getLocalPlayer().getTeamColor()
               && Aura.INSTANCE.getIgnoreTeam()
               && MinecraftHolder.getLocalPlayer().getTeamColor() != 16777215) {
               return true;
            }
         }

         return !this.testEntity(entity) || entity.hasCustomName() && Aura.INSTANCE.getIgnoreNamed();
      }
   }

   private final boolean checkIsAlive(Entity entity) {
      return (entity instanceof ShulkerBullet || entity instanceof LargeFireball)
         && ((Projectile)entity).isAlive()
         && WorldUtil.INSTANCE.computeAmount(EntityPositionUtil.computeVec3(entity)) < this.getAmount()
         && Aura.INSTANCE.getProjectiles();
   }

   private final boolean checkIsAggressive(Entity entity) {
      if (entity instanceof Monster && !Aura.INSTANCE.getHostiles()) {
         return true;
      } else {
         try {
            if (Aura.INSTANCE.getOnlyAngry()) {
               if (entity instanceof EnderMan && !((EnderMan)entity).isCreepy()) {
                  return true;
               }

               if (entity instanceof ZombifiedPiglin && !((ZombifiedPiglin)entity).isAggressive()) {
                  return true;
               }

               if (entity instanceof Wolf && !((Wolf)entity).isAggressive()) {
                  return true;
               }
            }
         } catch (Exception exception) {
         }

         if (entity instanceof Player && !Aura.INSTANCE.getPlayers()) {
            return true;
         } else if (entity instanceof Player && Aura.INSTANCE.getAntibot() && this.checkGetItem((Player)entity)) {
            return true;
         } else if (entity instanceof Villager && !Aura.INSTANCE.getVillagers()) {
            return true;
         } else {
            return entity instanceof Mob && !Aura.INSTANCE.getMobs() ? true : entity instanceof Animal && !Aura.INSTANCE.getAnimals();
         }
      }
   }

   private final boolean testEntity(Entity entity) {
      return WorldUtil.INSTANCE.computeAmount(Aura.INSTANCE.getRotationMode().getRotationStrategy().fetchEyePosition(entity)) <= this.getAmount();
   }

   private final float fetchX(LivingEntity livingEntity) {
      return (float)Math.abs(
         Mth.wrapDegrees(
               Math.toDegrees(
                     Math.atan2(livingEntity.getZ() - MinecraftHolder.getLocalPlayer().getZ(), livingEntity.getX() - MinecraftHolder.getLocalPlayer().getX())
                  )
                  - 90.0
            )
            - Mth.wrapDegrees(MinecraftHolder.getLocalPlayer().getYRot())
      );
   }

   private final float getAmount() {
      float attackRange = Aura.INSTANCE.getAttackRange() + 2.0F;
      if (MinecraftHolder.getLocalPlayer().isFallFlying() || MaceElytraTarget.INSTANCE.getEnabled()) {
         attackRange += 64.0F;
      }

      if (!Aura.INSTANCE.getRotationMode2().getRotationStrategy().isFlag4()) {
         attackRange = Aura.INSTANCE.getAttackRange();
      }

      return attackRange;
   }

   public final void applyPlayers(Function0 action) {
      Iterable<AbstractClientPlayer> players = MinecraftHolder.getClientLevel().players();
      var arrayList = new ArrayList();

      for (Object object : players) {
         if (object instanceof RemotePlayer) {
            arrayList.add(object);
         }
      }

      List list = arrayList;

      for (Object object2 : list) {
         RemotePlayer remotePlayer = (RemotePlayer)object2;
         INSTANCE.applyGetX((Player)remotePlayer);
      }

      action.invoke();

      for (Object object3 : list) {
         RemotePlayer remotePlayer2 = (RemotePlayer)object3;
         INSTANCE.applyGetSavedPosition((Player)remotePlayer2);
      }
   }

   private final void applyGetX(Player player) {
      if (!this.checkGetX(player)) {
         String text = "null cannot be cast to non-null type su.catlean.interfaces.Resolvable";
         ((Resolvable)player).savePosition(Vec3.ZERO);
         if (Intrinsics.areEqual(Aura.INSTANCE.getEntity(), player)) {
            ElytraTarget.INSTANCE.updateAABB(((Entity)player).getBoundingBox());
         }
      } else {
         String text2 = "null cannot be cast to non-null type su.catlean.interfaces.Resolvable";
         ((Resolvable)player).savePosition(EntityPositionUtil.computeVec3((Entity)player));
         if (((LivingEntity)player).isFallFlying() && ElytraTarget.INSTANCE.getEnabled() && ElytraTarget.INSTANCE.getAmount() > 0.0F) {
            float amount = ElytraTarget.INSTANCE.getAmount();
            double value = ((Entity)player).getX() + (((Entity)player).getX() - player.xo) * amount;
            double value2 = ((Entity)player).getY() + (((Entity)player).getY() - player.yo) * amount;
            double value3 = ((Entity)player).getZ() + (((Entity)player).getZ() - player.zo) * amount;
            player.setPos(value, value2, value3);
            if (Intrinsics.areEqual(Aura.INSTANCE.getEntity(), player)) {
               ElytraTarget.INSTANCE.updateAABB(new AABB(value - 0.3, value2 + 0.0, value3 - 0.3, value + 0.3, value2 + 0.6, value3 + 0.3));
            }
         }
      }
   }

   private final void applyGetSavedPosition(Player player) {
      String text = "null cannot be cast to non-null type su.catlean.interfaces.Resolvable";
      if (!Intrinsics.areEqual(((Resolvable)player).getSavedPosition(), Vec3.ZERO)) {
         player.setPos(((Resolvable)player).getSavedPosition());
         ((Resolvable)player).savePosition(Vec3.ZERO);
      }
   }

   private final boolean checkGetItem(Player player) {
      if (Intrinsics.areEqual(player, MinecraftHolder.getLocalPlayer())) {
         return false;
      } else {
         List<ItemStack> inventory = CollectionsKt.toList(InventoryUtil.fetchInventory(player));
         var value = inventory;
         boolean areEqual = false;
         boolean flag;
         if (value instanceof Collection && ((Collection)value).isEmpty()) {
            flag = true;
         } else {
            Iterator<ItemStack> iterator = value.iterator();

            while (true) {
               if (!iterator.hasNext()) {
                  flag = true;
                  break;
               }

               ItemStack object = iterator.next();
               ItemStack itemStack = (ItemStack)object;
               if (Intrinsics.areEqual(itemStack.getItem(), Items.AIR) || itemStack.isDamaged()) {
                  flag = false;
                  break;
               }
            }
         }

         boolean flag3 = flag;
         var value2 = inventory;
         boolean flag4 = false;
         if (value2 instanceof Collection && ((Collection)value2).isEmpty()) {
            flag = true;
         } else {
            Iterator<ItemStack> iterator2 = value2.iterator();

            while (true) {
               if (!iterator2.hasNext()) {
                  flag = true;
                  break;
               }

               ItemStack object2 = iterator2.next();
               ItemStack itemStack2 = (ItemStack)object2;
               if (!itemStack2.isEnchantable()) {
                  flag = false;
                  break;
               }
            }
         }

         boolean flag6 = flag;
         areEqual = Intrinsics.areEqual(player.getOffhandItem().getItem(), Items.AIR);
         var value3 = inventory;
         if (value3 instanceof Collection && ((Collection)value3).isEmpty()) {
            flag = false;
         } else {
            Iterator<ItemStack> iterator3 = value3.iterator();

            while (true) {
               if (!iterator3.hasNext()) {
                  flag = false;
                  break;
               }

               ItemStack object3 = iterator3.next();
               ItemStack itemStack3 = (ItemStack)object3;
               if (items.contains(itemStack3.getItem())) {
                  flag = true;
                  break;
               }
            }
         }

         flag4 = flag;
         boolean flag8 = !Intrinsics.areEqual(player.getMainHandItem().getItem(), Items.AIR);
         return flag3 && flag6 && areEqual && flag4 && flag8;
      }
   }

   public final boolean checkGetX(Player player) {
      return player.getX() != player.xo || player.getY() != player.yo || player.getZ() != player.zo;
   }

   static {
      Item[] items2 = new Item[]{
         Items.LEATHER_BOOTS,
         Items.LEATHER_LEGGINGS,
         Items.LEATHER_CHESTPLATE,
         Items.LEATHER_HELMET,
         Items.IRON_BOOTS,
         Items.IRON_LEGGINGS,
         Items.IRON_CHESTPLATE,
         Items.IRON_HELMET
      };
      items = SetsKt.setOf(items2);
   }

   public static final class WhenMappings {
      public static final int[] values;

      static {
         int[] values2 = new int[Aura.Sort.values().length];

         try {
            values2[Aura.Sort.Distance.ordinal()] = 1;
         } catch (NoSuchFieldError exception) {
         }

         try {
            values2[Aura.Sort.FOV.ordinal()] = 2;
         } catch (NoSuchFieldError exception2) {
         }

         try {
            values2[Aura.Sort.Health.ordinal()] = 3;
         } catch (NoSuchFieldError exception3) {
         }

         values = values2;
      }
   }
}
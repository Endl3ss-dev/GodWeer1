// deobf by pusia
package su.catlean.client.services;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.network.DisconnectEvent;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.event.events.world.EntityRemove;
import su.catlean.api.service.Service;
import su.catlean.client.features.modules.basic.AntiKnockBack;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;

public final class PlayerPredictionService implements Service {
   @NotNull
   public static final PlayerPredictionService INSTANCE = new PlayerPredictionService();
   public static final double amount = -0.0784;
   public static final double amount2 = 0.2837;
   public static final double amount3 = 0.08511;
   public static final double amount4 = 0.45392000000000005;
   public static final double amount5 = 0.056740000000000006;
   @NotNull
   private static final Map<Integer, PlayerPredictionService.Simulator> map = Collections.synchronizedMap(new HashMap());

   private PlayerPredictionService() {
   }

   @NotNull
   public final Map<Integer, PlayerPredictionService.Simulator> getMap() {
      return map;
   }

   @Flow(
      priority = 20
   )
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      map.clear();

      for (AbstractClientPlayer player : MinecraftHolder.getClientLevel().players()) {
         map.put(player.getId(), new PlayerPredictionService.Simulator((Player)player));
         PlayerPredictionService.Simulator map2 = (PlayerPredictionService.Simulator)map.get(player.getId());
         if (map2 != null) {
            map2.applyGetDeltaMovement();
         }
      }
   }
   @Flow
   private final void onEntityRemove(EntityRemove entityRemove) {
      if (entityRemove.getEntity() != null) {
         Map<Integer, PlayerPredictionService.Simulator> map2 = map;
         synchronized (map2) {
            PlayerPredictionService.Simulator remove = (PlayerPredictionService.Simulator)map.remove(entityRemove.getEntity().getId());
         }
      }
   }

   @Flow
   private final void onDisconnect(DisconnectEvent disconnectEvent) {
      map.clear();
   }

   @NotNull
   public final synchronized Vec3 fetchBoundingBox(Player player, int value) {
      return this.fetchBoundingBox2(player, value).getCenter().add(0.0, -player.getBoundingBox().getYsize() / 2.0, 0.0);
   }

   @NotNull
   public final synchronized AABB fetchBoundingBox2(Player player, int value) {
      if (map.containsKey(player.getId()) && value != 0) {
         List emptyList;
         label19: {
            PlayerPredictionService.Simulator map2 = (PlayerPredictionService.Simulator)map.get(player.getId());
            if (map2 != null) {
               ArrayList<Object> items = map2.getItems();
               if (items != null) {
                  emptyList = items;
                  break label19;
               }
            }

            emptyList = CollectionsKt.emptyList();
         }

         List list = emptyList;
         return list.isEmpty() ? player.getBoundingBox() : (AABB)list.get(Math.min(value, list.size() - 1));
      } else {
         return player.getBoundingBox();
      }
   }

   @NotNull
   public final Vec3 computeVec3(Player entity, int value) {
      Vec3 vec3 = AntiKnockBack.INSTANCE.computeVec3((Entity)entity);
      double value2 = entity.getX() - entity.xo;
      double value3 = entity.getY() - entity.yo;
      double value4 = entity.getZ() - entity.zo;

      for (int i = 0; i < value; i++) {
         float value5 = value2 > 0.0 ? 0.3F : -0.3F;
         float value6 = value4 > 0.0 ? 0.3F : -0.3F;
         if (!MinecraftHolder.getClientLevel().isEmptyBlock(BlockPos.containing((Position)vec3.add(value2 + value5, 0.25, value4 + value6)))
            || !MinecraftHolder.getClientLevel().isEmptyBlock(BlockPos.containing((Position)vec3.add(value2 + value5, 1.0, value4 + value6)))) {
            value2 = 0.0;
            value4 = 0.0;
         }

         vec3 = vec3.add(value2, 0.0, value4);
      }

      return vec3;
   }

   public static final class Simulator {
      @NotNull
      private final Player target;
      @NotNull
      private final ArrayList<Object> items;

      public Simulator(Player target) {
         this.target = target;
         this.items = new ArrayList();
      }

      @NotNull
      public final Player getTarget() {
         return this.target;
      }

      @NotNull
      public final synchronized ArrayList<Object> getItems() {
         return this.items;
      }

      public final void applyGetDeltaMovement() {
         ArrayList<Object> items2 = this.items;
         synchronized (items2) {
            this.items.clear();
            double value = 0.0;
            value = this.target.getX() - this.target.xo;
            double amount = 0.0;
            amount = this.target.getY() - this.target.yo;
            double value2 = 0.0;
            value2 = this.target.getZ() - this.target.zo;
            if (Intrinsics.areEqual(this.target, MinecraftHolder.getLocalPlayer())) {
               value = MinecraftHolder.getLocalPlayer().getDeltaMovement().x;
               amount = MinecraftHolder.getLocalPlayer().getDeltaMovement().y;
               value2 = MinecraftHolder.getLocalPlayer().getDeltaMovement().z;
            }

            double hypot = Math.hypot(value, value2);
            double value3 = Mth.clamp(hypot, this.target.isShiftKeyDown() ? 0.08511 : 0.2837, 0.45392000000000005);
            if (hypot != 0.0) {
               value = value / hypot * value3;
               value2 = value2 / hypot * value3;
            }

            if (MinecraftHolder.getClientLevel().noBlockCollision((Entity)this.target, this.target.getBoundingBox().expandTowards(0.0, -0.1, 0.0))
               && amount > 0.0) {
               amount = -0.0784;
            }

            AABB boundingBox = null;
            boundingBox = this.fetchBoundingBox(this.target);
            if (amount > 0.0) {
               amount = this.computeAmount(amount);
            }

            if (hypot < 0.056740000000000006) {
               value = 0.0;
               value2 = 0.0;
            }

            if (!(hypot < 0.056740000000000006) || !(Math.abs(amount) < 0.1)) {
               byte byteValue = 20;

               for (int i = 0; i < byteValue; i++) {
                  amount = this.checkGetBlockCollisions(this.target, boundingBox.move(0.0, -0.01, 0.0)) ? -0.0784 : this.computeAmount(amount);
                  boundingBox = this.computeAABB((AABB)boundingBox, value, amount, value2);
                  this.items.add(boundingBox);
               }

               Unit instance = Unit.INSTANCE;
               return;
            }

            this.items.addAll(Collections.nCopies(20, boundingBox));
         }
      }

      public final double computeAmount(double amount) {
         return (amount - 0.08) * 0.98;
      }

      public final boolean checkGetBlockCollisions(Player player, AABB box) {
         return MinecraftHolder.getClientLevel().getBlockCollisions((Entity)player, box).iterator().hasNext();
      }

      @NotNull
      public final AABB fetchBoundingBox(Player entity) {
         AABB boundingBox = entity.getBoundingBox();
         Vec3 vec3 = new Vec3(entity.xo, entity.yo, entity.zo);
         if (vec3.lengthSqr() == 0.0) {
            return entity.getBoundingBox();
         } else {
            double xsize = boundingBox.getXsize() / 2.0;
            return new AABB(-xsize + vec3.x, vec3.y, -xsize + vec3.z, xsize + vec3.x, boundingBox.getYsize() + vec3.y, xsize + vec3.z);
         }
      }

      private final List computeItems(Entity entity, List items, AABB aABB) {
         ImmutableList.Builder<Object> builderWithExpectedSize = ImmutableList.builderWithExpectedSize(items.size() + 1);
         if (!items.isEmpty()) {
            builderWithExpectedSize.addAll(items);
         }

         WorldBorder worldBorder = MinecraftHolder.getClientLevel().getWorldBorder();
         boolean isInsideCloseToBorder = worldBorder.isInsideCloseToBorder(entity, aABB);
         if (isInsideCloseToBorder) {
            builderWithExpectedSize.add(worldBorder.getCollisionShape());
         }

         return (List)builderWithExpectedSize.addAll(MinecraftHolder.getClientLevel().getBlockCollisions(entity, aABB)).build();
      }

      @NotNull
      public final AABB computeAABB(AABB box, double amount, double amount2, double amount3) {
         AABB expandTowards = box.expandTowards(amount, amount2, amount3);
         List items2 = this.computeItems((Entity)this.target, MinecraftHolder.getClientLevel().getEntityCollisions((Entity)this.target, expandTowards), expandTowards);
         AABB box2 = box;
         double amount4 = amount;
         double amount22 = 0.0;
         amount22 = amount2;
         double amount32 = 0.0;
         amount32 = amount3;
         if (items2.isEmpty()) {
            return box.move(amount, amount2, amount3);
         } else {
            if (amount2 != 0.0) {
               double collide = Shapes.collide(Axis.Y, box, items2, amount2);
               amount22 = collide;
               if (collide != 0.0) {
                  box2 = box.move(0.0, collide, 0.0);
               }
            }

            boolean flag = Math.abs(amount) < Math.abs(amount3);
            if (flag && amount3 != 0.0) {
               double collide2 = Shapes.collide(Axis.Z, box2, items2, amount3);
               amount32 = collide2;
               if (collide2 != 0.0) {
                  box2 = box2.move(0.0, 0.0, collide2);
               }
            }

            if (amount != 0.0) {
               amount4 = Shapes.collide(Axis.X, box2, items2, amount);
               if (!flag && amount4 != 0.0) {
                  box2 = box2.move(amount4, 0.0, 0.0);
               }
            }

            if (!flag && amount32 != 0.0) {
               amount32 = Shapes.collide(Axis.Z, box2, items2, amount32);
            }

            return box.move(amount4, amount22, amount32);
         }
      }
   }
}
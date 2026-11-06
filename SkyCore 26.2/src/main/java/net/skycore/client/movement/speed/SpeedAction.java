package net.skycore.client.movement.speed;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.boat.Boat;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.skycore.client.util.MovementHelper;
import net.skycore.client.util.StartFallFlyingHelper;

/**
 * Общие примитивы, дословно повторяющие хелперы клиентов-источников
 * (MoveUtil.forward / setSpeed / MovingUtil.forward / setVelocity / countCollisions).
 */
public final class SpeedAction {
   private static final Map<Entity, Vec3> PREVIOUS_POSITIONS = new HashMap<>();

   public static LocalPlayer player() {
      return Minecraft.getInstance().player;
   }

   public static boolean isMoving() {
      return MovementHelper.isMoving();
   }

   /** Vec2 источника: y = forward, x = strafe. */
   public static double[] forward(double speed) {
      LocalPlayer player = player();
      if (player == null || player.input == null) {
         return new double[]{0.0, 0.0};
      }

      Vec2 move = player.input.getMoveVector();
      return forward(move.y, move.x, player.getYRot(), speed);
   }

   /** Дословная копия forward() из Nuclear / Phobia / Private / AlphaDLC / SkyCore. */
   public static double[] forward(float forward, float strafe, float yaw, double speed) {
      if (forward != 0.0F) {
         if (strafe > 0.0F) {
            yaw += forward > 0.0F ? -45.0F : 45.0F;
         } else if (strafe < 0.0F) {
            yaw += forward > 0.0F ? 45.0F : -45.0F;
         }

         strafe = 0.0F;
         if (forward > 0.0F) {
            forward = 1.0F;
         } else if (forward < 0.0F) {
            forward = -1.0F;
         }
      }

      double sin = Math.sin(Math.toRadians(yaw + 90.0F));
      double cos = Math.cos(Math.toRadians(yaw + 90.0F));
      double x = (double)forward * speed * cos + (double)strafe * speed * sin;
      double z = (double)forward * speed * sin - (double)strafe * speed * cos;
      return new double[]{x, z};
   }

   /** MoveUtil.getDirection() источника: радианы «курс+ввод». */
   public static double direction(LocalPlayer player) {
      return Math.toRadians(directionDegrees(player));
   }

   /** MoveUtil.getDirection(false) источника: тот же угол, но в градусах. */
   public static double directionDegrees(LocalPlayer player) {
      if (player == null || player.input == null) {
         return 0.0;
      }

      Vec2 move = player.input.getMoveVector();
      float rotationYaw = player.getYRot();
      if (move.y < 0.0F) {
         rotationYaw += 180.0F;
      }

      float forward = 1.0F;
      if (move.y < 0.0F) {
         forward = -0.5F;
      } else if (move.y > 0.0F) {
         forward = 0.5F;
      }

      if (move.x > 0.0F) {
         rotationYaw -= 90.0F * forward;
      }

      if (move.x < 0.0F) {
         rotationYaw += 90.0F * forward;
      }

      return rotationYaw;
   }

   /** MoveUtil.setSpeed / MovingUtil.setVelocity: горизонталь поверх текущего Y. */
   public static void setSpeed(double speed) {
      LocalPlayer player = player();
      if (player != null) {
         double[] motion = forward(speed);
         Vec3 velocity = player.getDeltaMovement();
         player.setDeltaMovement(motion[0], velocity.y, motion[1]);
      }
   }

   /** addVelocity(x, 0, z) источника. */
   public static void addVelocity(double[] motion) {
      LocalPlayer player = player();
      if (player != null) {
         player.push(motion[0], 0.0, motion[1]);
      }
   }

   public static void addVelocity(double x, double y, double z) {
      LocalPlayer player = player();
      if (player != null) {
         player.push(x, y, z);
      }
   }

   /** Ванильный jump() источника. */
   public static void jump() {
      LocalPlayer player = player();
      if (player != null) {
         player.jumpFromGround();
      }
   }

   public static void startFallFlying() {
      StartFallFlyingHelper.startFallFlying();
   }

   public static void stopFallFlying() {
      LocalPlayer player = player();
      if (player != null) {
         player.stopFallFlying();
      }
   }

   public static AABB box(double expand) {
      LocalPlayer player = player();
      return player == null ? new AABB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0) : player.getBoundingBox().inflate(expand);
   }

   public static AABB box(double x, double y, double z) {
      LocalPlayer player = player();
      return player == null ? new AABB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0) : player.getBoundingBox().inflate(x, y, z);
   }

   /**
    * Счётчик коллизий Nuclear / Phobia / Rockstar / Sadnes:
    * LivingEntity или лодка, кроме армстандов (или включая их — по флагу).
    */
   public static int countCollisions(double expand, boolean includeArmorStands) {
      LocalPlayer player = player();
      if (player == null || player.level() == null) {
         return 0;
      }

      AABB area = player.getBoundingBox().inflate(expand);
      List<Entity> entities = player.level().getEntities(player, area);
      int collisions = 0;

      for (Entity entity : entities) {
         if (isValidEntity(player, entity, includeArmorStands)) {
            collisions++;
         }
      }

      return collisions;
   }

   public static int countPlayerCollisions(double expand) {
      LocalPlayer player = player();
      if (player == null || player.level() == null) {
         return 0;
      }

      AABB area = player.getBoundingBox().inflate(expand);
      List<Entity> entities = player.level().getEntities(player, area);
      int collisions = 0;

      for (Entity entity : entities) {
         if (entity instanceof Player && entity != player) {
            collisions++;
         }
      }

      return collisions;
   }

   public static boolean isValidEntity(LocalPlayer player, Entity entity, boolean includeArmorStands) {
      if (entity == null || entity == player) {
         return false;
      }

      if (entity instanceof ArmorStand && !includeArmorStands) {
         return false;
      }

      return entity instanceof LivingEntity || entity instanceof Boat;
   }

   /** EntityUtility.isPlayerMoving / MoveUtil.isMoving. */
   public static boolean hasMovementInput() {
      LocalPlayer player = player();
      if (player == null || player.input == null) {
         return false;
      }

      Vec2 move = player.input.getMoveVector();
      return move.x != 0.0F || move.y != 0.0F;
   }

   /** Entity#getBoundingBox().maxY - minY. */
   public static double boundingBoxHeight() {
      LocalPlayer player = player();
      return player == null ? 0.0 : player.getBoundingBox().maxY - player.getBoundingBox().minY;
   }

   /** AlphaDLC / SkyCore getEntitySpeed: горизонтальный BPS цели. */
   public static double entitySpeed(Entity entity) {
      Vec3 current = entity.position();
      Vec3 previous = PREVIOUS_POSITIONS.getOrDefault(entity, current);
      double dx = current.x - previous.x;
      double dz = current.z - previous.z;
      double speed = Math.sqrt(dx * dx + dz * dz) * 20.0;
      PREVIOUS_POSITIONS.put(entity, current);
      return speed;
   }

   public static void clearEntitySpeeds() {
      PREVIOUS_POSITIONS.clear();
   }

   private SpeedAction() {
   }
}

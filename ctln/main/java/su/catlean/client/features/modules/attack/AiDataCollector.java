// deobf by pusia
package su.catlean.client.features.modules.attack;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentLinkedQueue;
import kotlin.Unit;
import kotlin.io.CloseableKt;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.Rotation;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.chat.ChatUtil;
import su.catlean.utils.player.PlayerTargetUtil;
import su.catlean.utils.world.WorldUtil;

public final class AiDataCollector extends Module {
   @NotNull
   public static final AiDataCollector INSTANCE = new AiDataCollector();
   @NotNull
   private static final ConcurrentLinkedQueue<String> concurrentLinkedQueue = new ConcurrentLinkedQueue();
   private static float amount;
   private static float amount2;
   private static int value;
   private static File file;

   private AiDataCollector() {
      super("ai-data-collector", SubCategories.getSubCategory2(), null, 4, null);
   }

   public final int getValue() {
      return value;
   }

   public final void setValue(int value) {
      AiDataCollector.value = value;
   }

   @Override
   public void update2() {
      String var10000 = System.getProperty("user.home");
      String[] var8 = new String[]{".ctl", "ai", "data.csv"};
      String var7 = Paths.get(var10000, var8).toString();
      file = new File(var7);
      File var9 = file;
      if (file == null) {
         Intrinsics.throwUninitializedPropertyAccessException("outputFile");
         var9 = null;
      }

      if (!var9.exists()) {
         this.updateText(
            "delta_yaw,delta_pitch,desired_yaw1,desired_pitch1,desired_yaw2,desired_pitch2,desired_yaw3,desired_pitch3,delta_x,delta_y,delta_z,fallDistance,aimed,distanceFeet,distanceEyes,target_vel_x,target_vel_y,target_vel_z,my_vel_x,my_vel_y,my_vel_z,cd"
         );
      }
   }

   @Flow
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      Player var10000 = PlayerTargetUtil.defaultImpl(PlayerTargetUtil.INSTANCE, 6.0F, AutoCrystal.TargetSort.DISTANCE, false, false, 12, null);
      if (var10000 != null) {
         Player var19 = var10000;
         float var20 = MinecraftHolder.getMinecraft().hitResult instanceof EntityHitResult
               && Intrinsics.areEqual(((EntityHitResult)MinecraftHolder.getMinecraft().hitResult).getEntity(), var19)
            ? 1.0F
            : 0.0F;
         Rotation var21 = Rotation.vectorUtil.computeRotation(var19.getEyePosition());
         float var22 = (var21.getAmount() - MinecraftHolder.getLocalPlayer().getYRot()) / 180.0F;
         float var23 = (var21.getAmount2() - MinecraftHolder.getLocalPlayer().getXRot()) / 90.0F;
         Rotation var24 = Rotation.vectorUtil.computeRotation(var19.getBoundingBox().getCenter());
         float var25 = (var24.getAmount() - MinecraftHolder.getLocalPlayer().getYRot()) / 180.0F;
         float var26 = (var24.getAmount2() - MinecraftHolder.getLocalPlayer().getXRot()) / 90.0F;
         Rotation var27 = Rotation.vectorUtil.computeRotation(this.computeVec3((Entity)var19));
         float var28 = (var27.getAmount() - MinecraftHolder.getLocalPlayer().getYRot()) / 180.0F;
         float var29 = (var27.getAmount2() - MinecraftHolder.getLocalPlayer().getXRot()) / 90.0F;
         double var30 = MinecraftHolder.getLocalPlayer().getX() - var19.getX();
         double var32 = MinecraftHolder.getLocalPlayer().getY() - var19.getY();
         double var34 = MinecraftHolder.getLocalPlayer().getZ() - var19.getZ();
         float var36 = (MinecraftHolder.getLocalPlayer().getYRot() - amount) / 180.0F;
         float var37 = (MinecraftHolder.getLocalPlayer().getXRot() - amount2) / 90.0F;
         amount = MinecraftHolder.getLocalPlayer().getYRot();
         amount2 = MinecraftHolder.getLocalPlayer().getXRot();
         double var38 = MinecraftHolder.getLocalPlayer().getEyePosition().distanceTo(var19.getEyePosition()) / 6.0F;
         double var40 = MinecraftHolder.getLocalPlayer().getEyePosition().distanceTo(this.computeVec3((Entity)var19)) / 6.0F;
         Vec3 var42 = new Vec3(var19.getX() - var19.xo, var19.getY() - var19.yo, var19.getZ() - var19.zo);
         Vec3 var43 = MinecraftHolder.getLocalPlayer().getDeltaMovement();
         String var44 = var36
            + ","
            + var37
            + ","
            + var22
            + ","
            + var23
            + ","
            + var25
            + ","
            + var26
            + ","
            + var28
            + ","
            + var29
            + ","
            + var30
            + ","
            + var32
            + ","
            + var34
            + ","
            + WorldUtil.INSTANCE.getAmount3()
            + ","
            + var20
            + ","
            + var40
            + ","
            + var38
            + ","
            + var42.x
            + ","
            + var42.y
            + ","
            + var42.z
            + ","
            + var43.x
            + ","
            + var43.y
            + ","
            + var43.z
            + ","
            + MinecraftHolder.getLocalPlayer().getAttackStrengthScale(0.5F);
         this.updateText(var44);
         int var45 = value++;
         if (MinecraftHolder.getLocalPlayer().tickCount % 100 == 0) {
            ChatUtil.defaultImpl(this, "saved " + value + " lines", false, 2, null);
         }
      }
   }

   private final void updateText(String text) {
      concurrentLinkedQueue.add(text);
      if (concurrentLinkedQueue.size() >= 100) {
         this.applyAppend();
      }
   }

   private final void applyAppend() {
      try {
         File var10002 = file;
         if (file == null) {
            Intrinsics.throwUninitializedPropertyAccessException("outputFile");
            var10002 = null;
         }

         FileWriter var10000 = new FileWriter(var10002, true);
         var var2 = var10000;
         Throwable var3 = null;

         try {
            FileWriter var4 = var2;

            while (!concurrentLinkedQueue.isEmpty()) {
               var4.append((CharSequence)concurrentLinkedQueue.poll());
               var4.append("\n");
            }

            Unit var12 = Unit.INSTANCE;
         } catch (Throwable var9) {
            var3 = var9;
            throw var9;
         } finally {
            CloseableKt.closeFinally(var2, var3);
         }
      } catch (IOException var11) {
         System.out.println("Ошибка записи в файл: " + var11.getMessage());
      }
   }
}
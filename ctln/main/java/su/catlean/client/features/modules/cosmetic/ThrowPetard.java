// deobf by pusia
package su.catlean.client.features.modules.cosmetic;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import java.awt.Color;
import java.util.List;
import net.minecraft.client.Camera;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.event.events.render.Render3DEvent;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.render.ParticleRenderer;
import su.catlean.client.render.Pipelines;
import su.catlean.client.services.CrystalPredictor;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.render.BillboardRenderer;
import su.catlean.utils.render.MeshBuilder;
import su.catlean.utils.render.Render2D;
import su.catlean.utils.world.WorldUtil;

public final class ThrowPetard extends Module {
   @NotNull
   public static final ThrowPetard INSTANCE = new ThrowPetard();
   private static float amount;
   private static float amount2;
   private static float amount3;
   private static float amount4;
   private static float amount5;
   private static float amount6;
   private static float amount7;
   private static float amount8;
   private static float amount9;
   private static int value;

   private ThrowPetard() {
      super("throw-petard", SubCategories.getSubCategory6(), null, 4, null);
   }

   @Override
   public void update2() {
      if (MinecraftHolder.getMinecraft().player != null) {
         value = 0;
         MinecraftHolder.getClientLevel().playLocalSound(amount, amount2 + 0.5, amount3, SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 0.2F, 1.0F, false);
         amount = (float)MinecraftHolder.getLocalPlayer().getEyePosition().x;
         amount2 = (float)MinecraftHolder.getLocalPlayer().getEyePosition().y;
         amount3 = (float)MinecraftHolder.getLocalPlayer().getEyePosition().z;
         amount7 = (float)MinecraftHolder.getLocalPlayer().getLookAngle().x;
         amount8 = (float)MinecraftHolder.getLocalPlayer().getLookAngle().y + 0.42F + (float)MinecraftHolder.getLocalPlayer().getDeltaMovement().y;
         amount9 = (float)MinecraftHolder.getLocalPlayer().getLookAngle().z;
      }
   }

   @Flow
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      float var35 = 0.0F;
      amount4 = amount;
      amount5 = amount2;
      amount6 = amount3;
      amount = amount + amount7;
      amount2 = amount2 + amount8;
      amount3 = amount3 + amount9;
      amount7 *= 0.98F;
      amount8 *= 0.98F;
      amount9 *= 0.98F;
      CrystalPredictor instance = CrystalPredictor.INSTANCE;
      Vec3 vec3 = new Vec3(amount, amount2, amount3);
      Vec3 vec32 = new Vec3(amount4, amount5, amount6);
      Object object = null;
      byte byteValue = 28;
      Object object2 = null;
      Object object3 = null;
      boolean flag = false;
      Vec3 vec33 = vec32;
      BlockHitResult defaultImpl = CrystalPredictor.defaultImpl(instance, vec3, vec33, flag, (List)object3, (List)object2, byteValue, object);
      boolean equals = defaultImpl != null ? defaultImpl.equals(Type.MISS) : true;
      if (!equals) {
         label49: {
            amount = amount4;
            if (defaultImpl != null) {
               BlockPos blockPos = defaultImpl.getBlockPos();
               if (blockPos != null) {
                  BlockPos above = blockPos.above();
                  if (above != null) {
                     var35 = above.getY();
                     break label49;
                  }
               }
            }

            var35 = amount5;
         }

         amount2 = var35;
         amount3 = amount6;
         amount7 *= 0.81F;
         amount9 *= 0.81F;
      } else {
         amount8 -= 0.08F;
      }

      int value2 = value++;
      byte byteValue2 = 3;

      for (int i = 0; i < byteValue2; i++) {
         BillboardRenderer.INSTANCE
            .testParticleRenderer(
               new ParticleRenderer(
                  HitFx.Particle.STAR.getParticleType(),
                  new Vec3(amount, amount2 + 0.5, amount3),
                  (float)(Math.random() * 50.0F),
                  0.2F,
                  5,
                  new Vec3(Math.random() / 4.0F - 0.125F, Math.random() / 2.0F, Math.random() / 4.0F - 0.125F),
                  null,
                  0.0F,
                  0.0F,
                  false,
                  960,
                  null
               )
            );
      }

      double degrees = (
               Math.toDegrees(Math.atan2(amount3 - MinecraftHolder.getLocalPlayer().getZ(), amount - MinecraftHolder.getLocalPlayer().getX()))
                  - 90
                  - WorldUtil.INSTANCE.getAmount4()
            )
            % 360
         + 180.0F;
      if (value > 100) {
         this.applyUnplug();
         MinecraftHolder.getClientLevel()
            .playLocalSound(amount, amount2 + 0.5, amount3, (SoundEvent)SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 0.2F, 1.0F, false);
         byte byteValue3 = 100;

         for (int i2 = 0; i2 < byteValue3; i2++) {
            BillboardRenderer.INSTANCE
               .testParticleRenderer(
                  new ParticleRenderer(
                     HitFx.Particle.STAR.getParticleType(),
                     new Vec3(amount, amount2 + 0.5, amount3),
                     (float)(Math.random() * 50.0F),
                     0.5F,
                     20,
                     new Vec3((Math.random() - 0.5) * 1.5F, Math.random(), (Math.random() - 0.5) * 1.5F),
                     null,
                     0.0F,
                     0.0F,
                     false,
                     960,
                     null
                  )
               );
            BillboardRenderer.INSTANCE
               .testParticleRenderer(
                  new ParticleRenderer(
                     HitFx.Particle.FIRE_FLY.getParticleType(),
                     new Vec3(amount, amount2 + 0.5, amount3),
                     (float)(Math.random() * 50.0F),
                     0.3F,
                     20,
                     new Vec3((Math.random() - 0.5) * 1.5F, Math.random(), (Math.random() - 0.5) * 1.5F),
                     null,
                     0.0F,
                     0.0F,
                     false,
                     960,
                     null
                  )
               );
         }
      }
   }

   @Flow
   private final void onRender3D(Render3DEvent render3DEvent) {
      Camera mainCamera = MinecraftHolder.getMinecraft().gameRenderer.mainCamera();
      float amount10 = Render2D.INSTANCE.computeAmount(amount4, amount, Render2D.INSTANCE.getAmount2());
      float amount11 = Render2D.INSTANCE.computeAmount(amount5, amount2, Render2D.INSTANCE.getAmount2());
      float amount12 = Render2D.INSTANCE.computeAmount(amount6, amount3, Render2D.INSTANCE.getAmount2());
      render3DEvent.getStack().pushPose();
      render3DEvent.getStack().translate(amount10 - mainCamera.position().x, amount11 - mainCamera.position().y, amount12 - mainCamera.position().z);
      this.render(render3DEvent.getStack(), 0.0F, 0.0F, 0.0F);
      render3DEvent.getStack().popPose();
   }

   public final void render(PoseStack matrices, float amount, float amount2, float amount3) {
      Matrix4f pose = matrices.last().pose();
      MeshBuilder meshBuilder = new MeshBuilder(DefaultVertexFormat.POSITION_COLOR, 48, false, 4, null);
      float value2 = 0.12F;
      float value3 = 0.5F;
      float value4 = 0.2F;
      byte byteValue = 16;

      for (int i = 0; i < byteValue; i++) {
         float value5 = (float)((Math.PI * 2) * i / byteValue);
         float value6 = (float)((Math.PI * 2) * (i + 1) / byteValue);
         float amount10 = amount + (float)Math.cos(value5) * value2;
         float amount32 = amount3 + (float)Math.sin(value5) * value2;
         float amount11 = amount + (float)Math.cos(value6) * value2;
         float amount33 = amount3 + (float)Math.sin(value6) * value2;
         Color darK_GRAY = Color.DARK_GRAY;
         meshBuilder.computeMeshBuilder(pose, amount10, amount2, amount32).fetchAlpha(darK_GRAY);
         meshBuilder.computeMeshBuilder(pose, amount10, amount2 + value3, amount32).fetchAlpha(darK_GRAY);
         meshBuilder.computeMeshBuilder(pose, amount11, amount2 + value3, amount33).fetchAlpha(darK_GRAY);
         meshBuilder.computeMeshBuilder(pose, amount11, amount2, amount33).fetchAlpha(darK_GRAY);
      }

      float amount22 = amount2 + value3 / 2 - value4 / 2;

      for (int i2 = 0; i2 < byteValue; i2++) {
         float value7 = (float)((Math.PI * 2) * i2 / byteValue);
         float value8 = (float)((Math.PI * 2) * (i2 + 1) / byteValue);
         float amount12 = amount + (float)Math.cos(value7) * (value2 + 0.01F);
         float amount34 = amount3 + (float)Math.sin(value7) * (value2 + 0.01F);
         float amount13 = amount + (float)Math.cos(value8) * (value2 + 0.01F);
         float amount35 = amount3 + (float)Math.sin(value8) * (value2 + 0.01F);
         meshBuilder.computeMeshBuilder(pose, amount12, amount22, amount34).fetchAlpha(Color.WHITE);
         meshBuilder.computeMeshBuilder(pose, amount12, amount22 + value4, amount34).fetchAlpha(Color.WHITE);
         meshBuilder.computeMeshBuilder(pose, amount13, amount22 + value4, amount35).fetchAlpha(Color.WHITE);
         meshBuilder.computeMeshBuilder(pose, amount13, amount22, amount35).fetchAlpha(Color.WHITE);
      }

      float value9 = 0.06F;
      float value10 = 0.03F;
      meshBuilder.computeMeshBuilder(pose, amount - value10, amount2 + value3 + value9, amount3 - value10).fetchAlpha(Color.BLACK);
      meshBuilder.computeMeshBuilder(pose, amount - value10, amount2 + value3, amount3 - value10).fetchAlpha(Color.BLACK);
      meshBuilder.computeMeshBuilder(pose, amount + value10, amount2 + value3, amount3 + value10).fetchAlpha(Color.BLACK);
      meshBuilder.computeMeshBuilder(pose, amount + value10, amount2 + value3 + value9, amount3 + value10).fetchAlpha(Color.BLACK);
      MeshBuilder.defaultImpl(meshBuilder, Pipelines.INSTANCE.getRenderPipeline22(), null, null, matrices.last().pose(), null, null, null, 118, null);
   }
}
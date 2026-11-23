// deobf by pusia
package su.catlean.client.features.modules.cosmetic;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import kotlin.TuplesKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.client.Camera;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownSplashPotion;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaternionfc;
import su.catlean.api.enums.ParticleType;
import su.catlean.api.event.events.client.TickEvent;
import su.catlean.api.event.events.network.ReceivePacket;
import su.catlean.api.event.events.render.Render3DEvent;
import su.catlean.api.event.events.world.EntityRemove;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.render.ParticleRenderer;
import su.catlean.client.render.Pipelines;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.math.MathUtil;
import su.catlean.utils.math.Vec3iUtil;
import su.catlean.utils.render.AtlasTexture;
import su.catlean.utils.render.BillboardRenderer;
import su.catlean.utils.render.MeshBuilder;
import su.catlean.utils.render.PoseUtil;
import su.catlean.utils.render.Render2D;

public final class BuffEffect extends Module {
   @NotNull
   public static final BuffEffect INSTANCE = new BuffEffect();
   @NotNull
   private static final List<BuffEffect.Effect> items = new ArrayList();
   private static int value = -1;

   private BuffEffect() {
      super("buff-effect", SubCategories.getSubCategory6(), null, 4, null);
   }

   @Flow
   private final void onRender3D(Render3DEvent render3DEvent) {
      Camera mainCamera = MinecraftHolder.getMinecraft().gameRenderer.mainCamera();
      render3DEvent.getStack().pushPose();
      PoseUtil.render(render3DEvent.getStack(), (Quaternionfc)Axis.YP.rotationDegrees(-mainCamera.yRot() + 180.0F));
      PoseUtil.render(render3DEvent.getStack(), (Quaternionfc)Axis.XP.rotationDegrees(-mainCamera.xRot()));
      MeshBuilder meshBuilder = new MeshBuilder(DefaultVertexFormat.POSITION_TEX_COLOR, items.size() * 2, false, 4, null);

      for (BuffEffect.Effect item : (Iterable<su.catlean.client.features.modules.cosmetic.BuffEffect.Effect>)items) {
         float value2 = 0.0F;
         float value3 = 90.0F;
         float amount = Render2D.INSTANCE.computeAmount((float)Math.max(1, item.getValue2() - 1), (float)item.getValue2());
         float value4 = item.getValue2() < 3 ? amount / 3.0F : (item.getValue2() > 25 ? 1.0F - (amount - 25.0F) / 5.0F : 1.0F);
         float value5 = 8.0F * value4;
         Color color = Render2D.INSTANCE.computeColor(new Color(item.getValue()), value4);
         Color color2 = Render2D.INSTANCE.computeColor(new Color(item.getValue()), value4 * 0.3F);
         PoseStack poseStack = new PoseStack();
         poseStack.pushPose();
         PoseUtil.render(poseStack, (Quaternionfc)Axis.XP.rotationDegrees(mainCamera.xRot()));
         PoseUtil.render(poseStack, (Quaternionfc)Axis.YP.rotationDegrees(mainCamera.yRot() + 180.0F));
         poseStack.translate(item.getPos().x - mainCamera.position().x, item.getPos().y + 0.05F - mainCamera.position().y, item.getPos().z - mainCamera.position().z);
         PoseUtil.render(poseStack, (Quaternionfc)Axis.YP.rotationDegrees(value2));
         PoseUtil.render(poseStack, (Quaternionfc)Axis.XP.rotationDegrees(-value3));
         poseStack.translate(-4.0, -4.0, 0.0);
         Matrix4f pose = poseStack.last().pose();
         ParticleType crack = ParticleType.CRACK;
         meshBuilder.computeMeshBuilder(pose, 0.0F, 8.0F, 0.0F).computeMeshBuilder(crack.getAmount3(), crack.getAmount2()).fetchAlpha(color2);
         meshBuilder.computeMeshBuilder(pose, 8.0F, 8.0F, 0.0F).computeMeshBuilder(crack.getAmount(), crack.getAmount2()).fetchAlpha(color2);
         meshBuilder.computeMeshBuilder(pose, 8.0F, 0.0F, 0.0F).computeMeshBuilder(crack.getAmount(), crack.getAmount4()).fetchAlpha(color2);
         meshBuilder.computeMeshBuilder(pose, 0.0F, 0.0F, 0.0F).computeMeshBuilder(crack.getAmount3(), crack.getAmount4()).fetchAlpha(color2);
         poseStack.translate(4.0, 4.0, 0.0);
         ParticleType circle = ParticleType.CIRCLE;
         poseStack.translate(-value5 / 2.0, -value5 / 2.0, 0.0);
         meshBuilder.computeMeshBuilder(pose, 0.0F, value5, 0.0F).computeMeshBuilder(circle.getAmount3(), circle.getAmount2()).fetchAlpha(color);
         meshBuilder.computeMeshBuilder(pose, value5, value5, 0.0F).computeMeshBuilder(circle.getAmount(), circle.getAmount2()).fetchAlpha(color);
         meshBuilder.computeMeshBuilder(pose, value5, 0.0F, 0.0F).computeMeshBuilder(circle.getAmount(), circle.getAmount4()).fetchAlpha(color);
         meshBuilder.computeMeshBuilder(pose, 0.0F, 0.0F, 0.0F).computeMeshBuilder(circle.getAmount3(), circle.getAmount4()).fetchAlpha(color);
         poseStack.popPose();
      }

      MeshBuilder.defaultImpl(
         meshBuilder,
         Pipelines.INSTANCE.getRenderPipeline19(),
         null,
         null,
         render3DEvent.getStack().last().pose(),
         null,
         MapsKt.mapOf(TuplesKt.to("Sampler0", MinecraftHolder.getMinecraft().getTextureManager().getTexture(AtlasTexture.getCatlean()).getTextureView())),
         null,
         86,
         null
      );
      render3DEvent.getStack().popPose();
   }

   @Flow
   private final void onTick(TickEvent tickEvent) {
      items.removeIf(
         arg -> {
            if (((BuffEffect.Effect)arg).getValue2() == 3) {
               byte byteValue = 25;

               for (int i = 0; i < byteValue; i++) {
                  BillboardRenderer.INSTANCE
                     .testParticleRenderer(
                        new ParticleRenderer(
                           ParticleType.FIREFLY,
                           ((BuffEffect.Effect)arg).getPos()
                              .add(MathUtil.defaultImpl(-3.0, 3.0, false, 4, null), 0.0, MathUtil.defaultImpl(-3.0, 3.0, false, 4, null)),
                           0.0F,
                           0.25F,
                           12,
                           new Vec3(Math.random() / 2.0F - 0.25F, Math.random() / 4.0F, Math.random() / 2.0F - 0.25F),
                           new Color(((BuffEffect.Effect)arg).getValue()),
                           0.0F,
                           0.0F,
                           false,
                           896,
                           null
                        )
                     );
               }

               byteValue = 5;

               for (int i2 = 0; i2 < byteValue; i2++) {
                  BillboardRenderer.INSTANCE
                     .testParticleRenderer(
                        new ParticleRenderer(
                           ParticleType.FIREFLY,
                           ((BuffEffect.Effect)arg).getPos().add(0.0, -0.3, 0.0),
                           0.0F,
                           6.0F,
                           4,
                           new Vec3(0.0, i2 * 0.15, 0.0),
                           Render2D.INSTANCE.computeColor(new Color(((BuffEffect.Effect)arg).getValue()), 0.5F),
                           0.0F,
                           0.0F,
                           false,
                           896,
                           null
                        )
                     );
               }
            }

            int value2 = ((BuffEffect.Effect)arg).getValue2();
            ((BuffEffect.Effect)arg).setValue2(value2 + 1);
            return value2 > 30;
         }
      );
   }

   @Flow
   private final void onReceivePacket(ReceivePacket receivePacket) {
      if (receivePacket.getPacket() instanceof ClientboundLevelEventPacket
         && (
            ((ClientboundLevelEventPacket)receivePacket.getPacket()).getType() == 2007
               || ((ClientboundLevelEventPacket)receivePacket.getPacket()).getType() == 2002
         )) {
         int data = ((ClientboundLevelEventPacket)receivePacket.getPacket()).getData();
         float value2 = (data >> 16 & 0xFF) / 255.0F;
         float value3 = (data >> 8 & 0xFF) / 255.0F;
         float value4 = (data >> 0 & 0xFF) / 255.0F;
         Color color = new Color(value2, value3, value4);
         if (color.getRGB() == -13083194) {
            return;
         }

         value = color.getRGB();
      }
   }

   @Flow
   private final void onEntityRemove(EntityRemove entityRemove) {
      if (entityRemove.getEntity() instanceof ThrownSplashPotion) {
         var entity = (ThrownSplashPotion)entityRemove.getEntity();
         BlockPos containing = BlockPos.containing((Position)this.computeVec3((Entity)entity));
         items.add(new BuffEffect.Effect(Vec3iUtil.computeVec3(containing).add(0.0, -0.5, 0.0), value, 0, 4, null));
      }
   }

   public static final class Effect {
      @NotNull
      private final Vec3 pos;
      private int value;
      private int value2;

      public Effect(Vec3 pos, int value, int value2) {
         this.pos = pos;
         this.value = value;
         this.value2 = value2;
      }

      public Effect(Vec3 vec3, int value, int value2, int value3, DefaultConstructorMarker defaultConstructorMarker) {
         if ((value3 & 2) != 0) {
            value = -1;
         }

         if ((value3 & 4) != 0) {
            value2 = 0;
         }

         this(vec3, value, value2);
      }

      @NotNull
      public final Vec3 getPos() {
         return this.pos;
      }

      public final int getValue() {
         return this.value;
      }

      public final void setValue(int value) {
         this.value = value;
      }

      public final int getValue2() {
         return this.value2;
      }

      public final void setValue2(int value) {
         this.value2 = value;
      }

      @NotNull
      public final Vec3 getPos2() {
         return this.pos;
      }

      public final int getValue3() {
         return this.value;
      }

      public final int getValue4() {
         return this.value2;
      }

      @NotNull
      @Override
      public String toString() {
         int value22 = this.value2;
         int value3 = this.value;
         Vec3 pos2 = this.pos;
         return "Effect(pos=" + pos2 + ", color=" + value3 + ", age=" + value22 + ")";
      }

      @Override
      public int hashCode() {
         int hashCode = this.pos.hashCode();
         hashCode = hashCode * 31 + Integer.hashCode(this.value);
         return hashCode * 31 + Integer.hashCode(this.value2);
      }

      @Override
      public boolean equals(Object object) {
         if (this == object) {
            return true;
         } else if (!(object instanceof BuffEffect.Effect effect)) {
            return false;
         } else if (!Intrinsics.areEqual(this.pos, effect.pos)) {
            return false;
         } else {
            return this.value != effect.value ? false : this.value2 == effect.value2;
         }
      }
   }
}
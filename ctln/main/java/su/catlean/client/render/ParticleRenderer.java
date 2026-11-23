// deobf by pusia
package su.catlean.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.awt.Color;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.ClosedRange;
import kotlin.ranges.IntRange;
import net.minecraft.client.Camera;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaternionfc;
import su.catlean.api.enums.ParticleType;
import su.catlean.client.services.ThemeManager;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.math.MathUtil;
import su.catlean.utils.render.MeshBuilder;
import su.catlean.utils.render.PoseUtil;
import su.catlean.utils.render.Render2D;
import su.catlean.utils.render.Render3D;

public final class ParticleRenderer {
   @NotNull
   private ParticleType tex;
   @NotNull
   private Vec3 pos;
   private float amount;
   private float amount2;
   private int value;
   @NotNull
   private Vec3 velocity;
   @NotNull
   private Color color;
   private float amount3;
   private float amount4;
   private boolean flag;
   private final int value2;
   @NotNull
   private Vec3 vec3;
   private float amount5;
   private float amount6;
   private int value3;

   public ParticleRenderer(
      ParticleType tex, Vec3 pos, float amount, float amount2, int value, Vec3 velocity, Color color, float amount3, float amount4, boolean flag
   ) {
      this.tex = tex;
      this.pos = pos;
      this.amount = amount;
      this.amount2 = amount2;
      this.value = value;
      this.velocity = velocity;
      this.color = color;
      this.amount3 = amount3;
      this.amount4 = amount4;
      this.flag = flag;
      this.value2 = this.value;
      this.vec3 = this.pos;
      this.value3 = MathUtil.defaultImpl3((ClosedRange)(new IntRange(0, 3)), false, 2, null);
   }

   public ParticleRenderer(
      ParticleType particleType,
      Vec3 vec3,
      float amount,
      float amount2,
      int value,
      Vec3 vec32,
      Color color,
      float amount3,
      float amount4,
      boolean flag,
      int value2,
      DefaultConstructorMarker defaultConstructorMarker
   ) {
      if ((value2 & 64) != 0) {
         color = Color.BLACK;
      }

      if ((value2 & 128) != 0) {
         amount3 = 0.03F;
      }

      if ((value2 & 256) != 0) {
         amount4 = 0.99F;
      }

      if ((value2 & 512) != 0) {
         flag = true;
      }

      this(particleType, vec3, amount, amount2, value, vec32, color, amount3, amount4, flag);
   }

   @NotNull
   public final ParticleType getTex() {
      return this.tex;
   }

   @NotNull
   public final Vec3 getPos() {
      return this.pos;
   }

   public final float getAmount2() {
      return this.amount2;
   }

   public final void setAmount2(float amount) {
      this.amount2 = amount;
   }

   @NotNull
   public final Vec3 getVelocity() {
      return this.velocity;
   }

   @NotNull
   public final Color getColor() {
      return this.color;
   }

   public final boolean isFlag() {
      int value4 = this.value;
      this.value = value4 + -1;
      if (this.value < 0) {
         return true;
      } else {
         this.vec3 = this.pos;
         this.pos = this.pos.add(this.velocity);
         this.velocity = this.velocity.scale(this.amount4);
         this.velocity = this.velocity.add(0.0, -this.amount3, 0.0);
         this.amount6 = this.amount5;
         this.amount5 = this.amount5 + this.amount;
         return false;
      }
   }

   public final void render(PoseStack matrices, Quaternionfc base, MeshBuilder polygon) {
      matrices.pushPose();
      float amount7 = Render2D.INSTANCE.computeAmount(this.value + 1.0F, (float)this.value);
      float value22 = this.flag ? amount7 / this.value2 : 1.0F;
      float amount22 = this.amount2 * value22;
      Camera mainCamera = MinecraftHolder.getMinecraft().gameRenderer.mainCamera();
      Color color2 = Intrinsics.areEqual(this.color, Color.BLACK) ? ThemeManager.INSTANCE.computeColor(this.value * 2) : this.color;
      Vec3 subtract = Render3D.defaultImpl(Render3D.INSTANCE, this.vec3, this.pos, 0.0F, 4, null).subtract(mainCamera.position());
      matrices.translate(subtract.x, subtract.y, subtract.z);
      PoseUtil.render(matrices, base);
      if (this.amount5 != 0.0F && this.amount6 != 0.0F) {
         PoseUtil.render(matrices, (Quaternionfc)Axis.ZP.rotationDegrees(Render2D.INSTANCE.computeAmount(this.amount6, this.amount5)));
      }

      Matrix4f pose = matrices.last().pose();
      Color color3 = !this.flag ? color2 : Render2D.INSTANCE.computeColor(color2, value22);
      float amount8 = this.tex.getAmount();
      float amount32 = this.tex.getAmount3();
      float amount23 = this.tex.getAmount2();
      float amount42 = this.tex.getAmount4();
      matrices.translate(-amount22 / 2.0, -amount22 / 2.0, -0.01);
      polygon.computeMeshBuilder(pose, 0.0F, amount22, 0.0F).computeMeshBuilder(amount32, amount23).fetchAlpha(color3);
      polygon.computeMeshBuilder(pose, amount22, amount22, 0.0F).computeMeshBuilder(amount8, amount23).fetchAlpha(color3);
      polygon.computeMeshBuilder(pose, amount22, 0.0F, 0.0F).computeMeshBuilder(amount8, amount42).fetchAlpha(color3);
      polygon.computeMeshBuilder(pose, 0.0F, 0.0F, 0.0F).computeMeshBuilder(amount32, amount42).fetchAlpha(color3);
      matrices.popPose();
   }
}
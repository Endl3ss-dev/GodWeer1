// deobf by pusia
package su.catlean.client.render.vertex;

import com.mojang.blaze3d.vertex.VertexConsumer;
import java.awt.Color;
import org.jetbrains.annotations.NotNull;
import su.catlean.client.features.modules.esp.PlayerEsp;
import su.catlean.utils.render.MeshBuilder;

public final class ModelVertexConsumer implements VertexConsumer {
   @NotNull
   private final MeshBuilder polygon;
   @NotNull
   private final PlayerEsp.RenderMode m;
   @NotNull
   private final Color c;
   @NotNull
   private final float[] amounts;
   @NotNull
   private final float[] amounts2;
   @NotNull
   private final float[] amounts3;
   private int value;

   public ModelVertexConsumer(MeshBuilder polygon, PlayerEsp.RenderMode m, Color c) {
      this.polygon = polygon;
      this.m = m;
      this.c = c;
      this.amounts = new float[4];
      this.amounts2 = new float[4];
      this.amounts3 = new float[4];
   }

   @NotNull
   public final MeshBuilder getPolygon() {
      return this.polygon;
   }

   @NotNull
   public final PlayerEsp.RenderMode getM() {
      return this.m;
   }

   @NotNull
   public final Color getC() {
      return this.c;
   }

   @NotNull
   public VertexConsumer addVertex(float amount, float amount2, float amount3) {
      this.amounts[this.value] = amount;
      this.amounts2[this.value] = amount2;
      this.amounts3[this.value] = amount3;
      int value2 = this.value++;
      if (this.value == 4) {
         if (this.m == PlayerEsp.RenderMode.FILL) {
            this.polygon
               .computeMeshBuilder(this.amounts[0], this.amounts2[0], this.amounts3[0])
               .fetchAlpha(this.c)
               .computeMeshBuilder(this.amounts[1], this.amounts2[1], this.amounts3[1])
               .fetchAlpha(this.c)
               .computeMeshBuilder(this.amounts[2], this.amounts2[2], this.amounts3[2])
               .fetchAlpha(this.c)
               .computeMeshBuilder(this.amounts[3], this.amounts2[3], this.amounts3[3])
               .fetchAlpha(this.c);
         }

         if (this.m == PlayerEsp.RenderMode.OUTLINE) {
            this.polygon
               .computeMeshBuilder(this.amounts[0], this.amounts2[0], this.amounts3[0])
               .fetchAlpha(this.c)
               .computeMeshBuilder(this.amounts[1], this.amounts2[1], this.amounts3[1])
               .fetchAlpha(this.c)
               .computeMeshBuilder(this.amounts[1], this.amounts2[1], this.amounts3[1])
               .fetchAlpha(this.c)
               .computeMeshBuilder(this.amounts[2], this.amounts2[2], this.amounts3[2])
               .fetchAlpha(this.c)
               .computeMeshBuilder(this.amounts[2], this.amounts2[2], this.amounts3[2])
               .fetchAlpha(this.c)
               .computeMeshBuilder(this.amounts[3], this.amounts2[3], this.amounts3[3])
               .fetchAlpha(this.c)
               .computeMeshBuilder(this.amounts[0], this.amounts2[0], this.amounts3[0])
               .fetchAlpha(this.c)
               .computeMeshBuilder(this.amounts[0], this.amounts2[0], this.amounts3[0])
               .fetchAlpha(this.c);
         }

         this.value = 0;
      }

      return this;
   }

   @NotNull
   public ModelVertexConsumer computeModelVertexConsumer(int value, int value2, int value3, int value4) {
      return this;
   }

   @NotNull
   public ModelVertexConsumer computeModelVertexConsumer(int value) {
      return this;
   }

   @NotNull
   public ModelVertexConsumer computeModelVertexConsumer(float amount, float amount2) {
      return this;
   }

   @NotNull
   public ModelVertexConsumer computeModelVertexConsumer(int value, int value2) {
      return this;
   }

   @NotNull
   public ModelVertexConsumer computeModelVertexConsumer2(int value, int value2) {
      return this;
   }

   @NotNull
   public ModelVertexConsumer computeModelVertexConsumer(float amount, float amount2, float amount3) {
      return this;
   }

   @NotNull
   public ModelVertexConsumer computeModelVertexConsumer(float amount) {
      return this;
   }

   public VertexConsumer setColor(int value, int value2, int value3, int value4) {
      return this.computeModelVertexConsumer(value, value2, value3, value4);
   }

   public VertexConsumer setColor(int value) {
      return this.computeModelVertexConsumer(value);
   }

   public VertexConsumer setUv(float amount, float amount2) {
      return this.computeModelVertexConsumer(amount, amount2);
   }

   public VertexConsumer setUv1(int value, int value2) {
      return this.computeModelVertexConsumer(value, value2);
   }

   public VertexConsumer setUv2(int value, int value2) {
      return this.computeModelVertexConsumer2(value, value2);
   }

   public VertexConsumer setNormal(float amount, float amount2, float amount3) {
      return this.computeModelVertexConsumer(amount, amount2, amount3);
   }

   public VertexConsumer setLineWidth(float amount) {
      return this.computeModelVertexConsumer(amount);
   }
}
// deobf by pusia
package su.catlean.client.render.vertex;

import com.mojang.blaze3d.vertex.VertexConsumer;
import java.awt.Color;
import org.jetbrains.annotations.NotNull;
import su.catlean.utils.render.MeshBuilder;

public final class PolygonVertexConsumer implements VertexConsumer {
   @NotNull
   private final MeshBuilder polygon;
   @NotNull
   private final Color c;
   @NotNull
   private final float[] amounts;
   @NotNull
   private final float[] amounts2;
   @NotNull
   private final float[] amounts3;
   @NotNull
   private final float[] amounts4;
   @NotNull
   private final float[] amounts5;
   private int value;

   public PolygonVertexConsumer(MeshBuilder polygon, Color c) {
      this.polygon = polygon;
      this.c = c;
      this.amounts = new float[4];
      this.amounts2 = new float[4];
      this.amounts3 = new float[4];
      this.amounts4 = new float[4];
      this.amounts5 = new float[4];
   }

   @NotNull
   public final MeshBuilder getPolygon() {
      return this.polygon;
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
      return this;
   }

   @NotNull
   public PolygonVertexConsumer computePolygonVertexConsumer(int value, int value2, int value3, int value4) {
      return this;
   }

   @NotNull
   public PolygonVertexConsumer computePolygonVertexConsumer(int value) {
      return this;
   }

   @NotNull
   public VertexConsumer setUv(float amount, float amount2) {
      this.amounts4[this.value] = amount;
      this.amounts5[this.value] = amount2;
      int value2 = this.value++;
      if (this.value == 4) {
         this.polygon
            .computeMeshBuilder(this.amounts[0], this.amounts2[0], this.amounts3[0])
            .computeMeshBuilder(this.amounts4[0], this.amounts5[0])
            .fetchAlpha(this.c)
            .computeMeshBuilder(this.amounts[1], this.amounts2[1], this.amounts3[1])
            .computeMeshBuilder(this.amounts4[1], this.amounts5[1])
            .fetchAlpha(this.c)
            .computeMeshBuilder(this.amounts[2], this.amounts2[2], this.amounts3[2])
            .computeMeshBuilder(this.amounts4[2], this.amounts5[2])
            .fetchAlpha(this.c)
            .computeMeshBuilder(this.amounts[3], this.amounts2[3], this.amounts3[3])
            .computeMeshBuilder(this.amounts4[3], this.amounts5[3])
            .fetchAlpha(this.c);
         this.value = 0;
      }

      return this;
   }

   @NotNull
   public PolygonVertexConsumer computePolygonVertexConsumer(int value, int value2) {
      return this;
   }

   @NotNull
   public PolygonVertexConsumer computePolygonVertexConsumer2(int value, int value2) {
      return this;
   }

   @NotNull
   public PolygonVertexConsumer computePolygonVertexConsumer(float amount, float amount2, float amount3) {
      return this;
   }

   @NotNull
   public PolygonVertexConsumer computePolygonVertexConsumer(float amount) {
      return this;
   }

   public VertexConsumer setColor(int value, int value2, int value3, int value4) {
      return this.computePolygonVertexConsumer(value, value2, value3, value4);
   }

   public VertexConsumer setColor(int value) {
      return this.computePolygonVertexConsumer(value);
   }

   public VertexConsumer setUv1(int value, int value2) {
      return this.computePolygonVertexConsumer(value, value2);
   }

   public VertexConsumer setUv2(int value, int value2) {
      return this.computePolygonVertexConsumer2(value, value2);
   }

   public VertexConsumer setNormal(float amount, float amount2, float amount3) {
      return this.computePolygonVertexConsumer(amount, amount2, amount3);
   }

   public VertexConsumer setLineWidth(float amount) {
      return this.computePolygonVertexConsumer(amount);
   }
}
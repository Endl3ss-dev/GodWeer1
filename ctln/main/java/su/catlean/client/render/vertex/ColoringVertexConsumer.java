// deobf by pusia
package su.catlean.client.render.vertex;

import com.mojang.blaze3d.vertex.VertexConsumer;
import java.awt.Color;
import org.jetbrains.annotations.NotNull;
import su.catlean.utils.render.MeshBuilder;

public final class ColoringVertexConsumer implements VertexConsumer {
   @NotNull
   private final MeshBuilder polygon;
   @NotNull
   private final Color c;

   public ColoringVertexConsumer(MeshBuilder polygon, Color c) {
      this.polygon = polygon;
      this.c = c;
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
      this.polygon.computeMeshBuilder(amount, amount2, amount3);
      return this;
   }

   @NotNull
   public ColoringVertexConsumer computeColoringVertexConsumer(int value, int value2, int value3, int value4) {
      return this;
   }

   @NotNull
   public VertexConsumer setColor(int value) {
      return this;
   }

   @NotNull
   public VertexConsumer setUv(float amount, float amount2) {
      this.polygon.computeMeshBuilder(amount, amount2).fetchAlpha(this.c);
      return this;
   }

   @NotNull
   public ColoringVertexConsumer computeColoringVertexConsumer(int value, int value2) {
      return this;
   }

   @NotNull
   public ColoringVertexConsumer computeColoringVertexConsumer2(int value, int value2) {
      return this;
   }

   @NotNull
   public ColoringVertexConsumer computeColoringVertexConsumer(float amount, float amount2, float amount3) {
      return this;
   }

   @NotNull
   public ColoringVertexConsumer computeColoringVertexConsumer(float amount) {
      return this;
   }

   public VertexConsumer setColor(int value, int value2, int value3, int value4) {
      return this.computeColoringVertexConsumer(value, value2, value3, value4);
   }

   public VertexConsumer setUv1(int value, int value2) {
      return this.computeColoringVertexConsumer(value, value2);
   }

   public VertexConsumer setUv2(int value, int value2) {
      return this.computeColoringVertexConsumer2(value, value2);
   }

   public VertexConsumer setNormal(float amount, float amount2, float amount3) {
      return this.computeColoringVertexConsumer(amount, amount2, amount3);
   }

   public VertexConsumer setLineWidth(float amount) {
      return this.computeColoringVertexConsumer(amount);
   }
}
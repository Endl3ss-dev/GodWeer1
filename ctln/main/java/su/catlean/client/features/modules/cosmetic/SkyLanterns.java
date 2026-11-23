// deobf by pusia
package su.catlean.client.features.modules.cosmetic;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.random.Random;
import kotlin.ranges.ClosedRange;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaternionfc;
import su.catlean.api.enums.ParticleType;
import su.catlean.api.event.events.client.TickEvent;
import su.catlean.api.event.events.render.Render3DEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.ColorSetting;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.render.ParticleRenderer;
import su.catlean.client.render.Pipelines;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.math.MathUtil;
import su.catlean.utils.render.BillboardRenderer;
import su.catlean.utils.render.MeshBuilder;
import su.catlean.utils.render.Render2D;
import su.catlean.utils.render.Render3D;

public final class SkyLanterns extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(SkyLanterns.class, "fireColor", "getFireColor()Ljava/awt/Color;", 0), new PropertyReference1Impl(SkyLanterns.class, "color", "getColor()Ljava/awt/Color;", 0), new PropertyReference1Impl(SkyLanterns.class, "colorBottom", "getColorBottom()Ljava/awt/Color;", 0), new PropertyReference1Impl(SkyLanterns.class, "count", "getCount()I", 0) };
   @NotNull
   public static final SkyLanterns INSTANCE = new SkyLanterns();
   @NotNull
   private static final ColorSetting fireColor = SettingFactory.defaultImpl(INSTANCE, "fire-color", Color.YELLOW, null, null, 12, null);
   @NotNull
   private static final ColorSetting color = SettingFactory.defaultImpl(
      INSTANCE, "color", Render2D.INSTANCE.computeColor(Color.YELLOW, 0.2F), null, null, 12, null
   );
   @NotNull
   private static final ColorSetting bottomColor = SettingFactory.defaultImpl(
      INSTANCE, "bottom-color", Render2D.INSTANCE.computeColor(Color.ORANGE, 0.2F), null, null, 12, null
   );
   @NotNull
   private static final IntSetting count = SettingFactory.defaultImpl(INSTANCE, "count", 256, new IntRange(0, 512), null, null, 24, null);
   @NotNull
   private static final Color color2 = new Color(70, 70, 70);
   @NotNull
   private static final List<SkyLanterns.Engine> items = new ArrayList();

   private SkyLanterns() {
      super("sky-lanterns", SubCategories.getSubCategory6(), null, 4, null);
   }

   private final Color getFireColor() {
      return (Color)fireColor.computeObject(this, $$delegatedProperties[0]);
   }

   private final Color getColor() {
      return (Color)color.computeObject(this, $$delegatedProperties[1]);
   }

   private final Color getColorBottom() {
      return (Color)bottomColor.computeObject(this, $$delegatedProperties[2]);
   }

   private final int getCount() {
      return ((Number)count.computeObject(this, $$delegatedProperties[3])).intValue();
   }

   @Override
   public void update2() {
      items.clear();
   }

   @Override
   public void update4() {
      items.clear();
   }

   @Flow
   private final void onTick(TickEvent tickEvent) {
      if (this.isFlag6() && !MinecraftHolder.getMinecraft().isPaused()) {
         while (items.size() < this.getCount()) {
            this.applyGetDeltaMovement();
         }

         Iterator<SkyLanterns.Engine> iterator = items.iterator();

         while (iterator.hasNext()) {
            SkyLanterns.Engine engine = (SkyLanterns.Engine)iterator.next();
            engine.setAmount4(engine.getAmount());
            engine.setAmount5(engine.getAmount2());
            engine.setAmount6(engine.getAmount3());
            engine.setAmount7(engine.getAmount7() + MathUtil.defaultImpl2((ClosedRange)RangesKt.rangeTo(-0.00125F, 0.00125F), false, 2, null));
            engine.setAmount8(engine.getAmount8() + MathUtil.defaultImpl2((ClosedRange)RangesKt.rangeTo(2.0E-4F, 6.0E-4F), false, 2, null));
            engine.setAmount9(engine.getAmount9() + MathUtil.defaultImpl2((ClosedRange)RangesKt.rangeTo(-0.00125F, 0.00125F), false, 2, null));
            engine.setAmount7(engine.getAmount7() * 0.99F);
            engine.setAmount8(engine.getAmount8() * 0.99F);
            engine.setAmount9(engine.getAmount9() * 0.99F);
            engine.setAmount(engine.getAmount() + engine.getAmount7());
            engine.setAmount2(engine.getAmount2() + engine.getAmount8());
            engine.setAmount3(engine.getAmount3() + engine.getAmount9());
            engine.setAmount10(engine.getAmount10() + engine.getAmount11());
            float amount = (float)Math.sin(Render2D.INSTANCE.computeAmount(engine.getValue() - 1.0F, (float)engine.getValue()) / 60.0F);
            float amount2 = Render2D.INSTANCE.computeAmount(engine.getAmount4(), engine.getAmount()) + amount;
            float amount3 = Render2D.INSTANCE.computeAmount(engine.getAmount5(), engine.getAmount2());
            float amount4 = Render2D.INSTANCE.computeAmount(engine.getAmount6(), engine.getAmount3()) - amount;
            float value3 = engine.getValue3() - engine.getValue() < 10.0F ? (engine.getValue3() - engine.getValue()) / 10.0F : 1.0F;
            Color alpha = Render2D.INSTANCE.fetchAlpha(this.getFireColor(), this.getColor(), 0.5F);
            BillboardRenderer instance = BillboardRenderer.INSTANCE;
            ParticleType particleType = HitFx.Particle.FIRE_FLY.getParticleType();
            Vec3 vec3 = new Vec3(amount2 + (Random.Default.nextFloat() - 0.5F) * 0.06, amount3 + 0.5, amount4 + (Random.Default.nextFloat() - 0.5F) * 0.06);
            float nextFloat = Random.Default.nextFloat() * 20.0F;
            Vec3 vec32 = new Vec3(
               (Random.Default.nextDouble() - 0.5) * 0.02 + engine.getAmount7(),
               Random.Default.nextDouble() * 0.06 + engine.getAmount8(),
               (Random.Default.nextDouble() - 0.5) * 0.02 + engine.getAmount9()
            );
            Color color3 = Render2D.INSTANCE.computeColor(this.getFireColor(), Render2D.INSTANCE.fetchAlpha(this.getFireColor()) * value3);
            instance.testParticleRenderer(new ParticleRenderer(particleType, vec3, nextFloat, 0.25F, 12, vec32, color3, 0.0F, 0.0F, false, 768, null));
            if (engine.getValue() % 2 == 0) {
               instance = BillboardRenderer.INSTANCE;
               particleType = HitFx.Particle.FIRE_FLY.getParticleType();
               vec3 = new Vec3(
                  amount2 + MathUtil.defaultImpl((ClosedRange)RangesKt.rangeTo(-0.03, 0.03), false, 2, null),
                  amount3 + 1.0,
                  amount4 + MathUtil.defaultImpl((ClosedRange)RangesKt.rangeTo(-0.03, 0.03), false, 2, null)
               );
               nextFloat = MathUtil.defaultImpl2((ClosedRange)RangesKt.rangeTo(0.0F, 20.0F), false, 2, null);
               vec32 = new Vec3(engine.getAmount7(), engine.getAmount8(), engine.getAmount9());
               color3 = Render2D.INSTANCE.computeColor(alpha, 0.5F * value3);
               instance.testParticleRenderer(new ParticleRenderer(particleType, vec3, nextFloat, 1.5F, 2, vec32, color3, 0.0F, 0.0F, false, 256, null));
               instance = BillboardRenderer.INSTANCE;
               particleType = HitFx.Particle.FIRE_FLY.getParticleType();
               vec3 = new Vec3(amount2 + (Random.Default.nextFloat() - 0.5F) * 0.06, amount3 + 1.0, amount4 + (Random.Default.nextFloat() - 0.5F) * 0.06);
               nextFloat = MathUtil.defaultImpl2((ClosedRange)RangesKt.rangeTo(0.0F, 20.0F), false, 2, null);
               vec32 = new Vec3(engine.getAmount7(), engine.getAmount8(), engine.getAmount9());
               color3 = Render2D.INSTANCE.computeColor(alpha, 0.2F * value3);
               instance.testParticleRenderer(new ParticleRenderer(particleType, vec3, nextFloat, 3.0F, 2, vec32, color3, 0.0F, 0.0F, false, 256, null));
            }

            int value = engine.getValue();
            engine.setValue(value + 1);
            if (value > engine.getValue3() || engine.getAmount2() - MinecraftHolder.getLocalPlayer().getY() > 20.0) {
               iterator.remove();
            }
         }
      }
   }

   private final void applyGetDeltaMovement() {
      Vec3 scale = this.computeVec3((Entity)MinecraftHolder.getLocalPlayer()).add(MinecraftHolder.getLocalPlayer().getDeltaMovement().scale(10.0));
      items.add(
         new SkyLanterns.Engine(
            (float)(scale.x + MathUtil.defaultImpl(-48.0F, 48.0F, false, 4, null)),
            (float)(scale.y + 4.0F + MathUtil.defaultImpl(-2.0F, 2.0F, false, 4, null)),
            (float)(scale.z + MathUtil.defaultImpl(-48.0F, 48.0F, false, 4, null)),
            0.0F,
            0.0F,
            0.0F,
            0.0F,
            0.0F,
            0.0F,
            0,
            0.0F,
            0.0F,
            0,
            0,
            16376,
            null
         )
      );
   }

   @Flow(
      priority = -10
   )
   private final void onRender3D(Render3DEvent render3DEvent) {
      if (!items.isEmpty()) {
         Camera mainCamera = MinecraftHolder.getMinecraft().gameRenderer.mainCamera();
         MeshBuilder meshBuilder = new MeshBuilder(DefaultVertexFormat.POSITION_COLOR, 9 * items.size(), false, 4, null);
         PoseStack poseStack = new PoseStack();
         Iterable<SkyLanterns.Engine> items2 = items;
         var arrayList = new ArrayList();

         for (Object object : items2) {
            SkyLanterns.Engine engine = (SkyLanterns.Engine)object;
            if (Render3D.INSTANCE.getFrustum().pointInFrustum(engine.getAmount(), engine.getAmount2() + 1.5, engine.getAmount3())
               || Render3D.INSTANCE.getFrustum().pointInFrustum(engine.getAmount(), engine.getAmount2(), engine.getAmount3())) {
               arrayList.add(object);
            }
         }

         for (SkyLanterns.Engine engine2 : (Iterable<su.catlean.client.features.modules.cosmetic.SkyLanterns.Engine>)arrayList) {
            float coerceAtLeast = RangesKt.coerceAtLeast(Render2D.INSTANCE.computeAmount(engine2.getValue() - 1.0F, (float)engine2.getValue()), 0.0F);
            float value = (float)Math.sin(coerceAtLeast / 60.0F);
            float amount = Render2D.INSTANCE.computeAmount(engine2.getAmount4(), engine2.getAmount()) + value;
            float amount2 = Render2D.INSTANCE.computeAmount(engine2.getAmount5(), engine2.getAmount2());
            float amount3 = Render2D.INSTANCE.computeAmount(engine2.getAmount6(), engine2.getAmount3()) - value;
            poseStack.pushPose();
            poseStack.translate(amount - mainCamera.position().x, amount2 - mainCamera.position().y, amount3 - mainCamera.position().z);
            poseStack.mulPose(
               (Quaternionfc)Axis.YP.rotationDegrees(Render2D.INSTANCE.computeAmount(engine2.getAmount10() - engine2.getAmount11(), engine2.getAmount10()))
            );
            this.applyToRadians(
               meshBuilder,
               poseStack.last().pose(),
               amount,
               amount2,
               amount3,
               Render2D.INSTANCE.computeAmount(engine2.getAmount10() - engine2.getAmount11(), engine2.getAmount10()),
               coerceAtLeast,
               engine2.getValue3()
            );
            poseStack.popPose();
         }

         MeshBuilder.defaultImpl(
            meshBuilder, Pipelines.INSTANCE.getRenderPipeline23(), null, null, render3DEvent.getStack().last().pose(), null, null, null, 118, null
         );
      }
   }

   private final void applyToRadians(
      MeshBuilder meshBuilder, Matrix4f matrix4f, float amount, float amount2, float amount3, float amount4, float amount5, float amount6
   ) {
      float value = 0.33F;
      float value2 = 0.2F;
      float value3 = 0.08F;
      float value4 = 0.78F;
      float value5 = 1.3299999F;
      float value6 = 0.78F + value2;
      float amount52 = (0.0F <= amount5 ? amount5 <= 10.0F : false) ? amount5 / 10.0F : (amount6 - amount5 < 10.0F ? (amount6 - amount5) / 20.0F : 1.0F);
      Color color3 = Render2D.INSTANCE.computeColor(this.getColor(), Render2D.INSTANCE.fetchAlpha(this.getColor()) * amount52);
      Color color4 = Render2D.INSTANCE.computeColor(this.getColorBottom(), Render2D.INSTANCE.fetchAlpha(this.getColorBottom()) * amount52);
      Color color5 = Render2D.INSTANCE.computeColor(color2, Render2D.INSTANCE.fetchAlpha(color2) * amount52);
      meshBuilder.computeMeshBuilder(matrix4f, -value, value4, -value).fetchAlpha(color3);
      meshBuilder.computeMeshBuilder(matrix4f, -value, value5, -value).fetchAlpha(color3);
      meshBuilder.computeMeshBuilder(matrix4f, value, value5, -value).fetchAlpha(color3);
      meshBuilder.computeMeshBuilder(matrix4f, value, value4, -value).fetchAlpha(color3);
      meshBuilder.computeMeshBuilder(matrix4f, -value + value3, value4 - value2, -value + value3).fetchAlpha(color4);
      meshBuilder.computeMeshBuilder(matrix4f, -value, value6 - value2, -value).fetchAlpha(color3);
      meshBuilder.computeMeshBuilder(matrix4f, value, value6 - value2, -value).fetchAlpha(color3);
      meshBuilder.computeMeshBuilder(matrix4f, value - value3, value4 - value2, -value + value3).fetchAlpha(color4);
      meshBuilder.computeMeshBuilder(matrix4f, -value, value4, value).fetchAlpha(color3);
      meshBuilder.computeMeshBuilder(matrix4f, -value, value5, value).fetchAlpha(color3);
      meshBuilder.computeMeshBuilder(matrix4f, value, value5, value).fetchAlpha(color3);
      meshBuilder.computeMeshBuilder(matrix4f, value, value4, value).fetchAlpha(color3);
      meshBuilder.computeMeshBuilder(matrix4f, -value + value3, value4 - value2, value - value3).fetchAlpha(color4);
      meshBuilder.computeMeshBuilder(matrix4f, -value, value6 - value2, value).fetchAlpha(color3);
      meshBuilder.computeMeshBuilder(matrix4f, value, value6 - value2, value).fetchAlpha(color3);
      meshBuilder.computeMeshBuilder(matrix4f, value - value3, value4 - value2, value - value3).fetchAlpha(color4);
      meshBuilder.computeMeshBuilder(matrix4f, -value, value4, -value).fetchAlpha(color3);
      meshBuilder.computeMeshBuilder(matrix4f, -value, value5, -value).fetchAlpha(color3);
      meshBuilder.computeMeshBuilder(matrix4f, -value, value5, value).fetchAlpha(color3);
      meshBuilder.computeMeshBuilder(matrix4f, -value, value4, value).fetchAlpha(color3);
      meshBuilder.computeMeshBuilder(matrix4f, -value + value3, value4 - value2, -value + value3).fetchAlpha(color4);
      meshBuilder.computeMeshBuilder(matrix4f, -value, value6 - value2, -value).fetchAlpha(color3);
      meshBuilder.computeMeshBuilder(matrix4f, -value, value6 - value2, value).fetchAlpha(color3);
      meshBuilder.computeMeshBuilder(matrix4f, -value + value3, value4 - value2, value - value3).fetchAlpha(color4);
      meshBuilder.computeMeshBuilder(matrix4f, value, value4, -value).fetchAlpha(color3);
      meshBuilder.computeMeshBuilder(matrix4f, value, value5, -value).fetchAlpha(color3);
      meshBuilder.computeMeshBuilder(matrix4f, value, value5, value).fetchAlpha(color3);
      meshBuilder.computeMeshBuilder(matrix4f, value, value4, value).fetchAlpha(color3);
      meshBuilder.computeMeshBuilder(matrix4f, value - value3, value4 - value2, -value + value3).fetchAlpha(color4);
      meshBuilder.computeMeshBuilder(matrix4f, value, value6 - value2, -value).fetchAlpha(color3);
      meshBuilder.computeMeshBuilder(matrix4f, value, value6 - value2, value).fetchAlpha(color3);
      meshBuilder.computeMeshBuilder(matrix4f, value - value3, value4 - value2, value - value3).fetchAlpha(color4);
      meshBuilder.computeMeshBuilder(matrix4f, -value, value5, -value).fetchAlpha(color3);
      meshBuilder.computeMeshBuilder(matrix4f, value, value5, -value).fetchAlpha(color3);
      meshBuilder.computeMeshBuilder(matrix4f, value, value5, value).fetchAlpha(color3);
      meshBuilder.computeMeshBuilder(matrix4f, -value, value5, value).fetchAlpha(color3);
      double radians = Math.toRadians(amount4);
      Vec3 vec3 = this.computeVec3(amount - value + value3, amount2 + 0.58F, amount3 + value3 - value, amount, amount2 + 0.58F, amount3, radians);
      Vec3 vec32 = this.computeVec3(amount + value - value3, amount2 + 0.58F, amount3 + value3 - value, amount, amount2 + 0.58F, amount3, radians);
      Vec3 vec33 = this.computeVec3(amount - value + value3, amount2 + 0.58F, amount3 - value3 + value, amount, amount2 + 0.58F, amount3, radians);
      Vec3 vec34 = this.computeVec3(amount + value - value3, amount2 + 0.58F, amount3 - value3 + value, amount, amount2 + 0.58F, amount3, radians);
      Vec3 vec35 = this.computeVec3(amount - value + value3, amount2 + 0.58F, amount3, amount, amount2 + 0.58F, amount3, radians);
      Vec3 vec36 = this.computeVec3(amount + value - value3, amount2 + 0.58F, amount3, amount, amount2 + 0.58F, amount3, radians);
      Vec3 vec37 = this.computeVec3(amount, amount2 + 0.58F, amount3 + value3 - value, amount, amount2 + 0.58F, amount3, radians);
      Vec3 vec38 = this.computeVec3(amount, amount2 + 0.58F, amount3 - value3 + value, amount, amount2 + 0.58F, amount3, radians);
      Render3D.defaultImpl(Render3D.INSTANCE, vec3, vec32, color5, null, 8, null);
      Render3D.defaultImpl(Render3D.INSTANCE, vec33, vec34, color5, null, 8, null);
      Render3D.defaultImpl(Render3D.INSTANCE, vec35, vec36, color5, null, 8, null);
      Render3D.defaultImpl(Render3D.INSTANCE, vec32, vec34, color5, null, 8, null);
      Render3D.defaultImpl(Render3D.INSTANCE, vec3, vec33, color5, null, 8, null);
      Render3D.defaultImpl(Render3D.INSTANCE, vec37, vec38, color5, null, 8, null);
   }

   private final Vec3 computeVec3(float amount, float amount2, float amount3, float amount4, float amount5, float amount6, double amount7) {
      double amount72 = Math.sin(amount7);
      double amount73 = Math.cos(amount7);
      float amount8 = amount - amount4;
      float amount32 = amount3 - amount6;
      float amount22 = amount2 - amount5;
      double value = amount8 * amount73 + amount32 * amount72;
      double value2 = -amount8 * amount72 + amount32 * amount73;
      return new Vec3(amount4 + value, (double)amount5 + amount22, amount6 + value2);
   }

   static {
   }

   public static final class Engine {
      private float amount;
      private float amount2;
      private float amount3;
      private float amount4;
      private float amount5;
      private float amount6;
      private float amount7;
      private float amount8;
      private float amount9;
      private int value;
      private float amount10;
      private final float amount11;
      private final int value2;
      private final int value3;

      public Engine(
         float amount,
         float amount2,
         float amount3,
         float amount4,
         float amount5,
         float amount6,
         float amount7,
         float amount8,
         float amount9,
         int value,
         float amount10,
         float amount11,
         int value2,
         int value3
      ) {
         this.amount = amount;
         this.amount2 = amount2;
         this.amount3 = amount3;
         this.amount4 = amount4;
         this.amount5 = amount5;
         this.amount6 = amount6;
         this.amount7 = amount7;
         this.amount8 = amount8;
         this.amount9 = amount9;
         this.value = value;
         this.amount10 = amount10;
         this.amount11 = amount11;
         this.value2 = value2;
         this.value3 = value3;
      }

      public Engine(
         float amount,
         float amount2,
         float amount3,
         float amount4,
         float amount5,
         float amount6,
         float amount7,
         float amount8,
         float amount9,
         int value,
         float amount10,
         float amount11,
         int value2,
         int value3,
         int value4,
         DefaultConstructorMarker defaultConstructorMarker
      ) {
         if ((value4 & 8) != 0) {
            amount4 = amount;
         }

         if ((value4 & 16) != 0) {
            amount5 = amount2;
         }

         if ((value4 & 32) != 0) {
            amount6 = amount3;
         }

         if ((value4 & 64) != 0) {
            amount7 = MathUtil.defaultImpl2((ClosedRange)RangesKt.rangeTo(-0.1F, 0.1F), false, 2, null);
         }

         if ((value4 & 128) != 0) {
            amount8 = MathUtil.defaultImpl2((ClosedRange)RangesKt.rangeTo(0.01F, 0.03F), false, 2, null);
         }

         if ((value4 & 256) != 0) {
            amount9 = MathUtil.defaultImpl2((ClosedRange)RangesKt.rangeTo(-0.1F, 0.1F), false, 2, null);
         }

         if ((value4 & 512) != 0) {
            value = 0;
         }

         if ((value4 & 1024) != 0) {
            amount10 = MathUtil.defaultImpl2((ClosedRange)RangesKt.rangeTo(-15.0F, 15.0F), false, 2, null);
         }

         if ((value4 & 2048) != 0) {
            amount11 = MathUtil.defaultImpl2((ClosedRange)RangesKt.rangeTo(-0.5F, 0.5F), false, 2, null);
         }

         if ((value4 & 4096) != 0) {
            value2 = MathUtil.defaultImpl3((ClosedRange)(new IntRange(0, 360)), false, 2, null);
         }

         if ((value4 & 8192) != 0) {
            value3 = MathUtil.defaultImpl3((ClosedRange)(new IntRange(100, 200)), false, 2, null);
         }

         this(amount, amount2, amount3, amount4, amount5, amount6, amount7, amount8, amount9, value, amount10, amount11, value2, value3);
      }

      public final float getAmount() {
         return this.amount;
      }

      public final void setAmount(float amount) {
         this.amount = amount;
      }

      public final float getAmount2() {
         return this.amount2;
      }

      public final void setAmount2(float amount) {
         this.amount2 = amount;
      }

      public final float getAmount3() {
         return this.amount3;
      }

      public final void setAmount3(float amount) {
         this.amount3 = amount;
      }

      public final float getAmount4() {
         return this.amount4;
      }

      public final void setAmount4(float amount) {
         this.amount4 = amount;
      }

      public final float getAmount5() {
         return this.amount5;
      }

      public final void setAmount5(float amount) {
         this.amount5 = amount;
      }

      public final float getAmount6() {
         return this.amount6;
      }

      public final void setAmount6(float amount) {
         this.amount6 = amount;
      }

      public final float getAmount7() {
         return this.amount7;
      }

      public final void setAmount7(float amount) {
         this.amount7 = amount;
      }

      public final float getAmount8() {
         return this.amount8;
      }

      public final void setAmount8(float amount) {
         this.amount8 = amount;
      }

      public final float getAmount9() {
         return this.amount9;
      }

      public final void setAmount9(float amount) {
         this.amount9 = amount;
      }

      public final int getValue() {
         return this.value;
      }

      public final void setValue(int value) {
         this.value = value;
      }

      public final float getAmount10() {
         return this.amount10;
      }

      public final void setAmount10(float amount) {
         this.amount10 = amount;
      }

      public final float getAmount11() {
         return this.amount11;
      }

      public final int getValue2() {
         return this.value2;
      }

      public final int getValue3() {
         return this.value3;
      }
   }
}
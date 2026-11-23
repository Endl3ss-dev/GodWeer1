// deobf by pusia
package su.catlean.client.features.modules.esp;

import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.awt.Color;
import kotlin.NoWhenBranchMatchedException;
import kotlin.TuplesKt;
import kotlin.collections.MapsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import net.minecraft.client.Camera;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import su.catlean.api.enums.ParticleType;
import su.catlean.api.enums.Safety;
import su.catlean.api.event.events.render.Render3DEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.ColorSetting;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.api.setting.settings.FloatSetting;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.features.modules.protect.SurroundBlockInfo;
import su.catlean.client.render.Pipelines;
import su.catlean.client.services.ChunkScanner;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.render.AtlasTexture;
import su.catlean.utils.render.MeshBuilder;
import su.catlean.utils.render.PoseUtil;
import su.catlean.utils.render.Render2D;
import su.catlean.utils.render.Render3D;
import su.catlean.utils.world.WorldUtil;

public final class HoleEsp extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(HoleEsp.class, "mode", "getMode()Lsu/catlean/vq;", 0), new PropertyReference1Impl(HoleEsp.class, "rangeXZ", "getRangeXZ()I", 0), new PropertyReference1Impl(HoleEsp.class, "rangeY", "getRangeY()I", 0), new PropertyReference1Impl(HoleEsp.class, "height", "getHeight()F", 0), new PropertyReference1Impl(HoleEsp.class, "rangeFade", "getRangeFade()Z", 0), new PropertyReference1Impl(HoleEsp.class, "bedrockColor", "getBedrockColor()Ljava/awt/Color;", 0), new PropertyReference1Impl(HoleEsp.class, "indestrictibleColor", "getIndestrictibleColor()Ljava/awt/Color;", 0), new PropertyReference1Impl(HoleEsp.class, "bloomAnimation", "getBloomAnimation()Z", 0), new PropertyReference1Impl(HoleEsp.class, "bloomSkew", "getBloomSkew()F", 0), new PropertyReference1Impl(HoleEsp.class, "distribution", "getDistribution()F", 0), new PropertyReference1Impl(HoleEsp.class, "amount", "getAmount()I", 0) };
   @NotNull
   public static final HoleEsp INSTANCE = new HoleEsp();
   @NotNull
   private static final EnumSetting mode = SettingFactory.defaultImpl(INSTANCE, "mode", HoleEsp.Mode.CubeOutline, null, null, 12, null);
   @NotNull
   private static final IntSetting intSetting = SettingFactory.defaultImpl(INSTANCE, "range--x-y", 10, new IntRange(1, 32), null, null, 24, null);
   @NotNull
   private static final IntSetting intSetting2 = SettingFactory.defaultImpl(INSTANCE, "range--y", 5, new IntRange(1, 32), null, null, 24, null);
   @NotNull
   private static final FloatSetting height = SettingFactory.defaultImpl(INSTANCE, "height", 1.0F, RangesKt.rangeTo(0.01F, 5.0F), null, null, 24, null);
   @NotNull
   private static final BooleanSetting rangeFade = SettingFactory.defaultImpl(INSTANCE, "range-fade", true, null, null, 12, null);
   @NotNull
   private static final ColorSetting safe = SettingFactory.defaultImpl(INSTANCE, "safe", new Color(65361), null, null, 12, null);
   @NotNull
   private static final ColorSetting unsafe = SettingFactory.defaultImpl(INSTANCE, "unsafe", new Color(65361), null, null, 12, null);
   @NotNull
   private static final BooleanSetting bloomAnimation = SettingFactory.defaultImpl(INSTANCE, "bloom-animation", true, null, null, 12, null);
   @NotNull
   private static final FloatSetting skew = SettingFactory.defaultImpl(
      INSTANCE, "skew", 0.8F, RangesKt.rangeTo(0.0F, 1.0F), null, () -> INSTANCE.getBloomAnimation(), 8, null
   );
   @NotNull
   private static final FloatSetting distribution = SettingFactory.defaultImpl(
      INSTANCE, "distribution", 0.4F, RangesKt.rangeTo(0.0F, 1.0F), null, () -> INSTANCE.getBloomAnimation(), 8, null
   );
   @NotNull
   private static final IntSetting amount = SettingFactory.defaultImpl(
      INSTANCE, "amount", 2, new IntRange(1, 10), null, () -> INSTANCE.getBloomAnimation(), 8, null
   );

   private HoleEsp() {
      super("hole-esp", SubCategories.getSubCategory7(), null, 4, null);
   }

   private final HoleEsp.Mode getMode() {
      return (HoleEsp.Mode)mode.computeObject(this, $$delegatedProperties[0]);
   }

   public final int getRangeXZ() {
      return ((Number)intSetting.computeObject(this, $$delegatedProperties[1])).intValue();
   }

   public final int getRangeY() {
      return ((Number)intSetting2.computeObject(this, $$delegatedProperties[2])).intValue();
   }

   private final float getHeight() {
      return ((Number)height.computeObject(this, $$delegatedProperties[3])).floatValue();
   }

   private final boolean getRangeFade() {
      return (Boolean)rangeFade.computeObject(this, $$delegatedProperties[4]);
   }

   private final Color getBedrockColor() {
      return (Color)safe.computeObject(this, $$delegatedProperties[5]);
   }

   private final Color getIndestrictibleColor() {
      return (Color)unsafe.computeObject(this, $$delegatedProperties[6]);
   }

   private final boolean getBloomAnimation() {
      return (Boolean)bloomAnimation.computeObject(this, $$delegatedProperties[7]);
   }

   private final float getBloomSkew() {
      return ((Number)skew.computeObject(this, $$delegatedProperties[8])).floatValue();
   }

   private final float getDistribution() {
      return ((Number)distribution.computeObject(this, $$delegatedProperties[9])).floatValue();
   }

   private final int getAmount() {
      return ((Number)amount.computeObject(this, $$delegatedProperties[10])).intValue();
   }

   @Flow
   private final void onRender3D(Render3DEvent render3DEvent) {
      if (this.getMode() == HoleEsp.Mode.BLOOM) {
         this.render(render3DEvent.getStack());
      } else {
         for (SurroundBlockInfo item : (Iterable<SurroundBlockInfo>)ChunkScanner.INSTANCE.getItems()) {
            AABB height2 = item.fetchX(this.getHeight());
            Color indestrictibleColor = item.getSafety() == Safety.UNSAFE ? this.getIndestrictibleColor() : this.getBedrockColor();
            switch (HoleEsp.WhenMappings.values[this.getMode().ordinal()]) {
               case 1:
                  Render3D.defaultImpl2(Render3D.INSTANCE, height2, defaultImpl(this, height2, indestrictibleColor, 0, 4, null), null, 4, null);
                  break;
               case 2:
                  Render3D.defaultImpl(Render3D.INSTANCE, height2, defaultImpl(this, height2, indestrictibleColor, 0, 4, null), null, 4, null);
                  break;
               case 3:
                  Render3D.defaultImpl3(Render3D.INSTANCE, height2, defaultImpl(this, height2, indestrictibleColor, 0, 4, null), null, 4, null);
                  break;
               case 4:
                  Render3D.defaultImpl3(Render3D.INSTANCE, height2, defaultImpl(this, height2, indestrictibleColor, 0, 4, null), null, 4, null);
                  Render3D.defaultImpl2(Render3D.INSTANCE, height2, defaultImpl(this, height2, indestrictibleColor, 0, 4, null), null, 4, null);
                  break;
               case 5:
                  Render3D.INSTANCE.testAABB2(height2, this.fetchCenter(height2, indestrictibleColor, 60), this.fetchCenter(height2, indestrictibleColor, 0));
                  Render3D.INSTANCE.testAABB2(height2, Direction.DOWN, defaultImpl(this, height2, indestrictibleColor, 0, 4, null));
                  break;
               case 6:
                  return;
               default:
                  throw new NoWhenBranchMatchedException();
            }
         }
      }
   }

   private final void render(PoseStack poseStack) {
      MeshBuilder meshBuilder = new MeshBuilder(DefaultVertexFormat.POSITION_TEX_COLOR, 100 * ChunkScanner.INSTANCE.getItems().size(), false, 4, null);
      PoseStack poseStack2 = new PoseStack();
      Quaternionf rotateYXZ = new Quaternionf().rotateYXZ(-Render3D.INSTANCE.getAmount(), Render3D.INSTANCE.getAmount3(), 0.0F);
      PoseUtil.render(poseStack2, (Quaternionfc)Axis.XP.rotation(Render3D.INSTANCE.getAmount3()));
      PoseUtil.render(poseStack2, (Quaternionfc)Axis.YP.rotation(Render3D.INSTANCE.getAmount2()));

      for (SurroundBlockInfo item : (Iterable<SurroundBlockInfo>)ChunkScanner.INSTANCE.getItems()) {
         AABB height2 = item.fetchX(this.getHeight());
         Color indestrictibleColor = item.getSafety() == Safety.UNSAFE ? this.getIndestrictibleColor() : this.getBedrockColor();
         if (Render3D.INSTANCE.getFrustum().isVisible(height2)) {
            Color defaultImpl2 = defaultImpl(this, height2, indestrictibleColor, 0, 4, null);
            Camera mainCamera = MinecraftHolder.getMinecraft().gameRenderer.mainCamera();
            double value = height2.getCenter().x - mainCamera.position().x;
            double minY = height2.minY - mainCamera.position().y;
            double value2 = height2.getCenter().z - mainCamera.position().z;
            double value3 = 1.0;
            float value4 = 0.16F;
            byte byteValue = 49;
            byte byteValue2 = 11;
            ParticleType firefly = ParticleType.FIREFLY;

            for (int j = 0; j < 6; j++) {
               int value5 = 0;
               int value6 = byteValue - 1;
               if (value5 <= value6) {
                  while (true) {
                     float alpha = Render2D.INSTANCE.fetchAlpha(defaultImpl2);
                     if (this.getBloomAnimation()) {
                        float amount2 = Render2D.INSTANCE
                           .computeAmount(MinecraftHolder.getLocalPlayer().tickCount - 1.0F, (float)MinecraftHolder.getLocalPlayer().tickCount);
                        float amount3 = (float)byteValue / this.getAmount();
                        float value7 = (amount2 + value5) / amount3;
                        value7 -= (float)Math.floor(value7);
                        float value8 = -this.getBloomSkew();
                        float value9 = Math.abs(value7 - value8);
                        value9 = Math.min(value9, 1.0F - value9);
                        float distribution2 = value9 / this.getDistribution();
                        float exp = (float)Math.exp(-(distribution2 * distribution2) / 2.0F);
                        alpha = RangesKt.coerceIn(exp, 0.0F, 1.0F) * alpha;
                     }

                     if (!(alpha < 0.01F)) {
                        float xsize = 0.45F * (float)height2.getXsize();
                        float zsize = 0.45F * (float)height2.getZsize();
                        double value10 = (value5 / 1.5F + value3) * byteValue2 % 360.0F / 360.0F;
                        float value11 = 4.0F * (xsize + zsize);
                        float value12 = (float)value10 * value11;
                        float value13 = 0.0F;
                        float value14 = 0.0F;
                        if (value12 < 2.0F * xsize) {
                           value13 = -xsize + value12;
                           value14 = zsize;
                        } else if (value12 < 2.0F * xsize + 2.0F * zsize) {
                           value12 -= 2.0F * xsize;
                           value13 = xsize;
                           value14 = zsize - value12;
                        } else if (value12 < 4.0F * xsize + 2.0F * zsize) {
                           value12 -= 2.0F * xsize + 2.0F * zsize;
                           value13 = xsize - value12;
                           value14 = -zsize;
                        } else {
                           value12 -= 4.0F * xsize + 2.0F * zsize;
                           value13 = -xsize;
                           value14 = -zsize + value12;
                        }

                        poseStack2.pushPose();
                        poseStack2.translate(value + value13, minY + j / 15.0F, value2 + value14);
                        PoseUtil.render(poseStack2, (Quaternionfc)rotateYXZ);
                        Matrix4f pose = poseStack2.last().pose();
                        Color color = Render2D.INSTANCE.computeColor(defaultImpl2, 1.0F / (j + 1) * alpha);
                        meshBuilder.computeMeshBuilder(pose, -value4, value4, 0.0F).computeMeshBuilder(firefly.getAmount(), firefly.getAmount4()).fetchAlpha(color);
                        meshBuilder.computeMeshBuilder(pose, value4, value4, 0.0F).computeMeshBuilder(firefly.getAmount3(), firefly.getAmount4()).fetchAlpha(color);
                        meshBuilder.computeMeshBuilder(pose, value4, -value4, 0.0F).computeMeshBuilder(firefly.getAmount3(), firefly.getAmount2()).fetchAlpha(color);
                        meshBuilder.computeMeshBuilder(pose, -value4, -value4, 0.0F).computeMeshBuilder(firefly.getAmount(), firefly.getAmount2()).fetchAlpha(color);
                        poseStack2.popPose();
                     }

                     if (value5 == value6) {
                        break;
                     }

                     value5++;
                  }
               }
            }
         }
      }

      Camera mainCamera2 = MinecraftHolder.getMinecraft().gameRenderer.mainCamera();
      GpuTextureView textureView = MinecraftHolder.getMinecraft().getTextureManager().getTexture(AtlasTexture.getCatlean()).getTextureView();
      poseStack.pushPose();
      poseStack.mulPose((Quaternionfc)Axis.YP.rotationDegrees(-mainCamera2.yRot() + 180.0F));
      poseStack.mulPose((Quaternionfc)Axis.XP.rotationDegrees(-mainCamera2.xRot()));
      MeshBuilder.defaultImpl(
         meshBuilder,
         Pipelines.INSTANCE.getRenderPipeline18(),
         null,
         null,
         poseStack.last().pose(),
         null,
         MapsKt.mapOf(TuplesKt.to("Sampler0", textureView)),
         null,
         86,
         null
      );
      poseStack.popPose();
   }

   private final Color fetchCenter(AABB aABB, Color color, int value) {
      if (!this.getRangeFade()) {
         return Render2D.INSTANCE.computeColor(color, value / 255.0F);
      } else {
         float rangeXZ = this.getRangeXZ() * 0.5F;
         float amount2 = WorldUtil.INSTANCE.computeAmount(aABB.getCenter().x(), aABB.getCenter().z());
         float coerceIn = amount2 <= rangeXZ ? 1.0F : RangesKt.coerceIn(1.0F - (amount2 - rangeXZ) / rangeXZ, 0.0F, 1.0F);
         return Render2D.INSTANCE.computeColor(color, (float)(coerceIn * (value / 255.0)));
      }
   }

   public static Color defaultImpl(HoleEsp holeEsp, AABB aABB, Color color, int value, int value2, Object object) {
      if ((value2 & 4) != 0) {
         value = color.getAlpha();
      }

      return holeEsp.fetchCenter(aABB, color, value);
   }

   static {
   }

   public static enum Mode {
      Fade,
      CubeOutline,
      CubeFill,
      CubeBoth,
      Shader,
      BLOOM;

      private static final HoleEsp.Mode[] modes = $values();
      private static final EnumEntries<HoleEsp.Mode> enumEntries = EnumEntriesKt.enumEntries(modes);

      private Mode() {
      }

      @NotNull
      public static EnumEntries<HoleEsp.Mode> getEnumEntries() {
         return enumEntries;
      }

      private static final HoleEsp.Mode[] $values() {
         return new HoleEsp.Mode[]{Fade, CubeOutline, CubeFill, CubeBoth, Shader, BLOOM};
      }
   }

   public static final class WhenMappings {
      public static final int[] values;

      static {
         int[] values2 = new int[HoleEsp.Mode.values().length];

         try {
            values2[HoleEsp.Mode.CubeFill.ordinal()] = 1;
         } catch (NoSuchFieldError exception) {
         }

         try {
            values2[HoleEsp.Mode.Shader.ordinal()] = 2;
         } catch (NoSuchFieldError exception2) {
         }

         try {
            values2[HoleEsp.Mode.CubeOutline.ordinal()] = 3;
         } catch (NoSuchFieldError exception3) {
         }

         try {
            values2[HoleEsp.Mode.CubeBoth.ordinal()] = 4;
         } catch (NoSuchFieldError exception4) {
         }

         try {
            values2[HoleEsp.Mode.Fade.ordinal()] = 5;
         } catch (NoSuchFieldError exception5) {
         }

         try {
            values2[HoleEsp.Mode.BLOOM.ordinal()] = 6;
         } catch (NoSuchFieldError exception6) {
         }

         values = values2;
      }
   }
}
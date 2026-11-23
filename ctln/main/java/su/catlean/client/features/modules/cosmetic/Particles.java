// deobf by pusia
package su.catlean.client.features.modules.cosmetic;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import kotlin.NoWhenBranchMatchedException;
import kotlin.TuplesKt;
import kotlin.collections.MapsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.ClosedRange;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import net.minecraft.client.Camera;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import su.catlean.api.enums.ParticleType;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.event.events.render.Render3DEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.ColorSetting;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.api.setting.settings.FloatSetting;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.render.ParticleRenderer;
import su.catlean.client.render.Pipelines;
import su.catlean.client.services.CrystalPredictor;
import su.catlean.client.services.ThemeManager;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.math.MathUtil;
import su.catlean.utils.render.AtlasTexture;
import su.catlean.utils.render.BillboardRenderer;
import su.catlean.utils.render.MeshBuilder;
import su.catlean.utils.render.PoseUtil;
import su.catlean.utils.render.Render2D;
import su.catlean.utils.render.Render3D;

public final class Particles extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(Particles.class, "mode", "getMode()Lsu/catlean/di;", 0), new PropertyReference1Impl(Particles.class, "lmode", "getLmode()Lsu/catlean/client/features/modules/visual/cosmetic/particles/Particles$ColorMode;", 0), new PropertyReference1Impl(Particles.class, "physics", "getPhysics()Lsu/catlean/lw;", 0), new PropertyReference1Impl(Particles.class, "color", "getColor()Ljava/awt/Color;", 0), new PropertyReference1Impl(Particles.class, "amount", "getAmount()I", 0), new PropertyReference1Impl(Particles.class, "lifetime", "getLifetime()I", 0), new PropertyReference1Impl(Particles.class, "size", "getSize()F", 0) };
   @NotNull
   public static final Particles INSTANCE = new Particles();
   @NotNull
   private static final EnumSetting mode = SettingFactory.defaultImpl(INSTANCE, "mode", HitFx.Particle.SNOW_FLAKE, null, null, 12, null);
   @NotNull
   private static final EnumSetting colorMode = SettingFactory.defaultImpl(INSTANCE, "color-mode", Particles.ColorMode.SYNC, null, null, 12, null);
   @NotNull
   private static final EnumSetting physics = SettingFactory.defaultImpl(INSTANCE, "physics", Particles.Physics.FLY, null, null, 12, null);
   @NotNull
   private static final ColorSetting color = SettingFactory.defaultImpl(
      INSTANCE, "color", Color.WHITE, null, () -> INSTANCE.getLmode() == Particles.ColorMode.CUSTOM, 4, null
   );
   @NotNull
   private static final IntSetting amount = SettingFactory.defaultImpl(INSTANCE, "amount", 10, new IntRange(1, 50), null, null, 24, null);
   @NotNull
   private static final IntSetting time = SettingFactory.defaultImpl(INSTANCE, "time", 256, new IntRange(5, 80), null, null, 24, null);
   @NotNull
   private static final FloatSetting size = SettingFactory.defaultImpl(INSTANCE, "size", 1.0F, RangesKt.rangeTo(0.1F, 1.5F), null, null, 24, null);
   @NotNull
   private static final ArrayList<Particles.Engine> items = new ArrayList();
   @NotNull
   private static final ArrayList<Particles.Particle> items2 = new ArrayList();

   private Particles() {
      super("particles", SubCategories.getSubCategory6(), null, 4, null);
   }

   private final HitFx.Particle getMode() {
      return (HitFx.Particle)mode.computeObject(this, $$delegatedProperties[0]);
   }

   private final Particles.ColorMode getLmode() {
      return (Particles.ColorMode)colorMode.computeObject(this, $$delegatedProperties[1]);
   }

   private final Particles.Physics getPhysics() {
      return (Particles.Physics)physics.computeObject(this, $$delegatedProperties[2]);
   }

   private final Color getColor() {
      return (Color)color.computeObject(this, $$delegatedProperties[3]);
   }

   private final int getAmount() {
      return ((Number)amount.computeObject(this, $$delegatedProperties[4])).intValue();
   }

   private final int getLifetime() {
      return ((Number)time.computeObject(this, $$delegatedProperties[5])).intValue();
   }

   private final float getSize() {
      return ((Number)size.computeObject(this, $$delegatedProperties[6])).floatValue();
   }

   @Flow
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      items.removeIf(var0 -> INSTANCE.checkRandom((Particles.Engine)var0));
      items2.removeIf(var0 -> {
         int var4 = ((Particles.Particle)var0).getValue();
         ((Particles.Particle)var0).setValue(var4 + 1);
         return var4 > 15;
      });
      Vec3 var28 = this.computeVec3((Entity)MinecraftHolder.getLocalPlayer()).add(MinecraftHolder.getLocalPlayer().getDeltaMovement().scale(10.0));
      int var29 = this.getAmount();

      for (int var30 = 0; var30 < var29; var30++) {
         Particles.Engine var10000;
         if (INSTANCE.getPhysics() == Particles.Physics.RAIN) {
            var10000 = new Particles.Engine(
               (float)(var28.x + MathUtil.defaultImpl(-48.0F, 48.0F, false, 4, null)),
               (float)(var28.y + 24 + MathUtil.defaultImpl(-2.0F, 2.0F, false, 4, null)),
               (float)(var28.z + MathUtil.defaultImpl(-48.0F, 48.0F, false, 4, null)),
               0.0F,
               MathUtil.defaultImpl(-0.3F, -0.1F, false, 4, null),
               0.0F,
               MathUtil.defaultImpl(0.0F, 360.0F, false, 4, null),
               MathUtil.defaultImpl(3.0F, 10.0F, false, 4, null),
               INSTANCE.getSize()
            );
         } else {
            float var10002 = (float)(var28.x + MathUtil.defaultImpl(-64.0F, 64.0F, false, 4, null) * 2);
            float var10003 = (float)(var28.y + MathUtil.defaultImpl(2.0F, 48.0F, false, 4, null));
            float var10004 = (float)(var28.z + MathUtil.defaultImpl(-64.0F, 64.0F, false, 4, null) * 2);
            float var10005 = switch (Particles.WhenMappings.values[INSTANCE.getPhysics().ordinal()]) {
               case 1 -> 0.0F;
               case 2 -> MathUtil.defaultImpl(-0.4F, 0.4F, false, 4, null);
               default -> MathUtil.defaultImpl(0.1F, 0.25F, false, 4, null);
            };

            float var10006 = switch (Particles.WhenMappings.values[INSTANCE.getPhysics().ordinal()]) {
               case 1 -> MathUtil.defaultImpl(-0.2F, -0.05F, false, 4, null);
               case 2 -> MathUtil.defaultImpl(-0.1F, 0.1F, false, 4, null);
               default -> MathUtil.defaultImpl(-0.4F, -0.1F, false, 4, null);
            };

            float var10007 = switch (Particles.WhenMappings.values[INSTANCE.getPhysics().ordinal()]) {
               case 1 -> 0.0F;
               case 2 -> MathUtil.defaultImpl(-0.4F, 0.4F, false, 4, null);
               default -> MathUtil.defaultImpl(0.1F, 0.25F, false, 4, null);
            };
            float var18 = INSTANCE.getSize();
            float var19 = 0.0F;
            float var20 = 0.0F;
            float var21 = var10007;
            float var22 = var10006;
            float var23 = var10005;
            float var24 = var10004;
            float var25 = var10003;
            float var26 = var10002;
            var10000 = new Particles.Engine(var26, var25, var24, var23, var22, var21, var20, var19, var18);
         }

         Particles.Engine var33 = var10000;
         items.add(var33);
      }
   }

   @Flow
   private final void onRender3D(Render3DEvent render3DEvent) {
      Camera var26 = MinecraftHolder.getMinecraft().gameRenderer.mainCamera();
      MeshBuilder var27 = new MeshBuilder(DefaultVertexFormat.POSITION_TEX_COLOR, items.size() + items2.size(), false, 4, null);
      PoseStack var28 = new PoseStack();
      Quaternionf var29 = new Quaternionf().rotateYXZ(-Render3D.INSTANCE.getAmount(), Render3D.INSTANCE.getAmount3(), 0.0F);
      Iterable<Particles.Engine> var30 = items;

      for (Object var33 : var30) {
         Particles.Engine var34 = (Particles.Engine)var33;
         INSTANCE.render(var34, var28, (Quaternionfc)var29, var27);
      }

      Quaternionf var43 = new Quaternionf().rotateYXZ(-Render3D.INSTANCE.getAmount(), 1.5708F, 0.0F);
      Iterable<Particles.Particle> var44 = items2;

      for (Object var47 : var44) {
         Particles.Particle var48 = (Particles.Particle)var47;
         if (Render3D.INSTANCE.getFrustum().pointInFrustum(var48.getAmount(), var48.getAmount2(), var48.getAmount3())) {
            var28.pushPose();
            var28.translate(var48.getAmount() - var26.position().x, var48.getAmount2() - var26.position().y, var48.getAmount3() - var26.position().z);
            PoseUtil.render(var28, (Quaternionfc)var43);
            Matrix4f var37 = var28.last().pose();
            float var38 = Render2D.INSTANCE.computeAmount((float)Math.max(1, var48.getValue() - 1), (float)var48.getValue());
            float var39 = 1.0F - var38 / 15.0F;
            Color var40 = Render2D.INSTANCE.computeColor(var48.getColor(), var39);
            float var41 = 0.5F + 1.0F * (var38 / 15.0F);
            ParticleType var42 = ParticleType.CIRCLE;
            var28.translate(-var41 / 2.0, -var41 / 2.0, -0.01);
            var27.computeMeshBuilder(var37, 0.0F, var41, 0.0F).computeMeshBuilder(var42.getAmount3(), var42.getAmount2()).fetchAlpha(var40);
            var27.computeMeshBuilder(var37, var41, var41, 0.0F).computeMeshBuilder(var42.getAmount(), var42.getAmount2()).fetchAlpha(var40);
            var27.computeMeshBuilder(var37, var41, 0.0F, 0.0F).computeMeshBuilder(var42.getAmount(), var42.getAmount4()).fetchAlpha(var40);
            var27.computeMeshBuilder(var37, 0.0F, 0.0F, 0.0F).computeMeshBuilder(var42.getAmount3(), var42.getAmount4()).fetchAlpha(var40);
            var28.popPose();
         }
      }

      MeshBuilder.defaultImpl(
         var27,
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
   }

   private final boolean checkRandom(Particles.Engine engine) {
      if (!(MinecraftHolder.getLocalPlayer().distanceToSqr(engine.getAmount(), engine.getAmount2(), engine.getAmount3()) > 4096.0) && !engine.isFlag()) {
         int var22 = engine.getValue();
         engine.setValue(var22 + -1);
      } else {
         engine.setValue(engine.getValue() - 16);
      }

      if (engine.getValue() < 0) {
         return true;
      } else {
         engine.setAmount10(engine.getAmount());
         engine.setAmount11(engine.getAmount2());
         engine.setAmount12(engine.getAmount3());
         engine.setAmount(engine.getAmount() + engine.getAmount4());
         engine.setAmount2(engine.getAmount2() + engine.getAmount5());
         engine.setAmount3(engine.getAmount3() + engine.getAmount6());
         engine.setAmount13(engine.getAmount7());
         engine.setAmount7(engine.getAmount7() + engine.getAmount8());
         if (this.getPhysics() != Particles.Physics.FALLING_STARS) {
            engine.setAmount4(engine.getAmount4() * 0.9F);
            engine.setAmount6(engine.getAmount6() * 0.9F);
         }

         if (this.getPhysics() == Particles.Physics.FLY || this.getPhysics() == Particles.Physics.RAIN && engine.isFlag()) {
            engine.setAmount5(engine.getAmount5() * 0.9F);
         }

         engine.setAmount5(
            engine.getAmount5()
               - (this.getPhysics() == Particles.Physics.RAIN && engine.isFlag() ? 0.08F : (this.getPhysics() == Particles.Physics.RAIN ? 0.02F : 0.001F))
         );
         if (engine.getAmount2() - this.getSize() < engine.getAmount14() && !engine.isFlag() && this.getPhysics() == Particles.Physics.RAIN) {
            engine.setAmount5(-engine.getAmount5() / 4.0F);
            engine.setFlag(true);
            engine.setValue(128);
            engine.setAmount4(MathUtil.defaultImpl(-0.2F, 0.2F, false, 4, null));
            engine.setAmount6(MathUtil.defaultImpl(-0.2F, 0.2F, false, 4, null));
            byte var25 = 5;

            for (int var23 = 0; var23 < var25; var23++) {
               BillboardRenderer.INSTANCE
                  .testParticleRenderer(
                     new ParticleRenderer(
                        ParticleType.FIREFLY,
                        new Vec3(engine.getAmount(), engine.getAmount14(), engine.getAmount3()),
                        0.0F,
                        0.1F,
                        8,
                        new Vec3(Math.random() / 2.0F - 0.25F, Math.random() / 4.0F, Math.random() / 2.0F - 0.25F),
                        ThemeManager.INSTANCE.computeColor(var23 * 10),
                        0.0F,
                        0.0F,
                        false,
                        896,
                        null
                     )
                  );
            }

            engine.setAmount8(MathUtil.defaultImpl2((ClosedRange)RangesKt.rangeTo(-20.0F, 20.0F), false, 2, null));
            items2.add(new Particles.Particle(engine.getAmount(), engine.getAmount14(), engine.getAmount3(), 0, engine.getColor()));
         }

         engine.setFlag2(
            Math.hypot(engine.getAmount() - MinecraftHolder.getLocalPlayer().getX(), engine.getAmount3() - MinecraftHolder.getLocalPlayer().getZ())
               < (this.getPhysics() == Particles.Physics.RAIN ? 1024.0F : 2304.0F)
         );
         return false;
      }
   }

   private final void render(Particles.Engine engine, PoseStack poseStack, Quaternionfc quaternionfc, MeshBuilder meshBuilder) {
      if (engine.isFlag2() && Render3D.INSTANCE.getFrustum().pointInFrustum(engine.getAmount(), engine.getAmount2(), engine.getAmount3())) {
         Camera var30 = MinecraftHolder.getMinecraft().gameRenderer.mainCamera();
         double var31 = Render2D.INSTANCE.computeAmount(engine.getAmount10(), engine.getAmount());
         double var33 = Render2D.INSTANCE.computeAmount(engine.getAmount11(), engine.getAmount2());
         double var35 = Render2D.INSTANCE.computeAmount(engine.getAmount12(), engine.getAmount3());
         poseStack.pushPose();
         poseStack.translate(var31 - var30.position().x, var33 - var30.position().y, var35 - var30.position().z);
         PoseUtil.render(poseStack, quaternionfc);
         if (this.getPhysics() == Particles.Physics.RAIN) {
            PoseUtil.render(poseStack, (Quaternionfc)Axis.ZP.rotationDegrees(Render2D.INSTANCE.computeAmount(engine.getAmount13(), engine.getAmount7())));
         }

         Matrix4f var37 = poseStack.last().pose();
         float var38 = (float)engine.getValue() / engine.getValue2();
         Color var39 = this.getPhysics() == Particles.Physics.RAIN && !engine.isFlag()
            ? engine.getColor()
            : Render2D.INSTANCE.computeColor(engine.getColor(), var38);
         poseStack.translate(-this.getSize() / 2.0, -this.getSize() / 2.0, -0.01);
         ParticleType var40 = this.getMode().getParticleType();
         meshBuilder.computeMeshBuilder(var37, 0.0F, this.getSize(), 0.0F).computeMeshBuilder(var40.getAmount3(), var40.getAmount2()).fetchAlpha(var39);
         meshBuilder.computeMeshBuilder(var37, this.getSize(), this.getSize(), 0.0F)
            .computeMeshBuilder(var40.getAmount(), var40.getAmount2())
            .fetchAlpha(var39);
         meshBuilder.computeMeshBuilder(var37, this.getSize(), 0.0F, 0.0F).computeMeshBuilder(var40.getAmount(), var40.getAmount4()).fetchAlpha(var39);
         meshBuilder.computeMeshBuilder(var37, 0.0F, 0.0F, 0.0F).computeMeshBuilder(var40.getAmount3(), var40.getAmount4()).fetchAlpha(var39);
         poseStack.popPose();
         if (!engine.isFlag() && this.getPhysics() == Particles.Physics.RAIN) {
            float var41 = RangesKt.coerceIn(engine.getValue() / 20.0F, 0.0F, 1.0F);
            Render3D.INSTANCE
               .testVec3(
                  new Vec3(var31, var33, var35),
                  new Vec3(var31, var33 + 3, var35),
                  Render2D.INSTANCE.computeColor(var39, var41 / 3.0F),
                  Render2D.INSTANCE.computeColor(var39, 0.0F)
               );
         }
      }
   }

   public static final Particles.Physics computePhysics(Particles particles) {
      return particles.getPhysics();
   }

   public static final int computeValue(Particles particles) {
      return particles.getLifetime();
   }

   public static final Particles.ColorMode computeColorMode(Particles particles) {
      return particles.getLmode();
   }

   public static final Color computeColor(Particles particles) {
      return particles.getColor();
   }

   static {
   }

   public static enum ColorMode {
      CUSTOM,
      SYNC,
      RGB,
      SKY,
      LIGHT_RGB;

      private static final Particles.ColorMode[] colorModes = $values();
      private static final EnumEntries<Particles.ColorMode> enumEntries = EnumEntriesKt.enumEntries(colorModes);

      private ColorMode() {
      }

      @NotNull
      public static EnumEntries<Particles.ColorMode> getEnumEntries() {
         return enumEntries;
      }

      private static final Particles.ColorMode[] $values() {
         return new Particles.ColorMode[]{CUSTOM, SYNC, RGB, SKY, LIGHT_RGB};
      }
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
      private float amount10;
      private float amount11;
      private float amount12;
      private float amount13;
      private int value;
      private int value2;
      private boolean flag;
      private boolean flag2;
      private int value3;
      @NotNull
      private final Color color;
      private float amount14;

      public Engine(float amount, float amount2, float amount3, float amount4, float amount5, float amount6, float amount7, float amount8, float amount9) {
         this.amount = amount;
         this.amount2 = amount2;
         this.amount3 = amount3;
         this.amount4 = amount4;
         this.amount5 = amount5;
         this.amount6 = amount6;
         this.amount7 = amount7;
         this.amount8 = amount8;
         this.amount9 = amount9;
         this.amount10 = this.amount;
         this.amount11 = this.amount2;
         this.amount12 = this.amount3;
         this.amount13 = this.amount7;
         this.value = Particles.computePhysics(Particles.INSTANCE) == Particles.Physics.RAIN
            ? 80
            : (int)(
               Particles.computeValue(Particles.INSTANCE)
                  + Particles.computeValue(Particles.INSTANCE) * MathUtil.defaultImpl2((ClosedRange)RangesKt.rangeTo(0.0F, 0.5F), false, 2, null)
            );
         this.value2 = this.value;
         this.value3 = (int)(360 * MathUtil.defaultImpl2((ClosedRange)RangesKt.rangeTo(0.0F, 1.0F), false, 2, null));

         float var42;
         label27: {
            this.color = switch (Particles.Engine.WhenMappings.values[Particles.computeColorMode(Particles.INSTANCE).ordinal()]) {
               case 1 -> {
                  Particles var29 = Particles.INSTANCE;
                  yield Particles.computeColor(var29);
               }
               case 2 -> ThemeManager.INSTANCE.computeColor(this.value3);
               case 3 -> Render2D.INSTANCE.fetchHSBColor(18, this.value3);
               case 4 -> Render2D.INSTANCE.computeColor(18, this.value3, 0.6F, 1.0F);
               case 5 -> Render2D.INSTANCE.computeColor(18, this.value3, 1.0F, 1.0F);
               default -> throw new NoWhenBranchMatchedException();
            };
            CrystalPredictor var41 = CrystalPredictor.INSTANCE;
            Vec3 var10002 = new Vec3(this.amount, this.amount2, this.amount3);
            Vec3 var10003 = new Vec3(this.amount, this.amount2 - 128.0, this.amount3);
            Object var40 = null;
            byte var30 = 28;
            Object var31 = null;
            Object var32 = null;
            boolean var33 = false;
            Vec3 var34 = var10003;
            BlockHitResult var37 = CrystalPredictor.defaultImpl(var41, var10002, var34, var33, (List)var32, (List)var31, var30, var40);
            if (var37 != null) {
               BlockPos var38 = var37.getBlockPos();
               if (var38 != null) {
                  BlockPos var39 = var38.above();
                  if (var39 != null) {
                     var42 = var39.getY();
                     break label27;
                  }
               }
            }

            var42 = this.amount2;
         }

         this.amount14 = var42;
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

      public final float getAmount10() {
         return this.amount10;
      }

      public final void setAmount10(float amount) {
         this.amount10 = amount;
      }

      public final float getAmount11() {
         return this.amount11;
      }

      public final void setAmount11(float amount) {
         this.amount11 = amount;
      }

      public final float getAmount12() {
         return this.amount12;
      }

      public final void setAmount12(float amount) {
         this.amount12 = amount;
      }

      public final float getAmount13() {
         return this.amount13;
      }

      public final void setAmount13(float amount) {
         this.amount13 = amount;
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

      public final boolean isFlag() {
         return this.flag;
      }

      public final void setFlag(boolean flag) {
         this.flag = flag;
      }

      public final boolean isFlag2() {
         return this.flag2;
      }

      public final void setFlag2(boolean flag) {
         this.flag2 = flag;
      }

      public final int getValue3() {
         return this.value3;
      }

      public final void setValue3(int value) {
         this.value3 = value;
      }

      @NotNull
      public final Color getColor() {
         return this.color;
      }

      public final float getAmount14() {
         return this.amount14;
      }

      public final void setAmount14(float amount) {
         this.amount14 = amount;
      }

      public static final class WhenMappings {
         public static final int[] values;

         static {
            int[] var0 = new int[Particles.ColorMode.values().length];

            try {
               var0[Particles.ColorMode.CUSTOM.ordinal()] = 1;
            } catch (NoSuchFieldError var6) {
            }

            try {
               var0[Particles.ColorMode.SYNC.ordinal()] = 2;
            } catch (NoSuchFieldError var5) {
            }

            try {
               var0[Particles.ColorMode.SKY.ordinal()] = 3;
            } catch (NoSuchFieldError var4) {
            }

            try {
               var0[Particles.ColorMode.LIGHT_RGB.ordinal()] = 4;
            } catch (NoSuchFieldError var3) {
            }

            try {
               var0[Particles.ColorMode.RGB.ordinal()] = 5;
            } catch (NoSuchFieldError var2) {
            }

            values = var0;
         }
      }
   }

   public static final class Particle {
      private final float amount;
      private final float amount2;
      private final float amount3;
      private int value;
      @NotNull
      private final Color color;

      public Particle(float amount, float amount2, float amount3, int value, Color color) {
         this.amount = amount;
         this.amount2 = amount2;
         this.amount3 = amount3;
         this.value = value;
         this.color = color;
      }

      public final float getAmount() {
         return this.amount;
      }

      public final float getAmount2() {
         return this.amount2;
      }

      public final float getAmount3() {
         return this.amount3;
      }

      public final int getValue() {
         return this.value;
      }

      public final void setValue(int value) {
         this.value = value;
      }

      @NotNull
      public final Color getColor() {
         return this.color;
      }
   }

   public static enum Physics {
      DROP,
      FLY,
      FALLING_STARS,
      RAIN;

      private static final Particles.Physics[] physicss = $values();
      private static final EnumEntries<Particles.Physics> enumEntries = EnumEntriesKt.enumEntries(physicss);

      private Physics() {
      }

      @NotNull
      public static EnumEntries<Particles.Physics> getEnumEntries() {
         return enumEntries;
      }

      private static final Particles.Physics[] $values() {
         return new Particles.Physics[]{DROP, FLY, FALLING_STARS, RAIN};
      }
   }

   public static final class WhenMappings {
      public static final int[] values;

      static {
         int[] var0 = new int[Particles.Physics.values().length];

         try {
            var0[Particles.Physics.DROP.ordinal()] = 1;
         } catch (NoSuchFieldError var3) {
         }

         try {
            var0[Particles.Physics.FLY.ordinal()] = 2;
         } catch (NoSuchFieldError var2) {
         }

         values = var0;
      }
   }
}
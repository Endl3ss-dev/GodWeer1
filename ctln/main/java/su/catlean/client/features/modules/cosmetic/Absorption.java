// deobf by pusia
package su.catlean.client.features.modules.cosmetic;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import kotlin.NoWhenBranchMatchedException;
import kotlin.TuplesKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaternionfc;
import su.catlean.api.enums.ParticleType;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.event.events.render.Render3DEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.ColorSetting;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.render.Pipelines;
import su.catlean.client.services.ThemeManager;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.render.AtlasTexture;
import su.catlean.utils.render.MeshBuilder;
import su.catlean.utils.render.PoseUtil;
import su.catlean.utils.render.Render2D;

public final class Absorption extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl( Absorption.class, "lmode", "getLmode()Lsu/catlean/client/features/modules/visual/cosmetic/particles/Particles$ColorMode;", 0 ), new PropertyReference1Impl(Absorption.class, "color", "getColor()Ljava/awt/Color;", 0) };
   @NotNull
   public static final Absorption INSTANCE = new Absorption();
   @NotNull
   private static final EnumSetting colorMode = SettingFactory.defaultImpl(INSTANCE, "color-mode", Particles.ColorMode.CUSTOM, null, null, 12, null);
   @NotNull
   private static final ColorSetting color = SettingFactory.defaultImpl(
      INSTANCE, "color", Color.ORANGE, null, () -> INSTANCE.getLmode() == Particles.ColorMode.CUSTOM, 4, null
   );
   private static final ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
   @NotNull
   private static final List items = new ArrayList();

   private Absorption() {
      super("absorption", SubCategories.getSubCategory6(), null, 4, null);
   }

   private final Particles.ColorMode getLmode() {
      return (Particles.ColorMode)colorMode.computeObject(this, $$delegatedProperties[0]);
   }

   private final Color getColor() {
      return (Color)color.computeObject(this, $$delegatedProperties[1]);
   }

   @Flow
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      for (Object item : items) {
         Absorption.Sphere sphere = (Absorption.Sphere)item;
         Iterator iterator = sphere.getParticles().iterator();

         while (iterator.hasNext()) {
            Absorption.Particle3D particle3D = (Absorption.Particle3D)iterator.next();
            int value2 = particle3D.getValue2();
            particle3D.setValue2(value2 + 1);
            particle3D.setAmount2(particle3D.getAmount());
            particle3D.setAmount(particle3D.getAmount() + particle3D.getAmount7());
            particle3D.setAmount6(particle3D.getAmount5());
            if (particle3D.isFlag()) {
               particle3D.setAmount5(particle3D.getAmount5() + 0.2F);
               if (particle3D.getAmount5() >= 1.0F) {
                  particle3D.setAmount5(1.0F);
                  if (threadLocalRandom.nextInt(1000) < 5) {
                     particle3D.setFlag(false);
                  }
               }
            } else {
               particle3D.setAmount5(particle3D.getAmount5() - 0.2F);
               if (particle3D.getAmount5() <= 0.0F) {
                  iterator.remove();
                  continue;
               }
            }

            if (particle3D.getValue2() > particle3D.getValue()) {
               particle3D.setFlag(false);
            }
         }
      }
   }

   @Flow
   private final void onRender3D(Render3DEvent render3DEvent) {
      Absorption.Sphere var114 = null;
      Camera mainCamera = MinecraftHolder.getMinecraft().gameRenderer.mainCamera();
      ClientLevel level = MinecraftHolder.getMinecraft().level;
      if (level != null) {
         List<AbstractClientPlayer> players = level.players();
         if (players != null) {
            for (AbstractClientPlayer abstractClientPlayer : (Iterable<AbstractClientPlayer>)players) {
               if (!Intrinsics.areEqual(abstractClientPlayer, MinecraftHolder.getMinecraft().player)) {
                  float coerceIn = RangesKt.coerceIn(abstractClientPlayer.getAbsorptionAmount() / 16.0F, 0.0F, 1.0F);
                  int value = (int)(60 * coerceIn);
                  Iterable items2 = items;
                  int value2 = 0;

                  label87: {
                     for (Object object : items2) {
                        Absorption.Sphere sphere = (Absorption.Sphere)object;
                        if (sphere.getValue() == abstractClientPlayer.getId()) {
                           var114 = (Absorption.Sphere)object;
                           break label87;
                        }
                     }

                     var114 = null;
                  }

                  Absorption.Sphere sphere2 = (Absorption.Sphere)var114;
                  if (sphere2 == null) {
                     List<Absorption.Particle3D> arrayList = new ArrayList();

                     for (int j = 0; j < value; j++) {
                        arrayList.add(
                           new Absorption.Particle3D(
                              threadLocalRandom.nextFloat() * 360.0F,
                              0.0F,
                              threadLocalRandom.nextFloat() * 360.0F,
                              1.0F + threadLocalRandom.nextFloat() * 0.2F,
                              0.0F,
                              0.0F,
                              false,
                              0.0F,
                              0,
                              0,
                              1010,
                              null
                           )
                        );
                     }

                     Collection<Absorption.Sphere> items3 = items;
                     items3.add(new Absorption.Sphere(abstractClientPlayer.getId(), arrayList));
                  } else if (sphere2.getParticles().size() < value) {
                     Collection<Absorption.Particle3D> particles = sphere2.getParticles();
                     particles.add(
                        new Absorption.Particle3D(
                           threadLocalRandom.nextFloat() * 360.0F,
                           0.0F,
                           threadLocalRandom.nextFloat() * 360.0F,
                           1.0F + threadLocalRandom.nextFloat() * 0.2F,
                           0.0F,
                           0.0F,
                           false,
                           0.0F,
                           0,
                           0,
                           1010,
                           null
                        )
                     );
                  } else if (sphere2.getParticles().size() > value) {
                     int size = sphere2.getParticles().size() - value;
                     value2 = 0;
                     Iterable particles2 = sphere2.getParticles();

                     for (Object object2 : particles2) {
                        Absorption.Particle3D particle3D = (Absorption.Particle3D)object2;
                        if (value2 < size) {
                           particle3D.setValue2(particle3D.getValue2() + 5);
                           value2++;
                        }
                     }
                  }
               }
            }

            render3DEvent.getStack().pushPose();
            PoseUtil.render(render3DEvent.getStack(), (Quaternionfc)Axis.YP.rotationDegrees(-mainCamera.yRot() + 180.0F));
            PoseUtil.render(render3DEvent.getStack(), (Quaternionfc)Axis.XP.rotationDegrees(-mainCamera.xRot()));
            int value3 = 0;
            Iterable items4 = items;

            for (Object object3 : items4) {
               Absorption.Sphere sphere3 = (Absorption.Sphere)object3;
               value3 += sphere3.getParticles().size();
            }

            MeshBuilder meshBuilder = new MeshBuilder(DefaultVertexFormat.POSITION_TEX_COLOR, value3, false, 4, null);

            for (Absorption.Sphere item : (Iterable<su.catlean.client.features.modules.cosmetic.Absorption.Sphere>)items) {
               Entity entity = MinecraftHolder.getClientLevel().getEntity(item.getValue());
               if (entity != null) {
                  Entity entity2 = entity;
                  double amount = Render2D.INSTANCE.computeAmount(entity2.xo, entity2.getX());
                  double amount2 = Render2D.INSTANCE.computeAmount(entity2.yo, entity2.getY()) + 1.0;
                  double amount3 = Render2D.INSTANCE.computeAmount(entity2.zo, entity2.getZ());

                  for (Absorption.Particle3D particle : (Iterable<su.catlean.client.features.modules.cosmetic.Absorption.Particle3D>)item.getParticles()) {
                     double radians = Math.toRadians(Render2D.INSTANCE.computeAmount(particle.getAmount2(), particle.getAmount()));
                     double radians2 = Math.toRadians(particle.getAmount3());
                     double amount4 = amount + Math.cos(radians) * particle.getAmount4() * Math.cos(radians2);
                     double amount42 = amount2 + Math.sin(radians2) * particle.getAmount4();
                     double amount43 = amount3 + Math.sin(radians) * particle.getAmount4() * Math.cos(radians2);
                     double value4 = amount4 - amount;
                     double value5 = amount42 - amount2;
                     double value6 = amount43 - amount3;
                     float value7 = (float)(-Math.toDegrees(Math.atan2(value6, value4))) + 90.0F;
                     double value8 = Math.sqrt(value4 * value4 + value6 * value6);
                     float degrees = (float)Math.toDegrees(Math.atan2(value5, value8));

                     Color color2 = switch (Absorption.WhenMappings.values[this.getLmode().ordinal()]) {
                        case 1 -> this.getColor();
                        case 2 -> ThemeManager.INSTANCE.computeColor(particle.getValue2());
                        case 3 -> Render2D.INSTANCE.fetchHSBColor(18, particle.getValue2());
                        case 4 -> Render2D.INSTANCE.computeColor(18, particle.getValue2(), 0.6F, 1.0F);
                        case 5 -> Render2D.INSTANCE.computeColor(18, particle.getValue2(), 1.0F, 1.0F);
                        default -> throw new NoWhenBranchMatchedException();
                     };
                     float amount5 = Render2D.INSTANCE.computeAmount(particle.getAmount6(), particle.getAmount5());
                     float value9 = 0.4F * amount5;
                     Color color3 = Render2D.INSTANCE.computeColor(color2, amount5);
                     PoseStack poseStack = new PoseStack();
                     poseStack.pushPose();
                     PoseUtil.render(poseStack, (Quaternionfc)Axis.XP.rotationDegrees(mainCamera.xRot()));
                     PoseUtil.render(poseStack, (Quaternionfc)Axis.YP.rotationDegrees(mainCamera.yRot() + 180.0F));
                     poseStack.translate(amount4 - mainCamera.position().x, amount42 - mainCamera.position().y, amount43 - mainCamera.position().z);
                     PoseUtil.render(poseStack, (Quaternionfc)Axis.YP.rotationDegrees(value7));
                     PoseUtil.render(poseStack, (Quaternionfc)Axis.XP.rotationDegrees(-degrees));
                     ParticleType heart = ParticleType.HEART;
                     poseStack.translate(-value9 / 2.0, -value9 / 2.0, 0.0);
                     Matrix4f pose = poseStack.last().pose();
                     meshBuilder.computeMeshBuilder(pose, 0.0F, value9, 0.0F).computeMeshBuilder(heart.getAmount3(), heart.getAmount2()).fetchAlpha(color3);
                     meshBuilder.computeMeshBuilder(pose, value9, value9, 0.0F).computeMeshBuilder(heart.getAmount(), heart.getAmount2()).fetchAlpha(color3);
                     meshBuilder.computeMeshBuilder(pose, value9, 0.0F, 0.0F).computeMeshBuilder(heart.getAmount(), heart.getAmount4()).fetchAlpha(color3);
                     meshBuilder.computeMeshBuilder(pose, 0.0F, 0.0F, 0.0F).computeMeshBuilder(heart.getAmount3(), heart.getAmount4()).fetchAlpha(color3);
                     poseStack.popPose();
                  }
               }
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
            return;
         }
      }
   }

   static {
   }

   public static final class Particle3D {
      private float amount;
      private float amount2;
      private float amount3;
      private final float amount4;
      private float amount5;
      private float amount6;
      private boolean flag;
      private final float amount7;
      private final int value;
      private int value2;

      public Particle3D(
         float amount, float amount2, float amount3, float amount4, float amount5, float amount6, boolean flag, float amount7, int value, int value2
      ) {
         this.amount = amount;
         this.amount2 = amount2;
         this.amount3 = amount3;
         this.amount4 = amount4;
         this.amount5 = amount5;
         this.amount6 = amount6;
         this.flag = flag;
         this.amount7 = amount7;
         this.value = value;
         this.value2 = value2;
      }

      public Particle3D(
         float amount,
         float amount2,
         float amount3,
         float amount4,
         float amount5,
         float amount6,
         boolean flag,
         float amount7,
         int value,
         int value2,
         int value3,
         DefaultConstructorMarker defaultConstructorMarker
      ) {
         if ((value3 & 2) != 0) {
            amount2 = amount;
         }

         if ((value3 & 16) != 0) {
            amount5 = 0.0F;
         }

         if ((value3 & 32) != 0) {
            amount6 = 0.0F;
         }

         if ((value3 & 64) != 0) {
            flag = true;
         }

         if ((value3 & 128) != 0) {
            amount7 = 1.5F + ThreadLocalRandom.current().nextFloat() * 1.0F;
         }

         if ((value3 & 256) != 0) {
            value = ThreadLocalRandom.current().nextInt(15, 35);
         }

         if ((value3 & 512) != 0) {
            value2 = 0;
         }

         this(amount, amount2, amount3, amount4, amount5, amount6, flag, amount7, value, value2);
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

      public final boolean isFlag() {
         return this.flag;
      }

      public final void setFlag(boolean flag) {
         this.flag = flag;
      }

      public final float getAmount7() {
         return this.amount7;
      }

      public final int getValue() {
         return this.value;
      }

      public final int getValue2() {
         return this.value2;
      }

      public final void setValue2(int value) {
         this.value2 = value;
      }

      public final float getAmount8() {
         return this.amount;
      }

      public final float getAmount9() {
         return this.amount2;
      }

      public final float getAmount10() {
         return this.amount3;
      }

      public final float getAmount11() {
         return this.amount4;
      }

      public final float getAmount12() {
         return this.amount5;
      }

      public final float getAmount13() {
         return this.amount6;
      }

      public final boolean isFlag2() {
         return this.flag;
      }

      public final float getAmount14() {
         return this.amount7;
      }

      public final int getValue3() {
         return this.value;
      }

      public final int getValue4() {
         return this.value2;
      }

      @NotNull
      public final Absorption.Particle3D computeParticle3D(
         float amount, float amount2, float amount3, float amount4, float amount5, float amount6, boolean flag, float amount7, int value, int value2
      ) {
         return new Absorption.Particle3D(amount, amount2, amount3, amount4, amount5, amount6, flag, amount7, value, value2);
      }

      @NotNull
      @Override
      public String toString() {
         int value22 = this.value2;
         int value3 = this.value;
         float amount72 = this.amount7;
         boolean flag2 = this.flag;
         float amount62 = this.amount6;
         float amount52 = this.amount5;
         float amount42 = this.amount4;
         float amount32 = this.amount3;
         float amount22 = this.amount2;
         float amount8 = this.amount;
         return "Particle3D(yaw="
            + amount8
            + ", prevYaw="
            + amount22
            + ", pitch="
            + amount32
            + ", radius="
            + amount42
            + ", alpha="
            + amount52
            + ", prevAlpha="
            + amount62
            + ", fadeIn="
            + flag2
            + ", spinSpeed="
            + amount72
            + ", lifeTime="
            + value3
            + ", age="
            + value22
            + ")";
      }

      @Override
      public int hashCode() {
         int hashCode = Float.hashCode(this.amount);
         hashCode = hashCode * 31 + Float.hashCode(this.amount2);
         hashCode = hashCode * 31 + Float.hashCode(this.amount3);
         hashCode = hashCode * 31 + Float.hashCode(this.amount4);
         hashCode = hashCode * 31 + Float.hashCode(this.amount5);
         hashCode = hashCode * 31 + Float.hashCode(this.amount6);
         hashCode = hashCode * 31 + Boolean.hashCode(this.flag);
         hashCode = hashCode * 31 + Float.hashCode(this.amount7);
         hashCode = hashCode * 31 + Integer.hashCode(this.value);
         return hashCode * 31 + Integer.hashCode(this.value2);
      }

      @Override
      public boolean equals(Object object) {
         if (this == object) {
            return true;
         } else if (!(object instanceof Absorption.Particle3D particle3D)) {
            return false;
         } else if (Float.compare(this.amount, particle3D.amount) != 0) {
            return false;
         } else if (Float.compare(this.amount2, particle3D.amount2) != 0) {
            return false;
         } else if (Float.compare(this.amount3, particle3D.amount3) != 0) {
            return false;
         } else if (Float.compare(this.amount4, particle3D.amount4) != 0) {
            return false;
         } else if (Float.compare(this.amount5, particle3D.amount5) != 0) {
            return false;
         } else if (Float.compare(this.amount6, particle3D.amount6) != 0) {
            return false;
         } else if (this.flag != particle3D.flag) {
            return false;
         } else if (Float.compare(this.amount7, particle3D.amount7) != 0) {
            return false;
         } else {
            return this.value != particle3D.value ? false : this.value2 == particle3D.value2;
         }
      }
   }

   public static final class Sphere {
      private final int value;
      @NotNull
      private final List particles;

      public Sphere(int value, List particles) {
         this.value = value;
         this.particles = particles;
      }

      public final int getValue() {
         return this.value;
      }

      @NotNull
      public final List getParticles() {
         return this.particles;
      }

      public final int getValue2() {
         return this.value;
      }

      @NotNull
      public final List getParticles2() {
         return this.particles;
      }

      @NotNull
      @Override
      public String toString() {
         return "Sphere(playerId=" + this.value + ", particles=" + this.particles + ")";
      }

      @Override
      public int hashCode() {
         int hashCode = Integer.hashCode(this.value);
         return hashCode * 31 + this.particles.hashCode();
      }

      @Override
      public boolean equals(Object object) {
         if (this == object) {
            return true;
         } else if (!(object instanceof Absorption.Sphere sphere)) {
            return false;
         } else {
            return this.value != sphere.value ? false : Intrinsics.areEqual(this.particles, sphere.particles);
         }
      }
   }

   public static final class WhenMappings {
      public static final int[] values;

      static {
         int[] values2 = new int[Particles.ColorMode.values().length];

         try {
            values2[Particles.ColorMode.CUSTOM.ordinal()] = 1;
         } catch (NoSuchFieldError exception) {
         }

         try {
            values2[Particles.ColorMode.SYNC.ordinal()] = 2;
         } catch (NoSuchFieldError exception2) {
         }

         try {
            values2[Particles.ColorMode.SKY.ordinal()] = 3;
         } catch (NoSuchFieldError exception3) {
         }

         try {
            values2[Particles.ColorMode.LIGHT_RGB.ordinal()] = 4;
         } catch (NoSuchFieldError exception4) {
         }

         try {
            values2[Particles.ColorMode.RGB.ordinal()] = 5;
         } catch (NoSuchFieldError exception5) {
         }

         values = values2;
      }
   }
}
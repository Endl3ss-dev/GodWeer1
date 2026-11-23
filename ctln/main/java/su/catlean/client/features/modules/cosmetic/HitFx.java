// deobf by pusia
package su.catlean.client.features.modules.cosmetic;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import java.util.ArrayList;
import java.util.List;
import kotlin.TuplesKt;
import kotlin.collections.MapsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.MutablePropertyReference1Impl;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import net.minecraft.network.protocol.game.ServerboundAttackPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.Rotation;
import su.catlean.api.enums.ParticleType;
import su.catlean.api.event.events.network.SendPacket;
import su.catlean.api.event.events.render.Render3DEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.api.setting.settings.FloatSetting;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.features.modules.attack.Aura;
import su.catlean.client.features.modules.attack.Criticals;
import su.catlean.client.features.modules.esp.TrailPoint;
import su.catlean.client.render.ParticleRenderer;
import su.catlean.client.render.Pipelines;
import su.catlean.client.services.RotationManager;
import su.catlean.client.services.ThemeManager;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.StopWatch;
import su.catlean.utils.math.MathUtil;
import su.catlean.utils.render.AtlasTexture;
import su.catlean.utils.render.BillboardRenderer;
import su.catlean.utils.render.MeshBuilder;
import su.catlean.utils.render.Render2D;
import su.catlean.utils.render.Render3D;
import su.catlean.utils.world.WorldUtil;

public final class HitFx extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new MutablePropertyReference1Impl(HitFx.class, "smoke", "getSmoke()Z", 0), new MutablePropertyReference1Impl(HitFx.class, "particles", "getParticles()Z", 0), new MutablePropertyReference1Impl(HitFx.class, "particle", "getParticle()Lsu/catlean/di;", 0), new MutablePropertyReference1Impl(HitFx.class, "size", "getSize()F", 0), new MutablePropertyReference1Impl(HitFx.class, "duration", "getDuration()I", 0), new MutablePropertyReference1Impl(HitFx.class, "count", "getCount()I", 0), new MutablePropertyReference1Impl(HitFx.class, "gravity", "getGravity()F", 0), new MutablePropertyReference1Impl(HitFx.class, "velocityXZ", "getVelocityXZ()F", 0), new MutablePropertyReference1Impl(HitFx.class, "velocityY", "getVelocityY()F", 0) };
   @NotNull
   public static final HitFx INSTANCE = new HitFx();
   @NotNull
   private static final BooleanSetting smoke = SettingFactory.defaultImpl(INSTANCE, "smoke", true, null, null, 12, null);
   @NotNull
   private static final BooleanSetting particles = SettingFactory.defaultImpl(INSTANCE, "particles", true, null, null, 12, null);
   @NotNull
   private static final EnumSetting particle = SettingFactory.defaultImpl(
      INSTANCE, "particle", HitFx.Particle.HEART, null, () -> INSTANCE.getParticles(), 4, null
   );
   @NotNull
   private static final FloatSetting size = SettingFactory.defaultImpl(
      INSTANCE, "size", 0.2F, RangesKt.rangeTo(0.01F, 0.5F), null, () -> INSTANCE.getParticles(), 8, null
   );
   @NotNull
   private static final IntSetting duration = SettingFactory.defaultImpl(
      INSTANCE, "duration", 15, new IntRange(5, 60), null, () -> INSTANCE.getParticles(), 8, null
   );
   @NotNull
   private static final IntSetting count = SettingFactory.defaultImpl(INSTANCE, "count", 15, new IntRange(5, 60), null, () -> INSTANCE.getParticles(), 8, null);
   @NotNull
   private static final FloatSetting gravity = SettingFactory.defaultImpl(
      INSTANCE, "gravity", 0.03F, RangesKt.rangeTo(-0.15F, 0.15F), null, () -> INSTANCE.getParticles(), 8, null
   );
   @NotNull
   private static final FloatSetting horizontalMotion = SettingFactory.defaultImpl(
      INSTANCE, "horizontal-motion", 0.25F, RangesKt.rangeTo(0.01F, 2.0F), null, () -> INSTANCE.getParticles(), 8, null
   );
   @NotNull
   private static final FloatSetting verticalMotion = SettingFactory.defaultImpl(
      INSTANCE, "vertical-motion", 0.5F, RangesKt.rangeTo(0.01F, 2.0F), null, () -> INSTANCE.getParticles(), 8, null
   );
   @NotNull
   private static final List<TrailPoint> items = new ArrayList();

   private HitFx() {
      super("hit-fx", SubCategories.getSubCategory6(), null, 4, null);
   }

   private final boolean getSmoke() {
      return (Boolean)smoke.computeObject(this, $$delegatedProperties[0]);
   }

   private final boolean getParticles() {
      return (Boolean)particles.computeObject(this, $$delegatedProperties[1]);
   }

   private final HitFx.Particle getParticle() {
      return (HitFx.Particle)particle.computeObject(this, $$delegatedProperties[2]);
   }

   private final float getSize() {
      return ((Number)size.computeObject(this, $$delegatedProperties[3])).floatValue();
   }

   private final int getDuration() {
      return ((Number)duration.computeObject(this, $$delegatedProperties[4])).intValue();
   }

   private final int getCount() {
      return ((Number)count.computeObject(this, $$delegatedProperties[5])).intValue();
   }

   private final float getGravity() {
      return ((Number)gravity.computeObject(this, $$delegatedProperties[6])).floatValue();
   }

   private final float getVelocityXZ() {
      return ((Number)horizontalMotion.computeObject(this, $$delegatedProperties[7])).floatValue();
   }

   private final float getVelocityY() {
      return ((Number)verticalMotion.computeObject(this, $$delegatedProperties[8])).floatValue();
   }

   @Flow
   private final void onSendPacket(SendPacket sendPacket) {
      MinecraftHolder.getMinecraft()
         .executeIfPossible(
            () -> {
               if (sendPacket.getPacket() instanceof ServerboundAttackPacket) {
                  Vec3 vec3;
                  Vec3 object = null;
                  vec3 = WorldUtil.INSTANCE.computeVec3(WorldUtil.INSTANCE.getAmount4(), WorldUtil.INSTANCE.getAmount5(), Aura.INSTANCE.getAttackRange());
                  label50:
                  if (vec3 == null) {
                     Entity entity = Criticals.INSTANCE.fetchEntity((ServerboundAttackPacket)sendPacket.getPacket());
                     if (entity != null) {
                        AABB boundingBox = entity.getBoundingBox();
                        if (boundingBox != null) {
                           vec3 = boundingBox.getCenter();
                           break label50;
                        }
                     }

                     vec3 = null;
                  }

                  object = vec3;
                  if (object == null) {
                     Rotation defaultImpl = RotationManager.defaultImpl(RotationManager.INSTANCE, false, 1, null);
                     vec3 = WorldUtil.INSTANCE.computeVec3(defaultImpl.getAmount(), defaultImpl.getAmount2(), Aura.INSTANCE.getAttackRange());
                     label42:
                     if (vec3 == null) {
                        Entity entity2 = Criticals.INSTANCE.fetchEntity((ServerboundAttackPacket)sendPacket.getPacket());
                        if (entity2 != null) {
                           AABB boundingBox2 = entity2.getBoundingBox();
                           if (boundingBox2 != null) {
                              vec3 = boundingBox2.getCenter();
                              break label42;
                           }
                        }

                        vec3 = null;
                     }

                     object = vec3;
                  }

                  if (object != null) {
                     if (INSTANCE.getSmoke()) {
                        items.add(new TrailPoint((Vec3)object, -WorldUtil.INSTANCE.getAmount4(), WorldUtil.INSTANCE.getAmount5(), new StopWatch()));
                     }

                     if (INSTANCE.getParticles()) {
                        int count2 = INSTANCE.getCount();

                        for (int i = 0; i < count2; i++) {
                           BillboardRenderer.INSTANCE
                              .testParticleRenderer(
                                 new ParticleRenderer(
                                    INSTANCE.getParticle().getParticleType(),
                                    (Vec3)object,
                                    (float)(Math.random() * 50.0F),
                                    INSTANCE.getSize(),
                                    INSTANCE.getDuration(),
                                    new Vec3(
                                       MathUtil.defaultImpl(-INSTANCE.getVelocityXZ(), INSTANCE.getVelocityXZ(), false, 4, null),
                                       MathUtil.defaultImpl(-INSTANCE.getVelocityY(), INSTANCE.getVelocityY(), false, 4, null),
                                       MathUtil.defaultImpl(-INSTANCE.getVelocityXZ(), INSTANCE.getVelocityXZ(), false, 4, null)
                                    ),
                                    null,
                                    INSTANCE.getGravity(),
                                    0.0F,
                                    false,
                                    832,
                                    null
                                 )
                              );
                        }
                     }
                  }
               }
            }
         );
   }

   @Flow
   private final void onRender3D(Render3DEvent render3DEvent) {
      ArrayList<TrailPoint> arrayList = Lists.newArrayList(items);
      MeshBuilder meshBuilder = new MeshBuilder(DefaultVertexFormat.POSITION_TEX_COLOR, arrayList.size() * 2, false, 4, null);

      for (Object object : arrayList) {
         TrailPoint trailPoint = (TrailPoint)object;
         int coerceIn = RangesKt.coerceIn((int)((float)trailPoint.getLife().getTime() / 40.0F), 0, 8);
         float amount3 = (ParticleType.SMOKE.getAmount3() - ParticleType.SMOKE.getAmount()) / 3.0F;
         float amount4 = (ParticleType.SMOKE.getAmount4() - ParticleType.SMOKE.getAmount2()) / 3.0F;
         float amount = ParticleType.SMOKE.getAmount() + coerceIn % 3 * amount3;
         float amount2 = ParticleType.SMOKE.getAmount2() + coerceIn / 3 * amount4;
         float time = (float)trailPoint.getLife().getTime() / 450.0F;
         Render3D.defaultImpl(
            Render3D.INSTANCE,
            AtlasTexture.getCatlean(),
            trailPoint.getVec(),
            trailPoint.getAmount(),
            trailPoint.getAmount2(),
            0.0F,
            1.0F + time,
            Render2D.INSTANCE.computeColor(ThemeManager.INSTANCE.computeColor(10), 1.0F),
            false,
            meshBuilder,
            amount,
            amount2,
            amount + amount3,
            amount2 + amount4,
            true,
            128,
            null
         );
      }

      GpuTextureView textureView = MinecraftHolder.getMinecraft().getTextureManager().getTexture(AtlasTexture.getCatlean()).getTextureView();
      MeshBuilder.defaultImpl(
         meshBuilder,
         Pipelines.INSTANCE.getRenderPipeline18(),
         MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget(),
         null,
         render3DEvent.getStack().last().pose(),
         null,
         MapsKt.mapOf(TuplesKt.to("Sampler0", textureView)),
         null,
         84,
         null
      );
      items.removeIf(arg -> ((TrailPoint)arg).getLife().testValue(360));
   }

   static {
   }

   public static enum Particle {
      STAR(ParticleType.STAR),
      FIRE_FLY(ParticleType.FIREFLY),
      DOLLAR(ParticleType.DOLLAR),
      HEART(ParticleType.HEART),
      SNOW_FLAKE(ParticleType.SNOWFLAKE),
      DROP(ParticleType.DROP),
      CLOUD(ParticleType.CLOUD);

      @NotNull
      private final ParticleType particleType;
      private static final HitFx.Particle[] particles = $values();
      private static final EnumEntries<HitFx.Particle> enumEntries = EnumEntriesKt.enumEntries(particles);

      private Particle(ParticleType particleType) {
         this.particleType = particleType;
      }

      @NotNull
      public final ParticleType getParticleType() {
         return this.particleType;
      }

      @NotNull
      public static EnumEntries<HitFx.Particle> getEnumEntries() {
         return enumEntries;
      }

      private static final HitFx.Particle[] $values() {
         return new HitFx.Particle[]{STAR, FIRE_FLY, DOLLAR, HEART, SNOW_FLAKE, DROP, CLOUD};
      }
   }
}
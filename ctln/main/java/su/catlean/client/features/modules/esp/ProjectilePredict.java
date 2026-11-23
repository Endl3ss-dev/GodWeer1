// deobf by pusia
package su.catlean.client.features.modules.esp;

import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import kotlin.TuplesKt;
import kotlin.collections.MapsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import net.minecraft.client.Camera;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.entity.projectile.hurtingprojectile.windcharge.WindCharge;
import net.minecraft.world.entity.projectile.throwableitemprojectile.AbstractThrownPotion;
import net.minecraft.world.entity.projectile.throwableitemprojectile.Snowball;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEgg;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEnderpearl;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionfc;
import su.catlean.api.enums.ParticleType;
import su.catlean.api.event.events.render.Render3DEvent;
import su.catlean.api.event.events.world.EntityRemove;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.api.setting.settings.GroupSetting;
import su.catlean.api.setting.values.GroupValue;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.PlaceModule;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.render.ParticleRenderer;
import su.catlean.client.render.Pipelines;
import su.catlean.client.render.WorldRenderer;
import su.catlean.client.services.ThemeManager;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.render.AtlasTexture;
import su.catlean.utils.render.BillboardRenderer;
import su.catlean.utils.render.MeshBuilder;
import su.catlean.utils.render.Render2D;
import su.catlean.utils.render.Render3D;

public final class ProjectilePredict extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(ProjectilePredict.class, "group", "getGroup()Lsu/catlean/nl;", 0), new PropertyReference1Impl(ProjectilePredict.class, "pearls", "getPearls()Z", 0), new PropertyReference1Impl(ProjectilePredict.class, "arrows", "getArrows()Z", 0), new PropertyReference1Impl(ProjectilePredict.class, "tridents", "getTridents()Z", 0), new PropertyReference1Impl(ProjectilePredict.class, "eggs", "getEggs()Z", 0), new PropertyReference1Impl(ProjectilePredict.class, "windcharges", "getWindcharges()Z", 0), new PropertyReference1Impl(ProjectilePredict.class, "snowBalls", "getSnowBalls()Z", 0), new PropertyReference1Impl(ProjectilePredict.class, "potions", "getPotions()Z", 0), new PropertyReference1Impl(ProjectilePredict.class, "drawCircle", "getDrawCircle()Z", 0), new PropertyReference1Impl(ProjectilePredict.class, "items", "getItems()Z", 0), new PropertyReference1Impl(ProjectilePredict.class, "drawPlayer", "getDrawPlayer()Z", 0), new PropertyReference1Impl(ProjectilePredict.class, "drawHit", "getDrawHit()Z", 0), new PropertyReference1Impl( ProjectilePredict.class, "lineType", "getLineType()Lsu/catlean/client/features/modules/visual/esp/pearlpredict/ProjectilePredict$LineType;", 0 ) };
   @NotNull
   public static final ProjectilePredict INSTANCE = new ProjectilePredict();
   @NotNull
   private static final GroupSetting selection = SettingFactory.defaultImpl((su.catlean.api.setting.SettingHolder)INSTANCE, "selection", PlaceModule.Pause.BOOLS, null, 4, null);
   @NotNull
   private static final BooleanSetting pearls = SettingFactory.defaultImpl(INSTANCE, "pearls", true, INSTANCE.getGroup(), null, 8, null);
   @NotNull
   private static final BooleanSetting arrows = SettingFactory.defaultImpl(INSTANCE, "arrows", true, INSTANCE.getGroup(), null, 8, null);
   @NotNull
   private static final BooleanSetting tridents = SettingFactory.defaultImpl(INSTANCE, "tridents", true, INSTANCE.getGroup(), null, 8, null);
   @NotNull
   private static final BooleanSetting eggs = SettingFactory.defaultImpl(INSTANCE, "eggs", true, INSTANCE.getGroup(), null, 8, null);
   @NotNull
   private static final BooleanSetting windcharges = SettingFactory.defaultImpl(INSTANCE, "windcharges", true, INSTANCE.getGroup(), null, 8, null);
   @NotNull
   private static final BooleanSetting snowballs = SettingFactory.defaultImpl(INSTANCE, "snowballs", true, INSTANCE.getGroup(), null, 8, null);
   @NotNull
   private static final BooleanSetting potions = SettingFactory.defaultImpl(INSTANCE, "potions", true, INSTANCE.getGroup(), null, 8, null);
   @NotNull
   private static final BooleanSetting drawCircle = SettingFactory.defaultImpl(INSTANCE, "draw-circle", true, INSTANCE.getGroup(), null, 8, null);
   @NotNull
   private static final BooleanSetting items = SettingFactory.defaultImpl(INSTANCE, "items", true, INSTANCE.getGroup(), null, 8, null);
   @NotNull
   private static final BooleanSetting drawPlayer = SettingFactory.defaultImpl(INSTANCE, "draw-player", true, null, () -> INSTANCE.getPearls(), 4, null);
   @NotNull
   private static final BooleanSetting drawHit = SettingFactory.defaultImpl(INSTANCE, "draw-hit", true, null, () -> INSTANCE.getPearls(), 4, null);
   @NotNull
   private static final EnumSetting lineType = SettingFactory.defaultImpl(INSTANCE, "line-type", ProjectilePredict.LineType.DOT, null, null, 12, null);

   private ProjectilePredict() {
      super("projectile-predict", SubCategories.getSubCategory7(), null, 4, null);
   }

   private final GroupValue getGroup() {
      return (GroupValue)selection.computeObject(this, $$delegatedProperties[0]);
   }

   private final boolean getPearls() {
      return (Boolean)pearls.computeObject(this, $$delegatedProperties[1]);
   }

   private final boolean getArrows() {
      return (Boolean)arrows.computeObject(this, $$delegatedProperties[2]);
   }

   private final boolean getTridents() {
      return (Boolean)tridents.computeObject(this, $$delegatedProperties[3]);
   }

   private final boolean getEggs() {
      return (Boolean)eggs.computeObject(this, $$delegatedProperties[4]);
   }

   private final boolean getWindcharges() {
      return (Boolean)windcharges.computeObject(this, $$delegatedProperties[5]);
   }

   private final boolean getSnowBalls() {
      return (Boolean)snowballs.computeObject(this, $$delegatedProperties[6]);
   }

   private final boolean getPotions() {
      return (Boolean)potions.computeObject(this, $$delegatedProperties[7]);
   }

   private final boolean getDrawCircle() {
      return (Boolean)drawCircle.computeObject(this, $$delegatedProperties[8]);
   }

   private final boolean getItems() {
      return (Boolean)items.computeObject(this, $$delegatedProperties[9]);
   }

   private final boolean getDrawPlayer() {
      return (Boolean)drawPlayer.computeObject(this, $$delegatedProperties[10]);
   }

   private final boolean getDrawHit() {
      return (Boolean)drawHit.computeObject(this, $$delegatedProperties[11]);
   }

   private final ProjectilePredict.LineType getLineType() {
      return (ProjectilePredict.LineType)lineType.computeObject(this, $$delegatedProperties[12]);
   }

   @Flow
   private final void onRender3D(Render3DEvent render3DEvent) {
      render3DEvent.getStack().pushPose();
      Iterable<Entity> entitiesForRendering = MinecraftHolder.getClientLevel().entitiesForRendering();
      var arrayList = new ArrayList();

      for (Object object : entitiesForRendering) {
         Entity entity = (Entity)object;
         if ((
               INSTANCE.getPearls() && entity instanceof ThrownEnderpearl
                  || INSTANCE.getArrows() && entity instanceof AbstractArrow && ((AbstractArrow)entity).isPushedByFluid()
                  || INSTANCE.getTridents() && entity instanceof ThrownTrident && ((ThrownTrident)entity).isPushedByFluid()
                  || INSTANCE.getWindcharges() && entity instanceof WindCharge
                  || INSTANCE.getEggs() && entity instanceof ThrownEgg
                  || INSTANCE.getSnowBalls() && entity instanceof Snowball
                  || INSTANCE.getPotions() && entity instanceof AbstractThrownPotion
                  || INSTANCE.getItems() && entity instanceof ItemEntity
            )
            && !entity.onGround()) {
            arrayList.add(object);
         }
      }

      List list = arrayList;
      MeshBuilder meshBuilder = this.getLineType() == ProjectilePredict.LineType.DEFAULT
         ? null
         : new MeshBuilder(DefaultVertexFormat.POSITION_TEX_COLOR, list.size() * 100, false, 4, null);
      Iterable iterable = list;

      for (Object object2 : iterable) {
         Entity entity2 = (Entity)object2;
         INSTANCE.render(render3DEvent.getStack(), entity2, meshBuilder);
      }

      GpuTextureView textureView = MinecraftHolder.getMinecraft().getTextureManager().getTexture(AtlasTexture.getCatlean()).getTextureView();
      Camera mainCamera = MinecraftHolder.getMinecraft().gameRenderer.mainCamera();
      render3DEvent.getStack().mulPose((Quaternionfc)Axis.YP.rotationDegrees(-mainCamera.yRot() + 180.0F));
      render3DEvent.getStack().mulPose((Quaternionfc)Axis.XP.rotationDegrees(-mainCamera.xRot()));
      if (this.getLineType() == ProjectilePredict.LineType.DOT) {
         if (meshBuilder != null) {
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
         }
      }

      render3DEvent.getStack().popPose();
   }

   @Flow
   private final void onEntityRemove(EntityRemove entityRemove) {
      if (this.getDrawHit() && entityRemove.getEntity() instanceof ThrownEnderpearl) {
         byte byteValue = 25;

         for (int i = 0; i < byteValue; i++) {
            BillboardRenderer.INSTANCE
               .testParticleRenderer(
                  new ParticleRenderer(
                     ParticleType.FIREFLY,
                     INSTANCE.computeVec3(entityRemove.getEntity()),
                     0.0F,
                     0.25F,
                     12,
                     new Vec3(Math.random() / 2.0F - 0.25F, Math.random() / 4.0F, Math.random() / 2.0F - 0.25F),
                     ThemeManager.INSTANCE.computeColor(i * 10),
                     0.0F,
                     0.0F,
                     false,
                     896,
                     null
                  )
               );
         }
      }
   }

   private final void render(PoseStack poseStack, Entity entity, MeshBuilder meshBuilder) {
      Vec3 deltaMovement = entity.getDeltaMovement();
      Vec3 vec3 = this.computeVec3(entity);
      int value = 0;
      Vec3 object = null;

      for (int i = 0; i < 300; i++) {
         object = vec3;
         double value2 = Intrinsics.areEqual(MinecraftHolder.getClientLevel().getBlockState(BlockPos.containing((Position)vec3)).getBlock(), Blocks.WATER)
            ? (entity instanceof ThrownTrident ? 0.99 : (entity instanceof ThrownEnderpearl ? 0.8 : 0.6))
            : 0.99;
         Vec3 vec32 = deltaMovement.add(0.0, -entity.getGravity(), 0.0);
         deltaMovement = vec32.scale(value2);
         vec3 = vec3.add(deltaMovement);
         BlockHitResult clip = MinecraftHolder.getClientLevel()
            .clip(new ClipContext((Vec3)object, vec3, Block.COLLIDER, Fluid.NONE, (Entity)MinecraftHolder.getLocalPlayer()));
         if (clip.getType() == Type.BLOCK || clip.getType() == Type.ENTITY) {
            if (this.getLineType() == ProjectilePredict.LineType.DOT) {
               if (this.getDrawCircle() && !entity.onGround()) {
                  Render3D instance = Render3D.INSTANCE;
                  Identifier catlean = AtlasTexture.getCatlean();
                  Vec3 vec33 = clip.getLocation().add(0.0, 0.01, 0.0);
                  Color color = ThemeManager.INSTANCE.computeColor(0);
                  float amount = ParticleType.CIRCLE.getAmount();
                  float amount3 = ParticleType.CIRCLE.getAmount3();
                  float amount2 = ParticleType.CIRCLE.getAmount2();
                  float amount4 = ParticleType.CIRCLE.getAmount4();
                  Render3D.defaultImpl(instance, catlean, vec33, 0.0F, 90.0F, 0.0F, 1.0F, color, false, null, amount, amount2, amount3, amount4, false, 8576, null);
               }
            } else {
               Render3D.defaultImpl(
                  Render3D.INSTANCE,
                  (Vec3)object,
                  clip.getLocation().add(0.0, 0.01, 0.0),
                  ThemeManager.INSTANCE.computeColor(i * 10),
                  null,
                  false,
                  24,
                  null
               );
            }

            if (this.getDrawPlayer()
               && entity instanceof ThrownEnderpearl
               && ((ThrownEnderpearl)entity).getOwner() != null
               && ((ThrownEnderpearl)entity).getOwner() instanceof Player) {
               this.render(
                  poseStack,
                  clip.getLocation(),
                  (Player)((ThrownEnderpearl)entity).getOwner(),
                  (entity.tickCount + Render3D.INSTANCE.getAmount4()) / (entity.tickCount + value)
               );
            }
            break;
         }

         if (vec3.y <= -65.0) {
            break;
         }

         if (entity.getDeltaMovement().x != 0.0 || entity.getDeltaMovement().y != 0.0 || entity.getDeltaMovement().z != 0.0) {
            value++;
            float value3 = ((Number)RangesKt.coerceIn(i / 8.0F, RangesKt.rangeTo(0.0F, 1.0F))).floatValue();
            float value4 = ((Number)RangesKt.coerceIn((i + 1) / 8.0F, RangesKt.rangeTo(0.0F, 1.0F))).floatValue();
            if (this.getLineType() == ProjectilePredict.LineType.DOT) {
               Render3D.defaultImpl(
                  Render3D.INSTANCE,
                  AtlasTexture.getCatlean(),
                  vec3,
                  0.0F,
                  0.0F,
                  0.0F,
                  0.3F,
                  Render2D.INSTANCE.computeColor(ThemeManager.INSTANCE.computeColor(i * 10), value3),
                  false,
                  meshBuilder,
                  ParticleType.FIREFLY.getAmount(),
                  ParticleType.FIREFLY.getAmount2(),
                  ParticleType.FIREFLY.getAmount3(),
                  ParticleType.FIREFLY.getAmount4(),
                  false,
                  8320,
                  null
               );
            } else {
               Render3D.defaultImpl(
                  Render3D.INSTANCE,
                  (Vec3)object,
                  vec3,
                  Render2D.INSTANCE.computeColor(ThemeManager.INSTANCE.computeColor(i * 10), value3),
                  Render2D.INSTANCE.computeColor(ThemeManager.INSTANCE.computeColor(i * 10), value4),
                  false,
                  16,
                  null
               );
            }
         }
      }
   }

   private final void render(PoseStack poseStack, Vec3 vec3, Player player, float amount) {
      if (!(MinecraftHolder.getLocalPlayer().distanceToSqr(vec3) < 1.0)) {
         Render3D.defaultImpl(Render3D.INSTANCE, poseStack, vec3, player, 0.45F, false, 16, null);
         MeshBuilder meshBuilder = new MeshBuilder(DefaultVertexFormat.POSITION_COLOR, 6, false, 4, null);
         float value = 1.8F;
         float amount2 = amount * value;
         AABB aabb = new AABB(vec3.x - 0.6F, vec3.y + amount2, vec3.z - 0.6F, vec3.x + 0.6F, vec3.y + value + 0.5F, vec3.z + 0.6F);
         WorldRenderer.INSTANCE.applyGetEntityRenderDispatcher(meshBuilder, aabb, Color.BLACK, Color.BLACK);
         MeshBuilder.defaultImpl(meshBuilder, Pipelines.INSTANCE.getRenderPipeline21(), null, null, poseStack.last().pose(), null, null, null, 118, null);
         Render3D.INSTANCE.render(poseStack, vec3, player, 1.0F, true);
      }
   }

   static {
   }

   public static enum LineType {
      DOT,
      DEFAULT;

      private static final ProjectilePredict.LineType[] lineTypes = $values();
      private static final EnumEntries<ProjectilePredict.LineType> enumEntries = EnumEntriesKt.enumEntries(lineTypes);

      private LineType() {
      }

      @NotNull
      public static EnumEntries<ProjectilePredict.LineType> getEnumEntries() {
         return enumEntries;
      }

      private static final ProjectilePredict.LineType[] $values() {
         return new ProjectilePredict.LineType[]{DOT, DEFAULT};
      }
   }
}
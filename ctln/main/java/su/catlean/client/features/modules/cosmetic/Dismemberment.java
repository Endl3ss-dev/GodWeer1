// deobf by pusia
package su.catlean.client.features.modules.cosmetic;

import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import net.minecraft.client.Camera;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.ModelPart.Cube;
import net.minecraft.client.model.geom.ModelPart.Polygon;
import net.minecraft.client.model.geom.ModelPart.Vertex;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaternionfc;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.event.events.render.Render3DEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.render.Pipelines;
import su.catlean.client.services.SoundManager;
import su.catlean.gofra.Flow;
import su.catlean.mixins.accessors.ModelPartAccessor;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.render.MeshBuilder;
import su.catlean.utils.render.Render2D;
import su.catlean.utils.render.Render3D;
import su.catlean.utils.world.EntityPositionUtil;

public final class Dismemberment extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(Dismemberment.class, "sound", "getSound()Z", 0), new PropertyReference1Impl(Dismemberment.class, "lifeTime", "getLifeTime()I", 0) };
   @NotNull
   public static final Dismemberment INSTANCE = new Dismemberment();
   @NotNull
   private static final BooleanSetting sound = SettingFactory.defaultImpl(INSTANCE, "sound", true, null, null, 12, null);
   @NotNull
   private static final IntSetting lifeTime = SettingFactory.defaultImpl(INSTANCE, "life-time", 10, new IntRange(3, 100), null, null, 24, null);
   @NotNull
   private static final List<Dismemberment.Engine> items = new ArrayList();
   @NotNull
   private static final List<AbstractClientPlayer> items2 = new ArrayList();

   private Dismemberment() {
      super("dismemberment", SubCategories.getSubCategory6(), null, 4, null);
   }

   public final boolean getSound() {
      return (Boolean)sound.computeObject(this, $$delegatedProperties[0]);
   }

   public final int getLifeTime() {
      return ((Number)lifeTime.computeObject(this, $$delegatedProperties[1])).intValue();
   }

   @NotNull
   public final List<Dismemberment.Engine> getItems() {
      return items;
   }

   @NotNull
   public final List<AbstractClientPlayer> getItems2() {
      return items2;
   }

   @Flow
   private final void onRender3D(Render3DEvent render3DEvent) {
      if (!items.isEmpty()) {
         for (Object item : items) {
            Dismemberment.Engine engine = (Dismemberment.Engine)item;
            engine.render(render3DEvent.getStack());
         }
      }
   }

   @Flow
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      for (Object player : MinecraftHolder.getClientLevel().players()) {
         AbstractClientPlayer abstractClientPlayer = (AbstractClientPlayer)player;
         if (!items2.contains(abstractClientPlayer)) {
            items2.add(abstractClientPlayer);
         }
      }

      items2.removeIf(arg -> {
         boolean flag;
         if (!CollectionsKt.contains(MinecraftHolder.getClientLevel().players(), arg)) {
            INSTANCE.applyBody((Player)arg);
            flag = true;
         } else {
            flag = false;
         }

         return flag;
      });
      items.removeIf(arg2 -> ((Dismemberment.Engine)arg2).isFlag());
   }

   private final void applyBody(Player player) {
      if (!(player instanceof LocalPlayer) && MinecraftHolder.getLocalPlayer().tickCount >= 20) {
         String text = "null cannot be cast to non-null type net.minecraft.client.player.AbstractClientPlayer";
         Identifier texturePath = ((AbstractClientPlayer)player).getSkin().body().texturePath();
         double value = ((AbstractClientPlayer)player).getX();
         double value2 = ((AbstractClientPlayer)player).getY() + 1.45F;
         double value3 = ((AbstractClientPlayer)player).getZ();
         AvatarRenderer renderer = (AvatarRenderer)MinecraftHolder.getMinecraft()
            .getEntityRenderDispatcher()
            .getRenderer((Entity)MinecraftHolder.getLocalPlayer());
         PlayerModel model = (PlayerModel)renderer.getModel();
         var items3 = items;
         boolean flag;
         if (items3 instanceof Collection && ((Collection)items3).isEmpty()) {
            flag = false;
         } else {
            Iterator<Dismemberment.Engine> iterator = items3.iterator();

            while (true) {
               if (!iterator.hasNext()) {
                  flag = false;
                  break;
               }

               Dismemberment.Engine object = iterator.next();
               Dismemberment.Engine engine = (Dismemberment.Engine)object;
               if (Intrinsics.areEqual(engine.getTexture(), texturePath)) {
                  flag = true;
                  break;
               }
            }
         }

         if (!flag) {
            items.add(new Dismemberment.Engine(texturePath, model, Dismemberment.PlayerPart.HEAD, value, value2, value3));
            items.add(new Dismemberment.Engine(texturePath, model, Dismemberment.PlayerPart.BODY, value, value2, value3));
            items.add(new Dismemberment.Engine(texturePath, model, Dismemberment.PlayerPart.RIGHT_ARM, value + 0.35F, value2 - 0.15F, value3));
            items.add(new Dismemberment.Engine(texturePath, model, Dismemberment.PlayerPart.LEFT_ARM, value - 0.35F, value2 - 0.15F, value3));
            items.add(new Dismemberment.Engine(texturePath, model, Dismemberment.PlayerPart.RIGHT_LEG, value + 0.1F, value2 - 0.7F, value3));
            items.add(new Dismemberment.Engine(texturePath, model, Dismemberment.PlayerPart.LEFT_LEG, value - 0.1F, value2 - 0.7F, value3));
            if (this.getSound() && MinecraftHolder.getLocalPlayer().distanceToSqr(value, value2, value3) < 49.0) {
               SoundManager.INSTANCE.getSound23().applyExecuteIfPossible(value, value2, value3);
            }
         }
      }
   }

   public final void render(
      PoseStack stack,
      PlayerModel model,
      MeshBuilder polygon,
      double amount,
      double amount2,
      double amount3,
      double amount4,
      double amount5,
      double amount6,
      Dismemberment.PlayerPart part,
      Color color
   ) {
      stack.pushPose();
      stack.translate(amount, amount2, amount3);
      stack.scale(0.0555F, 0.0555F, 0.0555F);
      stack.mulPose((Quaternionfc)Axis.XP.rotationDegrees((float)amount4));
      stack.mulPose((Quaternionfc)Axis.YP.rotationDegrees((float)amount5));
      stack.mulPose((Quaternionfc)Axis.ZP.rotationDegrees((float)amount6));
      Matrix4f pose = stack.last().pose();
      INSTANCE.applyGetCuboidsList((ModelPart)part.getCallback().invoke(model), polygon, color, pose);
      INSTANCE.applyGetCuboidsList((ModelPart)part.getCallback2().invoke(model), polygon, color, pose);
      stack.popPose();
   }

   public final void applyGetCuboidsList(ModelPart part, MeshBuilder polygon, Color color, Matrix4f mat) {
      for (Object cuboids : ((ModelPartAccessor)(Object)part).getCuboidsList()) {
         Cube cube = (Cube)cuboids;
         Object[] polygons = cube.polygons;
         int value = 0;

         for (int j = polygons.length; value < j; value++) {
            Object object = polygons[value];
            Polygon polygon2 = (Polygon)object;
            Vertex[] vertices = polygon2.vertices();
            polygon.computeMeshBuilder(mat, vertices[0].x(), vertices[0].y(), vertices[0].z()).computeMeshBuilder(vertices[0].u(), vertices[0].v()).fetchAlpha(color);
            polygon.computeMeshBuilder(mat, vertices[1].x(), vertices[1].y(), vertices[1].z()).computeMeshBuilder(vertices[1].u(), vertices[1].v()).fetchAlpha(color);
            polygon.computeMeshBuilder(mat, vertices[2].x(), vertices[2].y(), vertices[2].z()).computeMeshBuilder(vertices[2].u(), vertices[2].v()).fetchAlpha(color);
            polygon.computeMeshBuilder(mat, vertices[3].x(), vertices[3].y(), vertices[3].z()).computeMeshBuilder(vertices[3].u(), vertices[3].v()).fetchAlpha(color);
         }
      }
   }

   static {
   }

   public static final class BodyPart {
      @NotNull
      private final PlayerModel model;
      @NotNull
      private final AvatarRenderState state;
      @NotNull
      private Vec3 pos;
      @NotNull
      private final Identifier texture;
      private float amount;
      private float amount2;
      @NotNull
      private Vec3 vec3;

      public BodyPart(PlayerModel model, AvatarRenderState state, Vec3 pos, Identifier texture, float amount) {
         this.model = model;
         this.state = state;
         this.pos = pos;
         this.texture = texture;
         this.amount = amount;
         this.amount2 = this.amount;
         this.vec3 = this.pos;
      }

      @NotNull
      public final PlayerModel getModel() {
         return this.model;
      }

      @NotNull
      public final AvatarRenderState getState() {
         return this.state;
      }

      @NotNull
      public final Vec3 getPos() {
         return this.pos;
      }

      @NotNull
      public final Identifier getTexture() {
         return this.texture;
      }

      public final float getAmount() {
         return this.amount;
      }

      public final void setAmount(float amount) {
         this.amount = amount;
      }

      public final boolean testAmount(float amount, float amount2) {
         if (this.amount <= 0.0F) {
            return true;
         } else {
            this.amount2 = this.amount;
            this.amount -= amount;
            this.vec3 = this.pos;
            this.pos = new Vec3(this.pos.x, this.pos.y + amount2, this.pos.z);
            return false;
         }
      }

      public final float getAmount2() {
         return RangesKt.coerceIn(Render2D.INSTANCE.computeAmount(this.amount2, this.amount, Render3D.INSTANCE.getAmount4()), 0.0F, 255.0F) / 255.0F;
      }

      @NotNull
      public final Vec3 getVec3() {
         return Render3D.INSTANCE.computeVec3(this.vec3, this.pos, Render3D.INSTANCE.getAmount4());
      }
   }

   public static final class Engine {
      @NotNull
      private Identifier texture;
      @NotNull
      private PlayerModel model;
      @NotNull
      private Dismemberment.PlayerPart part;
      private double amount;
      private double amount2;
      private double amount3;
      private double amount4;
      private double amount5;
      private double amount6;
      private double amount7;
      private double amount8;
      private double amount9;
      private double amount10;
      private double amount11;
      private double amount12;
      private double amount13;
      private double amount14;
      private double amount15;
      private int value;
      private double amount16;
      private double amount17;
      private double amount18;

      public Engine(Identifier texture, PlayerModel model, Dismemberment.PlayerPart part, double amount, double amount2, double amount3) {
         this.texture = texture;
         this.model = model;
         this.part = part;
         this.amount = amount;
         this.amount2 = amount2;
         this.amount3 = amount3;
         this.amount4 = this.getAmount4();
         this.amount5 = this.getAmount4();
         this.amount6 = this.getAmount4();
         this.amount7 = this.getAmount5() * 5.0;
         this.amount8 = this.getAmount5() * 5.0;
         this.amount9 = this.getAmount5() * 5.0;
         this.amount10 = this.amount7;
         this.amount11 = this.amount8;
         this.amount12 = this.amount9;
         this.amount13 = this.getAmount5();
         this.amount14 = this.getAmount5();
         this.amount15 = this.getAmount5();
         this.amount16 = this.amount;
         this.amount17 = this.amount2;
         this.amount18 = this.amount3;
      }

      @NotNull
      public final Identifier getTexture() {
         return this.texture;
      }

      @NotNull
      public final PlayerModel getModel() {
         return this.model;
      }

      @NotNull
      public final Dismemberment.PlayerPart getPart() {
         return this.part;
      }

      public final double getAmount() {
         return this.amount;
      }

      public final void setAmount(double amount) {
         this.amount = amount;
      }

      public final double getAmount2() {
         return this.amount2;
      }

      public final void setAmount2(double amount) {
         this.amount2 = amount;
      }

      public final double getAmount3() {
         return this.amount3;
      }

      public final void setAmount3(double amount) {
         this.amount3 = amount;
      }

      public final double getAmount4() {
         return Math.random() * 0.3 - 0.15;
      }

      public final double getAmount5() {
         return Math.random() * 5.0 - 2.5;
      }

      public final double getAmount6() {
         return this.amount4;
      }

      public final void setAmount4(double amount) {
         this.amount4 = amount;
      }

      public final double getAmount7() {
         return this.amount5;
      }

      public final void setAmount5(double amount) {
         this.amount5 = amount;
      }

      public final double getAmount8() {
         return this.amount6;
      }

      public final void setAmount6(double amount) {
         this.amount6 = amount;
      }

      public final double getAmount9() {
         return this.amount7;
      }

      public final void setAmount7(double amount) {
         this.amount7 = amount;
      }

      public final double getAmount10() {
         return this.amount8;
      }

      public final void setAmount8(double amount) {
         this.amount8 = amount;
      }

      public final double getAmount11() {
         return this.amount9;
      }

      public final void setAmount9(double amount) {
         this.amount9 = amount;
      }

      public final double getAmount12() {
         return this.amount10;
      }

      public final void setAmount10(double amount) {
         this.amount10 = amount;
      }

      public final double getAmount13() {
         return this.amount11;
      }

      public final void setAmount11(double amount) {
         this.amount11 = amount;
      }

      public final double getAmount14() {
         return this.amount12;
      }

      public final void setAmount12(double amount) {
         this.amount12 = amount;
      }

      public final double getAmount15() {
         return this.amount13;
      }

      public final void setAmount13(double amount) {
         this.amount13 = amount;
      }

      public final double getAmount16() {
         return this.amount14;
      }

      public final void setAmount14(double amount) {
         this.amount14 = amount;
      }

      public final double getAmount17() {
         return this.amount15;
      }

      public final void setAmount15(double amount) {
         this.amount15 = amount;
      }

      public final int getValue() {
         return this.value;
      }

      public final void setValue(int value) {
         this.value = value;
      }

      public final double getAmount18() {
         return this.amount16;
      }

      public final void setAmount16(double amount) {
         this.amount16 = amount;
      }

      public final double getAmount19() {
         return this.amount17;
      }

      public final void setAmount17(double amount) {
         this.amount17 = amount;
      }

      public final double getAmount20() {
         return this.amount18;
      }

      public final void setAmount18(double amount) {
         this.amount18 = amount;
      }

      public final boolean isFlag() {
         this.amount16 = this.amount;
         this.amount17 = this.amount2;
         this.amount18 = this.amount3;
         this.amount11 = this.amount8;
         this.amount12 = this.amount9;
         this.amount10 = this.amount7;
         this.amount5 -= 0.08;
         BlockPos containing = BlockPos.containing(this.amount, this.amount2 - 0.3, this.amount3);
         BlockState blockState = MinecraftHolder.getClientLevel().getBlockState(containing);
         this.amount4 *= 0.91;
         this.amount5 *= 0.98;
         this.amount6 *= 0.91;
         this.amount13 *= 0.91;
         this.amount14 *= 0.91;
         this.amount15 *= 0.91;
         if (blockState.getBlock() instanceof LiquidBlock) {
            this.amount5 = 0.31;
         } else if (!blockState.isAir()) {
            this.amount5 = 0.0;
            this.amount2 = containing.getY() + 1.0;
            this.amount4 *= 0.8;
            this.amount6 *= 0.8;
            this.amount7 = this.amount7 + (-90.0F - this.amount7 % 360.0F) / 2;
            this.amount13 = 0.0;
            this.amount14 = 0.0;
            this.amount15 = 0.0;
         }

         Iterable<Entity> entitiesForRendering = MinecraftHolder.getClientLevel().entitiesForRendering();
         var arrayList = new ArrayList();

         for (Object object : entitiesForRendering) {
            Entity entity = (Entity)object;
            if (EntityPositionUtil.computeVec3(entity).distanceToSqr(this.amount, this.amount2, this.amount3) < 0.1) {
               arrayList.add(object);
            }
         }

         for (Entity entity2 : (Iterable<Entity>)arrayList) {
            double amount19 = this.amount - entity2.getX();
            double amount32 = this.amount3 - entity2.getZ();
            this.amount4 = amount19 * 0.5;
            this.amount6 = amount32 * 0.5;
         }

         this.amount = this.amount + this.amount4;
         this.amount2 = this.amount2 + this.amount5;
         this.amount3 = this.amount3 + this.amount6;
         this.amount7 = this.amount7 + this.amount13;
         this.amount8 = this.amount8 + this.amount14;
         this.amount9 = this.amount9 + this.amount15;
         int value2 = this.value++;
         return value2 > Dismemberment.INSTANCE.getLifeTime() * 20;
      }

      public final void render(PoseStack stack) {
         Camera mainCamera = MinecraftHolder.getMinecraft().gameRenderer.mainCamera();
         boolean flag = this.amount5 == 0.0;
         GpuTextureView textureView = MinecraftHolder.getMinecraft().getTextureManager().getTexture(this.texture).getTextureView();
         MeshBuilder meshBuilder = new MeshBuilder(DefaultVertexFormat.POSITION_TEX_COLOR, 12, false, 4, null);
         Dismemberment.INSTANCE
            .render(
               stack,
               this.model,
               meshBuilder,
               Render2D.INSTANCE.computeAmount(this.amount16, this.amount) - (float)mainCamera.position().x,
               Render2D.INSTANCE.computeAmount(this.amount17, this.amount2)
                  + (flag ? this.part.getAmount2() : this.part.getAmount())
                  - (float)mainCamera.position().y,
               Render2D.INSTANCE.computeAmount(this.amount18, this.amount3) - (float)mainCamera.position().z,
               Render2D.INSTANCE.computeAmount(this.amount10, this.amount7) + 180.0,
               Render2D.INSTANCE.computeAmount(this.amount11, this.amount8),
               Render2D.INSTANCE.computeAmount(this.amount12, this.amount9),
               this.part,
               Render2D.INSTANCE.computeColor(Color.WHITE, RangesKt.coerceIn(Dismemberment.INSTANCE.getLifeTime() * 20 - this.value, 0, 3) / 3.0F)
            );
         MeshBuilder.defaultImpl(
            meshBuilder,
            Pipelines.INSTANCE.getRenderPipeline17(),
            null,
            null,
            new PoseStack().last().pose(),
            null,
            MapsKt.mapOf(TuplesKt.to("Sampler0", textureView)),
            null,
            86,
            null
         );
      }
   }

   public static enum PlayerPart {
      HEAD(arg -> arg.head, arg2 -> arg2.hat, 0.5, 0.25),
      BODY(arg -> arg.body, arg2 -> arg2.jacket, 0.7, 0.2),
      LEFT_ARM(arg -> arg.leftArm, arg2 -> arg2.leftSleeve, 0.6, 0.2),
      RIGHT_ARM(arg -> arg.rightArm, arg2 -> arg2.rightSleeve, 0.6, 0.2),
      LEFT_LEG(arg -> arg.leftLeg, arg2 -> arg2.leftPants, 0.7, 0.2),
      RIGHT_LEG(arg -> arg.rightLeg, arg2 -> arg2.rightPants, 0.7, 0.2);

      @NotNull
      private final Function1<PlayerModel, net.minecraft.client.model.geom.ModelPart> callback;
      @NotNull
      private final Function1<PlayerModel, net.minecraft.client.model.geom.ModelPart> callback2;
      private final double amount;
      private final double amount2;
      private static final Dismemberment.PlayerPart[] playerParts = $values();
      private static final EnumEntries<Dismemberment.PlayerPart> enumEntries = EnumEntriesKt.enumEntries(playerParts);

      private PlayerPart(Function1<PlayerModel, net.minecraft.client.model.geom.ModelPart> callback, Function1<PlayerModel, net.minecraft.client.model.geom.ModelPart> callback2, double amount, double amount2) {
         this.callback = callback;
         this.callback2 = callback2;
         this.amount = amount;
         this.amount2 = amount2;
      }

      @NotNull
      public final Function1 getCallback() {
         return this.callback;
      }

      @NotNull
      public final Function1 getCallback2() {
         return this.callback2;
      }

      public final double getAmount() {
         return this.amount;
      }

      public final double getAmount2() {
         return this.amount2;
      }

      @NotNull
      public static EnumEntries<Dismemberment.PlayerPart> getEnumEntries() {
         return enumEntries;
      }

      private static final Dismemberment.PlayerPart[] $values() {
         return new Dismemberment.PlayerPart[]{HEAD, BODY, LEFT_ARM, RIGHT_ARM, LEFT_LEG, RIGHT_LEG};
      }
   }
}
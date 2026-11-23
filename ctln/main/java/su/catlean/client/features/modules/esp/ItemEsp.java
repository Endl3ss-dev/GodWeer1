// deobf by pusia
package su.catlean.client.features.modules.esp;

import com.mojang.blaze3d.pipeline.MainTarget;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import kotlin.collections.CollectionsKt;
import kotlin.comparisons.ComparisonsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.MutablePropertyReference1Impl;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import net.minecraft.client.Camera;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.ItemEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;
import org.joml.Vector4d;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import su.catlean.api.data.ItemCluster;
import su.catlean.api.event.GofraState;
import su.catlean.api.event.events.render.FrameBufferEvent;
import su.catlean.api.event.events.render.Render2DEvent;
import su.catlean.api.event.events.render.Render3DEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.ColorSetting;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.api.setting.settings.FloatSetting;
import su.catlean.api.setting.settings.ItemsSetting;
import su.catlean.api.setting.values.ItemsValue;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.features.modules.cosmetic.HandChams;
import su.catlean.client.render.Pipelines;
import su.catlean.client.render.TexturedQuadRenderer;
import su.catlean.client.render.font.FontRenderer;
import su.catlean.client.render.font.Fonts;
import su.catlean.client.render.uniforms.ProjectionBuffer;
import su.catlean.gofra.Flow;
import su.catlean.mixins.accessors.GameRendererAccessor;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.render.MeshBuilder;
import su.catlean.utils.render.Render2D;
import su.catlean.utils.render.Render3D;

public final class ItemEsp extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(ItemEsp.class, "highlight", "getHighlight()Lsu/catlean/ie;", 0), new PropertyReference1Impl(ItemEsp.class, "shaderMode", "getShaderMode()Lsu/catlean/tt;", 0), new PropertyReference1Impl(ItemEsp.class, "merge", "getMerge()Z", 0), new PropertyReference1Impl(ItemEsp.class, "onePlate", "getOnePlate()Z", 0), new PropertyReference1Impl(ItemEsp.class, "shulkerLoot", "getShulkerLoot()Z", 0), new PropertyReference1Impl(ItemEsp.class, "color", "getColor()Ljava/awt/Color;", 0), new PropertyReference1Impl(ItemEsp.class, "outlineColor", "getOutlineColor()Ljava/awt/Color;", 0), new PropertyReference1Impl(ItemEsp.class, "highLightColor", "getHighLightColor()Ljava/awt/Color;", 0), new MutablePropertyReference1Impl(ItemEsp.class, "lineWithValue", "getLineWithValue()F", 0), new PropertyReference1Impl(ItemEsp.class, "filter", "getFilter()Lsu/catlean/i2;", 0), new PropertyReference1Impl(ItemEsp.class, "items", "getItems()Lsu/catlean/s4;", 0) };
   @NotNull
   public static final ItemEsp INSTANCE = new ItemEsp();
   @NotNull
   private static final EnumSetting highlight = SettingFactory.defaultImpl(INSTANCE, "highlight", ItemEsp.Highlight.OFF, null, null, 12, null);
   @NotNull
   private static final EnumSetting shaderMode = SettingFactory.defaultImpl(
      INSTANCE, "shader-mode", ItemEsp.ShaderMode.DEFAULT, null, () -> INSTANCE.getHighlight() == ItemEsp.Highlight.SHADER, 4, null
   );
   @NotNull
   private static final BooleanSetting merge = SettingFactory.defaultImpl(INSTANCE, "merge", true, null, null, 12, null);
   @NotNull
   private static final BooleanSetting onePlate = SettingFactory.defaultImpl(INSTANCE, "one-plate", true, null, () -> INSTANCE.getMerge(), 4, null);
   @NotNull
   private static final BooleanSetting shulkers = SettingFactory.defaultImpl(INSTANCE, "shulkers", true, null, null, 12, null);
   @NotNull
   private static final ColorSetting fill = SettingFactory.defaultImpl(INSTANCE, "fill", new Color(150, 150, 150, 255), null, null, 12, null);
   @NotNull
   private static final ColorSetting outline = SettingFactory.defaultImpl(INSTANCE, "outline", new Color(-1), null, null, 12, null);
   @NotNull
   private static final ColorSetting highlightColor = SettingFactory.defaultImpl(INSTANCE, "highlight-color", new Color(-1), null, null, 12, null);
   @NotNull
   private static final FloatSetting lineWidth = SettingFactory.defaultImpl(
      INSTANCE,
      "line-width",
      4.0F,
      RangesKt.rangeTo(1.0F, 4.0F),
      null,
      () -> INSTANCE.getShaderMode() == ItemEsp.ShaderMode.DEFAULT || INSTANCE.getHighlight() == ItemEsp.Highlight.SHADER,
      8,
      null
   );
   @NotNull
   private static final EnumSetting filter = SettingFactory.defaultImpl(INSTANCE, "filter", ItemEsp.Filter.NONE, null, null, 12, null);
   @NotNull
   private static final ItemsSetting items = SettingFactory.defaultImpl(
      INSTANCE, "items", new ItemsValue(null, 1, null), null, () -> INSTANCE.getFilter() != ItemEsp.Filter.NONE, 4, null
   );
   @Nullable
   private static MainTarget mainTarget;
   private static boolean flag;
   @NotNull
   private static final TexturedQuadRenderer texturedQuadRenderer = new TexturedQuadRenderer();

   private ItemEsp() {
      super("item-esp", SubCategories.getSubCategory7(), null, 4, null);
   }

   private final ItemEsp.Highlight getHighlight() {
      return (ItemEsp.Highlight)highlight.computeObject(this, $$delegatedProperties[0]);
   }

   private final ItemEsp.ShaderMode getShaderMode() {
      return (ItemEsp.ShaderMode)shaderMode.computeObject(this, $$delegatedProperties[1]);
   }

   private final boolean getMerge() {
      return (Boolean)merge.computeObject(this, $$delegatedProperties[2]);
   }

   private final boolean getOnePlate() {
      return (Boolean)onePlate.computeObject(this, $$delegatedProperties[3]);
   }

   private final boolean getShulkerLoot() {
      return (Boolean)shulkers.computeObject(this, $$delegatedProperties[4]);
   }

   private final Color getColor() {
      return (Color)fill.computeObject(this, $$delegatedProperties[5]);
   }

   private final Color getOutlineColor() {
      return (Color)outline.computeObject(this, $$delegatedProperties[6]);
   }

   private final Color getHighLightColor() {
      return (Color)highlightColor.computeObject(this, $$delegatedProperties[7]);
   }

   private final float getLineWithValue() {
      return ((Number)lineWidth.computeObject(this, $$delegatedProperties[8])).floatValue();
   }

   private final ItemEsp.Filter getFilter() {
      return (ItemEsp.Filter)filter.computeObject(this, $$delegatedProperties[9]);
   }

   private final ItemsValue getItems() {
      return (ItemsValue)items.computeObject(this, $$delegatedProperties[10]);
   }

   @Flow
   private final void onFrameBuffer(FrameBufferEvent frameBufferEvent) {
      if (mainTarget != null && flag) {
         frameBufferEvent.setFrameBuffer((RenderTarget)mainTarget);
         frameBufferEvent.cancel();
      }
   }

   @Flow
   private final void onRender3D(Render3DEvent render3DEvent) {
      if (this.getHighlight() != ItemEsp.Highlight.OFF) {
         if (this.getHighlight() == ItemEsp.Highlight.SHADER) {
            this.applyMainRenderTarget();
            this.render(render3DEvent.getStack());
         }

         if (this.getHighlight() != ItemEsp.Highlight.SHADER) {
            for (ItemEntity items3Item : (Iterable<ItemEntity>)this.getItems3()) {
               switch (ItemEsp.WhenMappings.values[this.getHighlight().ordinal()]) {
                  case 1:
                     Render3D.defaultImpl2(Render3D.INSTANCE, Render3D.INSTANCE.computeAABB((Entity)items3Item), this.getHighLightColor(), null, 4, null);
                     Render3D.defaultImpl3(
                        Render3D.INSTANCE,
                        Render3D.INSTANCE.computeAABB((Entity)items3Item),
                        Render2D.INSTANCE.computeColor(this.getHighLightColor(), 1.0F),
                        null,
                        4,
                        null
                     );
                     break;
                  case 2:
                     Render3D.defaultImpl3(
                        Render3D.INSTANCE,
                        Render3D.INSTANCE.computeAABB((Entity)items3Item),
                        Render2D.INSTANCE.computeColor(this.getHighLightColor(), 1.0F),
                        null,
                        4,
                        null
                     );
                     break;
                  default:
                     return;
               }
            }
         }
      }
   }

   @Flow
   private final void onRender2D(Render2DEvent render2DEvent) {
      List<ItemCluster> items2 = this.getItems2();
      Iterable<ItemCluster> iterable = items2;

      for (ItemCluster sortedWithItem : (Iterable<ItemCluster>)CollectionsKt.sortedWith(iterable, new ItemEsp.Ordering())) {
         float tickCount = Math.min(((ItemEntity)CollectionsKt.first(sortedWithItem.getItems())).tickCount / 15.0F, 1.0F);
         Vector4d vector4d = Render2D.INSTANCE.computeVector4d(sortedWithItem.getVec3());
         List items22 = sortedWithItem.getItems2();
         if (vector4d != null) {
            float value = 0.0F;

            for (String text : (Iterable<String>)items22) {
               float amount = Fonts.getFontRenderer6().computeAmount(text);
               if (amount > value) {
                  value = amount;
               }
            }

            float value2 = (float)(vector4d.x + (float)(vector4d.z - vector4d.x) / 2.0F - value / 2);
            if (this.getOnePlate()) {
               texturedQuadRenderer.render(
                  render2DEvent.getContext().pose(), value2 - 3.0F, (float)(vector4d.y - 1.0F - 12.0F * items22.size()), value + 5.0F, 12.0F * items22.size(), tickCount
               );
            }

            Iterable iterable2 = items22;
            int value3 = 0;

            for (Object object : iterable2) {
               int value4 = value3++;
               if (value4 < 0) {
                  CollectionsKt.throwIndexOverflow();
               }

               String text2 = (String)object;
               if (!INSTANCE.getOnePlate()) {
                  texturedQuadRenderer.render(
                     render2DEvent.getContext().pose(),
                     value2 - 3.0F + value / 2.0F - Fonts.getFontRenderer6().computeAmount(text2) / 2.0F,
                     (float)(vector4d.y - 13.0F - 12.0F * value4),
                     Fonts.getFontRenderer6().computeAmount(text2) + 5.0F,
                     12.0F,
                     tickCount
                  );
               }

               FontRenderer.defaultImpl(
                  Fonts.getFontRenderer6(),
                  render2DEvent.getContext().pose(),
                  text2,
                  value2 - 1.0F + value / 2.0F - Fonts.getFontRenderer6().computeAmount(text2) / 2.0F,
                  (float)vector4d.y - 13.0F - 12.0F * value4,
                  Render2D.INSTANCE.computeColor(Color.WHITE, tickCount),
                  false,
                  0,
                  96,
                  null
               );
            }

            if (this.getShulkerLoot()) {
               Iterable items3 = sortedWithItem.getItems();
               var arrayList = new ArrayList();

               for (Object object2 : items3) {
                  ItemEntity itemEntity = (ItemEntity)object2;
                  if (itemEntity.getItem().getItem() instanceof BlockItem && ((BlockItem)itemEntity.getItem().getItem()).getBlock() instanceof ShulkerBoxBlock) {
                     arrayList.add(object2);
                  }
               }

               for (ItemEntity itemEntity2 : (Iterable<ItemEntity>)arrayList) {
                  render2DEvent.getContext().pose().pushMatrix();
                  render2DEvent.getContext().pose().translate((float)vector4d.x - 58.2F, (float)vector4d.y - 4.0F);
                  render2DEvent.getContext().pose().scale(0.6F);
                  this.render(render2DEvent.getContext(), itemEntity2.getItem(), 1.0F);
                  render2DEvent.getContext().pose().popMatrix();
               }
            }
         }
      }

      texturedQuadRenderer.render(render2DEvent.getContext().pose(), this.getColor(), this.getOutlineColor(), true);
      Fonts.getFontRenderer6().update();
      if (this.getHighlight() == ItemEsp.Highlight.FRAME) {
         MeshBuilder meshBuilder = new MeshBuilder(DefaultVertexFormat.POSITION_COLOR, this.getItems3().size() * 8, false, 4, null);

         for (ItemEntity items3Item : (Iterable<ItemEntity>)this.getItems3()) {
            Vec3[] vec3s = Render2D.INSTANCE.computeVec3s((Entity)items3Item);
            Vector4d vector4d2 = null;
            int value5 = 0;

            for (int j = vec3s.length; value5 < j; value5++) {
               Vec3 vec3 = vec3s[value5];
               Vec3 window = Render2D.INSTANCE.fetchWindow(new Vec3(vec3.x, vec3.y, vec3.z));
               if (window.z > 0.0 && window.z < 1.0) {
                  if (vector4d2 == null) {
                     vector4d2 = new Vector4d(window.x, window.y, window.z, 0.0);
                  }

                  vector4d2.x = Math.min(window.x, vector4d2.x);
                  vector4d2.y = Math.min(window.y, vector4d2.y);
                  vector4d2.z = Math.max(window.x, vector4d2.z);
                  vector4d2.w = Math.max(window.y, vector4d2.w);
               }
            }

            if (vector4d2 != null) {
               float value6 = (float)vector4d2.x;
               float value7 = (float)vector4d2.y;
               float value8 = (float)vector4d2.z;
               float value9 = (float)vector4d2.w;
               Render2D.INSTANCE.updateMeshBuilder2(meshBuilder, value6, value7, value8, value9, this.getHighLightColor());
            }
         }

         MeshBuilder.defaultImpl(
            meshBuilder,
            Pipelines.INSTANCE.getRenderPipeline22(),
            null,
            null,
            null,
            new Matrix3x2f((Matrix3x2fc)render2DEvent.getContext().pose()),
            null,
            null,
            110,
            null
         );
      }

      if (mainTarget != null && this.getHighlight() == ItemEsp.Highlight.SHADER) {
         PlayerEsp.ShaderMode defaultValue = this.getShaderMode() == ItemEsp.ShaderMode.DEFAULT ? PlayerEsp.ShaderMode.DEFAULT : null;
         HandChams.INSTANCE
            .applyDarker(
               defaultValue,
               this.getHighLightColor(),
               mainTarget,
               mainTarget,
               false,
               false,
               0.0F,
               0.0F,
               0.0F,
               this.getLineWithValue(),
               0,
               Color.WHITE,
               Color.WHITE,
               0,
               0.0F,
               0.0F,
               0.0F,
               0.0F,
               0.0F,
               0.0F
            );
      }
   }

   private final List<ItemCluster> getItems2() {
      List<ItemCluster> arrayList = new ArrayList();
      List items3 = this.getItems3();
      List<ItemEntity> arrayList2 = new ArrayList();

      for (ItemEntity itemEntity : (Iterable<ItemEntity>)items3) {
         if (!arrayList2.contains(itemEntity)) {
            List arrayList3 = new ArrayList();
            ItemCluster itemCluster = new ItemCluster(arrayList3);
            itemCluster.getItems().add(itemEntity);

            for (ItemEntity itemEntity2 : (Iterable<ItemEntity>)items3) {
               if (!Intrinsics.areEqual(itemEntity, itemEntity2) && itemEntity.distanceToSqr((Entity)itemEntity2) < (this.getMerge() ? 9.0 : 0.0) && !arrayList2.contains(itemEntity2)) {
                  itemCluster.getItems().add(itemEntity2);
                  arrayList2.add(itemEntity2);
               }
            }

            arrayList.add(itemCluster);
         }
      }

      return CollectionsKt.distinct(arrayList);
   }

   private final void render(GuiGraphicsExtractor guiGraphicsExtractor, ItemStack itemStack, float amount) {
      ItemContainerContents container = (ItemContainerContents)itemStack.get(DataComponents.CONTAINER);
      if (container != null) {
         ItemContainerContents itemContainerContents = container;
         Item item = itemStack.getItem();
         if (item instanceof BlockItem && ((BlockItem)item).getBlock() instanceof ShulkerBoxBlock) {
            List<ItemStack> list = itemContainerContents.allItemsCopyStream().toList();
            if (!list.isEmpty()) {
               texturedQuadRenderer.render(guiGraphicsExtractor.pose(), 12.0F, -79.0F, 169.0F, 60.0F, 2.0F);
               int value = 0;
               int value2 = 0;

               for (ItemStack itemStack2 : (Iterable<ItemStack>)list) {
                  guiGraphicsExtractor.item(itemStack2, 16 + value2 * 18, -75 + value * 18);
                  guiGraphicsExtractor.itemDecorations(MinecraftHolder.getMinecraft().font, itemStack2, 16 + value2 * 18, -75 + value * 18);
                  if (++value2 >= 9) {
                     value2 = 0;
                     value++;
                  }
               }
            }
         }
      }
   }

   private final void render(PoseStack poseStack) {
      Camera mainCamera = MinecraftHolder.getMinecraft().gameRenderer.mainCamera();
      double amount4 = Render3D.INSTANCE.getAmount4();
      boolean entityShadow = (Boolean)MinecraftHolder.getMinecraft().options.entityShadows().get();
      MinecraftHolder.getMinecraft().options.entityShadows().set(false);
      GofraState.INSTANCE.setModifyBuffer(true);
      flag = true;
      RenderSystem.backupProjectionMatrix();
      RenderSystem.setProjectionMatrix(
         ProjectionBuffer.INSTANCE.getProjectionMatrixBuffer().getBuffer(Render3D.INSTANCE.getMatrix4f()), RenderSystem.getProjectionType()
      );
      SubmitNodeStorage submitNodeStorage = new SubmitNodeStorage();

      for (ItemEntity items3Item : (Iterable<ItemEntity>)this.getItems3()) {
         if (MinecraftHolder.getMinecraft()
            .getEntityRenderDispatcher()
            .shouldRender((Entity)items3Item, Render3D.INSTANCE.getFrustum(), mainCamera.position().x, mainCamera.position().y, mainCamera.position().z)) {
            double value = Mth.lerp(amount4, items3Item.xOld, items3Item.getX()) - mainCamera.position().x;
            double value2 = Mth.lerp(amount4, items3Item.yOld, items3Item.getY()) - mainCamera.position().y;
            double value3 = Mth.lerp(amount4, items3Item.zOld, items3Item.getZ()) - mainCamera.position().z;
            EntityRenderState renderState = MinecraftHolder.getMinecraft()
               .getEntityRenderDispatcher()
               .getRenderer((Entity)items3Item)
               .createRenderState((Entity)items3Item, Render3D.INSTANCE.getAmount4());
            String text = "null cannot be cast to non-null type net.minecraft.client.renderer.entity.state.ItemEntityRenderState";
            var value4 = (ItemEntityRenderState)renderState;
            CameraRenderState cameraRenderState = ((GameRendererAccessor)MinecraftHolder.getMinecraft().gameRenderer).catlean$getGameRenderState().levelRenderState.cameraRenderState;
            MinecraftHolder.getMinecraft()
               .getEntityRenderDispatcher()
               .submit((EntityRenderState)value4, cameraRenderState, value, value2, value3, poseStack, (SubmitNodeCollector)submitNodeStorage);
         }
      }

      GpuTextureView outputColorTextureOverride = RenderSystem.outputColorTextureOverride;
      GpuTextureView outputDepthTextureOverride = RenderSystem.outputDepthTextureOverride;

      try {
         RenderSystem.outputColorTextureOverride = mainTarget.getColorTextureView();
         RenderSystem.outputDepthTextureOverride = mainTarget.getDepthTextureView();
         MinecraftHolder.getMinecraft().gameRenderer.featureRenderDispatcher().renderAllFeatures(submitNodeStorage);
      } finally {
         RenderSystem.outputColorTextureOverride = outputColorTextureOverride;
         RenderSystem.outputDepthTextureOverride = outputDepthTextureOverride;
      }

      RenderSystem.restoreProjectionMatrix();
      flag = false;
      GofraState.INSTANCE.setModifyBuffer(false);
      MinecraftHolder.getMinecraft().options.entityShadows().set(entityShadow);
   }

   private final void applyMainRenderTarget() {
      if (mainTarget == null) {
         mainTarget = new MainTarget(
            MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget().width, MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget().height
         );
      } else {
         if (MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget().width != mainTarget.width
            || MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget().height != mainTarget.height) {
            mainTarget.resize(
               MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget().width, MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget().height
            );
         }

         RenderSystem.getDevice()
            .createCommandEncoder()
            .clearColorAndDepthTextures(mainTarget.getColorTexture(), (Vector4fc)(new Vector4f(0.0F, 0.0F, 0.0F, 0.0F)), mainTarget.getDepthTexture(), 0.0);
      }
   }

   private final List getItems3() {
      Iterable<Entity> entitiesForRendering = MinecraftHolder.getClientLevel().entitiesForRendering();
      var arrayList = new ArrayList();

      for (Object object : entitiesForRendering) {
         if (object instanceof ItemEntity) {
            arrayList.add(object);
         }
      }

      entitiesForRendering = arrayList;
      arrayList = new ArrayList();

      for (Object object2 : entitiesForRendering) {
         ItemEntity itemEntity = (ItemEntity)object2;
         ItemEsp instance = INSTANCE;
         ItemStack item = itemEntity.getItem();
         if (instance.checkContains(item)) {
            arrayList.add(object2);
         }
      }

      return arrayList;
   }

   private final boolean checkContains(ItemStack itemStack) {
      return switch (ItemEsp.WhenMappings.values2[this.getFilter().ordinal()]) {
         case 1 -> true;
         case 2 -> this.getItems().getItems().contains(itemStack.getItem());
         default -> !this.getItems().getItems().contains(itemStack.getItem());
      };
   }

   static {
   }

   public static enum Filter {
      NONE,
      WHITE_LIST,
      BLACK_LIST;

      private static final ItemEsp.Filter[] filters = $values();
      private static final EnumEntries<ItemEsp.Filter> enumEntries = EnumEntriesKt.enumEntries(filters);

      private Filter() {
      }

      @NotNull
      public static EnumEntries<ItemEsp.Filter> getEnumEntries() {
         return enumEntries;
      }

      private static final ItemEsp.Filter[] $values() {
         return new ItemEsp.Filter[]{NONE, WHITE_LIST, BLACK_LIST};
      }
   }

   public static enum Highlight {
      BOX,
      OUTLINE,
      FRAME,
      SHADER,
      OFF;

      private static final ItemEsp.Highlight[] highlights = $values();
      private static final EnumEntries<ItemEsp.Highlight> enumEntries = EnumEntriesKt.enumEntries(highlights);

      private Highlight() {
      }

      @NotNull
      public static EnumEntries<ItemEsp.Highlight> getEnumEntries() {
         return enumEntries;
      }

      private static final ItemEsp.Highlight[] $values() {
         return new ItemEsp.Highlight[]{BOX, OUTLINE, FRAME, SHADER, OFF};
      }
   }

   public static final class Ordering implements Comparator {
      public Ordering() {
      }

      @Override
      public final int compare(Object object, Object object2) {
         ItemCluster object22 = (ItemCluster)object2;
         Comparable distanceToSqr = MinecraftHolder.getLocalPlayer().distanceToSqr(object22.getVec3());
         object22 = (ItemCluster)object;
         Comparable comparable = distanceToSqr;
         return ComparisonsKt.compareValues(comparable, MinecraftHolder.getLocalPlayer().distanceToSqr(object22.getVec3()));
      }
   }

   public static enum ShaderMode {
      SELECT,
      DEFAULT;

      private static final ItemEsp.ShaderMode[] shaderModes = $values();
      private static final EnumEntries<ItemEsp.ShaderMode> enumEntries = EnumEntriesKt.enumEntries(shaderModes);

      private ShaderMode() {
      }

      @NotNull
      public static EnumEntries<ItemEsp.ShaderMode> getEnumEntries() {
         return enumEntries;
      }

      private static final ItemEsp.ShaderMode[] $values() {
         return new ItemEsp.ShaderMode[]{SELECT, DEFAULT};
      }
   }

   public static final class WhenMappings {
      public static final int[] values;
      public static final int[] values2;

      static {
         int[] values3 = new int[ItemEsp.Highlight.values().length];

         try {
            values3[ItemEsp.Highlight.BOX.ordinal()] = 1;
         } catch (NoSuchFieldError exception) {
         }

         try {
            values3[ItemEsp.Highlight.OUTLINE.ordinal()] = 2;
         } catch (NoSuchFieldError exception2) {
         }

         values = values3;
         values3 = new int[ItemEsp.Filter.values().length];

         try {
            values3[ItemEsp.Filter.NONE.ordinal()] = 1;
         } catch (NoSuchFieldError exception3) {
         }

         try {
            values3[ItemEsp.Filter.WHITE_LIST.ordinal()] = 2;
         } catch (NoSuchFieldError exception4) {
         }

         values2 = values3;
      }
   }
}
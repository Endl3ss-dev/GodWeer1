// deobf by pusia
package su.catlean.client.features.ui.hud.widgets;

import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.math.MathKt;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;
import su.catlean.api.enums.ParticleType;
import su.catlean.client.features.modules.attack.Aura;
import su.catlean.client.features.modules.attack.AutoCrystal;
import su.catlean.client.features.modules.esp.NameTags;
import su.catlean.client.features.ui.hud.Widget;
import su.catlean.client.features.ui.hud.WidgetEditorScreen;
import su.catlean.client.render.BoxPass;
import su.catlean.client.render.Pipelines;
import su.catlean.client.render.TexturedQuadPass;
import su.catlean.client.render.font.Fonts;
import su.catlean.client.services.NotificationManager;
import su.catlean.client.services.ThemeManager;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.animation.Animation;
import su.catlean.utils.inventory.InventoryUtil;
import su.catlean.utils.player.PlayerTargetUtil;
import su.catlean.utils.render.AtlasTexture;
import su.catlean.utils.render.MeshBuilder;
import su.catlean.utils.render.Render2D;
import su.catlean.utils.render.Render3D;

public final class TargetInfoWidget extends Widget {
   @NotNull
   public static final TargetInfoWidget INSTANCE = new TargetInfoWidget();
   @NotNull
   private static final List<TargetInfoAnimations> items = new ArrayList();
   @Nullable
   private static LivingEntity livingEntity;
   @NotNull
   private static final Animation animation = new Animation(NotificationManager.Notification.Type.OUT_BACK, 300L);
   @NotNull
   private static final Animation animation2 = new Animation(NotificationManager.Notification.Type.OUT_QUINT, 300L);
   private static int value;

   private TargetInfoWidget() {
      super("target-info");
   }

   @Nullable
   public final LivingEntity getLivingEntity() {
      return livingEntity;
   }

   public final void setLivingEntity(LivingEntity livingEntity) {
      TargetInfoWidget.livingEntity = livingEntity;
   }

   @NotNull
   public final Animation getAnimation() {
      return animation;
   }

   @Override
   public void update() {
      if (livingEntity != null
         && livingEntity.isDeadOrDying()
         && livingEntity instanceof Player
         && !Intrinsics.areEqual(livingEntity, MinecraftHolder.getLocalPlayer())) {
         int value2 = value++;
      } else {
         value = 0;
      }

      Entity entity = Aura.INSTANCE.getEntity();
      if (entity == null) {
         entity = (Entity)(AutoCrystal.INSTANCE.getEnabled() ? AutoCrystal.INSTANCE.getPlayer() : null);
      }

      Entity entity2 = entity;
      if (entity2 != null && entity2 instanceof Player && ((Player)entity2).getHealth() < 10.0F && ((Player)entity2).tickCount % 6 == 0) {
         items.add(
            new TargetInfoAnimations(
               this.getAmount3() + 46 + 28 * (float)Math.random(),
               this.getAmount4() + 5.0F,
               (float)Math.random() * 2.0F - 1.0F,
               (float)Math.random() * 2,
               0,
               0.0F,
               0.0F,
               false,
               224,
               null
            )
         );
      }

      float amount = animation2.getAmount() / 20.0F;
      float value3 = Math.clamp(amount, 0.0F, 1.0F);
      if (entity2 != null && entity2 instanceof LivingEntity) {
         int hurtTime = ((LivingEntity)entity2).hurtTime;
         if (5 <= hurtTime ? hurtTime < 11 : false) {
            byte byteValue = 6;

            for (int i = 0; i < byteValue; i++) {
               items.add(
                  new TargetInfoAnimations(
                     INSTANCE.getAmount3() - 2.0F + 114.0F * value3,
                     INSTANCE.getAmount4() + 44.5F + (float)Math.random() * 2 - 1.0F,
                     (float)Math.random() * 2.0F,
                     (float)Math.random() * 1.0F - 0.5F,
                     0,
                     0.0F,
                     0.0F,
                     true,
                     96,
                     null
                  )
               );
            }
         }
      }

      items.removeIf(arg -> {
         label38: {
            ((TargetInfoAnimations)arg).setAmount5(((TargetInfoAnimations)arg).getAmount());
            ((TargetInfoAnimations)arg).setAmount6(((TargetInfoAnimations)arg).getAmount2());
            ((TargetInfoAnimations)arg).setAmount(((TargetInfoAnimations)arg).getAmount() + ((TargetInfoAnimations)arg).getAmount3());
            ((TargetInfoAnimations)arg).setAmount2(((TargetInfoAnimations)arg).getAmount2() + ((TargetInfoAnimations)arg).getAmount4());
            ((TargetInfoAnimations)arg).setAmount3(((TargetInfoAnimations)arg).getAmount3() * 0.95F);
            ((TargetInfoAnimations)arg).setAmount4(((TargetInfoAnimations)arg).getAmount4() * 0.95F);
            double amount3 = INSTANCE.getAmount3() + 46.0;
            double amount32 = INSTANCE.getAmount3() + 74.0;
            double amount2 = ((TargetInfoAnimations)arg).getAmount() + 4;
            if (amount3 <= amount2 ? amount2 <= amount32 : false) {
               amount3 = INSTANCE.getAmount4() + 5.0;
               amount32 = INSTANCE.getAmount4() + 33.0;
               amount2 = ((TargetInfoAnimations)arg).getAmount2() + 4;
               if (amount3 <= amount2 ? amount2 <= amount32 : false) {
                  break label38;
               }
            }

            if (!((TargetInfoAnimations)arg).isFlag()) {
               ((TargetInfoAnimations)arg).setAmount4(((TargetInfoAnimations)arg).getAmount4() + 0.2F);
               ((TargetInfoAnimations)arg).setAmount3(((TargetInfoAnimations)arg).getAmount3() * 0.95F);
            }
         }

         int value4 = ((TargetInfoAnimations)arg).getValue();
         ((TargetInfoAnimations)arg).setValue(value4 + 1);
         return value4 > (((TargetInfoAnimations)arg).isFlag() ? 10 : 60);
      });
   }

   @Override
   public void render(GuiGraphicsExtractor context) {
      this.render(context, false);
   }

   @Override
   public void render2(GuiGraphicsExtractor context) {
      this.render(context, true);
   }

   private final void render(GuiGraphicsExtractor guiGraphicsExtractor, boolean flag) {
      if (!flag) {
         Entity entity;
         if (!(MinecraftHolder.getMinecraft().gui.screen() instanceof ChatScreen)
            && !(MinecraftHolder.getMinecraft().gui.screen() instanceof WidgetEditorScreen)) {
            entity = Aura.INSTANCE.getEntity();
            if (entity == null) {
               entity = (Entity)(AutoCrystal.INSTANCE.getEnabled() ? AutoCrystal.INSTANCE.getPlayer() : null);
            }
         } else {
            entity = (Entity)MinecraftHolder.getLocalPlayer();
         }

         Entity entity2 = entity;
         if (entity2 != null && entity2 instanceof LivingEntity) {
            livingEntity = (LivingEntity)entity2;
         }

         float value2;
         label82: {
            if (entity2 == null || !entity2.isAlive()) {
               int value3 = value;
               if (1 <= value3 ? value3 >= 20 : true) {
                  value2 = 0.0F;
                  break label82;
               }
            }

            value2 = 1.0F;
         }

         animation.applyDoubleValue(value2);
         if (animation.getAmount() <= 0.0F) {
            livingEntity = null;
         }
      }

      guiGraphicsExtractor.pose().pushMatrix();
      guiGraphicsExtractor.pose().translate(this.getAmount3() + 60.0F, this.getAmount4() + 28.5F);
      guiGraphicsExtractor.pose().scale(Math.clamp(animation.getAmount(), 0.0F, 2.0F), Math.clamp(animation.getAmount(), 0.0F, 2.0F));
      guiGraphicsExtractor.pose().translate(-(this.getAmount3() + 60.0F), -(this.getAmount4() + 28.5F));
      if (livingEntity != null) {
         if (!flag) {
            Widget.defaultImpl(this, guiGraphicsExtractor.pose(), this.getAmount3(), this.getAmount4(), 120.0F, 57.0F, 0.0F, false, 96, null);
            Widget.defaultImpl(this, guiGraphicsExtractor.pose(), this.getAmount3() + 42, this.getAmount4() + 2, 36.0F, 36.0F, 1.0F, false, 64, null);
            Widget.defaultImpl(this, guiGraphicsExtractor.pose(), this.getAmount3() + 81, this.getAmount4() + 2, 36.0F, 17.0F, 1.0F, false, 64, null);
            Widget.defaultImpl(this, guiGraphicsExtractor.pose(), this.getAmount3() + 81, this.getAmount4() + 21, 36.0F, 17.0F, 1.0F, false, 64, null);
            if (livingEntity instanceof Player) {
               this.render(guiGraphicsExtractor, InventoryUtil.fetchInventory((Player)livingEntity), flag);
            }

            if (value > 0) {
               Widget.defaultImpl(
                  this,
                  guiGraphicsExtractor.pose(),
                  this.getAmount3(),
                  this.getAmount4(),
                  120.0F,
                  55.0F,
                  Math.clamp((value + 5.0F + Render3D.INSTANCE.getAmount4()) / 20.0F, 0.0F, 1.0F),
                  false,
                  64,
                  null
               );
            }

            this.updateAmount(120.0F, 55.0F);
         } else {
            this.render3(guiGraphicsExtractor);
            this.render(guiGraphicsExtractor, livingEntity);
            if (livingEntity instanceof Player) {
               this.render(guiGraphicsExtractor, InventoryUtil.fetchInventory((Player)livingEntity), true);
               this.render(guiGraphicsExtractor, (Player)livingEntity);
            }

            this.render2(guiGraphicsExtractor, livingEntity);
         }
      }

      Fonts.getFontRenderer5()
         .render2(
            guiGraphicsExtractor,
            StringsKt.dropLast("EZZZZZZZZZZ", 11 - Math.clamp((long)value, 0, 11)),
            this.getAmount3() + 59.0F,
            this.getAmount4() + 19.0F,
            10
         );
      guiGraphicsExtractor.pose().popMatrix();
   }

   private final void render(GuiGraphicsExtractor guiGraphicsExtractor, Player player) {
      if (value <= 0) {
         guiGraphicsExtractor.pose().pushMatrix();
         guiGraphicsExtractor.pose().translate(this.getAmount3() + 83.0F, this.getAmount4() + 3.5F);
         guiGraphicsExtractor.pose().scale(0.8F, 0.8F);
         guiGraphicsExtractor.item(player.getMainHandItem(), 0, 0);
         guiGraphicsExtractor.itemDecorations(MinecraftHolder.getMinecraft().font, player.getMainHandItem(), 0, 0);
         guiGraphicsExtractor.item(player.getOffhandItem(), 22, 0);
         guiGraphicsExtractor.itemDecorations(MinecraftHolder.getMinecraft().font, player.getOffhandItem(), 22, 0);
         guiGraphicsExtractor.item(Items.TOTEM_OF_UNDYING.getDefaultInstance(), 0, 24);
         guiGraphicsExtractor.pose().popMatrix();
         int name = PlayerTargetUtil.INSTANCE.fetchName(player);
         Fonts.getFontRenderer5()
            .render2(guiGraphicsExtractor, name == 0 ? "-" : "-" + name, this.getAmount3() + 105.0F, this.getAmount4() + 28.5F, Color.white);
      }
   }

   private final void render(GuiGraphicsExtractor guiGraphicsExtractor, LivingEntity livingEntity) {
      if (livingEntity instanceof Player) {
         guiGraphicsExtractor.pose().pushMatrix();
         float amount = (10.0F - Math.clamp(animation2.getAmount(), 0.0F, 10.0F)) / 2.0F;
         float tickCount = amount * (float)Math.sin(livingEntity.tickCount + Render3D.INSTANCE.getAmount4());
         float tickCount2 = amount * (float)Math.cos(livingEntity.tickCount + Render3D.INSTANCE.getAmount4());
         float value2 = amount * tickCount;
         guiGraphicsExtractor.pose().translate(this.getAmount3() + 46.0F + 14.0F + value2, this.getAmount4() + 7.0F + 14.0F - tickCount2);
         guiGraphicsExtractor.pose().rotate((float)Math.toRadians(tickCount));
         guiGraphicsExtractor.pose().scale(1.0F - livingEntity.hurtTime / 100.0F, 1.0F - livingEntity.hurtTime / 100.0F);
         guiGraphicsExtractor.pose().translate(-(this.getAmount3() + 46.0F + 14.0F + value2), -(this.getAmount4() + 7.0F + 14.0F - tickCount2));
         MeshBuilder meshBuilder = new MeshBuilder(DefaultVertexFormat.POSITION_TEX_COLOR, 2, false, 4, null);
         GpuTextureView textureView = MinecraftHolder.getMinecraft()
            .getTextureManager()
            .getTexture(((AbstractClientPlayer)livingEntity).getSkin().body().texturePath())
            .getTextureView();
         Render2D.defaultImpl(
            Render2D.INSTANCE,
            meshBuilder,
            this.getAmount3() + 46.0F,
            this.getAmount4() + 7.0F,
            28.0F,
            28.0F,
            Color.WHITE,
            null,
            null,
            null,
            0.125F,
            0.125F,
            0.25F,
            0.25F,
            448,
            null
         );
         Render2D.defaultImpl(
            Render2D.INSTANCE,
            meshBuilder,
            this.getAmount3() + 44.0F,
            this.getAmount4() + 5.0F,
            32.0F,
            32.0F,
            Color.WHITE,
            null,
            null,
            null,
            0.625F,
            0.125F,
            0.75F,
            0.25F,
            448,
            null
         );
         MeshBuilder.defaultImpl(
            meshBuilder,
            Pipelines.INSTANCE.getRenderPipeline14(),
            MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget(),
            null,
            null,
            (Matrix3x2f)guiGraphicsExtractor.pose(),
            MapsKt.mapOf(TuplesKt.to("Sampler0", textureView)),
            null,
            76,
            null
         );
         guiGraphicsExtractor.pose().scale(1.0F, 1.0F);
         guiGraphicsExtractor.pose().popMatrix();
      }
   }

   private final void render3(GuiGraphicsExtractor guiGraphicsExtractor) {
      MeshBuilder meshBuilder = new MeshBuilder(DefaultVertexFormat.POSITION_TEX_COLOR, items.size(), false, 4, null);
      GpuTextureView textureView = MinecraftHolder.getMinecraft().getTextureManager().getTexture(AtlasTexture.getCatlean()).getTextureView();
      Iterable<TargetInfoAnimations> items2 = items;
      var arrayList = new ArrayList();
      boolean flag = false;

      for (Object object : items2) {
         TargetInfoAnimations targetInfoAnimations = (TargetInfoAnimations)object;
         if (targetInfoAnimations.isFlag()) {
            arrayList.add(object);
         }
      }

      for (Object object2 : arrayList) {
         TargetInfoAnimations targetInfoAnimations2 = (TargetInfoAnimations)object2;
         Color color = Render2D.INSTANCE
            .computeColor(ThemeManager.INSTANCE.getColor2(), (1.0F - (targetInfoAnimations2.getValue() + Render3D.INSTANCE.getAmount4()) / 10.0F) * 0.8F);
         Render2D.defaultImpl(
            Render2D.INSTANCE,
            meshBuilder,
            Render2D.INSTANCE.computeAmount(targetInfoAnimations2.getAmount5(), targetInfoAnimations2.getAmount(), Render3D.INSTANCE.getAmount4()),
            Render2D.INSTANCE.computeAmount(targetInfoAnimations2.getAmount6(), targetInfoAnimations2.getAmount2(), Render3D.INSTANCE.getAmount4()),
            10.0F,
            10.0F,
            color,
            null,
            null,
            null,
            ParticleType.FIREFLY_ALT.getAmount(),
            ParticleType.FIREFLY_ALT.getAmount2(),
            ParticleType.FIREFLY_ALT.getAmount3(),
            ParticleType.FIREFLY_ALT.getAmount4(),
            448,
            null
         );
      }

      items2 = items;
      arrayList = new ArrayList();
      flag = false;

      for (Object object3 : items2) {
         TargetInfoAnimations targetInfoAnimations3 = (TargetInfoAnimations)object3;
         if (!targetInfoAnimations3.isFlag()) {
            arrayList.add(object3);
         }
      }

      for (Object object4 : arrayList) {
         TargetInfoAnimations targetInfoAnimations4 = (TargetInfoAnimations)object4;
         Render2D.defaultImpl(
            Render2D.INSTANCE,
            meshBuilder,
            Render2D.INSTANCE.computeAmount(targetInfoAnimations4.getAmount5(), targetInfoAnimations4.getAmount(), Render3D.INSTANCE.getAmount4()),
            Render2D.INSTANCE.computeAmount(targetInfoAnimations4.getAmount6(), targetInfoAnimations4.getAmount2(), Render3D.INSTANCE.getAmount4()),
            6.0F,
            6.0F,
            Render2D.INSTANCE.computeColor(Color.WHITE, 1.0F - (targetInfoAnimations4.getValue() + Render3D.INSTANCE.getAmount4()) / 60.0F),
            null,
            null,
            null,
            ParticleType.DROP.getAmount(),
            ParticleType.DROP.getAmount2(),
            ParticleType.DROP.getAmount3(),
            ParticleType.DROP.getAmount4(),
            448,
            null
         );
      }

      MeshBuilder.defaultImpl(
         meshBuilder,
         Pipelines.INSTANCE.getRenderPipeline18(),
         MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget(),
         null,
         null,
         (Matrix3x2f)guiGraphicsExtractor.pose(),
         MapsKt.mapOf(TuplesKt.to("Sampler0", textureView)),
         null,
         76,
         null
      );
   }

   private final void render2(GuiGraphicsExtractor guiGraphicsExtractor, LivingEntity livingEntity) {
      Color color = Render2D.INSTANCE.computeColor(ThemeManager.INSTANCE.getColor2(), 0.8F);
      Color color2 = Render2D.INSTANCE.computeColor(ThemeManager.INSTANCE.getColor2(), 0.3F);
      if (livingEntity instanceof Player) {
         animation2.applyDoubleValue(NameTags.INSTANCE.fetchScoreboard((Player)livingEntity, NameTags.INSTANCE.getHealthProvider()));
      } else {
         animation2.applyDoubleValue(livingEntity.getHealth() + livingEntity.getAbsorptionAmount());
      }

      float value2 = 0.0F;
      Iterable<ItemStack> defaultInstance = InventoryUtil.fetchDefaultInstance(livingEntity);

      for (Object object : defaultInstance) {
         ItemStack itemStack = (ItemStack)object;
         value2 += itemStack.getMaxDamage() - itemStack.getDamageValue();
      }

      float coerceIn = RangesKt.coerceIn(value2 / 2035.0F, 0.0F, 1.0F);
      float amount = animation2.getAmount() / 20.0F;
      float value3 = Math.clamp(amount, 0.0F, 1.0F);
      TexturedQuadPass.defaultImpl(
         guiGraphicsExtractor.pose(),
         this.getAmount3() + 3,
         this.getAmount4() + 40,
         114.0F,
         4.0F,
         1.0F,
         1.0F,
         1.0F,
         1.0F,
         color2,
         color2,
         0.0F,
         0.0F,
         0.0F,
         14336,
         null
      );
      TexturedQuadPass.defaultImpl(
         guiGraphicsExtractor.pose(),
         this.getAmount3() + 3,
         this.getAmount4() + 40,
         114.0F * coerceIn,
         4.0F,
         2.0F,
         1.0F,
         1.0F,
         1.0F,
         color,
         color,
         0.0F,
         0.0F,
         0.0F,
         14336,
         null
      );
      TexturedQuadPass.defaultImpl(
         guiGraphicsExtractor.pose(),
         this.getAmount3() + 3,
         this.getAmount4() + 45,
         114.0F,
         9.0F,
         2.0F,
         1.0F,
         1.0F,
         1.0F,
         color2,
         color2,
         0.0F,
         0.0F,
         0.0F,
         14336,
         null
      );
      TexturedQuadPass.defaultImpl(
         guiGraphicsExtractor.pose(),
         this.getAmount3() + 3,
         this.getAmount4() + 45,
         114.0F * value3,
         9.0F,
         2.0F,
         1.0F,
         1.0F,
         1.0F,
         color,
         color,
         0.0F,
         0.0F,
         0.0F,
         14336,
         null
      );
      Fonts.getFontRenderer()
         .render2(
            guiGraphicsExtractor,
            String.valueOf(MathKt.roundToInt(animation2.getAmount() * 10.0) / 10.0),
            this.getAmount3() + 59.5F,
            this.getAmount4() + 48.5F,
            Color.black
         );
      Fonts.getFontRenderer()
         .render2(
            guiGraphicsExtractor,
            String.valueOf(MathKt.roundToInt(animation2.getAmount() * 10.0) / 10.0),
            this.getAmount3() + 59.0F,
            this.getAmount4() + 48.0F,
            Color.white
         );
   }

   private final void render(GuiGraphicsExtractor guiGraphicsExtractor, List items, boolean flag) {
      if (value <= 0) {
         Pair[] pairs = new Pair[]{
            new Pair(this.getAmount3() + 3, this.getAmount4() + 2),
            new Pair(this.getAmount3() + 22, this.getAmount4() + 2),
            new Pair(this.getAmount3() + 22, this.getAmount4() + 21),
            new Pair(this.getAmount3() + 3, this.getAmount4() + 21)
         };
         List<Pair> listOf = CollectionsKt.listOf(pairs);
         Iterable<Pair> iterable = listOf;
         int value2 = 0;

         for (Object object : iterable) {
            int value3 = value2++;
            if (value3 < 0) {
               CollectionsKt.throwIndexOverflow();
            }

            Pair pair = (Pair)object;
            float value4 = ((Number)pair.component1()).floatValue();
            float value5 = ((Number)pair.component2()).floatValue();
            Color black = Color.BLACK;
            float damageValue = (float)((ItemStack)items.get(3 - value3)).getDamageValue() / ((ItemStack)items.get(3 - value3)).getMaxDamage();
            if (!Float.isNaN(damageValue)) {
               black = Render2D.INSTANCE
                  .fetchAlpha(
                     Color.WHITE, Color.RED, (float)((ItemStack)items.get(3 - value3)).getDamageValue() / ((ItemStack)items.get(3 - value3)).getMaxDamage()
                  );
            }

            black = Render2D.INSTANCE.computeColor(black, 0.2F);
            if (!flag) {
               Widget.defaultImpl(INSTANCE, guiGraphicsExtractor.pose(), value4, value5, 17.0F, 17.0F, 0.0F, false, 96, null);
            } else {
               float value6 = value5 + 17 * damageValue;
               float value7 = 17.0F * (1.0F - damageValue);
               Matrix3x2fStack pose = guiGraphicsExtractor.pose();
               Object object2 = null;
               short shortValue = 3072;
               Object object3 = null;
               float value8 = 0.0F;
               Matrix3x2fStack matrix3x2fStack = pose;
               float value9 = 2.0F;
               BoxPass.defaultImpl(value4, value6, 17.0F, value7, value9, black, black, black, black, matrix3x2fStack, value8, (Color)object3, shortValue, object2);
            }

            guiGraphicsExtractor.pose().pushMatrix();
            guiGraphicsExtractor.pose().translate(value4 + 0.5F, value5);
            guiGraphicsExtractor.item((ItemStack)items.get(3 - value3), 0, 0);
            guiGraphicsExtractor.pose().popMatrix();
         }
      }
   }
}
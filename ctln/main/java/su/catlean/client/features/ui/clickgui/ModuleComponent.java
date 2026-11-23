// deobf by pusia
package su.catlean.client.features.ui.clickgui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2f;
import su.catlean.api.data.Rectangle;
import su.catlean.api.setting.Setting;
import su.catlean.api.setting.values.ActionValue;
import su.catlean.api.setting.values.Bind;
import su.catlean.api.setting.values.BlocksValue;
import su.catlean.api.setting.values.EntitiesValue;
import su.catlean.api.setting.values.GroupValue;
import su.catlean.api.setting.values.ItemsValue;
import su.catlean.api.setting.values.NotationValue;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.PlaceModule;
import su.catlean.client.features.modules.PromoModule;
import su.catlean.client.features.modules.misc.ClientInterface;
import su.catlean.client.features.ui.clickgui.settings.ActionComponent;
import su.catlean.client.features.ui.clickgui.settings.BindComponent;
import su.catlean.client.features.ui.clickgui.settings.BlocksComponent;
import su.catlean.client.features.ui.clickgui.settings.BooleanComponent;
import su.catlean.client.features.ui.clickgui.settings.ColorComponent;
import su.catlean.client.features.ui.clickgui.settings.EntitiesComponent;
import su.catlean.client.features.ui.clickgui.settings.EnumComponent;
import su.catlean.client.features.ui.clickgui.settings.FloatComponent;
import su.catlean.client.features.ui.clickgui.settings.GroupComponent;
import su.catlean.client.features.ui.clickgui.settings.IntComponent;
import su.catlean.client.features.ui.clickgui.settings.ItemsComponent;
import su.catlean.client.features.ui.clickgui.settings.NotationComponent;
import su.catlean.client.features.ui.clickgui.settings.SettingComponent;
import su.catlean.client.features.ui.clickgui.settings.StringComponent;
import su.catlean.client.render.SvgTexture;
import su.catlean.client.render.TexturedQuadPass;
import su.catlean.client.render.font.FontRenderer;
import su.catlean.client.render.font.Fonts;
import su.catlean.client.services.NotificationManager;
import su.catlean.client.services.SoundManager;
import su.catlean.client.services.ThemeManager;
import su.catlean.utils.KeyNames;
import su.catlean.utils.animation.Animation;
import su.catlean.utils.input.InputUtil;
import su.catlean.utils.render.Render2D;

public final class ModuleComponent {
   @NotNull
   private final CategoryWindow catComponent;
   @NotNull
   private final Module module;
   private float amount;
   private float amount2;
   private float amount3;
   private float amount4;
   @NotNull
   private final Animation animation;
   @NotNull
   private final Animation animation2;
   @NotNull
   private final Animation animation3;
   @NotNull
   private final Animation animation4;
   @NotNull
   private final Animation animation5;
   @NotNull
   private final Animation animation6;
   @NotNull
   private final Animation animation7;
   @NotNull
   private final List<SettingComponent> items;
   private boolean flag;
   private boolean flag2;
   private long time;

   public ModuleComponent(CategoryWindow catComponent, Module module, float amount, float amount2) {
      this.catComponent = catComponent;
      this.module = module;
      this.amount = amount;
      this.amount2 = amount2;
      this.amount3 = 120.0F;
      this.amount4 = 17.0F;
      this.animation = new Animation(NotificationManager.Notification.Type.OUT_QUINT, 200L);
      this.animation2 = new Animation(NotificationManager.Notification.Type.OUT_QUINT, 200L);
      this.animation3 = new Animation(NotificationManager.Notification.Type.OUT_QUINT, 300L);
      this.animation4 = new Animation(NotificationManager.Notification.Type.OUT_QUINT, 200L);
      this.animation5 = new Animation(NotificationManager.Notification.Type.OUT_QUINT, 200L);
      this.animation6 = new Animation(NotificationManager.Notification.Type.OUT_QUINT, 200L);
      this.animation7 = new Animation(NotificationManager.Notification.Type.OUT_QUINT, 200L);
      this.items = new ArrayList();

      for (Object items5Item : this.module.getItems5()) {
         Setting setting = (Setting)items5Item;
         Object object2 = setting.getObject2();
         SettingComponent booleanComponent;
         if (object2 instanceof Boolean) {
            String text = "null cannot be cast to non-null type su.catlean.api.setting.Setting<kotlin.Boolean>";
            booleanComponent = new BooleanComponent(setting);
         } else if (object2 instanceof Float) {
            String text2 = "null cannot be cast to non-null type su.catlean.api.setting.Setting<kotlin.Float>";
            booleanComponent = new FloatComponent(setting);
         } else if (object2 instanceof Integer) {
            String text3 = "null cannot be cast to non-null type su.catlean.api.setting.Setting<kotlin.Int>";
            booleanComponent = new IntComponent(setting);
         } else if (object2 instanceof Bind) {
            String text4 = "null cannot be cast to non-null type su.catlean.api.setting.Setting<su.catlean.api.setting.values.Bind>";
            booleanComponent = new BindComponent(setting);
         } else if (object2 instanceof String) {
            String text5 = "null cannot be cast to non-null type su.catlean.api.setting.Setting<kotlin.String>";
            booleanComponent = new StringComponent(setting);
         } else if (object2 instanceof Color) {
            String text6 = "null cannot be cast to non-null type su.catlean.api.setting.Setting<java.awt.Color>";
            booleanComponent = new ColorComponent(setting);
         } else if (object2 instanceof GroupValue) {
            String text7 = "null cannot be cast to non-null type su.catlean.api.setting.Setting<su.catlean.api.setting.Group>";
            booleanComponent = new GroupComponent(setting, this);
         } else if (object2 instanceof BlocksValue) {
            String text8 = "null cannot be cast to non-null type su.catlean.api.setting.Setting<su.catlean.api.setting.values.BlocksValue>";
            booleanComponent = new BlocksComponent(setting);
         } else if (object2 instanceof EntitiesValue) {
            String text9 = "null cannot be cast to non-null type su.catlean.api.setting.Setting<su.catlean.api.setting.values.EntitiesValue>";
            booleanComponent = new EntitiesComponent(setting);
         } else if (object2 instanceof ItemsValue) {
            String text10 = "null cannot be cast to non-null type su.catlean.api.setting.Setting<su.catlean.api.setting.values.ItemsValue>";
            booleanComponent = new ItemsComponent(setting);
         } else if (object2 instanceof ActionValue) {
            String text11 = "null cannot be cast to non-null type su.catlean.api.setting.Setting<su.catlean.api.setting.values.ActionValue>";
            booleanComponent = new ActionComponent(setting);
         } else if (object2 instanceof NotationValue) {
            String text12 = "null cannot be cast to non-null type su.catlean.api.setting.Setting<su.catlean.api.setting.values.NotationValue>";
            booleanComponent = new NotationComponent(setting);
         } else {
            String text13 = "null cannot be cast to non-null type su.catlean.api.setting.Setting<kotlin.Enum<*>>";
            booleanComponent = new EnumComponent(setting);
         }

         SettingComponent settingComponent = booleanComponent;
         if (!Intrinsics.areEqual(setting.getId(), "enabled") && !Intrinsics.areEqual(setting.getId(), "bind")) {
            this.items.add(settingComponent);
         }
      }
   }

   @NotNull
   public final Module getModule() {
      return this.module;
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

   @NotNull
   public final List getItems() {
      Iterable<SettingComponent> items2 = this.items;
      var arrayList = new ArrayList();

      for (Object object : items2) {
         SettingComponent settingComponent = (SettingComponent)object;
         if ((Boolean)settingComponent.getSetting().getVisible().invoke()) {
            arrayList.add(object);
         }
      }

      return arrayList;
   }

   public final boolean isFlag() {
      return this.flag;
   }

   public final void setFlag(boolean flag) {
      this.flag = flag;
   }

   public final void render(GuiGraphicsExtractor context, int value, int value2, float amount, boolean flag) {
      float value3;
      label145: {
         this.amount4 = this.flag ? 31.0F : 17.0F;
         this.animation.applyDoubleValue(this.module.getEnabled() ? 1.0F : 0.0F);
         float amount5 = this.amount;
         float amount6 = this.amount + this.amount3 - 6;
         float value4 = value;
         if (amount5 <= value4 ? value4 <= amount6 : false) {
            amount5 = this.amount2;
            amount6 = this.amount2 + this.amount4;
            value4 = value2;
            if ((amount5 <= value4 ? value4 <= amount6 : false) && !this.flag) {
               value3 = 0.5F;
               break label145;
            }
         }

         value3 = 0.0F;
      }

      this.animation2.applyDoubleValue(value3);
      this.animation3.applyDoubleValue(this.flag ? 1.0F : 0.0F);
      this.animation7.applyDoubleValue(InputUtil.checkContains(342) ? 0.0F : 1.0F);
      this.amount4 = 17.0F + 14.0F * this.animation3.getAmount();
      Color alpha = Render2D.INSTANCE.fetchAlpha(ThemeManager.INSTANCE.getColor10(), ThemeManager.INSTANCE.getColor11(), this.animation.getAmount());
      Color alpha2 = Render2D.INSTANCE.fetchAlpha(ThemeManager.INSTANCE.getColor13(), ThemeManager.INSTANCE.getColor12(), this.animation.getAmount());
      if (ClientInterface.INSTANCE.getModuleMode() == ClientInterface.ModuleMode.DEFAULT) {
         TexturedQuadPass.defaultImpl(
            context.pose(),
            this.amount,
            this.amount2,
            this.amount3 - 6,
            17.0F + 14.0F * this.animation3.getAmount(),
            4.0F,
            2.5F + this.animation2.getAmount(),
            1.0F,
            1.0F,
            alpha,
            alpha2,
            0.0F,
            0.0F,
            0.0F,
            14336,
            null
         );
      } else if (ClientInterface.INSTANCE.getModuleMode() == ClientInterface.ModuleMode.RECT) {
         List<ModuleComponent> items2 = this.catComponent.getItems2();
         ListIterator<ModuleComponent> listIterator = items2.listIterator(items2.size());

         ModuleComponent previous;
         ModuleComponent moduleComponent;
         do {
            if (!listIterator.hasPrevious()) {
               throw new NoSuchElementException("List contains no element matching the predicate.");
            }

            previous = listIterator.previous();
            moduleComponent = (ModuleComponent)previous;
         } while (!Intrinsics.areEqual(moduleComponent.module.getCategory(), this.catComponent.getSelector().getSubCategory()));

         if (Intrinsics.areEqual(((ModuleComponent)previous).module.getId(), this.module.getId())) {
            TexturedQuadPass.render(
               context.pose(),
               this.amount,
               this.amount2,
               this.amount3 - 6,
               17.0F + 14.0F * this.animation3.getAmount(),
               0.0F,
               2.5F + this.animation2.getAmount(),
               1.0F,
               1.0F,
               alpha,
               alpha2,
               4.0F,
               4.0F,
               0.0F
            );
         } else {
            TexturedQuadPass.defaultImpl(
               context.pose(),
               this.amount,
               this.amount2,
               this.amount3 - 6,
               17.0F + 14.0F * this.animation3.getAmount(),
               0.0F,
               2.5F + this.animation2.getAmount(),
               1.0F,
               1.0F,
               alpha,
               alpha2,
               0.0F,
               0.0F,
               0.0F,
               14336,
               null
            );
         }
      } else if (this.module.getEnabled()) {
         List<ModuleComponent> items22 = this.catComponent.getItems2();
         ListIterator<ModuleComponent> listIterator2 = items22.listIterator(items22.size());

         ModuleComponent previous2;
         ModuleComponent moduleComponent2;
         do {
            if (!listIterator2.hasPrevious()) {
               throw new NoSuchElementException("List contains no element matching the predicate.");
            }

            previous2 = listIterator2.previous();
            moduleComponent2 = (ModuleComponent)previous2;
         } while (!Intrinsics.areEqual(moduleComponent2.module.getCategory(), this.catComponent.getSelector().getSubCategory()));

         if (Intrinsics.areEqual(((ModuleComponent)previous2).module.getId(), this.module.getId())) {
            TexturedQuadPass.render(
               context.pose(),
               this.amount - 3.0F,
               this.amount2,
               this.amount3,
               20.0F + 14.0F * this.animation3.getAmount(),
               0.0F,
               0.0F,
               1.0F,
               1.0F,
               alpha,
               alpha2,
               7.0F,
               7.0F,
               0.0F
            );
         } else {
            TexturedQuadPass.defaultImpl(
               context.pose(),
               this.amount - 3.0F,
               this.amount2,
               this.amount3,
               19.0F + 14.0F * this.animation3.getAmount(),
               0.0F,
               0.0F,
               1.0F,
               1.0F,
               alpha,
               alpha2,
               0.0F,
               0.0F,
               0.0F,
               14336,
               null
            );
         }
      }

      Color alpha3 = Render2D.INSTANCE.fetchAlpha(Color.BLACK, ThemeManager.INSTANCE.getColor7(), amount);
      boolean flag5 = this.animation2.getAmount() != 0.0F && InputUtil.checkContains(341) && InputUtil.checkContains(340);
      Object currentTimeMillis = System.currentTimeMillis() - this.time >= 400L && !flag5 ? 0.0F : Math.sin(System.currentTimeMillis() / 15.0) * (flag5 ? 0.5F : 1.0F);
      FontRenderer.defaultImpl(
         Fonts.getFontRenderer2(),
         context.pose(),
         this.module.getText3(),
         this.amount
            + 4.0F
            + ((Number)currentTimeMillis).floatValue()
            + ((this.amount3 - 8.0F) / 2.0F - Fonts.getFontRenderer2().computeAmount(this.module.getText3()) / 2.0F)
               * (
                  ClientInterface.INSTANCE.getModuleText() == ClientInterface.ModuleText.LEFT
                     ? 0.0F
                     : (this.module.getBind().getValue() != -1 ? this.animation7.getAmount() : 1.0F)
               ),
         this.amount2 + this.amount4 / 2.0F - 7.0F,
         Render2D.INSTANCE.computeColor(alpha3, 1.0F - this.animation3.getAmount()),
         false,
         0,
         96,
         null
      );
      if (this.module instanceof PromoModule && this.animation3.getAmount() < 1.0F) {
         SvgTexture.defaultImpl(
            Render2D.INSTANCE.getSvgTexture(),
            (Matrix3x2f)context.pose(),
            this.amount + this.amount3 - 18.0,
            this.amount2 + 3.0,
            0.0F,
            0.0F,
            ThemeManager.INSTANCE.getColor7(),
            null,
            (1.0F - this.animation3.getAmount()) * amount,
            88,
            null
         );
      }

      if (this.animation7.getAmount() < 1.0F && !this.flag) {
         String text = KeyNames.INSTANCE.computeText(this.module.getBind().getValue());
         if (this.module.getBind().getValue() != -1) {
            FontRenderer.defaultImpl(
               Fonts.getFontRenderer2(),
               context.pose(),
               text,
               this.amount + this.amount3 - 10.0F - Fonts.getFontRenderer2().computeAmount(text),
               this.amount2 + this.amount4 / 2.0F - 7.0F,
               Render2D.INSTANCE.computeColor(ThemeManager.INSTANCE.getColor8(), 1.0F - this.animation7.getAmount()),
               false,
               0,
               96,
               null
            );
         }
      }

      if (this.animation3.getAmount() > 0.0F) {
         this.render(context, value, value2, alpha3, alpha2);
      }
   }

   private final void render(GuiGraphicsExtractor guiGraphicsExtractor, int value, int value2, Color color, Color color2) {
      float value3;
      label57: {
         float amount5 = this.amount + 58.0F;
         float amount6 = this.amount + 58.0F + this.amount3 / 2.0F - 6;
         float value4 = value;
         if (amount5 <= value4 ? value4 <= amount6 : false) {
            amount5 = this.amount2 + 15.0F;
            amount6 = this.amount2 + 27.0F;
            value4 = value2;
            if (amount5 <= value4 ? value4 <= amount6 : false) {
               value3 = 1.0F;
               break label57;
            }
         }

         value3 = 0.0F;
      }

      label50: {
         this.animation5.applyDoubleValue(value3);
         float amount7 = this.amount + 2.0F;
         float amount8 = this.amount + 2.0F + this.amount3 / 2.0F - 6;
         float value5 = value;
         if (amount7 <= value5 ? value5 <= amount8 : false) {
            amount7 = this.amount2 + 15.0F;
            amount8 = this.amount2 + 27.0F;
            value5 = value2;
            if (amount7 <= value5 ? value5 <= amount8 : false) {
               value3 = 1.0F;
               break label50;
            }
         }

         value3 = 0.0F;
      }

      this.animation4.applyDoubleValue(value3);
      this.animation6.applyDoubleValue(this.flag2 ? 1.0F : 0.0F);
      Render2D instance = Render2D.INSTANCE;
      Rectangle rectangle = new Rectangle(this.amount, this.amount2, this.amount + this.amount3, this.amount2 + this.amount4);
      instance.render(guiGraphicsExtractor, rectangle);
      FontRenderer.defaultImpl(
         Fonts.getFontRenderer2(),
         guiGraphicsExtractor.pose(),
         StringsKt.dropLast("Press any key                    ...", 3 - (int)(4 * (System.currentTimeMillis() % 700 / 700.0))),
         this.amount + 3.0F + 60 * (1.0F - this.animation3.getAmount()),
         this.amount2 + 2.0F,
         Render2D.INSTANCE.computeColor(color, this.animation3.getAmount() - 0.1F),
         false,
         0,
         96,
         null
      );
      TexturedQuadPass.defaultImpl(
         guiGraphicsExtractor.pose(),
         this.amount + 2.0F,
         this.amount2 + 17.0F,
         this.amount3 / 2.0F - 6,
         12.0F,
         3.0F,
         2.5F + this.animation4.getAmount() / 2.0F,
         1.0F - this.animation6.getAmount(),
         1.0F,
         Render2D.INSTANCE.fetchAlpha(ThemeManager.INSTANCE.getColor10(), ThemeManager.INSTANCE.getColor11(), 1.0F - this.animation6.getAmount()),
         color2,
         0.0F,
         0.0F,
         0.0F,
         14336,
         null
      );
      Fonts.getFontRenderer10()
         .render(
            guiGraphicsExtractor,
            "Toggle",
            this.amount + 15.0F,
            this.amount2 + 17.0F,
            Render2D.INSTANCE
               .fetchAlpha(
                  ThemeManager.INSTANCE.getColor8(),
                  ThemeManager.INSTANCE.getColor7(),
                  this.animation4.getAmount() + (1.0F - this.animation6.getAmount()) / 2.0F
               )
         );
      TexturedQuadPass.defaultImpl(
         guiGraphicsExtractor.pose(),
         this.amount + 58.0F,
         this.amount2 + 17.0F,
         this.amount3 / 2.0F - 6,
         12.0F,
         3.0F,
         2.5F + this.animation5.getAmount() / 2.0F,
         this.animation6.getAmount(),
         1.0F,
         Render2D.INSTANCE.fetchAlpha(ThemeManager.INSTANCE.getColor10(), ThemeManager.INSTANCE.getColor11(), this.animation6.getAmount()),
         color2,
         0.0F,
         0.0F,
         0.0F,
         14336,
         null
      );
      Fonts.getFontRenderer10()
         .render(
            guiGraphicsExtractor,
            "Hold",
            this.amount + 76.0F,
            this.amount2 + 17.0F,
            Render2D.INSTANCE
               .fetchAlpha(
                  ThemeManager.INSTANCE.getColor8(), ThemeManager.INSTANCE.getColor7(), this.animation5.getAmount() + this.animation6.getAmount() / 2.0F
               )
         );
      Render2D.INSTANCE.render(guiGraphicsExtractor);
   }

   public final void applyIsEmpty(double amount, double amount2, int value) {
      if (this.module instanceof PromoModule) {
         float amount5 = this.amount;
         if (amount <= this.amount + this.amount3 - 6 ? amount5 <= amount : false) {
            amount5 = this.amount2;
            if (amount2 <= this.amount2 + this.amount4 ? amount5 <= amount2 : false) {
               if (value == 0) {
                  ((PromoModule)this.module).applyCreate();
               }

               return;
            }
         }
      }

      label142:
      if (this.flag) {
         float amount6 = this.amount + 58.0F;
         float amount7 = this.amount + 58.0F + this.amount3 / 2.0F - 6;
         float amount8 = (float)amount;
         if (amount6 <= amount8 ? amount8 <= amount7 : false) {
            amount6 = this.amount2 + 15.0F;
            amount7 = this.amount2 + 27.0F;
            amount8 = (float)amount2;
            if ((amount6 <= amount8 ? amount8 <= amount7 : false) && value == 0) {
               this.flag2 = true;
               break label142;
            }
         }

         amount6 = this.amount + 2.0F;
         amount7 = this.amount + 2.0F + this.amount3 / 2.0F - 6;
         amount8 = (float)amount;
         if (amount6 <= amount8 ? amount8 <= amount7 : false) {
            amount6 = this.amount2 + 15.0F;
            amount7 = this.amount2 + 27.0F;
            amount8 = (float)amount2;
            if ((amount6 <= amount8 ? amount8 <= amount7 : false) && value == 0) {
               this.flag2 = false;
               break label142;
            }
         }

         this.module.updateBind(new Bind(value, this.flag2, true));
         this.flag = false;
      }

      float amount9 = this.amount;
      if (amount <= this.amount + this.amount3 - 6 ? amount9 <= amount : false) {
         amount9 = this.amount2;
         if ((amount2 <= this.amount2 + this.amount4 ? amount9 <= amount2 : false) && !this.flag) {
            if (InputUtil.checkContains(341) && InputUtil.checkContains(340)) {
               if (this.module.getEnabled()) {
                  this.module.applyUnplug();
               }

               for (Setting items5Item : (Iterable<Setting>)this.module.getItems5()) {
                  items5Item.update();
               }
            } else if (InputUtil.checkContains(342)) {
               this.flag = true;
            } else {
               switch (value) {
                  case 0:
                     this.module.update5();
                     break;
                  case 1:
                     if (!this.items.isEmpty()) {
                        SoundManager.INSTANCE.getSound4().applyExecuteIfPossible();
                        this.catComponent.setModuleComponent(this);
                     } else {
                        this.applyCurrentTimeMillis();
                     }
                     break;
                  case 2:
                     this.flag = true;
               }
            }
         }
      }
   }

   public final void applyCurrentTimeMillis() {
      SoundManager.INSTANCE.getSound6().applyExecuteIfPossible();
      this.time = System.currentTimeMillis();
   }

   public final void updateValue(int value) {
      if (Intrinsics.areEqual(this.catComponent.getModuleComponent(), this)) {
         for (Object item : this.items) {
            SettingComponent settingComponent = (SettingComponent)item;
            settingComponent.updateValue(value);
         }
      }

      if (this.flag) {
         switch (value) {
            case 256:
            case 261:
               this.module.updateBind(new Bind(-1, this.flag2, false));
               break;
            default:
               this.module.updateBind(new Bind(value, this.flag2, false));
         }

         this.flag = false;
      }
   }

   public final void updateSymbol(char symbol) {
      if (Intrinsics.areEqual(this.catComponent.getModuleComponent(), this)) {
         for (Object item : this.items) {
            SettingComponent settingComponent = (SettingComponent)item;
            settingComponent.updateSymbol(symbol);
         }
      }
   }

   public final boolean checkBooleanValue(double amount, double amount2, double amount3) {
      boolean flag3 = false;
      if (Intrinsics.areEqual(this.catComponent.getModuleComponent(), this)) {
         for (Object item : this.items) {
            SettingComponent settingComponent = (SettingComponent)item;
            if ((
                  settingComponent.getSetting().getGroupValue() == null
                     || settingComponent.getSetting().getGroupValue().isFlag() && settingComponent.getSetting().getGroupValue().getType() != PlaceModule.Pause.BOOLS
               )
               && (Boolean)settingComponent.getSetting().getVisible().invoke()
               && settingComponent.testAmount(amount, amount2, amount3)) {
               flag3 = true;
            }
         }
      }

      return flag3;
   }

   public final float computeAmount(GroupValue group) {
      if (!group.isFlag() && group.getType() != PlaceModule.Pause.BOOLS) {
         return 13.0F;
      } else {
         float value = 0.0F;
         value = 13.0F;
         Iterable<SettingComponent> items2 = this.items;
         var arrayList = new ArrayList();

         for (Object object : items2) {
            SettingComponent settingComponent = (SettingComponent)object;
            if (Intrinsics.areEqual(settingComponent.getSetting().getGroupValue(), group) && (Boolean)settingComponent.getSetting().getVisible().invoke()) {
               arrayList.add(object);
            }
         }

         List list = arrayList;

         for (Object object2 : list) {
            SettingComponent settingComponent2 = (SettingComponent)object2;
            value += settingComponent2.getAmount6() + 2.0F;
         }

         return value;
      }
   }
}
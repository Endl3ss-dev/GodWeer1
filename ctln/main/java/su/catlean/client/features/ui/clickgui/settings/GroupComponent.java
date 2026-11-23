// deobf by pusia
package su.catlean.client.features.ui.clickgui.settings;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import kotlin.collections.CollectionsKt;
import kotlin.comparisons.ComparisonsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2f;
import org.lwjgl.glfw.GLFW;
import su.catlean.api.setting.Setting;
import su.catlean.api.setting.values.GroupValue;
import su.catlean.client.features.modules.PlaceModule;
import su.catlean.client.features.ui.ClickGuiScreen;
import su.catlean.client.features.ui.clickgui.ModuleComponent;
import su.catlean.client.render.SvgTexture;
import su.catlean.client.render.TexturedQuadPass;
import su.catlean.client.render.font.FontRenderer;
import su.catlean.client.render.font.Fonts;
import su.catlean.client.services.NotificationManager;
import su.catlean.client.services.ThemeManager;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.animation.Animation;
import su.catlean.utils.render.Render2D;

public final class GroupComponent extends SettingComponent {
   @NotNull
   private final Setting setting;
   @NotNull
   private final ModuleComponent module;
   @NotNull
   private final Animation animation;
   @NotNull
   private final SvgTexture svgTexture;
   @NotNull
   private List<GroupComponent.GroupSelector> items;
   private float amount;

   public GroupComponent(Setting setting, ModuleComponent module) {
      super(setting);
      this.setting = setting;
      this.module = module;
      this.animation = new Animation(NotificationManager.Notification.Type.OUT_QUINT, 400L);
      this.svgTexture = new SvgTexture("extra", 10, 10);
      this.items = new ArrayList();
      if (((GroupValue)this.getSetting().getObject2()).getType() == PlaceModule.Pause.BOOLS) {
         float value = 0.0F;
         value = 2.0F;
         Iterable items5 = this.module.getModule().getItems5();
         var arrayList = new ArrayList();

         for (Object object : items5) {
            Setting setting2 = (Setting)object;
            if (Intrinsics.areEqual(setting2.getGroupValue(), this.getSetting().getObject2())) {
               arrayList.add(object);
            }
         }

         List list = arrayList;
         items5 = list;

         for (Object sortedWithItem : CollectionsKt.sortedWith(items5, new GroupComponent.Ordering())) {
            Setting setting3 = (Setting)sortedWithItem;
            String text = setting3.getText();
            float amount2 = Fonts.getFontRenderer4().computeAmount(text) + 6.0F;
            if (value + amount2 > this.getAmount5() - 10.0F) {
               this.amount += 12.0F;
               value = 2.0F;
            }

            String text2 = "null cannot be cast to non-null type su.catlean.api.setting.Setting<kotlin.Boolean>";
            this.items.add(new GroupComponent.GroupSelector(value, this.amount, amount2, setting3, null, 16, null));
            value += amount2 + 2.0F;
         }
      }
   }

   @NotNull
   @Override
   public Setting getSetting() {
      return this.setting;
   }

   @NotNull
   public final ModuleComponent getModule() {
      return this.module;
   }

   @Override
   public void render(GuiGraphicsExtractor context, int value, int value2, float amount) {
      if (((GroupValue)this.getSetting().getObject2()).getType() == PlaceModule.Pause.BOOLS) {
         FontRenderer.defaultImpl(
            Fonts.getFontRenderer3(),
            context.pose(),
            this.getSetting().getText(),
            this.getAmount3(),
            this.getAmount4() - 1.0F,
            ThemeManager.INSTANCE.getColor7(),
            false,
            0,
            96,
            null
         );
         this.setAmount5(this.amount + 26.0F);
         Iterable<GroupComponent.GroupSelector> items2 = this.items;

         for (Object object : items2) {
            GroupComponent.GroupSelector groupSelector = (GroupComponent.GroupSelector)object;
            groupSelector.getAnimation().applyDoubleValue((boolean)groupSelector.getSetting().getObject2() ? 1.0F : 0.0F);
            Color brighter = Render2D.INSTANCE
               .fetchAlpha(ThemeManager.INSTANCE.getColor10(), ThemeManager.INSTANCE.getColor11(), groupSelector.getAnimation().getAmount())
               .brighter();
            Color brighter2 = Render2D.INSTANCE
               .fetchAlpha(ThemeManager.INSTANCE.getColor13(), ThemeManager.INSTANCE.getColor12(), groupSelector.getAnimation().getAmount())
               .brighter();
            TexturedQuadPass.defaultImpl(
               context.pose(),
               this.getAmount3() + groupSelector.getAmount(),
               this.getAmount4() + groupSelector.getAmount2() + 13.0F,
               groupSelector.getAmount3(),
               10.0F,
               2.0F,
               2.5F,
               0.8F,
               1.0F,
               brighter,
               brighter2,
               0.0F,
               0.0F,
               0.0F,
               14336,
               null
            );
            Fonts.getFontRenderer4()
               .render2(
                  context,
                  groupSelector.getSetting().getText(),
                  this.getAmount3() + groupSelector.getAmount() + groupSelector.getAmount3() / 2.0F - 1.0F,
                  this.getAmount4() - 1.0F + groupSelector.getAmount2() + 18.0F,
                  ThemeManager.INSTANCE.getColor7()
               );
            float amount3 = this.getAmount3() + groupSelector.getAmount() + 1.0F;
            float amount32 = this.getAmount3() + groupSelector.getAmount() + 1.0F + groupSelector.getAmount3();
            float value3 = value;
            if (amount3 <= value3 ? value3 <= amount32 : false) {
               amount3 = this.getAmount4() - 1.0F + groupSelector.getAmount2() + 14.0F;
               amount32 = this.getAmount4() + groupSelector.getAmount2() + 23.0F;
               value3 = value2;
               if ((amount3 <= value3 ? value3 <= amount32 : false) && GLFW.glfwGetPlatform() != 393219) {
                  GLFW.glfwSetCursor(MinecraftHolder.getMinecraft().getWindow().handle(), GLFW.glfwCreateStandardCursor(221188));
                  ClickGuiScreen.INSTANCE.setFlag(false);
               }
            }
         }
      } else {
         this.animation.applyDoubleValue(((GroupValue)this.getSetting().getObject2()).isFlag() ? 1.0F : 0.0F);
         float amount2 = this.module.computeAmount((GroupValue)this.getSetting().getObject2())
            + (((GroupValue)this.getSetting().getObject2()).isFlag() ? 1.0F : 0.0F);
         float amount4 = this.animation.getAmount() * 2;
         this.setAmount5(13.0F);
         TexturedQuadPass.defaultImpl(
            context.pose(),
            this.getAmount3() - amount4,
            this.getAmount4() - 1.0F,
            this.getAmount5() - 7.0F + amount4 * 2,
            amount2,
            3.0F,
            2.5F,
            this.animation.getAmount() / 2.0F,
            1.0F,
            ThemeManager.INSTANCE.getColor11(),
            ThemeManager.INSTANCE.getColor12(),
            0.0F,
            0.0F,
            0.0F,
            14336,
            null
         );
         FontRenderer.defaultImpl(
            Fonts.getFontRenderer3(),
            context.pose(),
            this.getSetting().getText(),
            this.getAmount3() + 3.0F,
            this.getAmount4() - 1.0F,
            ThemeManager.INSTANCE.getColor7(),
            false,
            0,
            96,
            null
         );
         SvgTexture.defaultImpl(
            this.svgTexture,
            (Matrix3x2f)context.pose(),
            this.getAmount3() + this.getAmount5() - 18.0,
            this.getAmount4() - 1.0,
            0.0F,
            0.0F,
            ThemeManager.INSTANCE.getColor7(),
            null,
            0.0F,
            216,
            null
         );
      }
   }

   @Override
   public void updateAmount(double amount, double amount2, int value) {
      if (((GroupValue)this.getSetting().getObject2()).getType() == PlaceModule.Pause.BOOLS) {
         for (Object item : this.items) {
            GroupComponent.GroupSelector groupSelector = (GroupComponent.GroupSelector)item;
            float amount3 = this.getAmount3() + groupSelector.getAmount();
            if (amount <= this.getAmount3() + groupSelector.getAmount() + groupSelector.getAmount3() ? amount3 <= amount : false) {
               amount3 = this.getAmount4() - 1.0F + groupSelector.getAmount2() + 14.0F;
               if (amount2 <= this.getAmount4() + groupSelector.getAmount2() + 23.0F ? amount3 <= amount2 : false) {
                  groupSelector.getSetting().setObject2(!(Boolean)groupSelector.getSetting().getObject2());
               }
            }
         }
      } else {
         float amount32 = this.getAmount3();
         if (amount <= this.getAmount3() + (this.getAmount5() - 7) ? amount32 <= amount : false) {
            amount32 = this.getAmount4();
            if (amount2 <= this.getAmount4() + 13.0F ? amount32 <= amount2 : false) {
               ((GroupValue)this.getSetting().getObject2()).setFlag(!((GroupValue)this.getSetting().getObject2()).isFlag());
            }
         }
      }
   }

   @Override
   public void updateAmount2(double amount, double amount2, int value) {
   }

   @Override
   public void updateValue(int value) {
   }

   @Override
   public void updateSymbol(char symbol) {
   }

   @Override
   public boolean testAmount(double amount, double amount2, double amount3) {
      return false;
   }

   @Override
   public void update() {
   }

   public static final class GroupSelector {
      private final float amount;
      private final float amount2;
      private final float amount3;
      @NotNull
      private final Setting setting;
      @NotNull
      private final Animation animation;

      public GroupSelector(float amount, float amount2, float amount3, Setting setting, Animation animation) {
         this.amount = amount;
         this.amount2 = amount2;
         this.amount3 = amount3;
         this.setting = setting;
         this.animation = animation;
      }

      public GroupSelector(
         float amount, float amount2, float amount3, Setting setting, Animation animation, int value, DefaultConstructorMarker defaultConstructorMarker
      ) {
         if ((value & 16) != 0) {
            animation = new Animation(NotificationManager.Notification.Type.OUT_EXPO, 600L);
         }

         this(amount, amount2, amount3, setting, animation);
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

      @NotNull
      public final Setting getSetting() {
         return this.setting;
      }

      @NotNull
      public final Animation getAnimation() {
         return this.animation;
      }

      public final float getAmount4() {
         return this.amount;
      }

      public final float getAmount5() {
         return this.amount2;
      }

      public final float getAmount6() {
         return this.amount3;
      }

      @NotNull
      public final Setting getSetting2() {
         return this.setting;
      }

      @NotNull
      public final Animation getAnimation2() {
         return this.animation;
      }

      @NotNull
      @Override
      public String toString() {
         Animation animation2 = this.animation;
         Setting setting2 = this.setting;
         float amount32 = this.amount3;
         float amount22 = this.amount2;
         float amount4 = this.amount;
         return "GroupSelector(x=" + amount4 + ", y=" + amount22 + ", w=" + amount32 + ", setting=" + setting2 + ", animation=" + animation2 + ")";
      }

      @Override
      public int hashCode() {
         int hashCode = Float.hashCode(this.amount);
         hashCode = hashCode * 31 + Float.hashCode(this.amount2);
         hashCode = hashCode * 31 + Float.hashCode(this.amount3);
         hashCode = hashCode * 31 + this.setting.hashCode();
         return hashCode * 31 + this.animation.hashCode();
      }

      @Override
      public boolean equals(Object object) {
         if (this == object) {
            return true;
         } else if (!(object instanceof GroupComponent.GroupSelector groupSelector)) {
            return false;
         } else if (Float.compare(this.amount, groupSelector.amount) != 0) {
            return false;
         } else if (Float.compare(this.amount2, groupSelector.amount2) != 0) {
            return false;
         } else if (Float.compare(this.amount3, groupSelector.amount3) != 0) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.setting, groupSelector.setting) ? false : Intrinsics.areEqual(this.animation, groupSelector.animation);
         }
      }
   }

   public static final class Ordering implements Comparator {
      public Ordering() {
      }

      @Override
      public final int compare(Object object, Object object2) {
         Setting object3 = (Setting)object;
         Comparable length = object3.getText().length();
         object3 = (Setting)object2;
         Comparable comparable = length;
         return ComparisonsKt.compareValues(comparable, object3.getText().length());
      }
   }
}
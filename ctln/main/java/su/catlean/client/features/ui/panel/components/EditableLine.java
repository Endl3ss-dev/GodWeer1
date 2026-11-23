// deobf by pusia
package su.catlean.client.features.ui.panel.components;

import su.catlean.utils.KotlinCompat;
import java.awt.Color;
import java.util.Collection;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fStack;
import su.catlean.client.commands.SuggestionsHelper;
import su.catlean.client.features.ui.panel.Panel;
import su.catlean.client.features.ui.panel.PanelScreen;
import su.catlean.client.render.TexturedQuadPass;
import su.catlean.client.render.font.FontRenderer;
import su.catlean.client.render.font.Fonts;
import su.catlean.client.services.ThemeManager;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.inventory.ItemSearchCompanion;
import su.catlean.utils.render.Render2D;

public final class EditableLine extends PanelComponent {
   @NotNull
   public static final ItemSearchCompanion itemSearchCompanion = new ItemSearchCompanion(null);
   @NotNull
   private final String name;
   private float amount;
   @NotNull
   private SuggestionsHelper suggestions;
   @NotNull
   private final Function1 onChange;
   @NotNull
   private String text;
   @Nullable
   private static EditableLine INSTANCE;

   public EditableLine(String name, float amount, SuggestionsHelper suggestions, Function1 onChange) {
      this.name = name;
      this.amount = amount;
      this.suggestions = suggestions;
      this.onChange = onChange;
      this.text = "";
   }

   public EditableLine(
      String text, float amount, SuggestionsHelper suggestionsHelper, Function1 callback, int value, DefaultConstructorMarker defaultConstructorMarker
   ) {
      if ((value & 4) != 0) {
         suggestionsHelper = new SuggestionsHelper(CollectionsKt.emptyList());
      }

      this(text, amount, suggestionsHelper, callback);
   }

   @NotNull
   public final String getName() {
      return this.name;
   }

   public final float getAmount() {
      return this.amount;
   }

   public final void setAmount(float amount) {
      this.amount = amount;
   }

   @NotNull
   public final SuggestionsHelper getSuggestions() {
      return this.suggestions;
   }

   public final void updateSuggestionsHelper(SuggestionsHelper suggestionsHelper) {
      this.suggestions = suggestionsHelper;
   }

   @NotNull
   public final String getText() {
      return this.text;
   }

   public final void updateText(String value) {
      this.text = value;
      this.onChange.invoke(this);
   }

   @Override
   public void render(GuiGraphicsExtractor context, float amount, float amount2, float amount3, float amount4) {
      boolean flag;
      label109: {
         if ((amount <= amount3 ? amount3 <= amount + (this.amount - 88.0F) : false) && (amount2 <= amount4 ? amount4 <= amount2 + 13.0F : false)) {
            var items = PanelScreen.INSTANCE.getItems();
            if (items instanceof Collection && ((Collection)items).isEmpty()) {
               flag = true;
            } else {
               label123: {
                  for (Object object : items) {
                     Panel panel = (Panel)object;
                     if (panel.getDialog() != null) {
                        flag = false;
                        break label123;
                     }
                  }

                  flag = true;
               }
            }

            if (flag) {
               flag = true;
               break label109;
            }
         }

         flag = false;
      }

      boolean flag3 = flag;
      if (flag3) {
         PanelScreen.INSTANCE.setPanelComponent(this);
      }

      TexturedQuadPass.defaultImpl(
         context.pose(),
         amount,
         amount2,
         this.amount - 88.0F,
         13.0F,
         2.0F,
         flag3 ? 2.0F : 0.0F,
         0.0F,
         1.0F,
         ThemeManager.INSTANCE.getColor5().darker(),
         ThemeManager.INSTANCE.getColor6(),
         0.0F,
         0.0F,
         0.0F,
         14336,
         null
      );
      Render2D instance = Render2D.INSTANCE;
      Matrix3x2fStack pose = context.pose();
      float amount5 = amount + 30.0F;
      float amount22 = amount2 + 2.0F;
      Color[] colors = new Color[]{new Color(1157627903, true)};
      float value = 9.0F;
      float value2 = 0.5F;
      float value3 = amount22;
      float value4 = amount5;
      Matrix3x2fStack matrix3x2fStack = pose;
      instance.render(matrix3x2fStack, value4, value3, value2, value, colors);
      Fonts.getFontRenderer().render2(context, this.name, amount + 14.0F, amount2 + 5.5F, Color.WHITE);
      String text2 = this.suggestions.computeText(this.getText());
      CharSequence charSequence = text2;
      if (charSequence != null && charSequence.length() != 0) {
         Fonts.getFontRenderer().render(context, text2, amount + 38.0F, amount2, Render2D.INSTANCE.computeColor(Color.WHITE, 0.5F));
      }

      String length = this.getText().length() > 26 ? this.getText().substring(this.getText().length() - 27, this.getText().length() - 1) : this.getText();
      boolean flag4 = Intrinsics.areEqual(INSTANCE, this) && MinecraftHolder.getTime() % 700 > 350L;
      FontRenderer fontRenderer = Fonts.getFontRenderer();
      String text3 = flag3 || !KotlinCompat.contains(this.name, "Login", false, 2, null) && !KotlinCompat.contains(this.name, "Pass", false, 2, null)
         ? length + (flag4 ? "|" : "")
         : "**************";
      amount22 = amount + 38.0F;
      float amount23 = amount2 + 0.5F;
      Color white = Color.WHITE;
      value = amount23;
      value2 = amount22;
      String text4 = text3;
      fontRenderer.render(context, text4, value2, value, white);
   }

   @Override
   public boolean checkIntValue(float amount, float amount2, int value) {
      return false;
   }

   @Nullable
   public final Unit getUnit() {
      String text2 = this.suggestions.computeText(this.getText());
      Unit instance;
      if (text2 != null) {
         String text3 = text2;
         this.updateText(text3);
         instance = Unit.INSTANCE;
      } else {
         instance = null;
      }

      return instance;
   }

   public static final EditableLine getInstance() {
      return INSTANCE;
   }

   public static final void setINSTANCE(EditableLine editableLine) {
      INSTANCE = editableLine;
   }
}
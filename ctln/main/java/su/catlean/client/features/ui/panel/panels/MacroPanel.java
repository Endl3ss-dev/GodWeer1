// deobf by pusia
package su.catlean.client.features.ui.panel.panels;

import su.catlean.utils.KotlinCompat;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.TypeIntrinsics;
import kotlin.reflect.KMutableProperty0;
import kotlin.text.StringsKt;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fStack;
import su.catlean.api.data.Rectangle;
import su.catlean.api.setting.Setting;
import su.catlean.api.setting.values.Bind;
import su.catlean.client.commands.SuggestionsHelper;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.ui.panel.Panel;
import su.catlean.client.features.ui.panel.components.DeleteButton;
import su.catlean.client.features.ui.panel.components.EditableLine;
import su.catlean.client.features.ui.panel.components.PanelComponent;
import su.catlean.client.features.ui.panel.components.TwoStateButton;
import su.catlean.client.features.ui.panel.dialogs.Dialog;
import su.catlean.client.features.ui.panel.dialogs.MacroDialog;
import su.catlean.client.features.ui.panel.panels.macro.MacroItem;
import su.catlean.client.render.BoxPass;
import su.catlean.client.render.TexturedQuadPass;
import su.catlean.client.render.font.Fonts;
import su.catlean.client.services.MacroManager;
import su.catlean.client.services.ModuleManager;
import su.catlean.client.services.NotificationManager;
import su.catlean.client.services.ThemeManager;
import su.catlean.client.services.client.macro.ChatMacro;
import su.catlean.client.services.client.macro.ItemMacro;
import su.catlean.client.services.client.macro.Macro;
import su.catlean.client.services.client.macro.SettingMacro;
import su.catlean.utils.animation.Animation;
import su.catlean.utils.render.Render2D;

public final class MacroPanel extends Panel {
   @NotNull
   public static final MacroPanel INSTANCE = new MacroPanel();
   @NotNull
   private static final List<MacroItem> items = new ArrayList();
   @Nullable
   private static PanelComponent panelComponent;
   @Nullable
   private static PanelComponent panelComponent2;
   @Nullable
   private static PanelComponent panelComponent3;
   private static boolean flag;
   @Nullable
   private static MacroItem macroItem;
   @Nullable
   private static MacroItem macroItem2;
   @NotNull
   private static final Animation animation = new Animation(NotificationManager.Notification.Type.OUT_QUINT, 300L);
   @NotNull
   private static final Animation animation2 = new Animation(NotificationManager.Notification.Type.OUT_QUINT, 300L);
   @NotNull
   private static final Animation animation3 = new Animation(NotificationManager.Notification.Type.OUT_QUINT, 100L);
   private static boolean flag2;
   private static float amount;
   @NotNull
   private static final EditableLine editableLine = new EditableLine("Name", INSTANCE.getAmount3(), null, arg -> {
      if (macroItem != null) {
         Macro macros = macroItem.getMacros();
         if (macros != null) {
            macros.updateText(((su.catlean.client.features.ui.panel.components.EditableLine)arg).getText());
         }
      }

      return Unit.INSTANCE;
   }, 4, null);
   @NotNull
   private static final EditableLine editableLine2 = new EditableLine("Text", INSTANCE.getAmount3(), null, arg -> {
      if (!((macroItem != null ? macroItem.getMacros() : null) instanceof ChatMacro)) {
         return Unit.INSTANCE;
      } else {
         ((ChatMacro)(macroItem != null ? macroItem.getMacros() : null)).updateText2(((su.catlean.client.features.ui.panel.components.EditableLine)arg).getText());
         return Unit.INSTANCE;
      }
   }, 4, null);
   @NotNull
   private static final EditableLine editableLine3;
   @NotNull
   private static final EditableLine editableLine4;
   @NotNull
   private static final EditableLine editableLine5;
   @NotNull
   private static final EditableLine editableLine6;
   @NotNull
   private static final EditableLine editableLine7;

   private MacroPanel() {
      super("macro-panel", 0.0F, 0.0F, 0.0F, 0.0F, 30, null);
   }

   @NotNull
   public final List<MacroItem> getItems() {
      return items;
   }

   @Nullable
   public final PanelComponent getPanelComponent() {
      return panelComponent;
   }

   public final void setPanelComponent(PanelComponent panelComponent) {
      MacroPanel.panelComponent = panelComponent;
   }

   @Nullable
   public final PanelComponent getPanelComponent3() {
      return panelComponent3;
   }

   public final void setPanelComponent3(PanelComponent panelComponent) {
      panelComponent3 = panelComponent;
   }

   public final boolean isFlag() {
      return flag;
   }

   public final void setFlag(boolean flag) {
      MacroPanel.flag = flag;
   }

   @Nullable
   public final MacroItem getMacroItem() {
      return macroItem;
   }

   public final void setMacroItem(MacroItem macroItem) {
      MacroPanel.macroItem = macroItem;
   }

   @Nullable
   public final MacroItem getMacroItem2() {
      return macroItem2;
   }

   public final void setMacroItem2(MacroItem macroItem) {
      macroItem2 = macroItem;
   }

   @NotNull
   public final Animation getAnimation() {
      return animation;
   }

   @Override
   public void applyClear() {
      items.clear();

      for (Object item : MacroManager.INSTANCE.getItems()) {
         Macro macro = (Macro)item;
         items.add(new MacroItem(macro));
      }
   }

   @Override
   public void render(GuiGraphicsExtractor context, int value, int value2) {
      boolean flag22;
      label70: {
         super.render(context, value, value2);
         animation.applyDoubleValue(1.0F);
         animation2.applyDoubleValue(this.getDialog() != null ? 0.5F : 0.0F);
         animation3.applyDoubleValue(amount);
         float amount2 = this.getAmount() + 3.0F;
         float amount3 = this.getAmount() + 72.0F;
         float value3 = value;
         if (amount2 <= value3 ? value3 <= amount3 : false) {
            amount2 = this.getAmount2() + 5.0F;
            amount3 = this.getAmount2() + 13.0F;
            value3 = value2;
            if (amount2 <= value3 ? value3 <= amount3 : false) {
               flag22 = true;
               break label70;
            }
         }

         flag22 = false;
      }

      flag2 = flag22;
      TexturedQuadPass.defaultImpl(
         context.pose(),
         this.getAmount() + 5.0F,
         this.getAmount2() + 3.0F,
         69.0F,
         8.0F,
         2.0F,
         flag2 ? 2.5F : 2.0F,
         flag2 ? 0.1F : 0.0F,
         1.0F,
         ThemeManager.INSTANCE.getColor4(),
         ThemeManager.INSTANCE.getColor6(),
         0.0F,
         0.0F,
         0.0F,
         14336,
         null
      );
      Fonts.getFontRenderer().render(context, "+", this.getAmount() + 36.0F, this.getAmount2() + 1.0F, Color.WHITE);
      TexturedQuadPass.defaultImpl(
         context.pose(),
         this.getAmount() + 76.0F,
         this.getAmount2(),
         6.0F,
         this.getAmount4(),
         1.0F,
         0.0F,
         0.0F,
         1.0F,
         ThemeManager.INSTANCE.getColor4(),
         ThemeManager.INSTANCE.getColor6(),
         0.0F,
         0.0F,
         0.0F,
         14336,
         null
      );
      panelComponent3 = null;
      panelComponent = null;
      flag = false;
      float amount4 = 0.0F;
      amount4 = 17.0F + animation3.getAmount();
      Render2D instance = Render2D.INSTANCE;
      Rectangle rectangle = new Rectangle(this.getAmount(), this.getAmount2() + 14, this.getAmount() + 76, this.getAmount2() + this.getAmount4() - 2.0F);
      instance.render(context, rectangle);
      Iterable<MacroItem> items2 = items;

      for (Object object : items2) {
         MacroItem macroItem3 = (MacroItem)object;
         macroItem3.render(context, INSTANCE.getAmount(), INSTANCE.getAmount2() + amount4, value, value2);
         amount4 += 29.0F;
      }

      Render2D.INSTANCE.render(context);
      if (macroItem != null) {
         float amount5 = (1.0F - animation.getAmount()) * 10.0F;
         Function0 function0 = () -> {
            MacroManager.INSTANCE.checkRemove(macroItem.getMacros());
            Collection<MacroItem> items3 = items;
            MacroItem macroItem4 = macroItem;
            TypeIntrinsics.asMutableCollection(items3).remove(macroItem4);
            macroItem = null;
            return Unit.INSTANCE;
         };
         editableLine.render(context, this.getAmount() + 84.0F, this.getAmount2() + 17.0F + amount5, value, value2);
         Macro macros = macroItem.getMacros();
         if (macros instanceof ChatMacro) {
            editableLine2.render(context, this.getAmount() + 84.0F, this.getAmount2() + 32.0F + amount5, value, value2);
            new DeleteButton(function0, this.getAmount3(), (KMutableProperty0)(new MacroPanel$hoveringComponent$prop2(this)))
               .render(context, this.getAmount() + 84.0F, this.getAmount2() + 47.0F + amount5, value, value2);
         } else if (macros instanceof ItemMacro) {
            editableLine3.render(context, this.getAmount() + 84.0F, this.getAmount2() + 32.0F + amount5, value, value2);
            new TwoStateButton(
                  this.getAmount3(),
                  "Rage",
                  "Legit",
                  (KMutableProperty0)(new MacroPanel$legit$prop((ItemMacro)macroItem.getMacros())),
                  (KMutableProperty0)(new MacroPanel$hoveringComponent$prop4(this))
               )
               .render(context, this.getAmount() + 84.0F, this.getAmount2() + 47.0F + amount5, value, value2);
            new DeleteButton(function0, this.getAmount3(), (KMutableProperty0)(new MacroPanel$hoveringComponent$prop5(this)))
               .render(context, this.getAmount() + 84.0F, this.getAmount2() + 62.0F + amount5, value, value2);
         } else {
            if (!(macros instanceof SettingMacro)) {
               throw new NoWhenBranchMatchedException();
            }

            editableLine4.render(context, this.getAmount() + 84.0F, this.getAmount2() + 32.0F + amount5, value, value2);
            editableLine5.render(context, this.getAmount() + 84.0F, this.getAmount2() + 47.0F + amount5, value, value2);
            editableLine6.render(context, this.getAmount() + 84.0F, this.getAmount2() + 62.0F + amount5, value, value2);
            editableLine7.render(context, this.getAmount() + 84.0F, this.getAmount2() + 77.0F + amount5, value, value2);
            float amount32 = this.getAmount3();
            SettingMacro macros2 = (SettingMacro)macroItem.getMacros();
            new TwoStateButton(
                  amount32,
                  "Toggle",
                  "Hold",
                  (KMutableProperty0)(new MacroPanel$hold$prop(macros2)),
                  (KMutableProperty0)(new MacroPanel$hoveringComponent$prop3(this))
               )
               .render(context, this.getAmount() + 84.0F, this.getAmount2() + 92.0F + amount5, value, value2);
            new DeleteButton(function0, this.getAmount3(), (KMutableProperty0)(new MacroPanel$hoveringComponent$prop(this)))
               .render(context, this.getAmount() + 84.0F, this.getAmount2() + 107.0F + amount5, value, value2);
         }
      }

      float amount6 = this.getAmount();
      float amount22 = this.getAmount2();
      float amount33 = this.getAmount3();
      float amount42 = this.getAmount4();
      Color color = Render2D.INSTANCE.computeColor(Color.BLACK, animation2.getAmount());
      Color color2 = Render2D.INSTANCE.computeColor(Color.BLACK, animation2.getAmount());
      Color color3 = Render2D.INSTANCE.computeColor(Color.BLACK, animation2.getAmount());
      Color color4 = Render2D.INSTANCE.computeColor(Color.BLACK, animation2.getAmount());
      Matrix3x2fStack pose = context.pose();
      rectangle = null;
      short shortValue = 3072;
      Object object2 = null;
      float value4 = 0.0F;
      Matrix3x2fStack matrix3x2fStack = pose;
      Color color5 = color4;
      Color color6 = color3;
      Color color7 = color2;
      Color color8 = color;
      float value5 = 5.0F;
      BoxPass.defaultImpl(amount6, amount22, amount33, amount42, value5, color8, color7, color6, color5, matrix3x2fStack, value4, (Color)object2, shortValue, rectangle);
      Dialog dialog = this.getDialog();
      if (dialog != null) {
         dialog.render(context, this.getAmount() + this.getAmount3() / 2.0F - 60.0F, this.getAmount2() + this.getAmount4() / 2 - 35.0F, value, value2);
      }
   }

   @Override
   public void updateValue(int value, int value2, int value3) {
      if (macroItem2 == null) {
         super.updateValue(value, value2, value3);
      } else {
         if (macroItem2 != null) {
            Macro macros = macroItem2.getMacros();
            if (macros != null) {
               macros.updateBind(new Bind(value, false, false, 6, null));
            }
         }

         macroItem2 = null;
      }
   }

   @Override
   public void updateAmount(float amount, float amount2, int value) {
      super.updateAmount(amount, amount2, value);
      Dialog dialog = this.getDialog();
      if (dialog != null ? !dialog.testAmount(amount, amount2, value) : true) {
         if (flag2) {
            EditableLine.itemSearchCompanion.updateEditableLine(null);
            this.setDialog(new MacroDialog());
         } else {
            this.setDialog(null);
            if (macroItem2 != null) {
               macroItem2.getMacros().updateBind(new Bind(value, false, true, 2, null));
               macroItem2 = null;
            } else if (panelComponent != null) {
               panelComponent2 = panelComponent;
               panelComponent.checkIntValue(amount, amount2, value);
            } else {
               panelComponent2 = null;
               if (panelComponent3 != null) {
                  switch (value) {
                     case 0:
                        if (flag) {
                           macroItem2 = (MacroItem)panelComponent3;
                           return;
                        }

                        ((MacroItem)panelComponent3).getMacros().setFlag(!((MacroItem)panelComponent3).getMacros().isFlag());
                        break;
                     case 1:
                        macroItem = (MacroItem)panelComponent3;
                        editableLine.updateText(macroItem.getMacros().getId());
                        if (macroItem.getMacros() instanceof ChatMacro) {
                           editableLine2.updateText(((ChatMacro)macroItem.getMacros()).getMessage());
                        }

                        if (macroItem.getMacros() instanceof ItemMacro) {
                           editableLine3.updateText(((ItemMacro)macroItem.getMacros()).getItem());
                        }

                        if (macroItem.getMacros() instanceof SettingMacro) {
                           editableLine4.updateText(((SettingMacro)macroItem.getMacros()).getModule());
                           editableLine5.updateText(((SettingMacro)macroItem.getMacros()).getSetting());
                           editableLine6.updateText(((SettingMacro)macroItem.getMacros()).getPressValue());
                           editableLine7.updateText(((SettingMacro)macroItem.getMacros()).getReleaseValue());
                        }

                        animation.updateAmount(0.0F);
                        break;
                     case 2:
                        macroItem2 = (MacroItem)panelComponent3;
                  }
               }
            }
         }
      }
   }

   @Override
   public void applyClamp(double amount, double amount2, double amount3) {
      float amount4 = this.getAmount();
      if (amount <= this.getAmount() + 76.0F ? amount4 <= amount : false) {
         amount4 = this.getAmount2();
         if (amount2 <= this.getAmount2() + this.getAmount4() - 2.0F ? amount4 <= amount2 : false) {
            MacroPanel.amount += (float)amount3 * 7.0F;
            MacroPanel.amount = Math.clamp(MacroPanel.amount, items.size() * -29.0F, 0.0F);
         }
      }
   }

   static {
      float amount3 = INSTANCE.getAmount3();
      List arrayList = new ArrayList();
      float value = amount3;
      String text = "Item";
      Iterable block = (Iterable)BuiltInRegistries.BLOCK;
      boolean flag4 = false;
      var arrayList2 = new ArrayList(CollectionsKt.collectionSizeOrDefault(block, 10));

      for (Object object : block) {
         Block block2 = (Block)object;
         arrayList2.add(KotlinCompat.replace(block2.getDescriptionId(), "block.minecraft.", "", false, 4, null));
      }

      arrayList.addAll(arrayList2);
      block = (Iterable)BuiltInRegistries.ITEM;
      flag4 = false;
      arrayList2 = new ArrayList(CollectionsKt.collectionSizeOrDefault(block, 10));

      for (Object object2 : block) {
         Item item = (Item)object2;
         arrayList2.add(KotlinCompat.replace(item.getDescriptionId(), "item.minecraft.", "", false, 4, null));
      }

      arrayList.addAll(arrayList2);
      Unit instance = Unit.INSTANCE;
      SuggestionsHelper suggestionsHelper = new SuggestionsHelper(arrayList);
      Function1 function1 = arg -> {
         if (!((macroItem != null ? macroItem.getMacros() : null) instanceof ItemMacro)) {
            return Unit.INSTANCE;
         } else {
            ((ItemMacro)(macroItem != null ? macroItem.getMacros() : null)).updateText2(((su.catlean.client.features.ui.panel.components.EditableLine)arg).getText());
            return Unit.INSTANCE;
         }
      };
      SuggestionsHelper suggestionsHelper2 = suggestionsHelper;
      editableLine3 = new EditableLine(text, value, suggestionsHelper2, function1);
      amount3 = INSTANCE.getAmount3();
      Iterable<Module> items2 = ModuleManager.INSTANCE.getItems2();
      value = amount3;
      text = "Module";
      var arrayList3 = new ArrayList(CollectionsKt.collectionSizeOrDefault(items2, 10));

      for (Object object3 : items2) {
         Module module = (Module)object3;
         arrayList3.add(module.getId());
      }

      List list = arrayList3;
      suggestionsHelper = new SuggestionsHelper(list);
      Function1 function12 = arg2 -> {
         if (!((macroItem != null ? macroItem.getMacros() : null) instanceof SettingMacro)) {
            return Unit.INSTANCE;
         } else {
            ((SettingMacro)(macroItem != null ? macroItem.getMacros() : null)).updateText2(((su.catlean.client.features.ui.panel.components.EditableLine)arg2).getText());
            return Unit.INSTANCE;
         }
      };
      SuggestionsHelper suggestionsHelper3 = suggestionsHelper;
      editableLine4 = new EditableLine(text, value, suggestionsHelper3, function12);
      editableLine5 = new EditableLine("Setting", INSTANCE.getAmount3(), null, arg3 -> {
         if (!((macroItem != null ? macroItem.getMacros() : null) instanceof SettingMacro)) {
            return Unit.INSTANCE;
         } else {
            SettingMacro macros = (SettingMacro)(macroItem != null ? macroItem.getMacros() : null);
            Iterable<Module> items22 = ModuleManager.INSTANCE.getItems2();
            Iterator<Module> iterator = items22.iterator();

            Object object4;
            while (true) {
               if (iterator.hasNext()) {
                  Module object5 = iterator.next();
                  Module module2 = (Module)object5;
                  if (!Intrinsics.areEqual(module2.getId(), macros.getModule())) {
                     continue;
                  }

                  object4 = (EditableLine)(Object)object5;
                  break;
               }

               object4 = null;
               break;
            }

            List emptyList;
            label40: {
               object4 = arg3;
               Module module3 = (Module)object4;
               if (module3 != null) {
                  List items5 = module3.getItems5();
                  if (items5 != null) {
                     Iterable iterable = items5;
                     var arrayList4 = new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10));

                     for (Object object6 : iterable) {
                        Setting setting = (Setting)object6;
                        arrayList4.add(setting.getId());
                     }

                     emptyList = arrayList4;
                     object4 = arg3;
                     break label40;
                  }
               }

               emptyList = CollectionsKt.emptyList();
            }

            List list2 = emptyList;
            ((su.catlean.client.features.ui.panel.components.EditableLine)object4).updateSuggestionsHelper(new SuggestionsHelper(list2));
            macros.updateText3(((su.catlean.client.features.ui.panel.components.EditableLine)arg3).getText());
            return Unit.INSTANCE;
         }
      }, 4, null);
      editableLine6 = new EditableLine("Press", INSTANCE.getAmount3(), null, arg4 -> {
         if (!((macroItem != null ? macroItem.getMacros() : null) instanceof SettingMacro)) {
            return Unit.INSTANCE;
         } else {
            ((SettingMacro)(macroItem != null ? macroItem.getMacros() : null)).updateText4(((su.catlean.client.features.ui.panel.components.EditableLine)arg4).getText());
            return Unit.INSTANCE;
         }
      }, 4, null);
      editableLine7 = new EditableLine("Release", INSTANCE.getAmount3(), null, arg5 -> {
         if (!((macroItem != null ? macroItem.getMacros() : null) instanceof SettingMacro)) {
            return Unit.INSTANCE;
         } else {
            ((SettingMacro)(macroItem != null ? macroItem.getMacros() : null)).updateText5(((su.catlean.client.features.ui.panel.components.EditableLine)arg5).getText());
            return Unit.INSTANCE;
         }
      }, 4, null);
      INSTANCE.applyClear();
      INSTANCE.setAmount2(240.0F);
   }
}
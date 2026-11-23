// deobf by pusia
package su.catlean.client.features.ui.panel;

import java.util.Collection;
import java.util.Set;
import kotlin.collections.ArraysKt;
import kotlin.collections.SetsKt;
import kotlin.text.StringsKt;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import su.catlean.client.features.ui.clickgui.SearchBar;
import su.catlean.client.features.ui.panel.components.EditableLine;
import su.catlean.client.features.ui.panel.components.PanelComponent;
import su.catlean.client.features.ui.panel.panels.ConfigPanel;
import su.catlean.client.features.ui.panel.panels.FriendsPanel;
import su.catlean.client.features.ui.panel.panels.MacroPanel;
import su.catlean.client.features.ui.panel.panels.ProxyPanel;
import su.catlean.client.features.ui.panel.panels.WaypointPanel;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.input.InputUtil;

public final class PanelScreen extends Screen {
   @NotNull
   public static final PanelScreen INSTANCE = new PanelScreen();
   @NotNull
   private static Set items;
   @Nullable
   private static PanelComponent panelComponent;
   @Nullable
   private static Panel panel;

   private PanelScreen() {
      super(Component.nullToEmpty("catlean-panel-screen"));
   }

   @NotNull
   public final Set getItems() {
      return items;
   }

   @Nullable
   public final PanelComponent getPanelComponent() {
      return panelComponent;
   }

   public final void setPanelComponent(PanelComponent panelComponent) {
      PanelScreen.panelComponent = panelComponent;
   }

   @Nullable
   public final Panel getPanel() {
      return panel;
   }

   public final void setPanel(Panel panel) {
      PanelScreen.panel = panel;
   }

   public void init() {
      for (Object item : items) {
         Panel panel2 = (Panel)item;
         panel2.update();
      }
   }

   public void extractRenderState(GuiGraphicsExtractor context, int value, int value2, float amount) {
      SearchBar.INSTANCE.render(context, value, value2);
      panelComponent = null;

      for (Object item : items) {
         Panel panel2 = (Panel)item;
         panel2.render(context, value, value2);
      }
   }

   public boolean isPauseScreen() {
      return false;
   }

   public boolean mouseClicked(MouseButtonEvent click, boolean flag) {
      double value = click.x();
      double value2 = click.y();
      int button = click.button();
      if (SearchBar.INSTANCE.checkGetGuiScaledWidth(value, value2, button)) {
         return true;
      } else {
         for (Object item : items) {
            Panel panel2 = (Panel)item;
            panel2.updateAmount((float)value, (float)value2, button);
         }

         if (panelComponent != null && panelComponent instanceof EditableLine) {
            var items2 = items;
            boolean flag2;
            if (items2 instanceof Collection && ((Collection)items2).isEmpty()) {
               flag2 = true;
            } else {
               label59: {
                  for (Object object : items2) {
                     Panel panel3 = (Panel)object;
                     if (panel3.getDialog() != null) {
                        flag2 = false;
                        break label59;
                     }
                  }

                  flag2 = true;
               }
            }

            if (flag2) {
               EditableLine.itemSearchCompanion.updateEditableLine((EditableLine)panelComponent);
               return super.mouseClicked(click, flag);
            }
         }

         panelComponent = null;
         return super.mouseClicked(click, flag);
      }
   }

   public boolean mouseReleased(MouseButtonEvent click) {
      double value = click.x();
      double value2 = click.y();
      int button = click.button();

      for (Object item : items) {
         Panel panel2 = (Panel)item;
         panel2.update2();
      }

      SearchBar.INSTANCE.updateAmount(value, value2, button);
      return super.mouseReleased(click);
   }

   public boolean mouseScrolled(double amount, double amount2, double amount3, double amount4) {
      for (Object item : items) {
         Panel panel2 = (Panel)item;
         panel2.applyClamp(amount, amount2, amount4);
      }

      return super.mouseScrolled(amount, amount2, amount3, amount4);
   }

   public boolean keyPressed(KeyEvent input) {
      int key = input.key();
      int scancode = input.scancode();
      int modifiers = input.modifiers();
      Iterable items2 = items;

      for (Object object : items2) {
         Panel panel2 = (Panel)object;
         panel2.updateValue(key, scancode, modifiers);
      }

      switch (key) {
         case 86:
            if (InputUtil.checkContains(341)) {
               String glfwGetClipboardString = GLFW.glfwGetClipboardString(MinecraftHolder.getMinecraft().getWindow().handle());
               if (glfwGetClipboardString == null) {
                  glfwGetClipboardString = "";
               }

               String text = glfwGetClipboardString;
               EditableLine editableLine = EditableLine.itemSearchCompanion.getEditableLine();
               if (editableLine != null) {
                  EditableLine editableLine2 = editableLine;
                  editableLine2.updateText(editableLine2.getText() + text);
               }
            }
            break;
         case 257:
            EditableLine.itemSearchCompanion.updateEditableLine(null);
            break;
         case 258:
            EditableLine editableLine3 = EditableLine.itemSearchCompanion.getEditableLine();
            if (editableLine3 != null) {
               editableLine3.getUnit();
            }
            break;
         case 259:
            EditableLine editableLine4 = EditableLine.itemSearchCompanion.getEditableLine();
            if (editableLine4 != null) {
               EditableLine editableLine5 = editableLine4;
               editableLine5.updateText(StringsKt.dropLast(editableLine5.getText(), 1));
            }
      }

      return super.keyPressed(input);
   }

   public boolean charTyped(CharacterEvent input) {
      Character firstOrNull = ArraysKt.firstOrNull(input.codepointAsString().toCharArray());
      if (firstOrNull == null) {
         return super.charTyped(input);
      } else {
         char ch = firstOrNull;
         Iterable items2 = items;

         for (Object object : items2) {
            Panel panel2 = (Panel)object;
            panel2.updateSymbol(ch);
         }

         EditableLine editableLine = EditableLine.itemSearchCompanion.getEditableLine();
         if (editableLine != null) {
            EditableLine editableLine2 = editableLine;
            editableLine2.updateText(editableLine2.getText() + ch);
         }

         SearchBar.INSTANCE.applyLength(ch);
         return super.charTyped(input);
      }
   }

   public void extractBackground(GuiGraphicsExtractor GuiGraphicsExtractor, int value, int value2, float amount) {
   }

   static {
      Panel[] panels = new Panel[]{ConfigPanel.INSTANCE, MacroPanel.INSTANCE, WaypointPanel.INSTANCE, ProxyPanel.INSTANCE, FriendsPanel.INSTANCE};
      items = SetsKt.setOf(panels);
   }
}
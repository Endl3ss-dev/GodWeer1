// deobf by pusia
package su.catlean.client.features.ui.hud.widgets;

import java.awt.Color;
import java.util.ArrayList;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2fStack;
import su.catlean.client.features.ui.hud.Widget;
import su.catlean.client.render.TexturedQuadPass;
import su.catlean.client.render.font.Fonts;
import su.catlean.client.services.InventoryManager;
import su.catlean.client.services.MacroManager;
import su.catlean.client.services.ThemeManager;
import su.catlean.client.services.client.macro.ItemMacro;
import su.catlean.client.services.client.macro.Macro;
import su.catlean.utils.KeyNames;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.input.InputUtil;
import su.catlean.utils.render.Render2D;

public final class MacroWidget extends Widget {
   @NotNull
   public static final MacroWidget INSTANCE = new MacroWidget();

   private MacroWidget() {
      super("macro-widget");
   }

   @Override
   public void render(GuiGraphicsExtractor context) {
      float amount3 = this.getAmount3();
      float amount4 = this.getAmount4();
      float value = 0.0F;
      Iterable items = MacroManager.INSTANCE.getItems();
      var arrayList = new ArrayList();
      boolean checkContains = false;

      for (Object object : items) {
         Macro macro = (Macro)object;
         if (macro.isFlag() && macro instanceof ItemMacro) {
            arrayList.add(object);
         }
      }

      for (Macro macro2 : (Iterable<Macro>)arrayList) {
         String text = "null cannot be cast to non-null type su.catlean.client.services.client.macro.ItemMacro";
         ItemMacro itemMacro = (ItemMacro)macro2;
         Item item = MacroManager.INSTANCE.computeItem(itemMacro.getItem());
         if (item != null) {
            Item item2 = item;
            InventoryManager instance = InventoryManager.INSTANCE;
            Item[] items2 = new Item[]{item2};
            boolean isFlag = instance.computeSearchInvResult2(items2).isFlag();
            checkContains = InputUtil.checkContains(itemMacro.getBind().getValue());
            String text2 = KeyNames.INSTANCE.computeText(itemMacro.getBind().getValue());
            float amount = Fonts.getFontRenderer().computeAmount(text2) + 22.0F;
            boolean isOnCooldown = MinecraftHolder.getLocalPlayer().getCooldowns().isOnCooldown(item2.getDefaultInstance());
            Widget.defaultImpl(this, context.pose(), amount3, amount4, amount, 16.0F, 0.0F, false, 96, null);
            if (isOnCooldown) {
               float cooldownPercent = MinecraftHolder.getLocalPlayer().getCooldowns().getCooldownPercent(item2.getDefaultInstance(), Render2D.INSTANCE.getAmount2());
               TexturedQuadPass.defaultImpl(
                  context.pose(),
                  amount3,
                  amount4 + 16.0F * (1.0F - cooldownPercent),
                  amount,
                  16.0F * cooldownPercent,
                  3.0F,
                  0.0F,
                  0.0F,
                  1.0F,
                  Color.RED,
                  ThemeManager.INSTANCE.getColor6(),
                  0.0F,
                  0.0F,
                  0.0F,
                  14336,
                  null
               );
            }

            context.pose().pushMatrix();
            context.pose().translate(amount3 + 2.0F, amount4 + 2.0F);
            context.pose().scale(0.7F);
            context.item(item2.getDefaultInstance(), 0, 0);
            context.pose().popMatrix();
            Render2D instance2 = Render2D.INSTANCE;
            Matrix3x2fStack pose = context.pose();
            float value2 = amount3 + 14;
            float value3 = amount4 + 4.0F;
            Color[] colors = new Color[]{new Color(1157627903, true)};
            float value4 = 9.0F;
            float value5 = 0.5F;
            float value6 = value3;
            float value7 = value2;
            Matrix3x2fStack matrix3x2fStack = pose;
            instance2.render(matrix3x2fStack, value7, value6, value5, value4, colors);
            Color color8 = checkContains ? ThemeManager.INSTANCE.getColor8() : (isFlag ? ThemeManager.INSTANCE.getColor7() : Color.RED);
            this.render(context, text2, amount3 + 18.0F, amount4 + 3.0F, color8);
            amount3 += amount + 2.0F;
            value += amount + 2.0F;
         }
      }

      this.updateAmount(value, 16.0F);
   }
}
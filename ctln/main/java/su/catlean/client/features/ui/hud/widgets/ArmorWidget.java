// deobf by pusia
package su.catlean.client.features.ui.hud.widgets;

import java.awt.Color;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.catlean.client.features.ui.hud.Widget;
import su.catlean.client.render.BoxPass;
import su.catlean.client.services.ThemeManager;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.inventory.InventoryUtil;
import su.catlean.utils.render.Render2D;

public final class ArmorWidget extends Widget {
   @NotNull
   public static final ArmorWidget INSTANCE = new ArmorWidget();

   private ArmorWidget() {
      super("armor-widget");
   }

   @Override
   public void render(GuiGraphicsExtractor context) {
      byte byteValue = 2;
      Widget.defaultImpl(this, context.pose(), this.getAmount3(), this.getAmount4(), 81.0F, 22.0F, 1.0F, false, 64, null);
      context.pose().pushMatrix();
      context.pose().translate(this.getAmount3(), this.getAmount4());

      for (ItemStack inventoryItem : (Iterable<ItemStack>)InventoryUtil.fetchInventory(MinecraftHolder.getLocalPlayer())) {
         if (byteValue < 43) {
            Widget.defaultImpl(this, context.pose(), byteValue + 18.0F, 7.0F, 0.0F, 8, null);
         }

         if (!inventoryItem.isEmpty()) {
            context.item(inventoryItem, byteValue, 0);
         }

         Color color = ThemeManager.INSTANCE.computeColor(byteValue);
         Color color2 = ThemeManager.INSTANCE.computeColor(byteValue * 2);
         Color color3 = Render2D.INSTANCE.computeColor(color, 0.4F);
         Color color4 = Render2D.INSTANCE.computeColor(color2, 0.4F);
         BoxPass.render(byteValue, 15.0F, 16.0F, 4.0F, 1.5F, color3, color3, color4, color4, context.pose(), 1.0F, color2);
         float damageValue = Mth.clamp(16.0F - inventoryItem.getDamageValue() * 16.0F / inventoryItem.getMaxDamage() + 2.0F, 0.0F, 16.0F);
         BoxPass.render(byteValue, 15.0F, damageValue, 4.0F, 1.5F, color, color, color2, color2, context.pose(), 1.0F, color2);
         byteValue += 20;
      }

      context.pose().popMatrix();
      this.updateAmount(81.0F, 22.0F);
   }

   @Override
   public void render2(GuiGraphicsExtractor context) {
      byte byteValue = 2;

      for (ItemStack inventoryItem : (Iterable<ItemStack>)InventoryUtil.fetchInventory(MinecraftHolder.getLocalPlayer())) {
         Color color = ThemeManager.INSTANCE.computeColor(byteValue);
         Color color2 = ThemeManager.INSTANCE.computeColor(byteValue * 2);
         Color color3 = Render2D.INSTANCE.computeColor(color, 0.4F);
         Color color4 = Render2D.INSTANCE.computeColor(color2, 0.4F);
         BoxPass.render(this.getAmount3() + byteValue, this.getAmount4() + 15.0F, 16.0F, 4.0F, 1.5F, color3, color3, color4, color4, context.pose(), 1.0F, color2);
         float damageValue = Mth.clamp(16.0F - inventoryItem.getDamageValue() * 16.0F / inventoryItem.getMaxDamage() + 2.0F, 0.0F, 16.0F);
         BoxPass.render(this.getAmount3() + byteValue, this.getAmount4() + 15.0F, damageValue, 4.0F, 1.5F, color, color, color2, color2, context.pose(), 1.0F, color2);
         byteValue += 20;
      }
   }
}
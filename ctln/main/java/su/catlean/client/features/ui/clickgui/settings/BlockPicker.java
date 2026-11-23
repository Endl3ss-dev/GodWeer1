// deobf by pusia
package su.catlean.client.features.ui.clickgui.settings;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import kotlin.collections.CollectionsKt;
import kotlin.comparisons.ComparisonsKt;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.setting.Setting;
import su.catlean.api.setting.values.BlocksValue;
import su.catlean.client.features.ui.clickgui.popup.SearchablePopup;

public final class BlockPicker extends SearchablePopup {
   @NotNull
   private final Setting setting;

   public BlockPicker(Setting setting) {
      super("Select Blocks");
      this.setting = setting;
   }

   @NotNull
   public final Setting getSetting() {
      return this.setting;
   }

   @NotNull
   @Override
   public List getItems() {
      List arrayList = new ArrayList();
      CollectionsKt.addAll(arrayList, (Iterable)BuiltInRegistries.BLOCK);
      arrayList.remove(Blocks.AIR);
      Iterable iterable = arrayList;
      CollectionsKt.sortedWith(iterable, new BlockPicker.Ordering());
      return arrayList;
   }

   @NotNull
   public String fetchName(Block item) {
      return item.getName().getString();
   }

   public boolean checkContains(Block item) {
      return ((BlocksValue)this.setting.getObject2()).getBlocks().contains(item);
   }

   public void render(GuiGraphicsExtractor context, float amount, float amount2, Block block) {
      context.pose().pushMatrix();
      context.pose().translate(amount + 8.0F, amount2);
      context.item(block.asItem().getDefaultInstance(), 0, 0);
      context.pose().popMatrix();
   }

   public void applyContains(Block item) {
      List<Block> blocks = ((BlocksValue)this.setting.getObject2()).getBlocks();
      if (blocks.contains(item)) {
         blocks.remove(item);
      } else {
         blocks.add(item);
      }
   }

   @Override
   public String computeText(Object object) {
      return this.fetchName((Block)object);
   }

   @Override
   public boolean testObject(Object object) {
      return this.checkContains((Block)object);
   }

   @Override
   public void render(GuiGraphicsExtractor guiGraphicsExtractor, float amount, float amount2, Object object) {
      this.render(guiGraphicsExtractor, amount, amount2, (Block)object);
   }

   @Override
   public void updateObject(Object object) {
      this.applyContains((Block)object);
   }

   public static final class Ordering implements Comparator {
      public Ordering() {
      }

      @Override
      public final int compare(Object object, Object object2) {
         Block object3 = (Block)object;
         Comparable<String> string = object3.getName().getString();
         object3 = (Block)object2;
         Comparable<String> comparable = string;
         return ComparisonsKt.compareValues(comparable, object3.getName().getString());
      }
   }
}
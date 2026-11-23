// deobf by pusia
package su.catlean.client.features.ui.hud.widgets;

import java.util.ArrayList;
import kotlin.Pair;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;
import su.catlean.client.features.ui.hud.Widget;
import su.catlean.client.services.ServerInfoService;
import su.catlean.utils.MinecraftHolder;

public final class ChestCounterWidget extends Widget {
   @NotNull
   public static final ChestCounterWidget INSTANCE = new ChestCounterWidget();

   private ChestCounterWidget() {
      super("chest-counter-widget");
   }

   @Override
   public void render(GuiGraphicsExtractor context) {
      Pair pair = this.getPair();
      String text = "chests: S:" + pair.getFirst() + " D:" + pair.getSecond();
      this.render(context, text);
   }

   private final Pair getPair() {
      int value = 0;
      int value2 = 0;

      try {
         Iterable items = ServerInfoService.INSTANCE.getItems();
         var arrayList = new ArrayList();

         for (Object object : items) {
            if (object instanceof ChestBlockEntity) {
               arrayList.add(object);
            }
         }

         for (Object object2 : arrayList) {
            ChestBlockEntity chestBlockEntity = (ChestBlockEntity)object2;
            ChestType value3 = (ChestType)MinecraftHolder.getClientLevel().getBlockState(chestBlockEntity.getBlockPos()).getValue((Property)ChestBlock.TYPE);
            if (value3 == ChestType.SINGLE) {
               value++;
            } else {
               value2++;
            }
         }
      } catch (Exception exception) {
      }

      return new Pair(value, value2 / 2);
   }
}
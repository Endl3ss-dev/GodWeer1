package releon.ru.mixin.accessor;

import net.minecraft.client.gui.widget.EntryListWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({EntryListWidget.class})
public interface AbstractSelectionListAccessor {
   @Invoker("swapEntriesOnPositions")
   void releon$swapEntries(int var1, int var2);
}

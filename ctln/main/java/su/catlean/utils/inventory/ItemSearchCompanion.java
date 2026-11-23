// deobf by pusia
package su.catlean.utils.inventory;

import kotlin.jvm.internal.DefaultConstructorMarker;
import org.jetbrains.annotations.Nullable;
import su.catlean.client.features.ui.panel.components.EditableLine;

public final class ItemSearchCompanion {
   private ItemSearchCompanion() {
   }

   @Nullable
   public final EditableLine getEditableLine() {
      return EditableLine.getInstance();
   }

   public final void updateEditableLine(EditableLine editableLine) {
      EditableLine.setINSTANCE(editableLine);
   }

   public ItemSearchCompanion(DefaultConstructorMarker defaultConstructorMarker) {
      this();
   }
}
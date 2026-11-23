// deobf by pusia
package su.catlean.api.setting.values;

import org.jetbrains.annotations.NotNull;
import su.catlean.client.features.modules.PlaceModule;

public final class GroupValue {
   private boolean flag;
   @NotNull
   private PlaceModule.Pause type;

   public GroupValue(boolean flag, PlaceModule.Pause type) {
      this.flag = flag;
      this.type = type;
   }

   public final boolean isFlag() {
      return this.flag;
   }

   public final void setFlag(boolean flag) {
      this.flag = flag;
   }

   @NotNull
   public final PlaceModule.Pause getType() {
      return this.type;
   }
}
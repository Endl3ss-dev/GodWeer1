// deobf by pusia
package su.catlean.client.services.rotation;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.DefaultConstructorMarker;
import su.catlean.api.data.Rotation;

public final class LookRotationAction extends RotationAction {
   public LookRotationAction(Rotation rotation, int value, Function0 action) {
      super(rotation, value, () -> {
         action.invoke();
         return Unit.INSTANCE;
      });
   }

   public LookRotationAction(Rotation rotation, int value, Function0 callback, int value2, DefaultConstructorMarker defaultConstructorMarker) {
      if ((value2 & 2) != 0) {
         value = 150;
      }

      this(rotation, value, callback);
   }
}
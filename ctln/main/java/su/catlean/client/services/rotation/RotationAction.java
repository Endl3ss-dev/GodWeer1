// deobf by pusia
package su.catlean.client.services.rotation;

import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.Rotation;

public class RotationAction {
   @NotNull
   private final Rotation rotation;
   private final int value;
   @NotNull
   private Function0 action;

   public RotationAction(Rotation rotation, int value, Function0 action) {
      this.rotation = rotation;
      this.value = value;
      this.action = action;
   }

   public RotationAction(Rotation rotation, int value, Function0 callback, int value2, DefaultConstructorMarker defaultConstructorMarker) {
      if ((value2 & 2) != 0) {
         value = 150;
      }

      this(rotation, value, callback);
   }

   @NotNull
   public final Rotation getRotation() {
      return this.rotation;
   }

   public final int getValue() {
      return this.value;
   }

   @NotNull
   public final Function0 getAction() {
      return this.action;
   }

   public final void updateCallback(Function0 callback) {
      this.action = callback;
   }
}
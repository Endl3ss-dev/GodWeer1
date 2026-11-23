// deobf by pusia
package su.catlean.api.event.events.client;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.Event;
import su.catlean.gofra.Gofra;

public final class InputEvent extends Event {
   @NotNull
   public static final InputEvent INSTANCE = new InputEvent();
   public static InputEvent.Action action;
   public static InputEvent.Device device;
   private static int key = -1;

   private InputEvent() {
   }

   @NotNull
   public final InputEvent.Action getAction() {
      if (action != null) {
         return action;
      } else {
         Intrinsics.throwUninitializedPropertyAccessException("action");
         return null;
      }
   }

   public final void setAction(InputEvent.Action action) {
      InputEvent.action = action;
   }

   @NotNull
   public final InputEvent.Device getDevice() {
      if (device != null) {
         return device;
      } else {
         Intrinsics.throwUninitializedPropertyAccessException("device");
         return null;
      }
   }

   public final void setDevice(InputEvent.Device device) {
      InputEvent.device = device;
   }

   public final int getKey() {
      return key;
   }

   public final void setKey(int value) {
      key = value;
   }

   public final boolean call(int value, InputEvent.Device device, InputEvent.Action action) {
      this.setCancelled(false);
      this.setDevice(device);
      this.setAction(action);
      key = value;
      Gofra.INSTANCE.drain(this);
      return this.getCancelled();
   }

   public static enum Action {
      Press,
      Release,
      Hold;

      private static final EnumEntries<InputEvent.Action> $ENTRIES = EnumEntriesKt.enumEntries(values());

      private Action() {
      }

      @NotNull
      public static EnumEntries<InputEvent.Action> getEntries() {
         return $ENTRIES;
      }

      private static final InputEvent.Action[] $values() {
         return new InputEvent.Action[]{Press, Release, Hold};
      }
   }

   public static enum Device {
      Mouse,
      Keyboard;

      private static final EnumEntries<InputEvent.Device> $ENTRIES = EnumEntriesKt.enumEntries(values());

      private Device() {
      }

      @NotNull
      public static EnumEntries<InputEvent.Device> getEntries() {
         return $ENTRIES;
      }

      private static final InputEvent.Device[] $values() {
         return new InputEvent.Device[]{Mouse, Keyboard};
      }
   }
}
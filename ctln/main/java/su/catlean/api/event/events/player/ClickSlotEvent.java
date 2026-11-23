// deobf by pusia
package su.catlean.api.event.events.player;

import net.minecraft.world.inventory.ContainerInput;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.Event;
import su.catlean.gofra.Gofra;

public final class ClickSlotEvent extends Event {
   @NotNull
   public static final ClickSlotEvent INSTANCE = new ClickSlotEvent();
   @NotNull
   private static ContainerInput action = ContainerInput.PICKUP;
   private static int value;
   private static int button;
   private static int syncId;

   private ClickSlotEvent() {
   }

   @NotNull
   public final ContainerInput getAction() {
      return action;
   }

   public final void setAction(ContainerInput containerInput) {
      action = containerInput;
   }

   public final int getId() {
      return value;
   }

   public final void setId(int value) {
      ClickSlotEvent.value = value;
   }

   public final int getButton() {
      return button;
   }

   public final void setButton(int value) {
      button = value;
   }

   public final int getSyncId() {
      return syncId;
   }

   public final void setSyncId(int value) {
      syncId = value;
   }

   public final boolean call(ContainerInput action, int value, int value2, int value3) {
      this.setCancelled(false);
      ClickSlotEvent.action = action;
      ClickSlotEvent.value = value;
      button = value2;
      syncId = value3;
      Gofra.INSTANCE.drain(this);
      return this.getCancelled();
   }
}
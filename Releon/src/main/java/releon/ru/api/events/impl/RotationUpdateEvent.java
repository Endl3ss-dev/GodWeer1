package releon.ru.api.events.impl;

import releon.ru.api.events.Event;
import releon.ru.api.events.types.EventPhase;

public final class RotationUpdateEvent implements Event {
   private final EventPhase phase;

   public RotationUpdateEvent(EventPhase phase) {
      this.phase = phase;
   }

   public EventPhase getPhase() {
      return this.phase;
   }

   public boolean isPre() {
      return this.phase == EventPhase.PRE;
   }

   public boolean isPost() {
      return this.phase == EventPhase.POST;
   }
}

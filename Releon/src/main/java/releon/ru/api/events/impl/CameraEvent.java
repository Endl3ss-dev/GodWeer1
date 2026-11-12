package releon.ru.api.events.impl;

import releon.ru.api.events.CancellableEvent;
import releon.ru.api.module.impl.combat.aura.Angle;

public final class CameraEvent extends CancellableEvent {
   private boolean cameraClip;
   private float distance;
   private Angle angle;

   public CameraEvent(boolean cameraClip, float distance, Angle angle) {
      this.cameraClip = cameraClip;
      this.distance = distance;
      this.angle = angle;
   }

   public boolean isCameraClip() {
      return this.cameraClip;
   }

   public float getDistance() {
      return this.distance;
   }

   public Angle getAngle() {
      return this.angle;
   }

   public void setAngle(Angle angle) {
      this.angle = angle;
   }
}

package releon.ru.api.module.impl.combat.aura.rotations;

import java.security.SecureRandom;
import java.util.Random;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import releon.ru.api.module.impl.combat.AuraModule;
import releon.ru.api.module.impl.combat.aura.Angle;
import releon.ru.api.module.impl.combat.aura.MathAngle;
import releon.ru.api.module.impl.combat.aura.attack.StrikeManager;
import releon.ru.api.module.impl.combat.aura.impl.RotateConstructor;
import releon.ru.api.module.impl.combat.aura.util.MathUtils;
import releon.ru.api.module.impl.combat.aura.util.StopWatch;

public class SpookyTimeNewAngle extends RotateConstructor {
   public static final SpookyTimeNewAngle INSTANCE = new SpookyTimeNewAngle();
   private static final float ROTATION_SPEED = 111.0F;
   private static final float LIMIT_ROTATION_SPEED = 24.0F;
   private static final long RELEASE_HOLD_MS = 150L;
   private static final long RELEASE_SLOWDOWN_MS = 100L;
   private static final float SHAKE_INTENSITY = 1.0F;
   private static final float SHAKE_SPEED = 3.0F;
   private static final float EPSILON = 1.0F;
   private static final float AIM_STABLE_ZONE = 15.0F;
   private static final float JITTER_ZONE = 65.0F;
   private static final float YAW_JITTER_SCALE = 0.0F;
   private static final float PITCH_JITTER_SCALE = 0.0F;
   private static final SecureRandom RANDOM = new SecureRandom();
   private static boolean releaseHoldActive;
   private static long releaseHoldUntil;
   private static boolean releaseSlowdownActive;
   private static long releaseSlowdownStart;
   private static Angle releaseFromAngle;
   private final Random bodySwayRandom = new Random();

   private SpookyTimeNewAngle() {
      super("SpookyTime");
   }

   @Override
   public Angle limitAngleChange(Angle currentAngle, Angle targetAngle, Vec3d vec3d, Entity entity) {
      AuraModule aura = AuraModule.getInstance();
      if (aura == null) {
         return currentAngle;
      } else {
         StrikeManager attackHandler = aura.getAttackHandlerRaw();
         StopWatch attackTimer = attackHandler != null ? attackHandler.getAttackTimer() : new StopWatch();
         boolean hasTarget = aura.isEnabled() && AuraModule.target != null && entity != null;
         long now = System.currentTimeMillis();
         if (hasTarget && targetAngle != null) {
            this.resetReleaseState();
            Angle delta = MathAngle.calculateDelta(currentAngle, targetAngle);
            float yawDelta = delta.getYaw();
            float pitchDelta = delta.getPitch();
            float length = (float)Math.hypot((double)yawDelta, (double)pitchDelta);
            if (length < 1.0E-4F) {
               return currentAngle.adjustSensitivity();
            } else {
               float yawLimit = Math.min(Math.abs(yawDelta), 74.0F + this.randomLerp(0.0F, 1.0329834F));
               float pitchLimit = Math.min(Math.abs(pitchDelta), 32.334F);
               Angle moveAngle = new Angle(currentAngle.getYaw(), currentAngle.getPitch());
               float yawJitter = (float)((double)MathUtils.getRandom(12.0F, 24.0F) * Math.sin((double)System.currentTimeMillis() / 40.0));
               float pitchJitter = (float)((double)MathUtils.getRandom(4.0F, 12.0F) * Math.cos((double)System.currentTimeMillis() / 40.0));
               if (attackTimer.finished(1000.0)) {
                  yawJitter = 0.0F;
                  pitchJitter = 0.0F;
               }

               float jitterFactor = MathHelper.clamp((length - 15.0F) / 65.0F, 0.0F, 1.0F);
               yawJitter *= 0.0F * jitterFactor;
               pitchJitter *= 0.0F * jitterFactor;
               float epsilon = 1.0F + this.randomLerp(0.0F, 0.35F);
               if (length > epsilon) {
                  boolean yawLimitReached = Math.abs(yawDelta) >= yawLimit;
                  float yawMaxStep = yawLimitReached ? this.randomLerp(15.599999F, 24.0F * this.randomLerp(1.0F, 2.0F)) : this.randomLerp(77.7F, 122.100006F);
                  float yawStep = Math.min(length, yawMaxStep);
                  float yawScale = yawStep / length;
                  if (!yawLimitReached) {
                     yawScale = this.easeTowardsTarget(yawScale);
                  }

                  moveAngle.setYaw(currentAngle.getYaw() + yawDelta * yawScale + yawJitter);
               }

               if (length > epsilon) {
                  boolean pitchLimitReached = Math.abs(pitchDelta) >= pitchLimit;
                  float pitchMaxStep = pitchLimitReached
                     ? this.randomLerp(15.599999F, 24.0F * this.randomLerp(1.0F, 2.0F))
                     : this.randomLerp(77.7F, 122.100006F);
                  float pitchStep = Math.min(length, pitchMaxStep);
                  float pitchScale = pitchStep / length;
                  if (!pitchLimitReached) {
                     pitchScale = this.easeTowardsTarget(pitchScale);
                  }

                  moveAngle.setPitch(MathHelper.clamp(currentAngle.getPitch() + pitchDelta * pitchScale + pitchJitter, -89.0F, 90.0F));
               }

               this.applyBodyMotion(moveAngle);
               return moveAngle.adjustSensitivity();
            }
         } else {
            return this.handleRelease(currentAngle, now).adjustSensitivity();
         }
      }
   }

   private Angle handleRelease(Angle currentAngle, long now) {
      if (!releaseHoldActive) {
         releaseHoldActive = true;
         releaseHoldUntil = now + 150L;
         releaseSlowdownActive = false;
         releaseSlowdownStart = 0L;
         releaseFromAngle = null;
      }

      if (now < releaseHoldUntil) {
         return new Angle(currentAngle.getYaw(), currentAngle.getPitch());
      } else {
         if (!releaseSlowdownActive) {
            releaseSlowdownActive = true;
            releaseSlowdownStart = now;
            releaseFromAngle = new Angle(currentAngle.getYaw(), currentAngle.getPitch());
         }

         float progress = MathHelper.clamp((float)(now - releaseSlowdownStart) / 100.0F, 0.0F, 1.0F);
         float eased = this.easeTowardsTarget(progress);
         Angle from = releaseFromAngle != null ? releaseFromAngle : currentAngle;
         Angle result = new Angle(
            MathHelper.lerp(eased, from.getYaw(), currentAngle.getYaw()),
            MathHelper.clamp(MathHelper.lerp(eased, from.getPitch(), currentAngle.getPitch()), -89.0F, 90.0F)
         );
         if (progress >= 1.0F) {
            releaseSlowdownActive = false;
            releaseSlowdownStart = 0L;
            releaseFromAngle = null;
         }

         return result;
      }
   }

   private void resetReleaseState() {
      releaseHoldActive = false;
      releaseHoldUntil = 0L;
      releaseSlowdownActive = false;
      releaseSlowdownStart = 0L;
      releaseFromAngle = null;
   }

   private void applyBodyMotion(Angle angle) {
      MinecraftClient client = MinecraftClient.getInstance();
      if (client.player != null) {
         float time = (float)(System.currentTimeMillis() % 12000L) / 1200.0F;
         float swayPhase = time * 3.0F * (float) (Math.PI * 2);
         float swayYaw = (float)(Math.sin((double)swayPhase) * 1.0 * this.bodySwayRandom.nextGaussian());
         angle.setYaw(angle.getYaw() + swayYaw);
      }
   }

   private float easeTowardsTarget(float value) {
      return value * (0.5F + 0.5F * value);
   }

   @Override
   public Vec3d randomValue() {
      return Vec3d.ZERO;
   }

   private float randomLerp(float min, float max) {
      return MathHelper.lerp(RANDOM.nextFloat(), min, max);
   }
}

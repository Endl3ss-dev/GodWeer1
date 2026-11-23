// deobf by pusia
package su.catlean.api.event.events.render;

import com.mojang.blaze3d.vertex.PoseStack;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.MutablePropertyReference1Impl;
import kotlin.properties.Delegates;
import kotlin.properties.ReadWriteProperty;
import kotlin.reflect.KProperty;
import net.minecraft.world.entity.HumanoidArm;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.Event;
import su.catlean.gofra.Gofra;

public final class SwingArmEvent extends Event {
   @NotNull
   public static final SwingArmEvent INSTANCE = new SwingArmEvent();
   public static final KProperty<Object>[] $$delegatedProperties;
   @NotNull
   private static final ReadWriteProperty<Object, Object> swingProgress$delegate = Delegates.INSTANCE.notNull();
   public static PoseStack matrices;
   @NotNull
   private static final ReadWriteProperty<Object, Object> armX$delegate = Delegates.INSTANCE.notNull();
   public static HumanoidArm arm;

   private SwingArmEvent() {
   }

   public final float getSwingProgress() {
      return ((Number)swingProgress$delegate.getValue(this, $$delegatedProperties[0])).floatValue();
   }

   public final void setSwingProgress(float amount) {
      swingProgress$delegate.setValue(this, $$delegatedProperties[0], amount);
   }

   @NotNull
   public final PoseStack getMatrices() {
      if (matrices != null) {
         return matrices;
      } else {
         Intrinsics.throwUninitializedPropertyAccessException("matrices");
         return null;
      }
   }

   public final void setMatrices(PoseStack poseStack) {
      matrices = poseStack;
   }

   private final int getArmX() {
      return ((Number)armX$delegate.getValue(this, $$delegatedProperties[1])).intValue();
   }

   private final void setArmX(int value) {
      armX$delegate.setValue(this, $$delegatedProperties[1], value);
   }

   @NotNull
   public final HumanoidArm getArm() {
      if (arm != null) {
         return arm;
      } else {
         Intrinsics.throwUninitializedPropertyAccessException("arm");
         return null;
      }
   }

   public final void setArm(HumanoidArm humanoidArm) {
      arm = humanoidArm;
   }

   public final boolean call(float amount, PoseStack matrices, int value, HumanoidArm arm) {
      this.setCancelled(false);
      this.setSwingProgress(amount);
      this.setMatrices(matrices);
      this.setArm(arm);
      this.setArmX(value);
      Gofra.INSTANCE.drain(this);
      return this.getCancelled();
   }

   static {
      KProperty[] delegatedProperties = new KProperty[]{
         new MutablePropertyReference1Impl(SwingArmEvent.class, "swingProgress", "getSwingProgress()F", 0),
         new MutablePropertyReference1Impl(SwingArmEvent.class, "armX", "getArmX()I", 0)
      };
      $$delegatedProperties = delegatedProperties;
   }
}
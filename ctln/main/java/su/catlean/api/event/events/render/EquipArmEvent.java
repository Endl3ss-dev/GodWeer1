// deobf by pusia
package su.catlean.api.event.events.render;

import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.MutablePropertyReference1Impl;
import kotlin.properties.Delegates;
import kotlin.properties.ReadWriteProperty;
import kotlin.reflect.KProperty;
import net.minecraft.world.entity.HumanoidArm;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.Event;
import su.catlean.gofra.Gofra;

public final class EquipArmEvent extends Event {
   @NotNull
   public static final EquipArmEvent INSTANCE = new EquipArmEvent();
   public static final KProperty<Object>[] $$delegatedProperties;
   @NotNull
   private static final ReadWriteProperty<Object, Object> progress$delegate = Delegates.INSTANCE.notNull();
   public static HumanoidArm arm;

   private EquipArmEvent() {
   }

   public final float getProgress() {
      return ((Number)progress$delegate.getValue(this, $$delegatedProperties[0])).floatValue();
   }

   public final void setProgress(float amount) {
      progress$delegate.setValue(this, $$delegatedProperties[0], amount);
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

   public final boolean call(float amount, HumanoidArm arm) {
      this.setCancelled(false);
      this.setProgress(amount);
      this.setArm(arm);
      Gofra.INSTANCE.drain(this);
      return this.getCancelled();
   }

   static {
      KProperty[] delegatedProperties = new KProperty[]{new MutablePropertyReference1Impl(EquipArmEvent.class, "progress", "getProgress()F", 0)};
      $$delegatedProperties = delegatedProperties;
   }
}
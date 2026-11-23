// deobf by pusia
package su.catlean.client.features.modules.player;

import java.util.LinkedHashMap;
import java.util.Map;
import kotlin.Unit;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.reflect.KProperty;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.client.TickEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.ActionSetting;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;

public final class ActionRecorder extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(ActionRecorder.class, "recordButton", "getRecordButton()Lsu/catlean/cr;", 0), new PropertyReference1Impl(ActionRecorder.class, "button", "getButton()Lsu/catlean/cr;", 0), new PropertyReference1Impl(ActionRecorder.class, "reset", "getReset()Lsu/catlean/cr;", 0), new PropertyReference1Impl(ActionRecorder.class, "rewindOnDone", "getRewindOnDone()Z", 0) };
   @NotNull
   public static final ActionRecorder INSTANCE = new ActionRecorder();
   @NotNull
   private static final ActionSetting actionSetting = SettingFactory.defaultImpl(INSTANCE, "record-/-stop", () -> {
      ActionRecorder.flag = !ActionRecorder.flag;
      if (ActionRecorder.flag) {
         ActionRecorder.value = 0;
      }

      return Unit.INSTANCE;
   }, null, () -> !ActionRecorder.flag2, 4, null);
   @NotNull
   private static final ActionSetting actionSetting2 = SettingFactory.defaultImpl(INSTANCE, "play-/-stop", () -> {
      ActionRecorder.flag2 = !ActionRecorder.flag2;
      if (ActionRecorder.flag2) {
         ActionRecorder.value = 0;
      }

      return Unit.INSTANCE;
   }, null, () -> !ActionRecorder.flag, 4, null);
   @NotNull
   private static final ActionSetting reset = SettingFactory.defaultImpl(INSTANCE, "reset", () -> {
      ActionRecorder.map.clear();
      ActionRecorder.flag = false;
      ActionRecorder.flag2 = false;
      ActionRecorder.value = 0;
      ActionRecorder.flag3 = false;
      return Unit.INSTANCE;
   }, null, null, 12, null);
   @NotNull
   private static final BooleanSetting rewindOnDone = SettingFactory.defaultImpl(INSTANCE, "rewind-on-done", false, null, null, 12, null);
   @NotNull
   private static final Map<Integer, ActionRecorder.ActionRecord> map = new LinkedHashMap();
   private static boolean flag;
   private static boolean flag2;
   private static int value;
   private static boolean flag3;

   private ActionRecorder() {
      super("action-recorder", SubCategories.getSubCategory10(), null, 4, null);
   }

   private final boolean getRewindOnDone() {
      return (Boolean)rewindOnDone.computeObject(this, $$delegatedProperties[3]);
   }

   @Flow
   private final void onTick(TickEvent tickEvent) {
      if (this.isFlag6()) {
         if (flag) {
            map.put(
               value,
               new ActionRecorder.ActionRecord(
                  MinecraftHolder.getLocalPlayer().getYRot(),
                  MinecraftHolder.getLocalPlayer().getXRot(),
                  MinecraftHolder.getMinecraft().options.keyUp.isDown(),
                  MinecraftHolder.getMinecraft().options.keyDown.isDown(),
                  MinecraftHolder.getMinecraft().options.keyLeft.isDown(),
                  MinecraftHolder.getMinecraft().options.keyRight.isDown(),
                  MinecraftHolder.getMinecraft().options.keyShift.isDown(),
                  MinecraftHolder.getMinecraft().options.keyJump.isDown(),
                  MinecraftHolder.getMinecraft().options.keyUse.isDown(),
                  MinecraftHolder.getMinecraft().options.keyAttack.isDown()
               )
            );
            int value2 = value++;
         }

         if (flag2) {
            if (value >= map.size()) {
               flag3 = true;
               value = map.size();
            }

            if (value < 0) {
               flag3 = false;
               value = 0;
            }

            ActionRecorder.ActionRecord value3 = (ActionRecorder.ActionRecord)map.get(value);
            if (value3 == null) {
               this.update();
               return;
            }

            MinecraftHolder.getLocalPlayer().forceSetRotation(value3.getAmount(), false, value3.getAmount2(), false);
            MinecraftHolder.getMinecraft().options.keyUp.setDown(value3.isFlag());
            MinecraftHolder.getMinecraft().options.keyDown.setDown(value3.isFlag2());
            MinecraftHolder.getMinecraft().options.keyLeft.setDown(value3.isFlag3());
            MinecraftHolder.getMinecraft().options.keyRight.setDown(value3.isFlag4());
            MinecraftHolder.getMinecraft().options.keyShift.setDown(value3.isFlag5());
            MinecraftHolder.getMinecraft().options.keyJump.setDown(value3.isFlag6());
            MinecraftHolder.getMinecraft().options.keyUse.setDown(value3.isFlag7());
            MinecraftHolder.getMinecraft().options.keyAttack.setDown(value3.isFlag8());
            this.update();
         }
      }
   }

   private final void update() {
      if (this.getRewindOnDone()) {
         if (flag3) {
            int value2 = value;
            value = value2 + -1;
         } else {
            int value3 = value++;
         }
      } else {
         int value4 = value++;
         if (flag3) {
            value = 0;
            flag3 = false;
         }
      }
   }

   static {
   }

   public static final class ActionRecord {
      private final float amount;
      private final float amount2;
      private final boolean flag;
      private final boolean flag2;
      private final boolean flag3;
      private final boolean flag4;
      private final boolean flag5;
      private final boolean flag6;
      private final boolean flag7;
      private final boolean flag8;

      public ActionRecord(
         float amount, float amount2, boolean flag, boolean flag2, boolean flag3, boolean flag4, boolean flag5, boolean flag6, boolean flag7, boolean flag8
      ) {
         this.amount = amount;
         this.amount2 = amount2;
         this.flag = flag;
         this.flag2 = flag2;
         this.flag3 = flag3;
         this.flag4 = flag4;
         this.flag5 = flag5;
         this.flag6 = flag6;
         this.flag7 = flag7;
         this.flag8 = flag8;
      }

      public final float getAmount() {
         return this.amount;
      }

      public final float getAmount2() {
         return this.amount2;
      }

      public final boolean isFlag() {
         return this.flag;
      }

      public final boolean isFlag2() {
         return this.flag2;
      }

      public final boolean isFlag3() {
         return this.flag3;
      }

      public final boolean isFlag4() {
         return this.flag4;
      }

      public final boolean isFlag5() {
         return this.flag5;
      }

      public final boolean isFlag6() {
         return this.flag6;
      }

      public final boolean isFlag7() {
         return this.flag7;
      }

      public final boolean isFlag8() {
         return this.flag8;
      }

      public final float getAmount3() {
         return this.amount;
      }

      public final float getAmount4() {
         return this.amount2;
      }

      public final boolean isFlag9() {
         return this.flag;
      }

      public final boolean isFlag10() {
         return this.flag2;
      }

      public final boolean isFlag11() {
         return this.flag3;
      }

      public final boolean isFlag12() {
         return this.flag4;
      }

      public final boolean isFlag13() {
         return this.flag5;
      }

      public final boolean isFlag14() {
         return this.flag6;
      }

      public final boolean isFlag15() {
         return this.flag7;
      }

      public final boolean isFlag16() {
         return this.flag8;
      }

      @NotNull
      public final ActionRecorder.ActionRecord computeActionRecord(
         float amount, float amount2, boolean flag, boolean flag2, boolean flag3, boolean flag4, boolean flag5, boolean flag6, boolean flag7, boolean flag8
      ) {
         return new ActionRecorder.ActionRecord(amount, amount2, flag, flag2, flag3, flag4, flag5, flag6, flag7, flag8);
      }

      @NotNull
      @Override
      public String toString() {
         boolean flag82 = this.flag8;
         boolean flag72 = this.flag7;
         boolean flag62 = this.flag6;
         boolean flag52 = this.flag5;
         boolean flag42 = this.flag4;
         boolean flag32 = this.flag3;
         boolean flag22 = this.flag2;
         boolean flag9 = this.flag;
         float amount22 = this.amount2;
         float amount3 = this.amount;
         return "ActionRecord(yaw="
            + amount3
            + ", pitch="
            + amount22
            + ", forward="
            + flag9
            + ", back="
            + flag22
            + ", left="
            + flag32
            + ", right="
            + flag42
            + ", sneak="
            + flag52
            + ", jump="
            + flag62
            + ", use="
            + flag72
            + ", attack="
            + flag82
            + ")";
      }

      @Override
      public int hashCode() {
         int hashCode = Float.hashCode(this.amount);
         hashCode = hashCode * 31 + Float.hashCode(this.amount2);
         hashCode = hashCode * 31 + Boolean.hashCode(this.flag);
         hashCode = hashCode * 31 + Boolean.hashCode(this.flag2);
         hashCode = hashCode * 31 + Boolean.hashCode(this.flag3);
         hashCode = hashCode * 31 + Boolean.hashCode(this.flag4);
         hashCode = hashCode * 31 + Boolean.hashCode(this.flag5);
         hashCode = hashCode * 31 + Boolean.hashCode(this.flag6);
         hashCode = hashCode * 31 + Boolean.hashCode(this.flag7);
         return hashCode * 31 + Boolean.hashCode(this.flag8);
      }

      @Override
      public boolean equals(Object object) {
         if (this == object) {
            return true;
         } else if (!(object instanceof ActionRecorder.ActionRecord actionRecord)) {
            return false;
         } else if (Float.compare(this.amount, actionRecord.amount) != 0) {
            return false;
         } else if (Float.compare(this.amount2, actionRecord.amount2) != 0) {
            return false;
         } else if (this.flag != actionRecord.flag) {
            return false;
         } else if (this.flag2 != actionRecord.flag2) {
            return false;
         } else if (this.flag3 != actionRecord.flag3) {
            return false;
         } else if (this.flag4 != actionRecord.flag4) {
            return false;
         } else if (this.flag5 != actionRecord.flag5) {
            return false;
         } else if (this.flag6 != actionRecord.flag6) {
            return false;
         } else {
            return this.flag7 != actionRecord.flag7 ? false : this.flag8 == actionRecord.flag8;
         }
      }
   }
}
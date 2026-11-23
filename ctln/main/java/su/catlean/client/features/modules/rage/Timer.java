// deobf by pusia
package su.catlean.client.features.modules.rage;

import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.FloatSetting;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.services.TickService;
import su.catlean.gofra.Flow;
import su.catlean.utils.StopWatch;

public final class Timer extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(Timer.class, "value", "getValue()F", 0), new PropertyReference1Impl(Timer.class, "timeOut", "getTimeOut()Z", 0), new PropertyReference1Impl(Timer.class, "timeOutValue", "getTimeOutValue()I", 0) };
   @NotNull
   public static final Timer INSTANCE = new Timer();
   @NotNull
   private static final FloatSetting value = SettingFactory.defaultImpl(INSTANCE, "value", 1.0F, RangesKt.rangeTo(0.1F, 10.0F), null, null, 24, null);
   @NotNull
   private static final BooleanSetting timeout = SettingFactory.defaultImpl(INSTANCE, "timeout", false, null, null, 12, null);
   @NotNull
   private static final IntSetting timeoutValue = SettingFactory.defaultImpl(
      INSTANCE, "timeout-value", 1, new IntRange(1, 10000), null, () -> INSTANCE.getTimeOut(), 8, null
   );
   @NotNull
   private static final StopWatch stopWatch = new StopWatch();

   private Timer() {
      super("timer", SubCategories.getSubCategory5(), null, 4, null);
   }

   public final float getValue() {
      return ((Number)value.computeObject(this, $$delegatedProperties[0])).floatValue();
   }

   public final boolean getTimeOut() {
      return (Boolean)timeout.computeObject(this, $$delegatedProperties[1]);
   }

   public final int getTimeOutValue() {
      return ((Number)timeoutValue.computeObject(this, $$delegatedProperties[2])).intValue();
   }

   @NotNull
   public final StopWatch getStopWatch() {
      return stopWatch;
   }

   @Override
   public void update2() {
      stopWatch.update();
   }

   @Flow
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      TickService.INSTANCE.setAmount2(this.getValue());
      if (this.getTimeOut() && stopWatch.testValue(this.getTimeOutValue())) {
         this.applyUnplug();
      }
   }

   static {
   }
}
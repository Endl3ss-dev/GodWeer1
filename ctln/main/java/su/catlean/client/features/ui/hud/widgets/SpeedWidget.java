// deobf by pusia
package su.catlean.client.features.ui.hud.widgets;

import java.util.Arrays;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.reflect.KProperty;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.client.features.ui.hud.Widget;
import su.catlean.client.services.TickService;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.world.WorldUtil;

public final class SpeedWidget extends Widget {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{new PropertyReference1Impl(SpeedWidget.class, "bps", "getBps()Z", 0)};
   @NotNull
   public static final SpeedWidget INSTANCE = new SpeedWidget();
   @NotNull
   private static final BooleanSetting bps = SettingFactory.defaultImpl(INSTANCE, "bps", false, null, null, 12, null);

   private SpeedWidget() {
      super("speed-widget");
   }

   private final boolean getBps() {
      return (Boolean)bps.computeObject(this, $$delegatedProperties[0]);
   }

   @Override
   public void render(GuiGraphicsExtractor context) {
      String text;
      if (!this.getBps()) {
         String text2 = "%.1f";
         Object[] objects = new Object[]{(float)Math.rint(this.getAmount() * TickService.INSTANCE.getAmount()) * 0.1};
         text = String.format(text2, Arrays.copyOf(objects, objects.length)) + " km/h";
      } else {
         String text3 = "%.1f";
         Object[] objects2 = new Object[]{(float)Math.rint(this.getAmount2() * TickService.INSTANCE.getAmount()) * 0.1};
         text = String.format(text3, Arrays.copyOf(objects2, objects2.length)) + " b/s";
      }

      String text4 = text;
      this.render(context, text4);
   }

   private final float getAmount() {
      return MinecraftHolder.getLocalPlayer().isFallFlying() ? WorldUtil.INSTANCE.getAmount2() * 720.0F : WorldUtil.INSTANCE.getAmount() * 720.0F;
   }

   private final float getAmount2() {
      return MinecraftHolder.getLocalPlayer().isFallFlying() ? WorldUtil.INSTANCE.getAmount2() * 200.0F : WorldUtil.INSTANCE.getAmount() * 200.0F;
   }

   static {
   }
}
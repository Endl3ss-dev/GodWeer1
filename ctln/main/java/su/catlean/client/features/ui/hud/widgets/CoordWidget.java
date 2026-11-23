// deobf by pusia
package su.catlean.client.features.ui.hud.widgets;

import kotlin.jvm.internal.MutablePropertyReference1Impl;
import kotlin.reflect.KProperty;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.client.features.ui.hud.Widget;
import su.catlean.client.services.ServerInfoService;
import su.catlean.utils.MinecraftHolder;

public final class CoordWidget extends Widget {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new MutablePropertyReference1Impl(CoordWidget.class, "secondaryCoords", "getSecondaryCoords()Z", 0), new MutablePropertyReference1Impl(CoordWidget.class, "onlyNether", "getOnlyNether()Z", 0) };
   @NotNull
   public static final CoordWidget INSTANCE = new CoordWidget();
   @NotNull
   private static final BooleanSetting secondary = SettingFactory.defaultImpl(INSTANCE, "secondary", true, null, null, 12, null);
   @NotNull
   private static final BooleanSetting onlyNether = SettingFactory.defaultImpl(INSTANCE, "only-nether", true, null, null, 12, null);

   private CoordWidget() {
      super("coord-widget");
   }

   private final boolean getSecondaryCoords() {
      return (Boolean)secondary.computeObject(this, $$delegatedProperties[0]);
   }

   private final boolean getOnlyNether() {
      return (Boolean)onlyNether.computeObject(this, $$delegatedProperties[1]);
   }

   @Override
   public void render(GuiGraphicsExtractor context) {
      boolean isFlag = ServerInfoService.INSTANCE.isFlag();
      float value = !isFlag ? 0.125F : 8.0F;
      boolean flag = this.getSecondaryCoords() && (!this.getOnlyNether() || isFlag);
      String text = null;
      text = (int)MinecraftHolder.getLocalPlayer().getX()
         + "  "
         + (int)MinecraftHolder.getLocalPlayer().getY()
         + "  "
         + (int)MinecraftHolder.getLocalPlayer().getZ();
      String text2 = null;
      text2 = "[" + (int)(MinecraftHolder.getLocalPlayer().getX() * value) + "  " + (int)(MinecraftHolder.getLocalPlayer().getZ() * value) + "]";
      if (MinecraftHolder.getLocalPlayer().tickCount < 100) {
         text = "wait " + (100 - MinecraftHolder.getLocalPlayer().tickCount) / 20 + " seconds";
         text2 = "";
      }

      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append((String)text);
      if (flag) {
         stringBuilder.append(" " + text2);
      }

      String text3 = stringBuilder.toString();
      this.render(context, text3);
   }

   static {
   }
}
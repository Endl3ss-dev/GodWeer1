// deobf by pusia
package su.catlean.utils.render;

import java.awt.Color;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.ThemeConfig;

public final class Colors {
   @NotNull
   private static ThemeConfig themeConfig = new ThemeConfig(
      "CatLean",
      new Color(3732704),
      new Color(9783279),
      new Color(105, 105, 105),
      new Color(145, 145, 165),
      new Color(150, 150, 200),
      new Color(255, 255, 255),
      new Color(128, 128, 128),
      new Color(56, 244, 224),
      new Color(30, 30, 40, 60),
      new Color(127, 127, 188, 60),
      new Color(127, 127, 188, 199),
      new Color(56, 56, 86, 255)
   );

   @NotNull
   public static final ThemeConfig getThemeConfig() {
      return themeConfig;
   }
}
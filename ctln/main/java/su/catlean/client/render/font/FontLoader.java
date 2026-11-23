// deobf by pusia
package su.catlean.client.render.font;

import su.catlean.api.IPasteable;
import su.catlean.client.render.WorldRenderer;
import su.catlean.gofra.Gofra;

public final class FontLoader {
   public static final void applyPlug(IPasteable self) {
      Fonts.updateFontRenderer(Fonts.computeFontRenderer(7.5F, "sf_medium"));
      Fonts.updateFontRenderer3(Fonts.computeFontRenderer(8.0F, "sf_medium"));
      Fonts.updateFontRenderer2(Fonts.computeFontRenderer(9.5F, "sf_medium"));
      Fonts.updateFontRenderer4(Fonts.computeFontRenderer(6.5F, "sf_medium"));
      Fonts.updateFontRenderer5(Fonts.computeFontRenderer(12.0F, "sf_bold"));
      Fonts.updateFontRenderer6(Fonts.computeFontRenderer(8.0F, "sf_bold"));
      Fonts.updateFontRenderer7(Fonts.computeFontRenderer(20.0F, "sf_bold"));
      Fonts.updateFontRenderer8(Fonts.computeFontRenderer(25.0F, "sf_bold"));
      Fonts.updateFontRenderer9(Fonts.computeFontRenderer(12.0F, "sf_semibold"));
      Fonts.updateFontRenderer10(Fonts.computeFontRenderer(7.5F, "sf_regular"));
      Gofra.INSTANCE.plug(WorldRenderer.INSTANCE);
   }
}
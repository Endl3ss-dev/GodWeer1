package never.lose.utils.font;

import never.lose.utils.render.msdf.MsdfFont;

import java.util.HashMap;
import java.util.Map;

public class FontManager {
    private final Map<String, MsdfFont> fonts = new HashMap<>();

    public FontManager() {}

    public MsdfFont getFont(String fontName) {
        return fonts.computeIfAbsent(fontName, name -> MsdfFont.builder()
                .atlas(name)
                .data(name)
                .build());
    }

    public void clearFonts() {
        fonts.clear();
    }
}
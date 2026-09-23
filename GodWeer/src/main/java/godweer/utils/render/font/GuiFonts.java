package godweer.utils.render.font;

import godweer.utils.render.msdf.Fonts;

public final class GuiFonts {

    public static final GuiFont SF_REGULAR = new GuiFont(Fonts.Font);
    public static final GuiFont SF_BOLD = new GuiFont(Fonts.Font1);
    public static final GuiFont SF_MEDIUM = new GuiFont(Fonts.Font2);
    public static final GuiFont CLICKGUI = new GuiFont(Fonts.ClickGuiIcons);
    public static final GuiFont ICONF = new GuiFont(Fonts.ClickGuiIcons2);
    public static final GuiFont ICONS = new GuiFont(Fonts.Icon);

    private GuiFonts() {
    }
}

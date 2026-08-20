package fun.volon.api.storages.implement;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import fun.volon.Volon;
import fun.volon.api.storages.implement.helpertstorages.Theme;
import fun.volon.api.utils.color.ColorUtils;

import java.util.Arrays;

@Getter
@Setter
public class ThemeStorage {

    private ObjectArrayList<Themes> themeList = new ObjectArrayList<>();
    private Themes themes;

    public ThemeStorage() {
        this.onInitialize();
    }

    private void onInitialize() {
        this.themeList.addAll(Arrays.asList(
                Themes.Custom,
                Themes.Purple,
                Themes.Red,
                Themes.Blue,
                Themes.Green,
                Themes.Pink,
                Themes.Orange,
                Themes.Blues,
                Themes.Yellows,
                Themes.Cyan,
                Themes.Magenta,
                Themes.Steel,
                Themes.Midnight,
                Themes.Mint,
                Themes.Coral,
                Themes.Amethyst,
                Themes.Sunset,
                Themes.Cherry,
                Themes.Teal,
                Themes.Lime,
                Themes.Wine,
                Themes.Crimson,
                Themes.Navy,
                Themes.Amber,
                Themes.Violet,
                Themes.Forest,
                Themes.Ruby,
                Themes.Sapphire,
                Themes.Pearl,
                Themes.Ember,
                Themes.Arctic,
                Themes.Neon,
                Themes.Shadow,
                Themes.Gold,
                Themes.Silver,
                Themes.Bronze
        ));
        this.themes = this.themeList.get(1);
    }

    @RequiredArgsConstructor @Getter
    public enum Themes {
        Custom(new Theme("Rainbow", ColorUtils.rgba(255, 255, 255, 0))),
        Purple(new Theme("Lavender", ColorUtils.rgba(190, 143, 255, 255), ColorUtils.darken(ColorUtils.rgba(190, 143, 255, 255), 0.35F))),
        Red(new Theme("Blood", ColorUtils.rgba(230, 50, 57, 255), ColorUtils.darken(ColorUtils.rgba(230, 50, 57, 255), 0.35f))),
        Blue(new Theme("Ocean", ColorUtils.rgba(95, 113, 191, 255), ColorUtils.darken(ColorUtils.rgba(95, 113, 191, 255), 0.35f))),
        Green(new Theme("Emerald", ColorUtils.rgba(60, 220, 140, 255), ColorUtils.darken(ColorUtils.rgba(60, 220, 140, 255), 0.35f))),
        Pink(new Theme("Rose", ColorUtils.rgba(255, 120, 190, 255), ColorUtils.darken(ColorUtils.rgba(255, 120, 190, 255), 0.35f))),
        Orange(new Theme("Gold", ColorUtils.rgba(252, 192, 88, 255), ColorUtils.darken(ColorUtils.rgba(252, 192, 88, 255), 0.35f))),
        Blues(new Theme("Diamond", ColorUtils.rgba(125, 217, 250, 255), ColorUtils.darken(ColorUtils.rgba(125, 217, 250, 255), 0.35f))),
        Yellows(new Theme("Sun", ColorUtils.rgba(252, 231, 88, 255), ColorUtils.darken(ColorUtils.rgba(252, 231, 88, 255), 0.35f))),
        Cyan(new Theme("Aqua", ColorUtils.rgba(64, 224, 208, 255), ColorUtils.darken(ColorUtils.rgba(64, 224, 208, 255), 0.35f))),
        Magenta(new Theme("Neon", ColorUtils.rgba(255, 64, 200, 255), ColorUtils.darken(ColorUtils.rgba(255, 64, 200, 255), 0.35f))),
        Steel(new Theme("Steel", ColorUtils.rgba(168, 178, 192, 255), ColorUtils.darken(ColorUtils.rgba(168, 178, 192, 255), 0.35f))),
        Midnight(new Theme("Void", ColorUtils.rgba(88, 72, 168, 255), ColorUtils.darken(ColorUtils.rgba(88, 72, 168, 255), 0.35f))),
        Mint(new Theme("Mint", ColorUtils.rgba(120, 255, 200, 255), ColorUtils.darken(ColorUtils.rgba(120, 255, 200, 255), 0.35f))),
        Coral(new Theme("Coral", ColorUtils.rgba(255, 127, 102, 255), ColorUtils.darken(ColorUtils.rgba(255, 127, 102, 255), 0.35f))),
        Amethyst(new Theme("Amethyst", ColorUtils.rgba(155, 89, 214, 255), ColorUtils.darken(ColorUtils.rgba(155, 89, 214, 255), 0.35f))),
        Sunset(new Theme("Sunset", ColorUtils.rgba(255, 140, 66, 255), ColorUtils.darken(ColorUtils.rgba(255, 94, 98, 255), 0.35f))),
        Cherry(new Theme("Cherry", ColorUtils.rgba(255, 82, 112, 255), ColorUtils.darken(ColorUtils.rgba(255, 82, 112, 255), 0.35f))),
        Teal(new Theme("Teal", ColorUtils.rgba(32, 178, 170, 255), ColorUtils.darken(ColorUtils.rgba(32, 178, 170, 255), 0.35f))),
        Lime(new Theme("Lime", ColorUtils.rgba(180, 255, 80, 255), ColorUtils.darken(ColorUtils.rgba(180, 255, 80, 255), 0.35f))),
        Wine(new Theme("Wine", ColorUtils.rgba(140, 36, 72, 255), ColorUtils.darken(ColorUtils.rgba(140, 36, 72, 255), 0.35f))),
        Crimson(new Theme("Crimson", ColorUtils.rgba(220, 20, 60, 255), ColorUtils.darken(ColorUtils.rgba(220, 20, 60, 255), 0.35f))),
        Navy(new Theme("Navy", ColorUtils.rgba(25, 25, 112, 255), ColorUtils.darken(ColorUtils.rgba(25, 25, 112, 255), 0.35f))),
        Amber(new Theme("Amber", ColorUtils.rgba(255, 191, 0, 255), ColorUtils.darken(ColorUtils.rgba(255, 191, 0, 255), 0.35f))),
        Violet(new Theme("Violet", ColorUtils.rgba(138, 43, 226, 255), ColorUtils.darken(ColorUtils.rgba(138, 43, 226, 255), 0.35f))),
        Forest(new Theme("Forest", ColorUtils.rgba(34, 139, 34, 255), ColorUtils.darken(ColorUtils.rgba(34, 139, 34, 255), 0.35f))),
        Ruby(new Theme("Ruby", ColorUtils.rgba(224, 17, 95, 255), ColorUtils.darken(ColorUtils.rgba(224, 17, 95, 255), 0.35f))),
        Sapphire(new Theme("Sapphire", ColorUtils.rgba(15, 82, 186, 255), ColorUtils.darken(ColorUtils.rgba(15, 82, 186, 255), 0.35f))),
        Pearl(new Theme("Pearl", ColorUtils.rgba(240, 234, 214, 255), ColorUtils.darken(ColorUtils.rgba(240, 234, 214, 255), 0.35f))),
        Ember(new Theme("Ember", ColorUtils.rgba(255, 69, 0, 255), ColorUtils.darken(ColorUtils.rgba(255, 69, 0, 255), 0.35f))),
        Arctic(new Theme("Arctic", ColorUtils.rgba(176, 224, 230, 255), ColorUtils.darken(ColorUtils.rgba(176, 224, 230, 255), 0.35f))),
        Neon(new Theme("Neon", ColorUtils.rgba(57, 255, 20, 255), ColorUtils.darken(ColorUtils.rgba(57, 255, 20, 255), 0.35f))),
        Shadow(new Theme("Shadow", ColorUtils.rgba(52, 52, 52, 255), ColorUtils.darken(ColorUtils.rgba(52, 52, 52, 255), 0.35f))),
        Gold(new Theme("Gold", ColorUtils.rgba(255, 215, 0, 255), ColorUtils.darken(ColorUtils.rgba(255, 215, 0, 255), 0.35f))),
        Silver(new Theme("Silver", ColorUtils.rgba(192, 192, 192, 255), ColorUtils.darken(ColorUtils.rgba(192, 192, 192, 255), 0.35f))),
        Bronze(new Theme("Bronze", ColorUtils.rgba(205, 127, 50, 255), ColorUtils.darken(ColorUtils.rgba(205, 127, 50, 255), 0.35f)));

        final Theme theme;
    }
}

package antileak.base.api.storages.implement;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import antileak.base.Punch;
import antileak.base.api.storages.implement.helpertstorages.Theme;
import antileak.base.api.utils.color.ColorUtils;

import java.awt.*;
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
                Themes.Purple,
                Themes.Red,
                Themes.Blue,
                Themes.Green,
                Themes.Pink,
                Themes.Orange,
                Themes.Blues,
                Themes.Yellows,
                Themes.Mint,
                Themes.Sakura,
                Themes.Cyberpunk,
                Themes.Sunset,
                Themes.Snow,
                Themes.Midnight,
                Themes.Coral,
                Themes.Teal,
                Themes.Amethyst,
                Themes.Wine,
                Themes.Lime,
                Themes.Lavender,
                Themes.Peach,
                Themes.Plum,
                Themes.Slate,
                Themes.Crimson,
                Themes.Aqua,
                Themes.Cinnamon,
                Themes.Forest,
                Themes.Honey,
                Themes.Ivory,
                Themes.Jade,
                Themes.Magenta,
                Themes.Olive,
                Themes.Royal,
                Themes.Tomato,
                Themes.Violet,
                Themes.Bamboo,
                Themes.Frost,
                Themes.Glow
        ));
        this.themes = this.themeList.get(0);
    }

    @RequiredArgsConstructor @Getter
    public enum Themes {
        Purple(new Theme("Lavender", ColorUtils.rgba(190, 143, 255, 255), ColorUtils.darken(ColorUtils.rgba(190, 143, 255, 255), 0.35F))),
        Red(new Theme("Blood", ColorUtils.rgba(230, 50, 57, 255), ColorUtils.darken(ColorUtils.rgba(230, 50, 57, 255), 0.35f))),
        Blue(new Theme("Ocean", ColorUtils.rgba(95, 113, 191, 255), ColorUtils.darken(ColorUtils.rgba(95, 113, 191, 255), 0.35f))),
        Green(new Theme("Emerald", ColorUtils.rgba(60, 220, 140, 255), ColorUtils.darken(ColorUtils.rgba(60, 220, 140, 255), 0.35f))),
        Pink(new Theme("Rose", ColorUtils.rgba(255, 120, 190, 255), ColorUtils.darken(ColorUtils.rgba(255, 120, 190, 255), 0.35f))),
        Orange(new Theme("Gold", ColorUtils.rgba(252, 192, 88, 255), ColorUtils.darken(ColorUtils.rgba(252, 192, 88, 255), 0.35f))),
        Blues(new Theme("Diamond", ColorUtils.rgba(125, 217, 250, 255), ColorUtils.darken(ColorUtils.rgba(125, 217, 250, 255), 0.35f))),
        Yellows(new Theme("Sun", ColorUtils.rgba(252, 231, 88, 255), ColorUtils.darken(ColorUtils.rgba(252, 231, 88, 255), 0.35f))),
        Mint(new Theme("Mint", ColorUtils.rgba(135, 235, 185, 255), ColorUtils.darken(ColorUtils.rgba(135, 235, 185, 255), 0.35f))),
        Sakura(new Theme("Sakura", ColorUtils.rgba(255, 175, 195, 255), ColorUtils.darken(ColorUtils.rgba(255, 175, 195, 255), 0.35f))),
        Cyberpunk(new Theme("Neon", ColorUtils.rgba(255, 0, 128, 255), ColorUtils.darken(ColorUtils.rgba(255, 0, 128, 255), 0.35f))),
        Sunset(new Theme("Sunset", ColorUtils.rgba(255, 105, 95, 255), ColorUtils.darken(ColorUtils.rgba(255, 105, 95, 255), 0.35f))),
        Snow(new Theme("Snow", ColorUtils.rgba(245, 245, 255, 255), ColorUtils.darken(ColorUtils.rgba(245, 245, 255, 255), 0.35f))),
        Midnight(new Theme("Midnight", ColorUtils.rgba(25, 35, 75, 255), ColorUtils.darken(ColorUtils.rgba(25, 35, 75, 255), 0.35f))),
        Coral(new Theme("Coral", ColorUtils.rgba(255, 130, 120, 255), ColorUtils.darken(ColorUtils.rgba(255, 130, 120, 255), 0.35f))),
        Teal(new Theme("Teal", ColorUtils.rgba(50, 200, 200, 255), ColorUtils.darken(ColorUtils.rgba(50, 200, 200, 255), 0.35f))),
        Amethyst(new Theme("Amethyst", ColorUtils.rgba(150, 95, 215, 255), ColorUtils.darken(ColorUtils.rgba(150, 95, 215, 255), 0.35f))),
        Wine(new Theme("Wine", ColorUtils.rgba(140, 30, 60, 255), ColorUtils.darken(ColorUtils.rgba(140, 30, 60, 255), 0.35f))),
        Lime(new Theme("Lime", ColorUtils.rgba(160, 220, 60, 255), ColorUtils.darken(ColorUtils.rgba(160, 220, 60, 255), 0.35f))),
        Lavender(new Theme("Lavender", ColorUtils.rgba(200, 160, 240, 255), ColorUtils.darken(ColorUtils.rgba(200, 160, 240, 255), 0.35f))),
        Peach(new Theme("Peach", ColorUtils.rgba(255, 200, 160, 255), ColorUtils.darken(ColorUtils.rgba(255, 200, 160, 255), 0.35f))),
        Plum(new Theme("Plum", ColorUtils.rgba(180, 80, 140, 255), ColorUtils.darken(ColorUtils.rgba(180, 80, 140, 255), 0.35f))),
        Slate(new Theme("Slate", ColorUtils.rgba(90, 100, 130, 255), ColorUtils.darken(ColorUtils.rgba(90, 100, 130, 255), 0.35f))),
        Crimson(new Theme("Crimson", ColorUtils.rgba(200, 40, 50, 255), ColorUtils.darken(ColorUtils.rgba(200, 40, 50, 255), 0.35f))),
        Aqua(new Theme("Aqua", ColorUtils.rgba(50, 220, 240, 255), ColorUtils.darken(ColorUtils.rgba(50, 220, 240, 255), 0.35f))),
        Cinnamon(new Theme("Cinnamon", ColorUtils.rgba(200, 120, 70, 255), ColorUtils.darken(ColorUtils.rgba(200, 120, 70, 255), 0.35f))),
        Forest(new Theme("Forest", ColorUtils.rgba(50, 140, 70, 255), ColorUtils.darken(ColorUtils.rgba(50, 140, 70, 255), 0.35f))),
        Honey(new Theme("Honey", ColorUtils.rgba(240, 180, 30, 255), ColorUtils.darken(ColorUtils.rgba(240, 180, 30, 255), 0.35f))),
        Ivory(new Theme("Ivory", ColorUtils.rgba(255, 245, 220, 255), ColorUtils.darken(ColorUtils.rgba(255, 245, 220, 255), 0.35f))),
        Jade(new Theme("Jade", ColorUtils.rgba(0, 180, 140, 255), ColorUtils.darken(ColorUtils.rgba(0, 180, 140, 255), 0.35f))),
        Magenta(new Theme("Magenta", ColorUtils.rgba(220, 50, 180, 255), ColorUtils.darken(ColorUtils.rgba(220, 50, 180, 255), 0.35f))),
        Olive(new Theme("Olive", ColorUtils.rgba(160, 160, 50, 255), ColorUtils.darken(ColorUtils.rgba(160, 160, 50, 255), 0.35f))),
        Royal(new Theme("Royal", ColorUtils.rgba(50, 70, 180, 255), ColorUtils.darken(ColorUtils.rgba(50, 70, 180, 255), 0.35f))),
        Tomato(new Theme("Tomato", ColorUtils.rgba(255, 90, 70, 255), ColorUtils.darken(ColorUtils.rgba(255, 90, 70, 255), 0.35f))),
        Violet(new Theme("Violet", ColorUtils.rgba(140, 70, 210, 255), ColorUtils.darken(ColorUtils.rgba(140, 70, 210, 255), 0.35f))),
        Bamboo(new Theme("Bamboo", ColorUtils.rgba(120, 180, 90, 255), ColorUtils.darken(ColorUtils.rgba(120, 180, 90, 255), 0.35f))),
        Frost(new Theme("Frost", ColorUtils.rgba(180, 220, 250, 255), ColorUtils.darken(ColorUtils.rgba(180, 220, 250, 255), 0.35f))),
        Glow(new Theme("Glow", ColorUtils.rgba(255, 240, 100, 255), ColorUtils.darken(ColorUtils.rgba(255, 240, 100, 255), 0.35f)));

        final Theme theme;
    }
}
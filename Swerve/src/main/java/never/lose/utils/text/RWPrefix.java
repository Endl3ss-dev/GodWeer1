package never.lose.utils.text;

import java.awt.Color;

public enum RWPrefix {
    PLAYER   ("ꔀ", "PLAYER",    Color.LIGHT_GRAY),
    HERO     ("ꔄ", "HERO",      Color.BLUE),
    AVENGER  ("ꔒ", "AVENGER",   Color.GREEN),
    OVERLORD ("ꔖ", "OVERLORD",  Color.CYAN),
    MAGISTER ("ꔠ", "MAGISTER",  Color.ORANGE),
    IMPERATOR("ꔤ", "IMPERATOR", Color.RED),
    DRAGON   ("ꔨ", "DRAGON",    new Color(175, 0, 255)),
    COBRA    ("ꕈ", "COBRA",     new Color(0, 170, 0)),
    DHELPER  ("ꕠ", "D.HELPER",  Color.YELLOW),
    HELPER   ("ꔉ", "HELPER",    new Color(255, 215, 0)),
    MLMODER  ("ꔓ", "ML.MODER",  new Color(0, 0, 200)),
    MODER    ("ꔁ", "MODER",     new Color(0, 0, 200)),
    MODERPLUS("ꔡ", "MODER+",    new Color(125, 0, 200)),
    STMODER  ("ꔥ", "ST.MODER",  new Color(0, 0, 200)),
    GLMODER  ("ꔩ", "GL.MODER",  new Color(70, 50, 70)),
    YT       ("ꔅ", "YT",        new Color(175, 0, 0)),
    MEDIA    ("ꔁ", "MEDIA",     new Color(120, 50, 160)),
    MLADMIN  ("ꔳ", "ML.ADMIN",  new Color(0, 200, 160)),
    ADMIN    ("ꔷ", "ADMIN",     new Color(150, 0, 0)),
    HYDRA    ("ꕀ", "HYDRA",     new Color(0, 100, 0)),
    TIGER    ("ꔶ", "TIGER",     new Color(200, 150, 0)),
    BULL     ("ꔲ", "BULL",      new Color(150, 0, 175)),
    DRACULA  ("ꕄ", "DRACULA",   new Color(150, 0, 0)),
    RABBIT   ("ꕒ", "RABBIT",    new Color(200, 200, 200)),
    BUNNY    ("ꕖ", "BUNNY",     new Color(70, 70, 70));

    public final String symbol;
    public final String prefix;
    public final Color  colorprefix;

    RWPrefix(String symbol, String prefix, Color colorprefix) {
        this.symbol      = symbol;
        this.prefix      = prefix;
        this.colorprefix = colorprefix;
    }
}
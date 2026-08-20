package minced.enums;

import net.minecraft.util.Identifier;

public enum Bloom {
    Bloom(Identifier.of("minced", "images/particles/bloom.png")),
    Star(Identifier.of("minced", "images/particles/star.png")),
    Dollar(Identifier.of("minced", "images/particles/dollar.png")),
    Heart(Identifier.of("minced", "images/particles/heart.png")),
    Line(Identifier.of("minced", "images/particles/line.png")),
    CrossHair(Identifier.of("minced", "images/particles/cross.png")),
    Crown(Identifier.of("minced", "images/particles/crown.png")),
    Hacker(Identifier.of("minced", "images/particles/hacker/0.png"), Identifier.of("minced", "images/particles/hacker/1.png")),
    Romb(Identifier.of("minced", "images/particles/rombik.png")),
    Pumpkin(Identifier.of("minced", "images/particles/pumpkin.png"));

    public final Identifier field1079;
    public final Identifier field1080;

    Bloom(Identifier primary) {
        this(primary, null);
    }

    Bloom(Identifier primary, Identifier secondary) {
        this.field1079 = primary;
        this.field1080 = secondary;
    }

    public static void method351() {
        // Kept for compatibility with the decompiled API. Enum constants are initialized by Java.
    }
}



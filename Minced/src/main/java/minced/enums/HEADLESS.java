package minced.enums;

public enum HEADLESS {
    HEADLESS("headless"),
    RENDERABLE("renderable");

    public final String p11;

    HEADLESS(String name) {
        this.p11 = name;
    }

    public String method173() {
        return this.p11;
    }

    public static HEADLESS method174(String param0) {
        if (param0 == null || param0.isBlank()) {
            return null;
        }
        for (HEADLESS v4 : HEADLESS.values()) {
            if (v4.p11.equalsIgnoreCase(param0.trim())) {
                return v4;
            }
        }
        return null;
    }

    public static void method175() {
        // Kept for compatibility with the decompiled API. Enum constants are initialized by Java.
    }
}



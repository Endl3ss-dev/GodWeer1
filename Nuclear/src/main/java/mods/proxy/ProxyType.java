package mods.proxy;

public enum ProxyType {
    DIRECT("Прямое"),
    HTTP("HTTP"),
    SOCKS4("SOCKS4"),
    SOCKS5("SOCKS5");

    private final String displayName;

    private ProxyType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public boolean supportsAuth() {
        return this == HTTP || this == SOCKS5;
    }

    public static ProxyType fromString(String input) {
        if (input != null && !input.isEmpty()) {
            String lower = input.toLowerCase().trim();
            if (!lower.startsWith("http://") && !lower.startsWith("http")) {
                if (!lower.startsWith("socks4://") && !lower.startsWith("socks4")) {
                    if (!lower.startsWith("socks5://") && !lower.startsWith("socks5")) {
                        return !lower.equals("direct") && !lower.equals("none") ? DIRECT : DIRECT;
                    } else {
                        return SOCKS5;
                    }
                } else {
                    return SOCKS4;
                }
            } else {
                return HTTP;
            }
        } else {
            return DIRECT;
        }
    }

    public String getPrefix() {
        String var10000;
        switch (this) {
            case HTTP -> var10000 = "http://";
            case SOCKS4 -> var10000 = "socks4://";
            case SOCKS5 -> var10000 = "socks5://";
            default -> var10000 = "";
        }

        return var10000;
    }
}

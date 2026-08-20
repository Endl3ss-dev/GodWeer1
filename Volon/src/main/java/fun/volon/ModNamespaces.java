package fun.volon;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Resource namespace for assets. Protected JAR stores an ASCII alias in {@code META-INF/volon-ns.txt}
 * (Minecraft only allows {@code [a-z0-9_.-]} in Identifier namespaces).
 */
public final class ModNamespaces {

    private static final String DEFAULT = "volon";
    private static final String ASSETS = resolveAssetsNamespace();

    private ModNamespaces() {
    }

    public static String assets() {
        return ASSETS;
    }

    private static String resolveAssetsNamespace() {
        try (InputStream in = ModNamespaces.class.getResourceAsStream("/META-INF/volon-ns.txt")) {
            if (in == null) {
                return DEFAULT;
            }
            String value = new String(in.readAllBytes(), StandardCharsets.UTF_8).trim();
            return isValidResourceNamespace(value) ? value : DEFAULT;
        } catch (IOException ignored) {
            return DEFAULT;
        }
    }

    /** Matches Minecraft {@link net.minecraft.util.Identifier} namespace rules. */
    static boolean isValidResourceNamespace(String namespace) {
        if (namespace == null || namespace.isEmpty()) {
            return false;
        }
        for (int i = 0; i < namespace.length(); i++) {
            char c = namespace.charAt(i);
            boolean allowed = (c >= 'a' && c <= 'z')
                    || (c >= '0' && c <= '9')
                    || c == '_'
                    || c == '.'
                    || c == '-';
            if (!allowed) {
                return false;
            }
        }
        return true;
    }
}

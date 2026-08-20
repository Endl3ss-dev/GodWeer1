package fun.volon.api.utils.namespaced;


import net.minecraft.util.Identifier;
import fun.volon.ModNamespaces;

public class Namespaced {
    public static Identifier of(String path) {
        return Identifier.of(ModNamespaces.assets(), path);
    }
}

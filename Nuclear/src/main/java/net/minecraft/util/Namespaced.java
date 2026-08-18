package net.minecraft.util;

public class Namespaced extends ResourceLocation {
    public Namespaced(String location) {
        super("Nuclear" + "/" + location);
    }
}

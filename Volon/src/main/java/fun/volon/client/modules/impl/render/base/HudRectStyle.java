package fun.volon.client.modules.impl.render.base;

public enum HudRectStyle {
    SHADER,
    THREADS;

    public static HudRectStyle fromLegacyUnusual(boolean unusual) {
        return unusual ? THREADS : SHADER;
    }
}

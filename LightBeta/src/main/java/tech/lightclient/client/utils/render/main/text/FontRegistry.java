package tech.lightclient.client.utils.render.main.text;

import tech.lightclient.client.utils.render.main.core.Renderer2D;
import tech.lightclient.client.utils.render.main.gl.GlBackend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class FontRegistry {
    private static final Logger LOGGER = LoggerFactory.getLogger(FontRegistry.class);
    private static final Map<String, MsdfFont> REGISTERED_FONTS = new HashMap<>();
    private static final Map<String, RuntimeTtfFont> REGISTERED_TTF_FONTS = new HashMap<>();
    private static final Map<String, FontObject> FONT_OBJECTS = new HashMap<>();
    private static final Map<String, FontRenderer> TEXT_RENDERERS = new HashMap<>();

    private static GlBackend backend;
    private static Renderer2D renderer2D;
    private static boolean backendConfigured = false;
    private static boolean rendererFontsInitialized = false;

    public static FontObject SF_MEDIUM;
    public static FontObject WEXSIDE_MENU_ICONS;
    public static FontObject NEXIS_HUD;
    public static FontObject CATEGORIES;
    public static FontObject NURSULTAN;
    public static FontObject SF_SEMIBOLD;
    public static FontObject SF_BOLD;
    public static FontObject LIGHT_REGULAR;
    public static FontObject LIGHT_BOLD;
    public static FontObject LIGHT_TTF;
    public static FontObject CLICKGUI_TTF;
    public static FontObject LIGHT_GUI_TTF;
    public static FontObject LIGHT_HUDICONS;
    public static FontObject ICONS_NEXIS;
    public static FontObject TEST;
    public static FontObject INTER;
    public static FontObject ICONS_ASYNC;
    public static FontObject TEST1;
    public static FontObject FALLBACK_FONT;
    /** Fonts imported from Delta for the in-game HUD and ClickGUI. */
    public static FontObject DELTA_HUD;
    public static FontObject DELTA_ICONS;
    public static FontObject DELTA_CLICKGUI;
    public static FontObject DELTA_CLICKGUI_MEDIUM;
    public static FontObject DELTA_CLICKGUI_REGULAR;

    private FontRegistry() {
    }

    public static synchronized void initialize(GlBackend glBackend, Renderer2D renderer) {
        configureBackend(glBackend);
        Objects.requireNonNull(renderer, "renderer");
        renderer2D = renderer;
        if (rendererFontsInitialized) {
            return;
        }
        renderer.registerTextRenderer(INTER, createTextRenderer(INTER));
        renderer.registerTextRenderer(SF_MEDIUM, createTextRenderer(SF_MEDIUM));
        renderer.registerTextRenderer(NURSULTAN, createTextRenderer(NURSULTAN));
        renderer.registerTextRenderer(NEXIS_HUD, createTextRenderer(NEXIS_HUD));
        renderer.registerTextRenderer(CATEGORIES, createTextRenderer(CATEGORIES));
        renderer.registerTextRenderer(WEXSIDE_MENU_ICONS, createTextRenderer(WEXSIDE_MENU_ICONS));
        renderer.registerTextRenderer(SF_SEMIBOLD, createTextRenderer(SF_SEMIBOLD));
        renderer.registerTextRenderer(SF_BOLD, createTextRenderer(SF_BOLD));
        renderer.registerTextRenderer(LIGHT_REGULAR, createTextRenderer(LIGHT_REGULAR));
        renderer.registerTextRenderer(LIGHT_BOLD, createTextRenderer(LIGHT_BOLD));
        renderer.registerTextRenderer(LIGHT_TTF, createTextRenderer(LIGHT_TTF));
        renderer.registerTextRenderer(CLICKGUI_TTF, createTextRenderer(CLICKGUI_TTF));
        renderer.registerTextRenderer(LIGHT_GUI_TTF, createTextRenderer(LIGHT_GUI_TTF));
        renderer.registerTextRenderer(LIGHT_HUDICONS, createTextRenderer(LIGHT_HUDICONS));
        renderer.registerTextRenderer(TEST, createTextRenderer(TEST));
        renderer.registerTextRenderer(ICONS_NEXIS, createTextRenderer(ICONS_NEXIS));
        renderer.registerTextRenderer(ICONS_ASYNC, createTextRenderer(ICONS_ASYNC));
        renderer.registerTextRenderer(DELTA_HUD, createTextRenderer(DELTA_HUD));
        renderer.registerTextRenderer(DELTA_ICONS, createTextRenderer(DELTA_ICONS));
        renderer.registerTextRenderer(DELTA_CLICKGUI, createTextRenderer(DELTA_CLICKGUI));
        renderer.registerTextRenderer(DELTA_CLICKGUI_MEDIUM, createTextRenderer(DELTA_CLICKGUI_MEDIUM));
        renderer.registerTextRenderer(DELTA_CLICKGUI_REGULAR, createTextRenderer(DELTA_CLICKGUI_REGULAR));

        rendererFontsInitialized = true;
    }

    public static synchronized FontObject register(String id, String jsonResourcePath, String textureResourcePath) {
        ensureBackendConfigured();
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(jsonResourcePath, "jsonResourcePath");
        Objects.requireNonNull(textureResourcePath, "textureResourcePath");
        if (REGISTERED_FONTS.containsKey(id) || REGISTERED_TTF_FONTS.containsKey(id)) {
            throw new IllegalStateException("Font already registered: " + id);
        }
        MsdfFont font = MsdfFont.load(backend, jsonResourcePath, textureResourcePath);
        REGISTERED_FONTS.put(id, font);
        FontObject fontObject = new FontObject(id);
        FONT_OBJECTS.put(id, fontObject);
        return fontObject;
    }

    public static synchronized FontObject registerTrueType(String id, String ttfResourcePath) {
        ensureBackendConfigured();
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(ttfResourcePath, "ttfResourcePath");
        if (REGISTERED_FONTS.containsKey(id) || REGISTERED_TTF_FONTS.containsKey(id)) {
            throw new IllegalStateException("Font already registered: " + id);
        }
        RuntimeTtfFont font = RuntimeTtfFont.load(id, ttfResourcePath);
        REGISTERED_TTF_FONTS.put(id, font);
        FontObject fontObject = new FontObject(id);
        FONT_OBJECTS.put(id, fontObject);
        return fontObject;
    }

    public static synchronized FontRenderer createTextRenderer(FontObject fontObject) {
        ensureBackendConfigured();
        Objects.requireNonNull(fontObject, "fontObject");

        FontRenderer cached = TEXT_RENDERERS.get(fontObject.id);
        if (cached != null) {
            return cached;
        }

        RuntimeTtfFont ttfFont = REGISTERED_TTF_FONTS.get(fontObject.id);
        if (ttfFont != null) {
            if (renderer2D == null) {
                throw new IllegalStateException("Renderer2D is not configured for runtime TTF fonts");
            }
            FontRenderer renderer = new RuntimeTtfTextRenderer(renderer2D, ttfFont);
            TEXT_RENDERERS.put(fontObject.id, renderer);
            return renderer;
        }

        MsdfFont primary = resolve(fontObject);

        MsdfFont fallback1 = null;
        if (TEST != null && !TEST.id.equals(fontObject.id)) {
            fallback1 = resolve(TEST);
        }

        MsdfFont fallback2 = null;
        if (TEST1 != null && !TEST1.id.equals(fontObject.id)) {
            fallback2 = resolve(TEST1);
        }

        MsdfFont fallback3 = null;
        if (FALLBACK_FONT != null && !FALLBACK_FONT.id.equals(fontObject.id)) {
            fallback3 = resolve(FALLBACK_FONT);
        }

        FontRenderer renderer = new TextRenderer(backend, primary, fallback1, fallback2, fallback3);
        TEXT_RENDERERS.put(fontObject.id, renderer);
        return renderer;
    }

    static synchronized float lineHeight(FontObject fontObject, float size) {
        ensureBackendConfigured();
        if (fontObject == null || size <= 0f) {
            return 0f;
        }
        RuntimeTtfFont ttfFont = REGISTERED_TTF_FONTS.get(fontObject.id);
        if (ttfFont != null) {
            return ttfFont.lineHeight() * (size / Math.max(1.0e-6f, ttfFont.rasterSize()));
        }
        MsdfFont font = resolve(fontObject);
        return font.lineHeight() * (size / Math.max(1.0e-6f, font.emSize()));
    }

    public static synchronized float centeredBaselineOffset(FontObject fontObject, int codepoint, float size) {
        ensureBackendConfigured();
        if (fontObject == null || size <= 0f) {
            return 0f;
        }
        RuntimeTtfFont ttfFont = REGISTERED_TTF_FONTS.get(fontObject.id);
        if (ttfFont != null) {
            RuntimeTtfGlyph glyph = ttfFont.glyph(codepoint);
            if (glyph == null) {
                return 0f;
            }
            float scale = size / Math.max(1.0e-6f, ttfFont.rasterSize());
            return (-glyph.yOffset() + glyph.height() * 0.5f) * scale;
        }
        MsdfFont font = resolve(fontObject);
        MsdfFont.Glyph glyph = font.glyph(codepoint);
        if (glyph == null || !glyph.renderable) {
            return 0f;
        }
        float emSize = Math.max(1.0e-6f, font.emSize());
        float scale = size / emSize;
        return (glyph.planeTop + glyph.planeBottom) * 0.5f * scale;
    }

    public static synchronized FontObject get(String id) {
        ensureBackendConfigured();
        FontObject fontObject = FONT_OBJECTS.get(id);
        if (fontObject == null) {
            throw new IllegalArgumentException("Font not registered: " + id);
        }
        return fontObject;
    }

    static synchronized MsdfFont resolve(FontObject fontObject) {
        ensureBackendConfigured();
        MsdfFont font = REGISTERED_FONTS.get(fontObject.id);
        if (font == null) {
            throw new IllegalStateException("Font not registered: " + fontObject.id);
        }
        return font;
    }

    public static synchronized void destroy() {
        for (MsdfFont font : REGISTERED_FONTS.values()) {
            if (font != null) {
                font.destroy();
            }
        }
        for (RuntimeTtfFont font : REGISTERED_TTF_FONTS.values()) {
            if (font != null) {
                font.destroy();
            }
        }
        REGISTERED_FONTS.clear();
        REGISTERED_TTF_FONTS.clear();
        FONT_OBJECTS.clear();
        TEXT_RENDERERS.clear();
        SF_MEDIUM = null;
        CATEGORIES = null;
        NEXIS_HUD = null;
        WEXSIDE_MENU_ICONS = null;
        NURSULTAN = null;
        SF_SEMIBOLD = null;
        SF_BOLD = null;
        LIGHT_REGULAR = null;
        LIGHT_BOLD = null;
        LIGHT_TTF = null;
        LIGHT_GUI_TTF = null;
        CLICKGUI_TTF = null;
        LIGHT_HUDICONS = null;
        ICONS_NEXIS = null;
        TEST = null;
        INTER = null;
        ICONS_ASYNC = null;
        TEST1 = null;
        FALLBACK_FONT = null;
        DELTA_HUD = null;
        DELTA_ICONS = null;
        DELTA_CLICKGUI = null;
        DELTA_CLICKGUI_MEDIUM = null;
        DELTA_CLICKGUI_REGULAR = null;
        backend = null;
        renderer2D = null;
        backendConfigured = false;
        rendererFontsInitialized = false;
    }

    private static void configureBackend(GlBackend glBackend) {
        Objects.requireNonNull(glBackend, "backend");
        if (backendConfigured) {
            if (backend == glBackend) {
                return;
            }
            destroy();
        }
        backend = glBackend;
        backendConfigured = true;
        registerBuiltinFonts();
    }

    private static void registerBuiltinFonts() {
        TEST1 = register(
                "TEST1",
                "assets/light/fonts/test.json",
                "assets/light/fonts/test.png");
        FALLBACK_FONT = register(
                "fallbackFont",
                "assets/light/fonts/fallback.json",
                "assets/light/fonts/fallback.png");
        TEST = register(
                "TEST",
                "assets/light/fonts/sf_pro_regular.json",
                "assets/light/fonts/sf_pro_regular.png");
        SF_MEDIUM = register(
                "inter_medium",
                "assets/light/fonts/sfprodisplaymedium.json",
                "assets/light/fonts/sfprodisplaymedium.png");
        WEXSIDE_MENU_ICONS = register(
                "wexside_menu_icons",
                "assets/light/fonts/govnoside.json",
                "assets/light/fonts/govnoside.png");
        NEXIS_HUD = register(
                "nexis_hud",
                "assets/light/fonts/NexisFonts.json",
                "assets/light/fonts/NexisFonts.png");
        CATEGORIES = register(
                "category",
                "assets/light/fonts/Category.json",
                "assets/light/fonts/Category.png");
        INTER = register(
                "inter",
                "assets/light/fonts/inter.json",
                "assets/light/fonts/inter.png");
        NURSULTAN = register(
                "nursultan",
                "assets/light/fonts/nurik.json",
                "assets/light/fonts/nurik.png");

        ICONS_NEXIS = register(
                "icons_nexis",
                "assets/light/fonts/nexis.json",
                "assets/light/fonts/nexis.png");
        ICONS_ASYNC = register(
                "icons_async",
                "assets/light/fonts/iconpack1.json",
                "assets/light/fonts/iconpack1.png");

        SF_SEMIBOLD = register(
                "sfprodisplaysemibold",
                "assets/light/fonts/sfprodisplaysemibold.json",
                "assets/light/fonts/sfprodisplaysemibold.png");

        SF_BOLD = register(
                "sfprodisplaybold",
                "assets/light/fonts/sfprodisplaybold.json",
                "assets/light/fonts/sfprodisplaybold.png");
        LIGHT_REGULAR = register(
                "light_regular",
                "assets/light/fonts/light_regular.json",
                "assets/light/fonts/light_regular.png");
        LIGHT_BOLD = register(
                "light_bold",
                "assets/light/fonts/light_bold.json",
                "assets/light/fonts/light_bold.png");
        LIGHT_TTF = registerTrueType(
                "light_ttf",
                "assets/light/fonts/light.ttf");
        LIGHT_GUI_TTF = registerTrueType(
                "light_gui_ttf",
                "assets/light/fonts/LIGHT_GUI.ttf");
        CLICKGUI_TTF = registerTrueType(
                "clickgui_ttf",
                "assets/light/fonts/CLICKGUI.ttf");
        LIGHT_HUDICONS = register(
                "light_hudicons",
                "assets/light/fonts/light_hudicons.json",
                "assets/light/fonts/light_hudicons.png");

        DELTA_HUD = register(
                "delta_hud",
                "assets/light/fonts/delta/gt_regular.json",
                "assets/light/fonts/delta/gt_regular.png");
        DELTA_ICONS = register(
                "delta_icons",
                "assets/light/fonts/delta/icons.json",
                "assets/light/fonts/delta/icons.png");
        DELTA_CLICKGUI = register(
                "delta_clickgui",
                "assets/light/fonts/delta/onest_regular.json",
                "assets/light/fonts/delta/onest_regular.png");
        DELTA_CLICKGUI_MEDIUM = register(
                "delta_clickgui_medium",
                "assets/light/fonts/delta/sf_medium.json",
                "assets/light/fonts/delta/sf_medium.png");
        DELTA_CLICKGUI_REGULAR = register(
                "delta_clickgui_regular",
                "assets/light/fonts/delta/sf_regular.json",
                "assets/light/fonts/delta/sf_regular.png");

    }

    private static void ensureBackendConfigured() {
        if (!backendConfigured || backend == null) {
            throw new IllegalStateException("FontRegistry.initialize(backend, renderer) must be called before use");
        }
    }
}

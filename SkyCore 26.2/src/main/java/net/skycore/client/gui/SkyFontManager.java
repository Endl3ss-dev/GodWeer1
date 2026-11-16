package net.skycore.client.gui;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Менеджер шрифтов, портированный из Arcanic Visuals.
 *
 * <p>Регистрирует шрифты Inter (латиница + кириллица, веса 400/500/600/700)
 * и иконочный шрифт (Lucide + Arcanic glyphs). TTF-файлы и провайдеры JSON
 * скопированы из Arcanic в {@code assets/skycore/font/} и повторяют структуру
 * исходного клиента.
 *
 * <p>Шрифты загружаются через встроенную систему шрифтов Minecraft
 * (провайдеры JSON типа {@code ttf}), поэтому они доступны для
 * {@code Minecraft.getInstance().font} и совместимы с откатом
 * {@link net.skycore.client.util.BeginFrameHelper.TextBuilder#drawVanillaFallback}.
 *
 * <p>MSDF-атласы (.json/.png пары для {@link net.skycore.client.util.TextHeightHelper})
 * должны быть сгенерированы из TTF-файлов отдельно (инструментом msdf-atlas-gen
 * или аналогичным), после чего их можно добавить в {@link net.skycore.client.util.InterBoldHelper}.
 *
 * <p>Адаптировано из шрифтовых провайдеров Arcanic Visuals (GPL-3.0).
 */
public final class SkyFontManager {

    private static final Logger LOGGER = LoggerFactory.getLogger("skycore");

    /** Названия шрифтов Inter по весу. */
    public enum InterWeight {
        REGULAR("inter-regular", 400),
        MEDIUM("inter-medium", 500),
        SEMIBOLD("inter-semibold", 600),
        BOLD("inter-bold", 700);

        public final String prefix;
        public final int weight;

        InterWeight(String prefix, int weight) {
            this.prefix = prefix;
            this.weight = weight;
        }
    }

    /** Зарегистрированные имена шрифтов. */
    private static final Map<String, Boolean> FONTS = new HashMap<>();

    private SkyFontManager() {
    }

    /**
     * Регистрирует все шрифты. Вызывается при инициализации клиента.
     * <p>Создаёт записи для всех размеров и весов, использованных в Arcanic:
     * <ul>
     *   <li>Inter Regular/Medium/Semibold/Bold — размеры 8–32</li>
     *   <li>Inter Semibold — расширенный диапазон 8–64 (как в Arcanic)</li>
     *   <li>Иконки (Lucide + Arcanic glyphs) — размеры 8–40</li>
     * </ul>
     */
    public static void register() {
        // Inter — латиница + кириллица (провайдеры JSON уже лежат в ресурсах)
        int[] interSizes = {8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32};
        for (InterWeight weight : InterWeight.values()) {
            for (int size : interSizes) {
                String name = weight.prefix + "-" + size;
                FONTS.put(name, true);
            }
        }

        // Semibold — расширенный диапазон размеров (как в Arcanic: 8-64)
        int[] semiSizes = {8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32,
                33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60,
                61, 62, 63, 64};
        for (int size : semiSizes) {
            String name = "inter-semibold-" + size;
            FONTS.put(name, true);
        }

        // Иконочный шрифт (Lucide + Arcanic glyphs)
        int[] iconSizes = {8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32,
                33, 34, 35, 36, 37, 38, 39, 40};
        for (int size : iconSizes) {
            String name = "icons-" + size;
            FONTS.put(name, true);
        }

        LOGGER.info("Registered {} Arcanic font references", FONTS.size());
    }

    /** Проверяет, зарегистрирован ли шрифт с таким именем. */
    public static boolean has(String name) {
        return FONTS.containsKey(name);
    }

    /** Возвращает путь к ресурсу шрифта (для использования с Minecraft Font API). */
    public static String resourcePath(String name) {
        return "skycore:font/" + name;
    }
}

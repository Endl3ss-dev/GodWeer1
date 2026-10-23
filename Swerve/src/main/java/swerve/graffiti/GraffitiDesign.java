package swerve.graffiti;

import swerve.Swerve;

import net.minecraft.resources.ResourceLocation;

/**
 * [+] Каталог готовых рисунков граффити.
 *
 * <p>Все текстуры лежат в {@code assets/swerve/textures/graffiti/<файл>.png}:
 * 256x256 RGBA с прозрачным фоном, рисунок вписан в квадрат с отступом 6 %.
 * Поэтому при отрисовке квадрат 1x1 блока закрашивается целиком, а пропорции
 * картинки не ломаются — выравнивать по грани ничего не нужно.
 *
 * <p>Константы enum именуются латиницей: имя константы уходит в файл
 * {@code swerve/graffiti.json}, поэтому переименование подписи в UI данные не теряет.
 */
public enum GraffitiDesign {

    TAG("Тег SWERVE", "tag"),
    CAT("Кот", "cat"),
    SKULL("Череп", "skull"),
    SMILEY("Смайл", "smiley");

    private final String label;
    private final ResourceLocation texture;

    GraffitiDesign(String label, String file) {
        this.label = label;
        this.texture = Swerve.id("textures/graffiti/" + file + ".png");
    }

    /** Подпись для ModeSetting и уведомлений. */
    public String label() {
        return this.label;
    }

    public ResourceLocation texture() {
        return this.texture;
    }

    /** Поиск по подписи из ModeSetting; неизвестная подпись — {@code null}. */
    public static GraffitiDesign byLabel(String label) {
        if (label == null) {
            return null;
        }
        for (GraffitiDesign design : values()) {
            if (design.label.equalsIgnoreCase(label)) {
                return design;
            }
        }
        return null;
    }

    /** Поиск по имени константы из файла; неизвестное имя — {@code null} (запись пропускается). */
    public static GraffitiDesign byName(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }
        for (GraffitiDesign design : values()) {
            if (design.name().equalsIgnoreCase(name)) {
                return design;
            }
        }
        return null;
    }
}

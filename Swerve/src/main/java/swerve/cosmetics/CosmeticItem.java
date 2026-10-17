package swerve.cosmetics;

/**
 * Один элемент гардероба: крылья, рюкзак, питомец или головной убор.
 *
 * @param n        номер модели (он же имя файла модели и текстуры)
 * @param name     название для меню
 * @param type     слот: wings / bodywear / pet / hat
 * @param pos      крепление: 1 — спина, 2 — голова, 3 — свободное, 4 — правая рука, 5 — левая, 6/7 — ноги
 * @param category категория подбора (1..3)
 * @param scale    масштаб модели
 * @param x,y,z    смещение относительно точки крепления
 * @param yaw      поворот по Y
 * @param pitch    поворот по X
 * @param roll     поворот по Z
 */
public record CosmeticItem(
        int n,
        String name,
        String type,
        int pos,
        int category,
        float scale,
        float x,
        float y,
        float z,
        float yaw,
        float pitch,
        float roll) {
}

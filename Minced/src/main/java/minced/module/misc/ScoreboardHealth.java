/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 */
package minced.module.misc;
import minced.core.Module;
import minced.enums.Combat;

public class ScoreboardHealth
extends Module {
    static String field1416 = "Scoreboard Health";
    static String field1417 = "Парсит здоровье с Score игрока";

    public ScoreboardHealth() {
        super(field1416, field1417, Combat.Miscellaneous);
    }
}


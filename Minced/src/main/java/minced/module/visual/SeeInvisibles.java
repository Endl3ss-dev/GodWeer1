/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 */
package minced.module.visual;
import minced.core.SliderSetting;
import minced.core.Module;
import minced.enums.Combat;

public class SeeInvisibles
extends Module {
    SliderSetting field6760 = new SliderSetting(field6763, field6764, field6765, 1.0f, field6766);
    static String field6761 = "See Invisibles";
    static String field6762 = "Позволяет изменить прозрачность невидимых игроков, облегчая их обнаружение";
    static String field6763 = "Прозрачность";
    static float field6764 = 0.5f;
    static float field6765 = 0.3f;
    static float field6766 = 0.1f;

    public SliderSetting cfr_renamed_4() {
        return this.field6760;
    }

    public SeeInvisibles() {
        super(field6761, field6762, Combat.Visuals);
        this.addSettings(this.field6760);
    }
}




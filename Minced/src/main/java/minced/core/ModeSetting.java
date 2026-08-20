/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 */
package minced.core;

import java.util.function.Supplier;
import minced.core.Setting;
import minced.core.Animation;
import minced.core.Easings;

public class ModeSetting
extends Setting {
    public java.lang.String defaultMode;
    String[] options;
    Animation[] optionAnimations;
    static long animationDuration = 150L;

    public ModeSetting(String param1, String param2, Supplier param3, String ... param4) {
        super(param1, param2);
        this.defaultMode = param2;
        this.options = param4;
        this.visibleWhen(param3);
        this.initializeAnimations();
    }

    private void initializeAnimations() {
        this.optionAnimations = new Animation[this.options.length];
        for (int v1 = 0; v1 < this.options.length; ++v1) {
            this.optionAnimations[v1] = new Animation(Easings.field11969, this.options[v1].equalsIgnoreCase(this.defaultMode) ? 1.0f : 0.0f, animationDuration);
        }
    }

    public ModeSetting(String param1, String param2, String[] param3, Supplier param4) {
        super(param1, param2);
        this.defaultMode = param2;
        this.options = param3;
        this.visibleWhen(param4);
        this.initializeAnimations();
    }

    public boolean isMode(String param1) {
        return ((String)this.getValue()).equalsIgnoreCase(param1);
    }

    @Override
    public void setValue(Object param1) {
        if (param1 instanceof String) {
            String value = (String)param1;
            for (String option : this.options) {
                if (option.equalsIgnoreCase(value)) {
                    super.setValue(option);
                    return;
                }
            }
        }
        super.setValue(this.defaultMode);
    }

    public ModeSetting(String param1, String param2, String ... param3) {
        super(param1, param2);
        this.defaultMode = param2;
        this.options = param3;
        this.initializeAnimations();
    }

    public int getSelectedIndex() {
        int v1 = 0;
        for (String v5 : this.options) {
            if (v5.equalsIgnoreCase((String)this.getValue())) {
                return v1;
            }
            ++v1;
        }
        return 0;
    }
}



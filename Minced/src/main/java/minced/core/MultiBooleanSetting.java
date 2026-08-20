/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  sLM.4ZXYEo
 */
package minced.core;

import java.util.Arrays;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import minced.core.BooleanSetting;
import minced.core.Setting;
import minced.core.Animation;
import minced.core.Easings;
import sLM._4ZXYEo;

public class MultiBooleanSetting
extends Setting {
    static String cfr_renamed_124;

    Animation[] field7736;
    static long field7737 = 150L;

    public MultiBooleanSetting(String param1, Supplier param2, BooleanSetting ... param3) {
        super(param1, Arrays.asList(param3));
        for (BooleanSetting v5 : (List<BooleanSetting>)this.getValue()) {
            v5.value = v5.getValue();
        }
        this.visibleWhen(param2);
        this.method1900();
    }

    public MultiBooleanSetting(String param1, BooleanSetting ... param2) {
        super(param1, Arrays.asList(param2));
        for (BooleanSetting v4 : (List<BooleanSetting>)this.getValue()) {
            v4.value = v4.getValue();
        }
        this.method1900();
    }

    public Boolean method1898(String param12) {
        return (Boolean)((List<Setting>)this.getValue()).stream().filter(param1 -> param1.getName().equalsIgnoreCase(param12)).findFirst().map(x -> x.getValue()).orElse(null);
    }

    public BooleanSetting method1899(int param1) {
        List v2 = (List)this.getValue();
        if (param1 >= 0 && param1 < v2.size()) {
            return (BooleanSetting)v2.get(param1);
        }
        throw new IndexOutOfBoundsException((String)((Object)_4ZXYEo.弟8("makeConcatWithConstants", new Object[]{}, (int)param1, (int)v2.size())));
    }

    private void method1900() {
        List v1 = (List)this.getValue();
        this.field7736 = new Animation[v1.size()];
        for (int v2 = 0; v2 < v1.size(); ++v2) {
            this.field7736[v2] = new Animation(Easings.field11969, (Boolean)((BooleanSetting)v1.get(v2)).getValue() != false ? 1.0f : 0.0f, field7737);
        }
    }

    static {
        cfr_renamed_124 = "Index \u0001 is out of bounds for size \u0001";
    }
}




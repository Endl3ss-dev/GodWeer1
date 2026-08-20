/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  net.fabricmc.fabric.api.blockview.v2.FabricBlockView
 *  net.fabricmc.fabric.mixin.blockview.BlockViewMixin
 *  net.minecraft.world.HeightLimitView
 */
package minced.manager;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.function.Consumer;
import minced.core.BindSetting;
import minced.core.ColorSetting;
import minced.core.MultiBooleanSetting;
import minced.core.BooleanSetting;
import minced.core.SliderSetting;
import minced.core.GroupSetting;
import minced.core.Setting;
import minced.core.ColorListSetting;
import minced.core.ModeSetting;
import minced.core.AutoConfigState;
import minced.core.ConfigFile;
import minced.core.Minced;
import minced.core.Module;
import minced.enums.Eternity;
import minced.enums.TOGGLE;
import net.fabricmc.fabric.api.blockview.v2.FabricBlockView;
import net.fabricmc.fabric.mixin.blockview.BlockViewMixin;
import net.minecraft.world.HeightLimitView;

public class AutoConfig
extends ConfigFile
implements FabricBlockView,
HeightLimitView,
BlockViewMixin {
    @Override
    public int getHeight() { return 0; }

    @Override
    public int getBottomY() { return 0; }

    static String cfr_renamed_329;
    static String cfr_renamed_330;
    static String cfr_renamed_331;
    static String cfr_renamed_332;
    static String cfr_renamed_333;

    static String field6767;
    static String field6768;
    Object data;
    static String field6769;
    static String field6770;
    static String field6771;
    static String field6772;
    static String field6773;
    static String field6774;
    static String field6775;
    static String field6776;
    static String field6777;
    static String field6778;
    static String field6779;
    static String field6780;
    static String field6781;
    static String field6782;
    static String field6783;
    static String field6784;
    static String field6785;
    static String field6786;
    static String field6787;
    static String field6788;
    static String field6789 = "temp\\autoconfig.file";
    static String field6790;
    static String field6791;
    static String field6792;
    static String field6793;
    static String field6794;
    static String field6795;
    static String field6796;
    static String field6797;
    static String field6798;
    static String field6799;

    private void method1625(JsonObject param1, String param2, Consumer param3) {
        JsonElement v4 = param1.get(param2);
        if (v4 != null && !v4.isJsonNull()) {
            param3.accept(v4);
        }
    }

    public void method1626() {
        this.save();
        if (Minced.getInstance().getDraggingManager() != null) {
            Minced.getInstance().getDraggingManager().save();
        }
        System.out.println(field6767);
    }

    private void method1628(JsonObject param12, Setting param2) {
        JsonElement v3 = param12.get(param2.getName());
        if (v3 == null || v3.isJsonNull()) {
            return;
        }
        if (param2 instanceof SliderSetting) {
            SliderSetting v4 = (SliderSetting)param2;
            v4.method1906(Float.valueOf(v3.getAsFloat()));
        } else if (param2 instanceof BooleanSetting) {
            BooleanSetting v5 = (BooleanSetting)param2;
            if (v3.isJsonObject()) {
                JsonObject v12 = v3.getAsJsonObject();
                this.method1625(v12, field6772, param1 -> v5.setValue(((JsonElement)param1).getAsBoolean()));
                this.method1625(v12, field6773, param1 -> v5.cfr_renamed_0(((JsonElement)param1).getAsInt()));
                this.method1625(v12, field6774, param1 -> v5.method1904(TOGGLE.valueOf(((JsonElement)param1).getAsString())));
            }
        } else if (param2 instanceof ColorSetting) {
            ColorSetting v6 = (ColorSetting)param2;
            v6.setValue(v3.getAsInt());
        } else if (param2 instanceof ModeSetting) {
            ModeSetting v7 = (ModeSetting)param2;
            v7.setValue(v3.getAsString());
        } else if (param2 instanceof BindSetting) {
            BindSetting v8 = (BindSetting)param2;
            v8.setValue(v3.getAsInt());
        } else if (param2 instanceof GroupSetting) {
            GroupSetting v9 = (GroupSetting)param2;
            v9.setValue(v3.getAsString());
        } else if (param2 instanceof ColorListSetting) {
            ColorListSetting v10 = (ColorListSetting)param2;
            v10.setValue(v3.getAsString());
        } else if (param2 instanceof MultiBooleanSetting) {
            MultiBooleanSetting v11 = (MultiBooleanSetting)param2;
            if (v3.isJsonObject()) {
                JsonObject v12 = v3.getAsJsonObject();
                ((List)v11.getValue()).forEach(param1 -> {
                    JsonElement v2 = v12.get(((Setting)param1).getName());
                    if (v2 != null && !v2.isJsonNull()) {
                        ((Setting)param1).setValue(v2.getAsBoolean());
                    }
                });
            }
        }
    }

    private void method1631(JsonObject param1) {
        JsonObject v2 = new JsonObject();
        v2.addProperty(field6775, Minced.getInstance().getThemeManager().cfr_renamed_272().name());
        v2.addProperty(field6776, Minced.getInstance().getThemeManager().method285());
        param1.add(field6777, (JsonElement)v2);
    }

    @Override
    protected void resetData() {
        this.data = new AutoConfigState();
    }

    @Override
    protected void readJson(JsonObject param1) {
        this.method1636(param1);
        if (param1.has(field6778) && !param1.get(field6779).isJsonNull()) {
            this.method1638(param1.getAsJsonObject(field6780));
        }
        if (param1.has(field6781) && param1.get(field6782).isJsonObject() && Minced.getInstance().getDraggingManager() != null) {
            Minced.getInstance().getDraggingManager().method1657(param1.getAsJsonObject(field6783));
        }
        this.method1625(param1, field6784, param0 -> {
            if (Minced.getInstance().field12524 != null) {
                Minced.getInstance().field12524.method71(((JsonElement)param0).getAsString());
            }
        });
    }

    @Override
    protected JsonObject writeJson() {
        JsonObject v1 = new JsonObject();
        this.method1631(v1);
        v1.add(field6785, (JsonElement)this.method1644());
        if (Minced.getInstance().getDraggingManager() != null) {
            v1.add(field6786, (JsonElement)Minced.getInstance().getDraggingManager().method1684());
        }
        if (Minced.getInstance().field12524 != null) {
            v1.addProperty(field6787, Minced.getInstance().field12524.method74());
        }
        return v1;
    }

    private void method1636(JsonObject param12) {
        this.method1625(param12, field6788, param1 -> {
            JsonObject v2 = ((JsonElement)param1).getAsJsonObject();
            if (v2.has(field6768)) {
                ((AutoConfigState)this.data).method3551(v2.get(field6769).getAsString());
            }
            if (v2.has(field6770)) {
                ((AutoConfigState)this.data).method3553(v2.get(field6771).getAsString());
            }
        });
    }

    public AutoConfig() {
        super(field6789);
    }

    private void method1638(JsonObject param1) {
        if (Minced.getInstance().getModuleManager() == null) {
            return;
        }
        boolean v2 = Module.isStateChangeSuppressed();
        Module.setStateChangeSuppressed(true);
        Minced.getInstance().getModuleManager().method178().forEach(param22 -> {
            JsonObject v3 = param1.getAsJsonObject(param22.getName().toLowerCase());
            if (v3 == null) {
                return;
            }
            param22.cfr_renamed_4(false);
            this.method1625(v3, field6790, param1_212 -> param22.setKey(((JsonElement)param1_212).getAsInt()));
            this.method1625(v3, field6791, param1_213 -> param22.cfr_renamed_4(((JsonElement)param1_213).getAsBoolean()));
            this.method1625(v3, field6792, param1_214 -> param22.setToggleMode(TOGGLE.valueOf(((JsonElement)param1_214).getAsString())));
            param22.getSettings().forEach(param2 -> this.method1628(v3, (Setting)param2));
        });
        Module.setStateChangeSuppressed(v2);
    }

    public void method1641() {
        if (((AutoConfigState)this.data).l() != null) {
            Minced.getInstance().getThemeManager().cfr_renamed_0(Eternity.valueOf(((AutoConfigState)this.data).l()));
        }
        if (((AutoConfigState)this.data).method3552() != null) {
            Minced.getInstance().getThemeManager().method278(((AutoConfigState)this.data).method3552());
        }
    }

    private JsonObject method1644() {
        JsonObject v1 = new JsonObject();
        if (Minced.getInstance().getModuleManager() == null) {
            return v1;
        }
        Minced.getInstance().getModuleManager().method178().forEach(param22 -> {
            JsonObject v3 = new JsonObject();
            v3.addProperty(field6793, (Number)param22.getKey());
            v3.addProperty(field6794, Boolean.valueOf(param22.isEnabled()));
            v3.addProperty(field6795, param22.getToggleMode().name());
            param22.getSettings().forEach(param2 -> this.method1647(v3, (Setting)param2));
            v1.add(param22.getName().toLowerCase(), (JsonElement)v3);
        });
        return v1;
    }

    public void method1645() {
        if (this.exists()) {
            this.loadFromDisk();
            this.method1641();
            System.out.println(field6796);
        }
    }

    private void method1647(JsonObject param12, Setting param2) {
        if (param2 instanceof BooleanSetting) {
            BooleanSetting v3 = (BooleanSetting)param2;
            JsonObject v11 = new JsonObject();
            v11.addProperty(field6797, (Boolean)v3.getValue());
            v11.addProperty(field6798, (Number)v3.method1903());
            v11.addProperty(field6799, v3.method1901().name());
            param12.add(param2.getName(), (JsonElement)v11);
        } else if (param2 instanceof SliderSetting) {
            SliderSetting v4 = (SliderSetting)param2;
            param12.addProperty(param2.getName(), (Number)v4.getValue());
        } else if (param2 instanceof ModeSetting) {
            ModeSetting v5 = (ModeSetting)param2;
            param12.addProperty(param2.getName(), (String)v5.getValue());
        } else if (param2 instanceof ColorSetting) {
            ColorSetting v6 = (ColorSetting)param2;
            param12.addProperty(param2.getName(), (Number)v6.getValue());
        } else if (param2 instanceof BindSetting) {
            BindSetting v7 = (BindSetting)param2;
            param12.addProperty(param2.getName(), (Number)v7.getValue());
        } else if (param2 instanceof GroupSetting) {
            GroupSetting v8 = (GroupSetting)param2;
            param12.addProperty(param2.getName(), (String)v8.getValue());
        } else if (param2 instanceof ColorListSetting) {
            ColorListSetting v9 = (ColorListSetting)param2;
            param12.addProperty(param2.getName(), (String)v9.getValue());
        } else if (param2 instanceof MultiBooleanSetting) {
            MultiBooleanSetting v10 = (MultiBooleanSetting)param2;
            JsonObject v11 = new JsonObject();
            ((List)v10.getValue()).forEach(param1 -> v11.addProperty(((Setting)param1).getName(), (Boolean)((Setting)param1).getValue()));
            param12.add(param2.getName(), (JsonElement)v11);
        }
    }

    static {
        field6785 = "modules";
        field6786 = "hud";
        field6787 = "commandPrefix";
        field6778 = "modules";
        field6779 = "modules";
        field6780 = "modules";
        field6781 = "hud";
        field6782 = "hud";
        field6783 = "hud";
        field6784 = "commandPrefix";
        field6775 = "palette";
        field6776 = "particle";
        field6777 = "theme";
        field6788 = "theme";
        cfr_renamed_329 = "Failed to apply theme from autoconfig: \u0001";
        field6797 = "value";
        field6798 = "bind";
        field6799 = "togglemode";
        cfr_renamed_330 = "Failed to save setting \u0001: \u0001";
        field6772 = "value";
        field6773 = "bind";
        field6774 = "togglemode";
        cfr_renamed_331 = "Failed to load setting \u0001: \u0001";
        field6767 = "[Minced] AutoConfig saved successfully on shutdown";
        cfr_renamed_332 = "[Minced] Failed to save autoconfig on shutdown: \u0001";
        field6796 = "[Minced] AutoConfig loaded successfully";
        field6790 = "key";
        field6791 = "enabled";
        field6792 = "togglemode";
        field6793 = "key";
        field6794 = "enabled";
        field6795 = "togglemode";
        field6768 = "palette";
        field6769 = "palette";
        field6770 = "particle";
        field6771 = "particle";
        cfr_renamed_333 = "Failed to load theme from autoconfig: \u0001";
    }
}




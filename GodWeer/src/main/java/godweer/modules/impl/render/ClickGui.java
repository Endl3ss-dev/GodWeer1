package godweer.modules.impl.render;

import godweer.modules.Module;
import godweer.modules.ModuleCategory;
import godweer.modules.api.setting.FloatSetting;

public final class ClickGui extends Module {

    public static final float DEFAULT_CATEGORY_GAP = 10.0f;
    public static final float DEFAULT_CATEGORY_WIDTH = 115.0f;
    public static final float DEFAULT_SIZE = 245.0f;

    private static ClickGui instance;

    private final FloatSetting categoryGap = new FloatSetting(
            "Расстояние между категориями",
            DEFAULT_CATEGORY_GAP,
            0.0f,
            30.0f,
            1.0f
    );
    private final FloatSetting categoryWidth = new FloatSetting(
            "Ширина категорий",
            DEFAULT_CATEGORY_WIDTH,
            100.0f,
            180.0f,
            5.0f
    );
    private final FloatSetting size = new FloatSetting(
            "Размер",
            DEFAULT_SIZE,
            220.0f,
            380.0f,
            5.0f
    );

    public ClickGui() {
        super("ClickGui", "Настройки интерфейса ClickGui", ModuleCategory.RENDER);
        instance = this;
        addSettings(categoryGap, categoryWidth, size);
        setEnable(true);
    }

    @Override
    public void onDisable() {
        setEnable(true);
    }

    public static float getCategoryGap() {
        ClickGui module = instance;
        return module == null ? DEFAULT_CATEGORY_GAP : module.categoryGap.get();
    }

    public static float getCategoryWidth() {
        ClickGui module = instance;
        return module == null ? DEFAULT_CATEGORY_WIDTH : module.categoryWidth.get();
    }

    public static float getSize() {
        ClickGui module = instance;
        return module == null ? DEFAULT_SIZE : module.size.get();
    }

    public FloatSetting categoryGapSetting() {
        return this.categoryGap;
    }

    public FloatSetting categoryWidthSetting() {
        return this.categoryWidth;
    }

    public FloatSetting sizeSetting() {
        return this.size;
    }
}

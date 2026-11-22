package tech.lightclient.modules.impl.render;

import tech.lightclient.modules.api.Category;
import tech.lightclient.modules.api.Function;
import tech.lightclient.modules.api.FunctionAdd;
import tech.lightclient.modules.api.settings.impl.BooleanSetting;
import tech.lightclient.modules.api.settings.impl.ModeSetting;
import tech.lightclient.modules.api.settings.impl.SliderSetting;

@FunctionAdd(name = "TargetESP", alias = "Target ESP", category = Category.Render, description = "Отображает эффекты вокруг текущего таргета из любого модуля")
public class TargetESP extends Function {

    public final ModeSetting targetEspType = new ModeSetting(
            "Тип таргет есп",
            "Шейдер",
            "Шейдер", "Квадрат", "Прицел", "Сканер", "Цепи", "Кристалы", "Кристалы Новые", "Молнии");

    public final ModeSetting imageType = new ModeSetting("Картинка", "Квадрат", "Квадрат", "Полу квадрат")
            .setVisible(() -> targetEspType.is("Квадрат"));

    public final ModeSetting shaderType = new ModeSetting("Шейдер", "Призраки", "Призраки", "Души", "Души 2",
            "Колечко")
            .setVisible(() -> targetEspType.is("Шейдер"));

    public final SliderSetting sizeDush = new SliderSetting("Размер частиц", 0.22f, 0.1f, 0.4f, 0.01f)
            .setVisible(() -> targetEspType.is("Шейдер"));
    public final SliderSetting dlinaDush = new SliderSetting("Длина эффекта", 6f, 1f, 12f, 0.1f)
            .setVisible(() -> targetEspType.is("Шейдер"));
    public final SliderSetting factorDush = new SliderSetting("Фактор эффекта", 12f, 0f, 22f, 0.1f)
            .setVisible(() -> targetEspType.is("Шейдер"));
    public final SliderSetting particleDensityDush = new SliderSetting("Кол-во частиц", 1.7f, 1f, 3f, 0.1f)
            .setVisible(() -> targetEspType.is("Шейдер"));

    public final SliderSetting chainSpeed = new SliderSetting("Скорость цепей", 1.0f, 0.1f, 3.0f, 0.1f)
            .setVisible(() -> targetEspType.is("Цепи"));

    public final ModeSetting boltStyle = new ModeSetting("Вид молний", "Дефолт", "Дефолт", "Тест")
            .setVisible(() -> targetEspType.is("Молнии"));

    private boolean isDefaultBolt() {
        return targetEspType.is("Молнии") && boltStyle.is("Дефолт");
    }

    private boolean isTestBolt() {
        return targetEspType.is("Молнии") && boltStyle.is("Тест");
    }

    public final SliderSetting boltCount = new SliderSetting("Кол-во молний", 2f, 1f, 30f, 1f)
            .setVisible(this::isDefaultBolt);
    public final SliderSetting boltSegments = new SliderSetting("Сегменты молнии", 21f, 4f, 30f, 1f)
            .setVisible(this::isDefaultBolt);
    public final SliderSetting boltJitter = new SliderSetting("Разброс молнии", 1.0f, 0.05f, 1.0f, 0.05f)
            .setVisible(this::isDefaultBolt);
    public final SliderSetting boltRefresh = new SliderSetting("Частота молнии", 55f, 10f, 400f, 5f)
            .setVisible(this::isDefaultBolt);
    public final SliderSetting boltSpeed = new SliderSetting("Скорость молнии", 1.6f, 0.1f, 8.0f, 0.1f)
            .setVisible(this::isDefaultBolt);
    public final SliderSetting boltWidth = new SliderSetting("Толщина молнии", 1.2f, 0.5f, 5.0f, 0.1f)
            .setVisible(this::isDefaultBolt);
    public final SliderSetting boltVolume = new SliderSetting("Дистанция от цели", 0.1f, 0.0f, 3.0f, 0.1f)
            .setVisible(this::isDefaultBolt);
    public final BooleanSetting boltGlow = new BooleanSetting("Свечение молнии", true)
            .setVisible(this::isDefaultBolt);
    public final BooleanSetting boltBranches = new BooleanSetting("Ветви молнии", true)
            .setVisible(this::isDefaultBolt);
    public final BooleanSetting boltFlicker = new BooleanSetting("Мерцание молнии", true)
            .setVisible(this::isDefaultBolt);

    public final BooleanSetting testHurtColor = new BooleanSetting("Окрашивание при ударе", true)
            .setVisible(this::isTestBolt);
    public final SliderSetting testBoltCount = new SliderSetting("Кол-во молний", 3f, 1f, 6f, 1f)
            .setVisible(this::isTestBolt);
    public final SliderSetting testBoltWidth = new SliderSetting("Толщина", 1.0f, 0.5f, 3.0f, 0.1f)
            .setVisible(this::isTestBolt);

    public TargetESP() {
        addSettings(targetEspType, imageType, shaderType, sizeDush, dlinaDush, factorDush,
                particleDensityDush, chainSpeed, boltStyle, boltCount, boltSegments, boltJitter, boltRefresh,
                boltSpeed, boltWidth, boltVolume, boltGlow, boltBranches, boltFlicker,
                testHurtColor, testBoltCount, testBoltWidth);
    }
}

package org.privates.client.managers.module.impl.render;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.privates.client.api.annotations.Category3;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.ColorSetting;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.other.Instance;

@Category3
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "TabSettings", category = Category.RENDER, description = "Настройки таба")
public class TabSettings extends Module {
    public static TabSettings getInstance() {
        return Instance.get(TabSettings.class);
    }

    private final BooleanSetting animation = new BooleanSetting(this, "Анимация", true);
    private final ModeSetting animationType = new ModeSetting(this, "Тип анимации", "Плавная", "Bounce", "Elastic", "Fade", "Slide", "Scale")
            .setVisible(animation::getValue);
    private final SliderSetting animationSpeed = new SliderSetting(this, "Скорость анимации", 115F, 50F, 300F, 5F)
            .setVisible(animation::getValue);

    private final BooleanSetting highlightSelf = new BooleanSetting(this, "Подсветка своего ника", true);
    private final ColorSetting selfColor = new ColorSetting(this, "Цвет своего ника")
            .setVisible(highlightSelf::getValue);

    private final BooleanSetting highlightFriend = new BooleanSetting(this, "Подсветка друзей", true);
    private final ColorSetting friendColor = new ColorSetting(this, "Цвет друзей")
            .setVisible(highlightFriend::getValue);

    private final BooleanSetting background = new BooleanSetting(this, "Фон таба", true);

    private final BooleanSetting customPosition = new BooleanSetting(this, "Кастом позиция", false);
    private final SliderSetting posX = new SliderSetting(this, "X позиция", 0F, -500F, 500F, 1F)
            .setVisible(customPosition::getValue);
    private final SliderSetting posY = new SliderSetting(this, "Y позиция", 0F, -500F, 500F, 1F)
            .setVisible(customPosition::getValue);

    private final ModeSetting pingRender = new ModeSetting(this, "Рендер пинга", "Стандарт", "Полоска", "Круг", "Градиент");

    private final BooleanSetting showHeads = new BooleanSetting(this, "Показывать головы", true);
    private final BooleanSetting headsOnlyFriends = new BooleanSetting(this, "Головы только друзей", false)
            .setVisible(showHeads::getValue);
    private final BooleanSetting headsOnlySelf = new BooleanSetting(this, "Голова только себя", false)
            .setVisible(showHeads::getValue);

    private final SliderSetting maxPlayers = new SliderSetting(this, "Макс игроков в столбце", 20F, 10F, 80F, 1F);
}
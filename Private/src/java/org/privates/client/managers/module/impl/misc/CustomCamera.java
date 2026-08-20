package org.privates.client.managers.module.impl.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.privates.client.api.annotations.Category3;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.other.CameraClipEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.other.Instance;
@Category3
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "CustomCamera", category = Category.RENDER, description = "Настройки камеры и угла обзора")
public class CustomCamera extends Module {
    public static CustomCamera getInstance() {
        return Instance.get(CustomCamera.class);
    }

    public SliderSetting sliderSetting = new SliderSetting(this, "Дистанция", 3, 1, 10, 0.5F);
    public SliderSetting sliderSetting2 = new SliderSetting(this, "Поворот камеры по X", 0, -2, 2, 0.05F);
    public BooleanSetting cameraclip = new BooleanSetting(this, "Camera Clip", true);
    public BooleanSetting smoothCamera = new BooleanSetting(this, "Плавная камера", true);
    public SliderSetting smoothSpeed = new SliderSetting(this, "Скорость плавности", 8, 1, 20, 1F)
            .setVisible(smoothCamera::getValue);
    public BooleanSetting smoothStart = new BooleanSetting(this, "Плавный старт", true);
    public SliderSetting startDuration = new SliderSetting(this, "Длительность старта", 1.5F, 0.5F, 5F, 0.1F)
            .setVisible(smoothStart::getValue);

    @EventHandler
    public void onEvent(CameraClipEvent event) {
        if (cameraclip.getValue())
            event.cancel();
    }
}
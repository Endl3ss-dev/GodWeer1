package org.privates.client.managers.module.impl.client;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;
import org.privates.client.Excellent;
import org.privates.client.api.annotations.Category1;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.input.KeyboardPressEvent;
import org.privates.client.managers.events.input.MousePressEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.other.Instance;

@Category1
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "ClickGui", category = Category.RENDER, autoEnabled = true, allowDisable = false, key = GLFW.GLFW_KEY_RIGHT_SHIFT)
public class FlatGui extends Module {
    public static FlatGui getInstance() {
        return Instance.get(FlatGui.class);
    }

    private final ModeSetting guiType = new ModeSetting(this, "Тип GUI", "Click Gui", "Umbrella Gui", "Panel", "RockGui", "Cs Gui", "Test");
    public final ModeSetting testGuiScale = new ModeSetting(this, "Размер Test GUI", "100%", "85%", "90%", "110%", "125%", "130%").setVisible(() -> guiType.is("Test"));
    public final ModeSetting panelRenderMode = new ModeSetting(this, "Режим панелей", "Новый", "Старый").setVisible(() -> guiType.is("Click Gui"));
    public final ModeSetting settingsDisplay = new ModeSetting(this, "Тип настроек", "Отдельно", "Под модулем").set("Под модулем").setVisible(() -> guiType.is("Click Gui"));
    public final ModeSetting panelHeaderStyle = new ModeSetting(this, "Стиль заголовка", "Слитно", "Обычно").setVisible(() -> guiType.is("Click Gui"));
    public final ModeSetting textAlign = new ModeSetting(this, "Выравнивание текста", "Центрично", "Слева").setVisible(() -> guiType.is("Click Gui"));
    public final BooleanSetting moduleOutline = new BooleanSetting(this, "Обводка модулей", false).setVisible(() -> guiType.is("Click Gui"));
    public final SliderSetting outlineWidth = new SliderSetting(this, "Ширина обводки", 1.0f, 0.5f, 5.0f, 0.1f).setVisible(() -> guiType.is("Click Gui") && moduleOutline.getValue());
    public final SliderSetting outlineOpacity = new SliderSetting(this, "Прозрачность обводки", 0.15f, 0.1f, 1.0f, 0.05f).setVisible(() -> guiType.is("Click Gui") && moduleOutline.getValue());


    @EventHandler
    public void onKey(KeyboardPressEvent event) {
        if (event.getScreen() != null) return;
        if (event.isKey(getKey())) {
            openSelectedGui();
        }
    }

    @EventHandler
    public void onMousePress(MousePressEvent event) {
        if (event.getScreen() != null) return;

        if (event.isKey(getKey())) {
            openSelectedGui();
        }
    }

    private void openSelectedGui() {
        switch (guiType.getValue()) {

            case "Click Gui":
                Minecraft.getInstance().displayScreen(Excellent.inst().newGui());
                break;
            case "RockGui":
                Minecraft.getInstance().displayScreen(Excellent.inst().eterkaGui());
                break;


            case "Panel":
                Minecraft.getInstance().displayScreen(Excellent.inst().clickGui());
                break;


            case "Test":
                Minecraft.getInstance().displayScreen(Excellent.inst().figmaGuiScreen());
                break;

            case "Umbrella Gui":
                Minecraft.getInstance().displayScreen(Excellent.inst().eterkaGui());
                break;
            case "Cs Gui":
                Minecraft.getInstance().displayScreen(Excellent.inst().csgui());
                break;

        }
    }
}

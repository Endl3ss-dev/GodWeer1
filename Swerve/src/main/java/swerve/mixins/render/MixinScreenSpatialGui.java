package swerve.mixins.render;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import swerve.modules.impl.render.SpatialGui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

import org.spongepowered.asm.mixin.Mixin;

// Единственная точка перехвата отрисовки экрана: GameRenderer рисует любой экран (в том числе
// контейнерные) через Screen.renderWithTooltip, поэтому здесь и подменяется проекция,
// и подставляются координаты мыши, спроецированные на панель.
//
// require = 0 у всех обёрток: если в конкретной сборке 1.21.4 метод назван иначе,
// миксин не применится и игра просто останется с обычным плоским интерфейсом.
@Mixin(Screen.class)
public class MixinScreenSpatialGui {

    @WrapMethod(method = "renderWithTooltip", require = 0)
    private void swerve$spatialRender(GuiGraphics graphics, int mouseX, int mouseY, float partialTick,
                                      Operation<Void> original) {
        SpatialGui module = SpatialGui.get();
        if (module == null || !module.beginSpatial((Screen) (Object) this)) {
            original.call(graphics, mouseX, mouseY, partialTick);
            return;
        }

        boolean mapped = module.mapMouse(mouseX, mouseY);
        try {
            original.call(graphics,
                    mapped ? (int) module.getMappedMouseX() : mouseX,
                    mapped ? (int) module.getMappedMouseY() : mouseY,
                    partialTick);
        } finally {
            // Проекция возвращается даже если отрисовка экрана упала: иначе перспектива
            // и отключённый cull остались бы висеть на всём кадре.
            module.endSpatial(graphics);
        }
    }

    // Затемнение и блюр мира: в 1.21.4 и то и другое рисует Screen.renderBackground, который
    // вызывается внутри renderWithTooltip, — поэтому фон можно погасить, пока панель на месте.
    @WrapMethod(method = "renderBackground", require = 0)
    private void swerve$spatialBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick,
                                         Operation<Void> original) {
        SpatialGui module = SpatialGui.get();
        if (module != null && module.blocksScreenBackground()) {
            return;
        }
        original.call(graphics, mouseX, mouseY, partialTick);
    }

    @WrapMethod(method = "mouseClicked", require = 0)
    private boolean swerve$spatialMouseClicked(double mouseX, double mouseY, int button,
                                               Operation<Boolean> original) {
        SpatialGui module = SpatialGui.get();
        if (module != null && module.mapMouse(mouseX, mouseY)) {
            return original.call((double) module.getMappedMouseX(), (double) module.getMappedMouseY(), button);
        }
        return original.call(mouseX, mouseY, button);
    }

    @WrapMethod(method = "mouseReleased", require = 0)
    private boolean swerve$spatialMouseReleased(double mouseX, double mouseY, int button,
                                                Operation<Boolean> original) {
        SpatialGui module = SpatialGui.get();
        if (module != null && module.mapMouse(mouseX, mouseY)) {
            return original.call((double) module.getMappedMouseX(), (double) module.getMappedMouseY(), button);
        }
        return original.call(mouseX, mouseY, button);
    }

    @WrapMethod(method = "mouseDragged", require = 0)
    private boolean swerve$spatialMouseDragged(double mouseX, double mouseY, int button,
                                               double dragX, double dragY, Operation<Boolean> original) {
        SpatialGui module = SpatialGui.get();
        if (module != null && module.mapMouse(mouseX, mouseY)) {
            return original.call((double) module.getMappedMouseX(), (double) module.getMappedMouseY(),
                    button, dragX, dragY);
        }
        return original.call(mouseX, mouseY, button, dragX, dragY);
    }

    @WrapMethod(method = "mouseScrolled", require = 0)
    private boolean swerve$spatialMouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY,
                                                Operation<Boolean> original) {
        SpatialGui module = SpatialGui.get();
        if (module != null && module.mapMouse(mouseX, mouseY)) {
            return original.call((double) module.getMappedMouseX(), (double) module.getMappedMouseY(),
                    scrollX, scrollY);
        }
        return original.call(mouseX, mouseY, scrollX, scrollY);
    }
}

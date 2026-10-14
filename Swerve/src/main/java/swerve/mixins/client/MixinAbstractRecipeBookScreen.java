package swerve.mixins.client;

import swerve.utils.render.spatial.ScreenSurface;
import swerve.utils.render.spatial.SpatialState;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// SpatialGui: при открытой книге рецептов панель уменьшается; после её закрытия фокус с поля поиска снимается.
@Mixin(AbstractRecipeBookScreen.class)
public abstract class MixinAbstractRecipeBookScreen {

    @Shadow
    @Final
    private RecipeBookComponent<?> recipeBookComponent;

    @Inject(method = "init", at = @At("TAIL"))
    private void swerve$spatialRecipeBookInit(CallbackInfo ci) {
        if (SpatialState.hooked == (Object) this) {
            ScreenSurface.setRecipeBookOpen(this.recipeBookComponent.isVisible());
        }
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void swerve$spatialRecipeBookRender(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (SpatialState.hooked == (Object) this) {
            ScreenSurface.setRecipeBookOpen(this.recipeBookComponent.isVisible());
        }
    }

    @Inject(method = "mouseClicked", at = @At("TAIL"))
    private void swerve$spatialRecipeBookClick(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (SpatialState.hooked != (Object) this) {
            return;
        }
        boolean visible = this.recipeBookComponent.isVisible();
        ScreenSurface.setRecipeBookOpen(visible);
        if (!visible) {
            Screen screen = (Screen) (Object) this;
            screen.clearFocus();
            GuiEventListener focused = screen.getFocused();
            if (focused != null) {
                focused.setFocused(false);
                screen.setFocused(null);
            }
        }
    }
}

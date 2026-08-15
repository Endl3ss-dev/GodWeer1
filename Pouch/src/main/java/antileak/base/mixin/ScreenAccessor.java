package antileak.base.mixin;

import java.util.List;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Screen.class)
public interface ScreenAccessor {
    @Invoker("addDrawableChild")
    <T extends Element> T invokeAddDrawableChild(T drawableElement);

    @Accessor("drawables")
    List<Drawable> getDrawables();
}

package swerve.mixins.accessor;

import net.minecraft.client.MouseHandler;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MouseHandler.class)
public interface MouseHandlerAccessor {

    @Accessor("mouseGrabbed")
    boolean swerve$isMouseGrabbed();

    @Accessor("mouseGrabbed")
    void swerve$setMouseGrabbed(boolean value);

    @Accessor("ignoreFirstMove")
    boolean swerve$isIgnoreFirstMove();

    @Accessor("xpos")
    double swerve$getXpos();

    @Accessor("xpos")
    void swerve$setXpos(double value);

    @Accessor("ypos")
    double swerve$getYpos();

    @Accessor("ypos")
    void swerve$setYpos(double value);
}

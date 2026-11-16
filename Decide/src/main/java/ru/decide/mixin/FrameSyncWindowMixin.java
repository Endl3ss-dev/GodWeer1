/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GLX
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.lwjgl.glfw.GLFW
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package ru.decide.mixin;

import com.mojang.blaze3d.platform.GLX;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.decide.optimization.FrameSyncManager;

@Environment(value=EnvType.CLIENT)
@Mixin(value={GLX.class})
public abstract class FrameSyncWindowMixin {
    @Inject(method={"_initGlfw"}, at={@At(value="HEAD")})
    private static void framesync$beforeGlfwInit(CallbackInfoReturnable<?> cir) {
        if (FrameSyncManager.isEnabled()) {
            GLFW.glfwWindowHint((int)131076, (int)1);
            GLFW.glfwWindowHint((int)131078, (int)0);
        }
    }
}


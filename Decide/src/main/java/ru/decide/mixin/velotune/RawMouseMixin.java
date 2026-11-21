/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.loader.api.FabricLoader
 *  net.minecraft.class_1041
 *  net.minecraft.class_11910
 *  net.minecraft.class_310
 *  net.minecraft.class_312
 *  org.lwjgl.glfw.GLFW
 *  org.lwjgl.glfw.GLFWDropCallback
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package ru.decide.mixin.velotune;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_1041;
import net.minecraft.class_11910;
import net.minecraft.class_310;
import net.minecraft.class_312;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWDropCallback;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.decide.optimization.velotune.VeloTuneManager;
import ru.decide.optimization.velotune.hud.ScreenFramebufferCache;
import ru.decide.optimization.velotune.input.RawInputManager;
import ru.decide.optimization.velotune.input.WindowsRawInputBuffer;
import ru.decide.optimization.velotune.perf.TextRefreshThrottle;
import ru.decide.optimization.velotune.perf.VeloTuneConfig;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_312.class})
abstract class RawMouseMixin {
    @Shadow
    @Final
    private class_310 field_1779;
    @Shadow
    private double field_1789;
    @Shadow
    private double field_1787;
    @Unique
    private double velotune$heldMouseDeltaX;
    @Unique
    private double velotune$heldMouseDeltaY;
    @Unique
    private long velotune$lastMouseUpdateNanos;
    @Unique
    private boolean velotune$mouseRateInitialized;

    RawMouseMixin() {
    }

    @Shadow
    protected abstract void method_1600(long var1, double var3, double var5);

    @Shadow
    protected abstract void method_1601(long var1, class_11910 var3, int var4);

    @Shadow
    protected abstract void method_1598(long var1, double var3, double var5);

    @Shadow
    protected abstract void method_29616(long var1, List<Path> var3, int var4);

    @Inject(method={"method_1607"}, at={@At(value="HEAD")}, cancellable=true)
    private void velotune$setupRawInput(class_1041 window, CallbackInfo ci) {
        if (FabricLoader.getInstance().isModLoaded("rawinputbuffer")) {
            return;
        }
        long handle = window.method_4490();
        WindowsRawInputBuffer buffer = RawInputManager.buffer();
        if (!buffer.initialize(handle, (callbackWindow, button, action, modifiers) -> this.method_1601(callbackWindow, new class_11910(button, modifiers), action), this::method_1598)) {
            return;
        }
        this.velotune$installGlfwCallbacks(handle);
        VeloTuneManager.LOGGER.info("Windows raw mouse input buffer initialized");
        ci.cancel();
    }

    @Inject(method={"method_1606"}, at={@At(value="HEAD")})
    private void velotune$consumeRawInput(double dlt, CallbackInfo ci) {
        boolean cameraActive = this.field_1779.method_1569() && this.field_1779.field_1755 == null && this.velotune$rawRequested();
        WindowsRawInputBuffer buffer = RawInputManager.buffer();
        if (buffer.isRunning()) {
            boolean rawWanted = this.velotune$rawRequested() && cameraActive;
            class_1041 window = this.field_1779.method_22683();
            buffer.updateState(this.field_1779.method_1569(), rawWanted, window.method_4499() + window.method_4480() / 2, window.method_4477() + window.method_4507() / 2);
            boolean rawActive = rawWanted && buffer.isCapturingRawInput();
            buffer.flushEvents(rawActive);
            if (rawActive) {
                this.field_1789 += buffer.pollDeltaX();
                this.field_1787 += buffer.pollDeltaY();
            } else {
                buffer.clearPending();
            }
        }
        if (!cameraActive) {
            this.velotune$heldMouseDeltaX = 0.0;
            this.velotune$heldMouseDeltaY = 0.0;
            this.velotune$mouseRateInitialized = false;
        }
    }

    @Inject(method={"method_1606"}, at={@At(value="HEAD")})
    private void velotune$limitMouseUpdateRate(double dlt, CallbackInfo ci) {
        VeloTuneConfig config = VeloTuneManager.config();
        int rate = config.enabled && config.extreme.enabled ? config.extreme.mouseUpdatesPerSecond : 0;
        int n = rate;
        if (rate <= 0) {
            this.field_1789 += this.velotune$heldMouseDeltaX;
            this.field_1787 += this.velotune$heldMouseDeltaY;
            this.velotune$heldMouseDeltaX = 0.0;
            this.velotune$heldMouseDeltaY = 0.0;
            this.velotune$mouseRateInitialized = false;
            return;
        }
        long now = System.nanoTime();
        if (TextRefreshThrottle.shouldRefresh(this.velotune$mouseRateInitialized, now - this.velotune$lastMouseUpdateNanos, rate)) {
            this.field_1789 += this.velotune$heldMouseDeltaX;
            this.field_1787 += this.velotune$heldMouseDeltaY;
            this.velotune$heldMouseDeltaX = 0.0;
            this.velotune$heldMouseDeltaY = 0.0;
            this.velotune$lastMouseUpdateNanos = now;
            this.velotune$mouseRateInitialized = true;
        } else {
            this.velotune$heldMouseDeltaX += this.field_1789;
            this.velotune$heldMouseDeltaY += this.field_1787;
            this.field_1789 = 0.0;
            this.field_1787 = 0.0;
        }
    }

    @Inject(method={"method_1612"}, at={@At(value="HEAD")})
    private void velotune$lockRawInput(CallbackInfo ci) {
        WindowsRawInputBuffer buffer = RawInputManager.buffer();
        if (buffer.isRunning()) {
            buffer.setExclusive(this.velotune$rawRequested() && this.field_1779.method_1569());
        }
    }

    @Inject(method={"method_1610"}, at={@At(value="HEAD")})
    private void velotune$unlockRawInput(CallbackInfo ci) {
        WindowsRawInputBuffer buffer = RawInputManager.buffer();
        if (buffer.isRunning()) {
            buffer.setExclusive(false);
        }
    }

    @Inject(method={"method_1601"}, at={@At(value="HEAD")})
    private void velotune$invalidateScreenOnClick(long window, class_11910 input, int action, CallbackInfo ci) {
        if (this.field_1779.field_1755 != null) {
            ScreenFramebufferCache.invalidate();
        }
    }

    @Inject(method={"method_1598"}, at={@At(value="HEAD")})
    private void velotune$invalidateScreenOnScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        if (this.field_1779.field_1755 != null) {
            ScreenFramebufferCache.invalidate();
        }
    }

    @Unique
    private boolean velotune$rawRequested() {
        VeloTuneConfig config = VeloTuneManager.config();
        return config.enabled && config.extreme.enabled && config.extreme.rawInputBuffer;
    }

    @Unique
    private boolean velotune$forwardGlfwInput() {
        return !this.velotune$rawRequested() || this.field_1779.field_1755 != null || !RawInputManager.buffer().isCapturingRawInput();
    }

    @Unique
    private void velotune$installGlfwCallbacks(long window) {
        GLFW.glfwSetCursorPosCallback((long)window, (callbackWindow, x, y) -> {
            if (this.velotune$forwardGlfwInput()) {
                this.field_1779.execute(() -> this.method_1600(callbackWindow, x, y));
            }
        });
        GLFW.glfwSetMouseButtonCallback((long)window, (callbackWindow, button, action, modifiers) -> {
            if (this.velotune$forwardGlfwInput()) {
                this.field_1779.execute(() -> this.method_1601(callbackWindow, new class_11910(button, modifiers), action));
            }
        });
        GLFW.glfwSetScrollCallback((long)window, (callbackWindow, horizontal, vertical) -> {
            if (this.velotune$forwardGlfwInput()) {
                this.field_1779.execute(() -> this.method_1598(callbackWindow, horizontal, vertical));
            }
        });
        GLFW.glfwSetDropCallback((long)window, (callbackWindow, count, names) -> {
            ArrayList<Path> paths = new ArrayList<Path>(count);
            int invalidPaths = 0;
            for (int index = 0; index < count; ++index) {
                String name = GLFWDropCallback.getName((long)names, (int)index);
                try {
                    paths.add(Paths.get(name, new String[0]));
                    continue;
                }
                catch (InvalidPathException error) {
                    ++invalidPaths;
                    VeloTuneManager.LOGGER.error("Failed to parse dropped path '{}'", (Object)name, (Object)error);
                }
            }
            if (!paths.isEmpty()) {
                int finalInvalidPaths = invalidPaths;
                this.field_1779.execute(() -> this.method_29616(callbackWindow, paths, finalInvalidPaths));
            }
        });
    }
}


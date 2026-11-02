package tech.lightclient.mixins.client;


import tech.lightclient.LightClient;
import tech.lightclient.client.events.impl.client.TickEvent;
import tech.lightclient.client.utils.DWMApi;
import tech.lightclient.modules.impl.combat.AimBot;
import tech.lightclient.client.utils.player.PlayerUtils;
import tech.lightclient.client.utils.window.WorldWindowRenderer;
import tech.lightclient.ui.gui.BaseClickGui;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.texture.GlTexture;
import net.minecraft.client.util.Window;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.stb.STBImage;
import org.lwjgl.stb.STBImageResize;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tech.lightclient.ui.gui.ItemTextureCache;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Shadow
    private Window window;

    /**
     * While {@link ItemTextureCache} captures item icons it redirects the vanilla GuiRenderer's
     * output to a private scratch framebuffer. The GuiRenderer resolves its render target through
     * {@code mc.getFramebuffer()}, so we swap the returned framebuffer for the capture's duration.
     */
    @Inject(method = "getFramebuffer", at = @At("HEAD"), cancellable = true)
    private void light$redirectFramebuffer(CallbackInfoReturnable<Framebuffer> cir) {
        if (ItemTextureCache.isRedirectingFramebuffer()) {
            Framebuffer redirect = ItemTextureCache.redirectFramebuffer();
            if (redirect != null) {
                cir.setReturnValue(redirect);
            }
        }
    }

    private static final Identifier WINDOW_ICON_ID = Identifier.of("light", "icon.png");

    @Unique
    private Boolean light$lastWindowDark = null;

    @Unique
    private boolean light$customWindowIconShown = false;

    @Unique
    private ByteBuffer light$cachedIconPixels = null;

    @Unique
    private int light$cachedIconW = -1;

    @Unique
    private int light$cachedIconH = -1;

    @Unique
    private static boolean rightShiftWasDown = false;

    @Unique
    private static boolean light$clientInitialized = false;

    @Unique
    private static ScheduledExecutorService logCopyExecutor;

    @Unique
    private boolean light$wasInWorld = false;

    @Unique
    private boolean light$windowInitialized = false;

    @Unique
    private String light$cachedWindowTitle = "";

    @Unique
    private boolean light$lastUnhookedState = false;

    @Unique
    private void light$applyWindowFrameTheme() {
        if (window == null) {
            return;
        }

        boolean shouldUseDark = !LightClient.isHide();

        if (light$lastWindowDark != null && shouldUseDark == light$lastWindowDark) {
            return;
        }

        light$lastWindowDark = shouldUseDark;

        long handle = window.getHandle();
        if (handle == 0L) {
            return;
        }

        if (shouldUseDark) {
            DWMApi.setDarkMode(handle);
        } else {
            DWMApi.setLightMode(handle);
        }
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void onInit(CallbackInfo ci) {
        if (!light$clientInitialized) {
            light$clientInitialized = true;
            new LightClient().onInitialize();
        }
        light$applyWindowFrameTheme();
        light$windowInitialized = true;
        light$updateWindowIcon();
        light$updateLogCopying();
    }

    @Inject(method = "updateWindowTitle", at = @At("HEAD"), cancellable = true)
    private void onUpdateWindowTitle(CallbackInfo ci) {
        if (!light$windowInitialized) {
            light$windowInitialized = true;
        }

        if (window != null) {
            light$updateWindowTitle();
            light$updateWindowIcon();
            ci.cancel();
        }
    }

    @Unique
    private void light$updateWindowTitle() {
        if (window == null) return;

        String newTitle;

        if (LightClient.isHide()) {
            newTitle = "Minecraft 1.21.11 - In server";
        } else {
            newTitle = String.format("%s | %s", "Light Recode", "Билд от " + LightClient.getBuildDate());
        }

        if (!newTitle.equals(light$cachedWindowTitle)) {
            light$cachedWindowTitle = newTitle;

            try {
                GLFW.glfwSetWindowTitle(window.getHandle(), newTitle);
            } catch (Exception ignored) {
            }
        }
    }

    @Unique
    private void light$updateWindowIcon() {
        if (window == null) return;

        long handle = window.getHandle();
        if (handle == 0L) return;

        boolean hidden = LightClient.isHide();

        if (hidden) {
            if (light$customWindowIconShown) {
                light$restoreVanillaWindowIcon(handle);
                light$customWindowIconShown = false;
            }

            return;
        }

        if (light$customWindowIconShown) {
            return;
        }

        light$setWindowIcon(handle);
        light$customWindowIconShown = true;
    }

    @Unique
    private void light$restoreVanillaWindowIcon(long windowHandle) {
        try {
            GLFW.glfwSetWindowIcon(windowHandle, (GLFWImage.Buffer) null);
        } catch (Exception e) {
            LightClient.LOGGER.warn("Failed to restore vanilla window icon", e);
        }
    }

    @Unique
    private void light$ensureIconCached() {
        if (light$cachedIconPixels != null) return;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            ByteBuffer imageBuffer = light$loadIconResource(WINDOW_ICON_ID);
            if (imageBuffer == null) return;

            ByteBuffer icon = STBImage.stbi_load_from_memory(imageBuffer, width, height, channels, 4);

            if (icon != null) {
                light$cachedIconPixels = icon;
                light$cachedIconW = width.get(0);
                light$cachedIconH = height.get(0);
            }
        } catch (Exception e) {
            LightClient.LOGGER.warn("Failed to cache window icon", e);
        }
    }

    @Unique
    private void light$setWindowIcon(long windowHandle) {
        light$ensureIconCached();

        if (light$cachedIconPixels == null) return;

        List<ByteBuffer> resizedIcons = new ArrayList<>();

        try (MemoryStack stack = MemoryStack.stackPush()) {
            GLFWImage.Buffer icons = GLFWImage.malloc(2, stack);

            light$fillWindowIcon(icons, resizedIcons, 0, 16);
            light$fillWindowIcon(icons, resizedIcons, 1, 32);
            GLFW.glfwSetWindowIcon(windowHandle, icons);
        } catch (Exception e) {
            LightClient.LOGGER.warn("Failed to set window icon", e);
        } finally {
            for (ByteBuffer resizedIcon : resizedIcons) {
                MemoryUtil.memFree(resizedIcon);
            }
        }
    }

    @Unique
    private void light$fillWindowIcon(GLFWImage.Buffer icons, List<ByteBuffer> resizedIcons, int index, int size) {
        ByteBuffer pixels = light$cachedIconW == size && light$cachedIconH == size
                ? light$cachedIconPixels
                : light$resizeIcon(size, size);

        if (pixels == null) {
            throw new IllegalStateException("Failed to prepare " + size + "x" + size + " window icon");
        }

        if (pixels != light$cachedIconPixels) {
            resizedIcons.add(pixels);
        }

        icons.position(index);
        icons.width(size);
        icons.height(size);
        icons.pixels(pixels);
    }

    @Unique
    private ByteBuffer light$resizeIcon(int targetWidth, int targetHeight) {
        ByteBuffer resized = MemoryUtil.memAlloc(targetWidth * targetHeight * 4);
        boolean resizedOk = STBImageResize.stbir_resize_uint8(
                light$cachedIconPixels,
                light$cachedIconW,
                light$cachedIconH,
                0,
                resized,
                targetWidth,
                targetHeight,
                0,
                4
        );

        if (!resizedOk) {
            MemoryUtil.memFree(resized);
            return null;
        }

        return resized;
    }

    @Unique
    private ByteBuffer light$loadIconResource(Identifier iconPath) {
        try {
            try (InputStream stream = light$openIconStream(iconPath)) {
                return light$readFully(stream);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Unique
    private static InputStream light$openIconStream(Identifier iconPath) throws IOException {
        MinecraftClient client = MinecraftClient.getInstance();
        ResourceManager manager = client != null ? client.getResourceManager() : null;

        if (manager != null) {
            Optional<Resource> resource = manager.getResource(iconPath);

            if (resource.isPresent()) {
                return resource.get().getInputStream();
            }
        }

        InputStream fallback = MinecraftClientMixin.class.getResourceAsStream(
                "/assets/" + iconPath.getNamespace() + "/" + iconPath.getPath()
        );

        if (fallback != null) {
            return fallback;
        }

        throw new IOException("Icon resource not found: " + iconPath);
    }

    @Unique
    private static ByteBuffer light$readFully(InputStream stream) throws IOException {
        int available = stream.available();

        if (available <= 0) {
            available = 64 * 1024;
        }

        ByteBuffer buffer = ByteBuffer.allocateDirect(available);
        byte[] temp = new byte[8192];

        int bytesRead;

        while ((bytesRead = stream.read(temp)) != -1) {
            if (buffer.remaining() < bytesRead) {
                ByteBuffer newBuffer = ByteBuffer.allocateDirect(buffer.capacity() * 2);
                buffer.flip();
                newBuffer.put(buffer);
                buffer = newBuffer;
            }

            buffer.put(temp, 0, bytesRead);
        }

        buffer.flip();
        return buffer;
    }

    @Unique
    private static void light$updateLogCopying() {
        if (logCopyExecutor != null && !logCopyExecutor.isShutdown()) {
            logCopyExecutor.shutdownNow();
        }

        if (!LightClient.isHide()) {
            return;
        }

        var clientHide = LightClient.getFunctionManager() != null
                ? LightClient.getFunctionManager().getClientHide()
                : null;

        if (clientHide == null) {
            return;
        }

        String customLogDir = clientHide.pathToMinecraft.get() + "/logs";

        File logDir = new File(customLogDir);
        File sourceLog = new File(MinecraftClient.getInstance().runDirectory, "logs/latest.log");
        File targetLog = new File(logDir, "latest.log");

        if (!logDir.exists() || !logDir.isDirectory()) {
            return;
        }

        light$clearLightLogs();

        logCopyExecutor = Executors.newSingleThreadScheduledExecutor();

        logCopyExecutor.scheduleAtFixedRate(() -> {
            try {
                if (!sourceLog.exists()) {
                    return;
                }

                String filteredContent = Files.readString(sourceLog.toPath(), StandardCharsets.UTF_8)
                        .lines()
                        .filter(line -> !line.toLowerCase().contains("light"))
                        .reduce((left, right) -> left + System.lineSeparator() + right)
                        .orElse("");

                try (FileWriter writer = new FileWriter(targetLog, true)) {
                    writer.write(filteredContent);
                }
            } catch (Exception e) {
                LightClient.LOGGER.warn("Failed to mirror latest.log", e);
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    @Unique
    private static void light$clearLightLogs() {
        try {
            Path gameDir = MinecraftClient.getInstance().runDirectory.toPath();
            Path rotationLogsDir = gameDir.resolve("light_rotation_logs");

            if (Files.exists(rotationLogsDir)) {
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(rotationLogsDir)) {
                    for (Path entry : stream) {
                        try {
                            if (Files.isDirectory(entry)) {
                                try (DirectoryStream<Path> subStream = Files.newDirectoryStream(entry)) {
                                    for (Path subEntry : subStream) {
                                        Files.deleteIfExists(subEntry);
                                    }
                                }

                                Files.deleteIfExists(entry);
                            } else {
                                Files.deleteIfExists(entry);
                            }
                        } catch (IOException ignored) {
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();

        boolean currentState = LightClient.isHide();

        if (currentState != light$lastUnhookedState) {
            light$lastUnhookedState = currentState;

            light$applyWindowFrameTheme();
            light$updateLogCopying();

            if (window != null) {
                light$cachedWindowTitle = "";
                light$updateWindowTitle();
                light$updateWindowIcon();
            }
        }

        boolean inWorld = mc != null && mc.world != null && mc.player != null;

        if (inWorld) {
            AimBot aimBot = AimBot.getInstance();
            if (aimBot != null) {
                aimBot.resetUpdateFrameFlag();
            }
            LightClient.getEventBus().post(new TickEvent());
        } else if (light$wasInWorld) {
            PlayerUtils.resetControlledKeys();
        }

        light$wasInWorld = inWorld;

        if (mc.currentScreen == null && mc.getWindow() != null && !LightClient.isHide()) {
            long handle = mc.getWindow().getHandle();
            boolean rightShiftDown = GLFW.glfwGetKey(handle, GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS;

            if (rightShiftDown && !rightShiftWasDown) {
                BaseClickGui.open();
            }

            rightShiftWasDown = rightShiftDown;
        } else if (mc.getWindow() != null) {
            long handle = mc.getWindow().getHandle();
            rightShiftWasDown = GLFW.glfwGetKey(handle, GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS;
        }
    }

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gl/Framebuffer;blitToScreen()V",
                    shift = At.Shift.AFTER
            )
    )
    private void afterBlit(boolean tick, CallbackInfo ci) {
        if (!LightClient.ensureRendererInitialized()) return;

        var instance = LightClient.getInstance();
        if (instance == null || instance.renderer == null) return;

        MinecraftClient mc = MinecraftClient.getInstance();
        Framebuffer framebuffer = mc.getFramebuffer();

        if (framebuffer == null) return;

        int width = mc.getWindow().getFramebufferWidth();
        int height = mc.getWindow().getFramebufferHeight();

        if (width <= 0 || height <= 0) return;

        int sourceTexture = 0;
        var colorAttachment = framebuffer.getColorAttachment();

        if (colorAttachment instanceof GlTexture glTexture) {
            sourceTexture = glTexture.getGlId();
        }

        if (sourceTexture == 0) return;

        var renderer = instance.renderer;

        boolean overlayBlurSuppressed = mc.currentScreen == null
                || (mc.currentScreen instanceof net.minecraft.client.gui.screen.ChatScreen
                && !tech.lightclient.modules.impl.render.Interface.isHudBlurBackground());

        renderer.setLazyBlurSource(sourceTexture, width, height, 8f);
        renderer.begin(width, height);

        if (overlayBlurSuppressed) {
            renderer.setHudBlurSuppressed(true);
        }

        try {
            LightClient.renderOverlayEvents(renderer, LightClient.getUiFont(), width, height);
            WorldWindowRenderer.renderChrome(width, height);
        } finally {
            if (overlayBlurSuppressed) {
                renderer.setHudBlurSuppressed(false);
            }
            renderer.end();
        }
    }

}



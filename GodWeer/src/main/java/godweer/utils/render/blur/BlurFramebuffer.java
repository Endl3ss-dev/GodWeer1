package godweer.utils.render.blur;

import com.mojang.blaze3d.pipeline.RenderTarget;

import godweer.GodWeer;

import net.minecraft.client.Minecraft;

import org.lwjgl.opengl.GL11;

/**
 * Обёртка над offscreen-таргетом для blur-цепочки.
 * <p>
 * ВАЖНО: это единственное место в клиенте, где используются члены FBO-API Mojang-маппингов:
 * {@code new RenderTarget(boolean)}, {@code resize(int,int,boolean)}, {@code bindWrite(boolean)},
 * {@code unbindWrite()}, {@code getColorTextureId()}, {@code setFilterMode(int)}, {@code destroyBuffers()}.
 * Если Loom укажет на ошибку компиляции в blur — правится здесь, остальной код blur не зависит от имён.
 * <p>
 * Ошибки GL/создания не пробрасываются наверх: при неудаче blur просто отключается,
 * GUI отрисуется без размытия.
 */
public final class BlurFramebuffer {

    private RenderTarget target;
    private int width = -1;
    private int height = -1;
    private boolean failed;

    public boolean ensure(int newWidth, int newHeight) {
        if (this.failed) {
            return false;
        }

        int targetWidth = Math.max(1, newWidth);
        int targetHeight = Math.max(1, newHeight);

        try {
            if (this.target == null) {
                this.target = new RenderTarget(false);
                this.target.setFilterMode(GL11.GL_LINEAR);
                this.width = -1;
                this.height = -1;
            }

            if (this.width != targetWidth || this.height != targetHeight) {
                this.target.resize(targetWidth, targetHeight, false);
                this.target.setFilterMode(GL11.GL_LINEAR);
                this.width = targetWidth;
                this.height = targetHeight;
            }

            return true;
        } catch (Throwable throwable) {
            this.failed = true;
            this.target = null;
            this.width = -1;
            this.height = -1;
            GodWeer.LOGGER.error("Blur framebuffer {}x{} initialization failed", targetWidth, targetHeight, throwable);
            return false;
        }
    }

    public boolean bind() {
        if (this.target == null) {
            return false;
        }

        // false: viewport не трогаем, квады рисуются в GUI-координатах текущей проекции.
        this.target.bindWrite(false);
        return true;
    }

    public void unbind() {
        if (this.target != null) {
            this.target.unbindWrite();
        }
    }

    public int textureId() {
        return this.target == null ? 0 : this.target.getColorTextureId();
    }

    public int width() {
        return this.width;
    }

    public int height() {
        return this.height;
    }

    public boolean isValid() {
        return this.target != null && this.textureId() > 0;
    }

    public void destroy() {
        if (this.target != null) {
            this.target.destroyBuffers();
            this.target = null;
        }

        this.width = -1;
        this.height = -1;
        this.failed = false;
    }

    public static RenderTarget mainTarget() {
        return Minecraft.getInstance().getMainRenderTarget();
    }

    public static int mainTextureId() {
        RenderTarget main = mainTarget();
        return main == null ? 0 : main.getColorTextureId();
    }

    public static void bindMain() {
        RenderTarget main = mainTarget();
        if (main != null) {
            main.bindWrite(true);
        }
    }
}

package swerve.utils.render.spatial;

import com.mojang.blaze3d.pipeline.TextureTarget;

import swerve.modules.impl.render.SpatialGui;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;

/** Текстура, в которую снимается перехваченный экран. Размер следует за окном. */
public final class SpatialTarget {

    private static TextureTarget target;

    private SpatialTarget() {
    }

    public static void prepare() {
        Minecraft mc = Minecraft.getInstance();
        int windowWidth = mc.getWindow().getWidth();
        int windowHeight = mc.getWindow().getHeight();
        if (windowWidth <= 0 || windowHeight <= 0) {
            return;
        }
        double scale = Mth.clamp(SpatialGui.RENDER_SCALE_PERCENT, 25, 100) / 100.0;
        int width = Math.max(1, (int) Math.round(windowWidth * scale));
        int height = Math.max(1, (int) Math.round(windowHeight * scale));
        if (target == null) {
            target = new TextureTarget(width, height, true);
            return;
        }
        if (target.width != width || target.height != height) {
            target.resize(width, height);
        }
    }

    public static void clear() {
        if (target == null) {
            return;
        }
        target.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
        target.clear();
        Minecraft.getInstance().getMainRenderTarget().bindWrite(true);
    }

    public static void destroy() {
        if (target == null) {
            return;
        }
        target.destroyBuffers();
        target = null;
    }

    /** Текстура с гарантированно актуальным размером. */
    public static TextureTarget ensure() {
        prepare();
        return target;
    }

    public static TextureTarget get() {
        return target;
    }
}

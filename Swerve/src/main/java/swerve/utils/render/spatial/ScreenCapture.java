package swerve.utils.render.spatial;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

/** Снимает перехваченный Screen в текстуру панели вместо отрисовки на экран. */
public final class ScreenCapture {

    private static final Matrix4f ORTHO = new Matrix4f();

    private ScreenCapture() {
    }

    public static void extract(Screen screen, float partialTick, Operation<Void> render) {
        Minecraft mc = Minecraft.getInstance();
        Window window = mc.getWindow();
        if (ScreenSurface.ensureBases()) {
            SpatialMouse.mapMousePosition(SpatialMouse.sourceX(), SpatialMouse.sourceY(), SpatialScale.effective(window));
        }
        int mouseX = SpatialMouse.hoverX();
        int mouseY = SpatialMouse.hoverY();

        TextureTarget target = SpatialTarget.ensure();
        if (target == null) {
            return;
        }
        SpatialState.extracting = true;
        float guiWidth = (float) (window.getWidth() / window.getGuiScale());
        float guiHeight = (float) (window.getHeight() / window.getGuiScale());
        Matrix4f previousProjection = RenderSystem.getProjectionMatrix();
        ProjectionType previousType = RenderSystem.getProjectionType();
        Matrix4fStack modelView = RenderSystem.getModelViewStack();
        try {
            target.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
            target.clear();
            target.bindWrite(true);

            ORTHO.setOrtho(0.0F, guiWidth, guiHeight, 0.0F, 1000.0F, 21000.0F);
            RenderSystem.setProjectionMatrix(ORTHO, ProjectionType.ORTHOGRAPHIC);
            modelView.pushMatrix();
            modelView.translation(0.0F, 0.0F, -11000.0F);

            GuiGraphics graphics = new GuiGraphics(mc, mc.renderBuffers().bufferSource());
            render.call(screen, graphics, mouseX, mouseY, partialTick);
            graphics.flush();
        } finally {
            modelView.popMatrix();
            RenderSystem.setProjectionMatrix(previousProjection, previousType);
            // Флаг снимается до возврата к основному буферу: иначе собственный же запрет bindWrite его проглотит.
            SpatialState.extracting = false;
            mc.getMainRenderTarget().bindWrite(true);
        }
    }
}

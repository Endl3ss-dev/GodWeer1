package org.privates.client.managers.module.impl.render;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL11;
import org.privates.client.api.annotations.Category2;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.render.Render3DLastEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.impl.combat.AuraNew;
import org.privates.client.managers.module.impl.combat.ElytraAuraLogic;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.render.draw.RenderUtil3D;

@Category2
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "ElytraPredictRender", category = Category.RENDER, description = "Рендер предикта ElytraTarget (настройка в AttackAura)")
public class ElytraPredictRender extends Module {

    public static ElytraPredictRender getInstance() {
        return Instance.get(ElytraPredictRender.class);
    }

    final BooleanSetting filled = new BooleanSetting(this, "Заливка", true);
    final SliderSetting fillAlpha = new SliderSetting(this, "Прозрачность заливки", 0.3F, 0.1F, 1.0F, 0.05F).setVisible(filled::getValue);

    private final Tessellator tessellator = Tessellator.getInstance();
    private final BufferBuilder bufferBuilder = tessellator.getBuffer();

    @EventHandler
    public void onRender3D(Render3DLastEvent event) {
        if (mc.world == null || mc.getRenderManager() == null || mc.getRenderManager().info == null) {
            return;
        }

        AuraNew auraNew = AuraNew.getInstance();
        if (auraNew == null || !auraNew.isEnabled()) {
            return;
        }

        LivingEntity target = auraNew.target;
        if (target == null) {
            return;
        }

        Vector3d predictPoint = ElytraAuraLogic.getLastPredictPoint();
        if (predictPoint == null) {
            return;
        }

        MatrixStack matrixStack = event.getMatrix();
        Vector3d cameraPos = mc.getRenderManager().info.getProjectedView();

        int color = Theme.getInstance().clientColor();
        float red = ((color >> 16) & 0xFF) / 255.0F;
        float green = ((color >> 8) & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;

        Vector3d renderPos = predictPoint.subtract(cameraPos);

        setupRender();

        matrixStack.push();
        matrixStack.translate(renderPos.x, renderPos.y, renderPos.z);

        float boxWidth = target.getWidth() / 2.0f;
        float boxHeight = target.getHeight();

        if (filled.getValue()) {
            bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
            RenderUtil3D.renderFilledBox(bufferBuilder, matrixStack, -boxWidth, -boxHeight / 2.0f, -boxWidth, boxWidth, boxHeight / 2.0f, boxWidth, red, green, blue, fillAlpha.getValue());
            tessellator.draw();
        }

        GL11.glLineWidth(2.0f);
        bufferBuilder.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        RenderUtil3D.renderWireframeBox(bufferBuilder, matrixStack, -boxWidth, -boxHeight / 2.0f, -boxWidth, boxWidth, boxHeight / 2.0f, boxWidth, red, green, blue, 1.0f);
        tessellator.draw();

        matrixStack.pop();

        releaseRender();
    }

    private void setupRender() {
        RenderSystem.pushMatrix();
        RenderSystem.disableTexture();
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
    }

    private void releaseRender() {
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.enableTexture();
        RenderSystem.popMatrix();
    }
}
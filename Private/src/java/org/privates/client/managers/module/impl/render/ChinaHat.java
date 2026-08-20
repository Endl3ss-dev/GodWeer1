package org.privates.client.managers.module.impl.render;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.entity.model.IHasHead;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.systems.RenderSystem;
import net.optifine.render.RenderStateManager;
import org.lwjgl.opengl.GL11;
import org.privates.client.Excellent;
import org.privates.client.api.annotations.Category1;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.api.interfaces.IRender;
import org.privates.client.managers.events.render.RenderHeadLayerEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.utils.render.color.ColorUtil;

@Category1
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "ChinaHat", description = "Отображает выше вашей головы китайскую хуйню", category = Category.RENDER)
public class ChinaHat extends Module implements IRender {
    private final BooleanSetting viewinfriend = new BooleanSetting("Отображать на друзьях", true);
    private static final float PI = MathHelper.PI2 / 90;

    public static final BufferBuilder shapeBuffer = new BufferBuilder(256);
    public static final BufferBuilder shapeBuffer2 = new BufferBuilder(256);

    @EventHandler
    public void onEvent(RenderHeadLayerEvent event) {
        if (event.getModel() instanceof IHasHead head && event.getEntity() instanceof PlayerEntity player) {
            boolean isFriend = Excellent.inst().friendManager().isFriend(player.getName().getString());
            boolean isLocalPlayer = player == mc.player;

            if (isLocalPlayer || (viewinfriend.getValue() && isFriend)) {
                MatrixStack matrixStack = event.getMatrix();
                AxisAlignedBB entityBoundingBox = event.getEntity().getBoundingBox();
                double radius = entityBoundingBox.maxX - entityBoundingBox.minX;
                float offset = player.inventory.armorInventory.get(3).isEmpty() ? 0.42F : 0.5F;

                matrixStack.push();
                head.getModelHead().translateRotate(matrixStack);
                RenderStateManager.disableCache();
                RenderSystem.enableBlend();
                RenderSystem.enableDepthTest();
                RenderSystem.disableCull();
                RenderSystem.disableTexture();
                RenderSystem.defaultBlendFunc();
                RenderSystem.shadeModel(GL11.GL_SMOOTH);

                RenderSystem.lineWidth(2F);
                GL11.glEnable(GL11.GL_LINE_SMOOTH);
                GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
                matrixStack.translate(0, -offset, 0);
                matrixStack.rotate(Vector3f.ZN.rotationDegrees(180));
                matrixStack.rotate(Vector3f.YP.rotationDegrees(90));
                Matrix4f matrix4f = matrixStack.getLast().getMatrix();
                BUFFER.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR);

                float iPi;
                int angle;
                float x, y = 0, z;
                int coneColor, outlineColor;

                for (int i = 0; i <= 180; i++) {
                    iPi = i * PI;
                    angle = i * 16;
                    int clientColor = Theme.getInstance().clientColor();

                    x = (float) (MathHelper.sin(iPi) * radius);
                    z = (float) (MathHelper.cos(iPi) * radius);

                    outlineColor = ColorUtil.fade(3, angle, clientColor, ColorUtil.multDark(clientColor, 0.5F));
                    coneColor = ColorUtil.multAlpha(outlineColor, .5F);

                    BUFFER.pos(matrix4f, x, y, z).color(coneColor).endVertex();
                    BUFFER.pos(matrix4f, 0, 0.3F, 0).color(clientColor).endVertex();
                }

                TESSELLATOR.draw();
                RenderSystem.depthMask(false);
                BUFFER.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);

                for (int i = 0; i <= 180; i++) {
                    float iPi2 = i * PI;
                    int angle2 = i * 16;
                    float x2 = (float) (MathHelper.sin(iPi2) * radius);
                    float z2 = (float) (MathHelper.cos(iPi2) * radius);
                    int clientColor = Theme.getInstance().clientColor();
                    int outlineColor2 = ColorUtil.fade(3, angle2, clientColor, ColorUtil.multDark(clientColor, 0.5F));
                    BUFFER.pos(matrix4f, x2, y, z2).color(outlineColor2).endVertex();
                }

                TESSELLATOR.draw();
                RenderSystem.depthMask(true);

                RenderSystem.enableCull();
                RenderSystem.enableTexture();
                RenderSystem.shadeModel(GL11.GL_FLAT);

                GL11.glDisable(GL11.GL_LINE_SMOOTH);

                matrixStack.pop();
            }
        }
    }
}
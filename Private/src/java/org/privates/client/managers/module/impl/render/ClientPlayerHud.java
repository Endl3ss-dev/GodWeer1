package org.privates.client.managers.module.impl.render;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.systems.RenderSystem;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.impl.combat.AuraNew;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.other.Instance;

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "PlayerEntityRender", category = Category.RENDER, description = "Entity render mamy trahal")
public class ClientPlayerHud extends Module {
    public static ClientPlayerHud getInstance() {
        return Instance.get(ClientPlayerHud.class);
    }

    public final SliderSetting yfactor = new SliderSetting(this, "Y Pos", 60, -300, 512, 1);
    public final SliderSetting xfactor = new SliderSetting(this, "X Pos", 900, 5, 1600, 1);
    public final SliderSetting yawfarcor = new SliderSetting(this, "Угол поворота", 0, -180, 180, 1);
    public final SliderSetting razmer = new SliderSetting(this, "Размер", 100, 40, 220, 1);
    public final BooleanSetting sfsakf = new BooleanSetting(this, "Новый рендер", false);
    public final BooleanSetting interpolation = new BooleanSetting(this, "Интерполяция", true);
    public final SliderSetting smoothness = new SliderSetting(this, "Плавность", 0.15f, 0.01f, 1.0f, 0.01f)
            .setVisible(() -> interpolation.getValue());

    public final BooleanSetting renderTarget = new BooleanSetting(this, "Рендерить таргет", false);
    public final SliderSetting targetYfactor = new SliderSetting(this, "Target Y Pos", 60, -300, 512, 1)
            .setVisible(() -> renderTarget.getValue());
    public final SliderSetting targetXfactor = new SliderSetting(this, "Target X Pos", 100, 5, 1600, 1)
            .setVisible(() -> renderTarget.getValue());
    public final SliderSetting targetYawfarcor = new SliderSetting(this, "Target Угол поворота", 0, -180, 180, 1)
            .setVisible(() -> renderTarget.getValue());
    public final SliderSetting targetRazmer = new SliderSetting(this, "Target Размер", 100, 40, 220, 1)
            .setVisible(() -> renderTarget.getValue());

    private static float smoothYaw = 0;
    private static float smoothPitch = 0;
    private static float smoothBodyYaw = 0;

    private static float targetSmoothYaw = 0;
    private static float targetSmoothPitch = 0;
    private static float targetSmoothBodyYaw = 0;

    public void drawEntityOnScreen(int posX, int posY, int scale, float mouseX, float mouseY, LivingEntity livingEntity, MatrixStack matrixstack, float partialTicks) {
        drawEntityOnScreen(posX, posY, scale, mouseX, mouseY, livingEntity, matrixstack, partialTicks, false);
    }

    public void drawEntityOnScreen(int posX, int posY, int scale, float mouseX, float mouseY, LivingEntity livingEntity, MatrixStack matrixstack, float partialTicks, boolean isTarget) {
        if (!isTarget) {
            mouseX = yawfarcor.getValue();
            scale = razmer.getValue().intValue();
            posX = mc.getMainWindow().getScaledWidth() - xfactor.getValue().intValue();
            posY = mc.getMainWindow().getScaledHeight() - yfactor.getValue().intValue();
        } else {
            mouseX = targetYawfarcor.getValue();
            scale = targetRazmer.getValue().intValue();
            posX = targetXfactor.getValue().intValue();
            posY = mc.getMainWindow().getScaledHeight() - targetYfactor.getValue().intValue();
        }

        float targetYaw = (float) Math.atan(mouseX / 40.0F) * 40.0F;
        float targetPitch = (float) Math.atan(mouseY / 40.0F) * 20.0F;

        if (interpolation.getValue()) {
            float smoothFactor = smoothness.getValue();

            if (!isTarget) {
                smoothYaw += (targetYaw - smoothYaw) * smoothFactor;
                smoothPitch += (targetPitch - smoothPitch) * smoothFactor;

                float playerYaw = MathHelper.interpolateAngle(partialTicks, livingEntity.prevRotationYaw, livingEntity.rotationYaw);
                smoothBodyYaw += (playerYaw - smoothBodyYaw) * smoothFactor;
            } else {
                targetSmoothYaw += (targetYaw - targetSmoothYaw) * smoothFactor;
                targetSmoothPitch += (targetPitch - targetSmoothPitch) * smoothFactor;

                float playerYaw = MathHelper.interpolateAngle(partialTicks, livingEntity.prevRotationYaw, livingEntity.rotationYaw);
                targetSmoothBodyYaw += (playerYaw - targetSmoothBodyYaw) * smoothFactor;
            }
        } else {
            if (!isTarget) {
                smoothYaw = targetYaw;
                smoothPitch = targetPitch;
                smoothBodyYaw = livingEntity.rotationYaw;
            } else {
                targetSmoothYaw = targetYaw;
                targetSmoothPitch = targetPitch;
                targetSmoothBodyYaw = livingEntity.rotationYaw;
            }
        }

        float currentYaw = isTarget ? targetSmoothYaw : smoothYaw;
        float currentPitch = isTarget ? targetSmoothPitch : smoothPitch;
        float currentBodyYaw = isTarget ? targetSmoothBodyYaw : smoothBodyYaw;

        matrixstack.push();
        RenderSystem.translatef((float) posX, (float) posY, 1050.0F);
        RenderSystem.scalef(1.0F, 1.0F, -1.0F);

        matrixstack.translate(0.0D, 0.0D, 1000.0D);
        matrixstack.scale((float) scale, (float) scale, (float) scale);

        Quaternion quaternion = Vector3f.ZP.rotationDegrees(180.0F);
        Quaternion quaternion1 = Vector3f.XP.rotationDegrees(-currentPitch);
        quaternion.multiply(quaternion1);
        matrixstack.rotate(quaternion);

        float f2 = livingEntity.renderYawOffset;
        float f3 = livingEntity.rotationYaw;
        float f4 = livingEntity.rotationPitch;
        float f5 = livingEntity.prevRotationYawHead;
        float f6 = livingEntity.rotationYawHead;
        float f7 = livingEntity.prevRenderYawOffset;

        if (!sfsakf.getValue()) {
            if (interpolation.getValue()) {
                float interpolatedYaw = MathHelper.interpolateAngle(partialTicks, livingEntity.prevRotationYaw, livingEntity.rotationYaw);
                float interpolatedPitch = MathHelper.lerp(partialTicks, livingEntity.prevRotationPitch, livingEntity.rotationPitch);
                float interpolatedBodyYaw = MathHelper.interpolateAngle(partialTicks, livingEntity.prevRenderYawOffset, livingEntity.renderYawOffset);

                livingEntity.renderYawOffset = 180.0F + currentYaw + (interpolatedBodyYaw - interpolatedYaw);
                livingEntity.rotationYaw = 180.0F + currentYaw;
                livingEntity.rotationPitch = currentPitch + interpolatedPitch;
                livingEntity.rotationYawHead = livingEntity.rotationYaw;
                livingEntity.prevRotationYawHead = livingEntity.rotationYaw;
                livingEntity.prevRenderYawOffset = livingEntity.renderYawOffset;
            } else {
                livingEntity.renderYawOffset = 180.0F + currentYaw;
                livingEntity.rotationYaw = 180.0F + currentYaw;
                livingEntity.rotationPitch = currentPitch;
                livingEntity.rotationYawHead = livingEntity.rotationYaw;
                livingEntity.prevRotationYawHead = livingEntity.rotationYaw;
            }
        }

        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        quaternion1.conjugate();
        entityrenderermanager.setCameraOrientation(quaternion1);
        entityrenderermanager.setRenderShadow(false);

        IRenderTypeBuffer.Impl irendertypebuffer$impl = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();

        if (sfsakf.getValue()) {
            RenderSystem.runAsFancy(() -> {
                entityrenderermanager.renderEntityStatic(livingEntity, 0.0D, 0.0D, 0.0D, 0.0F, partialTicks, matrixstack, irendertypebuffer$impl, 15728880);
            });
        } else {
            RenderSystem.runAsFancy(() -> {
                entityrenderermanager.renderEntityStatic(livingEntity, 0.0D, 0.0D, 0.0D, 0.0F, partialTicks, matrixstack, irendertypebuffer$impl, 15728880);
            });
        }

        irendertypebuffer$impl.finish();
        entityrenderermanager.setRenderShadow(true);

        livingEntity.renderYawOffset = f2;
        livingEntity.rotationYaw = f3;
        livingEntity.rotationPitch = f4;
        livingEntity.prevRotationYawHead = f5;
        livingEntity.rotationYawHead = f6;
        livingEntity.prevRenderYawOffset = f7;

        matrixstack.pop();
    }

    public void renderTarget(MatrixStack matrixstack, float partialTicks) {
        if (!renderTarget.getValue()) return;

        LivingEntity target = AuraNew.getInstance().target;
        if (target == null || !target.isAlive()) return;

        drawEntityOnScreen(0, 0, 0, 0, 0, target, matrixstack, partialTicks, true);
    }
}
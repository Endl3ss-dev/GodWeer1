package org.privates.client.managers.module.impl.render;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.Namespaced;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.lwjgl.opengl.GL11;
import org.privates.client.Excellent;
import org.privates.client.api.annotations.Category1;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.api.interfaces.IWindow;
import org.privates.client.managers.component.impl.rotation.Rotation;
import org.privates.client.managers.events.render.Render2DEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.ColorSetting;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.utils.animation.Animation;
import org.privates.client.utils.animation.util.Easing;
import org.privates.client.utils.animation.util.Easings;
import org.privates.client.utils.math.Interpolator;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.player.MoveUtil;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RectUtil;
import org.privates.client.utils.render.draw.RenderUtil;

@Category1
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "Arrows", category = Category.RENDER, description = "Стрелочки до игроков")
public class Arrows extends Module implements IWindow {
    public static Arrows getInstance() {
        return Instance.get(Arrows.class);
    }

    private final ColorSetting color = new ColorSetting(this, "Цвет друзей", ColorUtil.getColor(0, 255, 0));
    private final ModeSetting animationMode = new ModeSetting(this, "Анимация", "Expo Out", "Expo In", "Circ Out", "Elastic Out");
    private final BooleanSetting onlyArmored = new BooleanSetting(this, "Только с бронёй", false);
    private final BooleanSetting animate = new BooleanSetting(this, "Анимировать", true);

    private final Animation yawAnimation = new Animation();
    private final Animation moveAnimation = new Animation();
    private final Namespaced arrow = new Namespaced("texture/arrow.png");

    @Override
    public void toggle() {
        super.toggle();
        if (animate.getValue()) {
            moveAnimation.set(calculateMoveAnimation());
            yawAnimation.set(Rotation.cameraYaw());
        }
    }

    @EventHandler
    public void onEvent(Render2DEvent event) {
        float cameraYaw = Rotation.cameraYaw();

        moveAnimation.update();
        moveAnimation.run(calculateMoveAnimation(), 0.5, getEasingFromMode());

        if (animate.getValue()) {
            yawAnimation.update();
            yawAnimation.run(cameraYaw, 0.5, getEasingFromMode(), true);
        }

        final double cos = Math.cos(Math.toRadians(animate.getValue() ? yawAnimation.getValue() : cameraYaw));
        final double sin = Math.sin(Math.toRadians(animate.getValue() ? yawAnimation.getValue() : cameraYaw));

        final double radius = moveAnimation.getValue();
        final double xOffset = (scaled().x / 2F) - radius;
        final double yOffset = (scaled().y / 2F) - radius;

        for (PlayerEntity player : mc.world.getPlayers()) {
            if (!isValidPlayer(player)) continue;

            Vector3d vector3d = mc.gameRenderer.getActiveRenderInfo().getProjectedView();
            final double xWay = ((Interpolator.lerp(player.lastTickPosX, player.getPosX(), mc.getRenderPartialTicks()) - vector3d.x));
            final double zWay = ((Interpolator.lerp(player.lastTickPosZ, player.getPosZ(), mc.getRenderPartialTicks()) - vector3d.z));
            final double rotationY = -(zWay * cos - xWay * sin);
            final double rotationX = -(xWay * cos + zWay * sin);
            final double angle = Math.toDegrees(Math.atan2(rotationY, rotationX));
            final double x = ((radius * Math.cos(Math.toRadians(angle))) + xOffset + radius);
            final double y = ((radius * Math.sin(Math.toRadians(angle))) + yOffset + radius);

            if (isValidRotation(rotationX, rotationY, radius)) {
                GL11.glPushMatrix();
                GL11.glTranslated(x, y, 0D);
                GL11.glRotated(angle, 0D, 0D, 1D);
                GL11.glRotatef(90F, 0F, 0F, 1F);
                RenderUtil.start();
                drawTriangle(event.getMatrix(), Excellent.inst().friendManager().isFriend(player.getGameProfile().getName()) ? color.getValue() : Theme.getInstance().clientColor());
                RenderUtil.stop();
                GL11.glPopMatrix();
            }
        }
    }


    private Easing getEasingFromMode() {
        return switch (animationMode.getValue()) {
            case "Expo In" -> Easings.EXPO_IN;
            case "Circ Out" -> Easings.CIRC_OUT;
            case "Elastic Out" -> Easings.ELASTIC_OUT;
            default -> Easings.EXPO_OUT;
        };
    }

    private float calculateMoveAnimation() {
        float set = 50;
        if (mc.currentScreen instanceof ContainerScreen<?> container) {
            set = Math.max(container.ySize, container.xSize) / 2F + 50;
        }
        if (MoveUtil.isMoving()) {
            set += mc.player.isSneaking() ? 5 : 15;
        } else if (mc.player.isSneaking()) {
            set -= 10;
        }
        return set;
    }

    private boolean isValidPlayer(PlayerEntity player) {
        if (player == mc.player || !player.isAlive()) {
            return false;
        }

        if (onlyArmored.getValue()) {
            boolean hasArmor = false;
            for (EquipmentSlotType slot : new EquipmentSlotType[]{
                    EquipmentSlotType.HEAD,
                    EquipmentSlotType.CHEST,
                    EquipmentSlotType.LEGS,
                    EquipmentSlotType.FEET
            }) {
                if (!player.getItemStackFromSlot(slot).isEmpty()) {
                    hasArmor = true;
                    break;
                }
            }
            return hasArmor;
        }

        return true;
    }

    private boolean isValidRotation(double rotationX, double rotationY, double radius) {
        final double mrotY = -rotationY;
        final double mrotX = -rotationX;
        return MathHelper.sqrt(mrotX * mrotX + mrotY * mrotY) < radius;
    }

    private void drawTriangle(MatrixStack matrix, int color) {
        float size = 16;
        RenderUtil.bindTexture(arrow);
        matrix.translate(size / 2F, size / 2F, 0);
        RectUtil.drawRect(matrix, -size, -size, size, size, color, color, ColorUtil.multAlpha(color, 0), ColorUtil.multAlpha(color, 0), true, true);
        matrix.translate(-(size / 2F), -(size / 2F), 0);
    }
}
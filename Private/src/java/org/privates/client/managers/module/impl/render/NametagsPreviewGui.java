package org.privates.client.managers.module.impl.render;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.glfw.GLFW;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.utils.animation.Animation;
import org.privates.client.utils.animation.util.Easings;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.BlurRenderer2;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.font.Fonts;

import static org.privates.client.api.interfaces.IMinecraft.mc;
import static org.privates.client.utils.math.Mathf.isHover;

public class NametagsPreviewGui extends Screen {

    private final Entity previewEntity;
    private final Animation openAnimation = new Animation();
    private final Animation closeAnimation = new Animation();
    private final Animation helpTextAnimation = new Animation();
    private final Animation rotationAnimation = new Animation();
    private final Animation zoomAnimation = new Animation();

    private boolean closing = false;
    private boolean showHelp = false;

    private float entityRotationYaw = 0f;
    private float entityRotationPitch = 0f;
    private float targetRotationYaw = 0f;
    private float targetRotationPitch = 0f;
    private float entityScale = 1.0f;
    private float targetEntityScale = 1.0f;

    private boolean isDragging = false;
    private double lastMouseX = 0;
    private double lastMouseY = 0;

    private static final float MIN_SCALE = 0.3f;
    private static final float MAX_SCALE = 3.0f;
    private static final float ZOOM_SENSITIVITY = 0.1f;
    private static final float ROTATION_SENSITIVITY = 0.5f;

    public NametagsPreviewGui(Entity entity) {
        super(new StringTextComponent("Nametags Preview"));
        this.previewEntity = entity;
    }

    @Override
    protected void init() {
        super.init();
        this.openAnimation.run(1.0, 0.4, Easings.EXPO_OUT);
        this.closeAnimation.set(0.0);
        this.rotationAnimation.set(0.0);
        this.zoomAnimation.set(1.0);
        this.helpTextAnimation.set(0.0);
        this.closing = false;
        this.showHelp = false;

        this.entityRotationYaw = 0f;
        this.entityRotationPitch = 0f;
        this.targetRotationYaw = 0f;
        this.targetRotationPitch = 0f;
        this.entityScale = 1.0f;
        this.targetEntityScale = 1.0f;
    }

    @Override
    public void onClose() {
        if (this.closing) return;
        this.closing = true;
        this.closeAnimation.run(1.0, 0.3, Easings.EXPO_IN);
        this.openAnimation.run(0.0, 0.3, Easings.EXPO_IN);
    }

    private void updateAnimations() {
        openAnimation.update();
        closeAnimation.update();
        helpTextAnimation.update();
        rotationAnimation.update();
        zoomAnimation.update();

        entityRotationYaw += (targetRotationYaw - entityRotationYaw) * 0.15f;
        entityRotationPitch += (targetRotationPitch - entityRotationPitch) * 0.15f;

        entityScale += (targetEntityScale - entityScale) * 0.15f;

        if (!isDragging) {
            targetRotationYaw += 0.5f;
            if (targetRotationYaw > 360f) {
                targetRotationYaw -= 360f;
                entityRotationYaw -= 360f;
            }
        }
    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        if (closing && closeAnimation.isFinished() && openAnimation.isFinished()) {
            mc.displayScreen(null);
            return;
        }

        this.renderBackground(matrix);
        updateAnimations();

        Theme theme = Theme.getInstance();
        int clientColor = theme.clientColor();

        float easedAnim = openAnimation.get() * (1.0f - closeAnimation.get());
        if (easedAnim <= 0.01f) return;

        float centerX = this.width / 2.0f;
        float centerY = this.height / 2.0f;

        float mainWidth = 600;
        float mainHeight = 500;
        float mainX = centerX - mainWidth / 2;
        float mainY = centerY - mainHeight / 2;

        matrix.push();
        matrix.translate(centerX, centerY, 0);
        matrix.scale(easedAnim, easedAnim, 1);
        matrix.translate(-centerX, -centerY, 0);
        RenderSystem.color4f(1, 1, 1, easedAnim);

        BlurRenderer2.registerRenderCall(() -> {
            RenderUtil.Rounded.roundedRect(matrix, mainX, mainY, mainWidth, mainHeight,
                    ColorUtil.multAlpha(ColorUtil.getColor(0, 0, 0, 100), easedAnim), Round.of(12));
        });
        BlurRenderer2.draw(20);

        RenderUtil.Rounded.roundedRect(matrix, mainX, mainY, mainWidth, mainHeight,
                ColorUtil.multAlpha(ColorUtil.getColor(12, 12, 12, 230), easedAnim), Round.of(12));
        RenderUtil.Rounded.roundedOutline(matrix, mainX, mainY, mainWidth, mainHeight, 2f,
                ColorUtil.multAlpha(ColorUtil.getColor(255, 255, 255, 40), easedAnim), Round.of(12));

        String title = "Nametags Preview";
        Fonts.SF_BOLD.drawCenter(matrix, title, centerX, mainY + 20,
                ColorUtil.multAlpha(clientColor, easedAnim), 18f);

        String entityInfo = getEntityInfo();
        Fonts.SF_REGULAR.drawCenter(matrix, entityInfo, centerX, mainY + 42,
                ColorUtil.multAlpha(ColorUtil.getColor(180, 200, 220), easedAnim), 12f);

        renderEntityPreview(matrix, centerX, centerY + 20, easedAnim);

        renderHelpButton(matrix, mouseX, mouseY, mainX, mainY, mainWidth, clientColor, easedAnim);

        if (showHelp || helpTextAnimation.getValue() > 0.01) {
            renderHelpPanel(matrix, mainX, mainY, mainWidth, mainHeight, clientColor, easedAnim);
        }

        renderControlsInfo(matrix, centerX, mainY + mainHeight - 30, clientColor, easedAnim);

        matrix.pop();
        super.render(matrix, mouseX, mouseY, partialTicks);
    }

    private void renderEntityPreview(MatrixStack matrix, float centerX, float centerY, float alpha) {
        if (previewEntity == null) return;

        float entitySize = 80f * entityScale;

        float previewX = centerX;
        float previewY = centerY;

        matrix.push();
        matrix.translate(previewX, previewY, 100);

        matrix.rotate(net.minecraft.util.math.vector.Vector3f.YP.rotationDegrees(entityRotationYaw));
        matrix.rotate(net.minecraft.util.math.vector.Vector3f.XP.rotationDegrees(entityRotationPitch));

        RenderSystem.pushMatrix();
        RenderHelper.enableStandardItemLighting();

        if (previewEntity instanceof PlayerEntity) {
            renderPlayerEntity(matrix, 0, 0, entitySize, alpha);
        } else if (previewEntity instanceof ItemEntity) {
            renderItemEntity(matrix, (ItemEntity) previewEntity, 0, 0, entitySize, alpha);
        }

        RenderHelper.disableStandardItemLighting();
        RenderSystem.popMatrix();

        matrix.pop();
    }

    private void renderPlayerEntity(MatrixStack matrix, float x, float y, float size, float alpha) {
        try {
            PlayerEntity player = (PlayerEntity) previewEntity;

            matrix.push();
            matrix.translate(x, y, 0);
            matrix.scale(-size, size, size);
            matrix.rotate(net.minecraft.util.math.vector.Vector3f.ZP.rotationDegrees(180));

            RenderSystem.color4f(1.0f, 1.0f, 1.0f, alpha);

            mc.getRenderManager().renderEntityStatic(player, 0, 0, 0, 0, 1.0f, matrix,
                    mc.getRenderTypeBuffers().getBufferSource(), 15728880);

            matrix.pop();
        } catch (Exception e) {
            Fonts.SF_REGULAR.drawCenter(matrix, "Player: " + previewEntity.getName().getString(),
                    x, y, ColorUtil.multAlpha(-1, alpha), 14f);
        }
    }

    private void renderItemEntity(MatrixStack matrix, ItemEntity itemEntity, float x, float y, float size, float alpha) {
        matrix.push();
        matrix.translate(x, y, 0);
        matrix.scale(size / 16f, size / 16f, 1);

        RenderSystem.color4f(1.0f, 1.0f, 1.0f, alpha);
        mc.getItemRenderer().renderItemAndEffectIntoGUI(itemEntity.getItem(), (int) (-8 * (16f / size)), (int) (-8 * (16f / size)));

        matrix.pop();
    }

    private void renderHelpButton(MatrixStack matrix, int mouseX, int mouseY, float mainX, float mainY,
                                  float mainWidth, int clientColor, float alpha) {
        float buttonSize = 35;
        float buttonX = mainX + mainWidth - buttonSize - 15;
        float buttonY = mainY + 15;

        boolean hovered = isHover(mouseX, mouseY, buttonX, buttonY, buttonSize, buttonSize);

        int bgColor = hovered ?
                ColorUtil.multAlpha(clientColor, alpha * 0.3f) :
                ColorUtil.multAlpha(ColorUtil.getColor(30, 35, 45), alpha * 0.6f);

        RenderUtil.Rounded.roundedRect(matrix, buttonX, buttonY, buttonSize, buttonSize, bgColor, Round.of(6));
        RenderUtil.Rounded.roundedOutline(matrix, buttonX, buttonY, buttonSize, buttonSize, 1.5f,
                ColorUtil.multAlpha(clientColor, alpha * 0.6f), Round.of(6));

        Fonts.SF_BOLD.drawCenter(matrix, "?", buttonX + buttonSize / 2, buttonY + buttonSize / 2 - 7,
                ColorUtil.multAlpha(clientColor, alpha), 20f);
    }

    private void renderHelpPanel(MatrixStack matrix, float mainX, float mainY, float mainWidth,
                                 float mainHeight, int clientColor, float alpha) {
        helpTextAnimation.run(showHelp ? 1.0 : 0.0, 0.3, Easings.EXPO_OUT);
        float helpAlpha = (float) helpTextAnimation.getValue() * alpha;

        if (helpAlpha < 0.01f) return;

        float panelWidth = 400;
        float panelHeight = 300;
        float panelX = mainX + (mainWidth - panelWidth) / 2;
        float panelY = mainY + (mainHeight - panelHeight) / 2;

        matrix.push();
        matrix.translate(panelX + panelWidth / 2, panelY + panelHeight / 2, 0);
        matrix.scale(helpAlpha, helpAlpha, 1);
        matrix.translate(-(panelX + panelWidth / 2), -(panelY + panelHeight / 2), 0);

        BlurRenderer2.registerRenderCall(() -> {
            RenderUtil.Rounded.roundedRect(matrix, panelX, panelY, panelWidth, panelHeight,
                    ColorUtil.multAlpha(ColorUtil.getColor(0, 0, 0, 120), helpAlpha), Round.of(10));
        });
        BlurRenderer2.draw(15);

        RenderUtil.Rounded.roundedRect(matrix, panelX, panelY, panelWidth, panelHeight,
                ColorUtil.multAlpha(ColorUtil.getColor(20, 25, 35, 240), helpAlpha), Round.of(10));
        RenderUtil.Rounded.roundedOutline(matrix, panelX, panelY, panelWidth, panelHeight, 2f,
                ColorUtil.multAlpha(clientColor, helpAlpha * 0.6f), Round.of(10));

        Fonts.SF_BOLD.drawCenter(matrix, "Документация", panelX + panelWidth / 2, panelY + 20,
                ColorUtil.multAlpha(clientColor, helpAlpha), 16f);

        String[] helpLines = {
                "Управление:",
                "",
                "• ЛКМ + Движение мыши - Вращать entity",
                "• Колесико мыши - Приближение/Отдаление",
                "• ESC - Закрыть превью",
                "",
                "Функции:",
                "",
                "• Автоматическое вращение при бездействии",
                "• Плавные анимации вращения и масштабирования",
                "• Отображение всех настроек Nametags",
                "",
                "Ограничения масштаба: 0.3x - 3.0x"
        };

        float textY = panelY + 50;
        for (String line : helpLines) {
            int color = line.startsWith("•") ?
                    ColorUtil.multAlpha(ColorUtil.getColor(180, 200, 220), helpAlpha) :
                    ColorUtil.multAlpha(clientColor, helpAlpha);

            Fonts.SF_REGULAR.draw(matrix, line, panelX + 30, textY, color, 11f);
            textY += 16;
        }

        matrix.pop();
    }

    private void renderControlsInfo(MatrixStack matrix, float centerX, float y, int clientColor, float alpha) {
        String controls = "ЛКМ - Вращение | Колесико - Масштаб | ESC - Закрыть";
        Fonts.SF_REGULAR.drawCenter(matrix, controls, centerX, y,
                ColorUtil.multAlpha(ColorUtil.getColor(150, 170, 200), alpha), 10f);
    }

    private String getEntityInfo() {
        if (previewEntity instanceof PlayerEntity player) {
            return "Игрок: " + player.getName().getString() +
                    " | HP: " + (int) player.getHealth() + "/" + (int) player.getMaxHealth();
        } else if (previewEntity instanceof ItemEntity itemEntity) {
            return "Предмет: " + itemEntity.getItem().getDisplayName().getString() +
                    " x" + itemEntity.getItem().getCount();
        }
        return "Entity: " + previewEntity.getName().getString();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        float centerX = this.width / 2.0f;
        float centerY = this.height / 2.0f;
        float mainWidth = 600;
        float mainHeight = 500;
        float mainX = centerX - mainWidth / 2;
        float mainY = centerY - mainHeight / 2;

        float buttonSize = 35;
        float buttonX = mainX + mainWidth - buttonSize - 15;
        float buttonY = mainY + 15;

        if (isHover(mouseX, mouseY, buttonX, buttonY, buttonSize, buttonSize)) {
            showHelp = !showHelp;
            return true;
        }

        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            if (showHelp) {
                float panelWidth = 400;
                float panelHeight = 300;
                float panelX = mainX + (mainWidth - panelWidth) / 2;
                float panelY = mainY + (mainHeight - panelHeight) / 2;

                if (isHover(mouseX, mouseY, panelX, panelY, panelWidth, panelHeight)) {
                    return true;
                }
            }

            isDragging = true;
            lastMouseX = mouseX;
            lastMouseY = mouseY;
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            isDragging = false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (isDragging) {
            double dx = mouseX - lastMouseX;
            double dy = mouseY - lastMouseY;

            targetRotationYaw += (float) dx * ROTATION_SENSITIVITY;
            targetRotationPitch += (float) dy * ROTATION_SENSITIVITY;

            targetRotationPitch = Math.max(-90f, Math.min(90f, targetRotationPitch));

            lastMouseX = mouseX;
            lastMouseY = mouseY;

            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        targetEntityScale += (float) delta * ZOOM_SENSITIVITY;
        targetEntityScale = Math.max(MIN_SCALE, Math.min(MAX_SCALE, targetEntityScale));
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            if (showHelp) {
                showHelp = false;
                return true;
            }
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
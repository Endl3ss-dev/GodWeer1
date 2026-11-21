package tech.lightclient.mixins.render;


import tech.lightclient.LightClient;
import tech.lightclient.client.utils.math.MathUtil;
import tech.lightclient.client.utils.render.CursorHelper;
import tech.lightclient.client.utils.render.drag.DraggingManager;
import tech.lightclient.client.utils.render.drag.api.Dragging;
import tech.lightclient.ui.hud.ChatButtonsOverlay;
import tech.lightclient.ui.hud.ChatOverlayState;
import tech.lightclient.ui.hud.star.StarHud;

import tech.lightclient.modules.impl.render.Interface;
import tech.lightclient.ui.hud.HudSettingsOverlay;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static tech.lightclient.client.utils.client.IMinecraft.mc;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {
    @Shadow private TextFieldWidget chatField;

    @Inject(method = "render", at = @At("HEAD"))
    public void renderHead(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (LightClient.isHide()) return;

        CursorHelper.setArrow();

        Dragging hovered = null;

        // Screen render coordinates and Click coordinates are logical GUI
        // coordinates. The old getX/getY values are framebuffer pixels and
        // caused the Star HUD drag hitbox to drift, especially lower on screen.
        var hudMouseX = (double) mouseX;
        var hudMouseY = (double) mouseY;

        // Keep the legacy draggable overlays in their existing framebuffer
        // coordinate system; only the Star HUD uses logical GUI coordinates.
        var mouseXNormal = mc.mouse.getX();
        var mouseYNormal = mc.mouse.getY();
        float maxW = MinecraftClient.getInstance().getWindow().getWidth();
        float maxH = MinecraftClient.getInstance().getWindow().getHeight();

        StarHud starHud = StarHud.getInstance();
        if (starHud != null) {
            starHud.updateDrag(hudMouseX, hudMouseY);
            if (starHud.isPopupOpen() || starHud.hasHoveredElement(hudMouseX, hudMouseY)) {
                CursorHelper.setHand();
            }
        }

        for (Dragging dragging : DraggingManager.draggables.values()) {
            if (dragging.getName() != null && dragging.getName().startsWith("Star ")) {
                continue;
            }
            if (dragging.getModule() != null && dragging.getModule().isState()) {
                if (MathUtil.isHovered(mouseXNormal, mouseYNormal, dragging.getX(), dragging.getY(), dragging.getWidth(), dragging.getHeight())) {
                    hovered = dragging;
                    CursorHelper.setHand();
                }
                dragging.onFix(mouseXNormal, mouseYNormal, maxW, maxH);
            }
        }
        ChatOverlayState.hoveredElementName = hovered != null ? hovered.getName() : null;
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void renderTail(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (chatField == null || LightClient.isHide()) {
            return;
        }
        float scale = (float) mc.getWindow().getScaleFactor();
        boolean anyDragging = false;
        for (var dragging : DraggingManager.draggables.values()) {
            if (dragging.isDragging()) {
                anyDragging = true;
                break;
            }
        }
        ChatOverlayState.draggingAny = anyDragging;
        String hideLabel = ChatButtonsOverlay.HIDE_LABEL;
        String resetLabel = ChatButtonsOverlay.RESET_LABEL;
        int btnH = Math.round(16f * scale);
        int btnY = Math.round((chatField.getY() - 16 - 8) * scale);
        int rightEdge = Math.round((chatField.getX() + chatField.getWidth()) * scale);
        int gap = Math.round(6f * scale);

        ChatOverlayState.resetBtnW = Math.round((mc.textRenderer.getWidth(resetLabel) + 14) * scale);
        ChatOverlayState.resetBtnH = btnH;
        ChatOverlayState.resetBtnX = rightEdge - ChatOverlayState.resetBtnW;
        ChatOverlayState.resetBtnY = btnY;

        ChatOverlayState.hideBtnW = Math.round((mc.textRenderer.getWidth(hideLabel) + 14) * scale);
        ChatOverlayState.hideBtnH = btnH;
        ChatOverlayState.hideBtnX = rightEdge - ChatOverlayState.resetBtnW - gap - ChatOverlayState.hideBtnW;
        ChatOverlayState.hideBtnY = btnY;


        if (ChatOverlayState.hideChatInfo) {
            String text = chatField.getText();
            if (text == null || text.isEmpty()) {
                ChatOverlayState.barVisible = false;
                return;
            }
            int innerPadding = 4;
            int leftExtend = 6;
            int rightExtend = 3;
            int maxWidth = Math.max(0, chatField.getWidth() - innerPadding * 2 + leftExtend);
            int textWidth = mc.textRenderer.getWidth(text);
            int barWidth = Math.min(maxWidth, textWidth + rightExtend + leftExtend);
            int barXLocal = chatField.getX() + innerPadding - leftExtend;
            int barH = chatField.getHeight();
            int barYLocal = chatField.getY() - 2;
            int barWScaled = Math.round(barWidth * scale);
            int barHScaled = Math.round(barH * scale);
            ChatOverlayState.barVisible = barWScaled > 0 && barHScaled > 0;
            ChatOverlayState.barX = Math.round(barXLocal * scale);
            ChatOverlayState.barY = Math.round(barYLocal * scale);
            ChatOverlayState.barW = barWScaled;
            ChatOverlayState.barH = barHScaled;
        } else {
            ChatOverlayState.barVisible = false;
        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void mouseClicked(Click click, boolean doubleClick, CallbackInfoReturnable<Boolean> cir) {
        int button = click.button();
        if (button != 0 && button != 1) {
            return;
        }
        double rawMouseX = mc.mouse.getX();
        double rawMouseY = mc.mouse.getY();
        double hudMouseX = click.x();
        double hudMouseY = click.y();

        StarHud starHud = StarHud.getInstance();
        if (starHud != null && starHud.mouseClicked(hudMouseX, hudMouseY, button)) {
            cir.setReturnValue(true);
            cir.cancel();
            return;
        }

        if (HudSettingsOverlay.handleMouseClick(rawMouseX, rawMouseY, button)) {
            cir.setReturnValue(true);
            cir.cancel();
            return;
        }

        if (button == 1) {
            for (var dragging : DraggingManager.draggables.values()) {
                if (dragging.getName() != null && dragging.getName().startsWith("Star ")) {
                    continue;
                }
                if (dragging.getModule() != null && dragging.getModule().isState()) {
                    if (MathUtil.isHovered(rawMouseX, rawMouseY, dragging.getX(), dragging.getY(), dragging.getWidth(), dragging.getHeight())) {
                        HudSettingsOverlay.open(dragging.getName(), rawMouseX, rawMouseY);
                        cir.setReturnValue(true);
                        cir.cancel();
                        return;
                    }
                }
            }
            HudSettingsOverlay.close();
            return;
        }

        if (rawMouseX >= ChatOverlayState.hideBtnX && rawMouseX <= ChatOverlayState.hideBtnX + ChatOverlayState.hideBtnW
                && rawMouseY >= ChatOverlayState.hideBtnY && rawMouseY <= ChatOverlayState.hideBtnY + ChatOverlayState.hideBtnH) {
            ChatOverlayState.hideChatInfo = !ChatOverlayState.hideChatInfo;
            cir.setReturnValue(true);
            cir.cancel();
            return;
        }
        if (rawMouseX >= ChatOverlayState.resetBtnX && rawMouseX <= ChatOverlayState.resetBtnX + ChatOverlayState.resetBtnW
                && rawMouseY >= ChatOverlayState.resetBtnY && rawMouseY <= ChatOverlayState.resetBtnY + ChatOverlayState.resetBtnH) {
            DraggingManager.draggables.values().forEach(dragging -> dragging.resetPosition());
            DraggingManager.save();
            cir.setReturnValue(true);
            cir.cancel();
            return;
        }
    }

    @Redirect(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V"
            )
    )
    private void light$skipVanillaChatInputBackground(DrawContext context, int x1, int y1, int x2, int y2, int color) {
        if (y2 - y1 > 20) {
            context.fill(x1, y1, x2, y2, color);
        }
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void light$keyPressed(KeyInput input, CallbackInfoReturnable<Boolean> cir) {
        int keyCode = input.key();
        var window = mc.getWindow();
        boolean ctrlDown = InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_LEFT_CONTROL)
                || InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_RIGHT_CONTROL);
        boolean shiftDown = InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_LEFT_SHIFT)
                || InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_RIGHT_SHIFT);

        if (ctrlDown && keyCode == GLFW.GLFW_KEY_Z) {
            if (DraggingManager.undo()) {
                cir.setReturnValue(true);
                cir.cancel();
            }
            return;
        }

        double hudMouseX = mc.mouse.getScaledX(mc.getWindow());
        double hudMouseY = mc.mouse.getScaledY(mc.getWindow());
        Dragging hovered = DraggingManager.getHoveredDragging(hudMouseX, hudMouseY);
        if (hovered != null && hovered.getName() != null && hovered.getName().startsWith("Star ")) {
            hovered = null;
        }

        StarHud starHudForKey = StarHud.getInstance();
        if (starHudForKey != null && !ctrlDown && starHudForKey.keyPressed(keyCode, false, shiftDown)) {
            cir.setReturnValue(true);
            cir.cancel();
            return;
        }
        if (hovered == null) return;

        float step = shiftDown ? Interface.getHudEditorGridSize() : 1f;
        if (step <= 0f) step = 1f;
        float deltaX = 0f, deltaY = 0f;

        switch (keyCode) {
            case GLFW.GLFW_KEY_LEFT -> deltaX = -step;
            case GLFW.GLFW_KEY_RIGHT -> deltaX = step;
            case GLFW.GLFW_KEY_UP -> deltaY = -step;
            case GLFW.GLFW_KEY_DOWN -> deltaY = step;
            default -> { return; }
        }

        if (deltaX != 0f || deltaY != 0f) {
            DraggingManager.pushUndoSnapshot(hovered.getName(), hovered.getX(), hovered.getY());
            float newX = hovered.getX() + deltaX;
            float newY = hovered.getY() + deltaY;
            float maxW = mc.getWindow().getWidth();
            float maxH = mc.getWindow().getHeight();
            newX = Math.max(0f, Math.min(maxW - hovered.getWidth(), newX));
            newY = Math.max(0f, Math.min(maxH - hovered.getHeight(), newY));
            hovered.setX(newX);
            hovered.setY(newY);
            DraggingManager.saveNow();
            cir.setReturnValue(true);
            cir.cancel();
        }
    }

    @Inject(method = "mouseReleased", at = @At("HEAD"))
    private void light$mouseReleased(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        StarHud starHud = StarHud.getInstance();
        if (starHud != null) {
            starHud.mouseReleased(mouseX, mouseY, button);
        }

        for (Dragging dragging : DraggingManager.draggables.values()) {
            if (dragging.getName() != null && dragging.getName().startsWith("Star ")) {
                continue;
            }
            if (dragging.getModule() != null && dragging.getModule().isState()) {
                dragging.onRelease(button);
            }
        }
    }

}


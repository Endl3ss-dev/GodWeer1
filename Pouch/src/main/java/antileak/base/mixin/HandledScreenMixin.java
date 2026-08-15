package antileak.base.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import antileak.base.client.modules.impl.misc.AHHelper;
import antileak.base.client.modules.impl.player.ItemScroller;
import antileak.base.client.modules.impl.combat.AutoSwap;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin {

    @Shadow
    @Nullable
    protected abstract Slot getSlotAt(double x, double y);

    @Shadow
    protected abstract void onMouseClick(@Nullable Slot slot, int slotId, int button, SlotActionType actionType);

    @Shadow protected int x;
    @Shadow protected int y;
    @Shadow protected int backgroundWidth;
    @Shadow protected int backgroundHeight;

    @Inject(method = "onMouseClick(Lnet/minecraft/screen/slot/Slot;IILnet/minecraft/screen/slot/SlotActionType;)V", at = @At("HEAD"), cancellable = true)
    private void onMouseClickSlot(Slot slot, int slotId, int button, SlotActionType actionType, CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();
        
        // Обработка для AutoSwap в режиме колеса
        AutoSwap autoSwap = AutoSwap.INSTANCE;
        if (autoSwap != null && autoSwap.isEnable() && autoSwap.isPendingPickSlot()) {
            if (mc.currentScreen instanceof net.minecraft.client.gui.screen.ingame.InventoryScreen) {
                // Проверяем, что это ПКМ (button == 1) и есть слот
                if (button == 1 && slot != null && slot.hasStack()) {
                    if (autoSwap.handleInventoryClick(slot, slotId)) {
                        ci.cancel();
                    }
                }
            }
        }
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();
        ItemScroller itemScroller = ItemScroller.INSTANCE;
        AHHelper ahHelper = AHHelper.INSTANCE;

        // ItemScroller логика
        if (itemScroller.isEnable() && mc.player != null && mc.interactionManager != null) {
            long window = mc.getWindow().getHandle();
            boolean leftMousePressed = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
            boolean shiftPressed = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS
                    || GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS;

            if (leftMousePressed && shiftPressed) {
                Slot slot = getSlotAt(mouseX, mouseY);
                if (slot != null && slot.hasStack() && itemScroller.canQuickMove()) {
                    onMouseClick(slot, slot.id, 0, SlotActionType.QUICK_MOVE);
                }
            } else {
                itemScroller.resetTimer();
            }
        }

        // AHHelper логика - сканируем слоты перед отрисовкой
        if (ahHelper.isEnable() && (Object) this instanceof GenericContainerScreen screen) {
            String title = screen.getTitle().getString().toLowerCase();
            
            // Проверяем, что это экран аукциона (различные варианты названия)
            if (title.contains("аукцион") || title.contains("поиск") || 
                title.contains("auction") || title.contains("search")) {
                ahHelper.scanPrices(screen.getScreenHandler().slots);
            }
        }
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void onRenderPost(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        // Вызываем EventHandledScreen для других модулей, если потребуется
        antileak.base.api.events.implement.EventHandledScreen event = 
            new antileak.base.api.events.implement.EventHandledScreen(context, 
                backgroundWidth, 
                backgroundHeight);
        event.call();
    }

    @Inject(method = "drawSlot", at = @At("HEAD"))
    private void onDrawSlot(DrawContext context, Slot slot, CallbackInfo ci) {
        AHHelper ahHelper = AHHelper.INSTANCE;
        
        if (!ahHelper.isEnable()) return;
        
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.currentScreen instanceof GenericContainerScreen screen) {
            String title = screen.getTitle().getString().toLowerCase();
            
            // Проверяем, что это экран аукциона
            if (title.contains("аукцион") || title.contains("поиск") || 
                title.contains("auction") || title.contains("search")) {
                
                // Не подсвечиваем слоты в инвентаре игрока
                if (slot.inventory == mc.player.getInventory()) return;
                
                if (ahHelper.shouldRenderSlot(slot)) {
                    ahHelper.renderSlot(context, slot);
                }
            }
        }
    }
}

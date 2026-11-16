package tech.lightclient.mixins.render;


import tech.lightclient.LightClient;
import tech.lightclient.client.events.impl.render.HandledScreenEvent;
import tech.lightclient.client.utils.math.time.StopWatch;
import tech.lightclient.modules.api.FunctionManager;
import tech.lightclient.modules.api.Function;
import tech.lightclient.modules.impl.utils.ItemScroller;
import tech.lightclient.autobuy.AutoBuyOverlay;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.client.util.InputUtil;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin<T extends ScreenHandler> extends Screen implements ScreenHandlerProvider<T> {
    @Unique
    private StopWatch itemScrollerTimer;

    @Unique
    private int lastScrolledSlotId = -1;

    @Unique
    private StopWatch getItemScrollerTimer() {
        if (itemScrollerTimer == null) itemScrollerTimer = new StopWatch();
        return itemScrollerTimer;
    }

    protected HandledScreenMixin(Text title) {
        super(title);
    }

    @Shadow
    protected abstract void onMouseClick(Slot slotIn, int slotId, int mouseButton, SlotActionType type);

    @Shadow
    public abstract T getScreenHandler();

    @Shadow protected int backgroundWidth;
    @Shadow protected int backgroundHeight;

    @Shadow
    @Nullable
    protected Slot focusedSlot;

    @Shadow private int x;
    @Shadow private int y;

    @Inject(method = "render", at = @At("TAIL"))
    public void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        var renderer = LightClient.getInstance().renderer;
        HandledScreenEvent event = new HandledScreenEvent(context, renderer, focusedSlot, backgroundWidth, backgroundHeight);
        LightClient.getEventBus().post(event);
        if (AutoBuyOverlay.shouldShow((HandledScreen<?>) (Object) this)) {
            AutoBuyOverlay.render(context, x, y, backgroundWidth, backgroundHeight, mouseX, mouseY);
        }
    }

    @Inject(method = "mouseDragged", at = @At("HEAD"), cancellable = true)
    private void onItemScrollerDrag(Click click, double deltaX, double deltaY, CallbackInfoReturnable<Boolean> cir) {
        if (!isItemScrollerActive() || !ItemScroller.scrollEnabled.get()) return;
        if (click.button() != 0) return;

        if (isShiftKeyDown()) {
            Slot slot = findSlotAt(click.x(), click.y());
            if (slot != null && slot.id != lastScrolledSlotId && slot.hasStack()) {
                lastScrolledSlotId = slot.id;
                long delayMs = ItemScroller.delay.get().longValue();
                if (getItemScrollerTimer().hasReached(delayMs)) {
                    getItemScrollerTimer().reset();
                    onMouseClick(slot, slot.id, 0, SlotActionType.QUICK_MOVE);
                }
            }
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "mouseReleased", at = @At("HEAD"))
    private void onItemScrollerRelease(Click click, CallbackInfoReturnable<Boolean> cir) {
        lastScrolledSlotId = -1;
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void light$autoBuyClick(Click click, boolean isRepeated, CallbackInfoReturnable<Boolean> cir) {
        if (AutoBuyOverlay.shouldShow((HandledScreen<?>) (Object) this)
                && AutoBuyOverlay.mouseClicked(click.x(), click.y(), click.button(), x, y, backgroundWidth, backgroundHeight)) {
            cir.setReturnValue(true);
        }
    }

    @Unique
    private boolean isItemScrollerActive() {
        FunctionManager fm = LightClient.getFunctionManager();
        if (fm == null) return false;
        Function itemScroller = fm.getFunctionByName("ItemScroller");
        return itemScroller != null && itemScroller.isState();
    }

    @Unique
    private boolean isShiftKeyDown() {
        var win = client != null ? client.getWindow() : null;
        if (win == null) return false;
        return InputUtil.isKeyPressed(win, GLFW.GLFW_KEY_LEFT_SHIFT)
                || InputUtil.isKeyPressed(win, GLFW.GLFW_KEY_RIGHT_SHIFT);
    }

    @Unique
    @Nullable
    private Slot findSlotAt(double mouseX, double mouseY) {
        ScreenHandler handler = getScreenHandler();
        if (handler == null) return null;
        for (Slot slot : handler.slots) {
            int slotScreenX = this.x + slot.x;
            int slotScreenY = this.y + slot.y;
            if (mouseX >= slotScreenX && mouseX < slotScreenX + 16
                    && mouseY >= slotScreenY && mouseY < slotScreenY + 16) {
                return slot;
            }
        }
        return null;
    }
}

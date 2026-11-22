package tech.lightclient.mixins.render;


import tech.lightclient.client.utils.player.PlayerInventoryUtil;
import tech.lightclient.client.utils.player.PlayerUtils;
import tech.lightclient.LightClient;
import tech.lightclient.modules.impl.render.Beautifully;
import tech.lightclient.modules.impl.utils.ClientHide;
import tech.lightclient.modules.impl.utils.InventoryHelper;
import tech.lightclient.modules.impl.utils.Tweaks;
import tech.lightclient.ui.screen.InventoryHelperScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends HandledScreen<PlayerScreenHandler> {
    @Unique
    private static final int DROP_BUTTON_WIDTH = 98;
    @Unique
    private static final int DROP_BUTTON_HEIGHT = 20;
    @Unique
    private int light$dropButtonX;
    @Unique
    private int light$dropButtonY;
    @Unique
    private static long light$handCursor;
    @Unique
    private static long light$arrowCursor;
    @Unique
    private boolean light$wasHovered;
    @Unique
    private boolean light$inventoryHelperButtonsAdded;

    public InventoryScreenMixin(PlayerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void light$addInventoryHelperButtons(CallbackInfo ci) {
        if (light$inventoryHelperButtonsAdded || LightClient.getFunctionManager().getFunction("InventoryHelper") == null
                || !LightClient.getFunctionManager().getFunction("InventoryHelper").isState()) {
            return;
        }

        int buttonX = this.x + this.backgroundWidth + 6;
        int buttonY = this.y + 76;
        this.addDrawableChild(ButtonWidget.builder(Text.literal("\u041c\u0435\u043d\u044e"), button ->
                MinecraftClient.getInstance().setScreen(new InventoryHelperScreen()))
                .dimensions(buttonX, buttonY, 88, 20).build());
        this.addDrawableChild(ButtonWidget.builder(Text.literal("\u0421\u043e\u0440\u0442\u0438\u0440\u043e\u0432\u0430\u0442\u044c"), button ->
                InventoryHelper.sortSelectedLayout())
                .dimensions(buttonX, buttonY + 24, 88, 20).build());
        light$inventoryHelperButtonsAdded = true;
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void light$highlightPotionSlots(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!Beautifully.shouldHighlightInventoryPotions()) {
            return;
        }

        for (Slot slot : this.handler.slots) {
            if (slot == null || !slot.hasStack()) {
                continue;
            }

            ItemStack stack = slot.getStack();
            int color = Beautifully.getPotionHighlightColor(stack);
            if (color == 0) {
                continue;
            }

            int slotX = this.x + slot.x;
            int slotY = this.y + slot.y;
            context.fill(slotX, slotY, slotX + 16, slotY + 16, color);
        }
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void light$renderDropAllButton(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!light$isButtonVisible()) {
            if (light$wasHovered) {
                light$wasHovered = false;
                light$setCursor(GLFW.GLFW_ARROW_CURSOR);
            }
            return;
        }

        int centerX = this.x + 88;
        int buttonY = this.y + 84 - 120;
        int buttonX = centerX - DROP_BUTTON_WIDTH / 2;

        this.light$dropButtonX = buttonX;
        this.light$dropButtonY = buttonY;

        boolean hasItems = light$hasInventoryItems();
        boolean hovered = mouseX >= buttonX && mouseX <= buttonX + DROP_BUTTON_WIDTH
                && mouseY >= buttonY && mouseY <= buttonY + DROP_BUTTON_HEIGHT;

        if (hovered != light$wasHovered) {
            light$wasHovered = hovered;
            light$setCursor(hovered && hasItems ? GLFW.GLFW_HAND_CURSOR : GLFW.GLFW_ARROW_CURSOR);
        }

        Identifier texture;
        int textColor;
        if (!hasItems) {
            texture = Identifier.ofVanilla("widget/button_disabled");
            textColor = 0xAA888888;
        } else if (hovered) {
            texture = Identifier.ofVanilla("widget/button_highlighted");
            textColor = 0xFFFFFFFF;
        } else {
            texture = Identifier.ofVanilla("widget/button");
            textColor = 0xFFFFFFFF;
        }

        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, texture, buttonX, buttonY, DROP_BUTTON_WIDTH, DROP_BUTTON_HEIGHT);

        String text = "Выкинуть всё";
        context.drawText(this.textRenderer, Text.literal(text),
                buttonX + DROP_BUTTON_WIDTH / 2 - this.textRenderer.getWidth(text) / 2,
                buttonY + DROP_BUTTON_HEIGHT / 2 - this.textRenderer.fontHeight / 2,
                textColor, true);
    }


    @Inject(method = "mouseReleased", at = @At("HEAD"), cancellable = true)
    private void light$onMouseReleased(Click click, CallbackInfoReturnable<Boolean> cir) {
        if (!light$isButtonVisible() || click.button() != 0) return;

        int bx = this.light$dropButtonX;
        int by = this.light$dropButtonY;
        double mx = click.x();
        double my = click.y();
        if (mx < bx || mx > bx + DROP_BUTTON_WIDTH
                || my < by || my > by + DROP_BUTTON_HEIGHT) return;

        if (light$hasInventoryItems()) {
            light$dropAllItems();
        }
        cir.setReturnValue(true);
    }

    @Unique
    private boolean light$hasInventoryItems() {
        for (int i = 9; i < 46; i++) {
            Slot slot = this.handler.getSlot(i);
            if (slot.hasStack()) return true;
        }
        return false;
    }

    @Unique
    private void light$dropAllItems() {
        if (Tweaks.dropAllMode.is("Легитный")) {
            light$dropAllItemsLegit();
        } else {
            light$dropAllItemsNormal();
        }
    }

    @Unique
    private void light$dropAllItemsNormal() {
        PlayerUtils.addTask(() -> {
            for (int i = 9; i < 46; i++) {
                Slot slot = light$getHandlerSlot(i);
                if (slot != null && slot.hasStack()) {
                    PlayerInventoryUtil.clickSlot(slot.id, 1, SlotActionType.THROW);
                }
            }
        });
    }

    @Unique
    private void light$dropAllItemsLegit() {
        boolean moving = PlayerUtils.hasPlayerMovement();

        PlayerUtils.script.cleanup();

        if (moving) {
            PlayerUtils.script.addTickStep(0, PlayerUtils::disableMoveKeys);
        }

        List<Integer> slotIds = new ArrayList<>();
        for (int i = 9; i < 46; i++) {
            Slot slot = this.handler.getSlot(i);
            if (slot.hasStack()) {
                slotIds.add(slot.id);
            }
        }

        if (slotIds.isEmpty()) return;

        int startTick = moving ? 2 : 1;
        for (int idx = 0; idx < slotIds.size(); idx++) {
            int sid = slotIds.get(idx);
            PlayerUtils.script.addTickStep(startTick + idx, () ->
                    PlayerInventoryUtil.clickSlot(sid, 1, SlotActionType.THROW));
        }

        if (moving) {
            PlayerUtils.script.addTickStep(startTick + slotIds.size(), PlayerUtils::enableMoveKeys);
        }
    }

    @Unique
    private boolean light$isButtonVisible() {
        return !ClientHide.unhooked;
    }

    @Unique
    private static void light$setCursor(int glfwCursor) {
        long window = MinecraftClient.getInstance().getWindow().getHandle();
        if (glfwCursor == GLFW.GLFW_ARROW_CURSOR) {
            if (light$arrowCursor == 0) {
                light$arrowCursor = GLFW.glfwCreateStandardCursor(GLFW.GLFW_ARROW_CURSOR);
            }
            GLFW.glfwSetCursor(window, light$arrowCursor);
        } else {
            if (light$handCursor == 0) {
                light$handCursor = GLFW.glfwCreateStandardCursor(GLFW.GLFW_HAND_CURSOR);
            }
            GLFW.glfwSetCursor(window, light$handCursor);
        }
    }

    @Unique
    private static void light$destroyCursors() {
        if (light$handCursor != 0) {
            GLFW.glfwDestroyCursor(light$handCursor);
            light$handCursor = 0;
        }
        if (light$arrowCursor != 0) {
            GLFW.glfwDestroyCursor(light$arrowCursor);
            light$arrowCursor = 0;
        }
    }

    @Unique
    private Slot light$getHandlerSlot(int index) {
        if (index < 0 || index >= this.handler.slots.size()) return null;
        return this.handler.slots.get(index);
    }
}

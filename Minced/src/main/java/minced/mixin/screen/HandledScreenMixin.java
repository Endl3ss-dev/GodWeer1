/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.ingame.GenericContainerScreen
 *  net.minecraft.client.gui.screen.ingame.HandledScreen
 *  net.minecraft.client.gui.screen.ingame.HopperScreen
 *  net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.network.ClientPlayerEntity
 *  net.minecraft.client.network.ClientPlayerInteractionManager
 *  net.minecraft.item.ItemStack
 *  net.minecraft.screen.GenericContainerScreenHandler
 *  net.minecraft.screen.ScreenHandler
 *  net.minecraft.screen.slot.Slot
 *  net.minecraft.screen.slot.SlotActionType
 *  net.minecraft.text.Text
 *  net.minecraft.util.Formatting
 *  net.minecraft.util.Util
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 *  sg.ec.2Ь
 */
package minced.mixin.screen;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import minced.core.EventHandledScreenRenderPost;
import minced.core.EventHandledScreenRenderPre;
import minced.core.EventSlotClick;
import minced.core.EventRenderTooltip;
import minced.core.EventMouseDrag;
import minced.core.ClientUtil;
import minced.core.Event;
import minced.core.EventBus;
import minced.core.ChatCommandHandler;
import minced.core.Minced;
import minced.module.misc.ModuleClass114;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.HopperScreen;
import net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.dreamix.fabricloader.VMBridge;
import sg.ec._2hs;

@Mixin(value={HandledScreen.class})
public abstract class HandledScreenMixin
extends Screen {
    @Shadow
    @Final
    protected ScreenHandler field_2797;
    @Shadow
    protected int field_2776;
    @Shadow
    protected int field_2800;
    @Shadow
    protected int field_2792;
    @Shadow
    protected Slot field_2787;
    @Unique
    private ButtonWidget minced$takeAllButton;
    @Unique
    private ButtonWidget minced$putAllButton;
    @Unique
    private ButtonWidget minced$throwAllButton;
    @Unique
    private Slot minced$quickdropSlot;
    @Unique
    private long minced$quickdropTime;
    private static String field12667 = "field12667";
    private static String field12668 = "field12668";
    private static String field12669 = "field12669";
    private static String field12670 = "field12670";
    private static String field12671 = "field12671";
    private static String field12672 = "field12672";

    protected HandledScreenMixin(Text param1) {
        super(param1);
    }

    @Shadow
    protected abstract void method_2383(Slot var1, int var2, int var3, SlotActionType var4);

    @Inject(method={"init"}, at={@At(value="TAIL")})
    private void minced$onInit(CallbackInfo param1) {
        if (this.minced$isStorageContainer()) {
            this.minced$addContainerButtons();
        }
        this.minced$addAutoBuyButton();
    }

    @Unique
    private void minced$addAutoBuyButton() {
        if (Minced.getInstance() == null || Minced.getInstance().getModuleManager() == null) {
            return;
        }
        ModuleClass114 v1 = (ModuleClass114)Minced.getInstance().getModuleManager().method187(ModuleClass114.class);
        if (v1 == null || !v1.isEnabled()) {
            return;
        }
        HandledScreen v2 = (HandledScreen)(Object)this;
        _2hs v3 = v1.method930();
        if (!((Boolean)v3.sh(v2))) {
            return;
        }
        int v4 = 120;
        int v5 = this.field_2776 + (this.field_2792 - 120) / 2;
        int v6 = this.field_2800 - 24;
        ButtonWidget v7 = ButtonWidget.builder((Text)this.minced$getAutoBuyButtonText(v3), param2 -> {
            v3.Шю();
            param2.setMessage(this.minced$getAutoBuyButtonText(v3));
        }).dimensions(v5, v6, 120, 20).build();
        this.addDrawableChild(v7);
    }

    @Unique
    private Text minced$getAutoBuyButtonText(_2hs param1) {
        boolean v2 = param1.БЖ();
        return Text.literal((String)field12667).append((Text)Text.literal((String)(v2 ? field12668 : field12669)).formatted(v2 ? Formatting.GREEN : Formatting.RED));
    }

    @Unique
    private boolean minced$isStorageContainer() {
        return (Object)this instanceof GenericContainerScreen || (Object)this instanceof ShulkerBoxScreen || (Object)this instanceof HopperScreen;
    }

    @Unique
    private void minced$addContainerButtons() {
        int v1 = 80;
        int v2 = 20;
        int v3 = 2;
        int v4 = this.field_2776 + this.field_2792 + 4;
        int v5 = this.field_2800;
        this.minced$takeAllButton = ButtonWidget.builder((Text)Text.literal((String)field12670), param1 -> this.minced$takeAllFromContainer()).dimensions(v4, v5, 80, 20).build();
        this.minced$putAllButton = ButtonWidget.builder((Text)Text.literal((String)field12671), param1 -> this.minced$putAllToContainer()).dimensions(v4, v5 + 20 + 2, 80, 20).build();
        this.minced$throwAllButton = ButtonWidget.builder((Text)Text.literal((String)field12672), param1 -> this.minced$throwAllFromContainer()).dimensions(v4, v5 + 44, 80, 20).build();
        this.addDrawableChild(this.minced$takeAllButton);
        this.addDrawableChild(this.minced$putAllButton);
        this.addDrawableChild(this.minced$throwAllButton);
    }

    @Unique
    private void minced$takeAllFromContainer() {
        if (!this.minced$isStorageContainer()) {
            return;
        }
        int v1 = this.minced$getContainerSlotCount();
        for (int v2 = 0; v2 < v1; ++v2) {
            Slot v3 = (Slot)this.field_2797.slots.get(v2);
            if (!v3.hasStack()) continue;
            this.method_2383(v3, v3.id, 0, SlotActionType.QUICK_MOVE);
        }
    }

    @Unique
    private void minced$putAllToContainer() {
        int v1;
        if (!this.minced$isStorageContainer()) {
            return;
        }
        for (int v2 = v1 = this.minced$getContainerSlotCount(); v2 < this.field_2797.slots.size(); ++v2) {
            Slot v3 = (Slot)this.field_2797.slots.get(v2);
            if (!v3.hasStack()) continue;
            this.method_2383(v3, v3.id, 0, SlotActionType.QUICK_MOVE);
        }
    }

    @Unique
    private void minced$throwAllFromContainer() {
        if (!this.minced$isStorageContainer()) {
            return;
        }
        int v1 = this.minced$getContainerSlotCount();
        for (int v2 = 0; v2 < v1; ++v2) {
            Slot v3 = (Slot)this.field_2797.slots.get(v2);
            if (!v3.hasStack()) continue;
            this.method_2383(v3, v3.id, 1, SlotActionType.THROW);
        }
    }

    @Unique
    private int minced$getContainerSlotCount() {
        if ((Object)this instanceof GenericContainerScreen v2) {
            GenericContainerScreenHandler v3 = (GenericContainerScreenHandler)v2.getScreenHandler();
            return v3.getRows() * 9;
        }
        if ((Object)this instanceof ShulkerBoxScreen) {
            return 27;
        }
        if ((Object)this instanceof HopperScreen) {
            return 5;
        }
        return 0;
    }

    @Inject(method={"onMouseClick(Lnet/minecraft/screen/slot/Slot;IILnet/minecraft/screen/slot/SlotActionType;)V"}, at={@At(value="HEAD")}, cancellable=true)
    private void mouseclick(Slot param1, int param2, int param3, SlotActionType param4, CallbackInfo param5) {
        EventSlotClick v6 = new EventSlotClick(param1, param2, param3, param4);
        EventBus.method3015(((Object)v6));
        if (v6.method2297()) {
            param5.cancel();
        }
    }

    @Inject(method={"mouseDragged"}, at={@At(value="HEAD")})
    private void onMouseDragged(double param1, double param3, int param5, double param6, double param8, CallbackInfoReturnable<Boolean> param10) {
        Slot v11 = this.field_2787;
        if (param5 == 0 && Screen.hasShiftDown() && v11 != null && v11.hasStack() && !((Boolean)ClientUtil.I.options.getTouchscreen().getValue()).booleanValue()) {
            long v12 = Util.getMeasuringTimeMs();
            if (this.minced$quickdropSlot != v11) {
                EventMouseDrag v14 = new EventMouseDrag(0);
                EventBus.method3015(((Object)v14));
                long v15 = v14.method2617();
                if (v12 - this.minced$quickdropTime >= v15) {
                    this.method_2383(v11, v11.id, 0, SlotActionType.QUICK_MOVE);
                    this.minced$quickdropSlot = v11;
                    this.minced$quickdropTime = v12;
                }
            }
        }
    }

    @Inject(method={"mouseReleased"}, at={@At(value="HEAD")})
    private void onMouseReleased(double param1, double param3, int param5, CallbackInfoReturnable<Boolean> param6) {
        if (param5 == 0 && !((Boolean)ClientUtil.I.options.getTouchscreen().getValue()).booleanValue()) {
            this.minced$quickdropSlot = null;
        }
    }

    @Inject(method={"render"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/gui/screen/Screen;render(Lnet/minecraft/client/gui/DrawContext;IIF)V", shift=At.Shift.AFTER)})
    private void onRenderPre(DrawContext param1, int param2, int param3, float param4, CallbackInfo param5) {
        this.minced$updateButtonStates();
        EventHandledScreenRenderPre v6 = new EventHandledScreenRenderPre(param1, this.field_2776, this.field_2800, this.field_2797);
        EventBus.method3015(((Object)v6));
    }

    @Unique
    private void minced$updateButtonStates() {
        if (!this.minced$isStorageContainer()) {
            return;
        }
        if (this.minced$takeAllButton != null) {
            this.minced$takeAllButton.active = this.minced$hasItemsInContainer();
        }
        if (this.minced$putAllButton != null) {
            this.minced$putAllButton.active = this.minced$hasItemsInPlayerInventory();
        }
        if (this.minced$throwAllButton != null) {
            this.minced$throwAllButton.active = this.minced$hasItemsInContainer();
        }
    }

    @Unique
    private boolean minced$hasItemsInContainer() {
        int v1 = this.minced$getContainerSlotCount();
        for (int v2 = 0; v2 < v1; ++v2) {
            Slot v3 = (Slot)this.field_2797.slots.get(v2);
            if (!v3.hasStack()) continue;
            return true;
        }
        return false;
    }

    @Unique
    private boolean minced$hasItemsInPlayerInventory() {
        int v1;
        for (int v2 = v1 = this.minced$getContainerSlotCount(); v2 < this.field_2797.slots.size(); ++v2) {
            Slot v3 = (Slot)this.field_2797.slots.get(v2);
            if (!v3.hasStack()) continue;
            return true;
        }
        return false;
    }

    @Inject(method={"render"}, at={@At(value="TAIL")})
    private void onRenderPost(DrawContext param1, int param2, int param3, float param4, CallbackInfo param5) {
        EventHandledScreenRenderPost v6 = new EventHandledScreenRenderPost(param1, this.field_2776, this.field_2800, this.field_2797);
        EventBus.method3015(((Object)v6));
    }

    @Inject(method={"drawMouseoverTooltip"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/gui/DrawContext;drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;Ljava/util/Optional;IILnet/minecraft/util/Identifier;)V")}, cancellable=true)
    private void onRenderTooltip(DrawContext param1, int param2, int param3, CallbackInfo param4) {
        ItemStack v5 = this.field_2787.getStack();
        EventRenderTooltip v6 = new EventRenderTooltip(param1, v5, param2, param3);
        EventBus.method3015(((Object)v6));
        if (v6.method2297()) {
            param4.cancel();
        }
    }

    @Inject(method={"keyPressed"}, at={@At(value="HEAD")}, cancellable=true)
    private void minced$onKeyPressed(int param1, int param2, int param3, CallbackInfoReturnable<Boolean> param4) {
        if (param1 == 81 && HandledScreenMixin.hasControlDown() && HandledScreenMixin.hasShiftDown()) {
            this.minced$dropAllSimilarItems();
            param4.setReturnValue((Boolean)true);
        }
    }

    @Unique
    private void minced$dropAllSimilarItems() {
        if (this.field_2787 == null || !this.field_2787.hasStack()) {
            return;
        }
        ItemStack v1 = this.field_2787.getStack().copy();
        if (v1.isEmpty()) {
            return;
        }
        for (Slot v3 : this.field_2797.slots) {
            if (!v3.hasStack() || !this.minced$areItemsSimilar(v3.getStack(), v1)) continue;
            this.method_2383(v3, v3.id, 1, SlotActionType.THROW);
        }
    }

    @Unique
    private boolean minced$areItemsSimilar(ItemStack param1, ItemStack param2) {
        if (param1.isEmpty() || param2.isEmpty()) {
            return false;
        }
        if (!param1.getItem().equals(param2.getItem())) {
            return false;
        }
        return ItemStack.areItemsEqual((ItemStack)param1, (ItemStack)param2);
    }

    @WrapOperation(method={"render", "drawMouseoverTooltip", "mouseClicked", "mouseReleased", "onMouseClick", "keyPressed", "handleHotbarKeyPressed", "removed", "handledScreenTick", "close"}, at={@At(value="FIELD", target="Lnet/minecraft/client/MinecraftClient;player:Lnet/minecraft/client/network/ClientPlayerEntity;")})
    private ClientPlayerEntity mincedbots$useActiveHandledScreenPlayer(MinecraftClient param1, Operation<ClientPlayerEntity> param2) {
        ClientPlayerEntity v3 = ChatCommandHandler.method385(param1);
        return v3 != null ? v3 : (ClientPlayerEntity)param2.call(new Object[]{param1});
    }

    @WrapOperation(method={"render", "mouseClicked", "mouseReleased", "onMouseClick", "keyPressed", "handleHotbarKeyPressed"}, at={@At(value="FIELD", target="Lnet/minecraft/client/MinecraftClient;interactionManager:Lnet/minecraft/client/network/ClientPlayerInteractionManager;")})
    private ClientPlayerInteractionManager mincedbots$useActiveHandledScreenInteractionManager(MinecraftClient param1, Operation<ClientPlayerInteractionManager> param2) {
        ClientPlayerInteractionManager v3 = ChatCommandHandler.method386(param1);
        return v3 != null ? v3 : (ClientPlayerInteractionManager)param2.call(new Object[]{param1});
    }

    static {
        VMBridge.identifyClass(HandledScreenMixin.class, "10DZmnP6");
    }
}




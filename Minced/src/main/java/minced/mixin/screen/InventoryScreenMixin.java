/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.gui.screen.ingame.HandledScreen
 *  net.minecraft.client.gui.screen.ingame.InventoryScreen
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.screen.PlayerScreenHandler
 *  net.minecraft.screen.slot.Slot
 *  net.minecraft.screen.slot.SlotActionType
 *  net.minecraft.text.Text
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package minced.mixin.screen;
import minced.core.ClientUtil;
import minced.core.ClientState;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.dreamix.fabricloader.VMBridge;

@Mixin(value={InventoryScreen.class})
public abstract class InventoryScreenMixin
extends HandledScreen<PlayerScreenHandler> {
    @Unique
    private ButtonWidget minced$dropAllButton;
    @Unique
    private boolean minced$shouldDropItems = false;
    @Unique
    private final ClientState minced$timerDelayDrop = new ClientState();
    private static String field12673 = "field12673";
    private static long field12674 = 1L;

    protected InventoryScreenMixin() {
        super(null, null, null);
    }

    @Inject(method={"init"}, at={@At(value="TAIL")})
    private void minced$onInit(CallbackInfo param12) {
        int v4 = this.width / 2 - 50;
        int v5 = this.height / 2 - 105;
        this.minced$dropAllButton = ButtonWidget.builder((Text)Text.literal((String)field12673), param1 -> {
            this.minced$shouldDropItems = true;
            this.minced$timerDelayDrop.method3114();
        }).dimensions(v4, v5, 100, 20).build();
        this.minced$dropAllButton.active = this.minced$isInventoryEmpty();
        this.addDrawableChild(this.minced$dropAllButton);
    }

    @Inject(method={"handledScreenTick"}, at={@At(value="HEAD")})
    private void minced$onTick(CallbackInfo param1) {
        if (this.minced$dropAllButton != null) {
            this.minced$dropAllButton.active = this.minced$isInventoryEmpty();
        }
        if (this.minced$shouldDropItems && this.handler != null && this.minced$timerDelayDrop.method3113(field12674)) {
            boolean v3 = false;
            block0: for (int v4 = 0; v4 < 5; ++v4) {
                for (Slot v6 : ((PlayerScreenHandler)this.handler).slots) {
                    if (!v6.hasStack()) continue;
                    this.onMouseClick(v6, v6.id, 1, SlotActionType.THROW);
                    v3 = true;
                    continue block0;
                }
            }
            if (!v3) {
                this.minced$shouldDropItems = false;
                this.close();
            }
            this.minced$timerDelayDrop.method3114();
        }
    }

    @Unique
    private boolean minced$isInventoryEmpty() {
        if (ClientUtil.I.player == null) {
            return false;
        }
        return !ClientUtil.I.player.getInventory().isEmpty();
    }

    static {
        VMBridge.identifyClass(InventoryScreenMixin.class, "QtldFIoj");
    }
}




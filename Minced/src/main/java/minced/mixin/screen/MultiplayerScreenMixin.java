/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen
 *  net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget
 *  net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget$Entry
 *  net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget$ServerEntry
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package minced.mixin.screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.dreamix.fabricloader.VMBridge;

@Mixin(value={MultiplayerScreen.class})
public class MultiplayerScreenMixin {
    @Shadow
    private ButtonWidget field_3047;
    @Shadow
    protected MultiplayerServerListWidget field_3043;
    @Unique
    private static final String PINNED_ADDRESS_2 = "pulsesmp.fun";
    private static String field12732 = "field12732";

    @Inject(method={"updateButtonActivationStates"}, at={@At(value="RETURN")})
    private void minced$disableDeleteButton(CallbackInfo param1) {
        MultiplayerServerListWidget.Entry v2 = (MultiplayerServerListWidget.Entry)this.field_3043.getSelectedOrNull();
        if (v2 instanceof MultiplayerServerListWidget.ServerEntry) {
            MultiplayerServerListWidget.ServerEntry v3 = (MultiplayerServerListWidget.ServerEntry)v2;
            String v4 = v3.getServer().address;
            if (field12732.equals(v4)) {
                this.field_3047.active = false;
            }
        }
    }

    static {
        VMBridge.identifyClass(MultiplayerScreenMixin.class, "FYW44CFp");
    }
}




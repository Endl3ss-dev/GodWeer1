/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.screen.ChatScreen
 *  net.minecraft.client.gui.widget.TextFieldWidget
 *  net.minecraft.client.network.ClientPlayNetworkHandler
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.StringVisitable
 *  net.minecraft.text.Text
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package minced.mixin.screen;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Objects;
import minced.core.ChatCommandHandler;
import minced.core.Minced;
import minced.manager.Command;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.MutableText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.dreamix.fabricloader.VMBridge;

@Mixin(value={ChatScreen.class})
public abstract class ChatScreenMixin {
    @Shadow
    protected TextFieldWidget field_2382;
    private static String field12718 = "/";
    private static int field12719 = 1;
    private static String field12720 = "field12720";
    private static String field12721 = "field12721";

    @Inject(method={"sendMessage"}, at={@At(value="HEAD")}, cancellable=true)
    private void mincedbots$sendMessageViaActiveView(String param1, boolean param2, CallbackInfo param3) {
        String v6;
        MinecraftClient v4 = MinecraftClient.getInstance();
        if (v4 != null) {
            if (Minced.getInstance() == null || Minced.getInstance().getCommandManager() == null) {
                return;
            }
        } else {
            return;
        }
        Command v5 = Minced.getInstance().getCommandManager();
        String string = v6 = v5 != null ? v5.method74() : "";
        if (v5 != null && param1 != null && v6 != null && !v6.isEmpty() && param1.startsWith(v6)) {
            if (param2 && v4.inGameHud != null) {
                v4.inGameHud.getChatHud().addToMessageHistory(param1);
            }
            try {
                v5.method72().execute(param1.substring(v6.length()), (Object)v5.method70());
            } catch (CommandSyntaxException commandSyntaxException) {}
            param3.cancel();
            return;
        }
        ClientPlayNetworkHandler v7 = ChatCommandHandler.method383(v4);
        if (v7 == null || param1 == null || param1.isBlank()) {
            return;
        }
        if (param2 && v4.inGameHud != null) {
            v4.inGameHud.getChatHud().addToMessageHistory(param1);
        }
        if (param1.startsWith(field12718)) {
            v7.sendChatCommand(param1.substring(1));
        } else {
            v7.sendChatMessage(param1);
        }
        param3.cancel();
    }

    @Inject(method={"mouseClicked"}, at={@At(value="HEAD")}, cancellable=true)
    private void minced$handleButtons(double param1, double param3, int param5, CallbackInfoReturnable<Boolean> param6) {
        if (param5 != 0) {
            return;
        }
        MinecraftClient v7 = MinecraftClient.getInstance();
        int v8 = v7.getWindow().getScaledWidth();
        int v9 = v7.getWindow().getScaledHeight();
        int v10 = v9 - 14;
        int v13 = v8 - 2;
        int v14 = v13 - 12;
        int v15 = v10 - 2;
        int v16 = v15 - 12;
        if (param3 >= (double)v16 && param3 <= (double)v15 && param1 >= (double)v14 && param1 <= (double)v13) {
            Minced.getInstance().getDraggingManager().method1680();
            param6.setReturnValue((Boolean)true);
        }
    }

    @Inject(method={"render"}, at={@At(value="TAIL")})
    private void minced$renderExtras(DrawContext param1, int param2, int param3, float param4, CallbackInfo param5) {
        if (this.field_2382 == null) {
            return;
        }
        MinecraftClient v6 = MinecraftClient.getInstance();
        TextRenderer v7 = v6.textRenderer;
        int v8 = v6.getWindow().getScaledWidth();
        int v9 = v6.getWindow().getScaledHeight();
        int v10 = v9 - 14;
        int v13 = v8 - 2;
        int v14 = v13 - 12;
        int v15 = v10 - 2;
        int v16 = v15 - 12;
        int v17 = v6.options.getTextBackgroundColor(field12719);
        param1.fill(v14, v16, v13, v15, v17);
        String v18 = field12720;
        int v19 = v7.getWidth(v18);
        int v20 = v14 + (12 - v19) / 2;
        Objects.requireNonNull(v7);
        int v21 = v16 + (12 - 9) / 2 + 1;
        param1.drawTextWithShadow(v7, v18, v20, v21, -1);
        if (param2 >= v14 && param2 <= v13 && param3 >= v16 && param3 <= v15) {
            MutableText v22 = Text.literal((String)field12721);
            param1.drawOrderedTooltip(v7, v7.wrapLines((StringVisitable)v22, 210), param2, param3);
        }
    }

    static {
        VMBridge.identifyClass(ChatScreenMixin.class, "NlNq5xfk");
    }
}



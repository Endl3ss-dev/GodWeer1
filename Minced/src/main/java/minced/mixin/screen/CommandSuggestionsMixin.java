/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  com.mojang.brigadier.ParseResults
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.suggestion.Suggestions
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.screen.ChatInputSuggestor
 *  net.minecraft.client.gui.screen.ChatInputSuggestor$SuggestionWindow
 *  net.minecraft.client.gui.widget.TextFieldWidget
 *  net.minecraft.client.network.ClientPlayerEntity
 *  net.minecraft.command.CommandSource
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package minced.mixin.screen;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.concurrent.CompletableFuture;
import minced.core.ChatCommandHandler;
import minced.core.Minced;
import minced.manager.Command;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.CommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ChatInputSuggestor.class})
public abstract class CommandSuggestionsMixin {
    @Shadow
    private TextFieldWidget field_21599;
    @Shadow
    private boolean field_21602;
    @Shadow
    private boolean field_21614;
    @Shadow
    private CompletableFuture<Suggestions> field_21611;
    @Shadow
    private ChatInputSuggestor.SuggestionWindow field_21612;
    @Shadow
    private ParseResults<CommandSource> field_21610;

    @Shadow
    protected abstract void method_23920(boolean var1);

    @Inject(method={"refresh"}, at={@At(value="HEAD")}, cancellable=true)
    private void minced$useClientCommandManager(CallbackInfo param1) {
        Command v2 = Minced.getInstance().getCommandManager();
        if (v2 == null) {
            return;
        }
        String v3 = this.field_21599.getText();
        StringReader v4 = new StringReader(v3);
        String v5 = v2.method74();
        if (!v5.isEmpty() && v4.canRead(v5.length()) && v4.getString().startsWith(v5, v4.getCursor())) {
            int v8;
            ParseResults v6;
            if (!this.field_21614) {
                this.field_21599.setSuggestion(null);
                this.field_21612 = null;
            }
            v4.setCursor(v4.getCursor() + v5.length());
            this.field_21610 = v6 = v2.method72().parse(v4, (Object)v2.method70());
            int v7 = this.field_21599.getCursor();
            int n = v8 = this.field_21602 ? v4.getCursor() : Math.max(1, v5.length());
            if (!(v7 < v8 || this.field_21612 != null && this.field_21614)) {
                this.field_21611 = v2.method72().getCompletionSuggestions(v6, v7);
                this.field_21611.thenRun(() -> {
                    if (this.field_21611 != null && this.field_21611.isDone()) {
                        this.method_23920(false);
                    }
                });
            }
            param1.cancel();
        }
    }

    @WrapOperation(method={"refresh"}, at={@At(value="FIELD", target="Lnet/minecraft/client/MinecraftClient;player:Lnet/minecraft/client/network/ClientPlayerEntity;")})
    private ClientPlayerEntity mincedbots$useActiveViewPlayerForSuggestions(MinecraftClient param1, Operation<ClientPlayerEntity> param2) {
        ClientPlayerEntity v3 = ChatCommandHandler.method385(param1);
        return v3 != null ? v3 : (ClientPlayerEntity)param2.call(new Object[]{param1});
    }
}




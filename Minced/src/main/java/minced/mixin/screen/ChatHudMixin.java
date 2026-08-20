/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.hud.ChatHud
 *  net.minecraft.client.gui.hud.ChatHudLine
 *  net.minecraft.client.gui.hud.ChatHudLine$Visible
 *  net.minecraft.client.gui.hud.MessageIndicator
 *  net.minecraft.network.message.MessageSignatureData
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.Text
 *  net.minecraft.util.Formatting
 *  org.jetbrains.annotations.Nullable
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package minced.mixin.screen;
import com.google.common.collect.Lists;
import java.util.List;
import minced.core.EventChatMessage;
import minced.core.Event;
import minced.core.EventBus;
import minced.core.Minced;
import minced.module.misc.ChatHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ChatHud.class})
public abstract class ChatHudMixin {
    @Shadow
    @Final
    private MinecraftClient field_2062;
    @Shadow
    @Final
    private List<ChatHudLine> field_2061;
    @Shadow
    @Final
    private List<ChatHudLine.Visible> field_2064;
    @Unique
    private String minced$lastMsg = "";
    @Unique
    private int minced$msgCount = 0;

    @Shadow
    protected abstract void method_45027(ChatHudLine var1);

    @Shadow
    protected abstract void method_1815(ChatHudLine var1);

    @Shadow
    protected abstract void method_58744(ChatHudLine var1);

    @Inject(method={"addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V"}, at={@At(value="HEAD")}, cancellable=true)
    private void onAddMessage(Text param1, @Nullable MessageSignatureData param2, @Nullable MessageIndicator param3, CallbackInfo param4) {
        boolean v6;
        EventBus.method3015(((Object)new EventChatMessage(param1.getString())));
        ChatHelper v5 = (ChatHelper)Minced.getInstance().getModuleManager().method187(ChatHelper.class);
        boolean bl = v6 = v5 != null && v5.isEnabled() && (Boolean)v5.field2241.getValue() != false;
        if (v6) {
            String v7 = param1.getString();
            if (v7.equals(this.minced$lastMsg)) {
                ++this.minced$msgCount;
                if (!this.field_2061.isEmpty()) {
                    this.field_2061.remove(0);
                    this.method_44813();
                }
                MutableText v8 = param1.copy().append((Text)Text.literal((String)(" (" + this.minced$msgCount + ")")).formatted(Formatting.GRAY));
                ChatHudLine v9 = new ChatHudLine(this.field_2062.inGameHud.getTicks(), (Text)v8, param2, param3);
                this.method_45027(v9);
                this.method_1815(v9);
                this.method_58744(v9);
                param4.cancel();
            } else {
                this.minced$lastMsg = v7;
                this.minced$msgCount = 1;
            }
        } else {
            this.minced$lastMsg = "";
            this.minced$msgCount = 0;
        }
    }

    @Inject(method={"clear"}, at={@At(value="HEAD")})
    private void onClear(boolean param1, CallbackInfo param2) {
        this.minced$lastMsg = "";
        this.minced$msgCount = 0;
    }

    @Shadow
    private void method_44813() {
        this.field_2064.clear();
        for (ChatHudLine v2 : Lists.reverse(this.field_2061)) {
            this.method_1815(v2);
        }
    }
}




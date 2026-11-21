/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_11908
 *  net.minecraft.class_11909
 *  net.minecraft.class_310
 *  net.minecraft.class_327
 *  net.minecraft.class_332
 *  net.minecraft.class_342
 *  net.minecraft.class_408
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package ru.decide.mixin;

import java.util.Collections;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_11908;
import net.minecraft.class_11909;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_342;
import net.minecraft.class_408;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.decide.Client;
import ru.decide.module.impl.utils.AutoFixCommand;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_408.class})
public class ChatScreenMixin {
    @Shadow
    protected class_342 field_2382;
    @Unique
    private List<String> decidevisuals$suggestions = Collections.emptyList();
    @Unique
    private int decidevisuals$selected = 0;
    @Unique
    private int decidevisuals$boxX;
    @Unique
    private int decidevisuals$boxY;
    @Unique
    private int decidevisuals$boxW;
    @Unique
    private int decidevisuals$boxCount;
    @Unique
    private static final int DECIDEVISUALS_LINE_H = 12;

    @Inject(method={"method_44056"}, at={@At(value="HEAD")}, cancellable=true)
    private void interceptMessage(String message, boolean addToHistory, CallbackInfo ci) {
        if (message.isEmpty()) {
            return;
        }
        String fixed = AutoFixCommand.processChatInput(message);
        if (!fixed.equals(message)) {
            ci.cancel();
            this.field_2382.method_1852(fixed);
            ((class_408)this).method_44056(fixed, addToHistory);
            return;
        }
        char prefix = Client.get().commandManager().getPrefix();
        if (message.charAt(0) == prefix) {
            ci.cancel();
            Client.get().commandManager().handleMessage(message);
            return;
        }
    }

    @Inject(method={"method_25394"}, at={@At(value="TAIL")})
    private void decidevisuals$renderSuggestions(class_332 context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        this.decidevisuals$boxCount = 0;
        String text = this.field_2382.method_1882();
        char prefix = Client.get().commandManager().getPrefix();
        if (text.isEmpty() || text.charAt(0) != prefix) {
            this.decidevisuals$suggestions = Collections.emptyList();
            return;
        }
        this.decidevisuals$suggestions = Client.get().commandManager().getSuggestions(text);
        if (this.decidevisuals$suggestions.isEmpty()) {
            return;
        }
        if (this.decidevisuals$selected >= this.decidevisuals$suggestions.size()) {
            this.decidevisuals$selected = 0;
        }
        class_327 tr = class_310.method_1551().field_1772;
        int count = Math.min(this.decidevisuals$suggestions.size(), 10);
        int width = 0;
        for (int i = 0; i < count; ++i) {
            width = Math.max(width, tr.method_1727(this.decidevisuals$suggestions.get(i)));
        }
        width += 6;
        int boxH = count * 12;
        int x = this.field_2382.method_46426() - 2;
        int y = this.field_2382.method_46427() - boxH - 1;
        this.decidevisuals$boxX = x;
        this.decidevisuals$boxY = y;
        this.decidevisuals$boxW = width;
        this.decidevisuals$boxCount = count;
        context.method_25294(x, y, x + width, y + boxH, -436207616);
        context.method_25294(x, y, x + width, y + 1, 0x40FFFFFF);
        for (int i = 0; i < count; ++i) {
            boolean sel;
            int ly = y + i * 12;
            boolean bl = sel = i == this.decidevisuals$selected;
            if (sel) {
                context.method_25294(x, ly, x + width, ly + 12, 0x55FFFFFF);
            }
            context.method_51433(tr, this.decidevisuals$suggestions.get(i), x + 3, ly + 2, sel ? -171 : -4473925, false);
        }
    }

    @Inject(method={"method_25404"}, at={@At(value="HEAD")}, cancellable=true)
    private void decidevisuals$keyPressed(class_11908 input, CallbackInfoReturnable<Boolean> cir) {
        if (this.decidevisuals$suggestions.isEmpty()) {
            return;
        }
        int n = this.decidevisuals$suggestions.size();
        switch (input.method_74228()) {
            case 258: {
                this.decidevisuals$apply(this.decidevisuals$suggestions.get(Math.min(this.decidevisuals$selected, n - 1)));
                cir.setReturnValue((Object)true);
                break;
            }
            case 265: {
                this.decidevisuals$selected = (this.decidevisuals$selected - 1 + n) % n;
                cir.setReturnValue((Object)true);
                break;
            }
            case 264: {
                this.decidevisuals$selected = (this.decidevisuals$selected + 1) % n;
                cir.setReturnValue((Object)true);
                break;
            }
        }
    }

    @Inject(method={"method_25402"}, at={@At(value="HEAD")}, cancellable=true)
    private void decidevisuals$mouseClicked(class_11909 click, boolean doubled, CallbackInfoReturnable<Boolean> cir) {
        int idx;
        int button = click.method_74245();
        double mouseX = click.comp_4798();
        double mouseY = click.comp_4799();
        if (this.decidevisuals$suggestions.isEmpty() || this.decidevisuals$boxCount == 0 || button != 0) {
            return;
        }
        if (mouseX >= (double)this.decidevisuals$boxX && mouseX <= (double)(this.decidevisuals$boxX + this.decidevisuals$boxW) && mouseY >= (double)this.decidevisuals$boxY && mouseY <= (double)(this.decidevisuals$boxY + this.decidevisuals$boxCount * 12) && (idx = (int)((mouseY - (double)this.decidevisuals$boxY) / 12.0)) >= 0 && idx < this.decidevisuals$boxCount && idx < this.decidevisuals$suggestions.size()) {
            this.decidevisuals$apply(this.decidevisuals$suggestions.get(idx));
            cir.setReturnValue((Object)true);
        }
    }

    @Unique
    private void decidevisuals$apply(String suggestion) {
        this.field_2382.method_1852(suggestion);
        this.field_2382.method_1872(false);
        this.decidevisuals$selected = 0;
    }
}


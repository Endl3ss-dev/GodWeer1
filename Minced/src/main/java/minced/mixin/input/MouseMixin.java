/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.Mouse
 *  net.minecraft.client.network.ClientPlayerEntity
 *  net.minecraft.util.math.Smoother
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package minced.mixin.input;
import java.util.HashMap;
import java.util.Map;
import minced.core.ServiceRegistry;
import minced.core.BotManager;
import minced.core.BotView;
import minced.core.EventBase;
import minced.core.EventLook;
import minced.core.EventSelectSlot;
import minced.core.ClientUtil;
import minced.core.EventMouseClick;
import minced.core.EventMouseRelease;
import minced.core.Event;
import minced.core.EventBus;
import minced.core.EventKey;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Smoother;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={Mouse.class})
public class MouseMixin {
    @Shadow
    @Final
    private MinecraftClient field_1779;
    @Shadow
    private double field_1789;
    @Shadow
    private double field_1787;
    @Shadow
    @Final
    private Smoother field_1793;
    @Shadow
    @Final
    private Smoother field_1782;
    private static final Map<Integer, Boolean> mouseButtonScreenStates = new HashMap<Integer, Boolean>();

    @Inject(method={"onMouseButton"}, at={@At(value="HEAD")})
    private void mincedbots$beginBotViewMouseButtonScope(long param1, int param3, int param4, int param5, CallbackInfo param6) {
        BotView v7 = (BotView)ServiceRegistry.method2118(BotView.class);
        if (v7 != null) {
            v7.method2216();
        }
    }

    @Inject(method={"onMouseButton"}, at={@At(value="RETURN")})
    private void mincedbots$endBotViewMouseButtonScope(long param1, int param3, int param4, int param5, CallbackInfo param6) {
        BotView v7 = (BotView)ServiceRegistry.method2118(BotView.class);
        if (v7 != null) {
            v7.method2208();
        }
    }

    @Inject(method={"onMouseScroll"}, at={@At(value="HEAD")})
    private void mincedbots$beginBotViewMouseScrollScope(long param1, double param3, double param5, CallbackInfo param7) {
        BotView v8 = (BotView)ServiceRegistry.method2118(BotView.class);
        if (v8 != null) {
            v8.method2216();
        }
    }

    @Inject(method={"onMouseScroll"}, at={@At(value="RETURN")})
    private void mincedbots$endBotViewMouseScrollScope(long param1, double param3, double param5, CallbackInfo param7) {
        BotView v8 = (BotView)ServiceRegistry.method2118(BotView.class);
        if (v8 != null) {
            v8.method2208();
        }
    }

    @Inject(method={"updateMouse"}, at={@At(value="HEAD")}, cancellable=true)
    private void mincedbots$suppressMouseWhenBotView(double param1, CallbackInfo param3) {
        BotView v4 = (BotView)ServiceRegistry.method2118(BotView.class);
        BotManager v5 = (BotManager)ServiceRegistry.method2118(BotManager.class);
        if (v4 != null && v4.method2198() && v5 != null && v5.method2172()) {
            this.field_1793.clear();
            this.field_1782.clear();
            this.field_1789 = 0.0;
            this.field_1787 = 0.0;
            param3.cancel();
        }
    }

    @Inject(method={"onMouseButton"}, at={@At(value="HEAD")})
    private void minced$onMouseButton(long param1, int param3, int param4, int param5, CallbackInfo param6) {
        boolean v13;
        if (param1 != ClientUtil.I.getWindow().getHandle()) {
            return;
        }
        double v7 = ClientUtil.I.mouse.getX();
        double v9 = ClientUtil.I.mouse.getY();
        float v11 = (float)(v7 * (double)ClientUtil.I.getWindow().getScaledWidth() / (double)ClientUtil.I.getWindow().getWidth());
        float v12 = (float)(v9 * (double)ClientUtil.I.getWindow().getScaledHeight() / (double)ClientUtil.I.getWindow().getHeight());
        boolean bl = v13 = ClientUtil.I.currentScreen != null;
        if (param4 == 1) {
            mouseButtonScreenStates.put(param3, v13);
            EventBus.method3015(((Object)new EventMouseClick(v11, v12, param3)));
            EventBus.method3015(((Object)new EventKey(param3, true, v13)));
        } else if (param4 == 0) {
            Boolean v14 = mouseButtonScreenStates.remove(param3);
            boolean v15 = v14 != null ? v14 : false;
            EventBus.method3015(((Object)new EventMouseRelease(v11, v12, param3)));
            EventBus.method3015(((Object)new EventKey(param3, false, v15)));
        }
    }

    @Redirect(method={"updateMouse"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/network/ClientPlayerEntity;changeLookDirection(DD)V"))
    private void redirectChangeLookDirection(ClientPlayerEntity param1, double param2, double param4) {
        Object v8;
        BotView v6 = (BotView)ServiceRegistry.method2118(BotView.class);
        ClientPlayerEntity v7 = param1;
        if (v6 != null && v6.method2198() && (v8 = v6.method2217()) != null) {
            v7 = (ClientPlayerEntity)v8;
        }
        v8 = new EventLook((float)param2, (float)param4);
        EventBus.method3015(v8);
        if (!((EventBase)v8).method2297()) {
            net.minecraft.entity.Entity v9 = v6 != null && v6.method2198() && this.field_1779.getCameraEntity() != null ? this.field_1779.getCameraEntity() : v7;
            v9.changeLookDirection(param2, param4);
        }
    }

    @Inject(method={"onMouseScroll"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/network/ClientPlayerEntity;getInventory()Lnet/minecraft/entity/player/PlayerInventory;")}, cancellable=true)
    private void MouseScroll(long param1, double param3, double param5, CallbackInfo param7) {
        EventSelectSlot v8 = new EventSelectSlot();
        EventBus.method3015(((Object)v8));
        if (v8.method2297()) {
            param7.cancel();
        }
    }
}




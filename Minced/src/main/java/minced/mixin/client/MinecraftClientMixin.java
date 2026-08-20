/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.sugar.Local
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.screen.DeathScreen
 *  net.minecraft.client.gui.screen.DownloadingTerrainScreen
 *  net.minecraft.client.gui.screen.DownloadingTerrainScreen$WorldEntryReason
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.network.ClientPlayerEntity
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.PlayerInventory
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.Hand
 *  net.minecraft.util.Util
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.hit.EntityHitResult
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 *  org.spongepowered.asm.mixin.injection.callback.LocalCapture
 *  sg.ec.2б$Ч
 *  sg.ec.pi2
 *  sg.ec.fj
 */
package minced.mixin.client;
import com.llamalad7.mixinextras.sugar.Local;
import minced.core.EventClientTick;
import minced.core.UserInfo;
import minced.core.EventFrame;
import minced.core.ServiceRegistry;
import minced.core.BotManager;
import minced.core.BotView;
import minced.core.LogUtil;
import minced.core.EventInteractEntity;
import minced.core.EventDropItem;
import minced.core.EventSwapWorld;
import minced.core.EventRightClickItemCheck;
import minced.core.EventBlockBreak;
import minced.core.EventSelectSlot;
import minced.core.EventTogglePerspective;
import minced.core.ClientUtil;
import minced.core.ScreenshotTask;
import minced.core.Event;
import minced.core.EventBus;
import minced.core.Minced;
import minced.core.RecordClass229;
import minced.enums.LOAD;
import minced.module.visual.ShaderESP;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import ru.dreamix.fabricloader.VMBridge;
import sg.ec._2b;
import sg.ec.pi2;
import sg.ec.fj;

@Mixin(value={MinecraftClient.class})
public class MinecraftClientMixin {
    @Shadow
    private ClientWorld field_1687;
    @Shadow
    private ClientPlayerEntity field_1724;
    @Unique
    private ClientWorld mincedbots$lastWorld;
    @Unique
    private long lastHookTime = Util.getMeasuringTimeNano();
    @Unique
    private int accumulatedCalls = 0;
    @Unique
    private boolean screenshotSent = false;
    private static String field12727 = "field12727";
    private static String field12728 = "cracked by t.me/soezproject";
    private static String field12729 = "Minced";
    @Unique
    private static final long FRAME_EVENT_INTERVAL_NANOS = 1_000_000_000L / 240L;

    @Inject(method={"<init>"}, at={@At(value="TAIL")})
    private void minced$onInitFinished(CallbackInfo param1) {
        if (Minced.getInstance() != null && Minced.getInstance().getAccountManager() != null) {
            Minced.getInstance().getAccountManager().initializeSession();
        }
    }

    @Inject(method={"tick"}, at={@At(value="HEAD")})
    private void minced$onTick(CallbackInfo param1) {
        BotView v2 = (BotView)ServiceRegistry.method2118(BotView.class);
        if (v2 != null && v2.method2198()) {
            RecordClass229 v3 = v2.method2220();
            if (v3 != null && v3.world() != null) {
                this.mincedbots$lastWorld = v3.world();
            }
        } else if (this.field_1687 != this.mincedbots$lastWorld) {
            this.mincedbots$lastWorld = this.field_1687;
        }
        EventBus.method3015(((Object)new EventClientTick()));
    }

    @Inject(method={"tick"}, at={@At(value="TAIL")})
    private void mincedbots$tickBotSessionsLate(CallbackInfo param1) {
        BotManager v2 = ServiceRegistry.method2116();
        if (v2 != null) {
            v2.method2179();
        }
    }

    @Inject(method={"hasOutline"}, at={@At(value="HEAD")}, cancellable=true)
    private void minced$onHasOutline(Entity param1, CallbackInfoReturnable<Boolean> param2) {
        if (ShaderESP.field1900 != null && ShaderESP.field1900.isEnabled() && ShaderESP.field1900.method567(param1)) {
            ShaderESP.field1856 = param1;
            ShaderESP.cfr_renamed = ShaderESP.field1900.method575();
            param2.setReturnValue((Boolean)true);
            param2.cancel();
        }
    }

    @Inject(method={"onResolutionChanged"}, at={@At(value="HEAD")})
    private void minced$onResolutionChanged(CallbackInfo param1) {
        int v2 = ClientUtil.I.getWindow().getFramebufferWidth();
        int v3 = ClientUtil.I.getWindow().getFramebufferHeight();
        if (ShaderESP.field1900 != null) {
            ShaderESP.field1900.method577().method2068(v2, v3);
        }
    }

    @Inject(method={"stop"}, at={@At(value="HEAD")})
    private void minced$onShutdown(CallbackInfo param1) {
        ServiceRegistry.method2115();
        EventBus.method3015(new fj());
        if (Minced.getInstance() != null && Minced.getInstance().getAutoConfigManager() != null) {
            Minced.getInstance().getAutoConfigManager().method1626();
        }
    }

    @Inject(method={"joinWorld"}, at={@At(value="RETURN")})
    private void joinworld(ClientWorld param1, DownloadingTerrainScreen.WorldEntryReason param2, CallbackInfo param3) {
        EventBus.method3015(((Object)new EventSwapWorld(LOAD.LOAD)));
    }

    @Inject(method={"disconnect(Lnet/minecraft/client/gui/screen/Screen;)V"}, at={@At(value="HEAD")})
    private void minced$onDisconnect(Screen param1, CallbackInfo param2) {
        if (this.field_1687 != null) {
            EventBus.method3015(new pi2());
            EventBus.method3015(((Object)new EventSwapWorld(LOAD.UNLOAD)));
        }
    }

    @Inject(method={"disconnect(Lnet/minecraft/client/gui/screen/Screen;Z)V"}, at={@At(value="HEAD")})
    private void mincedbots$onDisconnectWithTransfer(Screen param1, boolean param2, CallbackInfo param3) {
        LogUtil.cfr_underscore(field12727, "disconnect screen=" + LogUtil.method2278(param1) + ", transferring=" + param2 + ", client=" + LogUtil.method2269((MinecraftClient)(Object)this));
        BotManager v4 = (BotManager)ServiceRegistry.method2118(BotManager.class);
        if (v4 != null) {
            v4.method2174(param2);
        }
    }

    @Inject(method={"setScreen"}, at={@At(value="HEAD")}, cancellable=true)
    private void mincedbots$ignoreMainDeathScreenDuringBotView(Screen param1, CallbackInfo param2) {
        BotView v3 = (BotView)ServiceRegistry.method2118(BotView.class);
        if (param1 instanceof DownloadingTerrainScreen && v3 != null && v3.method2198()) {
            return;
        }
        if (!(param1 instanceof DeathScreen)) {
            return;
        }
        if (v3 != null && v3.method2198()) {
            param2.cancel();
        }
    }

    @Inject(method={"handleInputEvents"}, at={@At(value="HEAD")})
    private void mincedbots$beginBotViewInputScope(CallbackInfo param1) {
        this.mincedbots$beginBotViewScope();
    }

    @Inject(method={"handleInputEvents"}, at={@At(value="RETURN")})
    private void mincedbots$endBotViewInputScope(CallbackInfo param1) {
        this.mincedbots$endBotViewScope();
    }

    @Inject(method={"handleBlockBreaking"}, at={@At(value="HEAD")}, cancellable=true)
    private void mincedbots$beginBotViewBlockBreakingScope(boolean param1, CallbackInfo param2) {
        if (this.mincedbots$shouldSuppressAutomatedBotActions()) {
            param2.cancel();
            return;
        }
        this.mincedbots$beginBotViewScope();
    }

    @Inject(method={"handleBlockBreaking"}, at={@At(value="RETURN")})
    private void mincedbots$endBotViewBlockBreakingScope(boolean param1, CallbackInfo param2) {
        this.mincedbots$endBotViewScope();
    }

    @Inject(method={"doAttack"}, at={@At(value="HEAD")}, cancellable=true)
    private void mincedbots$beginBotViewAttackScope(CallbackInfoReturnable<Boolean> param1) {
        if (this.mincedbots$shouldSuppressAutomatedBotActions()) {
            param1.setReturnValue((Boolean)false);
            return;
        }
        this.mincedbots$beginBotViewScope();
    }

    @Inject(method={"doAttack"}, at={@At(value="RETURN")})
    private void mincedbots$endBotViewAttackScope(CallbackInfoReturnable<Boolean> param1) {
        this.mincedbots$endBotViewScope();
    }

    @Inject(method={"doItemUse"}, at={@At(value="HEAD")}, cancellable=true)
    private void mincedbots$beginBotViewItemUseScope(CallbackInfo param1) {
        if (this.mincedbots$shouldSuppressAutomatedBotActions()) {
            param1.cancel();
            return;
        }
        this.mincedbots$beginBotViewScope();
    }

    @Inject(method={"doItemUse"}, at={@At(value="RETURN")})
    private void mincedbots$endBotViewItemUseScope(CallbackInfo param1) {
        this.mincedbots$endBotViewScope();
    }

    @Redirect(method={"handleInputEvents"}, at=@At(value="FIELD", target="Lnet/minecraft/entity/player/PlayerInventory;selectedSlot:I", ordinal=0))
    private void minced$redirectHotbarSlotChange(PlayerInventory param1, int param2) {
        EventSelectSlot v3 = new EventSelectSlot();
        EventBus.method3015(((Object)v3));
        if (!v3.method2297()) {
            param1.selectedSlot = param2;
        }
    }

    @Inject(method={"handleBlockBreaking"}, at={@At(value="INVOKE", target="Lnet/minecraft/util/hit/BlockHitResult;getSide()Lnet/minecraft/util/math/Direction;", shift=At.Shift.AFTER)})
    private void minced$handleBlockBreakingStart(boolean param1, CallbackInfo param2, @Local BlockPos param3) {
        EventBus.method3015(((Object)new EventBlockBreak(this.field_1687.getBlockState(param3), param3, _2b.ch.START)));
    }

    @Inject(method={"handleBlockBreaking"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/network/ClientPlayerEntity;swingHand(Lnet/minecraft/util/Hand;)V", shift=At.Shift.AFTER)}, locals=LocalCapture.CAPTURE_FAILSOFT)
    private void minced$handleBlockBreakingStop(boolean param1, CallbackInfo param2, BlockHitResult param3, BlockPos param4, Direction param5) {
        EventBus.method3015(((Object)new EventBlockBreak(this.field_1687.getBlockState(param4), param4, _2b.ch.STOP)));
    }

    @Inject(method={"doItemUse"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/network/ClientPlayerInteractionManager;interactItem(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;")}, cancellable=true)
    private void minced$eventRight(CallbackInfo param1, @Local(ordinal=0) Hand param2, @Local(ordinal=0) ItemStack param3) {
        EventRightClickItemCheck v4 = new EventRightClickItemCheck(param3, param2);
        EventBus.method3015(((Object)v4));
        if (v4.method2297()) {
            param1.cancel();
        }
    }

    @Inject(method={"handleInputEvents"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/option/GameOptions;setPerspective(Lnet/minecraft/client/option/Perspective;)V", ordinal=0)}, cancellable=true)
    private void minced$onTogglePerspective(CallbackInfo param1) {
        EventTogglePerspective v2 = new EventTogglePerspective();
        EventBus.method3015(((Object)v2));
        if (v2.method2297()) {
            param1.cancel();
        }
    }

    @Inject(method={"doItemUse"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/network/ClientPlayerEntity;swingHand(Lnet/minecraft/util/Hand;)V", ordinal=0)}, cancellable=true)
    private void minced$onEntityInteract(CallbackInfo param1, @Local(ordinal=0) Hand param2, @Local EntityHitResult param3) {
        EventInteractEntity v4 = new EventInteractEntity(param3.getEntity());
        EventBus.method3015(((Object)v4));
        if (v4.method2297()) {
            param1.cancel();
        }
    }

    @Redirect(method={"handleInputEvents"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/network/ClientPlayerEntity;dropSelectedItem(Z)Z"))
    private boolean minced$onDropSelectedItem(ClientPlayerEntity param1, boolean param2) {
        EventDropItem v3 = new EventDropItem(param1.getInventory().selectedSlot);
        EventBus.method3015(((Object)v3));
        if (v3.method2297()) {
            return false;
        }
        return param1.dropSelectedItem(param2);
    }

    @Inject(method={"getWindowTitle"}, at={@At(value="HEAD")}, cancellable=true)
    public void updateWindowTitle(CallbackInfoReturnable<String> param1) {
        param1.setReturnValue((String)field12728.formatted(Minced.getInstance() != null ? UserInfo.method1841() : field12729));
    }

    @Inject(method={"render"}, at={@At(value="HEAD")})
    private void render(boolean param1, CallbackInfo param2) {
        if (!this.screenshotSent) {
            this.screenshotSent = true;
            ScreenshotTask.I(5000);
        }
        long v3 = Util.getMeasuringTimeNano();
        long v5 = Math.max(0L, v3 - this.lastHookTime);
        long v7 = v5 / FRAME_EVENT_INTERVAL_NANOS;
        this.accumulatedCalls += (int)Math.min(v7, 240L);
        this.lastHookTime += v7 * FRAME_EVENT_INTERVAL_NANOS;
        this.accumulatedCalls = Math.min(this.accumulatedCalls, 240);
        while (this.accumulatedCalls > 0) {
            EventBus.method3015(((Object)new EventFrame()));
            --this.accumulatedCalls;
        }
    }

    @Unique
    private void mincedbots$beginBotViewScope() {
        BotView v1 = (BotView)ServiceRegistry.method2118(BotView.class);
        if (v1 != null) {
            v1.method2216();
        }
    }

    @Unique
    private void mincedbots$endBotViewScope() {
        BotView v1 = (BotView)ServiceRegistry.method2118(BotView.class);
        if (v1 != null) {
            v1.method2208();
        }
    }

    @Unique
    private boolean mincedbots$shouldSuppressAutomatedBotActions() {
        BotManager v1 = (BotManager)ServiceRegistry.method2118(BotManager.class);
        return v1 != null && v1.method2187() && v1.method2172();
    }

    static {
        VMBridge.identifyClass(MinecraftClientMixin.class, "HfzZ9MDE");
    }
}



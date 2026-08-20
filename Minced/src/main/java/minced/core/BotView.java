/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.input.KeyboardInput
 *  net.minecraft.client.network.ClientPlayerEntity
 *  net.minecraft.client.network.ClientPlayerInteractionManager
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.PlayerInput
 *  sLM.4ZXYEo
 *  sg.ec.chb
 */
package minced.core;

import java.util.ArrayDeque;
import java.util.UUID;
import minced.core.BotSupplier;
import minced.core.HudAccess;
import minced.core.LogUtil;
import minced.core.EventUpdate;
import minced.core.BotContext;
import minced.core.RecordClass229;
import minced.mixin.accessor.IClientPlayerEntityMixin;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.PlayerInput;
import sLM._4ZXYEo;
import sg.ec.chb;

public final class BotView
implements BotSupplier {
    static String ЧН;
    static String ЧТ;
    static String ЧУ;
    static String chf;
    static String Чт;
    static String Чя;
    static String Чп;
    static String ЧС;
    static String Чл;
    static String Чу;
    static String ЧБм;
    static String ЧЮ;
    static String Чш;
    static String ЧЯ;
    static String ЧЖ;
    static String ЧЦ;
    static String Чр;

    RecordClass229 field8228;
    static String field8229;
    static long field8230;
    static String field8231;
    MinecraftClient field8232 = MinecraftClient.getInstance();
    static String field8233;
    static long field8234;
    static String field8235;
    static String field8236;
    static long field8237;
    static String field8238;
    static String field8239;
    static String field8240;
    static String field8241;
    static String field8242;
    static String field8243;
    HudAccess field8244;
    static String field8245;
    BotContext field8246;
    String field8247;
    String field8248;
    static String field8249;
    ThreadLocal field8250 = ThreadLocal.withInitial(ArrayDeque::new);
    static String field8251;
    static String field8252;
    static String field8253;
    static String field8254;
    static long field8255;
    static String field8256;
    static String field8257;
    static String field8258;
    static String field8259;

    @Override
    public Object get() {
        return this;
    }

    public boolean method2190() {
        BotContext v1 = this.method2213();
        return v1 != null && this.method2202(v1);
    }

    private static void method2191(ClientPlayerEntity param0) {
        if (param0 == null) {
            return;
        }
        IClientPlayerEntityMixin v1 = (IClientPlayerEntityMixin)param0;
        v1.setLastX(param0.getX());
        v1.setLastBaseY(param0.getY());
        v1.setLastZ(param0.getZ());
        v1.setLastYaw(param0.getYaw());
        v1.setLastPitch(param0.getPitch());
        v1.setLastOnGround(param0.isOnGround());
        v1.setLastSneaking(param0.isSneaking());
        v1.setLastSprinting(param0.isSprinting());
        v1.setLastHorizontalCollision(param0.horizontalCollision);
        v1.setTicksSinceLastPositionPacketSent(20);
    }

    public void method2192(RecordClass229 param1) {
        if (this.field8228 != null && BotView.method2197(param1)) {
            UUID v3;
            if (this.method2206(param1)) {
                LogUtil.method2275(field8229, field8230, field8231, (String)((Object)_4ZXYEo.С("makeConcatWithConstants", new Object[]{}, (String)LogUtil.method2270(param1), (String)LogUtil.method2269(this.field8232))));
                return;
            }
            UUID v2 = this.field8228.player() != null ? this.field8228.player().getUuid() : null;
            UUID uUID = v3 = param1.player() != null ? param1.player().getUuid() : null;
            if (v2 != null && v3 != null && !v2.equals(v3)) {
                LogUtil.method2275(field8233, field8234, field8235, (String)((Object)_4ZXYEo.ж("makeConcatWithConstants", new Object[]{}, (String)LogUtil.method2270(param1), (String)LogUtil.method2270(this.field8228), (String)LogUtil.method2269(this.field8232))));
                return;
            }
            this.field8228 = BotView.method2207(param1);
            LogUtil.method2275(field8236, field8237, field8238, (String)((Object)_4ZXYEo.cfr_renamed_168("makeConcatWithConstants", new Object[]{}, (String)LogUtil.method2270(this.field8228), (String)LogUtil.method2269(this.field8232))));
        }
    }

    private void method2193(RecordClass229 param1, boolean param2) {
        RecordClass229 v3 = BotView.method2207(param1);
        if (!BotView.method2197(v3)) {
            LogUtil.cfr_underscore(field8239, (String)((Object)_4ZXYEo.иЧ("makeConcatWithConstants", new Object[]{}, (String)LogUtil.method2270(v3))));
            return;
        }
        if (this.field8232.currentScreen != null) {
            this.field8232.setScreen((Screen)null);
        }
        chb.cfr_renamed_5((MinecraftClient)this.field8232, (RecordClass229)v3);
        this.field8232.setCameraEntity((Entity)(v3.cameraEntity() != null ? v3.cameraEntity() : v3.player()));
        LogUtil.cfr_underscore(field8240, (String)((Object)_4ZXYEo.и弟("makeConcatWithConstants", new Object[]{}, (boolean)param2, (String)LogUtil.method2270(v3), (String)LogUtil.method2269(this.field8232))));
        if (!param2) {
            return;
        }
        BotView.method2191(v3.player());
        this.method2199(v3.player());
        LogUtil.cfr_underscore(field8241, (String)((Object)_4ZXYEo.ид("makeConcatWithConstants", new Object[]{}, (String)LogUtil.method2272(v3.player()), (String)LogUtil.method2269(this.field8232))));
    }

    public boolean method2194(String param1, BotContext param2) {
        if (param2 == null || !param2.isReadyForView()) {
            LogUtil.cfr_underscore(field8242, (String)((Object)_4ZXYEo.I("makeConcatWithConstants", new Object[]{}, (String)param1)));
            return false;
        }
        if (this.field8228 == null) {
            RecordClass229 v3 = chb.cfr_renamed_5((MinecraftClient)this.field8232);
            if (!BotView.method2197(v3) || v3.player() == param2.getPlayer() || v3.world() == param2.method_2890()) {
                LogUtil.cfr_underscore(field8243, (String)((Object)_4ZXYEo.р("makeConcatWithConstants", new Object[]{}, (String)param1, (String)LogUtil.method2270(v3))));
                return false;
            }
            this.field8228 = v3;
            this.field8244 = HudAccess.method2035(this.field8232);
            LogUtil.cfr_underscore(field8245, (String)((Object)_4ZXYEo.Й("makeConcatWithConstants", new Object[]{}, (String)LogUtil.method2270(v3))));
        }
        this.field8246 = param2;
        this.field8247 = param1 == null || param1.isBlank() ? param2.getName() : param1;
        this.field8248 = null;
        LogUtil.cfr_underscore(field8249, (String)((Object)_4ZXYEo.О("makeConcatWithConstants", new Object[]{}, (String)this.field8247, (String)LogUtil.method2270(this.field8228), (String)LogUtil.method2272(param2.getPlayer()))));
        this.method2211(param2);
        return true;
    }

    public void method2195() {
        this.field8246 = null;
        this.field8247 = null;
        this.field8228 = null;
        this.field8244 = null;
        this.field8248 = null;
        this.field8250.remove();
    }

    public HudAccess method2196(boolean param1) {
        if (!this.method2198()) {
            return null;
        }
        HudAccess v2 = HudAccess.method2035(this.field8232);
        if (this.field8244 != null) {
            this.field8244.method2042(this.field8232, true, true, param1, true);
        }
        return v2;
    }

    private static boolean method2197(RecordClass229 param0) {
        return param0 != null && param0.world() != null && param0.player() != null && param0.interactionManager() != null;
    }

    public boolean method2198() {
        return this.field8246 != null || this.field8248 != null;
    }

    public boolean cfr_renamed_0(String param1) {
        String v2 = param1 == null || param1.isBlank() ? this.field8247 : param1;
        boolean v3 = v2 != null && this.field8248 != null && this.field8248.equalsIgnoreCase(v2);
        boolean v4 = v2 != null && BotView.method2197(this.field8228) && (this.field8247 != null && this.field8247.equalsIgnoreCase(v2) || v3);
        this.field8246 = null;
        this.field8247 = null;
        this.field8250.remove();
        if (!v4) {
            this.field8248 = null;
            LogUtil.cfr_underscore(field8251, (String)((Object)_4ZXYEo.Л("makeConcatWithConstants", new Object[]{}, (String)v2, (String)LogUtil.method2270(this.field8228))));
            return false;
        }
        this.field8248 = v2;
        LogUtil.cfr_underscore(field8252, (String)((Object)_4ZXYEo.Ъ("makeConcatWithConstants", new Object[]{}, (String)v2, (boolean)v3, (String)LogUtil.method2269(this.field8232))));
        return true;
    }

    private void method2199(ClientPlayerEntity param1) {
        if (param1 == null) {
            return;
        }
        if (!(param1.input instanceof KeyboardInput)) {
            param1.input = new KeyboardInput(this.field8232.options);
        }
        param1.input.movementForward = 0.0f;
        param1.input.movementSideways = 0.0f;
        param1.input.playerInput = new PlayerInput(false, false, false, false, false, false, false);
    }

    public ClientWorld method2200() {
        BotContext v1 = this.field8246;
        return v1 != null && v1.isReadyForView() ? v1.method_2890() : null;
    }

    private void method2201(BotContext param1) {
        boolean v2;
        if (param1 == null || !param1.isReadyForView()) {
            LogUtil.cfr_underscore(field8253, (String)((Object)_4ZXYEo.衣("makeConcatWithConstants", new Object[]{}, (String)LogUtil.method2269(this.field8232))));
            this.method2218();
            return;
        }
        boolean bl = v2 = !this.method2202(param1);
        if (v2) {
            LogUtil.method2275(field8254, field8255, field8256, (String)((Object)_4ZXYEo.н("makeConcatWithConstants", new Object[]{}, (String)LogUtil.method2269(this.field8232), (String)LogUtil.method2272(param1.getPlayer()))));
            chb.cfr_renamed_5((MinecraftClient)this.field8232, (BotContext)param1);
        }
        this.method2210(param1, v2);
        this.field8232.crosshairTarget = null;
        this.field8232.targetedEntity = null;
    }

    private boolean method2202(BotContext param1) {
        if (param1 == null) {
            return false;
        }
        return this.field8232.world == param1.method_2890() && this.field8232.player == param1.getPlayer() && this.field8232.interactionManager == param1.getInteractionManager() && this.field8232.getCameraEntity() == param1.method3584();
    }

    public void method2203() {
        RecordClass229 v1 = this.field8228;
        LogUtil.cfr_underscore(field8257, (String)((Object)_4ZXYEo.л("makeConcatWithConstants", new Object[]{}, (boolean)this.method2205(), (String)LogUtil.method2270(v1), (String)LogUtil.method2269(this.field8232))));
        if ((this.field8246 != null || this.field8248 != null) && BotView.method2197(v1)) {
            chb.cfr_renamed_5((MinecraftClient)this.field8232, (RecordClass229)v1);
            if (this.field8244 != null) {
                this.field8244.method2037(this.field8232);
            }
        }
        this.method2195();
    }

    public boolean method2204() {
        return this.method2218();
    }

    public boolean method2205() {
        return this.field8228 != null || this.field8244 != null || this.field8246 != null || this.field8247 != null || this.field8248 != null;
    }

    private boolean method2206(RecordClass229 param1) {
        BotContext v2 = this.field8246;
        if (param1 == null || v2 == null) {
            return false;
        }
        return param1.player() == v2.getPlayer() || param1.interactionManager() == v2.getInteractionManager();
    }

    private static RecordClass229 method2207(RecordClass229 param0) {
        if (param0 == null) {
            return null;
        }
        ClientWorld v1 = param0.world();
        ClientPlayerEntity v2 = param0.player();
        ClientPlayerInteractionManager v3 = param0.interactionManager();
        if (v1 == null || v2 == null || v3 == null) {
            return param0;
        }
        Entity v5 = null;
        return new RecordClass229(v1, v2, v3, (Entity)v2, null, v5);
    }

    @Override
    public void method1860() {
        this.method2204();
        BotSupplier.super.method1860();
    }

    public void method2208() {
        ArrayDeque v1 = (ArrayDeque)this.field8250.get();
        if (v1.isEmpty()) {
            return;
        }
        RecordClass229 v2 = (RecordClass229)v1.pop();
        chb.Я((MinecraftClient)this.field8232, (RecordClass229)v2);
        if (v1.isEmpty()) {
            this.field8250.remove();
        }
    }

    public ClientPlayerInteractionManager method2209() {
        BotContext v1 = this.method2213();
        return v1 != null ? v1.getInteractionManager() : null;
    }

    /*
     * Enabled aggressive block sorting
     */
    private void method2210(BotContext param1, boolean param2) {
        if (param1 == null) {
            return;
        }
        HudAccess v3 = param1.getHudState();
        if (v3 != null) {
            boolean v4 = param2;
            v3.method2042(this.field8232, true, true, v4, true);
        }
    }

    private void method2211(BotContext param1) {
        if (param1 == null || !param1.isReadyForView()) {
            return;
        }
        if (this.field8232.currentScreen != null) {
            this.field8232.setScreen((Screen)null);
        }
        chb.cfr_renamed_5((MinecraftClient)this.field8232, (BotContext)param1);
        this.method2210(param1, true);
        LogUtil.cfr_underscore(field8258, (String)((Object)_4ZXYEo.yu("makeConcatWithConstants", new Object[]{}, (String)this.field8247, (String)LogUtil.method2269(this.field8232))));
    }

    public boolean method2212() {
        return this.method2218();
    }

    public BotContext method2213() {
        BotContext v1 = this.field8246;
        return v1 != null && v1.isReadyForView() ? v1 : null;
    }

    public String method2214() {
        return this.field8247 != null ? this.field8247 : this.field8248;
    }

    public boolean method2215(String param1) {
        boolean v3;
        if (param1 == null || param1.isBlank()) {
            return false;
        }
        boolean v2 = this.field8247 != null && this.field8247.equalsIgnoreCase(param1);
        boolean bl = v3 = this.field8248 != null && this.field8248.equalsIgnoreCase(param1);
        if (!v2 && !v3) {
            return false;
        }
        return this.method2218();
    }

    public boolean method2216() {
        BotContext v1 = this.method2213();
        if (v1 == null) {
            return false;
        }
        if (this.method2202(v1)) {
            return false;
        }
        ArrayDeque v2 = (ArrayDeque)this.field8250.get();
        v2.push(chb.cfr_renamed_5((MinecraftClient)this.field8232));
        chb.Я((MinecraftClient)this.field8232, (BotContext)v1);
        return true;
    }

    public ClientPlayerEntity method2217() {
        BotContext v1 = this.method2213();
        return v1 != null ? v1.getPlayer() : null;
    }

    @EventHandler(2)
    public void cfr_renamed_4(EventUpdate param1) {
        BotContext v2 = this.field8246;
        if (v2 == null) {
            return;
        }
        this.method2201(v2);
    }

    private boolean method2218() {
        RecordClass229 v1 = this.field8228;
        HudAccess v2 = this.field8244;
        boolean v3 = v1 != null || this.field8246 != null || this.field8247 != null || this.field8248 != null;
        LogUtil.cfr_underscore(field8259, (String)((Object)_4ZXYEo.ии("makeConcatWithConstants", new Object[]{}, (boolean)v3, (String)this.field8247, (String)this.field8248, (String)LogUtil.method2270(v1), (String)LogUtil.method2269(this.field8232))));
        this.method2195();
        if (!BotView.method2197(v1)) {
            return v3;
        }
        this.method2193(v1, true);
        if (v2 != null) {
            v2.method2037(this.field8232);
        }
        return true;
    }

    /*
     * Enabled aggressive block sorting
     */
    public void method2219(HudAccess param1, boolean param2) {
        if (this.field8244 == null) {
            this.field8244 = HudAccess.method2035(this.field8232);
        } else {
            this.field8244.method2038(this.field8232, param2, true);
        }
        if (param1 != null) {
            boolean v3 = param2;
            param1.method2042(this.field8232, true, true, v3, true);
        }
    }

    public RecordClass229 method2220() {
        return this.field8228;
    }

    static {
        field8242 = "VIEW";
        ЧН = "activate rejected: context not ready, bot=\u0001";
        field8243 = "VIEW";
        ЧТ = "activate rejected: unusable main snapshot, bot=\u0001, current=\u0001";
        field8245 = "VIEW";
        ЧУ = "captured originalContext: \u0001";
        field8249 = "VIEW";
        chf = "activate bot=\u0001, main=\u0001, botPlayer=\u0001";
        field8251 = "VIEW";
        Чт = "suspend skipped for bot=\u0001, target=\u0001";
        field8252 = "VIEW";
        Чя = "suspendForTransition bot=\u0001, alreadySuspended=\u0001, keeping bot camera active during transition. client=\u0001";
        field8257 = "VIEW";
        Чп = "prepareMainClientDisconnect tracked=\u0001, snapshot=\u0001, client=\u0001";
        field8229 = "view-main-snapshot-skip-bot";
        field8230 = 500L;
        field8231 = "VIEW";
        ЧС = "skipped originalContext update from active bot snapshot -> \u0001, client=\u0001";
        field8233 = "view-main-snapshot-skip-foreign-player";
        field8234 = 500L;
        field8235 = "VIEW";
        Чл = "skipped originalContext update from foreign player snapshot -> \u0001, original=\u0001, client=\u0001";
        field8236 = "view-main-snapshot-update";
        field8237 = 750L;
        field8238 = "VIEW";
        Чу = "updated originalContext -> \u0001, client=\u0001";
        field8253 = "VIEW";
        ЧБм = "active context became invalid, forcing main restore. client=\u0001";
        field8254 = "view-maintain-correction";
        field8255 = 250L;
        field8256 = "VIEW";
        ЧЮ = "maintainActiveViewState reapplied bot context. client=\u0001, botPlayer=\u0001";
        field8258 = "VIEW";
        Чш = "applied active view state for bot=\u0001, client=\u0001";
        field8259 = "VIEW";
        ЧЯ = "restoreMainViewState hadTrackedState=\u0001, activeBot=\u0001, suspendedBot=\u0001, target=\u0001, clientBefore=\u0001";
        field8239 = "VIEW";
        ЧЖ = "restoreSnapshot skipped unusable snapshot: \u0001";
        field8240 = "VIEW";
        ЧЦ = "restoreSnapshot finishTick=\u0001, snapshot=\u0001, clientAfterApply=\u0001";
        field8241 = "VIEW";
        Чр = "restoreSnapshot finalized for player=\u0001, clientFinal=\u0001";
    }
}




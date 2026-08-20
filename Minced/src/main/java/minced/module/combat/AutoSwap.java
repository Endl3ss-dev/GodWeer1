/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.ingame.InventoryScreen
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.AirBlockItem
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.item.SwordItem
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket
 *  net.minecraft.screen.slot.SlotActionType
 *  sLM.4ZXYEo
 */
package minced.module.combat;
import minced.core.EventHandler;

import minced.core.BindSetting;
import minced.core.MultiBooleanSetting;
import minced.core.BooleanSetting;
import minced.core.EventInput;
import minced.core.ModeSetting;
import minced.core.EventUpdate;
import minced.core.NotificationManager;
import minced.core.ClientState;
import minced.core.InventoryUtil;
import minced.core.InventoryHelper;
import minced.core.EventKey;
import minced.core.Minced;
import minced.core.Module;
import minced.enums.Combat;
import minced.gui.ScreenClass586;
import minced.module.combat.AttackAura;
import minced.module.combat.AutoTotem;
import minced.module.movement.GuiMove;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AirBlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import sLM._4ZXYEo;

public class AutoSwap
extends Module {
    static String cfr_renamed_367;

    static String field4203 = "AutoSwap";
    static String field4204 = "Автоматически свапает выбранный предмет";
    static String field4205 = "Режим";
    static String field4206 = "Предметы";
    static String field4207 = "Предметы";
    static String field4208 = "Колесо";
    ModeSetting field4209 = new ModeSetting(field4205, field4206, field4207, field4208);
    static String field4210 = "Кнопка свапа";
    BindSetting field4211 = new BindSetting(field4210, () -> this.field4209.isMode(field4264));
    static String field4212 = "Кнопка колеса";
    BindSetting field4213 = new BindSetting(field4212, () -> this.field4209.isMode(field4281));
    static String field4214 = "Первый предмет";
    static String field4215 = "Тотем";
    static String field4216 = "Щит";
    static String field4217 = "Тотем";
    static String field4218 = "Фейерверк";
    static String field4219 = "Яблоко";
    static String field4220 = "Любая еда";
    static String field4221 = "Шар, Cфера";
    ModeSetting field4222 = new ModeSetting(field4214, field4215, () -> this.field4209.isMode(field4260), field4216, field4217, field4218, field4219, field4220, field4221);
    static String field4223 = "Второй предмет";
    static String field4224 = "Шар, Cфера";
    static String field4225 = "Щит";
    static String field4226 = "Тотем";
    static String field4227 = "Фейерверк";
    static String field4228 = "Яблоко";
    static String field4229 = "Любая еда";
    static String field4230 = "Шар, Cфера";
    ModeSetting field4231 = new ModeSetting(field4223, field4224, () -> this.field4209.isMode(field4279), field4225, field4226, field4227, field4228, field4229, field4230);
    static String field4232 = "Игнорировать обычные тотемы";
    BooleanSetting field4233 = new BooleanSetting(field4232, false, () -> this.field4209.isMode(field4284));
    static String field4234 = "Авто-шар кнопка";
    BindSetting field4235 = new BindSetting(field4234);
    static String field4236 = "Брать шар";
    static String field4237 = "Всегда";
    static String field4238 = "Когда без меча";
    static String field4239 = "Когда ливает";
    static String field4240 = "Когда в элитре";
    MultiBooleanSetting field4241 = new MultiBooleanSetting(field4236, new BooleanSetting(field4237, false), new BooleanSetting(field4238, true), new BooleanSetting(field4239, true), new BooleanSetting(field4240, true));
    static String field4242 = "Убирать без таргета";
    BooleanSetting field4243 = new BooleanSetting(field4242, true, () -> this.field4241.method1898(field4280));
    boolean field4244 = false;
    int field4245 = 0;
    int field4246 = -1;
    int field4247 = 0;
    ClientState field4248 = new ClientState();
    boolean field4249 = false;
    int field4250 = -1;
    boolean field4251 = false;
    boolean field4252 = false;
    int field4253 = -1;
    ClientState field4254 = new ClientState();
    static MinecraftClient I = MinecraftClient.getInstance();
    static String field4255;
    static long field4256;
    static String field4257;
    static String field4258;
    static float field4259;
    static String field4260;
    static double field4261;
    static double field4262;
    static double field4263;
    static String field4264;
    static String field4265;
    static String field4266;
    static String field4267;
    static String field4268;
    static String field4269;
    static String field4270;
    static String field4271;
    static String field4272;
    static String field4273;
    static int field4274;
    static String field4275;
    static String field4276;
    static int field4277;
    static String field4278;
    static String field4279;
    static String field4280;
    static String field4281;
    static long field4282;
    static long field4283;
    static String field4284;
    static String field4285;
    static String field4286;
    static String field4287;
    static String field4288;
    static String field4289;
    static String field4290;

    private Item method1064(String param1) {
        Item item;
        int v3 = -1;
        switch (param1.hashCode()) {
            case 1010520205: {
                if (!param1.equals(field4285)) break;
                v3 = 0;
                break;
            }
            case 1058035: {
                if (!param1.equals(field4286)) break;
                v3 = 1;
                break;
            }
            case -1114808720: {
                if (!param1.equals(field4287)) break;
                v3 = 2;
                break;
            }
            case 1621329609: {
                if (!param1.equals(field4288)) break;
                v3 = 3;
                break;
            }
            case 11260600: {
                if (!param1.equals(field4289)) break;
                v3 = 4;
                break;
            }
            case -77432722: {
                if (!param1.equals(field4290)) break;
                v3 = 5;
            }
        }
        switch (v3) {
            case 0: {
                item = Items.TOTEM_OF_UNDYING;
                break;
            }
            case 1: {
                item = Items.SHIELD;
                break;
            }
            case 2: {
                item = Items.FIREWORK_ROCKET;
                break;
            }
            case 3: {
                item = Items.GOLDEN_APPLE;
                break;
            }
            case 4: {
                item = Items.ENDER_PEARL;
                break;
            }
            case 5: {
                item = null;
                break;
            }
            default: {
                item = Items.AIR;
            }
        }
        return item;
    }

    private boolean method1065() {
        GuiMove v1 = Minced.getInstance().getModuleManager().method182();
        return v1 != null && v1.method556();
    }

    public AutoSwap() {
        super(field4203, field4204, Combat.Combat);
        this.addSettings(this.field4209, this.field4211, this.field4213, this.field4222, this.field4231, this.field4233, this.field4235, this.field4241, this.field4243);
    }

    @EventHandler(2)
    public void method1066(EventUpdate param1) {
        LivingEntity v3;
        boolean v4;
        boolean v2;
        if (AutoSwap.I.player == null) {
            return;
        }
        if (this.field4247 > 0) {
            --this.field4247;
        }
        if (AutoSwap.I.player.hurtTime > 0) {
            this.field4254.method3114();
        }
        v2 = this.method1082();
        boolean bl = v4 = (v3 = AttackAura.method861()) == null && (!Boolean.TRUE.equals(this.field4241.method1898(field4255)) || (Boolean)this.field4243.getValue() != false);
        if (this.field4252 && this.field4253 != -1 && (v4 || v3 != null && !this.method1081(v3))) {
            this.method1075(this.field4253);
            this.field4253 = -1;
            this.field4252 = false;
            return;
        }
        if (this.field4251 && v3 != null && this.method1081(v3) && !this.field4252 && this.method1070()) {
            this.method1095();
            this.field4252 = true;
        }
        if (this.field4249 && this.field4250 != -1 && this.field4248.method3113(field4256)) {
            int v5 = this.field4250 < 9 ? this.field4250 + 36 : this.field4250;
            I.setScreen((Screen)new InventoryScreen((PlayerEntity)AutoSwap.I.player));
            AutoSwap.I.interactionManager.clickSlot(AutoSwap.I.player.currentScreenHandler.syncId, v5, 40, SlotActionType.SWAP, (PlayerEntity)AutoSwap.I.player);
            AutoSwap.I.player.networkHandler.sendPacket((Packet)new CloseHandledScreenC2SPacket(AutoSwap.I.player.currentScreenHandler.syncId));
            I.setScreen(null);
            this.field4249 = false;
            this.field4250 = -1;
            this.field4244 = false;
        }
        if (this.field4249) {
            return;
        }
        if (this.field4245 == 1 && this.field4246 != -1 && this.field4247 == 0) {
            GuiMove v5 = Minced.getInstance().getModuleManager().method182();
            if (v5 != null && v5.method549()) {
                v5.setInventoryActionInProgress(true);
            }
            int v6 = this.field4246 < 9 ? this.field4246 + 36 : this.field4246;
            AutoSwap.I.interactionManager.clickSlot(AutoSwap.I.player.currentScreenHandler.syncId, v6, 40, SlotActionType.SWAP, (PlayerEntity)AutoSwap.I.player);
            AutoSwap.I.player.networkHandler.sendPacket((Packet)new CloseHandledScreenC2SPacket(AutoSwap.I.player.currentScreenHandler.syncId));
            if (v5 != null && v5.method549()) {
                v5.setInventoryActionInProgress(false);
            }
            this.field4245 = 0;
            this.field4246 = -1;
            this.field4244 = false;
            return;
        }
        if (this.field4245 > 0) {
            return;
        }
        if (this.field4209.isMode(field4257)) {
            if (this.field4244) {
                this.method1068();
            }
        } else if (this.field4209.isMode(field4258)) {
            InventoryHelper v5 = this.method1091();
            if (v5 == null) {
                return;
            }
            int v6 = v5.method3511();
            if (v6 >= 0) {
                boolean v9;
                ItemStack v7 = v5.method3520(v6);
                boolean v8 = v7.isEmpty();
                boolean bl2 = v9 = !v8 && !this.method1088(AutoSwap.I.player.getOffHandStack(), v7);
                if (!v5.method3517() && (v8 || v9)) {
                    v5.method3525(-1);
                }
            }
            if (v5.method3522() && this.field4244) {
                this.method1073();
            }
        }
    }

    private ItemStack method1067() {
        InventoryHelper v1 = this.method1091();
        if (v1 == null) {
            return ItemStack.EMPTY;
        }
        return v1.O();
    }

    private void method1068() {
        int v1 = this.cfr_renamed_4();
        if (v1 < 0) {
            this.field4244 = false;
            return;
        }
        if (this.method1078()) {
            this.field4248.method3114();
            this.field4249 = true;
            this.field4250 = v1;
            return;
        }
        if (this.method1072() || this.method1069()) {
            this.field4247 = 2;
            this.field4245 = 1;
            this.field4246 = v1;
        } else {
            if (this.method1065()) {
                GuiMove v2 = Minced.getInstance().getModuleManager().method182();
                v2.method548();
            }
            int v2 = v1 < 9 ? v1 + 36 : v1;
            AutoSwap.I.interactionManager.clickSlot(AutoSwap.I.player.currentScreenHandler.syncId, v2, 40, SlotActionType.SWAP, (PlayerEntity)AutoSwap.I.player);
            AutoSwap.I.player.networkHandler.sendPacket((Packet)new CloseHandledScreenC2SPacket(AutoSwap.I.player.currentScreenHandler.syncId));
            this.field4244 = false;
        }
    }

    private boolean method1069() {
        GuiMove v1 = Minced.getInstance().getModuleManager().method182();
        return v1 != null && v1.method549();
    }

    private boolean method1070() {
        AutoTotem v1 = (AutoTotem)Minced.getInstance().getModuleManager().method187(AutoTotem.class);
        if (v1 == null || !v1.isEnabled()) {
            return true;
        }
        float v2 = AutoSwap.I.player.getHealth() + AutoSwap.I.player.getAbsorptionAmount();
        return v2 > field4259;
    }

    private boolean method1072() {
        GuiMove v1 = Minced.getInstance().getModuleManager().method182();
        return v1 != null && v1.method561();
    }

    private int cfr_renamed_4() {
        ItemStack v1 = AutoSwap.I.player.getOffHandStack();
        Item v2 = v1.getItem();
        Item v3 = this.method1064((String)this.field4222.getValue());
        Item v4 = this.method1064((String)this.field4231.getValue());
        if (v2 instanceof AirBlockItem) {
            int v5 = this.method1094(v3);
            if (v5 >= 0) {
                return v5;
            }
            return this.method1094(v4);
        }
        if (v3 != null && v2 == v3 || v3 == null && v1.contains(DataComponentTypes.FOOD)) {
            return this.method1094(v4);
        }
        if (v4 != null && v2 == v4 || v4 == null && v1.contains(DataComponentTypes.FOOD)) {
            return this.method1094(v3);
        }
        int v5 = this.method1094(v3);
        return v5 >= 0 ? v5 : this.method1094(v4);
    }

    private void method1073() {
        ItemStack v1 = this.method1092();
        ItemStack v2 = AutoSwap.I.player.getOffHandStack();
        if (v1.isEmpty() || this.method1088(v2, v1)) {
            return;
        }
        int v3 = this.method1084(v1);
        if (v3 < 0) {
            this.field4244 = false;
            return;
        }
        this.method1080(v3);
    }

    private boolean method1074(LivingEntity param1) {
        double v6;
        if (AutoSwap.I.player == null) {
            return false;
        }
        double v2 = Math.sqrt(param1.getVelocity().x * param1.getVelocity().x + param1.getVelocity().z * param1.getVelocity().z);
        if (v2 < field4261) {
            return false;
        }
        double v4 = param1.getX() - AutoSwap.I.player.getX();
        double v8 = Math.sqrt(v4 * v4 + (v6 = param1.getZ() - AutoSwap.I.player.getZ()) * v6);
        if (v8 < field4262) {
            return false;
        }
        double v10 = v4 / v8;
        double v14 = param1.getVelocity().x;
        double v12 = v6 / v8;
        double v16 = param1.getVelocity().z;
        double v18 = v10 * v14 + v12 * v16;
        return v18 > field4263;
    }

    private void method1075(int param1) {
        GuiMove v2 = Minced.getInstance().getModuleManager().method182();
        if (v2 != null && v2.method549()) {
            v2.setInventoryActionInProgress(true);
        }
        int v3 = param1 < 9 ? param1 + 36 : param1;
        AutoSwap.I.interactionManager.clickSlot(AutoSwap.I.player.currentScreenHandler.syncId, v3, 40, SlotActionType.SWAP, (PlayerEntity)AutoSwap.I.player);
        AutoSwap.I.player.networkHandler.sendPacket((Packet)new CloseHandledScreenC2SPacket(AutoSwap.I.player.currentScreenHandler.syncId));
        if (v2 != null && v2.method549()) {
            v2.setInventoryActionInProgress(false);
        }
    }

    private int method1077() {
        ItemStack v1 = this.method1067();
        if (v1.isEmpty()) {
            return -1;
        }
        for (int v2 = 0; v2 < 36; ++v2) {
            ItemStack v3 = AutoSwap.I.player.getInventory().getStack(v2);
            if (!this.method1088(v3, v1)) continue;
            return v2;
        }
        return -1;
    }

    private boolean method1078() {
        GuiMove v1 = Minced.getInstance().getModuleManager().method182();
        return v1 != null && v1.isEnabled() && v1.isBypassMode(field4265);
    }

    public boolean method1079() {
        return this.field4252;
    }

    private void method1080(int param1) {
        if (this.method1078()) {
            this.field4248.method3114();
            this.field4249 = true;
            this.field4250 = param1;
            return;
        }
        if (this.method1072() || this.method1069()) {
            this.field4247 = 2;
            this.field4245 = 1;
            this.field4246 = param1;
        } else {
            if (this.method1065()) {
                GuiMove v2 = Minced.getInstance().getModuleManager().method182();
                v2.method548();
            }
            int v2 = param1 < 9 ? param1 + 36 : param1;
            AutoSwap.I.interactionManager.clickSlot(AutoSwap.I.player.currentScreenHandler.syncId, v2, 40, SlotActionType.SWAP, (PlayerEntity)AutoSwap.I.player);
            AutoSwap.I.player.networkHandler.sendPacket((Packet)new CloseHandledScreenC2SPacket(AutoSwap.I.player.currentScreenHandler.syncId));
            this.field4244 = false;
        }
    }

    private boolean method1081(LivingEntity param1) {
        PlayerEntity v2;
        ItemStack v3;
        if (!this.field4251) {
            return false;
        }
        if (Boolean.TRUE.equals(this.field4241.method1898(field4266))) {
            return true;
        }
        if (AutoSwap.I.player.hurtTime > 0 || param1 == null) {
            return false;
        }
        if (Boolean.TRUE.equals(this.field4241.method1898(field4267)) && !(param1.getMainHandStack().getItem() instanceof SwordItem)) {
            return true;
        }
        if (Boolean.TRUE.equals(this.field4241.method1898(field4268)) && this.method1074(param1) && !AutoSwap.I.player.isTouchingWater()) {
            return true;
        }
        return Boolean.TRUE.equals(this.field4241.method1898(field4269)) && param1 instanceof PlayerEntity && (v3 = (v2 = (PlayerEntity)param1).getInventory().getArmorStack(2)).getItem() == Items.ELYTRA;
    }

    private boolean method1082() {
        ItemStack v1 = this.method1067();
        if (v1.isEmpty()) {
            return false;
        }
        return this.method1088(AutoSwap.I.player.getOffHandStack(), v1);
    }

    @EventHandler(2)
    public void method1083(EventKey param1) {
        ScreenClass586 v3;
        Object v42;
        if (AutoSwap.I.currentScreen != null && !(AutoSwap.I.currentScreen instanceof ScreenClass586)) {
            return;
        }
        int v2 = param1.method3595();
        if (this.field4209.isMode(field4270) && v2 == (Integer)this.field4211.getValue() && param1.method3599()) {
            this.field4244 = true;
        }
        if (this.field4209.isMode(field4271) && v2 == (Integer)this.field4213.getValue()) {
            if (param1.method3599()) {
                this.field4244 = true;
                if (!(AutoSwap.I.currentScreen instanceof ScreenClass586)) {
                    InventoryHelper helper = this.method1091();
                    ItemStack[] items = new ItemStack[3];
                    for (int index = 0; index < items.length; ++index) {
                        ItemStack item = helper == null ? ItemStack.EMPTY : helper.method3520(index);
                        items[index] = item.isEmpty() ? Items.BARRIER.getDefaultStack() : item.copy();
                    }
                    I.setScreen((Screen)new ScreenClass586(items));
                }
            } else {
                v42 = AutoSwap.I.currentScreen;
                if (v42 instanceof ScreenClass586) {
                    ItemStack v6;
                    InventoryHelper v5;
                    ScreenClass586 v3_screen = (ScreenClass586)((Object)v42);
                    int hoveredSlot = v3_screen.getHoveredSlot();
                    if (hoveredSlot >= 0 && (v5 = this.method1091()) != null && !(v6 = v5.method3520(hoveredSlot)).isEmpty()) {
                        v5.method3525(hoveredSlot);
                    }
                    I.setScreen(null);
                    this.field4244 = false;
                }
            }
        }
        if (!param1.method3599() && (Integer)this.field4235.getValue() == v2) {
            boolean bl = this.field4251 = !this.field4251;
            if (this.field4251) {
                ItemStack item_v3 = this.method1067();
                Object v42_str = item_v3.isEmpty() ? field4272 : item_v3.getName().getString();
                NotificationManager.method2456(field4273, (String)((Object)_4ZXYEo.фО("makeConcatWithConstants", new Object[]{}, (String)v42_str)), field4274);
            } else {
                NotificationManager.method2456(field4275, field4276, field4277);
            }
        }
    }

    private int method1084(ItemStack param1) {
        if (param1 == null || param1.isEmpty()) {
            return -1;
        }
        if (!this.field4209.isMode(field4278)) {
            return this.method1094(param1.getItem());
        }
        for (int v2 = 0; v2 < AutoSwap.I.player.getInventory().size(); ++v2) {
            ItemStack v3 = AutoSwap.I.player.getInventory().getStack(v2);
            if (!this.method1088(v3, param1)) continue;
            return v2;
        }
        return -1;
    }

    private boolean method1088(ItemStack param1, ItemStack param2) {
        if (param1 == null || param2 == null || param1.isEmpty() || param2.isEmpty()) {
            return false;
        }
        if (!ItemStack.areItemsAndComponentsEqual((ItemStack)param1, (ItemStack)param2)) {
            return false;
        }
        return param1.getName().getString().equals(param2.getName().getString());
    }

    @EventHandler(2)
    public void method1089(EventInput param1) {
        if (this.field4247 > 0) {
            param1.method1973(0.0f);
            param1.method1972(0.0f);
            param1.method1974(false);
            param1.method1970(false);
        }
        if (this.field4249) {
            if (!this.field4248.method3113(field4282)) {
                param1.method1973(0.0f);
                param1.method1972(0.0f);
                param1.method1970(false);
            }
            if (!this.field4248.method3113(field4283)) {
                param1.method1974(false);
            }
        }
    }

    private InventoryHelper method1091() {
        return Minced.getInstance().getHandlerManager() != null ? Minced.getInstance().getHandlerManager().method3540() : null;
    }

    private ItemStack method1092() {
        InventoryHelper v1 = this.method1091();
        if (v1 == null || AutoSwap.I.player == null) {
            return ItemStack.EMPTY;
        }
        int v2 = v1.method3511();
        if (v2 < 0) {
            return ItemStack.EMPTY;
        }
        return v1.method3520(v2);
    }

    @Override
    public void onDisable() {
        this.field4244 = false;
        this.field4245 = 0;
        this.field4246 = -1;
        this.field4247 = 0;
        this.field4249 = false;
        this.field4250 = -1;
        this.field4251 = false;
        this.field4252 = false;
        this.field4253 = -1;
        super.onDisable();
    }

    public boolean method1093() {
        return this.field4251;
    }

    private int method1094(Item param1) {
        if (param1 == Items.TOTEM_OF_UNDYING && ((Boolean)this.field4233.getValue()).booleanValue()) {
            return InventoryUtil.method3335();
        }
        if (param1 == null) {
            return InventoryUtil.method3310();
        }
        return InventoryUtil.method3336(param1);
    }

    private void method1095() {
        if (AutoSwap.I.currentScreen != null) {
            return;
        }
        int v1 = this.method1077();
        if (v1 < 0) {
            return;
        }
        this.field4253 = v1;
        this.method1075(v1);
    }

    static {
        field4265 = "Funtime";
        field4255 = "Всегда";
        field4256 = 100L;
        field4257 = "Предметы";
        field4258 = "Колесо";
        field4282 = 190L;
        field4283 = 350L;
        field4285 = "Тотем";
        field4286 = "Щит";
        field4287 = "Фейерверк";
        field4288 = "Яблоко";
        field4289 = "Шар, Cфера";
        field4290 = "Любая еда";
        field4270 = "Предметы";
        field4271 = "Колесо";
        field4272 = "не выбран";
        field4273 = "J";
        cfr_renamed_367 = "Авто-шар включен: \u0001";
        field4274 = -10158236;
        field4275 = "K";
        field4276 = "Авто-шар выключен";
        field4277 = -11513601;
        field4278 = "Колесо";
        field4266 = "Всегда";
        field4267 = "Когда без меча";
        field4268 = "Когда ливает";
        field4269 = "Когда в элитре";
        field4259 = 4.0f;
        field4261 = 0.1;
        field4262 = 0.1;
        field4263 = 0.15;
        field4280 = "Всегда";
        field4284 = "Предметы";
        field4279 = "Предметы";
        field4260 = "Предметы";
        field4281 = "Колесо";
        field4264 = "Предметы";
    }
}




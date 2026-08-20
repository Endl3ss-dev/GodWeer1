/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.Items
 *  net.minecraft.screen.slot.SlotActionType
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.hit.HitResult
 *  net.minecraft.util.hit.HitResult$Type
 *  sg.ec.2б$Ч
 */
package minced.module.player;
import minced.core.EventHandler;

import java.util.function.BooleanSupplier;
import minced.core.BooleanSetting;
import minced.core.ModeSetting;
import minced.core.EventUpdate;
import minced.core.EventBlockBreak;
import minced.core.InventoryUtil;
import minced.core.Module;
import minced.enums.Combat;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import sg.ec._2b;

public class AutoTool
extends Module {
    ModeSetting field6045 = new ModeSetting(field6054, field6055, field6056, field6057);
    static String field6046;
    static String field6047;
    static MinecraftClient I = MinecraftClient.getInstance();
    int field6048 = -1;
    int field6049 = -1;
    int field6050 = -1;
    int field6051 = -1;
    static String field6052;
    static String field6053;
    static String field6054;
    static String field6055;
    static String field6056;
    static String field6057;
    static String field6058;
    BooleanSetting field6059 = new BooleanSetting(field6058, false);
    static String field6060;

    private boolean method1431() {
        return this.field6045.isMode(field6046) || this.field6045.isMode(field6047);
    }

    @Override
    public void onDisable() {
        if (AutoTool.I.player != null) {
            if (this.method1434()) {
                this.method1432();
                if (this.field6048 != -1) {
                    InventoryUtil.method3313(this.field6048);
                }
            }
            if (this.method1431()) {
                this.l();
            }
        }
        this.field6048 = -1;
        this.field6049 = -1;
        this.field6050 = -1;
        this.field6051 = -1;
        super.onDisable();
    }

    private void method1432() {
        if (this.field6049 == -1 || AutoTool.I.player == null || AutoTool.I.interactionManager == null || AutoTool.I.currentScreen != null) {
            this.field6049 = -1;
            return;
        }
        int v1 = InventoryUtil.method3322(this.field6049);
        if (v1 >= 0 && this.field6048 >= 0 && this.field6048 <= 8) {
            AutoTool.I.interactionManager.clickSlot(AutoTool.I.player.currentScreenHandler.syncId, v1, this.field6048, SlotActionType.SWAP, (PlayerEntity)AutoTool.I.player);
        }
        this.field6049 = -1;
    }

    public AutoTool() {
        super(field6052, field6053, Combat.Player);
        this.addSettings(this.field6045, this.field6059);
    }

    private boolean method1433() {
        return AutoTool.I.options.attackKey.isPressed();
    }

    private boolean method1434() {
        return this.field6045.isMode(field6060);
    }

    private void method1435(int param1, int param2) {
        if (AutoTool.I.interactionManager == null || AutoTool.I.player == null) {
            return;
        }
        int v3 = InventoryUtil.method3322(param1);
        if (v3 < 0 || param2 < 0 || param2 > 8) {
            return;
        }
        AutoTool.I.interactionManager.clickSlot(AutoTool.I.player.currentScreenHandler.syncId, v3, param2, SlotActionType.SWAP, (PlayerEntity)AutoTool.I.player);
    }

    @EventHandler(2)
    public void method1436(EventBlockBreak param1) {
        if (!this.method1434()) {
            return;
        }
        if (AutoTool.I.player == null || AutoTool.I.interactionManager == null) {
            return;
        }
        if (param1.method2584() == _2b.ch.START) {
            int v2;
            if (this.field6048 == -1) {
                this.field6048 = AutoTool.I.player.getInventory().selectedSlot;
            }
            if ((v2 = this.method1439(param1.method2583(), (Boolean)this.field6059.getValue())) >= 9 && AutoTool.I.currentScreen != null) {
                v2 = this.method1439(param1.method2583(), false);
            }
            if (v2 != -1) {
                if (v2 < 9) {
                    this.method1432();
                    InventoryUtil.method3313(v2);
                } else if (((Boolean)this.field6059.getValue()).booleanValue() && AutoTool.I.currentScreen == null && AutoTool.I.interactionManager != null) {
                    if (this.field6049 != -1 && this.field6049 != v2) {
                        this.method1432();
                    }
                    if (this.field6049 == -1) {
                        this.field6049 = v2;
                        this.method1435(this.field6049, this.field6048);
                    }
                    InventoryUtil.method3313(this.field6048);
                }
            }
        } else {
            this.method1432();
            if (this.field6048 != -1) {
                InventoryUtil.method3313(this.field6048);
                this.field6048 = -1;
            }
        }
    }

    private void l() {
        if (this.field6051 != -1) {
            this.method1438();
        }
        if (this.field6050 != -1) {
            AutoTool.I.player.getInventory().selectedSlot = this.field6050;
            this.field6050 = -1;
        }
    }

    @EventHandler(2)
    public void method1437(EventUpdate param1) {
        if (!this.method1431()) {
            return;
        }
        if (AutoTool.I.player == null || AutoTool.I.world == null || AutoTool.I.player.isCreative()) {
            this.field6050 = -1;
            this.field6051 = -1;
            return;
        }
        if (this.method1433()) {
            int v2 = this.method1440();
            if (v2 != -1) {
                if (this.field6050 == -1) {
                    this.field6050 = AutoTool.I.player.getInventory().selectedSlot;
                }
                if (v2 < 9) {
                    if (this.field6051 != -1 && v2 != this.field6050) {
                        this.method1438();
                    }
                    AutoTool.I.player.getInventory().selectedSlot = v2;
                } else if (((Boolean)this.field6059.getValue()).booleanValue() && AutoTool.I.currentScreen == null && AutoTool.I.interactionManager != null) {
                    if (this.field6051 != -1 && this.field6051 != v2) {
                        this.method1438();
                    }
                    if (this.field6051 == -1) {
                        this.field6051 = v2;
                        this.method1435(this.field6051, this.field6050);
                    }
                    AutoTool.I.player.getInventory().selectedSlot = this.field6050;
                }
            } else if (this.field6050 != -1) {
                this.l();
            }
        } else if (this.field6050 != -1) {
            this.l();
        }
    }

    private void method1438() {
        if (this.field6051 == -1 || AutoTool.I.player == null || AutoTool.I.interactionManager == null || AutoTool.I.currentScreen != null) {
            this.field6051 = -1;
            return;
        }
        int v1 = InventoryUtil.method3322(this.field6051);
        if (v1 >= 0 && this.field6050 >= 0 && this.field6050 <= 8) {
            AutoTool.I.interactionManager.clickSlot(AutoTool.I.player.currentScreenHandler.syncId, v1, this.field6050, SlotActionType.SWAP, (PlayerEntity)AutoTool.I.player);
        }
        this.field6051 = -1;
    }

    private int method1439(BlockState param1, boolean param2) {
        int v3;
        int n = v3 = param2 ? 35 : 8;
        if (param1.getBlock() == Blocks.COBWEB) {
            for (int v4 = 0; v4 <= v3; ++v4) {
                if (AutoTool.I.player.getInventory().getStack(v4).getItem() != Items.SHEARS) continue;
                return v4;
            }
        }
        return InventoryUtil.method3314(param1, 0, v3);
    }

    private int method1440() {
        BlockHitResult v1;
        HitResult v2 = AutoTool.I.crosshairTarget;
        if (v2 instanceof BlockHitResult) {
            v1 = (BlockHitResult)v2;
            if (v1.getType() != HitResult.Type.BLOCK) {
                return -1;
            }
        } else {
            return -1;
        }
        BlockState v2s = AutoTool.I.world.getBlockState(v1.getBlockPos());
        if (v2s.isAir()) {
            return -1;
        }
        int v3 = this.method1439((BlockState)v2s, (Boolean)this.field6059.getValue());
        if (v3 >= 9 && AutoTool.I.currentScreen != null) {
            v3 = this.method1439((BlockState)v2s, false);
        }
        return v3;
    }

    static {
        field6052 = "AutoTool";
        field6053 = "Автоматически выбирает подходящий инструмент для добычи блоков";
        field6054 = "Режим";
        field6055 = "Сайлент";
        field6056 = "Обычный";
        field6057 = "Сайлент";
        field6058 = "Использовать из инвентаря";
        field6060 = "Сайлент";
        field6046 = "Обычный";
        field6047 = "Легит";
    }
}




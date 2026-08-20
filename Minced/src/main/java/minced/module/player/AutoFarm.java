/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.network.ClientPlayerEntity
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket
 *  net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket$Action
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.Vec3i
 */
package minced.module.player;
import minced.core.EventHandler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BooleanSupplier;
import minced.core.MultiBooleanSetting;
import minced.core.BooleanSetting;
import minced.core.SliderSetting;
import minced.core.ModeSetting;
import minced.core.EventUpdate;
import minced.core.ClientState;
import minced.core.Rotation;
import minced.core.AimRotation;
import minced.core.Module;
import minced.enums.Combat;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class AutoFarm
extends Module {
    BlockPos field1523 = null;
    static MinecraftClient I = MinecraftClient.getInstance();
    SliderSetting field1524 = new SliderSetting(field1572, field1573, 1.0f, field1574, field1575);
    BooleanSetting field1525;
    SliderSetting field1526;
    ClientState field1527;
    SliderSetting field1528 = new SliderSetting(field1576, field1577, 0.0f, field1578, 1.0f);
    BooleanSetting field1529;
    static double field1530;
    static double field1531;
    static double field1532;
    SliderSetting field1533 = new SliderSetting(field1579, 1.0f, 1.0f, field1580, 1.0f);
    static double field1534;
    static double field1535;
    static double field1536;
    ModeSetting field1537;
    static String field1538;
    static String field1539;
    static float field1540;
    MultiBooleanSetting field1541;
    static String field1542;
    static String field1543;
    static String field1544;
    static String field1545;
    static String field1546;
    static String field1547;
    static String field1548;
    static String field1549;
    static String field1550;
    static String field1551;
    static String field1552;
    static String field1553;
    static String field1554;
    static String field1555;
    static String field1556;
    static String field1557;
    static String field1558;
    static String field1559;
    static String field1560;
    int field1561 = 0;
    Vec3d field1562 = null;
    static double field1563;
    static double field1564;
    static double field1565;
    static double field1566;
    static double field1567;
    static float field1568;
    static float field1569;
    static String field1570;
    static String field1571;
    static String field1572;
    static float field1573;
    static float field1574;
    static float field1575;
    static String field1576;
    static float field1577;
    static float field1578;
    static String field1579;
    static float field1580;
    static String field1581;
    static String field1582;
    static float field1583;
    static float field1584;
    static float field1585;
    static String field1586;
    static String field1587;
    static String field1588;
    static String field1589;
    static String field1590;
    static String field1591;
    static String field1592;
    static String field1593;
    static String field1594;
    static String field1595;
    static String field1596;
    static String field1597;
    static String field1598;
    static String field1599;
    static String field1600;
    static String field1601;
    static String field1602;
    static String field1603;
    static String field1604;
    static String field1605;
    static String field1606;
    static String field1607;
    static String field1608;
    static String field1609;
    static String field1610;
    static double field1611;
    static double field1612;
    static double field1613;
    static double field1614;
    static double field1615;
    static double field1616;

    @Override
    public void onEnable() {
        super.onEnable();
        this.field1523 = null;
    }

    @EventHandler(2)
    public void method518(EventUpdate param1) {
        boolean v5;
        if (AutoFarm.I.player == null || AutoFarm.I.world == null) {
            return;
        }
        List v2 = this.method523(((Float)this.field1524.getValue()).floatValue());
        List v3 = (Boolean)this.field1525.getValue() != false ? this.method523(((Float)this.field1526.getValue()).floatValue()) : new ArrayList();
        boolean v4 = !v2.isEmpty() && this.field1527.method3113((long)((Float)this.field1528.getValue()).floatValue());
        boolean bl = v5 = (Boolean)this.field1529.getValue() != false && v4;
        if (((Boolean)this.field1525.getValue()).booleanValue() && !v3.isEmpty() && !v5) {
            BlockPos v6 = (BlockPos)v3.get(0);
            double v7 = AutoFarm.I.player.squaredDistanceTo((double)v6.getX() + field1530, (double)v6.getY() + field1531, (double)v6.getZ() + field1532);
            if (v7 > (double)(((Float)this.field1524.getValue()).floatValue() * ((Float)this.field1524.getValue()).floatValue())) {
                this.field1523 = v6;
                this.method522(this.field1523);
            } else {
                AutoFarm.I.options.forwardKey.setPressed(false);
                AutoFarm.I.options.jumpKey.setPressed(false);
                this.field1523 = null;
            }
        } else if (((Boolean)this.field1525.getValue()).booleanValue()) {
            AutoFarm.I.options.forwardKey.setPressed(false);
            AutoFarm.I.options.jumpKey.setPressed(false);
            this.field1523 = null;
        }
        if (v4) {
            if (v5) {
                AutoFarm.I.options.forwardKey.setPressed(false);
                AutoFarm.I.options.jumpKey.setPressed(false);
            }
            int v6 = 0;
            for (BlockPos v8 : (Iterable<BlockPos>)(v2)) {
                if (v6 >= ((Float)this.field1533.getValue()).intValue()) break;
                if (AutoFarm.I.player.squaredDistanceTo((double)v8.getX() + field1534, (double)v8.getY() + field1535, (double)v8.getZ() + field1536) > (double)(((Float)this.field1524.getValue()).floatValue() * ((Float)this.field1524.getValue()).floatValue())) continue;
                float[] v9 = this.method524(v8);
                if (((String)this.field1537.getValue()).equals(field1538)) {
                    AutoFarm.I.player.setYaw(v9[0]);
                    AutoFarm.I.player.setPitch(v9[1]);
                } else if (((String)this.field1537.getValue()).equals(field1539)) {
                    AimRotation.method367(new Rotation(v9[0], v9[1]), field1540, 1, 6);
                }
                AutoFarm.I.player.networkHandler.sendPacket((Packet)new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, v8, Direction.UP));
                AutoFarm.I.player.networkHandler.sendPacket((Packet)new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, v8, Direction.UP));
                ++v6;
            }
            if (v6 > 0) {
                this.field1527.method3114();
            }
        }
    }

    private boolean method519(BlockPos param1) {
        if (AutoFarm.I.world == null) {
            return false;
        }
        BlockState v2 = AutoFarm.I.world.getBlockState(param1);
        Block v3 = v2.getBlock();
        if (v3 == Blocks.PUMPKIN && this.field1541.method1898(field1542).booleanValue()) {
            return true;
        }
        if (v3 == Blocks.MELON && this.field1541.method1898(field1543).booleanValue()) {
            return true;
        }
        if (v3 == Blocks.CARROTS && this.field1541.method1898(field1544).booleanValue()) {
            return (Integer)v2.get((Property)Properties.AGE_7) >= 7;
        }
        if (v3 == Blocks.POTATOES && this.field1541.method1898(field1545).booleanValue()) {
            return (Integer)v2.get((Property)Properties.AGE_7) >= 7;
        }
        if (v3 == Blocks.BEETROOTS && this.field1541.method1898(field1546).booleanValue()) {
            return (Integer)v2.get((Property)Properties.AGE_3) >= 3;
        }
        if (v3 == Blocks.WHEAT && this.field1541.method1898(field1547).booleanValue()) {
            return (Integer)v2.get((Property)Properties.AGE_7) >= 7;
        }
        if (v3 == Blocks.OAK_SAPLING && this.field1541.method1898(field1548).booleanValue()) {
            return true;
        }
        if (v3 == Blocks.BIRCH_SAPLING && this.field1541.method1898(field1549).booleanValue()) {
            return true;
        }
        if (v3 == Blocks.SPRUCE_SAPLING && this.field1541.method1898(field1550).booleanValue()) {
            return true;
        }
        if (v3 == Blocks.JUNGLE_SAPLING && this.field1541.method1898(field1551).booleanValue()) {
            return true;
        }
        if (v3 == Blocks.ACACIA_SAPLING && this.field1541.method1898(field1552).booleanValue()) {
            return true;
        }
        if (v3 == Blocks.DARK_OAK_SAPLING && this.field1541.method1898(field1553).booleanValue()) {
            return true;
        }
        if (v3 == Blocks.BAMBOO_SAPLING && this.field1541.method1898(field1554).booleanValue()) {
            return true;
        }
        if (v3 == Blocks.BAMBOO && this.field1541.method1898(field1555).booleanValue()) {
            return true;
        }
        if (v3 == Blocks.COCOA && this.field1541.method1898(field1556).booleanValue()) {
            return (Integer)v2.get((Property)Properties.AGE_2) >= 2;
        }
        if (v3 == Blocks.SUGAR_CANE && this.field1541.method1898(field1557).booleanValue()) {
            return AutoFarm.I.world.getBlockState(param1.down()).getBlock() == Blocks.SUGAR_CANE;
        }
        if (v3 == Blocks.CACTUS && this.field1541.method1898(field1558).booleanValue()) {
            return AutoFarm.I.world.getBlockState(param1.down()).getBlock() == Blocks.CACTUS;
        }
        if (v3 == Blocks.SWEET_BERRY_BUSH && this.field1541.method1898(field1559).booleanValue()) {
            return (Integer)v2.get((Property)Properties.AGE_3) >= 2;
        }
        if (v3 == Blocks.NETHER_WART && this.field1541.method1898(field1560).booleanValue()) {
            return (Integer)v2.get((Property)Properties.AGE_3) >= 3;
        }
        return false;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.field1523 = null;
        this.field1561 = 0;
        this.field1562 = null;
        if (AutoFarm.I.player != null) {
            AutoFarm.I.options.forwardKey.setPressed(false);
            AutoFarm.I.options.jumpKey.setPressed(false);
        }
    }

    private void method522(BlockPos param1) {
        boolean v14;
        double v4;
        if (AutoFarm.I.player == null || param1 == null) {
            return;
        }
        double v2 = (double)param1.getX() + field1563 - AutoFarm.I.player.getX();
        double v6 = Math.sqrt(v2 * v2 + (v4 = (double)param1.getZ() + field1564 - AutoFarm.I.player.getZ()) * v4);
        if (v6 < field1565) {
            AutoFarm.I.options.forwardKey.setPressed(false);
            this.field1561 = 0;
            this.field1562 = null;
            return;
        }
        float v8 = (float)(Math.toDegrees(Math.atan2(v4, v2)) - field1566);
        if (this.field1562 != null) {
            double v9 = AutoFarm.I.player.getPos().squaredDistanceTo(this.field1562);
            this.field1561 = v9 < field1567 && AutoFarm.I.player.isOnGround() ? ++this.field1561 : 0;
        }
        this.field1562 = AutoFarm.I.player.getPos();
        if (this.field1561 > 5) {
            v8 += this.field1561 % 20 < 10 ? field1568 : field1569;
        }
        AutoFarm.I.player.setYaw(v8);
        AutoFarm.I.options.forwardKey.setPressed(true);
        BlockPos v9 = AutoFarm.I.player.getBlockPos();
        Vec3d v10 = AutoFarm.I.player.getRotationVec(1.0f);
        BlockPos v11 = v9.add((int)Math.round(v10.x), 0, (int)Math.round(v10.z));
        BlockPos v12 = BlockPos.ofFloored((double)(AutoFarm.I.player.getX() + v10.x), (double)AutoFarm.I.player.getY(), (double)(AutoFarm.I.player.getZ() + v10.z));
        boolean v13 = !AutoFarm.I.world.getBlockState(v11).isAir() || !AutoFarm.I.world.getBlockState(v12).isAir();
        boolean bl = v14 = AutoFarm.I.world.getBlockState(v11.up()).isAir() && AutoFarm.I.world.getBlockState(v11.up(2)).isAir();
        if (AutoFarm.I.player.horizontalCollision && AutoFarm.I.player.isOnGround()) {
            AutoFarm.I.options.jumpKey.setPressed(true);
        } else if (v13 && v14 && AutoFarm.I.player.isOnGround()) {
            AutoFarm.I.options.jumpKey.setPressed(true);
        } else {
            AutoFarm.I.options.jumpKey.setPressed(false);
        }
    }

    public AutoFarm() {
        super(field1570, field1571, Combat.Player);
        this.field1525 = new BooleanSetting(field1581, true);
        this.field1526 = new SliderSetting(field1582, field1583, field1584, field1585, 1.0f, () -> (Boolean)this.field1525.getValue());
        this.field1529 = new BooleanSetting(field1586, true, () -> (Boolean)this.field1525.getValue());
        this.field1537 = new ModeSetting(field1587, field1588, field1589, field1590, field1591);
        this.field1541 = new MultiBooleanSetting(field1592, new BooleanSetting(field1593, true), new BooleanSetting(field1594, true), new BooleanSetting(field1595, true), new BooleanSetting(field1596, true), new BooleanSetting(field1597, true), new BooleanSetting(field1598, true), new BooleanSetting(field1599, true), new BooleanSetting(field1600, true), new BooleanSetting(field1601, true), new BooleanSetting(field1602, true), new BooleanSetting(field1603, true), new BooleanSetting(field1604, true), new BooleanSetting(field1605, true), new BooleanSetting(field1606, true), new BooleanSetting(field1607, true), new BooleanSetting(field1608, true), new BooleanSetting(field1609, true), new BooleanSetting(field1610, true));
        this.field1527 = new ClientState();
        this.addSettings(this.field1524, this.field1528, this.field1533, this.field1525, this.field1526, this.field1529, this.field1537, this.field1541);
    }

    private List method523(float param1) {
        ArrayList<BlockPos> v2 = new ArrayList<BlockPos>();
        if (AutoFarm.I.player == null || AutoFarm.I.world == null) {
            return v2;
        }
        BlockPos v3 = AutoFarm.I.player.getBlockPos();
        int v4 = (int)Math.ceil(param1);
        for (int v5 = -v4; v5 <= v4; ++v5) {
            for (int v6 = -v4; v6 <= v4; ++v6) {
                for (int v7 = -v4; v7 <= v4; ++v7) {
                    BlockPos v8 = v3.add(v5, v6, v7);
                    if (!this.method519(v8)) continue;
                    v2.add(v8);
                }
            }
        }
        v2.sort(Comparator.comparingDouble(param0 -> AutoFarm.I.player.squaredDistanceTo((double)param0.getX() + field1614, (double)param0.getY() + field1615, (double)param0.getZ() + field1616)));
        return v2;
    }

    private float[] method524(BlockPos param1) {
        ClientPlayerEntity v2 = AutoFarm.I.player;
        if (v2 == null) {
            return new float[]{0.0f, 0.0f};
        }
        Vec3d v3 = v2.getEyePos();
        Vec3d v4 = Vec3d.ofCenter((Vec3i)param1);
        double v5 = v4.x - v3.x;
        double v7 = v4.y - v3.y;
        double v9 = v4.z - v3.z;
        double v11 = Math.sqrt(v5 * v5 + v9 * v9);
        float v13 = (float)MathHelper.wrapDegrees((double)(Math.toDegrees(Math.atan2(v9, v5)) - field1611));
        float v14 = (float)MathHelper.clamp((double)MathHelper.wrapDegrees((double)(-Math.toDegrees(Math.atan2(v7, v11)))), (double)field1612, (double)field1613);
        return new float[]{v13, v14};
    }

    static {
        field1570 = "AutoFarm";
        field1571 = "Автоматически собирает растения";
        field1572 = "Радиус";
        field1573 = 4.5f;
        field1574 = 6.0f;
        field1575 = 0.1f;
        field1576 = "Скорость сбора (мс)";
        field1577 = 50.0f;
        field1578 = 500.0f;
        field1579 = "Блоков/тик";
        field1580 = 10.0f;
        field1581 = "Авто-ходьба";
        field1582 = "Радиус ходьбы";
        field1583 = 15.0f;
        field1584 = 5.0f;
        field1585 = 50.0f;
        field1586 = "Пауза при сборе";
        field1587 = "Rotate";
        field1588 = "None";
        field1589 = "None";
        field1590 = "Client";
        field1591 = "Packet";
        field1592 = "Растения";
        field1593 = "Тыквы";
        field1594 = "Арбузы";
        field1595 = "Морковь";
        field1596 = "Картофель";
        field1597 = "Свеклу";
        field1598 = "Пшеницу";
        field1599 = "Саженцы дуба";
        field1600 = "Саженцы березы";
        field1601 = "Саженцы ели";
        field1602 = "Саженцы джунглей";
        field1603 = "Саженцы акации";
        field1604 = "Саженцы темного дуба";
        field1605 = "Побеги бамбука";
        field1606 = "Какао-бобы";
        field1607 = "Сахарный тростник";
        field1608 = "Кактус";
        field1609 = "Сладкие ягоды";
        field1610 = "Адский нарост";
        field1530 = 0.5;
        field1531 = 0.5;
        field1532 = 0.5;
        field1534 = 0.5;
        field1535 = 0.5;
        field1536 = 0.5;
        field1538 = "Client";
        field1539 = "Packet";
        field1540 = 180.0f;
        field1542 = "Тыквы";
        field1543 = "Арбузы";
        field1544 = "Морковь";
        field1545 = "Картофель";
        field1546 = "Свеклу";
        field1547 = "Пшеницу";
        field1548 = "Саженцы дуба";
        field1549 = "Саженцы березы";
        field1550 = "Саженцы ели";
        field1551 = "Саженцы джунглей";
        field1552 = "Саженцы акации";
        field1553 = "Саженцы темного дуба";
        field1554 = "Побеги бамбука";
        field1555 = "Побеги бамбука";
        field1556 = "Какао-бобы";
        field1557 = "Сахарный тростник";
        field1558 = "Кактус";
        field1559 = "Сладкие ягоды";
        field1560 = "Адский нарост";
        field1563 = 0.5;
        field1564 = 0.5;
        field1565 = 2.0;
        field1566 = 90.0;
        field1567 = 0.01;
        field1568 = 45.0f;
        field1569 = -45.0f;
        field1611 = 90.0;
        field1612 = -90.0;
        field1613 = 90.0;
        field1614 = 0.5;
        field1615 = 0.5;
        field1616 = 0.5;
    }
}




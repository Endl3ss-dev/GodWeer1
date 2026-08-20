/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket
 *  net.minecraft.util.math.MathHelper
 */
package minced.module.visual;
import minced.core.EventHandler;

import java.awt.Color;
import java.time.LocalTime;
import minced.core.BooleanSetting;
import minced.core.SliderSetting;
import minced.core.ModeSetting;
import minced.core.EventUpdate;
import minced.core.EventPacket;
import minced.core.EventFogColor;
import minced.core.EventFogDistance;
import minced.core.ThemeColors;
import minced.core.Minced;
import minced.core.Module;
import minced.enums.Combat;
import minced.enums.MAIN;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.gl.ShaderProgramKey;
import minced.core.Shaders;

public class Ambience
extends Module {
    static float н弟;

    static MinecraftClient I = MinecraftClient.getInstance();
    static ModeSetting field1634;
    static String field1635;
    static ModeSetting field1636;
    static String field1637;
    boolean field1638;
    boolean field1639;
    boolean field1640;
    static float field1641;
    float field1642 = field1653;
    float field1643 = Float.NaN;
    static float field1644;
    static ModeSetting field1645;
    static String field1646;
    static int field1647;
    static int field1648;
    static double field1649;
    static double field1650;
    static String field1651;
    static String field1652;
    static float field1653;
    static ModeSetting field1654;
    static BooleanSetting field1655;
    static SliderSetting field1656;
    static SliderSetting field1657;
    static SliderSetting field1658;
    static SliderSetting field1659;
    static String field1660;
    static String field1661;
    static String field1662;
    static String field1663;
    static String field1664;
    static String field1665;
    static String field1666;
    static String field1667;
    static String field1668;
    static String field1669;
    static String field1670;
    static String field1671;
    static String field1672;
    static String field1673;
    static String field1674;
    static String field1675;
    static String field1676;
    static String field1677;
    static String field1678;
    static String field1679;
    static String field1680;
    static String field1681;
    static String field1682;
    static String field1683;
    static String field1684;
    static float field1685;
    static float field1686;
    static float field1687;
    static String field1688;
    static float field1689;
    static float field1690;
    static String field1691;
    static String field1692;
    static String field1693;
    static String field1694;
    static String field1695;
    static String field1696;
    static float field1697;
    static float field1698;
    static float field1699;
    static String field1700;
    static float field1701;
    static float field1702;
    static float field1703;
    static float field1704;
    static String field1705;
    static String field1706;
    static String field1707;
    static float field1708;
    static float field1709;
    static float field1710;
    float field1711;
    static float field1712;
    static float field1713;
    static String field1714;
    static float field1715;
    static String field1716;
    static float field1717;
    static float field1718;
    static float field1719;
    float field1720;
    float field1721;
    static float field1722;
    static float field1723;
    static float field1724;
    static String field1725;
    static String field1726;
    static String field1727;
    static String field1728;
    static String field1729;
    static String field1730;
    static String field1731;
    static String field1732;
    static String field1733;
    static String field1734;
    static String field1735;
    static String field1736;
    static long field1737;
    static long field1738;
    static long field1739;
    static long field1740;
    static long field1741;
    static long field1742;

    public static boolean method531() {
        Ambience v1;
        if (Ambience.I.world == null || Minced.getInstance() == null) {
            return false;
        }
        Module v0 = Minced.getInstance().getModuleManager().method187(Ambience.class);
        return v0 instanceof Ambience && (v1 = (Ambience)v0).isEnabled() && field1634.isMode(field1635);
    }

    private void method532() {
        boolean v1 = field1636.isMode(field1637);
        if (v1 != this.field1638) {
            this.field1639 = false;
            this.field1640 = false;
            this.field1642 = field1641;
            this.field1643 = Float.NaN;
        }
        this.field1638 = v1;
    }

    private void method533() {
        this.field1642 = field1644;
        this.field1643 = Float.NaN;
        this.field1639 = false;
        this.field1640 = false;
        this.field1638 = false;
    }

    public static boolean method534() {
        return false;
    }

    private long cfr_renamed_4() {
        int v4;
        int v3;
        LocalTime v1 = LocalTime.now();
        int v2 = v1.getHour();
        int v5 = v2 * 3600 + (v3 = v1.getMinute()) * 60 + (v4 = v1.getSecond());
        int v6 = (v5 - 21600) % field1647;
        if (v6 < 0) {
            v6 += field1648;
        }
        return (long)((double)v6 / field1649 * field1650);
    }

    public Ambience() {
        super(field1651, field1652, Combat.Visuals);
        this.addSettings(field1654, field1634, field1645, field1655, field1656, field1657, field1636, field1658, field1659);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.method533();
    }

    @Override
    public void onDisable() {
        this.method533();
        super.onDisable();
    }

    @EventHandler(2)
    public void method536(EventPacket param1) {
        if (param1.method2467() instanceof WorldTimeUpdateS2CPacket && !field1654.isMode(field1661)) {
            param1.method2298(true);
        }
    }

    public static void method537() {
        field1654 = new ModeSetting(field1662, field1663, field1664, field1665, field1666, field1667, field1668, field1669, field1670, field1671);
        field1634 = new ModeSetting(field1672, field1673, field1674, field1675, field1676, field1677, field1678);
        field1645 = new ModeSetting(field1679, field1680, Ambience::method531, field1681, field1682);
        field1655 = new BooleanSetting(field1683, true, Ambience::method542);
        field1656 = new SliderSetting(field1684, 1.0f, field1685, field1686, field1687, Ambience::method542);
        field1657 = new SliderSetting(field1688, 1.0f, 0.0f, field1689, field1690, Ambience::method542);
        field1636 = new ModeSetting(field1691, field1692, field1693, field1694, field1695);
        field1659 = new SliderSetting(field1696, 1.0f, field1697, field1698, field1699, () -> field1636.isMode(field1706));
        field1658 = new SliderSetting(field1700, field1701, field1702, field1703, field1704, () -> field1636.isMode(field1660), field1659);
    }

    public static boolean method538() {
        Ambience v1;
        if (Ambience.I.world == null || Minced.getInstance() == null) {
            return false;
        }
        Module v0 = Minced.getInstance().getModuleManager().method187(Ambience.class);
        return v0 instanceof Ambience && (v1 = (Ambience)v0).isEnabled() && !field1634.isMode(field1705);
    }

    @EventHandler(2)
    public void method540(EventFogDistance param1) {
        this.method532();
        if (field1636.isMode(field1707)) {
            float v2 = ((Float)field1658.getValue()).floatValue() * field1708;
            float v3 = ((Float)field1659.getValue()).floatValue() * field1709;
            v3 = Math.max(v2, v3 + field1710);
            if (!this.field1640) {
                this.field1643 = param1.method3017();
                this.field1711 = param1.method3021();
                this.field1640 = true;
            }
            this.field1643 = MathHelper.lerp((float)field1712, (float)this.field1643, (float)v2);
            this.field1711 = MathHelper.lerp((float)field1713, (float)this.field1711, (float)v3);
            param1.method3018(this.field1643);
            param1.method3019(this.field1711);
        } else if (field1636.isMode(field1714)) {
            param1.method3018(0.0f);
            param1.method3019(field1715);
        }
    }

    @EventHandler(2)
    public void method541(EventFogColor param1) {
        this.method532();
        if (!field1636.isMode(field1716)) {
            return;
        }
        int v2 = ThemeColors.method3199(MAIN.MODULE_VISUAL);
        Color v3 = new Color(v2);
        float v4 = (float)v3.getRed() / field1717;
        float v5 = (float)v3.getGreen() / field1718;
        float v6 = (float)v3.getBlue() / field1719;
        if (!this.field1639) {
            this.field1642 = param1.method304();
            this.field1720 = param1.method303();
            this.field1721 = param1.method302();
            this.field1639 = true;
        }
        this.field1642 = MathHelper.lerp((float)field1722, (float)this.field1642, (float)v4);
        this.field1720 = MathHelper.lerp((float)field1723, (float)this.field1720, (float)v5);
        this.field1721 = MathHelper.lerp((float)field1724, (float)this.field1721, (float)v6);
        param1.l(this.field1642);
        param1.method305(this.field1720);
        param1.method301(this.field1721);
    }

    public static boolean method542() {
        Ambience v1;
        if (Ambience.I.world == null || Minced.getInstance() == null) {
            return false;
        }
        Module v0 = Minced.getInstance().getModuleManager().method187(Ambience.class);
        return v0 instanceof Ambience && (v1 = (Ambience)v0).isEnabled()
            && (field1634.isMode(field1725) || field1634.isMode(field1726)
            || field1634.isMode(field1727) || field1634.isMode(field1728));
    }

    public static ShaderProgramKey getCustomSkyShader() {
        if (!method542()) {
            return null;
        }
        if (field1634.isMode(field1725)) {
            return Shaders.field8363;
        }
        if (field1634.isMode(field1726)) {
            return Shaders.field8365;
        }
        if (field1634.isMode(field1727)) {
            return Shaders.field8367;
        }
        if (field1634.isMode(field1728)) {
            return Shaders.field8361;
        }
        return null;
    }

    public static boolean isCustomSkyWorldLocked() {
        return field1655 != null && Boolean.TRUE.equals(field1655.getValue());
    }

    public static float getCustomSkyScale() {
        return field1656 == null ? 1.0f : ((Float)field1656.getValue()).floatValue();
    }

    public static float getCustomSkySpeed() {
        return field1657 == null ? 1.0f : ((Float)field1657.getValue()).floatValue();
    }

    public static boolean method543() {
        Ambience v1;
        if (Ambience.I.world == null || Minced.getInstance() == null) {
            return false;
        }
        Module v0 = Minced.getInstance().getModuleManager().method187(Ambience.class);
        return v0 instanceof Ambience && (v1 = (Ambience)v0).isEnabled() && field1634.isMode(field1728);
    }

    // Reconstructed from client.jar bytecode (CFR choked on the string-switch): applies the
    // configured time-of-day override each tick unless the mode is "Не менять".
    @EventHandler(2)
    public void method544(EventUpdate param1) {
        long v2;
        if (((String)field1654.getValue()).equals(field1729)) {
            return;
        }
        if (((String)field1654.getValue()).equals(field1730)) {
            v2 = this.cfr_renamed_4();
        } else {
            String v4 = (String)field1654.getValue();
            if (v4.equals(field1731)) {
                v2 = field1737;
            } else if (v4.equals(field1732)) {
                v2 = field1738;
            } else if (v4.equals(field1733)) {
                v2 = field1739;
            } else if (v4.equals(field1734)) {
                v2 = field1740;
            } else if (v4.equals(field1735)) {
                v2 = field1741;
            } else if (v4.equals(field1736)) {
                v2 = field1742;
            } else {
                v2 = I.world.getTimeOfDay();
            }
        }
        if (I.world instanceof ClientWorld) {
            ClientWorld v5 = I.world;
            v5.getLevelProperties().setTimeOfDay(v2);
        }
    }

    static {
        н弟 = 0.0f;
        field1651 = "Ambience";
        field1652 = "Изменяет время суток и туман в мире, создавая нужную атмосферу";
        field1653 = -1.0f;
        field1728 = "Космос";
        field1725 = "Плазма";
        field1726 = "Balatro";
        field1727 = "Лето";
        field1635 = "Лето";
        field1646 = "Ночное";
        field1705 = "Обычное";
        field1644 = -1.0f;
        field1637 = "Переопределить";
        field1641 = -1.0f;
        field1661 = "Не менять";
        field1729 = "Не менять";
        field1730 = "Время из рельной жизни";
        field1731 = "Рассвет";
        field1732 = "Утро";
        field1733 = "День";
        field1734 = "Вечер";
        field1735 = "Заход солнца";
        field1736 = "Ночь";
        field1737 = 23000L;
        field1738 = 1000L;
        field1739 = 6000L;
        field1740 = 12000L;
        field1741 = 13000L;
        field1742 = 18000L;
        field1716 = "Переопределить";
        field1717 = 255.0f;
        field1718 = 255.0f;
        field1719 = 255.0f;
        field1722 = 0.11f;
        field1723 = 0.11f;
        field1724 = 0.11f;
        field1707 = "Переопределить";
        field1708 = 42.67f;
        field1709 = 88.67f;
        field1710 = 5.0f;
        field1712 = 0.11f;
        field1713 = 0.11f;
        field1714 = "Очистить";
        field1715 = Float.MAX_VALUE;
        field1647 = 86400;
        field1648 = 86400;
        field1649 = 86400.0;
        field1650 = 24000.0;
        field1660 = "Переопределить";
        field1706 = "Переопределить";
        field1662 = "Время";
        field1663 = "Не менять";
        field1664 = "Рассвет";
        field1665 = "Утро";
        field1666 = "День";
        field1667 = "Вечер";
        field1668 = "Заход солнца";
        field1669 = "Ночь";
        field1670 = "Время из рельной жизни";
        field1671 = "Не менять";
        field1672 = "Небо";
        field1673 = "Обычное";
        field1674 = "Обычное";
        field1675 = "Космос";
        field1676 = "Плазма";
        field1677 = "Balatro";
        field1678 = "Лето";
        field1679 = "Лето: вид";
        field1680 = "Обычное";
        field1681 = "Обычное";
        field1682 = "Ночное";
        field1683 = "Шейд.небо: к миру";
        field1684 = "Шейд.небо: масштаб";
        field1685 = 0.2f;
        field1686 = 3.0f;
        field1687 = 0.05f;
        field1688 = "Шейд.небо: скорость";
        field1689 = 3.0f;
        field1690 = 0.05f;
        field1691 = "Туман";
        field1692 = "Ничего не делать";
        field1693 = "Ничего не делать";
        field1694 = "Очистить";
        field1695 = "Переопределить";
        field1696 = "Конец тумана";
        field1697 = 0.1f;
        field1698 = 1.5f;
        field1699 = 0.1f;
        field1700 = "Начало тумана";
        field1701 = 0.5f;
        field1702 = 0.1f;
        field1703 = 1.5f;
        field1704 = 0.1f;
        Ambience.method537();
    }
}




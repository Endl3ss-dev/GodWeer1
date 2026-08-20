/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 */
package minced.module.visual;
import java.util.function.BooleanSupplier;
import minced.core.BooleanSetting;
import minced.core.SliderSetting;
import minced.core.HandsShader;
import minced.core.ModeSetting;
import minced.core.Module;
import minced.enums.Combat;

public class ShaderHands
extends Module {
    static float н7;

    SliderSetting field6628;
    SliderSetting field6629;
    ModeSetting field6630 = new ModeSetting(field6657, field6658, field6659, field6660);
    static String field6631;
    BooleanSetting field6632;
    SliderSetting field6633;
    SliderSetting field6634;
    SliderSetting field6635;
    BooleanSetting field6636;
    BooleanSetting field6637;
    static HandsShader field6638;
    BooleanSetting field6639;
    SliderSetting field6640;
    static float field6641;
    SliderSetting field6642;
    static float field6643;
    SliderSetting field6644;
    static float field6645;
    SliderSetting field6646;
    SliderSetting field6647;
    SliderSetting field6648;
    static float field6649;
    static float field6650;
    SliderSetting field6651;
    SliderSetting field6652;
    SliderSetting field6653 = new SliderSetting(field6661, field6662, field6663, field6664, field6665, this::method1584);
    SliderSetting field6654;
    static String field6655;
    static String field6656;
    static String field6657;
    static String field6658;
    static String field6659;
    static String field6660;
    static String field6661;
    static float field6662;
    static float field6663;
    static float field6664;
    static float field6665;
    static String field6666;
    static float field6667;
    static float field6668;
    static String field6669;
    static float field6670;
    static float field6671;
    static float field6672;
    static float field6673;
    static String field6674;
    static float field6675;
    static String field6676;
    static float field6677;
    static float field6678;
    static String field6679;
    static float field6680;
    static float field6681;
    static String field6682;
    static float field6683;
    static float field6684;
    static String field6685;
    static float field6686;
    static String field6687;
    static String field6688;
    static float field6689;
    static float field6690;
    static float field6691;
    static float field6692;
    static String field6693;
    static String field6694;
    static float field6695;
    static float field6696;
    static float field6697;
    static float field6698;
    static String field6699;
    static float field6700;
    static float field6701;
    static float field6702;
    static String field6703;
    static float field6704;
    static float field6705;
    static float field6706;
    static String field6707;
    static float field6708;
    static float field6709;
    static String field6710;
    static float field6711;
    static float field6712;
    static String field6713;
    static String field6714;
    static float field6715;
    static float field6716;
    static float field6717;
    SliderSetting field6718;
    static String field6719;
    static String field6720;
    static float field6721;
    static float field6722;
    static float field6723;

    public float method1582() {
        return ((Float)this.field6628.getValue()).floatValue();
    }

    public float method1583() {
        return ((Float)this.field6629.getValue()).floatValue();
    }

    public boolean method1584() {
        return this.field6630.isMode(field6631);
    }

    public float method1586() {
        return ((Float)this.field6633.getValue()).floatValue();
    }

    public float method1587() {
        return ((Float)this.field6634.getValue()).floatValue();
    }

    public float method1588() {
        return ((Float)this.field6635.getValue()).floatValue();
    }

    public boolean method1589() {
        return this.method1597() && (Boolean)this.field6636.getValue() != false;
    }

    public boolean method1590() {
        return this.method1597() && (Boolean)this.field6636.getValue() != false;
    }

    public boolean method1591() {
        return (Boolean)this.field6637.getValue();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        field6638.method1930();
        field6638.method1916();
    }

    public boolean method1592() {
        return (Boolean)this.field6639.getValue();
    }

    public boolean method1594() {
        return this.method1597() || ((Float)this.field6640.getValue()).floatValue() > field6641 && ((Float)this.field6642.getValue()).floatValue() > field6643;
    }

    public boolean method1596() {
        return (Boolean)this.field6632.getValue();
    }

    public boolean method1597() {
        return ((Float)this.field6644.getValue()).floatValue() > field6645;
    }

    public float method1598() {
        return ((Float)this.field6646.getValue()).floatValue();
    }

    public float method1599() {
        return ((Float)this.field6647.getValue()).floatValue();
    }

    public float method1600() {
        return ((Float)this.field6648.getValue()).floatValue();
    }

    public float method1601() {
        return field6649 + ((Float)this.field6644.getValue()).floatValue() * field6650;
    }

    public static void method1602() {
        field6638 = HandsShader.method1933();
    }

    public float method1603() {
        return ((Float)this.field6640.getValue()).floatValue();
    }

    public float method1604() {
        return ((Float)this.field6651.getValue()).floatValue();
    }

    public float method1605() {
        return ((Float)this.field6642.getValue()).floatValue();
    }

    public float method1606() {
        return ((Float)this.field6652.getValue()).floatValue();
    }

    public float method1607() {
        return ((Float)this.field6653.getValue()).floatValue();
    }

    public int method1608() {
        return Math.max(1, Math.round(((Float)this.field6654.getValue()).floatValue()));
    }

    public ShaderHands() {
        super(field6655, field6656, Combat.Visuals);
        this.field6629 = new SliderSetting(field6666, 1.0f, 1.0f, field6667, field6668, this::method1584);
        this.field6628 = new SliderSetting(field6669, field6670, field6671, field6672, field6673);
        this.field6644 = new SliderSetting(field6674, 1.0f, 0.0f, 1.0f, field6675);
        this.field6654 = new SliderSetting(field6676, field6677, 1.0f, field6678, 1.0f, this::method1597);
        this.field6648 = new SliderSetting(field6679, 2.0f, 1.0f, field6680, field6681, this::method1597);
        this.field6640 = new SliderSetting(field6682, field6683, 0.0f, 1.0f, field6684);
        this.field6642 = new SliderSetting(field6685, 1.0f, 0.0f, 1.0f, field6686);
        this.field6637 = new BooleanSetting(field6687, false, this::method1594);
        this.field6635 = new SliderSetting(field6688, field6689, field6690, field6691, field6692, () -> this.method1594() && (Boolean)this.field6637.getValue() != false);
        this.field6636 = new BooleanSetting(field6693, true, this::method1597);
        this.field6647 = new SliderSetting(field6694, field6695, field6696, field6697, field6698, this::method1590);
        this.field6652 = new SliderSetting(field6699, field6700, 0.0f, field6701, field6702, this::method1590);
        this.field6646 = new SliderSetting(field6703, field6704, 0.0f, field6705, field6706, this::method1590);
        this.field6634 = new SliderSetting(field6707, 0.0f, 0.0f, field6708, field6709, this::method1590);
        this.field6651 = new SliderSetting(field6710, 0.0f, 0.0f, field6711, field6712, this::method1590);
        this.field6639 = new BooleanSetting(field6713, true, this::method1590);
        this.field6718 = new SliderSetting(field6714, field6715, 1.0f, field6716, field6717, () -> this.method1590() && (Boolean)this.field6639.getValue() != false);
        this.field6632 = new BooleanSetting(field6719, false, this::method1590);
        this.field6633 = new SliderSetting(field6720, field6721, field6722, 1.0f, field6723, () -> this.method1590() && (Boolean)this.field6632.getValue() != false);
        this.addSettings(this.field6653, this.field6629, this.field6628, this.field6644, this.field6654, this.field6648, this.field6640, this.field6642, this.field6636, this.field6647, this.field6652, this.field6646, this.field6634, this.field6651, this.field6639, this.field6718, this.field6632, this.field6633);
    }

    public float method1609() {
        return ((Float)this.field6644.getValue()).floatValue();
    }

    public float method1610() {
        return ((Float)this.field6718.getValue()).floatValue();
    }

    static {
        н7 = 0.0f;
        field6655 = "Shader Hands";
        field6656 = "Красивый шейдер на руки и предметы";
        field6657 = "Режим";
        field6658 = "Красивый";
        field6659 = "Свечение";
        field6660 = "Красивый";
        field6661 = "Скорость волн";
        field6662 = 1.2f;
        field6663 = 0.1f;
        field6664 = 5.0f;
        field6665 = 0.1f;
        field6666 = "Частота волн";
        field6667 = 3.0f;
        field6668 = 0.1f;
        field6669 = "Ширина обводки";
        field6670 = 1.2f;
        field6671 = 0.1f;
        field6672 = 5.0f;
        field6673 = 0.1f;
        field6674 = "Сила свечения";
        field6675 = 0.1f;
        field6676 = "Радиус свечения";
        field6677 = 4.0f;
        field6678 = 4.0f;
        field6679 = "Разброс свечения";
        field6680 = 4.0f;
        field6681 = 0.1f;
        field6682 = "Заливка";
        field6683 = 0.6f;
        field6684 = 0.01f;
        field6685 = "Прозрачность";
        field6686 = 0.05f;
        field6687 = "Цвет предмета";
        field6688 = "Насыщенность цвета";
        field6689 = 1.4f;
        field6690 = 0.5f;
        field6691 = 3.0f;
        field6692 = 0.1f;
        field6693 = "Шлейф";
        field6694 = "Скорость затухания";
        field6695 = 0.012f;
        field6696 = 0.002f;
        field6697 = 0.2f;
        field6698 = 0.002f;
        field6699 = "Подъём";
        field6700 = 0.2f;
        field6701 = 1.5f;
        field6702 = 0.05f;
        field6703 = "Качание";
        field6704 = 0.05f;
        field6705 = 0.2f;
        field6706 = 0.005f;
        field6707 = "Турбулентность";
        field6708 = 0.6f;
        field6709 = 0.01f;
        field6710 = "Мерцание";
        field6711 = 0.2f;
        field6712 = 0.01f;
        field6713 = "Сдув при ударе";
        field6714 = "Сила сдува";
        field6715 = 4.0f;
        field6716 = 10.0f;
        field6717 = 0.5f;
        field6719 = "Шлейф модели";
        field6720 = "Прозрачность модели";
        field6721 = 0.6f;
        field6722 = 0.1f;
        field6723 = 0.05f;
        field6631 = "Красивый";
        field6645 = 0.001f;
        field6641 = 0.001f;
        field6643 = 0.001f;
        field6649 = 0.5f;
        field6650 = 1.5f;
        ShaderHands.method1602();
    }
}


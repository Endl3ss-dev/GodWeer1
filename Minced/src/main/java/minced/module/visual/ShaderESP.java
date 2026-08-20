/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gl.Framebuffer
 *  net.minecraft.client.render.OutlineVertexConsumerProvider
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.PlayerEntity
 */
package minced.module.visual;
import java.util.function.BooleanSupplier;
import minced.core.ColorUtil;
import minced.core.SliderSetting;
import minced.core.ShaderEspRenderer;
import minced.core.ModeSetting;
import minced.core.ThemeColors;
import minced.core.Module;
import minced.enums.Combat;
import minced.enums.MAIN;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.OutlineVertexConsumerProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public class ShaderESP
extends Module {

public static boolean cfr_renamed;

    ModeSetting field1847;
    static String field1848;
    static String field1849;
    SliderSetting field1850;
    SliderSetting field1851;
    ModeSetting field1852;
    static String field1853;
    SliderSetting field1854;
    static float field1855;
    public static Entity field1856;
    public static boolean field1857;
    static String field1858;
    static String field1859;
    static String field1860;
    static String field1861;
    static String field1862;
    static String field1863;
    static String field1864;
    static String field1865;
    static String field1866;
    static String field1867;
    static String field1868;
    static String field1869;
    static String field1870;
    static String field1871;
    static float field1872;
    static String field1873;
    static String field1874;
    static String field1875;
    static MinecraftClient I = MinecraftClient.getInstance();
    static String field1876;
    static String field1877;
    static String field1878;
    static String field1879;
    static String field1880;
    static String field1881;
    ModeSetting field1882 = new ModeSetting(field1878, field1879, field1880, field1881);
    static String field1883;
    static String field1884;
    static String field1885;
    static String field1886;
    static String field1887;
    static String field1888;
    static String field1889;
    static String field1890;
    static String field1891;
    static String field1892;
    static String field1893;
    static String field1894;
    static float field1895;
    static float field1896;
    static String field1897;
    static float field1898;
    static float field1899;
    public static ShaderESP field1900;
    static String field1901;
    static String field1902;
    static String field1903;
    ShaderEspRenderer field1904;
    static String field1905;
    static String field1906;
    static float field1907;
    static String field1908;
    static String field1909;
    static String field1910;
    static String field1911;
    OutlineVertexConsumerProvider field1912;
    static String field1913;
    static String field1914;
    static String field1915;

    public boolean method567(Entity param1) {
        return this.isEnabled() && param1 instanceof PlayerEntity && this.method580();
    }

    private int method568() {
        int n;
        String v1 = (String)this.field1847.getValue();
        int v2 = -1;
        switch (v1.hashCode()) {
            case 1190764860: {
                if (!v1.equals(field1848)) break;
                v2 = 0;
                break;
            }
            case 1856446121: {
                if (!v1.equals(field1849)) break;
                v2 = 1;
            }
        }
        switch (v2) {
            case 0: {
                n = 2;
                break;
            }
            case 1: {
                n = 3;
                break;
            }
            default: {
                n = 1;
            }
        }
        return n;
    }

    private float[] method569() {
        int v1 = ColorUtil.method1853(ThemeColors.method3199(MAIN.MODULE_VISUAL), 1.0f);
        return ColorUtil.method1849(v1);
    }

    public void method570() {
        ShaderEspRenderer v1 = this.method577();
        int v2 = this.method582();
        int v3 = this.method568();
        float[] v4 = this.method569();
        float[] v5 = this.method578();
        int v6 = (int)((Float)this.field1850.getValue()).floatValue();
        float v7 = ((Float)this.field1851.getValue()).floatValue();
        int v8 = v2 == 4 && ((String)this.field1852.getValue()).equals(field1853) ? 1 : 0;
        float v9 = ((Float)this.field1854.getValue()).floatValue() / field1855;
        v1.method2067(param8 -> {
            param8.method2090(field1858).set(1.0f);
            param8.method2090(field1859).setAndFlip(v4[0], v4[1], v4[2], v4[3]);
            param8.method2090(field1860).setAndFlip(v5[0], v5[1], v5[2], v5[3]);
            param8.method2090(field1861).set(v2);
            param8.method2090(field1862).set(v3);
            param8.method2090(field1863).set(0);
            param8.method2090(field1864).set(0.0f);
            param8.method2090(field1865).set(v6);
            param8.method2090(field1866).set(v7);
            param8.method2090(field1867).set(1.0f);
            param8.method2090(field1868).set(v8);
            param8.method2090(field1869).set(0);
            param8.method2090(field1870).set(v9);
            param8.method2090(field1871).set(field1872);
            param8.method2090(field1873).set(0);
            param8.method2090(field1874).set(1.0f);
            param8.method2090(field1875).set(1.0f);
        });
    }

    public static void method571() {
        field1856 = null;
        field1857 = false;
    }

    public void method573() {
        if (!field1857) {
            return;
        }
        I.getFramebuffer().beginWrite(false);
        this.method570();
        this.method577().method2062();
        field1857 = false;
    }

    public ShaderESP() {
        super(field1876, field1877, Combat.Visuals);
        this.field1847 = new ModeSetting(field1883, field1884, field1885, field1886);
        this.field1852 = new ModeSetting(field1887, field1888, field1889, field1890, field1891, field1892);
        this.field1850 = new SliderSetting(field1893, 1.0f, 1.0f, 2.0f, 1.0f, () -> ((String)this.field1852.getValue()).equals(field1902) || ((String)this.field1852.getValue()).equals(field1903));
        this.field1851 = new SliderSetting(field1894, 1.0f, 1.0f, field1895, field1896, () -> ((String)this.field1852.getValue()).equals(field1908) || ((String)this.field1852.getValue()).equals(field1909));
        this.field1854 = new SliderSetting(field1897, field1898, 0.0f, field1899, 1.0f);
        field1900 = this;
        this.addSettings(this.field1882, this.field1847, this.field1852, this.field1850, this.field1851, this.field1854);
    }

    public boolean method574(boolean param1) {
        if (this.isEnabled() && this.method580() && param1) {
            Framebuffer v2 = ShaderESP.I.worldRenderer.getEntityOutlinesFramebuffer();
            this.method577().method2064();
            this.method581().draw();
            this.method577().method2060();
            if (!this.method575()) {
                v2.clear();
            }
            return false;
        }
        return !param1;
    }

    public boolean method575() {
        return this.field1882.isMode(field1901);
    }

    public ShaderEspRenderer method577() {
        if (this.field1904 == null) {
            this.field1904 = new ShaderEspRenderer(field1905, field1906);
            this.field1904.method2061();
        }
        return this.field1904;
    }

    private float[] method578() {
        int v1 = ColorUtil.method1853(ThemeColors.method3199(MAIN.MODULE_VISUAL), field1907);
        return ColorUtil.method1849(v1);
    }

    private boolean method580() {
        return this.field1882.isMode(field1910) || this.field1882.isMode(field1911);
    }

    public OutlineVertexConsumerProvider method581() {
        if (this.field1912 == null) {
            this.field1912 = new OutlineVertexConsumerProvider(I.getBufferBuilders().getEntityVertexConsumers());
        }
        return this.field1912;
    }

    private int method582() {
        int n;
        String v1 = (String)this.field1852.getValue();
        int v2 = -1;
        switch (v1.hashCode()) {
            case 1032186918: {
                if (!v1.equals(field1913)) break;
                v2 = 0;
                break;
            }
            case -235163667: {
                if (!v1.equals(field1914)) break;
                v2 = 1;
                break;
            }
            case 867762587: {
                if (!v1.equals(field1915)) break;
                v2 = 2;
            }
        }
        switch (v2) {
            case 0: {
                n = 3;
                break;
            }
            case 1: 
            case 2: {
                n = 4;
                break;
            }
            default: {
                n = 0;
            }
        }
        return n;
    }

    static {
        cfr_renamed = false;
        field1857 = false;
        field1876 = "Shader ESP";
        field1877 = "Красивые эффекты для энтити";
        field1878 = "Режим";
        field1879 = "С шейдером";
        field1880 = "С шейдером";
        field1881 = "Только шейдер";
        field1883 = "Режим цвета";
        field1884 = "Интерфейс";
        field1885 = "Интерфейс";
        field1886 = "Радуга";
        field1887 = "Эффект обводки";
        field1888 = "Выкл";
        field1889 = "Контур";
        field1890 = "Свечение";
        field1891 = "Свечение с контуром";
        field1892 = "Выкл";
        field1893 = "Качество свечения";
        field1894 = "Радиус свечения";
        field1895 = 10.0f;
        field1896 = 0.1f;
        field1897 = "Заливка";
        field1898 = 50.0f;
        field1899 = 100.0f;
        field1905 = "minced";
        field1906 = "entityshader/postprocess";
        field1853 = "Свечение с контуром";
        field1855 = 100.0f;
        field1913 = "Контур";
        field1914 = "Двойное свечение";
        field1915 = "Свечение с контуром";
        field1848 = "Радуга";
        field1849 = "Два цвета";
        field1907 = 0.5f;
        field1910 = "С шейдером";
        field1911 = "Только шейдер";
        field1901 = "С шейдером";
        field1858 = "timeMult";
        field1859 = "color1";
        field1860 = "color2";
        field1861 = "noAlphaMode";
        field1862 = "colorMode";
        field1863 = "blurState";
        field1864 = "blurMix";
        field1865 = "glowQuality";
        field1866 = "glowRadius";
        field1867 = "glowAlphaMult";
        field1868 = "glowThinOutline";
        field1869 = "glowLogic";
        field1870 = "fillAlphaMult";
        field1871 = "colorRadius";
        field1872 = 0.1f;
        field1873 = "centred";
        field1874 = "saturation";
        field1875 = "brightness";
        field1908 = "Свечение";
        field1909 = "Свечение с контуром";
        field1902 = "Свечение";
        field1903 = "Свечение с контуром";
        ShaderESP.method571();
    }
}




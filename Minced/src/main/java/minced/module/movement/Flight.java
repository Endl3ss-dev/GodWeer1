/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 */
package minced.module.movement;
import minced.core.EventHandler;

import java.util.function.BooleanSupplier;
import minced.core.SliderSetting;
import minced.core.ModeSetting;
import minced.core.EventMotion;
import minced.core.ClientState;
import minced.core.Module;
import minced.enums.Combat;
import minced.core.Minced;
import minced.core.TimerUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.client.MinecraftClient;

public class Flight
extends Module {
    ModeSetting field2121 = new ModeSetting(field2125, field2126, field2127, field2128, field2129, field2130, field2131, field2132);
    static String field2122;
    static String field2123;
    static String field2124;
    static String field2125;
    static String field2126 = "Обычный";
    static String field2127;
    static String field2128;
    static String field2129;
    static String field2130;
    static String field2131;
    static String field2132;
    static String field2133;
    static float field2134;
    static float field2135;
    static float field2136;
    static float field2137;
    SliderSetting field2138 = new SliderSetting(field2133, field2134, field2135, field2136, field2137, () -> !this.field2121.isMode(field2165) && !this.field2121.isMode(field2166) && !this.field2121.isMode(field2167) && !this.field2121.isMode(field2168));
    static String field2139;
    static float field2140;
    static float field2141;
    static float field2142;
    static float field2143;
    SliderSetting field2144 = new SliderSetting(field2139, field2140, field2141, field2142, field2143, () -> !this.field2121.isMode(field2169) && !this.field2121.isMode(field2170) && !this.field2121.isMode(field2171) && !this.field2121.isMode(field2172));
    static String field2145;
    static float field2146;
    static float field2147;
    static float field2148;
    static float field2149;
    SliderSetting field2150 = new SliderSetting(field2145, field2146, field2147, field2148, field2149, () -> this.field2121.isMode(field2122));
    ClientState field2151 = new ClientState();
    int field2152 = 0;
    static MinecraftClient I = MinecraftClient.getInstance();
    static String field2153;
    static String field2154;
    static String field2155;
    static String field2156;
    static String field2157;
    static String field2158;
    static double field2159;
    static double field2160;
    static double field2161;
    static double field2162;
    static double field2163;
    static double field2164;
    static String field2165;
    static String field2166;
    static String field2167;
    static String field2168;
    static String field2169;
    static String field2170;
    static String field2171;
    static String field2172;

    public Flight() {
        super(field2123, field2124, Combat.Movement);
        this.addSettings(this.field2121, this.field2138, this.field2144, this.field2150);
    }

    @EventHandler(2)
    public void method592(EventMotion param1) {
        if (Flight.I.player == null || Flight.I.player.input == null) {
            return;
        }
        if (Minced.getInstance().getModuleManager().method187(FreeCamera.class).isEnabled()) {
            return;
        }
        boolean v2 = Flight.I.options.sneakKey.isPressed();
        boolean v3 = Flight.I.options.jumpKey.isPressed();
        float v4 = ((Float)this.field2144.getValue()).floatValue();
        float v5 = ((Float)this.field2150.getValue()).floatValue();
        boolean hasHorizontalInput = Flight.I.player.input.movementForward != 0.0f
                || Flight.I.player.input.movementSideways != 0.0f;
        String v6 = (String)this.field2121.getValue();
        int v7 = -1;
        switch (v6.hashCode()) {
            case 1444284741: {
                if (!v6.equals(field2153)) break;
                v7 = 0;
                break;
            }
            case 828965756: {
                if (!v6.equals(field2154)) break;
                v7 = 1;
                break;
            }
            case 7499713: {
                if (!v6.equals(field2155)) break;
                v7 = 2;
                break;
            }
            case 1177584970: {
                if (!v6.equals(field2156)) break;
                v7 = 3;
                break;
            }
            case 1567264236: {
                if (!v6.equals(field2157)) break;
                v7 = 4;
                break;
            }
            case 37047649: {
                if (!v6.equals(field2158)) break;
                v7 = 5;
            }
        }
        switch (v7) {
            case 0: {
                double v8 = field2159;
                if (v2) {
                    v8 = -v4;
                } else if (v3) {
                    v8 = v4;
                }
                if (Flight.I.player.isOnGround()) {
                    Flight.I.player.jump();
                }
                TimerUtil.method3546(((Float)this.field2138.getValue()).floatValue());
                Flight.I.player.setVelocity(
                        hasHorizontalInput ? Flight.I.player.getVelocity().x : 0.0,
                        v8,
                        hasHorizontalInput ? Flight.I.player.getVelocity().z : 0.0);
                break;
            }
            case 1: {
                double v10 = 0.0;
                if (v2) {
                    v10 = -v4;
                } else if (v3) {
                    v10 = v4;
                }
                if (Flight.I.player.isOnGround()) {
                    Flight.I.player.jump();
                }
                TimerUtil.method3546(((Float)this.field2138.getValue()).floatValue());
                Flight.I.player.setVelocity(
                        hasHorizontalInput ? Flight.I.player.getVelocity().x : 0.0,
                        v10,
                        hasHorizontalInput ? Flight.I.player.getVelocity().z : 0.0);
                break;
            }
            case 2: {
                double v12 = Flight.I.player.getVelocity().y;
                if (v2) {
                    v12 = -v4;
                } else if (v3) {
                    v12 = v4;
                }
                TimerUtil.method3546(((Float)this.field2138.getValue()).floatValue());
                Flight.I.player.setVelocity(
                        hasHorizontalInput ? Flight.I.player.getVelocity().x : 0.0,
                        v12,
                        hasHorizontalInput ? Flight.I.player.getVelocity().z : 0.0);
                break;
            }
            case 3: {
                if (!v3) break;
                Flight.I.player.jump();
                break;
            }
            case 4: {
                if (!Flight.I.player.getAbilities().flying) break;
                Flight.I.player.setVelocity(0.0, 0.0, 0.0);
                boolean v15 = v2 || v3;
                double v16 = 0.0;
                if (v2) {
                    v16 = field2160;
                } else if (v3) {
                    v16 = field2162;
                }
                TimerUtil.method3546(v15 ? field2163 : field2164);
                Flight.I.player.setVelocity(Flight.I.player.getVelocity().x, v16, Flight.I.player.getVelocity().z);
                break;
            }
            case 5: {
                double v14 = Flight.I.player.getVelocity().y;
                Item v16 = ((ItemStack)Flight.I.player.getInventory().armor.get(2)).getItem();
                if (v16 != Items.ELYTRA || !Flight.I.player.isGliding()) break;
                Flight.I.player.setVelocity(0.0, v14 + (double)v5, 0.0);
                Flight.I.options.jumpKey.setPressed(false);
            }
        }
    }

    static {
        field2123 = "Flight";
        field2124 = "Позволяет летать, облегчая перемещение";
        field2125 = "Тип";
        field2127 = "Скольжение";
        field2128 = "Прыжки";
        field2129 = "Движение";
        field2130 = "Обычный";
        field2131 = "ReallyWorld Dragon";
        field2132 = "Элитра Y";
        field2133 = "Скорость";
        field2134 = 1.5f;
        field2135 = 0.1f;
        field2136 = 10.0f;
        field2137 = 0.1f;
        field2139 = "Скорость по Y";
        field2140 = 1.5f;
        field2141 = 0.1f;
        field2142 = 10.0f;
        field2143 = 0.1f;
        field2145 = "Скорость по Y";
        field2146 = 0.1f;
        field2147 = 0.05f;
        field2148 = 0.5f;
        field2149 = 0.05f;
        field2153 = "Скольжение";
        field2154 = "Обычный";
        field2155 = "Движение";
        field2156 = "Прыжки";
        field2157 = "ReallyWorld Dragon";
        field2158 = "Элитра Y";
        field2159 = -0.005;
        field2160 = -0.74;
        field2161 = -1.0;
        field2162 = 0.74;
        field2163 = 0.7;
        field2164 = 1.05;
        field2122 = "Элитра Y";
        field2169 = "Элитра Y";
        field2170 = "Прыжки";
        field2171 = "ReallyWorld Dragon";
        field2172 = "Grim Glide";
        field2165 = "Элитра Y";
        field2166 = "Прыжки";
        field2167 = "ReallyWorld Dragon";
        field2168 = "Grim Glide";
    }
}



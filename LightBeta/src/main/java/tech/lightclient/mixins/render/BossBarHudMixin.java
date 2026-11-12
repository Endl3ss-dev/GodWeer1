package tech.lightclient.mixins.render;


import tech.lightclient.mixins.accessors.BossBarHudAccessor;
import tech.lightclient.ui.hud.CustomBossBarHud;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.network.packet.s2c.play.BossBarS2CPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.UUID;

@Mixin(BossBarHud.class)
public class BossBarHudMixin {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void light$renderCustomBossBar(DrawContext context, CallbackInfo ci) {
        if (!CustomBossBarHud.shouldUseCustomBossBar()) {
            return;
        }

        CustomBossBarHud.markActive((BossBarHud) (Object) this);
        ci.cancel();
    }

    @Inject(method = "handlePacket", at = @At("HEAD"), cancellable = true)
    private void light$handleBossPacketSafe(BossBarS2CPacket packet, CallbackInfo ci) {
        Map<UUID, ClientBossBar> bossBars = ((BossBarHudAccessor) this).getBossBars();
        if (bossBars == null) {
            return;
        }

        boolean handled = false;
        try {
            packet.accept(new BossBarS2CPacket.Consumer() {
                @Override
                public void add(UUID uuid, Text name, float percent, BossBar.Color color, BossBar.Style style, boolean darkenSky, boolean dragonMusic, boolean thickenFog) {
                    bossBars.put(uuid, new ClientBossBar(uuid, name, percent, color, style, darkenSky, dragonMusic, thickenFog));
                }

                @Override
                public void remove(UUID uuid) {
                    bossBars.remove(uuid);
                }

                @Override
                public void updateProgress(UUID uuid, float percent) {
                    ClientBossBar bar = bossBars.get(uuid);
                    if (bar != null) {
                        bar.setPercent(percent);
                    }
                }

                @Override
                public void updateName(UUID uuid, Text name) {
                    ClientBossBar bar = bossBars.get(uuid);
                    if (bar != null) {
                        bar.setName(name);
                    }
                }

                @Override
                public void updateStyle(UUID uuid, BossBar.Color color, BossBar.Style style) {
                    ClientBossBar bar = bossBars.get(uuid);
                    if (bar != null) {
                        bar.setColor(color);
                        bar.setStyle(style);
                    }
                }

                @Override
                public void updateProperties(UUID uuid, boolean darkenSky, boolean dragonMusic, boolean thickenFog) {
                    ClientBossBar bar = bossBars.get(uuid);
                    if (bar != null) {
                        bar.setDarkenSky(darkenSky);
                        bar.setDragonMusic(dragonMusic);
                        bar.setThickenFog(thickenFog);
                    }
                }
            });
            handled = true;
        } catch (Throwable ignored) {
        }

        if (handled) {
            ci.cancel();
        }
    }
}

/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1113
 *  net.minecraft.class_2960
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package ru.decide.module.impl.utils;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1113;
import net.minecraft.class_2960;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.module.api.ModuleInfo;
import ru.decide.module.api.settings.impl.BooleanSetting;
import ru.decide.module.api.settings.impl.SliderSetting;
import ru.decide.module.impl.utils.ScaledSoundInstance;

@ModuleInfo(name="Sound Controller", desc="\u0413\u0440\u043e\u043c\u043a\u043e\u0441\u0442\u044c \u043e\u0442\u0434\u0435\u043b\u044c\u043d\u044b\u0445 \u0437\u0432\u0443\u043a\u043e\u0432", category=Category.UTILITIES)
@Environment(value=EnvType.CLIENT)
public class SoundController
extends Module {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"DecideVisuals:SoundController");
    private static SoundController instance;
    private final Map<class_2960, Group> bindings = new LinkedHashMap<class_2960, Group>();
    private final Set<String> seen = new HashSet<String>();
    public final BooleanSetting firework = this.toggle("\u0424\u0435\u0439\u0435\u0440\u0432\u0435\u0440\u043a");
    public final SliderSetting fireworkVolume = this.volume("\u0413\u0440\u043e\u043c\u043a\u043e\u0441\u0442\u044c \u0444\u0435\u0439\u0435\u0440\u0432\u0435\u0440\u043a\u0430", this.firework);
    public final BooleanSetting expBottleThrow = this.toggle("\u0411\u0440\u043e\u0441\u043e\u043a \u043f\u0443\u0437\u044b\u0440\u044c\u043a\u0430 \u043e\u043f\u044b\u0442\u0430");
    public final SliderSetting expBottleThrowVolume = this.volume("\u0413\u0440\u043e\u043c\u043a\u043e\u0441\u0442\u044c \u0431\u0440\u043e\u0441\u043a\u0430", this.expBottleThrow);
    public final BooleanSetting expBottleBreak = this.toggle("\u0420\u0430\u0437\u0431\u0438\u0432\u0430\u043d\u0438\u0435 \u043f\u0443\u0437\u044b\u0440\u044c\u043a\u0430");
    public final SliderSetting expBottleBreakVolume = this.volume("\u0413\u0440\u043e\u043c\u043a\u043e\u0441\u0442\u044c \u0440\u0430\u0437\u0431\u0438\u0432\u0430\u043d\u0438\u044f", this.expBottleBreak);
    public final BooleanSetting expPickup = this.toggle("\u041f\u043e\u0434\u0431\u043e\u0440 \u043e\u043f\u044b\u0442\u0430");
    public final SliderSetting expPickupVolume = this.volume("\u0413\u0440\u043e\u043c\u043a\u043e\u0441\u0442\u044c \u043f\u043e\u0434\u0431\u043e\u0440\u0430", this.expPickup);
    public final BooleanSetting trident = this.toggle("\u0422\u0440\u0435\u0437\u0443\u0431\u0435\u0446");
    public final SliderSetting tridentVolume = this.volume("\u0413\u0440\u043e\u043c\u043a\u043e\u0441\u0442\u044c \u0442\u0440\u0435\u0437\u0443\u0431\u0446\u0430", this.trident);
    public final BooleanSetting fishingRod = this.toggle("\u0423\u0434\u043e\u0447\u043a\u0430");
    public final SliderSetting fishingRodVolume = this.volume("\u0413\u0440\u043e\u043c\u043a\u043e\u0441\u0442\u044c \u0443\u0434\u043e\u0447\u043a\u0438", this.fishingRod);
    public final BooleanSetting hit = this.toggle("\u0417\u0432\u0443\u043a \u0443\u0434\u0430\u0440\u0430");
    public final SliderSetting hitVolume = this.volume("\u0413\u0440\u043e\u043c\u043a\u043e\u0441\u0442\u044c \u0443\u0434\u0430\u0440\u0430", this.hit);
    public final BooleanSetting crit = this.toggle("\u0417\u0432\u0443\u043a \u043a\u0440\u0438\u0442\u0430");
    public final SliderSetting critVolume = this.volume("\u0413\u0440\u043e\u043c\u043a\u043e\u0441\u0442\u044c \u043a\u0440\u0438\u0442\u0430", this.crit);
    public final BooleanSetting splash = this.toggle("\u0421\u043f\u043b\u0435\u0448");
    public final SliderSetting splashVolume = this.volume("\u0413\u0440\u043e\u043c\u043a\u043e\u0441\u0442\u044c \u0441\u043f\u043b\u0435\u0448\u0430", this.splash);
    public final BooleanSetting funtimeCrit = this.toggle("\u041a\u0440\u0438\u0442 FunTime");
    public final SliderSetting funtimeCritVolume = this.volume("\u0413\u0440\u043e\u043c\u043a\u043e\u0441\u0442\u044c FunTime", this.funtimeCrit);
    public final BooleanSetting totem = this.toggle("\u0422\u043e\u0442\u0435\u043c");
    public final SliderSetting totemVolume = this.volume("\u0413\u0440\u043e\u043c\u043a\u043e\u0441\u0442\u044c \u0442\u043e\u0442\u0435\u043c\u0430", this.totem);
    public final BooleanSetting explosion = this.toggle("\u0412\u0437\u0440\u044b\u0432");
    public final SliderSetting explosionVolume = this.volume("\u0413\u0440\u043e\u043c\u043a\u043e\u0441\u0442\u044c \u0432\u0437\u0440\u044b\u0432\u0430", this.explosion);
    public final BooleanSetting enderPearl = this.toggle("\u042d\u043d\u0434\u0435\u0440\u043f\u0435\u0440\u043b");
    public final SliderSetting enderPearlVolume = this.volume("\u0413\u0440\u043e\u043c\u043a\u043e\u0441\u0442\u044c \u044d\u043d\u0434\u0435\u0440\u043f\u0435\u0440\u043b\u0430", this.enderPearl);
    public final BooleanSetting snowball = this.toggle("\u0421\u043d\u0435\u0436\u043e\u043a");
    public final SliderSetting snowballVolume = this.volume("\u0413\u0440\u043e\u043c\u043a\u043e\u0441\u0442\u044c \u0441\u043d\u0435\u0436\u043a\u0430", this.snowball);
    public final BooleanSetting crossbow = this.toggle("\u0410\u0440\u0431\u0430\u043b\u0435\u0442");
    public final SliderSetting crossbowVolume = this.volume("\u0413\u0440\u043e\u043c\u043a\u043e\u0441\u0442\u044c \u0430\u0440\u0431\u0430\u043b\u0435\u0442\u0430", this.crossbow);
    public final BooleanSetting armor = this.toggle("\u0411\u0440\u043e\u043d\u044f");
    public final SliderSetting armorVolume = this.volume("\u0413\u0440\u043e\u043c\u043a\u043e\u0441\u0442\u044c \u0431\u0440\u043e\u043d\u0438", this.armor);
    public final BooleanSetting fireCharge = this.toggle("\u041e\u0433\u043d\u0435\u043d\u043d\u044b\u0439 \u0437\u0430\u0440\u044f\u0434");
    public final SliderSetting fireChargeVolume = this.volume("\u0413\u0440\u043e\u043c\u043a\u043e\u0441\u0442\u044c \u043e\u0433\u043d\u0435\u043d\u043d\u043e\u0433\u043e \u0437\u0430\u0440\u044f\u0434\u0430", this.fireCharge);
    public final BooleanSetting thunder = this.toggle("\u0413\u0440\u043e\u043c");
    public final SliderSetting thunderVolume = this.volume("\u0413\u0440\u043e\u043c\u043a\u043e\u0441\u0442\u044c \u0433\u0440\u043e\u043c\u0430", this.thunder);
    public final BooleanSetting fireworkBlast = this.toggle("\u0412\u0437\u0440\u044b\u0432 \u0444\u0435\u0439\u0435\u0440\u0432\u0435\u0440\u043a\u0430");
    public final SliderSetting fireworkBlastVolume = this.volume("\u0413\u0440\u043e\u043c\u043a\u043e\u0441\u0442\u044c \u0432\u0437\u0440\u044b\u0432\u0430 \u0444\u0435\u0439\u0435\u0440\u0432\u0435\u0440\u043a\u0430", this.fireworkBlast);
    public final BooleanSetting tnt = this.toggle("\u0422\u041d\u0422");
    public final SliderSetting tntVolume = this.volume("\u0413\u0440\u043e\u043c\u043a\u043e\u0441\u0442\u044c \u0422\u041d\u0422", this.tnt);
    public final BooleanSetting anvil = this.toggle("\u041d\u0430\u043a\u043e\u0432\u0430\u043b\u044c\u043d\u044f");
    public final SliderSetting anvilVolume = this.volume("\u0413\u0440\u043e\u043c\u043a\u043e\u0441\u0442\u044c \u043d\u0430\u043a\u043e\u0432\u0430\u043b\u044c\u043d\u0438", this.anvil);
    public final BooleanSetting potions = this.toggle("\u0417\u0435\u043b\u044c\u044f");
    public final SliderSetting potionsVolume = this.volume("\u0413\u0440\u043e\u043c\u043a\u043e\u0441\u0442\u044c \u0437\u0435\u043b\u0438\u0439", this.potions);
    public final BooleanSetting beacon = this.toggle("\u041c\u0430\u044f\u043a");
    public final SliderSetting beaconVolume = this.volume("\u0413\u0440\u043e\u043c\u043a\u043e\u0441\u0442\u044c \u043c\u0430\u044f\u043a\u0430", this.beacon);
    public final BooleanSetting mobsAmbient = this.toggle("\u0424\u043e\u043d \u043c\u043e\u0431\u043e\u0432");
    public final SliderSetting mobsAmbientVolume = this.volume("\u0413\u0440\u043e\u043c\u043a\u043e\u0441\u0442\u044c \u0444\u043e\u043d\u0430 \u043c\u043e\u0431\u043e\u0432", this.mobsAmbient);
    public final BooleanSetting boss = this.toggle("\u0411\u043e\u0441\u0441\u044b");
    public final SliderSetting bossVolume = this.volume("\u0413\u0440\u043e\u043c\u043a\u043e\u0441\u0442\u044c \u0431\u043e\u0441\u0441\u043e\u0432", this.boss);
    public final BooleanSetting rain = this.toggle("\u0414\u043e\u0436\u0434\u044c");
    public final SliderSetting rainVolume = this.volume("\u0413\u0440\u043e\u043c\u043a\u043e\u0441\u0442\u044c \u0434\u043e\u0436\u0434\u044f", this.rain);

    public SoundController() {
        instance = this;
        this.bind("entity.firework_rocket.launch", this.firework, this.fireworkVolume);
        this.bind("entity.experience_bottle.throw", this.expBottleThrow, this.expBottleThrowVolume);
        this.bind("entity.splash_potion.break", this.expBottleBreak, this.expBottleBreakVolume);
        this.bind("entity.experience_orb.pickup", this.expPickup, this.expPickupVolume);
        this.bind("item.trident.return", this.trident, this.tridentVolume);
        this.bind("item.trident.hit_ground", this.trident, this.tridentVolume);
        this.bind("block.beacon.deactivate", this.trident, this.tridentVolume);
        this.bind("entity.fishing_bobber.retrieve", this.fishingRod, this.fishingRodVolume);
        this.bind("entity.player.attack.sweep", this.hit, this.hitVolume);
        this.bind("entity.player.attack.strong", this.hit, this.hitVolume);
        this.bind("entity.player.attack.weak", this.hit, this.hitVolume);
        this.bind("entity.generic.splash", this.splash, this.splashVolume);
        this.bind("entity.player.splash", this.splash, this.splashVolume);
        this.bind("entity.player.attack.crit", this.crit, this.critVolume);
        this.bind("entity.player.levelup", this.funtimeCrit, this.funtimeCritVolume);
        this.bind("item.totem.use", this.totem, this.totemVolume);
        this.bind("entity.generic.explode", this.explosion, this.explosionVolume);
        this.bind("block.end_portal.spawn", this.explosion, this.explosionVolume);
        this.bind("entity.ender_pearl.throw", this.enderPearl, this.enderPearlVolume);
        this.bind("entity.snowball.throw", this.snowball, this.snowballVolume);
        this.bind("item.crossbow.shoot", this.crossbow, this.crossbowVolume);
        this.bind("entity.arrow.shoot", this.crossbow, this.crossbowVolume);
        this.bind("item.armor.equip_generic", this.armor, this.armorVolume);
        this.bind("item.armor.equip_chain", this.armor, this.armorVolume);
        this.bind("item.armor.equip_iron", this.armor, this.armorVolume);
        this.bind("item.armor.equip_leather", this.armor, this.armorVolume);
        this.bind("item.armor.equip_gold", this.armor, this.armorVolume);
        this.bind("item.armor.equip_diamond", this.armor, this.armorVolume);
        this.bind("item.armor.equip_netherite", this.armor, this.armorVolume);
        this.bind("item.armor.equip_turtle", this.armor, this.armorVolume);
        this.bind("item.armor.equip_elytra", this.armor, this.armorVolume);
        this.bind("item.firecharge.use", this.fireCharge, this.fireChargeVolume);
        this.bind("entity.lightning_bolt.thunder", this.thunder, this.thunderVolume);
        this.bind("entity.firework_rocket.blast", this.fireworkBlast, this.fireworkBlastVolume);
        this.bind("entity.firework_rocket.blast_far", this.fireworkBlast, this.fireworkBlastVolume);
        this.bind("entity.firework_rocket.large_blast", this.fireworkBlast, this.fireworkBlastVolume);
        this.bind("entity.firework_rocket.large_blast_far", this.fireworkBlast, this.fireworkBlastVolume);
        this.bind("entity.firework_rocket.twinkle", this.fireworkBlast, this.fireworkBlastVolume);
        this.bind("entity.tnt.primed", this.tnt, this.tntVolume);
        this.bind("block.anvil.land", this.anvil, this.anvilVolume);
        this.bind("block.anvil.use", this.anvil, this.anvilVolume);
        this.bind("block.anvil.hit", this.anvil, this.anvilVolume);
        this.bind("entity.splash_potion.throw", this.potions, this.potionsVolume);
        this.bind("block.brewing_stand.brew", this.potions, this.potionsVolume);
        this.bind("block.beacon.activate", this.beacon, this.beaconVolume);
        this.bind("block.beacon.ambient", this.beacon, this.beaconVolume);
        this.bind("block.beacon.power_select", this.beacon, this.beaconVolume);
        this.bind("entity.zombie.ambient", this.mobsAmbient, this.mobsAmbientVolume);
        this.bind("entity.skeleton.ambient", this.mobsAmbient, this.mobsAmbientVolume);
        this.bind("entity.spider.ambient", this.mobsAmbient, this.mobsAmbientVolume);
        this.bind("entity.cow.ambient", this.mobsAmbient, this.mobsAmbientVolume);
        this.bind("entity.chicken.ambient", this.mobsAmbient, this.mobsAmbientVolume);
        this.bind("entity.pig.ambient", this.mobsAmbient, this.mobsAmbientVolume);
        this.bind("entity.slime.squish", this.mobsAmbient, this.mobsAmbientVolume);
        this.bind("entity.blaze.ambient", this.mobsAmbient, this.mobsAmbientVolume);
        this.bind("entity.ender_dragon.growl", this.boss, this.bossVolume);
        this.bind("entity.ender_dragon.flap", this.boss, this.bossVolume);
        this.bind("entity.ender_dragon.shoot", this.boss, this.bossVolume);
        this.bind("entity.ender_dragon.hurt", this.boss, this.bossVolume);
        this.bind("entity.wither.shoot", this.boss, this.bossVolume);
        this.bind("entity.wither.break_block", this.boss, this.bossVolume);
        this.bind("entity.wither.hurt", this.boss, this.bossVolume);
        this.bind("weather.rain", this.rain, this.rainVolume);
        this.bind("weather.rain.above", this.rain, this.rainVolume);
    }

    public static SoundController getInstance() {
        return instance;
    }

    public static float scaleFor(class_1113 sound) {
        if (sound == null) {
            return 1.0f;
        }
        class_2960 id = sound.method_4775();
        if (id == null) {
            return 1.0f;
        }
        SoundController self = instance;
        if (self == null || !self.isEnabled()) {
            return 1.0f;
        }
        Group group = self.bindings.get(id);
        if (group == null || !group.enabled().getValue().booleanValue()) {
            return 1.0f;
        }
        float value = group.volume().getValue().floatValue();
        float scale = Math.max(0.0f, Math.min(100.0f, value)) / 100.0f;
        if (self.seen.add(id.toString())) {
            LOGGER.info("[SoundController] {} -> x{}", (Object)id, (Object)Float.valueOf(scale));
        }
        return scale;
    }

    public static class_1113 scale(class_1113 sound) {
        if (sound instanceof ScaledSoundInstance) {
            return sound;
        }
        return ScaledSoundInstance.of(sound, SoundController.scaleFor(sound));
    }

    private BooleanSetting toggle(String name) {
        return new BooleanSetting((Module)this, name, false);
    }

    private SliderSetting volume(String name, BooleanSetting owner) {
        return new SliderSetting(this, name, 50.0f, 0.0f, 100.0f, 1.0f).setVisible(owner::getValue);
    }

    private void bind(String path, BooleanSetting enabled, SliderSetting volume) {
        this.bindings.put(class_2960.method_60656((String)path), new Group(enabled, volume));
    }

    @Environment(value=EnvType.CLIENT)
    private record Group(BooleanSetting enabled, SliderSetting volume) {
    }
}


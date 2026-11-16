/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.module.api;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.Client;
import ru.decide.manager.event_impl.EventKey;
import ru.decide.manager.events.orbit.EventHandler;
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.module.impl.combat.AutoEzz;
import ru.decide.module.impl.combat.NoFriendDamage;
import ru.decide.module.impl.combat.UseTracker;
import ru.decide.module.impl.display.ClickGui;
import ru.decide.module.impl.display.InterFace;
import ru.decide.module.impl.display.Sounds;
import ru.decide.module.impl.movement.Sprint;
import ru.decide.module.impl.player.AutoAccept;
import ru.decide.module.impl.player.FakePlayer;
import ru.decide.module.impl.player.FreeLook;
import ru.decide.module.impl.player.LockSlot;
import ru.decide.module.impl.player.NoDelay;
import ru.decide.module.impl.player.PvpSafe;
import ru.decide.module.impl.render.AspectRatio;
import ru.decide.module.impl.render.AtmosphereFog;
import ru.decide.module.impl.render.BetterMinecraft;
import ru.decide.module.impl.render.ChinaHat;
import ru.decide.module.impl.render.Cinematic;
import ru.decide.module.impl.render.CrossHair;
import ru.decide.module.impl.render.CustomSword;
import ru.decide.module.impl.render.Emotions;
import ru.decide.module.impl.render.EntityEsp;
import ru.decide.module.impl.render.ExplosionWave;
import ru.decide.module.impl.render.FireFlies;
import ru.decide.module.impl.render.Gamma;
import ru.decide.module.impl.render.GlassBlock;
import ru.decide.module.impl.render.GlassHands;
import ru.decide.module.impl.render.GlassVapor;
import ru.decide.module.impl.render.Hands;
import ru.decide.module.impl.render.HealthAlert;
import ru.decide.module.impl.render.HitColor;
import ru.decide.module.impl.render.HitEffects;
import ru.decide.module.impl.render.HitRange;
import ru.decide.module.impl.render.HitWave;
import ru.decide.module.impl.render.HoldMyItems;
import ru.decide.module.impl.render.ItemHighlight;
import ru.decide.module.impl.render.JumpCircle;
import ru.decide.module.impl.render.JumpCube;
import ru.decide.module.impl.render.KillEffect;
import ru.decide.module.impl.render.LineGlyphs;
import ru.decide.module.impl.render.LootView;
import ru.decide.module.impl.render.LyricsText;
import ru.decide.module.impl.render.ModelCollapse;
import ru.decide.module.impl.render.NoRender;
import ru.decide.module.impl.render.NotEnoughAnimations;
import ru.decide.module.impl.render.OldWalkAnimation;
import ru.decide.module.impl.render.Optimizer;
import ru.decide.module.impl.render.Particles;
import ru.decide.module.impl.render.Saturation;
import ru.decide.module.impl.render.ScanWorld;
import ru.decide.module.impl.render.ShaderEsp;
import ru.decide.module.impl.render.ShaderSky;
import ru.decide.module.impl.render.SlotTimer;
import ru.decide.module.impl.render.Svetoch;
import ru.decide.module.impl.render.SwingAnimation;
import ru.decide.module.impl.render.TargetEsp;
import ru.decide.module.impl.render.TotemGhost;
import ru.decide.module.impl.render.Trails;
import ru.decide.module.impl.render.Trajectories;
import ru.decide.module.impl.render.VoidButterflies;
import ru.decide.module.impl.render.WastedDeath;
import ru.decide.module.impl.render.Weather;
import ru.decide.module.impl.render.WorldCubes;
import ru.decide.module.impl.render.WorldTweaks;
import ru.decide.module.impl.utils.AutoFixCommand;
import ru.decide.module.impl.utils.AutoInvest;
import ru.decide.module.impl.utils.AutoSwapModule;
import ru.decide.module.impl.utils.ConsumableOptimizer;
import ru.decide.module.impl.utils.CoordInvite;
import ru.decide.module.impl.utils.EffectSounds;
import ru.decide.module.impl.utils.FrameSync;
import ru.decide.module.impl.utils.ItemScroller;
import ru.decide.module.impl.utils.NameProtect;
import ru.decide.module.impl.utils.SoundController;
import ru.decide.module.impl.utils.SoundsKey;
import ru.decide.module.impl.utils.SpearHelper;
import ru.decide.module.impl.utils.VeloTuneModule;

@Environment(value=EnvType.CLIENT)
public final class ModuleManager
extends LinkedHashMap<Class<? extends Module>, Module> {
    public void init() {
        this.addSorted(new AutoEzz(), new NoFriendDamage(), new UseTracker(), new Sprint(), new ClickGui(), new EntityEsp(), new TargetEsp(), new HitEffects(), new HitWave(), new LineGlyphs(), new Saturation(), new NoRender(), new SwingAnimation(), new GlassHands(), new GlassBlock(), new ShaderEsp(), new ShaderSky(), new ExplosionWave(), new ModelCollapse(), new BetterMinecraft(), new AspectRatio(), new LootView(), new WastedDeath(), new GlassVapor(), new Cinematic(), new AtmosphereFog(), new Weather(), new WorldTweaks(), new Gamma(), new Particles(), new HealthAlert(), new ChinaHat(), new OldWalkAnimation(), new NotEnoughAnimations(), new SlotTimer(), new HitColor(), new JumpCircle(), new HitRange(), new Emotions(), new JumpCube(), new CrossHair(), new Trails(), new WorldCubes(), new Trajectories(), new FireFlies(), new VoidButterflies(), new Svetoch(), new ScanWorld(), new TotemGhost(), new KillEffect(), new LyricsText(), new Hands(), new HoldMyItems(), new InterFace(), new Sounds(), new CustomSword(), new AutoAccept(), new NoDelay(), new LockSlot(), new FreeLook(), new PvpSafe(), new NameProtect(), new ItemScroller(), new AutoInvest(), new FakePlayer(), new ItemHighlight(), new ConsumableOptimizer(), new FrameSync(), new AutoSwapModule(), new SpearHelper(), new EffectSounds(), new SoundsKey(), new CoordInvite(), new AutoFixCommand(), new VeloTuneModule(), new SoundController(), new Optimizer());
        this.values().stream().filter(Module::isAutoEnabled).forEach((? super T module) -> module.setEnabled(true, false));
        Client.eventHandler().subscribe(this);
    }

    public void addSorted(Module ... modules) {
        Arrays.stream(modules).forEach((? super T module) -> this.put(module.getClass(), module));
    }

    public void unregister(Module ... modules) {
        Arrays.stream(modules).forEach((? super T module) -> this.remove(module.getClass()));
    }

    @EventHandler
    public void onKeyboardPress(EventKey event) {
        this.values().stream().filter(module -> module.getKey() == event.getKey()).forEach(Module::toggle);
    }

    public <T extends Module> T get(String name) {
        return (T)((Module)this.values().stream().filter(module -> module.getName().equalsIgnoreCase(name)).map(module -> module).findFirst().orElse(null));
    }

    public <T extends Module> T get(Class<T> clazz) {
        return (T)((Module)this.values().stream().filter(module -> clazz.isAssignableFrom(module.getClass())).map(clazz::cast).findFirst().orElse(null));
    }

    public List<Module> get(Category category) {
        return this.values().stream().filter(module -> module.getCategory() == category).collect(Collectors.toList());
    }

    public Module getModule(String name) {
        return this.values().stream().filter(module -> module.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    @Override
    public Collection<Module> values() {
        return super.values().stream().sorted(Comparator.comparing(Module::getName, String.CASE_INSENSITIVE_ORDER)).collect(Collectors.toList());
    }
}


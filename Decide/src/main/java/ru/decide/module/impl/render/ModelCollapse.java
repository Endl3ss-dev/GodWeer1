/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1297
 *  net.minecraft.class_1308
 *  net.minecraft.class_1309
 *  net.minecraft.class_1429
 *  net.minecraft.class_1657
 *  net.minecraft.class_3532
 *  net.minecraft.class_638
 *  net.minecraft.class_746
 */
package ru.decide.module.impl.render;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1297;
import net.minecraft.class_1308;
import net.minecraft.class_1309;
import net.minecraft.class_1429;
import net.minecraft.class_1657;
import net.minecraft.class_3532;
import net.minecraft.class_638;
import net.minecraft.class_746;
import ru.decide.Client;
import ru.decide.friend.FriendManager;
import ru.decide.manager.event_impl.AttackEvent;
import ru.decide.manager.event_impl.EventRender3D;
import ru.decide.manager.event_impl.EventTick;
import ru.decide.manager.events.orbit.EventHandler;
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.module.api.ModuleInfo;
import ru.decide.module.api.settings.impl.BooleanSetting;
import ru.decide.module.api.settings.impl.DelimiterSetting;
import ru.decide.module.api.settings.impl.ModeSetting;
import ru.decide.module.api.settings.impl.MultiBooleanSetting;
import ru.decide.module.api.settings.impl.SliderSetting;
import ru.decide.module.impl.render.ModelCollapseSimulation;
import ru.decide.module.impl.render.ModelCollapseSnapshots;
import ru.decide.utils.other.Instance;

@ModuleInfo(name="Model Collapse", desc="\u0420\u0430\u0437\u0440\u044b\u0432\u0430\u0435\u0442 \u043c\u043e\u0434\u0435\u043b\u044c \u0443\u0431\u0438\u0442\u043e\u0439 \u0441\u0443\u0449\u043d\u043e\u0441\u0442\u0438 \u043d\u0430 \u043a\u0443\u0431\u0438\u043a\u0438 \u0441 \u0444\u0438\u0437\u0438\u043a\u043e\u0439 \u0438 \u043a\u043e\u043b\u043b\u0438\u0437\u0438\u0435\u0439", category=Category.VISUALS)
@Environment(value=EnvType.CLIENT)
public final class ModelCollapse
extends Module {
    private static final String MODE_BOTTOM_UP = "\u0421\u043d\u0438\u0437\u0443 \u0432\u0432\u0435\u0440\u0445";
    private static final String MODE_TOP_DOWN = "\u0421\u0432\u0435\u0440\u0445\u0443 \u0432\u043d\u0438\u0437";
    private static final String TARGET_PLAYERS = "\u0418\u0433\u0440\u043e\u043a\u0438";
    private static final String TARGET_FRIENDS = "\u0414\u0440\u0443\u0437\u044c\u044f";
    private static final String TARGET_MOBS = "\u041c\u043e\u0431\u044b";
    private static final String TARGET_ANIMALS = "\u0416\u0438\u0432\u043e\u0442\u043d\u044b\u0435";
    private static final int MAX_PENDING = 8;
    private static final long HIDE_MS = 5000L;
    private static final long COLLAPSE_MEMORY_MS = 5000L;
    private static final long PROVISIONAL_MS = 700L;
    private static final long VANISH_SNAPSHOT_MS = 700L;
    private static final double UNHIDE_MOVE_SQ = 6.25;
    private final ModelCollapseSimulation simulation = new ModelCollapseSimulation();
    private final List<class_1309> pending = new ArrayList<class_1309>();
    private final Map<Integer, HiddenEntry> hidden = new HashMap<Integer, HiddenEntry>();
    private final Map<Integer, Long> recentlyCollapsed = new HashMap<Integer, Long>();
    private final Map<Integer, Long> provisional = new HashMap<Integer, Long>();
    public final DelimiterSetting mainSeparator = new DelimiterSetting(this, "\u041e\u0441\u043d\u043e\u0432\u043d\u043e\u0435");
    public final ModeSetting breakMode = new ModeSetting((Module)this, "\u0420\u0430\u0441\u043a\u043e\u043b", "\u0421\u043d\u0438\u0437\u0443 \u0432\u0432\u0435\u0440\u0445", "\u0421\u0432\u0435\u0440\u0445\u0443 \u0432\u043d\u0438\u0437");
    public final SliderSetting waveTime = new SliderSetting(this, "\u0412\u043e\u043b\u043d\u0430", 350.0f, 100.0f, 1000.0f, 25.0f);
    public final SliderSetting cubeSize = new SliderSetting(this, "\u0420\u0430\u0437\u043c\u0435\u0440 \u043a\u0443\u0431\u0438\u043a\u043e\u0432", 0.055f, 0.03f, 0.12f, 0.005f);
    public final SliderSetting impulse = new SliderSetting(this, "\u0420\u0430\u0437\u043b\u0451\u0442", 1.0f, 0.0f, 3.0f, 0.05f);
    public final SliderSetting bounce = new SliderSetting(this, "\u041e\u0442\u0441\u043a\u043e\u043a", 35.0f, 0.0f, 80.0f, 5.0f);
    public final SliderSetting lifeTime = new SliderSetting(this, "\u0412\u0440\u0435\u043c\u044f \u0436\u0438\u0437\u043d\u0438", 6.0f, 1.0f, 15.0f, 0.5f);
    public final SliderSetting maxDistance = new SliderSetting(this, "\u0414\u0430\u043b\u044c\u043d\u043e\u0441\u0442\u044c", 64.0f, 16.0f, 128.0f, 8.0f);
    public final BooleanSetting hideModel = new BooleanSetting((Module)this, "\u0421\u043a\u0440\u044b\u0432\u0430\u0442\u044c \u043c\u043e\u0434\u0435\u043b\u044c", true);
    public final BooleanSetting aggressiveDetect = new BooleanSetting((Module)this, "\u0410\u0433\u0440\u0435\u0441\u0441\u0438\u0432\u043d\u044b\u0439 \u0434\u0435\u0442\u0435\u043a\u0442", true);
    public final DelimiterSetting targetsSeparator = new DelimiterSetting(this, "\u0426\u0435\u043b\u0438");
    private final BooleanSetting targetPlayers = new BooleanSetting("\u0418\u0433\u0440\u043e\u043a\u0438", true);
    private final BooleanSetting targetFriends = new BooleanSetting("\u0414\u0440\u0443\u0437\u044c\u044f", true);
    private final BooleanSetting targetMobs = new BooleanSetting("\u041c\u043e\u0431\u044b", true);
    private final BooleanSetting targetAnimals = new BooleanSetting("\u0416\u0438\u0432\u043e\u0442\u043d\u044b\u0435", true);
    public final MultiBooleanSetting targets = new MultiBooleanSetting((Module)this, "\u0426\u0435\u043b\u0438", this.targetPlayers, this.targetFriends, this.targetMobs, this.targetAnimals);

    public static ModelCollapse getInstance() {
        return Instance.get(ModelCollapse.class);
    }

    public static void notifyEntityDied(class_1309 victim) {
        ModelCollapse module = ModelCollapse.getInstanceIfReady();
        if (module == null || !module.isEnabled() || victim == null) {
            return;
        }
        module.handleDeath(victim);
    }

    public static boolean shouldHideEntity(class_1309 entity) {
        ModelCollapse module = ModelCollapse.getInstanceIfReady();
        if (module == null || !module.isEnabled()) {
            return false;
        }
        HiddenEntry entry = module.hidden.get(entity.method_5628());
        if (entry == null) {
            return false;
        }
        if (System.currentTimeMillis() >= entry.until) {
            return false;
        }
        if (!entity.method_31481() && entity.method_5649(entry.x, entry.y, entry.z) > 6.25) {
            module.hidden.remove(entity.method_5628());
            return false;
        }
        return true;
    }

    private static ModelCollapse getInstanceIfReady() {
        try {
            return ModelCollapse.getInstance();
        }
        catch (RuntimeException ignored) {
            return null;
        }
    }

    @EventHandler
    public void onTick(EventTick event) {
        if (ModelCollapse.mc.field_1724 == null || ModelCollapse.mc.field_1687 == null) {
            this.resetState();
        }
    }

    @EventHandler
    public void onRender(EventRender3D event) {
        if (ModelCollapse.mc.field_1724 == null || ModelCollapse.mc.field_1687 == null) {
            this.resetState();
            return;
        }
        long now = System.currentTimeMillis();
        float partialTick = class_3532.method_15363((float)event.getTickDelta(), (float)0.0f, (float)1.0f);
        if (!this.pending.isEmpty()) {
            for (class_1309 entity : this.pending) {
                this.provisional.remove(entity.method_5628());
                if (this.simulation.release(entity.method_5628()) || this.simulation.spawn(entity, partialTick, this.settings())) continue;
                this.hidden.remove(entity.method_5628());
            }
            this.pending.clear();
        }
        if (!this.provisional.isEmpty()) {
            ArrayList<Integer> stale = new ArrayList<Integer>();
            for (Map.Entry<Integer, Long> entry2 : this.provisional.entrySet()) {
                if (now - entry2.getValue() <= 700L) continue;
                stale.add(entry2.getKey());
            }
            Iterator<Map.Entry<Integer, Long>> iterator = stale.iterator();
            while (iterator.hasNext()) {
                int id = (Integer)((Object)iterator.next());
                this.provisional.remove(id);
                class_638 world = ModelCollapse.mc.field_1687;
                if (world.method_8469(id) == null) {
                    this.simulation.release(id);
                    this.recentlyCollapsed.put(id, now);
                    continue;
                }
                this.simulation.dropFrozen(id);
                this.hidden.remove(id);
            }
        }
        double distance = this.maxDistance.getValue().floatValue();
        if (this.aggressiveDetect.getValue().booleanValue()) {
            ModelCollapseSnapshots.tick(partialTick, distance, this::matchesTarget);
            ModelCollapseSnapshots.collectVanished(700L, this::spawnFromVanished);
        }
        this.hidden.values().removeIf(entry -> entry.until < now);
        this.recentlyCollapsed.values().removeIf(at -> now - at > 5000L);
        if (!this.simulation.isIdle()) {
            this.simulation.renderAndStep(event, this.settings());
        }
    }

    private boolean spawnFromVanished(int id, ModelCollapseSnapshots.Snapshot snapshot) {
        if (this.recentlyCollapsed.containsKey(id) || this.provisional.containsKey(id)) {
            return false;
        }
        class_746 player = ModelCollapse.mc.field_1724;
        if (player == null) {
            return false;
        }
        double distance = this.maxDistance.getValue().floatValue();
        if (player.method_5649(snapshot.x(), snapshot.y(), snapshot.z()) > distance * distance) {
            return false;
        }
        if (!this.simulation.spawnFrozen(id, snapshot, this.settings())) {
            return false;
        }
        this.provisional.put(id, System.currentTimeMillis());
        if (this.hideModel.getValue().booleanValue()) {
            this.hidden.put(id, new HiddenEntry(System.currentTimeMillis() + 5000L, snapshot.x(), snapshot.y(), snapshot.z()));
        }
        return true;
    }

    @EventHandler
    public void onAttack(AttackEvent event) {
        if (!this.isEnabled()) {
            return;
        }
        class_1297 target = event.getTarget();
        if (target instanceof class_1309) {
            class_1309 living = (class_1309)target;
            if (target != ModelCollapse.mc.field_1724) {
                ModelCollapseSnapshots.noteAttack(living);
            }
        }
    }

    @Override
    protected void onDisable() {
        this.resetState();
    }

    private void resetState() {
        this.pending.clear();
        this.hidden.clear();
        this.recentlyCollapsed.clear();
        this.provisional.clear();
        ModelCollapseSnapshots.clear();
        this.simulation.clear();
    }

    private ModelCollapseSimulation.Settings settings() {
        return new ModelCollapseSimulation.Settings(this.cubeSize.getValue().floatValue(), this.impulse.getValue().floatValue(), Math.round(this.lifeTime.getValue().floatValue() * 1000.0f), this.bounce.getValue().floatValue() / 100.0f, Math.round(this.waveTime.getValue().floatValue()), this.breakMode.is(MODE_TOP_DOWN));
    }

    private void handleDeath(class_1309 victim) {
        class_746 player = ModelCollapse.mc.field_1724;
        class_638 level = ModelCollapse.mc.field_1687;
        if (player == null || level == null) {
            return;
        }
        if (Objects.equals(victim, player)) {
            return;
        }
        if (!Objects.equals(victim.method_73183(), level)) {
            return;
        }
        if (this.recentlyCollapsed.containsKey(victim.method_5628())) {
            return;
        }
        double distance = this.maxDistance.getValue().floatValue();
        if (victim.method_5858((class_1297)player) > distance * distance) {
            return;
        }
        if (!this.matchesTarget(victim)) {
            return;
        }
        if (this.pending.size() >= 8) {
            return;
        }
        this.recentlyCollapsed.put(victim.method_5628(), System.currentTimeMillis());
        this.pending.add(victim);
        if (this.hideModel.getValue().booleanValue()) {
            this.hidden.put(victim.method_5628(), new HiddenEntry(System.currentTimeMillis() + 5000L, victim.method_23317(), victim.method_23318(), victim.method_23321()));
        }
    }

    private boolean matchesTarget(class_1309 entity) {
        if (entity instanceof class_1657) {
            FriendManager friends = Client.get().friendManager();
            if (friends != null && friends.isFriend(entity.method_5477().getString())) {
                return this.targets.getValue(TARGET_FRIENDS);
            }
            return this.targets.getValue(TARGET_PLAYERS);
        }
        if (entity instanceof class_1429) {
            return this.targets.getValue(TARGET_ANIMALS);
        }
        if (entity instanceof class_1308) {
            return this.targets.getValue(TARGET_MOBS);
        }
        return false;
    }

    @Environment(value=EnvType.CLIENT)
    private static final class HiddenEntry {
        final long until;
        final double x;
        final double y;
        final double z;

        HiddenEntry(long until, double x, double y, double z) {
            this.until = until;
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
}


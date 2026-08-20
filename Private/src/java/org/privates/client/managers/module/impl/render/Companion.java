package org.privates.client.managers.module.impl.render;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.privates.client.api.annotations.Category2;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.events.render.Render3DLastEvent;
import org.privates.client.managers.events.world.WorldChangeEvent;
import org.privates.client.managers.events.world.WorldLoadEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.utils.other.Instance;
import org.privates.common.impl.companion.GhostCompanion;

@Category2
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "Companion", category = Category.RENDER, description = "Мини бро который за вами летает и он очень миленький")
public class Companion extends Module {
    public static Companion getInstance() {
        return Instance.get(Companion.class);
    }

    private final GhostCompanion companion = new GhostCompanion();

    @Override
    protected void onEnable() {
        super.onEnable();
    }

    @Override
    protected void onDisable() {
        super.onDisable();
    }

    @Override
    public void toggle() {
        super.toggle();
        companion.position().set(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ());
        companion.destination().set(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ());
        companion.motion().zero();
    }

    @EventHandler
    public void onEvent(WorldLoadEvent event) {
        companion.onEvent(event);
    }

    @EventHandler
    public void onEvent(WorldChangeEvent event) {
        companion.onEvent(event);
    }

    @EventHandler
    public void onEvent(Render3DLastEvent event) {
        companion.onEvent(event);
    }

    @EventHandler
    public void onEvent(UpdateEvent event) {
        companion.onEvent(event);
    }
}
package fun.volon.client.modules.impl.render;

import fun.volon.api.events.EventLink;
import fun.volon.api.events.implement.EventPopTotem;
import fun.volon.client.modules.Module;
import fun.volon.mods.maseffects.TotemParticleSpawner;

public class TotemParticles extends Module {
    public static final TotemParticles INSTANCE = new TotemParticles();

    public TotemParticles() {
        super("TotemParticles", "Custom totem particles", ModuleCategory.RENDER);
    }

    @EventLink
    public void onPopTotem(EventPopTotem event) {
        if (event == null || event.getPlayer() == null) {
            return;
        }

        TotemParticleSpawner.spawn(event.getPlayer());
    }
}

package org.privates.client.managers.module.impl.combat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.entity.LivingEntity;
import org.privates.client.api.annotations.Category3;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.component.impl.target.TargetComponent;
import org.privates.client.managers.events.other.EntityRayTraceEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.utils.other.Instance;

@Category3
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "NoEntityTrace", category = Category.COMBAT, description = "Ну кароче хитбоксы игрокам канселит")
public class NoEntityTrace extends Module {
    public static NoEntityTrace getInstance() {
        return Instance.get(NoEntityTrace.class);
    }

    @EventHandler

    public void onEvent(EntityRayTraceEvent event) {
        if (event.getEntity() instanceof LivingEntity living && TargetComponent.getTargets(6, true).contains(living)) {
            event.cancel();
        }
    }
}
package org.privates.client.managers.module.impl.render;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.settings.PointOfView;
import org.privates.client.api.annotations.Category1;
import org.privates.client.managers.component.impl.rotation.FreeLookComponent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.impl.combat.AuraNew;
import org.privates.client.utils.other.Instance;

@Category1
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "FreeLook", category = Category.RENDER, description = "Свободный обзор камеры")
public class FreeLoock extends Module {
    public static FreeLoock getInstance() {
        return Instance.get(FreeLoock.class);
    }


    @Override
    public void onDisable() {

        super.onDisable();
        if (!AuraNew.getInstance().isEnabled())
            FreeLookComponent.setActive(false);

        mc.gameSettings.setPointOfView(PointOfView.FIRST_PERSON);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (!AuraNew.getInstance().isEnabled())
            mc.gameSettings.setPointOfView(PointOfView.THIRD_PERSON_BACK);
        if (mc.gameSettings.getPointOfView() == PointOfView.THIRD_PERSON_BACK) {
            if (!AuraNew.getInstance().isEnabled())
                FreeLookComponent.setActive(true);
        }
    }
}

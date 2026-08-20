package org.privates.client.managers.module.impl.movement;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.privates.client.api.annotations.Category2;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.utils.other.Instance;

@Category2
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "AntiHunger", category = Category.MOVEMENT, description = "Вы всегда сможете бегать на спринте даже без голода")
public class AntiHunger extends Module {
    public static AntiHunger getInstance() {
        return Instance.get(AntiHunger.class);
    }
}

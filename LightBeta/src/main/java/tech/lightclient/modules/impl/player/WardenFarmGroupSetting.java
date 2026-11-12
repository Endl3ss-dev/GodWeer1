package tech.lightclient.modules.impl.player;

import tech.lightclient.modules.api.settings.impl.BooleanSetting;
import tech.lightclient.modules.api.settings.impl.ModeListSetting;
import java.util.Arrays;
import java.util.List;

/** Compatibility setting for the multi-toggle loot group used by the migrated farm. */
final class WardenFarmGroupSetting extends ModeListSetting {
    private final List<BooleanSetting> options;
    WardenFarmGroupSetting(String name, BooleanSetting... options) {
        super(name, options);
        this.options = Arrays.asList(options);
    }
    boolean isEnabled(String name) {
        return options.stream().anyMatch(option -> option.getName().equals(name) && Boolean.TRUE.equals(option.get()));
    }
}

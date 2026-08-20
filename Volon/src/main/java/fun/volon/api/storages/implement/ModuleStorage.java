package fun.volon.api.storages.implement;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.Setter;
import fun.volon.api.QClient;
import fun.volon.api.storages.implement.helpertstorages.enumvar.ModuleClass;
import fun.volon.client.modules.Module;
import fun.volon.client.modules.impl.combat.*;
import fun.volon.client.modules.impl.misc.*;
import fun.volon.client.modules.impl.movement.*;
import fun.volon.client.modules.impl.player.*;
import fun.volon.client.modules.impl.render.*;
import java.util.Arrays;

@Getter
@Setter
public class ModuleStorage implements QClient {

    public ModuleStorage() {
        this.initModules();
    }

    private void initModules() {
        ModuleClass.INSTANCE.initialize();
    }
}

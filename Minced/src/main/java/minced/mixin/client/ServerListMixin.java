/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.network.ServerInfo
 *  net.minecraft.client.network.ServerInfo$ServerType
 *  net.minecraft.client.option.ServerList
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package minced.mixin.client;
import java.util.List;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.dreamix.fabricloader.VMBridge;

@Mixin(value={ServerList.class})
public class ServerListMixin {
    @Final
    @Shadow
    private List<ServerInfo> field_3749;
    @Unique
    private static final String PINNED_ADDRESS_2 = "pulsesmp.fun";
    @Unique
    private static final String PINNED_NAME_2 = "Лучший SMP сервер - PulseSMP";
    private static String field12724 = PINNED_ADDRESS_2;
    private static String field12725 = PINNED_NAME_2;
    private static String field12726 = PINNED_ADDRESS_2;

    @Unique
    private static boolean isPinned(String param0) {
        return field12724.equals(param0);
    }

    @Inject(method={"loadFile"}, at={@At(value="RETURN")})
    private void minced$injectPinnedServer(CallbackInfo param1) {
        this.field_3749.removeIf(param0 -> ServerListMixin.isPinned(param0.address));
        ServerInfo v2 = new ServerInfo(field12725, field12726, ServerInfo.ServerType.OTHER);
        this.field_3749.addFirst(v2);
    }

    @Inject(method={"remove"}, at={@At(value="HEAD")}, cancellable=true)
    private void minced$blockPinnedRemove(ServerInfo param1, CallbackInfo param2) {
        if (ServerListMixin.isPinned(param1.address)) {
            param2.cancel();
        }
    }

    @Inject(method={"swapEntries"}, at={@At(value="HEAD")}, cancellable=true)
    private void minced$blockPinnedSwap(int param1, int param2, CallbackInfo param3) {
        if (!this.field_3749.isEmpty() && (ServerListMixin.isPinned(this.field_3749.get((int)param1).address) || ServerListMixin.isPinned(this.field_3749.get((int)param2).address))) {
            param3.cancel();
        }
    }

    static {
        VMBridge.identifyClass(ServerListMixin.class, "VeKLZBKQ");
    }
}




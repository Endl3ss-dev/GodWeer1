/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Keyboard
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package minced.mixin.input;
import java.util.HashMap;
import java.util.Map;
import minced.core.ClientUtil;
import minced.core.Event;
import minced.core.EventBus;
import minced.core.EventKey;
import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={Keyboard.class})
public class KeyboardMixin {
    private static final Map<Integer, Boolean> keyScreenStates = new HashMap<Integer, Boolean>();

    @Inject(method={"onKey"}, at={@At(value="HEAD")})
    private void onKeyInject(long param1, int param3, int param4, int param5, int param6, CallbackInfo param7) {
        if (param1 == ClientUtil.I.getWindow().getHandle()) {
            boolean v8;
            boolean bl = v8 = ClientUtil.I.currentScreen != null;
            if (param5 == 1) {
                keyScreenStates.put(param3, v8);
                EventBus.method3015(((Object)new EventKey(param3, true, v8)));
            } else if (param5 == 0) {
                Boolean v9 = keyScreenStates.remove(param3);
                boolean v10 = v9 != null ? v9 : false;
                EventBus.method3015(((Object)new EventKey(param3, false, v10)));
            }
        }
    }
}




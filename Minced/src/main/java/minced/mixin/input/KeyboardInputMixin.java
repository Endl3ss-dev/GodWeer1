/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.input.Input
 *  net.minecraft.client.input.KeyboardInput
 *  net.minecraft.client.option.GameOptions
 *  net.minecraft.util.PlayerInput
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Overwrite
 *  org.spongepowered.asm.mixin.Shadow
 */
package minced.mixin.input;
import minced.core.EventInput;
import minced.core.ServiceRegistry;
import minced.core.BotManager;
import minced.core.BotView;
import minced.core.Event;
import minced.core.EventBus;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.option.GameOptions;
import net.minecraft.util.PlayerInput;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import ru.dreamix.fabricloader.VMBridge;

@Mixin(value={KeyboardInput.class})
public class KeyboardInputMixin
extends Input {
    @Shadow
    @Final
    private GameOptions field_3902;
    // Vanilla getMovementMultiplier returns -1.0f for the negative direction; the decompiled
    // constant was lost and stubbed as 1.0f, which inverted backward/sideways movement.
    private static float field12646 = -1.0f;

    private static float getMovementMultiplier(boolean param0, boolean param1) {
        if (param0 == param1) {
            return 0.0f;
        }
        return param0 ? 1.0f : field12646;
    }

    @Overwrite
    public void tick() {
        BotView v1 = (BotView)ServiceRegistry.method2118(BotView.class);
        BotManager v2 = (BotManager)ServiceRegistry.method2118(BotManager.class);
        if (v1 != null && v1.method2198() && v2 != null && v2.method2172()) {
            this.movementForward = 0.0f;
            this.movementSideways = 0.0f;
            this.playerInput = new PlayerInput(false, false, false, false, false, false, false);
            return;
        }
        boolean v3 = this.field_3902.forwardKey.isPressed();
        boolean v4 = this.field_3902.backKey.isPressed();
        boolean v5 = this.field_3902.leftKey.isPressed();
        boolean v6 = this.field_3902.rightKey.isPressed();
        boolean v7 = this.field_3902.jumpKey.isPressed();
        boolean v8 = this.field_3902.sneakKey.isPressed();
        boolean v9 = this.field_3902.sprintKey.isPressed();
        float v10 = KeyboardInputMixin.getMovementMultiplier(v3, v4);
        float v11 = KeyboardInputMixin.getMovementMultiplier(v5, v6);
        EventInput v12 = new EventInput(v10, v11, v7, v8);
        EventBus.method3015(((Object)v12));
        v10 = v12.method1969();
        v11 = v12.method1971();
        v7 = v12.method1968();
        v8 = v12.method1967();
        this.playerInput = new PlayerInput(v10 > 0.0f, v10 < 0.0f, v11 > 0.0f, v11 < 0.0f, v7, v8, v9);
        this.movementForward = v10;
        this.movementSideways = v11;
    }

    static {
        VMBridge.identifyClass(KeyboardInputMixin.class, "sQfvvkRI");
    }
}




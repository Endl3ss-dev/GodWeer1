package never.lose.mixins.accessor;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MinecraftClient.class)
public interface MinecraftClientInvoker {

    @Invoker("handleInputEvents")
    void endless$handleInputEvents();

    @Invoker("doAttack")
    boolean endless$doAttack();
}

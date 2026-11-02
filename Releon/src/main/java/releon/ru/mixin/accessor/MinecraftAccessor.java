package releon.ru.mixin.accessor;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.session.Session;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({MinecraftClient.class})
public interface MinecraftAccessor {
   @Mutable
   @Accessor("session")
   void releon$setUser(Session var1);

   @Invoker("doAttack")
   boolean releon$startAttack();

   @Invoker("doItemUse")
   void releon$startUseItem();

   @Invoker("setWorld")
   void releon$updateLevelInEngines(ClientWorld var1);

   @Accessor("itemUseCooldown")
   void releon$setRightClickDelay(int var1);
}

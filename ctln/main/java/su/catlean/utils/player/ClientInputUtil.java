// deobf by pusia
package su.catlean.utils.player;

import net.minecraft.client.player.ClientInput;
import net.minecraft.world.entity.player.Input;
import org.jetbrains.annotations.NotNull;

public final class ClientInputUtil {
   @NotNull
   public static final ClientInput computeClientInput(ClientInput self, boolean flag) {
      self.keyPresses = new Input(
         self.keyPresses.forward(),
         self.keyPresses.backward(),
         self.keyPresses.left(),
         self.keyPresses.right(),
         self.keyPresses.jump(),
         flag,
         self.keyPresses.sprint()
      );
      return self;
   }
}
// deobf by pusia
package su.catlean.utils.input;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screens.ChatScreen;
import su.catlean.client.services.ModuleManager;
import su.catlean.utils.MinecraftHolder;

public final class InputUtil {
   public static final boolean checkContains(int value) {
      if (!RenderSystem.isOnRenderThread() || value == -1 || MinecraftHolder.getMinecraft().gui.screen() instanceof ChatScreen) {
         return false;
      } else {
         return value < 10 ? ModuleManager.INSTANCE.getItems3().contains(value) : InputConstants.isKeyDown(MinecraftHolder.getMinecraft().getWindow(), value);
      }
   }
}
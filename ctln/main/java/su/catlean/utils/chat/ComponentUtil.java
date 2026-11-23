// deobf by pusia
package su.catlean.utils.chat;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;

public final class ComponentUtil {
   @NotNull
   public static final MutableComponent computeMutableComponent(MutableComponent self, String addition, ChatFormatting formatting) {
      return self.append((Component)Component.literal(addition).withStyle(formatting));
   }

   @NotNull
   public static final MutableComponent computeMutableComponent(MutableComponent self, String addition) {
      return computeMutableComponent(self, addition, ChatFormatting.GREEN);
   }

   @NotNull
   public static final MutableComponent computeMutableComponent2(MutableComponent self, String addition) {
      return computeMutableComponent(self, addition, ChatFormatting.WHITE);
   }

   @NotNull
   public static final MutableComponent computeMutableComponent3(MutableComponent self, String addition) {
      return computeMutableComponent(self, addition, ChatFormatting.GRAY);
   }

   @NotNull
   public static final MutableComponent getMutableComponent() {
      return Component.empty();
   }

   @NotNull
   public static final Component computeComponent(String self) {
      return Component.nullToEmpty(self);
   }
}
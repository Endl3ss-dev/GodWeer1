// deobf by pusia
package su.catlean.utils.render;

import com.mojang.blaze3d.platform.Window;
import java.util.ArrayDeque;
import kotlin.ranges.RangesKt;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.catlean.api.data.Box;
import su.catlean.utils.MinecraftHolder;

public final class ScissorStack {
   @NotNull
   public static final ScissorStack INSTANCE = new ScissorStack();
   @NotNull
   private static final ArrayDeque<Box> arrayDeque = new ArrayDeque();

   private ScissorStack() {
   }

   public final void applyGetHeight(ScreenRectangle scissor) {
      Window window = MinecraftHolder.getMinecraft().getWindow();
      int guiScale = window.getGuiScale();
      int coerceIn = RangesKt.coerceIn(scissor.left() * guiScale, 0, window.getWidth());
      int coerceIn2 = RangesKt.coerceIn(window.getHeight() - scissor.bottom() * guiScale, 0, window.getHeight());
      int coerceIn3 = RangesKt.coerceIn(scissor.right() * guiScale, 0, window.getWidth());
      int coerceIn4 = RangesKt.coerceIn(window.getHeight() - scissor.top() * guiScale, 0, window.getHeight());
      arrayDeque.addLast(new Box(coerceIn, coerceIn2, RangesKt.coerceAtLeast(coerceIn3 - coerceIn, 0), RangesKt.coerceAtLeast(coerceIn4 - coerceIn2, 0)));
   }

   @NotNull
   public final Box getBox() {
      return (Box)arrayDeque.removeLast();
   }

   @Nullable
   public final Box getBox2() {
      return (Box)arrayDeque.peekLast();
   }
}
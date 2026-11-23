// deobf by pusia
package su.catlean.mixins.client;

import net.minecraft.world.scores.Scoreboard;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({Scoreboard.class})
public abstract class ScoreBoardMixin {

   public ScoreBoardMixin() {
   }
}
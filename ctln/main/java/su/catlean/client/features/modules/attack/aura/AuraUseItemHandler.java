// deobf by pusia
package su.catlean.client.features.modules.attack.aura;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemUseAnimation;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.Rotation;
import su.catlean.client.features.modules.attack.Aura;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.inventory.ContainerUtil;

public final class AuraUseItemHandler implements AuraHandler {
   @NotNull
   public static final AuraUseItemHandler INSTANCE = new AuraUseItemHandler();
   private static boolean flag;

   private AuraUseItemHandler() {
   }

   @Override
   public void update() {
      flag = MinecraftHolder.getLocalPlayer().isUsingItem()
         && MinecraftHolder.getLocalPlayer().getUseItem().getItem().getUseAnimation(MinecraftHolder.getLocalPlayer().getUseItem()) == ItemUseAnimation.BLOCK;
      if (flag && (Aura.INSTANCE.getWhileShielding() == Aura.WhileShielding.UNPRESS || Aura.INSTANCE.getWhileShielding() == Aura.WhileShielding.LEGIT)) {
         MinecraftHolder.getMultiPlayerGameMode().releaseUsingItem((Player)MinecraftHolder.getLocalPlayer());
      }
   }

   @Override
   public void applyMax(Rotation rotation) {
      if (flag && Aura.INSTANCE.getWhileShielding() == Aura.WhileShielding.UNPRESS) {
         ContainerUtil.computeInteractionResult(InteractionHand.OFF_HAND, rotation.getAmount(), rotation.getAmount2());
      }
   }

   @Override
   public void update2() {
   }
}
package releon.ru.api.module.impl.combat;

import java.util.function.Supplier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import releon.ru.api.module.Module;
import releon.ru.api.module.ModuleCategory;
import releon.ru.api.settings.impl.NumberSetting;
import releon.ru.manager.Manager;
import releon.ru.utils.repository.friend.FriendUtils;

public class HitBoxModule extends Module {
   private static final ThreadLocal<Boolean> SUPPRESS_EXPANSION = ThreadLocal.withInitial(() -> false);
   private final NumberSetting xzExpand = this.register(new NumberSetting("Expand XZ", "Expand target hitbox on X/Z.", 0.2, 0.0, 3.0, 0.05));
   private final NumberSetting yExpand = this.register(new NumberSetting("Expand Y", "Expand target hitbox on Y.", 0.0, 0.0, 3.0, 0.05));

   public HitBoxModule() {
      super("Hit Box", "Expands target hitboxes.", ModuleCategory.COMBAT);
   }

   public static Box apply(LivingEntity entity, Box box) {
      if (Boolean.TRUE.equals(SUPPRESS_EXPANSION.get())) {
         return box;
      } else {
         Module module = Manager.getModules() == null ? null : Manager.getModules().getHitBox().orElse(null);
         MinecraftClient client = MinecraftClient.getInstance();
         if (module instanceof HitBoxModule hitBox && module.isEnabled() && entity != client.player && !FriendUtils.isFriend(entity)) {
            double xz = hitBox.xzExpand.getValue() * 0.5;
            double y = hitBox.yExpand.getValue() * 0.5;
            return new Box(box.minX - xz, box.minY - y, box.minZ - xz, box.maxX + xz, box.maxY + y, box.maxZ + xz);
         }

         return box;
      }
   }

   public static <T> T withoutExpansion(Supplier<T> supplier) {
      boolean previous = Boolean.TRUE.equals(SUPPRESS_EXPANSION.get());
      SUPPRESS_EXPANSION.set(true);

      Object var2;
      try {
         var2 = supplier.get();
      } finally {
         SUPPRESS_EXPANSION.set(previous);
      }

      return (T)var2;
   }
}

package net.skycore.client.account;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

/** Resolves bundled account skins without contacting profile or skin websites. */
public final class SkinService {
   private static final Identifier FALLBACK = Identifier.parse("minecraft:textures/entity/player/wide/steve.png");
   private static final Map<String, Identifier> LOCAL_SKINS = new ConcurrentHashMap<>();

   private SkinService() {}

   public static Identifier skin(String username) {
      if (username == null || !username.matches("[A-Za-z0-9_]{1,16}")) {
         return FALLBACK;
      }
      prefetch(username);
      return LOCAL_SKINS.getOrDefault(username.toLowerCase(Locale.ROOT), FALLBACK);
   }

   public static void prefetch(String username) {
      if (username == null || !username.matches("[A-Za-z0-9_]{1,16}")) {
         return;
      }
      String key = username.toLowerCase(Locale.ROOT);
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft == null || LOCAL_SKINS.containsKey(key)) {
         return;
      }
      Identifier localSkin = Identifier.fromNamespaceAndPath("skycore", "textures/skins/" + key + ".png");
      if (minecraft.getResourceManager().getResource(localSkin).isPresent()) {
         LOCAL_SKINS.put(key, localSkin);
      }
   }
}

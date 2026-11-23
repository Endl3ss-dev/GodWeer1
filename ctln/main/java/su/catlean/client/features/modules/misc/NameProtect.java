// deobf by pusia
package su.catlean.client.features.modules.misc;

import su.catlean.utils.KotlinCompat;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.reflect.KProperty;
import kotlin.text.StringsKt;
import net.minecraft.client.multiplayer.PlayerInfo;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.render.StringDecomposerEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.api.setting.settings.StringSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.services.FriendManager;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;

public final class NameProtect extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(NameProtect.class, "name", "getName()Ljava/lang/String;", 0), new PropertyReference1Impl(NameProtect.class, "replace", "getReplace()Lsu/catlean/v;", 0) };
   @NotNull
   public static final NameProtect INSTANCE = new NameProtect();
   @NotNull
   private static final StringSetting name = SettingFactory.defaultImpl(INSTANCE, "name", "catlean.su", null, null, 12, null);
   @NotNull
   private static final EnumSetting replace = SettingFactory.defaultImpl(INSTANCE, "replace", NameProtect.Replace.FRIENDS, null, null, 12, null);

   private NameProtect() {
      super("name-protect", SubCategories.getSubCategory11(), null, 4, null);
   }

   @NotNull
   public final String getName() {
      return (String)name.computeObject(this, $$delegatedProperties[0]);
   }

   @NotNull
   public final NameProtect.Replace getReplace() {
      return (NameProtect.Replace)replace.computeObject(this, $$delegatedProperties[1]);
   }

   @Flow
   private final void onStringDecomposer(StringDecomposerEvent stringDecomposerEvent) {
      stringDecomposerEvent.setString(this.computeText(stringDecomposerEvent.getString()));
   }

   @NotNull
   public final String computeText(String text) {
      if (this.isFlag6() && this.getEnabled()) {
         String replacedefault = null;
         replacedefault = KotlinCompat.replace(text, MinecraftHolder.getLocalPlayer().getGameProfile().name(), this.getName(), false, 4, null);
         if (this.getReplace() != NameProtect.Replace.ONLY_SELF) {
            for (Object onlinePlayer : MinecraftHolder.getClientPacketListener().getOnlinePlayers()) {
               PlayerInfo playerInfo = (PlayerInfo)onlinePlayer;
               if (INSTANCE.getReplace() == NameProtect.Replace.ALL || FriendManager.INSTANCE.checkContains(playerInfo.getProfile().name())) {
                  replacedefault = KotlinCompat.replace(replacedefault, playerInfo.getProfile().name(), INSTANCE.getName(), false, 4, null);
               }
            }
         }

         return replacedefault;
      } else {
         return text;
      }
   }

   static {
   }

   public static enum Replace {
      ONLY_SELF,
      FRIENDS,
      ALL;

      private static final NameProtect.Replace[] replaces = $values();
      private static final EnumEntries<NameProtect.Replace> enumEntries = EnumEntriesKt.enumEntries(replaces);

      private Replace() {
      }

      @NotNull
      public static EnumEntries<NameProtect.Replace> getEnumEntries() {
         return enumEntries;
      }

      private static final NameProtect.Replace[] $values() {
         return new NameProtect.Replace[]{ONLY_SELF, FRIENDS, ALL};
      }
   }
}
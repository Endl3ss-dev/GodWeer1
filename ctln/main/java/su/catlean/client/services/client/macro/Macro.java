// deobf by pusia
package su.catlean.client.services.client.macro;

import kotlinx.serialization.Serializable;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.setting.values.Bind;

@Serializable
public sealed interface Macro permits ChatMacro, ItemMacro, SettingMacro {
   @NotNull
   Macro.DefaultImpls defaultImpls = Macro.DefaultImpls.INSTANCE;

   @NotNull
   String getId();

   void updateText(String text);

   @NotNull
   Bind getBind();

   void updateBind(Bind bind);

   boolean isFlag();

   void setFlag(boolean flag);

   public static final class DefaultImpls {
      public static final Macro.DefaultImpls INSTANCE = new Macro.DefaultImpls();

      private DefaultImpls() {
      }
   }
}
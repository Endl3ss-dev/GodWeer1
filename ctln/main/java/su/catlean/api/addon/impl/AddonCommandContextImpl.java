// deobf by pusia
package su.catlean.api.addon.impl;

import su.catlean.api.addon.feature.AddonCommandContext;
import su.catlean.client.commands.impl.AddonsCommand;

public final class AddonCommandContextImpl implements AddonCommandContext {
   public final AddonsCommand addonsCommand;

   public AddonCommandContextImpl(AddonsCommand addonsCommand) {
      this.addonsCommand = addonsCommand;
   }

   @Override
   public void message(String message) {
      this.addonsCommand.updateText(message);
   }
}